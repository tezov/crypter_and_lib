/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.database.sqlLite.holder;

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

import android.content.ContextWrapper;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;

import com.tezov.lib_java_android.application.AppContext;
import com.tezov.lib_java.file.Directory;
import com.tezov.lib_java.file.File;

import static com.tezov.lib_java.file.StoragePackage.Type.PRIVATE_DATABASE;

public class dbContext extends ContextWrapper{
private final static String EXTENSION = "db";
private final static String EXTENSION_JOURNAL = "-journal";
public dbContext(){
    super(AppContext.get());
}

public Directory directory(){
    return new Directory(PRIVATE_DATABASE);
}

public int getVersion(){
    return -1;
}

public String fullName(String name){
    return name + File.DOT_SEPARATOR + EXTENSION;
}

@Override
public java.io.File getDatabasePath(String name){
    return new java.io.File(directory().getFile(), fullName(name));
}
@Override
public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory){
    return openOrCreateDatabase(name, mode, factory, null);
}
@Override
public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler){
    return SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name).getAbsolutePath(), null, errorHandler);
}

public void delete(String name){
    File file = new File(directory(), fullName(name));
    file.delete();
    File fileJournal = new File(directory(), fullName(name)+EXTENSION_JOURNAL);
    fileJournal.delete();
}

}
