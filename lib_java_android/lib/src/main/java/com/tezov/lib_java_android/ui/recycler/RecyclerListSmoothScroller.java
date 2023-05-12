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
import com.tezov.lib_java_android.toolbox.PostToHandler;

import android.content.Context;
import android.graphics.PointF;
import android.util.DisplayMetrics;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.ref.Ref;
import com.tezov.lib_java.type.ref.WR;
import com.tezov.lib_java.type.runnable.RunnableW;
import com.tezov.lib_java_android.wrapperAnonymous.RecyclerViewOnScrollListenerW;
import com.tezov.lib_java_android.ui.recycler.pager.PagerTabRowManager;

import static com.tezov.lib_java_android.ui.recycler.RecyclerList.Event.ON_STOP_SCROLL;
import static com.tezov.lib_java_android.ui.recycler.RecyclerList.PositionSnap.BOTTOM;
import static com.tezov.lib_java_android.ui.recycler.RecyclerList.PositionSnap.CENTER;
import static com.tezov.lib_java_android.ui.recycler.RecyclerList.PositionSnap.TOP;

public class RecyclerListSmoothScroller extends LinearSmoothScroller implements RecyclerList.SmoothScroller{
private static final float MILLISECONDS_PER_INCH = 50f;

private RecyclerList.PositionSnap.Is snapPosition = TOP;
private WR<RecyclerView> recyclerWR;
private RecyclerViewOnScrollListenerW scrollListener = null;

public RecyclerListSmoothScroller(Context context){
    super(context);
DebugTrack.start().create(this).end();
}

private RecyclerListSmoothScroller me(){
    return this;
}

@Override
public void scrollToPosition(RecyclerView recycler, RecyclerList.PositionSnap.Is snapPosition, int position){
    recyclerWR = WR.newInstance(recycler);
    me().snapPosition = snapPosition;
    PostToHandler.of(recycler, new RunnableW(){
        @Override
        public void runSafe(){
            RecyclerView recycler = recyclerWR.get();
            if(recycler != null){
                setTargetPosition(position);
                recycler.getLayoutManager().startSmoothScroll(me());
            }
        }
    });
}

@Override
protected void onStart(){
    super.onStart();
    if(Ref.isNull(recyclerWR)){
        return;
    }
    if(scrollListener != null){
        return;
    }
    scrollListener = new RecyclerViewOnScrollListenerW(){
        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState){
            if(newState != RecyclerList.SCROLL_STATE_IDLE){
                return;
            }
            RecyclerView recycler = recyclerWR.get();
            if(recycler == null){
                return;
            }
            recycler.removeOnScrollListener(this);
            if(recycler instanceof RecyclerList){
                ((RecyclerList)recycler).post(ON_STOP_SCROLL, ((RecyclerList)recycler).getPosition(snapPosition));
            } else {
                RecyclerView.Adapter adapter = recycler.getAdapter();
                if(adapter instanceof PagerTabRowManager){
                    ((PagerTabRowManager)adapter).post(ON_STOP_SCROLL, ((PagerTabRowManager)adapter).getViewPager().getCurrentItem());
                }
            }
        }
    };
    recyclerWR.get().addOnScrollListener(scrollListener);
}

@Override
public void stopScroll(){
    if(Ref.isNull(recyclerWR)){
        return;
    }
    if(scrollListener != null){
        return;
    }
    recyclerWR.get().removeOnScrollListener(scrollListener);
    scrollListener = null;
}

@Override
protected int getVerticalSnapPreference(){
    if(snapPosition == TOP){
        return LinearSmoothScroller.SNAP_TO_START;
    }
    if(snapPosition == CENTER){
        return snapPosition.ordinal();
    }
    if(snapPosition == BOTTOM){
        return LinearSmoothScroller.SNAP_TO_END;
    }

DebugException.start().unknown("position", snapPosition).end();

    return 0;
}

@Override
public int calculateDtToFit(int viewStart, int viewEnd, int boxStart, int boxEnd, int snapPreference){
    if(snapPosition == CENTER){
        return (boxStart + (boxEnd - boxStart) / 2) - (viewStart + (viewEnd - viewStart) / 2);
    } else {
        return super.calculateDtToFit(viewStart, viewEnd, boxStart, boxEnd, snapPreference);
    }
}

@Override
public PointF computeScrollVectorForPosition(int targetPosition){
    return super.computeScrollVectorForPosition(targetPosition);
}

@Override
protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics){
    return MILLISECONDS_PER_INCH / displayMetrics.densityDpi;
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

}
