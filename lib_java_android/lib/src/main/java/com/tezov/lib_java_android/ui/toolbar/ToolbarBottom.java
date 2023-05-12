/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.toolbar;

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
import com.tezov.lib_java_android.application.AppContext;
import com.tezov.lib_java_android.ui.activity.ActivityBase;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.tezov.lib_java.async.notifier.Notifier;
import com.tezov.lib_java.async.notifier.observable.ObservableEvent;
import com.tezov.lib_java.async.notifier.observer.event.ObserverEvent;
import com.tezov.lib_java_android.toolbox.PostToHandler;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.defEnum.Event;
import com.tezov.lib_java.type.ref.Ref;
import com.tezov.lib_java.type.ref.WR;
import com.tezov.lib_java.type.runnable.RunnableW;
import com.tezov.lib_java_android.ui.toolbar.behavior.bottom.BehaviorBottomViewSlideBase;
import com.tezov.lib_java_android.wrapperAnonymous.BottomNavigationViewOnItemSelectedListenerW;
import com.tezov.lib_java_android.ui.navigation.defMenuListener;

import static com.tezov.lib_java.type.defEnum.Event.ON_SELECT;

public class ToolbarBottom extends BottomNavigationView{
private WR<Object> spyWR = null;
private Notifier<Event.Is> notifier;

public ToolbarBottom(Context context){
    super(context);
    init();
}

public ToolbarBottom(Context context, @Nullable AttributeSet attrs){
    super(context, attrs);
    init();
}

public ToolbarBottom(Context context, @Nullable AttributeSet attrs, int defStyleAttr){
    super(context, attrs, defStyleAttr);
    init();
}

private void init(){
DebugTrack.start().create(this).end();
    notifier = new Notifier<>(new ObservableEvent<Event.Is, Object>(), false);
    super.setOnItemSelectedListener(new BottomNavigationViewOnItemSelectedListenerW(){
        @Override
        public boolean onNavigationItemSelected(MenuItem menuItem){
            if(notifier.hasObserver(ON_SELECT)){
                post(ON_SELECT, menuItem);
            }
            Object spy = Ref.get(spyWR);
            if(spy instanceof defMenuListener){
                defMenuListener widget = (defMenuListener)spy;
                if(widget.onMenuItemSelected(defMenuListener.Type.TOOLBAR_BOTTOM, menuItem)){
                    return true;
                }
            }
            ActivityBase activity = AppContext.getActivity();
            if(activity instanceof defMenuListener){
                defMenuListener widget = ((defMenuListener)activity);
                return widget.onMenuItemSelected(defMenuListener.Type.TOOLBAR_BOTTOM, menuItem);
            }
            return false;
        }
    });
}

public void attach(Object spy){
    detach();
    spyWR = WR.newInstance(spy);
}

protected void detach(){
    if(Ref.isNull(spyWR)){
        return;
    }
    detach(spyWR.get());
}

public void detach(Object spy){
    if(Ref.isNull(spyWR)){
        return;
    }

    if(!spyWR.get().equals(spy)){
DebugException.start()
                .log("Try to detach but " + DebugTrack.getFullSimpleNameWithHashcode(spy) + " is not the spy(" + DebugTrack.getFullSimpleNameWithHashcode(spyWR.get()) + ")")
        
                .end();
        return;
    }


    unObserve(spy);
    spyWR = null;
}

public Notifier.Subscription observe(ObserverEvent<Event.Is, Object> observer){
    return notifier.register(observer);
}

public void unObserve(Object spy){
    notifier.unregister(spy);
}

public void unObserveAll(){
    notifier.unregisterAll();
}

public void post(Event.Is event, Object object){
    ObservableEvent<Event.Is, Object>.Access access = notifier.obtainAccess(this, event);
    access.setValue(object);
}

public void clearMenu(){
    getMenu().clear();
}
@Override
public void inflateMenu(int resId){
    inflateMenu(resId, null);
}
public void inflateMenu(int resId, Integer setSelectedItemId){
    clearMenu();
    PostToHandler.of(this,  new RunnableW(){
        @Override
        public void runSafe(){
            inflateMenuSuper(resId);
        }
    });
    if(setSelectedItemId != null){
        setChecked(setSelectedItemId);
    }
}
public void setChecked(int id){
    PostToHandler.of(this,  new RunnableW(){
        @Override
        public void runSafe(){
            Menu menu = getMenu();
            MenuItem item = menu.findItem(id);
            if(item != null){
                item.setChecked(true);
            }
        }
    });

}

private void inflateMenuSuper(int resId){
    super.inflateMenu(resId);
}

public BehaviorBottomViewSlideBase getBehavior(){
    CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams)getLayoutParams();
    return (BehaviorBottomViewSlideBase)params.getBehavior();
}
public void slideVisible(boolean flag){
    if(flag){
        getBehavior().slideUp(this);
    }
    else{
        getBehavior().slideDown(this);
    }
}
public void setVisible(boolean flag){
    if(flag){
        setTranslationY(0);
        getBehavior().slideUp(this);
    }
    else{
        int height = getLayoutParams().height;
        setTranslationY(height);
        if(getHeight() > 0){
            getBehavior().slideDown(this);
        }
        else{
            getBehavior().setAdditionalHiddenOffsetY(this, height);
            getBehavior().slideDown(this);
            getBehavior().setAdditionalHiddenOffsetY(this, 0);
        }
    }
}

@Override
protected void onDetachedFromWindow(){
    notifier.unregisterAll();
    super.onDetachedFromWindow();
}

@Override
protected void finalize() throws Throwable{
    detach();
DebugTrack.start().destroy(this).end();
    super.finalize();
}

}
