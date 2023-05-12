/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.crypter.pager.page;

import com.tezov.lib_java.debug.DebugLog;
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
import static com.tezov.crypter.application.Environment.CachePath.CIPHER_TEXT;
import static com.tezov.crypter.application.Environment.EXTENSION_CIPHER_TEXT;
import static com.tezov.crypter.application.SharePreferenceKey.SP_CIPHER_TEXT_REMEMBER_FORMAT_STRING;
import static com.tezov.crypter.application.SharePreferenceKey.SP_ENCRYPT_SHARE_SUBJECT_PREFIX_STRING;
import static com.tezov.crypter.dialog.DialogMenuFormat.Format;

import android.view.View;
import android.widget.TextView;

import com.tezov.crypter.R;
import com.tezov.crypter.activity.activityFilter.ActivityFilterDispatcher;
import com.tezov.crypter.application.AppInfo;
import com.tezov.crypter.application.Application;
import com.tezov.crypter.application.Environment;
import com.tezov.crypter.data.item.ItemKey;
import com.tezov.crypter.data_transformation.DataQr;
import com.tezov.crypter.dialog.DialogMenuFormat;
import com.tezov.crypter.fragment.FragmentCipherText;
import com.tezov.crypter.pager.PagerCipherTextTabManager;
import com.tezov.lib_java_android.application.AppClipboard;
import com.tezov.lib_java_android.application.AppContext;
import com.tezov.lib_java_android.application.SharedPreferences;
import com.tezov.lib_java.async.Handler;
import com.tezov.lib_java.async.notifier.observer.event.ObserverEvent;
import com.tezov.lib_java.async.notifier.observer.state.ObserverStateE;
import com.tezov.lib_java.async.notifier.observer.value.ObserverValueE;
import com.tezov.lib_java.file.Directory;
import com.tezov.lib_java_android.file.StorageFile;
import com.tezov.lib_java_android.file.UriW;
import com.tezov.lib_java_android.file.UtilsFile;
import com.tezov.lib_java.toolbox.Nullify;
import com.tezov.lib_java_android.toolbox.PostToHandler;
import com.tezov.lib_java_android.annotation.ProguardFieldKeep;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java_android.wrapperAnonymous.TextViewOnFocusChangeListenerW;
import com.tezov.lib_java_android.wrapperAnonymous.ViewOnClickListenerW;
import com.tezov.lib_java.type.defEnum.Event;
import com.tezov.lib_java.type.primitive.string.StringCharTo;
import com.tezov.lib_java.type.runnable.RunnableW;
import com.tezov.lib_java_android.ui.component.plain.ButtonIconMaterial;
import com.tezov.lib_java_android.ui.component.plain.EditText;
import com.tezov.lib_java_android.ui.dialog.modal.DialogModalProgressIndeterminate;
import com.tezov.lib_java_android.ui.misc.StateView;
import com.tezov.lib_java_android.ui.navigation.Navigate;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class CipherTextOutBinder extends CipherTextBinder{
protected final static int SHARE_SUB_KEY_LENGTH = 8;
protected ButtonIconMaterial btnShareDialog = null;
protected ButtonIconMaterial btnShare = null;
protected ButtonIconMaterial btnCopy = null;
protected TextView lblCryptDate = null;
protected TextView lblSignatureKey = null;
protected TextView lblSignatureApp = null;
protected View containerCryptInfo = null;
private DataTextOut dataText = null;
private EditText frmText = null;
@ProguardFieldKeep
private ButtonsFadeListener buttonsFadeListener = null;
public CipherTextOutBinder(FragmentCipherText fragment, PagerCipherTextTabManager pageManager){
    super(fragment, pageManager);
}
@Override
public PagerCipherTextTabManager.ViewType.Is getViewType(){
    return PagerCipherTextTabManager.ViewType.OUT;
}
@Override
protected int getLayoutId(){
    return R.layout.pager_tab_cipher_text_out;
}
@Override
public void bindView(View itemView){
    super.bindView(itemView);
    frmText = itemView.findViewById(R.id.frm_text_out);
    frmText.addFocusChangeListener(new TextViewOnFocusChangeListenerW(){
        @Override
        public void onFocusChange(android.widget.EditText textView, boolean hasFocus){
            if(!hasFocus){
                notifyUpdate();
            }
        }
    });
    btnCopy = itemView.findViewById(R.id.btn_copy);
    btnCopy.setOnClickListener(new ViewOnClickListenerW(){
        @Override
        public void onClicked(View v){
            if(fragment().disableButtons()){
                copyToClipboard();
            }
        }
    });
    btnShare = itemView.findViewById(R.id.btn_share);
    btnShare.setOnClickListener(new ViewOnClickListenerW(){
        @Override
        public void onClicked(View v){
            if(fragment().disableButtons()){
                shareSelector(null, null);
            }
        }
    });
    btnShareDialog = itemView.findViewById(R.id.btn_share_dialog);
    btnShareDialog.setOnClickListener(new ViewOnClickListenerW(){
        @Override
        public void onClicked(View v){
            if(fragment().disableButtons()){
                openDialogShare();
            }
        }
    });

    lblCryptDate = itemView.findViewById(R.id.lbl_created_date);
    lblSignatureApp = itemView.findViewById(R.id.lbl_signature_app);
    lblSignatureKey = itemView.findViewById(R.id.lbl_signature_key);
    containerCryptInfo = itemView.findViewById(R.id.container_crypt_info);
    buttonsFadeListener = new ButtonsFadeListener(frmText, itemView.findViewById(R.id.container_button_share_glass));
    if(dataText != null){
        set(dataText);
    }
    fragment().onViewBound();
}
@Override
public void onDisabledButtons(StateView stateView){
    stateView.enableNot(btnShareDialog).enableNot(btnShare).enableNot(btnCopy);
}
@Override
public void enableButtons(FragmentCipherText.Operation operation){

}
@Override
public DataTextOut get(){
    DataTextOut data = dataText;
    if(data == null){
        data = new DataTextOut();
    }
    if(isBound()){
        data.setText(Nullify.string(frmText.getText()));
    }
    return data;
}
@Override
public void set(DataText data){
    dataText = (DataTextOut)data;
    if(isBound()){
        if(data == null){
            frmText.setText(null);
            containerCryptInfo.setVisibility(View.INVISIBLE);
        } else {
            frmText.setText(dataText.getText());
            containerCryptInfo.setVisibility(View.VISIBLE);
            lblCryptDate.setText(dataText.getEncryptedDate());
            lblSignatureApp.setText(dataText.getSignatureApp());
            lblSignatureKey.setText(dataText.getSignatureKey());
        }
    }

}
@Override
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
    set(null);
    btnShare.setEnabled(false);
    btnCopy.setEnabled(false);
    btnShareDialog.setEnabled(false);
}
@Override
protected void stepStart(FragmentCipherText.Step previousStep, FragmentCipherText.Operation operation){
    if(previousStep == FragmentCipherText.Step.START){
    }
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
    btnShareDialog.setEnabled(true);
    btnShare.setEnabled(true);
    btnCopy.setEnabled(true);
}
@Override
protected void stepFailed(FragmentCipherText.Step previousStep, FragmentCipherText.Operation operation){
    if(previousStep == FragmentCipherText.Step.FAILED){
    }

}
@Override
protected void stepAbort(FragmentCipherText.Step previousStep, FragmentCipherText.Operation operation){
    if(previousStep == FragmentCipherText.Step.ABORT){
    }

}
@Override
protected void stepAborted(FragmentCipherText.Step previousStep, FragmentCipherText.Operation operation){
    if(previousStep == FragmentCipherText.Step.ABORTED){
    }
}
private String makeDataName(String data){
    int nameResourceId = R.string.shr_encrypted_text;
    int start = data.length() - SHARE_SUB_KEY_LENGTH;
    if(start < 0){
        start = 0;
    }
    int end = data.length();
    return AppContext.getResources().getString(nameResourceId) + "_" + data.substring(start, end);
}
private void copyToClipboard(){
    DataTextOut dataText = get();
    String data = dataText.getText();
    String name = makeDataName(data);
    ItemKey itemKey = dataText.getItemKey();
    if(itemKey.mustEncryptAddDeepLinkToText()){
        data = ActivityFilterDispatcher.makeDeepLink(EXTENSION_CIPHER_TEXT, data);
    }
    AppClipboard.setText(name, data);
    fragment().restoreButtons();
}
private void openDialogShare(){
    DialogMenuFormat.State state = new DialogMenuFormat.State();
    DialogMenuFormat.Param param = state.obtainParam();
    param.setDefaultFormat(SP_CIPHER_TEXT_REMEMBER_FORMAT_STRING);
    Navigate.To(DialogMenuFormat.class, state).observe(new ObserverValueE<DialogMenuFormat>(this){
        @Override
        public void onComplete(DialogMenuFormat dialog){
            observeDialogShare(dialog);
        }
        @Override
        public void onException(DialogMenuFormat dialog, Throwable e){
            fragment().restoreButtons();
        }
    });
}
private void observeDialogShare(DialogMenuFormat dialog){
    dialog.observe(new ObserverEvent<Event.Is, Object>(this, Event.ON_CLICK_SHORT){
        @Override
        public void onComplete(Event.Is event, Object o){
            shareSelector(dialog, (Format)o);
        }
    });
    dialog.observe(new ObserverEvent<Event.Is, Object>(this, Event.ON_CANCEL){
        @Override
        public void onComplete(Event.Is event, Object object){
            fragment().restoreButtons();
        }
    });
}

