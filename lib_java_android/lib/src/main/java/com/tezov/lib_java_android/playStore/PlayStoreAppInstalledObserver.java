/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.playStore;

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
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import androidx.fragment.app.Fragment;

import com.tezov.lib_java.async.notifier.observer.state.ObserverStateE;
import com.tezov.lib_java.async.notifier.task.TaskState;
import com.tezov.lib_java.wrapperAnonymous.ConsumerW;
import com.tezov.lib_java_android.ui.misc.DelayState;

public abstract class PlayStoreAppInstalledObserver extends DelayState{
private final String packageName;
private TaskState.Observable observer = null;
private ConsumerW<Long> consumer = null;

public PlayStoreAppInstalledObserver(String packageName, long timeOut_ms){
    setTarget(timeOut_ms);
    this.packageName = packageName;
}


public DelayState start(){
    if(consumer == null){
        consumer = new ConsumerW<Long>(){
            @Override
            public void accept(Long delay){
                if(observer != null){
                    observer.cancel();
                    observer = null;
                }
                if(delay == null){
                    onTimeOut();
                    return;
                }
                observer = PlayStore.observeInstalled(packageName, delay);
                observer.observe(new ObserverStateE(this){
                    @Override
                    public void onComplete(){
                        observer = null;
                        onInstalled();
                    }

                    @Override
                    public void onException(java.lang.Throwable e){
                        observer = null;
                        onTimeOut();
                    }

                    @Override
                    public void onCancel(){
                        observer = null;
                    }
                });
            }
        };
    }
    return start(consumer);
}

@Override
public DelayState pause(){
    if(observer != null){
        observer.cancel();
        observer = null;
    }
    return super.pause();
}

public void destroy(){
    if(observer != null){
        observer.cancel();
        observer = null;
    }
    consumer = null;
}

protected abstract void onInstalled();

protected abstract void onTimeOut();

}
