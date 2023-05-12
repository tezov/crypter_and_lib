/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.async;

import com.tezov.lib_java.cipher.dataInput.Encoder;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.util.UtilsString;
import java.util.List;
import java.util.Set;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import com.tezov.lib_java.BuildConfig;
import com.tezov.lib_java.application.AppConfig;
import com.tezov.lib_java.application.AppConfigKey;
import com.tezov.lib_java.application.AppUUIDGenerator;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.toolbox.Compare;
import com.tezov.lib_java.toolbox.Nullify;
import com.tezov.lib_java.type.collection.ListEntry;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.runnable.RunnableW;
import com.tezov.lib_java.wrapperAnonymous.UncaughtExceptionHandlerW;

import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

public class Handler extends android.os.Handler{
public static final Handler LOG_DELAY;
public static final Handler DEBUG;
public static final Handler TEST;
private final static boolean TRACK_ENABLED;
private final static String POST = "POST";
private final static String RUN = "RUN";
private final static String CANCELED = "CANCELED";
private final static String NOT_FOUND = "!!! NOT FOUND !!!";
//private static int countTotal = 0;
//private static int countCurrent = 0;
private static Handler lastHandler = null;
private static Holder holder = null;

static{
    if(BuildConfig.DEBUG_ONLY){
        TRACK_ENABLED = Compare.isTrue(AppConfig.getBoolean(AppConfigKey.DEBUG_LOG_TRACK_HANDLER.getId()));
    } else {
        TRACK_ENABLED = false;
    }
}

static{
    if(BuildConfig.DEBUG_ONLY){
        LOG_DELAY = newHandler("log_delay", HandlerThread.MIN_PRIORITY, true, false);
        LOG_DELAY.enableTrack(false);
    } else {
        LOG_DELAY = null;
    }
}

static{
    if(BuildConfig.DEBUG_ONLY){
        DEBUG = newHandler("debug", HandlerThread.NORM_PRIORITY, true, false);
        DEBUG.enableTrack(false);
    } else {
        DEBUG = null;
    }

}

static{
    if(BuildConfig.DEBUG_ONLY){
        TEST = newHandler("test", HandlerThread.NORM_PRIORITY, true, false);
        TEST.enableTrack(TRACK_ENABLED);
        TEST.getThread().setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler(){
            @Override
            public void uncaughtException(Thread t, Throwable e){
DebugException.start().log(e).end();
            }
        });
    } else {
        TEST = null;
    }

}

private boolean isTrackEnabled = false;
private ListEntry<Integer, Runnable> tracks = null;
private Handler(HandlerThread handlerThread){
    super(handlerThread.getLooper());
//    countCurrent++;
//    countTotal++;
//    Log.d(DebugLog.TAG, "++++ " + getName() + " (" + countCurrent + "/" + countTotal + ") ++++");
}

private Handler(Looper looper){
    super(looper);
//    countCurrent++;
//    countTotal++;
//    Log.d(DebugLog.TAG, "++++ " + getName() + " (" + countCurrent + "/" + countTotal + ") ++++");
}
public static void onMainActivityStart(){
    if(holder == null){
        holder = new Holder();
        holder.start();
    }
}
public static void onApplicationClose(){
    if(holder != null){
        holder.stop();
        holder = null;
    }
}
public static Handler MAIN(){
    return holder.main();
}
public static Handler PRIMARY(){
    return holder.primary();
}
public static Handler SECONDARY(){
    return holder.secondary();
}
public static Handler LOW(){
    return holder.low();
}
public static Handler LOG_DELAY(){
    return LOG_DELAY;
}
public static Handler DEBUG(){
    return DEBUG;
}
public static Handler TEST(){
    return TEST;
}

