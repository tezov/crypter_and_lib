/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.application;

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

import static com.tezov.lib_java_android.application.AppContext.getActivity;
import static com.tezov.lib_java_android.application.AppContext.getResources;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Insets;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.WindowMetrics;

import androidx.annotation.RequiresApi;

import com.tezov.lib_java.async.notifier.observer.value.ObserverValue;
import com.tezov.lib_java_android.toolbox.PostToHandler;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.primaire.Size;
import com.tezov.lib_java.type.runnable.RunnableW;
import com.tezov.lib_java_android.ui.activity.ActivityBase;
import com.tezov.lib_java_android.ui.misc.Orientation;

import java.util.Locale;

public class AppDisplay{
private static int disableTouchScreenNumber = 0;
private static int disableRotationNumber = 0;
private static int orientation = ActivityInfo.SCREEN_ORIENTATION_USER;
private static int orientationMem = orientation;

static{
    AppContext.observeOnActivityChange(new ObserverValue<ActivityBase>(myClass()){
        @Override
        public void onComplete(ActivityBase activity){
            if(!isTouchScreenEnable()){
                activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
            if(!isRotationEnable()){
                activity.setRequestedOrientation(AppDisplay.orientationMem);
            }
        }
    });
}

private AppDisplay(){
}

public static Class<AppDisplay> myClass(){
    return AppDisplay.class;
}

public static int getRotation(){
    if(VersionSDK.isSupEqualTo30_R()){
        return getRotation_after30_R();
    } else {
        return getRotation_before30_R();
    }
}
@RequiresApi(api = Build.VERSION_CODES.R)
private static int getRotation_after30_R(){
    return AppContext.get().getDisplay().getRotation();
}
@SuppressWarnings("deprecation")
private static int getRotation_before30_R(){
    return AppContext.getActivity().getWindowManager().getDefaultDisplay().getRotation();
}
public static int getOrientationAngle(){
    return Orientation.get(getRotation());
}
public static int getConfigurationOrientation(){
    return getActivity().getResources().getConfiguration().orientation;
}
public static boolean isPortrait(){
    return getConfigurationOrientation() == Configuration.ORIENTATION_PORTRAIT;
}
public static boolean isLandscape(){
    return getConfigurationOrientation() == Configuration.ORIENTATION_LANDSCAPE;
}

public static Size getSize(){
    Size size = getSizeOriented();
    if(isPortrait()){
        return size;
    } else {
        return size.swap();
    }
}
public static Size getSizeOriented(){
    if(VersionSDK.isSupEqualTo30_R()){
        return getSizeOriented_after30_R();
    } else {
        return getSizeOriented_before30_R();
    }
}
@RequiresApi(api = Build.VERSION_CODES.R)
private static Size getSizeOriented_after30_R(){
    WindowMetrics windowMetrics = AppContext.getActivity().getWindowManager().getCurrentWindowMetrics();
    Insets insets = windowMetrics.getWindowInsets().getInsetsIgnoringVisibility(WindowInsets.Type.systemBars());
    int width = windowMetrics.getBounds().width();
    int height = windowMetrics.getBounds().height() - insets.top - insets.bottom;
    return new Size(width, height);
}
@SuppressWarnings("deprecation")
private static Size getSizeOriented_before30_R(){
    View root = getActivity().getViewRoot();
    return new Size(root.getWidth(), root.getHeight());
}

public static float getRatio(){
    Size size = getSizeOriented();
    return ((float)size.getHeight()) / ((float)size.getWidth());
}

public static int convertDpToPx(float dp){
    return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, AppContext.getResources().get().getDisplayMetrics());
}

public static float convertPxToDp(int px){
    return px / getResources().get().getDisplayMetrics().density;
}

public static int convertSpToPx(float sp){
    return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, AppContext.getResources().get().getDisplayMetrics());
}

public static float convertPxToSp(int px){
    return px / getResources().get().getDisplayMetrics().scaledDensity;
}

