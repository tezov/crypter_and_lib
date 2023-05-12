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
import static com.tezov.lib_java_android.database.sqlLite.dbTableDefinition.TypeDB.BOOLEAN;
import static com.tezov.lib_java_android.database.sqlLite.dbTableDefinition.TypeDB.FOREIGN_UID;
import static com.tezov.lib_java_android.database.sqlLite.dbTableDefinition.TypeDB.INT;
import static com.tezov.lib_java_android.database.sqlLite.dbTableDefinition.TypeDB.OBJECT_TO_STRING;
import static com.tezov.lib_java_android.database.sqlLite.dbTableDefinition.TypeDB.TEXT;

import com.tezov.crypter.data.table.db.dbKeyTable;
import com.tezov.lib_java_android.database.TableDescription;
import com.tezov.lib_java_android.database.firebase.fbTable;
import com.tezov.lib_java_android.database.sqlLite.dbField;
import com.tezov.lib_java_android.database.sqlLite.dbFieldAnnotation;
import com.tezov.lib_java_android.database.sqlLite.dbTable;
import com.tezov.lib_java.generator.uid.UUID;

import org.threeten.bp.ZoneId;

public interface DescriptionKey{
TableDescription INSTANCE = new TableDescription(){
    @Override
    public String name(){
        return "KEY";
    }
    @Override
    public Class<? extends dbField> getFieldType(){
        return Field.class;
    }
    @Override
    public Class<? extends dbTable> getLocalTableType(){
        return dbKeyTable.class;
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
    Is ALIAS = new Is("ALIAS");
    @dbFieldAnnotation(order = 1, typeDB = BLOB, typeJava = UUID.class)
    Is GUID = new Is("GUID");
    @dbFieldAnnotation(order = 2, typeDB = BOOLEAN, typeJava = boolean.class)
    Is IS_OWNER = new Is("IS_OWNER");
    @dbFieldAnnotation(order = 3, typeDB = FOREIGN_UID, typeJava = UUID.class, extra = "ItemKeyRing,UID")
    Is KEY_RING_UID = new Is("KEY_RING_UID");
    @dbFieldAnnotation(order = 4, typeDB = INT, typeJava = Long.class)
    Is CREATED_DATE = new Is("CREATED_DATE");
    @dbFieldAnnotation(order = 5, typeDB = OBJECT_TO_STRING, typeJava = ZoneId.class)
    Is CREATED_DATE_ZONE_ID = new Is("CREATED_DATE_ZONE_ID");
    @dbFieldAnnotation(order = 6, typeDB = BOOLEAN, typeJava = boolean.class)
    Is HAS_PASSWORD = new Is("HAS_PASSWORD");
    @dbFieldAnnotation(order = 7, typeDB = BLOB, typeJava = byte[].class)
    Is OPTION = new Is("OPTION");

    class Is extends dbField.Is{
        public Is(String name){
            super(name);
        }

    }

}

}
