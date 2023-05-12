/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.async.notifier.observer.value;

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

import com.tezov.lib_java.async.notifier.observable.ObservableValue;
import com.tezov.lib_java.async.notifier.observer.Observer;
import com.tezov.lib_java.debug.DebugException;


public abstract class ObserverValue<T> extends Observer<Void, ObservableValue<T>.Access>{

public ObserverValue(Object owner){
    super(owner, null);
}

@Override
final public void onChanged(ObservableValue<T>.Access access){
    if(!isSubscribeValid()){
        return;
    }
    if(isCanceled()){
        onCancel();
    } else {
        onComplete(access.getValue());
    }
}

public void onComplete(T t){

DebugException.start().notImplemented(getOwner()).end();

}

public void onCancel(){

DebugException.start().notImplemented(getOwner()).end();

}

}
