/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.crypter.activity.activityFilter;

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
import com.tezov.lib_java_android.application.AppContext;

import androidx.fragment.app.Fragment;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.crypter.activity.ActivityMain;
import com.tezov.lib_java.async.notifier.task.TaskValue;

import static com.tezov.lib_java_android.application.ApplicationSystem.State.CLOSED;
import static com.tezov.lib_java_android.application.ApplicationSystem.State.NOT_INITIALISED;
import static com.tezov.lib_java_android.application.ApplicationSystem.State.PAUSED;
import static com.tezov.lib_java_android.application.ApplicationSystem.State.RESTARTING;
import static com.tezov.lib_java_android.application.ApplicationSystem.State.STARTED;
import static com.tezov.lib_java_android.ui.navigation.NavigatorManager.NavigatorKey.FRAGMENT;

import android.app.Activity;
import android.content.Intent;

import com.tezov.crypter.application.ApplicationSystem;
import com.tezov.crypter.dialog.DialogExportKey;
import com.tezov.crypter.dialog.DialogImportKey;
import com.tezov.crypter.fragment.FragmentCipherFile;
import com.tezov.crypter.fragment.FragmentCipherText;
import com.tezov.crypter.navigation.NavigationArguments;
import com.tezov.crypter.navigation.NavigationHelper;
import com.tezov.lib_java.async.notifier.observer.event.ObserverEvent;
import com.tezov.lib_java.async.notifier.observer.state.ObserverState;
import com.tezov.lib_java.async.notifier.observer.value.ObserverValueE;
import com.tezov.lib_java.toolbox.Nullify;
import com.tezov.lib_java_android.ui.dialog.DialogNavigable;
import com.tezov.lib_java_android.ui.fragment.FragmentNavigable;
import com.tezov.lib_java_android.ui.navigation.Navigate;
import com.tezov.lib_java_android.ui.navigation.NavigationOption;
import com.tezov.lib_java_android.ui.navigation.NavigatorManager;
import com.tezov.lib_java_android.util.UtilsIntent;

