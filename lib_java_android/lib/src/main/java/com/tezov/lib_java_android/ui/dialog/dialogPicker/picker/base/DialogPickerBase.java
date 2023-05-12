/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.dialog.dialogPicker.picker.base;

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
import static com.tezov.lib_java_android.ui.dialog.dialogPicker.picker.base.DialogButtonAction.ButtonDetails.DEFAULT_BUTTON_ORDER;
import static com.tezov.lib_java_android.ui.dialog.dialogPicker.picker.base.DialogButtonView.ButtonPosition;
import static com.tezov.lib_java_android.util.UtilsView.Direction;
import static com.tezov.lib_java_android.util.UtilsView.findFirst;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.tezov.lib_java_android.R;
import com.tezov.lib_java.definition.defEntry;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java_android.wrapperAnonymous.ViewOnClickListenerW;
import com.tezov.lib_java.type.collection.ListEntry;
import com.tezov.lib_java.type.primaire.Entry;
import com.tezov.lib_java.type.ref.Ref;
import com.tezov.lib_java.type.ref.SR;
import com.tezov.lib_java.type.ref.WR;
import com.tezov.lib_java_android.ui.dialog.DialogNavigable;

import java.util.Iterator;

public abstract class DialogPickerBase<T> extends DialogNavigable implements defEntry<T>{
final protected static int RES_VIEW_CONTENT = R.id.content;
final private static int RES_LAYOUT_PICKER = R.layout.dialog_picker_base;
final private static int RES_LBL_TITLE = R.id.lbl_title;
private Ref<defEntry<?>> entryWR = null;
private ListEntry<ButtonPosition, DialogButtonView> buttons = null;

@Override
protected void onRestoreState(){
    super.onRestoreState();
    if(!hasParam()){
        return;
    }
    getParam().attachParamToButtonDetails();
}

@Override
protected State newState(){
    return new State();
}

@Override
public State obtainState(){
    return super.obtainState();
}

@Override
public State getState(){
    return (State)super.getState();
}

@Override
public Param obtainParam(){
    return super.obtainParam();
}

@Override
public Param getParam(){
    return super.getParam();
}

@Override
public Method obtainMethod(){
    return super.obtainMethod();
}

@Override
public Method getMethod(){
    return super.getMethod();
}

@Nullable
@Override
public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
    super.onCreateView(inflater, container, savedInstanceState);
    getDialog().getWindow().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
    getDialog().getWindow().setWindowAnimations(R.style.DialogNoAnimation);
    return inflater.inflate(getLayoutResourceId(), container);
}

@Override
protected int getWidth(){
    return ViewGroup.LayoutParams.MATCH_PARENT;
}
@Override
public void onPrepare(boolean hasBeenReconstructed){
    super.onPrepare(hasBeenReconstructed);
    View view = getView();
    FrameLayout contentLayout = view.findViewById(RES_VIEW_CONTENT);
    getLayoutInflater().inflate(getParam().getLayoutID(), contentLayout, true);
    ButtonOnClickListener buttonOnClickLister = new ButtonOnClickListener(this);
    createButtons(view, buttonOnClickLister);
    updateTextViewTitle(view);
}

@Override
public void onOpen(boolean hasBeenReconstructed, boolean hasBeenRestarted){
    super.onOpen(hasBeenReconstructed, hasBeenRestarted);
    updateButtonVisibility();
    if(!hasBeenReconstructed){
        T value = null;
        if(hasEntry()){
            value = getEntry().getValue(getEntryType());
        }
        if(value == null){
            value = getParam().getInitialValue();
        }
        setValue(getEntryType(), value);
    }
}

public FrameLayout getContentView(){
    return getView().findViewById(RES_VIEW_CONTENT);
}

@Override
public boolean onBackPressed(){
    return false;
}

protected int getLayoutResourceId(){
    return RES_LAYOUT_PICKER;
}

