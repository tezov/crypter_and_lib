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
import java.util.List;
import java.util.Set;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import androidx.fragment.app.Fragment;

import static com.tezov.lib_java_android.ui.misc.TouchDetectorMove.Action;
import static com.tezov.lib_java_android.ui.misc.TouchDetectorMove.Action.MOVE_DOWN;
import static com.tezov.lib_java_android.ui.misc.TouchDetectorMove.Action.MOVE_LEFT;
import static com.tezov.lib_java_android.ui.misc.TouchDetectorMove.Action.MOVE_RIGHT;
import static com.tezov.lib_java_android.ui.misc.TouchDetectorMove.Action.MOVE_UP;
import static com.tezov.lib_java_android.ui.misc.TouchEvent.TouchInfo;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tezov.lib_java_android.application.AppContext;
import com.tezov.lib_java.async.notifier.Notifier;
import com.tezov.lib_java.async.notifier.observable.ObservableEvent;
import com.tezov.lib_java.async.notifier.observer.event.ObserverEvent;
import com.tezov.lib_java.toolbox.Compare;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.wrapperAnonymous.FunctionW;
import com.tezov.lib_java_android.wrapperAnonymous.RecyclerOnItemTouchListenerW;
import com.tezov.lib_java.type.collection.ListKey;
import com.tezov.lib_java.type.defEnum.Event;
import com.tezov.lib_java.type.primaire.Point;
import com.tezov.lib_java.type.ref.WR;
import com.tezov.lib_java_android.ui.misc.TouchDetectorMove;
import com.tezov.lib_java_android.ui.misc.TouchEvent;

import java.util.LinkedList;

