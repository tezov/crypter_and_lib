/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.database.sqlLite.dbItem;

import com.tezov.lib_java.debug.DebugLog;
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

import com.tezov.lib_java_android.database.ItemBase;
import com.tezov.lib_java_android.database.prebuild.trash.item.ItemTrash;
import com.tezov.lib_java_android.database.prebuild.trash.table.dbTrashTable;
import com.tezov.lib_java_android.database.sqlLite.dbTable;
import com.tezov.lib_java.generator.uid.defUid;
import com.tezov.lib_java.debug.DebugTrack;

import java.util.List;

public abstract class dbItemUpdater<ITEM extends ItemBase<ITEM>>{
protected dbTable<ITEM>.Ref table = null;
protected dbTrashTable.Ref tableTrash = null;

public dbItemUpdater(){
DebugTrack.start().create(this).end();
}

public dbTable<ITEM>.Ref getTableRef(){
    return table;
}
public dbTrashTable.Ref getTableTrashRef(){
    return tableTrash;
}

public abstract dbTable<ITEM>.Ref newtTableRef();
public dbTrashTable.Ref newtTableTrashRef(){
    return null;
}

public boolean isOpen(){
    return table != null;
}
public void open(){
    table = newtTableRef();
}
public void close(){
    table = null;
    tableTrash = null;
}

public ITEM offer(ITEM item){
    if(item.getUid() == null){
        return insert(item);
    } else if(update(item)){
        return item;
    } else {
        return null;
    }
}
public ITEM insert(ITEM item){
    ITEM itemInserted = table.insert(item);
    onInserted(itemInserted != null, item);
    return itemInserted;
}
public boolean update(ITEM item){
    boolean success = table.update(item);
    onUpdated(success, item);
    return success;
}

public ITEM putToTrash(defUid requester, ItemTrash.Type.Is type, ITEM item){
    ITEM itemPutInTrash = table.putToTrash(item);
    ItemTrash itemTrash = insertTrashEntry(requester, type, itemPutInTrash);
    onPutToTrash(itemPutInTrash != null, itemTrash, itemPutInTrash);
    return itemPutInTrash;
}
private ItemTrash insertTrashEntry(defUid requester, ItemTrash.Type.Is type, ITEM item){
    if((tableTrash == null) || (item == null)){
        return null;
    }
    ItemTrash itemTrash = ItemTrash.obtain().clear().setTimestampToNow().setRequester(requester).setType(type).setItemTable(table).setItemUid(item.getUid());
    return tableTrash.insert(itemTrash);
}

public ITEM restoreFromTrash(ITEM item){
    ITEM itemRestoredFromTrash = table.restoreFromTrash(item);
    ItemTrash itemTrash = deleteFomTrashEntry(itemRestoredFromTrash);
    onRestoredFromTrash(itemRestoredFromTrash != null, itemTrash, item);
    return itemRestoredFromTrash;
}
private ItemTrash deleteFomTrashEntry(ITEM item){
    if((tableTrash == null) || (item == null)){
        return null;
    }
    return tableTrash.remove(table.getName(), item.getUid());
}

public ITEM remove(defUid requester, ITEM item){
    ITEM itemRemoved = table.remove(item);
    ItemTrash itemTrash = insertTrashEntry(requester, ItemTrash.Type.REMOVED, itemRemoved);
    onRemoved(itemRemoved != null, itemTrash, item);
    return itemRemoved;
}

protected void onInserted(boolean success, ITEM item){
}
protected void onUpdated(boolean success, ITEM item){
}
protected void onPutToTrash(boolean success, ItemTrash itemTrash, ITEM item){
}
protected void onRestoredFromTrash(boolean success, ItemTrash itemTrash, ITEM item){
}
protected void onRemoved(boolean success, ItemTrash itemTrash, ITEM item){
}

public List<defUid> selectItemUidInTrash(defUid requester){
    return selectItemUidInTrash(requester, null, null);
}
public List<defUid> selectItemUidInTrash(String itemTable){
    return selectItemUidInTrash(null, null, itemTable);
}
public List<defUid> selectItemUidInTrash(defUid requester, String itemTable){
    return selectItemUidInTrash(requester, null, itemTable);
}
public List<defUid> selectItemUidInTrash(defUid requester, ItemTrash.Type.Is type, String itemTable){
    return tableTrash.select(requester, type, itemTable);
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

}