private void createButtons(View dialog, ButtonOnClickListener buttonOnClickLister){
    buttons = new ListEntry<>();
    ButtonPosition[] buttonPositions = ButtonPosition.values();
    for(ButtonPosition position: buttonPositions){
        DialogButtonView button = position.findView(dialog);
        if(button.getPosition() != ButtonPosition.OPTION){
            button.newButton(DEFAULT_BUTTON_ORDER, buttonOnClickLister);
        } else {
            Iterator<Entry<DialogButtonAction.Is, DialogButtonAction.ButtonDetails>> iterator = getParam().getButtonsIterator();
            int order = DEFAULT_BUTTON_ORDER;
            while(iterator.hasNext()){
                DialogButtonAction.ButtonDetails ButtonDetails = iterator.next().value;
                if(ButtonDetails.getPosition() != ButtonPosition.OPTION){
                    continue;
                }
                if(ButtonDetails.getOrder() == DEFAULT_BUTTON_ORDER){
                    ButtonDetails.setOrder(order--);
                }
                button.newButton(ButtonDetails.getOrder(), buttonOnClickLister);
            }
        }
        buttons.add(position, button);
    }
}

private <B extends DialogButtonView> B getButton(ButtonPosition position){
    for(Entry<ButtonPosition, DialogButtonView> button: buttons){
        if(button.key == position){
            return (B)button.value;
        }
    }
    return null;
}

private void updateButtonVisibility(){
    for(Entry<ButtonPosition, DialogButtonView> entry: buttons){
        DialogButtonView button = entry.value;
        DialogButtonAction.ButtonDetails ButtonDetails = getParam().getButtonDetails(button.getPosition());
        if((ButtonDetails == null) && (button.getPosition() != ButtonPosition.OPTION)){
            button.showIcon(false);
        }
    }
    Iterator<Entry<DialogButtonAction.Is, DialogButtonAction.ButtonDetails>> iteratorButtonDetails = getParam().getButtonsIterator();
    while(iteratorButtonDetails.hasNext()){
        DialogButtonAction.ButtonDetails buttonDetails = iteratorButtonDetails.next().value;
        if(!buttonDetails.isPositionOwner() && (buttonDetails.getPosition() != ButtonPosition.OPTION)){
            continue;
        }
        int buttonId = buttonDetails.getOrder();
        DialogButtonView button = getButton(buttonDetails.getPosition());
        if(!buttonDetails.isVisible()){
            button.showIcon(buttonId, false);
        } else {
            if(button.getAction(buttonId) != buttonDetails.getAction()){
                button.setAction(buttonId, buttonDetails.getAction());
            }
            button.showIcon(buttonId, true);
            button.setClickable(buttonId, buttonDetails.isEnabled());
        }
    }
    DialogButtonViewOption buttonOption = getButton(ButtonPosition.OPTION);
    buttonOption.sortButtonById();
}

private void updateTextViewTitle(){
    updateTextViewTitle(getView());
}

private void updateTextViewTitle(View dialog){
    String title = obtainParam().getTitle();
    TextView textView = dialog.findViewById(RES_LBL_TITLE);
    if(title != null){
        textView.setText(title);
        textView.setVisibility(View.VISIBLE);
    } else {
        textView.setVisibility(View.GONE);
    }
}

@Override
public void attach(defEntry<?> entry){
    if(entry != null){
        entryWR = new WR<>(entry);
    }
}
@Override
public void link(defEntry<?> entry){
    if(entry != null){
        entryWR = new SR<>(entry);
    }
}

protected boolean hasEntry(){
    return Ref.isNotNull(entryWR);
}

protected defEntry<?> getEntry(){
    return Ref.get(entryWR);
}

@Override
public <T> boolean isAcceptedType(Class<T> type){
    return getEntryType() == type;
}

@Override
public boolean setValueFrom(defEntry<?> entry){
    Class type = entry.getEntryType();
    Object value = entry.getValue(type);
    return setValue(type, value);
}

@Override
public T getValue(){
    return getValue(getEntryType());
}

//STATE
public static class State extends DialogNavigable.State{
    @Override
    protected Param newParam(){
        return new Param();
    }

    @Override
    public Param getParam(){
        return (Param)super.getParam();
    }

    @Override
    public Param obtainParam(){
        return (Param)super.obtainParam();
    }

    @Override
    protected Method newMethod(){
        return new Method();
    }

    @Override
    public Method obtainMethod(){
        return (Method)super.obtainMethod();
    }

}

public static class Param extends DialogNavigable.Param{
    private final ListEntry<DialogButtonAction.Is, DialogButtonAction.ButtonDetails> buttonActionDetails = new ListEntry<>();
    public int layoutID = 0;
    public DialogButtonAction.Creator buttonCreator = null;
    private Object initialValue = null;

    @Override
    public DialogPickerBase getOwner(){
        return super.getOwner();
    }

