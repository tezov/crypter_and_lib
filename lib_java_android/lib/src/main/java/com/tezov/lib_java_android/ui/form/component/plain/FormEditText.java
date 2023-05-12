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
import android.text.Editable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import com.tezov.lib_java_android.R;
import com.tezov.lib_java.definition.defEntry;
import com.tezov.lib_java.definition.defValidable;
import com.tezov.lib_java.toolbox.Compare;
import com.tezov.lib_java.toolbox.Nullify;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java_android.wrapperAnonymous.TextViewOnEditorActionListenerW;
import com.tezov.lib_java_android.wrapperAnonymous.TextViewOnFocusChangeListenerW;
import com.tezov.lib_java.type.primitive.string.StringTransformer;
import com.tezov.lib_java.type.ref.Ref;
import com.tezov.lib_java.type.ref.SR;
import com.tezov.lib_java.type.ref.SRwO;
import com.tezov.lib_java.type.ref.WR;
import com.tezov.lib_java_android.ui.component.plain.EditTextLayout;
import com.tezov.lib_java_android.ui.component.plain.EditTextWithIconAction;
import com.tezov.lib_java.data.validator.Validator;
import com.tezov.lib_java_android.ui.misc.AttributeReader;
import com.tezov.lib_java_android.util.UtilsTypeWrapper;

