/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.application;

import com.tezov.lib_java.debug.DebugLog;
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
import com.tezov.lib_java.type.misc.SupplierSubscription;
import com.tezov.lib_java.async.Handler;
import com.tezov.lib_java_android.ui.component.plain.FocusCemetery;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING;
import static android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN;
import static android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE;
import static com.tezov.lib_java_android.type.android.LifecycleEvent.Event.DESTROY;
import static com.tezov.lib_java_android.type.android.LifecycleEvent.Event.RESUME;

import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.WindowInsets;
import android.view.inputmethod.InputMethodManager;
import android.widget.PopupWindow;

import androidx.annotation.RequiresApi;
import androidx.core.view.WindowInsetsCompat;

import com.tezov.lib_java_android.R;
import com.tezov.lib_java.async.notifier.Notifier;
import com.tezov.lib_java.async.notifier.observable.ObservableValue;
import com.tezov.lib_java.async.notifier.observer.value.ObserverValue;
import com.tezov.lib_java_android.toolbox.PostToHandler;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java_android.type.android.ViewTreeEvent;
import com.tezov.lib_java_android.type.android.LifecycleEvent;
import com.tezov.lib_java.wrapperAnonymous.FunctionW;
import com.tezov.lib_java.type.collection.ListKey;
import com.tezov.lib_java.type.collection.ListOrObject;
import com.tezov.lib_java.type.ref.WR;
import com.tezov.lib_java.type.runnable.RunnableSubscription;
import com.tezov.lib_java.type.runnable.RunnableW;
import com.tezov.lib_java_android.ui.activity.ActivityBase;
import com.tezov.lib_java_android.ui.layout.FrameLayout;
import com.tezov.lib_java_android.util.UtilsView;

