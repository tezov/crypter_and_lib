/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.database.firebase;

import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.util.UtilsString;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import androidx.fragment.app.Fragment;
import com.tezov.lib_java_android.application.AppConfigKey;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.ArrayMap;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.tezov.lib_java_android.application.AppInfo;
import com.tezov.lib_java_android.application.AppConfig;
import com.tezov.lib_java.application.AppRandomNumber;
import com.tezov.lib_java.async.Handler;
import com.tezov.lib_java.async.notifier.observer.state.ObserverStateE;
import com.tezov.lib_java.async.notifier.task.TaskState;
import com.tezov.lib_java_android.database.firebase.fbTableLock.ReferenceInfo.Lock;
import com.tezov.lib_java_android.database.firebase.fbTableLock.ReferenceInfo.LockRequest;
import com.tezov.lib_java_android.database.firebase.holder.fbTablesHandle;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.runnable.RunnableGroup;
import com.tezov.lib_java.type.runnable.RunnableQueue;
import com.tezov.lib_java.type.runnable.RunnableThread;
import com.tezov.lib_java.type.runnable.RunnableTimeOut;
import com.tezov.lib_java.type.runnable.RunnableW;
import com.tezov.lib_java_android.wrapperAnonymous.FireBaseChildEventListenerW;
import com.tezov.lib_java_android.wrapperAnonymous.FirebaseCompletionListenerW;
import com.tezov.lib_java_android.wrapperAnonymous.FirebaseTransactionW;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.tezov.lib_java_android.database.firebase.fbTableLock.ReferenceInfo.entry;

//IMPROVE do not release, just extend if no remote request
public class fbTableLock{
private final static long FB_TABLE_PAUSE_TIMEOUT_DELAY_ms = AppConfig.getLong(AppConfigKey.FB_TABLE_PAUSE_TIMEOUT_DELAY_ms.getId());

private static final String GUID = AppInfo.getGUID().toHexString();
private static final long ACQUIRED_MAX_RETAIN_TIME_ms = Clock.SecondTo.MilliSecond.with(30);
private static final long ACQUIRE_REQUEST_MAX_RETAIN_TIME_ms = Clock.MinuteTo.MilliSecond.with(5);
private static final int ACQUIRE_RETRY_MAX_ATTEMPT = 15;
private static final long ACQUIRE_RETRY_MIN_DELAY_ms = Clock.SecondTo.MilliSecond.with(2);
private static final long ACQUIRE_RETRY_RANDOM_DELAY_ms = Clock.SecondTo.MilliSecond.with(3);
protected fbTablesHandle fb;
private ReferenceInfo refInfo;
private RunnableQueue<Query> pendingQueries;
private boolean isStarted = true;
private boolean isLockRemotelyRequested = false;
private long acquireRetryMinDelay_ms = ACQUIRE_RETRY_MIN_DELAY_ms;
private long acquireRetryRandomDelay_ms = ACQUIRE_RETRY_RANDOM_DELAY_ms;
private RunnableThread acquireTimeOutRunnable = null;
private RunnableThread acquireLockRunnable = null;
private FireBaseChildEventListenerW lockOnChangeListener = null;
private FireBaseChildEventListenerW lockRemoteRequestListener = null;

public fbTableLock(fbTablesHandle fb, String ref){
    try{
DebugTrack.start().create(this).end();
        this.fb = fb;
        this.refInfo = new ReferenceInfo(ref);
        pendingQueries = new RunnableQueue<>(this);
    } catch(java.lang.Throwable e){

DebugException.start().log(e).end();

    }
}

private fbTableLock me(){
    return this;
}

private void lockRemoteRequestListen(Query query){
    if(lockRemoteRequestListener != null){
        return;
    }
    lockRemoteRequestListener = new FireBaseChildEventListenerW(query.handler){
        void lockRequest(DataSnapshot dataSnapshot){
            if(isLockRemotelyRequested){
                return;
            }
            if(!dataSnapshot.getKey().equals(ReferenceInfo.entry)){

DebugException.start().log("event with invalid retrofit.data: " + dataSnapshot).end();

                return;
            }
            Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
            String guid = null;
            Long timestamp = null;

            if(dataSnapshot.getChildrenCount() > 2){
DebugException.start().log("lockRequest value count > 2").end();
            }

            while(iterator.hasNext()){
                DataSnapshot entry = iterator.next();
                if(entry.getKey().equals(LockRequest.guid)){
                    guid = dataSnapshot.child(LockRequest.guid).getValue(String.class);
                } else {
                    if(entry.getKey().equals(LockRequest.timestamp)){
                        timestamp = dataSnapshot.child(LockRequest.timestamp).getValue(Long.class);
                    }
                }
            }
            if(!GUID.equals(guid) && ((timestamp != null) && (timestamp > Clock.MilliSecond.now()))){

                Log.REQUEST_LOCK_REMOTELY.send(me(), guid);

                isLockRemotelyRequested = true;
                lockRemoteRequestUnListen();
            }
        }

        @Override
        public void onAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s){
            lockRequest(dataSnapshot);
        }

        @Override
        public void onChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s){
            lockRequest(dataSnapshot);
        }
    };
    childInfo(refInfo.lockRequest.root()).addChildEventListener(lockRemoteRequestListener);
}

