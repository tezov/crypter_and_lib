/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.toolbar;

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

import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;

import com.tezov.lib_java.async.notifier.observer.event.ObserverEvent;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.defEnum.Event;
import com.tezov.lib_java.type.ref.Ref;
import com.tezov.lib_java.type.ref.WR;
import com.tezov.lib_java_android.ui.navigation.defMenuListener;

import static com.tezov.lib_java.type.defEnum.Event.ON_OPEN;

public abstract class DrawerMenuHelper implements defMenuListener{
private WR<Drawer> drawerWR;

public DrawerMenuHelper(){
DebugTrack.start().create(this).end();
}
protected DrawerMenuHelper me(){
    return this;
}
public Drawer getDrawer(){
    return Ref.get(drawerWR);
}
public DrawerMenuHelper setDrawer(Drawer drawer, int menuResourceId){
    drawerWR = WR.newInstance(drawer);
    drawer.inflateMenu(menuResourceId);
    drawer.attach(this);
    drawer.observe(new ObserverEvent<Event.Is, Object>(me(), ON_OPEN){
        @Override
        public void onComplete(Event.Is is, Object object){
            onOpen();
        }
    });
    return this;
}
private void onOpen(){
    Drawer drawer = getDrawer();
    Menu menu = drawer.getMenu();
    for(int menuSize = menu.size(), i = 0; i < menuSize; i++){
        MenuItem menuItem = menu.getItem(i);
        updateActionView(menuItem);
        SubMenu subMenu = menuItem.getSubMenu();
        if(subMenu != null){
            for(int subMenuSize = subMenu.size(), j = 0; j < subMenuSize; j++){
                updateActionView(subMenu.getItem(j));
            }
        }

    }
}

protected void updateActionView(MenuItem menuItem){

}

@Override
public boolean onMenuItemSelected(Type uiType, Object object){
    return false;
}
@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

}