private void shareSelector(DialogMenuFormat dialogMenuFormat, Format format){
    if(format == null){
        format = Format.FILE_TEXT;
        SharedPreferences sp = Application.sharedPreferences();
        String formatString = sp.getString(SP_CIPHER_TEXT_REMEMBER_FORMAT_STRING);
        if(formatString != null){
            format = Format.valueOf(formatString);
        }
    }
    if(format == Format.FILE_GIF){
        DialogModalProgressIndeterminate.State state = new DialogModalProgressIndeterminate.State();
        DialogModalProgressIndeterminate.Param param = state.obtainParam();
        param.setTitle(R.string.lbl_processing);
        Navigate.To(DialogModalProgressIndeterminate.class, state).observe(new ObserverValueE<>(this){
            @Override
            public void onComplete(DialogModalProgressIndeterminate dialog){
                if(dialogMenuFormat!=null){
                    dialogMenuFormat.close();
                }
                Handler.PRIMARY().post(this, new RunnableW(){
                    @Override
                    public void runSafe(){
                        shareFileGif(dialog);
                    }
                });
            }
            @Override
            public void onException(DialogModalProgressIndeterminate dialogProgressIndeterminate, Throwable e){
                if(dialogMenuFormat!=null){
                    dialogMenuFormat.close();
                }
                fragment().restoreButtons();
            }
        });
    } else {
        Format finalFormat = format;
        Handler.PRIMARY().post(this, new RunnableW(){
            @Override
            public void runSafe(){
                switch(finalFormat){
                    case FILE_TEXT:
                        shareFileText(dialogMenuFormat);
                        break;
                    case TEXT:
                        shareText(dialogMenuFormat);
                        break;
                    default:{
                        if(dialogMenuFormat!=null){
                            dialogMenuFormat.close();
                        }
                        fragment().restoreButtons();
                    }
                }
            }
        });
    }
}
private void shareText(DialogMenuFormat dialogMenuFormat){
    DataTextOut dataText = get();
    String data = dataText.getText();
    String name = makeDataName(data);
    ItemKey itemKey = dataText.getItemKey();
    if(itemKey.mustEncryptAddDeepLinkToText()){
        data = ActivityFilterDispatcher.makeDeepLink(EXTENSION_CIPHER_TEXT, data);
    }
    SharedPreferences sp = Application.sharedPreferences();
    String subjectPrefix = sp.getString(SP_ENCRYPT_SHARE_SUBJECT_PREFIX_STRING);
    String subject = (subjectPrefix != null ? subjectPrefix : "") + name;
    AppInfo.share(subject, data).observe(new ObserverStateE(this){
        @Override
        public void onComplete(){
            if(dialogMenuFormat!=null){
                dialogMenuFormat.close();
            }
            fragment().restoreButtons(true);
        }
        @Override
        public void onException(Throwable e){
            if(dialogMenuFormat!=null){
                dialogMenuFormat.close();
            }
            fragment().restoreButtons(true);
        }
    });
}
private void shareFileGif(DialogModalProgressIndeterminate dialogModalProgress){
    String data = get().getText();
    Directory cacheDirectory = Environment.obtainDirectoryCache(CIPHER_TEXT);
    String name = makeDataName(data);
    DataQr dataQr = new DataQr(data);
    dataQr.toGif(cacheDirectory, name).observe(new ObserverValueE<UriW>(this){
        @Override
        public void onComplete(UriW uri){
            SharedPreferences sp = Application.sharedPreferences();
            String subjectPrefix = sp.getString(SP_ENCRYPT_SHARE_SUBJECT_PREFIX_STRING);
            String subject = (subjectPrefix != null ? subjectPrefix : "") + name;
            AppInfo.share(subject, uri).observe(new ObserverStateE(this){
                @Override
                public void onComplete(){
                    dialogModalProgress.close();
                    fragment().restoreButtons(true);
                }
                @Override
                public void onException(Throwable e){
                    dialogModalProgress.close();
                    fragment().restoreButtons(true);
                }
            });
        }
        @Override
        public void onException(UriW uriW, Throwable e){
            dialogModalProgress.close();
            fragment().restoreButtons(true);
        }
    });
}
private void shareFileText(DialogMenuFormat dialogMenuFormat){
    String data = get().getText();
    Directory cacheDirectory = Environment.obtainDirectoryCache(CIPHER_TEXT);
    String name = makeDataName(data);
    UriW uri = StorageFile.obtainUniqueUri(cacheDirectory, name + com.tezov.lib_java.file.File.DOT_SEPARATOR + EXTENSION_CIPHER_TEXT);
    try{
        InputStream in = new ByteArrayInputStream(StringCharTo.Bytes(data));
        UtilsFile.transfer(in, uri);
        SharedPreferences sp = Application.sharedPreferences();
        String subjectPrefix = sp.getString(SP_ENCRYPT_SHARE_SUBJECT_PREFIX_STRING);
        String subject = (subjectPrefix != null ? subjectPrefix : "") + name;
        AppInfo.share(subject, uri).observe(new ObserverStateE(this){
            @Override
            public void onComplete(){
                if(dialogMenuFormat!=null){
                    dialogMenuFormat.close();
                }
                fragment().restoreButtons(true);
            }
            @Override
            public void onException(Throwable e){
                if(dialogMenuFormat!=null){
                    dialogMenuFormat.close();
                }
                fragment().restoreButtons(true);
            }
        });
    } catch(Throwable e){
        if(uri != null){
            uri.delete();
        }
        if(dialogMenuFormat!=null){
            dialogMenuFormat.close();
        }
        fragment().restoreButtons(true);
    }
}

public static class DataTextOut extends CipherTextBinder.DataText{
    private String text = null;
    private String signatureApp = null;
    private String encryptedDate = null;
    private ItemKey itemKey = null;
    public DataTextOut(){
DebugTrack.start().create(this).end();
    }
    public String getText(){
        return text;
    }
    public DataText setText(String text){
        this.text = text;
        return this;
    }
    public String getSignatureApp(){
        return signatureApp;
    }
    public DataTextOut setSignatureApp(String signature){
        this.signatureApp = signature;
        return this;
    }
    public String getSignatureKey(){
        if(itemKey.hasSignatureKey()){
            return itemKey.getSignatureKey();
        } else {
            return null;
        }
    }
    public String getEncryptedDate(){
        return encryptedDate;
    }
    public DataTextOut setEncryptedDate(String encryptedDate){
        this.encryptedDate = encryptedDate;
        return this;
    }
    public ItemKey getItemKey(){
        return itemKey;
    }
    public DataTextOut setItemKey(ItemKey itemKey){
        this.itemKey = itemKey;
        return this;
    }
    @Override
    protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
        super.finalize();
    }

}

}
