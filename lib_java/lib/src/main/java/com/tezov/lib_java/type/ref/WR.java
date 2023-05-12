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
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

//Weak
public class WR<T> extends Ref<T>{
protected WeakReference<T> t;

public WR(T referent){
    set(referent);
}

public WR(T referent, ReferenceQueue<? super T> q){
    set(referent, q);
}
public static <T> WR<T> newInstance(T referent){
    return new WR<>(referent);
}
public static <T> WR<T> newInstance(T referent, ReferenceQueue<? super T> q){
    return new WR<>(referent, q);
}

@Override
public boolean isNull(){
    return (t == null) || t.get() == null;
}

@Override
public boolean isNotNull(){
    return !isNull();
}

@Override
public T get(){
    return t.get();
}

@Override
public void set(T referent){
    updateInfo(referent);
    t = new WeakReference<>(referent);
}

public void set(T referent, ReferenceQueue<? super T> q){
    updateInfo(referent);
    t = new LostRef(referent, q);
}

}
