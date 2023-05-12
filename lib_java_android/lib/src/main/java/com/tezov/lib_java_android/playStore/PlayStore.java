/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.playStore;

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

import com.tezov.lib_java_android.R;
import com.tezov.lib_java_android.application.AppContext;
import com.tezov.lib_java_android.util.UtilsIntent;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.tezov.lib_java.async.notifier.observer.state.ObserverState;
import com.tezov.lib_java.async.notifier.task.TaskState;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.runnable.RunnableTimeOut;
import com.tezov.lib_java_android.wrapperAnonymous.ActivityLifecycleCallbacksW;
import com.tezov.lib_java_android.ui.activity.ActivityBase;

import java.util.concurrent.TimeUnit;

import static com.tezov.lib_java_android.util.UtilsIntent.openLink;

public class PlayStore{
private PlayStore(){
}

private static Class<PlayStore> myClass(){
    return PlayStore.class;
}
private static TaskState.Observable registerReceiver(String action, String packageName, Long delayTimeOut, TimeUnit unit){
    if(delayTimeOut == null){
        delayTimeOut = Long.MAX_VALUE;
        unit = TimeUnit.MILLISECONDS;
    }
    Task task = new Task(action, packageName, delayTimeOut, unit).start();
    return task.getObservable();
}

public static TaskState.Observable shareLink(){
    String subject = AppContext.getResources().getString(R.string.share_link_subject);
    subject = String.format(subject, AppContext.getApplicationName());
    String description = AppContext.getResources().getString(R.string.share_link_description);
    return shareLink(subject , subject + " " + description);
}
public static TaskState.Observable shareLink(int subject, int descriptionResourceId){
    return shareLink(AppContext.getResources().getString(subject), AppContext.getResources().getString(descriptionResourceId));
}
public static TaskState.Observable shareLink(String subject, String description){
    return UtilsIntent.send(subject, description + getPackageLink());
}

public static TaskState.Observable requestRate(){
    return openLink(getPackageLink());
}
public static RequestOpen requestOpen(String appId){
    return new RequestOpen(appId);
}
public static RequestUninstall requestUninstall(String appId){
    return new RequestUninstall(appId);
}

public static boolean isValidInstall(){
    String installer = AppContext.getPackageManager().getInstallerPackageName(AppContext.getPackageName());
    return installer != null && installer.startsWith("com.android.vending");
}
public static boolean isInstalled(String packageName){
    PackageManager packageManager = AppContext.getPackageManager();
    try{
        return packageManager.getApplicationInfo(packageName, 0).enabled;
    } catch(PackageManager.NameNotFoundException e){
        return false;
    }
}
public static TaskState.Observable observeInstalled(String packageName){
    return observeInstalled(packageName, null);
}
public static TaskState.Observable observeInstalled(String packageName, Long delayTimeOut_ms){
    return observeInstalled(packageName, delayTimeOut_ms, TimeUnit.MILLISECONDS);
}
public static TaskState.Observable observeInstalled(String packageName, Long delayTimeOut, TimeUnit unit){
    return registerReceiver(Intent.ACTION_PACKAGE_ADDED, packageName, delayTimeOut, unit);
}
public static TaskState.Observable observeUninstalled(String packageName){
    return observeUninstalled(packageName, null);
}
public static TaskState.Observable observeUninstalled(String packageName, Long delayTimeOut_ms){
    return observeUninstalled(packageName, delayTimeOut_ms, TimeUnit.MILLISECONDS);
}
public static TaskState.Observable observeUninstalled(String packageName, Long delayTimeOut, TimeUnit unit){
    return registerReceiver(Intent.ACTION_PACKAGE_REMOVED, packageName, delayTimeOut, unit);
}

public static String getPackageDeepLink(){
    return getPackageDeepLink(AppContext.getPackageName());
}
public static String getPackageDeepLink(String appId){
    return "market://details?id=" + appId;
}
public static String getPackageLink(){
    return getPackageLink(AppContext.getPackageName());
}
public static String getPackageLink(String appId){
    return "https://play.google.com/store/apps/details?id=" + appId;
}

public static class RequestOpen{
    private final TaskState task;
    private boolean started = false;
    private Intent intent;

    public RequestOpen(String appId){
DebugTrack.start().create(this).end();
        task = new TaskState();
        PackageManager packageManager = AppContext.getPackageManager();
        intent = packageManager.getLaunchIntentForPackage("com.android.vending");
        if(intent != null){
            intent.setData(Uri.parse(getPackageDeepLink(appId)));
        } else {
            intent = new Intent();
            intent.setData(Uri.parse(getPackageLink(appId)));
        }
        intent.setAction(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    }

    public RequestOpen observe(ObserverState observer){
        task.observe(observer);
        return this;
    }

    public <R extends RequestOpen> R start(){

        if(started){
DebugException.start().explode("requestCode not null, duplicate start").end();
        }

        started = true;
        AppContext.getApplication().registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacksW(){
            @Override
            public void onActivityResumed(@NonNull Activity activity){
                AppContext.getApplication().unregisterActivityLifecycleCallbacks(this);
                task.notifyComplete();
            }
        });
        AppContext.getActivity().startActivity(intent);
        return (R)this;
    }
    @Override
    protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
        super.finalize();
    }

}
public static class RequestUninstall extends ActivityBase.RequestForResult{
    private final TaskState task;
    public RequestUninstall(String appId){
        task = new TaskState();
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_UNINSTALL_PACKAGE);
        intent.setData(Uri.parse("package:" + appId));
        setIntent(intent);
    }
    public RequestUninstall observe(ObserverState observer){
        task.observe(observer);
        return this;
    }
    @Override
    public void onActivityResult(int resultCode, Intent data){
        task.notifyComplete();
    }
}
private static class Task extends RunnableTimeOut{
    TaskState task;
    String action;
    String packageName;
    BroadcastReceiver br = null;
    Task(String action, String packageName, long delay, TimeUnit timeUnit){
        super(myClass(), delay, timeUnit);
        task = new TaskState();
        this.action = action;
        this.packageName = packageName;
    }
    @Override
    public void onStart(){
        if(task.isCanceled()){
            task.notifyCanceled();
            return;
        }
        if(br != null){
            AppContext.unregisterReceiver(br);

DebugException.start().log("Receiver is not null").end();

        }
        br = new BroadcastReceiver(){
            @Override
            public void onReceive(android.content.Context context, Intent intent){
                Uri data = intent.getData();
                String packageNameInstalled = data.getEncodedSchemeSpecificPart();
                if(packageName.equals(packageNameInstalled)){
                    completed();
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(action);
        filter.addDataScheme("package");
        AppContext.registerReceiver(br, filter);
    }
    @Override
    public void onComplete(){
        AppContext.unregisterReceiver(br);
        br = null;
        if(!task.isCanceled()){
            task.notifyComplete();
        } else {
            task.notifyCanceled();
        }
    }
    @Override
    public void onTimeOut(){
        AppContext.unregisterReceiver(br);
        br = null;
        if(!task.isCanceled()){
            task.notifyException("timeout");
        } else {
            task.notifyCanceled();
        }
    }
    TaskState.Observable getObservable(){
        return task.getObservable();
    }

}


}
