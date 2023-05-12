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

import com.tezov.lib_java.debug.DebugException;

import androidx.annotation.NonNull;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;

import com.tezov.lib_java_android.R;
import com.tezov.lib_java.application.AppUUIDGenerator;
import com.tezov.lib_java_android.application.Application;
import com.tezov.lib_java.generator.uid.defUid;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java_android.ui.fragment.FragmentNavigable;
import com.tezov.lib_java_android.ui.navigation.NavigationHelper;
import com.tezov.lib_java_android.ui.navigation.defMenuListener;
import com.tezov.lib_java_android.ui.toolbar.Toolbar;
import com.tezov.lib_java_android.ui.toolbar.ToolbarBottom;
import com.tezov.lib_java_android.ui.toolbar.ToolbarCollapsible;

import static com.tezov.lib_java_android.ui.navigation.NavigatorManager.NavigatorKey.FRAGMENT;
import static com.tezov.lib_java_android.ui.navigation.defMenuListener.Type.TOOLBAR;
import static com.tezov.lib_java_android.ui.navigation.defMenuListener.Type.TOOLBAR_BOTTOM;


public abstract class ActivityToolbar extends ActivityNavigable implements defMenuListener{
protected Toolbar toolbar = null;
protected ToolbarCollapsible toolbarCollapsible = null;
protected ToolbarBottom toolbarBottom = null;
protected defUid navigateObserverOwner = AppUUIDGenerator.next();

public Toolbar getToolbar(){
    return toolbar;
}
public ToolbarCollapsible getToolBarCollapsible(){
    return toolbarCollapsible;
}
public ToolbarBottom getToolbarBottom(){
    return toolbarBottom;
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

protected <N extends NavigationHelper> N navigationHelper(){
    return (N)Application.navigationHelper();
}

@Override
protected void onCreate(Bundle savedInstanceState){
    super.onCreate(savedInstanceState);
    toolbarCollapsible = findViewById(R.id.toolbar_collapsible);
    toolbarBottom = findViewById(R.id.toolbar_bottom);
    toolbar = findViewById(R.id.toolbar);
    toolbar.setTitle("");
    setSupportActionBar(toolbar);
}

@Override
public <T> T getMenuHolder(Type uiType){
    if(uiType == TOOLBAR){
        return (T)toolbar;
    }
    else if(uiType == TOOLBAR_BOTTOM){
        return (T)toolbarBottom;
    }
    else{
DebugException.start().unknown("uiType",uiType).end();
        return null;    }

}
@Override
final public boolean onCreateOptionsMenu(Menu menu){
    return onCreateMenu();
}
protected boolean onCreateMenu(){
    return false;
}

@Override
final public boolean onOptionsItemSelected(@NonNull MenuItem item){
    return toolbar.onMenuItemClick(item);
}

@Override
public boolean onMenuItemSelected(Type uiType, Object object){
    return false;
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
