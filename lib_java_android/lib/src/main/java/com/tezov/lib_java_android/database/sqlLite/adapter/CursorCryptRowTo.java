/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.database.sqlLite.adapter;

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

import android.database.Cursor;

import com.tezov.lib_java.cipher.definition.defDecoder;
import com.tezov.lib_java_android.database.sqlLite.dbTableDefinition;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.type.primaire.Field;
import com.tezov.lib_java_android.type.android.wrapper.ParcelW;

import java.util.List;

import static android.database.Cursor.FIELD_TYPE_BLOB;
import static android.database.Cursor.FIELD_TYPE_FLOAT;
import static android.database.Cursor.FIELD_TYPE_INTEGER;
import static android.database.Cursor.FIELD_TYPE_NULL;
import static android.database.Cursor.FIELD_TYPE_STRING;

public class CursorCryptRowTo extends CursorTo{
protected defDecoder decoderValue = null;

@Override
public void setDecoderValue(defDecoder decryptValue){
    this.decoderValue = decryptValue;
}

protected boolean canDecoderValue(){
    return decoderValue != null;
}

protected <T> T decoderValue(String s, Class<T> type){
    return (T)decoderValue.decode(s, type);
}

private <T> T valueCryptFrom(Cursor cursor, Field field, int fieldOffset){
    int index = field.getIndex() + fieldOffset;
    Class type = field.getType();
    String key = field.getName();
    Object o;
    int preferredType = cursor.getType(index);
    switch(preferredType){
        case FIELD_TYPE_NULL:{
            o = null;
        }
        break;
        case FIELD_TYPE_INTEGER:{
            if(CompareType.INT.equal(type)){
                o = cursor.getInt(index);
            } else if(CompareType.LONG.equal(type)){
                o = cursor.getLong(index);
            } else {
                o = null;
DebugException.start().log("preferred type:" + type.getSimpleName() + " is not consistent with cursor type:INT ").end();
            }
        }
        break;
        case FIELD_TYPE_STRING:{
            o = cursor.getString(index);
        }
        break;
        case FIELD_TYPE_FLOAT:{
            o = cursor.getFloat(index);
        }
        break;
        case FIELD_TYPE_BLOB:{
            o = cursor.getBlob(index);
        }
        break;
        default:{
            o = null;
DebugException.start().log("preferred type:" + type.getSimpleName() + " is not unknown").end();
        }
    }
    if(type == dbTableDefinition.PRIMARY_KEY.class){

    } else if(CompareType.STRING.equal(type)){
        o = decoderValue((String)o, String.class);
    } else if(CompareType.LONG.equal(type)){
        o = decoderValue((String)o, Long.class);
    } else if(CompareType.INT.equal(type)){
        o = decoderValue((String)o, Integer.class);
    } else if(CompareType.BOOLEAN.equal(type)){
        o = decoderValue((String)o, String.class);
        if(o != null){
            o = o.equals("1");
        }
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
public ParcelW parcel(Cursor cursor, List<Field> fields){
    if(cursor == null){
        return null;
    }
    if(!canDecoderValue()){
        return super.parcel(cursor, fields);
    }
    ParcelW parcel = ParcelW.obtain();
    for(Field field: fields){
        parcel.write(valueCryptFrom(cursor, field, indexOffset));
    }
    return parcel;
}

}

