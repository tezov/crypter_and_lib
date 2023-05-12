/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.component.plain;

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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.google.android.material.textfield.TextInputEditText;
import com.tezov.lib_java_android.R;
import com.tezov.lib_java_android.application.AppContext;
import com.tezov.lib_java_android.application.AppDisplay;
import com.tezov.lib_java.async.Handler;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java_android.type.android.ViewTreeEvent;
import com.tezov.lib_java_android.ui.misc.AttributeReader;
import com.tezov.lib_java_android.wrapperAnonymous.EditTextTextWatcherListenerW;
import com.tezov.lib_java.type.runnable.RunnableW;

public class TextViewScrollable extends TextInputEditText{
final static private int[] ATTR_INDEX = R.styleable.TextViewScrollable_lib;

private final static float INTERCEPT_MOVE_THRESHOLD = AppDisplay.convertDpToPx(10);
private final static long INTERCEPT_RELEASE_DELAY_ms = 50;
private final static float DRAWABLE_SIZE_RATIO = 0.9f;
private final static int ICON_PADDING_START = AppDisplay.convertDpToPx(3);
private final static int ICON_PADDING_END = AppDisplay.convertDpToPx(3);
private final static float ICON_WIDTH_RATIO = 0.23f;
private final static float ICON_HEIGHT_RATIO = 0.9f;
private final static float THRESHOLD_START_ICON_VISIBLE = 0.05f;
private final static float THRESHOLD_END_ICON_NOT_VISIBLE = 0.95f;

private boolean touchEventIntercepted = false;
private float moveOriginX = 0.0f;
private float maxX = 0.0f;
private int colorIconScroll = 0;
private IconScrollStart iconStart = null;
private IconScrollEnd iconEnd = null;
private int drawableSize = 0;
private float position = 1.0f;
public TextViewScrollable(Context context){
    super(context);
    init(context, null, NO_ID);
}
public TextViewScrollable(Context context, AttributeSet attrs){
    super(context, attrs);
    init(context, attrs, NO_ID);
}
public TextViewScrollable(Context context, AttributeSet attrs, int defStyleAttr){
    super(context, attrs, defStyleAttr);
    init(context, attrs, defStyleAttr);
}
private void init(Context context, AttributeSet attrs, int defStyleAttr){
DebugTrack.start().create(this).end();
    iconStart = new IconScrollStart();
    iconEnd = new IconScrollEnd();
    Integer color = AppContext.getResources().resolveColorARGB(R.attr.colorPrimary);
    if(color != null){
        colorIconScroll = color;
    } else {
        colorIconScroll = Color.BLACK;
    }
    setInputType(InputType.TYPE_CLASS_TEXT);
    setEnabled(false);
    addTextChangedListener(new EditTextTextWatcherListenerW(){
        @Override
        public void onTextChanged(Editable es){
            if(getLayout() != null){
                updateMaxX();
            }
        }
    });
    Integer scrollPosition = null;
    if(attrs != null){
        AttributeReader attributes = new AttributeReader().setAttrsIndex(ATTR_INDEX).parse(context, attrs);
        scrollPosition = attributes.asInteger(R.styleable.TextViewScrollable_lib_scroll_position);
    }
    if(scrollPosition == null){
        scrollPosition = 1;
    }
    if(scrollPosition == 1){
        ViewTreeEvent.onPreDraw(this, new SupplierSubscription<>(){
            @Override
            public Boolean onComplete(){
                unsubscribe();
                moveToEnd();
                return false;
            }
        });
    }
}

public char[] getChars(){
    if(length() <= 0){
        return null;
    } else {
        char[] c = new char[length()];
        getText().getChars(0, c.length, c, 0);
        return c;
    }
}

@Override
protected void onSizeChanged(int w, int h, int oldw, int oldh){
    super.onSizeChanged(w, h, oldw, oldh);
    int offsetY = 0;
    int height = h - getPaddingTop() - getPaddingBottom();
    iconStart.computeSize(height, offsetY);
    iconEnd.computeSize(height, offsetY);
    Drawable[] drawables = getCompoundDrawables();
    Drawable drawableStart = drawables[EditTextWithIcon.IconPosition.START.value];
    if(drawableStart != null){
        drawableSize = (int)(height * DRAWABLE_SIZE_RATIO);
        drawableStart.setBounds(0, 0, drawableSize, drawableSize);
    }
    updateMaxX();
}
private void updateMaxX(){
    float maxWidth = getLayout().getLineMax(0);
    int width = getWidth();
    if((width <= 0) || (maxWidth <= 0.0f)){
        maxX = 0.0f;
    } else {
        maxX = maxWidth - (width - getPaddingStart() - getPaddingEnd() - drawableSize);
        if(maxX < 0){
            maxX = 0.0f;
        }
    }
    updateIconVisibility();
}
private void updateIconVisibility(){
    if(maxX <= 0.0f){
        iconStart.setVisibility(false);
        iconEnd.setVisibility(false);
        position = 1.0f;
    } else {
        position = getScrollX() / maxX;
        iconStart.updateVisibility(position);
        iconEnd.updatePosition(position);
    }
    invalidate();
}

@Override
protected void onLayout(boolean changed, int left, int top, int right, int bottom){
    super.onLayout(changed, left, top, right, bottom);
    updateMaxX();
}

@Override
protected void onDraw(Canvas canvas){
    canvas.save();
    if(drawableSize != 0){
        canvas.translate(drawableSize, 0);
    }
    canvas.clipRect(getScrollX() + iconStart.getWidth(), 0, getScrollX() + getWidth() - drawableSize - iconEnd.getWidth(), getHeight());
    Paint paint = getPaint();
    paint.setColor(getCurrentTextColor());
    getLayout().draw(canvas);
    canvas.restore();
    canvas.save();
    canvas.translate(getScrollX(), getPaddingTop());
    if(drawableSize != 0){
        Drawable[] drawables = getCompoundDrawables();
        Drawable drawableStart = drawables[EditTextWithIcon.IconPosition.START.value];
        drawableStart.draw(canvas);
        canvas.translate(drawableSize, 0);
    }
    paint.setColor(colorIconScroll);
    iconStart.onDraw(getPaint(), canvas);
    canvas.restore();
    canvas.save();
    canvas.translate(getScrollX() + getWidth() - iconEnd.getWidth(), getPaddingTop());
    iconEnd.onDraw(getPaint(), canvas);
    canvas.restore();
}
@Override
public void setEnabled(boolean enabled){
    setFocusable(enabled);
    setFocusableInTouchMode(enabled);
    setCursorVisible(enabled);
}
@Override
public boolean onTouchEvent(MotionEvent event){
    boolean canScrollHorizontally = canScrollHorizontally(-1) | canScrollHorizontally(1);
    if(canScrollHorizontally){
        int action = event.getActionMasked();
        switch(action){
            case (MotionEvent.ACTION_DOWN):
            case (MotionEvent.ACTION_POINTER_DOWN):{
                moveOriginX = event.getX();
            }
            break;
            case (MotionEvent.ACTION_MOVE):{
                if(!touchEventIntercepted){
                    boolean ignore = false;
                    float diff = moveOriginX - event.getX();
                    if(diff > 0){
                        ignore = position >= 1.0f;
                    } else if(diff < 0){
                        ignore = position <= 0.0f;
                    }
                    if(!ignore && Math.abs(diff) > INTERCEPT_MOVE_THRESHOLD){
                        touchEventIntercepted = true;
                        getParent().requestDisallowInterceptTouchEvent(true);
                    }
                }
            }
            break;
            case (MotionEvent.ACTION_UP):
            case (MotionEvent.ACTION_POINTER_UP):
            case (MotionEvent.ACTION_CANCEL):{
                if(touchEventIntercepted){
                    Handler.MAIN().post(this, INTERCEPT_RELEASE_DELAY_ms, new RunnableW(){
                        @Override
                        public void runSafe(){
                            touchEventIntercepted = false;
                            getParent().requestDisallowInterceptTouchEvent(false);
                        }
                    });
                }
            }
            break;
        }
        updateIconVisibility();
    }
    return super.onTouchEvent(event);
}

public void moveToStart(){
    moveTo(0);
}
public void moveTo(int index){
    setSelection(index);
}
public void moveToEnd(){
    Editable text = getText();
    if(text != null){
        moveTo(text.length());
    }
}
@Override
protected void onScrollChanged(int horiz, int vert, int oldHoriz, int oldVert){
    super.onScrollChanged(horiz, vert, oldHoriz, oldVert);
    updateIconVisibility();
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

private static class IconScrollStart{
    boolean isVisible = false;
    int width = 0;
    int height = 0;
    int widthHalf = 0;
    int heightHalf = 0;
    int xCenter = 0;
    int yCenter = 0;
    void computeSize(int maxHeight, int offsetY){
        width = (int)(maxHeight * ICON_WIDTH_RATIO);
        height = (int)(maxHeight * ICON_HEIGHT_RATIO);
        widthHalf = width / 2;
        heightHalf = height / 2;
        xCenter = width / 2;
        yCenter = (maxHeight / 2) + offsetY;
    }
    void setVisibility(boolean flag){
        isVisible = flag;
    }
    void updateVisibility(float position){
        if(position > THRESHOLD_START_ICON_VISIBLE){
            if(!isVisible){
                isVisible = true;
            }
        } else if(isVisible){
            isVisible = false;
        }
    }
    int getWidth(){
        if(isVisible){
            return width + ICON_PADDING_END;
        } else {
            return 0;
        }
    }
    void onDraw(Paint paint, Canvas canvas){
        if(isVisible){
            paint.setStyle(Paint.Style.FILL);
            Path path = new Path();
            path.moveTo(xCenter + widthHalf, yCenter - heightHalf);
            path.lineTo(xCenter - widthHalf, yCenter);
            path.lineTo(xCenter + widthHalf, yCenter + heightHalf);
            path.lineTo(xCenter + widthHalf, yCenter - heightHalf);
            path.close();
            canvas.drawPath(path, paint);
        }
    }

}

private static class IconScrollEnd{
    boolean isVisible = false;
    int width = 0;
    int height = 0;
    int widthHalf = 0;
    int heightHalf = 0;
    int xCenter = 0;
    int yCenter = 0;
    void computeSize(int maxHeight, int offsetY){
        width = (int)(maxHeight * ICON_WIDTH_RATIO);
        height = (int)(maxHeight * ICON_HEIGHT_RATIO);
        widthHalf = width / 2;
        heightHalf = height / 2;
        xCenter = width / 2;
        yCenter = (maxHeight / 2) + offsetY;
    }
    void setVisibility(boolean flag){
        isVisible = flag;
    }
    void updatePosition(float position){
        if(position > THRESHOLD_END_ICON_NOT_VISIBLE){
            if(isVisible){
                isVisible = false;
            }
        } else {
            if(!isVisible){
                isVisible = true;
            }
        }
    }
    int getWidth(){
        if(isVisible){
            return ICON_PADDING_START + width + ICON_PADDING_END;
        } else {
            return 0;
        }
    }
    void onDraw(Paint paint, Canvas canvas){
        if(isVisible){
            paint.setStyle(Paint.Style.FILL);
            Path path = new Path();
            path.moveTo(ICON_PADDING_START + xCenter - widthHalf, yCenter - heightHalf);
            path.lineTo(ICON_PADDING_START + xCenter + widthHalf, yCenter);
            path.lineTo(ICON_PADDING_START + xCenter - widthHalf, yCenter + heightHalf);
            path.lineTo(ICON_PADDING_START + xCenter - widthHalf, yCenter - heightHalf);
            path.close();
            canvas.drawPath(path, paint);
        }
    }

}

}
