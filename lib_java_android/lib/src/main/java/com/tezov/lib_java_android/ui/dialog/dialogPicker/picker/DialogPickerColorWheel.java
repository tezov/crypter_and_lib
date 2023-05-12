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
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import androidx.fragment.app.Fragment;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatEditText;

import com.rarepebble.colorpicker.ColorPickerView;
import com.tezov.lib_java_android.R;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java_android.type.primaire.Color;
import com.tezov.lib_java_android.ui.dialog.dialogPicker.picker.base.DialogPickerBase;
import com.tezov.lib_java_android.util.UtilsView;

public class DialogPickerColorWheel extends DialogPickerBase<Color>{
final private static int NULL_CURRENT_COLOR = 0xFFFFFFFF;
final private static int NULL_ORIGINAL_COLOR = 0x00FFFFFF;
protected ColorPickerView picker;


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
public Class<Color> getEntryType(){
    return Color.class;
}

@Override
public void onPrepare(boolean hasBeenReconstructed){
    super.onPrepare(hasBeenReconstructed);
    picker = getContentView().findViewById(R.id.picker);
    AppCompatEditText editText = UtilsView.findFirst(AppCompatEditText.class, picker, UtilsView.Direction.DOWN);
    if(editText == null){
        picker.showHex(false);
    } else {
        editText.setFocusable(false);
    }
    if(hasBeenReconstructed){
        setOriginal(getParam().getInitialValue());
        setCurrent(getState().savedInstance);
        getState().savedInstance = null;
    }
}

@Override
public void onSaveInstanceState(Bundle savedInstanceState){
    super.onSaveInstanceState(savedInstanceState);
    getState().savedInstance = getValue();
}

@Override
public Color getValue(){
    return Color.fromARGB(picker.getColor());
}

@Override
public boolean setValue(Color color){
    setCurrent(color);
    setOriginal(color);
    return true;
}

public void setCurrent(Color color){
    if(color != null){
        picker.setCurrentColor(color.getARGB());
    } else {
        picker.setCurrentColor(NULL_CURRENT_COLOR);
    }
}

public Color getOriginal(){
    return getParam().getInitialValue();
}

public void setOriginal(Color color){
    if(color != null){
        picker.setOriginalColor(color.getARGB());
        getParam().setInitialValue(color);
    } else {
        picker.setOriginalColor(NULL_ORIGINAL_COLOR);
        getParam().setInitialValue(Color.fromARGB(NULL_ORIGINAL_COLOR));
    }
}

@Override
public <T> boolean setValue(Class<T> type, T object){
    if(type == Color.class){
        setValue((Color)object);
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
    private Color savedInstance = null;

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
        setLayoutID(R.layout.dialog_picker_color_wheel);
    }

}

}