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
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import androidx.fragment.app.Fragment;

import android.view.View;

import com.tezov.lib_java_android.wrapperAnonymous.ViewOnAttachStateChangeListenerW;
import com.tezov.lib_java_android.ui.recycler.RecyclerList;
import com.tezov.lib_java_android.ui.recycler.RecyclerListDataManager;
import com.tezov.lib_java_android.ui.recycler.prebuild.row.label.LabelRowDescription;
import com.tezov.lib_java_android.ui.recycler.prebuild.row.label.LabelRowManager;

public class DataManagerNumber extends RecyclerListDataManager<String>{
private Integer modulo = null;

public DataManagerNumber(){
    super(String.class);
}

public static LabelRowManager<String> newRowManager(int layoutID){
    return new LabelRowManager<>(new DataManagerNumber(), LabelRowDescription.label(layoutID));
}

public DataManagerNumber setModulo(Integer modulo){
    this.modulo = modulo;
    return this;
}

@Override
public int size(){
    return Integer.MAX_VALUE;
}

@Override
public String get(int index){
    if(modulo == null){
        return String.valueOf(index);
    } else {
        return String.valueOf(index % modulo);
    }
}

@Override
public Integer indexOf(String data){
    if(modulo == null){
        return Integer.valueOf(data);
    } else {
        return Integer.parseInt(data) + centerPosition();
    }
}

private int centerPosition(){
    final int center = size() / 2;
    return center - center % modulo;
}

@Override
protected void onAttachedToRecyclerView(){
    if(modulo == null){
        return;
    }
    RecyclerList recyclerList = getRowManager().getRecyclerList();
    recyclerList.addOnAttachStateChangeListener(new ViewOnAttachStateChangeListenerW(){
        @Override
        public void onViewAttachedToWindow(View v){
            recyclerList.scrollToPositionWithOffset(centerPosition(), 0);
            recyclerList.removeOnAttachStateChangeListener(this);
        }

        @Override
        public void onViewDetachedFromWindow(View v){

        }
    });
}

}
