/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.recycler.prebuild.adapter;

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.util.UtilsString;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import androidx.fragment.app.Fragment;

import com.tezov.lib_java_android.ui.recycler.RecyclerListDataManager;
import com.tezov.lib_java_android.ui.recycler.prebuild.row.label.LabelRowBinder;
import com.tezov.lib_java_android.ui.recycler.prebuild.row.label.LabelRowDescription;
import com.tezov.lib_java_android.ui.recycler.prebuild.row.label.LabelRowManager;

import java.util.Arrays;
import java.util.List;

public class DataManagerFromList<TYPE> extends RecyclerListDataManager<TYPE>{
private final List<TYPE> datas;

public DataManagerFromList(List<TYPE> datas){
    super((Class<TYPE>)datas.get(0).getClass());
    this.datas = datas;
}

public static <TYPE extends Enum<TYPE>> LabelRowManager<TYPE> newRowManager(Class<TYPE> type, int layoutID, LabelRowBinder.AdapterItem<TYPE> itemToStringFunction){
    return newRowManager(type.getEnumConstants(), layoutID, itemToStringFunction);
}

public static <TYPE> LabelRowManager<TYPE> newRowManager(TYPE[] datas, int layoutID, LabelRowBinder.AdapterItem<TYPE> itemToStringFunction){
    return newRowManager(Arrays.asList(datas), layoutID, itemToStringFunction);
}

public static <TYPE> LabelRowManager<TYPE> newRowManager(List<TYPE> datas, int layoutID, LabelRowBinder.AdapterItem<TYPE> itemToStringFunction){
    DataManagerFromList<TYPE> dataManager = new DataManagerFromList<>(datas);
    return new LabelRowManager<>(dataManager, LabelRowDescription.label(layoutID), itemToStringFunction);
}

@Override
public int size(){
    return datas.size();
}

@Override
public TYPE get(int index){
    return datas.get(index);
}

@Override
public Integer indexOf(TYPE data){
    return datas.indexOf(data);
}

}
