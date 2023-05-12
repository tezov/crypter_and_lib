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
import static com.tezov.lib_java_android.ui.recycler.RecyclerListRowBinder.ViewType.DEFAULT;

import android.view.ViewGroup;
import android.widget.TextView;

import com.tezov.crypter.R;
import com.tezov.crypter.data.dbItem.dbKey;
import com.tezov.crypter.data.item.ItemKey;
import com.tezov.lib_java_android.ui.recycler.RecyclerListRowBinder;
import com.tezov.lib_java_android.ui.recycler.RecyclerListRowHolder;

public class KeyRowBinder extends RecyclerListRowBinder<KeyRowBinder.RowHolder, dbKey>{

public KeyRowBinder(KeyRowManager rowManager){
    super(rowManager);
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

public static class RowHolder extends RecyclerListRowHolder<dbKey>{
    private dbKey data;
    private final TextView label;
    public RowHolder(ViewGroup parent){
        super(R.layout.dialog_picker_recycler_wheel_row_label, parent);
        label = itemView.findViewById(R.id.lbl_label);
    }
    @Override
    public dbKey get(){
        return data;
    }
    @Override
    public void set(dbKey data){
        this.data = data;
        ItemKey key = data.getItem();
        StringBuilder alias = new StringBuilder();
        if(key.isOwner()){
            alias.append("> ");
        } else {
            alias.append("< ");
        }
        alias.append(key.getAlias());
        label.setText(alias);
    }

}

}
