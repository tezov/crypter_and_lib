/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.application;

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

import static com.tezov.lib_java_android.application.SharePreferenceKey.SP_APP_CURRENT_LAUNCH_BOOL;
import static com.tezov.lib_java_android.application.SharePreferenceKey.SP_APP_FIRST_LAUNCH_BOOL;
import static com.tezov.lib_java_android.application.SharePreferenceKey.SP_APP_PREVIOUS_LAUNCH_BOOL;

import android.content.Intent;

import com.tezov.lib_java_android.BuildConfig;
import com.tezov.lib_java.application.AppUUIDGenerator;
import com.tezov.lib_java.application.AppMemory;
import com.tezov.lib_java.async.Handler;
import com.tezov.lib_java_android.authentification.UserAuth;
import com.tezov.lib_java.file.FileQueue;
import com.tezov.lib_java.generator.uid.UUID;
import com.tezov.lib_java.toolbox.Compare;
import com.tezov.lib_java_android.ui.misc.TransitionManagerAnimation;
import com.tezov.lib_java_android.ui.navigation.NavigationHelper;
import com.tezov.lib_java_android.ui.notification.NotificationManager;

public class Application{
protected static State state = null;
protected static ConnectivityManager connectivityManager = null;
protected static NavigationHelper navigationHelper = null;
protected static TransitionManagerAnimation transitionManager = null;
protected static NotificationManager notificationManager = null;
protected static SharedPreferences sharedPreferences = null;
protected static FileQueue fileQueue = null;
protected static UserAuth userAuth = null;
protected Application(){
}

public static void onMainActivityStart(ApplicationSystem app, Intent source, boolean isRestarted){
    if(!isRestarted){
        Handler.onMainActivityStart();
        AppInfo.onMainActivityStart();
        if(BuildConfig.DEBUG_ONLY){
            if(Compare.isTrue(AppConfig.getBoolean(AppConfigKey.DEBUG_LOG_CONFIG_ENCRYPTED.getId()))){
                AppConfig.toDebugLogPropertiesToJsonEncrypted(app);
            }
            AppMemory.init();
        }
    }
    AppKeyboard.onMainActivityStart();
}
public static void onApplicationPause(ApplicationSystem app){

}
public static void onApplicationClose(ApplicationSystem app){
    AppKeyboard.onApplicationClose();
    Handler.onApplicationClose();
}

public static boolean hasState(){
    return getState() != null;
}
public static <S extends State> S getState(){
    return (S)state;
}
public static boolean hasParam(){
    return hasState() && getState().hasParam();
}
public static <P extends Param> P getParam(){
    if(!hasState()){
        return null;
    } else {
        return (P)getState().getParam();
    }
}
public static boolean hasMethod(){
    return hasState() && getState().hasMethod();
}
public static <M extends Method> M getMethod(){
    if(!hasState()){
        return null;
    } else {
        return (M)getState().getMethod();
    }
}

public static ConnectivityManager connectivityManager(){
    return connectivityManager;
}
public static NavigationHelper navigationHelper(){
    return navigationHelper;
}
public static TransitionManagerAnimation animationManager(){
    return transitionManager;
}
public static FileQueue fileQueue(){
    return fileQueue;
}
public static UserAuth userAuth(){
    return userAuth;
}

public static void sharedPreferences(SharedPreferences sp){
    sharedPreferences = sp;
    Boolean isFirstLaunch = sp.getBoolean(SP_APP_FIRST_LAUNCH_BOOL);
    if(isFirstLaunch == null){
        sp.put(SP_APP_FIRST_LAUNCH_BOOL, true);
    } else if(isFirstLaunch){
        sp.put(SP_APP_FIRST_LAUNCH_BOOL, false);
    }
    Long lastOpening = sp.getLong(SP_APP_CURRENT_LAUNCH_BOOL);
    long now = Clock.MilliSecond.now();
    if(lastOpening == null){
        lastOpening = now;
    }
    sp.put(SP_APP_CURRENT_LAUNCH_BOOL, now);
    sp.put(SP_APP_PREVIOUS_LAUNCH_BOOL, lastOpening);
}
public static SharedPreferences sharedPreferences(){
    return sharedPreferences;
}
public static NotificationManager notificationManager(){
    return notificationManager;
}

public static class State extends com.tezov.lib_java_android.ui.state.State<Param, Method>{
    protected UUID sessionUid = null;
    public void onMainActivityStart(ApplicationSystem app, Intent source){
        sessionUid = AppUUIDGenerator.next();
    }
    public void onApplicationClose(ApplicationSystem app){
        sessionUid = null;
    }
    public UUID sessionUid(){
        return sessionUid;
    }
    @Override
    protected Param newParam(){
        return new Param();
    }
    @Override
    protected Method newMethod(){
        return new Method();
    }
}
public static class Param extends com.tezov.lib_java_android.ui.state.Param{}
public static class Method extends com.tezov.lib_java_android.ui.state.Method{}


}

