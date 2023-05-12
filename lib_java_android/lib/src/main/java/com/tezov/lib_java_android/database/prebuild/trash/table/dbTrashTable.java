/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.database.prebuild.trash.table;

import com.tezov.lib_java.debug.DebugTrack;
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

import com.tezov.lib_java_android.database.prebuild.trash.item.ItemTrash;
import com.tezov.lib_java_android.database.sqlLite.dbTable;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilter;
import com.tezov.lib_java_android.definition.defCreatable;
import com.tezov.lib_java.generator.uid.UUIDGenerator;
import com.tezov.lib_java.generator.uid.defUid;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.type.collection.ListOrObject;

import java.util.List;

import static com.tezov.lib_java_android.database.prebuild.trash.table.DescriptionTrash.Field.ITEM_TABLE;
import static com.tezov.lib_java_android.database.prebuild.trash.table.DescriptionTrash.Field.ITEM_UID;
import static com.tezov.lib_java_android.database.prebuild.trash.table.DescriptionTrash.Field.REQUESTER;
import static com.tezov.lib_java_android.database.prebuild.trash.table.DescriptionTrash.Field.TYPE;

public class dbTrashTable extends dbTable<ItemTrash>{
    private dbTrashTable me(){
        return this;
    }
@Override
public defCreatable<ItemTrash> factory(){
    return ItemTrash.getFactory();
}

@Override
public UUIDGenerator getUidGenerator(){
    return ItemTrash.getUidGenerator();
}

@Override
protected dbTable<ItemTrash>.Ref createRef(){
    return new Ref();
}

public class Ref extends dbTable<ItemTrash>.Ref{
    protected Ref(){
        super();
    }

    public List<defUid> select(defUid requester, ItemTrash.Type.Is type, String itemTable){
        synchronized(me()){
            dbFilter filter = getNullFilterNoSync();
            if(requester != null){
                filter.where(REQUESTER, requester.toBytes(), false);
            }
            if(type != null){
                filter.where(TYPE, type.name(), false);
            }
            if(itemTable != null){
                filter.where(ITEM_TABLE, itemTable, false);
            }
            List<ItemTrash> items = select(filter);
            if(items == null){
                return null;
            }
            List<defUid> uids = new ListOrObject<defUid>();
            for(ItemTrash item: items){
                uids.add(item.itemUid);
            }
            return uids;
        }
    }

    public ItemTrash remove(String itemTable, defUid itemUid){
        synchronized(me()){
            List<ItemTrash> items = remove(getNullFilterNoSync().where(ITEM_TABLE, itemTable, false).where(ITEM_UID, itemUid, false));
            if(items == null){
                return null;
            } else if(items.size() == 1){
                return items.get(0);
            } else {

DebugLog.start().send(items).end();
DebugException.start().explode("More than 1 item removed").end();

                return null;
            }
        }
    }

}

}
