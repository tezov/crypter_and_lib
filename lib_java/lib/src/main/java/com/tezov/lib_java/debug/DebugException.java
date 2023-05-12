/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.debug;

import android.util.Log;

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
import com.tezov.lib_java.async.Handler;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class DebugException{
public static boolean SHOW_HIDDEN = false;
public static boolean VERBOSE = true;
private static final DebugException instance = new DebugException();
private final ReentrantLock locker;

private Action action = null;
private Throwable exception = null;

private DebugException(){
    locker = new ReentrantLock();
}
public static DebugException start(){
    try{
        if(!instance.locker.tryLock(1000, TimeUnit.MILLISECONDS)){
            throw new Throwable("debug exception failed to acquire lock");
        }
    } catch(Throwable e){
        Log.d(DebugLog.TAG, e.getMessage());
    }
    return instance;
}

public DebugException notImplemented(){
    return notImplemented(null);
}
public DebugException notImplemented(Object owner){
    DebugLog.TraceDetails trace = DebugLog.getTraceDetailsFirst();
    return log("L:" + trace.getLineNumber() + " C:" + trace.getClassNameFull() + " M:" + trace.getMethodName() + (owner != null ? (" " + DebugTrack.getFullSimpleName(owner)) : ""));
}

public DebugException unknown(String what){
    return unknown(what, null);
}
public DebugException unknown(String what, Object value){
    return log("unknown " + (what != null ? what : "") + ":" + DebugObject.toString(value));
}

public DebugException explode(String s){
    return explode(new Throwable(s));
}
public DebugException explode(Throwable e){
    action = Action.EXPLODE;
    exception = e;
    return this;
}

public DebugException log(String s){
    return log(new Throwable(s));
}
public DebugException log(Throwable e){
    action = Action.LOG;
    exception = e;
    return this;
}

public DebugException logHidden(String s){
    return logHidden(new Throwable(s));
}
public DebugException logHidden(Throwable e){
    action = Action.LOG_HIDDEN;
    exception = e;
    return this;
}

public void end(){
    switch(action){
        case LOG:{
            toDebugLog();
        }
        break;
        case LOG_HIDDEN:{
            if(SHOW_HIDDEN){
                toDebugLog();
            }
        }
        break;
        case EXPLODE:{
            toDebugLog();
            Runnable r = new Runnable(){
                @Override
                public void run(){
                    if(exception instanceof RuntimeException){
                        throw (RuntimeException)exception;
                    } else {
                        throw new RuntimeException(exception);
                    }
                }
            };
            if(Handler.MAIN().isMe()){
                r.run();
            }
            else {
                Handler.MAIN().post(r);
            }
        }
        break;
    }
    locker.unlock();
}
private void toDebugLog(){
    if(VERBOSE){
//            DebugLog.start().send(new Exception()).end();
DebugLog.start().send(exception).end();
    } else {
DebugLog.start().send(DebugTrack.getFullSimpleName(exception) + ": " + exception.getMessage()).end();
    }
}
private enum Action{
    LOG, EXPLODE, LOG_HIDDEN
}

}

