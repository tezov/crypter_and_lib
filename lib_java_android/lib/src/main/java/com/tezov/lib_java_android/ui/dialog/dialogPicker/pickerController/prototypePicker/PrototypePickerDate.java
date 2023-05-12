/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.dialog.dialogPicker.pickerController.prototypePicker;

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

import com.tezov.lib_java_android.ui.dialog.dialogPicker.picker.DialogPickerDate;
import com.tezov.lib_java_android.ui.dialog.dialogPicker.pickerController.DialogButtonAction;

public class PrototypePickerDate extends PrototypeBase<DialogPickerDate>{
private final boolean day;
private final boolean month;
private final boolean year;

public PrototypePickerDate(boolean day, boolean month, boolean year){
    this.day = day;
    this.month = month;
    this.year = year;
}

@Override
public Class<DialogPickerDate> getType(){
    return DialogPickerDate.class;
}

@Override
public DialogPickerDate.State build(){
    DialogPickerDate.State state = new DialogPickerDate.State();
    state.obtainParam().setVisibilities(day, month, year).setButtonCreator(DialogButtonAction.BUTTON_DETAILS_CREATOR);
    return state;
}

@Override
public void onDialogCreated(DialogPickerDate dialog){

}

}