public static Handler currentOrNull(){
    Looper myLooper = Looper.myLooper();
    if(myLooper == MAIN().getLooper()){
        return MAIN();
    }
    if(myLooper == PRIMARY().getLooper()){
        return PRIMARY();
    }
    if(myLooper == SECONDARY().getLooper()){
        return SECONDARY();
    }
    if(holder.isLowExist() && (myLooper == LOW().getLooper())){
        return LOW();
    }
    if(BuildConfig.DEBUG_ONLY && (holder != null)){
        if(myLooper == LOG_DELAY().getLooper()){
            return LOG_DELAY();
        }
        if(myLooper == DEBUG().getLooper()){
            return DEBUG();
        }
        if(myLooper == TEST().getLooper()){
            return TEST();
        }
    }
    return null;
}
public static Handler currentOrMain(){
    Handler handler = currentOrNull();
    if(handler != null){
        return handler;
    } else {
        return MAIN();
    }
}
public static Handler currentOrPrimary(){
    Handler handler = currentOrNull();
    if(handler != null){
        return handler;
    } else {
        return PRIMARY();
    }
}
public static Handler currentOrSecondary(){
    Handler handler = currentOrNull();
    if(handler != null){
        return handler;
    } else {
        return SECONDARY();
    }
}
public static Handler currentOrFromMyLooper(){
    Handler handler = currentOrNull();
    if(handler != null){
        return handler;
    } else {
        return fromMyLooper();
    }
}
public static Handler fromMyLooper(){
    return new Handler(Looper.myLooper());
}