public class AppKeyboard{
private final static long SHOW_DELAY_ms = 250;
private static Notifier<Void> keyBoardVisibility = null;
private static ListKey<Integer, ListenerDetails> keyboardListeners = null;
private static int height = 0;


private AppKeyboard(){
}

public static void onMainActivityStart(){
    if(keyboardListeners == null){
        keyboardListeners = new ListKey<>(ListOrObject::new, new FunctionW<ListenerDetails, Integer>(){
            @Override
            public Integer apply(ListenerDetails listener){
                return listener.getActivityHashCode();
            }
        });
        ObservableValue<Boolean> observable = new ObservableValue<>();
        ObservableValue<Boolean>.Access access = observable.obtainAccess(null);
        access.setValue(false);
        keyBoardVisibility = new Notifier<>(observable, true);
        AppContext.observeOnActivityChange(new ObserverValue<ActivityBase>(myClass()){
            @Override
            public void onComplete(ActivityBase activity){
                LifecycleEvent.on(RESUME, activity, new RunnableSubscription(){
                    @Override
                    public void onComplete(){
                        unsubscribe();
                        listenKeyboardChanged(activity);
                    }
                });
            }
        });
    }
}
public static void onApplicationClose(){
    if(keyboardListeners != null){
        keyboardListeners = null;
    }
    if(keyBoardVisibility != null){
        keyBoardVisibility.unregisterAll();
        keyBoardVisibility = null;
    }
}

private static Class<AppKeyboard> myClass(){
    return AppKeyboard.class;
}
private static ListenerDetails findListener(ActivityBase activity){
    return keyboardListeners.getValue(activity.hashCode());
}
private static ListenerDetails findListener(WR<ActivityBase> activityWR){
    return keyboardListeners.getValue(activityWR.hashCode());
}

private static void listenKeyboardChanged(ActivityBase activity){
    if(!activity.listenKeyboard() || findListener(activity) != null){
        return;
    }
    ListenerDetails listenerDetails;
    if(VersionSDK.isSupEqualTo30_R()){
        listenerDetails = new ListenerDetails_after30_R(activity);
    } else {
        listenerDetails = new ListenerDetails_before30_R(activity);
    }
    keyboardListeners.add(listenerDetails);
    LifecycleEvent.on(DESTROY, activity, new RunnableSubscription(){
        @Override
        public void onComplete(){
            unsubscribe();
            ListenerDetails listener = findListener(activity);
            listener.dismiss(activity);
            keyboardListeners.remove(listener);
        }
    });
}
public static Notifier.Subscription observeVisibilityChange(ObserverValue<Boolean> observer){
    return keyBoardVisibility.register(observer);
}
public static void unObserveVisibilityChange(Object owner){
    keyBoardVisibility.unregister(owner);
}
public static void unObserveVisibilityChangeAll(){
    keyBoardVisibility.unregisterAll();
}

public static InputMethodManager getInputMethod(){
    return AppContext.getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
}

public static boolean isVisible(){
    ObservableValue<Boolean>.Access access = keyBoardVisibility.obtainAccess(myClass(), null);
    return access.getValue();
}
public static void show(View view){
    if(view.isAttachedToWindow()){
        showDelayed(view);
    } else {
        ViewTreeEvent.onLayout(view, new RunnableSubscription(){
            @Override
            public void onComplete(){
                unsubscribe();
                showDelayed(view);
            }
        });
    }
}
private static void showDelayed(View view){
    PostToHandler.of(view, SHOW_DELAY_ms, new RunnableW(){
        @Override
        public void runSafe(){
            view.requestFocus();
            getInputMethod().showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    });
}
public static void hide(View view){
    PostToHandler.of(view, new RunnableW(){
        @Override
        public void runSafe(){
            getInputMethod().hideSoftInputFromWindow(view.getWindowToken(), 0);
            FocusCemetery.request(view, UtilsView.Direction.UP, false);
        }
    });
}

public static void setAdjustMode(AdjustMode mode){
    ActivityBase activity = AppContext.getActivity();
    PostToHandler.of(activity, new RunnableW(){
        @Override
        public void runSafe(){
            findListener(activity).setActivityAdjustMode(activity, mode);
        }
    });
}
public static void getAdjustMode(){
    findListener(AppContext.getActivity()).getActivityAdjustMode();
}

public static int getHeight(){
    return height;
}

public enum AdjustMode{
    UNKNOWN, PAN, NOTHING, RESIZE
}

private interface ListenerDetails{
    AdjustMode getActivityAdjustMode();

    void setActivityAdjustMode(ActivityBase activity, AdjustMode adjustMode);

    default void postVisible(boolean flag){
        ObservableValue<Boolean>.Access access = keyBoardVisibility.obtainAccess(myClass(), null);
        access.setValueIfDifferent(flag);
    }

    int getActivityHashCode();

    void dismiss(ActivityBase activity);

}

private static class ListenerDetails_before30_R extends PopupWindow implements ListenerDetails{
    private final static int VISIBILITY_THRESHOLD = AppDisplay.convertDpToPx(100);
    private final static int SHOW_DELAY_ms = 100;
    AdjustMode adjustMode = AdjustMode.UNKNOWN;
    int hashCode;
    ViewTreeEvent.Subscription globalLayoutSubscription = null;
    ListenerDetails_before30_R(ActivityBase activity){
        super(activity.getApplicationContext());
        this.hashCode = activity.hashCode();
        createPopup(activity);
        listenLayout(activity);
        show(activity);
    }
    void createPopup(ActivityBase activity){
        FrameLayout view = FrameLayout.newMM(activity.getApplicationContext());
        view.setBackgroundColor(AppContext.getResources().getColorARGB(R.color.Transparent));
        setContentView(view);
    }
    void listenLayout(ActivityBase activity){
                globalLayoutSubscription = ViewTreeEvent.onPreDraw(getContentView(), new SupplierSubscription<Boolean>(){
            @Override
            public Boolean onComplete(){
                View view = getContentView();
                int heightDiff = AppDisplay.getSizeOriented().getHeight() - view.getHeight();
                boolean imeVisible = heightDiff > VISIBILITY_THRESHOLD;
                if(imeVisible){
                    AppKeyboard.height = heightDiff;
                }
                postVisible(imeVisible);
                return true;
            }
        });
    }
    void show(ActivityBase activity){
//        View view = getContentView();
//        view.setBackgroundColor(UtilsAlpha.color(AppContext.getResources().getColorARGB(R.color.Red), 0.3f));
        setWidth(MATCH_PARENT);
        setHeight(MATCH_PARENT);
        setSoftInputMode(SOFT_INPUT_ADJUST_RESIZE);
        setInputMethodMode(INPUT_METHOD_NEEDED);
        setBackgroundDrawable(null);
        setFocusable(false);
        setTouchable(false);
        setClippingEnabled(true);
        View viewRoot = activity.getViewRoot();
        RunnableW runnable = new RunnableW(){
            @Override
            public void runSafe() throws Throwable{
                showAtLocation(viewRoot, Gravity.NO_GRAVITY, 0, 0);
            }
        };
        Handler.MAIN().post(this, SHOW_DELAY_ms, runnable);
    }
    @Override
    public AdjustMode getActivityAdjustMode(){
        return adjustMode;
    }
    @Override
    public void setActivityAdjustMode(ActivityBase activity, AdjustMode adjustMode){
        if(this.adjustMode == adjustMode){
            return;
        }
        this.adjustMode = adjustMode;
        switch(adjustMode){
            case PAN:{
                activity.getWindow().setSoftInputMode(SOFT_INPUT_ADJUST_PAN);
            }
            break;
            case NOTHING:{
                activity.getWindow().setSoftInputMode(SOFT_INPUT_ADJUST_NOTHING);
            }
            break;
            case RESIZE:{
                activity.getWindow().setSoftInputMode(SOFT_INPUT_ADJUST_RESIZE);
            }
            break;
        }
    }
    @Override
    public int getActivityHashCode(){
        return hashCode;
    }
    @Override
    public void dismiss(ActivityBase activity){
        if(globalLayoutSubscription != null){
            globalLayoutSubscription.unsubscribe();
        }
        super.dismiss();
    }
    @Override
    protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
        super.finalize();
    }

}

@RequiresApi(api = Build.VERSION_CODES.R)
private static class ListenerDetails_after30_R implements ListenerDetails{
    AdjustMode adjustMode = AdjustMode.UNKNOWN;
    int hashCode;
    View.OnApplyWindowInsetsListener listenerInsets = null;
    ListenerDetails_after30_R(ActivityBase activity){
DebugTrack.start().create(this).end();
        this.hashCode = activity.hashCode();
        listenLayout(activity);
    }
    void listenLayout(ActivityBase activity){
        listenerInsets = new View.OnApplyWindowInsetsListener() {
            @Override
            public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                boolean imeVisible = insets.isVisible(WindowInsetsCompat.Type.ime());
                if (imeVisible) {
                    AppKeyboard.height = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom - insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom;
                }
                if (adjustMode == AdjustMode.RESIZE) {
                    ListenerDetails_after30_R.this.resize(imeVisible, v, insets);
                }
                ListenerDetails_after30_R.this.postVisible(imeVisible);
                return insets;
            }
        };
        ViewTreeEvent.onLayout(activity.getWindow().getDecorView(), new RunnableSubscription() {
            @Override
            public void onComplete() {
                unsubscribe();
                activity.getWindow().getDecorView().setOnApplyWindowInsetsListener(listenerInsets);
            }
        });
    }
    @Override
    public AdjustMode getActivityAdjustMode(){
        return adjustMode;
    }
    @Override
    public void setActivityAdjustMode(ActivityBase activity, AdjustMode adjustMode){
        if(this.adjustMode == adjustMode){
            return;
        }
        this.adjustMode = adjustMode;
        switch(adjustMode){
            case PAN:{
                activity.getWindow().setSoftInputMode(SOFT_INPUT_ADJUST_PAN);
            }
            break;
            case NOTHING:{
                activity.getWindow().setSoftInputMode(SOFT_INPUT_ADJUST_NOTHING);
            }
            break;
            case RESIZE:{
                activity.getWindow().setSoftInputMode(SOFT_INPUT_ADJUST_NOTHING);
            }
            break;
        }
    }
    void resize(boolean imeVisible, View v, WindowInsets insets){
        if(imeVisible){
            int imeHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom;
            int navigationBarHeight = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom;
            v.setPadding(0, 0, 0, imeHeight - navigationBarHeight);
        } else {
            v.setPadding(0, 0, 0, 0);
        }
    }
    @Override
    public int getActivityHashCode(){
        return hashCode;
    }
    @Override
    public void dismiss(ActivityBase activity){
        if(listenerInsets != null){
            activity.getWindow().getDecorView().setOnApplyWindowInsetsListener(null);
            listenerInsets = null;
        }
    }
    @Override
    protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
        super.finalize();
    }

}


}
