/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.recycler.prebuild.db.item;

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
import com.tezov.lib_java_android.database.sqlLite.dbField;
import com.tezov.lib_java_android.database.sqlLite.dbView;
import com.tezov.lib_java_android.database.sqlLite.filter.dbSign;
import com.tezov.lib_java.toolbox.IteratorBuffer;
import com.tezov.lib_java.type.runnable.RunnableW;
import com.tezov.lib_java.wrapperAnonymous.PredicateW;
import com.tezov.lib_java_android.ui.recycler.prebuild.db.DataManagerBase;

public class DataManagerItem<ITEM extends ItemBase<ITEM>> extends DataManagerBase<ITEM, ITEM>{
public DataManagerItem(Class<ITEM> type, dbView<ITEM> view){
    super(type, view);
}

@Override
protected ITEM newDbItem(ITEM item){
    return item;
}

@Override
public <T> void filter(DataManagerBase.Filter<T> filter){
    filter((Filter)filter);
}

public void filter(Filter filter){
    RunnableW runnable = new RunnableW(){
        @Override
        public void runSafe(){
            if(getViewIterator() != null){
                getView().enableNotification(false);
                getView().where(filter.field, filter.sign, filter.getValue(), true);
                resetViewIterator();
                getRowManager().notifyUpdatedAll(false);
                getView().enableNotification(true);
            } else {
                getView().where(filter.field, filter.sign, filter.getValue(), true);
            }
        }
    };
    if(isRecyclerViewAttachedToWindow()){
        postToHandler(runnable);
    } else {
        runnable.run();
    }
}

@Override
public Integer indexOf(ITEM data){
    IteratorBuffer<ITEM> viewIterator = getViewIterator();
    if(viewIterator != null){
        Integer index = getViewIterator().indexOfFromBuffer(new PredicateW<ITEM>(){
            @Override
            public boolean test(ITEM itemFetched){
                return itemFetched.getUid().equals(data.getUid());
            }
        });
        if(index != null){
            return index;
        }
    }
    return getView().indexOf(data);
}

@Override
public ITEM get(int position){
    IteratorBuffer<ITEM> viewIterator = getViewIterator();
    if(viewIterator.isLoaded(position)){
        return viewIterator.get(position);
    }
    else{
        return viewIterator.getProvider().get(position);
    }
}

public static class Filter extends DataManagerBase.Filter<Object>{
    dbField.Is field;
    dbSign.Is sign;

    public Filter(dbField.Is field, dbSign.Is sign){
        this.field = field;
        this.sign = sign;
    }

    public dbField.Is getField(){
        return field;
    }

    public dbSign.Is getSign(){
        return sign;
    }

}

}
