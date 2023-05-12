/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.dialog;

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
import com.tezov.lib_java.debug.DebugLog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.tezov.lib_java.async.notifier.Notifier;
import com.tezov.lib_java.async.notifier.observable.ObservableEvent;
import com.tezov.lib_java.async.notifier.observer.event.ObserverEvent;
import com.tezov.lib_java_android.toolbox.PostToHandler;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java_android.type.android.LifecycleEvent;
import com.tezov.lib_java_android.type.android.LifecycleState;
import com.tezov.lib_java.type.defEnum.Event;
import com.tezov.lib_java.type.runnable.RunnableSubscription;
import com.tezov.lib_java.type.runnable.RunnableW;

import java.util.List;

import static com.tezov.lib_java_android.type.android.LifecycleEvent.Event.RESUME;

public abstract class DialogBase extends DialogFragment{
private final static float DIM_AMOUNT = 0.4f;

protected Notifier<Event.Is> notifier;
private State state = null;

protected DialogBase(){
DebugTrack.start().create(this).end();
    notifier = new Notifier<>(new ObservableEvent<Event.Is, Object>(), false);
}

public static <D extends DialogBase> D findByTag(String tag){
    return findByTag(AppContext.getFragmentManager(), tag);
}
public static <D extends DialogBase> D findByTag(FragmentManager fm, String tag){
    List<Fragment> fragments = fm.getFragments();
    for(Fragment fragment: fragments){
        if(!(fragment instanceof DialogBase)){
            continue;
        }
        DialogBase dialog = (DialogBase)fragment;
        if(tag.equals(dialog.getTag())){
            return (D)dialog;
        }
    }
    return null;
}

private DialogBase me(){
    return this;
}

protected <S extends State> S newState(){
    return (S)new State();
}

public boolean hasState(){
    return getState() != null;
}

private void makeState(Param param){
    state = newState();
    state.attach(this);
    if(param != null){
        if(!state.hasParam()){
            state.setParam(param);

        } else {
DebugException.start().explode("Step has already Param").end();
        }

    }
}

public <S extends State> S obtainState(){
    if(!hasState()){
        makeState(null);
    }
    return (S)state;
}

public <S extends State> S getState(){
    return (S)state;
}

protected void onRestoreState(){
}

final public void restoreState(State state){
    if(state != null){
        this.state = state;
        state.attach(this);
        onRestoreState();
    }
}

public boolean hasParam(){
    return hasState() && getState().hasParam();
}

public <P extends Param> P obtainParam(){
    return (P)obtainState().obtainParam();
}

public <P extends Param> P getParam(){
    if(!hasState()){
        return null;
    }
    return (P)getState().getParam();
}

public void setParam(Param param){
    if(hasState()){
        getState().setParam(param);
    } else {
        makeState(param);
    }
}

public boolean hasMethod(){
    return hasState() && getState().hasMethod();
}

public <M extends Method> M obtainMethod(){
    return (M)obtainState().obtainMethod();
}

public <M extends Method> M getMethod(){
    if(!hasState()){
        return null;
    }
    return (M)getState().getMethod();
}

//NOTIFIER
public Notifier.Subscription observe(ObserverEvent<Event.Is, Object> observer){
    return notifier.register(observer);
}

public void unObserve(Object owner){
    notifier.unregister(owner);
}

public void postValue(Object object){
    post(null, object);
}

public void postEvent(Event.Is event){
    post(event, null);
}

public void post(Event.Is event, Object object){
    ObservableEvent<Event.Is, Object>.Access access = notifier.obtainAccess(this, event);
    access.setValue(object);
}

public void postValueIfDifferent(Object object){
    postIfDifferent(null, object);
}

public void postIfDifferent(Event.Is event, Object object){
    ObservableEvent<Event.Is, Object>.Access access = notifier.obtainAccess(this, event);
    access.setValueIfDifferent(object);
}

//DIALOG
@Override
public void onAttach(android.content.Context context){
    super.onAttach(context);
DebugLog.start().track(this).end();
}

@Override
public void onCreate(@Nullable Bundle savedInstanceState){
    super.onCreate(savedInstanceState);
DebugLog.start().track(this).end();
}

@NonNull
@Override
public Dialog onCreateDialog(Bundle savedInstanceState){
DebugLog.start().track(this).end();
    Dialog dialog = new Dialog(getActivity(), getTheme()){
        Dialog init(){
DebugTrack.start().create(this).end();
            return this;
        }
        @Override
        public void onBackPressed(){
            if(!me().onBackPressed()){
                super.onBackPressed();
            }
        }
        @Override
        public boolean onTouchEvent(@NonNull MotionEvent event){
            if(isCancelable() && isShowing() && shouldCloseOnTouch(getContext(), event)){
                post(Event.ON_CANCEL, null);
                me().close();
                return true;
            } else {
                return false;
            }
        }
        private boolean shouldCloseOnTouch(android.content.Context context, MotionEvent event){
            boolean isOutside = event.getAction() == MotionEvent.ACTION_UP && isOutOfBounds(context, event) || event.getAction() == MotionEvent.ACTION_OUTSIDE;
            return getWindow().peekDecorView() != null && isOutside;
        }
        private boolean isOutOfBounds(android.content.Context context, MotionEvent event){
            int x = (int)event.getX();
            int y = (int)event.getY();
            int slop = ViewConfiguration.get(context).getScaledWindowTouchSlop();
            View decorView = getWindow().getDecorView();
            return (x < -slop) || (y < -slop) || (x > (decorView.getWidth() + slop)) || (y > (decorView.getHeight() + slop));
        }
        @Override
        protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
            super.finalize();
        }
    }.init();
    dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    return dialog;
}

