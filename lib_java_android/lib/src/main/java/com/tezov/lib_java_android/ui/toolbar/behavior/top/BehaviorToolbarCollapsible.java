/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.toolbar.behavior.top;

import com.google.android.material.appbar.AppBarLayout;
import com.tezov.lib_java.debug.DebugLog;
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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;

import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java_android.ui.toolbar.ToolbarCollapsible;

public class BehaviorToolbarCollapsible extends ToolbarCollapsible.Behavior{
protected boolean canScrollDrag = true;
protected boolean canTouchDrag = true;

public BehaviorToolbarCollapsible(){
    super();
    init(null, null);
}

public BehaviorToolbarCollapsible(Context context, AttributeSet attrs){
    super(context, attrs);
    init(context, attrs);
}

private void init(Context context, AttributeSet attrs){
DebugTrack.start().create(this).end();
    setDragCallback(new ToolbarCollapsible.Behavior.DragCallback(){
        @Override
        public boolean canDrag(com.google.android.material.appbar.AppBarLayout appBarLayout){
            return canTouchDrag;
        }
    });
}

public void enableDrag(boolean flag){
    enableDragTouch(flag);
    enableDragScroll(flag);
}

public void enableDragScroll(boolean flag){
    this.canScrollDrag = flag;
}

public void enableDragTouch(boolean flag){
    this.canTouchDrag = flag;
}

public boolean isDragEnabled(){
    return canScrollDrag || canTouchDrag;
}

public boolean isDragScrollEnabled(){
    return canScrollDrag;
}

public boolean isDragTouchEnabled(){
    return canTouchDrag;
}

@Override
public boolean onStartNestedScroll(CoordinatorLayout parent, com.google.android.material.appbar.AppBarLayout child, View directTargetChild, View target, int axes, int type){
    return canScrollDrag && axes == ViewCompat.SCROLL_AXIS_VERTICAL;
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

}
