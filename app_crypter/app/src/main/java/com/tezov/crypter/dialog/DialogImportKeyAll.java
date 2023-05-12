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
import com.tezov.lib_java.util.UtilsString;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import com.tezov.lib_java_android.application.AppContext;

import androidx.fragment.app.Fragment;
import static com.tezov.crypter.application.Environment.EXTENSION_KEYS_BACKUP;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tezov.crypter.R;
import com.tezov.crypter.data.item.ItemKey;
import com.tezov.crypter.data.item.ItemKeyRing;
import com.tezov.crypter.data_transformation.FileDecoder;
import com.tezov.crypter.export_import_keys.dbKeyFormatter;
import com.tezov.lib_java_android.application.AppDisplay;
import com.tezov.lib_java_android.application.AppKeyboard;
import com.tezov.lib_java.async.Handler;
import com.tezov.lib_java.async.notifier.observer.event.ObserverEventE;
import com.tezov.lib_java.async.notifier.observer.state.ObserverStateE;
import com.tezov.lib_java.async.notifier.observer.value.ObserverValueE;
import com.tezov.lib_java.cipher.misc.PasswordCipher;
import com.tezov.lib_java.file.Directory;
import com.tezov.lib_java.file.File;
import com.tezov.lib_java_android.file.StorageMedia;
import com.tezov.lib_java_android.file.UriW;
import com.tezov.lib_java_android.toolbox.PostToHandler;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java_android.wrapperAnonymous.ViewOnClickListenerW;
import com.tezov.lib_java.type.collection.ListOrObject;
import com.tezov.lib_java.type.runnable.RunnableW;
import com.tezov.lib_java_android.ui.component.plain.ButtonMultiIconMaterial;
import com.tezov.lib_java_android.ui.component.plain.FocusCemetery;
import com.tezov.lib_java_android.ui.dialog.modal.DialogModalRequest;
import com.tezov.lib_java_android.ui.form.component.plain.FormEditText;
import com.tezov.lib_java.data.validator.ValidatorFileExtension;
import com.tezov.lib_java.data.validator.ValidatorNotEmpty;
import com.tezov.lib_java_android.util.UtilsTextView;

