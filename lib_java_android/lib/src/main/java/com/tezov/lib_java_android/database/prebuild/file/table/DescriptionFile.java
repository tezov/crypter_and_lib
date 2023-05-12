/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.database.prebuild.file.table;

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

import com.tezov.lib_java_android.database.TableDescription;
import com.tezov.lib_java_android.database.firebase.fbTable;
import com.tezov.lib_java_android.database.sqlLite.dbField;
import com.tezov.lib_java_android.database.sqlLite.dbFieldAnnotation;
import com.tezov.lib_java_android.database.sqlLite.dbTable;
import com.tezov.lib_java.file.Directory;

import static com.tezov.lib_java_android.database.sqlLite.dbTableDefinition.TypeDB.BLOB;
import static com.tezov.lib_java_android.database.sqlLite.dbTableDefinition.TypeDB.INT;
import static com.tezov.lib_java_android.database.sqlLite.dbTableDefinition.TypeDB.TEXT;

public interface DescriptionFile{
TableDescription INSTANCE = new TableDescription(){
    @Override
    public String name(){
        return "FILE";
    }

    @Override
    public Class<? extends dbField> getFieldType(){
        return Field.class;
    }

    @Override
    public Class<? extends dbTable> getLocalTableType(){
        return dbFileTable.class;
    }

    @Override
    public Class<? extends fbTable> getRemoteTableType(){
        return fbFileTable.class;
    }
};

interface Field extends dbField{
    @dbFieldAnnotation(order = 0, typeDB = BLOB, typeJava = Directory.class)
    Is DIRECTORY = new Is("DIRECTORY");
    @dbFieldAnnotation(order = 1, typeDB = TEXT, typeJava = String.class)
    Is NAME = new Is("NAME");
    @dbFieldAnnotation(order = 2, typeDB = TEXT, typeJava = String.class)
    Is EXTENSION = new Is("EXTENSION");
    @dbFieldAnnotation(order = 3, typeDB = INT, typeJava = Integer.class)
    Is SIZE = new Is("SIZE");

    class Is extends dbField.Is{
        public Is(String value){
            super(value);
        }

    }

}

}
