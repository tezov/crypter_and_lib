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
import java.util.Set;
import com.tezov.lib_java.async.Handler;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.wrapperAnonymous.PredicateW;
import com.tezov.lib_java.wrapperAnonymous.UncaughtExceptionHandlerW;
import com.tezov.lib_java.type.ref.WR;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

public class RunnableQueue<R extends RunnableW> extends RunnableW{
private final WR<Object> ownerWR;
private final boolean isHandlerAutoCreate;
private final boolean isHandlerAutoQuit;
protected Queue<R> queue;
protected R currentRunnable = null;
private RunnableQueue<? extends RunnableW> parent = null;
private boolean isHandlerOwner = true;
private Handler handler = null;
private UncaughtExceptionHandlerW uncaughtExceptionHandler;
private RunnableW onStartRunnable = null;
private RunnableW onDoneRunnable = null;

public RunnableQueue(Object owner){
    this(owner, Handler.PRIMARY());
}
public RunnableQueue(Object owner, Handler handler){
    this(owner, false, false);
    this.handler = handler;
    this.isHandlerOwner = false;
}

public RunnableQueue(Object owner, boolean isHandlerAutoCreate, boolean isHandlerAutoQuit){
    ownerWR = WR.newInstance(owner);
    this.isHandlerAutoCreate = isHandlerAutoCreate;
    this.isHandlerAutoQuit = isHandlerAutoQuit;
    this.queue = new LinkedList<>();
}

public void link(RunnableQueue<? extends RunnableW> parent){
    this.parent = parent;
}

public RunnableQueue<R> setUncaughtExceptionHandler(UncaughtExceptionHandlerW eh){
    this.uncaughtExceptionHandler = eh;
    if(handler != null){
        if(isHandlerOwner){
            handler.setUncaughtExceptionHandler(eh);
        } else {
DebugException.start().log("try to setUncaughtExceptionHandler, but you are not the handler owner").end();
        }

    }
    return this;
}

public boolean isBusy(){
    synchronized(this){
        return currentRunnable != null;
    }
}
public boolean isEmpty(){
    synchronized(this){
        return queue.isEmpty();
    }
}
public R element(){
    synchronized(this){
        return queue.element();
    }
}
public R current(){
    synchronized(this){
        return currentRunnable;
    }
}
public boolean contain(R r){
    synchronized(this){
        return (currentRunnable == r) || queue.contains(r);
    }
}
public boolean contain(PredicateW<R> predicate){
    synchronized(this){
        if((currentRunnable != null) && predicate.test(currentRunnable)){
            return true;
        }
        for(R r: queue){
            if(predicate.test(r)){
                return true;
            }
        }
        return false;
    }
}
public int size(){
    synchronized(this){
        return queue.size() + (currentRunnable != null ? 1 : 0);
    }
}
public boolean forEachInQueue(PredicateW<R> predicate){
    synchronized(this){
        for(R r: queue){
            if(predicate.test(r)){
                return true;
            }
        }
        return false;
    }
}

public boolean hasHandler(){
    return handler != null;
}
private void createHandler(){
    if(parent != null){
        isHandlerOwner = false;
        handler = parent.getHandler();
    } else {
        isHandlerOwner = true;
        handler = Handler.newHandler(ownerWR.get()).setUncaughtExceptionHandler(uncaughtExceptionHandler);
    }
}
public Handler getHandler(){
    if(((handler == null) || !handler.isAlive()) && isHandlerAutoCreate){
        createHandler();
    }
    return handler;

}
public RunnableQueue<R> setHandler(Handler handler){
    this.isHandlerOwner = true;
    this.handler = handler;
    return this;
}
protected void post(R runnable){
    post(null, null, runnable);
}
protected void post(Long delay, TimeUnit unit, R runnable){
    if(!isHandlerAutoCreate && (handler == null)){
        runnable.run();
    } else if(delay == null){
        getHandler().post(this, runnable);
    } else {
        getHandler().post(this, delay, unit, runnable);
    }
}
public void clear(){
    synchronized(this){
        if((currentRunnable != null) && (handler != null)){
            handler.cancel(this, currentRunnable);
            currentRunnable = null;
        }
        onStartRunnable = null;
        queue.clear();
        onDoneRunnable = null;
    }
}
public void quitHandler(){
    if(handler != null){
        if(isHandlerOwner){
            handler.quit();
        }
        handler = null;
    }
}
public void quitAndClear(){
    synchronized(this){
        quitHandler();
        clear();
    }
}

public void add(R runnable){
    synchronized(this){
        if(runnable instanceof RunnableQueue){
            ((RunnableQueue<? extends RunnableW>)runnable).link(this);
        }
        queue.offer(runnable);
    }
}
public boolean removeRunnable(R runnable){
    synchronized(this){
        return queue.remove(runnable);
    }
}
public void setOnStart(RunnableW runnable){
    synchronized(this){
        onStartRunnable = runnable;
    }
}

@Override
public void runSafe(){
    start(null, null);
}
final public R start(){
    return start(null, null);
}
final public R start(Long delay_ms){
    return start(delay_ms, TimeUnit.MILLISECONDS);
}
public R start(Long delay, TimeUnit unit){
    synchronized(this){
        if((currentRunnable == null) && (onStartRunnable != null)){
            onStartRunnable.run();
            onStartRunnable = null;
        }
        currentRunnable = queue.remove();
        post(delay, unit, currentRunnable);
        return currentRunnable;
    }
}
public R next(){
    synchronized(this){
        currentRunnable = queue.remove();
        post(currentRunnable);
        return currentRunnable;
    }
}
public R repeat(){
    synchronized(this){
        post(currentRunnable);
        return currentRunnable;
    }
}
public void skip(int number){
    synchronized(this){
        for(int i = number; i > 0; i--){
            currentRunnable = queue.remove();
        }
        currentRunnable = queue.remove();
        post(currentRunnable);
    }
}
public void skipNext(){
    synchronized(this){
        currentRunnable = queue.remove();
        currentRunnable = queue.remove();
        post(currentRunnable);
    }
}
public void done(){
    synchronized(this){
        currentRunnable = null;
        if(isHandlerAutoQuit){
            quitHandler();
        }
        if(onDoneRunnable != null){
            onDoneRunnable.run();
            onDoneRunnable = null;
        }
    }
}
public void setOnDone(RunnableW runnable){
    synchronized(this){
        onDoneRunnable = runnable;
    }
}

@Override
protected void finalize() throws Throwable{
    if(isHandlerAutoQuit){
        quitHandler();
    }
    super.finalize();
}

}
