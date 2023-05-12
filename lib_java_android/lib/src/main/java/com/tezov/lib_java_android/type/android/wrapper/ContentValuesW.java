/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.type.android.wrapper;

import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.util.UtilsString;
import java.util.List;
import java.util.LinkedList;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import androidx.fragment.app.Fragment;

import android.content.ContentValues;

import com.tezov.lib_java.definition.defClearable;
import com.tezov.lib_java_android.definition.defCreatable;
import com.tezov.lib_java_android.factory.FactoryContentValues;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java.debug.DebugTrack;

import java.util.Arrays;
import java.util.Set;

//Avoid not portable...
public class ContentValuesW implements defClearable<ContentValuesW>{
private final ContentValues contentValues;

public ContentValuesW(){
DebugTrack.start().create(this).end();
    contentValues = new ContentValues();
}

public static DebugString toDebugString(ContentValues contentValues, String[] keysFilter){
    DebugString data = new DebugString();
    if((contentValues == null) || (contentValues.size() == 0)){
        data.append("ContentValues is null");
        return data;
    }
    Set<String> keys = contentValues.keySet();
    for(String key: keys){
        Object value = contentValues.get(key);
        if((keysFilter == null) || (Arrays.asList(keysFilter).contains(key))){
            data.append(key, value);
        }
    }
    return data;
}

public static DebugString toDebugString(ContentValues contentValues){
    return toDebugString(contentValues, null);
}

public static void toDebugLog(ContentValues contentValues){
DebugLog.start().send(toDebugString(contentValues, null)).end();
}

public static defCreatable<ContentValuesW> getFactory(){
    return FactoryContentValues.factory();
}

public static void factoryRelease(){
    FactoryContentValues.factoryRelease();
}

public static ContentValuesW obtain(){
    return FactoryContentValues.factory().create();
}

public ContentValues get(){
    return contentValues;
}

public Object getValue(String key){
    return contentValues.get(key);
}

public <T> T getValue(String key, Class<T> type){
    return (T)contentValues.get(key);
}

@Override
public ContentValuesW clear(){
    contentValues.clear();
    return this;
}

public DebugString toDebugString(){
    return toDebugString(contentValues, null);
}

final public void toDebugLog(){
DebugLog.start().send(toDebugString(contentValues, null)).end();
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

}
