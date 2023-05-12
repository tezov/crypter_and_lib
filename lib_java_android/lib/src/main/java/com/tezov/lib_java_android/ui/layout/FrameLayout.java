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
import com.tezov.lib_java.type.misc.SupplierSubscription;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.tezov.lib_java_android.definition.defViewContainer;
import com.tezov.lib_java_android.toolbox.PostToHandler;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java_android.type.android.ViewTreeEvent;
import com.tezov.lib_java.type.runnable.RunnableW;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class FrameLayout extends android.widget.FrameLayout implements defViewContainer{

protected ViewTreeEvent.Subscription xSubscription = null;
protected ViewTreeEvent.Subscription ySubscription = null;
protected Float xFraction = null;
protected Float yFraction = null;

public FrameLayout(Context context){
    super(context);
    init(context, null, -1, -1);
}

public FrameLayout(Context context, @Nullable AttributeSet attrs){
    super(context, attrs);
    init(context, attrs, -1, -1);
}

public FrameLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr){
    super(context, attrs, defStyleAttr);
    init(context, attrs, defStyleAttr, -1);
}

public FrameLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes){
    super(context, attrs, defStyleAttr, defStyleRes);
    init(context, attrs, defStyleAttr, defStyleRes);
}
public static FrameLayout newMM(Context context){
    FrameLayout frameLayout = new FrameLayout(context);
    frameLayout.setLayoutParams(new FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT));
    return frameLayout;
}
public static FrameLayout newMW(Context context){
    FrameLayout frameLayout = new FrameLayout(context);
    frameLayout.setLayoutParams(new FrameLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
    return frameLayout;
}
public static FrameLayout newWW(Context context){
    FrameLayout frameLayout = new FrameLayout(context);
    frameLayout.setLayoutParams(new FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
    return frameLayout;
}
public static FrameLayout newWithSize(Context context, int width, int height){
    FrameLayout frameLayout = new FrameLayout(context);
    frameLayout.setLayoutParams(new FrameLayout.LayoutParams(width, height));
    return frameLayout;
}

private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes){
DebugTrack.start().create(this).end();
}

@Override
public float getFractionX(){
    if(xFraction != null){
        return xFraction;
    }
    int width = getWidth();
    if(width != 0){
        return getX() / (width + getPaddingLeft() + getPaddingRight());
    } else {
        return 1;
    }
}
@Override
public void setFractionX(float fraction){
    this.xFraction = fraction;
    if(!isAttachedToWindow()){
        if(xSubscription == null){
            xSubscription = ViewTreeEvent.onPreDraw(this, new SupplierSubscription<>(){
                @Override
                public Boolean onComplete(){
                    unsubscribe();
                    xSubscription = null;
                    setFractionX(xFraction);
                    return false;
                }
            });
        }
        return;
    }
    float translationX = (getWidth() + getPaddingLeft() + getPaddingRight()) * fraction;
    setTranslationX(translationX);
}
@Override
public float getFractionY(){
    if(yFraction != null){
        return yFraction;
    }
    int height = getHeight();
    if(height != 0){
        return getY() / (height + getPaddingTop() + getPaddingBottom());
    } else {
        return 1;
    }
}
@Override
public void setFractionY(float fraction){
    this.yFraction = fraction;
    if(!isAttachedToWindow()){
        if(ySubscription == null){
            ySubscription = ViewTreeEvent.onPreDraw(this, new SupplierSubscription<>(){
                @Override
                public Boolean onComplete(){
                    unsubscribe();
                    ySubscription = null;
                    setFractionY(yFraction);
                    return false;
                }
            });
        }
        return;
    }
    float translationY = (getHeight() + getPaddingTop() + getPaddingBottom()) * fraction;
    setTranslationY(translationY);
}
@Override
protected void onDetachedFromWindow(){
    super.onDetachedFromWindow();
    if(xSubscription != null){
        xSubscription.unsubscribe();
        xSubscription = null;
    }
    if(ySubscription != null){
        ySubscription.unsubscribe();
        ySubscription = null;
    }
}
public <V extends View> V getView(){
    return (V)getChildAt(0);
}
public <V extends View> Class<V> getViewType(){
    if(isEmpty()){
        return null;
    }
    else {
        return (Class<V>)getChildAt(0).getClass();
    }
}
@Override
public boolean isEmpty(){
    return getChildCount() == 0;
}
@Override
public void addView(View view){
    PostToHandler.of(this,new RunnableW(){
        @Override
        public void runSafe(){
            removeAllViewsInLayout();
            addViewSuper(view);
        }
    });
}
protected void addViewSuper(View view){
    super.addView(view, -1);
}
@Override
public void putView(View view){
    addView(view);
}
@Override
public void putView(View view, int index){
    addView(view);
}
@Override
public void removeViewWithId(int id){
    View view = findViewById(id);
    if(view != null){
        removeView(view);
    }
}
@Override
public void removeView(View view){
    PostToHandler.of(this,new RunnableW(){
        @Override
        public void runSafe(){
            removeViewSuper(view);
        }
    });
}
protected void removeViewSuper(View view){
    super.removeView(view);
}
@Override
public void removeAllViews(){
    PostToHandler.of(this,new RunnableW(){
        @Override
        public void runSafe(){
            removeAllViewsInLayout();
        }
    });
}
@Override
public boolean hasView(View view){
    return hasView(view.getId());
}
@Override
public boolean hasView(int id){
    for(int end = getChildCount(), i = 0; i < end; i++){
        if(getChildAt(i).getId() == id){
            return true;
        }
    }
    return false;
}
@Override
public void replaceViewWithId(View newView, int id){
    PostToHandler.of(this,new RunnableW(){
        @Override
        public void runSafe(){
            if(getView().getId() == id){
                removeAllViewsInLayout();
                newView.setId(id);
                addViewSuper(newView);
            }
        }
    });
}
@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

}
