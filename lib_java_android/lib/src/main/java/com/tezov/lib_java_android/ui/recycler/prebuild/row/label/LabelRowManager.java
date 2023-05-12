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

import com.tezov.lib_java_android.ui.recycler.RecyclerListDataManager;
import com.tezov.lib_java_android.ui.recycler.RecyclerListRowManager;

import static com.tezov.lib_java_android.ui.recycler.RecyclerListRowBinder.ViewType.DEFAULT;

public class LabelRowManager<ITEM> extends RecyclerListRowManager<ITEM>{
public LabelRowManager(RecyclerListDataManager<ITEM> dataManager, LabelRowBinder.DescriptionView descriptionView){
    super(dataManager);
    add(new LabelRowBinder<>(this, descriptionView));
}

public LabelRowManager(RecyclerListDataManager<ITEM> dataManager, LabelRowBinder.DescriptionView descriptionView, LabelRowBinder.AdapterItem<ITEM> adapterItem){
    super(dataManager);
    add(new LabelRowBinder<>(this, descriptionView, adapterItem));
}

@Override
public int getItemViewType(int position){
    Integer type = getItemViewTypeIfValid(position);
    if(type != null){
        return type;
    }
    return DEFAULT.ordinal();
}

}
