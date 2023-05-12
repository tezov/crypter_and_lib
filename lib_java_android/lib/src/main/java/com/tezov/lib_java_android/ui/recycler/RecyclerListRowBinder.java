/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.recycler;

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

import android.view.View;
import android.view.ViewGroup;

import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.defEnum.EnumBase;
import com.tezov.lib_java.type.defEnum.Event;
import com.tezov.lib_java.type.ref.WR;
import com.tezov.lib_java_android.wrapperAnonymous.ViewOnClickListenerW;
import com.tezov.lib_java_android.wrapperAnonymous.ViewOnLongClickListenerW;

public abstract class RecyclerListRowBinder<HOLDER extends RecyclerListRowHolder<TYPE>, TYPE>{
private final WR<RecyclerListRowManager<TYPE>> rowManagerWR;

protected RecyclerListRowBinder(RecyclerListRowManager<TYPE> rowManager){
DebugTrack.start().create(this).end();
    rowManagerWR = WR.newInstance(rowManager);
}

public <R extends RecyclerListRowManager<TYPE>> R getRowManager(){
    return (R)rowManagerWR.get();
}

public abstract RecyclerListRowBinder.ViewType.Is getViewType();

public abstract HOLDER create(ViewGroup parent);

protected <V extends RecyclerListRowHolder<TYPE>> V addOnClick(V view){
    view.itemView.setOnClickListener(new ViewOnClickListenerW(){
        @Override
        public void onClicked(View v){
            post(Event.ON_CLICK_SHORT, v);
        }
    });
    return view;
}

protected <V extends RecyclerListRowHolder<TYPE>> V addOnLongClick(V view){
    view.itemView.setOnLongClickListener(new ViewOnLongClickListenerW(){
        @Override
        public boolean onLongClick(View v){
            post(Event.ON_CLICK_LONG, v);
            return true;
        }
    });
    return view;
}

public void bind(HOLDER view, TYPE data){
    view.set(data);
}

private void post(Event.Is event, View v){
    RecyclerListRowManager<TYPE> rowManager = getRowManager();
    HOLDER holder = ((RecyclerList)rowManager.getRecyclerView()).findRowHolder(v);
    rowManager.getDataManager().post(event, holder.get());
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

public interface ViewType{
    Is DEFAULT = new Is("DEFAULT");
    Is MODAL = new Is("MODAL");

    static Is find(int ordinal){
        return ViewType.Is.findInstanceOf(ViewType.Is.class, ordinal);
    }

    class Is extends EnumBase.Is{
        public Is(String name){
            super(name);
        }

    }

}

}