@Nullable
@Override
public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
DebugLog.start().track(this).end();
    return null;
}

@Override
public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
    super.onViewCreated(view, savedInstanceState);
DebugLog.start().track(this).end();
    getDialog().getWindow().setDimAmount(DIM_AMOUNT);
    if(obtainParam().requestPreload){
        getDialog().getWindow().getDecorView().setVisibility(View.INVISIBLE);
    }
}

public boolean onBackPressed(){
    if(!isCancelable()){
        return true;
    }
    if((getDialog() == null) || !hasState() || !getState().isVisible){
        return false;
    }
    else{
        post(Event.ON_CANCEL, null);
        close();
        return true;
    }
}

@Override
public void onViewStateRestored(@Nullable Bundle savedInstanceState){
    super.onViewStateRestored(savedInstanceState);
DebugLog.start().track(this).end();

//    if(savedInstanceState != null){
//        for(String key: savedInstanceState.keySet()){
//            DebugLog.start().send(this, "key:" + key).end();
//        }
//    }

}

@Override
public void onStart(){
    super.onStart();
DebugLog.start().track(this).end();
}

@Override
public void onResume(){
    super.onResume();
DebugLog.start().track(this).end();
    if(!hasState()){
        return;
    }
    if(getState().isHidden){
        getDialog().getWindow().getDecorView().setVisibility(View.INVISIBLE);
        postEvent(Event.ON_RECONSTRUCT);
    } else {
        if(getState().isVisible){
            postEvent(Event.ON_RECONSTRUCT);
        }
    }
}

@Override
public void onCancel(@NonNull DialogInterface dialog){
    super.onCancel(dialog);
DebugLog.start().track(this).end();
    close();
}

@Override
public void onPause(){
    super.onPause();
DebugLog.start().track(this).end();
}

@Override
public void onSaveInstanceState(Bundle outState){
    super.onSaveInstanceState(outState);
DebugLog.start().track(this).end();
}

@Override
public void onStop(){
    super.onStop();
DebugLog.start().track(this).end();
}

@Override
public void onDetach(){
DebugLog.start().track(this).end();
    notifier.unregisterAll();
    if(hasState()){
        state.detach(this);
    }
    super.onDetach();
}

@Override
public void onDestroyView(){
    super.onDestroyView();
DebugLog.start().track(this).end();
}

//USER METHOD
public boolean isVisibleToUser(){
    return getState().isVisible;
}

public boolean isHiddenToUser(){
    return getState().isHidden;
}

public boolean isLoaded(){
    return isVisibleToUser() || isHiddenToUser();
}

synchronized public <D extends DialogBase> D preload(){
    if(getState().isHidden || getParam().requestPreload){
        return (D)this;
    }
    getParam().requestPreload = true;
    if(getDialog() == null){
        return show();
    } else {
        return (D)this;
    }
}

synchronized public <D extends DialogBase> D show(){
    return show(obtainState());
}
synchronized public <D extends DialogBase> D show(State state){
    return show(AppContext.getFragmentManager().beginTransaction(), state);
}
synchronized public <D extends DialogBase> D show(FragmentTransaction transaction, State state){
    if(state.isVisible){
        return (D)this;
    }
    if(state.isBusy){
        return (D)this;
    }
DebugLog.start().track(this).end();
    if(getView() != null){
        PostToHandler.of(getView(), new RunnableW(){
            @Override
            public void runSafe(){
                State state = getState();
                state.isVisible = true;
                state.isHidden = false;
                getDialog().show();
                postEvent(Event.ON_OPEN);
            }
        });
    } else {
        if(LifecycleState.get(this) != LifecycleState.INITIALIZED){

DebugException.start().explode("Invalid Step, maybe dialog has been dismissed, " + LifecycleState.get(this)).end();
            return null;
        }
        String tag = state.getTag();
        LifecycleEvent.on(RESUME, this, new RunnableSubscription(){
            @Override
            public void onComplete(){
                unsubscribe();
                State state = getState();
                state.isBusy = false;
                if(!state.hasParam() || !state.getParam().requestPreload){
                    state.isVisible = true;
                    state.isHidden = false;
                    postEvent(Event.ON_OPEN);
                } else {
                    state.getParam().requestPreload = false;
                    state.isVisible = false;
                    state.isHidden = true;
                }
            }
        });
        state.isBusy = true;
        show(transaction, tag);
    }
    return (D)this;
}

