/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.layout_wrapper;

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

import com.tezov.lib_java_android.wrapperAnonymous.AnimatorListenerW;

import android.animation.Animator;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

import com.tezov.lib_java_android.R;
import com.tezov.lib_java_android.ui.misc.AttributeReader;
import com.tezov.lib_java_android.ui.misc.UtilsAnimator;

public class GlassAnimInOut extends FrameLayout{
private final static long ANIM_IN_DURATION_DEFAULT_ms = 250;
private final static long ANIM_OUT_DURATION_DEFAULT_ms = 250;
private final int[] ATTRS = R.styleable.GlassAnimInOut_lib;
private Animator animIn = null;
private Animator animOut = null;
private Step step = Step.IN;

public GlassAnimInOut(android.content.Context context){
    super(context);
    init(context, null, -1, -1);
}
public GlassAnimInOut(android.content.Context context, @Nullable AttributeSet attrs){
    super(context, attrs);
    init(context, attrs, -1, -1);
}
public GlassAnimInOut(android.content.Context context, @Nullable AttributeSet attrs, int defStyleAttr){
    super(context, attrs, defStyleAttr);
    init(context, attrs, defStyleAttr, -1);
}
public GlassAnimInOut(android.content.Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes){
    super(context, attrs, defStyleAttr, defStyleRes);
    init(context, attrs, defStyleAttr, defStyleRes);
}

private enum Step{
    UNKNOWN, IN, IN_RUNNING, OUT, OUT_RUNNING
}
private void init(android.content.Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes){
    if(attrs == null){
        return;
    }
    AttributeReader attributeReader = new AttributeReader().parse(context, ATTRS, attrs);
    Integer animInResourceId = attributeReader.getReference(R.styleable.GlassAnimInOut_lib_anim_in);
    if(animInResourceId == null){
        animInResourceId = R.animator.fade_in_015_100;
    }
    animIn = UtilsAnimator.getAnimator(animInResourceId);
    Long animInDuration = attributeReader.asLong(R.styleable.GlassAnimInOut_lib_anim_in_duration);
    if(animInDuration == null){
        animInDuration = ANIM_IN_DURATION_DEFAULT_ms;
    }
    animIn.setDuration(animInDuration);
    Long animInStartDelay = attributeReader.asLong(R.styleable.GlassAnimInOut_lib_anim_in_start_delay);
    if(animInStartDelay != null){
        animIn.setStartDelay(animInStartDelay);
    }
    Integer animOutResourceId = attributeReader.getReference(R.styleable.GlassAnimInOut_lib_anim_out);
    if(animOutResourceId == null){
        animOutResourceId = R.animator.fade_out_100_015;
    }
    animOut = UtilsAnimator.getAnimator(animOutResourceId);
    Long animOutDuration = attributeReader.asLong(R.styleable.GlassAnimInOut_lib_anim_out_duration);
    if(animOutDuration == null){
        animOutDuration = ANIM_OUT_DURATION_DEFAULT_ms;
    }
    animOut.setDuration(animOutDuration);
    Long animOutStartDelay = attributeReader.asLong(R.styleable.GlassAnimInOut_lib_anim_out_start_delay);
    if(animOutStartDelay != null){
        animOut.setStartDelay(animOutStartDelay);
    }
}

@Override
protected void onAttachedToWindow(){
    super.onAttachedToWindow();
    initAnimIn();
    initAnimOut();
}
@Override
protected void onDetachedFromWindow(){
    super.onDetachedFromWindow();
    if((animOut != null)&&(step == Step.OUT_RUNNING)){
        animOut.cancel();
    }
    if((animIn != null)&&(step == Step.IN_RUNNING)){
        animIn.cancel();
    }
}

public GlassAnimInOut setAnimIn(Animator animIn){
    this.animIn = animIn;
    initAnimIn();
    return this;
}
private void initAnimIn(){
    if((animIn != null) && (getChildCount() > 0)){
        View view = getChildAt(0);
        animIn.setTarget(view);
        animIn.addListener(new AnimatorListenerW(){
            @Override
            public void onAnimationStart(Animator animator){
                step = Step.IN_RUNNING;
            }
            @Override
            public void onAnimationEnd(Animator animator){
                step = Step.IN;
            }
            @Override
            public void onAnimationCancel(Animator animation){
                step = Step.UNKNOWN;
            }
        });
    }
}
public GlassAnimInOut setAnimOut(Animator animOut){
    this.animOut = animOut;
    return this;
}
private void initAnimOut(){
    if((animOut != null) && (getChildCount() > 0)){
        View view = getChildAt(0);
        animOut.setTarget(view);
        animOut.addListener(new AnimatorListenerW(){
            @Override
            public void onAnimationStart(Animator animator){
                step = Step.OUT_RUNNING;
            }
            @Override
            public void onAnimationEnd(Animator animator){
                step = Step.OUT;
            }
            @Override
            public void onAnimationCancel(Animator animation){
                step = Step.UNKNOWN;
            }
        });
    }
}
public boolean isRunning(){
    return (step == Step.IN_RUNNING) || (step == Step.OUT_RUNNING);
}

public void startOut(){
    if((step != Step.OUT)&&(step != Step.OUT_RUNNING)){
        if((animIn != null)&&(step == Step.IN_RUNNING)){
            animIn.end();
        }
        if(animOut != null){
            animOut.start();
        }
    }
}
public boolean isOut(){
    return (step == Step.OUT);
}
public boolean isRunningOut(){
    return (step == Step.OUT_RUNNING);
}

public void startIn(){
    if((step != Step.IN)&&(step != Step.IN_RUNNING)){
        if((animOut != null)&&(step == Step.OUT_RUNNING)){
            animOut.end();
        }
        if(animIn != null){
            animIn.start();
        }
    }
}
public boolean isIn(){
    return (step == Step.IN);
}
public boolean isRunningIn(){
    return (step == Step.IN_RUNNING);
}

}
