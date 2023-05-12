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
import com.tezov.lib_java.async.Handler;

import java.util.concurrent.TimeUnit;

public abstract class RunnableTimeOut extends RunnableThread{
private long delay;
private TimeUnit timeUnit;
private boolean isTimeout = false;

public RunnableTimeOut(Object owner, long delay_ms){
    this(owner, delay_ms, Handler.PRIMARY());
}
public RunnableTimeOut(Object owner, long delay_ms, Handler handler){
    this(owner, delay_ms, TimeUnit.MILLISECONDS, handler);
}
public RunnableTimeOut(Object owner, long delay, TimeUnit timeUnit){
    this(owner, delay, timeUnit, Handler.PRIMARY());
}
public RunnableTimeOut(Object owner, long delay, TimeUnit timeUnit, Handler handler){
    super(owner, handler);
    setDelay(delay, timeUnit);
}

public void setDelay(long delay_ms){
    setDelay(delay_ms, TimeUnit.MILLISECONDS);
}
public void setDelay(long delay, TimeUnit timeUnit){
    this.delay = delay;
    this.timeUnit = timeUnit;
}

public boolean isTimeout(){
    return isTimeout;
}

@Override
final public void runSafe(){
    if(!isCanceled()){
        isTimeout = true;
        onTimeOut();
    }
}

public <R extends RunnableTimeOut> R start(){
    if(!isCanceled()){
        isTimeout = false;
        onStart();
        post(delay, timeUnit);
    }
    return (R)this;
}
public <R extends RunnableTimeOut> R restart(){
    cancel();
    cancelClear();
    isTimeout = false;
    onReStart();
    post(delay, timeUnit);
    return (R)this;
}

final public void completed(){
    if(!isCanceled() && !isTimeout){
        cancel();
        onComplete();
    }
}

public void onStart(){
}
public void onReStart(){
}

public void onComplete(){
}
public abstract void onTimeOut();


}
