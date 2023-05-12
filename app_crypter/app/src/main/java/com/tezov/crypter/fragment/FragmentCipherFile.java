/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.crypter.fragment;

import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.toolbox.CompareType;
import java.util.Set;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import java.util.List;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import androidx.fragment.app.Fragment;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.util.UtilsString;
import com.tezov.lib_java.toolbox.Clock;
import java.util.LinkedList;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.crypter.data.table.db.dbHistoryTable;
import com.tezov.lib_java.async.notifier.observer.event.ObserverEvent;
import com.tezov.lib_java_android.ui.navigation.NavigatorManager;

import static com.tezov.crypter.application.Environment.EXTENSION_CIPHER_FILE;
import static com.tezov.crypter.application.SharePreferenceKey.SP_ENCRYPT_ADD_TIME_AND_DATE_TO_FILE_NAME_BOOL;
import static com.tezov.crypter.application.SharePreferenceKey.SP_ENCRYPT_DELETE_FILE_ORIGINAL_BOOL;
import static com.tezov.crypter.application.SharePreferenceKey.SP_ENCRYPT_FILE_NAME_BOOL;
import static com.tezov.crypter.application.SharePreferenceKey.SP_ENCRYPT_KEY_LENGTH_INT;
import static com.tezov.crypter.application.SharePreferenceKey.SP_ENCRYPT_KEY_TRANSFORMATION_INT;
import static com.tezov.crypter.application.SharePreferenceKey.SP_ENCRYPT_OVERWRITE_FILE_BOOL;
import static com.tezov.crypter.application.SharePreferenceKey.SP_HISTORY_FILE_DELETE_ON_CLOSE_BOOLEAN;
import static com.tezov.crypter.data.table.Descriptions.HISTORY;
import static com.tezov.crypter.navigation.NavigationHelper.DestinationKey.CIPHER_TEXT;
import static com.tezov.lib_java_android.ui.navigation.NavigatorManager.NavigatorKey.FRAGMENT;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.tezov.crypter.R;
import com.tezov.crypter.activity.ActivityMain;
import com.tezov.crypter.application.AppInfo;
import com.tezov.crypter.application.Application;
import com.tezov.crypter.application.Environment;
import com.tezov.crypter.application.Environment.MediaPath;
import com.tezov.crypter.data.dbItem.dbHistory;
import com.tezov.crypter.data.dbItem.dbKey;
import com.tezov.crypter.data.item.ItemHistory;
import com.tezov.crypter.data.item.ItemKey;
import com.tezov.crypter.data.item.ItemKeyRing;
import com.tezov.crypter.data.misc.ItemKeyMaker;
import com.tezov.crypter.data_transformation.FileDecoder;
import com.tezov.crypter.data_transformation.FileEncoder;
import com.tezov.crypter.data_transformation.PasswordCipherL2;
import com.tezov.crypter.data_transformation.StreamDecoder;
import com.tezov.crypter.navigation.NavigationArguments;
import com.tezov.crypter.recycler.historyFile.HistoryFileDataManager;
import com.tezov.crypter.recycler.historyFile.HistoryFileRowManager;
import com.tezov.crypter.recycler.historyFile.HistoryFileSwipper;
import com.tezov.crypter.user.UserAuth;
import com.tezov.lib_java.application.AppRandomNumber;
import com.tezov.lib_java_android.application.AppContext;
import com.tezov.lib_java_android.application.SharedPreferences;
import com.tezov.lib_java.async.Handler;
import com.tezov.lib_java.async.notifier.observer.event.ObserverEventE;
import com.tezov.lib_java.async.notifier.observer.state.ObserverStateE;
import com.tezov.lib_java.async.notifier.observer.value.ObserverValueE;
import com.tezov.lib_java_android.authentification.defAuthMethod;
import com.tezov.lib_java.cipher.key.KeySim;
import com.tezov.lib_java_android.file.StorageMedia;
import com.tezov.lib_java_android.file.UriW;
import com.tezov.lib_java.generator.uid.UUID;
import com.tezov.lib_java.toolbox.Compare;
import com.tezov.lib_java_android.toolbox.PostToHandler;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java_android.type.android.ViewTreeEvent;
import com.tezov.lib_java_android.wrapperAnonymous.ViewOnClickListenerW;
import com.tezov.lib_java.type.collection.ListOrObject;
import com.tezov.lib_java.type.runnable.RunnableGroup;
import com.tezov.lib_java.type.runnable.RunnableSubscription;
import com.tezov.lib_java.type.runnable.RunnableW;
import com.tezov.lib_java_android.ui.activity.ActivityToolbar;
import com.tezov.lib_java_android.ui.component.plain.ButtonIconMaterial;
import com.tezov.lib_java_android.ui.component.plain.ButtonMultiIconMaterial;
import com.tezov.lib_java_android.ui.form.component.plain.FormEditText;
import com.tezov.lib_java.data.validator.ValidatorNotEmpty;
import com.tezov.lib_java_android.ui.misc.StateView;
import com.tezov.lib_java_android.ui.misc.StateView.Feature;
import com.tezov.lib_java_android.ui.navigation.Navigate;
import com.tezov.lib_java_android.ui.recycler.RecyclerList;
import com.tezov.lib_java_android.ui.recycler.RecyclerListLayoutLinearVertical;
import com.tezov.lib_java_android.ui.recycler.prebuild.decoration.RecyclerDividerLineDecorationHorizontal;
import com.tezov.lib_java_android.ui.view.ProgressBarTransfer;