private DatabaseReference childInfo(String ref){
    return fb.child(ref);
}

private void updateChildren(String ref, Map<String, Object> data, FirebaseCompletionListenerW listener){
    fb.updateChildren(ref, data, listener);
}

private void runTransaction(String ref, Transaction.Handler handler){
    fb.runTransaction(ref, handler);
}

public void setAcquireRetryMinDelay(long value, TimeUnit timeUnit){
    this.acquireRetryMinDelay_ms = timeUnit.convert(value, TimeUnit.MILLISECONDS);
}

public void setAcquireRetryRandomDelay(long value, TimeUnit timeUnit){
    this.acquireRetryRandomDelay_ms = timeUnit.convert(value, TimeUnit.MILLISECONDS);
}

public fbTableLock start(){
    synchronized(me()){
        if(isStarted){
            return this;
        }
        this.isStarted = true;
        if(!pendingQueries.isBusy()){
            nextQuery();
        }
        return this;
    }
}

public TaskState.Observable pause(boolean force){
    synchronized(me()){
        TaskState task = new TaskState();
        if(!isStarted){
            task.notifyComplete();
            return task.getObservable();
        }
        if(!pendingQueries.isBusy()){
            isStarted = false;
            task.notifyComplete();
            return task.getObservable();
        }
        RunnableTimeOut rt = new RunnableTimeOut(this, FB_TABLE_PAUSE_TIMEOUT_DELAY_ms){
            @Override
            public void onStart(){
                isStarted = false;
                if(!pendingQueries.isBusy()){
                    completed();
                } else {
                    pendingQueries.setOnDone(new RunnableW(){
                        @Override
                        public void runSafe(){
                            completed();
                        }
                    });
                }
            }

            @Override
            public void onComplete(){
                task.notifyComplete();
            }

            @Override
            public void onTimeOut(){
                pendingQueries.setOnDone(null);
                Query query = pendingQueries.current();
                if(query != null){
                    query.cancel();
                }
                task.notifyException(new TimeoutException());
            }
        };
        if(!force){
            rt.start();
        } else {
            isStarted = false;
            rt.onTimeOut();
        }
        return task.getObservable();
    }
}

private void postQuery(Query query){
    pendingQueries.add(query);
    if(isStarted && !pendingQueries.isBusy()){
        nextQuery();
    }
}

private void nextQuery(){
    if(!isStarted || pendingQueries.isEmpty()){
        lockRemoteRequestUnListen();
        pendingQueries.done();
    } else {
        Query query = pendingQueries.element();
        query.getObservable().observe(new ObserverStateE(this){
            @Override
            public void onComplete(){
                nextQuery();
            }

            @Override
            public void onException(java.lang.Throwable e){
                onComplete();
            }

            @Override
            public void onCancel(){
                onComplete();
            }
        });
        lockRemoteRequestListen(query);
        pendingQueries.next();
    }
}

private boolean removeQuery(Query query){
    return pendingQueries.removeRunnable(query);
}

public Query newQuery(Handler handler){
    return new Query(handler);
}

