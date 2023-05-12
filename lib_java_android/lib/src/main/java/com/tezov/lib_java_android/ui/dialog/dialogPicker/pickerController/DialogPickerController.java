/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.dialog.dialogPicker.pickerController;

import com.tezov.lib_java.debug.DebugLog;
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

import com.tezov.lib_java_android.ui.dialog.DialogBase;
import com.tezov.lib_java.async.notifier.observer.value.ObserverValue;
import com.tezov.lib_java.async.notifier.observer.value.ObserverValueE;
import com.tezov.lib_java.async.notifier.task.TaskValue;
import com.tezov.lib_java.definition.defEntry;
import com.tezov.lib_java.toolbox.Compare;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.collection.ListEntry;
import com.tezov.lib_java.type.primaire.Entry;
import com.tezov.lib_java.type.ref.Ref;
import com.tezov.lib_java.type.ref.WR;
import com.tezov.lib_java.type.runnable.RunnableCommand;
import com.tezov.lib_java.wrapperAnonymous.ConsumerW;
import com.tezov.lib_java_android.ui.dialog.DialogNavigable;
import com.tezov.lib_java_android.ui.dialog.dialogPicker.picker.base.DialogButtonAction;
import com.tezov.lib_java_android.ui.dialog.dialogPicker.picker.base.DialogPickerBase;
import com.tezov.lib_java_android.ui.dialog.dialogPicker.pickerController.prototypePicker.PrototypeBase;
import com.tezov.lib_java_android.ui.dialog.dialogPicker.pickerController.selectorPicker.SelectorBase;
import com.tezov.lib_java_android.ui.navigation.Navigate;

import java.util.Iterator;

import static com.tezov.lib_java_android.ui.dialog.dialogPicker.picker.base.DialogButtonView.ButtonPosition;