public class FragmentCipherFile extends FragmentCipherBase{
public final static int NOTIFY_RECYCLER_RESET = AppRandomNumber.nextInt();

protected Step step = Step.IDLE;
protected Operation operation = Operation.NONE;

protected UriW uriIn = null;
protected FileEncoder fileEncoder = null;
protected FileDecoder fileDecoder = null;

protected ButtonMultiIconMaterial btnSelectFile = null;
protected FormEditText frmFileNameIn = null;
protected ProgressBarTransfer progressBarTransfer = null;

protected ButtonMultiIconMaterial btnEncrypt = null;
protected ButtonMultiIconMaterial btnDecrypt = null;
protected ButtonIconMaterial btnAbort = null;
protected View containerBtnAbort = null;

protected RecyclerList recyclerHistory = null;

@Override
protected int getLayoutId(){
    return R.layout.fragment_cipher_file;
}
private FragmentCipherFile me(){
    return this;
}
@Nullable
@Override
public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
    View view = super.onCreateView(inflater, container, savedInstanceState);
    bindView(view);

    progressBarTransfer = new ProgressBarTransfer(view.findViewById(R.id.container_bar_progress), R.layout.mrg_progress_bar);
    progressBarTransfer.setSeparator(AppContext.getResources().getString(R.string.transfer_sep));
    progressBarTransfer.setMax(Long.parseLong(AppContext.getResources().getString(R.string.transfer_max)));
    progressBarTransfer.setUnit(AppContext.getResources().getString(R.string.transfer_unit));
    progressBarTransfer.setCurrent(0);

    btnSelectFile = view.findViewById(R.id.btn_select_file);
    btnSelectFile.setOnClickListener(new ViewOnClickListenerW(){
        @Override
        public void onClicked(View v){
            if(disableButtons()){
                selectFile();
            }
        }
    });
    btnSelectFile.setIndex(0);

    frmFileNameIn = view.findViewById(R.id.lbl_file_name_in);
    frmFileNameIn.setValidator(new ValidatorNotEmpty<>());
    frmFileNameIn.link(new FormEditText.EntryString(){
        @Override
        public <T> void onSetValue(Class<T> type){
            if(getValue() == null){
                btnSelectFile.setIndex(0);
            }
            else{
                btnSelectFile.setIndex(1);
            }
        }
    });

    frmFileNameIn.addOnClickListener(new ViewOnClickListenerW(){
        @Override
        public void onClicked(View v){
            if(disableButtons()){
                selectFile();
            }
        }
    });

