/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.crypter.data.dbItem;

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugTrack;
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
import static com.tezov.crypter.data.table.Descriptions.KEY_RING;
import static com.tezov.lib_java_android.database.sqlLite.dbField.UID;

import com.tezov.crypter.application.Application;
import com.tezov.crypter.data.item.ItemKey;
import com.tezov.crypter.data.item.ItemKeyRing;
import com.tezov.crypter.data.table.db.dbKeyRingTable;
import com.tezov.crypter.data.table.dbItemHolder.dbItemKeyUpdater;
import com.tezov.crypter.data.table.dbItemHolder.dbItemUpdater;
import com.tezov.lib_java_android.database.ItemBase;
import com.tezov.lib_java_android.database.TableDescription;
import com.tezov.lib_java_android.database.prebuild.trash.item.ItemTrash;
import com.tezov.lib_java_android.database.sqlLite.dbItem.dbItem;
import com.tezov.lib_java.generator.uid.defUid;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.type.collection.ListEntry;

public class dbKey extends dbItem<ItemKey>{
final public static dbItemKeyUpdater updater = new dbItemKeyUpdater();

public dbKey(){
}
public dbKey(ItemKey item){
    super(item);
}
private static Class<dbKey> myClass(){
    return dbKey.class;
}

@Override
protected dbItemUpdater<ItemKey> getUpdater(){
    return updater;
}

@Override
protected ItemKey newItem(){
    return ItemKey.obtain().clear();
}

@Override
protected dbItem<ItemKey> newData(){
    return new dbKey(null);
}

@Override
public dbKey copy(){
    return (dbKey)super.copy();
}

private dbKeyRingTable.Ref getKeyRingRefDB(){
    return dbKeyRing.updater.getTableRef();
}

public dbKey initKeyRing(){
    return setDataKeyRing(new dbKeyRing());
}

public dbKey loadKeyRing(boolean reload){
    if(!reload && (getDataKeyRing() != null)){
        return this;
    }
    ItemKeyRing keyRing = null;
    if(getItem() != null){
        defUid keyRingUid = getItem().getKeyRingUid();
        if(keyRingUid != null){
            Application.lockerTables().lock(myClass());
            keyRing = getKeyRingRefDB().where(UID, keyRingUid, false).get();
            Application.lockerTables().unlock(myClass());
        }
    }
    return setKeyRing(keyRing);
}
public dbKey unloadKeyRing(){
    ItemKeyRing keyRing = null;
    if(getItem() != null){
        setDbItem(KEY_RING, null);
    }
    return this;
}

public dbKey setKeyRing(ItemKeyRing keyRing){
    if(keyRing != null){
        return setDataKeyRing(new dbKeyRing(keyRing));
    } else {
        return setDataKeyRing(null);
    }
}
public dbKeyRing getDataKeyRing(){
    return getDbItem(KEY_RING);
}
public dbKey setDataKeyRing(dbKeyRing dataKeyRing){
    if(getItem() != null){
        if(dataKeyRing == null){
            getItem().setKeyRingUid(null);
        } else {
            getItem().setKeyRingUid(dataKeyRing.getUid());
        }
    }
    return setDbItem(KEY_RING, dataKeyRing);
}

public void nullifyPassword(){
    getItem().hasPassword(false);
    getDataKeyRing().getItem().nullifyPassword();
}

@Override
protected boolean afterOfferDataItem(com.tezov.lib_java_android.database.TableDescription key, dbItem<? extends ItemBase> dbItem){
    if(key == KEY_RING){
        getItem().setKeyRingUid(getDataKeyRing().getUid());
    }
    return true;
}
@Override
protected boolean beforeOfferItem(){
    if(!getItem().getKeyRingUid().equals(getDataKeyRing().getUid())){
DebugException.start().log("key.keyRingUid != keyRing.uid").end();
    }
    return true;
}
@Override
public boolean offer(){
    ListEntry<TableDescription, Boolean> keys = new ListEntry<>();
    keys.add(KEY_RING, false);
    Application.lockerTables().lock(myClass());
    boolean result = offerTB(keys);
    Application.lockerTables().unlock(myClass());
    if(result){
        getDataKeyRing().getItem().clear();
        setDbItem(KEY_RING, null);
    }
    return result;
}

@Override
protected boolean afterInsertDataItem(com.tezov.lib_java_android.database.TableDescription key, dbItem<? extends ItemBase> dbItem){
    if(key == KEY_RING){
        getItem().setKeyRingUid(getDataKeyRing().getUid());
    }
    return true;
}

@Override
public boolean insert(){
    ListEntry<TableDescription, Boolean> keys = new ListEntry<>();
    keys.add(KEY_RING, false);
    Application.lockerTables().lock(myClass());
    boolean result = insertTB(keys);
    Application.lockerTables().unlock(myClass());
    if(result){
        getDataKeyRing().getItem().clear();
        setDbItem(KEY_RING, null);
    }
    return result;
}

@Override
public dbItem<ItemKey> putToTrash(defUid requester, ItemTrash.Type.Is type){
    if(getUpdater().isOpen()){
        Application.lockerTables().lock(myClass());
        loadKeyRing(true);
        if(getDataKeyRing().putToTrash(getUid(), type) == null){
DebugException.start().log("keyRing not put in trash").end();
        }
        ItemKey item = getUpdater().remove(requester, this.getItem());
        Application.lockerTables().unlock(myClass());
        if(item != null){
            setItem(item);
            return this;
        }
    }
    return null;
}
@Override
public dbItem<ItemKey> restoreFromTrash(){
    return null;
}

}
