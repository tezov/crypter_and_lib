/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.navigation.navigator;

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

import com.tezov.lib_java_android.application.Application;
import com.tezov.lib_java.toolbox.Compare;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java_android.ui.navigation.NavigationOption;
import com.tezov.lib_java_android.ui.navigation.NavigatorManager;
import com.tezov.lib_java_android.ui.navigation.defNavigable;
import com.tezov.lib_java_android.ui.navigation.destination.DestinationManager;
import com.tezov.lib_java_android.ui.navigation.stack.Stack;
import com.tezov.lib_java_android.ui.navigation.stack.StackEntry;

public abstract class Navigator{
public static final String KEY_BIND_ID = "KEY_BIND_ID";

private final NavigatorManager.NavigatorKey.Is navigatorKey;
private DestinationManager destinationManager = null;
private NavigateType currentNavType = null;
private Long currentNavID = null;
private NavigationOption option = null;

public Navigator(NavigatorManager.NavigatorKey.Is navigatorKey){
DebugTrack.start().create(this).end();
    this.navigatorKey = navigatorKey;
}

public static DestinationManager destinationManager(){
    return Application.navigationHelper().getDestinationManager();
}
public static Navigator getNavigator(NavigatorManager.NavigatorKey.Is navigatorKey){
    return Application.navigationHelper().getNavigator(navigatorKey);
}
public static StackEntry.Step getStepLife(defNavigable navigable){
    DestinationManager destinationManager = destinationManager();
    if(destinationManager == null){
        return null;
    } else {
        return destinationManager.refStepLife(navigable);
    }
}
public static Boolean hasBeenRestarted(defNavigable navigable){
    DestinationManager destinationManager = destinationManager();
    if(destinationManager == null){
        return null;
    } else {
        return destinationManager.refHasBeenRestarted(navigable);
    }
}
public static Boolean hasBeenReconstructed(defNavigable navigable){
    DestinationManager destinationManager = destinationManager();
    if(destinationManager == null){
        return null;
    } else {
        return destinationManager.refHasBeenReconstructed(navigable);
    }
}
public static void bind(defNavigable navigable){
    DestinationManager destinationManager = destinationManager();
    if(destinationManager != null){
        destinationManager.refBind(navigable);
    }
}
public static void confirmConstructed(NavigatorManager.NavigatorKey.Is navigatorKey, StackEntry current){
    Navigator navigator = getNavigator(navigatorKey);
    if(navigator != null){
        navigator.confirmConstructed(current);
    }
}
public static void confirmDestroyed(NavigatorManager.NavigatorKey.Is navigatorKey, StackEntry previous){
    Navigator navigator = getNavigator(navigatorKey);
    if(navigator != null){
        navigator.confirmDestroyed(previous);
    }
}
public void attach(DestinationManager destinationManager){
    this.destinationManager = destinationManager;
}
public NavigatorManager.NavigatorKey.Is getKey(){
    return navigatorKey;
}
public NavigatorManager getNavigatorManager(){
    return destinationManager.getNavigatorManager();
}
protected Stack stack(){
    return destinationManager.getStack();
}
public void setNav(NavigateType type, Long id, NavigationOption option){
    this.currentNavType = type;
    this.currentNavID = id;
    this.option = option;
}
public void clearNav(){
    this.currentNavID = null;
    this.option = null;
}
public NavigateType getNavType(){
    return currentNavType;
}
public Long getNavID(){
    return currentNavID;
}
public NavigationOption getNavOption(){
    return option;
}
public abstract boolean navigate(NavigateType type, StackEntry current, StackEntry target, NavigationOption option);
public abstract void onBind(StackEntry current);
public void confirmConstructed(StackEntry e){
    if((getNavType() == NavigateType.TO) && Compare.equals(getNavID(), e.getBindID())){
        onConfirmConstructed(e, getNavOption());
        clearNav();
        getNavigatorManager().confirmNavigate(NavigateType.TO, this);
    }
}
protected void onConfirmConstructed(StackEntry current, NavigationOption navOption){

}
public void confirmDestroyed(StackEntry e){
    if(((getNavType() == NavigateType.BACK) || (getNavType() == NavigateType.CLOSE)) && Compare.equals(getNavID(), e.getBindID())){
        onConfirmDestroyed(e, getNavOption());
        clearNav();
        getNavigatorManager().confirmNavigate(getNavType(), this);
    }
}
protected void onConfirmDestroyed(StackEntry destroyed, NavigationOption navOption){
}
@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

public enum NavigateType{
    TO, BACK, CLOSE
}

}
