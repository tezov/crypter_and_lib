/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.crypter.navigation;

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
import static com.tezov.lib_java_android.ui.misc.TransitionManager.Name.FADE;
import static com.tezov.lib_java_android.ui.misc.TransitionManagerAnimation.Name.SLIDE_OVER_LEFT;
import static com.tezov.lib_java_android.ui.navigation.NavigatorManager.NavigatorKey.ACTIVITY;
import static com.tezov.lib_java_android.ui.navigation.NavigatorManager.NavigatorKey.DIALOG;
import static com.tezov.lib_java_android.ui.navigation.NavigatorManager.NavigatorKey.FRAGMENT;

import com.tezov.crypter.R;
import com.tezov.crypter.activity.ActivityPreference;
import com.tezov.crypter.activity.ActivityPrivacyPolicy;
import com.tezov.crypter.fragment.FragmentCipherFile;
import com.tezov.crypter.fragment.FragmentCipherText;
import com.tezov.crypter.fragment.FragmentInfo;
import com.tezov.lib_java.async.Handler;
import com.tezov.lib_java.async.notifier.observer.event.ObserverEvent;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.runnable.RunnableW;
import com.tezov.lib_java_android.ui.dialog.DialogNavigable;
import com.tezov.lib_java_android.ui.navigation.Navigate;
import com.tezov.lib_java_android.ui.navigation.NavigationOption;
import com.tezov.lib_java_android.ui.navigation.NavigatorManager;
import com.tezov.lib_java_android.ui.navigation.defNavigable;
import com.tezov.lib_java_android.ui.navigation.destination.DestinationManager;
import com.tezov.lib_java_android.ui.navigation.navigator.NavigatorActivity;
import com.tezov.lib_java_android.ui.navigation.navigator.NavigatorDialog;
import com.tezov.lib_java_android.ui.navigation.navigator.NavigatorFragment;

import activity.ActivityMain_bt;

