/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.recycler;

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
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.tezov.lib_java_android.application.AppContext;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.type.ref.WR;

public class RecyclerListLayoutLinearHorizontal extends RecyclerListLinear.Layout{
private final Integer maxItemsToDisplay;
private WR<RecyclerList> recyclerWR;
private boolean isHorizontalScrollEnabled = true;
private boolean isWidthMeasured = false;

public RecyclerListLayoutLinearHorizontal(){
    this(null);
}
public RecyclerListLayoutLinearHorizontal(Integer maxItemsToDisplay){
    super(AppContext.getActivity(), RecyclerView.HORIZONTAL, false);
    this.maxItemsToDisplay = maxItemsToDisplay;
}

@Override
public void addView(View child, int index){
    super.addView(child, index);
    if((maxItemsToDisplay == null) || (isWidthMeasured)){
        return;
    }
    child.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
    int width = child.getMeasuredWidth();
    width += child.getPaddingStart() + child.getPaddingEnd();
    ViewGroup.LayoutParams lp = recyclerWR.get().getLayoutParams();
    lp.width = width * maxItemsToDisplay;
    recyclerWR.get().setLayoutParams(lp);
    isWidthMeasured = true;
}

@Override
public void onAttachedToWindow(RecyclerView recycler){
    super.onAttachedToWindow(recycler);
    this.recyclerWR = WR.newInstance((RecyclerList)recycler);
}

@Override
public void setEnable(boolean flag){
    this.isHorizontalScrollEnabled = flag;
}
@Override
public boolean isEnabled(){
    return isHorizontalScrollEnabled;
}

@Override
public boolean canScrollVertically(){
    return false;
}

@Override
public boolean canScrollHorizontally(){
    return isHorizontalScrollEnabled;
}

@Override
public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state){
    try{
        super.onLayoutChildren(recycler, state);
    } catch(IndexOutOfBoundsException e){

DebugException.start().logHidden("Predictive animation inhibit").end();

    }
}

}
