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
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.type.ref.WR;
import com.tezov.lib_java.wrapperAnonymous.UncaughtExceptionHandlerW;

//IMPROVE add timeout Option
public abstract class RunnableFuture<T> extends RunnableW{
private final WR<Object> ownerWR;
boolean isCanceled = false;
private Handler handler = null;
private T value = null;
private Throwable e = null;

public RunnableFuture(Object owner){
    this.ownerWR = WR.newInstance(owner);
}

public T getValue(){
    return value;
}
protected void setValue(T value){
    this.value = value;
    done();
}
public Throwable getException(){
    return e;
}
protected void setException(Throwable e){
    this.e = e;
    done();
}
public void done(){
    handler.quitSafely();
    handler = null;
}
protected void cancel(){
    this.isCanceled = true;
}
public boolean isCanceled(){
    return isCanceled;
}
public boolean isAlive(){
    return (handler != null) && (handler.isAlive());
}

public RunnableFuture<T> join(){
    if(handler != null){
        handler.join();
    }
    return this;
}
public RunnableFuture<T> start(){
    return start(true);
}
public RunnableFuture<T> start(boolean join){
    if(handler == null){
        handler = Handler.newHandler(ownerWR.get());
        handler.setUncaughtExceptionHandler(new UncaughtExceptionHandlerW(){
            @Override
            public void uncaughtException(Thread t,Throwable e){
DebugException.start().log(e).end();
            }
        });
        handler.post(ownerWR.get(), this);
        if(join){
            handler.join();
        }
    }
    return this;
}

}
