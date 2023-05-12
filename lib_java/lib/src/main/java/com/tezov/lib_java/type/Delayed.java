/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.type;

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
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.runnable.RunnableThread;
import com.tezov.lib_java.wrapperAnonymous.ConsumerW;

public class Delayed<T>{
private T lastValid;
private T last;
private Long delayTimeToValidate_ms = 1000L;
private RunnableThread runnableThread = null;
private ConsumerW<T> onChangedRunnable = null;

public Delayed(T t){
DebugTrack.start().create(this).end();
    last = t;
    lastValid = t;
}

public Delayed<T> setDelayTimeToValidate(Long l_ms){
    delayTimeToValidate_ms = l_ms;
    return this;
}

public Delayed<T> setOnChangedRunnable(ConsumerW<T> runnable){
    this.onChangedRunnable = runnable;
    return this;
}

public T getLastValid(){
    return lastValid;
}

public T getLast(){
    return last;
}

private void cancel(){
    if(runnableThread != null){
        runnableThread.cancel();
        runnableThread = null;
    }
}

public void update(T t){
    if(t.equals(lastValid)){
        cancel();
    } else if(!t.equals(last)){
        cancel();
        runnableThread = new RunnableThread(this){
            T t = null;
            @Override
            public void runSafe(){
                lastValid = t;
                if(onChangedRunnable != null){
                    onChangedRunnable.accept(lastValid);
                }
            }
            RunnableThread init(T t){
                this.t = t;
                return this;
            }
        }.init(t);
        runnableThread.post(delayTimeToValidate_ms);
    }
    last = t;
}

public void force(T t){
    if(t.equals(lastValid)){
        cancel();
    } else {
        cancel();
        lastValid = t;
        if(onChangedRunnable != null){
            onChangedRunnable.accept(lastValid);
        }
    }
    last = t;
}

public DebugString toDebugString(){
    DebugString data = new DebugString();
    data.append("lastValid", lastValid);
    data.append("last", last);
    data.append("delayTimeToValidate_ms", delayTimeToValidate_ms);
    data.appendCheckIfNull("runnableThread", runnableThread);
    data.appendCheckIfNull("onChangedRunnable", onChangedRunnable);
    return data;
}

final public void toDebugLog(){
DebugLog.start().send(toDebugString()).end();
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

}
