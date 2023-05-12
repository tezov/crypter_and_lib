/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ads.adMob;

import com.tezov.lib_java.debug.DebugLog;
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
import com.tezov.lib_java_android.application.AppContext;
import com.tezov.lib_java_android.application.VersionSDK;

import android.media.AudioManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.tezov.lib_java.async.Handler;
import com.tezov.lib_java.async.notifier.observer.state.ObserverStateE;
import com.tezov.lib_java.async.notifier.task.TaskState;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.runnable.RunnableW;

public class AdMobInterstitial{
private final String unitAd;
private TaskState loadTask = null;
private InterstitialAd interstitial = null;
private boolean isShowing = false;
private boolean hasBeenShown = false;
private boolean requestMute = true;
private boolean isMuted = false;

public AdMobInterstitial(String unitAd){
DebugTrack.start().create(this).end();
    this.unitAd = unitAd;
}
private AdMobInterstitial me(){
    return this;
}

public <A extends AdMobInterstitial> A mute(boolean flag){
    this.requestMute = flag;
    return (A)this;
}

public TaskState.Observable load(){
    if(loadTask != null){
        return loadTask.getObservable();
    }
    loadTask = new TaskState();
    if(isLoaded()){
        TaskState tmp = loadTask;
        loadTask = null;
        tmp.notifyComplete();
        return tmp.getObservable();
    } else {
        isShowing = false;
        hasBeenShown = false;
        AdRequest adRequest = new AdRequest.Builder().build();
        Handler.MAIN().post(this, new RunnableW(){
            @Override
            public void runSafe(){
                InterstitialAd.load(AppContext.get(), unitAd, adRequest, new AdMobInterstitialLoadListenerW(){
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitial){
                        if(me().interstitial != null){
                            me().interstitial = null;
                        }
                        me().interstitial = interstitial;
                        TaskState tmp = loadTask;
                        loadTask = null;
                        if(!tmp.isCanceled()){
                            onLoaded();
                        }
                        tmp.notifyComplete();
                    }
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError adError){
                        TaskState tmp = loadTask;
                        loadTask = null;
                        if(!tmp.isCanceled()){
                            onFailedLoad(adError);
                        }
                        tmp.notifyException(new Throwable(adError.getMessage()));
                    }
                });
            }
        });
    }
    return loadTask.getObservable();
}

public boolean isLoading(){
    return loadTask != null;
}
public boolean isLoaded(){
    return interstitial != null;
}
public boolean isShowing(){
    return isShowing;
}

public boolean hasBeenShown(){
    return hasBeenShown;
}

public TaskState.Observable show(){
    TaskState task = new TaskState();
    if(isLoaded()){
        interstitialShow(task);
    } else {
        load().observe(new ObserverStateE(this){
            @Override
            public void onComplete(){
                interstitialShow(task);
            }
            @Override
            public void onException(java.lang.Throwable e){
                task.notifyException(e);
            }
        });
    }
    return task.getObservable();
}

private void interstitialShow(TaskState task){
    if(requestMute && hasVolume()){
        mute();
    }
    isShowing = true;
    interstitial.setFullScreenContentCallback(new AdMobInterstitialShowListenerW(){
        @Override
        public void onAdFailedToShowFullScreenContent(AdError adError){
            unmute();
            onFailedOpen(adError);
            interstitial = null;
            task.notifyException(new Throwable(adError.getMessage()));
        }
        @Override
        public void onAdShowedFullScreenContent(){
            hasBeenShown = true;
            onOpened();
        }
        @Override
        public void onAdDismissedFullScreenContent(){
            isShowing = false;
            unmute();
            onClosed();
            interstitial = null;
            task.notifyComplete();
        }
    });
    interstitial.show(AppContext.getActivity());
}

private boolean hasVolume(){
    AudioManager m = AppContext.getSystemService(android.content.Context.AUDIO_SERVICE);
    return m.getStreamVolume(AudioManager.STREAM_MUSIC) != 0;
}

@RequiresApi(api = Build.VERSION_CODES.M)
private void streamMute_after23_M(AudioManager am, boolean flag){
    if(flag){
        if (!am.isStreamMute(AudioManager.STREAM_MUSIC)) {
            am.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_MUTE, 0);
            isMuted = true;
        }
    }
    else{
        am.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_UNMUTE , 0);
    }
}
@SuppressWarnings("deprecation")
private void streamMute_before23_M(AudioManager am, boolean flag){
    am.setStreamMute(AudioManager.STREAM_MUSIC, flag);
    isMuted = flag;
}

private void unmute(){
    synchronized(this){
        if(isMuted){
            AudioManager am = AppContext.getSystemService(android.content.Context.AUDIO_SERVICE);
            if(VersionSDK.isSupEqualTo23_MARSHMALLOW()){
                streamMute_after23_M(am, false);
            }
            else{
                streamMute_before23_M(am, false);
            }
        }
    }
}
private void mute(){
    synchronized(this){
        if(!requestMute){
            AudioManager am = AppContext.getSystemService(android.content.Context.AUDIO_SERVICE);
            if(VersionSDK.isSupEqualTo23_MARSHMALLOW()){
                streamMute_after23_M(am, true);
            }
            else{
                streamMute_before23_M(am, true);
            }
        }
    }

}

public TaskState.Observable destroy(){
    TaskState task = new TaskState();
    if(isLoading()){
        loadTask.cancel();
        loadTask.observe(new ObserverStateE(this){
            @Override
            public void onComplete(){
                done();
            }
            @Override
            public void onException(java.lang.Throwable e){
                done();
            }
            void done(){
                interstitial = null;
                onDestroyed();
                task.notifyComplete();
            }
        });
    } else {
        interstitial = null;
        onDestroyed();
        task.notifyComplete();
    }
    return task.getObservable();
}

protected void onLoaded(){
}
protected void onFailedLoad(LoadAdError adError){

DebugException.start().log("Failed to Load, " + adError.toString()).end();

}

protected void onOpened(){
}
protected void onClosed(){
}
protected void onFailedOpen(AdError adError){

DebugException.start().log("Failed to open, " + adError.toString()).end();

}

protected void onDestroyed(){
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

}
