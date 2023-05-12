/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.async.notifier.observer.state;

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

import com.tezov.lib_java.async.notifier.observer.value.ObserverValueE;
import com.tezov.lib_java.debug.DebugException;

public abstract class ObserverStateE extends ObserverValueE<Void>{
public ObserverStateE(Object owner){
    super(owner);
}

@Override
final public void onComplete(Void v){
    onComplete();
}
public void onComplete(){

DebugException.start().notImplemented(getOwner()).end();

}

@Override
final public void onException(Void v, Throwable e){
    onException(e);
}
public void onException(java.lang.Throwable e){

DebugException.start().notImplemented(getOwner()).end();

}

}
