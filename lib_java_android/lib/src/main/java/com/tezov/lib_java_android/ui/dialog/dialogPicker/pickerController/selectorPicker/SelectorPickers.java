/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.dialog.dialogPicker.pickerController.selectorPicker;

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
import static com.tezov.lib_java_android.ui.dialog.dialogPicker.pickerController.DialogButtonAction.NEXT;

import android.view.inputmethod.EditorInfo;

import com.tezov.lib_java.definition.defEntry;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java_android.ui.dialog.dialogPicker.picker.DialogPickerString;
import com.tezov.lib_java_android.ui.dialog.dialogPicker.picker.DialogPickerStringUnit;
import com.tezov.lib_java_android.ui.dialog.dialogPicker.picker.base.DialogPickerBase;
import com.tezov.lib_java_android.ui.dialog.dialogPicker.pickerController.DialogPickerController;
import com.tezov.lib_java_android.ui.dialog.dialogPicker.pickerController.PickerKey;
import com.tezov.lib_java_android.ui.dialog.dialogPicker.pickerController.PickerType;
import com.tezov.lib_java_android.ui.dialog.dialogPicker.pickerController.prototypePicker.PrototypeBase;
import com.tezov.lib_java_android.ui.dialog.dialogPicker.pickerController.prototypePicker.PrototypePickerColorWheel;
import com.tezov.lib_java_android.ui.dialog.dialogPicker.pickerController.prototypePicker.PrototypePickerDataWheel;
import com.tezov.lib_java_android.ui.dialog.dialogPicker.pickerController.prototypePicker.PrototypePickerDate;
import com.tezov.lib_java_android.ui.dialog.dialogPicker.pickerController.prototypePicker.PrototypePickerRecyclerWheel;
import com.tezov.lib_java_android.ui.dialog.dialogPicker.pickerController.prototypePicker.PrototypePickerRecyclerWheelMultiple;
import com.tezov.lib_java_android.ui.dialog.dialogPicker.pickerController.prototypePicker.PrototypePickerString;
import com.tezov.lib_java_android.ui.dialog.dialogPicker.pickerController.prototypePicker.PrototypePickerStringUnit;
import com.tezov.lib_java_android.ui.dialog.dialogPicker.pickerController.prototypePicker.PrototypePickerTime;

import java.util.ArrayList;
import java.util.List;

public class SelectorPickers extends SelectorBase{
protected PickerType.Is type;

public SelectorPickers(PickerType.Is type){
    super();
    this.type = type;
}

@Override
public PrototypeBase getPrototype(PickerKey.Is key){ //IMPROVE efficiency new/destroy each

    if(key != PickerKey.SINGLE){
DebugException.start().unknown("key", key).end();
        return null;
    }

    if(type == PickerType.DATE_YEAR){
        return new PrototypePickerDate(false, false, true);
    }
    if(type == PickerType.DATE_MONTH_YEAR){
        return new PrototypePickerDate(false, true, true);
    }
    if(type == PickerType.DATE_DAY_MONTH_YEAR){
        return new PrototypePickerDate(true, true, true);
    }
    if(type == PickerType.RECYCLER_WHEEL){
        return new PrototypePickerRecyclerWheel();
    }
    if(type == PickerType.RECYCLER_WHEEL_MULTIPLE){
        return new PrototypePickerRecyclerWheelMultiple();
    }
    if(type == PickerType.STRING){
        return new PrototypePickerString();
    }
    if(type == PickerType.STRING_UNIT){
        return new PrototypePickerStringUnit();
    }
    if(type == PickerType.TIME){
        return new PrototypePickerTime();
    }
    if(type == PickerType.DATA_WHEEL){
        return new PrototypePickerDataWheel();
    }
    if(type == PickerType.COLOR){
        return new PrototypePickerColorWheel();
    }

DebugException.start().unknown("type", type).end();

    return null;
}

@Override
public PickerType.Is getType(PickerKey.Is key){
    if(key == PickerKey.SINGLE){
        return type;
    }

DebugException.start().unknown("key", key).end();

    return null;
}

@Override
public List<PickerKey.Is> getKeys(){
    List<PickerKey.Is> list = new ArrayList<>();
    list.add(PickerKey.SINGLE);
    return list;
}

@Override
public DialogPickerBase.State build(PickerKey.Is key){
    return getPrototype(key).build();

}

@Override
public PickerKey.Is pick(defEntry initialValue){
    return PickerKey.SINGLE;
}

@Override
public boolean beforeShow(PickerKey.Is currentPicker, PickerKey.Is NextPicker){
    DialogPickerController controller = getController();
    if(type == PickerType.STRING){
        DialogPickerString.State state = controller.getPickerState(PickerKey.SINGLE);
        if(state.getParam().isButtonVisible(NEXT)){
            state.getParam().setActionButtonType(EditorInfo.IME_ACTION_NEXT);
        } else {
            state.getParam().setActionButtonType(EditorInfo.IME_ACTION_DONE);
        }
    } else if(type == PickerType.STRING_UNIT){
        DialogPickerStringUnit.State state = controller.getPickerState(PickerKey.SINGLE);
        if(state.getParam().isButtonVisible(NEXT)){
            state.getParam().setActionButtonType(EditorInfo.IME_ACTION_NEXT);
        } else {
            state.getParam().setActionButtonType(EditorInfo.IME_ACTION_DONE);
        }
    }
    return true;
}

}
