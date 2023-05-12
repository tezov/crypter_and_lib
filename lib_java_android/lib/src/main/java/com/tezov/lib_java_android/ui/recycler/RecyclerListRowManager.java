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
import static com.tezov.lib_java_android.ui.recycler.RecyclerListRowBinder.ViewType;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.definition.defDeletable;
import com.tezov.lib_java.type.ref.Ref;
import com.tezov.lib_java.type.ref.WR;
import com.tezov.lib_java.type.runnable.RunnableW;
import com.tezov.lib_java_android.toolbox.PostToHandler;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public abstract class RecyclerListRowManager<TYPE> extends RecyclerList.Adapter<RecyclerListRowHolder<TYPE>> implements defDeletable<TYPE>{
private final List<RecyclerListRowHolder<TYPE>> rowHolders;
private final RecyclerListDataManager<TYPE> dataManager;
private final LinkedHashMap<ViewType.Is, RecyclerListRowBinder<? extends RecyclerList.ViewHolder, TYPE>> viewBinders;
private WR<RecyclerView> recyclerViewWR;

public RecyclerListRowManager(RecyclerListDataManager<TYPE> dataManager){
DebugTrack.start().create(this).end();
    this.dataManager = dataManager;
    dataManager.attach(this);
    viewBinders = new LinkedHashMap<>();
    rowHolders = new ArrayList<>();
}

public <RC extends RecyclerView> RC getRecyclerView(){
    return (RC)Ref.get(recyclerViewWR);
}

public <RC extends RecyclerList> RC getRecyclerList(){
    return (RC)Ref.get(recyclerViewWR);
}

public <R extends RecyclerListDataManager<TYPE>> R getDataManager(){
    return (R)dataManager;
}

public RecyclerListRowManager add(RecyclerListRowBinder<? extends RecyclerList.ViewHolder, TYPE> viewBinder){
    viewBinders.put(viewBinder.getViewType(), viewBinder);
    return this;
}

public RecyclerListRowManager remove(RecyclerListRowBinder<? extends RecyclerList.ViewHolder, TYPE> viewBinder){
    viewBinders.remove(viewBinder.getViewType());
    return this;
}

public <R extends RecyclerListRowBinder<? extends RecyclerListRowHolder<TYPE>, TYPE>> R getRowBinder(int itemViewType){
    return getRowBinder(ViewType.find(itemViewType));
}

public <R extends RecyclerListRowBinder<? extends RecyclerListRowHolder<TYPE>, TYPE>> R getRowBinder(ViewType.Is viewType){
    RecyclerListRowBinder<? extends RecyclerListRowHolder<TYPE>, TYPE> rowBinder = viewBinders.get(viewType);
    if(rowBinder == null){
DebugException.start().explode("viewType " + viewType.name() + " do not exit in RowManager").end();
    }
    return (R)rowBinder;
}

public boolean isAttachedToRecyclerView(){
    return (getRecyclerView() != null);
}

public boolean isAttachedToWindow(){
    RecyclerList recyclerList = getRecyclerView();
    if(recyclerList != null){
        return recyclerList.isAttachedToWindow();
    } else {
        return false;
    }
}

@Override
public void onViewAttachedToWindow(@NonNull RecyclerListRowHolder<TYPE> newHolder){
    super.onViewAttachedToWindow(newHolder);
    for(RecyclerListRowHolder holder: rowHolders){
        if(holder.getLayoutPosition() == newHolder.getLayoutPosition()){
            boolean found = rowHolders.remove(holder);

DebugLog.start().send(this, "detached " + holder.getLayoutPosition() + " " + found).end();

            break;
        }
    }
    newHolder.validateViewType();
    rowHolders.add(newHolder);

DebugLog.start().send(this, "attached " + newHolder.getLayoutPosition()).end();

}

@Override
public void onViewDetachedFromWindow(@NonNull RecyclerListRowHolder<TYPE> holder){
    boolean found = rowHolders.remove(holder);

DebugLog.start().send(this, "detached " + holder.getLayoutPosition() + " " + found).end();

    super.onViewDetachedFromWindow(holder);
}

public boolean postToHandler(RunnableW runnable){
    return PostToHandler.of(getRecyclerView(), runnable);
}

public Integer getItemViewTypeIfValid(int position){
    for(RecyclerListRowHolder holder: rowHolders){
        if(holder.getLayoutPosition() == position){
            if(holder.isViewTypeInvalid()){
                break;
            }
            return holder.getItemViewType();
        }
    }
    return null;
}

private void viewTypeInvalidate(int position){
    for(RecyclerListRowHolder h: rowHolders){
        if(h.getLayoutPosition() == position){
            h.viewTypeInvalidate();
            break;
        }
    }
}

private void viewTypeInvalidate(){
    for(RecyclerListRowHolder h: rowHolders){
        h.viewTypeInvalidate();
    }
}

public void postUpdatedAll(boolean animate){
    postToHandler(new RunnableW(){
        @Override
        public void runSafe(){
            notifyUpdatedAll(animate);
        }
    });
}

public void notifyUpdatedAll(boolean animate){
    if(animate){
        RecyclerList recycler = getRecyclerView();
        if(recycler != null){
            recycler.scheduleLayoutAnimation();
        }
    }
    viewTypeInvalidate();
    notifyDataSetChanged();
}

public void postUpdate(int position){
    if(postToHandler(new RunnableW(){
        @Override
        public void runSafe(){
            notifyUpdate(position);
        }
    })){
DebugLog.start().send(this, "***** postUpdate " + position).end();
    }
}

public void notifyUpdate(int position){
    viewTypeInvalidate(position);
    notifyItemChanged(position);
}

public void postInserted(int position){
    if(postToHandler(new RunnableW(){
        @Override
        public void runSafe(){
            notifyInserted(position);
        }
    })){

DebugLog.start().send(this, "***** postInserted " + position).end();

    }
}

public void notifyInserted(int position){
    viewTypeInvalidate(position);
    notifyItemInserted(position);
}

public void postRemoved(int position){
    if(postToHandler(new RunnableW(){
        @Override
        public void runSafe(){
            notifyRemoved(position);
        }
    })){

DebugLog.start().send(this, "***** postRemoved " + position).end();

    }
}

public void notifyRemoved(int position){
    viewTypeInvalidate(position);
    notifyItemRemoved(position);
}

@Override
public void onAttachedToRecyclerView(RecyclerView recyclerView){
    super.onAttachedToRecyclerView(recyclerView);
    this.recyclerViewWR = WR.newInstance(recyclerView);
    getDataManager().onAttachedToRecyclerView();
}

@Override
public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView){
    super.onDetachedFromRecyclerView(recyclerView);
    this.recyclerViewWR = null;
    getDataManager().onDetachedFromRecyclerView();
}

@Override
final public RecyclerListRowHolder<TYPE> onCreateViewHolder(ViewGroup parent, int viewType){
    RecyclerListRowBinder<? extends RecyclerView.ViewHolder, TYPE> viewBinder = getRowBinder(viewType);
    return viewBinder.create(parent);
}

@Override
public void onBindViewHolder(RecyclerListRowHolder<TYPE> holder, int position){
    TYPE data = getDataManager().get(position);
    int viewType = holder.getItemViewType();
    RecyclerListRowBinder<RecyclerListRowHolder<TYPE>, TYPE> binder = getRowBinder(viewType);
    binder.bind(holder, data);
}

@Override
final public int getItemCount(){
    return getDataManager().size();
}

@Override
public TYPE putToTrash(int position){
    return getDataManager().putToTrash(position);
}

@Override
public TYPE restoreFromTrash(int position, TYPE data){
    return getDataManager().restoreFromTrash(position, data);
}

@Override
public TYPE remove(int position){
    return getDataManager().remove(position);
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

}
