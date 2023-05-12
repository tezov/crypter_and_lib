/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.recycler.prebuild.db.itemToSubItem;

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
import com.tezov.lib_java.toolbox.Compare;
import com.tezov.lib_java.toolbox.IteratorBuffer;
import com.tezov.lib_java.type.runnable.RunnableW;
import com.tezov.lib_java.wrapperAnonymous.FunctionW;
import com.tezov.lib_java.wrapperAnonymous.PredicateW;
import com.tezov.lib_java_android.ui.recycler.RecyclerListDataManager;
import com.tezov.lib_java_android.ui.recycler.prebuild.db.DataManagerBase;

import static com.tezov.lib_java_android.database.sqlLite.filter.dbSign.LIKE;

public class DataManagerSubItem<ITEM extends ItemBase<ITEM>> extends DataManagerBase<ITEM, SubItemString>{
private final FunctionW<ITEM, SubItemString> itemToSubItem;

public DataManagerSubItem(dbField.Is field, dbView<ITEM> view, FunctionW<ITEM, SubItemString> itemToSubItem){
    super(SubItemString.class, view);
    this.itemToSubItem = itemToSubItem;
    view.group(field, true, true);
    setDefaultFilter(new Filter(field, LIKE));
}

@Override
protected SubItemString newDbItem(ITEM item){
    return itemToSubItem.apply(item);
}

@Override
public <T> void filter(RecyclerListDataManager.Filter<T> filter){
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
public Integer indexOf(SubItemString data){
    data = rewrap(data);
    if(data.getUid() == null){
        return null;
    }
    ITEM item = getView().where(dbField.UID, data.getUid(), false).get();
    if(item == null){
        return null;
    }
    IteratorBuffer<ITEM> viewIterator = getViewIterator();
    if(viewIterator != null){
        Integer index = getViewIterator().indexOfFromBuffer(new PredicateW<ITEM>(){
            @Override
            public boolean test(ITEM itemFetched){
                return itemFetched.getUid().equals(item.getUid());
            }
        });
        if(index != null){
            return index;
        }
    }
    return getView().indexOf(item);
}

@Override
public SubItemString get(int position){
    IteratorBuffer<ITEM> viewIterator = getViewIterator();
    if(viewIterator.isLoaded(position)){
        return newDbItem(viewIterator.get(position));
    }
    else{
        return newDbItem(viewIterator.getProvider().get(position));
    }
}

private SubItemString rewrap(SubItemString dataToRewrap){
    if(dataToRewrap.getUid() != null){
        return dataToRewrap;
    }
    ITEM item = getViewIterator().getFromBuffer(new PredicateW<ITEM>(){
        @Override
        public boolean test(ITEM itemFetched){
            SubItemString dataFetched = newDbItem(itemFetched);
            return Compare.equals(dataFetched.getString(), dataToRewrap.getString());
        }
    });
    if(item != null){
        return newDbItem(item);
    }
    for(ITEM itemFetched: getView()){
        SubItemString dataFetched = newDbItem(itemFetched);
        if(!Compare.equals(dataFetched.getString(), dataToRewrap.getString())){
            continue;
        }
        return newDbItem(itemFetched);
    }
    return dataToRewrap;
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
