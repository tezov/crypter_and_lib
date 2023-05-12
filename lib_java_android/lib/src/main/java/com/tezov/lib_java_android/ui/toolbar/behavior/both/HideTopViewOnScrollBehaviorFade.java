
/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.toolbar.behavior.both;

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
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior;
import androidx.core.view.ViewCompat;

import com.google.android.material.animation.AnimationUtils;

public class HideTopViewOnScrollBehaviorFade<V extends View> extends Behavior<V> {

  protected static final int ENTER_ANIMATION_DURATION = 225;
  protected static final int EXIT_ANIMATION_DURATION = 175;

  private static final int STATE_FADE_IN = 1;
  private static final int STATE_FADE_OUT = 2;


  private int currentState = STATE_FADE_IN;
  @Nullable private ViewPropertyAnimator currentAnimator;

  public HideTopViewOnScrollBehaviorFade() {}

  public HideTopViewOnScrollBehaviorFade(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override
  public boolean onStartNestedScroll(
      @NonNull CoordinatorLayout coordinatorLayout,
      @NonNull V child,
      @NonNull View directTargetChild,
      @NonNull View target,
      int nestedScrollAxes,
      int type) {
    return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL;
  }

  @Override
  public void onNestedScroll(
      CoordinatorLayout coordinatorLayout,
      @NonNull V child,
      @NonNull View target,
      int dxConsumed,
      int dyConsumed,
      int dxUnconsumed,
      int dyUnconsumed,
      int type,
      @NonNull int[] consumed) {
    if (dyConsumed > 0) {
      fadeOut(child);
    } else if (dyConsumed < 0) {
      fadeIn(child);
    }
  }

  public void fadeIn(@NonNull V child) {
    if (currentState == STATE_FADE_IN) {
      return;
    }
    if (currentAnimator != null) {
      currentAnimator.cancel();
      child.clearAnimation();
    }
    currentState = STATE_FADE_IN;
    animateChildTo(child, 1.0f, ENTER_ANIMATION_DURATION, AnimationUtils.LINEAR_OUT_SLOW_IN_INTERPOLATOR);
  }

  public void fadeOut(@NonNull V child) {
    if (currentState == STATE_FADE_OUT) {
      return;
    }
    if (currentAnimator != null) {
      currentAnimator.cancel();
      child.clearAnimation();
    }
    currentState = STATE_FADE_OUT;
    animateChildTo(
        child,
        0.0f,
        EXIT_ANIMATION_DURATION,
        AnimationUtils.FAST_OUT_LINEAR_IN_INTERPOLATOR);
  }

  private void animateChildTo(
      @NonNull V child, float targetY, long duration, TimeInterpolator interpolator) {
    currentAnimator =
        child
            .animate()
            .alpha(targetY)
            .setInterpolator(interpolator)
            .setDuration(duration)
            .setListener(
                new AnimatorListenerAdapter() {
                  @Override
                  public void onAnimationEnd(Animator animation) {
                    currentAnimator = null;
                  }
                });
  }
}
