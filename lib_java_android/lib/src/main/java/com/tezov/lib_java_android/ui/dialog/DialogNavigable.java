/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.dialog;

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

import com.tezov.lib_java_android.application.AppContext;
import com.tezov.lib_java_android.application.AppDisplay;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java_android.wrapperAnonymous.FragmentLifecycleObserverW;

import androidx.annotation.NonNull;
import com.tezov.lib_java.type.defEnum.Event;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.tezov.lib_java_android.application.Application;
import com.tezov.lib_java.async.notifier.task.TaskValue;
import com.tezov.lib_java_android.type.android.LifecycleEvent;
import com.tezov.lib_java.type.runnable.RunnableSubscription;
import com.tezov.lib_java_android.ui.navigation.NavigationArguments;
import com.tezov.lib_java_android.ui.navigation.defNavigable;
import com.tezov.lib_java_android.ui.navigation.navigator.NavigatorDialog;
import com.tezov.lib_java_android.ui.navigation.stack.StackEntry;

import static com.tezov.lib_java_android.type.android.LifecycleEvent.Event.DESTROY;
import static com.tezov.lib_java_android.ui.navigation.NavigatorManager.NavigatorKey.DIALOG;

public abstract class DialogNavigable extends DialogBase implements defNavigable{
protected final static float WIDTH_RATIO = 0.9f;
private Long bindID = null;

public static <D extends DialogNavigable> TaskValue<D>.Observable show(Class<D> target, DialogNavigable.State state){
    return show(target, state, null);
}
public static <D extends DialogNavigable> TaskValue<D>.Observable show(Class<D> target, DialogNavigable.State state, NavigationArguments navArguments){
    TaskValue<D> task = new TaskValue<>();
    if(state == null){
        task.notifyException(null, "Step can not be null");
        return task.getObservable();
    }
    if(state.isBusy){
        task.notifyException(null, "Step is busy");
        return task.getObservable();
    }
    state.isBusy = true;
    if(navArguments == null){
        navArguments = NavigationArguments.create();
    }
    navArguments.setState(state);
    AppContext.getFragmentManager().registerFragmentLifecycleCallbacks(new FragmentLifecycleObserverW(){
        @Override
        public void onFragmentViewCreated(@NonNull FragmentManager fm, @NonNull Fragment f, @NonNull View v, @Nullable Bundle savedInstanceState){
            if(f instanceof DialogBase){
                DialogNavigable dialog = (DialogNavigable)f;
                if(state.getTag().equals(dialog.getTag())){
                    AppContext.getFragmentManager().unregisterFragmentLifecycleCallbacks(this);
                    state.isBusy = false;
                    task.notifyComplete((D)dialog);
                }
            }
        }
    }, false);
    Application.navigationHelper().navigateTo(DIALOG, (Class)target, null, true, navArguments.getArguments());
    return task.getObservable();
}

public static TaskValue<DialogNavigable>.Observable close(DialogNavigable.State state){
    TaskValue<DialogNavigable> task = new TaskValue<>();
    if(state.isBusy){
        task.notifyException(null, "Step is busy");
        return task.getObservable();
    }
    state.isBusy = true;
    DialogNavigable dialog = DialogBase.findByTag(state.getTag());
    if(dialog != null){
        LifecycleEvent.on(DESTROY, dialog, new RunnableSubscription(){
            @Override
            public void onComplete(){
                unsubscribe();
                state.isBusy = false;
                task.notifyComplete(dialog);
            }
        });
        dialog.close();
    } else {
        state.isBusy = false;
        task.notifyException(null, new NullPointerException());
    }
    return task.getObservable();
}

@Override
protected State newState(){
    return new State();
}

@Override
public State getState(){
    return super.getState();
}

@Override
public Long getBindID(){
    return bindID;
}

@Override
public void setBindID(Long id){
    bindID = id;
}

public StackEntry.Step getStepLife(){
    return NavigatorDialog.getStepLife(this);
}
public boolean hasBeenRestarted(){
    return NavigatorDialog.hasBeenRestarted(this);
}
public boolean hasBeenReconstructed(){
    return NavigatorDialog.hasBeenReconstructed(this);
}

@Override
public void onCreate(@Nullable Bundle savedInstanceState){
    super.onCreate(savedInstanceState);
    if(savedInstanceState != null){
        bindID = savedInstanceState.getLong(NavigatorDialog.KEY_BIND_ID);
    }
    NavigatorDialog.bind(this);
    NavigationArguments navigationArguments = NavigationArguments.get(this);
    if(navigationArguments.exist()){
        restoreState(navigationArguments.getState());
    }
}

protected int getWidth(){
    return (int)(AppDisplay.getSizeOriented().getWidth() * WIDTH_RATIO);
}
protected int getHeight(){
    return ViewGroup.LayoutParams.WRAP_CONTENT;
}

@Override
public void onPrepare(boolean hasBeenReconstructed){
DebugLog.start().track(this, hasBeenReconstructed ? " reconstructed" : null).end();
    Window window = getDialog().getWindow();
    window.setBackgroundDrawableResource(android.R.color.transparent);
    window.setLayout(getWidth(), getHeight());
}

@Override
public void onOpen(boolean hasBeenReconstructed, boolean hasBeenRestarted){
DebugLog.start().track(this, (hasBeenReconstructed ? " reconstructed" : null) + "-" + (hasBeenRestarted ? " restarted" : null)).end();
}
@Override
public void onSaveInstanceState(Bundle savedInstanceState){
    super.onSaveInstanceState(savedInstanceState);
    savedInstanceState.putLong(NavigatorDialog.KEY_BIND_ID, bindID);
    if(hasState()){
        NavigationArguments.obtain(this).setState(getState());
    }
}

@Override
public void confirmConstructed(StackEntry entry){
    NavigatorDialog.confirmConstructed(DIALOG, entry);
}
@Override
public void confirmDestroyed(StackEntry entry){
    NavigatorDialog.confirmDestroyed(DIALOG, entry);
}
@Override
public void removedFromStack(){
DebugLog.start().track(this).end();
}

@Override
public synchronized void close(){
    defNavigable ref = Application.navigationHelper().getLastRef(DIALOG, false);
    if(ref == this){
        Application.navigationHelper().navigateBackStack(false, null, null);
    }
    else{
        super.close();
    }
}

@Override
public boolean requestViewUpdate(Integer what, NavigationArguments arg){
    return false;
}
@Override
public boolean onNewNavigationArguments(NavigationArguments arg){
    return false;
}

@Override
public boolean onBackPressed(){
    if(isCancelable() && NavigatorDialog.backStack()){
        post(Event.ON_CANCEL, null);
        return true;
    } else {
        return super.onBackPressed();
    }
}

public static class State extends DialogBase.State{
    private boolean isBusy = false;

    @Override
    protected DialogBase.Param newParam(){
        return new Param();
    }

}
public static class Param extends DialogBase.Param{


}

}