public class NavigationHelper extends com.tezov.lib_java_android.ui.navigation.NavigationHelper{
private final static long DIALOG_CLOSE_DELAYED_ms = 500;

public NavigationHelper(){
    init();
}
public static void close(DialogNavigable dialog, boolean waitNavigateConfirmed){
    if(!waitNavigateConfirmed){
        dialog.close();
    } else {
        Navigate.observe(new ObserverEvent<NavigatorKey.Is, Event>(dialog){
            @Override
            public void onComplete(NavigatorManager.NavigatorKey.Is is, NavigatorManager.Event event){
                if(event == NavigatorManager.Event.NAVIGATE_TO_CONFIRMED){
                    unsubscribe();
                    Handler.PRIMARY().post(this, DIALOG_CLOSE_DELAYED_ms, new RunnableW(){
                        @Override
                        public void runSafe(){
                            dialog.close();
                        }
                    });
                }
            }
        });
    }
}
private void init(){
    DestinationManager destinationManager = getDestinationManager();

    NavigatorDialog navigatorDialog = new NavigatorDialog(DIALOG);
    addNavigator(navigatorDialog);

    NavigationOption option = new NavigationOption().setKeepInStack(false);
    NavigatorFragment navigatorFragment = new NavigatorFragment(FRAGMENT, R.id.container_fragment).doNotPopOutTheLastFragment(true);
    addNavigator(navigatorFragment);
    //MENU
    destinationManager.addDestination(null, ActivityMain_bt.class, FragmentInfo.class).setTransition(FADE).setOption(option);
    destinationManager.addDestination(null, FragmentCipherFile.class, FragmentInfo.class).setTransition(FADE).setOption(option);
    destinationManager.addDestination(null, FragmentCipherText.class, FragmentInfo.class).setTransition(FADE).setOption(option);

    //FILE CIPHER
    destinationManager.addDestination(null, ActivityMain_bt.class, FragmentCipherFile.class).setTransition(FADE).setOption(option);
    destinationManager.addDestination(null, FragmentInfo.class, FragmentCipherFile.class).setTransition(FADE).setOption(option);
    destinationManager.addDestination(null, FragmentCipherText.class, FragmentCipherFile.class).setTransition(FADE).setOption(option);

    //TEXT CIPHER
    destinationManager.addDestination(null, ActivityMain_bt.class, FragmentCipherText.class).setTransition(FADE).setOption(option);
    destinationManager.addDestination(null, FragmentInfo.class, FragmentCipherText.class).setTransition(FADE).setOption(option);
    destinationManager.addDestination(null, FragmentCipherFile.class, FragmentCipherText.class).setTransition(FADE).setOption(option);

    NavigatorActivity navigatorActivity = new NavigatorActivity(ACTIVITY);
    //ACTIVITY ENTRY POINT
    addNavigator(navigatorActivity);
    // MAIN
    destinationManager.addDestination(null, null, ActivityMain_bt.class);
    // POLICY PRIVACY
    destinationManager.addDestination(null, null, ActivityPrivacyPolicy.class).setTransition(SLIDE_OVER_LEFT);
    // PREFERENCE
    destinationManager.addDestination(null, null, ActivityPreference.class).setTransition(SLIDE_OVER_LEFT);
}
@Override
final public NavigatorKey.Is getNavigatorKey(Class<? extends defNavigable> type){
    NavigatorKey.Is navigatorKey = super.getNavigatorKey(type);

    if(navigatorKey == null){
DebugException.start().explode("this object does not have navigator key declared " + DebugTrack.getFullName(type)).end();
    }

    return navigatorKey;
}
@Override
public NavigatorKey.Is getNavigatorKey(NavigatorManager.DestinationKey.Is destination){
    if(destination == DestinationKey.MAIN){
        return ACTIVITY;
    }
    if(destination == DestinationKey.PRIVACY_POLICY){
        return ACTIVITY;
    }
    if(destination == DestinationKey.PREFERENCE){
        return ACTIVITY;
    }

    if(destination == DestinationKey.INFO){
        return FRAGMENT;
    }
    if(destination == DestinationKey.CIPHER_FILE){
        return FRAGMENT;
    }
    if(destination == DestinationKey.CIPHER_TEXT){
        return FRAGMENT;
    }

DebugException.start().explode("destination is not declared " + DebugTrack.getFullName(destination)).end();

    return null;
}
@Override
public <I extends NavigatorManager.DestinationKey.Is> I identify(Class<? extends defNavigable> type){
    if(type == ActivityMain_bt.class){
        return (I)DestinationKey.MAIN;
    }
    if(type == ActivityPrivacyPolicy.class){
        return (I)DestinationKey.PRIVACY_POLICY;
    }
    if(type == ActivityPreference.class){
        return (I)DestinationKey.PREFERENCE;
    }
    if(type == FragmentInfo.class){
        return (I)DestinationKey.INFO;
    }
    if(type == FragmentCipherFile.class){
        return (I)DestinationKey.CIPHER_FILE;
    }
    if(type == FragmentCipherText.class){
        return (I)DestinationKey.CIPHER_TEXT;
    }

DebugException.start().explode("type is not declared " + DebugTrack.getFullName(type)).end();


    return null;
}

public interface DestinationKey extends NavigatorManager.DestinationKey{
    NavigatorManager.DestinationKey.Is MAIN = new NavigatorManager.DestinationKey.Is("MAIN");
    NavigatorManager.DestinationKey.Is PRIVACY_POLICY = new NavigatorManager.DestinationKey.Is("PRIVACY_POLICY");
    NavigatorManager.DestinationKey.Is PREFERENCE = new NavigatorManager.DestinationKey.Is("PREFERENCE");
    Is INFO = new Is("INFO", R.id.mn_info);
    Is CIPHER_FILE = new Is("CIPHER_FILE", R.id.mn_cipher_file);
    Is CIPHER_TEXT = new Is("CIPHER_TEXT", R.id.mn_cipher_text);

    static Is find(String name){
        return Is.findTypeOf(Is.class, name);
    }

    class Is extends NavigatorManager.DestinationKey.Is{
        private final int id;
        public Is(String name, int id){
            super(name);
            this.id = id;
        }
        public int getId(){
            return id;
        }

    }

}

}
