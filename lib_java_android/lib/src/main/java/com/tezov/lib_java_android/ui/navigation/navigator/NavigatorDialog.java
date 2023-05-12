/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.navigation.navigator;

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

import static com.tezov.lib_java_android.ui.navigation.NavigatorManager.NavigatorKey.DIALOG;
import static com.tezov.lib_java_android.ui.navigation.stack.StackEntry.Step.DESTROYED;
import static com.tezov.lib_java_android.ui.navigation.stack.StackEntry.Step.PAUSED;

import com.tezov.lib_java_android.application.Application;
import com.tezov.lib_java_android.application.AppContext;
import com.tezov.lib_java.toolbox.Reflection;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java_android.type.android.LifecycleEvent;
import com.tezov.lib_java.type.defEnum.Event;
import com.tezov.lib_java.type.runnable.RunnableSubscription;
import com.tezov.lib_java_android.ui.dialog.DialogBase;
import com.tezov.lib_java_android.ui.dialog.DialogNavigable;
import com.tezov.lib_java_android.ui.navigation.NavigationArguments;
import com.tezov.lib_java_android.ui.navigation.NavigationOption;
import com.tezov.lib_java_android.ui.navigation.NavigatorManager;
import com.tezov.lib_java_android.ui.navigation.defNavigable;
import com.tezov.lib_java_android.ui.navigation.stack.StackEntry;

//NEXT_TODO animation on decor view
public class NavigatorDialog extends Navigator{
public NavigatorDialog(NavigatorManager.NavigatorKey.Is navigatorKey){
    super(navigatorKey);
}
public static boolean backStack(){
    NavigatorDialog navigator = Application.navigationHelper().getNavigator(DIALOG);
    if(navigator == null){
        return false;
    }
    if(navigator.stack().isEmpty()){
        return false;
    }
    navigator.getNavigatorManager().navigateBackStack(false, null, null);
    return true;
}
private NavigatorDialog me(){
    return this;
}
private DialogNavigable createDialog(Class<? extends DialogNavigable> type, NavigationArguments navigationArguments){
    DialogNavigable targetRef = Reflection.newInstance(type);
    if(navigationArguments.exist()){
        DialogBase.State state = navigationArguments.getState();
        if(state != null){
            // check state valid
            if(state.hasOwner()){
DebugException.start()
                        .explode("Step " + DebugTrack.getFullSimpleName(state) + " tag " + state.getTag() + " has owner " + DebugTrack.getFullSimpleName((Object)state.getOwner()))

                        .end();
            }
            DialogBase dialog = DialogBase.findByTag(state.getTag());
            if(dialog != null){
DebugException.start().explode("Dialog " + DebugTrack.getFullSimpleName(type) + " tag " + state.getTag() + " already exist").end();
            }
            //check done
        }
    }
    return targetRef;
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
    NavigationArguments navigationArguments = NavigationArguments.wrap(next.getArguments());
    DialogNavigable targetRef = createDialog(next.getDestination().getTarget(), navigationArguments);
    setNav(NavigateType.TO, next.getBindID(), option);
    if(navigationArguments.exist()){
        DialogBase.State state = navigationArguments.getState();
        if(state != null){
            targetRef.show(state);
            return true;
        }
    }
    targetRef.show();
    return true;
}
@Override
public void onBind(StackEntry current){
    DialogNavigable dialog = (DialogNavigable)current.getRef();
    LifecycleEvent.on(LifecycleEvent.Event.DESTROY, dialog, new RunnableSubscription(){
        @Override
        public void onComplete(){
            unsubscribe();
            if(!AppContext.getActivity().isChangingConfigurations()){
                getNavigatorManager().navigateBackStackOrRemove(me(), current.getBindID(), current.getDestination().obtainOption());
            }
        }
    });
    if(!current.hasBeenReconstructed()){
        getNavigatorManager().confirmBound(this);
    }
}

private boolean navigateBack(StackEntry current, StackEntry previous, NavigationOption option){
    if(option == null){
        option = current.getDestination().getOption();
    }
    if((option!=null)&&option.mustPostCancel_NavBack()){
        DialogNavigable ref = (DialogNavigable)current.getRef();
        if(ref != null){
            ref.post(Event.ON_CANCEL, null);
        }
    }
    if(current.getStep() == DESTROYED){
        getNavigatorManager().confirmNavigate(NavigateType.BACK, this);
    } else if(current.getStep().isAtLeast(PAUSED)){
        setNav(NavigateType.BACK, current.getBindID(), option);
    } else {
        defNavigable currentRef = current.getRef();
        if(currentRef instanceof DialogNavigable){
            DialogNavigable dialog = (DialogNavigable)current.getRef();
            dialog.unObserve(me());
            setNav(NavigateType.BACK, current.getBindID(), option);
            dialog.dismiss();
        }
        else{
            return false;
        }
    }
    return true;
}

private boolean navigateClose(StackEntry entryDialog, NavigationOption option){
    Integer entryIndex = stack().indexOf(entryDialog);
    if((entryIndex == null) || (entryDialog.getRef() == null)){
        return false;
    }
    setNav(NavigateType.CLOSE, entryDialog.getBindID(), option);
    NavigationArguments navigationArguments = NavigationArguments.get(entryDialog.getRef());
    if(navigationArguments.exist()){
        DialogBase.State state = navigationArguments.getState();
        DialogBase dialog = DialogBase.findByTag(state.getTag());
        if(dialog != null){
            dialog.close();
        }
    }
    return true;
}

}
