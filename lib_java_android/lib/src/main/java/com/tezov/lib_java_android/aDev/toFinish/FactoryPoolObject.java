/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.aDev.toFinish;

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

import com.tezov.lib_java.definition.defClearable;
import com.tezov.lib_java_android.definition.defCreatable;
import com.tezov.lib_java_android.definition.defReplaceable;
import com.tezov.lib_java_android.factory.FactoryObject;
import com.tezov.lib_java_android.factory.Pool;
import com.tezov.lib_java.toolbox.Reflection;
import com.tezov.lib_java_android.toolbox.SingletonHolder;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.wrapperAnonymous.FunctionW;
import com.tezov.lib_java_android.type.android.wrapper.ParcelW;

//NEXT_TODO
public class FactoryPoolObject<T extends defClearable<T>> implements defCreatable<T>{
private final Pool<T> pool;
private final boolean isReplaceable;

protected FactoryPoolObject(Pool<T> pool){
DebugTrack.start().create(this).end();
    this.pool = pool;
    isReplaceable = Reflection.hasInterface(pool.type(), defReplaceable.class);
}

public static <T> boolean exist(Class<T> type){
    return SingletonHolder.exist(FactoryPoolObject.class, type);
}

public static <T extends defClearable<T>> void init(defCreatable<T> factory){
    if(!Pool.exist(factory.getType())){
        Pool.init(factory.getType(), new FunctionW<Class<T>, T>(){
            @Override
            public T apply(Class<T> type){
                return factory.create();
            }
        });
    }
    SingletonHolder.getWithInit(FactoryPoolObject.class, factory.getType(), Pool.pool(factory.getType()));
}

public static <T extends defClearable<T>> void init(Class<T> type){
    if(!FactoryObject.exist(type)){
        FactoryObject.init(type);
    }
    init(FactoryObject.singleton(type));
}

public static <T extends defClearable<T>> FactoryPoolObject<T> singleton(Class<T> type){
    return (FactoryPoolObject<T>)SingletonHolder.get(FactoryPoolObject.class, type);
}

public static <T extends defClearable<T>> void factoryRelease(Class<T> type){
    Pool.poolRelease(type);
    FactoryObject.singletonRelease(type);
}

@Override
public Class<T> getType(){
    return pool.type();
}

@Override
public T create(){
    return pool.obtain();
}

@Override
public T create(ParcelW parcel){
    if(isReplaceable){
        return ((defReplaceable<T>)create()).replaceBy(parcel);
    }

DebugException.start().notImplemented().end();

    return null;
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

}
