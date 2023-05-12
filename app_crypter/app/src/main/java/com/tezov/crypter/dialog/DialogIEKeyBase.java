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
import com.tezov.lib_java.debug.DebugException;

import androidx.fragment.app.Fragment;
import static com.tezov.crypter.application.AppConfig.ENABLE_BUTTONS_AFTER_INTENT_SENT_DELAY_ms;
import static com.tezov.crypter.application.Environment.CachePath.KEY_SHARE_ENCRYPTED;
import static com.tezov.crypter.application.Environment.CachePath.KEY_SHARE_PUBLIC;
import static com.tezov.crypter.application.Environment.EXTENSION_SHARE_ENCRYPTED_KEY;
import static com.tezov.crypter.application.Environment.EXTENSION_SHARE_PUBLIC_KEY;
import static com.tezov.crypter.application.SharePreferenceKey.SP_ENCRYPT_SHARE_SUBJECT_PREFIX_STRING;
import static com.tezov.crypter.application.SharePreferenceKey.SP_KEY_SHARE_REMEMBER_FORMAT_STRING;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.tezov.crypter.R;
import com.tezov.crypter.activity.activityFilter.ActivityFilterDispatcher;
import com.tezov.crypter.application.AppInfo;
import com.tezov.crypter.application.Application;
import com.tezov.crypter.application.Environment;
import com.tezov.crypter.data.dbItem.dbKey;
import com.tezov.crypter.data.item.ItemKey;
import com.tezov.crypter.data_transformation.DataQr;
import com.tezov.crypter.dialog.DialogMenuFormat.Format;
import com.tezov.crypter.navigation.NavigationArguments;
import com.tezov.lib_java_android.application.AppClipboard;
import com.tezov.lib_java_android.application.SharedPreferences;
import com.tezov.lib_java.async.Handler;
import com.tezov.lib_java.async.notifier.observer.event.ObserverEvent;
import com.tezov.lib_java.async.notifier.observer.state.ObserverStateE;
import com.tezov.lib_java.async.notifier.observer.value.ObserverValue;
import com.tezov.lib_java.async.notifier.observer.value.ObserverValueE;
import com.tezov.lib_java.file.Directory;
import com.tezov.lib_java_android.file.StorageFile;
import com.tezov.lib_java_android.file.StorageMedia;
import com.tezov.lib_java_android.file.UriW;
import com.tezov.lib_java_android.file.UtilsFile;
import com.tezov.lib_java_android.toolbox.PostToHandler;
import com.tezov.lib_java_android.wrapperAnonymous.ViewOnClickListenerW;
import com.tezov.lib_java.type.collection.ListOrObject;
import com.tezov.lib_java.type.defEnum.Event;
import com.tezov.lib_java.type.primitive.BytesTo;
import com.tezov.lib_java.type.primitive.string.StringCharTo;
import com.tezov.lib_java.type.runnable.RunnableW;
import com.tezov.lib_java_android.ui.component.plain.ButtonIconMaterial;
import com.tezov.lib_java_android.ui.dialog.DialogNavigable;
import com.tezov.lib_java_android.ui.dialog.modal.DialogModalProgressIndeterminate;
import com.tezov.lib_java_android.ui.form.component.plain.FormEditText;
import com.tezov.lib_java_android.ui.misc.StateView;
import com.tezov.lib_java_android.ui.navigation.Navigate;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public abstract class DialogIEKeyBase extends DialogNavigable{
protected final static int SHARE_SUB_KEY_LENGTH = 8;
protected StateView stateView = null;
protected FormEditText frmKeyPublic = null;
protected FormEditText frmKeyShared = null;
protected TextView lblSignatureAppRemote = null;
protected TextView lblSignatureAppLocal = null;

protected ButtonIconMaterial btnCopy = null;
protected ButtonIconMaterial btnShow = null;
protected ButtonIconMaterial btnShareDialog = null;
protected ButtonIconMaterial btnShare = null;

protected ButtonIconMaterial btnPaste = null;
protected ButtonIconMaterial btnScan = null;
protected ButtonIconMaterial btnSelect = null;

@Override
public Param obtainParam(){
    return super.obtainParam();
}
@Override
public Param getParam(){
    return super.getParam();
}

protected abstract int getLayoutId();

@Override
public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
    super.onCreateView(inflater, container, savedInstanceState);
    View view = inflater.inflate(getLayoutId(), container, false);
    stateView = new StateView();
    Param param = getParam();
    TextView lblTitle = view.findViewById(R.id.lbl_title);
    lblTitle.setText(param.getTitle());
    ButtonIconMaterial btnClose = view.findViewById(R.id.btn_close);
    btnClose.setOnClickListener(new ViewOnClickListenerW(){
        @Override
        public void onClicked(View v){
            if(disableButtons()){
                post(Event.ON_CANCEL, null);
                close();
            }
        }
    });

    //STEP SHARE
    btnCopy = view.findViewById(R.id.btn_copy);
    btnCopy.setOnClickListener(new ViewOnClickListenerW(){
        @Override
        public void onClicked(View v){
            if(disableButtons()){
                copyToClipboard();
            }
        }
    });
    btnShow = view.findViewById(R.id.btn_show);
    btnShow.setOnClickListener(new ViewOnClickListenerW(){
        @Override
        public void onClicked(View v){
            if(disableButtons()){
                openDialogShow();
            }
        }
    });
    btnShare = view.findViewById(R.id.btn_share);
    btnShare.setOnClickListener(new ViewOnClickListenerW(){
        @Override
        public void onClicked(View v){
            if(disableButtons()){
                shareSelector(null, null);
            }
        }
    });
    btnShareDialog = view.findViewById(R.id.btn_share_dialog);
    btnShareDialog.setOnClickListener(new ViewOnClickListenerW(){
        @Override
        public void onClicked(View v){
            if(disableButtons()){
                openDialogShare();
            }
        }
    });

    //STEP RETRIEVE
    btnScan = view.findViewById(R.id.btn_scan);
    btnScan.setOnClickListener(new ViewOnClickListenerW(){
        @Override
        public void onClicked(View v){
            if(disableButtons()){
                openDialogScan();
            }
        }
    });
    btnPaste = view.findViewById(R.id.btn_paste);
    btnPaste.setOnClickListener(new ViewOnClickListenerW(){
        @Override
        public void onClicked(View v){
            if(disableButtons()){
                pasteFromClipboard();
            }
        }
    });
    btnSelect = view.findViewById(R.id.btn_select);
    btnSelect.setOnClickListener(new ViewOnClickListenerW(){
        @Override
        public void onClicked(View v){
            if(disableButtons()){
                selectFile();
            }
        }
    });

    frmKeyShared = view.findViewById(R.id.frm_key_shared);
    frmKeyShared.setText(null);
    frmKeyShared.link(new FormEditText.EntryString(){
        @Override
        public <T> void onSetValue(Class<T> type){
            onSetValueKeyShared();
        }
    });
    frmKeyPublic = view.findViewById(R.id.frm_key_public);
    frmKeyPublic.setText(null);
    frmKeyPublic.link(new FormEditText.EntryString(){
        @Override
        public <T> void onSetValue(Class<T> type){
            onSetValueKeyPublic();
        }
    });

    lblSignatureAppRemote = view.findViewById(R.id.lbl_signature_app_remote);
    lblSignatureAppLocal = view.findViewById(R.id.lbl_signature_app_local);
    lblSignatureAppLocal.setText(AppInfo.getSignature());
    return view;
}
@Override
public void onOpen(boolean hasBeenReconstructed, boolean hasBeenRestarted){
    super.onOpen(hasBeenReconstructed, hasBeenRestarted);
    if(!hasBeenRestarted){
        onNewNavigationArguments(NavigationArguments.get(this));
    }
}
@Override
public boolean onNewNavigationArguments(com.tezov.lib_java_android.ui.navigation.NavigationArguments arg){
    NavigationArguments arguments = (NavigationArguments)arg;
    if(arguments.exist()){
        String data = arguments.getData();
        if(data != null){
            pasteText(data);
            return true;
        }
        ListOrObject<UriW> uris = arguments.getUris();
        if(uris != null){
            selectFileText(uris.get());
            return true;
        }
    }
    return super.onNewNavigationArguments(arg);
}

