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

import com.tezov.lib_java.definition.defEntry;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java.type.runnable.RunnableCommand;
import com.tezov.lib_java.wrapperAnonymous.BiConsumerW;
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

import java.util.Arrays;
import java.util.List;

import static com.tezov.lib_java_android.ui.dialog.dialogPicker.pickerController.DialogButtonAction.ADD;
import static com.tezov.lib_java_android.ui.dialog.dialogPicker.pickerController.DialogButtonAction.EDIT;
import static com.tezov.lib_java_android.ui.dialog.dialogPicker.pickerController.DialogButtonAction.SELECT;

public class SelectorRecyclerWheelOrStringOrConsume<TYPE> extends SelectorBase{
@Override
public Param newParam(){
    return new Param();
}

@Override
public PrototypeBase getPrototype(PickerKey.Is key){
    if(key == PickerKey.SELECT){
        return new PrototypePickerRecyclerWheel();
    }
    if(key == PickerKey.EDIT){
        return new PrototypePickerString();
    }

DebugException.start().unknown("key", key).end();


    return null;
}

@Override
public PickerType.Is getType(PickerKey.Is key){
    if(key == PickerKey.SELECT){
        return PickerType.RECYCLER_WHEEL;
    }
    if(key == PickerKey.EDIT){
        return PickerType.STRING;
    }

DebugException.start().unknown("key", key).end();

    return null;
}

@Override
public List<PickerKey.Is> getKeys(){
    return Arrays.asList(PickerKey.EDIT, PickerKey.SELECT);
}

private void onOptionCommand(PickerKey.Is key, DialogButtonAction.Is action){
    DialogPickerController pickerController = getController();
    SelectorRecyclerWheelOrStringOrConsume.Param param = pickerController.getParam();
    if(param.optionCommand != null){
        param.optionCommand.accept(key, action);
    }
}

@Override
public DialogPickerBase.State build(PickerKey.Is key){
    DialogPickerBase.State state = getPrototype(key).build();
    DialogPickerBase.Param param = state.obtainParam();
    if(key == PickerKey.SELECT){
        param.obtainButton(ADD).setPositionDefault(true).addCommand(new RunnableCommand<SelectorRecyclerWheelOrStringOrConsume>(this){
            @Override
            public void execute(){
                onOptionCommand(key, ADD);
            }
        }).setOrder(0);
        param.obtainButton(EDIT).setPositionDefault(true).addCommand(new RunnableCommand<SelectorRecyclerWheelOrStringOrConsume>(this){
            @Override
            public void execute(){
                onOptionCommand(key, EDIT);
            }
        }).setOrder(1);
    } else {
        if(key == PickerKey.EDIT){
            param.obtainButton(SELECT).setPositionDefault(true).addCommand(new RunnableCommand<SelectorRecyclerWheelOrStringOrConsume>(this){
                @Override
                public void execute(){
                    getController().open(PickerKey.SELECT);
                }
            }).setOrder(0);
            param.obtainButton(EDIT).addCommand(new RunnableCommand<SelectorRecyclerWheelOrStringOrConsume>(this){
                @Override
                public void execute(){
                    onOptionCommand(key, EDIT);
                }
            }).setPositionDefault(true).setOrder(1);
        }
    }
    return state;
}

@Override
public PickerKey.Is pick(defEntry<?> initialValue){
    DialogPickerController pickerController = getController();
    SelectorRecyclerWheelOrStringOrConsume.Param param = pickerController.getParam();
    if(param.openOnStart() == null){
        RecyclerListRowManager<TYPE> rowManager = pickerController.<DialogPickerRecyclerWheel.State<TYPE>>getPickerState(PickerKey.SELECT).obtainParam().getRowManager();
        TYPE data = initialValue != null ? initialValue.getValue(rowManager.getDataManager().getType()) : null;
        if(data == null){
            if(rowManager.getDataManager().size() <= 0){
                return PickerKey.ADD;
            } else {
                return PickerKey.SELECT;
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
                return PickerKey.EDIT;
            }
        }
    }
    if(param.openOnStart() == PickerKey.SELECT){
        return PickerKey.SELECT;
    }
    if(param.openOnStart() == PickerKey.EDIT){
        return PickerKey.EDIT;
    }
    if(param.openOnStart() == PickerKey.ADD){
        return PickerKey.ADD;
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
    if(NextPicker == PickerKey.EDIT){
        DialogPickerRecyclerWheel.State<TYPE> pickerSelectState = controller.getPickerState(PickerKey.SELECT);
        DialogPickerString.State pickerEditState = controller.getPickerState(PickerKey.EDIT);
        if(pickerSelectState != null){
            Param paramController = controller.getParam();
            if(paramController.overrideSelectButtonVisibility){
                DialogPickerBase.Param paramPicker = pickerEditState.obtainParam();
                paramPicker.obtainButton(SELECT).setVisibility(pickerSelectState.obtainParam().getRowManager().getDataManager().size() > 0);
            }
        }
        return true;
    }
    if(NextPicker == PickerKey.ADD){
        DialogPickerBase.Method method = controller.getPickerState(PickerKey.SELECT).obtainMethod();
        method.executeCommandButton(ADD);
        return false;
    }
    return false;
}

public static class Param extends SelectorBase.Param{
    public BiConsumerW<PickerKey.Is, DialogButtonAction.Is> optionCommand = null;
    private boolean overrideSelectButtonVisibility = true;

    @Override
    public Param setButtonVisibility(DialogButtonAction.Is action, boolean flag){
        super.setButtonVisibility(action, flag);
        if(action == SELECT){
            overrideSelectButtonVisibility = flag;
        }
        return this;
    }

    public BiConsumerW<PickerKey.Is, DialogButtonAction.Is> getOptionCommand(){
        return optionCommand;
    }

    public Param onOptionCommand(BiConsumerW<? extends PickerKey.Is, ? extends DialogButtonAction.Is> consumer){
        this.optionCommand = (BiConsumerW)consumer;
        return this;
    }

    @Override
    public DebugString toDebugString(){
        DebugString data = super.toDebugString();
        data.appendCheckIfNull("optionCommand", optionCommand);
        return data;
    }

}

}
