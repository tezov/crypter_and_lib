/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.form.component.plain;

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugTrack;
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
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import com.tezov.lib_java.definition.defEntry;
import com.tezov.lib_java.definition.defValidable;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java_android.wrapperAnonymous.ViewOnClickListenerW;
import com.tezov.lib_java.type.ref.Ref;
import com.tezov.lib_java.type.ref.SR;
import com.tezov.lib_java.type.ref.SRwO;
import com.tezov.lib_java.type.ref.WR;
import com.tezov.lib_java_android.ui.component.plain.ButtonMultiIconMaterial;
import com.tezov.lib_java.data.validator.Validator;
import com.tezov.lib_java_android.ui.misc.AttributeReader;
import com.tezov.lib_java_android.util.UtilsTypeWrapper;

public class FormButton extends ButtonMultiIconMaterial implements defFormComponent<Integer>, defValidable{
private final SRwO<Integer> index = new SRwO<>();
private Integer componentType = null;
private defFormComponent previousForm = null;
private defFormComponent nextForm = null;
private Ref<defEntry<?>> entryWR = null;
private AttributeReader attributes = null;

public FormButton(Context context){
    super(context);
    init(context, null, 0);
}

public FormButton(Context context, AttributeSet attrs){
    super(context, attrs);
    init(context, attrs, 0);
}

public FormButton(Context context, AttributeSet attrs, int defStyleAttr){
    super(context, attrs, defStyleAttr);
    init(context, attrs, 0);
}

@Override
public AttributeReader getAttribute(Class type){
    if(type == defFormComponent.class){
        return attributes;
    }
    return null;
}

@Override
public void setAttribute(Class type, AttributeReader attributes){
    if(type == defFormComponent.class){
        this.attributes = attributes;
    }
}

private void init(Context context, AttributeSet attrs, int defStyleAttr){
    initOnClickListener();
    if(attrs == null){
        return;
    }
    defFormComponent.super.parseAttribute(context, defFormComponent.class, attrs);
}

protected void initOnClickListener(){
    setOnClickListener(new ViewOnClickListenerW(){
        @Override
        public void onClicked(View v){
            nextIndex();
            setValue(Integer.class, getIndex(), false);
        }
    });
}

@Override
public void attach(defEntry<?> entry){
    setEntry(entry, true);
}
@Override
public void link(defEntry<?> entry){
    setEntry(entry, false);
}
private void setEntry(defEntry<?> entry, boolean weak){
    if(entry != null){
        if(weak){
            entryWR = new WR<>(entry);
        } else {
            entryWR = new SR<>(entry);
        }
        updateIndex(entry.getValue(getEntryType()), true);
    } else {
        entryWR = null;
    }
}


@Override
public defEntry<?> getEntry(){
    if(Ref.isNull(entryWR)){
        return null;
    }
    return entryWR.get();
}

@Override
public Integer getComponentType(){
    return componentType;
}

@Override
public void setComponentType(Integer type){
    this.componentType = type;
}

@Override
public boolean hasPrevious(){
    return previousForm != null;
}

@Override
public <F extends defFormComponent<?>> F getPrevious(){
    return (F)previousForm;
}

@Override
public void setPreviousForm(defFormComponent<?> form){
    previousForm = form;
    onSetPrevious(form);
}

private void onSetPrevious(defFormComponent<?> form){
    if(form == null){
        setNextFocusUpId(0);
        setNextFocusLeftId(0);
    } else {
        int id = hasPrevious() ? getPrevious().getId() : 0;
        setNextFocusUpId(id);
        setNextFocusLeftId(id);
    }
}

@Override
public boolean hasNext(){
    return nextForm != null;
}

@Override
public <F extends defFormComponent<?>> F getNext(){
    return (F)nextForm;
}

@Override
public void setNextForm(defFormComponent<?> form){
    nextForm = form;
    onSetNext(form);
}

private void onSetNext(defFormComponent<?> form){
    if(form == null){
        setNextFocusDownId(0);
        setNextFocusRightId(0);
        setNextFocusForwardId(0);
        setImeOptions(EditorInfo.IME_ACTION_DONE);
    } else {
        int id = hasNext() ? getNext().getId() : 0;
        setNextFocusDownId(id);
        setNextFocusRightId(id);
        setNextFocusForwardId(id);
        setImeOptions(EditorInfo.IME_ACTION_NEXT);
    }
}

@Override
public <S, K, V extends Validator<S, K>> V getValidator(){
    return null;
}
@Override
public <S, K> defValidable setValidator(Validator<S, K> validator){
DebugException.start().notImplemented().end();
    return null;
}

@Override
public Boolean isValid(){
    return true;
}

@Override
public Boolean hasChanged(){

DebugException.start().notImplemented().end();

    return null;
}

@Override
public Boolean hasChangedFromInitial(){
    return index.hasChangedFromInitial();
}

@Override
public boolean hasValidator(){
    return false;
}

@Override
public void showError(){

}

private void updateIndex(Integer index, boolean updateButton){
    this.index.set(index);
    if(this.index.hasChanged()){
        if(updateButton){
            setIndex(index);
        }
        this.index.checked();
    }
}

@Override
public Class<Integer> getEntryType(){
    return Integer.class;
}

@Override
public <T> boolean isAcceptedType(Class<T> type){
    boolean result = (type == getEntryType());
    if(Ref.isNotNull(entryWR)){
        result = result || getEntry().isAcceptedType(type);
    }
    return result;
}

@Override
public <T> boolean setValue(Class<T> type, T object){
    return setValue(type, object, true);
}

public <T> boolean setValue(Class<T> type, T object, boolean updateButton){
    if(Ref.isNotNull(entryWR)){
        defEntry<?> entry = entryWR.get();
        if(UtilsTypeWrapper.isAcceptEntryType(entry, type)){
            UtilsTypeWrapper.setTo(entry, type, object);
            updateIndex(entry.getValue(getEntryType()), updateButton);
            entry.onSetValue(type);
            return true;
        }
    }
    if(type == Integer.class){
        updateIndex((Integer)object, updateButton);
        return true;
    } else {
DebugException.start().unknown("type", type.getName()).end();
        return false;
    }
}

@Override
public Integer getValue(){
    return index.get();
}

@Override
public <T> T getValue(Class<T> type){
    if(Ref.isNotNull(entryWR)){
        defEntry entry = entryWR.get();
        if(UtilsTypeWrapper.isAcceptEntryType(entry, type)){
            return UtilsTypeWrapper.getFrom(entry, type);
        }
    }
    if(type == Integer.class){
        return (T)getValue();
    } else {

DebugException.start().unknown("type", type.getName()).end();


        return null;
    }
}

@Override
public boolean setValueFrom(defEntry<?> entry){
    Class type = entry.getEntryType();
    Object value = entry.getValue(type);
    return UtilsTypeWrapper.setTo(this, type, value);
}

@Override
public boolean setValue(Integer value){
    setValue(Integer.class, value);
    return true;
}

}
