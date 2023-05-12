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

import static com.tezov.lib_java.type.defEnum.Event.CLOSED;
import static com.tezov.lib_java.type.defEnum.Event.ON_CLOSE;
import static com.tezov.lib_java.type.defEnum.Event.ON_OPEN;
import static com.tezov.lib_java.type.defEnum.Event.ON_SELECT;
import static com.tezov.lib_java.type.defEnum.Event.ON_SLIDE;
import static com.tezov.lib_java.type.defEnum.Event.OPENED;
import static com.tezov.lib_java_android.util.UtilsView.Direction.DOWN;

import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.tezov.lib_java_android.R;
import com.tezov.lib_java_android.application.AppContext;
import com.tezov.lib_java.async.notifier.Notifier;
import com.tezov.lib_java.async.notifier.observable.ObservableEvent;
import com.tezov.lib_java.async.notifier.observer.event.ObserverEvent;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java_android.wrapperAnonymous.NavigationViewOnItemSelectedListenerW;
import com.tezov.lib_java_android.wrapperAnonymous.ViewOnClickListenerW;
import com.tezov.lib_java.type.defEnum.Event;
import com.tezov.lib_java.type.ref.Ref;
import com.tezov.lib_java.type.ref.SRwO;
import com.tezov.lib_java.type.ref.WR;
import com.tezov.lib_java_android.ui.activity.ActivityBase;
import com.tezov.lib_java_android.ui.activity.ActivityNavigable;
import com.tezov.lib_java_android.ui.activity.ActivityToolbar;
import com.tezov.lib_java_android.ui.component.plain.Switch;
import com.tezov.lib_java_android.ui.navigation.defMenuListener;
import com.tezov.lib_java_android.util.UtilsView;

public class Drawer extends DrawerLayout{
protected DrawerStateListener drawerStateListener = null;
protected NavigationView navigationView = null;
protected boolean autoCloseOnItemSelected = true;
protected boolean enableDrawerButton = true;
private WR<Object> spyWR = null;
private Notifier<Event.Is> notifier;

public Drawer(@NonNull android.content.Context context){
    super(context);
    init();
}

public Drawer(@NonNull android.content.Context context, @Nullable AttributeSet attrs){
    super(context, attrs);
    init();
}

public Drawer(@NonNull android.content.Context context, @Nullable AttributeSet attrs, int defStyle){
    super(context, attrs, defStyle);
    init();
}

private void init(){
DebugTrack.start().create(this).end();
    notifier = new Notifier<>(new ObservableEvent<Event.Is, Object>(), false);
}

public void setAutoCloseOnItemSelected(boolean autoCloseOnItemSelected){
    this.autoCloseOnItemSelected = autoCloseOnItemSelected;
}

public void init(ActivityToolbar activity){
    ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(activity, this, activity.getToolbar(), R.string.navigation_open_drawer, R.string.navigation_close_drawer);
    addDrawerListener(actionBarDrawerToggle);
    drawerStateListener = new DrawerStateListener(this);
    addDrawerListener(drawerStateListener);
    actionBarDrawerToggle.syncState();
    actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
    DrawerArrowDrawable navigationButton = new DrawerArrowDrawable(activity);
    actionBarDrawerToggle.setHomeAsUpIndicator(navigationButton);
    actionBarDrawerToggle.setToolbarNavigationClickListener(new ViewOnClickListenerW(){
        @Override
        public void onClicked(View v){
            if(enableDrawerButton){
                toggle();
            }
        }
    });
}
public boolean isOpen(){
    return drawerStateListener.getPosition() == OPENED;
}
public boolean isOpening(){
    return drawerStateListener.getPosition() == ON_OPEN;
}
public boolean isClose(){
    return drawerStateListener.getPosition() == CLOSED;
}
public boolean isClosing(){
    return drawerStateListener.getPosition() == ON_CLOSE;
}
public boolean isSliding(){
    return drawerStateListener.getPosition() == ON_SLIDE;
}
public void close(){
    closeDrawer(GravityCompat.START);
}
public void open(){
    openDrawer(GravityCompat.START);
}
public void toggle(){
    if(isOpen()){
        close();
    } else {
        open();
    }
}
public void enableDrag(boolean flag){
    if(flag){
        setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, GravityCompat.START);
    } else if(isClose() || isClosing()){
        setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.START);
    } else if(isOpen() || isOpening()){
        setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN, GravityCompat.START);
    }
}
public void enableButton(boolean flag){
    enableDrawerButton = flag;
}
public void enable(boolean flag){
    enableDrag(flag);
    enableButton(flag);
}

public NavigationView getNavigationView(){
    return navigationView;
}

