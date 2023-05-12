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

import com.tezov.lib_java_android.database.ItemBase;
import com.tezov.lib_java_android.definition.defCreatable;
import com.tezov.lib_java_android.factory.FactoryObject;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.toolbox.Compare;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java_android.type.android.wrapper.ParcelW;

public class ItemTransactionSync extends ItemBase<ItemTransactionSync>{
public static final Creator<ItemTransactionSync> CREATOR = new Creator<ItemTransactionSync>(){
    @Override
    public ItemTransactionSync createFromParcel(Parcel p){
        ParcelW parcel = ParcelW.obtain().replace(p);
        return ItemTransactionSync.obtain().replaceBy(parcel);
    }

    @Override
    public ItemTransactionSync[] newArray(int size){
        return new ItemTransactionSync[size];
    }
};

static{
    FactoryObject.init(ItemTransactionSync.class);
}

public Long timestamp;
public String name;
public Direction direction;
public ItemSync sync;
public String failedMessage;

public static defCreatable<ItemTransactionSync> getFactory(){
    return FactoryObject.singleton(ItemTransactionSync.class);
}

public static void factoryRelease(){
    FactoryObject.singletonRelease(ItemTransactionSync.class);
}

public static ItemTransactionSync obtain(){
    return getFactory().create();
}

public ItemTransactionSync setTimestamp(Long data){
    this.timestamp = data;
    return this;
}

public ItemTransactionSync setName(String data){
    this.name = data;
    return this;
}

public ItemTransactionSync setDirection(Direction data){
    this.direction = data;
    return this;
}

public ItemTransactionSync setSync(ItemSync data){
    this.sync = data;
    return this;
}

public ItemTransactionSync setFailedMessage(String data){
    this.failedMessage = data;
    return this;
}

public ItemSync.Type getType(){
    if(sync == null){
        return null;
    }
    return sync.type;
}

public ItemTransactionSync initWith(String name, Direction direction, ItemSync itemSync){
    setUid(null);
    setTimestamp(Clock.MilliSecond.now());
    setName(name);
    setDirection(direction);
    setSync(itemSync);
    setFailedMessage(null);
    setDeleted(false);
    return this;
}

@Override
public ItemTransactionSync clear(){
    super.clear();
    setTimestamp(null);
    setName(null);
    setDirection(null);
    setSync(null);
    setFailedMessage(null);
    return this;
}

@Override
public ItemTransactionSync newItem(){
    return ItemTransactionSync.obtain();
}

@Override
public ItemTransactionSync copy(){
    ItemTransactionSync copy = super.copy();
    copy.setTimestamp(timestamp);
    copy.setName(name);
    copy.setDirection(direction);
    copy.setSync(sync.copy());
    copy.setFailedMessage(failedMessage);
    return copy;
}

@Override
protected void fromParcel(ParcelW parcel){
    super.fromParcel(parcel);
    setTimestamp(parcel.readLong());
    setName(parcel.readString());
    setDirection(Direction.valueOf(parcel.readString()));
    parcel.readDummy(); // dummyRead of typeSync
    parcel.readDummy(); // dummyRead of uidSync
    setSync(ItemSync.obtain().replaceBy(ParcelW.obtain().replace(parcel.readBytes())));
    setFailedMessage(parcel.readString());
}

@Override
protected void toParcel(Parcel parcel){
    super.toParcel(parcel);
    parcel.writeValue(timestamp);
    parcel.writeValue(name);
    parcel.writeValue(direction.name());
    if(sync != null){
        parcel.writeValue(sync.type.name());
        parcel.writeValue(sync.getUid().toBytes());
        ParcelW parcelSync = ParcelW.obtain().replace(sync);
        parcel.writeValue(parcelSync.toBytes());
        parcelSync.recycle();
    } else {
        parcel.writeValue(null);
        parcel.writeValue(null);
        parcel.writeValue(null);
    }
    parcel.writeValue(failedMessage);
}

@Override
public boolean equals(Object obj){
    if(obj instanceof ItemTransactionSync){
        boolean isEqual = super.equals(obj);
        if(!isEqual){
            return false;
        }
        ItemTransactionSync second = (ItemTransactionSync)obj;
        isEqual = (Compare.equals(this.timestamp, second.timestamp));
        isEqual &= (Compare.equals(this.name, second.name));
        isEqual &= (Compare.equals(this.direction, second.direction));
        isEqual &= (Compare.equals(this.sync, second.sync));
        return isEqual;
    }
    return super.equals(obj);
}

@Override
public DebugString toDebugString(){
    DebugString data = super.toDebugString();
    data.appendDateAndTime("timestamp", timestamp);
    data.appendElapsedTime("elapsed", timestamp);
    data.append("name", name);
    data.append("direction", direction);
    data.append("uid_sync", sync.getUid());
    data.append("sync.com.tezov.lib.type", sync.type);
    //        retrofit.data.append("sync", sync);
    data.append("failedMessage", failedMessage);
    return data;
}


public enum Direction{
    LOCAL, REMOTE
}

}














