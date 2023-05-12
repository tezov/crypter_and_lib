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
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import androidx.fragment.app.Fragment;

import com.tezov.lib_java_android.database.prebuild.listEntry.item.ItemEntry;
import com.tezov.lib_java_android.database.sqlLite.dbTable;
import com.tezov.lib_java_android.definition.defCreatable;
import com.tezov.lib_java.generator.uid.UUIDGenerator;
import com.tezov.lib_java.type.collection.ListEntry;
import com.tezov.lib_java.type.primaire.Entry;

import java.util.List;

import static com.tezov.lib_java_android.database.sqlLite.dbField.UID;
import static com.tezov.lib_java_android.database.sqlLite.filter.dbSign.LIKE;

public class dbListEntryTable extends dbTable<ItemEntry>{
@Override
public defCreatable<ItemEntry> factory(){
    return ItemEntry.getFactory();
}

@Override
public UUIDGenerator getUidGenerator(){
    return ItemEntry.getUidGenerator();
}

@Override
protected dbTable<ItemEntry>.Ref createRef(){
    return new Ref();
}

public class Ref extends dbTable<ItemEntry>.Ref{
    protected Ref(){
        super();
    }

    public List<ItemEntry> find(String key){
        return select(getNullFilterNoSync().where(UID, LIKE, ItemEntry.keyToUID(key), false));
    }

    public ItemEntry get(String key){
        return get(ItemEntry.keyToUID(key));
    }

    public boolean put(String key, String value){
        ItemEntry item = get(key);
        if(item == null){
            return insert(ItemEntry.obtain().clear().setKey(key).setValue(value)) != null;
        } else {
            return update(item.setValue(value));
        }
    }

    public int put(ListEntry<String, String> values){
        int count = 0;
        for(Entry<String, String> e: values){
            if(put(e.key, e.value)){
                count++;
            }
        }
        return count;
    }

    public ItemEntry remove(String key){
        return remove(ItemEntry.keyToUID(key));
    }

}

}
