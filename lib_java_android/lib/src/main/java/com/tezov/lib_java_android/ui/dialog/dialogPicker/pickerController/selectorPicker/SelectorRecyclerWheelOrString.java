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

import android.view.inputmethod.EditorInfo;

import com.tezov.lib_java.definition.defEntry;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java.type.runnable.RunnableCommand;
import com.tezov.lib_java_android.ui.dialog.dialogPicker.picker.DialogPickerRecyclerWheel;
import com.tezov.lib_java_android.ui.dialog.dialogPicker.picker.DialogPickerString;
import com.tezov.lib_java_android.ui.dialog.dialogPicker.picker.base.DialogButtonAction;
import com.tezov.lib_java_android.ui.dialog.dialogPicker.picker.base.DialogPickerBase;
import com.tezov.lib_java_android.ui.dialog.dialogPicker.pickerController.DialogPickerController;
import com.tezov.lib_java_android.ui.dialog.dialogPicker.pickerController.PickerKey;
import com.tezov.lib_java_android.ui.dialog.dialogPicker.pickerController.PickerType;
import com.tezov.lib_java_android.ui.dialog.dialogPicker.pickerController.prototypePicker.PrototypeBase;
import com.tezov.lib_java_android.ui.dialog.dialogPicker.pickerController.prototypePicker.PrototypePickerRecyclerWheel;
import com.tezov.lib_java_android.ui.dialog.dialogPicker.pickerController.prototypePicker.PrototypePickerString;
import com.tezov.lib_java_android.ui.recycler.RecyclerListDataManager;
import com.tezov.lib_java_android.ui.recycler.RecyclerListRowManager;

import java.util.ArrayList;
import java.util.List;

import static com.tezov.lib_java_android.ui.dialog.dialogPicker.pickerController.DialogButtonAction.ADD;
import static com.tezov.lib_java_android.ui.dialog.dialogPicker.pickerController.DialogButtonAction.NEXT;
import static com.tezov.lib_java_android.ui.dialog.dialogPicker.pickerController.DialogButtonAction.SELECT;

public class SelectorRecyclerWheelOrString<TYPE> extends SelectorBase{
@Override
public Param newParam(){
    return new Param();
}

@Override
public PrototypeBase getPrototype(PickerKey.Is key){
    if(key == PickerKey.ADD){
        return new PrototypePickerString();
    }
    if(key == PickerKey.SELECT){
        return new PrototypePickerRecyclerWheel();
    }

DebugException.start().unknown("key", key).end();

    return null;
}

@Override
public PickerType.Is getType(PickerKey.Is key){
    if(key == PickerKey.ADD){
        return PickerType.STRING;
    }
    if(key == PickerKey.SELECT){
        return PickerType.RECYCLER_WHEEL;
    }

DebugException.start().unknown("key", key).end();

    return null;
}

@Override
public List<PickerKey.Is> getKeys(){
    List<PickerKey.Is> list = new ArrayList<>();
    list.add(PickerKey.ADD);
    list.add(PickerKey.SELECT);
    return list;
}

@Override
public DialogPickerBase.State build(PickerKey.Is key){
    DialogPickerBase.State state = getPrototype(key).build();
    DialogPickerBase.Param param = state.obtainParam();
    if(key == PickerKey.SELECT){
        param.obtainButton(ADD).setPositionDefault(true).addCommand(new RunnableCommand<SelectorRecyclerWheelOrString>(this){
            @Override
            public void execute(){
                getController().open(PickerKey.ADD);
            }
        });
    } else {
        if(key == PickerKey.ADD){
            param.obtainButton(SELECT).setPositionDefault(true).addCommand(new RunnableCommand<SelectorRecyclerWheelOrString>(this){
                @Override
                public void execute(){
                    getController().open(PickerKey.SELECT);
                }
            });
        }
    }
    return state;
}

@Override
public PickerKey.Is pick(defEntry<?> initialValue){
    DialogPickerController controller = getController();
    DialogPickerController.Param param = controller.getParam();
    if(param.openOnStart() == null){

        RecyclerListRowManager<TYPE> rowManager = controller.<DialogPickerRecyclerWheel.State<TYPE>>getPickerState(PickerKey.SELECT).obtainParam().getRowManager();
        TYPE data = initialValue != null ? initialValue.getValue(rowManager.getDataManager().getType()) : null;
        if(data == null){
            if(rowManager.getDataManager().size() > 0){
                return PickerKey.SELECT;
            } else {
                return PickerKey.ADD;
            }
        } else {
            RecyclerListDataManager<TYPE> dataManager = rowManager.getDataManager();
            dataManager.acquireLock(this);
            Integer index = dataManager.indexOf(data);
            TYPE dataInDb = index != null ? dataManager.get(index) : null;
            dataManager.releaseLock(this);
            if((dataInDb != null) && (dataInDb.equals(data))){
                return PickerKey.SELECT;
            } else {
                return PickerKey.ADD;
            }
        }
    }
    if(param.openOnStart() == PickerKey.ADD){
        return PickerKey.ADD;
    }
    if(param.openOnStart() == PickerKey.SELECT){
        return PickerKey.SELECT;
    }

DebugException.start().unknown("picker", param.openOnStart()).end();

    return null;
}

@Override
public boolean beforeShow(PickerKey.Is currentPicker, PickerKey.Is NextPicker){
    DialogPickerController controller = getController();
    if(NextPicker == PickerKey.SELECT){
        return true;
    }
    if(NextPicker == PickerKey.ADD){
        DialogPickerRecyclerWheel.State<TYPE> pickerSelectState = controller.getPickerState(PickerKey.SELECT);
        DialogPickerString.State pickerAddState = controller.getPickerState(PickerKey.ADD);
        if(pickerSelectState != null){
            Param paramController = controller.getParam();
            if(paramController.overrideSelectButtonVisibility){
                DialogPickerBase.Param paramPicker = pickerAddState.obtainParam();
                paramPicker.obtainButton(SELECT).setVisibility(pickerSelectState.obtainParam().getRowManager().getDataManager().size() > 0);
            }
        }
        if(pickerAddState.obtainParam().isButtonVisible(NEXT)){
            pickerAddState.obtainParam().setActionButtonType(EditorInfo.IME_ACTION_NEXT);
        } else {
            pickerAddState.obtainParam().setActionButtonType(EditorInfo.IME_ACTION_DONE);
        }
        return true;
    }
    return false;
}

public static class Param extends SelectorBase.Param{
    private boolean overrideSelectButtonVisibility = true;

    @Override
    public Param setButtonVisibility(DialogButtonAction.Is action, boolean flag){
        super.setButtonVisibility(action, flag);
        if(action == SELECT){
            overrideSelectButtonVisibility = flag;
        }
        return this;
    }

    @Override
    public DebugString toDebugString(){
        DebugString data = super.toDebugString();
        data.append("overrideSelectButtonVisibility", overrideSelectButtonVisibility);
        return data;
    }

}

}
