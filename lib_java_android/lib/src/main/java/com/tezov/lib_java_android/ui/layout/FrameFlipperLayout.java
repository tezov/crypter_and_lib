/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.layout;

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
import com.tezov.lib_java_android.toolbox.PostToHandler;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.primaire.Size;
import com.tezov.lib_java.type.runnable.RunnableW;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class FrameFlipperLayout extends FrameLayout{

private final Size maxSize = new Size(0, 0);
private int currentShowingId = NO_ID;
private boolean resizeAllViewAtMaxSize = false;

public FrameFlipperLayout(Context context){
    super(context);
    init(context, null, -1, -1);
}
public FrameFlipperLayout(Context context, @Nullable AttributeSet attrs){
    super(context, attrs);
    init(context, attrs, -1, -1);
}
public FrameFlipperLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr){
    super(context, attrs, defStyleAttr);
    init(context, attrs, defStyleAttr, -1);
}
public FrameFlipperLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes){
    super(context, attrs, defStyleAttr, defStyleRes);
    init(context, attrs, defStyleAttr, defStyleRes);
}

public static FrameFlipperLayout newMM(Context context){
    FrameFlipperLayout frameLayout = new FrameFlipperLayout(context);
    frameLayout.setLayoutParams(new LayoutParams(MATCH_PARENT, MATCH_PARENT));
    return frameLayout;
}
public static FrameFlipperLayout newMW(Context context){
    FrameFlipperLayout frameLayout = new FrameFlipperLayout(context);
    frameLayout.setLayoutParams(new LayoutParams(MATCH_PARENT, WRAP_CONTENT));
    return frameLayout;
}
public static FrameFlipperLayout newWW(Context context){
    FrameFlipperLayout frameLayout = new FrameFlipperLayout(context);
    frameLayout.setLayoutParams(new LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
    return frameLayout;
}

private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes){
DebugTrack.start().create(this).end();
}

public boolean isResizeAllViewAtMaxSize(){
    return resizeAllViewAtMaxSize;
}
public FrameFlipperLayout setResizeAllViewAtMaxSize(boolean flag){
    this.resizeAllViewAtMaxSize = flag;
    return this;
}
public Size getMaxSize(){
    return maxSize;
}
public int getMaxWidth(){
    return maxSize.getWidth();
}
public int getMaxHeight(){
    return maxSize.getHeight();
}

public void showViewDefault(){
    showView(NO_ID);
}
public void showView(int id){
    PostToHandler.of(this, new RunnableW(){
        @Override
        public void runSafe(){
            showViewSuper(id);
        }
    });
}
protected void showViewSuper(int id){
    if(id != NO_ID){
        for(int end = getChildCount(), i = 0; i < end; i++){
            View view = getChildAt(i);
            if(view.getId() == id){
                currentShowingId = id;
                view.setVisibility(VISIBLE);
            }
            else if(view.getVisibility() != GONE){
                view.setVisibility(GONE);
            }
        }
    }
    else{
        currentShowingId = NO_ID;
        int end = getChildCount();
        for(int i = 1; i < end; i++){
            View view = getChildAt(i);
            if(view.getVisibility() != GONE){
                view.setVisibility(GONE);
            }
        }
        if(end > 0){
            getChildAt(0).setVisibility(VISIBLE);
        }
    }
}

@Override
public void addView(View view){
    PostToHandler.of(this,new RunnableW(){
        @Override
        public void runSafe(){
            addViewSuper(view);
        }
    });
}

public void putAndShowView(View view){
    PostToHandler.of(this, new RunnableW(){
        @Override
        public void runSafe(){
            addViewSuper(view);
            showViewSuper(view.getId());
        }
    });
}

@Override
protected void addViewSuper(View view){
    view.setVisibility(GONE);
    super.addViewSuper(view);
}

@Override
protected void removeViewSuper(View view){
    boolean showViewDefault = view.getVisibility() == VISIBLE;
    super.removeViewSuper(view);
    int childCount =  getChildCount();
    if(showViewDefault || (childCount <= 1)){
        currentShowingId = NO_ID;
        if(childCount == 1){
            getChildAt(0).setVisibility(VISIBLE);
        }
    }
}

public int getCurrentShowingId(){
    return currentShowingId;
}

@Override
protected void onSizeChanged(int w, int h, int oldw, int oldh){
    super.onSizeChanged(w, h, oldw, oldh);
    maxSize.setWidth(Math.max(maxSize.getWidth(), w)).setHeight(Math.max(maxSize.getHeight(), h));
    if(resizeAllViewAtMaxSize){
        for(int end = getChildCount(), i = 0; i < end; i++){
            View view = getChildAt(i);
            ViewGroup.LayoutParams params = view.getLayoutParams();
            if((params!=null) && (params.width != maxSize.getWidth())&&(params.height != maxSize.getHeight())){
                params.width = maxSize.getWidth();
                params.height = maxSize.getHeight();
                view.setLayoutParams(params);
            }
        }
    }
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

}
