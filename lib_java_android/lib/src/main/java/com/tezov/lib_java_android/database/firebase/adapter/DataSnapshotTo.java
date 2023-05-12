/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.database.firebase.adapter;

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.util.UtilsString;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.tezov.lib_java.cipher.definition.defDecoder;
import com.tezov.lib_java_android.database.ItemBase;
import com.tezov.lib_java_android.database.sqlLite.dbTableDefinition;
import com.tezov.lib_java_android.definition.defCreatable;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.primaire.Field;
import com.tezov.lib_java.type.primitive.string.StringHexTo;
import com.tezov.lib_java_android.type.android.wrapper.ParcelW;

import java.util.List;

public class DataSnapshotTo implements defDataSnapshotTo{
public DataSnapshotTo(){
DebugTrack.start().create(this).end();
}

@Override
public void setDecoderValue(defDecoder decryptValue){

DebugException.start().notImplemented().end();

}

private <T> T valueFrom(DataSnapshot dataSnapshot, Field field){
    Class type = field.getType();
    String key = field.getName();
    dataSnapshot = dataSnapshot.child(key);
    Object o;
    if(type == dbTableDefinition.PRIMARY_KEY.class){
        String s = dataSnapshot.getValue(String.class);
        o = StringHexTo.Bytes(s);
    } else if(CompareType.STRING.equal(type)){
        o = dataSnapshot.getValue(String.class);
    } else if(CompareType.LONG.equal(type)){
        o = dataSnapshot.getValue(Long.class);
    } else if(CompareType.INT.equal(type)){
        o = dataSnapshot.getValue(Integer.class);
    } else if(CompareType.BOOLEAN.equal(type)){
        o = dataSnapshot.getValue(String.class);
        if(o != null){
            o = o.equals("1");
        }
    } else if(CompareType.FLOAT.equal(type)){
        o = dataSnapshot.getValue(Float.class);
    } else if(CompareType.UID.equal(type)){
        String s = dataSnapshot.getValue(String.class);
        o = StringHexTo.Bytes(s);
    } else if(CompareType.BYTES.equal(type)){
        String s = dataSnapshot.getValue(String.class);
        o = StringHexTo.Bytes(s);
    } else {

DebugException.start().log("unknown type:" + DebugTrack.getFullSimpleName(type) + " key:" + key).end();

        o = null;
    }
    return (T)o;
}

@Override
public ParcelW parcel(DataSnapshot dataSnapshot, List<Field> fields){
    if(dataSnapshot == null){
        return null;
    }
    ParcelW parcel = ParcelW.obtain();
    for(Field field: fields){
        parcel.write(valueFrom(dataSnapshot, field));
    }
    return parcel;
}

@Override
public <ITEM extends ItemBase> ITEM item(DataSnapshot dataSnapshot, List<Field> fields, defCreatable<ITEM> factory){
    ParcelW parcel = parcel(dataSnapshot, fields);
    if(parcel == null){
        return null;
    }
    return factory.create(parcel);
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

}

