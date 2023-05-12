/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.crypter.data.table.description;

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
import static com.tezov.lib_java_android.database.sqlLite.dbTableDefinition.TypeDB.BLOB;
import static com.tezov.lib_java_android.database.sqlLite.dbTableDefinition.TypeDB.INT;
import static com.tezov.lib_java_android.database.sqlLite.dbTableDefinition.TypeDB.TEXT;

import com.tezov.crypter.data.table.db.dbHistoryTable;
import com.tezov.lib_java_android.database.TableDescription;
import com.tezov.lib_java_android.database.firebase.fbTable;
import com.tezov.lib_java_android.database.sqlLite.dbField;
import com.tezov.lib_java_android.database.sqlLite.dbFieldAnnotation;
import com.tezov.lib_java_android.database.sqlLite.dbTable;

public interface DescriptionHistory{
TableDescription INSTANCE = new TableDescription(){
    @Override
    public String name(){
        return "HISTORY";
    }
    @Override
    public Class<? extends dbField> getFieldType(){
        return Field.class;
    }
    @Override
    public Class<? extends dbTable> getLocalTableType(){
        return dbHistoryTable.class;
    }
    @Override
    public Class<? extends fbTable> getRemoteTableType(){
        return null;
    }
    @Override
    public String toString(){
        return name();
    }
};

interface Field extends dbField{
    @dbFieldAnnotation(order = 0, typeDB = TEXT, typeJava = String.class)
    Is TYPE = new Is("TYPE");
    @dbFieldAnnotation(order = 1, typeDB = INT, typeJava = Long.class)
    Is TIMESTAMP = new Is("TIMESTAMP");
    @dbFieldAnnotation(order = 2, typeDB = BLOB, typeJava = byte[].class)
    Is DATA = new Is("DATA");

    class Is extends dbField.Is{
        public Is(String name){
            super(name);
        }

    }

}

}
