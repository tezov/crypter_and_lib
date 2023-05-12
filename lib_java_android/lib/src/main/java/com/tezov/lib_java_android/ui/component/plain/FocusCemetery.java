/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.component.plain;

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
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.tezov.lib_java_android.application.AppContext;
import com.tezov.lib_java_android.application.AppKeyboard;
import com.tezov.lib_java_android.util.UtilsView;

public class FocusCemetery extends View{

public FocusCemetery(Context context){
    super(context);
    init(context, null, NO_ID);
}
public FocusCemetery(Context context, AttributeSet attrs){
    super(context, attrs);
    init(context, attrs, NO_ID);
}
public FocusCemetery(Context context, AttributeSet attrs, int defStyleAttr){
    super(context, attrs, defStyleAttr);
    init(context, attrs, defStyleAttr);
}

private void init(Context context, AttributeSet attrs, int defStyleAttr){
    setFocusable(true);
    setFocusableInTouchMode(true);
    setAlpha(0.0f);
}

@Override
public void setLayoutParams(ViewGroup.LayoutParams params){
    params.width = 1;
    params.height = 1;
    super.setLayoutParams(params);
}
public static void request(){
    request(AppContext.getActivity().getCurrentFocus(), UtilsView.Direction.UP, true);
}
public static void request(View view){
    request(view, UtilsView.Direction.DOWN, true);
}
public static void request(View view, boolean hideKeyBoard){
    request(view, UtilsView.Direction.DOWN, hideKeyBoard);
}
public static void request(View view, UtilsView.Direction direction){
    request(view, direction, true);
}
public static void request(View view, UtilsView.Direction direction, boolean hideKeyBoard){
    if((view != null) && (!(view instanceof FocusCemetery))){
        if(hideKeyBoard){
            AppKeyboard.hide(view);
        }
        FocusCemetery cemetery = UtilsView.findFirst(FocusCemetery.class, view, direction);
        if(cemetery != null){
            cemetery.requestFocus();
        }
        else {
            view.clearFocus();
        }
    }
}

@Override
protected void onFocusChanged(boolean gainFocus, int direction, @Nullable Rect previouslyFocusedRect){
    super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
    if(gainFocus){
        AppKeyboard.hide(this);
    }
}

}

