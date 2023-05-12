/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.toolbar.behavior.bottom;

import com.tezov.lib_java.debug.DebugLog;
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
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

public class BehaviorBottomViewHideSlideWhenScrollDown extends BehaviorBottomViewSlideBase{

public BehaviorBottomViewHideSlideWhenScrollDown(){
    super();
    init(null, null);
}

public BehaviorBottomViewHideSlideWhenScrollDown(Context context, AttributeSet attrs){
    super(context, attrs);
    init(context, attrs);
}

private void init(Context context, AttributeSet attrs){

}

@Override
public void onNestedScroll(CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type,
        @NonNull int[] consumed){
    if(isEnableScrollDrag()){
        if((dyConsumed > 0) || (dyUnconsumed > 0)){
            slideDown(child);
        } else if((dyConsumed < 0) || (dyUnconsumed < 0)){
            slideUp(child);
        }
    }

}

}
