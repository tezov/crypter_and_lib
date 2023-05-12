/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.database.firebase;

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.util.UtilsString;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;

import com.tezov.lib_java.async.IteratorBufferAsync;
import com.tezov.lib_java.async.notifier.Subscription;
import com.tezov.lib_java.async.notifier.observer.event.ObserverEvent;
import com.tezov.lib_java.async.notifier.observer.value.ObserverValue;
import com.tezov.lib_java.async.notifier.observer.value.ObserverValueE;
import com.tezov.lib_java.async.notifier.task.Task;
import com.tezov.lib_java.async.notifier.task.TaskState;
import com.tezov.lib_java.async.notifier.task.TaskValue;
import com.tezov.lib_java_android.database.ItemBase;
import com.tezov.lib_java.definition.defProviderAsync;
import com.tezov.lib_java.toolbox.Nullify;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.collection.ListOrObject;
import com.tezov.lib_java.type.runnable.RunnableW;

import java.util.List;

import static com.tezov.lib_java_android.database.firebase.fbTable.Event;

public class fbTableIterable<ITEM extends ItemBase<ITEM>> implements Iterable<ITEM>, defProviderAsync<ITEM>{
private static final Integer PRELOAD_SIZE = 30;
private static final Integer PRELOAD_TRIGGER = 10;
private final SRwNOC<ITEM> size;
private final boolean notificationEnabled = true;
protected fbTable<ITEM>.Ref fb;
private boolean isSizeValid;

public fbTableIterable(fbTable<ITEM>.Ref fb){
DebugTrack.start().create(this).end();
    this.fb = fb;
    isSizeValid = false;
    this.size = new SRwNOC<>(0);
    fb.observe((ObserverEvent)new SizeObserver(this));
}

protected fbTableIterable<ITEM> me(){
    return this;
}

public <D extends fbTable<ITEM>.Ref> D database(){
    return (D)fb;
}

public Subscription observeOnChanged(ObserverValue<SRwNOC.Value<ITEM>> observer){
    return size.observe(observer);
}

public void unObserveOnChanged(Object owner){
    size.unObserve(owner);
}

private void invalidate(){
    isSizeValid = false;
}

@NonNull
@Override
public IteratorBufferAsync<ITEM> iterator(){
    return new IteratorBufferAsync<>(this).setSizeToDownload(PRELOAD_SIZE).setTriggerSizeBeforeDownload(PRELOAD_TRIGGER);
}

public TaskState.Observable acquireLock(Object inquirer){
    return fb.acquireLock(inquirer);
}
public TaskState.Observable acquireLock(Object inquirer, RunnableW runnableOnTimeOut){
    return fb.acquireLock(inquirer, runnableOnTimeOut);
}
@Override
public TaskState.Observable acquireLock(Object inquirer, Long maxRetainTime_ms, Long acquireRequestMaxRetainTime_ms, RunnableW runnableOnTimeOut){
    return fb.acquireLock(inquirer, maxRetainTime_ms, acquireRequestMaxRetainTime_ms, runnableOnTimeOut);
}

@Override
public TaskState.Observable releaseLock(Object inquirer){
    return fb.releaseLock(inquirer);
}

@Override
public TaskValue<Integer>.Observable getFirstIndex(){
    return fb.getFirstIndex();
}

@Override
public TaskValue<Integer>.Observable getLastIndex(){
    return fb.getLastIndex();
}

private void notifySizeChange(Event event, ListOrObject<ITEM> items){
    fb.size().observe(new ObserverValueE<Integer>(this){
        @Override
        public void onComplete(Integer integer){
            if(notificationEnabled){
                size.set(integer, items);
            } else {
                invalidate();
            }
        }

        @Override
        public void onException(Integer integer, java.lang.Throwable e){

DebugException.start().log(e).end();

        }
    });
}

@Override
public TaskValue<Integer>.Observable size(){
    TaskValue<Integer> task = new TaskValue<>();
    if(!isSizeValid){
        fb.size().observe(new ObserverValueE<Integer>(this){
            @Override
            public void onComplete(Integer size){
                fbTableIterable.this.size.set(size);
                isSizeValid = true;
                task.notifyComplete(size);
            }

            @Override
            public void onException(Integer size, java.lang.Throwable e){

DebugException.start().log(e).end();

                task.notifyException(null, e);
            }
        });
    } else {
        task.notifyComplete(size.get());
    }
    return task.getObservable();
}

@Override
public TaskValue<List<ITEM>>.Observable select(int offset, int length){
    return fb.select(offset, length);
}

@Override
public TaskValue<ITEM>.Observable get(int index){
    return fb.get(index);
}

@Override
public TaskValue<Integer>.Observable indexOf(ITEM item){
    return fb.indexOf(item);
}

@Override
public TaskValue<ITEM>.Observable putToTrash(int index){

DebugException.start().notImplemented().end();

    return null;
}

@Override
public TaskValue<ITEM>.Observable restoreFromTrash(int index, ITEM item){

DebugException.start().notImplemented().end();

    return null;
}

@Override
public TaskValue<ITEM>.Observable remove(int index){

DebugException.start().notImplemented().end();

    return null;
}

@Override
public TaskState.Observable toDebugLog(){
    return fb.toDebugLog();
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

public static class SRwNOC<ITEM> extends com.tezov.lib_java.type.ref.SRwNOC<Integer>{
    public SRwNOC(Integer integer){
        super(integer);
    }

    @Override
    protected Value<ITEM> newValue(){
        return new Value<>();
    }

    @Override
    protected Value<ITEM> getValue(){
        return super.getValue();
    }

    @Override
    public void set(Integer size){
        getValue().set((ListOrObject<ITEM>)null);
        notificationEnable(false);
        super.set(size);
        notificationEnable(true);
    }

    public void set(Integer size, ListOrObject<ITEM> itemsChanged){
        getValue().set(Nullify.collection(itemsChanged));
        super.set(size);
    }

    public static class Value<ITEM> extends com.tezov.lib_java.type.ref.SRwNOC.Value<Integer>{
        public ListOrObject<ITEM> itemsChanged = null;

        public Value<ITEM> set(ListOrObject<ITEM> itemsChanged){
            this.itemsChanged = itemsChanged;
            return this;
        }

        @NonNull
        @Override
        public DebugString toDebugString(){
            DebugString data = super.toDebugString();
            if(itemsChanged != null){
                for(ITEM item: itemsChanged){
                    data.append("\n").append(item);
                }
            }
            return data;
        }

    }

}

private static class SizeObserver extends ObserverEvent<Event, ListOrObject>{
    public SizeObserver(fbTableIterable owner){
        super(owner);
    }

    @Override
    public fbTableIterable getOwner(){
        return super.getOwner();
    }

    @Override
    public void onComplete(Event event, ListOrObject list){
        getOwner().notifySizeChange(event, list);
    }

}

}