public abstract class RecyclerListSwipper extends RecyclerView.ItemDecoration implements RecyclerView.OnChildAttachStateChangeListener{
private final static float ACC_FACTOR_WEIGHT = 1.3f;
private final static float VIEW_MODAL_CLOSED_EPSILON = 2.0f;
private final ListKey<Integer, HolderState> holderStates;
private final Notifier<Event.Is> notifier;
private WR<RecyclerList> recyclerListWR;
private TouchDetectorMove touchDetector = null;
private RecyclerOnItemTouchListenerW touchListener = null;
private Boolean touchOwner = null;
private RecyclerListRowHolder activeHolder = null;
private RecyclerListRowHolder currentHolder = null;
private Processor processor;

public RecyclerListSwipper(){
DebugTrack.start().create(this).end();
    notifier = new Notifier<>(new ObservableEvent<Event.Is, GestureInfo>(), false);
    holderStates = new ListKey<Integer, HolderState>(LinkedList::new, new FunctionW<HolderState, Integer>(){
        @Override
        public Integer apply(HolderState holderState){
            return holderState.holder.itemView.hashCode();
        }
    });
}
protected RecyclerListSwipper me(){
    return this;
}
public <TYPE> Notifier.Subscription observe(ObserverEvent<Event.Is, GestureInfo<TYPE>> observer){
    return notifier.register(observer);
}
public void unObserve(Object owner){
    notifier.unregister(owner);
}
public void unObserveAll(){
    notifier.unregisterAll();
}
public void post(Event.Is event, GestureInfo gestureInfo){
    ObservableEvent<Event.Is, GestureInfo>.Access access = notifier.obtainAccess(this, event);
    access.setValue(gestureInfo);
}
protected abstract Integer modalViewLayoutID(Action.Is direction, RecyclerListRowHolder holder);
protected int modalViewMeasureSpec(Action.Is direction){
    return processor.modalViewMeasureSpec(direction);
}
protected float swipeThresholdDistanceRatio(Action.Is direction){
    return 0.5f;
}
protected Animation animation(Action.Is direction){
    return Animation.SCALE;
}
private HolderState findHolderState(RecyclerListRowHolder holder){
    return holderStates.getValue(holder.itemView.hashCode());
}
private HolderState putHolderState(RecyclerListRowHolder holder){
    HolderState holderState = new HolderState(holder);
    boolean replaced = holderStates.put(holderState);
    if(replaced){
DebugException.start().log("holderState was existing for this view").end();
    }
    return holderState;
}
private void removeHolderState(View view){
    HolderState holderState = holderStates.removeKey(view.hashCode());
    if(holderState != null){
        holderState.clean();
        if((currentHolder != null) && (currentHolder.itemView == view)){
            currentHolder = null;
        }
    }
}
public void attach(RecyclerList recycler){
    if(touchListener != null){
        if(recycler != getRecyclerList()){
DebugException.start().explode("try attach to a different com.tezov.lib.ui.recycler not null").end();
        } else {
DebugException.start().log("itemTouchListener not null").end();
        }
        return;
    }
    this.recyclerListWR = WR.newInstance(recycler);
    touchDetector = new TouchDetectorMove(AppContext.get()){
        @Override
        protected boolean onClick(TouchInfo touchInfo){
            return performModalClick(touchInfo);
        }
        @Override
        protected boolean onDown(TouchInfo touchInfo){
            touchOwner = null;
            activeHolder = null;
            return false;
        }
        @Override
        protected boolean onMove(TouchInfo touchInfo){
            if((touchOwner == null) && processor.unhandledMovement(touchInfo)){
                touchOwner = false;
                return false;
            }
            if(Compare.isFalse(touchOwner)){
                return false;
            }
            if((touchOwner == null) && processor.handledMovement(touchInfo)){
                if(activeHolder == null){
                    activeHolder = findSelectedHolder(touchInfo);
                    if(activeHolder != null){
                        onActiveHolderSelected(activeHolder);
                        touchOwner = true;
                    } else {
                        touchOwner = false;
                    }
                }
                return touchOwner;
            }
            if(!Compare.isTrue(touchOwner)){
                return false;
            }
            HolderState holderState = findHolderState(activeHolder);
            holderState.update(touchInfo.getPositionLast());
            getRecyclerList().postInvalidate();
            return true;
        }
        @Override
        protected boolean onUp(TouchInfo touchInfo){
            boolean touchOwnerMem = Compare.isTrue(touchOwner);
            touchOwner = null;
            if(activeHolder != null){
                swipeToBoundary();
                activeHolder = null;
            }
            return touchOwnerMem;
        }
        @Override
        protected boolean onCancel(TouchInfo touchInfo){
            touchOwner = false;
            if(activeHolder != null){
                swipeToBoundary();
                activeHolder = null;
            }
            return false;
        }
    };
    touchListener = new RecyclerOnItemTouchListenerW(){
        @Override
        public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e){
            return touchDetector.onTouchEvent(e);
        }
        @Override
        public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e){
            touchDetector.onTouchEvent(e);
        }
        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept){
            if(disallowIntercept){
                touchDetector.cancelAll();
            }
        }
    };
    recycler.addOnItemTouchListener(touchListener);
    recycler.addItemDecoration(this);
    recycler.addOnChildAttachStateChangeListener(this);
    int orientation = ((LinearLayoutManager)getRecyclerList().getLayoutManager()).getOrientation();
    if(orientation == RecyclerList.VERTICAL){
        processor = new ProcessorVertical();
    } else if(orientation == RecyclerList.HORIZONTAL){
        processor = new ProcessorHorizontal();
    } else {
DebugException.start().unknown("orientation", orientation).end();
    }
}
public void detach(RecyclerList recycler){
    if(recycler != getRecyclerList()){
        return;
    }
    recycler.removeOnItemTouchListener(touchListener);
    recycler.removeItemDecoration(this);
    recycler.removeOnChildAttachStateChangeListener(this);
    unObserveAll();
    for(HolderState holderState: holderStates){
        holderState.clean();
    }
    holderStates.clear();
    currentHolder = null;
    activeHolder = null;
    touchOwner = null;
    touchDetector = null;
    touchListener = null;
    processor = null;
}
private boolean performModalClick(TouchEvent.TouchInfo details){
    if(currentHolder == null){
        return false;
    }
    HolderState holderState = findHolderState(currentHolder);
    if(holderState.isClosed()){
        return false;
    }
    View modalChildView = holderState.findModalChildView(details);
    if(modalChildView == null){
        return false;
    } else {
        post(Event.ON_CLICK_SHORT, new GestureInfo(currentHolder, findHolderState(currentHolder).direction, modalChildView.getId()));
        close();
        return true;
    }
}
public RecyclerList getRecyclerList(){
    return recyclerListWR.get();
}
public RecyclerListRowManager getRowManager(){
    return getRecyclerList().getRowManager();
}
private RecyclerListRowHolder findSelectedHolder(TouchEvent.TouchInfo details){
    RecyclerList recyclerList = getRecyclerList();
    Point origin = details.getPositionOrigin();
    for(int end = recyclerList.getChildCount(), i = 0; i < end; i++){
        View view = recyclerList.getChildAt(i);
        if((origin.getX() > view.getLeft()) && (origin.getX() < view.getRight()) && (origin.getY() > view.getTop()) && (origin.getY() < view.getBottom())){
            return recyclerList.findRowHolder(view);
        }
    }
    return null;
}
private HolderState onActiveHolderSelected(RecyclerListRowHolder holder){
    HolderState holderState = findHolderState(holder);
    if(holderState == null){
        holderState = putHolderState(holder);
    } else {
        holderState.previousPosition = null;
    }
    currentHolder = holder;
    return holderState;
}
@Override
public void onChildViewAttachedToWindow(@NonNull View view){

}
@Override
public void onChildViewDetachedFromWindow(@NonNull View view){
    removeHolderState(view);
}
@Override
public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state){
    for(HolderState holderState: holderStates){
        if(!holderState.holder.equals(currentHolder)){
            holderState.close();
            holderStates.remove(holderState);
        } else {
            holderState.draw(c);
        }
    }
}
public void swipeToBoundary(){
    if(currentHolder == null){
        return;
    }
    HolderState holderState = findHolderState(currentHolder);
    if(!holderState.swipeToBoundary()){
        currentHolder = null;
    } else {
        post(Event.OPENED, new GestureInfo(currentHolder, holderState.direction, null));
    }
    getRecyclerList().postInvalidate();
}
public void swipe(View itemView, float ratio, Action.Is direction){
    swipe(getRecyclerList().findRowHolder(itemView), ratio, direction);
}
public void swipe(int holderIndex, float ratio, Action.Is direction){
    swipe(getRecyclerList().findRowHolderInLayout(holderIndex), ratio, direction);
}
public void swipe(RecyclerListRowHolder holder, float ratio, Action.Is direction){
    if(holder != null){
        HolderState holderState = onActiveHolderSelected(holder);
        holderState.swipe(ratio, direction);
        getRecyclerList().postInvalidate();
    }
}
public void close(){
    if(currentHolder != null){
        currentHolder = null;
        getRecyclerList().postInvalidate();
    }
}
@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

