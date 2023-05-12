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

import com.tezov.lib_java_android.ui.misc.UtilsAnimator;
import com.tezov.lib_java_android.R;
import com.tezov.lib_java.toolbox.Compare;

import android.animation.Animator;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

import com.tezov.lib_java_android.ui.misc.AttributeReader;

public class GlassAnimInOutAuto extends FrameLayout{
private final static long ANIM_IN_DURATION_DEFAULT_ms = 250;
private final static long ANIM_OUT_DURATION_DEFAULT_ms = 2000;
private final static long ANIM_OUT_START_DELAI_DEFAULT_ms = 3000;
private final int[] ATTRS = com.tezov.lib_java_android.R.styleable.GlassAnimInOutAuto_lib;
private boolean isStarted = true;
private Animator animIn = null;
private Animator animOut = null;

public GlassAnimInOutAuto(android.content.Context context){
    super(context);
    init(context, null, -1, -1);
}
public GlassAnimInOutAuto(android.content.Context context, @Nullable AttributeSet attrs){
    super(context, attrs);
    init(context, attrs, -1, -1);
}
public GlassAnimInOutAuto(android.content.Context context, @Nullable AttributeSet attrs, int defStyleAttr){
    super(context, attrs, defStyleAttr);
    init(context, attrs, defStyleAttr, -1);
}
public GlassAnimInOutAuto(android.content.Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes){
    super(context, attrs, defStyleAttr, defStyleRes);
    init(context, attrs, defStyleAttr, defStyleRes);
}

private void init(android.content.Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes){
    if(attrs == null){
        return;
    }
    AttributeReader attributeReader = new AttributeReader().parse(context, ATTRS, attrs);
    Integer animInResourceId = attributeReader.getReference(com.tezov.lib_java_android.R.styleable.GlassAnimInOutAuto_lib_anim_in);
    if(animInResourceId == null){
        animInResourceId = R.animator.fade_in_015_100;
    }
    animIn = UtilsAnimator.getAnimator(animInResourceId);
    Long animInDuration = attributeReader.asLong(com.tezov.lib_java_android.R.styleable.GlassAnimInOutAuto_lib_anim_in_duration);
    if(animInDuration == null){
        animInDuration = ANIM_IN_DURATION_DEFAULT_ms;
    }
    animIn.setDuration(animInDuration);
    Long animInStartDelay = attributeReader.asLong(com.tezov.lib_java_android.R.styleable.GlassAnimInOutAuto_lib_anim_in_start_delay);
    if(animInStartDelay != null){
        animIn.setStartDelay(animInStartDelay);
    }
    Integer animOutResourceId = attributeReader.getReference(com.tezov.lib_java_android.R.styleable.GlassAnimInOutAuto_lib_anim_out);
    if(animOutResourceId == null){
        animOutResourceId = R.animator.fade_out_100_015;
    }
    animOut = UtilsAnimator.getAnimator(animOutResourceId);
    Long animOutDuration = attributeReader.asLong(com.tezov.lib_java_android.R.styleable.GlassAnimInOutAuto_lib_anim_out_duration);
    if(animOutDuration == null){
        animOutDuration = ANIM_OUT_DURATION_DEFAULT_ms;
    }
    animOut.setDuration(animOutDuration);
    Long animOutStartDelay = attributeReader.asLong(com.tezov.lib_java_android.R.styleable.GlassAnimInOutAuto_lib_anim_out_start_delay);
    if(animOutStartDelay == null){
        animOutStartDelay = ANIM_OUT_START_DELAI_DEFAULT_ms;
    }
    animOut.setStartDelay(animOutStartDelay);
    isStarted = Compare.isTrueOrNull(attributeReader.asBoolean(com.tezov.lib_java_android.R.styleable.GlassAnimInOutAuto_lib_autostart));
}
@Override
protected void onAttachedToWindow(){
    super.onAttachedToWindow();
    if((animIn != null) && (animOut != null) && (getChildCount() > 0)){
        View view = getChildAt(0);
        animIn.setTarget(view);
        animIn.addListener(new com.tezov.lib_java_android.wrapperAnonymous.AnimatorListenerW(){
            @Override
            public void onAnimationEnd(Animator animator){
                if(isStarted){
                    animOut.start();
                }
            }
        });
        animOut.setTarget(view);
        tick();
    }
}
@Override
protected void onDetachedFromWindow(){
    super.onDetachedFromWindow();
    if(animOut != null){
        animOut.cancel();
    }
    if(animIn != null){
        animIn.cancel();
    }
}
@Override
public boolean onInterceptTouchEvent(MotionEvent event){
    int action = event.getActionMasked();
    if(action == MotionEvent.ACTION_DOWN){
        tick();
    }
    return super.onInterceptTouchEvent(event);
}

public void tick(){
    if(isStarted){
        if(!animOut.isRunning()){
            animOut.cancel();
            animOut.start();
        } else {
            animOut.cancel();
            animIn.start();
        }
    }
}
public void start(){
    isStarted = true;
    tick();
}
public void stop(){
    isStarted = false;
    if(animOut.isRunning()){
        animOut.cancel();
        animIn.start();
    }
    else{
        animOut.cancel();
    }
}

}
