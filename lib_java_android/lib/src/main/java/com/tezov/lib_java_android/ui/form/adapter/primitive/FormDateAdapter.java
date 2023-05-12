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
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.util.UtilsString;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import androidx.fragment.app.Fragment;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java_android.ui.form.adapter.FormAdapter;
import com.tezov.lib_java_android.ui.form.adapter.FormManager;

import org.threeten.bp.LocalDate;
import org.threeten.bp.ZoneId;

import java.util.concurrent.TimeUnit;

public class FormDateAdapter extends FormAdapter<LocalDate>{
private final Clock.FormatDate format;
private final TimeUnit timeUnit;
private final ZoneId zoneId;

public FormDateAdapter(FormManager.Target.Is target, Clock.FormatDate format, ZoneId zoneId){
    this(target, format, zoneId, TimeUnit.MILLISECONDS);
}

public FormDateAdapter(FormManager.Target.Is target, Clock.FormatDate format, ZoneId zoneId, TimeUnit timeUnit){
    super(target);
    this.format = format;
    this.timeUnit = timeUnit;
    if(zoneId == null){
        this.zoneId = Clock.ZoneIdLocal();
    } else {
        this.zoneId = zoneId;
    }
}

@Override
public Class<LocalDate> getEntryType(){
    return LocalDate.class;
}

@Override
public String valueToString(){
    if(isNULL()){
        return null;
    }
    return Clock.DateTo.string(getValue(), format);
}

@Override
public <T> boolean isAcceptedType(Class<T> type){
    return (type == String.class) || (type == Long.class) || (type == LocalDate.class);
}

@Override
public <T> boolean setValue(Class<T> type, T object){
    if(type == String.class){
        setValue(Clock.StringTo.Date.with((String)object, format));
        return true;
    } else if(type == Long.class){
        setValue(Clock.MilliSecondTo.Date.with(TimeUnit.MILLISECONDS.convert((Long)object, timeUnit), zoneId));
        return true;
    } else if(type == LocalDate.class){
        setValue(((LocalDate)object));
        return true;
    } else {
DebugException.start().unknown("type", type.getName()).end();
        return false;
    }


}

@Override
public <T> T getValue(Class<T> type){
    if(type == String.class){
        return (T)valueToString();

    } else if(type == Long.class){
        if(isNULL()){
            return null;
        }
        Long date = Clock.DateTo.MilliSecond.with(getValue(), zoneId);
        if(timeUnit == TimeUnit.MILLISECONDS){
            return (T)date;
        } else {
            Long dateConvert = timeUnit.convert(date, TimeUnit.MILLISECONDS);
            return (T)dateConvert;
        }

    } else if(type == LocalDate.class){
        return (T)getValue();
    } else {

DebugException.start().unknown("type", type.getName()).end();

        return null;
    }
}

}
