/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.type.ref;

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
import com.tezov.lib_java.async.notifier.Notifier;
import com.tezov.lib_java.async.notifier.Subscription;
import com.tezov.lib_java.async.notifier.observable.ObservableValue;
import com.tezov.lib_java.async.notifier.observer.value.ObserverValue;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugString;

//Strong notify onChange
public class SRwNOC<T> extends Ref<T>{
private final Value<T> value;
private boolean notificationEnabled = true;
private Notifier<Void> onSetNotifier = null;

public SRwNOC(T t){
    value = newValue();
    value.previous = t;
    value.current = t;
}

public SRwNOC<T> notificationEnable(boolean flag){
    notificationEnabled = flag;
    return this;
}

@Override
public T get(){
    return value.current;
}

protected <V extends Value<T>> V getValue(){
    return (V)value;
}

public Subscription observe(ObserverValue<? extends Value<T>> observer){
    if(onSetNotifier == null){
        onSetNotifier = new Notifier<>(new ObservableValue<Value<T>>(), true);
    }
    return onSetNotifier.register(observer);
}

public void unObserve(Object owner){
    if(hasObserver()){
        onSetNotifier.unregister(owner);
        if(!onSetNotifier.hasObserver()){
            onSetNotifier = null;
        }
    }
}
public void unObserveAll(){
    if(hasObserver()){
        onSetNotifier.unregisterAll();
        onSetNotifier = null;
    }
}
public boolean hasObserver(){
    return onSetNotifier != null;
}

protected Value<T> newValue(){
    return new Value<>();
}

@Override
public void set(T t){
    value.set(t);
    if(notificationEnabled && (onSetNotifier != null)){
        ObservableValue<Value<T>>.Access access = onSetNotifier.obtainAccess(this, null);
        access.setValue(value);
    }
}

@Override
public int hashCode(){
    return get().hashCode();
}

public static class Value<T>{
    public T previous;
    public T current;

    public Value<T> set(T t){
        this.previous = current;
        this.current = t;
        return this;
    }

    public DebugString toDebugString(){
        DebugString data = new DebugString();
        data.append("previous", previous);
        data.append("current", current);
        return data;
    }

    final public void toDebugLog(){
DebugLog.start().send(toDebugString()).end();
    }

}

}
