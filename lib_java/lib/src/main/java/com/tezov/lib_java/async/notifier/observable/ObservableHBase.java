/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.async.notifier.observable;

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.util.UtilsString;
import java.util.LinkedList;
import java.util.Set;

import com.tezov.lib_java.async.Handler;
import com.tezov.lib_java.async.notifier.Notifier;
import com.tezov.lib_java.type.runnable.RunnableTimeOut;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

//TEST
public abstract class ObservableHBase<EVENT, OBJECT> extends ObservableBase<EVENT, ObservableHBase<EVENT, OBJECT>.Access>{
private final static long AUTO_FLUSH_MIN_DELAY_SECOND = 60;
private final static long AUTO_FLUSH_MAX_DELAY_SECOND = AUTO_FLUSH_MIN_DELAY_SECOND * 5;
protected int historyMaxSize = 5;
private Long autoFlushDelay_s = null;
private Long autoFlushDelayCurrent_s = null;
private boolean hasFlushed;
private RunnableTimeOut autoFlushRunnable = null;

protected ObservableHBase(){
    setAutoFlushDelay(AUTO_FLUSH_MIN_DELAY_SECOND);
}

public void setHistoryMaxSize(int historyMaxSize){
    this.historyMaxSize = historyMaxSize;
}

public void setAutoFlushDelay(Long autoFlushDelay){
    setAutoFlushDelay(autoFlushDelay, TimeUnit.SECONDS);
}

public void setAutoFlushDelay(Long autoFlushDelay, TimeUnit unit){
    if(autoFlushDelay == null){
        if(autoFlushRunnable != null){
            autoFlushRunnable.cancel();
            autoFlushRunnable = null;
        }
    } else {
        this.autoFlushDelay_s = TimeUnit.SECONDS.convert(autoFlushDelay, unit);
        autoFlushDelayCurrent_s = this.autoFlushDelay_s;
        if(autoFlushRunnable == null){
            AutoFlushStart();
        }
    }
}

private void AutoFlushStart(){
    if(autoFlushRunnable == null){
        autoFlushRunnable = new RunnableTimeOut(this, autoFlushDelayCurrent_s, TimeUnit.SECONDS, Handler.LOW()){
            @Override
            public void onComplete(){

            }

            @Override
            public void onTimeOut(){
                Iterator<Access> iterator = ((List)getAccessList()).iterator();
                hasFlushed = false;
                while(iterator.hasNext()){
                    Access access = iterator.next();
                    if(access.clearHistory()){
                        hasFlushed = true;
                    }
                }
                if(hasFlushed){
                    autoFlushDelayCurrent_s = autoFlushDelay_s;
                } else {
                    autoFlushDelayCurrent_s += autoFlushDelay_s;
                    if(autoFlushDelayCurrent_s > AUTO_FLUSH_MAX_DELAY_SECOND){
                        autoFlushDelayCurrent_s = AUTO_FLUSH_MAX_DELAY_SECOND;
                    }

                }
                setAutoFlushDelay(autoFlushDelay_s, TimeUnit.SECONDS);
                start();
            }
        };
    }
    autoFlushRunnable.start();
}

@Override
protected void finalize() throws Throwable{
    if(autoFlushRunnable != null){
        autoFlushRunnable.cancel();
        autoFlushRunnable = null;
    }
    super.finalize();
}

public abstract class Access extends Notifier.defObservable.Access<EVENT>{
    abstract public boolean clearHistory();

}

}
