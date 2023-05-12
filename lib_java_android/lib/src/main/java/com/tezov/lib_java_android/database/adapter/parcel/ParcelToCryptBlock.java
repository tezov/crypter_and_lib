/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.database.adapter.parcel;

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

import com.tezov.lib_java.buffer.ByteBufferOutput;
import com.tezov.lib_java_android.database.sqlLite.dbTableDefinition;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.primaire.Field;
import com.tezov.lib_java_android.type.android.wrapper.ContentValuesW;
import com.tezov.lib_java_android.type.android.wrapper.ParcelW;

import java.util.List;

import static com.tezov.lib_java_android.database.adapter.contentValues.ContentValuesCryptBlockTo.KEY_PAYLOAD;

public class ParcelToCryptBlock extends ParcelToCryptRow{
protected ByteBufferOutput byteBuffer;

public ParcelToCryptBlock(){
    byteBuffer = ByteBufferOutput.obtain();
}

private void valueFromParcelToBuffer(ParcelW parcel, ContentValuesW contentValues, Field field){
    Class type = field.getType();
    String key = field.getName();
    if(type == dbTableDefinition.PRIMARY_KEY.class){
        byte[] data = parcel.readBytes();
        contentValues.get().put(key, data); //keep uid visible
    } else {
        byteBuffer.put(key);
        if(CompareType.STRING.equal(type)){
            String data = parcel.readString();
            byteBuffer.put(data);
        } else if(CompareType.LONG.equal(type)){
            Long data = parcel.readLong();
            byteBuffer.put(data);
        } else if(CompareType.INT.equal(type)){
            Integer data = parcel.readInteger();
            byteBuffer.put(data);
        } else if(CompareType.BOOLEAN.equal(type)){
            Boolean data = parcel.readBoolean();
            byteBuffer.put(data);
        } else if(CompareType.FLOAT.equal(type)){
            Float data = parcel.readFloat();
            byteBuffer.put(data);
        } else if(CompareType.UID.equal(type)){
            byte[] data = parcel.readBytes();
            byteBuffer.put(data);
        } else if(CompareType.BYTES.equal(type)){
            byte[] data = parcel.readBytes();
            byteBuffer.put(data);
        } else {
DebugException.start().log("unknown type:" + DebugTrack.getFullSimpleName(type) + " key:" + key).end();
        }

    }
}

@Override
public ContentValuesW contentValues(ParcelW parcel, List<Field> fields){
    if(!canEncoderValue()){
        return super.contentValues(parcel, fields);
    }
    ContentValuesW contentValues = ContentValuesW.obtain();
    parcel.resetPosition();
    for(Field field: fields){
        valueFromParcelToBuffer(parcel, contentValues, field);
    }
    String payloadEncrypted = encoderValue(byteBuffer.toBytes(), byte[].class);
    byteBuffer.reset();
    contentValues.get().put(KEY_PAYLOAD, payloadEncrypted);
    parcel.recycle();
    return contentValues;
}

}