synchronized public void hide(){
DebugLog.start().track(this).end();
    if(!PostToHandler.of(getView(), new RunnableW(){
        @Override
        public void runSafe(){
            if(getDialog() == null){
                pendingHide();
            } else {
                if(!getState().isVisible){
                    return;
                }
                getDialog().hide();
                getState().isVisible = false;
                getState().isHidden = true;
                postEvent(Event.ON_CLOSE);
            }
        }
    })){
        pendingHide();
    }
}
public void pendingHide(){
    pendingOnResume(new RunnableW(){
        @Override
        public void runSafe(){
            hide();
        }
    });
}

@Override
synchronized public void dismiss(){
DebugLog.start().track(this).end();
    if(!PostToHandler.of(getView(), new RunnableW(){
        @Override
        public void runSafe(){
            if(getDialog() == null){
                pendingDismiss();
            } else {
                if(!getState().isVisible){
                    return;
                }
                getDialog().dismiss();
                getState().isVisible = false;
                getState().isHidden = false;
                postEvent(Event.ON_CLOSE);
            }
        }
    })){
        pendingDismiss();
    }
}
private void pendingDismiss(){
    pendingOnResume(new RunnableW(){
        @Override
        public void runSafe(){
            dismiss();
        }
    });
}

synchronized public void close(){
DebugLog.start().track(this).end();
    if(obtainParam().hideOnClose){
        hide();
    } else {
        dismiss();
    }
}
private void pendingOnResume(RunnableW r){
    LifecycleEvent.on(RESUME, me(), new RunnableSubscription(){
        @Override
        public void onComplete(){
            unsubscribe();
            r.run();
        }
    });
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

//STATE
public static class State extends com.tezov.lib_java_android.ui.state.State<Param, Method>{
    private boolean isBusy = false;
    private boolean isVisible = false;
    private boolean isHidden = false;

    @Override
    protected Param newParam(){
        return new Param();
    }

    @Override
    protected Method newMethod(){
        return new Method();
    }

    public <D extends DialogBase> D findDialog(){
        return findByTag(getTag());
    }

    public String getTag(){
        return Integer.toString(hashCode());
    }

    public boolean isBusy(){
        return isBusy;
    }

    public boolean isVisible(){
        return isVisible;
    }

    public boolean canBeShown(){
        return !isBusy && !isVisible;
    }

    public boolean isHidden(){
        return isHidden;
    }

    @Override
    public DebugString toDebugString(){
        DebugString data = super.toDebugString();
        data.append("tag", getTag());
        data.append("isBusy", isBusy);
        data.append("isVisible", isVisible);
        data.append("isHidden", isHidden);
        return data;
    }

}

public static class Param extends com.tezov.lib_java_android.ui.state.Param{
    public boolean requestPreload = false;
    public boolean hideOnClose = false;
    public String title = null;
    public String confirmButtonText = null;
    public String cancelButtonText = null;

    public boolean isRequestPreload(){
        return requestPreload;
    }

    public Param setRequestPreload(boolean requestPreload){
        this.requestPreload = requestPreload;
        return this;
    }

    public boolean isHideOnClose(){
        return hideOnClose;
    }

    public Param hideOnClose(boolean hideOnClose){
        this.hideOnClose = hideOnClose;
        return this;
    }

    public String getTitle(){
        return title;
    }
    public Param setTitle(int resourceId){
        return setTitle(AppContext.getResources().getString(resourceId));
    }
    public Param setTitle(String title){
        this.title = title;
        return this;
    }

    public String getConfirmButtonText(){
        return confirmButtonText;
    }
    public Param setConfirmButtonText(int resourceId){
        return setConfirmButtonText(AppContext.getResources().getString(resourceId));
    }
    public Param setConfirmButtonText(String confirmButtonText){
        this.confirmButtonText = confirmButtonText;
        return this;
    }

    public String getCancelButtonText(){
        return cancelButtonText;
    }
    public Param setCancelButtonText(int resourceId){
        return setCancelButtonText(AppContext.getResources().getString(resourceId));
    }
    public Param setCancelButtonText(String cancelButtonText){
        this.cancelButtonText = cancelButtonText;
        return this;
    }

    @Override
    public DebugString toDebugString(){
        DebugString data = super.toDebugString();
        data.append("title", title);
        data.append("confirmButtonText", confirmButtonText);
        data.append("cancelButtonText", cancelButtonText);
        data.append("requestPreload", requestPreload);
        data.append("hideOnClose", hideOnClose);
        return data;
    }

}

public static class Method extends com.tezov.lib_java_android.ui.state.Method{
    @Override
    public DialogBase getOwner(){
        return super.getOwner();
    }

    public boolean isVisibleToUser(){
        return getOwner().isVisibleToUser();
    }

    public boolean isHiddenToUser(){
        return getOwner().isHiddenToUser();
    }

    public boolean isLoaded(){
        return getOwner().isLoaded();
    }

    public void preload(){
        getOwner().preload();
    }

    public void show(){
        getOwner().show(getState());
    }

    public void hide(){
        getOwner().hide();
    }

    public void dismiss(){
        getOwner().dismiss();
    }

    public void close(){
        getOwner().close();
    }

}

}