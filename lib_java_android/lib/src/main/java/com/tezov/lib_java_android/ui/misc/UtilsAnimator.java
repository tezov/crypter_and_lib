/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.misc;

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
import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.Transformation;

import com.tezov.lib_java_android.application.AppContext;
import com.tezov.lib_java_android.wrapperAnonymous.AnimatorListenerW;

import java.util.ArrayList;

public class UtilsAnimator{


public static Animator getAnimator(View view, int animatorResource){
    Animator animator = getAnimator(animatorResource);
    if(view != null){
        animator.setTarget(view);
    }
    return animator;
}
public static Animator getAnimator(int animatorResource){
    return AnimatorInflater.loadAnimator(AppContext.get(), animatorResource);
}
public static long getTotalDuration(Animator animator){
    if(animator instanceof AnimatorSet){
        AnimatorSet animatorSet = (AnimatorSet)animator;
        long totalDuration = 0;
        for(Animator a: animatorSet.getChildAnimations()){
            long duration = a.getDuration();
            if(duration != -1){
                totalDuration += duration;
            }
        }
        return totalDuration;
    } else {
        return animator.getDuration();
    }

}
public Animation wrap(Animator animator, View view){
    return new AnimatorWrapper(animator, view);
}
public static class AnimatorWrapper extends Animation{
    private final Animator animator;
    private boolean hasStarted = false;
    private boolean hasEnded = false;
    private AnimationListener listener = null;
    public AnimatorWrapper(Animator animator, View view){
        this.animator = animator;
        animator.setTarget(view);
        animator.addListener(new AnimatorListenerW(){
            @Override
            public void onAnimationStart(Animator animator){
                hasStarted = true;
                if(listener != null){
                    listener.onAnimationStart(me());
                }
            }
            @Override
            public void onAnimationEnd(Animator animator){
                hasEnded = true;
                if(listener != null){
                    listener.onAnimationEnd(me());
                }
            }
            @Override
            public void onAnimationRepeat(Animator animator){
                if(listener != null){
                    listener.onAnimationRepeat(me());
                }
            }
        });
    }
    private AnimatorWrapper me(){
        return this;
    }
    @Override
    public boolean hasStarted(){
        return hasStarted;
    }
    @Override
    public void start(){
        hasEnded = false;
        animator.start();
    }
    @Override
    public void startNow(){
        start();
    }
    @Override
    public long getStartTime(){
        return animator.getStartDelay();
    }
    @Override
    public void setStartTime(long startTimeMillis){
        animator.setStartDelay(startTimeMillis);
    }
    @Override
    public long getDuration(){
        return animator.getDuration();
    }
    @Override
    public void setDuration(long durationMillis){
        animator.setDuration(durationMillis);
    }
    @Override
    public long computeDurationHint(){
        return UtilsAnimator.getTotalDuration(animator);
    }
    @Override
    public boolean hasEnded(){
        return hasEnded;
    }
    @Override
    public void reset(){
        hasStarted = false;
        hasEnded = false;
    }
    @Override
    public void cancel(){
        animator.cancel();
    }
    @Override
    public void setAnimationListener(AnimationListener listener){
        this.listener = listener;
    }
    @Override
    public int getRepeatMode(){
        if(animator instanceof AnimatorSet){
            ArrayList<Animator> animators = ((AnimatorSet)animator).getChildAnimations();
            if(animators.size() >= 1){
                return ((ValueAnimator)animators.get(0)).getRepeatMode();
            }
        } else if(animator instanceof ValueAnimator){
            return ((ValueAnimator)animator).getRepeatMode();
        }
        return Animation.RESTART;
    }
    @Override
    public void setRepeatMode(int repeatMode){
        if(animator instanceof AnimatorSet){
            ArrayList<Animator> animators = ((AnimatorSet)animator).getChildAnimations();
            if(animators.size() >= 1){
                ((ValueAnimator)animators.get(0)).setRepeatMode(repeatMode);
            }
        } else if(animator instanceof ValueAnimator){
            ((ValueAnimator)animator).setRepeatMode(repeatMode);
        }
    }
    @Override
    public int getRepeatCount(){
        if(animator instanceof AnimatorSet){
            ArrayList<Animator> animators = ((AnimatorSet)animator).getChildAnimations();
            if(animators.size() >= 1){
                return ((ValueAnimator)animators.get(0)).getRepeatCount();
            }
        } else if(animator instanceof ValueAnimator){
            return ((ValueAnimator)animator).getRepeatCount();
        }
        return 0;
    }
    @Override
    public void setRepeatCount(int repeatCount){
        if(animator instanceof AnimatorSet){
            ArrayList<Animator> animators = ((AnimatorSet)animator).getChildAnimations();
            if(animators.size() >= 1){
                ((ValueAnimator)animators.get(0)).setRepeatCount(repeatCount);
            }
        } else if(animator instanceof ValueAnimator){
            ((ValueAnimator)animator).setRepeatCount(repeatCount);
        }
    }
    @Override
    public void setInterpolator(android.content.Context context, int resID){
        setInterpolator(AnimationUtils.loadInterpolator(context, resID));
    }
    @Override
    public Interpolator getInterpolator(){
        return (Interpolator)animator.getInterpolator();
    }
    @Override
    public void setInterpolator(Interpolator i){
        animator.setInterpolator(i);
    }
    @Override
    protected Animation clone() throws CloneNotSupportedException{
        throw new CloneNotSupportedException();
    }

    //USELESS
    @Override
    public void scaleCurrentDuration(float scale){

    }
    @Override
    public long getStartOffset(){
        return 0;
    }
    @Override
    public void setStartOffset(long startOffset){

    }
    @Override
    public boolean isInitialized(){
        return true;
    }
    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight){

    }
    @Override
    public boolean isFillEnabled(){
        return true;
    }
    @Override
    public void setFillEnabled(boolean fillEnabled){

    }
    @Override
    public void restrictDuration(long durationMillis){

    }
    @Override
    protected float getScaleFactor(){
        return 1.0f;
    }
    @Override
    public boolean getFillBefore(){
        return false;
    }
    @Override
    public void setFillBefore(boolean fillBefore){

    }
    @Override
    public boolean getFillAfter(){
        return true;
    }
    @Override
    public void setFillAfter(boolean fillAfter){

    }
    @Override
    public int getZAdjustment(){
        return 0;
    }
    @Override
    public void setZAdjustment(int zAdjustment){

    }
    @Override
    public int getBackgroundColor(){
        return 0x00000000;
    }
    @Override
    public void setBackgroundColor(int bg){

    }
    @Override
    public boolean getDetachWallpaper(){
        return false;
    }
    @Override
    public void setDetachWallpaper(boolean detachWallpaper){

    }
    @Override
    public boolean willChangeTransformationMatrix(){
        return false;
    }
    @Override
    public boolean willChangeBounds(){
        return false;
    }
    @Override
    protected void ensureInterpolator(){

    }
    @Override
    public boolean getTransformation(long currentTime, Transformation outTransformation){
        return false;
    }
    @Override
    public boolean getTransformation(long currentTime, Transformation outTransformation, float scale){
        return false;
    }
    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t){

    }
    @Override
    protected float resolveSize(int type, float value, int size, int parentSize){
        return value;
    }

}

}
