/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.recycler.prebuild.row.label;

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

import android.view.View;
import android.view.ViewGroup;

import com.tezov.lib_java.toolbox.Reflection;
import com.tezov.lib_java.type.collection.ListEntry;
import com.tezov.lib_java.type.primaire.Entry;
import com.tezov.lib_java_android.ui.recycler.RecyclerListRowBinder;
import com.tezov.lib_java_android.ui.recycler.RecyclerListRowHolder;
import com.tezov.lib_java_android.ui.recycler.prebuild.db.itemToSubItem.SubItemString;

import static com.tezov.lib_java_android.ui.recycler.RecyclerListRowBinder.ViewType.DEFAULT;

public class LabelRowBinder<ITEM> extends RecyclerListRowBinder<LabelRowBinder<ITEM>.RowHolder, ITEM>{
private final DescriptionView descriptionView;
private final AdapterItem<ITEM> adapterItem;

public LabelRowBinder(LabelRowManager rowManager, DescriptionView descriptionView){
    this(rowManager, descriptionView, null);
}

public LabelRowBinder(LabelRowManager rowManager, DescriptionView descriptionView, AdapterItem<ITEM> adapterItem){
    super(rowManager);
    this.descriptionView = descriptionView;
    if(adapterItem != null){
        this.adapterItem = adapterItem;
    } else {
        this.adapterItem = new AdapterItem<ITEM>(){
            @Override
            public <T> T extractValue(int id, Class<T> type, ITEM item){
                if(item == null){
                    return null;
                } else {
                    if(type == String.class){
                        if(item instanceof SubItemString){
                            return (T)((SubItemString)item).getString();
                        } else {
                            return (T)item.toString();
                        }
                    } else {
                        if(Reflection.isInstanceOf(item, type)){
                            return (T)item;
                        } else {
                            return null;
                        }
                    }
                }
            }
        };
    }
}

@Override
public ViewType.Is getViewType(){
    return DEFAULT;
}

@Override
public RowHolder create(ViewGroup parent){
    RowHolder view = new RowHolder(parent);
    return addOnClick(view);
}

public interface DescriptionView{
    int getLayoutId();

    ListEntry<Integer, Class<?>> getViewIds();

    void setValue(int id, View view, Object value);

}

public interface AdapterItem<ITEM>{
    <T> T extractValue(int id, Class<T> type, ITEM item);

}

public class RowHolder extends RecyclerListRowHolder<ITEM>{
    private ITEM item;

    public RowHolder(ViewGroup parent){
        super(descriptionView.getLayoutId(), parent);
    }

    @Override
    public ITEM get(){
        return item;
    }

    @Override
    public void set(ITEM item){
        this.item = item;
        ListEntry<Integer, Class<?>> l = descriptionView.getViewIds();
        for(Entry<Integer, Class<?>> e: l){
            Object value = adapterItem.extractValue(e.key, e.value, item);
            descriptionView.setValue(e.key, itemView.findViewById(e.key), value);
        }
    }

}

}
