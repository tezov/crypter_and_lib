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
import static com.tezov.lib_java_android.ui.dialog.dialogPicker.prebuild.DialogPickerRecyclerWheelUnitBuilder.UNIT_KEY;

import com.tezov.lib_java.toolbox.Nullify;
import com.tezov.lib_java.type.collection.ListEntry;
import com.tezov.lib_java.type.primaire.Entry;
import com.tezov.lib_java.type.primaire.Pair;
import com.tezov.lib_java.type.unit.defEnumUnit;
import com.tezov.lib_java_android.ui.dialog.dialogPicker.picker.DialogPickerRecyclerWheelMultiple;
import com.tezov.lib_java_android.ui.form.adapter.FormManager;
import com.tezov.lib_java.util.UtilsNumber;
import com.tezov.lib_java.util.UtilsString;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FormRecyclerWheelUnitAdapter<UNIT extends Enum<UNIT> & defEnumUnit<UNIT>> extends DialogPickerRecyclerWheelMultiple.FormDataAdapter<Integer>{
private final UNIT unitDefault;
private final int integerPrecision;
private final int decimalPrecision;
private UNIT unitPreferred = null;
private String separator;

public FormRecyclerWheelUnitAdapter(FormManager.Target.Is target, UNIT unitDefault, int integerPrecision, int decimalPrecision, String separator){
    super(target);
    this.unitDefault = unitDefault;
    this.integerPrecision = integerPrecision;
    this.decimalPrecision = decimalPrecision;
    setSeparator(separator);
}

private static boolean isIntegerKey(Integer key){
    return key >= 1;
}

private static boolean isDecimalKey(Integer key){
    return (key <= -1) && (!key.equals(UNIT_KEY));
}

private void setSeparator(String separator){
    if(separator != null){
        this.separator = separator;
    } else {
        this.separator = "";
    }
}

public FormRecyclerWheelUnitAdapter<UNIT> setUnitPreferred(UNIT unitPreferred){
    this.unitPreferred = unitPreferred;
    return this;
}

private UNIT[] getUnits(){
    return (UNIT[])unitDefault.getClass().getEnclosingClass().getEnumConstants();
}

private String getIntegerPart(ListEntry<Integer, Object> datas){
    StringBuilder data = new StringBuilder();
    for(Entry<Integer, Object> e: datas){
        if(isIntegerKey(e.key)){
            data.append((String)e.value);
        } else {
            break;
        }
    }
    return Nullify.string(UtilsString.removeLeadingZero(data.toString()));
}

private String getDecimalPart(ListEntry<Integer, Object> datas){
    StringBuilder data = new StringBuilder();
    for(Entry<Integer, Object> e: datas){
        if(isDecimalKey(e.key)){
            data.append((String)e.value);
        }
    }
    return Nullify.string(data.toString());
}

private UNIT getUnit(ListEntry<Integer, Object> datas){
    return (UNIT)datas.getValue(UNIT_KEY);
}

private UNIT getUnit(String unit){
    return UNIT.valueOf((Class<UNIT>)unitDefault.getClass(), unit);  //NOW valueOf
}

private void setIntegerPart(ListEntry<Integer, Object> datas, int value){
    setIntegerPart(datas, String.format(Locale.US, "%0" + integerPrecision + "d", value));
}

private void setIntegerPart(ListEntry<Integer, Object> datas, String value){
    if(value.length() < integerPrecision){
        for(int end = value.length(), i = integerPrecision; i > end; i--){
            datas.add(i, "0");
        }
    }
    for(int start = value.length(), i = start; i > 0; i--){
        datas.add(i, String.valueOf(value.charAt(start - i)));
    }
}

private void setDecimalPart(ListEntry<Integer, Object> datas, int value){
    setDecimalPart(datas, String.format(Locale.US, "%0" + decimalPrecision + "d", value));
}

private void setDecimalPart(ListEntry<Integer, Object> datas, String value){
    for(int end = value.length(), i = 1; i <= end; i++){
        datas.add(-i, String.valueOf(value.charAt(i - 1)));
    }
    if(value.length() < decimalPrecision){
        for(int start = value.length(), i = start; i < decimalPrecision; i++){
            datas.add(i, "0");
        }
    }
}

private void setUnit(ListEntry<Integer, Object> datas, String unit){
    setUnit(datas, getUnit(unit));
}

private void setUnit(ListEntry<Integer, Object> datas, UNIT unit){
    datas.add(UNIT_KEY, unit);
}

private ListEntry<Integer, Object> makeData(String integer, String decimal, String unit){
    ListEntry<Integer, Object> datas = new ListEntry<Integer, Object>();
    if(integer != null){
        setIntegerPart(datas, integer);
    }
    if(decimal != null){
        setDecimalPart(datas, decimal);
    }
    setUnit(datas, getUnit(unit));
    return datas;
}

private ListEntry<Integer, Object> makeData(int integer, int decimal, UNIT unit){
    UNIT bestUnit = findBestUnit(integer, unit);
    if(bestUnit != unit){
        float value = UtilsNumber.intToFloat(integer, decimal, this.decimalPrecision);
        float valueConverted = bestUnit.convert(value, unit);
        Pair<Integer, Integer> p = UtilsNumber.floatToInt(valueConverted, decimalPrecision);
        integer = p.first;
        decimal = p.second;
        unit = bestUnit;
    }
    ListEntry<Integer, Object> datas = new ListEntry<Integer, Object>();
    if(this.integerPrecision > 0){
        setIntegerPart(datas, integer);
    }
    if(this.decimalPrecision > 0){
        setDecimalPart(datas, decimal);
    }
    setUnit(datas, unit);
    return datas;
}

private UNIT findBestUnit(int integer, UNIT unit){
    if(unitPreferred != null){
        int integerWithPreferredUnit = (int)unitPreferred.convert(integer, unit);
        if(String.valueOf(integerWithPreferredUnit).length() <= integerPrecision){
            return unitPreferred;
        }
    }
    if(String.valueOf(integer).length() > integerPrecision){
        UNIT[] units = getUnits();
        do{
            UNIT nextUnit = units[unit.ordinal() + 1];
            integer = (int)nextUnit.convert(integer, unit);
            unit = nextUnit;
        } while((String.valueOf(integer).length() > integerPrecision) && (unit.ordinal() < (units.length - 1)));
    }
    return unit;
}

@Override
protected String valueToString(ListEntry<Integer, Object> datas){
    if(datas == null){
        return null;
    } else {
        String integer = getIntegerPart(datas);
        String decimal = getDecimalPart(datas);
        String unit = getUnit(datas).name();
        if((integer.length() > integerPrecision) || ((unitPreferred != null) && (getUnit(datas) != unitPreferred))){
            datas = makeData(integer, decimal, unit);
            setValue(datas);
            integer = getIntegerPart(datas);
            decimal = getDecimalPart(datas);
            unit = getUnit(datas).name();
        }
        if(separator != null){
            return integer + separator + decimal + unit;
        } else {
            return integer + unit;
        }
    }
}

@Override
protected ListEntry<Integer, Object> valueFromString(String s){
    if(s == null){
        return null;
    } else {
        if(separator != null){
            Pattern pattern = Pattern.compile("^([0-9]+)" + Pattern.quote(separator) + "([0-9" + "]+?)([a-zA-Z]+)$");
            Matcher matcher = pattern.matcher(s);
            if(matcher.matches()){
                return makeData(matcher.group(1), matcher.group(2), matcher.group(3));
            } else {
                return null;
            }
        } else {
            Pattern pattern = Pattern.compile("^([0-9]+)([a-zA-Z]+)$");
            Matcher matcher = pattern.matcher(s);
            if(matcher.matches()){
                return makeData(matcher.group(1), null, matcher.group(2));
            } else {
                return null;
            }
        }
    }
}

@Override
public <T> boolean isAcceptedType(Class<T> type){
    return (type == Long.class) || (type == Pair.class) || (type == Float.class) || (type == Integer.class) || super.isAcceptedType(type);
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

private BigDecimal toBigDecimal(UNIT unit){
    ListEntry<Integer, Object> datas = getValue();
    String integer = getIntegerPart(datas);
    String decimal = getDecimalPart(datas);
    float value = UtilsNumber.intToFloat(integer != null ? Integer.parseInt(integer) : 0, decimal != null ? Integer.parseInt(decimal) : 0, this.decimalPrecision);
    float valueConverted = unit.convert(value, getUnit(datas));
    return new BigDecimal(valueConverted);
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
        setValue(null);
    } else {
        setValue(makeData(value.intValue(), 0, unit));
    }
}

public Long getLong(UNIT unit){
    if(isNULL()){
        return null;
    } else {
        return toBigDecimal(unit).setScale(0, RoundingMode.HALF_UP).longValue();
    }
}

public Pair<Float, UNIT> getPair(){
    if(isNULL()){
        return null;
    } else {
        UNIT unit = getUnit(getValue());
        float value = toBigDecimal(unit).floatValue();
        return new Pair<>(value, unit);
    }
}

public void setPair(Pair<Float, UNIT> value){
    if(value == null){
        setValue(null);
    } else {
        Pair<Integer, Integer> p = UtilsNumber.floatToInt(value.first, decimalPrecision);
        setValue(makeData(p.first, p.second, value.second));
    }
}

public void setFloat(Float value, UNIT unit){
    if(value == null){
        setValue(null);
    } else {
        Pair<Integer, Integer> p = UtilsNumber.floatToInt(value, decimalPrecision);
        setValue(makeData(p.first, p.second, unit));
    }
}

public Float getFloat(UNIT unit){
    if(isNULL()){
        return null;
    } else {
        return toBigDecimal(unit).floatValue();
    }
}

public void setInt(Integer value, UNIT unit){
    if(value == null){
        setValue(null);
    } else {
        setValue(makeData(value, 0, unit));
    }
}

public Integer getInt(UNIT unit){
    if(isNULL()){
        return null;
    } else {
        return toBigDecimal(unit).setScale(0, RoundingMode.HALF_UP).intValue();
    }
}

}
