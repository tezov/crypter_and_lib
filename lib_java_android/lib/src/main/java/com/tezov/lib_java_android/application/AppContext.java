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
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.util.UtilsString;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import androidx.fragment.app.Fragment;
import com.tezov.lib_java_android.file.EnvironnementAndroid;
import com.tezov.lib_java.file.StoragePackage;
import com.tezov.lib_java.type.ref.Ref;
import com.tezov.lib_java.type.ref.WR;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.fragment.app.FragmentManager;

import com.tezov.lib_java.async.notifier.Notifier;
import com.tezov.lib_java.async.notifier.observable.ObservableValue;
import com.tezov.lib_java.async.notifier.observer.value.ObserverValue;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java_android.ui.activity.ActivityBase;

public class AppContext extends com.tezov.lib_java.application.AppContext{
private static android.content.Context context = null;
private static Notifier<Void> notifier = null;
private static WR<ActivityBase> activityWR = null;

protected AppContext(){}
private static Class<AppContext> myClass(){
    return AppContext.class;
}

public static void init(android.content.Context context, ApplicationSystem applicationSystem){
    AppContext.context = context;
    setPackageName(context.getPackageName());
    setResource(new AppResources(applicationSystem));
    notifier = new Notifier<>(new ObservableValue<>(), false);
    activityWR = WR.newInstance(null);
    StoragePackage.setEnvironnement(new EnvironnementAndroid());
}

public static android.content.Context get(){
    return context;
}

public static <A extends ActivityBase> Notifier.Subscription observeOnActivityChange(ObserverValue<A> observer){
    return notifier.register(observer);
}
public static void unObserveOnActivityChange(Object owner){
    notifier.unregister(owner);
}
public static void unObserveOnActivityChangeAll(){
    notifier.unregisterAll();
}

public static <A extends ApplicationSystem> A getApplication(){
    return (A)getActivity().getApplication();
}

public static boolean hasActivity(){
    return notifier != null && getActivity() != null;
}
public static <A extends ActivityBase> A getActivity(){
    return (A)Ref.get(activityWR);
}
public static void setActivity(ActivityBase activity){
    activityWR.set(activity);
    if(activity != null){
        ObservableValue.Access access = notifier.obtainAccess(myClass(), null);
        access.setValueIfDifferent(activity);
    }
    else{
        notifier.getObservable().clearAccess();
    }
}
public static FragmentManager getFragmentManager(){
    return getActivity().getSupportFragmentManager();
}
public static boolean isChangingConfigurations(){
    return getActivity().isChangingConfigurations();
}

public static boolean hasSystemFeature(String feature){
    return getPackageManager().hasSystemFeature(feature);
}
public static <V> V getSystemService(String name){
    return (V)get().getSystemService(name);
}
public static PackageManager getPackageManager(){
    return get().getPackageManager();
}

public static String getApplicationName(){
    return get().getApplicationInfo().loadLabel(getPackageManager()).toString();
}
public static Bundle getMetaData(){
    try{
        ApplicationInfo ai = AppContext.getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
        return ai.metaData;
    } catch(PackageManager.NameNotFoundException e){

DebugException.start().log(e).end();

        return null;
    }
}

public static void registerReceiver(BroadcastReceiver receiver, IntentFilter filter){
    get().registerReceiver(receiver, filter);
}
public static void unregisterReceiver(BroadcastReceiver receiver){
    get().unregisterReceiver(receiver);
}

public static boolean bindService(Intent service, ServiceConnection conn, int flags){
    return get().bindService(service, conn, flags);
}
public static void unbindService(ServiceConnection conn){
    get().unbindService(conn);
}
public static ContentResolver getContentResolver(){
    return get().getContentResolver();
}

public static AppResources getResources(){
    return (AppResources)com.tezov.lib_java.application.AppContext.getResources();
}
public static SharedPreferences getSharedPreferences(String name, int mode){
    return get().getSharedPreferences(name, mode);
}


}
