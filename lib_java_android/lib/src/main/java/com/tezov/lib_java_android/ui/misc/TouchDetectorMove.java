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
import static com.tezov.lib_java_android.ui.misc.TouchDetectorMove.Action.CLICK_SHORT;
import static com.tezov.lib_java_android.ui.misc.TouchDetectorMove.Action.MOVE_DOWN;
import static com.tezov.lib_java_android.ui.misc.TouchDetectorMove.Action.MOVE_LEFT;
import static com.tezov.lib_java_android.ui.misc.TouchDetectorMove.Action.MOVE_RIGHT;
import static com.tezov.lib_java_android.ui.misc.TouchDetectorMove.Action.MOVE_UP;
import static com.tezov.lib_java_android.ui.misc.TouchEvent.Action.CANCEL;
import static com.tezov.lib_java_android.ui.misc.TouchEvent.Action.DOWN;
import static com.tezov.lib_java_android.ui.misc.TouchEvent.Action.MOVE;
import static com.tezov.lib_java_android.ui.misc.TouchEvent.Action.UP;

import android.content.Context;

import com.tezov.lib_java_android.application.AppDisplay;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.type.primaire.Point;

public class TouchDetectorMove extends TouchEvent{
public static final int MOVE_THRESHOLD_DISTANCE_X = AppDisplay.convertDpToPx(24);
public static final int MOVE_THRESHOLD_DISTANCE_Y = AppDisplay.convertDpToPx(24);
public static final int CLICK_THRESHOLD_DISTANCE_MAX = AppDisplay.convertDpToPx(12);

private boolean clickDisabled = false;
public TouchDetectorMove(Context context){


}

public int moveThresholdDistanceX(){
    return MOVE_THRESHOLD_DISTANCE_X;
}
public int moveThresholdDistanceY(){
    return MOVE_THRESHOLD_DISTANCE_Y;
}
public int clickThresholdDistanceMax(){
    return CLICK_THRESHOLD_DISTANCE_MAX;
}

@Override
final public boolean onTouch(TouchInfo touchInfo){
    if((touchInfo == null) || (touchInfo.getId() != 0)){
        return false;
    }
    if(touchInfo.getAction() == DOWN){
        clickDisabled = false;
        return onDown(touchInfo);
    }
    if(touchInfo.getAction() == UP){
        boolean intercept = false;
        if(!clickDisabled){
            Point diff = touchInfo.diffFromOrigin();
            if((Math.abs(diff.getX()) < clickThresholdDistanceMax()) && (Math.abs(diff.getY()) < clickThresholdDistanceMax())){
                touchInfo.update(CLICK_SHORT);
                intercept = onClick(touchInfo);
                touchInfo.update(UP);
            }
        }
        return intercept | onUp(touchInfo);
    }
    if(touchInfo.getAction() == CANCEL){
        return onCancel(touchInfo);
    }
    if(touchInfo.getAction() == MOVE){
        Point diff = touchInfo.diffFromOrigin();
        if(Math.abs(diff.getX()) > Math.abs(diff.getY())){
            if(Math.abs(diff.getX()) > moveThresholdDistanceX()){
                clickDisabled = true;
                if(diff.getX() > 0){
DebugLog.start().send(this, "POINTER:0 MOVE_RIGHT").end();
                    touchInfo.update(MOVE_RIGHT);
                    return onMove(touchInfo);
                } else if(diff.getX() < 0){
DebugLog.start().send(this, "POINTER:0 MOVE_LEFT").end();
                    touchInfo.update(MOVE_LEFT);
                    return onMove(touchInfo);
                }
            }
        } else {
            if((Math.abs(diff.getY()) > moveThresholdDistanceY())){
                clickDisabled = true;
                if(diff.getY() > 0){
DebugLog.start().send(this, "POINTER:0 MOVE_DOWN").end();
                    touchInfo.update(MOVE_DOWN);
                    return onMove(touchInfo);
                } else if(diff.getY() < 0){
DebugLog.start().send(this, "POINTER:0 MOVE_UP").end();
                    touchInfo.update(MOVE_UP);
                    return onMove(touchInfo);
                }
            }
        }
        return onMove(touchInfo);
    }
    return false;
}
protected boolean onClick(TouchInfo touchInfo){
    return false;
}
protected boolean onDown(TouchInfo touchInfo){
    return false;
}
protected boolean onMove(TouchInfo touchInfo){
    return false;
}
protected boolean onUp(TouchInfo touchInfo){
    return false;
}
protected boolean onCancel(TouchInfo touchInfo){
    return false;
}

public interface Action extends TouchEvent.Action{
    Is MOVE_LEFT = new Is("MOVE_LEFT");
    Is MOVE_RIGHT = new Is("MOVE_RIGHT");
    Is MOVE_DOWN = new Is("MOVE_DOWN");
    Is MOVE_UP = new Is("MOVE_UP");
    Is CLICK_SHORT = new Is("CLICK_SHORT");

}

}
