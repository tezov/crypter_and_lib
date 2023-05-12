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
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.util.UtilsString;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
//Strong with exception
public class SRwE<T> extends Ref<T>{
private T t;
private Throwable e;

protected SRwE(T t, Throwable e){
    this.t = t;
    this.e = e;
}

public static <T> SRwE<T> newInstance(){
    return new SRwE<>(null, null);
}

public static <T> SRwE<T> newInstance(T t){
    return new SRwE<>(t, null);
}

public static <T> SRwE<T> newInstance(Throwable e){
    return new SRwE<>(null, e);
}

public static <T> SRwE<T> newInstance(T t, Throwable e){
    return new SRwE<>(t, e);
}

@Override
public void set(T o){
    if(!(o instanceof Throwable)){
        this.t = o;
    } else {
        e = (Throwable)o;
    }
}

public void set(T t, Throwable exception){
    this.t = t;
    this.e = exception;
}

@Override
public T get(){
    return t;
}

public Throwable getException(){
    return e;
}

}
