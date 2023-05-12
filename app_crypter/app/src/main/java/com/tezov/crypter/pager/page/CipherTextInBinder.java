/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.crypter.pager.page;

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
import com.tezov.crypter.fragment.FragmentCipherFile;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java_android.ui.navigation.NavigatorManager;

import androidx.fragment.app.Fragment;
import static com.tezov.crypter.application.Environment.EXTENSION_CIPHER_TEXT;
import static com.tezov.crypter.navigation.NavigationHelper.DestinationKey.CIPHER_FILE;
import static com.tezov.crypter.pager.PagerCipherTextTabManager.ViewType.IN;
import static com.tezov.lib_java_android.application.AppContext.getActivity;
import static com.tezov.lib_java_android.ui.navigation.NavigatorManager.NavigatorKey.FRAGMENT;
import static com.tezov.lib_java.util.UtilsList.NULL_INDEX;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.tezov.crypter.R;
import com.tezov.crypter.activity.activityFilter.ActivityFilterDispatcher;
import com.tezov.crypter.application.Environment;
import com.tezov.crypter.data_transformation.DataQr;
import com.tezov.crypter.dialog.DialogQrScan;
import com.tezov.crypter.fragment.FragmentCipherText;
import com.tezov.crypter.navigation.NavigationArguments;
import com.tezov.crypter.pager.PagerCipherTextTabManager;
import com.tezov.lib_java_android.application.AppClipboard;
import com.tezov.lib_java.async.Handler;
import com.tezov.lib_java.async.notifier.observer.event.ObserverEvent;
import com.tezov.lib_java.async.notifier.observer.value.ObserverValue;
import com.tezov.lib_java.async.notifier.observer.value.ObserverValueE;
import com.tezov.lib_java_android.file.StorageMedia;
import com.tezov.lib_java_android.file.UriW;
import com.tezov.lib_java_android.file.UtilsFile;
import com.tezov.lib_java.toolbox.Nullify;
import com.tezov.lib_java_android.toolbox.PostToHandler;
import com.tezov.lib_java_android.annotation.ProguardFieldKeep;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java_android.wrapperAnonymous.EditTextOnTextChangedListenerW;
import com.tezov.lib_java_android.wrapperAnonymous.TextViewOnFocusChangeListenerW;
import com.tezov.lib_java_android.wrapperAnonymous.ViewOnClickListenerW;
import com.tezov.lib_java.type.collection.ListOrObject;
import com.tezov.lib_java.type.defEnum.Event;
import com.tezov.lib_java.type.primitive.BytesTo;
import com.tezov.lib_java.type.runnable.RunnableTimeOut;
import com.tezov.lib_java.type.runnable.RunnableW;
import com.tezov.lib_java_android.ui.activity.ActivityToolbar;
import com.tezov.lib_java_android.ui.component.plain.ButtonIconMaterial;
import com.tezov.lib_java_android.ui.component.plain.ButtonMultiIconMaterial;
import com.tezov.lib_java_android.ui.component.plain.EditText;
import com.tezov.lib_java_android.ui.dialog.modal.DialogModalProgressIndeterminate;
import com.tezov.lib_java_android.ui.layout_wrapper.GlassAnimInOut;
import com.tezov.lib_java_android.ui.misc.StateView;
import com.tezov.lib_java_android.ui.misc.StateView.Feature;
import com.tezov.lib_java_android.ui.navigation.Navigate;

import java.io.ByteArrayOutputStream;


