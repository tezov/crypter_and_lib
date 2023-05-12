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
import com.tezov.crypter.data.item.ItemHistory;
import com.tezov.crypter.data.table.dbItemHolder.dbItemHistoryUpdater;
import com.tezov.crypter.data.table.dbItemHolder.dbItemUpdater;
import com.tezov.lib_java_android.database.prebuild.trash.item.ItemTrash;
import com.tezov.lib_java_android.database.sqlLite.dbItem.dbItem;
import com.tezov.lib_java.generator.uid.defUid;

public class dbHistory extends dbItem<ItemHistory>{
final public static dbItemHistoryUpdater updater = new dbItemHistoryUpdater();

public dbHistory(){
}
public dbHistory(ItemHistory item){
    super(item);
}
private static Class<dbHistory> myClass(){
    return dbHistory.class;
}
@Override
protected dbItemUpdater<ItemHistory> getUpdater(){
    return updater;
}
@Override
protected ItemHistory newItem(){
    return ItemHistory.obtain().clear();
}
@Override
protected dbItem<ItemHistory> newData(){
    return new dbHistory(null);
}
@Override
public dbHistory copy(){
    return (dbHistory)super.copy();
}

@Override
public dbItem<ItemHistory> putToTrash(defUid requester, ItemTrash.Type.Is type){
    if(getUpdater().isOpen()){
        ItemHistory item = getUpdater().remove(requester, this.getItem());
        if(item != null){
            setItem(item);
            return this;
        }
    }
    return null;
}
@Override
public dbItem<ItemHistory> restoreFromTrash(){
    return null;
}


}
