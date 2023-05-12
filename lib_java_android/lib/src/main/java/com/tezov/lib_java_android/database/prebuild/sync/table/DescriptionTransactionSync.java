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
import com.tezov.lib_java_android.database.prebuild.sync.item.ItemTransactionSync;
import com.tezov.lib_java_android.database.sqlLite.dbField;
import com.tezov.lib_java_android.database.sqlLite.dbFieldAnnotation;
import com.tezov.lib_java_android.database.sqlLite.dbTable;
import com.tezov.lib_java.generator.uid.defUid;

import static com.tezov.lib_java_android.database.sqlLite.dbTableDefinition.TypeDB.BLOB;
import static com.tezov.lib_java_android.database.sqlLite.dbTableDefinition.TypeDB.FOREIGN_UID;
import static com.tezov.lib_java_android.database.sqlLite.dbTableDefinition.TypeDB.INT;
import static com.tezov.lib_java_android.database.sqlLite.dbTableDefinition.TypeDB.OBJECT_TO_STRING;
import static com.tezov.lib_java_android.database.sqlLite.dbTableDefinition.TypeDB.TEXT;

public interface DescriptionTransactionSync{
TableDescription INSTANCE = new TableDescription(){
    @Override
    public String name(){
        return "TRANSACTION_SYNC";
    }

    @Override
    public Class<? extends dbField> getFieldType(){
        return DescriptionTransactionSync.Field.class;
    }

    @Override
    public Class<? extends dbTable> getLocalTableType(){
        return dbTransactionSync.class;
    }

    @Override
    public Class<? extends fbTable> getRemoteTableType(){
        return null;
    }
};

interface Field extends dbField{
    @dbFieldAnnotation(order = 0, typeDB = INT, typeJava = Long.class)
    Is TIMESTAMP = new Is("TIMESTAMP");
    @dbFieldAnnotation(order = 1, typeDB = TEXT, typeJava = String.class)
    Is TRANSACTION_NAME = new Is("TRANSACTION_NAME");
    @dbFieldAnnotation(order = 2, typeDB = OBJECT_TO_STRING, typeJava = ItemTransactionSync.Direction.class)
    Is DIRECTION = new Is("DIRECTION");
    @dbFieldAnnotation(order = 3, typeDB = OBJECT_TO_STRING, typeJava = ItemSync.Type.class)
    Is TYPE_SYNC = new Is("TYPE_SYNC");
    @dbFieldAnnotation(order = 4, typeDB = FOREIGN_UID, typeJava = defUid.class)
    Is SYNC_UID = new Is("SYNC_UID");
    @dbFieldAnnotation(order = 5, typeDB = BLOB, typeJava = ItemSync.class)
    Is SYNC = new Is("SYNC");
    @dbFieldAnnotation(order = 6, typeDB = TEXT, typeJava = String.class)
    Is FAILED_MESSAGE = new Is("FAILED_MESSAGE");

    class Is extends dbField.Is{
        public Is(String value){
            super(value);
        }

    }

}

}
