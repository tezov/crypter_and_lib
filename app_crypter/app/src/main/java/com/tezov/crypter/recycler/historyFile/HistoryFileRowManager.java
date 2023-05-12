/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.crypter.recycler.historyFile;

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
import static com.tezov.lib_java.type.defEnum.Event.OPENED;
import static com.tezov.lib_java_android.ui.misc.TouchDetectorMove.Action.MOVE_LEFT;

import androidx.recyclerview.widget.RecyclerView;

import com.tezov.crypter.data.dbItem.dbHistory;
import com.tezov.lib_java.async.notifier.observer.event.ObserverEvent;
import com.tezov.lib_java.type.defEnum.Event;
import com.tezov.lib_java_android.ui.recycler.RecyclerListDataManager;
import com.tezov.lib_java_android.ui.recycler.RecyclerListRowBinder;
import com.tezov.lib_java_android.ui.recycler.RecyclerListRowManager;
import com.tezov.lib_java_android.ui.recycler.RecyclerListSwipper;

public class HistoryFileRowManager extends RecyclerListRowManager<dbHistory>{
public HistoryFileRowManager(RecyclerListDataManager<dbHistory> dataManager){
    super(dataManager);
    add(new HistoryFileRowBinder(this));
}

@Override
public void onAttachedToRecyclerView(RecyclerView recyclerView){
    super.onAttachedToRecyclerView(recyclerView);
    RecyclerListSwipper itemSwipper = getRecyclerList().getItemTouchSwipper();
    if(itemSwipper != null){
        itemSwipper.observe(new ObserverEvent<Event.Is, RecyclerListSwipper.GestureInfo<dbHistory>>(this, OPENED){
            @Override
            public void onComplete(Event.Is is, RecyclerListSwipper.GestureInfo<dbHistory> info){
                if(info.getDirection() == MOVE_LEFT){
                    dbHistory data = info.getHolder().get();
                    data.putToTrash();
                }
            }
        });
    }
}

@Override
public int getItemViewType(int position){
    return RecyclerListRowBinder.ViewType.DEFAULT.ordinal();
}


}