public class DialogPickerController{
private final SelectorBase selector;
private final ListEntry<PickerKey.Is, DialogPickerBase.State> pickersState = new ListEntry<>();
private SelectorBase.Param param = null;
private WR<defEntry> entryWR = null;
private boolean isBusy = false;
private PickerKey.Is currentKey = null;

public DialogPickerController(SelectorBase selector){
DebugTrack.start().create(this).end();
    this.selector = selector;
    selector.attach(this);
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

private DialogPickerController me(){
    return this;
}

public void attach(defEntry entry){
    this.entryWR = WR.newInstance(entry);
}

private defEntry getEntry(){
    return Ref.get(entryWR);
}

public boolean hasParam(){
    return param != null;
}

public <P extends SelectorBase.Param> P getParam(){
    return (P)param;
}

public DialogPickerController build(){
    if(!hasParam()){
        return build(selector.newParam());
    } else {
        return build(getParam());
    }
}

public DialogPickerController build(SelectorBase.Param param){
    this.param = param;
    param.attach(this);
    for(PickerKey.Is key: selector.getKeys()){
        setPickerState(key, build(key));
    }
    return this;
}

private DialogPickerBase.State build(PickerKey.Is key){
    DialogPickerBase.State state = selector.build(key);
    DialogPickerBase.Param param = state.obtainParam();
    param.hideOnClose(false);
    CommandButtonConfirm commandConfirm = new CommandButtonConfirm(me());
    param.obtainButton(com.tezov.lib_java_android.ui.dialog.dialogPicker.pickerController.DialogButtonAction.CANCEL).setPositionDefault(true);
    param.obtainButton(com.tezov.lib_java_android.ui.dialog.dialogPicker.pickerController.DialogButtonAction.CONFIRM).setPositionDefault(true).addCommand(commandConfirm);
    param.obtainButton(com.tezov.lib_java_android.ui.dialog.dialogPicker.pickerController.DialogButtonAction.NEXT).setPositionDefault(false).addCommand(commandConfirm);
    param.obtainButton(com.tezov.lib_java_android.ui.dialog.dialogPicker.pickerController.DialogButtonAction.PREVIOUS).setPositionDefault(false).addCommand(commandConfirm);
    return state;
}

public void restorePickerState(){
    for(Entry<PickerKey.Is, DialogPickerBase.State> entry: pickersState){
        DialogPickerBase dialog = DialogBase.findByTag(entry.value.getTag());
        if(dialog != null){
            selector.getPrototype(entry.key).onDialogCreated(dialog);
            CommandButtonConfirm commandButtonConfirm = (CommandButtonConfirm)entry.value.getParam()
                    .getButton(com.tezov.lib_java_android.ui.dialog.dialogPicker.pickerController.DialogButtonAction.CONFIRM)
                    .getCommand(me());
            commandButtonConfirm.attach(dialog);
        }
    }
}

public <D extends DialogPickerBase.State> D getCurrentPickerState(){
    if(currentKey == null){
        return null;
    }
    return getPickerState(currentKey);
}

public <D extends DialogPickerBase.State> D getPickerState(PickerKey.Is key){
    return (D)pickersState.getValue(key);
}

public Iterator<PickerKey.Is> getPickerKeysIterator(){
    return pickersState.iteratorKeys();
}

private void setPickerState(PickerKey.Is key, DialogPickerBase.State state){
    pickersState.put(key, state);
}

public <P extends DialogPickerBase.Param> P obtainParamPicker(PickerKey.Is key){
    return (P)getPickerState(key).obtainParam();
}

public void setPickerParam(PickerKey.Is key, DialogPickerBase.Param param){
    getPickerState(key).setParam(param);
}

public boolean isBusy(){
    return isBusy;
}

public boolean isVisible(){
    DialogPickerBase.State state = getCurrentPickerState();
    if(state != null){
        return state.isVisible();
    } else {
        return false;
    }
}

public boolean canBeShown(){
    return !isBusy && !isVisible();
}

public TaskValue.Observable open(){
    return open(selector.pick(getEntry()));
}

public TaskValue.Observable open(PickerKey.Is nextKey){
    if(isBusy || (isVisible() && Compare.equalsAndNotNull(currentKey, nextKey))){
        return null;
    }
    isBusy = true;
    if(!selector.beforeShow(currentKey, nextKey)){
        isBusy = false;
        return null;
    }
    currentKey = nextKey;
    PrototypeBase prototype = selector.getPrototype(nextKey);
    DialogPickerBase.State state = getPickerState(nextKey);
    DialogPickerBase.Param param = state.getParam();
    TaskValue.Observable observable =  Navigate.To(prototype.getType(), state);
    observable.observe(new ObserverValue<DialogPickerBase>(this){
        @Override
        public void onComplete(DialogPickerBase dialog){
            prototype.onDialogCreated(dialog);
            CommandButtonConfirm commandButtonConfirm = (CommandButtonConfirm)param.getButton(com.tezov.lib_java_android.ui.dialog.dialogPicker.pickerController.DialogButtonAction.CONFIRM).getCommand(me());
            commandButtonConfirm.attach(dialog);
            dialog.attach(getEntry());
            isBusy = false;
        }
    });
    return observable;
}

public void close(){
    DialogPickerBase.State state = getCurrentPickerState();
    if(isBusy || ((state != null) && !state.isVisible())){
        return;
    }
    isBusy = true;
    DialogNavigable.close(state).observe(new ObserverValueE<DialogNavigable>(this){
        @Override
        public void onComplete(DialogNavigable dialog){
            isBusy = false;
        }

        @Override
        public void onException(DialogNavigable dialog, Throwable e){
        }
    });
}

public static class Param extends com.tezov.lib_java_android.ui.state.Param{
    public PickerKey.Is openOnStart = null;
    private WR<DialogPickerController> controllerWR;

    public void attach(DialogPickerController controller){
        controllerWR = WR.newInstance(controller);
    }

    public DialogPickerController getController(){
        return Ref.get(controllerWR);
    }

    public PickerKey.Is openOnStart(){
        return openOnStart;
    }

    public Param openOnStart(PickerKey.Is openOnStart){
        this.openOnStart = openOnStart;
        return this;
    }

    private void pickersDo(ConsumerW<DialogPickerBase.State> consumer){
        for(Entry<PickerKey.Is, DialogPickerBase.State> entry: getController().pickersState){
            consumer.accept(entry.value);
        }
    }

    public void setTitle(String title){
        pickersDo(new ConsumerW<DialogPickerBase.State>(){
            @Override
            public void accept(DialogPickerBase.State pickerState){
                pickerState.obtainParam().setTitle(title);
            }
        });
    }

    public Param setButtonVisibility(DialogButtonAction.Is action, boolean flag){
        pickersDo(new ConsumerW<DialogPickerBase.State>(){
            @Override
            public void accept(DialogPickerBase.State pickerState){
                com.tezov.lib_java_android.ui.dialog.dialogPicker.pickerController.DialogButtonAction.ButtonDetails button = pickerState.obtainParam().getButton(action);
                if(button != null){
                    button.setVisibility(flag);
                }
            }
        });
        return this;
    }

    public void enableButton(DialogButtonAction.Is action, boolean flag){
        pickersDo(new ConsumerW<DialogPickerBase.State>(){
            @Override
            public void accept(DialogPickerBase.State pickerState){
                com.tezov.lib_java_android.ui.dialog.dialogPicker.pickerController.DialogButtonAction.ButtonDetails button = pickerState.obtainParam().getButton(action);
                if(button != null){
                    button.enable(flag);
                }
            }
        });
    }

    public void setButtonPosition(DialogButtonAction.Is action, ButtonPosition position){
        pickersDo(new ConsumerW<DialogPickerBase.State>(){
            @Override
            public void accept(DialogPickerBase.State pickerState){
                com.tezov.lib_java_android.ui.dialog.dialogPicker.pickerController.DialogButtonAction.ButtonDetails button = pickerState.obtainParam().getButton(action);
                if(button != null){
                    button.setPosition(position);
                }
            }
        });
    }

    public void addButtonCommand(DialogButtonAction.Is action, RunnableCommand command){
        pickersDo(new ConsumerW<DialogPickerBase.State>(){
            @Override
            public void accept(DialogPickerBase.State pickerState){
                pickerState.obtainParam().obtainButton(action).addCommand(command);
            }
        });
    }

    public <BOSS> void clearButtonCommand(BOSS boss){
        pickersDo(new ConsumerW<DialogPickerBase.State>(){
            @Override
            public void accept(DialogPickerBase.State pickerState){
                pickerState.obtainParam().clearButtonCommand(boss);
            }
        });
    }

    @Override
    public DebugString toDebugString(){
        DebugString data = super.toDebugString();
        data.append("openOnStart", openOnStart);
        return data;
    }

}

private static class CommandButtonConfirm extends RunnableCommand<DialogPickerController>{
    WR<defEntry> dialogEntry;

    CommandButtonConfirm(DialogPickerController boss){
        super(boss);
        this.dialogEntry = null;
    }

    void attach(defEntry dialogEntry){
        this.dialogEntry = WR.newInstance(dialogEntry);
    }

    @Override
    public void execute(){
        defEntry entry = getBoss().getEntry();
        if((entry != null) && (Ref.isNotNull(dialogEntry))){
            entry.setValueFrom(dialogEntry.get());
        }
    }

}

}