public class CipherTextInBinder extends CipherTextBinder{
private final static long UNDO_ALLOWED_DELAY_ms = 3000;
private DataTextIn dataText = null;
private EditText frmText = null;
private ButtonMultiIconMaterial btnEncrypt = null;
private ButtonMultiIconMaterial btnDecrypt = null;
private ButtonMultiIconMaterial btnPaste = null;
private ButtonIconMaterial btnSelect = null;
private ButtonMultiIconMaterial btnErase = null;
private ButtonIconMaterial btnScan = null;
private EditTextOnTextChangedListenerW textChangeListenerAfterOperationCompleted = null;
private TextWatcherExternalPasted textWatcherExternalPasted = null;
@ProguardFieldKeep
private ButtonsFadeListener buttonsFadeListener = null;

public CipherTextInBinder(FragmentCipherText fragment, PagerCipherTextTabManager pageManager){
    super(fragment, pageManager);
}
@Override
public PagerCipherTextTabManager.ViewType.Is getViewType(){
    return IN;
}
@Override
protected int getLayoutId(){
    return R.layout.pager_tab_cipher_text_in;
}

@Override
protected void bindView(View itemView){
    super.bindView(itemView);
    fragment().bindView(itemView);
    frmText = itemView.findViewById(R.id.frm_text_in);
    frmText.addFocusChangeListener(new TextViewOnFocusChangeListenerW(){
        @Override
        public void onFocusChange(android.widget.EditText textView, boolean hasFocus){
            if(!hasFocus){
                notifyUpdate();
            }
        }
    });
    textWatcherExternalPasted = new TextWatcherExternalPasted();
    frmText.addTextWatcherListener(textWatcherExternalPasted);
    btnEncrypt = itemView.findViewById(R.id.btn_encrypt);
    btnEncrypt.setOnClickListener(new ViewOnClickListenerW(){
        @Override
        public void onClicked(View v){
            FragmentCipherText fragment = fragment();
            if(fragment.disableButtons()){
                fragment.startEncrypt();
            }
        }
    });
    btnEncrypt.setIndex(0);
    btnDecrypt = itemView.findViewById(R.id.btn_decrypt);
    btnDecrypt.setOnClickListener(new ViewOnClickListenerW(){
        @Override
        public void onClicked(View v){
            FragmentCipherText fragment = fragment();
            if(fragment.disableButtons()){
                fragment.startDecrypt();
            }
        }
    });
    btnDecrypt.setIndex(0);

    btnErase = itemView.findViewById(R.id.btn_erase);
    btnErase.setOnClickListener(new ViewOnClickListenerW(){
        DataTextIn erasedDataIn = null;
        RunnableTimeOut runnable = null;
        @Override
        public void onClicked(View v){
            FragmentCipherText fragment = fragment();
            if(fragment.disableButtons()){
                if(btnErase.getIndex() == 1){
                    restore();
                } else if(btnErase.getIndex() == 0){
                    erase();
                }
            }
        }
        void erase(){
            erasedDataIn = get();
            if((erasedDataIn != null) && (Nullify.string(erasedDataIn.getText()) != null)){
                set(null);
                notifyUpdate();
                btnErase.setIndex(1);
                runnable = new RunnableTimeOut(this, UNDO_ALLOWED_DELAY_ms){
                    @Override
                    public void onTimeOut(){
                        PostToHandler.of(itemView, new RunnableW(){
                            @Override
                            public void runSafe(){
                                clear();
                            }
                        });
                    }
                };
                runnable.start();
                fragment().setStep(FragmentCipherText.Step.IDLE);
            } else {
                erasedDataIn = null;
                fragment().restoreButtons();
            }
        }
        void restore(){
            if(runnable != null){
                runnable.cancel();
                runnable = null;
            }
            if(erasedDataIn != null){
                set(erasedDataIn);
                notifyUpdate();
                erasedDataIn = null;
            }
            btnErase.setIndex(0);
            fragment().restoreButtons();
        }
        void clear(){
            runnable = null;
            erasedDataIn = null;
            btnErase.setIndex(0);
        }
    });
    btnErase.setIndex(0);
    btnPaste = itemView.findViewById(R.id.btn_paste);
    btnPaste.setOnClickListener(new ViewOnClickListenerW(){
        DataTextIn replacedDataIn = null;
        RunnableTimeOut runnable = null;
        @Override
        public void onClicked(View v){
            FragmentCipherText fragment = fragment();
            if(fragment.disableButtons()){
                if(btnPaste.getIndex() == 1){
                    restore();
                } else if(btnPaste.getIndex() == 0){
                    replace();
                }
            }
        }
        void replace(){
            replacedDataIn = get();
            if(pasteFromClipboard()){
                if((replacedDataIn != null) && (Nullify.string(replacedDataIn.getText()) != null)){
                    btnPaste.setIndex(1);
                    runnable = new RunnableTimeOut(this, UNDO_ALLOWED_DELAY_ms){
                        @Override
                        public void onTimeOut(){
                            PostToHandler.of(itemView, new RunnableW(){
                                @Override
                                public void runSafe(){
                                    clear();
                                }
                            });
                        }
                    };
                    runnable.start();
                } else {
                    replacedDataIn = null;
                }
                fragment().setStep(FragmentCipherText.Step.IDLE);
            }
        }
        void restore(){
            if(runnable != null){
                runnable.cancel();
                runnable = null;
            }
            if(replacedDataIn != null){
                set(replacedDataIn);
                notifyUpdate();
                replacedDataIn = null;
            }
            btnPaste.setIndex(0);
            fragment().restoreButtons();
        }
        void clear(){
            runnable = null;
            replacedDataIn = null;
            btnPaste.setIndex(0);
        }
    });
    btnSelect = itemView.findViewById(R.id.btn_select);
    btnSelect.setOnClickListener(new ViewOnClickListenerW(){
        @Override
        public void onClicked(View v){
            if(fragment().disableButtons()){
                selectFile();
            }
        }
    });
    btnScan = itemView.findViewById(R.id.btn_scan);
    btnScan.setOnClickListener(new ViewOnClickListenerW(){
        @Override
        public void onClicked(View v){
            if(fragment().disableButtons()){
                openDialogScan();
            }
        }
    });
    buttonsFadeListener = new ButtonsFadeListener(frmText, itemView.findViewById(R.id.container_button_retrieve_glass));
    set(dataText);
}
@Override
public void onDisabledButtons(StateView stateView){
    stateView.enableNot(frmText).enableNot(btnEncrypt).enableNot(btnDecrypt).enableNot(btnErase).enableNot(btnSelect).enableNot(btnScan).enableNot(btnPaste);
}
@Override
public void enableButtons(FragmentCipherText.Operation operation){
    btnScan.setEnabled(true);
    btnPaste.setEnabled(true);
    btnSelect.setEnabled(true);
    btnErase.setEnabled(true);
    frmText.setEnabled(true);
    if(btnEncrypt.getIndex() == 0){
        if((operation != FragmentCipherText.Operation.DECRYPT)){
            btnEncrypt.setEnabled(true);
        }
    } else {
        btnEncrypt.setEnabled(true);
    }
    if(btnDecrypt.getIndex() == 0){
        if((operation != FragmentCipherText.Operation.ENCRYPT)){
            btnDecrypt.setEnabled(true);
        }
    } else {
        btnDecrypt.setEnabled(true);
    }
}
@Override
public void onPasswordChanged(StateView stateView){
    if(btnEncrypt.getIndex() != 0){
        btnEncrypt.setIndex(0);
    }
    stateView.put(Feature.ENABLED, btnEncrypt, true);
    if(btnDecrypt.getIndex() != 0){
        btnDecrypt.setIndex(0);
    }
    stateView.put(Feature.ENABLED, btnDecrypt, true);
}
@Override
public DataTextIn get(){
    DataTextIn data = dataText;
    if(data == null){
        data = new DataTextIn();
    }
    if(isBound()){
        data.setText(Nullify.string(frmText.getText()));
    }
    return data;
}
@Override
public void set(DataText data){
    dataText = (DataTextIn)data;
    if(isBound()){
        textWatcherExternalPasted.setEnable(false);
        if(data == null){
            frmText.setText(null);
        }
        else {
            frmText.setText(dataText.text);
        }
        textWatcherExternalPasted.setEnable(true);
    }
}

private void addTextChangedListenerAfterOperationCompleted(FragmentCipherText.Step previousStep, FragmentCipherText.Operation operation){
    if(textChangeListenerAfterOperationCompleted == null){
        textChangeListenerAfterOperationCompleted = new EditTextOnTextChangedListenerW(){
            @Override
            public void onTextChanged(EditText editText, Editable es){
                fragment().setStep(FragmentCipherText.Step.IDLE);
            }
        };
        frmText.addTextChangedListener(textChangeListenerAfterOperationCompleted);
    }
}
private void removeTextChangedListenerAfterOperationCompleted(){
    if(textChangeListenerAfterOperationCompleted != null){
        frmText.removeTextChangedListener(textChangeListenerAfterOperationCompleted);
        textChangeListenerAfterOperationCompleted = null;
    }
}
public void setStep(FragmentCipherText.Step previousStep, FragmentCipherText.Step newStep, FragmentCipherText.Operation operation){
    if(isBound()){
        PostToHandler.of(itemView(), new RunnableW(){
            @Override
            public void runSafe(){
                switch(newStep){
                    case IDLE:
                        stepIdle(previousStep, operation);
                        break;
                    case START:
                        stepStart(previousStep, operation);
                        break;
                    case STARTED:
                        stepStarted(previousStep, operation);
                        break;
                    case SUCCEED:
                        stepSucceed(previousStep, operation);
                        break;
                    case FAILED:
                        stepFailed(previousStep, operation);
                        break;
                    case ABORT:
                        stepAbort(previousStep, operation);
                        break;
                    case ABORTED:
                        stepAborted(previousStep, operation);
                        break;
                }
            }
        });
    }
}
@Override
protected void stepIdle(FragmentCipherText.Step previousStep, FragmentCipherText.Operation operation){
    removeTextChangedListenerAfterOperationCompleted();
    if(btnEncrypt.getIndex() != 0){
        btnEncrypt.setIndex(0);
    }
    if(btnDecrypt.getIndex() != 0){
        btnDecrypt.setIndex(0);
    }
    enableButtons(operation);
}
@Override
protected void stepStart(FragmentCipherText.Step previousStep, FragmentCipherText.Operation operation){
    if(previousStep == FragmentCipherText.Step.START){
        return;
    }
    btnEncrypt.setIndex(0);
    btnDecrypt.setIndex(0);
}
@Override
protected void stepStarted(FragmentCipherText.Step previousStep, FragmentCipherText.Operation operation){
    if(previousStep == FragmentCipherText.Step.STARTED){
    }
}
@Override
protected void stepSucceed(FragmentCipherText.Step previousStep, FragmentCipherText.Operation operation){
    if(previousStep == FragmentCipherText.Step.SUCCEED){
        return;
    }
    if(operation == FragmentCipherText.Operation.ENCRYPT){
        btnEncrypt.setIndex(1);
    } else if(operation == FragmentCipherText.Operation.DECRYPT){
        btnDecrypt.setIndex(1);
    }
    addTextChangedListenerAfterOperationCompleted(previousStep, operation);
    enableButtons(operation);
}
@Override
protected void stepFailed(FragmentCipherText.Step previousStep, FragmentCipherText.Operation operation){
    if(previousStep == FragmentCipherText.Step.FAILED){
        return;
    }
    if(operation == FragmentCipherText.Operation.ENCRYPT){
        btnEncrypt.setIndex(2);
    } else if(operation == FragmentCipherText.Operation.DECRYPT){
        btnDecrypt.setIndex(2);
    }
    addTextChangedListenerAfterOperationCompleted(previousStep, operation);
    enableButtons(operation);
}
@Override
protected void stepAbort(FragmentCipherText.Step previousStep, FragmentCipherText.Operation operation){
    if(previousStep == FragmentCipherText.Step.ABORT){
    }
}
@Override
protected void stepAborted(FragmentCipherText.Step previousStep, FragmentCipherText.Operation operation){
    if(previousStep == FragmentCipherText.Step.ABORTED){
        return;
    }
    if(operation == FragmentCipherText.Operation.ENCRYPT){
        btnEncrypt.setIndex(3);
    } else if(operation == FragmentCipherText.Operation.DECRYPT){
        btnDecrypt.setIndex(3);
    }
    addTextChangedListenerAfterOperationCompleted(previousStep, operation);
    enableButtons(operation);
}

private boolean pasteFromClipboard(){
    return pastText(AppClipboard.getText());
}
private boolean pastText(String text){
    if(text != null){
        text = ActivityFilterDispatcher.removeDeepLinkIfStartWith(EXTENSION_CIPHER_TEXT, text);
        set(new DataTextIn().setText(text));
        notifyUpdate();
        if(isBound()){
            fragment().setStep(FragmentCipherText.Step.IDLE);
        }
        return true;
    } else {
        if(isBound()){
            fragment().restoreButtons();
        }
        return false;
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
            if(Environment.EXTENSION_CIPHER_FILE.equals(uri.getExtension())){
                NavigationArguments arguments = NavigationArguments.create();
                arguments.setUris(uris);
                arguments.setTarget(FragmentCipherFile.class);
                Navigate.observe(new ObserverEvent<>(this, FRAGMENT){
                    @Override
                    public void onComplete(NavigatorManager.NavigatorKey.Is navigator, NavigatorManager.Event event){
                        if(event == NavigatorManager.Event.ON_NAVIGATE_TO){
                            unsubscribe();
                            ((ActivityToolbar)getActivity()).getToolbarBottom().setChecked(CIPHER_FILE.getId());
                        }
                    }
                });
                Navigate.To(CIPHER_FILE, arguments);
            } else {
                onSelectedFile(uri);
            }
        }
        @Override
        public void onException(ListOrObject<UriW> uris, Throwable e){
            fragment().restoreButtons(true);
        }
    });
}
private void onSelectedFile(UriW uri){
    String extension = uri.getExtension();
    if(EXTENSION_CIPHER_TEXT.equals(extension)){
        selectFileText(uri);
    } else {
        DialogModalProgressIndeterminate.State state = new DialogModalProgressIndeterminate.State();
        DialogModalProgressIndeterminate.Param param = state.obtainParam();
        param.setTitle(R.string.lbl_processing);
        Navigate.To(DialogModalProgressIndeterminate.class, state).observe(new ObserverValueE<>(this){
            @Override
            public void onComplete(DialogModalProgressIndeterminate dialog){
                Handler.PRIMARY().post(this, new RunnableW(){
                    @Override
                    public void runSafe(){
                        if(DataQr.EXTENSION_GIF.equals(extension)){
                            selectFileGif(dialog, uri);
                        } else {
                            dialog.close();
                            fragment().restoreButtons(true);
                        }
                    }
                });
            }
            @Override
            public void onException(DialogModalProgressIndeterminate dialogProgressIndeterminate, Throwable e){
                fragment().restoreButtons(true);
            }
        });
    }
}
private void selectFileGif(DialogModalProgressIndeterminate dialog, UriW uri){
    DataQr dataQr = new DataQr();
    dataQr.fromGif(uri, true).observe(new ObserverValueE<String>(this){
        @Override
        public void onComplete(String s){
            set(new DataTextIn().setText(s));
            notifyUpdate();
            dialog.close();
            fragment().setStep(FragmentCipherText.Step.IDLE);
        }
        @Override
        public void onException(String s, Throwable e){
            dialog.close();
            fragment().restoreButtons(true);
        }
    });
}
public void selectFileText(UriW uri){
    try{
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        UtilsFile.transfer(uri.getInputStream(), out);
        String data = BytesTo.StringChar(out.toByteArray());
        set(new DataTextIn().setText(data));
        notifyUpdate();
        if(isBound()){
            fragment().setStep(FragmentCipherText.Step.IDLE);
        }
    } catch(Throwable e){
        if(isBound()){
            fragment().restoreButtons(true);
        }
    }
}

