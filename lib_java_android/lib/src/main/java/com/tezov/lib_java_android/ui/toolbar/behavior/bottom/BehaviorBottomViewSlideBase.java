/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.toolbar.behavior.bottom;

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
import androidx.fragment.app.Fragment;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;

import com.google.android.material.behavior.HideBottomViewOnScrollBehavior;
import com.tezov.lib_java.debug.DebugTrack;

public class BehaviorBottomViewSlideBase extends HideBottomViewOnScrollBehavior<View>{
protected boolean enableScrollDrag = true;

public BehaviorBottomViewSlideBase(){
    super();
    init(null, null);
}

public BehaviorBottomViewSlideBase(Context context, AttributeSet attrs){
    super(context, attrs);
    init(context, attrs);
}

private void init(Context context, AttributeSet attrs){
DebugTrack.start().create(this).end();
}

public void enableScrollDrag(boolean enableScrollDrag){
    this.enableScrollDrag = enableScrollDrag;
}

public boolean isEnableScrollDrag(){
    return enableScrollDrag;
}

@Override
public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View view, @NonNull View directTargetChild, @NonNull View target, int axes, int type){
    return enableScrollDrag && axes == ViewCompat.SCROLL_AXIS_VERTICAL;
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

}