    btnEncrypt = view.findViewById(R.id.btn_encrypt);
    btnEncrypt.setOnClickListener(new ViewOnClickListenerW(){
        @Override
        public void onClicked(View v){
            if(disableButtons()){
                startEncrypt();
            }
        }
    });
    btnEncrypt.setIndex(0);
    btnDecrypt = view.findViewById(R.id.btn_decrypt);
    btnDecrypt.setOnClickListener(new ViewOnClickListenerW(){
        @Override
        public void onClicked(View v){
            if(disableButtons()){
                startDecrypt();
            }
        }
    });
    btnDecrypt.setIndex(0);
    containerBtnAbort = view.findViewById(R.id.container_button_abort);
    btnAbort = view.findViewById(R.id.btn_abort);
    btnAbort.setOnClickListener(new ViewOnClickListenerW(){
        @Override
        public void onClicked(View v){
            if(disableButtons()){
                if(operation == Operation.ENCRYPT){
                    abortEncrypt();
                }
                else if(operation == Operation.DECRYPT){
                    abortDecrypt();
                }
            }
        }
    });
    recyclerHistory = view.findViewById(R.id.recycler);
    ViewTreeEvent.onLayout(view, new RunnableSubscription(){
        @Override
        public void onComplete(){
            unsubscribe();
            onViewBound();
        }
    });
    return view;
}

@Override
public void onPrepare(boolean hasBeenReconstructed){
    super.onPrepare(hasBeenReconstructed);
    onNewNavigationArguments(NavigationArguments.get(this));
    if(!hasBeenReconstructed){
        SharedPreferences sp = Application.sharedPreferences();
        if(Compare.isTrue(sp.getBoolean(SP_HISTORY_FILE_DELETE_ON_CLOSE_BOOLEAN))){
            dbHistoryTable.Ref table = Application.tableHolder().handle().getMainRef(HISTORY);
            table.remove(ItemHistory.Type.FILE);
        }
    }
    RecyclerListLayoutLinearVertical layoutManager = new RecyclerListLayoutLinearVertical();
    layoutManager.setReverseLayout(true);
    recyclerHistory.setLayoutManager(layoutManager);
    recyclerHistory.addItemDecoration(new RecyclerDividerLineDecorationHorizontal(R.drawable.recycler_divider, true));
    recyclerHistory.setItemTouchSwipper(new HistoryFileSwipper());
    HistoryFileDataManager dataManager = new HistoryFileDataManager();
    recyclerHistory.setRowManager(new HistoryFileRowManager(dataManager));
}

@Override
public boolean requestViewUpdate(Integer what, com.tezov.lib_java_android.ui.navigation.NavigationArguments arg){
    if(what == NOTIFY_RECYCLER_RESET){
        HistoryFileDataManager dataManager = new HistoryFileDataManager();
        recyclerHistory.setRowManager(new HistoryFileRowManager(dataManager));
        dataManager.postUpdatedAll(false);
        if(dataManager.size() > 0){
            Handler.PRIMARY().post(this, new RunnableW(){
                @Override
                public void runSafe(){
                    recyclerHistory.scrollToEnd();
                }
            });
        }
        setStep(Step.IDLE);
        return true;
    }
    else {
        return super.requestViewUpdate(what,arg);
    }
}
@Override
public boolean onNewNavigationArguments(com.tezov.lib_java_android.ui.navigation.NavigationArguments arg){
    NavigationArguments arguments = (NavigationArguments)arg;
    if(arguments.isTargetMe(getClass())){
        if(arguments.exist()){
            if(operation == Operation.NONE){
                ListOrObject<UriW> uris = arguments.getUris();
                setUriIn(uris.get());
                setStep(Step.IDLE);
                return true;
            }
        }
    }
    return super.onNewNavigationArguments(arg);
}
@Override
public void onOpen(boolean hasBeenReconstructed, boolean hasBeenRestarted){
    super.onOpen(hasBeenReconstructed, hasBeenRestarted);
    setToolbarTittle(R.string.frg_cipher_file_title);
}

