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

import com.tezov.lib_java.cipher.definition.defDecoder;
import com.tezov.lib_java_android.database.ItemBase;
import com.tezov.lib_java_android.database.adapter.definition.defContentValuesTo;
import com.tezov.lib_java_android.definition.defCreatable;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.primaire.Field;
import com.tezov.lib_java_android.type.android.wrapper.ContentValuesW;
import com.tezov.lib_java_android.type.android.wrapper.ParcelW;

import java.util.List;

public class ContentValuesTo implements defContentValuesTo{

public ContentValuesTo(){
DebugTrack.start().create(this).end();
}

@Override
public void setDecoderValue(defDecoder decrypt){

DebugException.start().notImplemented().end();

}


private Object valueFrom(ContentValuesW contentValues, Field field){
    if(CompareType.BOOLEAN.equal(field.getType())){
        String s = contentValues.getValue(field.getName(), String.class);
        if(s == null){
            return null;
        } else {
            return s.equals("1");
        }
    } else {
        return contentValues.get().get(field.getName());
    }
}

@Override
public ParcelW parcel(ContentValuesW contentValues, List<Field> fields){
    if(contentValues == null){
        return null;
    }
    ParcelW parcel = ParcelW.obtain();
    for(Field field: fields){
        parcel.write(valueFrom(contentValues, field));
    }
    return parcel;
}

@Override
public <ITEM extends ItemBase> ITEM item(ContentValuesW contentValues, List<Field> fields, defCreatable<ITEM> factory){
    ParcelW parcel = parcel(contentValues, fields);
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
