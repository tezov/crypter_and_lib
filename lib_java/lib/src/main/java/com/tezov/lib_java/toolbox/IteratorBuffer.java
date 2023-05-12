/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.toolbox;

import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.util.UtilsString;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java.async.IteratorBufferAsync;
import com.tezov.lib_java.async.notifier.observer.state.ObserverStateE;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.definition.defProvider;
import com.tezov.lib_java.wrapperAnonymous.PredicateW;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

//TODO low update with check direction on get (see kotin async version
//TODO only 1 thread update instead of future with new thread each time

public class IteratorBuffer<T> implements ListIterator<T>{
private RunnableFuture futureUpdater = null;
private boolean updateTailInProgress = false;
private boolean updateHeadInProgress = false;
private defProvider<T> provider;
private List<T> dataList;
private Integer currentIndex = null;
private Integer lastIndex = null;
private Integer firstIndex = null;
private int triggerSizeBeforeDownload = 5;
private int sizeToDownload = 20;

public IteratorBuffer(){
    this(null, null);
}
public IteratorBuffer(defProvider<T> provider){
    this(provider, null);
}
public IteratorBuffer(defProvider<T> provider, Integer startIndex){
DebugTrack.start().create(this).end();
    this.provider = provider;
    if(provider != null){
        setNextIndex(startIndex);
    }
}

public IteratorBuffer<T> setTriggerSizeBeforeDownload(Integer size){
    synchronized(this){
        triggerSizeBeforeDownload = size;
        if(currentIndex != null){
            updateHeadAndTailNoSync();
        }
    }
    return this;
}
public IteratorBuffer<T> setSizeToDownload(Integer size){
    synchronized(this){
        sizeToDownload = size;
    }
    return this;
}
public IteratorBuffer<T> setProvider(defProvider<T> provider){
    return setProvider(provider, null);
}
public IteratorBuffer<T> setProvider(defProvider<T> provider, Integer startIndex){
    synchronized(this){
        this.provider = provider;
        setNextIndex(startIndex);
    }
    return this;
}
public defProvider<T> getProvider(){
    synchronized(this){
        return provider;
    }
}

private void updateHeadAndTailNoSync(){
    updateTailInProgress = true;
    updateTailNoSync();
    updateTailInProgress = false;
    updateHeadInProgress = true;
    updateHeadNoSync();
    updateHeadInProgress = false;
}

private IteratorBuffer<T> setNextIndexNoSync(Integer index){
    if(index == null){
        firstIndex = provider.getFirstIndex();
        if(firstIndex != null){
            lastIndex = firstIndex - 1;
            currentIndex = lastIndex;
            setDataList(newDataList());
            updateHeadAndTailNoSync();
        }
        else {
            lastIndex = null;
            currentIndex = null;
            setDataList(null);
        }
    } else {
        int size = provider.size();
        if(index >= size){
DebugException.start().log(new IndexOutOfBoundsException("index:" + index + " size:" + size)).end();
            return this;
        }
        firstIndex = index;
        lastIndex = firstIndex - 1;
        currentIndex = lastIndex;
        setDataList(newDataList());
        updateHeadAndTailNoSync();
    }
    return this;
}

public IteratorBuffer<T> resetIndex(){
    synchronized(this){
        return setNextIndexNoSync(null);
    }
}

public IteratorBuffer<T> setNextIndex(Integer index){
    synchronized(this){
        if(isLoadedNoSync(index)){
            currentIndex = index;
            updateHeadAndTailNoSync();
        } else {
            setNextIndexNoSync(index);
        }
        return this;
    }
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
    setDataList(getDataList().subList(sizeToRemove, getDataList().size()));
DebugLog.start().send(this, "reduce head from " + firstIndex + " to " + (firstIndex + sizeToRemove)).end();
    firstIndex += sizeToRemove;
}

private void updateTailNoSync(){
    if((lastIndex - currentIndex) > triggerSizeBeforeDownload){
        return;
    }
    List<T> newDatas = provider.select(lastIndex + 1, sizeToDownload);
    if((newDatas == null) || (newDatas.size() <= 0)){
        return;
    }
DebugLog.start().send(this, "increase tail from " + (lastIndex + 1) + " to " + (lastIndex + newDatas.size())).end();
    lastIndex += newDatas.size();
    getDataList().addAll(newDatas);
    reduceHeadNoSync();
}

@Override
public boolean hasNext(){
    synchronized(this){
        if(currentIndex == null){
            update(new RunnableFuture(this){
                @Override
                public void runSafe(){
                    setNextIndexNoSync(null);
                    done();
                }
            }, true);
        } else if(currentIndex >= lastIndex){
            if(updateTailInProgress && futureUpdater.isAlive()){
                futureUpdater.join();
            } else {
                update(new RunnableFuture(this){
                    @Override
                    public void runSafe(){
                        updateTailNoSync();
                        done();
                    }
                }, true);
            }
        }
        else if(!updateTailInProgress){
            updateTailInProgress = true;
            update(new RunnableFuture(this){
                @Override
                public void runSafe(){
                    updateTailNoSync();
                    updateTailInProgress = false;
                    done();
                }
            }, false);
        }
        return (currentIndex != null) && (currentIndex < lastIndex);
    }
}

@Override
public T next(){
    synchronized(this){
        currentIndex++;
        return getDataList().get(currentIndex - firstIndex);
    }
}

private void reduceTailNoSync(){
    if((lastIndex - currentIndex) <= sizeToDownload){
        return;
    }
    Integer sizeToRemove = (lastIndex - currentIndex) - sizeToDownload - 1;
    setDataList(getDataList().subList(0, getDataList().size() - sizeToRemove));
DebugLog.start().send(this, "reduce tail from " + lastIndex + " to " + (lastIndex - sizeToRemove)).end();
    lastIndex -= sizeToRemove;
}

private void updateHeadNoSync(){
    if((currentIndex - firstIndex) >= triggerSizeBeforeDownload){
        return;
    }
    List<T> newDatas = provider.select(firstIndex - sizeToDownload, sizeToDownload);
    if((newDatas == null) || (newDatas.size() <= 0)){
        return;
    }
DebugLog.start().send(this, "increase head from " + (firstIndex - 1) + " to " + (firstIndex - newDatas.size())).end();
    firstIndex -= newDatas.size();
    newDatas.addAll(getDataList());
    setDataList(newDatas);
    reduceTailNoSync();
}

@Override
public boolean hasPrevious(){
    synchronized(this){
        if(currentIndex == null){
            update(new RunnableFuture(this){
                @Override
                public void runSafe(){
                    setNextIndexNoSync(null);
                    done();
                }
            }, true);
        } else
            if(firstIndex >= currentIndex){
                if(updateHeadInProgress && futureUpdater.isAlive()){
                    futureUpdater.join();
                } else {
                    update(new RunnableFuture(this){
                        @Override
                        public void runSafe(){
                            updateHeadNoSync();
                            done();
                        }
                    }, true);
                }
            }
            else if(!updateHeadInProgress){
                updateHeadInProgress = true;
                update(new RunnableFuture(this){
                    @Override
                    public void runSafe(){
                        updateHeadNoSync();
                        updateHeadInProgress = false;
                        done();
                    }
                }, false);
            }
        return (currentIndex != null) && (firstIndex <= currentIndex);
    }
}

@Override
public T previous(){
    synchronized(this){
        T t = getDataList().get(currentIndex - firstIndex);
        currentIndex--;
        return t;
    }
}

private boolean isLoadedNoSync(Integer index){
    return (currentIndex != null) && ((index >= firstIndex) && (index <= lastIndex));
}

public boolean isLoaded(Integer index){
    synchronized(this){
        return isLoadedNoSync(index);
    }
}

public T get(int index){
    synchronized(this){
        if(isLoadedNoSync(index)){
            currentIndex = index;
            updateHeadAndTailNoSync();
        }
        else {
            setNextIndexNoSync(index);
            currentIndex = index;
        }
        return getDataList().get(currentIndex - firstIndex);
    }
}

public T getFromBuffer(PredicateW<T> predicate){
    synchronized(this){
        List<T> dataList = getDataList();
        if(dataList != null){
            for(T t: dataList){
                if(predicate.test(t)){
                    return t;
                }
            }
        }
        return null;
    }
}
public Integer indexOfFromBuffer(PredicateW<T> predicate){
    synchronized(this){
        List<T> dataList = getDataList();
        if(dataList != null){
            Iterator<T> iterator = dataList.iterator();
            for(int i = 0; iterator.hasNext(); i++){
                if(predicate.test(iterator.next())){
                    return i + firstIndex;
                }
            }
        }
        return null;
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
    synchronized(this){
        if(isLoadedNoSync(index)){
            getDataList().add(index - firstIndex, t);
            lastIndex += 1;
        } else if(currentIndex != null){
            if(index < firstIndex){
                firstIndex += 1;
                lastIndex += 1;
            }
        }
        else{
            firstIndex = index;
            lastIndex = firstIndex;
            currentIndex = lastIndex;
            setDataList(newDataList());
            getDataList().add(0, t);
        }
    }
}
public void notifyUpdate(int index, T t){
    synchronized(this){
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
    synchronized(this){
        List<T> dataList = getDataList();
        if(dataList != null){
            Iterator<T> iterator = dataList.iterator();
            for(int i = 0; iterator.hasNext(); i++){
                if(iterator.next().equals(t)){
                    notifyRemove(i);
                    return;
                }
            }
        }
    }
}
public void notifyRemove(int index){
    synchronized(this){
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

public DebugString toDebugString(){
    DebugString data = new DebugString();
    data.append("currentIndex", currentIndex);
    data.append("lastIndex", lastIndex);
    data.append("firstIndex", firstIndex);
    data.append("triggerSizeBeforeDownload", triggerSizeBeforeDownload);
    data.append("sizeToDownload", sizeToDownload);
    return data;
}

final public void toDebugLog(){
DebugLog.start().send(toDebugString()).end();
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