public void setUriIn(UriW uri){
    if(uri != null){
        PostToHandler.of(getView(), new RunnableW(){
            @Override
            public void runSafe(){
                String fileFullName = uri.getFullName();
                frmFileNameIn.setText(fileFullName);
                frmFileNameIn.moveToEnd();
            }
        });
    }
    else {
        PostToHandler.of(getView(), new RunnableW(){
            @Override
            public void runSafe(){
                frmFileNameIn.setText(null);
            }
        });
    }
    this.uriIn = uri;
}
private void addHistoryEntry(Step step, FileEncoder fileEncoder, ItemKey itemKey){
    String signatureKey = null;
    if(itemKey.hasSignatureKey()){
        signatureKey = itemKey.getSignatureKey();
    }
    ItemHistory.File historyFile = new ItemHistory.File().clear()
            .setTimestamp(fileEncoder.getTimestamp())
            .setOperation(Operation.ENCRYPT)
            .setResult(step)
            .setUriIn(this.uriIn, !itemKey.mustEncryptDeleteOriginalFile())
            .setUriOut(fileEncoder.getUriOut())
            .setSignatureApp(AppInfo.getSignature())
            .setSignatureKey(signatureKey);
    dbHistory dataHistory = new dbHistory(historyFile.toItem());
    boolean result = dataHistory.offer();
    if(!result){
DebugException.start().log("fail to offer").end();
    }
    recyclerHistory.scrollToStart();
}
private void addHistoryEntry(Step step, FileDecoder fileDecoder, StreamDecoder.ItemKeyMaker itemKeyMaker){
    ItemKey itemKey = itemKeyMaker.getItemKey();
    String signatureKey = null;
    if((itemKey != null) && itemKey.hasSignatureKey()){
        signatureKey = itemKey.getSignatureKey();
    }
    ItemHistory.File historyFile = new ItemHistory.File().clear()
            .setTimestamp(fileDecoder.getTimestamp())
            .setOperation(Operation.DECRYPT)
            .setResult(step)
            .setUriIn(this.uriIn, (itemKey == null) || !itemKey.mustDecryptDeleteEncryptedFile())
            .setUriOut(fileDecoder.getUriOut())
            .setSignatureApp(itemKeyMaker.getSignatureApp())
            .setSignatureKey(signatureKey);
    dbHistory dataHistory = new dbHistory(historyFile.toItem());
    boolean result = dataHistory.offer();
    if(!result){
DebugException.start().log("fail to offer").end();
    }
    recyclerHistory.scrollToStart();
}

@Override
public void onDestroy(){
    if(operation == Operation.ENCRYPT){
        abortEncrypt();
    } else if(operation == Operation.DECRYPT){
        abortDecrypt();
    }
    super.onDestroy();
}
@Override
protected void onPasswordChanged(StateView stateView){
    if(btnEncrypt.getIndex() != 0){
        btnEncrypt.setIndex(0);
    }
    stateView.put(Feature.ENABLED, btnEncrypt, true);
    if(btnDecrypt.getIndex() != 0){
        btnDecrypt.setIndex(0);
    }
    stateView.put(Feature.ENABLED, btnDecrypt, true);
    super.onPasswordChanged(stateView);
}

@Override
protected void onDisabledButtons(StateView stateView){
    stateView.enableNot(btnSelectFile)
            .clickableNot(frmFileNameIn)
            .clickableIconNot(frmFileNameIn)
            .enableNot(btnEncrypt)
            .enableNot(btnDecrypt)
            .enableNot(btnAbort);
}
@Override
protected void onEnabledButtons(){
    btnSelectFile.setEnabled(true);
    frmFileNameIn.setClickable(true);
    frmFileNameIn.setClickableIcon(true);
    if((this.uriIn == null)&&(btnSelectFile.getIndex() != 0)){
        frmFileNameIn.setText(null);
    }
    btnAbort.setEnabled(false);
    containerBtnAbort.setVisibility(View.INVISIBLE);
    if(btnEncrypt.getIndex() == 0){
        if((operation != Operation.DECRYPT) || (this.uriIn != null)){
            btnEncrypt.setEnabled(true);
        }
    } else {
        btnEncrypt.setEnabled(true);
    }
    if(btnDecrypt.getIndex() == 0){
        if((operation != Operation.ENCRYPT) || (this.uriIn != null)){
            btnDecrypt.setEnabled(true);
        }
    } else {
        btnDecrypt.setEnabled(true);
    }
}

