/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.toolbox;

import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.util.UtilsString;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import androidx.fragment.app.Fragment;

import java.util.Collections;

import com.tezov.lib_java.async.Handler;
import com.tezov.lib_java.toolbox.Reflection;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.runnable.RunnableW;
import com.tezov.lib_java.wrapperAnonymous.ComparatorW;
import com.tezov.lib_java.util.UtilsMap;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SingletonHolder{
// Private
private final static ConcurrentHashMap<String, Object> classes = new ConcurrentHashMap<>();

private SingletonHolder(){
}

private static Class myClass(){
    return SingletonHolder.class;
}

private static String key(Class<?> type){
    return type.getName();
}

private static String key(Class<?> type, Object generic){
    return type.getName() + "<" + generic.toString() + ">";
}

private static boolean exist(String key){
    return classes.containsKey(key);
}

private static <T> T get(String key){
    T t = (T)classes.get(key);

DebugLog.start().send(myClass(), "obtain initTables of class " + key).end();

    return t;
}

private static <T> T create(String key, Class<T> type, List<Object> argTypes){
    if(exist(key)){
        return get(key);
    }
    T t = Reflection.newInstance(type, argTypes);
    classes.put(key, t);

DebugLog.start().send(myClass(), "creation initTables with initialisation of " + key).end();

    return t;

}

private static <T> T release(String key){
    if(!exist(key)){
        return null;
    }
    T t = (T)classes.remove(key);

DebugLog.start().send(myClass(), "release initTables of class " + key).end();

    return t;
}

// Public
synchronized public static <T> T get(Class<T> type){
    if(exist(type)){
        return get(key(type));
    } else {
        return create(key(type), type, null);
    }
}

synchronized public static <T> T get(Class<T> type, Object generic){
    if(exist(type, generic)){
        return get(key(type, generic));
    } else {
        return create(key(type, generic), type, null);
    }
}

synchronized public static <T> T getWithInit(Class<T> type, Object o){
    if(exist(type)){

DebugException.start().log("Can not initialize, singleton already exist:" + DebugTrack.getFullSimpleName(type)).end();


        return get(key(type));
    } else {
        return create(key(type), type, Collections.singletonList(o));
    }
}

synchronized public static <T> T getWithInit(Class<T> type, List<Object> argTypes){
    if(exist(type)){

DebugException.start().log("Can not initialize, singleton already exist:" + DebugTrack.getFullSimpleName(type)).end();


        return get(key(type));
    } else {
        return create(key(type), type, argTypes);
    }
}

synchronized public static <T> T getWithInit(Class<T> type, Object generic, Object o){
    if(exist(type, generic)){

DebugException.start().log("Can not initialize, singleton already exist:" + DebugTrack.getFullSimpleName(type) + ":" + DebugTrack.getFullSimpleName(generic)).end();


        return get(key(type, generic));
    } else {
        return create(key(type, generic), type, Collections.singletonList(o));
    }
}

synchronized public static <T> T getWithInit(Class<T> type, Object generic, List<Object> argTypes){
    if(exist(type, generic)){

DebugException.start().log("Can not initialize, singleton already exist:" + DebugTrack.getFullSimpleName(type) + ":" + DebugTrack.getFullSimpleName(generic)).end();


        return get(key(type, generic));
    } else {
        return create(key(type, generic), type, argTypes);
    }
}

synchronized public static <T> T release(Class<T> type){
    return release(key(type));
}

synchronized public static <T> T release(Class<T> type, Object generic){
    return release(key(type, generic));
}

public static boolean exist(Class<?> type){
    return exist(key(type));
}

public static boolean exist(Class<?> type, Object generic){
    return exist(key(type, generic));
}

public static void toDebugLog(){
    toDebugLog(null);
}

public static void toDebugLog(Class<?> type){
    Handler.LOG_DELAY().post(new RunnableW(){
        @Override
        public void runSafe(){
            ComparatorW<Map.Entry<String, Object>> filter = new ComparatorW<Map.Entry<String, Object>>(){
                @Override
                public int compare(Map.Entry<String, Object> e1, Map.Entry<String, Object> e2){
                    String s1 = DebugTrack.getFullSimpleName(e1.getValue()).toLowerCase();
                    String s2 = DebugTrack.getFullSimpleName(e2.getValue()).toLowerCase();
                    return s1.compareTo(s2);
                }
            };
            Map<String, Object> classesSorted = UtilsMap.sort(classes, filter);
DebugLog.start().send("************ List Object in memory **************").end();
            for(Map.Entry<String, Object> e: classesSorted.entrySet()){
                if((type == null) || (e.getValue().getClass() == type)){
DebugLog.start().send(DebugTrack.getFullSimpleName(e.getValue()) + " " + DebugTrack.getAliveInfo(e.getValue())).end();
                }
            }
        }
    });
}

}
