/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.database.sqlLite.dbItem;

import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.util.UtilsString;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import androidx.fragment.app.Fragment;
import androidx.annotation.Nullable;

import com.tezov.lib_java_android.database.ItemBase;
import com.tezov.lib_java_android.database.TableDescription;
import com.tezov.lib_java_android.database.prebuild.trash.item.ItemTrash;
import com.tezov.lib_java.definition.defCopyable;
import com.tezov.lib_java.generator.uid.defHasUid;
import com.tezov.lib_java.generator.uid.defUid;
import com.tezov.lib_java.toolbox.Compare;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.collection.ListEntry;
import com.tezov.lib_java.type.primaire.Entry;

import java.util.Arrays;
import java.util.List;

public abstract class dbItem<ITEM extends ItemBase<ITEM>> implements defHasUid, defCopyable<dbItem<ITEM>>{
private ITEM item;
private ListEntry<TableDescription, dbItem<? extends ItemBase>> dataItems = null;

public dbItem(){
    trackClassCreate();
    setItem(newItem());
}

public dbItem(ITEM item){
    trackClassCreate();
    setItem(item);
}

private void trackClassCreate(){
DebugTrack.start().create(this).end();
}

private ListEntry<TableDescription, dbItem<? extends ItemBase>> getDbItems(){
    if(dataItems == null){
        dataItems = new ListEntry<>();
    }
    return dataItems;
}

protected abstract dbItemUpdater<ITEM> getUpdater();

protected abstract ITEM newItem();

protected abstract dbItem<ITEM> newData();

protected void onSetItem(){

}

final public <D extends dbItem<ITEM>> D setItem(ITEM item, boolean callOnSetItem){
    this.item = item;
    if(callOnSetItem){
        onSetItem();
    }
    return (D)this;
}

public ITEM getItem(){
    return item;
}

public <D extends dbItem<ITEM>> D setItem(ITEM item){
    return setItem(item, true);
}

public <D extends dbItem<ITEM>> D setDbItem(TableDescription key, dbItem<?> dbItem){
    getDbItems().put(key, dbItem);
    return (D)this;
}

public <D extends dbItem<? extends ItemBase>> D getDbItem(TableDescription key){
    if(dataItems == null){
        return null;
    } else {
        return (D)dataItems.getValue(key);
    }
}

@Override
public defUid getUid(){
    if(item == null){
        return null;
    } else {
        return item.getUid();
    }
}
public dbItem<ITEM> setUid(defUid uid){

DebugException.start().notImplemented().end();

    return null;
}
public defUid bytesToUID(byte[] uid){

DebugException.start().notImplemented().end();

    return null;
}

@Override
public dbItem<ITEM> copy(){
    dbItem copy = newData();
    copy.item = item.copy();
    if(dataItems != null){
        for(Entry<TableDescription, dbItem<? extends ItemBase>> e: dataItems){
            copy.setDbItem(e.key, e.value != null ? e.value.copy() : null);
        }
    }
    return copy;
}

protected boolean beforeOfferItem(){
    return true;
}
protected boolean beforeOfferDataItem(TableDescription key, dbItem<? extends ItemBase> dbItem){
    return true;
}
protected boolean afterOfferItem(){
    return true;
}
protected boolean afterOfferDataItem(TableDescription key, dbItem<? extends ItemBase> dbItem){
    return true;
}
public boolean offer(){
    return offer(true);
}
public boolean offer(boolean offerDataItems){
    if(offerDataItems && !offerItems()){
        return false;
    }
    return offerItem();
}
public boolean offer(TableDescription... keys){
    return offer((List)Collections.singletonList(keys));
}
public boolean offer(List<? extends TableDescription> keys){
    if(!offerItems(keys)){
        return false;
    }
    offerItem();
    return true;
}
public boolean offerTB(ListEntry<? extends TableDescription, Boolean> keys){
    if(!offerItemsTB(keys)){
        return false;
    }
    offerItem();
    return true;
}
public boolean offerTL(ListEntry<? extends TableDescription, List<? extends TableDescription>> keys){
    if(!offerItemsTL(keys)){
        return false;
    }
    offerItem();
    return true;
}
public boolean offerTLB(ListEntry<? extends TableDescription, ListEntry<? extends TableDescription, Boolean>> keys){
    if(!offerItemsTLB(keys)){
        return false;
    }
    offerItem();
    return true;
}
private boolean offerItem(){
    if(!beforeOfferItem()){
        return false;
    }
    ITEM item = getUpdater().offer(this.item);
    if(item == null){
        return false;
    }
    setItem(item);
    return afterOfferItem();
}
private boolean offerItems(){
    if(dataItems != null){
        for(Entry<TableDescription, dbItem<? extends ItemBase>> e: dataItems){
            if(!beforeOfferDataItem(e.key, e.value)){
                return false;
            }
            if(!e.value.offer(true)){
                return false;
            }
            if(!afterOfferDataItem(e.key, e.value)){
                return false;
            }
        }
    }
    return true;
}
private boolean offerItem(TableDescription key, boolean offerDataItems){
    if(dataItems == null){
        return true;
    }
    dbItem<? extends ItemBase> dbItem = dataItems.getValue(key);
    if(!beforeOfferDataItem(key, dbItem)){
        return false;
    }
    boolean success = dbItem.offer(offerDataItems);
    if(!afterOfferDataItem(key, dbItem)){
        return false;
    }
    return success;
}
private boolean offerItem(TableDescription key, List<? extends TableDescription> keys){
    if(dataItems == null){
        return true;
    }
    dbItem<? extends ItemBase> dbItem = dataItems.getValue(key);
    if(!beforeOfferDataItem(key, dbItem)){
        return false;
    }
    boolean success = dbItem.offer(keys);
    if(!afterOfferDataItem(key, dbItem)){
        return false;
    }
    return success;
}
private boolean offerItem(TableDescription key, ListEntry<? extends TableDescription, Boolean> keys){
    if(dataItems == null){
        return true;
    }
    dbItem<? extends ItemBase> dbItem = dataItems.getValue(key);
    if(!beforeOfferDataItem(key, dbItem)){
        return false;
    }
    boolean success = dbItem.offerItemsTB(keys);
    if(!afterOfferDataItem(key, dbItem)){
        return false;
    }
    return success;
}
private boolean offerItems(List<? extends TableDescription> keys){
    for(TableDescription key: keys){
        if(!offerItem(key, true)){
            return false;
        }
    }
    return true;
}
private boolean offerItemsTB(ListEntry<? extends TableDescription, Boolean> keys){
    for(Entry<? extends TableDescription, Boolean> e: keys){
        if(!offerItem(e.key, e.value)){
            return false;
        }
    }
    return true;
}
private boolean offerItemsTL(ListEntry<? extends TableDescription, List<? extends TableDescription>> keys){
    for(Entry<? extends TableDescription, List<? extends TableDescription>> e: keys){
        if(!offerItem(e.key, e.value)){
            return false;
        }
    }
    return true;
}
private boolean offerItemsTLB(ListEntry<? extends TableDescription, ListEntry<? extends TableDescription, Boolean>> keys){
    for(Entry<? extends TableDescription, ListEntry<? extends TableDescription, Boolean>> e: keys){
        if(!offerItem(e.key, e.value)){
            return false;
        }
    }
    return true;
}

protected boolean beforeInsertItem(){
    return true;
}
protected boolean beforeInsertDataItem(TableDescription key, dbItem<? extends ItemBase> dbItem){
    return true;
}
protected boolean afterInsertItem(){
    return true;
}
protected boolean afterInsertDataItem(TableDescription key, dbItem<? extends ItemBase> dbItem){
    return true;
}
public boolean insert(){
    return insert(true);
}
public boolean insert(boolean insertDataItems){
    if(insertDataItems && !insertItems()){
        return false;
    }
    return insertItem();
}
public boolean insert(TableDescription... keys){
    return insert((List)Collections.singletonList(keys));
}
public boolean insert(List<? extends TableDescription> keys){
    if(!insertItems(keys)){
        return false;
    }
    insertItem();
    return true;
}
public boolean insertTB(ListEntry<? extends TableDescription, Boolean> keys){
    if(!insertItemsTB(keys)){
        return false;
    }
    insertItem();
    return true;
}
public boolean insertTL(ListEntry<? extends TableDescription, List<? extends TableDescription>> keys){
    if(!insertItemsTL(keys)){
        return false;
    }
    insertItem();
    return true;
}
public boolean insertTLB(ListEntry<? extends TableDescription, ListEntry<? extends TableDescription, Boolean>> keys){
    if(!insertItemsTLB(keys)){
        return false;
    }
    insertItem();
    return true;
}
private boolean insertItem(){
    if(!beforeInsertItem()){
        return false;
    }
    ITEM item = getUpdater().insert(this.item);
    if(item == null){
        return false;
    }
    setItem(item);
    return afterInsertItem();
}
private boolean insertItems(){
    if(dataItems != null){
        for(Entry<TableDescription, dbItem<? extends ItemBase>> e: dataItems){
            if(!beforeInsertDataItem(e.key, e.value)){
                return false;
            }
            if(!e.value.insert(true)){
                return false;
            }
            if(!afterInsertDataItem(e.key, e.value)){
                return false;
            }
        }
    }
    return true;
}
private boolean insertItem(TableDescription key, boolean insertDataItems){
    if(dataItems == null){
        return true;
    }
    dbItem<? extends ItemBase> dbItem = dataItems.getValue(key);
    if(!beforeInsertDataItem(key, dbItem)){
        return false;
    }
    boolean success = dbItem.insert(insertDataItems);
    if(!afterInsertDataItem(key, dbItem)){
        return false;
    }
    return success;
}
private boolean insertItem(TableDescription key, List<? extends TableDescription> keys){
    if(dataItems == null){
        return true;
    }
    dbItem<? extends ItemBase> dbItem = dataItems.getValue(key);
    if(!beforeInsertDataItem(key, dbItem)){
        return false;
    }
    boolean success = dbItem.insert(keys);
    if(!afterInsertDataItem(key, dbItem)){
        return false;
    }
    return success;
}
private boolean insertItem(TableDescription key, ListEntry<? extends TableDescription, Boolean> keys){
    if(dataItems == null){
        return true;
    }
    dbItem<? extends ItemBase> dbItem = dataItems.getValue(key);
    if(!beforeInsertDataItem(key, dbItem)){
        return false;
    }
    boolean success = dbItem.insertItemsTB(keys);
    if(!afterInsertDataItem(key, dbItem)){
        return false;
    }
    return success;
}
private boolean insertItems(List<? extends TableDescription> keys){
    for(TableDescription key: keys){
        if(!insertItem(key, true)){
            return false;
        }
    }
    return true;
}
private boolean insertItemsTB(ListEntry<? extends TableDescription, Boolean> keys){
    for(Entry<? extends TableDescription, Boolean> e: keys){
        if(!insertItem(e.key, e.value)){
            return false;
        }
    }
    return true;
}
private boolean insertItemsTL(ListEntry<? extends TableDescription, List<? extends TableDescription>> keys){
    for(Entry<? extends TableDescription, List<? extends TableDescription>> e: keys){
        if(!insertItem(e.key, e.value)){
            return false;
        }
    }
    return true;
}
private boolean insertItemsTLB(ListEntry<? extends TableDescription, ListEntry<? extends TableDescription, Boolean>> keys){
    for(Entry<? extends TableDescription, ListEntry<? extends TableDescription, Boolean>> e: keys){
        if(!insertItem(e.key, e.value)){
            return false;
        }
    }
    return true;
}

public dbItem<ITEM> putToTrash(){
    return putToTrash(ItemTrash.Type.PUT_TO_TRASH);
}
public dbItem<ITEM> putToTrash(ItemTrash.Type.Is type){
    return putToTrash(null, type);
}
public dbItem<ITEM> putToTrash(defUid requester){
    return putToTrash(requester, ItemTrash.Type.PUT_TO_TRASH);
}
public abstract dbItem<ITEM> putToTrash(defUid requester, ItemTrash.Type.Is type);

public abstract dbItem<ITEM> restoreFromTrash();

public dbItem<ITEM> remove(){
    return remove(null);
}
public dbItem<ITEM> remove(defUid requester){

DebugException.start().notImplemented().end();

    return null;
}

@Override
public boolean equals(@Nullable Object obj){
    if(obj == null){
        return this.getItem() == null;
    }
    if(!(obj instanceof dbItem)){
        return false;
    }
    return Compare.equals(this.getItem(), ((dbItem)obj).getItem());
}

public DebugString toDebugString(){
    DebugString data = new DebugString();
    data.append("item", item);
    if(dataItems != null){
        for(Entry<TableDescription, dbItem<? extends ItemBase>> e: dataItems){
            data.append(e.key.name(), e.value);
        }
    }
    return data;
}

final public void toDebugLog(){
DebugLog.start().send(toDebugString()).end();
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

}