private void setStep(Step newStep){
    PostToHandler.of(getView(), new RunnableW(){
        @Override
        public void runSafe(){
            Step previousStep = me().step;
            me().step = newStep;
            switch(newStep){
                case IDLE:
                    stepIdle(previousStep);
                    break;
                case START:
                    stepStart(previousStep);
                    break;
                case STARTED:
                    stepStarted(previousStep);
                    break;
                case SUCCEED:
                    stepSucceed(previousStep);
                    break;
                case FAILED:
                    stepFailed(previousStep);
                    break;
                case ABORT:
                    stepAbort(previousStep);
                    break;
                case ABORTED:
                    stepAborted(previousStep);
                    break;
            }
        }
    });
}
private void stepIdle(Step previousStep){
    progressBarTransfer.setCurrent(0);
    if(btnEncrypt.getIndex() != 0){
        btnEncrypt.setIndex(0);
    }
    if(btnDecrypt.getIndex() != 0){
        btnDecrypt.setIndex(0);
    }
    enableButtons();
}
private void stepStart(Step previousStep){
    if(previousStep == Step.START){
        return;
    }
    progressBarTransfer.setCurrent(0);
    btnEncrypt.setIndex(0);
    btnDecrypt.setIndex(0);
}
private void stepStarted(Step previousStep){
    if(previousStep == Step.STARTED){
        return;
    }
    btnAbort.setEnabled(true);
    containerBtnAbort.setVisibility(View.VISIBLE);
}
private void stepSucceed(Step previousStep){
    if(previousStep == Step.SUCCEED){
        return;
    }
    ActivityMain activityMain = (ActivityMain)getActivity();
    activityMain.showInterstitial().observe(new ObserverStateE(this){
        @Override
        public void onComplete(){
            if(operation == Operation.ENCRYPT){
                btnEncrypt.setIndex(1);

            } else if(operation == Operation.DECRYPT){
                btnDecrypt.setIndex(1);
            }
            enableButtons();
            operation = Operation.NONE;
        }
        @Override
        public void onException(Throwable e){
            onComplete();
        }
    });
}
private void stepFailed(Step previousStep){
    if(previousStep == Step.FAILED){
        return;
    }
    if(operation == Operation.ENCRYPT){
        btnEncrypt.setIndex(2);
    } else if(operation == Operation.DECRYPT){
        btnDecrypt.setIndex(2);
    }
    enableButtons();
    operation = Operation.NONE;
}
private void stepAbort(Step previousStep){
    if(previousStep == Step.ABORT){
    }

}
private void stepAborted(Step previousStep){
    if(previousStep == Step.ABORTED){
        return;
    }
    if(operation == Operation.ENCRYPT){
        btnEncrypt.setIndex(3);
    } else if(operation == Operation.DECRYPT){
        btnDecrypt.setIndex(3);
    }
    enableButtons();
    operation = Operation.NONE;
}

