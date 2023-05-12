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

import com.tezov.lib_java_android.ui.dialog.dialogPicker.picker.DialogPickerDataWheel;
import com.tezov.lib_java_android.ui.dialog.dialogPicker.pickerController.DialogButtonAction;

public class PrototypePickerDataWheel extends PrototypeBase<DialogPickerDataWheel>{
@Override
public Class<DialogPickerDataWheel> getType(){
    return DialogPickerDataWheel.class;
}

@Override
public DialogPickerDataWheel.State build(){
    DialogPickerDataWheel.State state = new DialogPickerDataWheel.State();
    state.obtainParam().setButtonCreator(DialogButtonAction.BUTTON_DETAILS_CREATOR);
    return state;
}

@Override
public void onDialogCreated(DialogPickerDataWheel dialog){

}

}