public static Handler newHandler(Object requester){
    return newHandler(requester, HandlerThread.NORM_PRIORITY, true);
}
public static Handler newHandler(Object requester, boolean daemon){
    return newHandler(requester, HandlerThread.NORM_PRIORITY, daemon);
}
public static Handler newHandler(Object requester, int priority){
    return newHandler(requester, priority, true);
}
public static Handler newHandler(Object requester, int priority, boolean daemon){
    return newHandler(requester, priority, daemon, true);
}
private static Handler newHandler(Object requester, int priority, boolean daemon, boolean postfixNameWithUID){
    if(requester instanceof Class){
        return newHandler(((Class)requester).getSimpleName(), priority, daemon, postfixNameWithUID);
    }
    else if(requester instanceof String){
        return newHandler(((String)requester), priority, daemon, postfixNameWithUID);
    }
    else {
        return newHandler(requester.getClass().getSimpleName(), priority, daemon, postfixNameWithUID);
    }
}
private static Handler newHandler(String name, int priority, boolean daemon, boolean postfixNameWithUID){
    if(Nullify.string(name) == null){
DebugException.start().logHidden("Name is null").end();
    }
    if(postfixNameWithUID){
        name += "_" + AppUUIDGenerator.next().toHexString();
    }
    HandlerThread handlerThread = new HandlerThread(name);
    handlerThread.setDaemon(daemon);
    handlerThread.start();
    handlerThread.setPriority(priority);
    return new Handler(handlerThread);
}
public static DebugString toDebugStringCurrentThread(){
    DebugString data = new DebugString();
    data.appendHashcodeString("Thread:" + Thread.currentThread().getName(), Thread.currentThread());
    return data;
}
public static void toDebugLogCurrentThread(){
DebugLog.start().send(toDebugStringCurrentThread()).end();
}
public static boolean isThread(Handler handler){
    return (Looper.myLooper() == handler.getLooper());
}
public static boolean equals(android.os.Handler h1, Handler h2){
    return (h1.getLooper() == h2.getLooper());
}
private static void checkAllowedQuit(Looper looper, String text){
    if(BuildConfig.DEBUG_ONLY && (holder != null)){
        Handler handler = null;
        if(looper == MAIN().getLooper()){
            handler = MAIN();
        }
        if(looper == PRIMARY().getLooper()){
            handler = PRIMARY();
        }
        if(looper == SECONDARY().getLooper()){
            handler = SECONDARY();
        }
        if(holder.isLowExist() && (looper == LOW().getLooper())){
            handler = LOW();
        }
        if(handler != null){
DebugException.start().log(text + " on " + handler.getName() + " thread").end();
        }
    }

}
private static void checkAllowedJoin(Looper looper, String text){
    if(BuildConfig.DEBUG_ONLY && (holder != null)){
        Handler handler = null;
        if(looper == MAIN().getLooper()){
            handler = MAIN();
        }
        if(looper == PRIMARY().getLooper()){
            handler = PRIMARY();
        }
        if(looper == SECONDARY().getLooper()){
            handler = SECONDARY();
        }
        if(handler != null){
DebugException.start().log(text + " on " + handler.getName() + " thread").end();
        }
    }
}
private static void checkAllowedSleep(Looper looper, String text){
    if(BuildConfig.DEBUG_ONLY && (holder != null)){
        Handler handler = null;
        if(looper == MAIN().getLooper()){
            handler = MAIN();
        }
        if(looper == PRIMARY().getLooper()){
            handler = PRIMARY();
        }
        if(looper == SECONDARY().getLooper()){
            handler = SECONDARY();
        }
        if(holder.isLowExist() && (looper == LOW().getLooper())){
            handler = LOW();
        }
        if(handler != null){
DebugException.start().log(text + " on " + handler.getName() + " thread").end();
        }
    }
}
public static void sleep(long delayMillis){
    try{
        checkAllowedSleep(Looper.myLooper(), "sleep");
        java.lang.Thread.sleep(delayMillis);
    } catch(java.lang.Throwable e){
DebugException.start().log(e).end();
    }
}
public static void sleep(long delay, TimeUnit timeUnit){
    sleep(timeUnit.toMillis(delay));
}
public static void yield(){
    java.lang.Thread.yield();
}
public Handler enableTrack(boolean flag){
    isTrackEnabled = flag;
    initTrack();
    return this;
}
private void initTrack(){
    if(isTrackEnabled){
        tracks = new ListEntry<Integer, Runnable>(LinkedList::new);
    }
}
private Runnable removeTrack(Integer key){
    synchronized(tracks){
        return tracks.removeKey(key);
    }
}
private Runnable wrapTrack(Object owner, RunnableW runnable){
    toDebugLogTrack(POST, owner, runnable);
    Runnable wrap = new Runnable(){
        @Override
        public void run(){
            toDebugLogTrack(RUN, owner, runnable);
            runnable.run();
            if(removeTrack(runnable.hashCode()) == null){
                toDebugLogTrack(NOT_FOUND, owner, runnable);
            }
        }
    };
    synchronized(tracks){
        tracks.add(runnable.hashCode(), runnable);
    }
    return wrap;
}
public void throwUncaughtException(Throwable e){
    getThread().getUncaughtExceptionHandler().uncaughtException(getThread(), e);
}
public <U extends Thread.UncaughtExceptionHandler> U getUncaughtExceptionHandler(){
    @SuppressWarnings("unchecked") U r = (U)getThread().getUncaughtExceptionHandler();
    return r;
}
public Handler setUncaughtExceptionHandler(UncaughtExceptionHandlerW eh){
    getThread().setUncaughtExceptionHandler(eh);
    return this;
}
public <R extends RunnableW> R post(Object owner, long delayMillis, RunnableW runnable){
    Runnable r = runnable;
    if(isTrackEnabled){
        r = wrapTrack(owner, runnable);
    }
    Message message = Message.obtain(this, r);
    message.what = runnable.what();
    if(sendMessageDelayed(message, delayMillis)){
        @SuppressWarnings("unchecked") R rt = (R)runnable;
        return rt;
    }
    else {
        return null;
    }
}
public <R extends RunnableW> R post(Object owner, RunnableW runnable){
    return post(owner, 0, runnable);
}
public <R extends RunnableW> R post(Object owner, long delay, TimeUnit timeUnit, RunnableW runnable){
    return post(owner, timeUnit.toMillis(delay), runnable);
}
public <R extends RunnableW> R post(Object owner, Delay delay, TimeUnit timeUnit, RunnableW runnable){
    return post(owner, delay.millisecond(), timeUnit, runnable);
}
public <R extends RunnableW> R post(Object owner, Delay delay, RunnableW runnable){
    return post(owner, delay.millisecond(), TimeUnit.MILLISECONDS, runnable);
}
public boolean hasRunnable(RunnableW runnable){
    return hasMessages(runnable.what());
}
public boolean cancel(Object owner, RunnableW runnable){
    if(isTrackEnabled){
        removeTrack(runnable.hashCode());
    }
    boolean exist = hasMessages(runnable.what());
    if(exist){
        removeMessages(runnable.what());
    }
    if(isTrackEnabled){
        if(exist){
            toDebugLogTrack(CANCELED, owner, runnable);
        } else {
            toDebugLogTrack(NOT_FOUND, owner, runnable);
        }
    }
    return exist;
}
public String getName(){
    return getThread().getName();
}
public java.lang.Thread getThread(){
    return getLooper().getThread();
}
public boolean isAlive(){
    return getThread().isAlive() && !getThread().isInterrupted();
}
public boolean isMe(){
    return getLooper() == Looper.myLooper();
}
private void toDebugLogTrack(String status, Object owner, RunnableW runnable){
    if(lastHandler != this){
        lastHandler = this;
DebugLog.start().send("thread is " + lastHandler.getName()).end();
    }
    String ownerName = DebugTrack.getFullSimpleName(owner);
DebugLog.start().send(getName() + " " + (tracks != null ? tracks.size() + " " : "") + ":: " + status + " " + ownerName + " " + ObjectTo.hashcodeIdentityString(runnable)).end();
}