private void openDialogScan(){
    DialogQrScan.State state = new DialogQrScan.State();
    DialogQrScan.Param param = state.obtainParam();
    param.setType(R.string.lbl_type_encrypted_text).setTitle(R.string.lbl_scan_title);
    DialogQrScan.open(this, state).observe(new ObserverValueE<DialogQrScan>(this){
        @Override
        public void onComplete(DialogQrScan dialog){
            observeDialogScan(dialog);
        }
        @Override
        public void onException(DialogQrScan dialog, Throwable e){
            fragment().restoreButtons(false);
        }
    });
}
private void observeDialogScan(DialogQrScan dialog){
    dialog.observe(new ObserverValue<String>(this){
        @Override
        public void onComplete(String s){
            dialog.close();
            set(new DataTextIn().setText(s));
            notifyUpdate();
            fragment().setStep(FragmentCipherText.Step.IDLE);
        }
    });
    dialog.observe(new ObserverEvent<Event.Is, Object>(this, Event.ON_CANCEL){
        @Override
        public void onComplete(Event.Is event, Object o){
            fragment().restoreButtons(false);
        }
    });
}

public void onNewNavigationArguments(NavigationArguments arguments){
    if(arguments.exist()){
        String data = arguments.getData();
        if(data != null){
            pastText(data);
            return;
        }
        ListOrObject<UriW> uris = arguments.getUris();
        if(uris != null){
            selectFileText(uris.get());
        }
    }
}