final public boolean disableButtons(){
    if(!stateView.isLocked()){
        disableButtonsShare();
        disableButtonsRetrieve();
        return true;
    } else {
        return false;
    }
}

final public boolean disableButtonsShare(){
    if(stateView.lock()){
        PostToHandler.of(getView(), new RunnableW(){
            @Override
            public void runSafe(){
                stateView.clear().enableNot(btnCopy).enableNot(btnShow).enableNot(btnShareDialog).enableNot(btnShare);
                onDisabledButtonsShare(stateView);
                stateView.unlock();
            }
        });
        return true;
    } else {
        return false;
    }
}
protected void onDisabledButtonsShare(StateView stateView){

}
final public boolean disableButtonsRetrieve(){
    if(stateView.lock()){
        PostToHandler.of(getView(), new RunnableW(){
            @Override
            public void runSafe(){
                stateView.clear().enableNot(btnPaste).enableNot(btnScan).enableNot(btnSelect);
                onDisabledButtonsRetrieve(stateView);
                stateView.unlock();
            }
        });
        return true;
    } else {
        return false;
    }
}
protected void onDisabledButtonsRetrieve(StateView stateView){

}

final public void enableButtonsShare(Step step){
    boolean delayed = ((step == Step.SHARED) || (step == Step.SELECTED));
    enableButtonsShare(delayed);
}
final public void enableButtonsShare(boolean withDelay){
    PostToHandler.of(getView(), withDelay ? ENABLE_BUTTONS_AFTER_INTENT_SENT_DELAY_ms : 0, new RunnableW(){
        @Override
        public void runSafe(){
            btnCopy.setEnabled(true);
            btnShow.setEnabled(true);
            btnShare.setEnabled(true);
            btnShareDialog.setEnabled(true);
            onEnableButtonsShare();
        }
    });
}
protected void onEnableButtonsShare(){

}