public class FormEditText extends EditTextWithIconAction implements defFormComponent<String>, defValidable{
final static private int[] ATTR_INDEX = R.styleable.EditTextForm_lib;
private final SRwO<String> text = new SRwO<>();
private Integer componentType = null;
private defFormComponent previousForm = null;
private defFormComponent nextForm = null;
private Ref<defEntry<?>> entryWR = null;
private Validator<String, Object> validator = null;
private AttributeReader attributes = null;
private String hintInner = null;
private String hintOuter = null;
private StringTransformer transformer = null;

public FormEditText(Context context){
    super(context);
    init(context, null, 0);
}
public FormEditText(Context context, AttributeSet attrs){
    super(context, attrs);
    init(context, attrs, 0);
}
public FormEditText(Context context, AttributeSet attrs, int defStyleAttr){
    super(context, attrs, defStyleAttr);
    init(context, attrs, 0);
}
private FormEditText me(){
    return this;
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
    observeFocusChanged();
    observeFocusLost();
    if(attrs == null){
        return;
    }
    defFormComponent.super.parseAttribute(context, defFormComponent.class, attrs);
    AttributeReader attributes = new AttributeReader().parse(context, ATTR_INDEX, attrs);
    hintInner = attributes.asString(R.styleable.EditTextForm_lib_hint_inner);
    hintOuter = attributes.asString(R.styleable.EditTextForm_lib_hint_outer);
}

protected void observeFocusLost(){
    addEditorActionListener(new TextViewOnEditorActionListenerW(){
        @Override
        public boolean onAction(EditText textView, int actionId, KeyEvent event){
            int result = actionId & EditorInfo.IME_MASK_ACTION;
            switch(result){
                case EditorInfo.IME_ACTION_DONE:
                case EditorInfo.IME_ACTION_NEXT:{
                    Editable e = getText();
                    String s = e != null ? Nullify.string(e.toString()) : null;
                    if(!Compare.equals(s, getValue())){
                        setValue(String.class, s, false);
                    }
                }
                break;
            }
            return false;
        }
    });
    addFocusChangeListener(new TextViewOnFocusChangeListenerW(){
        @Override
        public void onFocusChange(EditText textView, boolean hasFocus){
            Editable e = getText();
            String s = e != null ? Nullify.string(e.toString()) : null;
            if(!Compare.equals(s, getValue())){
                setValue(String.class, s, false);
            }
        }
    });
}

protected void observeFocusChanged(){
    addFocusChangeListener(new TextViewOnFocusChangeListenerW(){
        @Override
        public void onFocusChange(EditText textView, boolean hasFocus){
            if(hasFocus){
                EditTextLayout textInputLayout = getTextLayout();
                if(textInputLayout != null){
                    if(textInputLayout.getError() != null){
                        showError(textInputLayout, false, null, null);
                    }
                } else {
                    if(getError() != null){
                        showError(null, false, null, null);
                    }
                }
            }
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
        if(entry instanceof defValidable){
            setValidator(((defValidable)entry).getValidator());
        }
        updateText(entry.getValue(getEntryType()), true);
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

public FormEditText setTransformer(StringTransformer transformer){
    this.transformer = transformer;
    return this;
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
    if(form == null){
        setNextFocusUpId(0);
        setNextFocusLeftId(0);
    } else {
        int id = form.getId();
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
    if(form == null){
        setNextFocusDownId(0);
        setNextFocusRightId(0);
        setNextFocusForwardId(0);
        setImeOptions(EditorInfo.IME_ACTION_DONE);
    } else {
        int id = form.getId();
        setNextFocusDownId(id);
        setNextFocusRightId(id);
        setNextFocusForwardId(id);
        setImeOptions(EditorInfo.IME_ACTION_NEXT);
    }
}

@Override
public <S, K, V extends Validator<S, K>> V getValidator(){
    return (V)validator;
}
@Override
public <S, K> FormEditText setValidator(Validator<S, K> validator){
    this.validator = (Validator)validator;
    return this;
}

@Override
public Boolean isValid(){
    if(validator == null){
        return null;
    } else {
        return validator.isValid(text.get());
    }
}

@Override
public Boolean hasChanged(){

DebugException.start().notImplemented().end();

    return null;
}

@Override
public Boolean hasChangedFromInitial(){
    return text.hasChangedFromInitial();
}

@Override
public boolean hasValidator(){
    return validator != null;
}

@Override
public void showError(){
    showError(Compare.isFalse(isValid()), null);
}
public void showError(boolean flag){
    showError(flag, null);
}
public void showError(String extra){
    showError(Compare.isFalse(isValid()), extra);
}
public void showError(boolean flag, String extra){
    showError(flag, null, extra);
}
public <K> void showError(K keyMessage, String extra){
    showError(true, keyMessage, extra);
}
public <K> void showError(boolean flag, K keyMessage, String extra){
    showError(getTextLayout(), flag, keyMessage, extra);
}
private void showError(EditTextLayout textInputLayout, boolean flag, Object keyMessage, String extra){
    if(textInputLayout != null){
        if(flag && (validator != null)){
            String errorMessage = validator.getErrorMessage(getValue(), keyMessage, extra);
            textInputLayout.setHintEnabled(text.get() != null);
            textInputLayout.setError(errorMessage);
        } else {
            textInputLayout.setHintEnabled(true);
            textInputLayout.setErrorEnabled(false);
//            textInputLayout.setError(null);
        }
    } else {
        if(flag && (validator != null)){
            String errorMessage = validator.getErrorMessage(getValue(), keyMessage, extra);
            setError(errorMessage);
        } else {
            setError(null);
        }
    }
}

private void updateHint(boolean innerOrOuter){
    EditTextLayout textInputLayout = getTextLayout();
    if(textInputLayout != null){
        if(innerOrOuter){
            textInputLayout.setHint(hintInner);
        } else {
            textInputLayout.setHint(hintOuter);
        }
    }
}

public void setText(String s){
    setValue(String.class, s);
}
private void updateText(String s, boolean updateEditText){
    s = Nullify.string(s);
    if(transformer != null){
        s = transformer.alter(s);
    }
    text.set(s);
    if(text.hasChanged() || updateEditText){
        if(updateEditText){
            super.setText(s);
        }
        if(validator != null){
            boolean showError = !validator.isValid(s) && text.hasChangedFromInitial();
            showError(showError, null);
        }
        updateHint(s == null);
        text.checked();
    }
}

@Override
protected void onClickActionUp(IconAction mode){
    if(mode == IconAction.CLEAR){
        if(Ref.isNull(entryWR) || getEntry().command(IconAction.CLEAR)){
            setValue(String.class, null);
        }
    } else if(mode == IconAction.ACTION){
        if(Ref.isNull(entryWR) || getEntry().command(IconAction.ACTION)){

        }
    } else {
        super.onClickActionUp(mode);
    }
}

@Override
public Class<String> getEntryType(){
    return String.class;
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

private <T> boolean setValue(Class<T> type, T object, boolean updateEditText){
    if(Ref.isNotNull(entryWR)){
        defEntry<?> entry = entryWR.get();
        if(UtilsTypeWrapper.isAcceptEntryType(entry, type)){
            if(UtilsTypeWrapper.setTo(entry, type, object)){
                updateText(entry.getValue(getEntryType()), updateEditText);
                entry.onSetValue(type);
                return true;
            } else {
                return false;
            }
        }
    }
    if(type == String.class){
        updateText((String)object, updateEditText);
        return true;
    } else {
DebugException.start().unknown("type", type.getName()).end();
        return false;
    }
}

@Override
public String getValue(){
    if(transformer == null){
        return text.get();
    } else {
        return transformer.restore(text.get());
    }
}
@Override
public <T> T getValue(Class<T> type){
    if(Ref.isNotNull(entryWR)){
        defEntry<?> entry = entryWR.get();
        if(UtilsTypeWrapper.isAcceptEntryType(entry, type)){
            return UtilsTypeWrapper.getFrom(entry, type);
        }
    }
    if(type == String.class){
        return (T)getValue();
    } else {
DebugException.start().unknown("type", type.getName()).end();
        return null;
    }

}
@Override
public boolean setValue(String value){
    setValue(String.class, value);
    return true;
}
@Override
public boolean setValueFrom(defEntry<?> entry){
    Class type = entry.getEntryType();
    Object value = entry.getValue(type);
    return UtilsTypeWrapper.setTo(this, type, value);
}

public static abstract class EntryStringBase implements defEntry<String>{
    @Override
    final public Class<String> getEntryType(){
        return String.class;
    }
    @Override
    final public void attach(defEntry entry){
DebugException.start().notImplemented().end();
    }
    @Override
    final public void link(defEntry entry){
DebugException.start().notImplemented().end();
    }
    @Override
    final public <T> boolean isAcceptedType(Class<T> type){
        return type == String.class;
    }
    @Override
    final public <T> boolean setValue(Class<T> type, T value){
        if(type == String.class){
            return setValue((String)value);
        } else {
            return false;
        }
    }
    @Override
    final public boolean setValueFrom(defEntry entry){

DebugException.start().notImplemented().end();
        return false;

    }
    @Override
    final public <T> T getValue(Class<T> type){
        if(type == String.class){
            return (T)getValue();
        } else {
            return null;
        }
    }

}

public static class EntryString extends EntryStringBase{
    protected String value = null;
    @Override
    public boolean setValue(String value){
        this.value = value;
        return true;
    }
    @Override
    public String getValue(){
        return value;
    }
}

}