public static class DataTextIn extends CipherTextBinder.DataText{
    private String text = null;
    public String getText(){
        return text;
    }
    public DataText setText(String text){
        this.text = text;
        return this;
    }
    public DebugString toDebugString(){
        DebugString data = new DebugString();
        data.append("text", text);
        return data;
    }
    final public void toDebugLog(){
DebugLog.start().send(toDebugString()).end();
    }

}
private class TextWatcherExternalPasted implements TextWatcher{
    boolean enabled = true;
    String text = null;
    int indexStart = NULL_INDEX;
    int indexEnd = indexStart;
    public boolean isEnabled(){
        return enabled;
    }
    public void setEnable(boolean enable){
        this.enabled = enable;
    }
    @Override
    public void beforeTextChanged(CharSequence charSequence, int start, int countCurrent, int countAfter){
        if(isEnabled()){
            if(countAfter > ActivityFilterDispatcher.DEEP_LINK.length()){
                indexStart = start;
                indexEnd = start + countAfter;
            } else {
                indexStart = indexEnd = NULL_INDEX;
            }
        }
    }
    @Override
    public void onTextChanged(CharSequence charSequence, int start, int countBefore, int countCurrent){
        if(isEnabled()){
            if((indexStart != NULL_INDEX) && (indexEnd != NULL_INDEX)){
                removeDeepLink(charSequence.subSequence(indexStart, indexEnd).toString());
            }
        }
    }
    void removeDeepLink(String text){
        if(text != null){
            if(ActivityFilterDispatcher.startWithDeepLink(EXTENSION_CIPHER_TEXT, text)){
                this.text = ActivityFilterDispatcher.removeDeepLink(EXTENSION_CIPHER_TEXT, text);
            }
        }
    }
    @Override
    public void afterTextChanged(Editable editable){
        if(isEnabled()){
            if(text != null){
                set(new DataTextIn().setText(text));
                notifyUpdate();
                if(isBound()){
                    fragment().setStep(FragmentCipherText.Step.IDLE);
                }
            }
        }
        text = null;
    }
}
private static class ButtonsFadeListener extends CipherTextBinder.ButtonsFadeListener{
    private final static int MIN_UPDATE_ON_TEXT_CHANGED_DELAY_ms = 500;
    private final RunnableTimeOut updateTextChanged;
    public ButtonsFadeListener(EditText frmText, GlassAnimInOut glassButtonsFade){
        super(frmText, glassButtonsFade);
        updateTextChanged = new RunnableTimeOut(this, MIN_UPDATE_ON_TEXT_CHANGED_DELAY_ms){
            @Override
            public void onTimeOut(){
                if(tryLockBusy()){
                    if(shouldFadeButtons()){
                        glassButtonsDelayedFade.update(true);
                    }
                    else {
                        glassButtonsDelayedFade.update(false);
                    }
                    unLockBusy();
                }
            }
        };
        frmText.addFocusChangeListener(new TextViewOnFocusChangeListenerW(){
            @Override
            public void onFocusChange(android.widget.EditText textView, boolean hasFocus){
                updateOnFocus(hasFocus);
            }
        });
        frmText.addTextChangedListener(new EditTextOnTextChangedListenerW(){
            @Override
            public void onTextChanged(EditText editText, Editable es){
                updateOnTextChanged();
            }
        });
    }
    private ButtonsFadeListener me(){
        return this;
    }
    private void updateOnFocus(boolean gainFocus){
        if(tryLockBusy()){
            if(gainFocus){
                if(shouldFadeButtons()){
                    glassButtonsDelayedFade.update(true);
                }
            } else {
                glassButtonsDelayedFade.update(false);
            }
            unLockBusy();
        }
    }
    private void updateOnTextChanged(){
        if(frmText.hasFocus()){
            updateTextChanged.restart();
        }
    }
}

}
