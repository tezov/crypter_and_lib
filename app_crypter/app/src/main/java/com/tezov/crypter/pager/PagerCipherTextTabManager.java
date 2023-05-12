/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.crypter.pager;

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
import static com.tezov.crypter.fragment.FragmentCipherText.Operation;
import static com.tezov.crypter.fragment.FragmentCipherText.Step;
import static com.tezov.lib_java_android.ui.recycler.RecyclerList.Event;

import android.util.SparseArray;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.tezov.crypter.R;
import com.tezov.crypter.fragment.FragmentCipherText;
import com.tezov.crypter.navigation.NavigationArguments;
import com.tezov.crypter.pager.page.CipherTextBinder;
import com.tezov.crypter.pager.page.CipherTextInBinder;
import com.tezov.crypter.pager.page.CipherTextOutBinder;
import com.tezov.lib_java.async.notifier.observer.event.ObserverEvent;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.type.runnable.RunnableW;
import com.tezov.lib_java_android.ui.layout.TabLayout;
import com.tezov.lib_java_android.ui.misc.StateView;
import com.tezov.lib_java_android.ui.recycler.RecyclerListRowBinder;
import com.tezov.lib_java_android.ui.recycler.RecyclerListRowHolder;
import com.tezov.lib_java_android.ui.recycler.pager.PagerTabRowManager;

import java.lang.reflect.Field;

public class PagerCipherTextTabManager extends PagerTabRowManager<CipherTextBinder.DataText>{
private final SparseArray<ViewType.Is> viewTypes;

{
    viewTypes = new SparseArray<>();
    viewTypes.append(0, ViewType.IN);
    viewTypes.append(1, ViewType.OUT);
}

private PagerCipherTextTabManager(FragmentCipherText fragment, ViewPager2 viewPager, TabLayout tabs){
    super(viewPager, tabs, new PagerCipherTextDataManager(tabs));
    add(new CipherTextInBinder(fragment, this));
    add(new CipherTextOutBinder(fragment, this));
    viewPager.setOffscreenPageLimit(tabs.getSize());
    try{
        Field recyclerViewField = ViewPager2.class.getDeclaredField("mRecyclerView");
        recyclerViewField.setAccessible(true);
        RecyclerView recyclerView = (RecyclerView)recyclerViewField.get(viewPager);
        Field touchSlopField = RecyclerView.class.getDeclaredField("mTouchSlop");
        touchSlopField.setAccessible(true);
        Integer touchSlop = (Integer)touchSlopField.get(recyclerView);
        touchSlopField.set(recyclerView, touchSlop * 5);
    } catch(Throwable e){
DebugException.start().log(e).end();
    }
}
public static PagerCipherTextTabManager newInstance(FragmentCipherText fragment, View view){
    ViewPager2 viewPager = view.findViewById(R.id.pager);
    TabLayout tabs = view.findViewById(R.id.pager_tabs);
    return new PagerCipherTextTabManager(fragment, viewPager, tabs);
}
public void showPage(ViewType.Is viewType, RunnableW runnableOnTargetReached){
    int targetPosition = getViewPosition(viewType);
    int currentPosition = getViewPager().getCurrentItem();
    if(currentPosition != targetPosition){
        if(runnableOnTargetReached != null){
            observe(new ObserverEvent<Event.Is, Object>(this, Event.ON_STOP_SCROLL){
                @Override
                public void onComplete(Event.Is event, Object object){
                    unsubscribe();
                    runnableOnTargetReached.run();
                }
            });
        }
        scrollToPosition(targetPosition);
    } else {
        if(runnableOnTargetReached != null){
            runnableOnTargetReached.run();
        }
    }
}
@Override
public void onViewAttachedToWindow(@NonNull RecyclerListRowHolder<CipherTextBinder.DataText> newHolder){
    super.onViewAttachedToWindow(newHolder);
    getRowBinder(getItemViewType(newHolder.getLayoutPosition())).onViewAttachedToWindow();
}
@Override
public void onViewDetachedFromWindow(@NonNull RecyclerListRowHolder<CipherTextBinder.DataText> holder){
    super.onViewDetachedFromWindow(holder);
    getRowBinder(getItemViewType(holder.getLayoutPosition())).onViewDetachedFromWindow();
}
@Override
public int getItemViewType(int position){
    return viewTypes.get(position).ordinal();
}
@Override
public PagerCipherTextDataManager getDataManager(){
    return super.getDataManager();
}
@Override
public CipherTextBinder getRowBinder(int itemViewType){
    return super.getRowBinder(itemViewType);
}
@Override
public CipherTextBinder getRowBinder(RecyclerListRowBinder.ViewType.Is viewType){
    return super.getRowBinder(viewType);
}

public ViewType.Is getCurrentViewType(){
    return viewTypes.get(getViewPager().getCurrentItem());
}
public int getViewPosition(ViewType.Is viewType){
    for(int end = viewTypes.size(), i = 0; i < end; i++){
        int key = viewTypes.keyAt(i);
        if(viewTypes.get(key) == viewType){
            return key;
        }
    }
DebugException.start().unknown("type", viewType).end();
    return -1;
}

public <D extends CipherTextBinder.DataText> D getData(ViewType.Is view){
    return (D)getDataManager().get(view);

}
public void setData(ViewType.Is view, CipherTextBinder.DataText data, boolean postUpdate){
    getDataManager().set(view, data);
    if(postUpdate){
        getRowBinder(view).set(data);
    }
}

public void onNewNavigationArguments(NavigationArguments arg){
    ((CipherTextInBinder)getRowBinder(ViewType.IN)).onNewNavigationArguments(arg);
}

public void setStep(Step previousStep, Step newStep, Operation operation){
    for(int end = viewTypes.size(), i = 0; i < end; i++){
        getRowBinder(viewTypes.valueAt(i)).setStep(previousStep, newStep, operation);
    }
    if((newStep == Step.IDLE) || (newStep == Step.SUCCEED) || (newStep == Step.FAILED) || (newStep == Step.ABORTED)){
        getViewPager().setEnabled(true);
    }
}

public void onDisabledButtons(StateView stateView){
    for(int end = viewTypes.size(), i = 0; i < end; i++){
        getRowBinder(viewTypes.valueAt(i)).onDisabledButtons(stateView);
    }
    getViewPager().setEnabled(false);
}
public void enableButtons(FragmentCipherText.Operation operation){
    for(int end = viewTypes.size(), i = 0; i < end; i++){
        getRowBinder(viewTypes.valueAt(i)).enableButtons(operation);
    }
    getViewPager().setEnabled(true);
}
public void onPasswordChanged(StateView stateView){
    for(int end = viewTypes.size(), i = 0; i < end; i++){
        getRowBinder(viewTypes.valueAt(i)).onPasswordChanged(stateView);
    }
}

public interface ViewType extends RecyclerListRowBinder.ViewType{
    Is IN = new Is("IN");
    Is OUT = new Is("OUT");

    class Is extends RecyclerListRowBinder.ViewType.Is{
        public Is(String name){
            super(name);
        }

    }

}


}
