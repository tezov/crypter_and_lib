/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.async;

import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.util.UtilsString;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java.async.notifier.observer.state.ObserverStateE;
import com.tezov.lib_java.async.notifier.observer.value.ObserverValueE;
import com.tezov.lib_java.async.notifier.task.TaskState;
import com.tezov.lib_java.async.notifier.task.TaskValue;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.definition.defProviderAsync;
import com.tezov.lib_java.toolbox.IteratorBuffer;
import com.tezov.lib_java.type.runnable.RunnableGroup;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

//TODO low update with check direction on get (see kotin async version
//TODO only 1 thread update instead of future with new thread each time
// Obsolete use kotlin version

public class IteratorBufferAsync<T> implements ListIterator<T>{
private defProviderAsync<T> provider;
private RunnableFuture futureUpdater = null;
private boolean updateTailInProgress = false;
private boolean updateHeadInProgress = false;
private List<T> dataList;
private Integer currentIndex;
private Integer lastIndex;
private Integer firstIndex;
private int triggerSizeBeforeDownload = 5;
private int sizeToDownload = 20;

public IteratorBufferAsync(){
    this(null, null);
}
public IteratorBufferAsync(defProviderAsync<T> provider){
    this(provider, null);
}
public IteratorBufferAsync(defProviderAsync<T> provider, Integer startIndex){
DebugTrack.start().create(this).end();
    this.provider = provider;
    if(provider != null){
        setNextIndexNoSync(startIndex);
    }
}

private IteratorBufferAsync<T> me(){
    return this;
}

public IteratorBufferAsync<T> setTriggerSizeBeforeDownload(int size){
    synchronized(me()){
        triggerSizeBeforeDownload = size;
        if(currentIndex != null){
            updateHeadAndTailNoSync();
        }
    }
    return this;
}
public IteratorBufferAsync<T> setSizeToDownload(int size){
    synchronized(me()){
        sizeToDownload = size;
    }
    return this;
}
public IteratorBufferAsync<T> setProvider(defProviderAsync<T> provider){
    return setProvider(provider, null);
}
public IteratorBufferAsync<T> setProvider(defProviderAsync<T> provider, Integer startIndex){
    synchronized(me()){
        this.provider = provider;
        setNextIndexNoSync(startIndex);
    }
    return this;
}
public defProviderAsync<T> getProvider(){
    synchronized(me()){
        return provider;
    }
}

private TaskState.Observable setNextIndexNoSync(Integer index){
    TaskState task = new TaskState();
    if(index == null){
        provider.getFirstIndex().observe(new ObserverValueE<Integer>(this){
            @Override
            public void onComplete(Integer index){
                firstIndex = index;
                if(firstIndex != null){
                    lastIndex = firstIndex - 1;
                    currentIndex = lastIndex;
                    setDataList(newDataList());
                    updateHeadAndTailNoSync().observe(new ObserverStateE(this){
                        @Override
                        public void onComplete(){
                            task.notifyComplete();
                        }
                        @Override
                        public void onException(java.lang.Throwable e){
                            task.notifyException(e);
                        }
                    });
                } else {
                    lastIndex = null;
                    currentIndex = null;
                    setDataList(null);
                    task.notifyComplete();
                }
            }
            @Override
            public void onException(Integer index, java.lang.Throwable e){
                task.notifyException(e);
            }
        });
    }
    else {
        provider.size().observe(new ObserverValueE<Integer>(this){
            @Override
            public void onComplete(Integer size){
                if(index >= size){
                    task.notifyException(new IndexOutOfBoundsException("index:" + index + " size" + ":" + size));
                    return;
                }
                firstIndex = index;
                lastIndex = firstIndex - 1;
                currentIndex = lastIndex;
                setDataList(newDataList());
                updateHeadAndTailNoSync().observe(new ObserverStateE(this){
                    @Override
                    public void onComplete(){
                        task.notifyComplete();
                    }
                    @Override
                    public void onException(java.lang.Throwable e){
                        task.notifyException(e);
                    }
                });
            }

            @Override
            public void onException(Integer size, java.lang.Throwable e){
                task.notifyException(e);
            }
        });
    }
    return task.getObservable();
}
public TaskState.Observable resetIndex(){
    synchronized(me()){
        return setNextIndexNoSync(null);
    }
}
public TaskState.Observable setNextIndex(Integer index){
    synchronized(me()){
        if(isLoadedNoSync(index)){
            currentIndex = index;
            return updateHeadAndTailNoSync();
        } else {
            return setNextIndexNoSync(index);
        }
    }
}
private TaskState.Observable updateHeadAndTailNoSync(){
    TaskState task = new TaskState();
    RunnableGroup gr = new RunnableGroup(this).name("updateHeadAndTail");
    if(!updateTailInProgress){
        updateTailInProgress = true;
        gr.add(new RunnableGroup.Action(){
            @Override
            public void runSafe(){
                updateTailNoSync().observe(new ObserverStateE(this){
                    @Override
                    public void onComplete(){
                        updateTailInProgress = false;
                        next();
                    }
                    @Override
                    public void onException(java.lang.Throwable e){
                        updateTailInProgress = false;
                        putException(e);
                        done();
                    }
                });
            }
        }.name("updateTail"));
    }
    if(!updateHeadInProgress){
        updateHeadInProgress = true;
        gr.add(new RunnableGroup.Action(){
            @Override
            public void runSafe(){
                updateHeadNoSync().observe(new ObserverStateE(this){
                    @Override
                    public void onComplete(){
                        updateHeadInProgress = false;
                        next();
                    }
                    @Override
                    public void onException(java.lang.Throwable e){
                        updateHeadInProgress = false;
                        putException(e);
                        done();
                    }
                });
            }
        }.name("updateHead"));
    }
    gr.notifyOnDone(task);
    gr.start();
    return task.getObservable();
}

private List<T> newDataList(){
    return new ArrayList<>();
}
private List<T> getDataList(){
    return dataList;
}
private void setDataList(List<T> list){
    dataList = list;
}

private void update(RunnableFuture r, boolean waitCompletion){
    if((futureUpdater != null) && (futureUpdater.isAlive())){
        if(waitCompletion){
            futureUpdater.join();
        }
DebugException.start().log("runnable ignored").end();
    }
    futureUpdater = r;
    futureUpdater.start(waitCompletion);
}

private void reduceHeadNoSync(){
    if((currentIndex - firstIndex) <= sizeToDownload){
        return;
    }
    Integer sizeToRemove = (currentIndex - firstIndex) - sizeToDownload;
    setDataList(dataList.subList(sizeToRemove, dataList.size()));
DebugLog.start().send(me(), "reduce head from " + firstIndex + " to " + (firstIndex + sizeToRemove)).end();
    firstIndex += sizeToRemove;
}
private TaskState.Observable updateTailNoSync(){
    TaskState task = new TaskState();
    if((lastIndex - currentIndex) > triggerSizeBeforeDownload){
        task.notifyComplete();
    } else {
        int index = lastIndex + 1;
        provider.select(index, sizeToDownload).observe(new ObserverValueE<List<T>>(this){
            @Override
            public void onComplete(List<T> newDatas){
                if((newDatas == null) || (newDatas.size() <= 0)){
                    task.notifyComplete();
                    return;
                }
                if(index <= lastIndex){
                    int diff = lastIndex - index;
                    if((index + diff) > (newDatas.size() - 1)){
                        return;
                    } else {
                        newDatas = newDatas.subList(index + diff + 1, newDatas.size());
                    }
                }
                if(newDatas.size() <= 0){
                    task.notifyComplete();
                    return;
                }

DebugLog.start().send(me(), "increase tail from " + (lastIndex + 1) + " to " + (lastIndex + newDatas.size())).end();

                lastIndex += newDatas.size();
                getDataList().addAll(newDatas);
                reduceHeadNoSync();
                task.notifyComplete();
            }
            @Override
            public void onException(List<T> data, java.lang.Throwable e){
                task.notifyException(e);
            }
        });
    }
    return task.getObservable();
}
@Override
public boolean hasNext(){
    synchronized(me()){
        if(currentIndex == null){
            update(new RunnableFuture(this){
                @Override
                public void runSafe(){
                    setNextIndexNoSync(null).observe(new ObserverStateE(this){
                        @Override
                        public void onComplete(){
                            done();
                        }

                        @Override
                        public void onException(java.lang.Throwable e){

DebugException.start().log(e).end();

                            done();
                        }

                        @Override
                        public void onCancel(){
                            done();
                        }
                    });
                }
            }, true);
        }
        else if(currentIndex >= lastIndex){
            if(updateTailInProgress && futureUpdater.isAlive()){
                futureUpdater.join();
            } else {
                update(new RunnableFuture(this){
                    @Override
                    public void runSafe(){
                        updateTailNoSync().observe(new ObserverStateE(this){
                            @Override
                            public void onComplete(){
                                done();
                            }

                            @Override
                            public void onException(java.lang.Throwable e){

DebugException.start().log(e).end();

                                done();
                            }

                            @Override
                            public void onCancel(){
                                done();
                            }
                        });
                    }
                }, true);
            }
        }
        else if(!updateTailInProgress){
            updateTailInProgress = true;
            update(new RunnableFuture(this){
                    @Override
                    public void runSafe(){
                        updateTailNoSync().observe(new ObserverStateE(this){
                            @Override
                            public void onComplete(){
                                done();
                                updateTailInProgress = false;
                            }
                            @Override
                            public void onException(java.lang.Throwable e){

DebugException.start().log(e).end();

                                done();
                                updateTailInProgress = false;
                            }
                            @Override
                            public void onCancel(){
                                updateTailInProgress = false;
                                done();
                            }
                        });
                    }
                }, false);
        }
        return (currentIndex != null) && (currentIndex < lastIndex);
    }
}
@Override
public T next(){
    synchronized(me()){
        currentIndex++;
        return getDataList().get(currentIndex - firstIndex);
    }
}

private void reduceTailNoSync(){
    if((lastIndex - currentIndex) <= sizeToDownload){
        return;
    }
    int sizeToRemove = (lastIndex - currentIndex) - sizeToDownload - 1;
    setDataList(dataList.subList(0, dataList.size() - sizeToRemove));
DebugLog.start().send(me(), "reduce tail from " + lastIndex + " to " + (lastIndex - sizeToRemove)).end();
    lastIndex -= sizeToRemove;
}
private TaskState.Observable updateHeadNoSync(){
    TaskState task = new TaskState();
    if((currentIndex - firstIndex) >= triggerSizeBeforeDownload){
        task.notifyComplete();
    } else {
        int finalIndex = firstIndex - sizeToDownload;
        provider.select(finalIndex, sizeToDownload).observe(new ObserverValueE<List<T>>(this){
            @Override
            public void onComplete(List<T> newDatas){
                if((newDatas == null) || (newDatas.size() <= 0)){
                    task.notifyComplete();
                    return;
                }
                int index = finalIndex + (sizeToDownload - newDatas.size());
                if(firstIndex < (index + sizeToDownload)){
                    int diff = firstIndex - index;
                    if(diff > 0){
                        newDatas = newDatas.subList(0, diff);
                    } else {
                        newDatas.clear();
                    }
                }

DebugLog.start().send(me(), "increase head from " + (firstIndex - 1) + " to " + (firstIndex - newDatas.size())).end();

                if(newDatas.size() <= 0){
                    task.notifyComplete();
                    return;
                }
                firstIndex -= newDatas.size();
                newDatas.addAll(getDataList());
                setDataList(newDatas);
                reduceTailNoSync();
                task.notifyComplete();
            }
            @Override
            public void onException(List<T> data, java.lang.Throwable e){
                task.notifyException(e);
            }
        });
    }
    return task.getObservable();
}
@Override
public boolean hasPrevious(){
    synchronized(me()){
        if(currentIndex == null){
            update(new RunnableFuture(this){
                @Override
                public void runSafe(){
                    setNextIndexNoSync(null).observe(new ObserverStateE(this){
                        @Override
                        public void onComplete(){
                            done();
                        }

                        @Override
                        public void onException(java.lang.Throwable e){

DebugException.start().log(e).end();

                            done();
                        }

                        @Override
                        public void onCancel(){
                            done();
                        }
                    });
                }
            }, true);
        } else if(firstIndex >= currentIndex){
            if(updateHeadInProgress && futureUpdater.isAlive()){
                futureUpdater.join();
            } else {
                update(new RunnableFuture(this){
                    @Override
                    public void runSafe(){
                        updateHeadNoSync().observe(new ObserverStateE(this){
                            @Override
                            public void onComplete(){
                                done();
                            }

                            @Override
                            public void onException(java.lang.Throwable e){

DebugException.start().log(e).end();

                                done();
                            }

                            @Override
                            public void onCancel(){
                                done();
                            }
                        });
                    }
                }, true);
            }
        } else {
            if(!updateHeadInProgress){
                updateHeadInProgress = true;
                update(new RunnableFuture(this){
                    @Override
                    public void runSafe(){
                        updateHeadNoSync().observe(new ObserverStateE(this){
                            @Override
                            public void onComplete(){
                                done();
                                updateHeadInProgress = false;
                            }

                            @Override
                            public void onException(java.lang.Throwable e){

DebugException.start().log(e).end();

                                done();
                                updateHeadInProgress = false;
                            }

                            @Override
                            public void onCancel(){
                                updateHeadInProgress = false;
                                done();
                            }
                        });
                    }
                }, false);
            }
        }
        return (currentIndex != null) && (firstIndex <= currentIndex);
    }
}
@Override
public T previous(){
    synchronized(me()){
        T t = getDataList().get(currentIndex - firstIndex);
        currentIndex--;
        return t;
    }
}

private boolean isLoadedNoSync(Integer index){
    return (currentIndex != null) && ((index >= firstIndex) && (index <= lastIndex));
}
public boolean isLoaded(Integer index){
    synchronized(me()){
        return isLoadedNoSync(index);
    }
}

//TODO low update with check direction
public T get(int index){
    synchronized(me()){
        if(isLoadedNoSync(index)){
            currentIndex = index;
            updateHeadAndTailNoSync();
        } else {
            new com.tezov.lib_java.type.runnable.RunnableFuture<>(this){
                @Override
                public void runSafe(){
                    setNextIndexNoSync(index).observe(new ObserverStateE(this){
                        @Override
                        public void onComplete(){
                            currentIndex = index;
                            done();
                        }
                        @Override
                        public void onException(java.lang.Throwable e){
DebugException.start().log(e).end();
                            done();
                        }
                    });
                }
            }.start(true);
        }
        return dataList.get(index - firstIndex);
    }
}
public Integer indexOfFromBuffer(T t){
    synchronized(me()){
        List<T> dataList = getDataList();
        if(dataList == null){
            return null;
        }
        Iterator<T> iterator = dataList.iterator();
        for(int i = 0; iterator.hasNext(); i++){
            if(t.equals(iterator.next())){
                return i + firstIndex;
            }
        }
        return null;
    }
}
public TaskValue<Integer>.Observable sizeFromProvider(){
    return provider.size();
}
public TaskValue<T>.Observable getFromProvider(int index){
    return provider.get(index);
}
public TaskValue<Integer>.Observable indexOfFromProvider(T t){
    synchronized(me()){
        List<T> dataList = getDataList();
        if(dataList != null){
            Iterator<T> iterator = dataList.iterator();
            for(int i = 0; iterator.hasNext(); i++){
                if(t.equals(iterator.next())){
                    TaskValue<Integer> task = new TaskValue<>();
                    task.notifyComplete(i + firstIndex);
                    return task.getObservable();
                }
            }
        }
        return provider.indexOf(t);
    }
}

@Override
public void remove(){

DebugException.start().notImplemented().end();

}
@Override
public void set(T t){

DebugException.start().notImplemented().end();

}
@Override
public void add(T t){

DebugException.start().notImplemented().end();

}
@Override
public int nextIndex(){

DebugException.start().notImplemented().end();

    return -1;
}
@Override
public int previousIndex(){

DebugException.start().notImplemented().end();

    return -1;
}

public void notifyInsert(int index, T t){
    synchronized(me()){
        if(isLoaded(index)){
            getDataList().add(index - firstIndex, t);
            lastIndex += 1;
        }
        else if(currentIndex != null){
            if(index < firstIndex){
                firstIndex += 1;
                lastIndex += 1;
            }
        }
        else {
            firstIndex = index;
            lastIndex = firstIndex;
            currentIndex = lastIndex;
            setDataList(newDataList());
            getDataList().add(0, t);
        }
    }
}
public void notifyUpdate(int index, T t){
    synchronized(me()){
        if(isLoadedNoSync(index)){
            getDataList().remove(index - firstIndex);
            getDataList().add(index - firstIndex, t);
        }
    }
}
public void notifyUpdateAll(){
    setNextIndexNoSync(null);
}
public void notifyRemove(T t){
    synchronized(me()){
        List<T> dataList = getDataList();
        if(dataList != null){
            Iterator<T> iterator = dataList.iterator();
            for(int i = 0; iterator.hasNext(); i++){
                if(iterator.next().equals(t)){
                    notifyRemove(i + firstIndex);
                    return;
                }
            }
        }
    }
}
public void notifyRemove(int index){
    synchronized(me()){
        if(isLoadedNoSync(index)){
            getDataList().remove(index - firstIndex);
            if(index == lastIndex){
                currentIndex -= 1;
            }
            lastIndex -= 1;
            if(lastIndex < firstIndex){
                firstIndex = null;
                currentIndex = null;
                lastIndex = null;
                setDataList(null);
            }
        }
        else if(currentIndex != null){
            if(index < firstIndex){
                firstIndex -= 1;
                lastIndex -= 1;
            }
        }
    }
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

private abstract class RunnableFuture extends com.tezov.lib_java.type.runnable.RunnableFuture<Void>{
    public RunnableFuture(Object owner){
        super(owner);
    }
    @Override
    public void done(){
        super.done();
        futureUpdater = null;
    }

}

}
