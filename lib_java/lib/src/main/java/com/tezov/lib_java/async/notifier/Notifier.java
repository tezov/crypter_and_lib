/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.async.notifier;

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.util.UtilsString;
import java.util.LinkedList;
import java.util.Set;

import com.tezov.lib_java.async.notifier.observer.Observer;
import com.tezov.lib_java.toolbox.Compare;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.collection.ConcurrentLinkedSet;
import com.tezov.lib_java.type.ref.Ref;
import com.tezov.lib_java.type.ref.WR;
import com.tezov.lib_java.wrapperAnonymous.SupplierW;

import java.util.Iterator;
import java.util.List;

public class Notifier<EVENT>{
private final defObservable observable;
private ConcurrentLinkedSet<Observer> observers = null;
private boolean notifyOnAddObserver;

public Notifier(defObservable observable, boolean notifyOnAddObserver){
DebugTrack.start().create(this).end();
    this.observable = observable;
    this.notifyOnAddObserver = notifyOnAddObserver;
    observable.attachTo(this);
}

public void notifyOnAddObserver(boolean value){
    synchronized(this){
        notifyOnAddObserver = value;
    }
}

public <OBSERVABLE extends defObservable<EVENT>> OBSERVABLE getObservable(){
    synchronized(this){
        return (OBSERVABLE)observable;
    }
}

public <ACCESS extends defObservable.Access<EVENT>> ACCESS obtainAccess(Object requester, EVENT event){
    synchronized(this){
        return (ACCESS)observable.obtainAccess(event);
    }
}

public Subscription register(Observer<EVENT, ? extends defObservable.Access<EVENT>> observer){
    synchronized(this){
        return register(observer, Subscription::new);
    }
}
public Subscription register(Observer<EVENT, ? extends defObservable.Access<EVENT>> observer, SupplierW<Subscription> supplierSupplier){
    synchronized(this){
        if(observers == null){
            observers = new ConcurrentLinkedSet<>();
        }
        observers.add(observer);
        Subscription subscription = supplierSupplier.get().attach(observer).attach(this);
        observer.bind(subscription);
        if(notifyOnAddObserver){
            if(observer.hasEvent()){
                if(observable.hasAccess(observer.getEvent())){
                    defObservable.Access access = observable.obtainAccess(observer.getEvent());
                    if((access != null) && access.hasValue()){
                        notifyObserver(observer, access);
                    }
                }
            } else {
                List<defObservable.Access> accessList = observable.getAccessList();
                if(accessList != null){
                    for(defObservable.Access access: accessList){
                        if(access.hasValue()){
                            notifyObserver(observer, access);
                        }
                    }
                }
            }
        }
        return subscription;
    }
}

public void unregister(Observer<EVENT, ? extends defObservable.Access<EVENT>> observer){
    synchronized(this){
        if(observers == null){
            return;
        }
        if(observer == null){
            observers.clear();
            observers = null;
            return;
        }
        Iterator<Observer> iterator = observers.iterator();
        while(iterator.hasNext()){
            Observer o = iterator.next();
            if((o == observer) || !o.hasOwner()){
                iterator.remove();
            }
        }
        if(observers.size() <= 0){
            observers = null;
        }
    }
}

public void unregister(Object owner){
    synchronized(this){
        if(observers == null){
            return;
        }
        Iterator<Observer> iterator = observers.iterator();
        while(iterator.hasNext()){
            Observer o = iterator.next();
            if(!o.hasOwner() || o.getOwner().equals(owner)){
                iterator.remove();
            }
        }
        if(observers.size() <= 0){
            observers = null;
        }
    }
}

public void unregisterAll(){
    synchronized(this){
        if(observers == null){
            return;
        }
        observers.clear();
        observers = null;
    }
}

public void notifyEvent(defObservable.Access<EVENT> access){
    synchronized(this){
        if(observers == null){
            return;
        }
        if(!observable.isValid(access)){

DebugException.start().explode("Only observable who belong to this Notifier can notify observer").end();

            return;
        }
        for(Observer o: observers){
            notifyObserver(o, access);
        }
    }
}

private void notifyObserver(Observer observer, defObservable.Access access){
    if(!observer.hasOwner()){
        unregister(observer);
    } else {
        if(Compare.equals(observer.getEvent(), access.getEvent()) || !observer.hasEvent()){
            Observer.notify(observer, access);
        }
    }
}

public boolean hasObserver(EVENT event){
    synchronized(this){
        if(observers == null){
            return false;
        }
        for(Observer o: observers){
            if(Compare.equals(o.getEvent(), event) || !o.hasEvent()){
                return true;
            }
        }
        return false;
    }
}

public boolean hasObserver(){
    synchronized(this){
        if(observers == null){
            return false;
        } else {
            return observers.size() > 0;
        }
    }
}

public <O extends Observer> ConcurrentLinkedSet<O> getObservers(){
    synchronized(this){
        return (ConcurrentLinkedSet<O>)observers;
    }
}

@Override
protected void finalize() throws Throwable{
    unregisterAll();
DebugTrack.start().destroy(this).end();
    super.finalize();
}

public interface defObservable<EVENT>{
    void attachTo(Notifier notifier);

    boolean isValid(Access<EVENT> access);

    boolean hasAccess(EVENT event);

    <ACCESS extends Access<EVENT>> ACCESS obtainAccess(EVENT event);

    <ACCESS extends Access<EVENT>> List<ACCESS> getAccessList();

    void clearAccess();

    abstract class Access<EVENT>{
        public Access(){
DebugTrack.start().create(this).end();
        }

        public abstract EVENT getEvent();

        public abstract boolean hasValue();

        public abstract <O> O getValue();

        @Override
        protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
            super.finalize();
        }

    }

}

public static class Subscription extends com.tezov.lib_java.async.notifier.Subscription<Notifier>{
    private WR<Observer> observerWR = null;

    private Subscription attach(Observer observer){
        this.observerWR = WR.newInstance(observer);
        return this;
    }

    public Object getOwner(){
        if(Ref.isNotNull(observerWR)){
            return observerWR.get().getOwner();
        } else {
            return null;
        }
    }

    @Override
    public boolean unsubscribe(){
        if(Ref.isNull(observerWR)){
            return false;
        } else {
            getRef().unregister(observerWR.get());
            return true;
        }
    }

    @Override
    public boolean isCanceled(){
        return false;
    }

}

}
