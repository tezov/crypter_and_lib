/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.database.prebuild.trash.table;

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
import com.tezov.lib_java.generator.uid.defUid;

import org.threeten.bp.ZoneId;

import static com.tezov.lib_java_android.database.prebuild.trash.item.ItemTrash.Type;
import static com.tezov.lib_java_android.database.sqlLite.dbTableDefinition.TypeDB.BLOB;
import static com.tezov.lib_java_android.database.sqlLite.dbTableDefinition.TypeDB.FOREIGN_UID;
import static com.tezov.lib_java_android.database.sqlLite.dbTableDefinition.TypeDB.INT;
import static com.tezov.lib_java_android.database.sqlLite.dbTableDefinition.TypeDB.OBJECT_TO_STRING;

public interface DescriptionTrash{
TableDescription INSTANCE = new TableDescription(){
    @Override
    public String name(){
        return "TRASH";
    }

    @Override
    public Class<? extends dbField> getFieldType(){
        return Field.class;
    }

    @Override
    public Class<? extends dbTable> getLocalTableType(){
        return dbTrashTable.class;
    }

    @Override
    public Class<? extends fbTable> getRemoteTableType(){
        return fbTrashTable.class;
    }
};

interface Field extends dbField{
    @dbFieldAnnotation(order = 0, typeDB = INT, typeJava = Long.class)
    Is TIMESTAMP = new Is("TIMESTAMP");
    @dbFieldAnnotation(order = 1, typeDB = OBJECT_TO_STRING, typeJava = ZoneId.class)
    Is TIMESTAMP_ZONE_ID = new Is("TIMESTAMP_ZONE_ID");
    @dbFieldAnnotation(order = 2, typeDB = BLOB, typeJava = defUid.class)
    Is REQUESTER = new Is("REQUESTER");
    @dbFieldAnnotation(order = 3, typeDB = OBJECT_TO_STRING, typeJava = Type.Is.class)
    Is TYPE = new Is("TYPE");
    @dbFieldAnnotation(order = 4, typeDB = OBJECT_TO_STRING, typeJava = TableDescription.class)
    Is ITEM_TABLE = new Is("ITEM_TABLE");
    @dbFieldAnnotation(order = 5, typeDB = FOREIGN_UID, typeJava = defUid.class, extra = "ItemBase,Uid")
    Is ITEM_UID = new Is("ITEM_UID");

    class Is extends dbField.Is{
        public Is(String value){
            super(value);
        }

    }

}

}
