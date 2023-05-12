/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.component.preference.custom;

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

import com.tezov.lib_java_android.ui.component.plain.FocusCemetery;
import com.tezov.lib_java_android.ui.form.component.plain.FormEditText;
import com.tezov.lib_java_android.ui.component.preference.EditTextPreference;

import androidx.appcompat.app.AlertDialog;

import com.tezov.lib_java_android.wrapperAnonymous.TextViewOnEditorActionListenerW;
import com.tezov.lib_java_android.util.UtilsView;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceDialogFragmentCompat;

public class DialogPreferenceEditText extends PreferenceDialogFragmentCompat{
private static final String SAVE_STATE_TEXT = "DialogPreferenceEditText.text";
private FormEditText editText;
private CharSequence text;
private DialogPreferenceEditText me(){
    return this;
}
@Override
public void onCreate(Bundle savedInstanceState){
    super.onCreate(savedInstanceState);
    if(savedInstanceState == null){
        text = getEditTextPreference().getText();
    } else {
        text = savedInstanceState.getCharSequence(SAVE_STATE_TEXT);
    }
}
@NonNull
@Override
public Dialog onCreateDialog(Bundle savedInstanceState){
    Dialog dialog = super.onCreateDialog(savedInstanceState);
    ((AlertDialog)dialog).setButton(DialogInterface.BUTTON_POSITIVE, getEditTextPreference().getPositiveButtonText(), new DialogInterface.OnClickListener(){
        @Override
        public void onClick(DialogInterface dialogInterface, int i){
            FocusCemetery.request(editText, UtilsView.Direction.UP);
            me().onClick(dialogInterface, i);
        }
    });
    return dialog;
}
@Override
public void onSaveInstanceState(@NonNull Bundle outState){
    super.onSaveInstanceState(outState);
    outState.putCharSequence(SAVE_STATE_TEXT, text);
}
@Override
protected void onBindDialogView(View view){
    super.onBindDialogView(view);
    editText = view.findViewById(android.R.id.edit);
    editText.addEditorActionListener(new TextViewOnEditorActionListenerW(){
        @Override
        public boolean onAction(EditText textView, int actionId, KeyEvent event){
            if((actionId & EditorInfo.IME_MASK_ACTION) == EditorInfo.IME_ACTION_DONE){
                ((AlertDialog)getDialog()).getButton(DialogInterface.BUTTON_POSITIVE).performClick();
                return true;
            }
            return false;
        }
    });
    editText.link(new FormEditText.EntryStringBase(){
        @Override
        public boolean setValue(String value){
            text = value;
            return true;
        }
        @Override
        public String getValue(){
            if(text != null){
                return text.toString();
            }
            else {
                return null;
            }
        }
    });
    if(getEditTextPreference().getOnBindEditTextListener() != null){
        getEditTextPreference().getOnBindEditTextListener().onBindEditText(editText);
    }
    editText.requestFocus();
}
private EditTextPreference getEditTextPreference(){
    return (EditTextPreference)getPreference();
}
@Override
protected boolean needInputMethod(){
    return true;
}
@Override
public void onDialogClosed(boolean positiveResult){
    if(positiveResult){
        String value = editText.getValue();
        EditTextPreference preference = getEditTextPreference();
        if(preference.callChangeListener(value)){
            preference.setText(value);
        }
    }
}
}
