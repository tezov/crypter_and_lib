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

import com.tezov.lib_java.cipher.definition.defEncoder;
import com.tezov.lib_java_android.database.sqlLite.dbTableDefinition;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.primaire.Field;
import com.tezov.lib_java_android.type.android.wrapper.ContentValuesW;
import com.tezov.lib_java_android.type.android.wrapper.ParcelW;

import java.util.List;


public class ParcelToCryptRow extends ParcelTo{
protected defEncoder encoderValue = null;

@Override
public void setEncoderValue(defEncoder encoder){
    this.encoderValue = encoder;
}

protected String encoderValue(Object o, Class type){
    return (String)encoderValue.encode(o, type);
}

protected boolean canEncoderValue(){
    return encoderValue != null;
}

private void valueFromParcelToContentValueCrypt(ParcelW parcel, ContentValuesW contentValues, Field field){
    Class type = field.getType();
    String key = field.getName();
    if(type == dbTableDefinition.PRIMARY_KEY.class){
        byte[] data = parcel.readBytes();
        contentValues.get().put(key, data); // do not encrypt Uid
    } else if(CompareType.STRING.equal(type)){
        String data = parcel.readString();
        contentValues.get().put(key, encoderValue(data, String.class));
    } else if(CompareType.LONG.equal(type)){
        Long data = parcel.readLong();
        contentValues.get().put(key, encoderValue(data, Long.class));
    } else if(CompareType.INT.equal(type)){
        Integer data = parcel.readInteger();
        contentValues.get().put(key, encoderValue(data, Integer.class));
    } else if(CompareType.BOOLEAN.equal(type)){
        Boolean data = parcel.readBoolean();
        contentValues.get().put(key, encoderValue(data, Boolean.class));
    } else if(CompareType.FLOAT.equal(type)){
        Float data = parcel.readFloat();
        contentValues.get().put(key, encoderValue(data, Float.class));
    } else if(CompareType.UID.equal(type)){
        byte[] data = parcel.readBytes();
        contentValues.get().put(key, encoderValue(data, byte[].class));
    } else if(CompareType.BYTES.equal(type)){
        byte[] data = parcel.readBytes();
        contentValues.get().put(key, encoderValue(data, byte[].class));
    } else {
DebugException.start().log("unknown type:" + DebugTrack.getFullSimpleName(type) + " key:" + key).end();
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
        valueFromParcelToContentValueCrypt(parcel, contentValues, field);
    }
    parcel.recycle();
    return contentValues;
}

}