public enum Animation{
    SCALE, SLIDE_OUT, SLIDE_ABOVE
}

private interface Processor{
    int getOrientation();

    int modalViewMeasureSpec(Action.Is direction);

    boolean unhandledMovement(TouchEvent.TouchInfo details);

    boolean handledMovement(TouchEvent.TouchInfo details);

    void modalViewMeasure(ViewGroup modalView, Action.Is direction);

    void modalViewScale(ViewGroup modalView, View itemView);

    float position(Point p);

    int getSize(View itemView);

    int getMaxTranslate(ViewGroup modalView);

    Action.Is directionStart();

    Action.Is directionEnd();

    Animation animation(Action.Is direction);

    void setTranslate(View itemView, float value);

    float getTranslate(View itemView);

    void canvasTranslate(Canvas c, View itemView, float value);

    void canvasScale(Canvas c, ViewGroup modalView, View itemView, float abs, boolean scale);

    View findModalChildView(Point p, HolderState holderState);

    default View findModalChildView(ViewGroup modalView, float x, float y){
        Rect childBoundaries = new Rect();
        for(int end = modalView.getChildCount(), i = 0; i < end; i++){
            View child = modalView.getChildAt(i);
            child.getGlobalVisibleRect(childBoundaries);
            if(childBoundaries.contains((int)x, (int)y)){
                return child;
            }
        }
        return null;
    }

}

public static class GestureInfo<TYPE>{
    private final RecyclerListRowHolder<TYPE> holder;
    private final Action.Is direction;
    private final Integer modalViewId;

    public GestureInfo(RecyclerListRowHolder<TYPE> holder, Action.Is direction, Integer modalViewId){
DebugTrack.start().create(this).end();
        this.holder = holder;
        this.direction = direction;
        this.modalViewId = modalViewId;
    }

