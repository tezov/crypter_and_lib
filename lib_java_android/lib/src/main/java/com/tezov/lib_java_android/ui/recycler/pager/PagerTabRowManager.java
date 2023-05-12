/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.recycler.pager;

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

import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayoutMediator;
import com.tezov.lib_java.async.notifier.Notifier;
import com.tezov.lib_java.async.notifier.observable.ObservableEvent;
import com.tezov.lib_java.async.notifier.observer.event.ObserverEvent;
import com.tezov.lib_java_android.toolbox.PostToHandler;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.type.ref.Ref;
import com.tezov.lib_java.type.ref.WR;
import com.tezov.lib_java.type.runnable.RunnableW;
import com.tezov.lib_java_android.wrapperAnonymous.PagerTabLayoutStrategyW;
import com.tezov.lib_java_android.ui.layout.TabLayout;
import com.tezov.lib_java_android.ui.recycler.RecyclerListDataManager;
import com.tezov.lib_java_android.ui.recycler.RecyclerListRowBinder;
import com.tezov.lib_java_android.ui.recycler.RecyclerListRowHolder;
import com.tezov.lib_java_android.ui.recycler.RecyclerListRowManager;
import com.tezov.lib_java_android.ui.recycler.RecyclerListSmoothScroller;

import static com.tezov.lib_java_android.ui.recycler.RecyclerList.Event;
import static com.tezov.lib_java_android.ui.recycler.RecyclerList.PositionSnap.CENTER;

public abstract class PagerTabRowManager<TYPE> extends RecyclerListRowManager<TYPE>{
private final WR<ViewPager2> viewPagerWR;
private final WR<TabLayout> tabsWR;
private final Notifier<Event.Is> notifier;
private RecyclerListSmoothScroller smoothScroller = null;

public PagerTabRowManager(ViewPager2 viewPager, TabLayout tabs){
    this(viewPager, tabs, new RecyclerListDataManager<TYPE>(null){
        @Override
        public int size(){
            return tabs.getSize();
        }
        @Override
        public TYPE get(int index){
            return null;
        }
    });
}
public PagerTabRowManager(ViewPager2 viewPager, TabLayout tabs, RecyclerListDataManager<TYPE> dataManager){
    super(dataManager);
    this.viewPagerWR = WR.newInstance(viewPager);
    this.tabsWR = WR.newInstance(tabs);
    dataManager.attach(this);
    viewPager.setAdapter(this);
    ObservableEvent<Event.Is, Object> observable = new ObservableEvent<>();
    notifier = new Notifier<>(observable, false);
    viewPager.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener(){
        @Override
        public void onViewAttachedToWindow(View v){

        }
        @Override
        public void onViewDetachedFromWindow(View v){
            dataManager.unObserveAll();
            viewPager.setAdapter(null);
        }
    });
}

private PagerTabRowManager<TYPE> me(){
    return this;
}

public Notifier.Subscription observe(ObserverEvent<Event.Is, Object> observer){
    return notifier.register(observer);
}

public void unObserve(Object owner){
    notifier.unregister(owner);
}

public void post(Event.Is event, Object object){
    ObservableEvent<Event.Is, Object>.Access access = notifier.obtainAccess(this, event);
    access.setValue(object);
}

public void postIfDifferent(Event.Is event, Object object){
    ObservableEvent<Event.Is, Object>.Access access = notifier.obtainAccess(this, event);
    access.setValueIfDifferent(object);
}

public ViewPager2 getViewPager(){
    return Ref.get(viewPagerWR);
}

public void setEnable(boolean flag){
    PostToHandler.of(getViewPager(), new RunnableW(){
        @Override
        public void runSafe(){
            getViewPager().setUserInputEnabled(flag);
        }
    });
    PostToHandler.of(getTabLayout(), new RunnableW(){
        @Override
        public void runSafe(){
            getTabLayout().setClickable(flag);
        }
    });
}
public boolean isEnabled(){
    return getViewPager().isUserInputEnabled();
}

public <T extends TabLayout> T getTabLayout(){
    return (T)Ref.get(tabsWR);
}

@Override
final public RecyclerListRowManager add(RecyclerListRowBinder<? extends RecyclerView.ViewHolder, TYPE> viewBinder){

DebugException.start().explode("Please use RecyclerListRowManager add(PagerTabRowBinder<? extends RecyclerView.ViewHolder, TYPE> viewBinder) instead").end();

    return null;
}

public RecyclerListRowManager add(PagerTabRowBinder<? extends RecyclerView.ViewHolder, TYPE> viewBinder){
    return super.add(viewBinder);
}

@Override
public void onViewAttachedToWindow(@NonNull RecyclerListRowHolder<TYPE> newHolder){
    super.onViewAttachedToWindow(newHolder);
    if(Ref.isNotNull(tabsWR)){
        attachMediator(getViewPager(), getTabLayout());
    }
}

private void attachMediator(ViewPager2 viewPager, TabLayout tabs){
    new TabLayoutMediator(tabs, viewPager, new PagerTabLayoutStrategyW(){
        @Override
        public void onConfigureTab(@NonNull TabLayout.Tab tab, int position){
            me().onConfigureTab(tab, position);
        }
    }).attach();
}

protected void onConfigureTab(@NonNull TabLayout.Tab tab, int position){
    PagerTabRowBinder rowBinder = getRowBinder(getItemViewType(position));
    String title = rowBinder.getTitle();
    if(title == null){
        title = getTabLayout().getTitle(position);
    }
    tab.setText(title);
    Drawable icon = rowBinder.getIcon();
    if(icon == null){
        icon = getTabLayout().getIcon(position);
    }
    tab.setIcon(icon);
}

public <R extends RecyclerListRowHolder> R findRowHolder(int position){
    RecyclerView recyclerView = getRecyclerView();
    for(int childCount = recyclerView.getChildCount(), i = 0; i < childCount; ++i){
        RecyclerView.ViewHolder holder = recyclerView.getChildViewHolder(recyclerView.getChildAt(i));
        if(holder.getAbsoluteAdapterPosition() == position){
            return (R)holder;
        }
    }
    return null;
}

public <R extends RecyclerListRowHolder> R findRowHolder(View view){
    RecyclerView recyclerView = getRecyclerView();
    for(int childCount = recyclerView.getChildCount(), i = 0; i < childCount; ++i){
        RecyclerView.ViewHolder holder = recyclerView.getChildViewHolder(recyclerView.getChildAt(i));
        if(holder.itemView == view){
            return (R)holder;
        }
    }
    return null;
}

private RecyclerListSmoothScroller newSmoothScroller(){
    return new RecyclerListSmoothScroller(getRecyclerView().getContext());
}

public void scrollToPosition(int position){
    if((smoothScroller != null) && smoothScroller.isRunning()){
        getRecyclerView().stopScroll();
        smoothScroller.stopScroll();
    }
    smoothScroller = newSmoothScroller();
    smoothScroller.scrollToPosition(getRecyclerView(), CENTER, position);
}

}
