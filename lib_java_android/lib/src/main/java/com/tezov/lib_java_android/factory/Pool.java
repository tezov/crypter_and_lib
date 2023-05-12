/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.factory;

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
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.definition.defClearable;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.type.ref.WoSR;
import com.tezov.lib_java.wrapperAnonymous.FunctionW;
import com.tezov.lib_java_android.application.AppConfig;
import com.tezov.lib_java_android.application.AppConfigKey;
import com.tezov.lib_java_android.toolbox.SingletonHolder;

import java.lang.ref.ReferenceQueue;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

// NEXT_TODO destroy inactive after inactive time

public class Pool<T>{
private final static boolean POOL_ENABLE = AppConfig.getBoolean(AppConfigKey.POOL_ENABLE.getId());

private static final String CREATED = " CREATED ";
private static final String RECYCLED = " OBTAIN ";
private static final String RELEASED = " RELEASED ";
private static final String LOST = " LOST ";

private final ConcurrentHashMap<Integer, ObjectDetails> poolActiveObject = new ConcurrentHashMap<>();
private final ConcurrentHashMap<Class<?>, ConcurrentLinkedQueue<Pool.ObjectDetails>> poolInactiveObject = new ConcurrentHashMap<>();
private final ConcurrentHashMap<Class<?>, CounterDetails> counters = new ConcurrentHashMap<>();
public ReferenceQueue<T> lostObjects = new ReferenceQueue<>();
private FunctionW<Class<? extends T>, T> factory;
private Class<T> type;
private int maxRetain = 50;
private int maxRetainByClass = 10;
private int countRetained = 0;
private int countCreated = 0;
private int countObtained = 0;
private int countReleased = 0;
private int countLost = 0;

protected Pool(Class<T> type, FunctionW<Class<? extends T>, T> factory){
    try{
DebugTrack.start().create(this).end();
        this.factory = factory;
        this.type = type;
    } catch(java.lang.Throwable e){

DebugException.start().log(e).end();

    }
}
public static <T> boolean exist(Class<T> type){
    return SingletonHolder.exist(Pool.class, type);
}
public static <T> void init(Class<T> type, FunctionW<Class<T>, T> createFunction){
    SingletonHolder.getWithInit(Pool.class, type, Arrays.asList(type, createFunction));
}
public static <T> Pool<T> pool(Class<T> type){
    return SingletonHolder.get(Pool.class, type);

}
public static <T extends defClearable<T>> void poolRelease(Class<T> type){
    SingletonHolder.release(Pool.class, type);
}

public static <T> void toDebugLog(Pool<T> pool){
    String buffer = "************ Pool " + pool.type.getSimpleName() + " **************" +
            "\n" + pool.poolActiveObject.size() + " references actives" +
            "\n" + pool.poolInactiveObject.size() + " references inactives" +
            "\n" + pool.countCreated + " created" +
            "\n" + pool.countRetained + " retained" +
            "\n" + pool.countObtained + " obtained" +
            "\n" + pool.countReleased + " released" +
            "\n" + pool.countLost + " cleanLost";
DebugLog.start().send(buffer).end();

DebugLog.start().send("************ List of " + pool.poolActiveObject.size() + " Active Object in" + " pool " + pool.type.getSimpleName() + "**************").end();
    for(Pool.ObjectDetails object: pool.poolActiveObject.values()){
DebugLog.start().send(" id:0x" + object.hashCodeString() + " released " + object.countReleased + " times" + " obtained " + object.countObtained + " times").end();
    }

DebugLog.start().send("************ List of " + pool.poolInactiveObject.size() + " Inactive " + "Object in pool " + pool.type.getSimpleName() + "**************").end();
    for(Map.Entry<Class<?>, ConcurrentLinkedQueue<Pool.ObjectDetails>> e: pool.poolInactiveObject.entrySet()){
        for(Pool.ObjectDetails object: e.getValue()){
DebugLog.start()
                    .send(" id:0x" + object.hashCodeString() + "of " + e.getKey().getSimpleName() + " released " + object.countReleased + " times" + " obtained " + object.countObtained + " times")
                    .end();
        }
    }
}

public Class<T> type(){
    return type;
}

public Pool<T> setMaxRetain(int maxRetain){
    this.maxRetain = maxRetain;
    return this;
}

public Pool<T> setMaxRetainByClass(int maxRetainByClass){
    this.maxRetainByClass = maxRetainByClass;
    return this;
}

private void pushPoolInactive(Class<? extends T> type, ObjectDetails object){
    if(object.canBeRetained()){
        poolInactiveObject.get(type).offer(object);
    }
}

private ObjectDetails popPoolInactive(Class<? extends T> type){
    if(!poolInactiveObject.containsKey(type)){
        poolInactiveObject.put(type, new ConcurrentLinkedQueue<Pool.ObjectDetails>());
    }
    return poolInactiveObject.get(type).poll();
}

private void putPoolActive(ObjectDetails object){
    poolActiveObject.put(object.hashCode(), object);
}

private ObjectDetails removePoolActive(ObjectDetails object){
    return poolActiveObject.remove(object.hashCode());
}

public <Tex extends T> Tex obtain(Class<Tex> type){
    synchronized(this){
        cleanLost();
        return (Tex)obtainObject(type).makeWeak();
    }
}

public <Tex extends T> Tex obtain(){
    return (Tex)obtain(type);
}

private ObjectDetails obtainObject(Class<? extends T> type){
    ObjectDetails object = popPoolInactive(type);
    if((object == null) || !POOL_ENABLE){
        T t = factory.apply(type);
        object = new ObjectDetails(t);

DebugLog.start().send(this, " id:0x" + object.hashCodeString() + CREATED + "of " + DebugTrack.getFullSimpleName(object.ref.getType())).end();

    } else {
        countObtained++;

DebugLog.start()
                .send(this, " id:0x" + object.hashCodeString() + RECYCLED + "of " + DebugTrack.getFullSimpleName(object.ref.getType()) + " obtained " + object.countObtained + " times" + "  released" +
                            object.countReleased + " times")
                .end();

    }
    putPoolActive(object);
    return object;
}

public void release(T t){
    synchronized(this){
        ObjectDetails object = poolActiveObject.get(t.hashCode());
        releaseObject((Class<T>)t.getClass(), object.makeStrong());
    }
}

private void releaseObject(Class<? extends T> type, ObjectDetails object){
    synchronized(this){
        removePoolActive(object);
        if(POOL_ENABLE){
            pushPoolInactive(type, object);
        } else {
DebugException.start().logHidden("POOL IS DISABLED").end();
        }

        countReleased++;

DebugLog.start()
                .send(this,
                        " id:0x" + object.hashCodeString() + RELEASED + "of " + DebugTrack.getFullSimpleName(object.ref.getType()) + " released " + object.countReleased + " times" + "  obtained " +
                        object.countObtained + " times")
                .end();

    }
}

private void cleanLost(){
    WoSR.LostRef weakRef;
    while((weakRef = (WoSR.LostRef)lostObjects.poll()) != null){
        ObjectDetails object = poolActiveObject.remove(weakRef.hashCode());
        countLost++;

DebugLog.start()
                .send(this, " id:0x" + object.hashCodeString() + LOST + "of " + DebugTrack.getFullSimpleName(weakRef.get()) + " released " + object.countReleased + " times" + " obtained " +
                            object.countObtained + " times")
                .end();

    }
}

final public void toDebugLog(){
    toDebugLog(this);
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

public static class CounterDetails{
    int count = 0;
    long lastUsed;
    public CounterDetails(){
        updateTimestamp();
    }
    public void updateTimestamp(){
        lastUsed = Clock.MilliSecond.now();
    }

}

public class ObjectDetails{
    private final WoSR<T> ref;
    private Integer countReleased = 0;
    private Integer countObtained = 0;
    public ObjectDetails(T t){
        this.ref = new WoSR(t, false);
        countCreated++;
        countRetained++;
        incCount();
    }
    public T ref(){
        return ref.get();
    }
    public T makeWeak(){
        countObtained++;
        return ref.makeWeak().get();
    }
    public ObjectDetails makeStrong(){
        countReleased++;
        ref.makeStrong();
        return this;
    }
    protected CounterDetails getCounterDetails(){
        Class<T> type = ref.getType();
        CounterDetails details = counters.get(type);
        if(details == null){
            details = new CounterDetails();
            counters.put(type, details);
        }
        return details;
    }
    protected void incCount(){
        Class<T> type = ref.getType();
        CounterDetails details = getCounterDetails();
        details.count++;
        counters.put(type, details);
    }
    protected void decCount(){
        Class<T> type = ref.getType();
        CounterDetails details = getCounterDetails();
        details.count--;
        if(details.count > 0){
            counters.put(type, details);
        } else {
            counters.remove(type);
        }
    }
    protected boolean canBeRetained(){
        CounterDetails details = getCounterDetails();
        details.updateTimestamp();
        return ((details.count < maxRetainByClass) && (countRetained < maxRetain));
    }
    @Override
    public int hashCode(){
        return ref.hashCode();
    }
    public String hashCodeString(){
        return ref.hashCodeString();
    }
    @Override
    public boolean equals(Object obj){
        if(!(obj instanceof Pool.ObjectDetails)){
            return false;
        }
        return ref.equals(((ObjectDetails)obj).ref);
    }
    @Override
    protected void finalize() throws Throwable{
        countRetained--;
        if(ref.isNotNull()){
            decCount();
        }
        super.finalize();
    }

}

}
