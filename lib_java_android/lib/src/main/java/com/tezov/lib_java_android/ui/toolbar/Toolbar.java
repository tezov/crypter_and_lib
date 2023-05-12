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
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import static com.tezov.lib_java.type.defEnum.Event.ON_SELECT;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;

import com.google.android.material.appbar.MaterialToolbar;
import com.tezov.lib_java_android.application.AppContext;
import com.tezov.lib_java.application.AppUIDGenerator;
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
import com.tezov.lib_java_android.ui.activity.ActivityBase;
import com.tezov.lib_java_android.ui.navigation.defMenuListener;
import com.tezov.lib_java_android.ui.toolbar.behavior.top.BehaviorTopViewSlideBase;

public class Toolbar extends MaterialToolbar{
private final static int CONTENT_VIEW_ID = AppUIDGenerator.nextInt();
private WR<Object> spyWR = null;
private Notifier<Event.Is> notifier;

public Toolbar(Context context){
    super(context);
    init();
}

public Toolbar(Context context, @Nullable AttributeSet attrs){
    super(context, attrs);
    init();
}

public Toolbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr){
    super(context, attrs, defStyleAttr);
    init();
}

private void init(){
DebugTrack.start().create(this).end();
    notifier = new Notifier<>(new ObservableEvent<Event.Is, Object>(), false);
}

public boolean onMenuItemClick(MenuItem menuItem){
    if(notifier.hasObserver(ON_SELECT)){
        post(ON_SELECT, menuItem);
    }
    Object spy = Ref.get(spyWR);
    if(spy instanceof defMenuListener){
        defMenuListener widget = (defMenuListener)spy;
        if(widget.onMenuItemSelected(defMenuListener.Type.TOOLBAR, menuItem)){
            return true;
        }
    }
    ActivityBase activity = AppContext.getActivity();
    if(activity instanceof defMenuListener){
        defMenuListener widget = ((defMenuListener)activity);
        return widget.onMenuItemSelected(defMenuListener.Type.TOOLBAR, menuItem);
    }
    return false;
}

public void attach(Object spy){
    detach();
    spyWR = WR.newInstance(spy);
}

protected void detach(){
    if(Ref.isNotNull(spyWR)){
        detach(spyWR.get());
    }
}

public void detach(Object spy){
    if(Ref.isNotNull(spyWR)){
        if(!spyWR.equals(spy)){
DebugException.start()
                    .log("Try to detach but " + DebugTrack.getFullSimpleNameWithHashcode(spy) + " is not the spy(" + DebugTrack.getFullSimpleNameWithHashcode(spyWR.get()) + ")")
              
                    .end();
            return;
        }
        unObserve(spy);
        spyWR = null;
    }
}

public Notifier.Subscription observe(ObserverEvent<Event.Is, Object> callbackEvent){
    return notifier.register(callbackEvent);
}

public void unObserve(Object spy){
    notifier.unregister(spy);
}

public void unObserveAll(){
    notifier.unregisterAll();
}

public void postValue(Object object){
    post(null, object);
}

public void postEvent(Event.Is event){
    post(event, null);
}

public void post(Event.Is event, Object object){
    ObservableEvent<Event.Is, Object>.Access access = notifier.obtainAccess(this, event);
    access.setValue(object);
}

public void postValueIfDifferent(Object object){
    postIfDifferent(null, object);
}

public void postIfDifferent(Event.Is event, Object object){
    ObservableEvent<Event.Is, Object>.Access access = notifier.obtainAccess(this, event);
    access.setValueIfDifferent(object);
}

public BehaviorTopViewSlideBase getBehavior(){
    CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams)getLayoutParams();
    return (BehaviorTopViewSlideBase)params.getBehavior();
}
public void slideVisible(boolean flag){
    if(flag){
        getBehavior().slideDown(this);
    }
    else{
        getBehavior().slideUp(this);
    }
}
public void setVisible(boolean flag){
    if(flag){
        setTranslationY(getLayoutParams().height);
        getBehavior().slideDown(this);
    }
    else{
        setTranslationY(0);
        getBehavior().slideUp(this);
    }
}

public void clearMenu(){
    getMenu().clear();
}
@Override
public void inflateMenu(int resId){
    clearMenu();
    super.inflateMenu(resId);
}
public View getContentView(){
    return findViewById(CONTENT_VIEW_ID);
}
public void setContentView(View view){
    PostToHandler.of(this, new RunnableW(){
        @Override
        public void runSafe(){
            View oldView = findViewById(CONTENT_VIEW_ID);
            if(oldView != null){
                removeView(oldView);
            }
            if(view != null){
                view.setId(CONTENT_VIEW_ID);
                addView(view);
            }
        }
    });
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