private void tryAcquire(Query query){
    if(query.isCanceled()){
        return;
    }
    query.setState(State.ACQUIRING);
    if(isLockRemotelyRequested){
        isLockRemotelyRequested = false;
        acquireDelayedStart(query);
        fbLockOnChangeListen(query);
        query.setState(State.WAITING_TO_RETRY);
    }
    RunnableGroup gr = new RunnableGroup(this).name("try to acquire lock");
    int LBL_LOCK_REQUEST_FILL = gr.label();
    int LBL_LOCK_REQUEST_FREE = gr.label();
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            runTransaction(refInfo.lock.root(), new FirebaseTransactionW(query.handler){
                @NonNull
                @Override
                public Transaction.Result doTransaction(@NonNull MutableData currentData){
                    if(query.isCanceled()){
                        return Transaction.abort();
                    } else {
                        Map<String, Object> data = new ArrayMap<>();
                        data.put(Lock.guid, GUID);
                        data.put(Lock.timestamp, Clock.MilliSecond.now() + query.acquiredMaxRetainTime_ms);
                        if(currentData.getValue() == null){
                            currentData.setValue(data);
                            return Transaction.success(currentData);
                        } else {
                            Long expiredTimestamp = currentData.child(Lock.timestamp).getValue(Long.class);
                            String guid = currentData.child(Lock.guid).getValue(String.class);

                            if((expiredTimestamp != null) && (expiredTimestamp < Clock.MilliSecond.now()) && (guid != null)){
                                if(!guid.equals(GUID)){
DebugException.start().logHidden("device guid: " + guid + " did not release lock within allowed time").end();
                                } else {
DebugException.start().logHidden("this device did not release lock within allowed time").end();
                                }
                            }

                            if((guid == null) || (guid.equals(GUID)) || ((expiredTimestamp != null) && (expiredTimestamp < Clock.MilliSecond.now()))){
                                currentData.setValue(data);
                                return Transaction.success(currentData);
                            } else {
                                return Transaction.abort();
                            }
                        }
                    }
                }
                @Override
                public void onDone(@Nullable DatabaseError error, boolean b, @Nullable DataSnapshot dataSnapshot){
                    if(query.isCanceled()){
                        done();
                    } else {

                        if(error != null){
DebugException.start().logHidden(error.toException()).end();
                        }

                        putValue(b);
                        if(b){
                            skipUntilLabel(LBL_LOCK_REQUEST_FREE);
                        } else {
                            skipUntilLabel(LBL_LOCK_REQUEST_FILL);
                        }
                    }
                }
            });
        }
    }.name("acquire start"));
    gr.add(new RunnableGroup.Action(LBL_LOCK_REQUEST_FILL){
        @Override
        public void runSafe(){
            Map<String, Object> data = new ArrayMap<>();
            data.put(LockRequest.guid, GUID);
            data.put(LockRequest.timestamp, Clock.MilliSecond.now() + query.acquireRequestMaxRetainTime_ms);
            updateChildren(refInfo.lockRequest.root() + "/" + entry, data, null);
            done();
        }
    }.name("lock request fill"));
    gr.add(new RunnableGroup.Action(LBL_LOCK_REQUEST_FREE){
        @Override
        public void runSafe(){
            runTransaction(refInfo.lockRequest.root(), new FirebaseTransactionW(query.handler){
                @NonNull
                @Override
                public Transaction.Result doTransaction(@NonNull MutableData currentData){
                    if(query.isCanceled()){
                        return Transaction.abort();
                    } else {
                        if(currentData.getValue() != null){
                            MutableData values = currentData.child(entry);
                            if(values.getValue() != null){
                                String guid = values.child(LockRequest.guid).getValue(String.class);
                                if(GUID.equals(guid)){
                                    currentData.setValue(null);
                                }
                            } else {
                                currentData.setValue(null);
                            }
                        }
                        return Transaction.success(currentData);
                    }
                }

                @Override
                public void onDone(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData){

                    if(!query.isCanceled()){
                        if(error != null){
DebugException.start().logHidden(error.toException()).end();
                        }
                    }

                    done();
                }
            });
        }
    }.name("lock request free"));
    gr.setOnDone(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            if(!query.isCanceled()){
                boolean result = getValue();
                if(result){
                    acquiredTimeOutStart(query);
                    query.setState(State.ACQUIRED);
                    query.acquireListener.onAcquired();
                } else {
                    acquireDelayedStart(query);
                    fbLockOnChangeListen(query);
                    query.setState(State.WAITING_TO_RETRY);
                }
            }
        }
    });
    gr.start();
}

private void acquiredTimeOutStart(Query query){
    acquiredTimeOutCancel();
    acquireTimeOutRunnable = new RunnableThread(this, query.handler){
        @Override
        public void runSafe(){
            acquireTimeOutRunnable = null;
            query.setState(State.ACQUIRE_EXPIRED);
            query.acquireListener.onAcquiredTimeout();
        }
    };
    acquireTimeOutRunnable.post(query.acquiredMaxRetainTime_ms);
}

