/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.debug.debugBar;

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
import android.view.ViewTreeObserver;

import androidx.annotation.Nullable;

import com.tezov.lib_java_android.application.AppContext;
import com.tezov.lib_java_android.definition.defViewContainer;
import com.tezov.lib_java_android.wrapperAnonymous.ViewTreeObserverOnOnScrollChangedListenerW;
import com.tezov.lib_java_android.ui.layout.ScrollViewHorizontal;

import static com.tezov.lib_java_android.debug.debugBar.DebugBarLogLayout.SHARE_PREFERENCES_DEBUG;

public class DebugBarScrollView extends ScrollViewHorizontal implements defViewContainer{
private final static String SCROLL_X = DebugBarScrollView.class.getSimpleName() + "_SCROLL_X";
protected ViewTreeObserverOnOnScrollChangedListenerW scrollListener = null;
private android.content.SharedPreferences sp;

public DebugBarScrollView(Context context){
    super(context);
    init(context, null, -1, -1);
}

public DebugBarScrollView(Context context, @Nullable AttributeSet attrs){
    super(context, attrs);
    init(context, attrs, -1, -1);
}

public DebugBarScrollView(Context context, @Nullable AttributeSet attrs, int defStyleAttr){
    super(context, attrs, defStyleAttr);
    init(context, attrs, defStyleAttr, -1);
}
public DebugBarScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes){
    super(context, attrs, defStyleAttr, defStyleRes);
    init(context, attrs, defStyleAttr, defStyleRes);
}
private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes){
    sp = AppContext.getSharedPreferences(SHARE_PREFERENCES_DEBUG, android.content.Context.MODE_PRIVATE);
}

@Override
protected void onAttachedToWindow(){
    super.onAttachedToWindow();
    int scrollX = Integer.parseInt(sp.getString(SCROLL_X, "-1"));
    if((scrollX != -1)){
        getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener(){
            @Override
            public boolean onPreDraw(){
                getViewTreeObserver().removeOnPreDrawListener(this);
                smoothScrollTo(scrollX, 0);
                return false;
            }
        });
    }
    if(scrollListener == null){
        scrollListener = new ViewTreeObserverOnOnScrollChangedListenerW(){
            @Override
            public void onScrollChanged(){
                sp.edit().putString(SCROLL_X, String.valueOf(getScrollX())).apply();
            }
        };
        getViewTreeObserver().addOnScrollChangedListener(scrollListener);
    }
}
@Override
protected void onDetachedFromWindow(){
    super.onDetachedFromWindow();
    if(scrollListener != null){
        getViewTreeObserver().removeOnScrollChangedListener(scrollListener);
        scrollListener = null;
    }
}

}
