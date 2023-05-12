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
import com.tezov.lib_java.async.notifier.observer.value.ObserverValueE;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Looper;
import android.os.SystemClock;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.multidex.MultiDexApplication;

import com.jakewharton.threetenabp.AndroidThreeTen;
import com.tezov.lib_java.async.notifier.observer.Observer;
import com.tezov.lib_java.async.notifier.observer.state.ObserverState;
import com.tezov.lib_java.async.notifier.task.TaskState;
import com.tezov.lib_java.async.notifier.task.TaskValue;
import com.tezov.lib_java.cipher.SecureProvider;
import com.tezov.lib_java.generator.uid.UUID;
import com.tezov.lib_java.type.primitive.BytesTo;
import com.tezov.lib_java.type.primitive.string.StringCharTo;
import com.tezov.lib_java.type.ref.Ref;
import com.tezov.lib_java.type.ref.WR;
import com.tezov.lib_java.wrapperAnonymous.ConsumerW;
import com.tezov.lib_java_android.type.android.LifecycleEvent;
import com.tezov.lib_java_android.type.android.wrapper.BundleW;
import com.tezov.lib_java_android.ui.activity.ActivityBase;
import com.tezov.lib_java_android.ui.navigation.Navigate;
import com.tezov.lib_java_android.util.UtilsIntent;
import com.tezov.lib_java_android.wrapperAnonymous.ActivityLifecycleCallbacksW;

import java.util.concurrent.TimeUnit;

