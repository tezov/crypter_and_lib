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

import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.debug.DebugException;
import com.google.firebase.database.DataSnapshot;
import com.tezov.lib_java.cipher.definition.defDecoder;
import com.tezov.lib_java_android.database.sqlLite.dbTableDefinition;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.type.primaire.Field;
import com.tezov.lib_java.type.primitive.string.StringHexTo;
import com.tezov.lib_java_android.type.android.wrapper.ParcelW;

import java.util.List;

public class DataSnapshotCryptRowTo extends DataSnapshotTo{
protected defDecoder decoderValue = null;

@Override
public void setDecoderValue(defDecoder decryptValue){
    this.decoderValue = decryptValue;
}

protected <T> T decoderValue(String s, Class<T> type){
    return (T)decoderValue.decode(s, type);
}

protected boolean canDecoderValue(){
    return decoderValue != null;
}

private <T> T valueCryptFrom(DataSnapshot dataSnapshot, Field field){
    Class type = field.getType();
    String key = field.getName();
    Object o = dataSnapshot.child(key).getValue(String.class);
    if(type == dbTableDefinition.PRIMARY_KEY.class){
        o = StringHexTo.Bytes((String)o);
    } else if(CompareType.STRING.equal(type)){
        o = decoderValue((String)o, String.class);
    } else if(CompareType.LONG.equal(type)){
        o = decoderValue((String)o, Long.class);
    } else if(CompareType.INT.equal(type)){
        o = decoderValue((String)o, Integer.class);
    } else if(CompareType.BOOLEAN.equal(type)){
        o = decoderValue((String)o, String.class);
    } else if(CompareType.FLOAT.equal(type)){
        o = decoderValue((String)o, Float.class);
    } else if(CompareType.UID.equal(type)){
        o = decoderValue((String)o, byte[].class);
    } else if(CompareType.BYTES.equal(type)){
        o = decoderValue((String)o, byte[].class);
    } else {
DebugException.start().log("unknown type:" + DebugTrack.getFullSimpleName(type) + " key:" + key).end();
    }

    return (T)o;
}

@Override
public ParcelW parcel(DataSnapshot dataSnapshot, List<Field> fields){
    if(dataSnapshot == null){
        return null;
    }
    if(!canDecoderValue()){
        return super.parcel(dataSnapshot, fields);
    }
    ParcelW parcel = ParcelW.obtain();
    for(Field field: fields){
        parcel.write(valueCryptFrom(dataSnapshot, field));
    }
    return parcel;
}

}

