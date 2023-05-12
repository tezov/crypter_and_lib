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
import static com.tezov.lib_java_android.ui.misc.TransitionManagerAnimator.Name.FADE;
import static com.tezov.lib_java_android.ui.misc.TransitionManagerAnimator.Name.FLIP_DOWN;
import static com.tezov.lib_java_android.ui.misc.TransitionManagerAnimator.Name.FLIP_LEFT;
import static com.tezov.lib_java_android.ui.misc.TransitionManagerAnimator.Name.FLIP_RIGHT;
import static com.tezov.lib_java_android.ui.misc.TransitionManagerAnimator.Name.FLIP_UP;
import static com.tezov.lib_java_android.ui.misc.TransitionManagerAnimator.Name.SLIDE_DOWN;
import static com.tezov.lib_java_android.ui.misc.TransitionManagerAnimator.Name.SLIDE_LEFT;
import static com.tezov.lib_java_android.ui.misc.TransitionManagerAnimator.Name.SLIDE_RIGHT;
import static com.tezov.lib_java_android.ui.misc.TransitionManagerAnimator.Name.SLIDE_UP;

import android.animation.Animator;
import android.view.View;

import com.tezov.lib_java_android.R;
import com.tezov.lib_java.type.ExtremaAverage;
import com.tezov.lib_java.wrapperAnonymous.BiFunctionW;
import com.tezov.lib_java.wrapperAnonymous.FunctionW;

import java.util.Arrays;
import java.util.List;

import java9.util.stream.StreamSupport;

public class TransitionManagerAnimator extends TransitionManager<Animator, TransitionManagerAnimator.Transitions>{

public TransitionManagerAnimator(){
    addDefaultAnimator();
}
private void addDefaultAnimator(){
    add(FADE, R.animator.fade_in, R.animator.fade_out);
    add(FLIP_LEFT, R.animator.flip_right_in, R.animator.flip_left_out, R.animator.flip_right_out, R.animator.flip_left_in);
    add(FLIP_RIGHT, R.animator.flip_left_in, R.animator.flip_right_out, R.animator.flip_left_out, R.animator.flip_right_in);
    add(FLIP_UP, R.animator.flip_down_in, R.animator.flip_up_out, R.animator.flip_down_out, R.animator.flip_up_in);
    add(FLIP_DOWN, R.animator.flip_up_in, R.animator.flip_down_out, R.animator.flip_up_out, R.animator.flip_down_in);
    add(SLIDE_LEFT, R.animator.slide_right_in, R.animator.slide_left_out, R.animator.slide_right_out, R.animator.slide_left_in);
    add(SLIDE_RIGHT, R.animator.slide_left_in, R.animator.slide_right_out, R.animator.slide_left_out, R.animator.slide_right_in);
    add(SLIDE_UP, R.animator.slide_down_in, R.animator.slide_up_out, R.animator.slide_down_out, R.animator.slide_down_in);
    add(SLIDE_DOWN, R.animator.slide_up_in, R.animator.slide_down_out, R.animator.slide_up_out, R.animator.slide_down_in);
}

@Override
protected Transitions newTransition(int enter, int exit, int enterBack, int exitBack){
    return new Transitions(enter, exit, enterBack, exitBack);
}
@Override
public long getDuration(TransitionManager.Name.Is name){
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
    Is FLIP_UP = new Is("FLIP_UP");
    Is FLIP_DOWN = new Is("FLIP_DOWN");
    Is FLIP_LEFT = new Is("FLIP_LEFT");
    Is FLIP_RIGHT = new Is("FLIP_RIGHT");

}

public static class Transitions extends TransitionManager.Transitions<Animator>{
    public Transitions(int enter, int exit, int enterBack, int exitBack){
        super(enter, exit, enterBack, exitBack);
    }
    public Transitions(int enter, int exit){
        super(enter, exit);
    }
    @Override
    public Animator getExitFor(View view){
        return UtilsAnimator.getAnimator(view, exit);
    }
    @Override
    public Animator getExitBackFor(View view){
        return UtilsAnimator.getAnimator(view, exitBack);
    }
    @Override
    public Animator getEnterFor(View view){
        return UtilsAnimator.getAnimator(view, enter);
    }
    @Override
    public Animator getEnterBackFor(View view){
        return UtilsAnimator.getAnimator(view, enterBack);
    }

}

}
