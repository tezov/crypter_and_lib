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

import android.view.inputmethod.EditorInfo;

import com.tezov.lib_java.async.notifier.observer.event.ObserverEvent;
import com.tezov.lib_java.type.defEnum.Event;
import com.tezov.lib_java_android.ui.dialog.dialogPicker.picker.DialogPickerStringUnit;
import com.tezov.lib_java_android.ui.dialog.dialogPicker.pickerController.DialogButtonAction;

public class PrototypePickerStringUnit extends PrototypeBase<DialogPickerStringUnit>{
@Override
public Class<DialogPickerStringUnit> getType(){
    return DialogPickerStringUnit.class;
}

@Override
public DialogPickerStringUnit.State build(){
    DialogPickerStringUnit.State picker = new DialogPickerStringUnit.State<>();
    picker.obtainParam().setButtonCreator(DialogButtonAction.BUTTON_DETAILS_CREATOR);
    return picker;
}

@Override
public void onDialogCreated(DialogPickerStringUnit dialog){
    dialog.observe(new ObserverEvent<Event.Is, Object>(this, Event.ON_KEYBOARD_DONE){
        @Override
        public void onComplete(Event.Is event, Object object){
            int action = (int)object; //EditorInfo.IME_ACTION_NEXT || EditorInfo.IME_ACTION_DONE
            DialogPickerStringUnit.Method method = dialog.obtainMethod();
            if(action == EditorInfo.IME_ACTION_NEXT){
                method.callOnClickButton(DialogButtonAction.NEXT);
            } else {
                method.callOnClickButton(DialogButtonAction.CONFIRM);
            }
        }
    });
}

}
