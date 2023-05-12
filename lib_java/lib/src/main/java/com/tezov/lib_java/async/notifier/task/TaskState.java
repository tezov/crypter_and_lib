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
import com.tezov.lib_java.async.notifier.observer.Observer;
import com.tezov.lib_java.async.notifier.observer.state.ObserverState;
import com.tezov.lib_java.async.notifier.observer.state.ObserverStateE;
import com.tezov.lib_java.async.notifier.observer.value.ObserverValue;
import com.tezov.lib_java.async.notifier.observer.value.ObserverValueE;
import com.tezov.lib_java.debug.DebugException;

public class TaskState extends TaskValue<Void>{

public static TaskState.Observable Complete(){
    TaskState task = new TaskState();
    task.notifyComplete();
    return task.getObservable();
}
public static TaskState.Observable Exception(String e){
    return Exception(new Throwable(e));
}
public static TaskState.Observable Exception(java.lang.Throwable e){
    TaskState task = new TaskState();
    task.notifyException(e);
    return task.getObservable();
}
@Override
protected TaskState me(){
    return this;
}
public Notifier.Subscription observe(ObserverStateE observer){
    return observeSuper(observer);
}
public Notifier.Subscription observe(ObserverState observer){
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

public class Observable extends TaskValue<Void>.Observable{
    @Override
    final public Notifier.Subscription observe(ObserverValueE<Void> observer){

DebugException.start().explode("please use observe(ObserverValueE<VALUE> observer) instead").end();

        return null;
    }
    @Override
    final public Notifier.Subscription observe(ObserverValue<Void> observer){

DebugException.start().explode("please use observe(ObserverValueE<VALUE> observer) instead").end();

        return null;
    }
    public Notifier.Subscription observe(ObserverStateE observer){
        return me().observe(observer);
    }
    public Notifier.Subscription observe(ObserverState observer){
        return me().observe(observer);
    }

}

}