public abstract class ApplicationSystem extends MultiDexApplication{
public final static String EXTRA_LAUNCH_PACKAGE = "EXTRA_LAUNCH_PACKAGE";
private final static String EXTRA_EXIT = "EXTRA_EXIT";
private final static long CLOSE_FORCES_DELAY_ms = 250;
private State state = State.NOT_INITIALISED;
private TaskValue<State> task = null;

public ApplicationSystem(){
}
private static Class<ApplicationSystem> myClass(){
    return ApplicationSystem.class;
}
public static void closeForced(Activity currentActivity, boolean newTask){
    ApplicationSystem applicationSystem = (ApplicationSystem)currentActivity.getApplication();
    if((applicationSystem.getState() == State.NOT_INITIALISED) || (applicationSystem.getState() == State.STARTED)){
        //IMPROVE, navigate back to main if not already
        Window currentActivityWindow = currentActivity.getWindow();
        if(currentActivityWindow != null){
            currentActivityWindow.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
        Intent intent = new Intent(currentActivity, applicationSystem.launcherActivityType());
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        if(newTask){
            intent.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        }        
        applicationSystem.registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacksW(){
            @Override
            public void onActivityStarted(@NonNull Activity activity){
                if(activity.getClass() == applicationSystem.launcherActivityType()){
                    Window activityWindow = activity.getWindow();
                    if(activityWindow != null){
                        activityWindow.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    }
                }
            }
            @Override
            public void onActivityResumed(@NonNull Activity activity){
                if(activity.getClass() == applicationSystem.launcherActivityType()){
                    currentActivity.finish();
                    activity.finish();
                    android.os.Handler handler = new android.os.Handler(Looper.myLooper());
                    handler.postDelayed(()->System.exit(0), CLOSE_FORCES_DELAY_ms);
                }
            }
        });
        currentActivity.startActivity(intent);
        currentActivity.overridePendingTransition(0, 0);
    }
    else if(applicationSystem.getState() == State.RESTARTING){
        TaskValue<State>.Observable task = applicationSystem.getTaskObservable();
        if(task != null){
            task.observe(new ObserverValueE<>(myClass()){
                @Override
                public void onComplete(State state){
                    closeForced(currentActivity, newTask);
                }
                @Override
                public void onException(State state, Throwable e){
                    System.exit(0);
                }
            });
        }
        else{
            System.exit(0);
        }
    }
}
public abstract Class<? extends Activity> launcherActivityType();
public abstract Class<? extends Activity> mainActivityType();
public char[] configPassword(){
    return AppContext.getApplicationId().toCharArray();
}
protected AppConfigKey.Adapter configNewKeyAdapter(){
    return new AppConfigKey.Adapter();
}
public String secureProviderType(){
    return SecureProvider.BOUNCY_CASTLE;
}
public State getState(){
    return state;
}
private void setState(State state){
//    Log.d(DebugLog.TAG, state.name());
    this.state = state;
}
@Override
public void onCreate(){
    super.onCreate();
    android.content.Context context = getApplicationContext();
    AppContext.init(context, this);
    SecureProvider.init(secureProviderType());
    AppConfig.init(this);
    AndroidThreeTen.init(context);
    Observer.setOnNewObserverConsumer(new ConsumerW<Observer<?, ?>>(){
        @Override
        public void accept(Observer<?, ?> observer){
            Object owner = observer.getOwner();
            if(owner instanceof LifecycleOwner){
                LifecycleEvent.on(LifecycleEvent.Event.DESTROY, (LifecycleOwner)owner, observer.newSubscription());
            }
        }
    });
}
protected abstract TaskState.Observable onMainActivityStart(Intent source, boolean isRestarted);
protected void onApplicationPause(){}
protected void onApplicationClose(){
    AppContext.setActivity(null);
}
final public TaskValue<State>.Observable getTaskObservable(){
    return task.getObservable();
}
final public TaskValue<State>.Observable startMainActivity(Activity activitySource, boolean redirectSourceIntent, boolean newTask){
    if(task != null){
        return task.getObservable();
    }
    if(getState() != State.NOT_INITIALISED){
        return TaskValue.Exception(null, "invalid state :" + getState());
    }
    setState(State.STARTING);
    task = new TaskValue<>();
    onMainActivityStart(activitySource.getIntent(), false).observe(new ObserverState(this){
        @Override
        public void onComplete(){
            Intent intent;
            if(redirectSourceIntent){
                intent = new Intent(activitySource.getIntent());
            } else {
                intent = new Intent();
            }
            intent.setClass(activitySource, mainActivityType());
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            if(newTask){
                intent.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            }
            registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacksW(){
                @Override
                public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState){
                    if(activity.getClass() == mainActivityType()){
                        unregisterActivityLifecycleCallbacks(this);
                        setState(State.STARTED);
                        TaskValue<State> tmp = task;
                        task = null;
                        tmp.notifyComplete(getState());
                    }
                }
            });
            activitySource.startActivity(intent);
            activitySource.overridePendingTransition(0, 0);
            activitySource.finish();
        }
    });
    return task.getObservable();
}
final public TaskValue<State>.Observable reachCurrentActivity(Activity activitySource, boolean redirectIntent){
    if(task != null){
        return task.getObservable();
    }
    if(getState() != State.STARTED){
        return TaskValue.Exception(null, "invalid state :" + getState());
    }
    Activity targetActivity = AppContext.getActivity();
    if(targetActivity == null){
        return TaskValue.Exception(null, "context activity is null");
    }
    task = new TaskValue<>();
    Intent intent;
    if(redirectIntent){
        intent = new Intent(activitySource.getIntent());
    } else {
        intent = new Intent();
    }
    intent.setClass(activitySource, targetActivity.getClass());
    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
    intent.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
    registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacksW(){
        @Override
        public void onActivityResumed(@NonNull Activity activity){
            if(activity.getClass() == targetActivity.getClass()){
                unregisterActivityLifecycleCallbacks(this);
                TaskValue<State> tmp = task;
                task = null;
                tmp.notifyComplete(getState());
            }
        }
    });
    activitySource.startActivity(intent);
    activitySource.overridePendingTransition(0, 0);
    activitySource.finish();
    return task.getObservable();
}
final public TaskValue<State>.Observable wakeUpMainActivity(Activity activitySource){
    if(task != null){
        return task.getObservable();
    }
    if(getState() != State.PAUSED){
        return TaskValue.Exception(null, "invalid state :" + getState());
    }
    task = new TaskValue<>();
    Intent intent = new Intent();
    intent.setClass(activitySource, launcherActivityType());
    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
    intent.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
    activitySource.startActivity(intent);
    activitySource.overridePendingTransition(0, 0);
    activitySource.finish();
    return task.getObservable();
}
final public TaskValue<State>.Observable closeMainActivity(){
    return closeMainActivity(null, null);
}
final public TaskValue<State>.Observable closeMainActivity(BundleW bundle, Integer extra_flag){
    if(task != null){
        return task.getObservable();
    }
    if(getState() != State.STARTED){
        return TaskValue.Exception(null, "invalid state :" + getState());
    }
    Activity mainActivity = AppContext.getActivity();
    if(mainActivity.getClass() != mainActivityType()){
        return TaskValue.Exception(null, "current activity(" + mainActivity.getClass().getSimpleName() + ") is not main activity");
    }
    if(ReceiverAlarmCloseApp.isAlarmExist(mainActivity)){
        return TaskValue.Exception(null, "alarm already exist");
    }
    task = new TaskValue<>();
    setState(State.PAUSING);
    BundleW extras = bundle;
    if(bundle == null){
        extras = BundleW.obtain();
    }
    extras.put(EXTRA_EXIT, true);
    Intent intent = new Intent(mainActivity, launcherActivityType());
    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
    if(extra_flag != null){
        intent.addFlags(extra_flag);
    }
    intent.putExtras(extras.get());
    registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacksW(){
        boolean isStarted = false;
        @Override
        public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState){
            if(activity.getClass() == launcherActivityType()){
                onApplicationPause();
                activity.moveTaskToBack(true);
            }
        }
        @Override
        public void onActivityResumed(@NonNull Activity activity){
            if(isStarted && (activity.getClass() == launcherActivityType())){
                isStarted = false;
                unregisterActivityLifecycleCallbacks(this);
                ReceiverAlarmCloseApp.cancel(activity.getApplicationContext(), activity);
                setState(State.RESTARTING);
                onMainActivityStart(null, true).observe(new ObserverState(this){
                    @Override
                    public void onComplete(){
                        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacksW(){
                            @Override
                            public void onActivityResumed(@NonNull Activity activity){
                                if(activity.getClass() == mainActivityType()){
                                    unregisterActivityLifecycleCallbacks(this);
                                    setState(State.STARTED);
                                    if(activity instanceof ActivityBase){
                                        ((ActivityBase)activity).onApplicationRestarted();
                                    }
                                    if(task != null){
                                        TaskValue<State> tmp = task;
                                        task = null;
                                        tmp.notifyComplete(getState());
                                    }
                                }
                            }
                        });
                        activity.finish();
                        activity.overridePendingTransition(0, 0);
                    }
                });
            }
        }
        @Override
        public void onActivityPaused(@NonNull Activity activity){
            if(!isStarted && (activity.getClass() == launcherActivityType())){
                isStarted = true;
                ReceiverAlarmCloseApp.start(activity.getApplicationContext(), activity);
                setState(State.PAUSED);
                TaskValue<State> tmp = task;
                task = null;
                tmp.notifyComplete(getState());
                String packageName = activity.getIntent().getStringExtra(EXTRA_LAUNCH_PACKAGE);
                if(packageName != null){
                    PackageManager packageManager = activity.getPackageManager();
                    Intent intent = packageManager.getLaunchIntentForPackage(packageName);
                    if(intent != null){
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        activity.startActivity(intent);
                        activity.overridePendingTransition(0, 0);
                    }
                }
            }
            if(activity.getClass() == mainActivityType()){
                if(activity instanceof ActivityBase){
                    ((ActivityBase)activity).onApplicationPause();
                }
            }
        }
        @Override
        public void onActivityDestroyed(@NonNull Activity activity){
            if(isStarted && (activity.getClass() == launcherActivityType())){
                isStarted = false;
                unregisterActivityLifecycleCallbacks(this);
                ReceiverAlarmCloseApp.cancel(activity.getApplicationContext(), activity);
            }
        }
    });
    mainActivity.startActivity(intent);
    mainActivity.overridePendingTransition(0, 0);
    return task.getObservable();
}
final public boolean startPackageAndCloseMainActivity(Activity activitySource, String packageName){
    PackageManager packageManager = activitySource.getPackageManager();
    Intent intent = packageManager.getLaunchIntentForPackage(packageName);
    if(intent == null){
        return false;
    } else {
        BundleW bundle = BundleW.obtain();
        bundle.put(EXTRA_LAUNCH_PACKAGE, packageName);
        closeMainActivity(bundle, null);
        return true;
    }
}
final public boolean isClosing(Activity activityLauncher){
    return activityLauncher.getIntent().getBooleanExtra(EXTRA_EXIT, false);
}

