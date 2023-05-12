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

import com.tezov.lib_java_android.ui.component.plain.EditTextWithIconAction.IconAction;
import com.tezov.lib_java_android.toolbox.PostToHandler;
import com.tezov.lib_java.type.runnable.RunnableW;
import com.tezov.lib_java_android.R;
import com.tezov.lib_java.async.notifier.observer.value.ObserverValueE;
import com.tezov.lib_java_android.file.StorageTree;
import com.tezov.lib_java_android.file.UriW;
import com.tezov.lib_java.type.collection.ListOrObject;
import com.tezov.lib_java_android.wrapperAnonymous.ViewOnClickListenerW;
import com.tezov.lib_java_android.ui.component.plain.ButtonMultiIconMaterial;
import com.tezov.lib_java_android.ui.component.preference.DirectoryPreference;
import com.tezov.lib_java_android.ui.form.component.plain.FormEditText;

import androidx.appcompat.app.AlertDialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.SoundEffectConstants;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceDialogFragmentCompat;

public class DialogPreferenceDirectory extends PreferenceDialogFragmentCompat{
private static final long DELAY_PERFORM_CLICK_ms = 100;
private static final String SAVE_STATE_TEXT = "DialogPreferenceDirectory.text";
private CharSequence text;
private FormEditText editText;
private ViewOnClickListenerW onClickListener = null;
protected ButtonMultiIconMaterial btnSelectFolder = null;

@Override
public void onCreate(Bundle savedInstanceState){
    super.onCreate(savedInstanceState);
    if(savedInstanceState == null){
        text = getFolderPreference().getText();
    } else {
        text = savedInstanceState.getCharSequence(SAVE_STATE_TEXT);
    }
}
@NonNull
@Override
public Dialog onCreateDialog(Bundle savedInstanceState){
    Dialog dialog = super.onCreateDialog(savedInstanceState);
    ((AlertDialog)dialog).setButton(DialogInterface.BUTTON_POSITIVE, null, (DialogInterface.OnClickListener)null);
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
    onClickListener = new ViewOnClickListenerW(){
        @Override
        public void setEnabled(boolean enabled){
            super.setEnabled(enabled);
            editText.setClickableIcon(enabled);
        }
        @Override
        public void onClicked(View v){
            setEnabled(false);
            selectDirectory();
        }
    };
    editText = view.findViewById(android.R.id.edit);
    editText.addOnClickListener(onClickListener);
    editText.link(new FormEditText.EntryStringBase(){
        @Override
        public boolean setValue(String value){
            text = value;
            return true;
        }
        @Override
        public String getValue(){
            String displayName = null;
            if(text != null){
                StorageTree uri = StorageTree.fromLink(text.toString());
                if(uri != null){
                    displayName = uri.getDisplayPath();
                }
            }
            return displayName;
        }
        @Override
        public <T> void onSetValue(Class<T> type){
            PostToHandler.of(((AlertDialog)getDialog()).getButton(DialogInterface.BUTTON_POSITIVE), DELAY_PERFORM_CLICK_ms, new RunnableW(){
                @Override
                public void runSafe(){
                    onClick(null, DialogInterface.BUTTON_POSITIVE);
                    dismiss();
                }
            });
        }
        @Override
        public boolean command(Object o){
            if(o instanceof IconAction){
                IconAction action = (IconAction)o;
                if(action == IconAction.CLEAR){
                    editText.playSoundEffect(SoundEffectConstants.CLICK);
                }
            }
            return super.command(o);
        }
    });
    btnSelectFolder = view.findViewById(R.id.btn_select_folder);
    btnSelectFolder.setOnClickListener(onClickListener);
}
private void selectDirectory(){
    StorageTree.openDocumentTree().observe(new ObserverValueE<ListOrObject<UriW>>(this){
        @Override
        public void onComplete(ListOrObject<UriW> uris){
            editText.setValue(String.class, StorageTree.toLink(uris.get()));
            onClickListener.setEnabled(true);
        }
        @Override
        public void onException(ListOrObject<UriW> uriWS, Throwable e){
            onClickListener.setEnabled(true);
        }
    });
}
private DirectoryPreference getFolderPreference(){
    return (DirectoryPreference)getPreference();
}
@Override
protected boolean needInputMethod(){
    return false;
}
@Override
public void onDialogClosed(boolean positiveResult){
    if(positiveResult){
        String value = null;
        if(text != null){
            value = text.toString();
        }
        DirectoryPreference preference = getFolderPreference();
        if(preference.callChangeListener(value)){
            preference.setText(value);
        }
    }
}
}
