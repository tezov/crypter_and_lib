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
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.Gravity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

public class TextViewVertical extends AppCompatTextView{
private boolean topDown;

public TextViewVertical(@NonNull Context context){
    super(context);
    init(context, null, NO_ID);
}

public TextViewVertical(@NonNull Context context, @Nullable AttributeSet attrs){
    super(context, attrs);
    init(context, attrs, NO_ID);
}

public TextViewVertical(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr){
    super(context, attrs, defStyleAttr);
    init(context, attrs, defStyleAttr);
}

private void init(Context context, AttributeSet attrs, int defStyleAttr){
    int gravity = getGravity();
    if(Gravity.isVertical(gravity) && ((gravity & Gravity.VERTICAL_GRAVITY_MASK) == Gravity.BOTTOM)){
        setGravity((gravity & Gravity.HORIZONTAL_GRAVITY_MASK) | Gravity.TOP);
        topDown = false;
    } else {
        topDown = true;
    }
}

@Override
protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
    super.onMeasure(heightMeasureSpec, widthMeasureSpec);
    setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
}

@Override
protected void onDraw(Canvas canvas){
//    TextPaint textPaint = getPaint();
//    textPaint.setColor(getCurrentTextColor());
//    textPaint.drawableState = getDrawableState();
    canvas.save();
    if(topDown){
        canvas.translate(getWidth(), 0);
        canvas.rotate(90);
    } else {
        canvas.translate(0, getHeight());
        canvas.rotate(-90);
    }
    canvas.translate(getCompoundPaddingLeft(), getExtendedPaddingTop());
    getLayout().draw(canvas);
    canvas.restore();
}

}
