/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.database;

import com.tezov.lib_java.debug.DebugException;
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

import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;

import com.tezov.lib_java.definition.defClearable;
import com.tezov.lib_java.definition.defCopyable;
import com.tezov.lib_java_android.definition.defReplaceable;
import com.tezov.lib_java.generator.uid.UidBase;
import com.tezov.lib_java.generator.uid.defHasUid;
import com.tezov.lib_java.generator.uid.defUid;
import com.tezov.lib_java.toolbox.Compare;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.wrapperAnonymous.FunctionW;
import com.tezov.lib_java_android.type.android.wrapper.ParcelW;

import java.util.List;

public abstract class ItemBase<T extends ItemBase> implements BaseColumns, Parcelable, defHasUid, defCopyable<T>, defReplaceable<T>, defClearable<T>{
private defUid uid;
private boolean deleted;

protected ItemBase(){
DebugTrack.start().create(this).end();
}

public static void toDebugLog(List<? extends ItemBase> list){
DebugLog.start().send(list).end();
}

@Override
public defUid getUid(){
    return uid;
}

public ItemBase<T> setUid(defUid uid){
    this.uid = uid;
    return this;
}
protected defUid bytesToUID(byte[] bytes){
    return UidBase.fromBytes(bytes);
}

public boolean isDeleted(){
    return deleted;
}

public T setDeleted(boolean deleted){
    this.deleted = deleted;
    return (T)this;
}

@Override
public T clear(){
    setUid(null);
    setDeleted(false);
    return (T)this;
}

public abstract T newItem();

@Override
public T copy(){
    T copy = newItem();
    copy.setUid(uid);
    copy.setDeleted(deleted);
    return copy;
}

protected void fromParcel(ParcelW parcel){
    parcel.resetPosition();
    setUid(bytesToUID(parcel.readBytes()));
    setDeleted(parcel.readBoolean());
}

@Override
final public T replaceBy(ParcelW parcel){
    if(parcel == null){
        return null;
    }
    fromParcel(parcel);
    parcel.recycle();
    return (T)this;
}

protected void toParcel(Parcel parcel){
    parcel.writeValue(uid!=null?uid.toBytes():null);
    parcel.writeValue(deleted);
}

@Override
final public void writeToParcel(Parcel parcel, int flags){
    toParcel(parcel);
}

@Override
public int describeContents(){
    return 0;
}

@Override
public boolean equals(Object obj){
    if(obj instanceof ItemBase){
        ItemBase second = (ItemBase)obj;
        boolean isEqual = (Compare.equals(this.uid, second.uid));
        isEqual &= (Compare.equals(this.deleted, second.deleted));
        return isEqual;
    }
    if(obj instanceof defUid){
        defUid second = (defUid)obj;
        if(this.uid == null){
            return false;
        }
        return this.uid.equals(second);
    }
    return false;
}

public DebugString toDebugString(){
    DebugString data = new DebugString();
    data.append("uuid", uid);
    data.append("deleted", deleted);
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














