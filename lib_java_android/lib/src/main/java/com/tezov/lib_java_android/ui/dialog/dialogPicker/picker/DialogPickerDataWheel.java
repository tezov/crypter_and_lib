/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.dialog.dialogPicker.picker;

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.util.UtilsString;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.widget.NumberPicker;

import com.tezov.lib_java_android.R;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java_android.ui.dialog.dialogPicker.picker.base.DialogPickerBase;

import java.util.List;

public class DialogPickerDataWheel extends DialogPickerBase<String>{
protected NumberPicker picker;

@Override
protected State newState(){
    return new State();
}

@Override
public State getState(){
    return (State)super.getState();
}

@Override
public State obtainState(){
    return (State)super.obtainState();
}

@Override
public Param getParam(){
    return (Param)super.getParam();
}

@Override
public Param obtainParam(){
    return (Param)super.obtainParam();
}

@Override
public Class<String> getEntryType(){
    return String.class;
}

@Override
public void onPrepare(boolean hasBeenReconstructed){
    super.onPrepare(hasBeenReconstructed);
    picker = getContentView().findViewById(R.id.picker);
    picker.setMinValue(0);
    List<String> datas = getParam().dataList;
    picker.setMaxValue(datas.size() - 1);
    picker.setDisplayedValues(datas.toArray(new String[0]));
    if(hasBeenReconstructed){
        picker.setValue(getState().savedInstance);
        getState().savedInstance = null;
    }
}

@Override
public void onSaveInstanceState(Bundle savedInstanceState){
    super.onSaveInstanceState(savedInstanceState);
    getState().savedInstance = picker.getValue();
}

public int getIndex(){
    return picker.getValue();
}

@Override
public boolean setValue(String value){
    int index;
    if(value != null){
        List<String> datas = getParam().dataList;
        index = datas.indexOf(value);
        if(index == -1){
            index = 0;
        }
    } else {
        index = 0;
    }
    picker.setValue(index);
    return true;
}

@Override
public String getValue(){
    return getParam().dataList.get(getIndex());
}

@Override
public <T> boolean setValue(Class<T> type, T object){
    if(type == String.class){
        setValue((String)object);
        return true;
    } else {
DebugException.start().unknown("name", type.getName()).end();
        return false;
    }

}

@Override
public <T> T getValue(Class<T> type){
    if(type == getEntryType()){
        return (T)getValue();
    } else {

DebugException.start().unknown("name", type.getName()).end();

        return null;
    }
}

public static class State extends DialogPickerBase.State{
    Integer savedInstance = null;

    @Override
    protected Param newParam(){
        return new Param();
    }

    @Override
    public Param getParam(){
        return (Param)super.getParam();
    }

    @Override
    public Param obtainParam(){
        return (Param)super.obtainParam();
    }

}

public static class Param extends DialogPickerBase.Param{
    List<String> dataList = null;

    public Param(){
        setLayoutID(R.layout.dialog_picker_data_wheel);
    }

    public List<String> getDataList(){
        return dataList;
    }

    public Param setDataList(List<String> dataList){
        this.dataList = dataList;
        return this;
    }

    @Override
    public DebugString toDebugString(){
        DebugString data = super.toDebugString();
        data.append("dataDialogFilter", dataList);
        return data;
    }

}

}