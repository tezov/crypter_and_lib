/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.dialog.dialogPicker.pickerController.selectorPicker;

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugException;
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
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.ref.Ref;
import com.tezov.lib_java.type.ref.WR;
import com.tezov.lib_java.type.runnable.RunnableCommand;
import com.tezov.lib_java_android.ui.dialog.dialogPicker.picker.base.DialogButtonAction;
import com.tezov.lib_java_android.ui.dialog.dialogPicker.picker.base.DialogPickerBase;
import com.tezov.lib_java_android.ui.dialog.dialogPicker.pickerController.DialogPickerController;
import com.tezov.lib_java_android.ui.dialog.dialogPicker.pickerController.PickerKey;
import com.tezov.lib_java_android.ui.dialog.dialogPicker.pickerController.PickerType;
import com.tezov.lib_java_android.ui.dialog.dialogPicker.pickerController.prototypePicker.PrototypeBase;

import java.util.List;

public abstract class SelectorBase{
private WR<DialogPickerController> controllerWR;

protected SelectorBase(){
DebugTrack.start().create(this).end();
}

public Param newParam(){
    return new Param();
}

public void attach(DialogPickerController controller){
    if(controller != null){
        controllerWR = WR.newInstance(controller);
    } else {
        controllerWR = null;
    }
}

final protected DialogPickerController getController(){
    return Ref.get(controllerWR);
}

public abstract PrototypeBase getPrototype(PickerKey.Is key);

public abstract PickerType.Is getType(PickerKey.Is key);

public abstract List<PickerKey.Is> getKeys();

public abstract DialogPickerBase.State build(PickerKey.Is key);

public abstract PickerKey.Is pick(defEntry<?> initialValue);

public abstract boolean beforeShow(PickerKey.Is currentPicker, PickerKey.Is NextPicker);

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

public static class Param extends DialogPickerController.Param{
    public Param setCommand(PickerKey.Is key, DialogButtonAction.Is action, RunnableCommand command){
        DialogPickerBase.Param param = getController().getPickerState(key).obtainParam();
        param.obtainButton(action).addCommand(command);
        return this;
    }

}

}
