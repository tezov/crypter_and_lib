/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.database.prebuild.listEntry.item;

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

import android.os.Parcel;

import com.tezov.lib_java_android.database.ItemBase;
import com.tezov.lib_java_android.definition.defCreatable;
import com.tezov.lib_java_android.factory.FactoryObject;
import com.tezov.lib_java.generator.uid.UUIDGenerator;
import com.tezov.lib_java.generator.uid.UidBase;
import com.tezov.lib_java.generator.uid.defUid;
import com.tezov.lib_java.toolbox.Compare;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java.type.primitive.BytesTo;
import com.tezov.lib_java.type.primitive.string.StringCharTo;
import com.tezov.lib_java_android.type.android.wrapper.ParcelW;

public class ItemEntry extends ItemBase<ItemEntry>{
public static final Creator<ItemEntry> CREATOR = new Creator<ItemEntry>(){
    @Override
    public ItemEntry createFromParcel(Parcel p){
        ParcelW parcel = ParcelW.obtain().replace(p);
        return ItemEntry.obtain().replaceBy(parcel);
    }

    @Override
    public ItemEntry[] newArray(int size){
        return new ItemEntry[size];
    }
};
final private static UUIDGenerator UUID_GENERATOR = UUIDGenerator.newInstance();

static{
    FactoryObject.init(ItemEntry.class);
}

public String value;

public static UUIDGenerator getUidGenerator(){
    return UUID_GENERATOR;
}

public static defUid keyToUID(String s){
    if(s == null){
        return null;
    } else {
        return UidBase.fromBytes(StringCharTo.Bytes(s));
    }
}

public static String bytesToKey(byte[] bytes){
    if(bytes == null){
        return null;
    } else {
        return BytesTo.StringChar(bytes);
    }
}

public static defCreatable<ItemEntry> getFactory(){
    return FactoryObject.singleton(ItemEntry.class);
}

public static void factoryRelease(){
    FactoryObject.singletonRelease(ItemEntry.class);
}

public static ItemEntry obtain(){
    return getFactory().create();
}

public ItemEntry setValue(String data){
    this.value = data;
    return this;
}

public String getKey(){
    if(getUid() == null){
        return null;
    } else {
        return bytesToKey(getUid().toBytes());
    }
}

public ItemEntry setKey(String data){
    setUid(keyToUID(data));
    return this;
}

@Override
public ItemEntry clear(){
    super.clear();
    setKey(null);
    setValue(null);
    return this;
}

@Override
public ItemEntry newItem(){
    return ItemEntry.obtain();
}

@Override
public ItemEntry copy(){
    ItemEntry copy = super.copy();
    copy.setValue(value);
    return this;
}

@Override
protected void fromParcel(ParcelW parcel){
    super.fromParcel(parcel);
    setValue(parcel.readString());
}

@Override
protected void toParcel(Parcel parcel){
    super.toParcel(parcel);
    parcel.writeValue(value);
}

@Override
public boolean equals(Object obj){
    if(obj instanceof ItemEntry){
        boolean isEqual = super.equals(obj);
        if(!isEqual){
            return false;
        }
        ItemEntry second = (ItemEntry)obj;
        isEqual &= (Compare.equals(this.value, second.value));
        return isEqual;
    }
    return super.equals(obj);
}

@Override
public DebugString toDebugString(){
    DebugString data = super.toDebugString();
    data.append("key", getKey());
    data.append("value", value);
    return data;
}

}














