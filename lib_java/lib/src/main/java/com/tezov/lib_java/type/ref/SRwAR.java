/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.type.ref;

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.util.UtilsString;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java.async.Handler;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.type.runnable.RunnableThread;
import com.tezov.lib_java.wrapperAnonymous.ConsumerW;

import java.util.concurrent.TimeUnit;

//Strong with Auto Release
public class SRwAR<T> extends Ref<T>{
private final long delay;
private final TimeUnit unit;
private final ConsumerW<T> destroyConsumer;
private RunnableThread runnableThread = null;
private boolean isLock = false;
private long lastUsedTimeStamp;
private T t;

public SRwAR(T ref, long delay){
    this(ref, delay, (ConsumerW<T>)null);
}
public SRwAR(T ref, long delay, ConsumerW<T> destroyConsumer){
    this(ref, delay, TimeUnit.SECONDS, destroyConsumer);
}
public SRwAR(T ref, long delay, TimeUnit unit){
    this(ref, delay, unit, null);
}
public SRwAR(T ref, long delay, TimeUnit unit, ConsumerW<T> destroyConsumer){
    this.delay = delay;
    this.unit = unit;
    this.t = ref;
    this.destroyConsumer = destroyConsumer;
    tick();
    if(ref != null){
        start();
    }

}

public void start(){
    if(runnableThread == null){
        runnableThread = new RunnableThread(this, Handler.LOW()){
            @Override
            public void runSafe(){
                if(isLock){
                    tick();
                    post();
                } else {
                    long diff = Clock.MilliSecond.now() - lastUsedTimeStamp;
                    if(diff < TimeUnit.MILLISECONDS.convert(delay, unit)){
                        post();
                    } else {
                        if(destroyConsumer != null){
                            destroyConsumer.accept(t);
                        }
                        t = null;
                    }
                }
            }
        };
        runnableThread.post(delay, unit);
    }
}
public void stop(){
    if(runnableThread != null){
        runnableThread.cancel();
        runnableThread = null;
    }
}

@Override
public boolean isNull(){
    return t == null;
}

@Override
public boolean isNotNull(){
    return t != null;
}

public void lock(boolean flag){
    this.isLock = flag;
}

public void tick(){
    this.lastUsedTimeStamp = Clock.MilliSecond.now();
}

@Override
public T get(){
    tick();
    return t;
}

@Override
public void set(Object referent){

DebugException.start().notImplemented().end();

}

}
