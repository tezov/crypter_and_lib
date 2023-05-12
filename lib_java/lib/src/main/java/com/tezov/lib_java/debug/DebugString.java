/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.debug;

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.util.UtilsString;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.primitive.LongTo;

import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.type.primitive.ObjectTo;

import java.util.Collection;

public class DebugString{
private final StringBuilder sb;

public DebugString(){
DebugTrack.start().create(this).end();
    sb = new StringBuilder();
}

public DebugString nextLine(){
    sb.append('\n');
    return this;
}

public DebugString append(String s){
    sb.append(s);
    return this;
}

public DebugString append(Object o){
    sb.append(DebugObject.toString(o));
    return this;
}

public DebugString append(Object name, Object o){
    append("[").append(DebugObject.toString(name)).append(":").append(DebugObject.toString(o)).append("]");
    return this;
}

public DebugString appendSize(Object name, Collection c){
    return append(DebugObject.toString(name) + ".size()", c != null ? c.size() : null);
}

public DebugString appendCheckIfNull(Object name, Object o){
    return append(name, o == null ? "null" : "not null");
}

public DebugString appendFullSimpleName(Object name, Object o){
    return append(name, DebugTrack.getFullSimpleName(o));
}

public DebugString appendFullSimpleNameWithHashcode(Object name, Object o){
    return append(name, DebugTrack.getFullSimpleNameWithHashcode(o));
}

public DebugString appendHashcodeString(Object name, Object o){
    return append(name, ObjectTo.hashcodeString(o));
}

public DebugString appendDateAndTime(Object name, Long ms){
    if(ms == null){
        return append(name, null);
    } else {
        return append(name, Clock.MilliSecondTo.DateAndTime.toString(ms));
    }
}

public DebugString appendDate(Object name, Long ms){
    if(ms == null){
        return append(name, null);
    } else {
        return append(name, Clock.MilliSecondTo.Date.toString(ms));
    }
}
public DebugString appendHex(Object name, Integer value){
    if(value == null){
        return append(name, value);
    } else {
        return append(name, IntTo.StringHex(value));
    }
}
public DebugString appendHex(Object name, Long value){
    if(value == null){
        return append(name, value);
    } else {
        return append(name, LongTo.StringHex(value));
    }
}

public DebugString appendTime(Object name, Long ms){
    if(ms == null){
        return append(name, null);
    } else {
        return append(name, Clock.MilliSecondTo.Time.toString(ms));
    }
}

public DebugString appendElapsedTime(Object name, Long ms){
    if(ms == null){
        return append(name, null);
    } else {
        return append(name, Clock.MilliSecondTo.MilliSecond.Elapsed.toString(ms));
    }
}

@Override
public String toString(){
    return sb.toString();
}

public DebugString toDebugString(){
    return this;
}

final public void toDebugLog(){
DebugLog.start().send(sb).end();
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

}
