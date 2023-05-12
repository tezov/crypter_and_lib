/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.util;

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.util.UtilsString;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import androidx.fragment.app.Fragment;

import com.tezov.lib_java.definition.defEntry;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugTrack;

import java.util.ArrayList;
import java.util.List;

public class UtilsTypeWrapper{
private static final List<def> WRAP_TYPES = new ArrayList<>();

private UtilsTypeWrapper(){
}

public static void addWrapper(def typeWrapper){
    WRAP_TYPES.add(typeWrapper);
}
private static <T, WT> def<T, WT> getWrapper(Class<WT> wrapType){
    for(def boxes: WRAP_TYPES){
        if(boxes.getWrapType() == wrapType){
            return boxes;
        }
    }
    return null;
}

public static <T> boolean isWrapType(Class<T> wrapType){
    if(wrapType == null){
        return false;
    }
    def wrapper = getWrapper(wrapType);
    return wrapper != null;
}
public static <T, WT> Class<T> getType(Class<WT> wrapType){
    def<T, WT> wrapper = getWrapper(wrapType);
    if(wrapper == null){
        return null;
    } else {
        return wrapper.getType();
    }
}
public static <T, WT> T unWrap(WT wrapObject){
    if(wrapObject == null){
        return null;
    }
    Class<WT> wrapType = (Class<WT>)wrapObject.getClass();
    def<T, WT> wrapper = getWrapper(wrapType);
    if(wrapper == null){

DebugException.start().unknown("type", DebugTrack.getFullSimpleName(wrapObject)).end();


        return null;
    } else {
        return wrapper.unWrap(wrapObject);
    }
}
public static <T> boolean isAcceptEntryType(defEntry<?> entry, Class<T> type){
    def wrapper = getWrapper(type);
    if(wrapper == null){
        return entry.isAcceptedType(type);
    } else {
        return entry.isAcceptedType(wrapper.getType());
    }
}
public static <T> T getFrom(defEntry<?> entry, Class<T> type){
    def wrapper = getWrapper(type);
    if(wrapper == null){
        return entry.getValue(type);
    } else {
        return (T)wrapper.wrap(entry.getValue(wrapper.getType()));
    }
}
public static <T> boolean setTo(defEntry<?> entry, Class<T> type, T value){
    def wrapper = getWrapper(type);
    if(wrapper == null){
        return entry.setValue(type, value);
    } else {
        return entry.setValue(wrapper.getType(), wrapper.unWrap(value));
    }
}

public interface def<TYPE, WRAP_TYPE>{
    Class<TYPE> getType();
    Class<WRAP_TYPE> getWrapType();
    WRAP_TYPE wrap(TYPE t);
    TYPE unWrap(WRAP_TYPE wt);
}

}
