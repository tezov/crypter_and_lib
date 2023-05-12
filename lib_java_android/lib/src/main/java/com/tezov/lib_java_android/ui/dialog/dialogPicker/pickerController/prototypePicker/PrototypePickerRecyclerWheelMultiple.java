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

import com.tezov.lib_java.async.notifier.observer.event.ObserverEvent;
import com.tezov.lib_java.type.defEnum.Event;
import com.tezov.lib_java_android.ui.dialog.dialogPicker.picker.DialogPickerRecyclerWheelMultiple;
import com.tezov.lib_java_android.ui.dialog.dialogPicker.pickerController.DialogButtonAction;

import static com.tezov.lib_java.type.defEnum.Event.ON_CLICK_SHORT;

public class PrototypePickerRecyclerWheelMultiple extends PrototypeBase<DialogPickerRecyclerWheelMultiple>{
@Override
public Class<DialogPickerRecyclerWheelMultiple> getType(){
    return DialogPickerRecyclerWheelMultiple.class;
}

@Override
public DialogPickerRecyclerWheelMultiple.State build(){
    DialogPickerRecyclerWheelMultiple.State picker = new DialogPickerRecyclerWheelMultiple.State<>();
    picker.obtainParam().setRecyclerDefaultDecorationDrawable().setButtonCreator(DialogButtonAction.BUTTON_DETAILS_CREATOR);
    return picker;
}

@Override
public void onDialogCreated(DialogPickerRecyclerWheelMultiple dialog){
    dialog.observe(new ObserverEvent<Event.Is, Object>(this, ON_CLICK_SHORT){
        @Override
        public void onComplete(Event.Is event, Object object){
            DialogPickerRecyclerWheelMultiple.Param param = dialog.obtainParam();
            DialogPickerRecyclerWheelMultiple.Method method = dialog.obtainMethod();
            if(param.isButtonVisible(DialogButtonAction.NEXT)){
                method.callOnClickButton(DialogButtonAction.NEXT);
            } else {
                if(param.isButtonVisible(DialogButtonAction.CONFIRM)){
                    method.callOnClickButton(DialogButtonAction.CONFIRM);
                }
            }
        }
    });
}

}