public enum State{
    NOT_INITIALISED, STARTING, STARTED, PAUSING, PAUSED, RESTARTING, CLOSING, CLOSED
}

public static class ReceiverAlarmCloseApp extends BroadcastReceiver{
    private final static long CLOSE_DELAY_ms = TimeUnit.MILLISECONDS.convert(2, TimeUnit.MINUTES);
    private final static String action = "com.tezov.lib.ALARM.close.application";
    private final static String extraRequestCode = "RequestCode";
    private final static int requestCodeNotValid = -1;
    private static WR<Activity> activityLauncherWR = null;
    private static Class<ReceiverAlarmCloseApp> myClass(){
        return ReceiverAlarmCloseApp.class;
    }
    private static int getRequestCode(){
        UUID sessionUid = Application.getState().sessionUid();
        if(sessionUid == null){
            return requestCodeNotValid;
        } else {
            return BytesTo.Int(StringCharTo.BytesHashcode64(action + sessionUid.toHexString()));
        }
    }
    public static void start(android.content.Context context, Activity activityLauncher){
        if(Ref.isNotNull(activityLauncherWR)){
            return;
        }
        ApplicationSystem application = (ApplicationSystem)activityLauncher.getApplication();
        if(application.getState() != State.PAUSING){
            return;
        }
        ReceiverAlarmCloseApp.activityLauncherWR = WR.newInstance(activityLauncher);
        int requestCode = getRequestCode();
        Intent intent = new Intent(context, myClass());
        intent.setAction(action);
        intent.putExtra(extraRequestCode, requestCode);
        PendingIntent pendingIntent = UtilsIntent.getBroadcast_UPDATE(context, requestCode, intent);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + CLOSE_DELAY_ms, pendingIntent);
    }
    public static void cancel(android.content.Context context, Activity activityLauncher){
        if(Ref.isNull(activityLauncherWR)){
            return;
        }
        ApplicationSystem application = (ApplicationSystem)activityLauncher.getApplication();
        if(application.getState() != State.PAUSED){
            return;
        }
        ReceiverAlarmCloseApp.activityLauncherWR = null;
        Intent intent = new Intent(context, myClass());
        intent.setAction(action);
        PendingIntent pendingIntent = UtilsIntent.getBroadcast_CANCEL(context, getRequestCode(), intent);
        if(pendingIntent != null){
            AlarmManager alarmManager = (AlarmManager)context.getSystemService(android.content.Context.ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);
        }
    }
    public static boolean isAlarmExist(android.content.Context context){
        Intent intent = new Intent(context, myClass());
        intent.setAction(action);
        PendingIntent pendingIntent = UtilsIntent.getBroadcast_EXIST(context, getRequestCode(), intent);
        return pendingIntent != null;
    }
    @Override
    public void onReceive(android.content.Context context, Intent receiverIntent){
        int requestCode = getRequestCode();
        if((requestCode == requestCodeNotValid) || (receiverIntent.getIntExtra(extraRequestCode, requestCodeNotValid) != requestCode)){
            return;
        }
        Activity mainActivity = AppContext.getActivity();
        if(mainActivity == null){
            return;
        }
        ApplicationSystem application = (ApplicationSystem)mainActivity.getApplication();
        if(application.getState() != State.PAUSED){
            return;
        }
        if(application.task != null){
            return;
        }
        application.task = new TaskValue<>();
        application.setState(State.CLOSING);
        application.registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacksW(){
            @Override
            public void onActivityDestroyed(@NonNull Activity activity){
                if(activity.getClass() == application.mainActivityType()){
                    if(Ref.isNotNull(activityLauncherWR)){
                        Activity activityLauncher = ReceiverAlarmCloseApp.activityLauncherWR.get();
                        ReceiverAlarmCloseApp.activityLauncherWR = null;
                        activityLauncher.finish();
                    } else {
                        System.exit(0);
                    }
                } else if(activity.getClass() == application.launcherActivityType()){
                    application.unregisterActivityLifecycleCallbacks(this);
                    application.onApplicationClose();
                    application.setState(State.CLOSED);
                    TaskValue<State> tmp = application.task;
                    application.task = null;
                    tmp.notifyComplete(application.getState());
                }
            }
        });
        Navigate.CloseApplication();
    }

}

}
