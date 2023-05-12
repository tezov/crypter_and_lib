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
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.util.UtilsString;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import androidx.fragment.app.Fragment;
import static com.tezov.lib_java.application.AppConfigKey.SPEC_MUTUAL;
import static com.tezov.lib_java.util.UtilsNull.NULL_OBJECT;
import static com.tezov.lib_java_android.application.SharePreferenceKey.SP_APP_CURRENT_LAUNCH_BOOL;
import static com.tezov.lib_java_android.application.SharePreferenceKey.SP_APP_FIRST_INSTALL_LONG;
import static com.tezov.lib_java_android.application.SharePreferenceKey.SP_APP_FIRST_LAUNCH_BOOL;
import static com.tezov.lib_java_android.application.SharePreferenceKey.SP_APP_PREVIOUS_LAUNCH_BOOL;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import androidx.core.content.res.ResourcesCompat;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.tezov.lib_java.async.Handler;
import com.tezov.lib_java.cipher.key.KeyMutual;
import com.tezov.lib_java.cipher.misc.PasswordCipher;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.generator.uid.UUID;
import com.tezov.lib_java.generator.uid.UidBase;
import com.tezov.lib_java.generator.uid.defUid;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.toolbox.Compare;
import com.tezov.lib_java.type.primitive.BytesTo;
import com.tezov.lib_java.type.runnable.RunnableW;
import com.tezov.lib_java_android.ui.view.status.StatusParam;
import com.tezov.lib_java_android.ui.view.status.StatusSnackBar;
import com.tezov.lib_java_android.ui.view.status.StatusToast;

