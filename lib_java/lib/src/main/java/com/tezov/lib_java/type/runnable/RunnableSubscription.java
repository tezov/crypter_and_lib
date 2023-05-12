/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.type.runnable;

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
import com.tezov.lib_java.async.notifier.Subscription;

public abstract class RunnableSubscription extends RunnableW{
private Subscription subscription = null;
private boolean subscribeValid = true;
private boolean unsubscribeRequested = false;

public void bind(Subscription subscription){
    this.subscription = subscription;
}

public <S extends Subscription> S getSubscription(){
    return (S)subscription;
}

public void unsubscribe(){
    unsubscribeRequested = true;
    if((subscription != null) && subscription.unsubscribe()){
        subscription = null;
    }
}

private boolean isSubscribeValid(){
    if(!subscribeValid){
        return false;
    }
    if(unsubscribeRequested){
        unsubscribeRequested = false;
        subscribeValid = false;
    }
    return subscribeValid;
}

@Override
final public void runSafe(){
    if(isSubscribeValid()){
        onComplete();
    }
}

public abstract void onComplete();

}
