/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.database.adapter.contentValues;

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
import com.tezov.lib_java.cipher.definition.defDecoder;
import com.tezov.lib_java_android.database.sqlLite.dbTableDefinition;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.type.primaire.Field;
import com.tezov.lib_java_android.type.android.wrapper.ContentValuesW;
import com.tezov.lib_java_android.type.android.wrapper.ParcelW;

import java.util.List;

public class ContentValuesCryptRowTo extends ContentValuesTo{
protected defDecoder decoderValue = null;

@Override
public void setDecoderValue(defDecoder decrypt){
    this.decoderValue = decrypt;
}

protected <T> T decoderValue(String s, Class<T> type){
    return (T)decoderValue.decode(s, type);
}

protected boolean canDecodeValue(){
    return decoderValue != null;
}

private Object valueCryptFrom(ContentValuesW contentValues, Field field){
    Class type = field.getType();
    String key = field.getName();
    Object o = contentValues.get().get(key);
    if(type == dbTableDefinition.PRIMARY_KEY.class){
        //do not decode
    } else if(CompareType.STRING.equal(type)){
        o = decoderValue((String)o, String.class);
    } else if(CompareType.LONG.equal(type)){
        o = decoderValue((String)o, Long.class);
    } else if(CompareType.INT.equal(type)){
        o = decoderValue((String)o, Integer.class);
    } else if(CompareType.BOOLEAN.equal(type)){
        o = decoderValue((String)o, Boolean.class);
    } else if(CompareType.FLOAT.equal(type)){
        o = decoderValue((String)o, Float.class);
    } else if(CompareType.UID.equal(type)){
        o = decoderValue((String)o, byte[].class);
    } else if(CompareType.BYTES.equal(type)){
        o = decoderValue((String)o, byte[].class);
    } else {
DebugException.start().log("unknown type:" + DebugTrack.getFullSimpleName(type) + " key:" + key).end();
    }

    return o;
}

@Override
public ParcelW parcel(ContentValuesW contentValues, List<Field> fields){
    if(!canDecodeValue()){
        return super.parcel(contentValues, fields);
    }
    if(contentValues == null){
        return null;
    }
    ParcelW parcel = ParcelW.obtain();
    for(Field field: fields){
        parcel.write(valueCryptFrom(contentValues, field));
    }
    return parcel;
}

}