public class ActivityFilterDispatcher{
public final static String DEEP_LINK = "https://com.tezov.crypter/";
public final static String EXTRA_REQUEST = "EXTRA_REQUEST";
public final static String EXTRA_REQUEST_VIEW = "EXTRA_REQUEST_VIEW";
public final static String EXTRA_REQUEST_LINK = "EXTRA_REQUEST_LINK";
public final static String EXTRA_TARGET_FRAGMENT = "EXTRA_REQUEST_FRAGMENT";
public final static String EXTRA_TARGET_DIALOG = "EXTRA_TARGET_DIALOG";

private static Class<ActivityFilterDispatcher> myClass(){
    return ActivityFilterDispatcher.class;
}

public static String makeDeepLink(String extension){
    return DEEP_LINK + extension + "/";
}
public static String makeDeepLink(String extension, String data){
    if(Nullify.string(data) == null){
        return null;
    } else {
        return makeDeepLink(extension) + data;
    }
}
public static String removeDeepLinkIfStartWith(String extension, String data){
    if(Nullify.string(data) == null){
        return null;
    } else {
        String link = makeDeepLink(extension);
        if(data.startsWith(link)){
            data = data.replace(link, "");
        }
        return data;
    }
}
public static String removeDeepLink(String extension, String data){
    String link = makeDeepLink(extension);
    data = data.replace(link, "");
    return data;
}
public static boolean startWithDeepLink(String extension, String data){
    if(Nullify.string(data) == null){
        return false;
    } else {
        String link = makeDeepLink(extension);
        return data.startsWith(link);
    }
}

private static boolean hasExtraRequest(Intent intent){
    return (intent != null) && intent.hasExtra(EXTRA_REQUEST);
}

public static void startActivityFrom(Activity activity, boolean redirectIntent){
    boolean isDone = false;
    ApplicationSystem application = (ApplicationSystem)activity.getApplication();
DebugLog.start().track(myClass(), redirectIntent ? "intent redirected / " + application.getState() : "intent not redirected / " + application.getState()).end();
    if(application.getState() == NOT_INITIALISED){
        isDone = true;
        application.startMainActivity(activity, redirectIntent, true).observe(new ObserverValueE<>(myClass()){
            @Override
            public void onComplete(com.tezov.lib_java_android.application.ApplicationSystem.State state){
                if(state != STARTED){
DebugException.start().log("invalid state " + state).end();
                    System.exit(0);
                }
            }
            @Override
            public void onException(com.tezov.lib_java_android.application.ApplicationSystem.State state, Throwable e){
DebugException.start().log(e).end();
                System.exit(0);
            }
        });
    }
    else if(application.getState() == STARTED){
        isDone = true;
        application.reachCurrentActivity(activity, redirectIntent).observe(new ObserverValueE<>(myClass()){
            @Override
            public void onComplete(com.tezov.lib_java_android.application.ApplicationSystem.State state){
                if(state != STARTED){
DebugException.start().log("invalid state " + state).end();
                    System.exit(0);
                }
            }
            @Override
            public void onException(com.tezov.lib_java_android.application.ApplicationSystem.State state, Throwable e){
DebugException.start().log(e).end();
                System.exit(0);
            }
        });
    }
    else if(application.getState() == PAUSED){
        isDone = true;
        application.wakeUpMainActivity(activity).observe(new ObserverValueE<>(myClass()){
            @Override
            public void onComplete(com.tezov.lib_java_android.application.ApplicationSystem.State state){
                if(state == STARTED){
                    ActivityFilterDispatcher.onNewIntent(AppContext.getActivity());
                }
                else{
DebugException.start().log("invalid state " + state).end();
                    System.exit(0);
                }
            }
            @Override
            public void onException(com.tezov.lib_java_android.application.ApplicationSystem.State state, Throwable e){
DebugException.start().log(e).end();
                System.exit(0);
            }
        });
    }
    else if(application.getState() == RESTARTING){
        TaskValue<com.tezov.lib_java_android.application.ApplicationSystem.State>.Observable task = application.getTaskObservable();
        if(task != null){
            isDone = true;
            task.observe(new ObserverValueE<>(myClass()){
                @Override
                public void onComplete(com.tezov.lib_java_android.application.ApplicationSystem.State state){
                    if(state == STARTED){
                        ActivityFilterDispatcher.onNewIntent(AppContext.getActivity());
                    }
                    else{
DebugException.start().log("invalid state " + state).end();
                        System.exit(0);
                    }
                }
                @Override
                public void onException(com.tezov.lib_java_android.application.ApplicationSystem.State state, Throwable e){
DebugException.start().log(e).end();
                    System.exit(0);
                }
            });
        }
    }
    if(!isDone) {
DebugException.start().log("application state " + application.getState() + " not handled").end();
        activity.finish();
    }
}
public static void onNewIntent(android.app.Activity activity){
DebugLog.start().track(myClass(), DebugTrack.getFullSimpleName(activity)).end();
    if(activity instanceof ActivityMain){
        dispatchToFragment((ActivityMain)activity);
    } else {
        Intent intent = activity.getIntent();
        if(hasExtraRequest(intent)){
            Navigate.Back(new NavigationOption().setRedirectIntent(true));
        }
    }
}
public static boolean onPrepare(android.app.Activity activity){
DebugLog.start().track(myClass(), DebugTrack.getFullSimpleName(activity)).end();
    if(activity instanceof ActivityMain){
        return dispatchToFragment((ActivityMain)activity);
    } else {
        return false;
    }
}
private static boolean dispatchToFragment(ActivityMain activity){
    Intent intent = activity.getIntent();
    if(!hasExtraRequest(intent)){
        return false;
    }
    NavigationArguments arguments = NavigationArguments.create();
    String extraRequest = intent.getStringExtra(EXTRA_REQUEST);
    if(EXTRA_REQUEST_VIEW.equals(extraRequest)){
        arguments.setUris(UtilsIntent.getUris(intent, false));
    } else if(EXTRA_REQUEST_LINK.equals(extraRequest)){
        arguments.setData(intent.getData().toString());
    } else {
        return false;
    }
    Class<? extends FragmentNavigable> targetFragment;
    String nameFragment = intent.getStringExtra(EXTRA_TARGET_FRAGMENT);
    if(nameFragment != null){
        if(nameFragment.equals(FragmentCipherFile.class.getSimpleName())){
            targetFragment = FragmentCipherFile.class;
        } else if(nameFragment.equals(FragmentCipherText.class.getSimpleName())){
            targetFragment = FragmentCipherText.class;
        } else {
            return false;
        }
    } else {
        targetFragment = null;
    }
    Class<? extends DialogNavigable> targetDialog;
    String nameDialog = intent.getStringExtra(EXTRA_TARGET_DIALOG);
    if(nameDialog != null){
        if(nameDialog.equals(DialogImportKey.class.getSimpleName())){
            targetDialog = DialogImportKey.class;
        } else if(nameDialog.equals(DialogExportKey.class.getSimpleName())){
            targetDialog = DialogExportKey.class;
        } else {
            return false;
        }
        arguments.setTarget(targetDialog);
    } else {
        arguments.setTarget(targetFragment);
    }
DebugLog.start().track(myClass(), nameFragment + " / " + nameDialog).end();
    Navigate.CloseDialogAll(new NavigationOption().setPostCancel_NavBack(true)).observe(new ObserverState(activity){
        @Override
        public void onComplete(){
            FragmentNavigable currentRef = Navigate.getCurrentFragmentRef();
            if(currentRef != null){
                Class<? extends FragmentNavigable> current = currentRef.getClass();
                if(current == targetFragment){
                    currentRef.onNewNavigationArguments(arguments);
                    return;
                }
                if((targetFragment == null) && ((FragmentCipherFile.class == current) || (FragmentCipherText.class == current))){
                    currentRef.onNewNavigationArguments(arguments);
                    return;
                }
            }
            NavigationHelper.DestinationKey.Is target;
            if(targetFragment == null){
                target = Navigate.identify(FragmentCipherFile.class);
            } else {
                target = Navigate.identify(targetFragment);
            }
            Navigate.observe(new ObserverEvent<>(this, FRAGMENT){
                @Override
                public void onComplete(NavigatorManager.NavigatorKey.Is navigator, NavigatorManager.Event event){
                    if(event == NavigatorManager.Event.NAVIGATE_TO_CONFIRMED){
                        unsubscribe();
                        activity.getToolbarBottom().setChecked(target.getId());
                    }
                }
            });
            Navigate.To(target, arguments);
        }
    });
    return true;
}

}
