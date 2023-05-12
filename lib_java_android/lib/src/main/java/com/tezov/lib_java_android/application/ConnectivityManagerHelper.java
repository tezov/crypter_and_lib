/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.application;

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

import com.tezov.lib_java.async.notifier.observer.state.ObserverState;
import com.tezov.lib_java.async.notifier.task.TaskState;

public class ConnectivityManagerHelper extends ConnectivityManager{
private Step step = Step.DISCONNECTED;
private boolean isEnabled = false;

public ConnectivityManagerHelper(){
    Query onConnect = new Query(){
        @Override
        public void action(State state){
            synchronized(this){
                connect(state);
            }
        }
    }.addGoogleSocketTest();
    addQuery(onConnect);
    Query onDisconnect = new Query(){
        @Override
        public void action(State state){
            synchronized(this){
                disconnect(state, true);
            }
        }
    }.addDisConnectedState();
    addQuery(onDisconnect);
}

public ConnectivityManager enable(boolean flag){
    isEnabled = flag;
    return this;
}

private void setStep(Step step){
    this.step = step;
}

public TaskState.Observable connect(){
    synchronized(this){
        return connect(getState());
    }
}

private TaskState.Observable connect(State state){
    TaskState task = new TaskState();
    if(!isEnabled){
        task.notifyException("connectivity is disabled");
    } else if(step != Step.DISCONNECTED){
        if(step == Step.CONNECTED){
            task.notifyComplete();
        } else {
            task.notifyException("connectivity invalid step " + step.name());
        }
    } else {
        setStep(Step.CONNECTING);
        task.observe(new ObserverState(this){
            @Override
            public void onComplete(){
                setStep(Step.CONNECTED);
            }
        });
        onConnecting(task);
    }
    return task.getObservable();
}
protected void onConnecting(TaskState task){
    task.notifyComplete();
}

public TaskState.Observable disconnect(boolean force){
    synchronized(this){
        return disconnect(getState(), force);
    }
}

private TaskState.Observable disconnect(State state, boolean force){
    TaskState task = new TaskState();
    if(!isEnabled){
        task.notifyException(new Throwable("connectivity is disabled"));
        return task.getObservable();
    }
    if(step != Step.CONNECTED){
        if(step == Step.DISCONNECTED){
            task.notifyComplete();
        } else {
            task.notifyException(new Throwable("connectivity invalid step " + step.name()));
        }
        return task.getObservable();
    }
    setStep(Step.DISCONNECTING);
    task.observe(new ObserverState(this){
        @Override
        public void onComplete(){
            setStep(Step.DISCONNECTED);
        }
    });
    onDisconnecting(task);
    return task.getObservable();
}
protected void onDisconnecting(TaskState task){
    task.notifyComplete();
}


public enum Step{
    DISCONNECTED, CONNECTING, CONNECTED, DISCONNECTING
}

}
