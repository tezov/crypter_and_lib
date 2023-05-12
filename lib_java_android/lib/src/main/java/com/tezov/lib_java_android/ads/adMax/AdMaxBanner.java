/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ads.adMax;

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

import com.tezov.lib_java_android.R;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import androidx.fragment.app.Fragment;
import android.annotation.SuppressLint;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdFormat;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;
import com.tezov.lib_java.async.notifier.observer.state.ObserverStateE;
import com.tezov.lib_java.async.notifier.task.TaskState;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.primaire.Size;
import com.tezov.lib_java.type.runnable.RunnableSubscription;
import com.tezov.lib_java.type.runnable.RunnableW;
import com.tezov.lib_java_android.application.AppContext;
import com.tezov.lib_java_android.application.AppDisplay;
import com.tezov.lib_java_android.toolbox.PostToHandler;
import com.tezov.lib_java_android.type.android.ViewTreeEvent;
import com.tezov.lib_java_android.ui.layout.FrameFlipperLayout;
import com.tezov.lib_java_android.ui.layout.FrameLayout;
import com.tezov.lib_java_android.util.UtilsView;

public class AdMaxBanner {
private final String unitAd;
private MaxAdView adView = null;
private Size size = null;
private TaskState loadingTask = null;
private FrameFlipperLayout container;
private boolean isPaused = true;

public AdMaxBanner(String unitAd){
DebugTrack.start().create(this).end();
    this.unitAd = unitAd;
}
private AdMaxBanner me(){
    return this;
}
public AdMaxBanner setContainer(FrameFlipperLayout container){
    if(this.container != container){
        this.container = container;
        computeBannerSize(container);
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
                int heightDp = MaxAdFormat.BANNER.getAdaptiveSize(AppContext.getActivity()).getHeight();
                me().size = new Size(adWidthPixels, AppDisplay.convertDpToPx(heightDp));
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
                    adView.setListener(null);
                    adView.destroy();
                }
                adView = new MaxAdView(unitAd, AppContext.get());
                adView.setExtraParameter( "adaptive_banner", "true");
                adView.setBackgroundResource(R.color.Transparent);
                UtilsView.generateAndSetId(container, adView);
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(size.getWidth(), size.getHeight());
                adView.setLayoutParams(params);
                adView.setListener(new AdMaxBannerListenerW(){
                    @Override
                    public void onAdLoaded(MaxAd ad) {
//                        DebugLog.start().send("banner from " + ad.getNetworkName()).end();
                        int height = ad.getSize().getHeight();
                        me().size.setHeight(AppDisplay.convertDpToPx(height));
                        me().onAdLoaded();
                    }
                    @Override
                    public void onAdLoadFailed(String adUnitId, MaxError error) {
                        me().onAdFailedToLoad(error);
                    }
                });
                adView.loadAd();
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
        adView.setListener(null);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) adView.getLayoutParams();
        params.height = size.getHeight();
        params.width = size.getWidth();
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
private void onAdFailedToLoad(MaxError loadAdError){
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
                adView.startAutoRefresh();
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
                adView.stopAutoRefresh();
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
        adView.stopAutoRefresh();
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
        adView.setListener(null);
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
