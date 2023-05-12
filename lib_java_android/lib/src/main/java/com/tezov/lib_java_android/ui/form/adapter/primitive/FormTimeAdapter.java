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

import org.threeten.bp.LocalTime;
import org.threeten.bp.ZoneId;

import java.util.concurrent.TimeUnit;

public class FormTimeAdapter extends FormAdapter<LocalTime>{
private final Clock.FormatTime format;
private final TimeUnit timeUnit;
private final ZoneId zoneId;

public FormTimeAdapter(FormManager.Target.Is target, Clock.FormatTime format, ZoneId zoneId){
    this(target, format, zoneId, TimeUnit.MILLISECONDS);
}

public FormTimeAdapter(FormManager.Target.Is target, Clock.FormatTime format, ZoneId zoneId, TimeUnit timeUnit){
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
public Class<LocalTime> getEntryType(){
    return LocalTime.class;
}

@Override
public String valueToString(){
    if(isNULL()){
        return null;
    }
    return Clock.TimeTo.string(getValue(), format);
}

@Override
public <T> boolean isAcceptedType(Class<T> type){
    return (type == String.class) || (type == Long.class) || (type == LocalTime.class);
}

@Override
public <T> boolean setValue(Class<T> type, T object){
    if(type == String.class){
        setValue(Clock.StringTo.Time.with((String)object));
        return true;
    } else if(type == Long.class){
        setValue(Clock.MilliSecondTo.Time.with(TimeUnit.MILLISECONDS.convert((Long)object, timeUnit), zoneId));
        return true;
    } else if(type == LocalTime.class){
        setValue((LocalTime)object);
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
        Long time = Clock.TimeTo.MilliSecond.with(getValue(), zoneId);
        if(timeUnit == TimeUnit.MILLISECONDS){
            return (T)time;
        } else {
            Long timeConvert = timeUnit.convert(time, TimeUnit.MILLISECONDS);
            return (T)timeConvert;
        }

    } else if(type == LocalTime.class){
        return (T)getValue();
    } else {

DebugException.start().unknown("type", type.getName()).end();

        return null;
    }
}

}
