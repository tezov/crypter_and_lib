/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.form.component.dialog;

import com.tezov.lib_java.debug.DebugLog;
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
import static com.tezov.lib_java_android.ui.dialog.dialogPicker.picker.base.DialogButtonView.ButtonPosition;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.tezov.lib_java_android.R;
import com.tezov.lib_java.async.notifier.task.TaskValue;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.runnable.RunnableCommand;
import com.tezov.lib_java_android.ui.dialog.dialogPicker.picker.base.DialogPickerBase;
import com.tezov.lib_java_android.ui.dialog.dialogPicker.pickerController.DialogButtonAction;
import com.tezov.lib_java_android.ui.dialog.dialogPicker.pickerController.DialogPickerController;
import com.tezov.lib_java_android.ui.dialog.dialogPicker.pickerController.PickerKey;
import com.tezov.lib_java_android.ui.dialog.dialogPicker.pickerController.PickerType;
import com.tezov.lib_java_android.ui.dialog.dialogPicker.pickerController.selectorPicker.SelectorBase;
import com.tezov.lib_java_android.ui.form.component.plain.defFormComponent;
import com.tezov.lib_java_android.ui.misc.AttributeReader;
import com.tezov.lib_java_android.ui.state.Method;
import com.tezov.lib_java_android.ui.state.Param;

import java.util.Iterator;

