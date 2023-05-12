/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.database.sqlLite.adapter;

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

import android.database.Cursor;

import com.tezov.lib_java.cipher.definition.defDecoder;
import com.tezov.lib_java_android.definition.defCreatable;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.primaire.Field;
import com.tezov.lib_java_android.type.android.wrapper.ParcelW;

import java.util.List;

import static android.database.Cursor.FIELD_TYPE_BLOB;
import static android.database.Cursor.FIELD_TYPE_FLOAT;
import static android.database.Cursor.FIELD_TYPE_INTEGER;
import static android.database.Cursor.FIELD_TYPE_NULL;
import static android.database.Cursor.FIELD_TYPE_STRING;

public class CursorTo implements defCursorTo{
protected int indexOffset = 0;

public CursorTo(){
DebugTrack.start().create(this).end();
}

private static String cursorTypeToString(int preferredType){
    switch(preferredType){
        case FIELD_TYPE_NULL:{
            return "null";
        }
        case FIELD_TYPE_INTEGER:{
            return "integer";
        }
        case FIELD_TYPE_FLOAT:{
            return "float";
        }
        case FIELD_TYPE_STRING:{
            return "string";
        }
        case FIELD_TYPE_BLOB:{
            return "blob";
        }
        default:{

DebugException.start().log("preferred type is not unknown").end();

            return "unknown";
        }
    }
}

public static void toDebugLogColumnName(Cursor cursor){
    if(cursor == null){
DebugLog.start().send("cursor is null").end();
    } else if(cursor.getCount() <= 0){
DebugLog.start().send("cursor is null").end();
        cursor.close();
    } else {
        cursor.moveToFirst();
        for(int i = 0; i < cursor.getCount(); i++){
DebugLog.start().send(cursor.getColumnName(i) + ":" + cursorTypeToString(cursor.getType(i))).end();
        }
        cursor.close();
    }
}

public static void toDebugLog(Cursor cursor){
    if(cursor == null){
DebugLog.start().send("cursor is null").end();
    } else if(cursor.getCount() <= 0){
DebugLog.start().send("cursor is null").end();
    } else if(cursor.getCount() > 0){
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            StringBuilder data = new StringBuilder().append("[");
            for(int i = 0; i < cursor.getColumnCount(); i++){
                data.append(cursor.getColumnName(i)).append(":").append(cursor.getString(i)).append("|");
            }
            if(data.length() > 0){
                data.replace(data.length() - 1, data.length(), "]");
            } else {
                data.append("]");
            }
DebugLog.start().send(data.toString()).end();
            cursor.moveToNext();
        }
    }
}

public CursorTo setIndexOffset(int indexOffset){
    this.indexOffset = indexOffset;
    return this;
}

@Override
public void setDecoderValue(defDecoder decryptValue){

DebugException.start().notImplemented().end();

}

@Override
public ParcelW parcel(Cursor cursor, List<Field> fields){
    if(cursor == null){
        return null;
    }
    ParcelW parcel = ParcelW.obtain();
    for(Field field: fields){
        parcel.write(valueFrom(cursor, field, indexOffset));
    }
    return parcel;
}

@Override
public <ITEM> ITEM item(Cursor cursor, List<Field> fields, defCreatable<ITEM> factory){
    ParcelW parcel = parcel(cursor, fields);
    if(parcel == null){
        return null;
    }
    return factory.create(parcel);
}

@Override
final public <T> T valueFrom(Cursor cursor, Field field, int fieldOffset){
    int index = field.getIndex() + fieldOffset;
    Class type = field.getType();
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
        case FIELD_TYPE_FLOAT:{
            o = cursor.getFloat(index);
        }
        break;
        case FIELD_TYPE_STRING:{
            o = cursor.getString(index);
            if(CompareType.BOOLEAN.equal(type)){
                if(o != null){
                    o = o.equals("1");
                }
            }
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
    return (T)o;
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

}