public class DialogImportKeyAll extends DialogModalRequest{
private ViewState viewState;
private FormEditText frmPassword = null;
private FormEditText frmFileName = null;
private ButtonMultiIconMaterial btnFilePick = null;
private UriW uriIn = null;
private DialogImportKeyAll me(){
    return this;
}

@Override
protected int getFrameLayoutId(){
    return R.layout.dialog_ie_keys_request_import;
}
@Override
protected void onFrameMerged(View view, Bundle savedInstanceState){
    viewState = ViewState.REQUEST;
    frmFileName = view.findViewById(R.id.lbl_file_name_in);
    frmFileName.setValidator(new ValidatorFileExtension(EXTENSION_KEYS_BACKUP));
    frmFileName.setText(null);
    frmFileName.addOnClickListener(new ViewOnClickListenerW(){
        @Override
        public void onClicked(View v){
            selectFile();
        }
    });

    frmPassword = view.findViewById(R.id.frm_password);
    frmPassword.setValidator(new ValidatorNotEmpty<>());
    frmPassword.setText(null);
    AppKeyboard.show(frmPassword);
    btnFilePick = view.findViewById(R.id.btn_select_file);
    btnFilePick.setOnClickListener(new ViewOnClickListenerW(){
        @Override
        public void onClicked(View v){
            selectFile();
        }
    });
}
@Override
protected void onConfirm(){
    if(viewState == ViewState.REQUEST){
        FocusCemetery.request(getView());
        if(!frmPassword.isValid() || !frmFileName.isValid()){
            if(!frmPassword.isValid()){
                frmPassword.showError();
            }
            if(!frmFileName.isValid()){
                frmFileName.showError();
            }
            setButtonsEnable(true);
            return;
        }
        PasswordCipher password = PasswordCipher.fromClear(frmPassword.getChars());
        frmPassword = null;
        frmFileName = null;
        btnFilePick = null;
        decodeFile(password);

    } else if(viewState == ViewState.RESULT_SUCCESS){
        postConfirm();
        close();
    } else if(viewState == ViewState.RESULT_EXCEPTION){
        postException();
        close();
    }
}

private void selectFile(){
    StorageMedia.openDocument(false).observe(new ObserverValueE<ListOrObject<UriW>>(this){
        @Override
        public void onComplete(ListOrObject<UriW> uris){
            if(uris.size() > 1){
DebugException.start().log("multiple uri ignored").end();
            }
            UriW uri = uris.get();
            boolean isFileAccepted = false;
            String fileFullName = uri.getFullName();
            if(fileFullName != null){
                frmFileName.setText(fileFullName);
                isFileAccepted = frmFileName.isValid();
            }
            if(isFileAccepted){
                me().uriIn = uri;
                btnFilePick.setIndex(1);
            } else {
                me().uriIn = null;
                btnFilePick.setIndex(0);
            }
        }
        @Override
        public void onException(ListOrObject<UriW> uri, Throwable e){
        }
    });
}

private void decodeFile(PasswordCipher password){
    Handler.PRIMARY().post(this, new RunnableW(){
        @Override
        public void runSafe(){
            inflateProgress();
            FileDecoder.ItemKeyMaker itemKeyMaker = new FileDecoder.ItemKeyMaker(){
                @Override
                protected void rebuildKey() throws Throwable{
                    ItemKey itemKey = ItemKey.obtain().clear().generate(null, getGuidKey());
                    ItemKeyRing itemKeyRing = ItemKeyRing.obtain().clear();
                    itemKeyRing.rebuildKeyKey(password, getGuidKey(), getSpecKey());
                    itemKey.setDecryptOverwriteFile(false);
                    itemKey.setDecryptDeleteEncryptedFile(false);
                    setItemKey(itemKey, itemKeyRing);
                }
            };
            try{
                Directory cacheDirectory = dbKeyFormatter.newCacheDirectory(dbKeyFormatter.Direction.IMPORT);
                File file = new File(cacheDirectory, uriIn.getFullName());
                UriW uriOut = new UriW(file);
                FileDecoder fileDecoder = new FileDecoder();
                fileDecoder.setItemKeyMaker(itemKeyMaker);
                fileDecoder.observe(new ObserverEventE<FileDecoder.Step, Integer>(this, FileDecoder.Step.FINALISE){
                    @Override
                    public void onComplete(FileDecoder.Step step, Integer value){
                        fileToData(password, fileDecoder.getUriOut(), cacheDirectory);
                    }
                    @Override
                    public void onException(FileDecoder.Step step, Integer value, Throwable e){
DebugException.start().log(e).end();
                        inflateResultFailed(AppContext.getResources().getString(R.string.lbl_exception_import_keys));
                    }
                });
                fileDecoder.decode(me().uriIn, uriOut);
            } catch(Throwable e){
                inflateResultFailed(AppContext.getResources().getString(R.string.lbl_exception_import_keys));
            }
        }
    });
}
private void fileToData(PasswordCipher password, UriW uri, Directory cacheDirectory){
    Handler.PRIMARY().post(this, new RunnableW(){
        @Override
        public void runSafe(){
            dbKeyFormatter formatter = new dbKeyFormatter();
            formatter.fromUri(password, uri, true, cacheDirectory).observe(new ObserverStateE(this){
                @Override
                public void onComplete(){
                    uri.delete();
                    inflateResultSuccess();
                }
                @Override
                public void onException(Throwable e){
DebugException.start().log(e).end();
                    uri.delete();
                    inflateResultFailed(AppContext.getResources().getString(R.string.lbl_exception_import_keys));
                }
            });
        }
    });
}

private void inflateProgress(){
    viewState = ViewState.PROGRESS;
    setCancelable(false);
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
    setCancelable(true);
    replaceFrameView(R.layout.dialog_ie_keys_result, this::onInflateResultFailed, message);

}
private void onInflateResultFailed(View view, String message){
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
private void inflateResultSuccess(){
    viewState = ViewState.RESULT_SUCCESS;
    setCancelable(true);
    replaceFrameView(R.layout.dialog_ie_keys_result, this::onInflatedResultSuccess);

}
private void onInflatedResultSuccess(View view, Void v){
    android.widget.FrameLayout imgIcon = view.findViewById(R.id.img_icon);
    Drawable drawable = AppContext.getResources().getDrawable(R.drawable.ic_success_24dp);
    drawable.setTint(AppContext.getResources().getColorARGB(R.color.DarkGreen));
    imgIcon.setBackground(drawable);
    TextView lblMessage = view.findViewById(R.id.lbl_message);
    UtilsTextView.setFileNameAndTruncate(lblMessage, uriIn.getFullName());
    PostToHandler.of(getView(), new RunnableW(){
        @Override
        public void runSafe(){
            getBtnConfirm().setEnabled(true);
            getBtnConfirm().setVisibility(View.VISIBLE);
        }
    });
}
@Override
public void onPrepare(boolean hasBeenReconstructed){
    super.onPrepare(hasBeenReconstructed);
    getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    getDialog().getWindow().setLayout((int)(AppDisplay.getSizeOriented().getWidth() * 0.9f), ViewGroup.LayoutParams.WRAP_CONTENT);
}

private enum ViewState{
    REQUEST, PROGRESS, RESULT_SUCCESS, RESULT_EXCEPTION
}

}