public static float convertDpToSp(float dp){
    return convertDpToPx(dp) / getResources().get().getDisplayMetrics().scaledDensity;
}

public static float convertSpToDp(float sp){
    return convertPxToDp((int)(sp * getResources().get().getDisplayMetrics().scaledDensity));
}

public static float convertXmlSizeToPx(int resourceId){
    return getResources().get().getDimensionPixelSize(resourceId);
}

public static void disableTouchScreen(Object requester){
    PostToHandler.of(getActivity(), new RunnableW(){
        @Override
        public void runSafe() throws Throwable{
DebugLog.start().send(myClass(), "TouchScreen: DISABLED " + disableTouchScreenNumber + " by " + DebugTrack.getFullSimpleName(requester)).end();
            disableTouchScreenNumber += 1;
            if(disableTouchScreenNumber == 1){
                getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        }
    });
}

public static void enableTouchScreen(Object requester){
    PostToHandler.of(getActivity(), new RunnableW(){
        @Override
        public void runSafe() throws Throwable{
DebugLog.start().send(myClass(), "TouchScreen: ENABLED " + disableTouchScreenNumber + " by " + DebugTrack.getFullSimpleName(requester)).end();
            disableTouchScreenNumber -= 1;
            if(disableTouchScreenNumber == 0){
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            } else if(disableTouchScreenNumber < 0){
DebugException.start().log("Try to enable touchscreen, but already enable").end();
                disableTouchScreenNumber = 0;
            }
        }
    });
}

synchronized public static boolean isTouchScreenEnable(){
    return disableTouchScreenNumber == 0;
}

synchronized public static void disableRotation(Object requester){
    PostToHandler.of(getActivity(), new RunnableW(){
        @Override
        public void runSafe() throws Throwable{
DebugLog.start().send(myClass(), "Rotation: DISABLED " + disableRotationNumber + " by " + DebugTrack.getFullSimpleName(requester)).end();
            disableRotationNumber += 1;
            if(disableRotationNumber == 1){
                orientationMem = orientation;
                updateOrientationConfiguration(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
            }
        }
    });
}

synchronized public static void enableRotation(Object requester){
    PostToHandler.of(getActivity(), new RunnableW(){
        @Override
        public void runSafe() throws Throwable{
DebugLog.start().send(myClass(), "Rotation: ENABLED " + disableRotationNumber + " by " + DebugTrack.getFullSimpleName(requester)).end();
            disableRotationNumber -= 1;
            if(disableRotationNumber == 0){
                updateOrientationConfiguration(AppDisplay.orientationMem);
            } else if(disableRotationNumber < 0){
DebugException.start().log("Try to enable rotation, but already enable").end();
                disableRotationNumber = 0;
            }
        }
    });
}

synchronized public static boolean isRotationEnable(){
    return disableRotationNumber == 0;
}

private static void updateOrientationConfiguration(int orientation){
    AppDisplay.orientation = orientation;
    getActivity().setRequestedOrientation(orientation);
}

synchronized public static void setOrientationUser(boolean force){
    AppDisplay.orientationMem = ActivityInfo.SCREEN_ORIENTATION_USER;
    if(force || isRotationEnable()){
        updateOrientationConfiguration(AppDisplay.orientationMem);
    }
}

synchronized public static void setOrientationPortrait(boolean force){
    AppDisplay.orientationMem = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    if(force || isRotationEnable()){
        updateOrientationConfiguration(AppDisplay.orientationMem);
    }
}

synchronized public static void setOrientationLandscape(boolean force){
    AppDisplay.orientationMem = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
    if(force || isRotationEnable()){
        updateOrientationConfiguration(AppDisplay.orientationMem);
    }
}

public static void toDebugLog(){
DebugLog.start().send("size:" + getSizeOriented() + " ratio:" + String.format(Locale.US, "%.2f", getRatio())).end();
}

}
