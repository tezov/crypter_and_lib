/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.async.notifier.observer.event;

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
import com.tezov.lib_java.async.notifier.observable.ObservableEventE;
import com.tezov.lib_java.async.notifier.observer.Observer;
import com.tezov.lib_java.debug.DebugException;

public abstract class ObserverEventE<EVENT, OBJECT> extends Observer<EVENT, ObservableEventE<EVENT, OBJECT>.Access>{
public ObserverEventE(Object owner){
    super(owner, null);
}

public ObserverEventE(Object owner, EVENT event){
    super(owner, event);
}

@Override
final public void onChanged(ObservableEventE<EVENT, OBJECT>.Access access){
    if(!isSubscribeValid()){
        return;
    }
    if(isCanceled()){
        onCancel(access.getEvent());
    } else {
        java.lang.Throwable e = access.getException();
        if(e == null){
            onComplete(access.getEvent(), access.getValue());
        } else {
            onException(access.getEvent(), access.getValue(), e);
        }
    }
}

public void onCancel(EVENT event){

DebugException.start().notImplemented(getOwner()).end();

}

public void onComplete(EVENT event, OBJECT object){

DebugException.start().notImplemented(getOwner()).end();

}

public void onException(EVENT event, OBJECT object, java.lang.Throwable e){

DebugException.start().notImplemented(getOwner()).end();

}

}