public class AppInfo{
public static final String SIGNATURE_APP_PREFIX = "app-id-";
public static UUID guid = null;
public static Object duid = NULL_OBJECT;

protected AppInfo(){
}
public static void onMainActivityStart(){
    if(guid == null){
        guid = UUID.fromHexString(com.tezov.lib_java.application.AppConfig.getString(AppConfigKey.GUID.getId()));
        Handler.SECONDARY().post(myClass(), new RunnableW(){
            @Override
            public void runSafe() throws Throwable{
                AdvertisingIdClient.Info info = AdvertisingIdClient.getAdvertisingIdInfo(AppContext.get());
                String id = info.getId().replaceAll("-", "");
                duid = UidBase.fromHexString(id.toUpperCase());
            }
            @Override
            public void onException(Throwable e){
                duid = null;
            }
        });
    }
}
private static Class<AppInfo> myClass(){
    return AppInfo.class;
}

public static boolean isDuidLoaded(){
    return duid != NULL_OBJECT;
}
public static boolean isDuidNotNull(){
    return isDuidLoaded() && (duid != null);
}
public static defUid getDUID(){
    return (defUid)duid;
}
public static UUID getGUID(){
    return guid;
}
public static defUid getDuidOrGuid(){
    defUid uid;
    if(isDuidNotNull()){
        uid = (defUid)duid;
    } else {
        uid = guid;
    }
    return uid;
}
public static boolean isSameApp(defUid uid){
    return Compare.equals(uid, guid) || Compare.equals(uid, duid);
}

public static String getSignature(){
    return toSignature(guid);
}
public static String toSignature(defUid uid){
    return toSignature(SIGNATURE_APP_PREFIX, uid);
}
public static String toSignature(byte[] bytes){
    return toSignature(SIGNATURE_APP_PREFIX, bytes);
}
public static String toSignature(String prefix, defUid uid){
    return toSignature(prefix, uid.toBytes());
}
public static String toSignature(String prefix, byte[] bytes){
    if(bytes == null){
        return null;
    } else {
        return prefix + BytesTo.StringBase49(bytes);
    }
}

public static boolean isFirstLaunch(){
    SharedPreferences sp = Application.sharedPreferences();
    return Compare.isTrue(sp.getBoolean(SP_APP_FIRST_LAUNCH_BOOL));
}
public static long getInstalledTimestamp(){
    SharedPreferences sp = Application.sharedPreferences();
    Long timestamp = sp.getLong(SP_APP_FIRST_INSTALL_LONG);
    if(timestamp == null){
        try{
            PackageInfo packageInfo = AppContext.get().getPackageManager().getPackageInfo(AppContext.getPackageName(), 0);
            timestamp = packageInfo.firstInstallTime;
        } catch(Throwable e){

DebugException.start().log(e).end();

        }
        if(timestamp == null){
            timestamp = Clock.MilliSecond.now();

        }
        sp.put(SP_APP_FIRST_INSTALL_LONG, timestamp);
    }
    return timestamp;
}
public static long getOpenedTimestamp(){
    SharedPreferences sp = Application.sharedPreferences();
    return sp.getLong(SP_APP_CURRENT_LAUNCH_BOOL);
}
public static long getOpenedPreviousTimestamp(){
    SharedPreferences sp = Application.sharedPreferences();
    return sp.getLong(SP_APP_PREVIOUS_LAUNCH_BOOL);
}
public static Long getUpdatedTimestamp(){
    try{
        PackageInfo packageInfo = AppContext.get().getPackageManager().getPackageInfo(AppContext.getPackageName(), 0);
        return packageInfo.lastUpdateTime;
    } catch(Throwable e){

DebugException.start().log(e).end();

        return null;
    }
}

public static Drawable getLauncherDrawable(String packageName){
    try{
        PackageManager packageManager = AppContext.getPackageManager();
        ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
        Resources res = packageManager.getResourcesForApplication(packageName);
        return ResourcesCompat.getDrawable(res, applicationInfo.icon, null);
    } catch(Throwable e){

DebugException.start().log(e).end();

        return null;
    }
}
public static KeyMutual getKeyMutual(PasswordCipher password){
    String spec = AppConfig.getString(SPEC_MUTUAL.getId());
    if(spec == null){
        return null;
    } else {
        return KeyMutual.fromSpec(password, spec);
    }
}

public static void toast(int textResourceId, long duration){
    toast(textResourceId, duration, StatusParam.Color.INFO, false);
}
public static void toast(int textResourceId, long duration, boolean attachToRoot){
    toast(textResourceId, duration, StatusParam.Color.INFO, attachToRoot);
}
public static void toast(int textResourceId, long duration, StatusParam.Color.Is color){
    toast(AppContext.getResources().getString(textResourceId), duration, color, false);
}
public static void toast(int textResourceId, long duration, StatusParam.Color.Is color, boolean attachToRoot){
    toast(AppContext.getResources().getString(textResourceId), duration, color, attachToRoot);
}
public static void toast(String text, long duration){
    toast(text, duration, StatusParam.Color.INFO, false);
}
public static void toast(String text, long duration, boolean attachToRoot){
    toast(text, duration, StatusParam.Color.INFO, attachToRoot);
}
public static void toast(String text, long duration, StatusParam.Color.Is color){
    toast(text, duration, color, false);
}
public static void toast(String text, long duration, StatusParam.Color.Is color, boolean attachToRoot){
    StatusToast.Builder builder = new StatusToast.Builder().setDuration(duration).setColor(color).setMessage(text);
    builder.build(attachToRoot).show();
}

public static void snack(int textResourceId, long duration){
    snack(textResourceId, duration, StatusParam.Color.INFO, false);
}
public static void snack(int textResourceId, long duration, boolean lookForCoordinatorLayout){
    snack(textResourceId, duration, StatusParam.Color.INFO, lookForCoordinatorLayout);
}
public static void snack(int textResourceId, long duration, StatusParam.Color.Is color){
    snack(AppContext.getResources().getString(textResourceId), duration, color, false);
}
public static void snack(int textResourceId, long duration, StatusParam.Color.Is color, boolean lookForCoordinatorLayout){
    snack(AppContext.getResources().getString(textResourceId), duration, color, lookForCoordinatorLayout);
}
public static void snack(String text, long duration){
    snack(text, duration, StatusParam.Color.INFO, false);
}
public static void snack(String text, long duration, boolean lookForCoordinatorLayout){
    snack(text, duration, StatusParam.Color.INFO, lookForCoordinatorLayout);
}
public static void snack(String text, long duration, StatusParam.Color.Is color){
    snack(text, duration, color, false);
}
public static void snack(String text, long duration, StatusParam.Color.Is color, boolean lookForCoordinatorLayout){
    StatusSnackBar.Builder builder = new StatusSnackBar.Builder().setDuration(duration).setColor(color).setMessage(text);
    builder.build(lookForCoordinatorLayout).show();
}


}
