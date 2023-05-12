/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.navigation;

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
import com.tezov.lib_java.async.notifier.task.TaskState;
import com.tezov.lib_java_android.application.Application;
import com.tezov.lib_java.async.notifier.Notifier;
import com.tezov.lib_java.async.notifier.observer.event.ObserverEvent;
import com.tezov.lib_java.async.notifier.task.TaskValue;
import com.tezov.lib_java_android.ui.activity.ActivityNavigable;
import com.tezov.lib_java_android.ui.dialog.DialogNavigable;
import com.tezov.lib_java_android.ui.fragment.FragmentNavigable;
import com.tezov.lib_java_android.ui.navigation.destination.DestinationDetails;

import static com.tezov.lib_java_android.ui.navigation.NavigatorManager.NavigatorKey.ACTIVITY;
import static com.tezov.lib_java_android.ui.navigation.NavigatorManager.NavigatorKey.FRAGMENT;

public class Navigate{

public static NavigationHelper helper(){
    return Application.navigationHelper();
}
private static Class<Navigate> myClass(){
    return Navigate.class;
}

public static Notifier.Subscription observe(ObserverEvent<NavigatorManager.NavigatorKey.Is, NavigatorManager.Event> observer){
    return helper().observe(observer);
}
public static void unObserve(Object owner){
    helper().unObserve(owner);
}

public static boolean isCurrent(defNavigable navigable){
    return getCurrent() == identify(navigable);
}

public static <I extends NavigatorManager.DestinationKey.Is> I identify(defNavigable navigable){
    return identify(navigable.getClass());
}
public static <I extends NavigatorManager.DestinationKey.Is> I identify(Class<? extends defNavigable> type){
    return helper().identify(type);
}

public static NavigatorManager.DestinationKey.Is getSource(defNavigable navigable){
    return helper().getDestinationKeySource(navigable);
}

public static NavigatorManager.DestinationKey.Is getSource(){
    return helper().getLastDestinationKeySource();
}

public static NavigatorManager.DestinationKey.Is getCurrent(){
    return helper().getLastDestinationKey();
}

public static NavigatorManager.DestinationKey.Is getCurrentFragment(){
    DestinationDetails lastDestination = helper().getLastDestination(FRAGMENT, true);
    if(lastDestination == null){
        return null;
    } else {
        return lastDestination.getKey();
    }
}

public static <N extends FragmentNavigable> N getCurrentFragmentRef(){
    return helper().getLastRef(FRAGMENT, true);
}

public static NavigatorManager.DestinationKey.Is getCurrentActivity(){
    DestinationDetails lastDestination = helper().getLastDestination(ACTIVITY, true);
    if(lastDestination == null){
        return null;
    } else {
        return lastDestination.getKey();
    }
}

public static <N extends ActivityNavigable> N getCurrentActivityRef(){
    return helper().getLastRef(ACTIVITY, true);
}

public static void To(NavigatorManager.DestinationKey.Is destinationKey, NavigationArguments navigationArguments){
    To(destinationKey, null, navigationArguments);
}
public static void To(NavigatorManager.DestinationKey.Is destinationKey, NavigationOption option, NavigationArguments navigationArguments){
    if(navigationArguments != null){
        helper().navigateTo(destinationKey, option, true, navigationArguments.getArguments());
    } else {
        helper().navigateTo(destinationKey, option, false, null);
    }
}
public static void To(NavigatorManager.DestinationKey.Is destinationKey){
    To(destinationKey, (NavigationOption)null);
}
public static void To(NavigatorManager.DestinationKey.Is destinationKey, NavigationOption option){
    helper().navigateTo(destinationKey, option, false, null);
}

public static <D extends DialogNavigable> TaskValue<D>.Observable To(Class<D> target, DialogNavigable.State state, NavigationArguments navigationArguments){
    return helper().navigateTo(target, state, navigationArguments);
}
public static <D extends DialogNavigable> TaskValue<D>.Observable To(Class<D> target, DialogNavigable.State state){
    return helper().navigateTo(target, state);
}

public static void Back(NavigationArguments navigationArguments){
    Back(navigationArguments, null);
}
public static void Back(NavigationArguments navigationArguments, NavigationOption option){
    helper().navigateBackStack(true, navigationArguments.getArguments(), option);
}
public static void Back(){
    Back((NavigationOption)null);
}
public static void Back(NavigationOption option){
    helper().navigateBackStack(false, null, option);
}

public static void Close(NavigatorManager.DestinationKey.Is destinationKey){
    helper().navigateClose(destinationKey, null);
}
public static void Close(NavigatorManager.DestinationKey.Is destinationKey, NavigationOption option){
    helper().navigateClose(destinationKey, option);
}

public static TaskState.Observable CloseDialogAll(NavigationOption option){
    return helper().CloseDialogAll(option);
}

public static void CloseApplication(){
    helper().navigateCloseApplication();
}

}
