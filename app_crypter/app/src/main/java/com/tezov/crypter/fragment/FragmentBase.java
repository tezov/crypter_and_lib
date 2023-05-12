/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.crypter.fragment;

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
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.tezov.crypter.activity.ActivityMain;
import com.tezov.crypter.navigation.ToolbarContent;
import com.tezov.crypter.navigation.ToolbarHeaderBuilder;
import com.tezov.lib_java_android.application.AppResources;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java_android.ui.fragment.FragmentMenu;
import com.tezov.lib_java_android.ui.fragment.FragmentNavigable;

public abstract class FragmentBase extends FragmentMenu{

@Override
protected State newState(){
    return new State();
}
@Override
public State getState(){
    return super.getState();
}
@Override
public State obtainState(){
    return super.obtainState();
}

@Override
public Param getParam(){
    return super.getParam();
}
@Override
public Param obtainParam(){
    return super.obtainParam();
}

protected <DATA> void setToolbarTittle(DATA data){
    obtainParam().setTitleData(data);
    ActivityMain activity = (ActivityMain)getActivity();
    ToolbarContent toolbarContent = activity.getToolbarContent();
    if(data == null){
        toolbarContent.setToolBarView(null);
    } else {
        ToolbarHeaderBuilder header = new ToolbarHeaderBuilder().setData(data);
        toolbarContent.setToolBarView(header.build(activity.getToolbar()));
    }
}

@Nullable
@Override
public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
    return super.onCreateView(inflater, container, savedInstanceState);
}

public static class State extends FragmentMenu.State{
    @Override
    protected Param newParam(){
        return new Param();
    }
}
public static class Param extends FragmentMenu.Param{
    public Object titleData = null;
    public <DATA> DATA getTitleData(){
        return (DATA)titleData;
    }
    public <DATA> Param setTitleData(DATA data){
        this.titleData = data;
        return this;
    }
}

}
