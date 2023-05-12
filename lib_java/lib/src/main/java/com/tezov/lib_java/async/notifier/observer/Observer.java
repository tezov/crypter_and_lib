/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.async.notifier.observer;

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.util.UtilsString;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java.async.Handler;
import com.tezov.lib_java.async.notifier.Subscription;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.ref.Ref;
import com.tezov.lib_java.type.ref.WR;
import com.tezov.lib_java.type.runnable.RunnableW;
import com.tezov.lib_java.wrapperAnonymous.ConsumerW;

public abstract class Observer<EVENT, OBSERVABLE>{
private Handler handler = null;
private boolean enable = true;
private WR ownerWR;
private EVENT event;
private Subscription subscription = null;
private boolean subscribeValid = true;
private boolean unsubscribeRequested = false;

private static ConsumerW<Observer<?, ?>> onNewObserverConsumer = null;
public static void setOnNewObserverConsumer(ConsumerW<Observer<?, ?>> onNewObserverConsumer){
    Observer.onNewObserverConsumer = onNewObserverConsumer;
}

public Observer(Object owner, EVENT event){
DebugTrack.start().create(this).end();
    if(owner == null){
DebugException.start().explode(new NullPointerException()).end();
        return;
    }
    this.ownerWR = WR.newInstance(owner);
    this.event = event;
    if(onNewObserverConsumer != null){
        onNewObserverConsumer.accept(this);
    }
}

public static void notify(Observer observer, Object o){
    observer.notify(o);
}

public static void notify(Ref<Observer> observer, Object o){
    if(Ref.isNotNull(observer)){
        notify(observer.get(), o);
    }
}

public boolean isEnabled(){
    return enable;
}

public void enable(boolean flag){
    this.enable = flag;
}

public <OWNER> OWNER getOwner(){
    return (OWNER)Ref.get(ownerWR);
}

public boolean hasOwner(){
    return Ref.isNotNull(ownerWR);
}

public void setHandler(Handler handler){
    this.handler = handler;
}

public boolean hasHandler(){
    return handler != null;
}

public EVENT getEvent(){
    return event;
}

public <O extends Observer<EVENT, OBSERVABLE>> O setEvent(EVENT event){
    this.event = event;
    return (O)this;
}

public boolean hasEvent(){
    return event != null;
}

public void bind(Subscription subscription){
    this.subscription = subscription;
}

public void unsubscribe(){
    unsubscribeRequested = true;
    if((subscription != null) && subscription.unsubscribe()){
        subscription = null;
    }
}

protected boolean isSubscribeValid(){
    if(!subscribeValid){
        return false;
    }
    if(unsubscribeRequested){
        unsubscribeRequested = false;
        subscribeValid = false;
    }
    return subscribeValid;
}

public boolean isCanceled(){
    if(subscription == null){
        return false;
    } else {
        return subscription.isCanceled();
    }
}

final public void notify(OBSERVABLE t){
    if(!isEnabled()){
        return;
    }
    if(hasHandler()){
        handler.post(new RunnableW(){
            @Override
            public void runSafe(){
                onChanged(t);
            }
        });
    } else {
        onChanged(t);
    }
}

public abstract void onChanged(OBSERVABLE t);

public RunnableSubscription newSubscription(){
    return new RunnableSubscription(this);
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

private static class RunnableSubscription extends com.tezov.lib_java.type.runnable.RunnableSubscription{
    WR<Observer> observerWR;
    public RunnableSubscription(Observer observer){
        observerWR = WR.newInstance(observer);
    }
    @Override
    public void onComplete(){
        unsubscribe();
        if(Ref.isNotNull(observerWR)){
            observerWR.get().unsubscribe();
        }
    }

}

}