    public RecyclerListRowHolder<TYPE> getHolder(){
        return holder;
    }

    public Action.Is getDirection(){
        return direction;
    }

    public Integer getModalViewId(){
        return modalViewId;
    }

    @Override
    protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
        super.finalize();
    }

}

private class ProcessorVertical implements Processor{
    @Override
    public int getOrientation(){
        return RecyclerList.VERTICAL;
    }
    @Override
    public int modalViewMeasureSpec(Action.Is direction){
        return View.MeasureSpec.AT_MOST;
    }
    @Override
    public boolean unhandledMovement(TouchEvent.TouchInfo details){
        return (details.getAction() == MOVE_UP) || (details.getAction() == MOVE_DOWN);
    }
    @Override
    public boolean handledMovement(TouchEvent.TouchInfo details){
        return (details.getAction() == MOVE_RIGHT) || (details.getAction() == MOVE_LEFT);
    }
    @Override
    public void modalViewMeasure(ViewGroup modalView, Action.Is direction){
        modalView.measure(View.MeasureSpec.makeMeasureSpec(getSize(getRecyclerList()), me().modalViewMeasureSpec(direction)), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
    }
    @Override
    public void modalViewScale(ViewGroup modalView, View itemView){
        modalView.setScaleY(((float)itemView.getHeight()) / ((float)modalView.getMeasuredHeight()));
    }
    @Override
    public float position(Point p){
        return p.getX();
    }
    @Override
    public int getSize(View itemView){
        return itemView.getWidth();
    }
    @Override
    public int getMaxTranslate(ViewGroup modalView){
        return getSize(modalView);
    }
    @Override
    public Action.Is directionStart(){
        return MOVE_RIGHT;
    }
    @Override
    public Action.Is directionEnd(){
        return MOVE_LEFT;
    }
    @Override
    public Animation animation(Action.Is direction){
        return Animation.SLIDE_ABOVE;
    }
    @Override
    public void setTranslate(View itemView, float value){
        itemView.setTranslationX(value);
    }
    @Override
    public float getTranslate(View itemView){
        return itemView.getTranslationX();
    }

    @Override
    public void canvasTranslate(Canvas c, View itemView, float value){
        c.translate(value, itemView.getTop());
    }
    @Override
    public void canvasScale(Canvas c, ViewGroup modalView, View itemView, float totalTranslated, boolean scale){
        float scaleFactor = 1.0f;
        if(scale){
            scaleFactor = (totalTranslated + (itemView.getLeft() * 2)) / getMaxTranslate(modalView);
        }
        c.scale(scaleFactor, modalView.getScaleY());
    }
    @Override
    public View findModalChildView(Point p, HolderState holderState){
        ViewGroup modalView = null;
        View itemView = holderState.holder.itemView;
        Action.Is direction = holderState.direction;
        float x = p.getX();
        if(direction == processor.directionStart()){
            modalView = holderState.modalViewStart;
        } else if(direction == directionEnd()){
            x -= (itemView.getRight() + itemView.getTranslationX());
            modalView = holderState.modalViewEnd;
        }
        float scaleX = Math.abs(holderState.totalTranslated) / modalView.getWidth();
        x /= scaleX;
        float y = p.getY() - itemView.getTop();
        y /= modalView.getScaleY();
        return findModalChildView(modalView, x, y);
    }

}

private class ProcessorHorizontal implements Processor{
    @Override
    public int getOrientation(){
        return RecyclerList.HORIZONTAL;
    }
    @Override
    public int modalViewMeasureSpec(Action.Is direction){
        return View.MeasureSpec.EXACTLY;
    }
    @Override
    public boolean unhandledMovement(TouchEvent.TouchInfo details){
        return (details.getAction() == MOVE_RIGHT) || (details.getAction() == MOVE_LEFT);
    }
    @Override
    public boolean handledMovement(TouchEvent.TouchInfo details){
        return (details.getAction() == MOVE_UP) || (details.getAction() == MOVE_DOWN);
    }
    @Override
    public void modalViewMeasure(ViewGroup modalView, Action.Is direction){
        modalView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(getSize(getRecyclerList()), me().modalViewMeasureSpec(direction)));
    }
    @Override
    public void modalViewScale(ViewGroup modalView, View itemView){
        modalView.setScaleX(((float)itemView.getWidth()) / ((float)modalView.getMeasuredWidth()));
    }
    @Override
    public Animation animation(Action.Is direction){
        return Animation.SCALE;
    }
    @Override
    public float position(Point p){
        return p.getY();
    }
    @Override
    public int getSize(View itemView){
        return itemView.getHeight();
    }
    @Override
    public int getMaxTranslate(ViewGroup modalView){
        return getSize(modalView);
    }
    @Override
    public Action.Is directionStart(){
        return MOVE_DOWN;
    }
    @Override
    public Action.Is directionEnd(){
        return MOVE_UP;
    }
    @Override
    public void setTranslate(View itemView, float value){
        itemView.setTranslationY(value);
    }
    @Override
    public float getTranslate(View itemView){
        return itemView.getTranslationY();
    }
    @Override
    public void canvasTranslate(Canvas c, View itemView, float value){
        c.translate(itemView.getLeft(), value);
    }
    @Override
    public void canvasScale(Canvas c, ViewGroup modalView, View itemView, float totalTranslated, boolean scale){
        int margin = itemView.getTop() - getRecyclerList().getTop();
        float scaleFactor = (totalTranslated + (margin * 2)) / getMaxTranslate(modalView);
        c.scale(modalView.getScaleX(), scaleFactor);
    }
    @Override
    public View findModalChildView(Point p, HolderState holderState){
        ViewGroup modalView = null;
        View itemView = holderState.holder.itemView;
        Action.Is direction = holderState.direction;
        float y = p.getY();
        if(direction == processor.directionEnd()){
            y -= (itemView.getBottom() + itemView.getTranslationY());
            modalView = holderState.modalViewEnd;
        } else if(direction == processor.directionStart()){
            modalView = holderState.modalViewStart;
        }

        float scaleY = Math.abs(holderState.totalTranslated) / modalView.getHeight();
        y /= scaleY;
        float x = p.getX() - itemView.getLeft();
        x /= modalView.getScaleX();
        return findModalChildView(modalView, x, y);
    }

}

