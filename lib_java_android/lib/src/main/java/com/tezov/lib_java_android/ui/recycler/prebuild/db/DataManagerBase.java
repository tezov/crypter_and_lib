/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.recycler.prebuild.db;

import com.tezov.lib_java.debug.DebugTrack;
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
import com.tezov.lib_java.async.notifier.observer.value.ObserverValue;
import com.tezov.lib_java_android.database.ItemBase;
import com.tezov.lib_java_android.database.sqlLite.dbView;
import com.tezov.lib_java_android.database.sqlLite.dbView.SRwNOC;
import com.tezov.lib_java.generator.uid.defHasUid;
import com.tezov.lib_java.generator.uid.defUid;
import com.tezov.lib_java.toolbox.IteratorBuffer;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.wrapperAnonymous.PredicateW;
import com.tezov.lib_java.type.defEnum.Event;
import com.tezov.lib_java.type.runnable.RunnableW;
import com.tezov.lib_java_android.ui.recycler.RecyclerList;
import com.tezov.lib_java_android.ui.recycler.RecyclerListDataManager;
import com.tezov.lib_java_android.ui.recycler.RecyclerListRowHolder;
import com.tezov.lib_java_android.ui.recycler.RecyclerListRowManager;

import java.util.List;

public abstract class DataManagerBase<ITEM extends ItemBase<ITEM>, DATA extends defHasUid> extends RecyclerListDataManager<DATA>{
private dbView<ITEM> view;
private IteratorBuffer<ITEM> viewIterator = null;
private int size = 0;

public DataManagerBase(Class<DATA> type, dbView<ITEM> view){
    super(type);
    setView(view);
}

public DataManagerBase(){
    super(null);
}

protected abstract DATA newDbItem(ITEM item);

@Override
protected void onAttachedToRecyclerView(){
    super.onAttachedToRecyclerView();
    if(viewIterator == null){
        resetViewIterator();
    }
}

@Override
public void reset(){
    //IMPROVE cancel pending notification
    RunnableW runnable = new RunnableW(){
        @Override
        public void runSafe(){
            getView().enableNotification(false);
            resetViewIterator();
            RecyclerListRowManager rowManager = getRowManager();
            if(rowManager != null){
                rowManager.notifyUpdatedAll(false);
            }
            getView().enableNotification(true);
DebugLog.start().send(me(), "*** Removed all, size " + size).end();
        }
    };
    if(!postToHandler(runnable)){
        runnable.run();
    }
}

public dbView<ITEM> getView(){
    return view;
}
public void setView(dbView<ITEM> view){
    this.view = view;
    if(view != null){
        view.observeOnChanged(new ObserverValue<SRwNOC.Value<ITEM>>(this){
            @Override
            public void onComplete(SRwNOC.Value<ITEM> size){
                onSizeChanged(size);
            }
        });
    }
}
protected void onSizeChanged(SRwNOC.Value<ITEM> size){
    int sizeDiff = size.current - size.previous;
    if(size.itemsChanged == null){
        if(size.current == 0){
            dataRemovedAll();
        } else if(sizeDiff != 0){

DebugException.start().log(" previous and current size are different but  items changed is null").end();

            reset();
        }
    } else if((sizeDiff != 0) && (Math.abs(sizeDiff) != size.itemsChanged.size())){

DebugException.start().logHidden("diff and items.size are different").end();

        reset();
    } else if(sizeDiff == 0){
        dataUpdated(size.itemsChanged);
    } else if(sizeDiff > 0){
        dataInserted(size.itemsChanged);
    } else {
        dataRemoved(size.itemsChanged);
    }
    size.itemsChanged = null;
}
public IteratorBuffer<ITEM> getViewIterator(){
    return viewIterator;
}

public void resetViewIterator(){
    viewIterator = getView().iterator();
    size = viewIterator.getProvider().size();
}

@Override
public int size(){
    return size;
}

private void dataInserted(List<ITEM> items){
    RunnableW runnable = new RunnableW(){
        @Override
        public void runSafe(){
            for(ITEM item: items){
                Integer index = findIndexInBuffer(item);
                if(index == null){
                    index = getView().indexOf(item);
                }
                if(index != null){
                    size += 1;
DebugLog.start().send(me(), item.getUid().toHexString() + " inserted index " + index + " size " + size).end();
                    viewIterator.notifyInsert(index, item);
                    getRowManager().notifyInserted(index);
                    onDataInserted(index, item);
                }
            }
        }
    };
    if(isRecyclerViewAttachedToWindow()){
        postToHandler(runnable);
    } else {
        if(viewIterator != null){
            runnable.run();
        }
    }
}

private void dataUpdated(List<ITEM> items){
    RunnableW runnable = new RunnableW(){
        @Override
        public void runSafe(){
            for(ITEM item: items){
                Integer index = findIndexInBuffer(item);
                if(index == null){
                    index = getView().indexOf(item);
                }
                //NOW if index in buffer != index in view --> update and moved
                if(index != null){
DebugLog.start().send(me(), item.getUid().toHexString() + " updated index " + index + " size " + size).end();
                    viewIterator.notifyUpdate(index, item); //NOW must notify moved
                    getRowManager().notifyUpdate(index);
                    onDataUpdated(index, item);
                }
            }
        }
    };
    if(isRecyclerViewAttachedToWindow()){
        postToHandler(runnable);
    } else {
        if(viewIterator != null){
            runnable.run();
        }
    }

}

private void dataRemoved(List<ITEM> items){
    RunnableW runnable = new RunnableW(){
        @Override
        public void runSafe(){
            for(ITEM item: items){
                Integer index = findIndex(item);
                size -= 1;
DebugLog.start().send(me(), item.getUid().toHexString() + " removed index " + index + " size " + size).end();
                if(index != null){
                    viewIterator.notifyRemove(index);
                    getRowManager().notifyRemoved(index);
                    onDataRemoved(index, item);
                }
            }
        }
    };
    if(isRecyclerViewAttachedToWindow()){
        postToHandler(runnable);
    } else if(viewIterator != null){
        runnable.run();
    }
}

private Integer findIndex(ITEM item){
    if(isAttachedToRecyclerView()){
        Integer index = findIndexInAdapter(item);
        if(index != null){
            return index;
        }
    }
    return findIndexInBuffer(item);
}

private Integer findIndexInAdapter(ITEM item){
    RecyclerList recyclerList = getRowManager().getRecyclerView();
    for(int childCount = recyclerList.getChildCount(), i = 0; i < childCount; ++i){
        RecyclerListRowHolder<DATA> holder = (RecyclerListRowHolder<DATA>)recyclerList.getChildViewHolder(recyclerList.getChildAt(i));
        defUid uidFetched = holder.get().getUid();
        if(uidFetched.equals(item.getUid())){
            Integer index = holder.getAbsoluteAdapterPosition();
            if(index == -1){
                index = null;
            }
            return index;
        }
    }
    return null;
}

private Integer findIndexInBuffer(ITEM item){
    if(viewIterator != null){
        return viewIterator.indexOfFromBuffer(new PredicateW<ITEM>(){
            @Override
            public boolean test(ITEM itemFetched){
                return itemFetched.getUid().equals(item.getUid());
            }
        });
    } else {
        return null;
    }
}

private void dataRemovedAll(){
    reset();
}

protected void onDataInserted(int position, ITEM item){
    post(Event.ON_INSERT, newDbItem(item));
}

protected void onDataUpdated(int position, ITEM item){
    post(Event.ON_UPDATE, newDbItem(item));
}

protected void onDataRemoved(int position, ITEM item){
    post(Event.ON_REMOVE, newDbItem(item));
}

@Override
public void destroy(){
    if(view != null){
        view.enableNotification(false);
        view = null;
    }
    viewIterator = null;
}

}
