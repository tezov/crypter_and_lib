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
import com.tezov.lib_java.cipher.definition.defEncoder;
import com.tezov.lib_java_android.database.ItemBase;
import com.tezov.lib_java_android.database.TableDescription;
import com.tezov.lib_java_android.database.prebuild.sync.item.ItemSync;
import com.tezov.lib_java_android.database.sqlLite.dbTable;
import com.tezov.lib_java_android.database.sqlLite.dbTableDefinition;
import com.tezov.lib_java_android.definition.defCreatable;
import com.tezov.lib_java.generator.uid.defUIDGenerator;

public class dbSyncTable<ITEM extends ItemBase<ITEM>> extends dbTable<ItemSync>{
private dbTable<ITEM>.Ref tableSync = null;

@Override
public defCreatable<ItemSync> factory(){
    return ItemSync.getFactory();
}

@Override
public <GEN extends defUIDGenerator> GEN getUidGenerator(){
    return tableSync.getUidGenerator();
}

public dbTable<ItemSync> setTableDefinition(dbTable<ITEM>.Ref tableSync, dbTableDefinition definition){
    this.tableSync = tableSync;
    return super.setTableDefinition(definition);
}

public dbTable<ItemSync> setTableDescription(dbTable<ITEM>.Ref tableSync, TableDescription description, defEncoder encoderField){
    this.tableSync = tableSync;
    return super.setTableDescription(description, encoderField);
}

@Override
protected dbTableDefinition.Ref newTableDefinition(dbTableDefinition definition){
    return definition.newRef(tableSync.getTableDefinition().getName());
}

@Override
protected dbTable<ItemSync>.Ref createRef(){
    return new Ref();
}

public class Ref extends dbTable<ItemSync>.Ref{
    protected Ref(){
        super();
    }

    public dbTable<ITEM>.Ref getTableSync(){
        return tableSync;
    }

}

}
