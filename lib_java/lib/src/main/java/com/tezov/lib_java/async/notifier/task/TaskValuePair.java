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
import com.tezov.lib_java.async.notifier.observer.value.ObserverValue;
import com.tezov.lib_java.async.notifier.observer.value.ObserverValueE;
import com.tezov.lib_java.async.notifier.observer.valuePair.ObserverValueEPair;
import com.tezov.lib_java.async.notifier.observer.valuePair.ObserverValuePair;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.type.primaire.Pair;

public class TaskValuePair<F, S> extends TaskValue<Pair<F, S>>{
@Override
protected TaskValuePair<F, S> me(){
    return this;
}
public Notifier.Subscription observe(ObserverValueEPair<F, S> observer){
    return observeSuper(observer);
}
public Notifier.Subscription observe(ObserverValuePair<F, S> observer){
    return observeSuper((Observer)observer);
}
@Override
protected Observable newObservable(){
    return new Observable();
}
@Override
public TaskValuePair<F, S>.Observable getObservable(){
    return (TaskValuePair<F, S>.Observable)super.getObservable();
}
public class Observable extends TaskValue<Pair<F, S>>.Observable{
    @Override
    final public Notifier.Subscription observe(ObserverValueE<Pair<F, S>> observer){

DebugException.start().explode("please use observe(ObserverValueE<VALUE> observer) instead").end();

        return null;
    }
    @Override
    final public Notifier.Subscription observe(ObserverValue<Pair<F, S>> observer){

DebugException.start().explode("please use observe(ObserverValueE<VALUE> observer) instead").end();

        return null;
    }
    public Notifier.Subscription observe(ObserverValueEPair<F, S> observer){
        return me().observe(observer);
    }
    public Notifier.Subscription observe(ObserverValuePair<F, S> observer){
        return me().observe(observer);
    }

}

}
