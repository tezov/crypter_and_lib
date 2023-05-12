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
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.util.UtilsList;
import com.tezov.lib_java.util.UtilsString;
import java.util.LinkedList;
import java.util.Set;
import android.util.Log;

import com.tezov.lib_java.application.AppMemory;
import com.tezov.lib_java.async.Handler;
import com.tezov.lib_java.toolbox.Compare;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.ref.Ref;
import com.tezov.lib_java.wrapperAnonymous.ComparatorW;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.util.UtilsMap;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import static com.tezov.lib_java.toolbox.Clock.FormatTime;
import static com.tezov.lib_java.toolbox.Clock.MilliSecond;
import static com.tezov.lib_java.toolbox.Clock.MilliSecondTo;

//IMPROVE remove destroy and replace with weakreferencelistener
//@DebugLogClassFilterArray({
//       @DebugLogClassFilter(ItemBase.class),
//       @DebugLogClassFilter(ByteBuffer.class),
//       @DebugLogClassFilter(WR.class),
//       @DebugLogClassFilter(DialogNavigable.class),
//       @DebugLogClassFilter(FragmentNavigable.class),})
//@DebugLogDataFilterArray({
////        @DebugLogDataFilter("RecyclerListItemTouchSwipperHorizontal"),
//})

public class DebugTrack{
public static boolean ENABLE_LOG = false; // USE BY REFLEXION IN DebugLog
public static boolean ENABLE_RECORD = ENABLE_LOG;
private static final boolean TRACK_ONLY_LOGGED = true;
private static final boolean LOG_CREATED = true;
private static final boolean LOG_DESTROYED = true;
private final ConcurrentHashMap<String, Integer> numberInMemoryByClassMap;
private final ConcurrentHashMap<String, Integer> numberCreatedByClassMap;
private final ConcurrentHashMap<Integer, InstanceDetails> detailsMap;

private static final DebugTrack instance = new DebugTrack();
private final ReentrantLock locker;

private DebugTrack me(){
    return this;
}
private DebugTrack(){
    locker = new ReentrantLock();
    numberInMemoryByClassMap = new ConcurrentHashMap<>();
    numberCreatedByClassMap = new ConcurrentHashMap<>();
    detailsMap = new ConcurrentHashMap<>();
}
public static DebugTrack start(){
    try{
        if(!instance.locker.tryLock(1000, TimeUnit.MILLISECONDS)){
            throw new Throwable("debug track failed to acquire lock");
        }
    } catch(Throwable e){
        Log.d(DebugLog.TAG, e.getMessage());
    }
    return instance;
}
public void end(){
    locker.unlock();
}

public static Class<DebugTrack> myClass(){
    return DebugTrack.class;
}

private static String getTypeName(Class type, boolean simple){
    if(simple){
        if(!type.isAnonymousClass()){
            return type.getSimpleName();
        } else {
            return "***Anonymous***";
        }
    } else {
        if(!type.isAnonymousClass()){
            return type.getName();
        } else {
            return "***Anonymous***";
        }
    }
}
private static String getClassName(Class type, boolean simple){
    if(type == null){
        return "object is null";
    }
    List<Class> types = new ArrayList<>();
    while(type != null){
        types.add(type);
        type = type.getEnclosingClass();
    }
    StringBuilder name = new StringBuilder();
    for(int i = (types.size() - 1); i >= 0; i--){
        type = types.get(i);
        name.append(getTypeName(type, simple) + ":");
    }
    return name.substring(0, name.length() - 1);
}
public static String getFullSimpleName(Class type){
    return getClassName(type, true);
}
public static String getFullSimpleName(Object o){
    if(o == null){
        return "object is null";
    } else {
        if(o instanceof Class){
            return getFullSimpleName((Class)o);
        } else {
            return getFullSimpleName(o.getClass());
        }
    }
}
public static String getFullName(Class type){
    return getClassName(type, false);
}
public static String getFullName(Object o){
    if(o == null){
        return "object is null";
    } else {
        if(o instanceof Class){
            return getFullName((Class)o);
        } else {
            return getFullName(o.getClass());
        }
    }
}
public static String getFullSimpleNameWithHashcode(Class type){
    String name = getClassName(type, true);
    name += ":" + ObjectTo.hashcodeIdentityString(type);
    return name;
}
public static String getFullSimpleNameWithHashcode(Object o){
    if(o == null){
        return "object is null";
    } else if(o instanceof Class){
        return getFullSimpleNameWithHashcode((Class)o);
    } else {
        if((o instanceof Ref) && ((Ref)o).isNotNull()){
            o = ((Ref)o).get();
        }
        String name = getClassName(o.getClass(), true);
        name += ":" + ObjectTo.hashcodeIdentityString(o);
        return name;
    }
}
public static String getFullNameWithHashcode(Class type){
    String name = getClassName(type, false);
    name += ":" + ObjectTo.hashcodeIdentityString(type);
    return name;
}
public static String getFullNameWithHashcode(Object o){
    if(o == null){
        return "object is null";
    } else {
        if(o instanceof Class){
            return getFullNameWithHashcode((Class)o);
        } else {
            String name = getClassName(o.getClass(), false);
            name += ":" + ObjectTo.hashcodeIdentityString(o);
            return name;
        }
    }
}

public static String getAliveInfo(Object o){
    instance.locker.lock();
    InstanceDetails details = instance.detailsMap.get(ObjectTo.hashcodeIdentity(o));
    if(details == null){
        return null;
    }
    String value = "created at " + details.createdTime() + " alive since " + details.aliveSince();
    instance.locker.unlock();
    return value;
}
private int getInstanceCounter(Object o){
    Integer count = numberCreatedByClassMap.get(getFullSimpleName(o));
    return (count != null) ? count : 0;
}
private int getNumberInstance(Object o){
    Integer count = numberInMemoryByClassMap.get(getFullSimpleName(o));
    return (count != null) ? count : 0;
}
public static int getInstanceNumber(Object o){
    instance.locker.lock();
    InstanceDetails instanceDetails = instance.detailsMap.get(ObjectTo.hashcodeIdentity(o));
    int value = (instanceDetails != null) ? instanceDetails.number : UtilsList.NULL_INDEX;
    instance.locker.unlock();
    return value;
}

private boolean isClassExist(Object o){
    return numberInMemoryByClassMap.containsKey(getFullSimpleName(o));
}
private boolean isClassHasExisted(Object o){
    return numberCreatedByClassMap.containsKey(getFullSimpleName(o));
}
private boolean isInstanceExist(Object o){
    return detailsMap.containsKey(ObjectTo.hashcodeIdentity(o));
}

private static boolean isEnable(Object o){
    if(!ENABLE_RECORD){
        return false;
    }
    if(TRACK_ONLY_LOGGED){
        Boolean allowed = DebugLog.allow(myClass(), o.getClass());
        return Compare.isTrueOrNull(allowed);
    } else {
        return true;
    }
}

public DebugTrack create(Object o){
    if(isEnable(o)){
        Handler.DEBUG().post(new Runnable(){
            @Override
            public void run(){
                try{
                    if(!isInstanceExist(o)){
                        if(isClassExist(o)){
                            numberCreatedByClassMap.put(getFullSimpleName(o), getInstanceCounter(o) + 1);
                            numberInMemoryByClassMap.put(getFullSimpleName(o), getNumberInstance(o) + 1);
                        } else {
                            if(!isClassHasExisted(o)){
                                numberCreatedByClassMap.put(getFullSimpleName(o), 1);
                            }
                            numberInMemoryByClassMap.put(getFullSimpleName(o), 1);
                        }
                        InstanceDetails instanceDetails = new InstanceDetails(o);
                        if(!isInstanceExist(o)){
                            detailsMap.put(ObjectTo.hashcodeIdentity(o), instanceDetails);
                        } else {
DebugException.start().explode(myClass().getName() + " HashCode already exist in " + "hashMap").end();
                        }

                        if(LOG_CREATED && DebugLog.allow(myClass(), o)){
                            String data = String.format(Locale.US, "0x%8h    CREATED  n°%06d " + "/ %06d %s", ObjectTo.hashcodeIdentity(o), instanceDetails.number, getNumberInstance(o),
                                    instanceDetails.simpleName);
DebugLog.start().send(myClass(), data).end();
                        }
                    }
                } catch(Throwable e){
                    Log.d(">>:", o.getClass().getName());
                }
            }
        });
    }
    return this;

}
public DebugTrack destroy(Object o){
    if(isEnable(o)){
        Handler.DEBUG().post(new Runnable(){
            @Override
            public void run(){
                try{
                    if(isInstanceExist(o)){
                        numberInMemoryByClassMap.put(getFullSimpleName(o), getNumberInstance(o) - 1);
                        InstanceDetails details = detailsMap.remove(ObjectTo.hashcodeIdentity(o));
                        if(LOG_DESTROYED && DebugLog.allow(myClass(), o)){
                            String data = String.format(Locale.US, "0x%8h  DESTROYED  n°%06d " + "/ %06d %s " + " lived %s ", ObjectTo.hashcodeIdentity(o), details.number,
                                    getNumberInstance(o), details.simpleName, details.aliveSince());
DebugLog.start().send(myClass(), data).end();
                        }
                        if(getNumberInstance(o) <= 0){
                            numberInMemoryByClassMap.remove(getFullSimpleName(o));
                        }
                    } else {
DebugException.start().log("Try to destroy object which do not exist " + DebugTrack.getFullSimpleNameWithHashcode(o)).end();
                    }

                } catch(Throwable e){
                    Log.d(">>:", o.getClass().getName());
                }
            }
        });
    }
    return this;
}

public static int count(){
    return count(null);
}
public static int count(Class type){
    instance.locker.lock();
    int count = 0;
    for(Map.Entry<Integer, InstanceDetails> entry: instance.detailsMap.entrySet()){
        InstanceDetails instanceDetails = entry.getValue();
        Object ref = instanceDetails.ref();
        if((type == null) || ((ref != null) && type.isInstance(ref))){
            count++;
        }
    }
    instance.locker.unlock();
    return count;
}

public static void toDebugLogCreated(){
    Handler.DEBUG().post(new Runnable(){
        @Override
        public void run(){
            instance.locker.lock();
            ComparatorW<Map.Entry<String, Integer>> filter = new ComparatorW<Map.Entry<String, Integer>>(){
                @Override
                public int compare(Map.Entry<String, Integer> e1, Map.Entry<String, Integer> e2){
                    return e2.getValue().compareTo(e1.getValue());
                }
            };
            LinkedHashMap<String, Integer> numberCreatedByClassMapSorted = UtilsMap.sort(instance.numberCreatedByClassMap, filter);

DebugLog.start().send(" ************ Objects Created since app started **************\n").end();
            for(Map.Entry<String, Integer> e: numberCreatedByClassMapSorted.entrySet()){
                Boolean allow = DebugLog.allow(myClass(), e.getKey());
                if((allow == null) || allow){
DebugLog.start().send("%6d %s\n", e.getValue(), e.getKey()).end();
                }
            }
            instance.locker.unlock();
        }
    });
}
public static void toDebugLogInMemory(){
    Handler.DEBUG().post(new Runnable(){
        @Override
        public void run(){
            instance.locker.lock();
            ComparatorW<Map.Entry<String, Integer>> filter = new ComparatorW<Map.Entry<String, Integer>>(){
                @Override
                public int compare(Map.Entry<String, Integer> e1, Map.Entry<String, Integer> e2){
                    return e2.getValue().compareTo(e1.getValue());
                }
            };
            LinkedHashMap<String, Integer> numberInMemoryByClassMapSorted = UtilsMap.sort(instance.numberInMemoryByClassMap, filter);
DebugLog.start().send("************ Name and number Object in memory **************\n").end();
            for(Map.Entry<String, Integer> e: numberInMemoryByClassMapSorted.entrySet()){
                Boolean allow = DebugLog.allow(myClass(), e.getKey());
                if((allow == null) || allow){
DebugLog.start().send("%6d %35s", e.getValue(), e.getKey()).end();
                }
            }
            instance.locker.unlock();
        }
    });
}
public static void toDebugLogMemoryList(){
    Handler.DEBUG().post(new Runnable(){
        @Override
        public void run(){
            instance.locker.lock();
            ComparatorW<Map.Entry<Integer, InstanceDetails>> filter = new ComparatorW<Map.Entry<Integer, InstanceDetails>>(){
                @Override
                public int compare(Map.Entry<Integer, InstanceDetails> e1, Map.Entry<Integer, InstanceDetails> e2){
                    return e1.getValue().timestamp.compareTo(e2.getValue().timestamp);
                }
            };
            LinkedHashMap<Integer, InstanceDetails> objectInstanceNumberSorted = UtilsMap.sort(instance.detailsMap, filter);
DebugLog.start().send("************ " + instance.detailsMap.size() + " object in memory " + "**************").end();
            for(Map.Entry<Integer, InstanceDetails> e: objectInstanceNumberSorted.entrySet()){
                InstanceDetails details = e.getValue();
                if(DebugLog.allow(myClass(), details.ref.get())){
DebugLog.start().send("%35s   0x%8h   n°%06d  || %s live %s", details.simpleName, Integer.toHexString(e.getKey()), details.number, details.createdTime(), details.aliveSince()).end();
                }
            }
            instance.locker.unlock();
        }
    });
}
public static void toDebugLogMemorySize(){
    Handler.DEBUG().post(new Runnable(){
        @Override
        public void run(){
DebugLog.start().send(instance.detailsMap.size() + " objects Size " + AppMemory.used(UnitByte.Mo) + "Mo").end();
        }
    });
}
public static void toDebugLog(){
    toDebugLogCreated();
    toDebugLogInMemory();
    toDebugLogMemoryList();
}

private static class InstanceDetails{
    WeakReference ref;
    String simpleName;
    int number;
    Long timestamp;
    InstanceDetails(final Object ref){
        this.ref = new WeakReference<>(ref);
        this.simpleName = getFullSimpleName(ref);
        this.number = instance.getInstanceCounter(ref);
        this.timestamp = MilliSecond.now();
        //            this.size = TODO_LATER
    }
    Object ref(){
        return ref.get();
    }
    String createdTime(){
        return MilliSecondTo.Time.toString(timestamp, FormatTime.H24_FULL_SSS);
    }
    String aliveSince(){
        return MilliSecondTo.Time.Elapsed.toString(timestamp, FormatTime.mm_ss_SSS);
    }
}

}