public void quit(){
    checkAllowedQuit(getLooper(), "quit");
    getLooper().quit();
}
public void quitSafely(){
    checkAllowedQuit(getLooper(), "quitSafely");
    getLooper().quitSafely();
}
public void join(){
    try{
        checkAllowedJoin(getLooper(), "join");
        getThread().join();
    } catch(java.lang.Throwable e){
DebugException.start().log(e).end();
    }
}

@Override
protected void finalize() throws Throwable{
//    countCurrent--;
//    Log.d(DebugLog.TAG, "---- " + getName() + " (" + countCurrent + "/" + countTotal + ") ----");
    super.finalize();
}

public enum Delay{
    VERY_SHORT(50), SHORT(200), NORM(500), LONG(1000), VERY_LONG(5000), DELAY_1(1), DELAY_2(2), DELAY_5(5), DELAY_10(10);
    long d;
    Delay(int d){
        this.d = d;
    }
    public long millisecond(){
        return d;
    }
    public long to(TimeUnit unit){
        return unit.convert(d, TimeUnit.MILLISECONDS);
    }
}

private static class Holder{
    public Handler MAIN = null;
    public Handler PRIMARY = null;
    public Handler SECONDARY = null;
    public Handler LOW = null;

    Holder(){

    }
    void start(){
        initMain();
        initPrimary();
        initSecondary();
    }
    void stop(){
        if(MAIN != null){
            MAIN = null;
        }
        if(PRIMARY != null){
            PRIMARY.getLooper().quit();
            PRIMARY = null;
        }
        if(SECONDARY != null){
            SECONDARY.getLooper().quit();
            SECONDARY = null;
        }
        if(LOW != null){
            LOW.getLooper().quit();
            LOW = null;
        }
    }

    void initMain(){
        MAIN = new Handler(Looper.getMainLooper());
        MAIN.enableTrack(TRACK_ENABLED);
//        MAIN.getThread().setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler(){
//            @Override
//            public void uncaughtException(Thread t, Throwable e){
//                DebugException.start().log(e).end();
//            }
//        });
    }
    void initPrimary(){
        PRIMARY = newHandler("primary", HandlerThread.NORM_PRIORITY, true, false);
        PRIMARY.enableTrack(TRACK_ENABLED);
        PRIMARY.getThread().setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler(){
            @Override
            public void uncaughtException(Thread t, Throwable e){
DebugException.start().log(e).end();
            }
        });
    }
    void initSecondary(){
        SECONDARY = newHandler("secondary", HandlerThread.NORM_PRIORITY, true, false);
        SECONDARY.enableTrack(TRACK_ENABLED);
        SECONDARY.getThread().setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler(){
            @Override
            public void uncaughtException(Thread t, Throwable e){
DebugException.start().log(e).end();
            }
        });

    }
    void initLow(){
        LOW = newHandler("low", HandlerThread.MIN_PRIORITY, true, false);
        LOW.enableTrack(TRACK_ENABLED);
        LOW.getThread().setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler(){
            @Override
            public void uncaughtException(Thread t, Throwable e){
DebugException.start().log(e).end();
            }
        });
    }

    Handler main(){
        return MAIN;
    }
    Handler primary(){
        return PRIMARY;
    }
    Handler secondary(){
        return SECONDARY;
    }
    Handler low(){
        if(LOW == null){
            initLow();
        }
        return LOW;
    }
    boolean isLowExist(){
        return LOW != null;
    }

}

}