protected void selectFile(){
    StorageMedia.openDocument(true).observe(new ObserverValueE<>(this){
        @Override
        public void onComplete(ListOrObject<UriW> uris){
            if(uris.size() > 1){
DebugException.start().log("multiple uri ignored").end();
            }
            UriW uri = uris.get();
            if(Environment.EXTENSION_CIPHER_TEXT.equals(uri.getExtension())){
                NavigationArguments arguments = NavigationArguments.create();
                arguments.setUris(uris);
                arguments.setTarget(FragmentCipherText.class);
                Navigate.observe(new ObserverEvent<>(this, FRAGMENT){
                    @Override
                    public void onComplete(NavigatorManager.NavigatorKey.Is navigator, NavigatorManager.Event event){
                        if(event == NavigatorManager.Event.ON_NAVIGATE_TO){
                            unsubscribe();
                            ((ActivityToolbar)getActivity()).getToolbarBottom().setChecked(CIPHER_TEXT.getId());
                        }
                    }
                });
                Navigate.To(CIPHER_TEXT, arguments);
            }
            else {
                setUriIn(uri);
                setStep(Step.IDLE);
            }
        }
        @Override
        public void onException(ListOrObject<UriW> uris, Throwable e){
            restoreButtons();
        }
    });
}
@Override
protected boolean canStart(){
    return super.canStart() && frmFileNameIn.isValid();
}
protected void startEncrypt(){
    if(!canStart()){
        if(!frmFileNameIn.isValid()){
            frmFileNameIn.showError();
        }
        if(!isAlias && !frmPassword.isValid()){
            frmPassword.showError();
        }
        if(isAlias && !frmAlias.isValid()){
            frmAlias.showError();
        }
        restoreButtons();
        return;
    }
    operation = Operation.ENCRYPT;
    setStep(Step.START);
    RunnableGroup gr = new RunnableGroup(this).name("Encrypt");
    int LABEL_REQUEST_PERMISSION = gr.label();
    int LABEL_ENCRYPT = gr.label();
    if(isAlias && !UserAuth.isKeystoreOpened()){
        gr.add(new RunnableGroup.Action(){
            @Override
            public void runSafe(){
                Application.userAuth().signIn(null).observe(new ObserverValueE<defAuthMethod.State.Is>(this){
                    @Override
                    public void onComplete(defAuthMethod.State.Is state){
                        if(Application.userAuth().isAuthenticated()){
                            if(hasLabel(LABEL_REQUEST_PERMISSION)){
                                skipUntilLabel(LABEL_REQUEST_PERMISSION);
                            } else {
                                skipUntilLabel(LABEL_ENCRYPT);
                            }
                        } else {
                            next();
                        }
                    }
                    @Override
                    public void onException(defAuthMethod.State.Is state, Throwable e){
                        next();
                    }
                });
            }
        }.name("try to open keystore"));
        gr.add(new RunnableGroup.Action(){
            @Override
            public void runSafe(){
                openDialog_OpenKeystore(false).observe(new ObserverStateE(this){
                    @Override
                    public void onComplete(){
DebugLog.start().here().end();
                        next();
                    }
                    @Override
                    public void onException(Throwable e){
                        putException(e);
                        done();
                    }
                });
            }
        }.name("open dialog KeystoreOpen"));
    }
    if(!StorageMedia.PERMISSION_CHECK_WRITE()){
        gr.add(new RunnableGroup.Action(LABEL_REQUEST_PERMISSION){
            @Override
            public void runSafe() throws Throwable{
                StorageMedia.PERMISSION_REQUEST_WRITE(true).observe(new ObserverStateE(this){
                    @Override
                    public void onComplete(){
                        next();
                    }
                    @Override
                    public void onException(Throwable e){
                        putException(e);
                        done();
                    }
                });
            }
        }.name("request permission write"));
    }
    gr.add(new RunnableGroup.Action(LABEL_ENCRYPT){
        @Override
        public void runSafe(){
            SharedPreferences sp = Application.sharedPreferences();
            ItemKey itemKey;
            ItemKeyRing itemKeyRing;
            if(isAlias){
                dbKey dataKey = getDataKey(true);
                if(dataKey == null){
                    throw new NullPointerException();
                }
                itemKeyRing = dataKey.getDataKeyRing().getItem().generateRing();
                itemKey = dataKey.getItem();
            } else {
                UUID guid = AppInfo.getGUID();
                KeySim.Transformation keyTransformation = KeySim.Transformation.find(sp.getInt(SP_ENCRYPT_KEY_TRANSFORMATION_INT));
                KeySim.Length keyLength = KeySim.Length.findWithId(sp.getInt(SP_ENCRYPT_KEY_LENGTH_INT));
                itemKeyRing = ItemKeyRing.obtain().generateKey(PasswordCipherL2.fromClear(frmPassword.getChars()), guid, keyTransformation, keyLength).generateRing();
                itemKey = ItemKey.obtain().clear().generate(null, guid);
                itemKey.setEncryptDeleteOriginalFile(Compare.isTrue(sp.getBoolean(SP_ENCRYPT_DELETE_FILE_ORIGINAL_BOOL)));
                itemKey.setEncryptAddTimeAndTimeToFileName(Compare.isTrue(sp.getBoolean(SP_ENCRYPT_ADD_TIME_AND_DATE_TO_FILE_NAME_BOOL)));
                itemKey.setEncryptFileName(Compare.isTrue(sp.getBoolean(SP_ENCRYPT_FILE_NAME_BOOL)));
                itemKey.setEncryptOverwriteFile(Compare.isTrue(sp.getBoolean(SP_ENCRYPT_OVERWRITE_FILE_BOOL)));
                itemKey.setEncryptStrictMode(false);
            }
            FileEncoder fileEncoder = new FileEncoder();
            me().fileEncoder = fileEncoder;
            fileEncoder.setItemKey(itemKey, itemKeyRing);
            fileEncoder.observe(new ObserverEventE<FileEncoder.Step, Integer>(this, FileEncoder.Step.START){
                @Override
                public void onComplete(FileEncoder.Step step, Integer value){
                    setStep(Step.STARTED);
                }
            });
            fileEncoder.observe(new ObserverEventE<FileEncoder.Step, Integer>(this, FileEncoder.Step.PROGRESS){
                @Override
                public void onComplete(FileEncoder.Step step, Integer value){
                    progressBarTransfer.setCurrent(value);
                }
            });
            fileEncoder.observe(new ObserverEventE<FileEncoder.Step, Integer>(this, FileEncoder.Step.DONE){
                @Override
                public void onComplete(FileEncoder.Step step, Integer value){
                    progressBarTransfer.setCurrent(100);
                    addHistoryEntry(Step.SUCCEED, fileEncoder, itemKey);
                }
                @Override
                public void onException(FileEncoder.Step encoderStep, Integer value, Throwable e){
DebugException.start().log(e).end();
                    Step step;
                    if((me().step == Step.START) || (me().step == Step.STARTED)){
                        step = Step.FAILED;
                    } else if(me().step == Step.ABORT){
                        step = Step.ABORTED;
                    } else {
                        step = Step.FAILED;
DebugException.start().unknown("step", me().step).end();
                    }
                    addHistoryEntry(step, fileEncoder, itemKey);
                }
            });
            fileEncoder.observe(new ObserverEventE<FileEncoder.Step, Integer>(this, FileEncoder.Step.FINALISE){
                @Override
                public void onComplete(FileEncoder.Step step, Integer value){
                    ItemKeyRing itemKeyRing = fileEncoder.getItemKeyRing();
                    if(itemKeyRing != null){
                        itemKeyRing.clear();
                    }
                    setUriIn(null);
                    me().fileEncoder = null;
                    setStep(Step.SUCCEED);
                    done();
                }
                @Override
                public void onException(FileEncoder.Step encoderStep, Integer value, Throwable e){
DebugException.start().log(e).end();
                    Step step;
                    if((me().step == Step.START) || (me().step == Step.STARTED)){
                        step = Step.FAILED;
                    } else if(me().step == Step.ABORT){
                        step = Step.ABORTED;
                    } else {
                        step = Step.FAILED;
DebugException.start().unknown("step", me().step).end();
                    }
                    ItemKeyRing itemKeyRing = fileEncoder.getItemKeyRing();
                    if(itemKeyRing != null){
                        itemKeyRing.clear();
                    }
                    me().fileEncoder = null;
                    setStep(step);
                    done();
                }
            });
            fileEncoder.encode(me().uriIn, MediaPath.ENCRYPTED_FILES, EXTENSION_CIPHER_FILE);
        }
    }.name("encrypt"));
    gr.setOnDone(new RunnableGroup.Action(){
        @Override
        public void runSafe() throws Throwable{
            if(getException() != null){
                setStep(Step.FAILED);
            }
        }
    });
    gr.start();
}
protected void abortEncrypt(){
    setStep(Step.ABORT);
    if(me().fileEncoder != null){
        me().fileEncoder.abort();
    } else {
        setStep(Step.ABORTED);
    }
}