private class HolderState{
    Action.Is direction;
    Animation animation = null;
    float accFactor;
    Float previousPosition;
    float totalTranslated;
    RecyclerListRowHolder holder;
    ViewGroup modalViewStart;
    ViewGroup modalViewEnd;
    HolderState(RecyclerListRowHolder holder){
DebugTrack.start().create(this).end();
        this.holder = holder;
        reset();
        modalViewStart = measureModalView(processor.directionStart());
        modalViewEnd = measureModalView(processor.directionEnd());
    }
    ViewGroup measureModalView(Action.Is direction){
        Integer layout = modalViewLayoutID(direction, holder);
        if(layout != null){
            ViewGroup modalView = (ViewGroup)LayoutInflater.from(AppContext.getActivity()).inflate(layout, getRecyclerList(), false);
            processor.modalViewMeasure(modalView, direction);
            modalView.layout(0, 0, modalView.getMeasuredWidth(), modalView.getMeasuredHeight());
            processor.modalViewScale(modalView, holder.itemView);
            return modalView;
        } else {
            return null;
        }
    }
    protected void update(Point p){
        float currentPosition = processor.position(p);
        if(this.previousPosition == null){
            this.previousPosition = currentPosition;
        }
        float diff = (currentPosition - this.previousPosition) * accFactor;
        this.previousPosition = currentPosition;
        totalTranslated += diff;
        if(totalTranslated > VIEW_MODAL_CLOSED_EPSILON){
            if(modalViewStart != null){
                direction = processor.directionStart();
                animation = me().animation(direction);
                int maxTranslate = processor.getMaxTranslate(modalViewStart);
                if(totalTranslated > maxTranslate){
                    totalTranslated = maxTranslate;
                }
                accFactor = 1.0f + (totalTranslated / maxTranslate) * ACC_FACTOR_WEIGHT;
            } else {
                direction = null;
                animation = null;
                accFactor = 1.0f;
                totalTranslated = 0.0f;
            }
        } else if(totalTranslated < -VIEW_MODAL_CLOSED_EPSILON){
            if(modalViewEnd != null){
                direction = processor.directionEnd();
                animation = me().animation(direction);
                int maxTranslate = processor.getMaxTranslate(modalViewEnd);
                if(-totalTranslated > maxTranslate){
                    totalTranslated = -maxTranslate;
                }
                accFactor = 1.0f - (totalTranslated / maxTranslate) * ACC_FACTOR_WEIGHT;
            } else {
                direction = null;
                animation = null;
                accFactor = 1.0f;
                totalTranslated = 0.0f;
            }
        } else {
            direction = null;
            animation = null;
            accFactor = 1.0f;
        }
    }
    protected void reset(){
        direction = null;
        accFactor = 1.0f;
        previousPosition = null;
        totalTranslated = 0.0f;
    }

