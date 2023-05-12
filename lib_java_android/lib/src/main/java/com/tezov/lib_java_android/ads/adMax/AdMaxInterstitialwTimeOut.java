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

import android.content.Context;

import com.tezov.lib_java.async.notifier.observer.state.ObserverStateE;
import com.tezov.lib_java.async.notifier.task.TaskState;
import com.tezov.lib_java.type.runnable.RunnableTimeOut;

import java.util.concurrent.TimeUnit;

public abstract class AdMaxInterstitialwTimeOut extends AdMaxInterstitial {
public AdMaxInterstitialwTimeOut(String unitAd, Context context){
    super(unitAd);
}
protected AdMaxInterstitialwTimeOut me(){
    return this;
}

public void show(long adTimeOutDelay_ms){
    show(adTimeOutDelay_ms, TimeUnit.MILLISECONDS);
}

public void show(long adTimeOutDelay, TimeUnit unit){
    if(hasBeenShown()){
        onComplete();
    } else {
        if(isLoaded()){
            me().showAd();
        } else {
            RunnableTimeOut timeOut = new RunnableTimeOut(this, adTimeOutDelay, unit){
                @Override
                public void onComplete(){
                    me().showAd();
                }

                @Override
                public void onTimeOut(){
                    me().onTimeOut();
                }
            };
            TaskState.Observable loadTask = load();
            loadTask.observe(new ObserverStateE(this){
                @Override
                public void onComplete(){
                    timeOut.completed();
                }

                @Override
                public void onException(Throwable e){
                    timeOut.cancel();
                    me().onException(e);
                }

                @Override
                public void onCancel(){
                    timeOut.cancel();
                    me().onCanceled();
                }
            });
            timeOut.start();
        }
    }
}

private void showAd(){
    show().observe(new ObserverStateE(this){
        @Override
        public void onComplete(){
            me().onComplete();
        }

        @Override
        public void onException(Throwable e){
            me().onException(e);
        }

        @Override
        public void onCancel(){
            me().onCanceled();
        }
    });
}

protected abstract void onComplete();

protected void onTimeOut(){

DebugException.start().log("TimeOut").end();

    onComplete();
}

protected void onException(Throwable e){

DebugException.start().log(e).end();

    onComplete();
}

protected void onCanceled(){

DebugException.start().log("Canceled").end();

    onComplete();
}

}