    @Override
    public Method obtainMethod(){
        return super.obtainMethod();
    }

    public int getLayoutID(){
        return layoutID;
    }

    public Param setLayoutID(int layoutID){
        this.layoutID = layoutID;
        return this;
    }

    public DialogButtonAction.Creator getButtonCreator(){
        return buttonCreator;
    }

    public Param setButtonCreator(DialogButtonAction.Creator creator){
        this.buttonCreator = creator;
        return this;
    }

    @Override
    public Param setTitle(String title){
        super.setTitle(title);
        if(!hasOwner()){
            return this;
        }
        DialogPickerBase dialog = getOwner();
        if(dialog.isVisibleToUser()){
            dialog.updateTextViewTitle();
        }
        return this;
    }

    public <O> O getInitialValue(){
        return (O)initialValue;
    }

    public Param setInitialValue(Object value){

        if(hasOwner()){
            DialogPickerBase dialog = getOwner();
            if((value != null) && (!value.getClass().equals(dialog.getEntryType()))){
DebugException.start().explode("Initial Value Expected type " + dialog.getEntryType().getSimpleName() + " received " + value.getClass().getSimpleName()).end();
            }
        }

        initialValue = value;
        return this;
    }

    protected DialogButtonAction.ButtonDetails getButtonDetails(ButtonPosition position){
        Iterator<Entry<DialogButtonAction.Is, DialogButtonAction.ButtonDetails>> iterator = getButtonsIterator();
        while(iterator.hasNext()){
            DialogButtonAction.ButtonDetails ButtonDetails = iterator.next().value;
            if((position != null) && (ButtonDetails.getPosition() == position) && ButtonDetails.isPositionOwner()){
                return ButtonDetails;
            }
        }
        return null;
    }

    private DialogButtonAction.ButtonDetails getButton(ButtonPosition position, int order){
        Iterator<Entry<DialogButtonAction.Is, DialogButtonAction.ButtonDetails>> iterator = getButtonsIterator();
        while(iterator.hasNext()){
            DialogButtonAction.ButtonDetails ButtonDetails = iterator.next().value;
            if((ButtonDetails.getPosition() == position) && (ButtonDetails.getOrder() == order)){
                return ButtonDetails;
            }
        }
        return null;
    }

    public <B extends DialogButtonAction.ButtonDetails> B getButton(DialogButtonAction.Is action){
        return (B)buttonActionDetails.getValue(action);
    }

    public DialogButtonAction.ButtonDetails obtainButton(DialogButtonAction.Is action){
        DialogButtonAction.ButtonDetails ButtonDetails = getButton(action);
        if(ButtonDetails == null){
            DialogPickerBase dialog = getOwner();
            if((dialog != null) && (dialog.getDialog() != null)){

DebugException.start().explode("Dialog already built, this button " + action.name() + " doesn't exist and can not be created anymore").end();

                return null;
            }
            if(getButtonCreator() == null){

DebugException.start().explode("Button creator is null " + DebugTrack.getFullSimpleName(getOwner())).end();

                return null;
            }
            ButtonDetails = getButtonCreator().create(action);
            ButtonDetails.attach(this);
            this.buttonActionDetails.put(action, ButtonDetails);
        }
        return ButtonDetails;
    }

    private Iterator<Entry<DialogButtonAction.Is, DialogButtonAction.ButtonDetails>> getButtonsIterator(){
        return buttonActionDetails.iterator();
    }

    private void attachParamToButtonDetails(){
        Iterator<Entry<DialogButtonAction.Is, DialogButtonAction.ButtonDetails>> iterator = getButtonsIterator();
        while(iterator.hasNext()){
            iterator.next().value.attach(this);
        }
    }

    public boolean hasButton(DialogButtonAction.Is action){
        return getButton(action) != null;
    }

    public ButtonPosition getButtonPosition(DialogButtonAction.Is action){
        DialogButtonAction.ButtonDetails ButtonDetails = getButton(action);
        if(ButtonDetails == null){
            return null;
        }
        return ButtonDetails.getPosition();
    }

    public boolean isButtonVisible(DialogButtonAction.Is action){
        DialogButtonAction.ButtonDetails buttonDetails = getButton(action);
        if(buttonDetails == null){
            return false;
        }
        return buttonDetails.isVisible();
    }