public void setNavigationView(NavigationView drawerNavigationView){
    this.navigationView = drawerNavigationView;
    drawerNavigationView.setNavigationItemSelectedListener(new NavigationViewOnItemSelectedListenerW(){
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem){
            if(autoCloseOnItemSelected){
                Drawer.this.closeDrawer(GravityCompat.START);
            }
            if(notifier.hasObserver(ON_SELECT)){
                post(ON_SELECT, menuItem);
            }
            Object spy = Ref.get(spyWR);
            if(spy instanceof defMenuListener){
                defMenuListener widget = (defMenuListener)spy;
                if(widget.onMenuItemSelected(defMenuListener.Type.DRAWER, menuItem)){
                    return true;
                }
            }
            ActivityBase activity = AppContext.getActivity();
            if(activity instanceof defMenuListener){
                defMenuListener widget = ((defMenuListener)activity);
                return widget.onMenuItemSelected(defMenuListener.Type.DRAWER, menuItem);
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

    if(!spyWR.equals(spy)){
DebugException.start()
                .log("Try to detach but " + DebugTrack.getFullSimpleNameWithHashcode(spy) + " is not the owner(" + DebugTrack.getFullSimpleNameWithHashcode(spyWR.get()) + ")")
        
                .end();
        return;
    }


    unObserve(spy);
    spyWR = null;
}

public Notifier.Subscription observe(ObserverEvent<Event.Is, Object> callbackEvent){
    return notifier.register(callbackEvent);
}

protected void unObserve(Object spy){
    notifier.unregister(spy);
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

public Menu getMenu(){
    return getNavigationView().getMenu();
}

public void clearMenu(){
    clearActionView();
    getMenu().clear();
    detach();
}
private void clearActionView(){
    Menu menu = getMenu();
    for(int i = 0; i < menu.size(); i++){
        switchClear(menu.getItem(i));
    }
}

public void inflateMenu(int id){
    clearMenu();
    getNavigationView().inflateMenu(id);
    initActionView();
}
private void initActionView(){
    Menu menu = getMenu();
    for(int menuSize = menu.size(), i = 0; i < menuSize; i++){
        MenuItem menuItem = menu.getItem(i);
        switchInit(menuItem);
        SubMenu subMenu = menuItem.getSubMenu();
        if(subMenu != null){
            for(int subMenuSize = subMenu.size(), j = 0; j < subMenuSize; j++){
                switchInit(subMenu.getItem(j));
            }
        }
    }
}

@Override
public boolean onInterceptTouchEvent(MotionEvent ev){
    if(super.onInterceptTouchEvent(ev)){
        return true;
    } else {
        ActivityNavigable activity = AppContext.getActivity();
        return activity.onTouchEvent(ev);
    }
}

public <C extends Class<O>, O extends View> O find(C type, MenuItem menuItem){
    View view = menuItem.getActionView();
    if(view == null){
        return null;
    }
    return UtilsView.findFirst(type, view, DOWN);
}

private void switchClear(MenuItem menuItem){
    Switch sw = find(Switch.class, menuItem);
    if(sw != null){
        sw.setOnClickListener(null);
        sw.setEntry(null);
    }
}
private void switchInit(MenuItem menuItem){
    Switch sw = find(Switch.class, menuItem);
    if(sw != null){
        sw.setOnClickListener(new ViewOnClickListenerW(){
            @Override
            public void onClicked(View v){
                if(autoCloseOnItemSelected){
                    Drawer.this.closeDrawer(GravityCompat.START);
                }
            }
        });
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

private static class DrawerStateListener implements DrawerLayout.DrawerListener{
    protected WR<Drawer> drawerWR;
    protected SRwO<Event.Is> position;
    protected float previousSlideOffset;

    public DrawerStateListener(Drawer drawer){
        this.drawerWR = WR.newInstance(drawer);
        previousSlideOffset = 0.0f;
        position = new SRwO<>();
        position.set(CLOSED);
    }

    public void post(Event.Is event, float dx){
        if(event != ON_SLIDE){
            if(position.setIfDifferent(event)){
                getDrawer().post(event, dx);
                position.checked();
            }
        } else {
            getDrawer().post(event, dx);
        }
    }

    public Drawer getDrawer(){
        return Ref.get(drawerWR);
    }

    public Event.Is getPosition(){
        return position.get();
    }

    @Override
    public void onDrawerSlide(@NonNull View drawerView, float slideOffset){
        float dx = previousSlideOffset - slideOffset;
        if(slideOffset == 0){
            post(CLOSED, slideOffset);
        } else {
            if(slideOffset == 1){
                post(OPENED, slideOffset);
            } else {
                if((dx < 0) && (position.get() == CLOSED)){
                    post(ON_OPEN, slideOffset);
                } else {
                    if((dx > 0) && (position.get() == OPENED)){
                        post(ON_CLOSE, slideOffset);
                    } else {
                        if(Math.abs(dx) > 0){
                            post(ON_SLIDE, slideOffset);
                        }
                    }
                }
            }
        }
        previousSlideOffset = slideOffset;
    }

    @Override
    public void onDrawerOpened(@NonNull View drawerView){
    }

    @Override
    public void onDrawerClosed(@NonNull View drawerView){
    }

    @Override
    public void onDrawerStateChanged(int newState){

    }

}

}
