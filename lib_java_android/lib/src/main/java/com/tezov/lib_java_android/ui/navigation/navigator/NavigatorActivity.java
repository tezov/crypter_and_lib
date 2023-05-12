/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.navigation.navigator;

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugTrack;
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

import static com.tezov.lib_java_android.type.android.LifecycleEvent.Event.PAUSE;
import static com.tezov.lib_java_android.ui.misc.TransitionManager.Name;
import static com.tezov.lib_java_android.ui.navigation.NavigatorManager.NavigatorKey.ACTIVITY;

import android.content.Intent;

import com.tezov.lib_java_android.application.Application;
import com.tezov.lib_java_android.application.AppContext;
import com.tezov.lib_java.toolbox.Iterable;
import com.tezov.lib_java.toolbox.Reflection;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java_android.type.android.LifecycleEvent;
import com.tezov.lib_java.type.runnable.RunnableSubscription;
import com.tezov.lib_java_android.ui.activity.ActivityNavigable;
import com.tezov.lib_java_android.ui.dialog.DialogNavigable;
import com.tezov.lib_java_android.ui.fragment.FragmentNavigable;
import com.tezov.lib_java_android.ui.misc.TransitionManagerAnimation;
import com.tezov.lib_java_android.ui.navigation.NavigationOption;
import com.tezov.lib_java_android.ui.navigation.NavigatorManager;
import com.tezov.lib_java_android.ui.navigation.defNavigable;
import com.tezov.lib_java_android.ui.navigation.stack.StackEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class NavigatorActivity extends Navigator{

public NavigatorActivity(NavigatorManager.NavigatorKey.Is navigatorKey){
    super(navigatorKey);
}

@Override
public boolean navigate(NavigateType type, StackEntry current, StackEntry target, NavigationOption option){
    switch(type){
        case TO:
            return navigateTo(current, target, option);
        case BACK:
            return navigateBack(current, target, option);
        case CLOSE:
            return navigateClose(target, option);
        default:
            return false;
    }
}

private boolean navigateTo(StackEntry current, StackEntry next, NavigationOption option){
    if(option == null){
        option = next.getDestination().getOption();
    }
    ActivityNavigable currentActivity = AppContext.getActivity();
    Intent intent;
    if((option != null) && option.isRedirectIntent()){
        intent = new Intent(currentActivity.getIntent());
    } else {
        intent = new Intent();
    }
    intent.setClass(currentActivity, next.getDestination().getTarget());
    overrideActivityTransition(currentActivity, next.getDestination().getTransition(), true);
    setNav(NavigateType.TO, next.getBindID(), option);
    currentActivity.startActivity(intent);
    return true;
}
@Override
public void onBind(StackEntry current){
    if(!current.hasBeenReconstructed()){
        getNavigatorManager().confirmBound(this);
    }
}
@Override
protected void onConfirmConstructed(StackEntry current, NavigationOption option){
    if(stack().size() <= 1){
        return;
    }
    StackEntry entryPreviousActivity = null;
    List<StackEntry> entryToRemove = new ArrayList<>();
    for(StackEntry e: Iterable.Reversed.from(stack().list(), stack().size() - 1)){
        defNavigable ref = e.getRef();
        if(ref instanceof ActivityNavigable){
            entryPreviousActivity = e;
            break;
        }
        if((ref instanceof FragmentNavigable) || (ref instanceof DialogNavigable) || ((ref == null) && (Reflection.isInstanceOf(e.getRefType(), DialogNavigable.class)))){
            entryToRemove.add(e);
        } else {
DebugException.start().log("unhandled ref").end();
        }

    }
    if((option == null) || option.isKeptInStack_NavTo()){
        return;
    }
    for(StackEntry e: entryToRemove){
        getNavigatorManager().removeEntry(e.getBindID(), option);
    }
    ActivityNavigable activity = (ActivityNavigable)entryPreviousActivity.getRef();
    activity.finish();
    getNavigatorManager().removeEntry(entryPreviousActivity.getBindID(), option);
}

private boolean navigateBack(StackEntry current, StackEntry previous, NavigationOption option){
    if(option == null){
        option = current.getDestination().getOption();
    }
    setNav(NavigateType.BACK, current.getBindID(), option);
    if(previous != null){
        ActivityNavigable currentActivity = AppContext.getActivity();
        overrideActivityTransition(currentActivity, current.getDestination().getTransition(), false);
        if((option != null) && option.isRedirectIntent()){
            ActivityNavigable targetActivity = getNavigatorManager().getLastRef(ACTIVITY, false);
            if(targetActivity != null){
                Intent intent = new Intent(currentActivity.getIntent());
                intent.setClass(AppContext.getActivity(), targetActivity.getClass());
                currentActivity.startActivity(intent);
            }
        }
        currentActivity.finish();
        return true;
    } else {
        return false;
    }
}

private boolean navigateClose(StackEntry entryActivity, NavigationOption option){
    Integer entryIndex = stack().indexOf(entryActivity);
    if(entryIndex == null){
        return false;
    }
    setNav(NavigateType.CLOSE, entryActivity.getBindID(), option);
    if((entryIndex + 1) < stack().size()){
        for(ListIterator<StackEntry> it = stack().list().listIterator(entryIndex + 1); it.hasNext(); ){
            StackEntry e = it.next();
            defNavigable ref = e.getRef();
            if(ref instanceof ActivityNavigable){
                break;
            }
            if((ref instanceof FragmentNavigable) || (ref instanceof DialogNavigable) || ((ref == null) && (Reflection.isInstanceOf(e.getRefType(), DialogNavigable.class)))){
                getNavigatorManager().removeEntry(e.getBindID(), option);
            } else {
DebugException.start().log("unhandled ref").end();
            }
        }
    }
    ActivityNavigable activity = (ActivityNavigable)entryActivity.getRef();
    activity.finish();
    getNavigatorManager().removeEntry(entryActivity.getBindID(), option);
    return true;
}

private void overrideActivityTransition(ActivityNavigable activity, Name.Is transitionKey, boolean forward){
    if(Application.animationManager() != null){
        LifecycleEvent.on(PAUSE, activity, new RunnableSubscription(){
            @Override
            public void onComplete(){
                unsubscribe();
                TransitionManagerAnimation transitionManager = Application.animationManager();
                TransitionManagerAnimation.Transitions transition = transitionManager.get(transitionKey);
                if(transition != null){
                    if(forward){
                        activity.overridePendingTransition(transition.enter, transition.exit);
                    } else {
                        activity.overridePendingTransition(transition.exitBack, transition.enterBack);
                    }
                }
            }
        });
    }
}

}
