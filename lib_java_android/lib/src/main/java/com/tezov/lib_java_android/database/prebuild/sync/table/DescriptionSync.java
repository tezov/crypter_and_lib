/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.database.prebuild.sync.table;

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
import com.tezov.lib_java_android.database.prebuild.sync.item.ItemSync;
import com.tezov.lib_java_android.database.sqlLite.dbField;
import com.tezov.lib_java_android.database.sqlLite.dbFieldAnnotation;
import com.tezov.lib_java_android.database.sqlLite.dbTable;
import com.tezov.lib_java.generator.uid.UUID;

import static com.tezov.lib_java_android.database.sqlLite.dbTableDefinition.TypeDB.INT;
import static com.tezov.lib_java_android.database.sqlLite.dbTableDefinition.TypeDB.OBJECT_TO_STRING;
import static com.tezov.lib_java_android.database.sqlLite.dbTableDefinition.TypeDB.TEXT;

public interface DescriptionSync{
TableDescription INSTANCE = new TableDescription(){
    @Override
    public String name(){
        return "SYNC";
    }
    @Override
    public Class<? extends dbField> getFieldType(){
        return DescriptionSync.Field.class;
    }
    @Override
    public Class<? extends dbTable> getLocalTableType(){
        return dbSyncTable.class;
    }
    @Override
    public Class<? extends fbTable> getRemoteTableType(){
        return fbSyncTable.class;
    }
};

interface Field extends dbField{
    @dbFieldAnnotation(order = 0, typeDB = INT, typeJava = Long.class)
    Is TIMESTAMP = new Is("TIMESTAMP");
    @dbFieldAnnotation(order = 1, typeDB = OBJECT_TO_STRING, typeJava = ItemSync.Type.class)
    Is UPDATE_TYPE = new Is("UPDATE_TYPE");
    @dbFieldAnnotation(order = 2, typeDB = TEXT, typeJava = UUID.class)
    Is GUID = new Is("GUID");

    class Is extends dbField.Is{
        public Is(String value){
            super(value);
        }

    }

}

}
