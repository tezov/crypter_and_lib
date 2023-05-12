/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.database.sqlLite.holder;

import com.tezov.lib_java.debug.DebugLog;
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
import androidx.fragment.app.Fragment;

import com.tezov.lib_java.debug.DebugException;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.tezov.lib_java_android.database.ItemBase;
import com.tezov.lib_java_android.database.TableDescription;
import com.tezov.lib_java_android.database.sqlLite.dbTable;
import com.tezov.lib_java_android.database.sqlLite.dbView;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import com.tezov.lib_java.file.Directory;
import com.tezov.lib_java.file.File;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.collection.ListKey;

public class dbTablesHandle{
private SQLiteDatabase db = null;
private ListKey<String, dbTable> tables = null;

public dbTablesHandle(){
DebugTrack.start().create(this).end();
}
public static Integer getVersion(dbContext context, String name){
    Directory dbDirectory = context.directory();
    if(dbDirectory.exists()){
        File file = new File(dbDirectory, name);
        if(file.exists()){
            SQLiteDatabase db = SQLiteDatabase.openDatabase(file.getPath(), null, SQLiteDatabase.OPEN_READONLY);
            int version = db.getVersion();
            db.close();
            return version;
        }
    }
    return null;
}
public dbTablesHandle open(dbTablesOpener opener){
    db = opener.getWritableDatabase();
    this.tables = opener.getTables();
    for(dbTable table: this.tables){
        table.setDatabase(this);
    }
    return this;
}
public <T extends dbTable> T getTable(String name){
    return (T)tables.getValue(name);
}

public <R extends dbTable.Ref> R getMainRef(String name){
    return (R)getTable(name).mainRef();
}

public <R extends dbTable.Ref> R newRef(String name){
    return (R)getTable(name).newRef();
}

public <ITEM extends ItemBase<ITEM>> dbView<ITEM> newView(String name){
    return new dbView(newRef(name));
}

public <T extends dbTable> T getTable(TableDescription t){
    return (T)tables.getValue(t.name());
}

public <R extends dbTable.Ref> R getMainRef(TableDescription t){
    dbTable table = getTable(t);
    if(table == null){
        return null;
    } else {
        return (R)table.mainRef();
    }
}

public <R extends dbTable.Ref> R newRef(TableDescription t){
    dbTable table = getTable(t);
    if(table == null){
        return null;
    } else {
        return (R)table.newRef();
    }
}

public <ITEM extends ItemBase<ITEM>> dbView<ITEM> newView(TableDescription t){
    return newView(t.name());
}

public <T extends dbTable> T getTable(TableDescription t, TableDescription postFix){
    return (T)tables.getValue(postFix.name(t.name()));
}

public <R extends dbTable.Ref> R getMainRef(TableDescription t, TableDescription postFix){
    dbTable table = getTable(t, postFix);
    if(table == null){
        return null;
    } else {
        return (R)table.mainRef();
    }
}

public <R extends dbTable.Ref> R newRef(TableDescription t, TableDescription postFix){
    dbTable table = getTable(t, postFix);
    if(table == null){
        return null;
    } else {
        return (R)table.newRef();
    }
}

public <ITEM extends ItemBase<ITEM>> dbView<ITEM> newView(TableDescription t, TableDescription postFix){
    return newView(t.name(postFix.name()));
}

public String getName(){
    String path = db.getPath();
    int index = path.lastIndexOf(Directory.PATH_SEPARATOR);
    return path.substring(index + 1);
}

public void beginTransaction(){
    db.beginTransaction();
}
public void endTransaction(){
    db.setTransactionSuccessful();
    db.endTransaction();
}
public void execSQL(ChunkCommand statement) throws SQLException{
    Object[] bindArgs = statement.getValuesArray();
    if(bindArgs != null){
        db.execSQL(statement.getSql(), bindArgs);
    } else {
        db.execSQL(statement.getSql());
    }
}
public Cursor rawQuery(ChunkCommand statement){
    return db.rawQuery(statement.getSql(), statement.getValuesArray());
}
public long insert(String table, ContentValues values){
    long id = db.insert(table, null, values);
    if(id != -1){
        return id;
    } else {
DebugException.start().log(new Throwable(getName())).end();
        return -1;
    }
}
public int update(int expectedRowNumberAffected, String table, ContentValues values, String whereClause){
    int rowNumberAffected = db.update(table, values, whereClause, null);
    if(rowNumberAffected != expectedRowNumberAffected){
DebugException.start().log(new Throwable(getName())).end();
    }
    return rowNumberAffected;
}

public void execSQL(String sql, Object[] bindArgs) throws SQLException{
    if(bindArgs != null){
        db.execSQL(sql, bindArgs);
    } else {
        db.execSQL(sql);
    }
}
public Cursor rawQuery(String sql, String[] bindArgs) throws SQLException{
    return db.rawQuery(sql, bindArgs);
}

public void close(){
    db.close();
    db = null;
}

@Override
protected void finalize() throws Throwable{
    if(db != null){
        db.close();
    }
DebugTrack.start().destroy(this).end();
    super.finalize();
}

}
