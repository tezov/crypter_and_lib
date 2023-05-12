/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.crypter.dialog;

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import com.tezov.lib_java.type.primaire.Pair;

import androidx.fragment.app.Fragment;
import static com.tezov.crypter.application.Environment.EXTENSION_KEYS_BACKUP;
import static com.tezov.crypter.data.table.Descriptions.KEY;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.tezov.crypter.R;
import com.tezov.crypter.application.AppInfo;
import com.tezov.crypter.application.Application;
import com.tezov.crypter.application.Environment;
import com.tezov.crypter.data.item.ItemKey;
import com.tezov.crypter.data.item.ItemKeyRing;
import com.tezov.crypter.data.table.db.dbKeyTable;
import com.tezov.crypter.data_transformation.FileEncoder;
import com.tezov.crypter.data_transformation.PasswordCipherL2;
import com.tezov.crypter.export_import_keys.dbKeyFormatter;
import com.tezov.crypter.misc.ValidatorCredential;
import com.tezov.crypter.user.UserAuth;
import com.tezov.lib_java_android.application.AppContext;
import com.tezov.lib_java_android.application.AppKeyboard;
import com.tezov.lib_java.async.Handler;
import com.tezov.lib_java.async.notifier.observer.event.ObserverEventE;
import com.tezov.lib_java.async.notifier.observer.state.ObserverStateE;
import com.tezov.lib_java.async.notifier.observer.value.ObserverValueE;
import com.tezov.lib_java.cipher.key.KeySim;
import com.tezov.lib_java.cipher.misc.PasswordCipher;
import com.tezov.lib_java.file.File;
import com.tezov.lib_java_android.file.StorageMedia;
import com.tezov.lib_java_android.file.UriW;
import com.tezov.lib_java_android.file.UtilsFile;
import com.tezov.lib_java.generator.uid.UUID;
import com.tezov.lib_java_android.toolbox.PostToHandler;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java_android.wrapperAnonymous.ViewOnClickListenerW;
import com.tezov.lib_java.type.runnable.RunnableW;
import com.tezov.lib_java_android.ui.component.plain.FocusCemetery;
import com.tezov.lib_java_android.ui.dialog.modal.DialogModalRequest;
import com.tezov.lib_java_android.ui.form.component.plain.FormEditText;
import com.tezov.lib_java.data.validator.ValidatorNotEmpty;
import com.tezov.lib_java.util.UtilsString;

