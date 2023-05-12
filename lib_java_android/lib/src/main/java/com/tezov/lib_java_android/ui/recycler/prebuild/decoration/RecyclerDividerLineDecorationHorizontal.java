/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.recycler.prebuild.decoration;

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

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tezov.lib_java_android.R;
import com.tezov.lib_java_android.application.AppContext;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugTrack;


public class RecyclerDividerLineDecorationHorizontal extends RecyclerView.ItemDecoration{
private final Drawable divider;
private boolean isReversed;

public RecyclerDividerLineDecorationHorizontal(boolean isReversed){
    this(R.drawable.recycler_divider_line, isReversed);
}
public RecyclerDividerLineDecorationHorizontal(int resourceId, boolean isReversed){
    this(AppContext.getResources().getDrawable(resourceId), isReversed);
}
public RecyclerDividerLineDecorationHorizontal(Drawable divider, boolean isReversed){
DebugTrack.start().create(this).end();
    this.divider = divider;
    this.isReversed = isReversed;
    if(divider.getIntrinsicHeight() <= 0){
DebugException.start().log("height is null").end();
    }
}
@Override
public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state){
    super.getItemOffsets(outRect, view, parent, state);
    if(isReversed){
        outRect.top += divider.getIntrinsicHeight();
    }
    else {
        outRect.bottom += divider.getIntrinsicHeight();
    }
}

@Override
public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state){
    if(isReversed){
        for(int end = (parent.getChildCount() - 1), i = 0; i < end; i++){
            View view = parent.getChildAt(i);
            divider.setBounds(view.getLeft(), view.getTop() - divider.getIntrinsicHeight(), view.getRight(), view.getTop());
            divider.draw(c);
        }
    }
    else{
        for(int end = (parent.getChildCount() - 1), i = 0; i < end; i++){
            View view = parent.getChildAt(i);
            divider.setBounds(view.getLeft(), view.getBottom(), view.getRight(), view.getBottom() + divider.getIntrinsicHeight());
            divider.draw(c);
        }
    }
}
@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

}
