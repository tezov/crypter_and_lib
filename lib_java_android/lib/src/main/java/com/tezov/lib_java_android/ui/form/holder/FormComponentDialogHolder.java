/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.form.holder;

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
import static com.tezov.lib_java_android.ui.form.adapter.FormManager.Target;

import android.view.View;
import android.view.ViewGroup;

import com.tezov.lib_java_android.definition.defViewContainer;
import com.tezov.lib_java.type.collection.ListEntry;
import com.tezov.lib_java.type.primaire.Entry;
import com.tezov.lib_java_android.ui.dialog.dialogPicker.pickerController.PickerType;
import com.tezov.lib_java_android.ui.form.adapter.FormManager;
import com.tezov.lib_java_android.ui.form.component.dialog.defFormComponentDialog;
import com.tezov.lib_java_android.ui.form.component.plain.defFormComponent;

public class FormComponentDialogHolder<FORM extends FormManager, DEFINITION extends FormComponentDialogHolder.ViewDefinition> extends FormComponentHolder<FORM, DEFINITION>{

public <V extends ViewGroup & defViewContainer> FormComponentDialogHolder(V container){
    super(container);
}

@Override
protected State newState(){
    return new State();
}

@Override
public State getState(){
    return (State)super.getState();
}

@Override
public State obtainState(){
    return (State)super.obtainState();
}

@Override
protected FormComponentHolder newHolder(){
    return new FormComponentDialogHolder(getContainer());
}

@Override
public FormComponentDialogHolder<?, ?> sub(){
    return (FormComponentDialogHolder<?, ?>)super.sub();
}

@Override
public DEFINITION getViewDefinition(){
    return super.getViewDefinition();
}

@Override
protected void onBuildComponent(View view){
    ListEntry<Target.Is, PickerType.Is> types = getViewDefinition().getPickerTypes();
    for(Entry<Target.Is, ? extends defFormComponent> e: getPickers()){
        if(e.value instanceof defFormComponentDialog){
            defFormComponentDialog form = (defFormComponentDialog)e.value;
            form.build(types.getValue(e.key), view);
        }
    }
}

public static abstract class ViewDefinition extends FormComponentHolder.ViewDefinition{
    abstract public ListEntry<Target.Is, PickerType.Is> getPickerTypes();

    @Override
    public ListEntry<Target.Is, ? extends defFormComponentDialog> getPickers(){
        return formHolder.getPickers();
    }

}

public static class State extends FormComponentHolder.State{
    ListEntry<Target.Is, defFormComponentDialog.State> pickerStates = null;

    @Override
    public FormComponentDialogHolder getOwner(){
        return (FormComponentDialogHolder)super.getOwner();
    }

    @Override
    protected void build(){
        pickerStates = new ListEntry<Target.Is, defFormComponentDialog.State>();
        ListEntry<Target.Is, defFormComponentDialog> pickers = getOwner().getPickers();
        for(Entry<Target.Is, PickerType.Is> e: getOwner().getViewDefinition().getPickerTypes()){
            pickerStates.add(e.key, pickers.getValue(e.key).getState());
        }
        super.build();
    }

    @Override
    void restore(){
        ListEntry<Target.Is, defFormComponentDialog> pickers = getOwner().getPickers();
        for(Entry<Target.Is, defFormComponentDialog.State> e: pickerStates){
            pickers.getValue(e.key).restoreState(e.value);
        }
    }

    @Override
    void clear(){
        pickerStates = null;
        super.clear();
    }

}

}