final public void enableButtonsRetrieve(Step step){
    boolean delayed = ((step == Step.SHARED) || (step == Step.SELECTED));
    enableButtonsRetrieve(delayed);
}
final public void enableButtonsRetrieve(boolean withDelay){
    PostToHandler.of(getView(), withDelay ? ENABLE_BUTTONS_AFTER_INTENT_SENT_DELAY_ms : 0, new RunnableW(){
        @Override
        public void runSafe(){
            btnPaste.setEnabled(true);
            btnScan.setEnabled(true);
            btnSelect.setEnabled(true);
            onEnableButtonsRetrieve();
        }
    });
}
protected void onEnableButtonsRetrieve(){

}

protected void postStep(Step step){
    postStep(step, (Throwable)null);
}
protected void postStep(Step step, String e){
    postStep(step, new Throwable(e));
}
protected void postStep(Step step, Throwable e){
    onStep(step, e);
}

protected abstract String getDataToShare();

protected abstract dbKey getDataKey();

protected abstract int getTypeResourceId();
private String makeDataName(String data){
    int nameResourceId;
    int typeResourceId = getTypeResourceId();
    if(typeResourceId == R.string.lbl_type_public_key){
        nameResourceId = R.string.shr_public_key;
    } else if(typeResourceId == R.string.lbl_type_encrypted_key){
        nameResourceId = R.string.shr_encrypted_key;
    } else {
        return null;
    }
    int start = data.length() - SHARE_SUB_KEY_LENGTH;
    if(start < 0){
        start = 0;
    }
    int end = data.length();
    return AppContext.getResources().getString(nameResourceId) + "_" + data.substring(start, end);
}
private String getExtension(){
    String extension = null;
    int typeResourceId = getTypeResourceId();
    if(typeResourceId == R.string.lbl_type_public_key){
        extension = EXTENSION_SHARE_PUBLIC_KEY;
    } else if(typeResourceId == R.string.lbl_type_encrypted_key){
        extension = EXTENSION_SHARE_ENCRYPTED_KEY;
    }
    return extension;
}
protected String getExtensionComplement(){
    String extension = null;
    int typeResourceId = getTypeResourceId();
    if(typeResourceId == R.string.lbl_type_public_key){
        extension = EXTENSION_SHARE_ENCRYPTED_KEY;
    } else if(typeResourceId == R.string.lbl_type_encrypted_key){
        extension = EXTENSION_SHARE_PUBLIC_KEY;
    }
    return extension;
}
private Directory getDirectoryCache(){
    Directory cacheDirectory = null;
    int typeResourceId = getTypeResourceId();
    if(typeResourceId == R.string.lbl_type_public_key){
        cacheDirectory = Environment.obtainDirectoryCache(KEY_SHARE_PUBLIC);
    } else if(typeResourceId == R.string.lbl_type_encrypted_key){
        cacheDirectory = Environment.obtainDirectoryCache(KEY_SHARE_ENCRYPTED);
    }
    return cacheDirectory;
}

