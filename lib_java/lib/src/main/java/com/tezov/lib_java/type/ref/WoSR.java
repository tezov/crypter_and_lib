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
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.util.UtilsString;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java.debug.DebugException;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

//Weak or Strong
public class WoSR<T> extends Ref<T>{
protected Object object;
protected ReferenceQueue q = null;

public WoSR(T t, boolean keepAsWeakReference){
    updateInfo(t);
    if(keepAsWeakReference){
        object = new WeakReference<>(t);
    } else {
        object = t;
    }
}

public WoSR(T t, ReferenceQueue<? super T> q, boolean keepAsWeakReference){
    updateInfo(t);
    this.q = q;
    if(keepAsWeakReference){
        object = new LostRef(t, q);
    } else {
        object = t;
    }
}

public WoSR<T> makeWeak(){
    T t = get();
    if(q == null){
        object = new WeakReference<>(t);
    } else {
        object = new LostRef(t, q);
    }
    return this;
}

public WoSR<T> makeStrong(){
    object = get();
    return this;
}

public boolean isWeak(){
    return (object instanceof WeakReference);
}

public boolean isStrong(){
    return !(object instanceof WeakReference);
}

@Override
public T get(){
    if(isWeak()){
        return ((WeakReference<T>)object).get();
    }
    return (T)object;
}

@Override
public void set(T referent){

DebugException.start().notImplemented().end();

}

}