public class DialogExportKeyAll extends DialogModalRequest{
private final static String FILE_NAME = "keys";
private ViewState viewState;
private boolean onBackEnabled;
private FormEditText frmPasswordKeystore = null;
private FormEditText frmPasswordExportedKeys = null;

@Override
protected boolean enableScrollbar(){
    return true;
}
@Override
protected int getFrameLayoutId(){
    return R.layout.dialog_ie_keys_request_export;
}
@Override
protected void onFrameMerged(View view, Bundle savedInstanceState){
    viewState = ViewState.REQUEST;
    onBackEnabled = true;
    frmPasswordKeystore = view.findViewById(R.id.frm_password_keystore);
    frmPasswordKeystore.setValidator(new ValidatorCredential());
    frmPasswordKeystore.setText(null);
    frmPasswordExportedKeys = view.findViewById(R.id.frm_password_exported_keys);
    frmPasswordExportedKeys.setValidator(new ValidatorNotEmpty<>());
    frmPasswordExportedKeys.setText(null);

    dbKeyTable.Ref ref = Application.tableHolder().handle().getMainRef(KEY);
    int keyOwnedNot = ref.size(false);
    if(keyOwnedNot <= 0){
        View viewOption = view.findViewById(R.id.container_option_button);
        viewOption.setVisibility(View.GONE);
    } else {
        View viewOptions = view.findViewById(R.id.container_option_buttons);
        Button btnOptionStart = view.findViewById(R.id.btn_option_start);
        Button btnOptionEnd = view.findViewById(R.id.btn_option_end);
        ViewOnClickListenerW onClickOptionListener = new ViewOnClickListenerW(){
            @Override
            public void onClicked(View v){
                if(viewOptions.getVisibility() == View.VISIBLE){
                    viewOptions.setVisibility(View.GONE);
                    btnOptionStart.setActivated(false);
                    btnOptionEnd.setActivated(false);
                } else {
                    viewOptions.setVisibility(View.VISIBLE);
                    btnOptionStart.setActivated(true);
                    btnOptionEnd.setActivated(true);
                    FocusCemetery.request(getView());
                }
            }
        };
        btnOptionEnd.setOnClickListener(onClickOptionListener);
        btnOptionStart.setOnClickListener(onClickOptionListener);
        TextView textViewOwned = view.findViewById(R.id.lbl_key_owned_number);
        textViewOwned.setText(String.valueOf(ref.size(true)));
        TextView textViewOwnedNot = view.findViewById(R.id.lbl_key_owned_not_number);
        textViewOwnedNot.setText(String.valueOf(keyOwnedNot));

    }
    AppKeyboard.show(frmPasswordKeystore);
}
@Override
protected void onConfirm(){
    if(viewState == ViewState.REQUEST){
        FocusCemetery.request(getView());
        if(!frmPasswordKeystore.isValid()){
            frmPasswordKeystore.showError();
            setButtonsEnable(true);
            return;
        }
        PasswordCipherL2 password = PasswordCipherL2.fromCiphered(frmPasswordKeystore.getChars()).scramble(AppInfo.getGUID().toBytes());
        if(!UserAuth.checkCredential(password)){
            frmPasswordKeystore.showError(ValidatorCredential.STRING_WRONG_CREDENTIAL, null);
            setButtonsEnable(true);
            return;
        }
        if(!frmPasswordExportedKeys.isValid()){
            frmPasswordExportedKeys.showError();
            setButtonsEnable(true);
            return;
        }
        StorageMedia.PERMISSION_REQUEST_WRITE(true).observe(new ObserverStateE(this){
            @Override
            public void onComplete(){
                PasswordCipher password = PasswordCipher.fromClear(frmPasswordExportedKeys.getChars());
                frmPasswordKeystore = null;
                frmPasswordExportedKeys = null;
                dataToFile(password);
            }
            @Override
            public void onException(Throwable e){
            }
        });
    } else if(viewState == ViewState.RESULT_SUCCESS){
        postConfirm();
        close();
    } else if(viewState == ViewState.RESULT_EXCEPTION){
        postException();
        close();
    }
}
private void dataToFile(PasswordCipher password){
    Handler.PRIMARY().post(this, new RunnableW(){
        @Override
        public void runSafe(){
            inflateProgress();
            dbKeyFormatter formatter = new dbKeyFormatter();
            String fileName = UtilsString.appendDateAndTime(FILE_NAME);
            formatter.toFile(password, fileName + File.DOT_SEPARATOR + EXTENSION_KEYS_BACKUP, true).observe(new ObserverValueE<File>(this){
                @Override
                public void onComplete(File file){
                    UriW uri = new UriW(file);
                    encodeFile(password, uri);
                }
                @Override
                public void onException(File file, Throwable e){
                    if(file != null){
                        file.delete();
                    }
DebugException.start().log(e).end();
                    inflateResultFailed(AppContext.getResources().getString(R.string.lbl_exception_export_keys));
                }
            });
        }
    });
}
private void encodeFile(PasswordCipher password, UriW uriIn){
    Handler.PRIMARY().post(this, new RunnableW(){
        @Override
        public void runSafe(){
            UUID guid = AppInfo.getGUID();
            ItemKeyRing itemKeyRing = ItemKeyRing.obtain().generateKey(password, guid, KeySim.DEFAULT_TRANSFORMATION, KeySim.DEFAULT_LENGTH).generateRing();
            ItemKey itemKey = ItemKey.obtain().clear().generate(null, guid);
            itemKey.setEncryptOverwriteFile(false);
            itemKey.setEncryptFileName(false);
            itemKey.setEncryptAddTimeAndTimeToFileName(false);
            itemKey.setEncryptDeleteOriginalFile(true);
            itemKey.setEncryptStrictMode(false);
            FileEncoder fileEncoder = new FileEncoder();
            fileEncoder.setItemKey(itemKey, itemKeyRing);
            fileEncoder.observe(new ObserverEventE<FileEncoder.Step, Integer>(this, FileEncoder.Step.FINALISE){
                @Override
                public void onComplete(FileEncoder.Step step, Integer value){
                    inflateResultSuccess(fileEncoder.getUriOut());
                }
                @Override
                public void onException(FileEncoder.Step step, Integer value, Throwable e){
DebugException.start().log(e).end();
                    uriIn.delete();
                    inflateResultFailed(AppContext.getResources().getString(R.string.lbl_exception_export_keys));
                }
            });
            fileEncoder.encode(uriIn, Environment.MediaPath.EXPORTED_KEYS, null);
        }
    });
}
private void inflateProgress(){
    viewState = ViewState.PROGRESS;
    onBackEnabled = false;
    replaceFrameView(R.layout.dialog_ie_keys_progress_indeterminate);
    PostToHandler.of(getView(), new RunnableW(){
        @Override
        public void runSafe(){
            setButtonsVisibility(View.GONE);
        }
    });
}
private void inflateResultFailed(String message){
    viewState = ViewState.RESULT_EXCEPTION;
    onBackEnabled = true;
    replaceFrameView(R.layout.dialog_ie_keys_result, this::onInflatedResultFailed, message);
}
private void onInflatedResultFailed(View view, String message){
    android.widget.FrameLayout imgIcon = view.findViewById(R.id.img_icon);
    Drawable drawable = AppContext.getResources().getDrawable(R.drawable.ic_error_24dp);
    drawable.setTint(AppContext.getResources().getColorARGB(R.color.DarkRed));
    imgIcon.setBackground(drawable);
    TextView lblResult = view.findViewById(R.id.lbl_message);
    lblResult.setText(message);
    PostToHandler.of(getView(), new RunnableW(){
        @Override
        public void runSafe(){
            getBtnConfirm().setEnabled(true);
            getBtnConfirm().setVisibility(View.VISIBLE);
        }
    });
}

private void inflateResultSuccess(UriW uri){
    viewState = ViewState.RESULT_SUCCESS;
    onBackEnabled = true;
    replaceFrameView(R.layout.dialog_ie_keys_result_success_export, this::onInflatedResultSuccess, uri);

}
private void onInflatedResultSuccess(View view, UriW uri){
    TextView lblFileName = view.findViewById(R.id.lbl_file_name);
    lblFileName.setText(uri.getFullName());
    TextView lblFolder = view.findViewById(R.id.lbl_folder);
    Pair<String, String> p = UtilsFile.splitToPathAndFileName(uri.getDisplayPath());
    if(p != null){
        lblFolder.setText(p.first);
    }
    getBtnConfirm().setEnabled(true);
    getBtnConfirm().setVisibility(View.VISIBLE);
}

@Override
public boolean onBackPressed(){
    if(onBackEnabled){
        return super.onBackPressed();
    } else {
        return false;
    }
}

private enum ViewState{
    REQUEST, PROGRESS, RESULT_SUCCESS, RESULT_EXCEPTION
}

}

