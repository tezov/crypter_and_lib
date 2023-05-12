/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.database.prebuild.sync.item;

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.debug.DebugException;
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

import android.os.Parcel;

import com.tezov.lib_java_android.application.AppInfo;
import com.tezov.lib_java_android.database.ItemBase;
import com.tezov.lib_java_android.definition.defCreatable;
import com.tezov.lib_java_android.factory.FactoryObject;
import com.tezov.lib_java.generator.uid.UUID;
import com.tezov.lib_java.generator.uid.defUid;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.toolbox.Compare;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java_android.type.android.wrapper.ParcelW;

public class ItemSync extends ItemBase<ItemSync>{
public static final Creator<ItemSync> CREATOR = new Creator<ItemSync>(){
    @Override
    public ItemSync createFromParcel(Parcel p){
        ParcelW parcel = ParcelW.obtain().replace(p);
        return ItemSync.obtain().replaceBy(parcel);
    }

    @Override
    public ItemSync[] newArray(int size){
        return new ItemSync[size];
    }
};

static{
    FactoryObject.init(ItemSync.class);
}

public Long timestamp;
public Type type;
public UUID guid;

public static defCreatable<ItemSync> getFactory(){
    return FactoryObject.singleton(ItemSync.class);
}

public static void factoryRelease(){
    FactoryObject.singletonRelease(ItemSync.class);
}

public static ItemSync obtain(){
    return getFactory().create();
}

public ItemSync setTimestamp(Long data){
    this.timestamp = data;
    return this;
}

public ItemSync setType(Type data){
    this.type = data;
    return this;
}

public ItemSync setGUID(UUID data){
    this.guid = data;
    return this;
}

public ItemSync initWith(Type type, defUid uid){
    setUid(uid);
    this.timestamp = Clock.MilliSecond.now();
    this.type = type;
    this.guid = AppInfo.getGUID();
    setDeleted(false);
    return this;
}

public ItemSync updateWith(Type type){
    this.timestamp = Clock.MilliSecond.now();
    this.type = type;
    this.guid = AppInfo.getGUID();
    return this;
}

@Override
public ItemSync clear(){
    super.clear();
    setTimestamp(null);
    setType(null);
    setGUID(null);
    return this;
}

@Override
public ItemSync newItem(){
    return ItemSync.obtain();
}

@Override
public ItemSync copy(){
    ItemSync copy = super.copy();
    copy.setTimestamp(timestamp);
    copy.setType(type);
    copy.setGUID(guid);
    return this;
}

@Override
protected void fromParcel(ParcelW parcel){
    super.fromParcel(parcel);
    setTimestamp(parcel.readLong());
    setType(Type.valueOf(parcel.readString()));
    setGUID(UUID.fromBytes(parcel.readBytes()));
}

@Override
protected void toParcel(Parcel parcel){
    super.toParcel(parcel);
    parcel.writeValue(timestamp);
    parcel.writeValue(type.name());
    parcel.writeValue(guid.toBytes());
}

@Override
public boolean equals(Object obj){
    if(obj instanceof ItemSync){
        boolean isEqual = super.equals(obj);
        if(!isEqual){
            return false;
        }
        ItemSync second = (ItemSync)obj;
        isEqual = (Compare.equals(this.timestamp, second.timestamp));
        isEqual &= (Compare.equals(this.type, second.type));
        isEqual &= (Compare.equals(this.guid, second.guid));
        return isEqual;
    }
    return super.equals(obj);
}

@Override
public DebugString toDebugString(){
    DebugString data = super.toDebugString();
    data.appendDateAndTime("timestamp", timestamp);
    data.append("com.tezov.lib.type", type);
    data.append("guid", guid);
    return data;
}


public enum Type{
    INSERT, REMOVE, UPDATE
}

}














