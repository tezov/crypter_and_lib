/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.recycler.prebuild.decoration;

import com.tezov.lib_java.debug.DebugLog;
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

import com.tezov.lib_java_android.application.AppDisplay;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.MeasureSpec;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tezov.lib_java_android.application.AppContext;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java_android.ui.recycler.RecyclerList;

import static android.view.View.MeasureSpec.UNSPECIFIED;
import static com.tezov.lib_java_android.ui.recycler.RecyclerList.PositionSnap.CENTER;

public class RecyclerTargetLineDecorationHorizontal extends RecyclerView.ItemDecoration{
private final static int DIVIDER_OFFSET_px = AppDisplay.convertDpToPx(2);
private final Drawable divider;

public RecyclerTargetLineDecorationHorizontal(int dividerResourceId){
    this(AppContext.getResources().getDrawable(dividerResourceId));
}
public RecyclerTargetLineDecorationHorizontal(Drawable divider){
DebugTrack.start().create(this).end();
    this.divider = divider;
}

private int getViewHeight(View view){
    int height = view.getMeasuredHeight();
    if(height <= 0){
        view.measure(
                MeasureSpec.makeMeasureSpec(0, UNSPECIFIED),
                MeasureSpec.makeMeasureSpec(0, UNSPECIFIED));
        height = view.getMeasuredHeight();
    }
    return height;
}

@Override
public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state){
    super.getItemOffsets(outRect, view, parent, state);
    int position = parent.getChildLayoutPosition(view);
    if(position == 0){
        int height = getViewHeight(view);
        outRect.top = (int)((height * 0.05) + (parent.getLayoutParams().height - height) / 2);
    }
    if(position == (state.getItemCount() - 1)){
        int height = getViewHeight(view);
        outRect.bottom = (int)((height * 0.05) + (parent.getLayoutParams().height - height) / 2);
    }
}

@Override
public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state){
    RecyclerList recycler = (RecyclerList)parent;
    RecyclerView.ViewHolder viewHolder = recycler.findRowHolderInLayout(recycler.getPosition(CENTER));
    if(viewHolder != null){
        int lastChildHeight_half = getViewHeight(viewHolder.itemView) / 2;
        int center = c.getHeight() / 2;
        divider.setBounds(0  , center - lastChildHeight_half - DIVIDER_OFFSET_px,
                c.getWidth(), center + lastChildHeight_half + DIVIDER_OFFSET_px);
        divider.draw(c);
    }
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

}
