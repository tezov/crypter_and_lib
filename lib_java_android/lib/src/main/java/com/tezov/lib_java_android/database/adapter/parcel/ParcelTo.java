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
import com.tezov.lib_java_android.database.ItemBase;
import com.tezov.lib_java_android.database.adapter.definition.defParcelTo;
import com.tezov.lib_java_android.database.sqlLite.dbTableDefinition;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.primaire.Field;
import com.tezov.lib_java_android.type.android.wrapper.ContentValuesW;
import com.tezov.lib_java_android.type.android.wrapper.ParcelW;

import java.util.List;


public class ParcelTo implements defParcelTo{

public ParcelTo(){
DebugTrack.start().create(this).end();
}

@Override
public void setEncoderValue(defEncoder encrypt){

DebugException.start().notImplemented().end();

}

private void valueFromParcelToContentValue(ParcelW parcel, ContentValuesW contentValues, Field field){
    Class type = field.getType();
    String key = field.getName();
    if(type == dbTableDefinition.PRIMARY_KEY.class){
        contentValues.get().put(key, parcel.readBytes());
    } else if(CompareType.STRING.equal(type)){
        String data = parcel.readString();
        contentValues.get().put(key, data);
    } else if(CompareType.LONG.equal(type)){
        Long data = parcel.readLong();
        contentValues.get().put(key, data);
    } else if(CompareType.INT.equal(type)){
        Integer data = parcel.readInteger();
        contentValues.get().put(key, data);
    } else if(CompareType.BOOLEAN.equal(type)){
        Boolean data = parcel.readBoolean();
        if(data == null){
            contentValues.get().put(key, (String)null);
        } else {
            contentValues.get().put(key, data ? "1" : "0");
        }
    } else if(CompareType.FLOAT.equal(type)){
        Float data = parcel.readFloat();
        contentValues.get().put(key, data);
    } else if(CompareType.UID.equal(type)){
        contentValues.get().put(key, parcel.readBytes());
    } else if(CompareType.BYTES.equal(type)){
        contentValues.get().put(key, parcel.readBytes());
    } else {
DebugException.start().log("unknown type:" + DebugTrack.getFullSimpleName(type) + " key:" + key).end();
    }


}

@Override
public ContentValuesW contentValues(ParcelW parcel, List<Field> fields){
    ContentValuesW contentValues = ContentValuesW.obtain();
    parcel.resetPosition();
    for(Field field: fields){
        valueFromParcelToContentValue(parcel, contentValues, field);
    }
    parcel.recycle();
    return contentValues;
}

@Override
public ContentValuesW contentValues(ItemBase parcelable, List<Field> fields){
    if(parcelable == null){
        return null;
    }
    ParcelW parcel = ParcelW.obtain().replace(parcelable);
    return contentValues(parcel, fields);
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

}