private void acquiredTimeOutCancel(){
    if(acquireTimeOutRunnable != null){
        acquireTimeOutRunnable.cancel();
        acquireTimeOutRunnable = null;
    }

}

private void acquireDelayedStart(Query query){
    if(acquireLockRunnable != null){
        return;
    }
    acquireLockRunnable = new RunnableThread(this, query.handler){
        @Override
        public void runSafe(){
            fbLockOnChangeUnListen();
            acquireLockRunnable = null;
            query.acquire();
        }
    };
    acquireLockRunnable.post(acquireRetryMinDelay_ms + AppRandomNumber.nextLong(acquireRetryRandomDelay_ms));
}

private void acquireDelayedCancel(){
    if(acquireLockRunnable != null){
        acquireLockRunnable.cancel();
        acquireLockRunnable = null;
    }
}

private void fbLockOnChangeListen(Query query){
    if(lockOnChangeListener != null){
        return;
    }
    lockOnChangeListener = new FireBaseChildEventListenerW(query.handler){
        @Override
        public void onRemoved(@NonNull DataSnapshot dataSnapshot){
            acquireDelayedCancel();
            fbLockOnChangeUnListen();
            query.acquire();
        }
    };
    childInfo(refInfo.lock.root()).addChildEventListener(lockOnChangeListener);
}

private void fbLockOnChangeUnListen(){
    if(lockOnChangeListener == null){
        return;
    }
    childInfo(refInfo.lock.root()).removeEventListener(lockOnChangeListener);
    lockOnChangeListener = null;
}

private void lockRemoteRequestUnListen(){
    if(lockRemoteRequestListener == null){
        return;
    }
    childInfo(refInfo.lockRequest.root()).removeEventListener(lockRemoteRequestListener);
    lockRemoteRequestListener = null;
}

private void tryRelease(Query query){
    query.setState(State.RELEASING);
    Map<String, Object> data = new ArrayMap<>();
    data.put(Lock.guid, null);
    data.put(Lock.timestamp, Clock.MilliSecond.now());
    updateChildren(refInfo.lock.root(), data, new FirebaseCompletionListenerW(query.handler){
        @Override
        public void onDone(@Nullable DatabaseError databaseError, @NonNull DatabaseReference ref){
            acquiredTimeOutCancel();
            if(databaseError == null){
                query.setState(State.RELEASED);
                query.releaseListener.onReleased();
            } else {
                query.setState(State.RELEASE_FAILED);
                query.releaseListener.onReleaseFailed();
            }
            query.task.notifyComplete();
        }
    });
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}


public enum State{
    INITIALIZED, PENDING, ACQUIRING, WAITING_TO_RETRY, ACQUIRED, ACQUIRE_FAILED, ACQUIRE_CANCELED, ACQUIRE_EXPIRED, RELEASING, RELEASED, RELEASE_FAILED, CANCELED,
}

private enum Log{
    STATE, ACQUIRED_TIME, RELEASED_TIME, REQUEST_LOCK_REMOTELY;
    void send(fbTableLock tableLock, String s){

DebugLog.start().send(tableLock, IntTo.StringHex(tableLock.hashCode()) + " " + name() + " " + s).end();

    }
}

public interface OnAcquireListener{
    void onAcquired();

    void onAcquireFailed();

    void onAcquiredTimeout();

}

public interface OnReleaseListener{
    void onReleased();

    void onAcquiredCanceled();

    void onReleaseFailed();

}

static class ReferenceInfo{
    final static String entry = "entry";
    final static String ref = "info";
    String root;
    Lock lock;
    LockRequest lockRequest;
    ReferenceInfo(String root){
        this.root = root + "/" + ref;
        this.lock = ()->this.root + "/" + Lock.ref;
        this.lockRequest = ()->this.root + "/" + LockRequest.ref;
    }
    interface Lock{
        String ref = "lock";
        String guid = "guid";
        String timestamp = "timestamp";

        String root();

    }

    interface LockRequest{
        String ref = "lockRequest";
        String guid = "guid";
        String timestamp = "timestamp";

        String root();

    }

}

