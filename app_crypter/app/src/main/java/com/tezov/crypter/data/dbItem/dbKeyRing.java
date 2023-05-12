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
import com.tezov.crypter.data.item.ItemKeyRing;
import com.tezov.crypter.data.table.dbItemHolder.dbItemKeyRingUpdater;
import com.tezov.crypter.data.table.dbItemHolder.dbItemUpdater;
import com.tezov.lib_java_android.database.prebuild.trash.item.ItemTrash;
import com.tezov.lib_java_android.database.sqlLite.dbItem.dbItem;
import com.tezov.lib_java.generator.uid.defUid;

public class dbKeyRing extends dbItem<ItemKeyRing>{
final public static dbItemKeyRingUpdater updater = new dbItemKeyRingUpdater();

public dbKeyRing(){
}
public dbKeyRing(ItemKeyRing item){
    super(item);
}

@Override
protected dbItemUpdater<ItemKeyRing> getUpdater(){
    return updater;
}

@Override
protected ItemKeyRing newItem(){
    return ItemKeyRing.obtain().clear();
}

@Override
protected dbItem<ItemKeyRing> newData(){
    return new dbKeyRing(null);
}

@Override
public dbKeyRing copy(){
    return (dbKeyRing)super.copy();
}

@Override
public boolean offer(){
    if(getItem().canOffer()){
        return super.offer();
    } else {
        return false;
    }
}
@Override
public boolean insert(){
    if(getItem().canOffer()){
        return super.insert();
    } else {
        return false;
    }
}

@Override
public dbItem<ItemKeyRing> putToTrash(defUid requester, ItemTrash.Type.Is type){
    if(getUpdater().isOpen()){
        ItemKeyRing item = getUpdater().remove(requester, this.getItem());
        if(item != null){
            setItem(item);
            return this;
        }
    }
    return null;
}
@Override
public dbItem<ItemKeyRing> restoreFromTrash(){
    return null;
}


}