protected void startDecrypt(){
    if(!canStart()){
        if(!frmFileNameIn.isValid()){
            frmFileNameIn.showError();
        }
        if(!isAlias && !frmPassword.isValid()){
            frmPassword.showError();
        }
        if(isAlias && !frmAlias.isValid()){
            frmAlias.showError();
        }
        restoreButtons();
        return;
    }
    operation = Operation.DECRYPT;
    setStep(Step.START);
    RunnableGroup gr = new RunnableGroup(this).name("Decrypt");
    int LABEL_REQUEST_PERMISSION = gr.label();
    int LABEL_DECRYPT = gr.label();
    if(isAlias && !UserAuth.isKeystoreOpened()){
        gr.add(new RunnableGroup.Action(){
            @Override
            public void runSafe(){
                Application.userAuth().signIn(null).observe(new ObserverValueE<defAuthMethod.State.Is>(this){
                    @Override
                    public void onComplete(defAuthMethod.State.Is state){
                        if(Application.userAuth().isAuthenticated()){
                            if(hasLabel(LABEL_REQUEST_PERMISSION)){
                                skipUntilLabel(LABEL_REQUEST_PERMISSION);
                            } else {
                                skipUntilLabel(LABEL_DECRYPT);
                            }
                        } else {
                            next();
                        }
                    }
                    @Override
                    public void onException(defAuthMethod.State.Is state, Throwable e){
                        next();
                    }
                });
            }
        }.name("try to open keystore"));
        gr.add(new RunnableGroup.Action(){
            @Override
            public void runSafe(){
                openDialog_OpenKeystore(false).observe(new ObserverStateE(this){
                    @Override
                    public void onComplete(){
                        next();
                    }
                    @Override
                    public void onException(Throwable e){
                        putException(e);
                        done();
                    }
                });
            }
        }.name("open dialog KeystoreOpen"));
    }
    if(!StorageMedia.PERMISSION_CHECK_WRITE()){
        gr.add(new RunnableGroup.Action(LABEL_REQUEST_PERMISSION){
            @Override
            public void runSafe() throws Throwable{
                StorageMedia.PERMISSION_REQUEST_WRITE(true).observe(new ObserverStateE(this){
                    @Override
                    public void onComplete(){
                        next();
                    }
                    @Override
                    public void onException(Throwable e){
                        putException(e);
                        done();
                    }
                });
            }
        }.name("request permission write"));
    }

    gr.add(new RunnableGroup.Action(LABEL_DECRYPT){
        @Override
        public void runSafe(){
            StreamDecoder.ItemKeyMaker itemKeyMaker = new ItemKeyMaker(isAlias, frmPassword.getChars(), getDataKey(true));
            FileDecoder fileDecoder = new FileDecoder();
            me().fileDecoder = fileDecoder;
            fileDecoder.setItemKeyMaker(itemKeyMaker);
            fileDecoder.observe(new ObserverEventE<FileDecoder.Step, Integer>(this, FileDecoder.Step.START){
                @Override
                public void onComplete(FileDecoder.Step step, Integer value){
                    setStep(Step.STARTED);
                }
            });
            fileDecoder.observe(new ObserverEventE<FileDecoder.Step, Integer>(this, FileDecoder.Step.PROGRESS){
                @Override
                public void onComplete(FileDecoder.Step step, Integer value){
                    progressBarTransfer.setCurrent(value);
                }
            });
            fileDecoder.observe(new ObserverEventE<FileDecoder.Step, Integer>(this, FileDecoder.Step.DONE){
                @Override
                public void onComplete(FileDecoder.Step step, Integer value){
                    progressBarTransfer.setCurrent(100);
                    addHistoryEntry(Step.SUCCEED, fileDecoder, itemKeyMaker);
                }
                @Override
                public void onException(FileDecoder.Step decoderStep, Integer value, Throwable e){
                    Step step;
                    if((me().step == Step.START) || (me().step == Step.STARTED)){
                        step = Step.FAILED;
                    } else if(me().step == Step.ABORT){
                        step = Step.ABORTED;
                    } else {
                        step = Step.FAILED;
DebugException.start().unknown("step", me().step).end();
                    }
                    addHistoryEntry(step, fileDecoder, itemKeyMaker);
                }
            });
            fileDecoder.observe(new ObserverEventE<FileDecoder.Step, Integer>(this, FileDecoder.Step.FINALISE){
                @Override
                public void onComplete(FileDecoder.Step step, Integer value){
                    ItemKeyRing itemKeyRing = fileDecoder.getItemKeyRing();
                    if(itemKeyRing != null){
                        itemKeyRing.clear();
                    }
                    setUriIn(null);
                    me().fileDecoder = null;
                    setStep(Step.SUCCEED);
                }
                @Override
                public void onException(FileDecoder.Step decoderStep, Integer value, Throwable e){
DebugException.start().log(e).end();
                    Step step;
                    if((me().step == Step.START) || (me().step == Step.STARTED)){
                        step = Step.FAILED;
                    } else if(me().step == Step.ABORT){
                        step = Step.ABORTED;
                    } else {
                        step = Step.FAILED;
DebugException.start().unknown("step", me().step).end();
                    }
                    ItemKeyRing itemKeyRing = fileDecoder.getItemKeyRing();
                    if(itemKeyRing != null){
                        itemKeyRing.clear();
                    }
                    me().fileEncoder = null;
                    setStep(step);
                }
            });
            fileDecoder.decode(me().uriIn, MediaPath.DECRYPTED_FILES);
        }
    }.name("decrypt"));
    gr.setOnDone(new RunnableGroup.Action(){
        @Override
        public void runSafe() throws Throwable{
            if(getException() != null){
                setStep(Step.FAILED);
            }
        }
    });
    gr.start();
}
protected void abortDecrypt(){
    setStep(Step.ABORT);
    if(me().fileDecoder != null){
        me().fileDecoder.abort();
    } else {
        setStep(Step.ABORTED);
    }
}

public enum Step{
    IDLE, START, STARTED, FAILED, SUCCEED, ABORT, ABORTED,
}
public enum Operation{
    NONE, ENCRYPT, DECRYPT
}

}
