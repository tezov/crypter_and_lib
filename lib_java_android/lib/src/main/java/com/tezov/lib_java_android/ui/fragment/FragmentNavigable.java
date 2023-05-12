/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.fragment;

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
import com.tezov.lib_java.debug.DebugLog;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java_android.ui.navigation.NavigationArguments;
import com.tezov.lib_java_android.ui.navigation.defNavigable;
import com.tezov.lib_java_android.ui.navigation.navigator.Navigator;
import com.tezov.lib_java_android.ui.navigation.navigator.NavigatorFragment;
import com.tezov.lib_java_android.ui.navigation.stack.StackEntry;

import static com.tezov.lib_java_android.ui.navigation.NavigatorManager.NavigatorKey.FRAGMENT;

public abstract class FragmentNavigable extends FragmentBase implements defNavigable{
private Long bindID = null;
private State state = null;

protected <S extends State> S newState(){
    return (S)new State();
}
public boolean hasState(){
    return getState() != null;
}
private void makeState(Param param){
    state = newState();
    state.attach(this);
    if(param != null){
        if(!state.hasParam()){
            state.setParam(param);
        } else {
DebugException.start().explode("Step has already Param").end();
        }

    }
}
public <S extends State> S obtainState(){
    if(!hasState()){
        makeState(null);
    }
    return getState();
}
public <S extends State> S getState(){
    return (S)state;
}
protected void onRestoreState(){
}
final public void restoreState(State state){
    if(state != null){
        this.state = state;
        state.attach(this);
        onRestoreState();
    }
}

public boolean hasParam(){
    return hasState() && getState().hasParam();
}
public <P extends Param> P obtainParam(){
    return (P)obtainState().obtainParam();
}
public <P extends Param> P getParam(){
    if(!hasState()){
        return null;
    }
    else{
        return (P)getState().getParam();
    }
}
public void setParam(Param param){
    if(hasState()){
        getState().setParam(param);
    } else {
        makeState(param);
    }
}

public boolean hasMethod(){
    return hasState() && getState().hasMethod();
}
public <M extends Method> M obtainMethod(){
    return (M)obtainState().obtainMethod();
}
public <M extends Method> M getMethod(){
    if(!hasState()){
        return null;
    }
    return (M)getState().getMethod();
}

public StackEntry.Step getStepLife(){
    return NavigatorFragment.getStepLife(this);
}

public boolean hasBeenRestarted(){
    return NavigatorFragment.hasBeenRestarted(this);
}

public boolean hasBeenReconstructed(){
    return NavigatorFragment.hasBeenReconstructed(this);
}

@Override
public Long getBindID(){
    return bindID;
}

@Override
public void setBindID(Long id){
    bindID = id;
}

@Override
public void onCreate(@Nullable Bundle savedInstanceState){
    super.onCreate(savedInstanceState);
    if(savedInstanceState != null){
        bindID = savedInstanceState.getLong(Navigator.KEY_BIND_ID);
    }
    NavigatorFragment.bind(this);
    NavigationArguments navigationArguments = NavigationArguments.get(this);
    if(navigationArguments.exist()){
        restoreState(navigationArguments.getState());
    }
}
@Override
public void onPrepare(boolean hasBeenReconstructed){
DebugLog.start().track(this, hasBeenReconstructed ? " reconstructed" : null).end();
}
@Override
public void onOpen(boolean hasBeenReconstructed, boolean hasBeenRestarted){
DebugLog.start().track(this, (hasBeenReconstructed ? " reconstructed" : null) + "-" + (hasBeenRestarted ? " restarted" : null)).end();
}
@Override
public void onSaveInstanceState(Bundle savedInstanceState){
    super.onSaveInstanceState(savedInstanceState);
    savedInstanceState.putLong(Navigator.KEY_BIND_ID, bindID);
    if(hasState()){
        NavigationArguments.obtain(this).setState(getState());
    }
}

@Override
public void onDetach(){
    if(hasState()){
        state.detach(this);
    }
    super.onDetach();
}

@Override
public boolean requestViewUpdate(Integer what, NavigationArguments arg){
    return false;
}
@Override
public boolean onNewNavigationArguments(NavigationArguments arg){
    return false;
}

@Override
public void confirmConstructed(StackEntry entry){
    NavigatorFragment.confirmConstructed(FRAGMENT, entry);
}

@Override
public void confirmDestroyed(StackEntry entry){
    NavigatorFragment.confirmDestroyed(FRAGMENT, entry);
}

@Override
public void removedFromStack(){
DebugLog.start().track(this).end();
}

public boolean onBackPressed(){
    return false;
}

public static class State extends com.tezov.lib_java_android.ui.state.State<Param, Method>{
    @Override
    protected Param newParam(){
        return new Param();
    }
    @Override
    protected Method newMethod(){
        return new Method();
    }

}
public static class Param extends com.tezov.lib_java_android.ui.state.Param{}
public static class Method extends com.tezov.lib_java_android.ui.state.Method{}

}

