/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ads.adMax;

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
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import androidx.fragment.app.Fragment;
import com.tezov.lib_java.debug.DebugException;
import com.applovin.mediation.MaxError;
import com.tezov.lib_java.async.Handler;
import com.tezov.lib_java.async.notifier.observer.state.ObserverStateE;
import com.tezov.lib_java.async.notifier.task.TaskState;
import com.tezov.lib_java.type.runnable.RunnableW;

import java.util.concurrent.TimeUnit;

public class AdMaxInterstitialCyclicLoad extends AdMaxInterstitial {
private final Handler handler = Handler.PRIMARY();
private int countErrorSinceLastFailed = 0;
private int maxRetryIfError = 3;
private long delayAfterError_ms = TimeUnit.MILLISECONDS.convert(60, TimeUnit.SECONDS);
private RunnableW runnable = null;
private long delayCyclic_ms;
private boolean stopForced = true;
private boolean stopWhenClosed = false;
private boolean isReadyToBeShown = false;

public AdMaxInterstitialCyclicLoad(String unitAd, long delayCyclic_ms){
    this(unitAd, delayCyclic_ms, null);
}
public AdMaxInterstitialCyclicLoad(String unitAd, long delayCyclic, TimeUnit unit){
    super(unitAd);
    if(unit == null){
        this.delayCyclic_ms = delayCyclic;
    } else {
        this.delayCyclic_ms = TimeUnit.MILLISECONDS.convert(delayCyclic, unit);
    }
}

public AdMaxInterstitialCyclicLoad setDelayCyclic(long delayCyclic_ms){
    this.delayCyclic_ms = delayCyclic_ms;
    return this;
}

public AdMaxInterstitialCyclicLoad setDelayCyclic(long delayCyclic, TimeUnit unit){
    this.delayCyclic_ms = TimeUnit.MILLISECONDS.convert(delayCyclic, unit);
    return this;
}

public AdMaxInterstitialCyclicLoad setMaxRetryIfError(int maxRetryIfError){
    this.maxRetryIfError = maxRetryIfError;
    return this;
}

public AdMaxInterstitialCyclicLoad setDelayAfterError(long delayAfterError_ms){
    this.delayAfterError_ms = delayAfterError_ms;
    return this;
}

public AdMaxInterstitialCyclicLoad setDelayAfterError(long delayAfterError, TimeUnit unit){
    this.delayAfterError_ms = TimeUnit.MILLISECONDS.convert(delayAfterError, unit);
    return this;
}

protected long getDelayCyclic_ms(){
    return delayCyclic_ms;
}

public boolean isReadyToBeShown(){
    return isReadyToBeShown;
}

public boolean isRunning(){
    return runnable != null;
}

public AdMaxInterstitialCyclicLoad start(long delay_ms){
    start(delay_ms, TimeUnit.MILLISECONDS);
    return this;
}
public AdMaxInterstitialCyclicLoad start(long delay, TimeUnit unit){
    if(!isRunning() && !isReadyToBeShown()){
        countErrorSinceLastFailed = 0;
        stopWhenClosed = false;
        stopForced = false;
        post(delay, unit);
    }
    return this;
}

public void stop(){
    stop(false);
}
public void stop(boolean stopWhenClosed){
    if(isRunning()){
        if(stopWhenClosed){
            this.stopWhenClosed = true;
        } else {
            stopForced = true;
            handler.cancel(this, runnable);
            runnable = null;
            onStopped();
        }
    }
}

private void post(Long delay, TimeUnit unit){
    if(runnable == null){
        runnable = new RunnableW(){
            @Override
            public void runSafe(){
                load().observe(new ObserverStateE(this){
                    @Override
                    public void onComplete(){
                        if(!stopForced){
                            if(isLoaded()){
                                isReadyToBeShown = true;
                                onReadyToBeShown();
                            }
                        } else {
                            runnable = null;
                            onStopped();
                        }
                    }
                    @Override
                    public void onException(Throwable e){
                        //useless because override onFailedLoad is done
                    }
                });
            }
        };
    }
    isReadyToBeShown = false;
    handler.post(this, delay, unit, runnable);
}

protected void onReadyToBeShown(){

}
protected void onStopped(){
}
@Override
protected void onClosed(){
    isReadyToBeShown = false;
    countErrorSinceLastFailed = 0;
    if(isRunning()){
        if(!stopWhenClosed){
            post(delayCyclic_ms, TimeUnit.MILLISECONDS);
        } else {
            runnable = null;
            onStopped();
        }
    }
}

@Override
protected void onFailedLoad(MaxError adError){
    countErrorSinceLastFailed++;
    if(countErrorSinceLastFailed < maxRetryIfError){
        post(delayAfterError_ms, TimeUnit.MILLISECONDS);
    } else {
        runnable = null;
        onFailedMaxError(adError);
    }
}
@Override
protected void onFailedOpen(MaxError adError){
    countErrorSinceLastFailed++;
    if(countErrorSinceLastFailed < maxRetryIfError){
        post(delayAfterError_ms, TimeUnit.MILLISECONDS);
    } else {
        runnable = null;
        onFailedMaxError(adError);
    }
}
protected void onFailedMaxError(MaxError adError){

DebugException.start().log("Failed to Load, max error, " + adError.toString()).end();

}

@Override
public TaskState.Observable destroy(){
    stop();
    return super.destroy();
}

}
