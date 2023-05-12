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
import com.tezov.lib_java.async.notifier.observable.ObservableValueE;
import com.tezov.lib_java.async.notifier.observer.Observer;
import com.tezov.lib_java.debug.DebugException;

public abstract class ObserverValueE<T> extends Observer<Void, ObservableValueE<T>.Access>{

public ObserverValueE(Object owner){
    super(owner, null);
}

@Override
final public void onChanged(ObservableValueE<T>.Access access){
    if(!isSubscribeValid()){
        return;
    }
    if(isCanceled()){
        onCancel();
    } else {
        java.lang.Throwable e = access.getException();
        if(e == null){
            onComplete(access.getValue());
        } else {
            onException(access.getValue(), access.getException());
        }
    }
}

public void onComplete(T t){

DebugException.start().notImplemented(getOwner()).end();

}
public void onException(T t, java.lang.Throwable e){

DebugException.start().notImplemented(getOwner()).end();

}
public void onCancel(){

DebugException.start().notImplemented(getOwner()).end();

}

}
