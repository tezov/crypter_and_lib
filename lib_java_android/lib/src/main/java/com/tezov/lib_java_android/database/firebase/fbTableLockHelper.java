/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.database.firebase;

import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.util.UtilsString;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import androidx.fragment.app.Fragment;

import com.tezov.lib_java.async.notifier.task.TaskState;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.ref.WR;
import com.tezov.lib_java.type.runnable.RunnableW;

import java.util.concurrent.TimeUnit;

public class fbTableLockHelper{
private final WR<fbTable.Ref> fbTableWR;
private final WR<Object> ownerWR;
private fbTableLock.Query lockQuery = null;

public fbTableLockHelper(fbTable.Ref fbTable, Object owner){
    this.fbTableWR = WR.newInstance(fbTable);
DebugTrack.start().create(this).end();
    ownerWR = WR.newInstance(owner);
}
public Object getOwner(){
    return ownerWR.get();
}

private long getTime(){
    return Clock.MicroSecond.now();
}
private String getTimeUnit(){
    return "us"; //microsecond
}

public TaskState.Observable acquireLock(Long acquiredMaxRetainTime_ms, Long acquireRequestMaxRetainTime_ms, RunnableW runnableOnTimeOut){
    TaskState task = new TaskState();

DebugLog.start().send(this, DebugTrack.getFullSimpleName(getOwner()) + " lock request for " + fbTableWR.get().getName() + "Thread:" + Thread.currentThread().getName()).end();
    long requestTime = getTime();
    if(lockQuery != null){
DebugException.start().explode("not null").end();
    }

    lockQuery = fbTableWR.get().getLockQuery();
    if(acquiredMaxRetainTime_ms != null){
        lockQuery.setAcquiredMaxRetainTime(acquiredMaxRetainTime_ms, TimeUnit.MILLISECONDS);
    }
    if(acquireRequestMaxRetainTime_ms != null){
        lockQuery.setAcquireRequestMaxRetainTime(acquireRequestMaxRetainTime_ms, TimeUnit.MILLISECONDS);
    }
    lockQuery.acquire(new fbTableLock.OnAcquireListener(){
        @Override
        public void onAcquired(){

            long lockedTime = getTime();
DebugLog.start().send(this, DebugTrack.getFullSimpleName(getOwner()) + " locked " + fbTableWR.get().getName() + "Thread:" + Thread.currentThread().getName() + " " + (lockedTime - requestTime) +
                                getTimeUnit()).end();

            task.notifyComplete();
        }
        @Override
        public void onAcquireFailed(){
            task.notifyException("acquire failed");
        }
        @Override
        public void onAcquiredTimeout(){
DebugException.start().log("lock timeout " + fbTableWR.get().getName() + " owner:" + ownerWR.getType()).end();
            if(runnableOnTimeOut != null){
                runnableOnTimeOut.run();
            }
        }
    });
    return task.getObservable();
}
public TaskState.Observable releaseLock(){
    TaskState task = new TaskState();
    if(lockQuery == null){
        task.notifyComplete();
    } else {

DebugLog.start().send(this, DebugTrack.getFullSimpleName(getOwner()) + " unlock request for " + fbTableWR.get().getName() + "Thread:" + Thread.currentThread().getName()).end();
        long requestTime = getTime();

        lockQuery.release(new fbTableLock.OnReleaseListener(){
            @Override
            public void onReleased(){

                long unlockedTime = getTime();
DebugLog.start().send(this,
                        DebugTrack.getFullSimpleName(getOwner()) + " unlocked " + fbTableWR.get().getName() + "Thread:" + Thread.currentThread().getName() + " " + (unlockedTime - requestTime) +
                        getTimeUnit()).end();

                task.notifyComplete();
            }
            @Override
            public void onAcquiredCanceled(){
                task.notifyComplete();
            }
            @Override
            public void onReleaseFailed(){
                task.notifyException("failed to release lock");
            }
        });
    }
    return task.getObservable();
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

}
