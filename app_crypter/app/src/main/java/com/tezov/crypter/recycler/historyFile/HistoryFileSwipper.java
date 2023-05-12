/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.crypter.recycler.historyFile;

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugTrack;
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
import static com.tezov.lib_java_android.ui.misc.TouchDetectorMove.Action.MOVE_LEFT;
import static com.tezov.lib_java_android.ui.misc.TouchDetectorMove.Action.MOVE_RIGHT;

import android.view.View;

import com.tezov.crypter.R;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java_android.ui.misc.TouchDetectorMove;
import com.tezov.lib_java_android.ui.recycler.RecyclerListRowHolder;
import com.tezov.lib_java_android.ui.recycler.RecyclerListSwipper;

public class HistoryFileSwipper extends RecyclerListSwipper{
private final static int VIEW_MODAL_RIGHT_LAYOUT = R.layout.recycler_history_file_row_swipe_left;
@Override
protected Integer modalViewLayoutID(TouchDetectorMove.Action.Is direction, RecyclerListRowHolder holder){
    if(direction == MOVE_LEFT){
        return VIEW_MODAL_RIGHT_LAYOUT;
    } else if(direction == MOVE_RIGHT){
        return null;
    } else {
DebugException.start().unknown("direction", direction).end();
        return null;
    }
}
@Override
protected int modalViewMeasureSpec(TouchDetectorMove.Action.Is direction){
    if(direction == MOVE_LEFT){
        return View.MeasureSpec.EXACTLY;
    } else {
        return super.modalViewMeasureSpec(direction);
    }
}
@Override
protected float swipeThresholdDistanceRatio(TouchDetectorMove.Action.Is direction){
    if(direction == MOVE_LEFT){
        return 0.8f;
    } else {
        return super.swipeThresholdDistanceRatio(direction);
    }
}
@Override
protected Animation animation(TouchDetectorMove.Action.Is direction){
    if(direction == MOVE_LEFT){
        return Animation.SLIDE_ABOVE;
    } else {
        return super.animation(direction);
    }
}

}
