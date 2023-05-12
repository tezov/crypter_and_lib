/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.recycler;

import com.tezov.lib_java.debug.DebugLog;
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
import static com.tezov.lib_java_android.ui.recycler.RecyclerList.PositionSnap.BOTTOM;
import static com.tezov.lib_java_android.ui.recycler.RecyclerList.PositionSnap.BOTTOM_COMPLETELY_VISIBLE;
import static com.tezov.lib_java_android.ui.recycler.RecyclerList.PositionSnap.CENTER;
import static com.tezov.lib_java_android.ui.recycler.RecyclerList.PositionSnap.TOP;
import static com.tezov.lib_java_android.ui.recycler.RecyclerList.PositionSnap.TOP_COMPLETELY_VISIBLE;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugTrack;

public class RecyclerListLinear extends RecyclerList{
public RecyclerListLinear(Context context){
    super(context);
    init();
}

public RecyclerListLinear(Context context, AttributeSet attrs){
    super(context, attrs);
    init();
}

public RecyclerListLinear(Context context, AttributeSet attrs, int defStyle){
    super(context, attrs, defStyle);
    init();
}

private void init(){

}

@Override
public void setLayoutManager(@Nullable RecyclerView.LayoutManager layout){
    if(layout instanceof Layout){
        super.setLayoutManager(layout);
    } else {
DebugException.start().explode("Must extend RecyclerListLinear.Layout").end();
    }
}
public <L extends Layout> L getLayout(){
    return (L)super.getLayoutManager();
}

@Override
public int getPosition(PositionSnap.Is snapPosition){
    if(snapPosition == TOP){
        return getLayout().findFirstVisibleItemPosition();
    }
    else if(snapPosition == TOP_COMPLETELY_VISIBLE){
        return getLayout().findFirstCompletelyVisibleItemPosition();
    }
    else if(snapPosition == CENTER){
        if(getLayout().canScrollVertically()){
            int canvasCenterLine = getHeight() / 2;
            return findPositionInCenterVertically(canvasCenterLine);
        }
        if(getLayout().canScrollHorizontally()){
            int canvasCenterLine = getWidth() / 2;
            return findPositionInCenterHorizontally(canvasCenterLine);
        }
        return RecyclerList.NO_POSITION;
    }
    else if(snapPosition == BOTTOM){
        return getLayout().findLastVisibleItemPosition();
    }
    else if(snapPosition == BOTTOM_COMPLETELY_VISIBLE){
        return getLayout().findLastCompletelyVisibleItemPosition();
    }
DebugException.start().unknown("position", snapPosition).end();
    return RecyclerList.NO_POSITION;
}

private int findPositionInCenterVertically(int canvasCenterLine){
    int smallestDistance = -1;
    int smallestDistanceChildIndex = -1;
    for(int end = getChildCount(), i = 0; i < end; i+= 1){
        View v = getChildAt(i);
        int viewCenterLine = v.getTop() + v.getHeight() / 2;
        int distance = Math.abs(canvasCenterLine - viewCenterLine);
        if((distance < smallestDistance) || (smallestDistance == -1)){
            smallestDistance = distance;
            smallestDistanceChildIndex = i;
        }
        else if(distance > smallestDistance){
            return getChildLayoutPosition(getChildAt(smallestDistanceChildIndex));
        }
    }
    return RecyclerList.NO_POSITION;
}
private int findPositionInCenterHorizontally(int canvasCenterLine){
    int smallestDistance = -1;
    int smallestDistanceChildIndex = -1;
    for(int end = getChildCount(), i = 0; i < end; i+= 1){
        View v = getChildAt(i);
        int viewCenterLine = v.getLeft() + v.getWidth() / 2;
        int distance = Math.abs(canvasCenterLine - viewCenterLine);
        if((distance < smallestDistance) || (smallestDistance == -1)){
            smallestDistance = distance;
            smallestDistanceChildIndex = i;
        }
        else if(distance > smallestDistance){
            return getChildLayoutPosition(getChildAt(smallestDistanceChildIndex));
        }
    }
    return RecyclerList.NO_POSITION;
}

@Override
public int[] getPositions(PositionSnap.Is snapPosition){
    return new int[]{getPosition(snapPosition)};
}

@Override
public boolean isTop(){
    int position = getLayout().findFirstCompletelyVisibleItemPosition();
    return (position == 0) && canScrollVertically(1);
}

@Override
public boolean isBottom(){
    int position = getLayout().findLastCompletelyVisibleItemPosition();
    return (position == (getAdapter().getItemCount() - 1)) && canScrollVertically(-1);
}

@Override
public void scrollToPositionWithOffset(int position, int offset){
    getLayout().scrollToPositionWithOffset(position, offset);
}

@Override
public void setEnabled(boolean flag){
    getLayout().setEnable(flag);
}
@Override
public boolean isEnabled(){
    return (getLayout() != null) && getLayout().isEnabled();
}

public static abstract class Layout extends androidx.recyclerview.widget.LinearLayoutManager{
    public Layout(Context context, int orientation, boolean reverseLayout){
        super(context, orientation, reverseLayout);
DebugTrack.start().create(this).end();
    }
    public abstract void setEnable(boolean flag);
    public abstract boolean isEnabled();
    @Override
    protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
        super.finalize();
    }

}

}

