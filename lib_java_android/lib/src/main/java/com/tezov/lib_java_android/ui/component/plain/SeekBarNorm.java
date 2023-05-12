/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.component.plain;

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

import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.util.AttributeSet;

import com.h6ah4i.android.widget.verticalseekbar.VerticalSeekBar;
import com.tezov.lib_java_android.R;
import com.tezov.lib_java_android.application.AppContext;
import com.tezov.lib_java_android.application.AppDisplay;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.NormInt;
import com.tezov.lib_java_android.ui.misc.AttributeReader;
import com.tezov.lib_java.util.UtilsAlpha;

public class SeekBarNorm extends VerticalSeekBar{
final static private int[] ATTR_INDEX = R.styleable.SeekBarNorm_lib;
// ALPHA STYLE
private final static int STROKE_THICK = AppDisplay.convertDpToPx(2);
private final static int BAR_HEIGHT = AppDisplay.convertDpToPx(20);
private final static int THUMB_WIDTH = AppDisplay.convertDpToPx(6);
private final static int THUMB_HEIGHT = AppDisplay.convertDpToPx(12);
private final static int STROKE_COLOR = AppContext.getResources().getColorARGB(R.color.Gray);
private final static int THUMB_COLOR = AppContext.getResources().getColorARGB(R.color.White);
private final static int BAR_COLOR_LEFT = UtilsAlpha.color(AppContext.getResources().getColorARGB(R.color.Gray), 0.3f);
private final static int BAR_COLOR_RIGHT = UtilsAlpha.color(AppContext.getResources().getColorARGB(R.color.Black), 0.8f);
private NormInt normSeekBar = null;
private float min;
private float max;

public SeekBarNorm(android.content.Context context){
    super(context);
    init(context, null, NO_ID);
}

public SeekBarNorm(android.content.Context context, AttributeSet attrs){
    super(context, attrs);
    init(context, attrs, NO_ID);
}

public SeekBarNorm(android.content.Context context, AttributeSet attrs, int defStyleAttr){
    super(context, attrs, defStyleAttr);
    init(context, attrs, defStyleAttr);
}

private void init(android.content.Context context, AttributeSet attrs, int defStyleAttr){
DebugTrack.start().create(this).end();
    setFocusable(false);
    setFocusableInTouchMode(false);
    if(attrs == null){
        setPrecision(0);
        min = 0.0f;
        max = getMax();
        return;
    }
    AttributeReader attributes = new AttributeReader().parse(context, ATTR_INDEX, attrs);

    Integer decimalPrecision = attributes.asInteger(R.styleable.SeekBarNorm_lib_precision);
    if(decimalPrecision == null){
        decimalPrecision = 0;
    }
    setPrecision(decimalPrecision);

    if(attributes.has(R.styleable.SeekBarNorm_lib_min_float)){
        min = attributes.asFloat(R.styleable.SeekBarNorm_lib_min_float);
    } else {
        min = 0.0f;
    }

    if(attributes.has(R.styleable.SeekBarNorm_lib_max_float)){
        max = attributes.asFloat(R.styleable.SeekBarNorm_lib_max_float);
    } else {
        max = getMax();
    }
}

public SeekBarNorm setPrecision(int precision){
    int max = (int)Math.pow(10, (2 + precision));
    super.setMax(max);
    normSeekBar = new NormInt(max);
    return this;
}

public SeekBarNorm setMinValue(float min){
    this.min = min;
    return this;
}

public SeekBarNorm setMaxValue(float max){
    this.max = max;
    return this;
}

public float getValue(){
    return normSeekBar.getNorm(getProgress()) * (max - min) + min;
}

public void setValue(float v){
    if(v < min){
DebugException.start().log("values " + v + " < min " + min).end();
    }
    if(v > max){
DebugException.start().log("values " + v + " > max " + max).end();
    }
    setProgress(normSeekBar.getRaw((v - min) / (max - min)));
}

public SeekBarNorm reverse(){
    setScaleX(-1);
    return this;
}

public SeekBarNorm setAlphaStyle(){
    float[] outR = new float[]{6, 6, 6, 6, 6, 6, 6, 6};
    ShapeDrawable thumb = new ShapeDrawable(new RoundRectShape(outR, null, null));
    thumb.setIntrinsicHeight(THUMB_HEIGHT);
    thumb.setIntrinsicWidth(THUMB_WIDTH);
    thumb.getPaint().setColor(THUMB_COLOR);
    setThumb(thumb);
    setGradientColor(BAR_HEIGHT, BAR_COLOR_LEFT, BAR_COLOR_RIGHT, STROKE_THICK, STROKE_COLOR);
    return this;
}

public void setGradientColor(int height, int leftColor, int rightColor, int strokeWidth, int strokeColor){
    GradientDrawable grad = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{leftColor, rightColor});
    grad.setStroke(strokeWidth, strokeColor);
    grad.setCornerRadius(height / 4.0f);
    grad.setSize(0, height);
    setProgressDrawable(grad);
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

}
