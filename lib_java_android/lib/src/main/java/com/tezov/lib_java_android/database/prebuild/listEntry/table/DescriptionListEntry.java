/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.database.prebuild.listEntry.table;

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

import com.tezov.lib_java_android.database.firebase.fbTable;
import com.tezov.lib_java_android.database.sqlLite.dbField;
import com.tezov.lib_java_android.database.sqlLite.dbFieldAnnotation;
import com.tezov.lib_java_android.database.sqlLite.dbTable;

import static com.tezov.lib_java_android.database.sqlLite.dbTableDefinition.TypeDB.TEXT;

public interface DescriptionListEntry{
TableDescription INSTANCE = new TableDescription(){
    @Override
    public String name(){
        return "PREFERENCE";
    }
};

interface Field extends dbField{
    @dbFieldAnnotation(order = 1, typeDB = TEXT, typeJava = String.class)
    Is VALUE = new Is("VALUE");

    class Is extends dbField.Is{
        public Is(String value){
            super(value);
        }

    }

}

abstract class TableDescription implements com.tezov.lib_java_android.database.TableDescription{
    @Override
    public Class<? extends dbField> getFieldType(){
        return Field.class;
    }
    @Override
    public Class<? extends dbTable> getLocalTableType(){
        return dbListEntryTable.class;
    }
    @Override
    public Class<? extends fbTable> getRemoteTableType(){
        return fbListEntryTable.class;
    }

}

}
