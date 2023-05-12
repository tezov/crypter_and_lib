/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.recycler;

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

import com.tezov.lib_java_android.application.AppContext;
import com.tezov.lib_java_android.ui.misc.TransitionManagerAnimator;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.view.View;
import android.view.animation.Animation;

import androidx.recyclerview.widget.RecyclerView;

import com.tezov.lib_java.async.notifier.Notifier;
import com.tezov.lib_java.async.notifier.observable.ObservableEvent;
import com.tezov.lib_java.async.notifier.observer.event.ObserverEvent;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.defEnum.EnumBase;
import com.tezov.lib_java_android.wrapperAnonymous.AnimationListenerW;
import com.tezov.lib_java_android.wrapperAnonymous.AnimatorListenerW;
import com.tezov.lib_java_android.ui.misc.TransitionManager;

import java.util.ArrayList;
import java.util.List;

import static com.tezov.lib_java_android.ui.recycler.RecyclerListItemAnimator.State.INSERT;
import static com.tezov.lib_java_android.ui.recycler.RecyclerListItemAnimator.State.MOVE_DOWN;
import static com.tezov.lib_java_android.ui.recycler.RecyclerListItemAnimator.State.MOVE_LEFT;
import static com.tezov.lib_java_android.ui.recycler.RecyclerListItemAnimator.State.MOVE_RIGHT;
import static com.tezov.lib_java_android.ui.recycler.RecyclerListItemAnimator.State.MOVE_UP;
import static com.tezov.lib_java_android.ui.recycler.RecyclerListItemAnimator.State.REMOVE;
import static com.tezov.lib_java_android.ui.recycler.RecyclerListItemAnimator.State.UPDATE;

