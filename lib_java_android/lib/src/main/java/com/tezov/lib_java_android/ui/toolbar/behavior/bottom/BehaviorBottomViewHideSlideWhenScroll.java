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
import com.tezov.lib_java.async.Handler;
import com.tezov.lib_java.type.runnable.RunnableW;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

public class BehaviorBottomViewHideSlideWhenScroll extends BehaviorBottomViewSlideBase{
private final static Long DELAY_SLIDE_UP_ms = 500L;
private SlideUp slideUp = null;

private BehaviorBottomViewHideSlideWhenScroll me(){
    return this;
}

private class SlideUp extends RunnableW{
    View child = null;
    @Override
    public void runSafe() throws Throwable{
        if(child != null){
            slideUp(child);
            child = null;
        }
    }
    void start(View child){
        cancel();
        this.child = child;
        Handler.MAIN().post(me(), DELAY_SLIDE_UP_ms, this);
    }
    boolean cancel(){
        return super.cancel(this, Handler.MAIN());
    }
}

public BehaviorBottomViewHideSlideWhenScroll(){
    super();
    init(null, null);
}

public BehaviorBottomViewHideSlideWhenScroll(Context context, AttributeSet attrs){
    super(context, attrs);
    init(context, attrs);
}

private void init(Context context, AttributeSet attrs){
    slideUp = new SlideUp();
}

@Override
public void onStopNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View target, int type){
    if(isEnableScrollDrag()){
        slideUp.start(child);
    }
    super.onStopNestedScroll(coordinatorLayout, child, target, type);
}
@Override
public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View directTargetChild, @NonNull View target, int axes, int type){
    if(isEnableScrollDrag()){
        slideUp.cancel();
        slideDown(child);
    }
    return super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, axes, type);
}
@Override
public void onNestedPreScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View target, int dx, int dy, @NonNull int[] consumed, int type){

}
@Override
public void onNestedScroll(CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type,
        @NonNull int[] consumed){

}

}
