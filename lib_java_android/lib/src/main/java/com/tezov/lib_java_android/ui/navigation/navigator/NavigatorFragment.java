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

import com.tezov.lib_java_android.ui.misc.TransitionManagerAnimation;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.tezov.lib_java_android.application.Application;
import com.tezov.lib_java_android.application.AppContext;
import com.tezov.lib_java.toolbox.Iterable;
import com.tezov.lib_java.toolbox.Reflection;
import com.tezov.lib_java.type.runnable.RunnableW;
import com.tezov.lib_java_android.ui.activity.ActivityNavigable;
import com.tezov.lib_java_android.ui.fragment.FragmentNavigable;
import com.tezov.lib_java_android.ui.navigation.NavigationOption;
import com.tezov.lib_java_android.ui.navigation.NavigatorManager;
import com.tezov.lib_java_android.ui.navigation.defNavigable;
import com.tezov.lib_java_android.ui.navigation.stack.StackEntry;


public class NavigatorFragment extends Navigator{
private final int fragmentContainerID;
private boolean doNotPopOutTheLastFragment = true;

public NavigatorFragment(NavigatorManager.NavigatorKey.Is navigatorKey, int fragmentContainerID){
    super(navigatorKey);
    this.fragmentContainerID = fragmentContainerID;
}

private FragmentNavigable createFragment(Class<? extends FragmentNavigable> type){
    return Reflection.newInstance(type);
}

private FragmentManager getFragmentManager(){
    return AppContext.getFragmentManager();
}

public NavigatorFragment doNotPopOutTheLastFragment(boolean flag){
    doNotPopOutTheLastFragment = flag;
    return this;
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
    FragmentTransaction transaction = getFragmentManager().beginTransaction();
    setEnterAnimations(transaction, current, next);
    showNextFragment(transaction, next, option);
    setNav(NavigateType.TO, next.getBindID(), option);
    transaction.commit();
    transaction = getFragmentManager().beginTransaction();
    hidePreviousFragment(transaction, option);
    transaction.commit();
    return true;
}
private void setEnterAnimations(FragmentTransaction transaction, StackEntry current, StackEntry next){
    TransitionManagerAnimation transitionManager = Application.animationManager();
    if(transitionManager != null){
        TransitionManagerAnimation.Transitions transition = transitionManager.get(next.getDestination().getTransition());
        defNavigable currentRef = current.getRef();
        if(transition != null){
            transaction.setCustomAnimations(transition.enter, transition.exit);
            if(currentRef instanceof FragmentNavigable){
                transaction.runOnCommit(new RunnableW(){
                    @Override
                    public void runSafe(){
                        transition.getExitFor(((Fragment)currentRef).getView());
                    }
                });
            }
        }
    }
}

private void showNextFragment(FragmentTransaction transaction, StackEntry next, NavigationOption option){
    FragmentNavigable nextRef = null;
    if((option != null) && option.isSingle()){
        Class<FragmentNavigable> nextRefType = next.getDestination().getTarget();
        for(StackEntry e: Iterable.Reversed.from(stack().list(), stack().size() - 1)){
            Class refType = e.getRefType();
            if(Reflection.isInstanceOf(refType, ActivityNavigable.class)){
                break;
            }
            if(!Reflection.isInstanceOf(refType, nextRefType)){
                continue;
            }
            nextRef = (FragmentNavigable)e.getRef();
            if(nextRef == null){
                nextRef = createFragment(next.getDestination().getTarget());
                transaction.add(fragmentContainerID, nextRef);
            } else {
                transaction.show(nextRef);
                FragmentNavigable finalTargetRef = nextRef;
                e.unbind();
                transaction.runOnCommit(new RunnableW(){
                    @Override
                    public void runSafe(){
                        finalTargetRef.setBindID(null);
                        bind(finalTargetRef);
                    }
                });
            }
            getNavigatorManager().removeEntry(e.getBindID(), null);
            break;
        }
    }
    if(nextRef == null){
        nextRef = createFragment(next.getDestination().getTarget());
        transaction.add(fragmentContainerID, nextRef);
    }
    transaction.setPrimaryNavigationFragment(nextRef);
}
private void hidePreviousFragment(FragmentTransaction transaction, NavigationOption option){
    if(stack().size() <= 1){
        return;
    }
    for(StackEntry e: Iterable.Reversed.from(stack().list(), stack().size() - 1)){
        defNavigable ref = e.getRef();
        if(ref instanceof ActivityNavigable){
            return;
        }
        if(!(ref instanceof FragmentNavigable)){
            continue;
        }
        FragmentNavigable previousRef = ((FragmentNavigable)ref);
        if(option == null){
            option = e.getDestination().getOption();
        }
        if((option != null) && !option.isKeptInStack_NavTo()){
            transaction.remove((FragmentNavigable)ref);
            getNavigatorManager().removeEntry(e.getBindID(), option);
        } else if((option == null) || !option.isReleasedRef()){
            transaction.hide(previousRef);
            ((FragmentNavigable)ref).onPause();
        } else {
            transaction.remove((FragmentNavigable)ref);
        }
        break;
    }
}

@Override
public void onBind(StackEntry current){
    if(!current.hasBeenReconstructed()){
        getNavigatorManager().confirmBound(this);
    }
}
private boolean navigateBack(StackEntry current, StackEntry previous, NavigationOption option){
    if(option == null){
        option = current.getDestination().getOption();
    }
    defNavigable previousRef = previous.getRef();
    if((previousRef instanceof ActivityNavigable) && doNotPopOutTheLastFragment){
        return false;
    } else {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        setExitAnimations(transaction, current, previous);
        showPreviousFragment(transaction, option);
        hideCurrentFragment(transaction, current, option);
        setNav(NavigateType.BACK, current.getBindID(), option);
        transaction.commit();
        return true;
    }
}
private void setExitAnimations(FragmentTransaction transaction, StackEntry current, StackEntry previous){
    TransitionManagerAnimation transitionManager = Application.animationManager();
    if(transitionManager != null){
        TransitionManagerAnimation.Transitions transition = transitionManager.get(current.getDestination().getTransition());
        defNavigable previousRef = previous.getRef();
        if(transition != null){
            transaction.setCustomAnimations(transition.exitBack, transition.enterBack);
            if(previousRef instanceof FragmentNavigable){
                transaction.runOnCommit(new RunnableW(){
                    @Override
                    public void runSafe(){
                        transition.getExitBackFor(((Fragment)previousRef).getView());
                    }
                });
            }
        }
    }
}
private void showPreviousFragment(FragmentTransaction transaction, NavigationOption option){
    for(StackEntry e: Iterable.Reversed.from(stack().list(), stack().size())){
        Class refType = e.getRefType();
        if(Reflection.isInstanceOf(refType, ActivityNavigable.class)){
            return;
        }
        if(!Reflection.isInstanceOf(refType, FragmentNavigable.class)){
            continue;
        }
        FragmentNavigable previousRef = ((FragmentNavigable)e.getRef());
        if(previousRef == null){
            previousRef = createFragment(refType);
            previousRef.setBindID(e.getBindID());
            transaction.add(fragmentContainerID, previousRef);
        } else {
            transaction.show(previousRef);
            FragmentNavigable finalPreviousRef = previousRef;
            transaction.runOnCommit(new RunnableW(){
                @Override
                public void runSafe(){
                    finalPreviousRef.onOpen(false, true);
                }
            });
        }
        transaction.setPrimaryNavigationFragment(previousRef);
        break;
    }
}
private void hideCurrentFragment(FragmentTransaction transaction, StackEntry current, NavigationOption option){
    FragmentNavigable currentRef = (FragmentNavigable)current.getRef();
    if((option!=null) && option.isKeptInStack_NavBack()){
        if(option.isReleasedRef()){
            transaction.remove(currentRef);
        } else {
            transaction.hide(currentRef);
            transaction.runOnCommit(new RunnableW(){
                @Override
                public void runSafe(){
                    confirmDestroyed(current);
                }
            });
        }
        for(StackEntry e: Iterable.Reversed.from(stack().list(), stack().size() - 1)){
            defNavigable ref = e.getRef();
            if(ref instanceof ActivityNavigable){
                getNavigatorManager().insertEntry(e.getBindID(), current, option);
                break;
            }
        }
    } else {
        transaction.remove(currentRef);
    }
}
private boolean navigateClose(StackEntry entryFragment, NavigationOption option){
    Integer entryIndex = stack().indexOf(entryFragment);
    if((entryIndex == null) || (entryFragment.getRef() == null)){
        return false;
    }
    setNav(NavigateType.CLOSE, entryFragment.getBindID(), option);
    FragmentTransaction transaction = getFragmentManager().beginTransaction();
    transaction.remove((Fragment)entryFragment.getRef());
    transaction.commit();
    return true;
}

}
