/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.type.runnable;

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
import com.tezov.lib_java.application.AppUIDGenerator;
import com.tezov.lib_java.async.Handler;
import com.tezov.lib_java.async.notifier.task.TaskState;
import com.tezov.lib_java.async.notifier.task.TaskValue;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java.wrapperAnonymous.PredicateW;
import com.tezov.lib_java.type.collection.Arguments;

import java.util.concurrent.TimeUnit;

//@DebugLogDataFilterArray({
//     @DebugLogDataFilter("selectAlias"),
//     @DebugLogDataFilter("signIn"),
//     @DebugLogDataFilter("onSignIn"),
//     @DebugLogDataFilter("initiateCreateUser"),
//     @DebugLogDataFilter("createUserReference"),
//     @DebugLogDataFilter("getUserReference"),
//
//     @DebugLogDataFilter("getSignState"),
//     @DebugLogDataFilter("setSigningState"),
//
//     @DebugLogDataFilter("signOut"),
//     @DebugLogDataFilter("onSignOut"),
//     @DebugLogDataFilter("closeResource"),
//
//     @DebugLogDataFilter("beforeOpen fbUserTables"),
//     @DebugLogDataFilter("build cipher"),
//     @DebugLogDataFilter("retrieve cipher retrofit.data"),
//     @DebugLogDataFilter("generate cipher"),
//     @DebugLogDataFilter("retrieve retrofit.data from lite version"),
//
//     @DebugLogDataFilter("start client"),
//     @DebugLogDataFilter("start server"),
//     @DebugLogDataFilter("search server"),
//
//     @DebugLogDataFilter("onNewClient"),
//     @DebugLogDataFilter("onTransferStart"),
//
//     @DebugLogDataFilter("buy"),
//     @DebugLogDataFilter("isOwned"),
//     @DebugLogDataFilter("purchaseFind"),
//     @DebugLogDataFilter("isOwnedNoAds"),
//     @DebugLogDataFilter("showSuggestBuy"),
//
//})
public class RunnableGroup extends RunnableQueue<RunnableGroup.Action>{
public final static int NO_LABEL = -1;
private final static int KEY_VALUE = AppUIDGenerator.nextInt();
private final static int KEY_EXCEPTION = AppUIDGenerator.nextInt();
private String name = null;
private Arguments<?> arg = null;
public RunnableGroup(Object owner, Handler handler){
    super(owner, handler);
}
public RunnableGroup(Object owner){
    super(owner);
}
public RunnableGroup(Object owner, boolean isHandlerAutoCreate, boolean isHandlerAutoQuit){
    super(owner, isHandlerAutoCreate, isHandlerAutoQuit);
}
private static DebugString toDebugStringName(RunnableGroup gr){
    DebugString data = new DebugString();
    if(gr.name != null){
        data.appendHashcodeString(gr.name, gr);
    } else {
        data.appendHashcodeString("", gr);
    }
    return data;
}
private static DebugString toDebugStringName(Action action){
    DebugString data = new DebugString();
    if(action.name != null){
        data.appendHashcodeString(action.name, action);
    } else {
        data.appendHashcodeString("", action);
    }
    return data;
}
public RunnableGroup name(String name){
    this.name = name;
    return this;
}
public String name(){
    return name;
}
public int label(){
    return AppUIDGenerator.nextInt();
}
public int key(){
    return AppUIDGenerator.nextInt();
}
@Override
public void clear(){
    super.clear();
    getArguments().clear();
}

public <K> Arguments<K> clearArguments(){
    return (Arguments<K>)getArguments().clear();
}
public boolean hasArguments(){
    return arg != null;
}
public <K> Arguments<K> getArguments(){
    if(!hasArguments()){
        arg = new Arguments<>();
    }
    return (Arguments<K>)arg;
}
public <K> void put(K key, Object value){
    getArguments().put(key, value);
}
public <K, V> V get(K key){
    return getArguments().getValue(key);
}
public void putValue(Object value){
    put(KEY_VALUE, value);
}
public <V> V getValue(){
    return get(KEY_VALUE);
}
public void putException(Throwable value){
    put(KEY_EXCEPTION, value);
}
public void putException(String value){
    put(KEY_EXCEPTION, new Throwable(value));
}

public Throwable getException(){
    return get(KEY_EXCEPTION);
}
public void setOnStart(Action action){
    super.setOnStart(action);
    action.link(this);
}
public void add(Action action){
    super.add(action);
    action.link(this);
}
public void setOnDone(Action action){
    super.setOnDone(action);
    action.link(this);
}
@Override
public Action start(Long delay, TimeUnit unit){
    synchronized(this){
        if(queue.isEmpty()){
            done();
            return null;
        } else if(currentRunnable == null){


DebugLog.start().send(this, "*+" + toDebugStringName(this) + " is started size " + queue.size()).end();
            Action action = super.element();
DebugLog.start().send(this, "\t" + toDebugStringName(this) + " " + toDebugStringName(action) + " is processed").end();

            return super.start(delay, unit);
        } else {

DebugException.start().log("Already started").end();

            return null;
        }
    }
}
@Override
public Action next(){
    synchronized(this){
        if(queue.isEmpty()){
            done();
            return null;
        } else {

            Action action = super.element();
DebugLog.start().send(this, "\t" + toDebugStringName(this) + " " + toDebugStringName(action) + " is processed").end();

            return super.next();
        }
    }
}
@Override
public Action repeat(){

DebugLog.start().send(this, "\t" + toDebugStringName(this) + " " + toDebugStringName(currentRunnable) + " is repeated").end();

    return super.repeat();
}
public boolean hasLabel(int label){
    return forEachInQueue(new PredicateW<>(){
        @Override
        public boolean test(Action action){
            return action.label == label;
        }
    });
}
public void skipUntilLabel(int label){
    synchronized(this){
        do{
            currentRunnable = queue.remove();
            if(currentRunnable.label != label){
DebugLog.start().send(this, "\t #" + toDebugStringName(this) + " " + toDebugStringName(currentRunnable) + " is skipped").end();
            }
        } while(currentRunnable.label != label);
DebugLog.start().send(this, "\t" + toDebugStringName(this) + " " + toDebugStringName(currentRunnable) + " is processed").end();
        post(currentRunnable);
    }
}
@Override
public void skip(int number){
    synchronized(this){
        for(int i = number; i > 0; i--){
            currentRunnable = queue.remove();

DebugLog.start().send(this, "\t #" + toDebugStringName(this) + " " + toDebugStringName(currentRunnable) + " is skipped").end();

        }
        currentRunnable = queue.remove();

DebugLog.start().send(this, "\t" + toDebugStringName(this) + " " + toDebugStringName(currentRunnable) + " is processed").end();

        post(currentRunnable);
    }
}
@Override
public void skipNext(){
    skip(1);
}
@Override
public void done(){
DebugLog.start().send(this, "*-" + toDebugStringName(this) + " is complete").end();
    super.done();
}

private void notify(TaskValue<?> task){
    if(task.isCanceled()){
        task.notifyCanceled();
    } else if(hasArguments()){
        Throwable e = getException();
        if(e == null){
            task.notifyComplete(getValue());
        } else {
            task.notifyException(getValue(), e);
        }
    } else {
        task.notifyComplete(null);
    }
}
private void notify(TaskState task){
    if(task.isCanceled()){
        task.notifyCanceled();
    } else if(hasArguments()){
        Throwable e = getException();
        if(e == null){
            task.notifyComplete();
        } else {
            task.notifyException(e);
        }
    } else {
        task.notifyComplete();
    }
}
public void notifyOnDone(TaskValue<?> task){
    setOnDone(new Action(){
        @Override
        public void runSafe(){
            notify(task);
        }
    });
}
public void notifyOnDone(TaskState task){
    setOnDone(new Action(){
        @Override
        public void runSafe(){
            notify(task);
        }
    });
}

public static abstract class Action extends RunnableW{
    private final int label;
    private String name = null;
    private RunnableGroup gr = null;
    public Action(){
        this(NO_LABEL);
    }

