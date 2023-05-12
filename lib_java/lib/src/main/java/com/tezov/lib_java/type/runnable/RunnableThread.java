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
import com.tezov.lib_java.type.ref.WR;

import java.util.concurrent.TimeUnit;

public abstract class RunnableThread extends RunnableW{
private final Handler handler;
private final WR<Object> ownerWR;
private boolean isCanceled = false;

public RunnableThread(Object owner){
    this(owner, null);
}
public RunnableThread(Object owner, Handler handler){
    this.ownerWR = WR.newInstance(owner);
    if(handler != null){
        this.handler = handler;
    } else {
        this.handler = Handler.PRIMARY();
    }
}

public Handler getHandler(){
    return handler;
}

public void post(long delayMilliseconds){
    if(!isCanceled){
        handler.post(ownerWR.get(), delayMilliseconds, this);
    }
}
public void post(){
    post(0);
}
public void post(long delay, TimeUnit timeUnit){
    post(timeUnit.toMillis(delay));
}
public void post(Handler.Delay delay, TimeUnit timeUnit){
    post(timeUnit.toMillis(delay.millisecond()));
}

public boolean cancel(){
    isCanceled = true;
    return handler.cancel(ownerWR.get(), this);
}
public void cancelClear(){
    isCanceled = false;
}

public boolean isCanceled(){
    return isCanceled;
}


}
