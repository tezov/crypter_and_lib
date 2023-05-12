/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.recycler;

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

import com.tezov.lib_java.async.LockThread;
import com.tezov.lib_java.async.notifier.Notifier;
import com.tezov.lib_java.async.notifier.observable.ObservableEvent;
import com.tezov.lib_java.async.notifier.observer.event.ObserverEvent;
import com.tezov.lib_java.definition.defProvider;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.defEnum.Event;
import com.tezov.lib_java.type.ref.Ref;
import com.tezov.lib_java.type.ref.WR;
import com.tezov.lib_java.type.runnable.RunnableW;

import java.util.List;

public abstract class RecyclerListDataManager<TYPE> implements defProvider<TYPE>{
private final Notifier<Event.Is> notifier;
private LockThread<RecyclerListDataManager<TYPE>> threadLock = null;
private Class<TYPE> type;
private WR<RecyclerListRowManager<TYPE>> rowManagerWR;
private Filter defaultFilter = null;

protected RecyclerListDataManager(Class<TYPE> type){
DebugTrack.start().create(this).end();
    this.type = type;
    notifier = new Notifier<>(new ObservableEvent<Event.Is, TYPE>(), false);
}

protected <DM extends RecyclerListDataManager<TYPE>> DM me(){
    return (DM)this;
}

public void attach(RecyclerListRowManager<TYPE> adapter){
    this.rowManagerWR = WR.newInstance(adapter);
}

private LockThread<RecyclerListDataManager<TYPE>> getThreadLock(){
    if(threadLock == null){
        threadLock = new LockThread<>(this);
    }
    return threadLock;
}

@Override
public boolean tryAcquireLock(Object inquirer){
    return getThreadLock().tryLock(inquirer);
}

@Override
public void acquireLock(Object inquirer){
    getThreadLock().lock(inquirer);
}

@Override
public void releaseLock(Object inquirer){
    getThreadLock().unlock(inquirer);
}

protected void onAttachedToRecyclerView(){

}

protected void onDetachedFromRecyclerView(){

}

public boolean isAttachedToRecyclerView(){
    RecyclerListRowManager<TYPE> rowManager = getRowManager();
    return (rowManager != null) && rowManager.isAttachedToRecyclerView();
}

public boolean isRecyclerViewAttachedToWindow(){
    RecyclerListRowManager<TYPE> rowManager = getRowManager();
    return (rowManager != null) && rowManager.isAttachedToWindow();
}

public Class<TYPE> getType(){
    return type;
}

protected void setType(Class<TYPE> type){
    this.type = type;
}

public RecyclerListRowManager<TYPE> getRowManager(){
    return Ref.get(rowManagerWR);
}

public void observe(ObserverEvent<Event.Is, TYPE> observer){
    notifier.register(observer);
}

public void unObserve(Object owner){
    notifier.unregister(owner);
}

public void unObserveAll(){
    notifier.unregisterAll();
}

public void post(Event.Is event, TYPE data){
    ObservableEvent<Event.Is, TYPE>.Access access = notifier.obtainAccess(this, event);
    access.setValue(data);
}

public void postIfDifferent(Event.Is event, TYPE data){
    ObservableEvent<Event.Is, TYPE>.Access access = notifier.obtainAccess(this, event);
    access.setValueIfDifferent(data);
}

public <T> Filter<T> getDefaultFilter(){
    return defaultFilter;
}

public <DM extends RecyclerListDataManager<TYPE>> DM setDefaultFilter(Filter defaultFilter){
    this.defaultFilter = defaultFilter;
    return (DM)this;
}

public <T> void filter(T value){
    filter(getDefaultFilter().setValue(value));
}

public <T> void filter(Filter<T> filter){

DebugException.start().notImplemented().end();

}

@Override
public Integer getFirstIndex(){

DebugException.start().notImplemented().end();

    return null;
}

@Override
public Integer getLastIndex(){

DebugException.start().notImplemented().end();

    return null;
}

@Override
public List<TYPE> select(int offset, int length){

DebugException.start().notImplemented().end();

    return null;
}

@Override
public Integer indexOf(TYPE data){

DebugException.start().notImplemented().end();

    return null;
}

@Override
public TYPE remove(int position){

DebugException.start().notImplemented().end();

    return null;
}

@Override
public TYPE putToTrash(int index){

DebugException.start().notImplemented().end();

    return null;
}

@Override
public TYPE restoreFromTrash(int index, TYPE data){

DebugException.start().notImplemented().end();

    return null;
}

public void reset(){
}

public void destroy(){
}

public boolean postToHandler(RunnableW runnable){
    RecyclerListRowManager rowManager = getRowManager();
    if(rowManager == null){
        return false;
    } else {
        return rowManager.postToHandler(runnable);
    }
}

public void postInserted(int position){
    getRowManager().postInserted(position);
}

public void postUpdated(int position){
    getRowManager().postUpdate(position);
}

public void postUpdatedAll(boolean animate){
    getRowManager().postUpdatedAll(animate);
}

public void postRemoved(int position){
    getRowManager().postRemoved(position);
}

@Override
final public void toDebugLog(){
    acquireLock(this);
    int size = size();
    if(size > 0){
        for(int i = 0; i < size; i++){
DebugLog.start().send(get(i)).end();
        }
    } else {
DebugLog.start().send(DebugTrack.getFullName(this) + " is null").end();
    }
    releaseLock(this);
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

public abstract static class Filter<T>{
    private T value = null;

    public Filter(){
    }

    public T getValue(){
        return value;
    }

    public <F extends Filter<T>> F setValue(T value){
        this.value = value;
        return (F)this;
    }

}


}
