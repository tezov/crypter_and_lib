/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.crypter.data.table.dbItemHolder;

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
import com.tezov.lib_java_android.database.ItemBase;
import com.tezov.lib_java_android.database.prebuild.trash.item.ItemTrash;
import com.tezov.lib_java.debug.DebugException;

public abstract class dbItemUpdater<ITEM extends ItemBase<ITEM>> extends com.tezov.lib_java_android.database.sqlLite.dbItem.dbItemUpdater<ITEM>{
public dbItemUpdater(){
}

@Override
protected void onInserted(boolean success, ITEM item){

    if(!success){
DebugException.start().log("insert failed. " + item).end();
    }


}
@Override
protected void onUpdated(boolean success, ITEM item){

    if(!success){
DebugException.start().log("update failed. " + item).end();
    }


}
@Override
protected void onPutToTrash(boolean success, ItemTrash itemTrash, ITEM item){

    if(!success){
DebugException.start().log("put to trash failed. " + item).end();
    }


}
@Override
protected void onRestoredFromTrash(boolean success, ItemTrash itemTrash, ITEM item){

    if(!success){
DebugException.start().log("restore getRaw trash failed. " + item).end();
    }


}

}
