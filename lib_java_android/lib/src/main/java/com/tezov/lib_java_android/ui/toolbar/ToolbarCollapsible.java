/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.toolbar;

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
import static com.tezov.lib_java_android.ui.toolbar.ToolbarCollapsible.Status.COLLAPSED;
import static com.tezov.lib_java_android.ui.toolbar.ToolbarCollapsible.Status.EXPANDED;
import static com.tezov.lib_java_android.ui.toolbar.ToolbarCollapsible.Status.INTERMEDIATE;
import static com.tezov.lib_java_android.ui.toolbar.ToolbarCollapsible.Status.UNKNOWN;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.NormInt;
import com.tezov.lib_java.type.runnable.RunnableSubscription;
import com.tezov.lib_java.type.runnable.RunnableW;
import com.tezov.lib_java_android.toolbox.PostToHandler;
import com.tezov.lib_java_android.type.android.ViewTreeEvent;
import com.tezov.lib_java_android.ui.toolbar.behavior.top.BehaviorToolbarCollapsible;

public class ToolbarCollapsible extends com.google.android.material.appbar.AppBarLayout{
private boolean isReady = false;
private Status pendingStatus = UNKNOWN;
private Float pendingPosition = null;
private NormInt layoutPosition;

public ToolbarCollapsible(Context context){
    super(context);
    init(context, null, 0);
}

public ToolbarCollapsible(Context context, AttributeSet attrs){
    super(context, attrs);
    init(context, attrs, 0);
}

public ToolbarCollapsible(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr){
    super(context, attrs, defStyleAttr);
    init(context, attrs, defStyleAttr);
}
private void init(Context context, AttributeSet attrs, int defStyleAttr){
DebugTrack.start().create(this).end();
    layoutPosition = new NormInt().revert(true).negate(true);
    ViewTreeEvent.onLayout(this, new RunnableSubscription(){
        @Override
        public void onComplete(){
            unsubscribe();
            onReady();
        }
    });
}

public View getContentView(){
    if(getChildCount() > 0){
        return getChildAt(0);
    } else {
        return null;
    }
}
public void setContentView(View view){
    PostToHandler.of(this, new RunnableW(){
        @Override
        public void runSafe(){
            removeAllViews();
            if(view != null){
                ViewGroup.LayoutParams params = view.getLayoutParams();
                if(!(params instanceof ToolbarCollapsible.LayoutParams)){
                    params = new ToolbarCollapsible.LayoutParams(params);
                    ((ToolbarCollapsible.LayoutParams)params).setScrollFlags(
                        ToolbarCollapsible.LayoutParams.SCROLL_FLAG_SCROLL
                        | ToolbarCollapsible.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS_COLLAPSED);
                }
                addView(view);
            }
        }
    });
}

@Override
public BehaviorToolbarCollapsible getBehavior(){
    CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams)getLayoutParams();
    return (BehaviorToolbarCollapsible)params.getBehavior();
}

@Override
protected void onSizeChanged(int w, int h, int oldw, int oldh){
    super.onSizeChanged(w, h, oldw, oldh);
    layoutPosition.setBase(getTotalScrollRange());
}
public Status getStatus(){
    if(!isReady){
        return UNKNOWN;
    }
    if(getPosition() == 0.0f){
        return COLLAPSED;
    }
    if(getPosition() == 1.0f){
        return EXPANDED;
    }
    return INTERMEDIATE;
}

protected void onReady(){
    isReady = true;
    if(pendingStatus == UNKNOWN){
        return;
    }
    if(pendingStatus == COLLAPSED){
        collapse();
    } else if(pendingStatus == EXPANDED){
        expand();
    } else if(pendingStatus == INTERMEDIATE){
        setPosition(pendingPosition);
    }
}

public void expand(){
    PostToHandler.of(this, new RunnableW(){
        @Override
        public void runSafe(){
            Status status = getStatus();
            if(status == UNKNOWN){
                pendingStatus = EXPANDED;
            } else if(status != EXPANDED){
                setExpanded(true, true);
            }
        }
    });
}

public void collapse(){
    PostToHandler.of(this, new RunnableW(){
        @Override
        public void runSafe(){
            Status status = getStatus();
            if(status == UNKNOWN){
                pendingStatus = COLLAPSED;
            } else if(status != COLLAPSED){
                setExpanded(false, true);
            }
        }
    });
}

public void setPosition(float position){
    Status status = getStatus();
    if(status == UNKNOWN){
        pendingStatus = INTERMEDIATE;
        pendingPosition = position;
    } else if(layoutPosition.isBaseNotNull()){
        getBehavior().setTopAndBottomOffset(layoutPosition.getRaw(position));
    } else {
        getBehavior().setTopAndBottomOffset(0);
    }
}

public float getPosition(){
    if(layoutPosition.isBaseNotNull()){
        return layoutPosition.getNorm(getBehavior().getTopAndBottomOffset());
    } else {
        return 0.0f;
    }
}

@Override
public void setVisibility(int visibility){
DebugException.start().notImplemented().end();
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}


public enum Status{
    COLLAPSED, EXPANDED, INTERMEDIATE, UNKNOWN
}

}
