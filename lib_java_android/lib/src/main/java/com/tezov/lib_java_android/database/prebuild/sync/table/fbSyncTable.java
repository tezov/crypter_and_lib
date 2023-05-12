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
import com.tezov.lib_java_android.database.firebase.fbTable;
import com.tezov.lib_java_android.database.prebuild.sync.item.ItemSync;
import com.tezov.lib_java_android.database.sqlLite.dbTableDefinition;
import com.tezov.lib_java_android.definition.defCreatable;
import com.tezov.lib_java.generator.uid.defUIDGenerator;

public class fbSyncTable<ITEM extends ItemBase<ITEM>> extends fbTable<ItemSync>{
private fbTable<ITEM>.Ref tableSync = null;

@Override
protected fbSyncTable<ITEM> me(){
    return (fbSyncTable<ITEM>)super.me();
}

@Override
public defCreatable<ItemSync> factory(){
    return ItemSync.getFactory();
}

@Override
public <GEN extends defUIDGenerator> GEN getUidGenerator(){
    return tableSync.getUidGenerator();
}

public fbTable<ItemSync> setTableDefinition(fbTable<ITEM>.Ref tableSync, dbTableDefinition definition){
    this.tableSync = tableSync;
    return super.setTableDefinition(definition);
}

public fbTable<ItemSync> setTableDescription(fbTable<ITEM>.Ref tableSync, TableDescription description, defEncoder encoderField){
    this.tableSync = tableSync;
    return super.setTableDescription(description, encoderField);
}

@Override
protected dbTableDefinition.Ref newTableDefinition(dbTableDefinition definition){
    return definition.newRef(tableSync.getTableDefinition().getName());
}

public <T extends fbTable<ITEM>.Ref> T getTableSync(){
    return (T)tableSync;
}

@Override
public fbTable<ItemSync>.Ref createRef(){
    return new Ref();
}

public class Ref extends fbTable<ItemSync>.Ref{
    protected Ref(){
        super();
    }

    public <T extends fbTable<ITEM>.Ref> T getTableSync(){
        return me().getTableSync();
    }

}

}
