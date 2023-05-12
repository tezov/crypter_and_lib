/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.recycler;

import com.tezov.lib_java.debug.DebugLog;
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

import android.content.Context;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.GridLayoutAnimationController;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.tezov.lib_java.async.notifier.Notifier;
import com.tezov.lib_java.async.notifier.observable.ObservableEvent;
import com.tezov.lib_java.async.notifier.observer.event.ObserverEvent;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.defEnum.EnumBase;

import static com.tezov.lib_java_android.ui.recycler.RecyclerList.PositionSnap.BOTTOM;
import static com.tezov.lib_java_android.ui.recycler.RecyclerList.PositionSnap.TOP;

public abstract class RecyclerList extends RecyclerView{
private State state = null;

private Notifier<Event.Is> notifier = null;
private RecyclerListSmoothScroller smoothScroller = null;

public RecyclerList(Context context){
    super(context);
    init();
}

public RecyclerList(Context context, AttributeSet attrs){
    super(context, attrs);
    init();
}

public RecyclerList(Context context, AttributeSet attrs, int defStyle){
    super(context, attrs, defStyle);
    init();
}

protected <S extends State> S newState(){
    return (S)new State();
}

public boolean hasState(){
    return getState() != null;
}

private void makeState(Param param){
    state = newState();
    state.attach(this);
    if(param != null){
        if(!state.hasParam()){
            state.setParam(param);
        } else {
DebugException.start().explode("Step has already Param").end();
        }

    }
}

public <S extends State> S obtainState(){
    if(!hasState()){
        makeState(null);
    }
    return getState();
}

public <S extends State> S getState(){
    return (S)state;
}

final public void restoreState(State state){
    this.state = state;
    state.attach(this);
    if(hasParam()){
        Param param = state.getParam();
        super.setAdapter(param.adapter);
        RecyclerListItemAnimator animator = param.itemAnimator;
        super.setItemAnimator(animator);
        if(animator != null){
            animator.attach(this);
        }
        RecyclerListSwipper itemTouchSwipper = param.getItemTouchSwipper();
        if(itemTouchSwipper != null){
            itemTouchSwipper.attach(this);
        }
    }
    if(state.layoutSaveInstance != null){
        LayoutManager layout = getLayoutManager();
        if(layout != null){
            layout.onRestoreInstanceState(state.layoutSaveInstance);
        }
        state.layoutSaveInstance = null;
    }
}

protected boolean hasParam(){
    return hasState() && state.hasParam();
}

protected <P extends Param> P obtainParam(){
    return (P)obtainState().obtainParam();
}

protected void setParam(Param param){
    if(hasState()){
        getState().setParam(param);
    } else {
        makeState(param);
    }
}

private void init(){
DebugTrack.start().create(this).end();
    ObservableEvent<Event.Is, Object> observable = new ObservableEvent<>();
    notifier = new Notifier<>(observable, false);
}

@Override
protected Parcelable onSaveInstanceState(){
    State state = obtainState();
    LayoutManager layout = getLayoutManager();
    if(layout != null){
        state.layoutSaveInstance = layout.onSaveInstanceState();
    }
    return super.onSaveInstanceState();
}

public Notifier.Subscription observe(ObserverEvent<Event.Is, Object> observer){
    return notifier.register(observer);
}

public void unObserve(Object owner){
    notifier.unregister(owner);
}

public void post(Event.Is event, Object object){
    ObservableEvent<Event.Is, Object>.Access access = notifier.obtainAccess(this, event);
    access.setValue(object);
}

public void postIfDifferent(Event.Is event, Object object){
    ObservableEvent<Event.Is, Object>.Access access = notifier.obtainAccess(this, event);
    access.setValueIfDifferent(object);
}

@Override
public void setAdapter(Adapter adapter){

DebugException.start().explode("Use setRowManager instead").end();

}

public <R extends RecyclerListRowManager> R getRowManager(){
    return (R)super.getAdapter();
}

public void setRowManager(RecyclerListRowManager adapter){
    obtainParam().adapter = adapter;
    super.setAdapter(adapter);
}

public <D extends RecyclerListDataManager> D getDataManager(){
    RecyclerListRowManager rowManager = getRowManager();
    if(rowManager == null){
        return null;
    } else {
        return (D)rowManager.getDataManager();
    }
}

public <R extends RecyclerListSwipper> R getItemTouchSwipper(){
    return state.getParam().getItemTouchSwipper();
}

public RecyclerList setItemTouchSwipper(RecyclerListSwipper itemTouchSwipper){
    obtainParam().itemTouchSwipper = itemTouchSwipper;
    itemTouchSwipper.attach(this);
    return this;
}

public <I extends ItemAnimator> I getAnimator(){
    return state.getParam().getItemAnimator();
}

public void setAnimator(RecyclerListItemAnimator animator){
    super.setItemAnimator(animator);
    obtainParam().itemAnimator = animator;
    animator.attach(this);
}

public RecyclerList enableCenterSnap(){
    LinearSnapHelper snapHelper = new LinearSnapHelper();
    snapHelper.attachToRecyclerView(this);
    return this;
}

public <R extends RecyclerListRowHolder> R findRowHolderInAdapter(int position){
    return (R)findViewHolderForAdapterPosition(position);
}

public <R extends RecyclerListRowHolder> R findRowHolderInLayout(int position){
    return (R)findViewHolderForLayoutPosition(position);
}

public <R extends RecyclerListRowHolder> R findRowHolder(View view){
    return (R)findContainingViewHolder(view);
}

private RecyclerListSmoothScroller newSmoothScroller(){
    return new RecyclerListSmoothScroller(getContext());
}

public void scrollToStart(){
    scrollToPosition(TOP, 0);
}
public void scrollToEnd(){
    scrollToPosition(BOTTOM, getDataManager().size()-1);
}
public void scrollToPosition(PositionSnap.Is snapPosition, int position){
    if((smoothScroller != null) && smoothScroller.isRunning()){
        stopScroll();
        smoothScroller.stopScroll();
    }
    smoothScroller = newSmoothScroller();
    smoothScroller.scrollToPosition(this, snapPosition, position);
}

public abstract boolean isTop();
public abstract boolean isBottom();
public abstract int getPosition(PositionSnap.Is snapPosition);
public abstract int[] getPositions(PositionSnap.Is snapPosition);
public abstract void scrollToPositionWithOffset(int position, int offset);
@Override
public abstract void setEnabled(boolean flag);
@Override
public abstract boolean isEnabled();

@Override
protected void attachLayoutAnimationParameters(View child, ViewGroup.LayoutParams params, int index, int count){
    LayoutManager layoutManager = super.getLayoutManager();
    if((getAdapter() != null) && (layoutManager instanceof GridLayoutManager)){

        GridLayoutAnimationController.AnimationParameters animationParams = (GridLayoutAnimationController.AnimationParameters)params.layoutAnimationParameters;

        if(animationParams == null){
            animationParams = new GridLayoutAnimationController.AnimationParameters();
            params.layoutAnimationParameters = animationParams;
        }

        animationParams.count = count;
        animationParams.index = index;

        int columns = ((GridLayoutManager)layoutManager).getSpanCount();
        animationParams.columnsCount = columns;
        animationParams.rowsCount = count / columns;

        int invertedIndex = count - 1 - index;
        animationParams.column = columns - 1 - (invertedIndex % columns);
        animationParams.row = animationParams.rowsCount - 1 - invertedIndex / columns;

    } else {
        super.attachLayoutAnimationParameters(child, params, index, count);
    }
}

@Override
protected void onDetachedFromWindow(){
    notifier.unregisterAll();
    RecyclerListRowManager rowManager = getRowManager();
    if(rowManager != null){
        RecyclerListDataManager dataManager = rowManager.getDataManager();
        if(dataManager != null){
            dataManager.unObserveAll();
        }
    }
    if(state.getParam().itemAnimator != null){
        state.getParam().itemAnimator.unObserveAll();
    }
    if(state.getParam().itemTouchSwipper != null){
        state.getParam().itemTouchSwipper.detach(this);
    }
    super.setItemAnimator(null);
    super.setAdapter(null);
    super.onDetachedFromWindow();
    if(hasState()){
        state.detach(this);
    }
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

public interface Event{
    Is ON_STOP_SCROLL = new Is("ON_STOP_SCROLL");
    class Is extends EnumBase.Is{
        public Is(String name){
            super(name);
        }
    }
}

public interface PositionSnap{
    Is TOP = new Is("TOP");
    Is TOP_COMPLETELY_VISIBLE = new Is("TOP_COMPLETELY_VISIBLE");
    Is BOTTOM = new Is("BOTTOM");
    Is BOTTOM_COMPLETELY_VISIBLE = new Is("BOTTOM_COMPLETELY_VISIBLE");
    Is CENTER = new Is("CENTER");
    class Is extends EnumBase.Is{
        public Is(String name){
            super(name);
        }

        public Is(String name, int ordinal){
            super(name, ordinal);
        }

    }
}

public interface SmoothScroller{
    void scrollToPosition(RecyclerView recycler, PositionSnap.Is snapPosition, int position);

    void stopScroll();

}

public static class State extends com.tezov.lib_java_android.ui.state.State<Param, Method>{
    Parcelable layoutSaveInstance = null;

    @Override
    public Param getParam(){
        return super.getParam();
    }

    @Override
    protected Param newParam(){
        return new Param();
    }

    @Override
    protected Method newMethod(){
        return new Method();
    }

    @Override
    public DebugString toDebugString(){
        DebugString data = super.toDebugString();
        data.appendCheckIfNull("layoutSaveInstance", layoutSaveInstance);
        return data;
    }

}

public static class Param extends com.tezov.lib_java_android.ui.state.Param{
    private RecyclerListRowManager adapter = null;
    private RecyclerListItemAnimator itemAnimator = null;
    private RecyclerListSwipper itemTouchSwipper = null;

    @Override
    public Method obtainMethod(){
        return super.obtainMethod();
    }

    public RecyclerListRowManager getRowManager(){
        return adapter;
    }

    public void setRowManager(RecyclerListRowManager adapter){
        if(hasOwner()){
            obtainMethod().setRowManager(adapter);
        } else {
            this.adapter = adapter;
        }
    }

    public <I extends ItemAnimator> I getItemAnimator(){
        return (I)itemAnimator;
    }

    public void setItemAnimator(RecyclerListItemAnimator itemAnimator){
        if(hasOwner()){
            obtainMethod().setItemAnimator(itemAnimator);
        } else {
            this.itemAnimator = itemAnimator;
        }
    }

    public <R extends RecyclerListSwipper> R getItemTouchSwipper(){
        return (R)itemTouchSwipper;
    }

    public void setItemTouchSwipper(RecyclerListSwipper itemTouchSwipper){
        if(hasOwner()){
            obtainMethod().setItemTouchSwipper(itemTouchSwipper);
        } else {
            this.itemTouchSwipper = itemTouchSwipper;
        }
    }

    @Override
    public DebugString toDebugString(){
        DebugString data = super.toDebugString();
        data.appendFullSimpleNameWithHashcode("adapter", adapter);
        data.appendFullSimpleNameWithHashcode("itemAnimator", itemAnimator);
        data.appendFullSimpleNameWithHashcode("itemTouchSwipper", itemTouchSwipper);
        return data;
    }

}

protected static class Method extends com.tezov.lib_java_android.ui.state.Method{
    @Override
    public RecyclerList getOwner(){
        return super.getOwner();
    }

    void setRowManager(RecyclerListRowManager adapter){
        getOwner().setRowManager(adapter);
    }

    void setItemAnimator(RecyclerListItemAnimator animator){
        getOwner().setAnimator(animator);
    }

    void setItemTouchSwipper(RecyclerListSwipper itemTouchSwipper){
        getOwner().setItemTouchSwipper(itemTouchSwipper);
    }

}

}

