/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.crypter.recycler.keystore;

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
import static com.tezov.crypter.application.Event.ON_CLICK_CANCEL;
import static com.tezov.crypter.application.Event.ON_CLICK_EDIT;
import static com.tezov.crypter.application.Event.ON_CLICK_SHARE;
import static com.tezov.lib_java.type.defEnum.Event.ON_CLICK_SHORT;
import static com.tezov.lib_java.type.defEnum.Event.OPENED;
import static com.tezov.lib_java_android.ui.misc.TouchDetectorMove.Action.MOVE_LEFT;

import androidx.recyclerview.widget.RecyclerView;

import com.tezov.crypter.R;
import com.tezov.crypter.data.dbItem.dbKey;
import com.tezov.crypter.data.item.ItemKey;
import com.tezov.lib_java.async.notifier.observer.event.ObserverEvent;
import com.tezov.lib_java.type.defEnum.Event;
import com.tezov.lib_java_android.ui.recycler.RecyclerListDataManager;
import com.tezov.lib_java_android.ui.recycler.RecyclerListRowBinder;
import com.tezov.lib_java_android.ui.recycler.RecyclerListRowManager;
import com.tezov.lib_java_android.ui.recycler.RecyclerListSwipper;

public class KeyRowManager extends RecyclerListRowManager<dbKey>{
public KeyRowManager(RecyclerListDataManager<dbKey> dataManager){
    super(dataManager);
    add(new KeyRowBinder(this));
}

@Override
public void onAttachedToRecyclerView(RecyclerView recyclerView){
    super.onAttachedToRecyclerView(recyclerView);
    RecyclerListSwipper itemSwipper = getRecyclerList().getItemTouchSwipper();
    if(itemSwipper != null){
        itemSwipper.observe(new ObserverEvent<Event.Is, RecyclerListSwipper.GestureInfo<dbKey>>(this, ON_CLICK_SHORT){
            @Override
            public void onComplete(Event.Is is, RecyclerListSwipper.GestureInfo<dbKey> info){
                switch(info.getModalViewId()){
                    case R.id.btn_cancel:
                        getDataManager().post(ON_CLICK_CANCEL, info.getHolder().get());
                        break;
                    case R.id.btn_edit:
                        getDataManager().post(ON_CLICK_EDIT, info.getHolder().get());
                        break;
                    case R.id.btn_share:{
                        dbKey dataKey = info.getHolder().get();
                        ItemKey itemKey = dataKey.getItem();
                        if(itemKey.isOwner()){
                            getDataManager().post(ON_CLICK_SHARE, info.getHolder().get());
                        }
                    }
                    break;
                }
            }
        });
        itemSwipper.observe(new ObserverEvent<com.tezov.crypter.application.Event.Is, RecyclerListSwipper.GestureInfo<dbKey>>(this, OPENED){
            @Override
            public void onComplete(com.tezov.crypter.application.Event.Is is, RecyclerListSwipper.GestureInfo<dbKey> info){
                if(info.getDirection() == MOVE_LEFT){
                    dbKey data = info.getHolder().get();
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
