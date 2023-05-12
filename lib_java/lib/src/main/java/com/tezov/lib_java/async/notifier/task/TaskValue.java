/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.async.notifier.task;

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
import com.tezov.lib_java.async.notifier.Notifier;
import com.tezov.lib_java.async.notifier.observable.ObservableValueE;
import com.tezov.lib_java.async.notifier.observer.Observer;
import com.tezov.lib_java.async.notifier.observer.value.ObserverValue;
import com.tezov.lib_java.async.notifier.observer.value.ObserverValueE;
import com.tezov.lib_java.debug.DebugException;

public class TaskValue<VALUE> extends Task<Void, ObservableValueE<VALUE>, ObservableValueE<VALUE>.Access>{
public TaskValue(){
    super(null, new ObservableValueE<>());
    getNotifier().notifyOnAddObserver(true);
}
public static <VALUE> TaskValue<VALUE>.Observable Complete(VALUE value){
    TaskValue<VALUE> task = new TaskValue<>();
    task.notifyComplete(value);
    return task.getObservable();
}
public static <VALUE> TaskValue<VALUE>.Observable Exception(VALUE value, java.lang.Throwable e){
    TaskValue<VALUE> task = new TaskValue<>();
    task.notifyException(value, e);
    return task.getObservable();
}
public static <VALUE> TaskValue<VALUE>.Observable Exception(VALUE value, String e){
    TaskValue<VALUE> task = new TaskValue<>();
    task.notifyException(value, e);
    return task.getObservable();
}
@Override
protected TaskValue<VALUE> me(){
    return this;
}
@Override
final public Notifier.Subscription observe(Observer<Void, ObservableValueE<VALUE>.Access> observer){

DebugException.start().explode("please use observe(ObserverValueE<VALUE> observer) instead").end();

    return null;
}
final protected Notifier.Subscription observeSuper(Observer<Void, ObservableValueE<VALUE>.Access> observer){
    return super.observe(observer);
}
public Notifier.Subscription observe(ObserverValueE<VALUE> observer){
    return observeSuper(observer);
}
public Notifier.Subscription observe(ObserverValue<VALUE> observer){
    return observeSuper((Observer)observer);
}
@Override
protected Observable newObservable(){
    return new Observable();
}
@Override
public Observable getObservable(){
    return (Observable)super.getObservable();
}
public void notifyComplete(){
    getAccess().set(null, null);
}
public void notifyComplete(VALUE value){
    getAccess().set(value, null);
}
public void notifyComplete(VALUE value, java.lang.Throwable e){
    getAccess().set(value, e);
}
public void notifyException(VALUE value, java.lang.Throwable e){
    getAccess().set(value, e);
}
public void notifyException(VALUE value, String e){
    getAccess().set(value, new java.lang.Throwable(e));
}
public void notifyException(java.lang.Throwable e){
    getAccess().set(null, e);
}
public void notifyException(String e){
    getAccess().set(null, new java.lang.Throwable(e));
}

public class Observable extends Task.Observable{
    @Override
    final public Notifier.Subscription observe(Observer observer){

DebugException.start().explode("please use observe(ObserverValueE<VALUE> observer) instead").end();

        return null;
    }
    public Notifier.Subscription observe(ObserverValueE<VALUE> observer){
        return me().observe(observer);
    }
    public Notifier.Subscription observe(ObserverValue<VALUE> observer){
        return me().observe(observer);
    }

}

}
