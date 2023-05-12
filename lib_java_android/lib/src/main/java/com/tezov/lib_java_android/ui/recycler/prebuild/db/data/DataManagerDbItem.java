/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.recycler.prebuild.db.data;

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
import com.tezov.lib_java_android.database.ItemBase;
import com.tezov.lib_java_android.database.sqlLite.dbItem.dbItem;
import com.tezov.lib_java_android.database.sqlLite.dbView;
import com.tezov.lib_java.toolbox.IteratorBuffer;
import com.tezov.lib_java.wrapperAnonymous.PredicateW;
import com.tezov.lib_java_android.ui.recycler.prebuild.db.DataManagerBase;

public abstract class DataManagerDbItem<ITEM extends ItemBase<ITEM>, DATA extends dbItem<ITEM>> extends DataManagerBase<ITEM, DATA>{
public DataManagerDbItem(){
    setType(getDbType());
    setView(newView());
}

protected abstract Class<DATA> getDbType();

protected abstract dbView<ITEM> newView();

public void resetView(){
    setView(newView());
    resetViewIterator();
}

@Override
public Integer indexOf(DATA data){
    IteratorBuffer<ITEM> viewIterator = getViewIterator();
    if(viewIterator != null){
        Integer index = viewIterator.indexOfFromBuffer(new PredicateW<ITEM>(){
            @Override
            public boolean test(ITEM itemFetched){
                return itemFetched.getUid().equals(data.getUid());
            }
        });
        if(index != null){
            return index;
        }
    }
    return getView().indexOf(data.getItem());
}

@Override
public DATA get(int position){
    IteratorBuffer<ITEM> viewIterator = getViewIterator();
    if(viewIterator.isLoaded(position)){
        return newDbItem(viewIterator.get(position));
    } else {
        return newDbItem(viewIterator.getProvider().get(position));
    }
}
@Override
public DATA putToTrash(int position){
    return (DATA)get(position).putToTrash();
}
@Override
public DATA restoreFromTrash(int position, DATA data){
    return (DATA)data.restoreFromTrash();
}

}
