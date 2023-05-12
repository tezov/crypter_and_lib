/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.form.adapter;

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
import com.tezov.lib_java.definition.defEntry;
import com.tezov.lib_java.definition.defValidable;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.wrapperAnonymous.BiPredicateW;
import com.tezov.lib_java.type.ref.Ref;
import com.tezov.lib_java.type.ref.SRwO;
import com.tezov.lib_java.type.ref.WR;
import com.tezov.lib_java.data.validator.Validator;

public abstract class FormAdapter<T> implements defEntry<T>, defValidable{
private final FormManager.Target.Is target;
private final SRwO<T> value;
private WR<FormManager> formManagerWR = null;
private WR<defEntry<?>> entryWR = null;
private Validator validator = null;
private Class<?> onSetType = null;
private Boolean isValid = null;
private boolean hasBeenReplace = false;

public FormAdapter(FormManager.Target.Is target){
DebugTrack.start().create(this).end();
    value = new SRwO<>();
    this.target = target;
}

public FormAdapter<T> setPredicate(BiPredicateW<T, T> predicate){
    value.setPredicate(predicate);
    return this;
}

@Override
public void attach(defEntry<?> entry){
    if(entry != null){
        this.entryWR = WR.newInstance(entry);
    } else {
        entryWR = null;
    }
}
@Override
public void link(defEntry<?> entry){
DebugException.start().notImplemented().end();
}
public void attach(FormManager formManager){
    if(formManager != null){
        this.formManagerWR = WR.newInstance(formManager);
    } else {
        formManagerWR = null;
    }
}

public FormManager getFormManager(){
    return Ref.get(formManagerWR);
}

public <T extends FormManager.Target.Is> T getTarget(){
    return (T)target;
}

public Class<?> getOnSetType(){
    return onSetType;
}

protected boolean entryIsNotNull(){
    return Ref.isNotNull(entryWR);
}

protected defEntry<?> getEntry(){
    return entryWR.get();
}

@Override
public <T> void onSetValue(Class<T> type){
    onSetType = type;
    if(validator != null){
        isValid = validator.isValid(valueToString());
    }
    FormManager formManager = getFormManager();
    if((formManager != null) && formManager.isReady() && !hasBeenReplace){
        formManager.notifySetValue(this);
    }
    hasBeenReplace = false;
}

public boolean isNULL(){
    return value.isNull();
}

public boolean isNotNULL(){
    return value.isNotNull();
}

protected abstract String valueToString();

public boolean setValue(T value){
    this.value.set(value);
    if(validator != null){
        isValid = validator.isValid(valueToString());
    }
    return true;
}

public void replace(T object){
    if((object != null) && (object.getClass() != getEntryType())){
        setValue((Class<Object>)object.getClass(), object);
    } else {
        setValue(object);
    }
    if(entryIsNotNull()){
        hasBeenReplace = true;
        getEntry().setValueFrom(this);
    }
}

@Override
public boolean setValueFrom(defEntry entry){
    return setValue(entry.getEntryType(), entry.getValue());
}

@Override
public T getValue(){
    return value.get();
}

@Override
public <S, K, V extends Validator<S, K>> V getValidator(){
    return (V)validator;
}

@Override
public <S, K> FormAdapter<T> setValidator(Validator<S, K> validator){
    this.validator = validator;
    return this;
}

@Override
public boolean hasValidator(){
    return validator != null;
}

@Override
public Boolean isValid(){
    return isValid;
}

@Override
public Boolean hasChanged(){
    boolean isChanged = value.hasChanged();
    value.checked();
    return isChanged;
}

@Override
public Boolean hasChangedFromInitial(){
    return value.hasChangedFromInitial();
}

public DebugString toDebugString(){
    DebugString data = new DebugString();
    data.append("target.name()", target.name());
    data.append("valid", isValid());
    data.append("hasChangedFromInitial()", hasChangedFromInitial());
    data.appendFullSimpleName("validator", validator);
    return data;
}

final public void toDebugLog(){
DebugLog.start().send(toDebugString()).end();
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

}
