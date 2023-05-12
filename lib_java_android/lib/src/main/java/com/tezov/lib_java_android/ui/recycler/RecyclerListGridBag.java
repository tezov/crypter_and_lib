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
import com.tezov.lib_java.util.UtilsArray;
import com.tezov.lib_java.util.UtilsString;

import java.util.Arrays;
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

import com.tezov.lib_java.debug.DebugException;

public class RecyclerListGridBag extends RecyclerList{
public RecyclerListGridBag(Context context){
    super(context);
    init();
}

public RecyclerListGridBag(Context context, AttributeSet attrs){
    super(context, attrs);
    init();
}

public RecyclerListGridBag(Context context, AttributeSet attrs, int defStyle){
    super(context, attrs, defStyle);
    init();
}

private void init(){

}

@Override
public void setLayoutManager(@Nullable LayoutManager layout){
    if(layout instanceof Layout){
        super.setLayoutManager(layout);
    } else {
DebugException.start().explode("Must extend RecyclerListGridBag.Layout").end();
    }
}
public <L extends Layout> L getLayout(){
    return (L)super.getLayoutManager();
}

@Override
public int getPosition(PositionSnap.Is snapPosition){
    int position = RecyclerList.NO_POSITION;
    int[] positions = getPositions(snapPosition);
    if((snapPosition == TOP)||(snapPosition == TOP_COMPLETELY_VISIBLE)){
        int top = getBottom();
        for(int p:positions){
            if(p!=RecyclerList.NO_POSITION){
                View child = findRowHolderInAdapter(p).itemView;
                if(child.getTop() < top){
                    position = p;
                    top = child.getTop();
                }
            }
        }
    }
    else if(snapPosition == CENTER){
        if(getLayout().canScrollVertically()){
            return findPositionInCenterVertically(positions);
        }
        if(getLayout().canScrollHorizontally()){
            return findPositionInCenterHorizontally(positions);
        }
        return RecyclerList.NO_POSITION;
    }
    else if((snapPosition == BOTTOM)||(snapPosition == BOTTOM_COMPLETELY_VISIBLE)){
        int bottom = getTop();
        for(int p:positions){
            if(p!=RecyclerList.NO_POSITION){
                View child = findRowHolderInAdapter(p).itemView;
                if(child.getBottom() > bottom){
                    position = p;
                    bottom = child.getBottom();
                }
            }
        }
    }
    return position;
}
@Override
public int[] getPositions(PositionSnap.Is snapPosition){
    if(snapPosition == TOP){
        return  getLayout().findFirstVisibleItemPositions(null);
    }
    else if(snapPosition == TOP_COMPLETELY_VISIBLE){
        return getLayout().findFirstCompletelyVisibleItemPositions(null);
    }
    else if(snapPosition == CENTER){
        int spanCount = getLayout().getSpanCount();
        int[] position = new int[spanCount];
        if(getLayout().canScrollVertically()){
            int canvasCenterLine = getHeight() / 2;
            for(int i=0; i<spanCount; i++){
                position[i] = findPositionInCenterVertically(i, spanCount, canvasCenterLine);
            }
            return position;
        }
        if(getLayout().canScrollHorizontally()){
            int canvasCenterLine = getWidth() / 2;
            for(int i=0; i<spanCount; i++){
                position[i] = findPositionInCenterHorizontally(i, spanCount, canvasCenterLine);
            }
            return position;
        }
        Arrays.fill(position, RecyclerList.NO_POSITION);
        return position;
    }
    else if(snapPosition == BOTTOM){
        return  getLayout().findLastVisibleItemPositions(null);
    }
    else if(snapPosition == BOTTOM_COMPLETELY_VISIBLE){
        return getLayout().findLastCompletelyVisibleItemPositions(null);
    }
DebugException.start().unknown("position", snapPosition).end();
    int spanCount = getLayout().getSpanCount();
    int[] position = new int[spanCount];
    Arrays.fill(position, RecyclerList.NO_POSITION);
    return position;
}

private int findPositionInCenterVertically(int[] positions){
    int canvasCenterLine = getHeight() / 2;
    int smallestDistance = -1;
    int smallestDistanceChildPosition = RecyclerList.NO_POSITION;
    for(int p:positions){
        if(p != RecyclerList.NO_POSITION){
            View v = findRowHolderInAdapter(p).itemView;
            int viewCenterLine = v.getTop() + v.getHeight() / 2;
            int distance = Math.abs(canvasCenterLine - viewCenterLine);
            if((distance < smallestDistance) || (smallestDistance == -1)){
                smallestDistance = distance;
                smallestDistanceChildPosition = p;
            }
        }
    }
    return smallestDistanceChildPosition;
}
private int findPositionInCenterVertically(int start, int step, int canvasCenterLine){
    int smallestDistance = -1;
    int smallestDistanceChildIndex = -1;
    for(int end = getChildCount(), i = start; i < end; i+=step){
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
private int findPositionInCenterHorizontally(int[] positions){
    int canvasCenterLine = getWidth() / 2;
    int smallestDistance = -1;
    int smallestDistanceChildPosition = RecyclerList.NO_POSITION;
    for(int p:positions){
        if(p != RecyclerList.NO_POSITION){
            View v = findRowHolderInAdapter(p).itemView;
            int viewCenterLine = v.getLeft() + v.getWidth() / 2;
            int distance = Math.abs(canvasCenterLine - viewCenterLine);
            if((distance < smallestDistance) || (smallestDistance == -1)){
                smallestDistance = distance;
                smallestDistanceChildPosition = p;
            }
        }
    }
    return smallestDistanceChildPosition;
}
private int findPositionInCenterHorizontally(int start, int step, int canvasCenterLine){
    int smallestDistance = -1;
    int smallestDistanceChildIndex = -1;
    for(int end = getChildCount(), i = start; i < end; i+=step){
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
public boolean isTop(){
    int[] position = getLayout().findFirstCompletelyVisibleItemPositions(null);
    for(int p:position){
        if((p == 0) && canScrollVertically(1)){
            return true;
        }
    }
    return false;
}
@Override
public boolean isBottom(){
    int[] position = getLayout().findLastCompletelyVisibleItemPositions(null);
    for(int p:position){
        if((p == (getAdapter().getItemCount() - 1)) && canScrollVertically(-1)){
            return true;
        }
    }
    return false;
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

public static abstract class Layout extends androidx.recyclerview.widget.StaggeredGridLayoutManager{
    public Layout(int spanCount, int orientation){
        super(spanCount, orientation);
    }
    public abstract void setEnable(boolean flag);
    public abstract boolean isEnabled();
}

}

