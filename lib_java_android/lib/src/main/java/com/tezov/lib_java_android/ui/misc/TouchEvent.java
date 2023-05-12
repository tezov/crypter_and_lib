/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.misc;

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
import static com.tezov.lib_java_android.ui.misc.TouchEvent.Action.CANCEL;
import static com.tezov.lib_java_android.ui.misc.TouchEvent.Action.MOVE;
import static com.tezov.lib_java_android.ui.misc.TouchEvent.Action.UP;

import android.graphics.RectF;
import android.view.MotionEvent;

import androidx.annotation.Nullable;

import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.wrapperAnonymous.FunctionW;
import com.tezov.lib_java.type.collection.ListKey;
import com.tezov.lib_java.type.defEnum.EnumBase;
import com.tezov.lib_java.type.primaire.Point;

import java.util.LinkedList;

public class TouchEvent{
private final ListKey<Integer, TouchInfo> events;

public TouchEvent(){
DebugTrack.start().create(this).end();
    events = new ListKey<Integer, TouchInfo>(LinkedList::new, new FunctionW<TouchInfo, Integer>(){
        @Override
        public Integer apply(TouchInfo details){
            return details.id;
        }
    });
}

public boolean onTouchEvent(MotionEvent event){
    int action = event.getActionMasked();
    switch(action){
        case (MotionEvent.ACTION_DOWN):
        case (MotionEvent.ACTION_POINTER_DOWN):{
            int index = event.getActionIndex();
DebugLog.start().send(this, "POINTER:" + event.getPointerId(index) + " ACTION_DOWN").end();
            TouchInfo touchInfo = newTouchInfo(index, event);
            return onTouch(touchInfo);
        }
        case (MotionEvent.ACTION_MOVE):{
            boolean intercept = false;
            for(int i = 0; i < event.getPointerCount(); i++){
DebugLog.start().send(this, "POINTER:" + event.getPointerId(i) + " ACTION_MOVE").end();
                TouchInfo touchInfo = updateTouchInfo(i, MOVE, event);
                intercept |= onTouch(touchInfo);
            }
            return intercept;
        }
        case (MotionEvent.ACTION_UP):
        case (MotionEvent.ACTION_POINTER_UP):{
            int index = event.getActionIndex();
DebugLog.start().send(this, "POINTER:" + event.getPointerId(index) + " ACTION_UP").end();
            updateTouchInfo(index, UP, event);
            TouchInfo touchInfo = removeTouchInfo(index, event);
            return onTouch(touchInfo);

        }
        case (MotionEvent.ACTION_SCROLL):{
DebugLog.start().send(this, "POINTER:" + event.getPointerId(event.getActionIndex()) + " ACTION_SCROLL").end();
        }
        break;
        case (MotionEvent.ACTION_CANCEL):{
            boolean intercept = false;
            for(int i = 0; i < event.getPointerCount(); i++){
DebugLog.start().send(this, "POINTER:" + event.getPointerId(i) + " ACTION_CANCEL").end();
                updateTouchInfo(i, CANCEL, event);
                TouchInfo touchInfo = removeTouchInfo(i, event);
                intercept |= onTouch(touchInfo);
            }
            return intercept;
        }
        case (MotionEvent.ACTION_OUTSIDE):{
DebugLog.start().send(this, "POINTER:" + event.getPointerId(event.getActionIndex()) + " ACTION_OUTSIDE").end();
        }
        break;
        case (MotionEvent.ACTION_BUTTON_PRESS):{
DebugLog.start().send(this, "POINTER:" + event.getPointerId(event.getActionIndex()) + " ACTION_BUTTON_PRESS").end();
        }
        break;
        case (MotionEvent.ACTION_BUTTON_RELEASE):{
DebugLog.start().send(this, "POINTER:" + event.getPointerId(event.getActionIndex()) + " ACTION_BUTTON_RELEASE").end();
        }
        break;
        case (MotionEvent.ACTION_HOVER_ENTER):{
DebugLog.start().send(this, "POINTER:" + event.getPointerId(event.getActionIndex()) + " ACTION_HOVER_ENTER").end();
        }
        break;
        case (MotionEvent.ACTION_HOVER_EXIT):{
DebugLog.start().send(this, "POINTER:" + event.getPointerId(event.getActionIndex()) + " ACTION_HOVER_EXIT").end();
        }
        break;
        case (MotionEvent.ACTION_HOVER_MOVE):{
DebugLog.start().send(this, "POINTER:" + event.getPointerId(event.getActionIndex()) + " ACTION_HOVER_MOVE").end();
        }
        break;
        default:{
DebugLog.start().send(this, "POINTER:" + event.getPointerId(event.getActionIndex()) + " UNKNOWN ACTION " + action).end();
        }
    }
    return false;
}
protected boolean onTouch(TouchInfo touchInfo){
    return false;
}
protected TouchInfo findTouchInfo(int id){
    return events.getValue(id);
}
protected TouchInfo newTouchInfo(int index, MotionEvent event){
    TouchInfo touchInfo = new TouchInfo(index, Action.DOWN, event);
    boolean replaced = events.put(touchInfo);
    if(replaced){
DebugException.start().log("Pointer(" + touchInfo.id + ") already exist, has been overwritten").end();
    }
    return touchInfo;
}
protected TouchInfo updateTouchInfo(int index, Action.Is action, MotionEvent event){
    int id = event.getPointerId(index);
    TouchInfo details = events.getValue(id);
    if(details == null){
        return null;
    } else {
        return details.update(index, action, event);
    }
}
protected TouchInfo removeTouchInfo(int index, MotionEvent event){
    return events.removeKey(event.getPointerId(index));
}
public void cancelAll(){
    events.clear();
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

public interface Action{
    Is DOWN = new Is("DOWN");
    Is UP = new Is("UP");
    Is MOVE = new Is("MOVE");
    Is CANCEL = new Is("CANCEL");

    class Is extends EnumBase.Is{
        public Is(String name){
            super(name);
        }

    }

}

public static class TouchInfo{
    private final Point positionOrigin;
    private final int id;
    private Action.Is action;
    private Point positionPrevious;
    private Point positionLast;
    public TouchInfo(int index, Action.Is action, MotionEvent event){
DebugTrack.start().create(this).end();
        this.id = event.getPointerId(index);
        this.action = action;
        this.positionOrigin = new Point(event.getX(index), event.getY(index));
        this.positionPrevious = positionOrigin;
        this.positionLast = positionOrigin;
    }
    protected TouchInfo update(int index, Action.Is action, MotionEvent event){
        this.action = action;
        positionPrevious = positionLast;
        positionLast = new Point(event.getX(index), event.getY(index));
        return this;
    }
    protected void update(Action.Is action){
        this.action = action;
    }
    public int getId(){
        return id;
    }
    public Action.Is getAction(){
        return action;
    }
    public Point getPositionOrigin(){
        return positionOrigin;
    }
    public Point getPositionLast(){
        return positionLast;
    }
    public Point diffFromOrigin(){
        return positionLast.minus(positionOrigin);
    }
    public double distanceFromOrigin(){
        return positionLast.distance(positionOrigin);
    }
    public Point diffFromPrevious(){
        return positionLast.minus(positionPrevious);
    }
    public double distanceFromPrevious(){
        return positionLast.distance(positionPrevious);
    }
    public boolean isInside(RectF mask){
        return positionLast.isInside(mask);
    }
    @Override
    public boolean equals(@Nullable Object obj){
        if(obj instanceof Integer){
            return obj.equals(id);
        } else if(obj instanceof TouchInfo){
            return id == ((TouchInfo)obj).id;
        } else {
            return super.equals(obj);
        }
    }
    public DebugString toDebugString(){
        DebugString data = new DebugString();
        data.append("action", action);
        data.append("pointerId", id);
        data.append("positionOrigin", positionOrigin);
        data.append("positionPrevious", positionPrevious);
        data.append("positionLast", positionLast);
        data.append("diffFromOrigin", diffFromOrigin());
        data.append("distanceFromOrigin", distanceFromOrigin());
        data.append("diffFromPrevious", diffFromPrevious());
        data.append("distanceFromPrevious", distanceFromPrevious());
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

}

}