public interface defFormComponentDialog<T> extends defFormComponent<T>{
String ATTR_TITLE_KEY = "title_";
int[] ATTR_INDEX = R.styleable.defFormDialogPicker_lib;

default boolean hasState(){
    return getState() != null;
}

default void makeState(){
    State state = new State();
    state.attach(this);
    setState(state);
}

default <S extends State> S obtainState(){
    if(!hasState()){
        makeState();
    }
    return getState();
}

<S extends State> S getState();

void setState(State state);

default void restoreState(State state){
    setState(state);
}

default void onDetachedFromWindow(){
    if(hasState()){
        State state = getState();
        if(state.controller != null){
            state.controller.getParam().clearButtonCommand(this);
        }
        state.detach(this);
    }
}

default PickerType.Is getType(){
    return getState().dialogPickerFormType;
}

@Override
default void parseAttribute(Context context, Class type, AttributeSet attrs){
    if(type == defFormComponentDialog.class){
        AttributeReader attributes = new AttributeReader().parse(context, attrs).setAttrsIndex(ATTR_INDEX);
        setAttribute(defFormComponentDialog.class, attributes);
    } else {
        defFormComponent.super.parseAttribute(context, type, attrs);
    }
}

default void linkPickers(AttributeReader attributes){
    setButtonVisibility(DialogButtonAction.PREVIOUS, hasPrevious());
    setButtonVisibility(DialogButtonAction.NEXT, hasNext());
}

default void build(PickerType.Is type, View formContainer){
    defFormComponent.super.build(formContainer);
    AttributeReader attributes = takeAttribute(defFormComponentDialog.class);
    State state;
    if(!hasState()){
        state = makeState(attributes, type);
    } else {
        state = getState();
        state.controller.attach(this);
        state.controller.restorePickerState();
    }
    linkPickers(attributes);
    addButtonCommand(state.controller);
}

default State makeState(AttributeReader attributes, PickerType.Is type){
    State state = obtainState();
    state.dialogPickerFormType = type;
    if(type == null){
        return state;
    }
    DialogPickerController controller = type.create();
    state.controller = controller;
    controller.attach(this);
    controller.build();
    controller.getParam().setButtonPosition(DialogButtonAction.PREVIOUS, ButtonPosition.BOTTOM_LEFT);
    if(attributes == null){
        return state;
    }
    Iterator<PickerKey.Is> keys = controller.getPickerKeysIterator();
    while(keys.hasNext()){
        PickerKey.Is key = keys.next();
        String title = attributes.asString(ATTR_TITLE_KEY + key.name());
        if(title != null){
            DialogPickerBase.State pickerState = controller.getPickerState(key);
            if(pickerState != null){
                pickerState.obtainParam().setTitle(title);
            } else {
DebugException.start().log("Can't set dialog title, picker doesn't exist for key " + key.name()).end();
            }

        }
    }
    return state;
}

default void addButtonCommand(DialogPickerController controller){
    DialogPickerController.Param param = controller.getParam();
    param.addButtonCommand(DialogButtonAction.PREVIOUS, new RunnableCommand<defFormComponentDialog<?>>(this){
        @Override
        public void execute(){
            defFormComponentDialog<?> form = getPrevious();
            if(form != null){
                form.performClick();
            }
        }
    });
    param.addButtonCommand(DialogButtonAction.NEXT, new RunnableCommand<defFormComponentDialog<?>>(this){
        @Override
        public void execute(){
            defFormComponentDialog<?> form = getNext();
            if(form != null){
                form.performClick();
            }
        }
    });
}

@Override
default void setPrevious(defFormComponent<?> formPicker){
    setPrevious((defFormComponentDialog<?>)formPicker);
}

default void setPrevious(defFormComponentDialog<?> formPicker){
    defFormComponent.super.setPrevious(formPicker);
    if(formPicker != null){
        formPicker.setButtonVisibility(DialogButtonAction.NEXT, hasNext());
    }
    setButtonVisibility(DialogButtonAction.PREVIOUS, hasPrevious());
}

@Override
default void setNext(defFormComponent<?> formPicker){
    setNext((defFormComponentDialog<?>)formPicker);
}

default void setNext(defFormComponentDialog<?> formPicker){
    defFormComponent.super.setNext(formPicker);
    if(formPicker != null){
        formPicker.setButtonVisibility(DialogButtonAction.PREVIOUS, hasPrevious());
    }
    setButtonVisibility(DialogButtonAction.NEXT, hasNext());
}

default void setButtonVisibility(DialogButtonAction.Is action, boolean flag){
    if(!hasState()){
        return;
    }
    State state = getState();
    DialogPickerController.Param param = state.controller.getParam();
    if(action == DialogButtonAction.NEXT){
        if(flag){
            param.setButtonPosition(DialogButtonAction.CANCEL, ButtonPosition.TOP_LEFT);
            param.setButtonPosition(DialogButtonAction.CONFIRM, ButtonPosition.TOP_RIGHT);
            param.setButtonVisibility(DialogButtonAction.NEXT, true);
        } else {
            param.setButtonVisibility(DialogButtonAction.NEXT, false);
            param.setButtonPosition(DialogButtonAction.CONFIRM, ButtonPosition.BOTTOM_RIGHT);
            param.setButtonPosition(DialogButtonAction.CANCEL, ButtonPosition.TOP_RIGHT);
        }
        return;
    }
    if(action == DialogButtonAction.CONFIRM){
        param.setButtonVisibility(DialogButtonAction.CONFIRM, flag);
        return;
    }
    if(action == DialogButtonAction.CANCEL){
        param.setButtonVisibility(DialogButtonAction.CANCEL, flag);
        return;
    }
    if(action == DialogButtonAction.PREVIOUS){
        param.setButtonVisibility(DialogButtonAction.PREVIOUS, flag);
        return;
    }
    if(action == DialogButtonAction.SELECT){
        param.setButtonVisibility(DialogButtonAction.SELECT, flag);
        return;
    }
    if(action == DialogButtonAction.ADD){
        param.setButtonVisibility(DialogButtonAction.ADD, flag);
    }
}

default DialogPickerController getController(){
    return obtainState().controller;
}

default <V> V getValue(PickerKey.Is key){
    return getController().getPickerState(key).obtainMethod().get();
}

default <P extends SelectorBase.Param> P getParamController(){
    return obtainState().controller.getParam();
}

default boolean isVisible(){
    if(obtainState().controller == null){
        return false;
    }
    return obtainState().controller.isVisible();
}

default boolean isBusy(){
    if(obtainState().controller == null){
        return false;
    }
    return obtainState().controller.isBusy();
}

default boolean canBeShown(){
    if(obtainState().controller == null){
        return false;
    }
    return obtainState().controller.canBeShown();
}

boolean performClick();

default TaskValue.Observable show(){
    if(!canBeShown()){
        return null;
    }
    State state = getState();
    if(state.controller == null){
        setValue(Void.class, null);
        return null;
    } else {
        return state.controller.open();
    }
}

default void close(){
    if(obtainState().controller != null){
        obtainState().controller.close();
    }
}

class State extends com.tezov.lib_java_android.ui.state.State<Param, Method>{
    PickerType.Is dialogPickerFormType = null;
    DialogPickerController controller = null;

    @Override
    public DebugString toDebugString(){
        DebugString data = super.toDebugString();
        data.append("dialogPickerFormType", DebugTrack.getFullSimpleName(dialogPickerFormType));
        data.append("controller", DebugTrack.getFullSimpleNameWithHashcode(controller));
        return data;
    }

}

}
