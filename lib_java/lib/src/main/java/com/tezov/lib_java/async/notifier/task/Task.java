/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.async.notifier.task;

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
import com.tezov.lib_java.async.notifier.Notifier;
import com.tezov.lib_java.async.notifier.observer.Observer;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.collection.ListOrObject;
import com.tezov.lib_java.wrapperAnonymous.SupplierW;

import java.util.Iterator;

public class Task<EVENT, OBSERVABLE extends Notifier.defObservable<EVENT>, ACCESS extends Notifier.defObservable.Access<EVENT>>{

private final EVENT event;
private final Notifier<EVENT> notifier;
private ListOrObject<Notifier.Subscription> subscriptions = null;
private boolean canceled = false;
private Observable observable = null;

public Task(EVENT event, OBSERVABLE observable){
DebugTrack.start().create(this).end();
    this.event = event;
    this.notifier = new Notifier<>(observable, true);
}

protected Task<EVENT, OBSERVABLE, ACCESS> me(){
    return this;
}

public Notifier<EVENT> getNotifier(){
    return notifier;
}

public OBSERVABLE getNotifierObservable(){
    return notifier.getObservable();
}

public ACCESS getAccess(){
    return notifier.obtainAccess(this, event);
}

public EVENT getEvent(){
    return event;
}

public boolean isCanceled(){
    return canceled;
}

public void cancel(){
    canceled = true;
}

public Notifier.Subscription observe(Observer<EVENT, ACCESS> observer){
    if(subscriptions == null){
        subscriptions = new ListOrObject<>();
    }
    Notifier.Subscription subscription = notifier.register(observer, new SupplierW<Notifier.Subscription>(){
        @Override
        public Notifier.Subscription get(){
            return new Subscription(me());
        }
    });
    subscriptions.add(subscription);
    return subscription;
}
public void unObserve(Object owner){
    if(subscriptions != null){
        Iterator<Notifier.Subscription> it = subscriptions.iterator();
        while(it.hasNext()){
            if(owner.equals(it.next().getOwner())){
                it.remove();
            }
        }
        notifier.unregister(owner);
    }
}
public void unObserveAll(){
    if(subscriptions != null){
        subscriptions.clear();
        notifier.unregisterAll();
    }
}

protected Observable newObservable(){
    return new Observable();
}

public synchronized Observable getObservable(){
    if(observable == null){
        observable = newObservable();
    }
    return observable;
}

public void notifyCanceled(){

    if(!isCanceled()){
DebugException.start().explode("task is not cancel...").end();
    }

    getNotifier().notifyEvent(getAccess());
}

@Override
protected void finalize() throws Throwable{
    if(subscriptions != null){
        for(Notifier.Subscription subscription: subscriptions){
            subscription.unsubscribe();
        }
    }
DebugTrack.start().destroy(this).end();
    super.finalize();
}

public static class Subscription extends Notifier.Subscription{
    private final Task task;

    protected Subscription(Task task){
        this.task = task;
    }

    @Override
    public boolean unsubscribe(){
        return super.unsubscribe();
    }

    @Override
    public boolean isCanceled(){
        return task.isCanceled();
    }

}

public class Observable{
    protected Observable(){
DebugTrack.start().create(this).end();
    }

    public Notifier.Subscription observe(Observer<EVENT, ACCESS> observer){
        return me().observe(observer);
    }
    public void unObserve(Object owner){
        me().unObserve(owner);
    }
    public void unObserveAll(){
        me().unObserveAll();
    }

    public EVENT getEvent(){
        return me().getEvent();
    }

    public void cancel(){
        me().cancel();
    }

    public boolean isCanceled(){
        return me().isCanceled();
    }

    @Override
    protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
        super.finalize();
    }

}

}
