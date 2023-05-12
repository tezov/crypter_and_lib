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
import android.annotation.SuppressLint;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.tezov.lib_java.async.notifier.observer.state.ObserverStateE;
import com.tezov.lib_java.async.notifier.task.TaskState;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.runnable.RunnableSubscription;
import com.tezov.lib_java.type.runnable.RunnableW;
import com.tezov.lib_java_android.application.AppContext;
import com.tezov.lib_java_android.application.AppDisplay;
import com.tezov.lib_java_android.toolbox.PostToHandler;
import com.tezov.lib_java_android.type.android.ViewTreeEvent;
import com.tezov.lib_java_android.ui.layout.FrameFlipperLayout;
import com.tezov.lib_java_android.ui.layout.FrameLayout;
import com.tezov.lib_java_android.util.UtilsView;

public class AdMobBanner{
private final String unitAd;
private AdView adView = null;
private AdSize size;
private TaskState loadingTask = null;
private FrameFlipperLayout container;
private boolean isPaused = true;

public AdMobBanner(String unitAd){
DebugTrack.start().create(this).end();
    this.unitAd = unitAd;
}
private AdMobBanner me(){
    return this;
}
public AdMobBanner setContainer(FrameFlipperLayout container){
    return setContainer(container, null);
}
public AdMobBanner setContainer(FrameFlipperLayout container, AdSize size){
    if(this.container != container){
        this.container = container;
        if(size != null){
            this.size = size;
        } else {
            computeBannerSize(container);
        }
    }
    return this;
}

private void computeBannerSize(FrameLayout container){
    RunnableSubscription compute = new RunnableSubscription(){
        @Override
        public void onComplete(){
            int adWidthPixels = container.getWidth();
            if(adWidthPixels > 0){
                unsubscribe();
                me().size = AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(container.getContext(), (int)AppDisplay.convertPxToDp(adWidthPixels));
                if(me().size == AdSize.INVALID){
                    me().size = AdSize.FLUID;
                }
            } else {
DebugException.start().logHidden("width is <= 0").end();
            }
        }
    };
    if(container.isLaidOut() && (container.getWidth() > 0)){
        compute.run();
    } else {
        ViewTreeEvent.onLayout(container, compute);
    }
}
private TaskState.Observable load(){
    if(loadingTask != null){
        return loadingTask.getObservable();
    }
    loadingTask = new TaskState();
    RunnableSubscription load = new RunnableSubscription(){
        @SuppressLint("MissingPermission")
        @Override
        public void onComplete(){
            if(size != null){
                unsubscribe();
                if(adView != null){
                    adView.setAdListener(new AdMobBannerListenerW(){});
                    adView.destroy();
                }
                adView = new AdView(AppContext.get());
                UtilsView.generateAndSetId(container, adView);
                adView.setAdSize(size);
                adView.setAdUnitId(unitAd);
                adView.setAdListener(new AdMobBannerListenerW(){
                    @Override
                    public void onAdFailedToLoad(LoadAdError loadAdError){
                        me().onAdFailedToLoad(loadAdError);
                    }
                    @Override
                    public void onAdLoaded(){
                        me().onAdLoaded();
                    }
                });
                AdRequest adRequest = new AdRequest.Builder().build();
                adView.loadAd(adRequest);
            } else {
DebugException.start().logHidden("size is null").end();
            }
        }
    };
    if((size != null) && container.isLaidOut()){
        load.run();
    } else {
        ViewTreeEvent.onLayout(container, load);
    }
    return loadingTask.getObservable();
}
private void onAdLoaded(){
    if(loadingTask == null){
        return;
    }
    if(loadingTask.isCanceled()){
        taskCanceled();
    } else if(container != null){
        adView.setAdListener(new AdMobBannerListenerW(){});
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, size.getHeightInPixels(container.getContext()));
        adView.setLayoutParams(params);
        container.putAndShowView(adView);
        PostToHandler.of(container, new RunnableW(){
            @Override
            public void runSafe(){
                taskCompleted();
            }
        });
    } else {
        taskCompleted();
    }

}
private void onAdFailedToLoad(LoadAdError loadAdError){
    if(loadingTask == null){
        return;
    }
    adView = null;
    taskFailed(new Throwable(loadAdError.getMessage()));
}
private void taskCanceled(){
    destroyAdview();
    TaskState tmp = loadingTask;
    loadingTask = null;
    tmp.notifyCanceled();
}
private void taskCompleted(){
    TaskState tmp = loadingTask;
    loadingTask = null;
    tmp.notifyComplete();
}
private void taskFailed(Throwable e){
    TaskState tmp = loadingTask;
    loadingTask = null;
    tmp.notifyException(e);
}

public boolean isLoading(){
    return loadingTask != null;
}
public boolean isLoaded(){
    return (container != null) && (adView != null) && container.hasView(adView);
}
public boolean isPaused(){
    return isPaused;
}

public TaskState.Observable resume(){
    TaskState task = new TaskState();
    if(isLoaded()){
        if(isPaused()){
            if(adView != null){
                adView.resume();
            }
            isPaused = false;
        }
        task.notifyComplete();
    } else {
        load().observe(new ObserverStateE(this){
            @Override
            public void onComplete(){
                isPaused = false;
                task.notifyComplete();
            }
            @Override
            public void onException(Throwable e){
                task.notifyException(e);
            }
            @Override
            public void onCancel(){
                task.cancel();
                task.notifyCanceled();
            }
        });
    }
    return task.getObservable();
}
public TaskState.Observable pause(){
    TaskState task = new TaskState();
    if(isPaused()){
        task.notifyComplete();
    } else if(isLoading()){
        this.loadingTask.cancel();
        this.loadingTask.observe(new ObserverStateE(this){
            @Override
            public void onComplete(){
                if(container != null){
                    container.removeView(adView);
                }
                adView.pause();
                isPaused = true;
                task.notifyComplete();
            }
            @Override
            public void onException(Throwable e){
                isPaused = true;
                task.notifyException(e);
            }
            @Override
            public void onCancel(){
                isPaused = true;
                task.notifyComplete();
            }
        });
    } else if(isLoaded()){
        if(container != null){
            container.removeView(adView);
        }
        adView.pause();
        isPaused = true;
        task.notifyComplete();
    } else {
        isPaused = true;
        task.notifyComplete();
    }
    return task.getObservable();
}
public TaskState.Observable destroy(){
    TaskState task = new TaskState();
    if(!isLoading()){
        destroyAdview();
        task.notifyComplete();
    } else if(isLoading()){
        this.loadingTask.cancel();
        this.loadingTask.observe(new ObserverStateE(this){
            @Override
            public void onComplete(){
                destroyAdview();
                task.notifyComplete();
            }
            @Override
            public void onException(Throwable e){
                destroyAdview();
                task.notifyException(e);
            }
            @Override
            public void onCancel(){
                destroyAdview();
                task.notifyComplete();
            }
        });
    }
    return task.getObservable();
}
private void destroyAdview(){
    if(container != null){
        container.removeView(adView);
        container = null;
    }
    if(adView != null){
        adView.setAdListener(new AdMobBannerListenerW(){});
        adView.destroy();
        adView = null;
    }
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

}
