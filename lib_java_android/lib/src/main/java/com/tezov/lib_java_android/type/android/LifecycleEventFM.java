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

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.tezov.lib_java_android.application.AppContext;
import com.tezov.lib_java.toolbox.Compare;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.ref.Ref;
import com.tezov.lib_java.type.ref.WR;
import com.tezov.lib_java.type.runnable.RunnableSubscription;
import com.tezov.lib_java_android.wrapperAnonymous.FragmentLifecycleObserverW;

import static com.tezov.lib_java_android.type.android.LifecycleEventFM.Event.ATTACHED;
import static com.tezov.lib_java_android.type.android.LifecycleEventFM.Event.CREATED;
import static com.tezov.lib_java_android.type.android.LifecycleEventFM.Event.DESTROYED;
import static com.tezov.lib_java_android.type.android.LifecycleEventFM.Event.DETACHED;
import static com.tezov.lib_java_android.type.android.LifecycleEventFM.Event.PAUSED;
import static com.tezov.lib_java_android.type.android.LifecycleEventFM.Event.RESUMED;
import static com.tezov.lib_java_android.type.android.LifecycleEventFM.Event.STARTED;
import static com.tezov.lib_java_android.type.android.LifecycleEventFM.Event.STOPPED;

public class LifecycleEventFM{
private static final boolean RECURSIVE = false;
private final WR<FragmentManager> fragmentManagerWR;
private final WR<Fragment> fragmentWR;
private RunnableSubscription runnable;
private FragmentLifecycleObserverW observer = null;
private boolean pendingRun = false;

private LifecycleEventFM(Event event, FragmentManager fragmentManager, Fragment fragment, RunnableSubscription runnable){
DebugTrack.start().create(this).end();
    this.fragmentManagerWR = WR.newInstance(fragmentManager);
    this.fragmentWR = WR.newInstance(fragment);
    this.runnable = runnable;
    androidx.lifecycle.Lifecycle lifecycle = fragment.getLifecycle();
    if(event == ATTACHED){
        onAttached(fragmentManager);
    } else if(event == CREATED){
        if(LifecycleState.get(lifecycle).isAtLeast(LifecycleState.CREATED)){
            pendingRun = true;
        } else {
            onCreated(fragmentManager);
        }
    } else if(event == STARTED){
        if(LifecycleState.get(lifecycle).isAtLeast(LifecycleState.STARTED)){
            pendingRun = true;
        } else {
            onStarted(fragmentManager);
        }
    } else if(event == RESUMED){
        if(LifecycleState.get(lifecycle).isAtLeast(LifecycleState.RESUMED)){
            pendingRun = true;
        } else {
            onResumed(fragmentManager);
        }
    } else if(event == PAUSED){
        onPaused(fragmentManager);
    } else if(event == STOPPED){
        onStopped(fragmentManager);
    } else if(event == DESTROYED){
        onDestroyed(fragmentManager);
    } else if(event == DETACHED){
        onDetached(fragmentManager);
    } else {
DebugException.start().unknown("event", event).end();
    }

}

public static Subscription on(Event event, FragmentManager fragmentManager, Fragment fragment, RunnableSubscription runnable){
    LifecycleEventFM lifecycle = new LifecycleEventFM(event, fragmentManager, fragment, runnable);
    Subscription subscription = new Subscription(lifecycle);
    runnable.bind(subscription);
    if(lifecycle.pendingRun){
        runnable.run();
    }
    return subscription;
}

public static Subscription on(Event event, Fragment fragment, RunnableSubscription runnable){
    return on(event, AppContext.getFragmentManager(), fragment, runnable);
}

private LifecycleEventFM me(){
    return this;
}

private void onAttached(FragmentManager fragmentManager){
    observer = new FragmentLifecycleObserverW(){
        @Override
        public void onFragmentAttached(@NonNull FragmentManager fm, @NonNull Fragment f, @NonNull android.content.Context context){
            synchronized(me()){
                if(Compare.equals(fragmentWR, f) && (runnable != null)){
                    runnable.run();
                }
            }
        }
    };
    fragmentManager.registerFragmentLifecycleCallbacks(observer, RECURSIVE);
}

private void onCreated(FragmentManager fragmentManager){
    observer = new FragmentLifecycleObserverW(){
        @Override
        public void onFragmentCreated(@NonNull FragmentManager fm, @NonNull Fragment f, @Nullable Bundle savedInstanceState){
            synchronized(me()){
                if(Compare.equals(fragmentWR, f) && (runnable != null)){
                    runnable.run();
                }
            }
        }
    };
    fragmentManager.registerFragmentLifecycleCallbacks(observer, RECURSIVE);
}

private void onStarted(FragmentManager fragmentManager){
    observer = new FragmentLifecycleObserverW(){
        @Override
        public void onFragmentStarted(@NonNull FragmentManager fm, @NonNull Fragment f){
            synchronized(me()){
                if(Compare.equals(fragmentWR, f) && (runnable != null)){
                    runnable.run();
                }
            }
        }
    };
    fragmentManager.registerFragmentLifecycleCallbacks(observer, RECURSIVE);
}

private void onResumed(FragmentManager fragmentManager){
    observer = new FragmentLifecycleObserverW(){
        @Override
        public void onFragmentResumed(@NonNull FragmentManager fm, @NonNull Fragment f){
            synchronized(me()){
                if(Compare.equals(fragmentWR, f) && (runnable != null)){
                    runnable.run();
                }
            }
        }
    };
    fragmentManager.registerFragmentLifecycleCallbacks(observer, RECURSIVE);
}

private void onPaused(FragmentManager fragmentManager){
    observer = new FragmentLifecycleObserverW(){
        @Override
        public void onFragmentPaused(@NonNull FragmentManager fm, @NonNull Fragment f){
            synchronized(me()){
                if(Compare.equals(fragmentWR, f) && (runnable != null)){
                    runnable.run();
                }
            }
        }
    };
    fragmentManager.registerFragmentLifecycleCallbacks(observer, RECURSIVE);
}

private void onStopped(FragmentManager fragmentManager){
    observer = new FragmentLifecycleObserverW(){
        @Override
        public void onFragmentStopped(@NonNull FragmentManager fm, @NonNull Fragment f){
            synchronized(me()){
                if(Compare.equals(fragmentWR, f) && (runnable != null)){
                    runnable.run();
                }
            }
        }
    };
    fragmentManager.registerFragmentLifecycleCallbacks(observer, RECURSIVE);
}

private void onDestroyed(FragmentManager fragmentManager){
    observer = new FragmentLifecycleObserverW(){
        @Override
        public void onFragmentDestroyed(@NonNull FragmentManager fm, @NonNull Fragment f){
            synchronized(me()){
                if(Compare.equals(fragmentWR, f) && (runnable != null)){
                    runnable.run();
                }
            }
        }
    };
    fragmentManager.registerFragmentLifecycleCallbacks(observer, RECURSIVE);
}

private void onDetached(FragmentManager fragmentManager){
    observer = new FragmentLifecycleObserverW(){
        @Override
        public void onFragmentDetached(@NonNull FragmentManager fm, @NonNull Fragment f){
            synchronized(me()){
                if(Compare.equals(fragmentWR, f) && (runnable != null)){
                    runnable.run();
                }
            }
        }
    };
    fragmentManager.registerFragmentLifecycleCallbacks(observer, RECURSIVE);
}

public boolean unsubscribe(){
    synchronized(me()){
        if(observer == null){
            return false;
        }
        if(Ref.isNotNull(fragmentManagerWR)){
            fragmentManagerWR.get().unregisterFragmentLifecycleCallbacks(observer);
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
    ATTACHED, CREATED, STARTED, RESUMED, PAUSED, STOPPED, DESTROYED, DETACHED
}

public static class Subscription extends com.tezov.lib_java.async.notifier.Subscription<LifecycleEventFM>{
    private Subscription(LifecycleEventFM ref){
        super(ref);
    }

    @Override
    public boolean unsubscribe(){
        return getRef().unsubscribe();
    }

}

}