private String makeTitle(int titleResourceId){
    return makeTitle(AppContext.getResources().getString(titleResourceId));
}
private String makeTitle(String title){
    dbKey dataKey = getDataKey();
    if(dataKey == null){
        return title;
    } else {
        return title + ": " + dataKey.getItem().getAlias();
    }
}

private void copyToClipboard(){
    String data = getDataToShare();
    String name = makeDataName(data);
    if(name == null){
        postStep(Step.SHARED, "type unknown " + getTypeResourceId());
        return;
    }
    data = ActivityFilterDispatcher.makeDeepLink(getExtension(), data);
    AppClipboard.setText(name, data);
    postStep(Step.COPIED);
}
private void openDialogShow(){
    String title = AppContext.getResources().getString(R.string.lbl_share_title);
    dbKey dataKey = getDataKey();
    if(dataKey != null){
        title +=": " + dataKey.getItem().getAlias();
    }
    DialogQrShow.State state = new DialogQrShow.State();
    DialogQrShow.Param param = state.obtainParam();
    param.setData(new DataQr(getDataToShare()))
            .setType(getTypeResourceId())
        .setTitle(title);
    int typeResourceId = getTypeResourceId();
    if(typeResourceId == R.string.lbl_type_public_key){
        param.setSignatureApp(AppInfo.getSignature());
    } else if(typeResourceId == R.string.lbl_type_encrypted_key){
        ItemKey itemKey = dataKey.getItem();
        param.setSignatureApp(AppInfo.getSignature());
        param.setSignatureKey(itemKey.getSignatureKey());
    } else {
        postStep(Step.SHOWED, new Throwable("type unknown " + typeResourceId));
        return;
    }
    Navigate.To(DialogQrShow.class, state).observe(new ObserverValueE<DialogQrShow>(this){
        @Override
        public void onComplete(DialogQrShow dialog){
            observeDialogShow(dialog);
        }
        @Override
        public void onException(DialogQrShow dialogQrShow, Throwable e){
            PostToHandler.of(getView(), new RunnableW(){
                @Override
                public void runSafe(){
                    postStep(Step.SHOWED, e);
                }
            });
        }
    });
}
private void observeDialogShow(DialogQrShow dialog){
    dialog.observe(new ObserverEvent<Event.Is, Object>(this, Event.ON_CLOSE){
        @Override
        public void onComplete(Event.Is event, Object o){
            PostToHandler.of(getView(), new RunnableW(){
                @Override
                public void runSafe(){
                    postStep(Step.SHOWED);
                }
            });
        }
    });
}

private void openDialogShare(){
    DialogMenuFormat.State state = new DialogMenuFormat.State();
    DialogMenuFormat.Param param = state.obtainParam();
    param.setDefaultFormat(SP_KEY_SHARE_REMEMBER_FORMAT_STRING);
    Navigate.To(DialogMenuFormat.class, state).observe(new ObserverValueE<DialogMenuFormat>(this){
        @Override
        public void onComplete(DialogMenuFormat dialog){
            observeDialogShare(dialog);
        }
        @Override
        public void onException(DialogMenuFormat dialog, Throwable e){
            PostToHandler.of(getView(), new RunnableW(){
                @Override
                public void runSafe(){
                    postStep(Step.SHARED, e);
                }
            });
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
            PostToHandler.of(getView(), new RunnableW(){
                @Override
                public void runSafe(){
                    postStep(Step.SHARED, "canceled");
                }
            });
        }
    });
}

