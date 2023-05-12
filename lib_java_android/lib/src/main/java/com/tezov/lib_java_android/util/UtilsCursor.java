/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.util;

import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.util.UtilsString;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import androidx.fragment.app.Fragment;

import com.tezov.lib_java.type.collection.ListOrObject;

import static android.database.Cursor.FIELD_TYPE_BLOB;
import static android.database.Cursor.FIELD_TYPE_FLOAT;
import static android.database.Cursor.FIELD_TYPE_INTEGER;
import static android.database.Cursor.FIELD_TYPE_NULL;
import static android.database.Cursor.FIELD_TYPE_STRING;

import android.database.Cursor;

import com.tezov.lib_java.toolbox.Nullify;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.type.collection.ListEntry;

public class UtilsCursor{
private UtilsCursor(){
}
public static ListOrObject<ListEntry<String, Object>> toListEntryNoClose(Cursor cursor){
    ListOrObject<ListEntry<String, Object>> datas = new ListOrObject<>();
    ListEntry<String, Object> data = new ListEntry<>();
    cursor.moveToFirst();
    while(!cursor.isAfterLast()){
        for(int i = 0; i < cursor.getColumnCount(); i++){
            Object o = null;
            switch(cursor.getType(i)){
                case FIELD_TYPE_NULL:{

                }
                break;
                case FIELD_TYPE_INTEGER:{
                    o = cursor.getString(i);
                }
                break;
                case FIELD_TYPE_FLOAT:{
                    o = cursor.getFloat(i);
                }
                break;
                case FIELD_TYPE_STRING:{
                    o = cursor.getString(i);
                }
                break;
                case FIELD_TYPE_BLOB:{
                    o = cursor.getBlob(i);
                }
                break;
                default:{
DebugException.start().unknown("type", cursor.getType(i)).end();
                }
            }
            data.put(cursor.getColumnName(i), o);
        }
        datas.add(data);
        cursor.moveToNext();
    }
    return Nullify.collection(datas);
}
public static void toDebugLogNoClose(Cursor cursor){
DebugLog.start().send(toListEntryNoClose(cursor)).end();
}


}
