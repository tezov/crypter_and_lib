/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.navigation;

import com.tezov.lib_java.async.notifier.observer.state.ObserverStateE;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.runnable.RunnableGroup;
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
import com.tezov.lib_java.async.notifier.observer.event.ObserverEvent;
import com.tezov.lib_java.async.notifier.task.TaskState;
import com.tezov.lib_java.async.notifier.task.TaskValue;
import com.tezov.lib_java.type.collection.Arguments;
import com.tezov.lib_java_android.ui.dialog.DialogNavigable;
import com.tezov.lib_java_android.ui.fragment.FragmentNavigable;
import com.tezov.lib_java_android.ui.navigation.destination.DestinationDetails;

import static com.tezov.lib_java_android.ui.navigation.NavigatorManager.NavigatorKey.ACTIVITY;
import static com.tezov.lib_java_android.ui.navigation.NavigatorManager.NavigatorKey.DIALOG;
import static com.tezov.lib_java_android.ui.navigation.NavigatorManager.NavigatorKey.FRAGMENT;

public abstract class NavigationHelper extends NavigatorManager{

public void navigateTo(DestinationKey.Is destinationKey){
    navigateTo(destinationKey, null);
}
public void navigateTo(DestinationKey.Is destinationKey, NavigationOption option){
    navigateTo(getNavigatorKey(destinationKey), destinationKey, option);
}
public void navigateTo(DestinationKey.Is destinationKey, boolean bindArguments, Arguments arguments){
    navigateTo(destinationKey, null, bindArguments, arguments);
}
public void navigateTo(DestinationKey.Is destinationKey, NavigationOption option, boolean bindArguments, Arguments arguments){
    navigateTo(getNavigatorKey(destinationKey), destinationKey, option, bindArguments, arguments);
}

public <D extends DialogNavigable> TaskValue<D>.Observable navigateTo(Class<D> target, DialogNavigable.State state, NavigationArguments navigationArguments){
    return DialogNavigable.show(target, state, navigationArguments);
}
public <D extends DialogNavigable> TaskValue<D>.Observable navigateTo(Class<D> target, DialogNavigable.State state){
    return DialogNavigable.show(target, state);
}

public TaskState.Observable CloseDialogAll(NavigationOption option){
    TaskState task = new TaskState();
    CloseDialogAll(task, option);
    return task.getObservable();
}
private void CloseDialogAll(TaskState task, NavigationOption option){
    defNavigable ref = getLastRef(null, false);
    if(ref instanceof DialogNavigable){
        observe(new ObserverEvent<>(this, DIALOG){
            @Override
            public void onComplete(NavigatorManager.NavigatorKey.Is key, NavigatorManager.Event event){
                if(event == NavigatorManager.Event.NAVIGATE_BACK_CONFIRMED){
                    unsubscribe();
                    CloseDialogAll(task, option);
                }
            }
        });
        navigateBackStack(false, null, option);
    }
    else{
        task.notifyComplete();
    }
}

public Boolean isArgumentsChanged(defNavigable navigable){
    return getDestinationManager().isArgumentsChanged(navigable);
}

public DestinationDetails getLastDestination(){
    return getLastDestination(null, true);
}
public DestinationKey.Is getLastDestinationKey(){
    return getLastDestination().getKey();
}
public DestinationKey.Is getDestinationKey(defNavigable navigable){
    DestinationDetails destinationDetails = getDestination(navigable);
    if(destinationDetails == null){
        return null;
    } else {
        return destinationDetails.getKey();
    }
}
public DestinationKey.Is getLastDestinationKeySource(){
    DestinationDetails source = getLastDestinationSource(null, true);
    if(source == null){
        return null;
    } else {
        return source.getKey();
    }
}
public DestinationKey.Is getDestinationKeySource(defNavigable navigable){
    DestinationDetails source = getDestinationSource(navigable, null, true);
    if(source == null){
        return null;
    } else {
        return source.getKey();
    }
}
public <N extends defNavigable> N getLastRef(){
    return getLastRef(null, true);
}

}