private void shareSelector(DialogMenuFormat dialogMenuFormat, Format format){
    if(format == null){
        format = Format.FILE_TEXT;
        SharedPreferences sp = Application.sharedPreferences();
        String formatString = sp.getString(SP_KEY_SHARE_REMEMBER_FORMAT_STRING);
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
                PostToHandler.of(getView(), new RunnableW(){
                    @Override
                    public void runSafe(){
                        if(dialogMenuFormat!=null){
                            dialogMenuFormat.close();
                        }
                        postStep(Step.SHARED, e);
                    }
                });
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
                        PostToHandler.of(getView(), new RunnableW(){
                            @Override
                            public void runSafe() throws Throwable{
                                if(dialogMenuFormat!=null){
                                    dialogMenuFormat.close();
                                }
                                postStep(Step.SHARED, "unknown format " + finalFormat);
                            }
                        });
                    }
                }
            }
        });
    }
}
private void shareFileGif(DialogModalProgressIndeterminate dialogModalProgress){
    String data = getDataToShare();
    Directory cacheDirectory = getDirectoryCache();
    String name = makeDataName(data);
    if((name == null) || (cacheDirectory == null)){
        PostToHandler.of(getView(), new RunnableW(){
            @Override
            public void runSafe(){
                dialogModalProgress.close();
                postStep(Step.SHARED, "type unknown " + getTypeResourceId());
            }
        });
        return;
    }
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
                    PostToHandler.of(getView(), new RunnableW(){
                        @Override
                        public void runSafe(){
                            dialogModalProgress.close();
                            postStep(Step.SHARED);
                        }
                    });
                }
                @Override
                public void onException(Throwable e){
                    PostToHandler.of(getView(), new RunnableW(){
                        @Override
                        public void runSafe(){
                            dialogModalProgress.close();
                            postStep(Step.SHARED, e);
                        }
                    });
                }
            });
        }
        @Override
        public void onException(UriW uriW, Throwable e){
            PostToHandler.of(getView(), new RunnableW(){
                @Override
                public void runSafe(){
                    dialogModalProgress.close();
                    postStep(Step.SHARED, e);
                }
            });
        }
    });
}
private void shareFileText(DialogMenuFormat dialogMenuFormat){
    String data = getDataToShare();
    Directory cacheDirectory = getDirectoryCache();
    String name = makeDataName(data);
    if((name == null) || (cacheDirectory == null)){
        PostToHandler.of(getView(), new RunnableW(){
            @Override
            public void runSafe(){
                if(dialogMenuFormat != null){
                    dialogMenuFormat.close();
                }
                postStep(Step.SHARED, "type unknown " + getTypeResourceId());
            }
        });
        return;
    }
    UriW uri = StorageFile.obtainUniqueUri(cacheDirectory, name + com.tezov.lib_java.file.File.DOT_SEPARATOR + getExtension());
    try{
        InputStream in = new ByteArrayInputStream(StringCharTo.Bytes(data));
        UtilsFile.transfer(in, uri);
        SharedPreferences sp = Application.sharedPreferences();
        String subjectPrefix = sp.getString(SP_ENCRYPT_SHARE_SUBJECT_PREFIX_STRING);
        String subject = (subjectPrefix != null ? subjectPrefix : "") + name;
        AppInfo.share(subject, uri).observe(new ObserverStateE(this){
            @Override
            public void onComplete(){
                PostToHandler.of(getView(), new RunnableW(){
                    @Override
                    public void runSafe(){
                        if(dialogMenuFormat != null){
                            dialogMenuFormat.close();
                        }
                        postStep(Step.SHARED);
                    }
                });
            }
            @Override
            public void onException(Throwable e){
                PostToHandler.of(getView(), new RunnableW(){
                    @Override
                    public void runSafe(){
                        if(dialogMenuFormat != null){
                            dialogMenuFormat.close();
                        }
                        postStep(Step.SHARED, e);
                    }
                });
            }
        });
    } catch(Throwable e){
        if(uri != null){
            uri.delete();
        }
        PostToHandler.of(getView(), new RunnableW(){
            @Override
            public void runSafe(){
                if(dialogMenuFormat != null){
                    dialogMenuFormat.close();
                }
                postStep(Step.SHARED, e);
            }
        });
    }
}
private void shareText(DialogMenuFormat dialogMenuFormat){
    String data = getDataToShare();
    String name = makeDataName(data);
    if(name == null){
        PostToHandler.of(getView(), new RunnableW(){
            @Override
            public void runSafe() throws Throwable{
                if(dialogMenuFormat != null){
                    dialogMenuFormat.close();
                }
                postStep(Step.SHARED, "type unknown " + getTypeResourceId());
            }
        });
        return;
    }
    data = ActivityFilterDispatcher.makeDeepLink(getExtension(), data);
    SharedPreferences sp = Application.sharedPreferences();
    String subjectPrefix = sp.getString(SP_ENCRYPT_SHARE_SUBJECT_PREFIX_STRING);
    String subject = (subjectPrefix != null ? subjectPrefix : "") + name;
    AppInfo.share(subject, data).observe(new ObserverStateE(this){
        @Override
        public void onComplete(){
            PostToHandler.of(getView(), new RunnableW(){
                @Override
                public void runSafe(){
                    if(dialogMenuFormat != null){
                        dialogMenuFormat.close();
                    }
                    postStep(Step.SHARED);
                }
            });
        }
        @Override
        public void onException(Throwable e){
            PostToHandler.of(getView(), new RunnableW(){
                @Override
                public void runSafe(){
                    if(dialogMenuFormat != null){
                        dialogMenuFormat.close();
                    }
                    postStep(Step.SHARED, e);
                }
            });
        }
    });
}

