/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.crypter.data.item;

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugTrack;
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

import com.tezov.crypter.data.misc.ClockFormat;
import com.tezov.lib_java.cipher.key.ecdh.KeyAgreement;
import com.tezov.lib_java_android.database.ItemBase;
import com.tezov.lib_java_android.definition.defCreatable;
import com.tezov.lib_java_android.factory.FactoryObject;
import com.tezov.lib_java.generator.uid.UUIDGenerator;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java_android.type.android.wrapper.ParcelW;

public class ItemKeyAgreement extends ItemBase<ItemKeyAgreement>{
public static final Creator<ItemKeyAgreement> CREATOR = new Creator<ItemKeyAgreement>(){
    @Override
    public ItemKeyAgreement createFromParcel(Parcel p){
        ParcelW parcel = ParcelW.obtain().replace(p);
        return ItemKeyAgreement.obtain().replaceBy(parcel);
    }
    @Override
    public ItemKeyAgreement[] newArray(int size){
        return new ItemKeyAgreement[size];
    }
};
final private static UUIDGenerator UUID_GENERATOR = UUIDGenerator.newInstance();

static{
    FactoryObject.init(ItemKeyAgreement.class);
}

private KeyAgreement key = null;
private Long timestamp = null;

public static UUIDGenerator getUidGenerator(){
    return UUID_GENERATOR;
}
public static defCreatable<ItemKeyAgreement> getFactory(){
    return FactoryObject.singleton(ItemKeyAgreement.class);
}
public static void factoryRelease(){
    FactoryObject.singletonRelease(ItemKeyAgreement.class);
}
public static ItemKeyAgreement obtain(){
    return getFactory().create();
}

public KeyAgreement getKey(){
    return key;
}
public ItemKeyAgreement setKey(KeyAgreement key){
    this.key = key;
    return this;
}
public Long getTimestamp(){
    return timestamp;
}
public ItemKeyAgreement setTimestamp(Long timestamp){
    this.timestamp = timestamp;
    return this;
}
public boolean canOffer(){
    if(key != null){
        return key.canMakeByteBuffer();
    } else {
        return false;
    }
}

@Override
public ItemKeyAgreement clear(){
    super.clear();
    timestamp = null;
    return clearKey();
}
public ItemKeyAgreement clearKey(){
    if(this.key != null){
        key.destroyNoThrow();
        key = null;
    }
    return this;
}

@Override
public ItemKeyAgreement newItem(){
    return ItemKeyAgreement.obtain();
}

@Override
public ItemKeyAgreement copy(){
DebugException.start().notImplemented().end();
    return null;
}

@Override
protected void fromParcel(ParcelW parcel){
    super.fromParcel(parcel);
    this.key = KeyAgreement.fromKey(parcel.readBytes());
    this.timestamp = parcel.readLong();

}
@Override
protected void toParcel(Parcel parcel){
    super.toParcel(parcel);
    parcel.writeValue(key.toBytes());
    parcel.writeValue(timestamp);
}

@Override
public DebugString toDebugString(){
    DebugString data = super.toDebugString();
    data.append("timestamp", ClockFormat.longToDateTime_FULL(timestamp));
    data.append("key", key);
    return data;
}


}














