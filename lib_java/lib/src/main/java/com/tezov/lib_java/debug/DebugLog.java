/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.debug;

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.runnable.RunnableW;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import java.util.Set;
import android.util.Log;

import com.tezov.lib_java.async.Handler;
import com.tezov.lib_java.toolbox.Compare;
import com.tezov.lib_java.toolbox.Reflection;
import com.tezov.lib_java.debug.annotation.DebugLogClassFilter;
import com.tezov.lib_java.debug.annotation.DebugLogClassFilterArray;
import com.tezov.lib_java.debug.annotation.DebugLogDataFilter;
import com.tezov.lib_java.debug.annotation.DebugLogDataFilterArray;
import com.tezov.lib_java.debug.annotation.DebugLogEnable;
import com.tezov.lib_java.util.UtilsList;
import com.tezov.lib_java.util.UtilsString;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import java9.util.function.BiConsumer;

//@DebugLogClassFilterArray({
////        @DebugLogClassFilter(FragmentNavigable.class),
//})
//@DebugLogDataFilterArray({
////        @DebugLogDataFilter("SingletonHolder"),
//})
public class DebugLog{
public static final String TAG = ":>> ";
private static final int MAX_DATA_LENGTH = 1000;
private static final boolean logEnable = true;
private static final boolean forceLogAll = false;
private static final boolean tagEnable = false;

private long logNumber = 1L;
private long debugNumber = 1L;
private BiConsumer<String, String> output = getOutputDefault();
private static final DebugLog instance = new DebugLog();
private final ReentrantLock locker;

private DebugLog me(){
    return this;
}

private DebugLog(){
    locker = new ReentrantLock();
}

public static void setOutput(BiConsumer<String, String> output){
    instance.output = output;
}
public static BiConsumer<String, String> getOutputDefault(){
    return new BiConsumer<String, String>(){
        @Override
        public void accept(String tag, String data){
            if("".equals(data)){
                data = " ";
            }
            if(data.length() <= MAX_DATA_LENGTH){
                Log.d(tag, data);
            } else {
                String[] dataSplit = data.split("\n");
                if(dataSplit.length <= 1){
                    dataSplit = UtilsString.split(data, MAX_DATA_LENGTH);
                }
                for(String s: dataSplit){
                    accept(tag, s);
                }
            }
        }
    };
}
private void print(Class type, String data, boolean forceLog){
    Handler.LOG_DELAY().post(new Runnable(){
        @Override
        public void run(){
            boolean allowLog = logEnable;
            allowLog &= (forceLog | forceLogAll | isEnable(type) | Compare.isTrue(allow(me(), data)) | Compare.isTrue(allow(type, data)));
            if(!allowLog){
                return;
            }
            String tag = String.format(Locale.US, "%6d", logNumber++);
            if(!tagEnable){
                tag += TAG;
            }
            else {
                tag += DebugTrack.getFullSimpleName(type) + TAG;
            }
            if(data == null){
                output.accept(tag, "null");
            }
            else {
                output.accept(tag, data);
            }
        }
    });
}

public static DebugLog start(){
    try{
        if(!instance.locker.tryLock(1000, TimeUnit.MILLISECONDS)){
            throw new Throwable("debug log failed to acquire lock");
        }
    } catch(Throwable e){
        Log.d(DebugLog.TAG, e.getMessage());
    }
    return instance;
}
public void end(){
    locker.unlock();
}

public DebugLog send(Object object, String data){
    print(object instanceof Class? (Class)object : object.getClass(), data, false);
    return this;
}
public DebugLog send(Object data){
    print(null, DebugObject.toString(data), true);
    return this;
}
public DebugLog send(String data){
    print(null, data, true);
    return this;
}
public DebugLog send(String format, Object... args){
    print(null, String.format(Locale.US, format, args), true);
    return this;
}
public DebugLog send(){
    print(null, " ", true);
    return this;
}

public DebugLog here(){
    return here(null, getTraceDetailsFirst());
}
public DebugLog here(Object o){
    return here(DebugObject.toString(o), getTraceDetailsFirst());
}
private DebugLog here(String text, TraceDetails trace){
    return send("L:" + trace.lineNumber + " C:" + trace.typeName + " M:" + trace.methodName + ((text != null ? (" " + text) : "")));
}

public DebugLog track(Object o){
    return track(o, null);
}
public DebugLog track(Object o, Object message){
    DebugLog.TraceDetails st = DebugLog.getTraceDetailsFirst();
    StringBuilder data = new StringBuilder();
    data.append(DebugTrack.getFullSimpleName(o)).append("::").append(st.getMethodName());
    int number = DebugTrack.getInstanceNumber(o);
    if(number != UtilsList.NULL_INDEX){
        data.append(" n°").append(number);
    }
    if(message != null){
        data.append(" ->").append(DebugObject.toString(message));
    }
    return send(o, data.toString());
}

public void trace(){
    StackTraceElement[] traces = java.lang.Thread.currentThread().getStackTrace();
    send(DebugObject.toString("", traces));
}

public static TraceDetails getTraceDetailsFirst(StackTraceElement[] traces){
    for(StackTraceElement ste: traces){
        TraceDetails st = new TraceDetails(ste);
        if(!st.isDebugMethod()){
            return st;
        }
    }
    return null;
}
public static TraceDetails getTraceDetailsFirst(Throwable e){
    return getTraceDetailsFirst(e.getStackTrace());
}
public static TraceDetails getTraceDetailsFirst(){
    return getTraceDetailsFirst(java.lang.Thread.currentThread().getStackTrace());
}

public DebugLog debug(){
    return debug(null, null, null);
}
public DebugLog debug(long initNumber){
    return debug(initNumber, null, null);
}
public DebugLog debug(Object o){
    return debug(null, o, null);
}
public DebugLog debug(String s){
    return debug(null, null, s);
}
public DebugLog debug(long initNumber, Object o){
    return debug(initNumber, o, null);
}
public DebugLog debug(long initNumber, String s){
    return debug(initNumber, null, s);
}
public DebugLog debug(Object o, String s){
    return debug(null, o, s);
}
public DebugLog debug(Long initNumber, Object o, String s){
    if(initNumber != null){
        debugNumber = initNumber;
    }
    StringBuilder data = new StringBuilder();
    data.append(debugNumber);
    if(o != null){
        data.append(" " + DebugTrack.getFullSimpleNameWithHashcode(o));
    }
    if(s != null){
        data.append(" " + s);
    }
    debugNumber++;
    return send(data.toString());
}

public static boolean isEnable(Object object){
    return isEnable(object.getClass());
}
public static Boolean isEnable(Class type){
    if(type == null){
        return true;
    }
    return isEnableByMember(type) || isEnableByAnnotation(type);
}
public static boolean isEnableByMember(Object object){
    return isEnableByMember(object.getClass());
}
public static boolean isEnableByMember(Class type){
    try{
        Field f = type.getDeclaredField("ENABLE_LOG");
        return f.getBoolean(null);
    } catch(java.lang.Throwable e){
        return false;
    }
}
public static boolean isEnableByAnnotation(Object object){
    return isEnableByAnnotation(object.getClass());
}
public static boolean isEnableByAnnotation(Class type){
    do{
        DebugLogEnable annotations = (DebugLogEnable)type.getAnnotation(DebugLogEnable.class);
        if(annotations != null){
            return annotations.value();
        }
    } while((type = type.getSuperclass()) != null);
    return false;
}
public static List<Class> findFilterClass(Class type){
    DebugLogClassFilterArray filterArray = (DebugLogClassFilterArray)type.getAnnotation(DebugLogClassFilterArray.class);
    if(filterArray == null){
        return null;
    }
    DebugLogClassFilter[] annotations = filterArray.value();
    List<Class> list = new LinkedList<>();
    for(DebugLogClassFilter annotation: annotations){
        list.add(annotation.value());
    }
    if(list.size() <= 0){
        return null;
    }
    return list;
}
public static List<String> findFilterData(Class type){
    DebugLogDataFilterArray filterArray = (DebugLogDataFilterArray)type.getAnnotation(DebugLogDataFilterArray.class);
    if(filterArray == null){
        return null;
    }
    DebugLogDataFilter[] annotations = filterArray.value();
    List<String> list = new LinkedList<>();
    for(DebugLogDataFilter annotation: annotations){
        list.add(annotation.value());
    }
    if(list.size() <= 0){
        return null;
    }
    return list;
}
public static Boolean allow(Object owner, Class typeToFilter){
    if(typeToFilter == null){
        return true;
    }
    Class typeOwner;
    if(owner instanceof Class){
        typeOwner = (Class)owner;
    } else {
        typeOwner = owner.getClass();
    }
    List<Class> filters = DebugLog.findFilterClass(typeOwner);
    if(filters == null){
        return null;
    }
    for(Class c: filters){
        if(Reflection.isInstanceOf(typeToFilter, c)){
            return true;
        }
    }
    return false;
}
public static Boolean allow(Object owner, String data){
    if(owner == null){
        return true;
    }
    if(data == null){
        return true;
    }
    Class typeOwner;
    if(owner instanceof Class){
        typeOwner = (Class)owner;
    } else {
        typeOwner = owner.getClass();
    }
    List<String> filters = findFilterData(typeOwner);

    if(filters == null){
        return null;
    }
    boolean filterDataFound = false;
    for(String filter: filters){
        if(data.contains(filter)){
            filterDataFound = true;
            break;
        }
    }
    return filterDataFound;
}
public static boolean allow(Object owner, Object objectToFilter){
    if(objectToFilter == null){
        return true;
    }
    Boolean byClass = allow(owner, objectToFilter.getClass());
    Boolean byData = allow(owner, objectToFilter.getClass().getName());
    if((byClass == null) && (byData == null)){
        return true;
    }
    if((byClass != null) && (byData == null)){
        return byClass;
    }
    if((byClass == null) && (byData != null)){
        return byData;
    }
    return byClass || byData;
}

public static class TraceDetails{
    private final int lineNumber;
    private final String typeName;
    private final String methodName;
    public TraceDetails(StackTraceElement trace){
        this.lineNumber = trace.getLineNumber();
        this.typeName = trace.getClassName().replace("$", ".");
        this.methodName = trace.getMethodName();
    }
    public int getLineNumber(){
        return lineNumber;
    }
    public String getClassNameFull(){
        return typeName;
    }
    public String getClassName(){
        String[] s = this.typeName.split("\\.");
        if(s.length > 0){
            return s[s.length - 1].replace("$", ".");
        } else {
            return typeName;
        }
    }
    public String getMethodName(){
        return methodName;
    }
    public boolean isDebugMethod(){
        String s = typeName;
        return s.startsWith("dalvik.system.VMStack") || s.startsWith(DebugTrack.getFullName(java.lang.Thread.class)) ||
               s.startsWith(DebugTrack.getFullName(DebugException.class)) || s.startsWith(DebugTrack.getFullName(DebugLog.class)) ||
               s.startsWith(DebugTrack.getFullName(DebugTrack.class));

    }
    public DebugString toDebugString(){
        DebugString data = new DebugString();
        data.append(getClassName()).append(":");
        data.append(methodName).append(":");
        data.append(lineNumber);
        return data;
    }
    final public void toDebugLog(){
DebugLog.start().send(toDebugString()).end();
    }
}

}