    public Action(int label){
        this.label = label;
    }
    public Action name(String name){
        this.name = name;
        return this;
    }
    public String name(){
        return name;
    }
    public String groupName(){
        return gr.name();
    }
    private void link(RunnableGroup gr){
        this.gr = gr;
    }
    public RunnableGroup gr(){
        return gr;
    }

    public Handler getHandler(){
        return gr.getHandler();
    }
    public int label(){
        return label;
    }

    public void next(){
        gr.next();
    }
    public void repeat(){
        gr.repeat();
    }
    public boolean hasLabel(int label){
        return gr.hasLabel(label);
    }
    public void skipUntilLabel(int label){
        gr.skipUntilLabel(label);
    }
    public void skip(int number){
        gr.skip(number);
    }
    public void skipNext(){
        gr.skipNext();
    }
    public void done(){
        gr.done();
    }

    public <K> void put(K key, Object value){
        gr.put(key, value);
    }
    public <K, V> V get(K key){
        return gr.get(key);
    }
    public void putValue(Object value){
        gr.putValue(value);
    }
    public <V> V getValue(){
        return gr.getValue();
    }
    public void putException(Throwable value){
        gr.putException(value);
    }
    public void putException(String value){
        gr.putException(value);
    }
    public Throwable getException(){
        return gr.getException();
    }

    public void notify(TaskValue<?> task){
        gr.notify(task);
    }
    public void notify(TaskState task){
        gr.notify(task);
    }
}

public static abstract class ActionTimeout extends Action{
    private RunnableTimeOut runnableTimeOut = null;
    public ActionTimeout(){
        super();
    }
    private ActionTimeout me(){
        return this;
    }

    public void startTimeout(long delay_ms){
        startTimeout(delay_ms, TimeUnit.MILLISECONDS);
    }
    public void startTimeout(long delay, TimeUnit timeUnit){
        if(runnableTimeOut != null){
            runnableTimeOut.cancel();
            runnableTimeOut = null;
        }
        runnableTimeOut = new RunnableTimeOut(this, delay, timeUnit, gr().getHandler()){
            @Override
            public void onTimeOut(){
                me().onTimeOut();
            }

        };
        runnableTimeOut.start();
    }
    public abstract void onTimeOut();
    public boolean isTimeout(){
        return runnableTimeOut != null && runnableTimeOut.isTimeout();
    }
    public void completed(){
        if(runnableTimeOut != null){
            runnableTimeOut.completed();
        } else {
DebugException.start().log("try to complete a timeout but it is not started").end();
        }
    }
    @Override
    public boolean cancel(Object owner, Handler handler){
        if(runnableTimeOut != null){
            runnableTimeOut.cancel();
            runnableTimeOut = null;
        }
        return super.cancel(owner, handler);
    }

}


}
