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
import com.tezov.lib_java.buffer.ByteBufferInput;
import com.tezov.lib_java_android.database.sqlLite.dbTableDefinition;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.primaire.Field;
import com.tezov.lib_java.type.primitive.string.StringHexTo;
import com.tezov.lib_java_android.type.android.wrapper.ParcelW;

import java.util.List;

import static com.tezov.lib_java_android.database.adapter.contentValues.ContentValuesCryptBlockTo.KEY_PAYLOAD;

public class DataSnapshotCryptBlockTo extends DataSnapshotCryptRowTo{

private Object valueBlockCryptFrom(ByteBufferInput payload, Field field){
    Class type = field.getType();
    String key = payload.getString();

    if(!(key.equals(field.getName()))){
DebugException.start().log("fields read (" + key + " is not the expected fields (" + field.getName() + ")").end();
        return null;
    }

    Object o;
    if(CompareType.STRING.equal(type)){
        o = payload.getString();
    } else if(CompareType.LONG.equal(type)){
        o = payload.getLong();
    } else if(CompareType.INT.equal(type)){
        o = payload.getInt();
    } else if(CompareType.BOOLEAN.equal(type)){
        o = payload.getBoolean();
    } else if(CompareType.FLOAT.equal(type)){
        o = payload.getFloat();
    } else if(CompareType.UID.equal(type)){
        o = payload.getBytes();
    } else if(CompareType.BYTES.equal(type)){
        o = payload.getBytes();
    } else {

DebugException.start().log("unknown type:" + DebugTrack.getFullSimpleName(type) + " key:" + key).end();

        o = null;
    }
    return o;
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
    ByteBufferInput payload = ByteBufferInput.wrap(decoderValue(dataSnapshot.child(KEY_PAYLOAD).getValue(String.class), byte[].class));
    for(Field field: fields){
        if(field.getType() == dbTableDefinition.PRIMARY_KEY.class){
            String s = dataSnapshot.child(field.getName()).getValue(String.class);
            parcel.write(StringHexTo.Bytes(s));
        } else {
            parcel.write(valueBlockCryptFrom(payload, field));
        }
    }
    return parcel;
}

}

