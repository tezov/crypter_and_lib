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
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.util.UtilsString;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import androidx.fragment.app.Fragment;

import com.tezov.lib_java_android.definition.defCreatable;
import com.tezov.lib_java_android.definition.defReplaceable;
import com.tezov.lib_java.toolbox.Reflection;
import com.tezov.lib_java_android.toolbox.SingletonHolder;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java_android.type.android.wrapper.ParcelW;

public class FactoryObject<T> implements defCreatable<T>{
private Class<T> type;
private boolean isReplaceable;

protected FactoryObject(Class<T> type){
    try{
DebugTrack.start().create(this).end();
        this.type = type;
        isReplaceable = Reflection.hasInterface(type, defReplaceable.class);
    } catch(java.lang.Throwable e){

DebugException.start().log(e).end();

    }
}

public static <T> boolean exist(Class<T> type){
    return SingletonHolder.exist(FactoryObject.class, type);
}

public static <T> void init(Class<T> type){
    SingletonHolder.getWithInit(FactoryObject.class, type, type);
}

public static <T> FactoryObject<T> singleton(Class<T> type){
    return (FactoryObject<T>)SingletonHolder.get(FactoryObject.class, type);
}

public static <T> void singletonRelease(Class<T> generic){
    SingletonHolder.release(FactoryObject.class, generic);
}

public boolean isReplaceable(){
    return isReplaceable;
}

@Override
public Class<T> getType(){
    return type;
}

@Override
public T create(){
    return Reflection.newInstance(getType());
}

@Override
public T create(ParcelW parcel){
    if(isReplaceable()){
        return ((defReplaceable<T>)create()).replaceBy(parcel);
    } else {

DebugException.start().notImplemented().end();

        return null;
    }
}

public DebugString toDebugString(){
    DebugString data = new DebugString();
    data.append("Factory type", DebugTrack.getFullSimpleName(this));
    data.append("Created type", DebugTrack.getFullSimpleName(getType()));
    return data;
}

final public void toDebugLog(){
DebugLog.start().send(toDebugString()).end();
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

}