private void openDialogScan(){
    DialogQrScan.State state = new DialogQrScan.State();
    DialogQrScan.Param param = state.obtainParam();
    param.setType(getTypeResourceId()).setTitle(R.string.lbl_scan_title);
    DialogQrScan.open(this, state).observe(new ObserverValueE<DialogQrScan>(this){
        @Override
        public void onComplete(DialogQrScan dialog){
            observeDialogScan(dialog);
        }
        @Override
        public void onException(DialogQrScan dialog, Throwable e){
            PostToHandler.of(getView(), new RunnableW(){
                @Override
                public void runSafe(){
                    postStep(Step.SCANNED, e);
                }
            });
        }
    });
}
private void observeDialogScan(DialogQrScan dialog){
    dialog.observe(new ObserverValue<String>(this){
        @Override
        public void onComplete(String s){
            dialog.close();
            PostToHandler.of(getView(), new RunnableW(){
                @Override
                public void runSafe(){
                    if(onScanned(s)){
                        postStep(Step.SCANNED);
                    } else {
                        postStep(Step.SCANNED, "not accepted");
                    }
                }
            });
        }
    });
    dialog.observe(new ObserverEvent<Event.Is, Object>(this, Event.ON_CANCEL){
        @Override
        public void onComplete(Event.Is event, Object o){
            PostToHandler.of(getView(), new RunnableW(){
                @Override
                public void runSafe(){
                    postStep(Step.SCANNED, "canceled");
                }
            });
        }
    });
}
protected abstract boolean onScanned(String data);

private void pasteFromClipboard(){
    pasteText(AppClipboard.getText());
}
private void pasteText(String text){
    PostToHandler.of(getView(), new RunnableW(){
        @Override
        public void runSafe(){
            if(text != null){
                String textNoLink = ActivityFilterDispatcher.removeDeepLinkIfStartWith(getExtensionComplement(), text);
                if(onPastedFromClipboard(textNoLink)){
                    postStep(Step.PASTED);
                } else {
                    postStep(Step.PASTED, "not accepted");
                }
            } else {
                postStep(Step.PASTED, "nothing");
            }

        }
    });
}
protected abstract boolean onPastedFromClipboard(String data);

