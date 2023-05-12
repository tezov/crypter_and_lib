/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.type.ref;

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.util.UtilsString;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java.application.AppKryo;
import com.tezov.lib_java.definition.defCopyable;
import com.tezov.lib_java.toolbox.Compare;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.wrapperAnonymous.BiPredicateW;
import com.tezov.lib_java.util.UtilsNull;

//Strong with origin
public class SRwO<T> extends Ref<T>{
private static final Object NULL = UtilsNull.NULL_OBJECT;
private Object initialValue = NULL;
private Object currentValue = NULL;
private boolean hasChanged = false;
private BiPredicateW<Object, Object> predicate = null;

public SRwO(){
}
private BiPredicateW<Object, Object> getPredicate(){
    if(predicate == null){
        predicate = new BiPredicateW<Object, Object>(){
            @Override
            public boolean test(Object o1, Object o2){
                return Compare.equals(o1, o2);
            }
        };
    }
    return predicate;
}
public SRwO<T> setPredicate(BiPredicateW<T, T> predicate){
    this.predicate = (BiPredicateW<Object, Object>)predicate;
    return this;
}

@Override
public T get(){
    if(currentValue == NULL){
        return null;
    } else {
        return (T)currentValue;
    }
}
protected void setValue(T value){
    if(Compare.equals(initialValue, NULL)){
        updateInfo(value);
        if(value == null){
            initialValue = null;
        } else if(CompareType.isImmutable(value.getClass())){
            initialValue = value;
        } else if(value instanceof defCopyable){
            initialValue = ((defCopyable)value).copy();
        } else {
            initialValue = AppKryo.copy(value);
            if(initialValue == value){
DebugException.start().log("InitialValue and value are the same reference : " + value + " class:" + value.getClass().getSimpleName()).end();
            }
        }
    }
    this.currentValue = value;
}
@Override
public void set(T value){
    if(!Compare.equals(currentValue, value)){
        hasChanged = true;
    }
    setValue(value);
}
public boolean setIfDifferent(T value){
    if(getPredicate().test(currentValue, value)){
        return false;
    }
    else{
        hasChanged = true;
        setValue(value);
        return true;
    }
}

public boolean hasChanged(){
    return hasChanged;
}
public void checked(){
    hasChanged = false;
}

public boolean hasValue(){
    return !Compare.equals(currentValue, NULL);
}
public boolean hasChangedFromInitial(){
    return !getPredicate().test(initialValue, currentValue);
}

@Override
public int hashCode(){
    if((currentValue == null) || (currentValue == NULL)){
        return 0;
    } else {
        return currentValue.hashCode();
    }
}
@Override
public String hashCodeString(){
    return Integer.toHexString(hashCode());
}
@Override
public boolean equals(Object obj){
    return (obj instanceof SRwO) && (hashCode() == obj.hashCode());
}


}