    public boolean isButtonEnabled(DialogButtonAction.Is action){
        DialogButtonAction.ButtonDetails ButtonDetails = getButton(action);
        if(ButtonDetails == null){
            return false;
        }
        return ButtonDetails.isEnabled();
    }

    public boolean isButtonVisible(ButtonPosition position){
        Iterator<Entry<DialogButtonAction.Is, DialogButtonAction.ButtonDetails>> iterator = getButtonsIterator();
        while(iterator.hasNext()){
            DialogButtonAction.ButtonDetails ButtonDetails = iterator.next().value;
            if((ButtonDetails.getPosition() == position) && ButtonDetails.isVisible()){
                return true;
            }
        }
        return false;
    }

    public boolean isButtonEnabled(ButtonPosition position){
        Iterator<Entry<DialogButtonAction.Is, DialogButtonAction.ButtonDetails>> iterator = getButtonsIterator();
        while(iterator.hasNext()){
            DialogButtonAction.ButtonDetails ButtonDetails = iterator.next().value;
            if((ButtonDetails.getPosition() == position) && ButtonDetails.isEnabled()){
                return true;
            }
        }
        return false;
    }

    public void clearButtonCommand(){
        Iterator<Entry<DialogButtonAction.Is, DialogButtonAction.ButtonDetails>> iterator = getButtonsIterator();
        while(iterator.hasNext()){
            DialogButtonAction.ButtonDetails ButtonDetails = iterator.next().value;
            ButtonDetails.clearCommand();
        }
    }

    public <BOSS> void clearButtonCommand(BOSS boss){
        Iterator<Entry<DialogButtonAction.Is, DialogButtonAction.ButtonDetails>> iterator = getButtonsIterator();
        while(iterator.hasNext()){
            DialogButtonAction.ButtonDetails ButtonDetails = iterator.next().value;
            ButtonDetails.clearCommand(boss);
        }
    }

    @Override
    public DebugString toDebugString(){
        DebugString data = super.toDebugString();
        data.append("initialValue", initialValue);
        if(buttonActionDetails.size() <= 0){
            data.append("ButtonDetails", null);
        } else {
            Iterator<Entry<DialogButtonAction.Is, DialogButtonAction.ButtonDetails>> iterator = getButtonsIterator();
            while(iterator.hasNext()){
                DialogButtonAction.ButtonDetails buttonDetails = iterator.next().value;
                data.append("\n" + buttonDetails.toString());
            }
            data.append("\n");
        }
        return data;
    }

}

public static class Method extends DialogNavigable.Method{
    @Override
    public DialogPickerBase getOwner(){
        return (DialogPickerBase)super.getOwner();
    }
    @Override
    public State getState(){
        return super.getState();
    }
    @Override
    public Param getParam(){
        return super.getParam();
    }
    public void callOnClickButton(DialogButtonAction.Is action){
        DialogButtonAction.ButtonDetails ButtonDetails = getParam().getButton(action);
        if(ButtonDetails == null){
            return;
        }
        if(!ButtonDetails.hasPosition()){
            return;
        }
        getOwner().getButton(ButtonDetails.getPosition()).callOnClick(ButtonDetails.getOrder());
    }
    public void executeCommandButton(DialogButtonAction.Is action){
        DialogButtonAction.ButtonDetails ButtonDetails = getParam().getButton(action);
        if(ButtonDetails == null){
            return;
        }
        ButtonDetails.execute();
    }
    public <T> T get(){
        return (T)getOwner().getValue();
    }

}

private static class ButtonOnClickListener extends ViewOnClickListenerW{
    WR<DialogPickerBase> dialogWR;

    ButtonOnClickListener(DialogPickerBase dialog){
        this.dialogWR = WR.newInstance(dialog);
    }

    DialogPickerBase getDialog(){
        return Ref.get(dialogWR);
    }

    @Override
    public void onClicked(View v){
        DialogPickerBase dialog = getDialog();
        DialogButtonView buttonView = findFirst(DialogButtonView.class, v, Direction.UP);
        DialogButtonAction.ButtonDetails ButtonDetails;
        ButtonPosition position = buttonView.getPosition();
        if(position != ButtonPosition.OPTION){
            ButtonDetails = dialog.getParam().getButtonDetails(position);
        } else {
            ButtonDetails = dialog.getParam().getButton(position, v.getId());
        }
        if((ButtonDetails == null) || !ButtonDetails.isVisible()){
            return;
        }
        ButtonDetails.execute();
        dialog.close();
    }

}

}