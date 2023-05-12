/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.misc;

import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.debug.DebugException;
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
import static com.tezov.lib_java_android.ui.misc.TouchDetectorSwipe.Action.SWIPE_DOWN;
import static com.tezov.lib_java_android.ui.misc.TouchDetectorSwipe.Action.SWIPE_LEFT;
import static com.tezov.lib_java_android.ui.misc.TouchDetectorSwipe.Action.SWIPE_RIGHT;
import static com.tezov.lib_java_android.ui.misc.TouchDetectorSwipe.Action.SWIPE_UP;
import static com.tezov.lib_java_android.ui.misc.TouchEvent.Action.CANCEL;
import static com.tezov.lib_java_android.ui.misc.TouchEvent.Action.DOWN;
import static com.tezov.lib_java_android.ui.misc.TouchEvent.Action.UP;

import android.content.Context;
import android.view.MotionEvent;

import androidx.core.view.GestureDetectorCompat;

import com.tezov.lib_java_android.application.AppDisplay;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.type.primaire.Point;

public class TouchDetectorSwipe extends TouchEvent implements android.view.GestureDetector.OnGestureListener{
public static final int SWIPE_THRESHOLD_DISTANCE_X = AppDisplay.convertDpToPx(48);
public static final int SWIPE_THRESHOLD_VELOCITY_X = 100;
public static final int SWIPE_THRESHOLD_DISTANCE_Y = AppDisplay.convertDpToPx(48);
public static final int SWIPE_THRESHOLD_VELOCITY_Y = 100;
private final GestureDetectorCompat gestureDetector;

public TouchDetectorSwipe(Context context){
    gestureDetector = new GestureDetectorCompat(context, this);
    gestureDetector.setIsLongpressEnabled(false);
}

public boolean onTouchEvent(MotionEvent event){
    gestureDetector.onTouchEvent(event);
    return super.onTouchEvent(event);
}

@Override
final public boolean onDown(MotionEvent e){
    return false;
}
@Override
final public void onShowPress(MotionEvent e){

}
@Override
final public boolean onSingleTapUp(MotionEvent e){
    TouchInfo touchInfo = findTouchInfo(0);
    if(touchInfo == null){
        return false;
    }
    touchInfo.update(TouchDetectorMove.Action.CLICK_SHORT);
    return onClick(touchInfo);
}
@Override
final public void onLongPress(MotionEvent e){

}

public int swipedThresholdDistanceX(){
    return SWIPE_THRESHOLD_DISTANCE_X;
}
public int swipedThresholdDistanceY(){
    return SWIPE_THRESHOLD_DISTANCE_Y;
}
public int swipedThresholdVelocityX(){
    return SWIPE_THRESHOLD_VELOCITY_X;
}
public int swipedThresholdVelocityY(){
    return SWIPE_THRESHOLD_VELOCITY_Y;
}

@Override
final public boolean onTouch(TouchInfo touchInfo){
    if((touchInfo == null) || (touchInfo.getId() != 0)){
        return false;
    }
    if(touchInfo.getAction() == DOWN){
        return onDown(touchInfo);
    }
    if(touchInfo.getAction() == UP){
        return onUp(touchInfo);
    }
    if(touchInfo.getAction() == CANCEL){
        return onCancel(touchInfo);
    }
    return false;
}
@Override
final public boolean onScroll(MotionEvent downEvent, MotionEvent moveEvent, float distanceX, float distanceY){
    return false;
}
@Override
final public boolean onFling(MotionEvent downEvent, MotionEvent moveEvent, float velocityX, float velocityY){
    TouchInfo touchInfo = findTouchInfo(0);
    if(touchInfo == null){
        return false;
    }
    Point diff = touchInfo.diffFromOrigin();
    if((Math.abs(diff.getX()) > Math.abs(diff.getY()))){
        if((Math.abs(diff.getX()) > swipedThresholdDistanceX()) && (Math.abs(velocityX) > swipedThresholdVelocityX())){
            if(diff.getX() > 0){
DebugLog.start().send(this, "POINTER:0 SWIPE_RIGHT").end();
                touchInfo.update(SWIPE_RIGHT);
                return onSwipe(touchInfo);
            } else if(diff.getX() < 0){
DebugLog.start().send(this, "POINTER:0 SWIPE_LEFT").end();
                touchInfo.update(SWIPE_LEFT);
                return onSwipe(touchInfo);
            }
        }
    } else {
        if((Math.abs(diff.getY()) > swipedThresholdDistanceY()) && (Math.abs(velocityY) > swipedThresholdVelocityY())){
            if(diff.getY() > 0){
DebugLog.start().send(this, "POINTER:0 SWIPE_DOWN").end();
                touchInfo.update(SWIPE_DOWN);
                return onSwipe(touchInfo);
            } else if(diff.getY() < 0){
DebugLog.start().send(this, "POINTER:0 SWIPE_UP").end();
                touchInfo.update(SWIPE_UP);
                return onSwipe(touchInfo);
            }
        }
    }
    return onSwipe(touchInfo);
}

protected boolean onClick(TouchInfo touchInfo){
    return false;
}
protected boolean onDown(TouchInfo touchInfo){
    return false;
}
protected boolean onSwipe(TouchInfo touchInfo){
    return false;
}
protected boolean onUp(TouchInfo touchInfo){
    return false;
}
protected boolean onCancel(TouchInfo touchInfo){
    return false;
}

public interface Action extends TouchEvent.Action{
    Is SWIPE_LEFT = new Is("SWIPE_LEFT");
    Is SWIPE_RIGHT = new Is("SWIPE_RIGHT");
    Is SWIPE_DOWN = new Is("SWIPE_DOWN");
    Is SWIPE_UP = new Is("SWIPE_UP");
    Is CLICK_SHORT = new Is("CLICK_SHORT");

}

}
