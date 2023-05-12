/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.database.prebuild.file.item;

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

import com.tezov.lib_java_android.database.prebuild.file.table.dbFileTable;
import com.tezov.lib_java_android.database.prebuild.trash.item.ItemTrash;
import com.tezov.lib_java_android.database.sqlLite.dbItem.dbItem;
import com.tezov.lib_java.file.File;
import com.tezov.lib_java.generator.uid.defUid;
import com.tezov.lib_java.debug.DebugException;

public abstract class dbFile extends dbItem<ItemFile>{
public dbFile(){
}

public dbFile(ItemFile item){
    super(item);
}

public dbFile(File file){
    super(null);
    setFile(file);
}

@Override
protected ItemFile newItem(){
    return ItemFile.obtain().clear();
}

@Override
public dbFile copy(){
    return (dbFile)super.copy();
}

@Override
protected void onSetItem(){

}

private dbFileTable.Ref getFileTable(){
    return (dbFileTable.Ref)getUpdater().getTableRef();
}

public <DBF extends dbFile> DBF setFile(File file){
    if(getItem() == null){
        setItem(newItem());
    }
    getItem().setFile(file);
    getItem().setUid(getFileTable().getUID(file));
    return (DBF)this;
}

@Override
protected boolean beforeOfferItem(){

    if(!getItem().file.exists()){
DebugException.start().log("StorageFile doesn't exist, item:" + getItem()).end();
    }

    return getItem().file.exists();
}

@Override
public dbFile putToTrash(defUid requester, ItemTrash.Type.Is type){
    ItemFile item = getUpdater().putToTrash(requester, type, this.getItem());
    if(item == null){
        return null;
    } else {
        setItem(item);
        return this;
    }
}

@Override
public dbFile restoreFromTrash(){
    ItemFile item = getUpdater().restoreFromTrash(this.getItem());
    if(item == null){
        return null;
    } else {
        setItem(item);
        return this;
    }
}

@Override
public dbItem<ItemFile> remove(defUid requester){
    ItemFile item = getUpdater().remove(requester, this.getItem());
    if(item == null){
        return null;
    } else {
        setItem(item);
        return this;
    }
}

}
