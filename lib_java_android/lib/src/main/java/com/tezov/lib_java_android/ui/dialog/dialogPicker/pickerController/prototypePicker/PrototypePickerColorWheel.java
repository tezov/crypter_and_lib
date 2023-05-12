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

import com.tezov.lib_java_android.ui.dialog.dialogPicker.picker.DialogPickerColorWheel;
import com.tezov.lib_java_android.ui.dialog.dialogPicker.pickerController.DialogButtonAction;

public class PrototypePickerColorWheel extends PrototypeBase<DialogPickerColorWheel>{
@Override
public Class<DialogPickerColorWheel> getType(){
    return DialogPickerColorWheel.class;
}

@Override
public DialogPickerColorWheel.State build(){
    DialogPickerColorWheel.State state = new DialogPickerColorWheel.State();
    state.obtainParam().setButtonCreator(DialogButtonAction.BUTTON_DETAILS_CREATOR);
    return state;
}

@Override
public void onDialogCreated(DialogPickerColorWheel dialog){

}

}
