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
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import androidx.fragment.app.Fragment;
import static com.tezov.lib_java_android.ui.misc.TransitionManager.Name.FADE;
import static com.tezov.lib_java_android.ui.misc.TransitionManager.Name.SLIDE_DOWN;
import static com.tezov.lib_java_android.ui.misc.TransitionManager.Name.SLIDE_LEFT;
import static com.tezov.lib_java_android.ui.misc.TransitionManager.Name.SLIDE_RIGHT;
import static com.tezov.lib_java_android.ui.misc.TransitionManager.Name.SLIDE_UP;
import static com.tezov.lib_java_android.ui.misc.TransitionManagerAnimation.Name.SLIDE_OVER_LEFT;
import static com.tezov.lib_java_android.ui.misc.TransitionManagerAnimation.Name.SLIDE_OVER_RIGHT;

import android.view.View;
import android.view.animation.Animation;

import com.tezov.lib_java_android.R;
import com.tezov.lib_java.type.ExtremaAverage;
import com.tezov.lib_java.wrapperAnonymous.BiFunctionW;
import com.tezov.lib_java.wrapperAnonymous.FunctionW;

import java.util.Arrays;
import java.util.List;

import java9.util.stream.StreamSupport;

public class TransitionManagerAnimation extends TransitionManager<Animation, TransitionManagerAnimation.Transitions>{

public TransitionManagerAnimation(){
    addDefaultAnimation();
}
private void addDefaultAnimation(){
    add(SLIDE_RIGHT, R.anim.slide_left_in, R.anim.slide_right_out, R.anim.slide_left_out, R.anim.slide_right_in);
    add(SLIDE_OVER_RIGHT, R.anim.slide_over_left_in, R.anim.slide_over_right_out, R.anim.slide_over_left_out, R.anim.slide_over_right_in);
    add(SLIDE_LEFT, R.anim.slide_right_in, R.anim.slide_left_out, R.anim.slide_right_out, R.anim.slide_left_in);
    add(SLIDE_OVER_LEFT, R.anim.slide_over_right_in, R.anim.slide_over_left_out, R.anim.slide_over_right_out, R.anim.slide_over_left_in);
    add(SLIDE_DOWN, R.anim.slide_up_in, R.anim.slide_down_out, R.anim.slide_up_out, R.anim.slide_down_in);
    add(SLIDE_UP, R.anim.slide_down_in, R.anim.slide_up_out, R.anim.slide_down_out, R.anim.slide_up_in);
    add(FADE, R.anim.fade_in, R.anim.fade_out);
}
@Override
protected Transitions newTransition(int enter, int exit, int enterBack, int exitBack){
    return new Transitions(enter, exit, enterBack, exitBack);
}
@Override
public long getDuration(Name.Is name){
    Transitions t = get(name);
    if(t == null){
        return 0;
    }
    List<Long> durations = Arrays.asList(UtilsAnimator.getTotalDuration(UtilsAnimator.getAnimator(t.enter)), UtilsAnimator.getTotalDuration(UtilsAnimator.getAnimator(t.exit)),
            UtilsAnimator.getTotalDuration(UtilsAnimator.getAnimator(t.enterBack)), UtilsAnimator.getTotalDuration(UtilsAnimator.getAnimator(t.exitBack)));
    if(StreamSupport.stream(durations).distinct().count() == 1){
        return durations.get(0);
    } else {
        ExtremaAverage<Long> extrema = new ExtremaAverage<>(durations, new BiFunctionW<Long, Long, Long>(){
            @Override
            public Long apply(Long totalDuration, Long duration){
                if(totalDuration == null){
                    return duration;
                }
                return totalDuration + duration;
            }
        }, new FunctionW<Long, Long>(){
            @Override
            public Long apply(Long totalDuration){
                return totalDuration / durations.size();
            }
        });
        Long average = extrema.getAverage();
        if(average != null){
            return average;
        } else {
            return -1;
        }
    }
}

public interface Name extends TransitionManager.Name{
    Is SLIDE_OVER_LEFT = new Is("SLIDE_OVER_LEFT");
    Is SLIDE_OVER_RIGHT = new Is("SLIDE_OVER_RIGHT");

}

public static class Transitions extends TransitionManager.Transitions<Animation>{
    public Transitions(int enter, int exit, int enterBack, int exitBack){
        super(enter, exit, enterBack, exitBack);
    }
    public Transitions(int enter, int exit){
        super(enter, exit);
    }
    @Override
    public Animation getExitFor(View view){
        return UtilsAnimation.getAnimation(view, exit);
    }
    @Override
    public Animation getExitBackFor(View view){
        return UtilsAnimation.getAnimation(view, exitBack);
    }
    @Override
    public Animation getEnterFor(View view){
        return UtilsAnimation.getAnimation(view, enter);
    }
    @Override
    public Animation getEnterBackFor(View view){
        return UtilsAnimation.getAnimation(view, enterBack);
    }

}


}
