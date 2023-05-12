/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.type.android;

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

import com.tezov.lib_java.async.Handler;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.ref.Ref;
import com.tezov.lib_java.type.ref.WR;
import com.tezov.lib_java.type.runnable.RunnableSubscription;
import com.tezov.lib_java.type.runnable.RunnableW;
import com.tezov.lib_java_android.wrapperAnonymous.LifecycleObserverW;

import static androidx.lifecycle.Lifecycle.Event.ON_DESTROY;
import static androidx.lifecycle.Lifecycle.Event.ON_PAUSE;
import static androidx.lifecycle.Lifecycle.Event.ON_RESUME;
import static androidx.lifecycle.Lifecycle.Event.ON_START;
import static androidx.lifecycle.Lifecycle.Event.ON_STOP;
import static com.tezov.lib_java_android.type.android.LifecycleEvent.Event.DESTROY;
import static com.tezov.lib_java_android.type.android.LifecycleEvent.Event.PAUSE;
import static com.tezov.lib_java_android.type.android.LifecycleEvent.Event.RESTART;
import static com.tezov.lib_java_android.type.android.LifecycleEvent.Event.RESUME;
import static com.tezov.lib_java_android.type.android.LifecycleEvent.Event.START;
import static com.tezov.lib_java_android.type.android.LifecycleEvent.Event.STOP;

public class LifecycleEvent{
private final WR<LifecycleOwner> lifecycleOwnerWR;
private RunnableSubscription runnable;
private LifecycleObserverW observer;
private boolean pendingRun = false;

private LifecycleEvent(Event event, LifecycleOwner lifecycleOwner, RunnableSubscription runnable){
DebugTrack.start().create(this).end();
    this.lifecycleOwnerWR = WR.newInstance(lifecycleOwner);
    this.runnable = runnable;
    androidx.lifecycle.Lifecycle lifecycle = lifecycleOwner.getLifecycle();
    if(event == START){
        if(LifecycleState.get(lifecycle).isAtLeast(LifecycleState.STARTED)){
            pendingRun = true;
        } else {
            onStart(lifecycleOwner.getLifecycle());
        }
    } else if(event == RESUME){
        if(LifecycleState.get(lifecycle).isAtLeast(LifecycleState.RESUMED)){
            pendingRun = true;
        } else {
            onResume(lifecycleOwner.getLifecycle());
        }
    } else if(event == PAUSE){
        onPause(lifecycleOwner.getLifecycle());
    } else if(event == RESTART){
        onRestart(lifecycleOwner.getLifecycle());
    } else if(event == STOP){
        onStop(lifecycleOwner.getLifecycle());
    } else if(event == DESTROY){
        onDestroy(lifecycleOwner.getLifecycle());
    } else {
DebugException.start().unknown("event", event).end();
    }

}

public static Subscription on(Event event, LifecycleOwner lifecycleOwner, RunnableSubscription runnable){
    LifecycleEvent lifecycle = new LifecycleEvent(event, lifecycleOwner, runnable);
    Subscription subscription = new Subscription(lifecycle);
    runnable.bind(subscription);
    if(lifecycle.pendingRun){
        runnable.run();
    }
    return subscription;
}

private LifecycleEvent me(){
    return this;
}

private void onStart(androidx.lifecycle.Lifecycle lifecycle){
    observer = new LifecycleObserverW(){
        @OnLifecycleEvent(ON_START)
        public void onStart(){
            synchronized(me()){
                if(runnable != null){
                    runnable.run();
                }
            }
        }
    };
    addLifeCycleObserver(lifecycle, observer);
}
private void onResume(androidx.lifecycle.Lifecycle lifecycle){
    observer = new LifecycleObserverW(){
        @OnLifecycleEvent(ON_RESUME)
        public void onResume(){
            synchronized(me()){
                if(runnable != null){
                    runnable.run();
                }
            }
        }
    };
    addLifeCycleObserver(lifecycle, observer);
}
private void onPause(androidx.lifecycle.Lifecycle lifecycle){
    observer = new LifecycleObserverW(){
        @OnLifecycleEvent(ON_PAUSE)
        public void onPause(){
            synchronized(me()){
                if(runnable != null){
                    runnable.run();
                }
            }
        }
    };
    addLifeCycleObserver(lifecycle, observer);
}
private void onRestart(androidx.lifecycle.Lifecycle lifecycle){
    observer = new LifecycleObserverW(){
        boolean hasBeenPaused = false;
        @OnLifecycleEvent(ON_PAUSE)
        public void onPause(){
            hasBeenPaused = true;
        }
        @OnLifecycleEvent(ON_RESUME)
        public void onResume(){
            if(hasBeenPaused){
                hasBeenPaused = false;
                synchronized(me()){
                    if(runnable != null){
                        runnable.run();
                    }
                }
            }
        }
    };
    addLifeCycleObserver(lifecycle, observer);
}
private void onStop(androidx.lifecycle.Lifecycle lifecycle){
    observer = new LifecycleObserverW(){
        @OnLifecycleEvent(ON_STOP)
        public void onStop(){
            synchronized(me()){
                if(runnable != null){
                    runnable.run();
                }
            }
        }
    };
    addLifeCycleObserver(lifecycle, observer);
}
private void onDestroy(androidx.lifecycle.Lifecycle lifecycle){
    observer = new LifecycleObserverW(){
        @OnLifecycleEvent(ON_DESTROY)
        public void onDestroy(){
            synchronized(me()){
                if(runnable != null){
                    runnable.run();
                }
            }
        }
    };
    addLifeCycleObserver(lifecycle, observer);
}

private void addLifeCycleObserver(androidx.lifecycle.Lifecycle lifecycle, LifecycleObserverW observer){
    if(Handler.MAIN().isMe()){
        lifecycle.addObserver(observer);
    }
    else{
        Handler.MAIN().post(this, new RunnableW(){
            @Override
            public void runSafe(){
                lifecycle.addObserver(observer);
            }
        });
    }
}
private boolean unsubscribe(){
    synchronized(me()){
        if(observer == null){
            return false;
        }
        if(Ref.isNotNull(lifecycleOwnerWR)){
            lifecycleOwnerWR.get().getLifecycle().removeObserver(observer);
        }
        observer = null;
        runnable = null;
        return true;
    }
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}


public enum Event{
    START, RESUME, PAUSE, RESTART, STOP, DESTROY,
}

public static class Subscription extends com.tezov.lib_java.async.notifier.Subscription<LifecycleEvent>{
    private Subscription(LifecycleEvent lifecycle){
        super(lifecycle);
    }

    @Override
    public boolean unsubscribe(){
        return getRef().unsubscribe();
    }

    public <L extends LifecycleOwner> WR<L> getLifecycleOwnerWR(){
        return (WR<L>)getRef().lifecycleOwnerWR;
    }

    public LifecycleOwner getLifecycleOwner(){
        return Ref.get(getRef().lifecycleOwnerWR);
    }

}

}
