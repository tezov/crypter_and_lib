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
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.tezov.lib_java_android.R;
import com.tezov.lib_java_android.application.AppContext;
import com.tezov.lib_java_android.application.AppDisplay;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java_android.wrapperAnonymous.ViewOnClickListenerW;

import java.util.List;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class ButtonBarHorizontal extends ScrollViewHorizontal{
protected LinearLayout btnContainer = null;
private View.OnClickListener onClickListener = null;
public ButtonBarHorizontal(Context context){
    super(context);
    init(context, null, NO_ID, NO_ID);
}
public ButtonBarHorizontal(Context context, @Nullable AttributeSet attrs){
    super(context, attrs);
    init(context, attrs, NO_ID, NO_ID);
}

public ButtonBarHorizontal(Context context, @Nullable AttributeSet attrs, int defStyleAttr){
    super(context, attrs, defStyleAttr);
    init(context, attrs, defStyleAttr, NO_ID);
}
public ButtonBarHorizontal(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes){
    super(context, attrs, defStyleAttr, defStyleRes);
    init(context, attrs, defStyleAttr, defStyleRes);
}
private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes){
DebugTrack.start().create(this).end();
    btnContainer = new LinearLayout(context);
    btnContainer.setOrientation(LinearLayout.HORIZONTAL);
    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
    btnContainer.setLayoutParams(params);
    addView(btnContainer);
}

public void add(Object tag, int resourceIconId){
    add(tag, AppContext.getResources().getDrawable(resourceIconId));
}
public void add(Object tag, Drawable icon){
    FrameLayout btn = new FrameLayout(getContext());
    int size = AppDisplay.convertDpToPx(48);
    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(size, size);
    Float paddingFloat = AppContext.getResources().resolveDimension(R.attr.dimPaddingElement_4);
    if(paddingFloat != null){
        int padding = paddingFloat.intValue();
        params.setMarginEnd(padding);
        if(btnContainer.getChildCount() == 0){
            params.setMarginStart(padding);
        }
    }
    btn.setLayoutParams(params);
    LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{AppContext.getResources().getDrawable(R.drawable.button_border), icon});
    btn.setBackground(layerDrawable);
    btn.setTag(tag);
    btn.setOnClickListener(new ViewOnClickListenerW(){
        @Override
        public void onClicked(View v){
            onClickListener.onClick(btn);
        }
    });
    btnContainer.addView(btn);
}

public void setEnable(boolean flag){
    setEnable(flag, null);
}
public void setEnable(boolean flag, List<String> exclude){
    for(int end = btnContainer.getChildCount(), i = 0; i < end; i++){
        View view = btnContainer.getChildAt(i);
        if((exclude == null) || (!exclude.contains(view.getTag()))){
            view.setEnabled(flag);
        }
    }
}

@Override
public void setOnClickListener(@Nullable OnClickListener l){
    onClickListener = l;
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

}
