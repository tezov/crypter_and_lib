/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.database.sqlLite.holder;

import com.tezov.lib_java.debug.DebugLog;
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
import android.database.sqlite.SQLiteOpenHelper;

import com.tezov.lib_java_android.database.sqlLite.dbTable;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.collection.ListKey;

public class dbTablesOpener extends SQLiteOpenHelper{
private ListKey<String, dbTable> tables;

public dbTablesOpener(dbContext context, String name){
    super(context, name, null, context.getVersion());
DebugTrack.start().create(this).end();
}

public ListKey<String, dbTable> getTables(){
    return tables;
}
public dbTablesOpener setTables(ListKey<String, dbTable> tables){
    this.tables = tables;
    return this;
}

public boolean isExist(SQLiteDatabase db, dbTable table){
    Cursor cursor = db.rawQuery(table.getTableDefinition().IS_EXIST(), null);
    boolean isExist = cursor.getCount() > 0;
    cursor.close();
    return isExist;
}

@Override
public void onCreate(SQLiteDatabase db){
}

@Override
public void onOpen(SQLiteDatabase db){

}

@Override
public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){

}

@Override
public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion){

}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}


}
