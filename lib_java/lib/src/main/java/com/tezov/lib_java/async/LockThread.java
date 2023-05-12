/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.async;

import com.tezov.lib_java.debug.annotation.DebugLogEnable;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.util.UtilsString;
import java.util.List;
import java.util.Set;
import com.tezov.lib_java.BuildConfig;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.wrapperAnonymous.FunctionW;
import com.tezov.lib_java.type.collection.ListKey;

import java.util.LinkedList;

public class LockThread<OWNER> extends java.util.concurrent.locks.ReentrantLock{
private OWNER owner;
private ListKey<String, Query> lockers;

public LockThread(OWNER owner){
    super(true);
DebugTrack.start().create(this).end();
    if(BuildConfig.DEBUG_ONLY){
        this.owner = owner;
        this.lockers = new ListKey<>(LinkedList::new, new FunctionW<Query, String>(){
            @Override
            public String apply(Query query){
                return query.id();
            }
        });
    }
}
@Override
public void lock(){
DebugException.start().explode("Use lock(Object inquirer) instead of lock() method").end();
}
public void lock(Object inquirer){
    if(BuildConfig.DEBUG_ONLY){
        Query query = lockers.getValue(Query.id(inquirer));
        if(query == null){
            query = new Query(inquirer);
            lockers.add(query);
        }
        query.requestLock(this);
        super.lock();
        query.lockResult(this, true);
    }
    else{
        super.lock();
    }
}
@Override
public boolean tryLock(){
DebugException.start().explode("Use tryLock(Object inquirer) instead of tryLock() method").end();
    return super.tryLock();
}
public boolean tryLock(Object inquirer){
    if(BuildConfig.DEBUG_ONLY){
        Query query = lockers.getValue(Query.id(inquirer));
        if(query == null){
            query = new Query(inquirer);
            lockers.add(query);
        }
        query.tryRequestLock(this);
        boolean success = super.tryLock();
        query.lockResult(this, success);
        if(query.count()<=0){
            lockers.remove(query);
        }
        return success;
    }
    else{
        return super.tryLock();
    }

}
@Override
public void unlock(){
DebugException.start().explode("Use unlock(Object inquirer) instead of unlock() method").end();
}
public void unlock(Object inquirer){
    if(BuildConfig.DEBUG_ONLY){
        Query query = lockers.getValue(Query.id(inquirer));
        if(query == null){
DebugException.start().log(DebugTrack.getFullSimpleName(inquirer) + " doesn't have lock").end();
            return;
        }
        query.requestUnlock(this);
        super.unlock();
        query.unlocked(this);
        if(query.count()<=0){
            lockers.remove(query);
        }
    }
    else{
        super.unlock();
    }
}
@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

private static class Query{
    String ownerName;
    int ownerHashcode;
    String thread;
    long lockedTime;
    int requestCount;
    long requestTime;
    long resultTime;
    public Query(Object inquirer){
DebugTrack.start().create(this).end();
        ownerName = DebugTrack.getFullSimpleName(inquirer);
        ownerHashcode = inquirer.hashCode();
        thread = java.lang.Thread.currentThread().getName();
        requestCount = 0;
    }
    public static String id(Object inquirer){
        return DebugTrack.getFullSimpleName(inquirer) + "_" + inquirer.hashCode() + "_" + java.lang.Thread.currentThread().getName();
    }
    public String id(){
        return ownerName + "_" + ownerHashcode + "_" + thread;
    }
    public int count(){
        return requestCount;
    }

    public void requestLock(LockThread lock){
        requestTime = Clock.MicroSecond.now();
DebugLog.start().send(lock, id() + " request lock for " + DebugTrack.getFullSimpleName(lock.owner) + " count:" + requestCount).end();
    }
    public void tryRequestLock(LockThread lock){
        requestTime = Clock.MicroSecond.now();
DebugLog.start().send(lock, id() + " try request lock for " + DebugTrack.getFullSimpleName(lock.owner) + " count:" + requestCount).end();
    }
    public void lockResult(LockThread lock, boolean success){
        resultTime = Clock.MicroSecond.now();
        if(success){
            if(requestCount == 0){
                lockedTime = resultTime;
            }
            requestCount++;
DebugLog.start().send(lock, id() + " locked for " + DebugTrack.getFullSimpleName(lock.owner) +
                        " time to lock " + (resultTime - requestTime) + "us count:" + requestCount).end();
        }
        else{
DebugLog.start().send(lock, id()
                + " failed to lock for " + DebugTrack.getFullSimpleName(lock.owner) +
                    " time to lock " + (resultTime - requestTime) + "us count:" + requestCount).end();
        }
    }

    public void requestUnlock(LockThread lock){
        requestTime = Clock.MicroSecond.now();
DebugLog.start().send(lock, id() + " request unlock for " + DebugTrack.getFullSimpleName(lock.owner)  +
                            " count:" + requestCount).end();
    }
    public void unlocked(LockThread lock){
        resultTime = Clock.MicroSecond.now();
        requestCount--;
DebugLog.start().send(lock,
                id() + " unlocked for " + DebugTrack.getFullSimpleName(lock.owner) +
                " time to unlock " + (resultTime - requestTime) + "us count:" + requestCount).end();
        if(requestCount <= 0){
DebugLog.start().send(lock, id() + " retained lock  " + ((resultTime - lockedTime) / 1000) + "ms").end();
        }
    }
    @Override
    protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
        super.finalize();
    }
}

}
