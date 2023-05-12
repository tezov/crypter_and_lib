/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.type.android.wrapper;

import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.util.UtilsString;
import java.util.List;
import java.util.LinkedList;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.os.Parcelable;

import com.tezov.lib_java.definition.defClearable;
import com.tezov.lib_java_android.definition.defCreatable;
import com.tezov.lib_java_android.factory.FactoryBundle;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.primitive.BytesTo;

import java.util.Arrays;
import java.util.Set;

//Avoid not portable...
public class BundleW implements defClearable<BundleW>{
private Bundle bundle = null;

protected BundleW(){
DebugTrack.start().create(this).end();
}

public static DebugString toStringDebug(Bundle bundle){
    return toStringDebug(bundle, null);
}
public static void toDebugLog(Bundle bundle){
DebugLog.start().send(toStringDebug(bundle)).end();
}

public static defCreatable<BundleW> getFactory(){
    return FactoryBundle.factory();
}

public static void factoryRelease(){
    FactoryBundle.factoryRelease();
}

public static BundleW obtain(){
    return FactoryBundle.factory().create();
}
public static DebugString toStringDebug(Bundle bundle, String[] keysFilter){
    DebugString data = new DebugString();
    if(bundle == null){
        data.append("bundle is null");
    } else if(bundle.size() == 0){
        data.append("bundle is empty");
    } else {
        Set<String> keys = bundle.keySet();
        for(String key: keys){
            Object value = bundle.get(key);
            if((keysFilter == null) || (Arrays.asList(keysFilter).contains(key))){
                if(!(value instanceof byte[])){
                    data.append("[k-").append(key).append(":").append((value == null) ? null : "c-" + DebugTrack.getFullSimpleName(value) + "-v-" + value.toString()).append("]");
                } else {
                    data.append("[k-").append(key).append(":").append("c-byte[]-v-" + BytesTo.StringHex((byte[])value)).append("]");
                }
            }
        }
    }
    return data;
}
public BundleW replace(Bundle bundle){
    this.bundle = bundle;
    return this;
}
public boolean isEmpty(){
    return bundle == null || bundle.isEmpty();
}
public Bundle get(){
    if(bundle == null){
        bundle = new Bundle();
    }
    return bundle;
}
@Override
public BundleW clear(){
    if(bundle != null){
        bundle.clear();
    }
    return this;
}
public void put(String key, String value){
    get().putString(key, value);
}
public void put(String key, byte[] value){
    get().putByteArray(key, value);
}
public void put(String key, boolean value){
    get().putBoolean(key, value);
}
public void put(String key, int value){
    get().putInt(key, value);
}
public void put(String key, long value){
    get().putLong(key, value);
}
public void put(String key, float value){
    get().putFloat(key, value);
}
public void put(String key, Parcelable value){
    get().putParcelable(key, value);
}
public void put(String key, Object value){
    if(value instanceof String){
        put(key, (String)value);
    } else if(value instanceof byte[]){
        put(key, (byte[])value);
    } else if(value instanceof Boolean){
        put(key, (boolean)value);
    } else if(value instanceof Integer){
        put(key, (int)value);
    } else if(value instanceof Long){
        put(key, (long)value);
    } else if(value instanceof Float){
        put(key, (float)value);
    } else if(value instanceof Parcelable){
        put(key, (Parcelable)value);
    } else {

DebugException.start().unknown("type", value).end();

    }
}

public Set<String> getKeys(){
    return get().keySet();
}
final public void toDebugLog(){
DebugLog.start().send(toStringDebug(bundle, null)).end();
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}


}
