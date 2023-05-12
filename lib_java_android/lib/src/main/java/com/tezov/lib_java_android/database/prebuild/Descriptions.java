/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.database.prebuild;

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
import com.tezov.lib_java_android.database.prebuild.file.table.DescriptionFile;
import com.tezov.lib_java_android.database.prebuild.listEntry.table.DescriptionListEntry;
import com.tezov.lib_java_android.database.prebuild.sync.table.DescriptionSync;
import com.tezov.lib_java_android.database.prebuild.trash.table.DescriptionTrash;
import com.tezov.lib_java_android.database.sqlLite.dbField;
import com.tezov.lib_java_android.database.sqlLite.dbTable;
import com.tezov.lib_java.type.defEnum.EnumBase;

public interface Descriptions{
Is TRASH = new Is(DescriptionTrash.INSTANCE);
Is FILE = new Is(DescriptionFile.INSTANCE);
Is PREFERENCE = new Is(DescriptionListEntry.INSTANCE);
Is SYNC = new Is(DescriptionSync.INSTANCE);

static Is findInstanceOf(String name){
    return Is.findInstanceOf(Is.class, name);
}

class Is extends EnumBase.Is implements TableDescription{
    TableDescription description;
    public Is(TableDescription description){
        super(description.name());
        this.description = description;
    }
    @Override
    public String name(){
        return description.name();
    }
    @Override
    public String name(String prefix){
        return description.name(prefix);
    }
    @Override
    public Class<? extends dbField> getFieldType(){
        return description.getFieldType();
    }
    @Override
    public Class<? extends dbTable> getLocalTableType(){
        return description.getLocalTableType();
    }
    @Override
    public Class<? extends fbTable> getRemoteTableType(){
        return description.getRemoteTableType();
    }

}

}