public class Query extends RunnableW{
    private final Handler handler;
    private final TaskState task;
    private long acquireRequestMaxRetainTime_ms = ACQUIRE_REQUEST_MAX_RETAIN_TIME_ms;
    private State state;
    private int attempt = 0;
    private int acquireMaxAttempt = ACQUIRE_RETRY_MAX_ATTEMPT;
    private long timeStamp;
    private long acquiredMaxRetainTime_ms = ACQUIRED_MAX_RETAIN_TIME_ms;
    private OnAcquireListener acquireListener = null;
    private OnReleaseListener releaseListener = null;

    private Query(Handler handler){
        timeStamp = Clock.MilliSecond.now();
        task = new TaskState();
        state = State.INITIALIZED;
        this.handler = handler;
    }
    private TaskState.Observable getObservable(){
        return task.getObservable();
    }

    public void acquire(OnAcquireListener onAcquireListener){

        if(this.acquireListener != null){
DebugException.start().explode("onAcquire ignored").end();
            return;
        }

        this.acquireListener = onAcquireListener;
        setState(State.PENDING);
        synchronized(me()){
            me().postQuery(this);
        }
    }
    public void release(OnReleaseListener onReleaseListener){

        if(this.releaseListener != null){
DebugException.start().explode("onRelease ignored").end();
            return;
        }

        this.releaseListener = onReleaseListener;
        release();
    }

    private void cancel(){
        setState(State.CANCELED);
        task.cancel();
        task.notifyCanceled();
    }
    private boolean isCanceled(){
        return task.isCanceled();
    }

    public State getState(){
        return state;
    }
    private void setState(State state){

        Log.STATE.send(me(), state.name());

        this.state = state;
        if(state == State.ACQUIRED){

            Log.ACQUIRED_TIME.send(me(), "time to acquire: " + Clock.MilliSecondTo.MilliSecond.Elapsed.toString(timeStamp) + "ms");

            timeStamp = Clock.MilliSecond.now();
        } else if(state == State.RELEASED){

            Log.RELEASED_TIME.send(me(), "time acquired: " + Clock.MilliSecondTo.MilliSecond.Elapsed.toString(timeStamp) + "ms");

        }
    }

    public Query setAcquireMaxAttempt(int value){
        this.acquireMaxAttempt = value;
        return this;
    }
    public Query setAcquiredMaxRetainTime(long value, TimeUnit timeUnit){
        this.acquiredMaxRetainTime_ms = timeUnit.convert(value, TimeUnit.MILLISECONDS);
        return this;
    }
    public Query setAcquireRequestMaxRetainTime(long value, TimeUnit timeUnit){
        this.acquireRequestMaxRetainTime_ms = timeUnit.convert(value, TimeUnit.MILLISECONDS);
        return this;
    }

    @Override
    final public void runSafe(){
        acquire();
    }
    private void acquire(){
        if(attempt >= acquireMaxAttempt){
            setState(State.ACQUIRE_FAILED);
            acquireListener.onAcquireFailed();
            task.notifyException(new Throwable("acquireMaxAttempt " + acquireMaxAttempt));
        } else {
            attempt++;
            me().tryAcquire(this);
        }
    }
    private void release(){
        if(state == State.PENDING){
            if(me().removeQuery(this)){
                setState(State.ACQUIRE_CANCELED);
                releaseListener.onAcquiredCanceled();
                task.notifyCanceled();
            } else {
                setState(State.RELEASE_FAILED);
                releaseListener.onReleaseFailed();
                task.notifyException("release failed");
            }
        } else if(state == State.ACQUIRING){
            setState(State.ACQUIRE_CANCELED);
            releaseListener.onAcquiredCanceled();
            task.notifyComplete();
        } else if(state == State.WAITING_TO_RETRY){
            me().acquireDelayedCancel();
            me().fbLockOnChangeUnListen();
            if(me().removeQuery(this)){
                setState(State.ACQUIRE_CANCELED);
                releaseListener.onAcquiredCanceled();
                task.notifyCanceled();
            } else {
                setState(State.RELEASE_FAILED);
                releaseListener.onReleaseFailed();
                task.notifyException("release failed");
            }
        } else if(state == State.ACQUIRE_EXPIRED){
            setState(State.RELEASE_FAILED);
            releaseListener.onReleaseFailed();
            task.notifyException("release failed");
        } else if(state == State.ACQUIRED){
            me().tryRelease(this);
        } else {
            setState(State.RELEASE_FAILED);
            releaseListener.onReleaseFailed();
            task.notifyException("release failed");
        }
    }

}

}



