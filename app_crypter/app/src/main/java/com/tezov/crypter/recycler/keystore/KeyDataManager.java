/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.crypter.recycler.keystore;

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
import static com.tezov.crypter.data.table.Descriptions.KEY;
import static com.tezov.crypter.data.table.description.DescriptionKey.Field.ALIAS;
import static com.tezov.lib_java_android.database.sqlLite.dbField.DELETED;
import static com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder.Direction.ASC;

import com.tezov.crypter.application.Application;
import com.tezov.crypter.data.dbItem.dbKey;
import com.tezov.crypter.data.item.ItemKey;
import com.tezov.crypter.data.table.description.DescriptionKey;
import com.tezov.lib_java_android.database.sqlLite.dbView;
import com.tezov.lib_java_android.database.sqlLite.filter.dbSign;
import com.tezov.lib_java.type.runnable.RunnableW;
import com.tezov.lib_java_android.ui.recycler.prebuild.db.data.DataManagerDbItem;

public class KeyDataManager extends DataManagerDbItem<ItemKey, dbKey>{
@Override
protected Class<dbKey> getDbType(){
    return dbKey.class;
}

@Override
protected dbView<ItemKey> newView(){
    dbView<ItemKey> view = Application.tableHolder().handle().newView(KEY);
    view = view.where(DELETED, false, true).order(ALIAS, ASC, true).setPrimary(ALIAS, ASC);
    return view;
}

@Override
protected dbKey newDbItem(ItemKey item){
    return new dbKey(item);
}

@Override
public <T> void filter(DataManagerDbItem.Filter<T> filter){
    filter((Filter)filter);
}

public void filter(Filter filter){
    RunnableW runnable = new RunnableW(){
        @Override
        public void runSafe(){
            if(getViewIterator() != null){
                getView().enableNotification(false);
                getView().where(filter.field, filter.sign, filter.getValue(), true).iterator();
                resetViewIterator();
                getRowManager().notifyUpdatedAll(false);
                getView().enableNotification(true);
            } else {
                getView().where(filter.field, filter.sign, filter.getValue(), true).iterator();
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
protected void onDataUpdated(int position, ItemKey ItemKey){
}

public static class Filter extends DataManagerDbItem.Filter<Object>{
    DescriptionKey.Field.Is field;
    dbSign.Is sign;

    public Filter(DescriptionKey.Field.Is field, dbSign.Is sign){
        this.field = field;
        this.sign = sign;
    }

    public DescriptionKey.Field.Is getField(){
        return field;
    }

    public dbSign.Is getSign(){
        return sign;
    }

}

}
