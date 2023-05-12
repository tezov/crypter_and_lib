/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.form.adapter.primitive;

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import androidx.fragment.app.Fragment;

import com.tezov.lib_java.type.primaire.Pair;
import com.tezov.lib_java.type.unit.defEnumUnit;
import com.tezov.lib_java_android.ui.dialog.dialogPicker.picker.DialogPickerStringUnit;
import com.tezov.lib_java_android.ui.dialog.dialogPicker.picker.DialogPickerStringUnit.Data;
import com.tezov.lib_java_android.ui.form.adapter.FormManager;
import com.tezov.lib_java.util.UtilsNumber;
import com.tezov.lib_java.util.UtilsString;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FormStringUnitAdapter<UNIT extends Enum<UNIT> & defEnumUnit<UNIT>> extends DialogPickerStringUnit.FormDataAdapter<UNIT>{
private final UNIT unitDefault;
private UNIT unitPreferred = null;
private Integer integerPrecisionPreferred;

public FormStringUnitAdapter(FormManager.Target.Is target, UNIT unitDefault){
    super(target);
    this.unitDefault = unitDefault;
}

public FormStringUnitAdapter<UNIT> setUnitPreferred(UNIT unitPreferred){
    this.unitPreferred = unitPreferred;
    return this;
}

public FormStringUnitAdapter<UNIT> setIntegerPrecisionPreferred(Integer integerPrecisionPreferred){
    this.integerPrecisionPreferred = integerPrecisionPreferred;
    return this;
}

private UNIT[] getUnits(){
    return (UNIT[])unitDefault.getClass().getEnclosingClass().getEnumConstants();
}

private UNIT getUnit(String unit){
    return UNIT.valueOf((Class<UNIT>)unitDefault.getClass(), unit);  //NOW valueOf
}

private Data<UNIT> makeData(int integer, int decimal, UNIT unit){
    if((unit != unitPreferred) || (integerPrecisionPreferred != null)){
        UNIT bestUnit = findBestUnit(integer, unit);
        if(bestUnit != unit){
            float value = UtilsNumber.intToFloat(integer, decimal);
            float valueConverted = bestUnit.convert(value, unit);
            Pair<Integer, Integer> p = UtilsNumber.floatToInt(valueConverted);
            integer = p.first;
            decimal = p.second;
        }
        unit = bestUnit;
    }
    StringBuilder value = new StringBuilder();
    value.append(integer);
    if(decimal != 0){
        value.append(UtilsString.NUMBER_SEPARATOR).append(decimal);
    }
    return new Data<>(value.toString(), unit);
}

private UNIT findBestUnit(int integer, UNIT currentUnit){
    if(unitPreferred != null){
        int integerWithPreferredUnit = (int)unitPreferred.convert(integer, currentUnit);
        if((integerPrecisionPreferred == null) || String.valueOf(integerWithPreferredUnit).length() <= integerPrecisionPreferred){
            return unitPreferred;
        }
    }
    if((integerPrecisionPreferred != null) && (String.valueOf(integer).length() > integerPrecisionPreferred)){
        UNIT[] units = getUnits();
        do{
            UNIT nextUnit = units[currentUnit.ordinal() + 1];
            integer = (int)nextUnit.convert(integer, currentUnit);
            currentUnit = nextUnit;
        } while((String.valueOf(integer).length() > integerPrecisionPreferred) && (currentUnit.ordinal() < (units.length - 1)));
    }
    return currentUnit;
}

@Override
protected String valueToString(Data<UNIT> data){
    if((data == null) || (data.getValue() == null)){
        return null;
    } else {
        UtilsString.Number number = UtilsString.parseNumber(data.getValue());
        if(((integerPrecisionPreferred != null) && (number.getIntegerPrecision() > integerPrecisionPreferred)) || ((unitPreferred != null) && (data.getUnit() != unitPreferred))){
            data = makeData(number.getInteger(), number.getDecimal(), data.getUnit());
            this.setValue(data);
        }
        String value = UtilsString.removeSeparatorIfLast(data.getValue());
        return value + data.getUnit().name();
    }
}

@Override
protected Data<UNIT> valueFromString(String s){
    if(s == null){
        return defaultData();
    } else {
        Pattern pattern = Pattern.compile("^([0-9]+" + Pattern.quote(UtilsString.NUMBER_SEPARATOR) + "?[0-9]+?)([a-zA-Z]+)$");
        Matcher matcher = pattern.matcher(s);
        if(matcher.matches()){
            return new Data<>(matcher.group(1), getUnit(matcher.group(2)));
        } else {
            return defaultData();
        }
    }
}

private Data<UNIT> defaultData(){
    if(unitPreferred != null){
        return new Data<>(null, unitPreferred);
    } else if(unitDefault != null){
        return new Data<>(null, unitDefault);
    } else {
        return null;
    }
}

@Override
public <T> boolean isAcceptedType(Class<T> type){
    return (type == Long.class) || (type == Integer.class) || (type == Float.class) || (type == Pair.class) || super.isAcceptedType(type);
}

@Override
public boolean setValue(Data<UNIT> value){
    if(value != null){
        super.setValue(value);
    } else {
        super.setValue(defaultData());
    }
    return true;
}
@Override
public <T> boolean setValue(Class<T> type, T object){
    if(type == Long.class){
        setLong((Long)object, unitDefault);
        return true;
    } else if(type == Pair.class){
        setPair((Pair<Float, UNIT>)object);
        return true;
    } else if(type == Float.class){
        setFloat((Float)object, unitDefault);
        return true;
    } else if(type == Integer.class){
        setInt((Integer)object, unitDefault);
        return true;
    } else {
        return super.setValue(type, object);
    }
}

@Override
public <T> T getValue(Class<T> type){
    if((type == Long.class)){
        return (T)getLong(unitDefault);
    }
    if((type == Pair.class)){
        return (T)getPair();
    }
    if((type == Float.class)){
        return (T)getFloat(unitDefault);
    }
    if((type == Integer.class)){
        return (T)getInt(unitDefault);
    }
    return super.getValue(type);
}

public void setLong(Long value, UNIT unit){
    if(value == null){
        this.setValue(null);
    } else {
        this.setValue(makeData(value.intValue(), 0, unit));
    }
}

public Long getLong(UNIT unitLiter){
    Data<UNIT> data = getValue();
    if((data == null) || (data.getValue() == null)){
        return null;
    } else {
        return (long)Math.round(unitLiter.convert(Float.parseFloat(data.getValue()), data.getUnit()));
    }
}

public Pair<Float, UNIT> getPair(){
    if(isNULL()){
        return null;
    } else {
        Data<UNIT> data = getValue();
        if(data.getValue() == null){
            return null;
        } else {
            return new Pair<>(Float.valueOf(data.getValue()), data.getUnit());
        }
    }
}

public void setPair(Pair<Float, UNIT> value){
    if(value == null){
        this.setValue(null);
    } else {
        Pair<Integer, Integer> p = UtilsNumber.floatToInt(value.first);
        this.setValue(makeData(p.first, p.second != 0 ? p.second : null, value.second));
    }
}

public void setFloat(Float value, UNIT unit){
    if(value == null){
        this.setValue(null);
    } else {
        Pair<Integer, Integer> p = UtilsNumber.floatToInt(value);
        this.setValue(makeData(p.first, p.second != 0 ? p.second : null, unit));
    }
}

public Float getFloat(UNIT unitLiter){
    if(isNULL()){
        return null;
    } else {
        Data<UNIT> data = getValue();
        return unitLiter.convert(Float.parseFloat(data.getValue()), data.getUnit());
    }
}

public void setInt(Integer value, UNIT unit){
    if(value == null){
        this.setValue(null);
    } else {
        this.setValue(makeData(value, 0, unit));
    }
}

public Integer getInt(UNIT unitLiter){
    if(isNULL()){
        return null;
    } else {
        Data<UNIT> data = getValue();
        return Math.round(unitLiter.convert(Float.parseFloat(data.getValue()), data.getUnit()));
    }
}

}