    protected void clean(){
        processor.setTranslate(holder.itemView, 0.0f);
    }

    boolean isClosed(){
        return Math.abs(processor.getTranslate(holder.itemView)) <= VIEW_MODAL_CLOSED_EPSILON;
    }

    protected void draw(Canvas c){
        if(direction == processor.directionStart()){
            switch(animation){
                case SCALE:
                    draw(c, modalViewStart, 0.0f, true);
                    break;
                case SLIDE_OUT:
                    draw(c, modalViewStart, -processor.getSize(holder.itemView) + totalTranslated, false);
                    break;
                case SLIDE_ABOVE:
                    draw(c, modalViewStart, 0, false);
                    break;
            }
        } else if(direction == processor.directionEnd()){
            switch(animation){
                case SCALE:
                    draw(c, modalViewEnd, processor.getSize(holder.itemView) + totalTranslated, true);
                    break;
                case SLIDE_OUT:
                    draw(c, modalViewEnd, processor.getSize(holder.itemView) + totalTranslated, false);
                    break;
                case SLIDE_ABOVE:
                    draw(c, modalViewEnd, 0, false);
                    break;
            }
        } else {
            close();
        }
    }

    protected void draw(Canvas c, ViewGroup modalView, float translateOffset, boolean scale){
        c.save();
        processor.canvasTranslate(c, holder.itemView, translateOffset);
        processor.canvasScale(c, modalView, holder.itemView, Math.abs(totalTranslated), scale);
        modalView.draw(c);
        c.restore();
        processor.setTranslate(holder.itemView, totalTranslated);
    }

    void close(){
        if(totalTranslated != 0.0f){
            reset();
        }
        clean();
    }

    View findModalChildView(TouchInfo details){
        return processor.findModalChildView(details.getPositionLast(), this);
    }

    void swipe(float position, com.tezov.lib_java_android.ui.misc.TouchEvent.Action.Is direction){
        if((position < 0.0f) || (position > 1.0f)){
            return;
        }
        if((direction == processor.directionStart()) && (modalViewStart != null)){
            this.direction = direction;
            this.previousPosition = null;
            this.totalTranslated = processor.getMaxTranslate(modalViewStart) * position;
        } else if((direction == processor.directionEnd()) && (modalViewEnd != null)){
            this.direction = direction;
            this.previousPosition = null;
            this.totalTranslated = -processor.getMaxTranslate(modalViewEnd) * position;
        }
    }
    boolean swipeToBoundary(){
        boolean swiped = false;
        if((direction == processor.directionStart()) && ((totalTranslated / processor.getMaxTranslate(modalViewStart)) > swipeThresholdDistanceRatio(direction))){
            swipe(1.0f, direction);
            swiped = true;
        } else if((direction == processor.directionEnd()) && ((-totalTranslated / processor.getMaxTranslate(modalViewEnd)) > swipeThresholdDistanceRatio(direction))){
            swipe(1.0f, direction);
            swiped = true;
        }
        return swiped;
    }

    public DebugString toDebugString(){
        DebugString sb = new DebugString();
        sb.append("direction", direction);
        sb.append("accFactor", accFactor);
        sb.append("dxLastMoved", previousPosition);
        sb.append("xMovedTotal", totalTranslated);
        return sb;
    }
    final public void toDebugLog(){
DebugLog.start().send(toDebugString()).end();
    }

    @Override
    public boolean equals(@Nullable Object obj){
        if(obj instanceof Integer){
            return obj.equals(holder.itemView.hashCode());
        } else {
            return super.equals(obj);
        }
    }

    @Override
    protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
        super.finalize();
    }

}

}
