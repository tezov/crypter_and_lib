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
import com.tezov.lib_java.util.UtilsString;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import androidx.fragment.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.widget.TimePicker;

import androidx.annotation.RequiresApi;

import com.tezov.lib_java_android.R;
import com.tezov.lib_java_android.application.VersionSDK;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java_android.ui.dialog.dialogPicker.picker.base.DialogPickerBase;

import org.threeten.bp.LocalTime;

import java.util.ArrayList;
import java.util.Arrays;

public class DialogPickerTime extends DialogPickerBase<LocalTime>{
private TimePicker picker;

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
public Class<LocalTime> getEntryType(){
    return LocalTime.class;
}

@Override
public void onPrepare(boolean hasBeenReconstructed){
    super.onPrepare(hasBeenReconstructed);
    picker = getContentView().findViewById(R.id.picker);
    if(hasBeenReconstructed){
        setValue(getState().savedInstance);
        getState().savedInstance = null;
    }
}

@Override
public void onSaveInstanceState(Bundle savedInstanceState){
    super.onSaveInstanceState(savedInstanceState);
    getState().savedInstance = getValue();
}

public ArrayList<Integer> getValues(){
    return new ArrayList<>(Arrays.asList(getHour(), getMinute()));
}

protected int getHour(){
    if(VersionSDK.isSupEqualTo23_MARSHMALLOW()){
        return getHour_after23_M();
    } else {
        return getHour_before23_M();
    }
}
protected void setHour(int value){
    if(VersionSDK.isSupEqualTo23_MARSHMALLOW()){
        setHour_after23_M(value);
    } else {
        setHour_before23_M(value);
    }
}
@RequiresApi(api = Build.VERSION_CODES.M)
private int getHour_after23_M(){
    return picker.getHour();
}
@RequiresApi(api = Build.VERSION_CODES.M)
private void setHour_after23_M(int value){
    picker.setHour(value);
}
@SuppressWarnings("deprecation")
private int getHour_before23_M(){
    return picker.getCurrentHour();
}
@SuppressWarnings("deprecation")
private void setHour_before23_M(int value){
    picker.setCurrentHour(value);
}

protected int getMinute(){
    if(VersionSDK.isSupEqualTo23_MARSHMALLOW()){
        return getMinute_after23_M();
    } else {
        return getMinute_before23_M();
    }
}
protected void setMinute(int value){
    if(VersionSDK.isSupEqualTo23_MARSHMALLOW()){
        setMinute_after23_M(value);
    } else {
        setMinute_before23_M(value);
    }
}
@RequiresApi(api = Build.VERSION_CODES.M)
private int getMinute_after23_M(){
    return picker.getHour();
}
@RequiresApi(api = Build.VERSION_CODES.M)
private void setMinute_after23_M(int value){
    picker.setHour(value);
}
@SuppressWarnings("deprecation")
private int getMinute_before23_M(){
    return picker.getCurrentHour();
}
@SuppressWarnings("deprecation")
private void setMinute_before23_M(int value){
    picker.setCurrentHour(value);
}

@Override
public boolean setValue(LocalTime time){
    setHour(time.getHour());
    setMinute(time.getMinute());
    return true;
}

@Override
public LocalTime getValue(){
    return LocalTime.of(getHour(), getMinute());
}

@Override
public <T> boolean setValue(Class<T> type, T object){
    if(type == LocalTime.class){
        setValue((LocalTime)object);
        return true;
    } else {
DebugException.start().unknown("type", type.getName()).end();
        return false;
    }


}

@Override
public <T> T getValue(Class<T> type){
    if(type == getEntryType()){
        return (T)getValue();
    } else {

DebugException.start().unknown("type", type.getName()).end();

        return null;
    }
}

public static class State extends DialogPickerBase.State{
    private LocalTime savedInstance = null;

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

    public Param(){
        setLayoutID(R.layout.dialog_picker_time);
    }

    public Param setToNow(){
        return (Param)setInitialValue(LocalTime.now());
    }

    @Override
    public <O> O getInitialValue(){
        LocalTime time = super.getInitialValue();
        if(time != null){
            return (O)time;
        } else {
            return (O)Clock.Time.now();
        }
    }

}


}