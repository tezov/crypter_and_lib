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

import com.tezov.crypter.data.table.db.dbKeyRingTable;
import com.tezov.lib_java.cipher.key.KeyMac;
import com.tezov.lib_java_android.database.TableDescription;
import com.tezov.lib_java_android.database.firebase.fbTable;
import com.tezov.lib_java_android.database.sqlLite.dbField;
import com.tezov.lib_java_android.database.sqlLite.dbFieldAnnotation;
import com.tezov.lib_java_android.database.sqlLite.dbTable;
import com.tezov.lib_java.generator.uid.UUID;

public interface DescriptionKeyRing{
TableDescription INSTANCE = new TableDescription(){
    @Override
    public String name(){
        return "KEY_RING";
    }
    @Override
    public Class<? extends dbField> getFieldType(){
        return Field.class;
    }
    @Override
    public Class<? extends dbTable> getLocalTableType(){
        return dbKeyRingTable.class;
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
    @dbFieldAnnotation(order = 0, typeDB = BLOB, typeJava = byte[].class)
    Is KEY_PASSWORD = new Is("KEY_PASSWORD");
    @dbFieldAnnotation(order = 1, typeDB = BLOB, typeJava = KeyMac.class)
    Is KEY_KEY = new Is("KEY_KEY");
    @dbFieldAnnotation(order = 2, typeDB = BLOB, typeJava = UUID.class)
    Is OWNER_UID = new Is("OWNER_UID");

    class Is extends dbField.Is{
        public Is(String name){
            super(name);
        }

    }

}

}
