/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.dialog.dialogPicker.picker;

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
import static com.tezov.lib_java.type.defEnum.Event.ON_KEYBOARD_DONE;

import android.os.Bundle;
import android.os.Parcelable;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import androidx.annotation.Nullable;

import com.tezov.lib_java_android.R;
import com.tezov.lib_java_android.application.AppKeyboard;
import com.tezov.lib_java.async.Handler;
import com.tezov.lib_java.toolbox.Nullify;
import com.tezov.lib_java_android.toolbox.PostToHandler;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java_android.type.android.ViewTreeEvent;
import com.tezov.lib_java_android.wrapperAnonymous.EditTextOnTextChangedListenerW;
import com.tezov.lib_java_android.wrapperAnonymous.TextViewOnEditorActionListenerW;
import com.tezov.lib_java.type.primitive.string.StringTransformer;
import com.tezov.lib_java.type.runnable.RunnableSubscription;
import com.tezov.lib_java.type.runnable.RunnableW;
import com.tezov.lib_java_android.ui.component.plain.EditTextWithIconAction;
import com.tezov.lib_java_android.ui.dialog.dialogPicker.picker.base.DialogPickerBase;

public class DialogPickerString extends DialogPickerBase<String>{
private EditTextWithIconAction editText = null;
private RunnableSubscription subscription = null;

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
public Param getParam(){
    return (Param)super.getParam();
}

@Override
public Param obtainParam(){
    return (Param)super.obtainParam();
}

@Override
public Class<String> getEntryType(){
    return String.class;
}

@Nullable
@Override
public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
    View view = super.onCreateView(inflater, container, savedInstanceState);
    getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    return view;
}

@Override
public void onPrepare(boolean hasBeenReconstructed){
    super.onPrepare(hasBeenReconstructed);
    editText = getContentView().findViewById(R.id.edit_text);
    editText.addEditorActionListener(new TextViewOnEditorActionListenerW(){
        @Override
        public boolean onAction(EditText textView, int actionId, KeyEvent event){
            int result = actionId & EditorInfo.IME_MASK_ACTION;
            switch(result){
                case EditorInfo.IME_ACTION_DONE:
                    post(ON_KEYBOARD_DONE, EditorInfo.IME_ACTION_DONE);
                    break;
                case EditorInfo.IME_ACTION_NEXT:
                    post(ON_KEYBOARD_DONE, EditorInfo.IME_ACTION_NEXT);
                    break;
            }
            return false;
        }
    });
    EditTextOnTextChangedListenerW textWatcher = getParam().textWatcher;
    if(textWatcher != null){
        editText.addTextChangedListener(textWatcher);
    }
    if(hasBeenReconstructed){
        editText.onRestoreInstanceState(getState().editTextSavedInstance);
        getState().editTextSavedInstance = null;
    }
}

@Override
public void onOpen(boolean hasBeenReconstructed, boolean hasBeenRestarted){
    super.onOpen(hasBeenReconstructed, hasBeenRestarted);
    updateKeyBoard();
    if(!hasBeenReconstructed){
        showKeyBoard();
    }
}

private void updateKeyBoard(){
    editText.setImeOptions(getParam().actionButtonType);
    editText.setInputType(getParam().inputType);
    editText.requestFocus();
}

private void showKeyBoard(){
    if(subscription != null){
        subscription.unsubscribe();
    }
    subscription = new RunnableSubscription(){
        @Override
        public void onComplete(){
            unsubscribe();
            subscription = null;
            PostToHandler.of(editText, Handler.Delay.SHORT.millisecond(), new RunnableW(){
                @Override
                public void runSafe(){
                    editText.requestFocus();
                    AppKeyboard.show(editText);
                }
            });
        }
    };
    ViewTreeEvent.onLayout(editText, subscription);
}

@Override
public void onSaveInstanceState(Bundle savedInstanceState){
    super.onSaveInstanceState(savedInstanceState);
    getState().editTextSavedInstance = editText.onSaveInstanceState();
}

@Override
public String getValue(){
    StringTransformer transformer = getParam().transformer;
    if(transformer == null){
        return Nullify.string(editText.getText());
    } else {
        return transformer.restore(Nullify.string(editText.getText()));
    }
}

@Override
public boolean setValue(String s){
    StringTransformer transformer = getParam().transformer;
    if(transformer != null){
        s = transformer.alter(s);
    }
    editText.setText(s);
    if(s != null){
        editText.moveToEnd();
    }
    return true;
}

@Override
public <T> boolean setValue(Class<T> type, T object){
    if(type == getEntryType()){
        setValue((String)object);
        return true;
    } else {
DebugException.start().unknown("type", type.getName()).end();
        return false;
    }


}

@Override
public <T> T getValue(Class<T> type){
    if(type == getEntryType()){
        return (T)getValue();
    } else {

DebugException.start().unknown("type", type.getName()).end();

        return null;
    }

}

@Override
public void onDestroy(){
    if(subscription != null){
        subscription.unsubscribe();
    }
    super.onDestroy();
}

public static class State extends DialogPickerBase.State{
    private Parcelable editTextSavedInstance = null;

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

}

public static class Param extends DialogPickerBase.Param{
    public int actionButtonType = EditorInfo.IME_ACTION_DONE;
    public int inputType = InputType.TYPE_CLASS_TEXT;
    public StringTransformer transformer = null;
    public EditTextOnTextChangedListenerW textWatcher = null;

    public Param(){
        setLayoutID(R.layout.dialog_picker_string);
    }

    public StringTransformer getTransformer(){
        return transformer;
    }

    public Param setTransformer(StringTransformer transformer){
        this.transformer = transformer;
        return this;
    }

    public EditTextOnTextChangedListenerW getTextWatcher(){
        return textWatcher;
    }

    public Param setTextWatcher(EditTextOnTextChangedListenerW textWatcher){
        this.textWatcher = textWatcher;
        return this;
    }

    public int getActionButtonType(){
        return actionButtonType;
    }

    public Param setActionButtonType(int actionButtonType){
        this.actionButtonType = actionButtonType;
        return this;
    }

    public int getInputType(){
        return inputType;
    }

    public Param setInputType(int inputType){
        this.inputType = inputType;
        return this;
    }

    @Override
    public DebugString toDebugString(){
        DebugString data = super.toDebugString();
        data.append("actionButtonType", actionButtonType);
        data.append("inputType", inputType);
        return data;
    }

}

}