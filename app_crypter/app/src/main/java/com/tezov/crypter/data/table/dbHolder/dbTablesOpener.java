/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.crypter.data.table.dbHolder;

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.debug.DebugException;
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
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.tezov.lib_java_android.database.sqlLite.dbTable;
import com.tezov.lib_java_android.database.sqlLite.holder.dbContext;
import com.tezov.lib_java.type.collection.ListKey;

public class dbTablesOpener extends com.tezov.lib_java_android.database.sqlLite.holder.dbTablesOpener{

public dbTablesOpener(dbContext context, String name){
    super(context, name);
}

@Override
public void onOpen(SQLiteDatabase db){
    tablesCreateIfNotExist(db, getTables());
}
@Override
public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
    tablesDropAll(db);
    tablesCreateIfNotExist(db, getTables());
}
@Override
public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion){
    tablesDropAll(db);
    tablesCreateIfNotExist(db, getTables());
}

public void tablesCreateIfNotExist(SQLiteDatabase db, ListKey<String, dbTable> tables){
    for(dbTable table: tables){
        if(!isExist(db, table)){
            db.execSQL(table.getTableDefinition().CREATE());
        }
    }
}
public void tablesDropIfExist(SQLiteDatabase db, ListKey<String, dbTable> tables){
    for(dbTable table: tables){
        if(isExist(db, table)){
            db.execSQL(table.getTableDefinition().DROP());
        }
    }
}

public void tablesDropAll(SQLiteDatabase db){
    Cursor cursor = db.rawQuery("SELECT DISTINCT tbl_name FROM sqlite_master " + "WHERE " + "type IS 'table' AND tbl_name NOT IN ('android_metadata', " + "'sqlite_master', 'sqlite_sequence')", null);
    if(cursor.getCount() > 0){
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            db.execSQL("DROP TABLE " + cursor.getString(0));
            cursor.moveToNext();
        }
    }
    cursor.close();
}

}