private void selectFile(){
    StorageMedia.openDocument(false).observe(new ObserverValueE<ListOrObject<UriW>>(this){
        @Override
        public void onComplete(ListOrObject<UriW> uris){
            if(uris.size() > 1){
DebugException.start().log("multiple uri ignored").end();
            }
            onSelectedFile(uris.get());
        }
        @Override
        public void onException(ListOrObject<UriW> uris, Throwable e){
            PostToHandler.of(getView(), new RunnableW(){
                @Override
                public void runSafe(){
                    postStep(Step.SELECTED, e);
                }
            });
        }
    });
}
protected void onSelectedFile(UriW uri){
    String extension = uri.getExtension();
    if(DataQr.EXTENSION_GIF.equals(extension)){
        DialogModalProgressIndeterminate.State state = new DialogModalProgressIndeterminate.State();
        DialogModalProgressIndeterminate.Param param = state.obtainParam();
        param.setTitle(R.string.lbl_processing);
        Navigate.To(DialogModalProgressIndeterminate.class, state).observe(new ObserverValueE<>(this){
            @Override
            public void onComplete(DialogModalProgressIndeterminate dialog){
                Handler.PRIMARY().post(this, new RunnableW(){
                    @Override
                    public void runSafe(){
                        selectFileGif(dialog, uri);
                    }
                });
            }
            @Override
            public void onException(DialogModalProgressIndeterminate dialogProgressIndeterminate, Throwable e){
                PostToHandler.of(getView(), new RunnableW(){
                    @Override
                    public void runSafe(){
                        postStep(Step.SELECTED, "unknown extension " + extension);
                    }
                });
            }
        });
    } else {
        Handler.PRIMARY().post(this, new RunnableW(){
            @Override
            public void runSafe(){
                int typeResourceId = getTypeResourceId();
                String extension = uri.getExtension();
                if((typeResourceId == R.string.lbl_type_public_key) && EXTENSION_SHARE_ENCRYPTED_KEY.equals(extension)){
                    selectFileText(uri);
                } else if((typeResourceId == R.string.lbl_type_encrypted_key) && EXTENSION_SHARE_PUBLIC_KEY.equals(extension)){
                    selectFileText(uri);
                } else {
                    postStep(Step.SELECTED, "unknown extension " + extension);
                }
            }
        });
    }
}
private void selectFileGif(DialogModalProgressIndeterminate dialog, UriW uri){
    DataQr dataQr = new DataQr();
    dataQr.fromGif(uri, true).observe(new ObserverValueE<String>(this){
        @Override
        public void onComplete(String s){
            dialog.close();
            PostToHandler.of(getView(), new RunnableW(){
                @Override
                public void runSafe(){
                    if(onSelected(s)){
                        postStep(Step.SELECTED);
                    } else {
                        postStep(Step.SELECTED, "not accepted");
                    }
                }
            });
        }
        @Override
        public void onException(String s, Throwable e){
            dialog.close();
            PostToHandler.of(getView(), new RunnableW(){
                @Override
                public void runSafe(){
                    postStep(Step.SELECTED, e);
                }
            });
        }
    });
}
private void selectFileText(UriW uri){
    try{
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        UtilsFile.transfer(uri.getInputStream(), out);
        String data = BytesTo.StringChar(out.toByteArray());
        PostToHandler.of(getView(), new RunnableW(){
            @Override
            public void runSafe(){
                if(onSelected(data)){
                    postStep(Step.SELECTED);
                } else {
                    postStep(Step.SELECTED, "not accepted");
                }
            }
        });
    } catch(Throwable e){
        postStep(Step.SELECTED, e);
    }
}
protected abstract boolean onSelected(String data);

protected abstract void onSetValueKeyShared();

protected abstract void onSetValueKeyPublic();

protected abstract void onStep(Step step, Throwable e);
protected enum Step{
    COPIED, SHOWED, SHARED, PASTED, SCANNED, SELECTED
}
public static class State extends DialogNavigable.State{
    @Override
    protected Param newParam(){
        return new Param();
    }
    @Override
    public Param obtainParam(){
        return (Param)super.obtainParam();
    }

}
public static class Param extends DialogNavigable.Param{


}

}