//TODO BUGGY
public class RecyclerListItemAnimator extends DefaultItemAnimator{
private final static int CAMERA_SCALE_FACTOR = 8000;
private final List<TransitionDetails> transitions;
private final List<AnimatorDetails> animations;
private final Notifier<State.Is> notifier;

public RecyclerListItemAnimator(){
DebugTrack.start().create(this).end();
    transitions = new ArrayList<>();
    animations = new ArrayList<>();
    notifier = new Notifier<>(new ObservableEvent<State.Is, RecyclerView.ViewHolder>(), false);
}

//public void computeDuration(){
//    List<Long> insert = new ArrayList<>();
//    List<Long> update = new ArrayList<>();
//    List<Long> remove = new ArrayList<>();
//    List<Long> move = new ArrayList<>();
//
//    for(TransitionDetails t: transitions){
//        TransitionAnimationManager transitionManager = Application.animationManager();
//        long duration = transitionManager.getDuration(ANIMATOR, t.transition);
//        if(t.state == INSERT){
//            insert.add(duration);
//        } else if(t.state == UPDATE){
//            update.add(duration);
//        } else if(t.state == REMOVE){
//            remove.add(duration);
//        } else if(t.state == MOVE_UP){
//            move.add(duration);
//        } else if(t.state == MOVE_DOWN){
//            move.add(duration);
//        } else if(t.state == MOVE_LEFT){
//            move.add(duration);
//        } else if(t.state == MOVE_RIGHT){
//            move.add(duration);
//        }
//    }
//    long count = StreamSupport.stream(insert).distinct().count();
//    if(count == 1){
//        setAddDuration(insert.get(0));
//    } else if(count >= 1){
//DebugException.pop().produce("insert duration are not the same for all animation").log().pop();
//
//    }
//
//
//    count = StreamSupport.stream(update).distinct().count();
//    if(count == 1){
//        setChangeDuration(update.get(0));
//    } else if(count >= 1){
//DebugException.pop().produce("update duration are not the same for all animation").log().pop();
//    }
//
//
//    count = StreamSupport.stream(remove).distinct().count();
//    if(count == 1){
//        setRemoveDuration(remove.get(0));
//    } else if(count >= 1){
//DebugException.pop().produce("remove duration are not the same for all animation").log().pop();
//
//    }
//
//
//    count = StreamSupport.stream(move).distinct().count();
//    if(count == 1){
//        setMoveDuration(move.get(0));
//    } else if(count >= 1){
//DebugException.pop().produce("move duration are not the same for all animation").log().pop();
//    }
//
//
//}

@Override
public boolean animateAdd(RecyclerView.ViewHolder newHolder){

DebugLog.start().send(this, "ADDITION position " + newHolder.getLayoutPosition()).end();

    AnimatorDetails animatorDetails = obtainAnimator(INSERT, newHolder);
    if(animatorDetails == null){
        return super.animateAdd(newHolder);
    }
    resetAnimation(newHolder);
    newHolder.itemView.setAlpha(0);
    animations.add(animatorDetails.setHolder(newHolder, true));
    mPendingAdditions.add(newHolder);
    return true;
}

public void attach(RecyclerList recycler){
    recycler.setLayoutAnimationListener(new AnimationListenerW(){
        @Override
        public void onAnimationStart(Animation animation){
            for(int childCount = recycler.getChildCount(), i = 0; i < childCount; i++){
                recycler.getChildAt(i).setAlpha(1);
            }
        }
    });
}

protected AnimatorDetails findAnimationDetails(RecyclerView.ViewHolder holder){
    for(AnimatorDetails animationDetails: animations){
        if(animationDetails.holder == holder){
            return animationDetails;
        }
    }
    return null;
}

protected void removeAnimationDetails(AnimatorDetails animationDetails){
    animations.remove(animationDetails);
}

protected void addTransition(State.Is state, RecyclerListRowBinder.ViewType.Is current, RecyclerListRowBinder.ViewType.Is target, TransitionManager.Name.Is transition){
    TransitionDetails transitionDetails = new TransitionDetails(state, current, target, transition);
    this.transitions.add(transitionDetails);
}

public void addTransitionForUpdate(RecyclerListRowBinder.ViewType.Is current, RecyclerListRowBinder.ViewType.Is target, TransitionManager.Name.Is transition){
    addTransition(UPDATE, current, target, transition);
}

public void addTransitionForRemove(RecyclerListRowBinder.ViewType.Is current, TransitionManager.Name.Is transitionRemove){
    addTransition(REMOVE, current, null, transitionRemove);
}

public void addTransitionForInsert(RecyclerListRowBinder.ViewType.Is target, TransitionManager.Name.Is transition){
    addTransition(INSERT, null, target, transition);
}

public void addTransitionForMove(State.Is state, RecyclerListRowBinder.ViewType.Is current, TransitionManager.Name.Is transition){
    addTransition(state, current, null, transition);
}

protected AnimatorDetails obtainAnimator(State.Is state, RecyclerView.ViewHolder holder, RecyclerView.ViewHolder newHolder){
    if(transitions == null){
        return null;
    }
    int currentOrdinal = holder != null ? holder.getItemViewType() : RecyclerListRowBinder.ViewType.Is.NULL().ordinal();
    int targetOrdinal = newHolder != null ? newHolder.getItemViewType() : RecyclerListRowBinder.ViewType.Is.NULL().ordinal();

    // ** explicit current and target
//    for(Transitions d: transitions){
//        if(d.state != state){
//            continue;
//        }
//        if((d.current == null) || (d.target == null)){
//            continue;
//        }
//        //forward
//        if((d.current.ordinal() == currentOrdinal) && (d.target.ordinal() == targetOrdinal)){
//            return new AnimatorDetails(d, true);
//        }
//        //backward
//        if((d.current.ordinal() == targetOrdinal) && (d.target.ordinal() == currentOrdinal)){
//            return new AnimatorDetails(d, false);
//        }
//    }

    // ** implicit current or target
//    for(Transitions d: transitions){
//        if(d.state != state){
//            continue;
//        }
//        if((d.current == null) && (d.target == null)){
//            continue;
//        }
//        //forward
//        if(((d.current == null) || (d.current.ordinal() == currentOrdinal)) && ((d.target == null) || (d.target.ordinal() == targetOrdinal))){
//            return new AnimatorDetails(d, true);
//        }
//        //backward
//        if(((d.current == null) || (d.current.ordinal() == targetOrdinal)) && ((d.target == null) || (d.target.ordinal() == currentOrdinal))){
//            return new AnimatorDetails(d, false);
//        }
//    }
    return null;
}

protected AnimatorDetails obtainAnimator(State.Is state, RecyclerView.ViewHolder holder){
    if(transitions == null){
        return null;
    }
    int currentOrdinal = holder != null ? holder.getItemViewType() : RecyclerListRowBinder.ViewType.Is.NULL().ordinal();

    // ** explicit current
//    for(Transitions d: transitions){
//        if(d.state != state){
//            continue;
//        }
//        if((d.current != null) && (d.current.ordinal() == currentOrdinal)){
//            return new AnimatorDetails(d, true);
//        }
//    }

    // ** implicit current
//    for(Transitions d: transitions){
//        if(d.state != state){
//            continue;
//        }
//        if(d.current == null){
//            return new AnimatorDetails(d, true);
//        }
//    }
    return null;
}

public <T> Notifier.Subscription observe(ObserverEvent<State.Is, RecyclerListRowHolder<T>> observer){
    return notifier.register(observer);
}

public void unObserve(Object owner){
    notifier.unregister(owner);
}

public void unObserveAll(){
    notifier.unregisterAll();
}

protected void post(State.Is state, RecyclerView.ViewHolder holder){
    ObservableEvent<State.Is, RecyclerView.ViewHolder>.Access access = notifier.obtainAccess(this, state);
    access.setValue(holder);
}

@Override
protected void animateAddImpl(RecyclerView.ViewHolder newHolder){

DebugLog.start().send(this, "ADDITION ANIMATE position " + newHolder.getLayoutPosition()).end();

    AnimatorDetails animationDetails = findAnimationDetails(newHolder);
    if(animationDetails == null){
        super.animateAddImpl(newHolder);
        return;
    }

    View newView = newHolder.itemView;
    mAddAnimations.add(newHolder);
    float scale = AppContext.getActivity().getResources().getDisplayMetrics().density;
    newView.setCameraDistance(CAMERA_SCALE_FACTOR * scale);
    animationDetails.addListener(new AnimatorListenerW(){
        @Override
        public void onAnimationStart(Animator animator){
            dispatchAddStarting(newHolder);
        }

        @Override
        public void onAnimationEnd(Animator animator){
            clean();
            dispatchFinishedWhenDone();
        }

        @Override
        public void onAnimationCancel(Animator animation){
            clean();
        }

        public void clean(){
            removeAnimationDetails(animationDetails);
            dispatchAddFinished(newHolder);
            mAddAnimations.remove(newHolder);
        }
    });
    animationDetails.start();
}

@Override
public boolean animateRemove(RecyclerView.ViewHolder holder){

DebugLog.start().send(this, "REMOVE position " + holder.getOldPosition()).end();

    AnimatorDetails animatorDetails = obtainAnimator(REMOVE, holder);
    if(animatorDetails == null){
        return super.animateRemove(holder);
    }
    resetAnimation(holder);
    animations.add(animatorDetails.setHolder(holder, false));
    mPendingRemovals.add(holder);
    return true;
}

@Override
protected void animateRemoveImpl(RecyclerView.ViewHolder holder){

DebugLog.start().send(this, "REMOVE ANIMATE position " + holder.getOldPosition()).end();

    AnimatorDetails animationDetails = findAnimationDetails(holder);
    if(animationDetails == null){
        super.animateRemoveImpl(holder);
        return;
    }
    View view = holder.itemView;
    mRemoveAnimations.add(holder);
    float scale = AppContext.getActivity().getResources().getDisplayMetrics().density;
    view.setCameraDistance(CAMERA_SCALE_FACTOR * scale);
    animationDetails.addListener(new AnimatorListenerW(){
        @Override
        public void onAnimationStart(Animator animator){
            dispatchRemoveStarting(holder);
        }

        @Override
        public void onAnimationEnd(Animator animator){
            clean();
            dispatchFinishedWhenDone();
        }

        @Override
        public void onAnimationCancel(Animator animation){
            clean();
        }

        public void clean(){
            removeAnimationDetails(animationDetails);
            dispatchRemoveFinished(holder);
            mRemoveAnimations.remove(holder);
            resetView(holder);
            holder.itemView.setAlpha(0);
        }
    });
    animationDetails.start();
}

@Override
public boolean animateChange(RecyclerView.ViewHolder oldHolder, RecyclerView.ViewHolder newHolder, int fromX, int fromY, int toX, int toY){

DebugLog.start().send(this, "UPDATE position " + oldHolder.getLayoutPosition()).end();

    if(oldHolder == newHolder){
        return animateMove(oldHolder, fromX, fromY, toX, toY);
    }

    AnimatorDetails animatorDetails = obtainAnimator(UPDATE, oldHolder, newHolder);
    if(animatorDetails == null){
        return super.animateChange(oldHolder, newHolder, fromX, fromY, toX, toY);
    }
    resetAnimation(oldHolder);
    animations.add(animatorDetails.setHolder(oldHolder, false));
    if(newHolder != null){
        resetAnimation(newHolder);
        newHolder.itemView.setAlpha(0);
        animations.add(animatorDetails.copy().setHolder(newHolder, true));
    }

    final float prevTranslationX = oldHolder.itemView.getTranslationX();
    final float prevTranslationY = oldHolder.itemView.getTranslationY();
    int deltaX = (int)(toX - fromX - prevTranslationX);
    int deltaY = (int)(toY - fromY - prevTranslationY);
    // recover prev translation state after ending animation
    oldHolder.itemView.setTranslationX(prevTranslationX);
    oldHolder.itemView.setTranslationY(prevTranslationY);
    if(newHolder != null){
        // carry over translation values
        newHolder.itemView.setTranslationX(-deltaX);
        newHolder.itemView.setTranslationY(-deltaY);
    }
    mPendingChanges.add(new ChangeInfo(oldHolder, newHolder, fromX, fromY, toX, toY));
    return true;
}

@Override
protected void animateChangeImpl(ChangeInfo changeInfo){

DebugLog.start().send(this, "UPDATE ANIMATE position " + changeInfo.oldHolder.getLayoutPosition()).end();

    RecyclerView.ViewHolder holder = changeInfo.oldHolder;
    RecyclerView.ViewHolder newHolder = changeInfo.newHolder;

    AnimatorDetails animationDetailsView = findAnimationDetails(holder);
    if(animationDetailsView == null){
        super.animateChangeImpl(changeInfo);
        return;
    }

    View view = holder == null ? null : holder.itemView;
    if(view != null){
        float scale = AppContext.getActivity().getResources().getDisplayMetrics().density;
        view.setCameraDistance(CAMERA_SCALE_FACTOR * scale);
        mChangeAnimations.add(changeInfo.oldHolder);
        animationDetailsView.addListener(new AnimatorListenerW(){
            @Override
            public void onAnimationStart(Animator animator){
                dispatchChangeStarting(changeInfo.oldHolder, true);
            }

            @Override
            public void onAnimationEnd(Animator animator){
                clean();
                dispatchFinishedWhenDone();
                post(UPDATE, newHolder);
            }

            @Override
            public void onAnimationCancel(Animator animation){
                clean();
            }

            public void clean(){
                removeAnimationDetails(animationDetailsView);
                dispatchChangeFinished(changeInfo.oldHolder, true);
                mChangeAnimations.remove(changeInfo.oldHolder);
                resetView(holder);
                holder.itemView.setAlpha(0);
            }
        });
        animationDetailsView.start();
    }

    final View newView = newHolder != null ? newHolder.itemView : null;
    if(newView != null){
        AnimatorDetails animationDetailsNewView = findAnimationDetails(newHolder);
        float scale = AppContext.getActivity().getResources().getDisplayMetrics().density;
        newView.setCameraDistance(CAMERA_SCALE_FACTOR * scale);
        mChangeAnimations.add(changeInfo.newHolder);
        animationDetailsNewView.addListener(new AnimatorListenerW(){
            @Override
            public void onAnimationStart(Animator animator){
                dispatchChangeStarting(changeInfo.newHolder, false);
            }

            @Override
            public void onAnimationEnd(Animator animator){
                clean();
                dispatchFinishedWhenDone();
            }

            @Override
            public void onAnimationCancel(Animator animation){
                clean();
            }

            public void clean(){
                removeAnimationDetails(animationDetailsNewView);
                dispatchChangeFinished(changeInfo.newHolder, false);
                mChangeAnimations.remove(changeInfo.newHolder);
            }
        });
        animationDetailsNewView.start();
    }
}

@Override
public boolean animateMove(RecyclerView.ViewHolder holder, int fromX, int fromY, int toX, int toY){

DebugLog.start().send(this, "MOVE position " + holder.getLayoutPosition()).end();

    int deltaY = toY - fromY;
    int deltaX = toX - fromX;
    if((deltaY == 0) && (deltaX == 0)){
        dispatchMoveFinished(holder);
        return false;
    }
    boolean dispatch = true;
    resetAnimation(holder);
    if(deltaY != 0){
        AnimatorDetails animatorDetails;
        if(deltaY > 0){
            animatorDetails = obtainAnimator(MOVE_DOWN, holder);
        } else {
            animatorDetails = obtainAnimator(MOVE_UP, holder);
        }
        if(animatorDetails != null){
            animations.add(animatorDetails.setHolder(holder, true));
            mPendingMoves.add(new MoveInfo(holder, fromX, fromY, toX, toY));
            holder.itemView.setTranslationY(-deltaY);
            dispatch = false;
        }
    }
    if(deltaX != 0){
        AnimatorDetails animatorDetails;
        if(deltaX > 0){
            animatorDetails = obtainAnimator(MOVE_RIGHT, holder);
        } else {
            animatorDetails = obtainAnimator(MOVE_LEFT, holder);
        }
        if(animatorDetails != null){
            animations.add(animatorDetails.setHolder(holder, true));
            mPendingMoves.add(new MoveInfo(holder, fromX, fromY, toX, toY));
            holder.itemView.setTranslationY(-deltaX);
            dispatch = false;
        }
    }
    if(dispatch){
        return super.animateMove(holder, fromX, fromY, toX, toY);
    } else {
        return true;
    }
}

@Override
protected void animateMoveImpl(RecyclerView.ViewHolder holder, int fromX, int fromY, int toX, int toY){

DebugLog.start().send(this, "MOVE ANIMATE position " + holder.getLayoutPosition()).end();

    AnimatorDetails animationDetails = findAnimationDetails(holder);
    if(animationDetails == null){
        super.animateMoveImpl(holder, fromX, fromY, toX, toY);
        return;
    }
    View view = holder.itemView;
    mMoveAnimations.add(holder);
    float scale = AppContext.getActivity().getResources().getDisplayMetrics().density;
    view.setCameraDistance(CAMERA_SCALE_FACTOR * scale);
    animationDetails.addListener(new AnimatorListenerW(){
        @Override
        public void onAnimationStart(Animator animator){
            dispatchMoveStarting(holder);
        }

        @Override
        public void onAnimationEnd(Animator animator){
            clean();
            dispatchFinishedWhenDone();
        }

        @Override
        public void onAnimationCancel(Animator animation){
            clean();
        }

        public void clean(){
            removeAnimationDetails(animationDetails);
            dispatchMoveFinished(holder);
            mMoveAnimations.remove(holder);
        }
    });
    animationDetails.start();
}

protected void resetView(RecyclerView.ViewHolder holder){

DebugLog.start().send(this, "RESET VIEW").end();

    holder.itemView.setTranslationX(0);
    holder.itemView.setTranslationY(0);
    holder.itemView.setRotationX(0);
    holder.itemView.setRotationY(0);
}

protected boolean cancelAnimation(RecyclerView.ViewHolder holder){

DebugLog.start().send(this, "ON_CANCEL VIEW position " + holder.getLayoutPosition()).end();

    AnimatorDetails animationDetails = findAnimationDetails(holder);
    if(animationDetails != null){
        animationDetails.cancel();
        holder.itemView.animate().cancel();
        removeAnimationDetails(animationDetails);
        return true;
    }
    return false;
}

@Override
protected void resetAnimation(RecyclerView.ViewHolder holder){
    if(!cancelAnimation(holder)){
        super.resetAnimation(holder);
    }
}

@Override
public void endAnimation(RecyclerView.ViewHolder holder){
    if(!cancelAnimation(holder)){
        super.endAnimation(holder);
    }
}

@Override
void cancelAll(List<RecyclerView.ViewHolder> holders){
    for(int size = holders.size() - 1, i = size; i >= 0; i--){
        RecyclerView.ViewHolder holder = holders.get(i);
        if(!cancelAnimation(holder)){
            endAnimation(holder);
        }
    }
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

public interface State{
    Is INSERT = new Is("INSERT");
    Is UPDATE = new Is("UPDATE");
    Is REMOVE = new Is("REMOVE");
    Is MOVE_UP = new Is("MOVE_UP");
    Is MOVE_DOWN = new Is("MOVE_DOWN");
    Is MOVE_LEFT = new Is("MOVE_LEFT");
    Is MOVE_RIGHT = new Is("MOVE_RIGHT");

    class Is extends EnumBase.Is{
        protected Is(String name){
            super(name);
        }

    }

}

protected static class TransitionDetails{
    public State.Is state;
    public RecyclerListRowBinder.ViewType.Is current;
    public RecyclerListRowBinder.ViewType.Is target;
    public TransitionManager.Name.Is transition;

    public TransitionDetails(State.Is state, RecyclerListRowBinder.ViewType.Is current, RecyclerListRowBinder.ViewType.Is target, TransitionManager.Name.Is transition){
DebugTrack.start().create(this).end();
        this.state = state;
        this.target = target;
        this.current = current;
        this.transition = transition;
    }

    public DebugString toDebugString(){
        DebugString sb = new DebugString();
        sb.append("target", target);
        sb.append("current", current);
        sb.append("transition", transition);
        return sb;
    }

    final public void toDebugLog(){
DebugLog.start().send(toDebugString()).end();
    }

    @Override
    protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
        super.finalize();
    }
}

protected class AnimatorDetails{
    boolean reverse = true;
    boolean direction;
    TransitionManagerAnimator.Transitions transitions;
    RecyclerView.ViewHolder holder = null;
    AnimatorSet animatorSet = null;
    protected AnimatorDetails(TransitionManagerAnimator.Transitions transitions, boolean direction){
DebugTrack.start().create(this).end();
        this.transitions = transitions;
        this.direction = direction;
    }
    protected AnimatorDetails setHolder(RecyclerView.ViewHolder holder, boolean reverse){
        this.holder = holder;
        this.reverse = reverse;
        return this;
    }
    protected void addListener(AnimatorListenerW listener){
        if(!reverse){
//            if(direction){
//                animatorSet = transitions.setAnimatorSetExitTo(holder.itemView);
//            } else {
//                animatorSet = transitions.setAnimatorSetEnterBackTo(holder.itemView);
//            }
        } else {
//            if(direction){
//                animatorSet = transitions.setAnimatorSetEnterTo(holder.itemView);
//            } else {
//                animatorSet = transitions.setAnimatorSetExitBackTo(holder.itemView);
//            }
        }
        animatorSet.addListener(listener);
    }
    protected AnimatorDetails copy(){
        return new AnimatorDetails(transitions, direction);
    }
    protected void start(){
        if(animatorSet != null){
            animatorSet.start();
        }
    }
    protected void cancel(){
        if(animatorSet != null){
            animatorSet.cancel();
        }
    }
    @Override
    protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
        super.finalize();
    }

}

}
