/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.activity;

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

import static com.tezov.lib_java_android.application.AppResources.NULL_ID;
import static com.tezov.lib_java_android.ui.navigation.NavigatorManager.NavigatorKey.FRAGMENT;
import static com.tezov.lib_java_android.ui.navigation.defMenuListener.Type.DRAWER;

import android.os.Bundle;
import android.view.MotionEvent;

import com.google.android.material.navigation.NavigationView;
import com.tezov.lib_java_android.R;
import com.tezov.lib_java.application.AppUUIDGenerator;
import com.tezov.lib_java_android.application.Application;
import com.tezov.lib_java.generator.uid.defUid;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java_android.ui.fragment.FragmentNavigable;
import com.tezov.lib_java_android.ui.navigation.NavigationHelper;
import com.tezov.lib_java_android.ui.navigation.defMenuListener;
import com.tezov.lib_java_android.ui.toolbar.Drawer;
import com.tezov.lib_java_android.ui.toolbar.ToolbarCollapsible;

public abstract class ActivityDrawer extends ActivityToolbar implements defMenuListener{
protected Drawer drawer = null;
protected defUid navigateObserverOwner = AppUUIDGenerator.next();

public Drawer getDrawer(){
    return drawer;
}

@Override
protected <S extends ActivityNavigable.State> S newState(){
    return (S)new State();
}

@Override
protected void onRestoreState(){
    State state = getState();
    if(state.toolbarCollapsiblePosition != null){
        ToolbarCollapsible toolbarCollapsible = getToolBarCollapsible();
        if(toolbarCollapsible != null){
            toolbarCollapsible.setPosition(state.toolbarCollapsiblePosition);
        }
        state.toolbarCollapsiblePosition = null;
    }
}

@Override
protected void onSaveInstanceState(Bundle savedInstanceState){
    State state = obtainState();
    ToolbarCollapsible toolbarCollapsible = getToolBarCollapsible();
    if(toolbarCollapsible != null){
        state.toolbarCollapsiblePosition = toolbarCollapsible.getPosition();
    } else {
        state.toolbarCollapsiblePosition = null;
    }
    super.onSaveInstanceState(savedInstanceState);
}

protected int getDrawerHeaderResourceID(){
    return NULL_ID;
}

protected <N extends NavigationHelper> N navigationHelper(){
    return (N)Application.navigationHelper();
}

@Override
protected void onCreate(Bundle savedInstanceState){
    super.onCreate(savedInstanceState);
    drawer = findViewById(R.id.drawer);
    NavigationView nav = drawer.findViewById(R.id.nav_view);
    int headerResourceID = getDrawerHeaderResourceID();
    if(headerResourceID != -1){
        nav.inflateHeaderView(headerResourceID);
    }
    drawer.setNavigationView(nav);
    drawer.init(this);
}

@Override
public <T> T getMenuHolder(Type uiType){
    if(uiType == DRAWER){
        return (T)drawer;
    } else {
        return super.getMenuHolder(uiType);
    }

}

@Override
protected void onDestroy(){
    super.onDestroy();
    navigationHelper().unObserve(navigateObserverOwner);
}

@Override
public boolean onTouchEvent(MotionEvent event){
    FragmentNavigable fragment = navigationHelper().getLastRef(FRAGMENT, true);
    if(fragment == null){
        return false;
    }
    return fragment.onTouchEvent(event);
}

public static class State extends ActivityNavigable.State{
    Float toolbarCollapsiblePosition = null;

    @Override
    public DebugString toDebugString(){
        DebugString data = super.toDebugString();
        data.append("appBarLayoutPosition", toolbarCollapsiblePosition);
        return data;
    }

}

}
