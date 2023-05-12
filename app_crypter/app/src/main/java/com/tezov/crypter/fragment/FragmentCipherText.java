/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.crypter.fragment;

import com.tezov.crypter.data_transformation.DataQr;
import com.tezov.crypter.data_transformation.StreamEncoder;
import com.tezov.lib_java.async.Handler;
import com.tezov.lib_java.async.notifier.observer.event.ObserverEvent;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.defEnum.Event;
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
import static com.tezov.crypter.application.SharePreferenceKey.SP_ENCRYPT_ADD_DEEPLINK_TO_TEXT_BOOL;
import static com.tezov.crypter.application.SharePreferenceKey.SP_ENCRYPT_KEY_LENGTH_INT;
import static com.tezov.crypter.application.SharePreferenceKey.SP_ENCRYPT_KEY_TRANSFORMATION_INT;
import static com.tezov.crypter.application.SharePreferenceKey.SP_ENCRYPT_SIGN_TEXT_BOOL;
import static com.tezov.crypter.pager.PagerCipherTextTabManager.ViewType;
import static com.tezov.crypter.pager.PagerCipherTextTabManager.newInstance;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.tezov.crypter.R;
import com.tezov.crypter.activity.ActivityMain;
import com.tezov.crypter.application.AppInfo;
import com.tezov.crypter.application.Application;
import com.tezov.crypter.data.dbItem.dbKey;
import com.tezov.crypter.data.item.ItemKey;
import com.tezov.crypter.data.item.ItemKeyRing;
import com.tezov.crypter.data.misc.ItemKeyMaker;
import com.tezov.crypter.data_transformation.FileDecoder;
import com.tezov.crypter.data_transformation.FileEncoder;
import com.tezov.crypter.data_transformation.PasswordCipherL2;
import com.tezov.crypter.data_transformation.StreamDecoder;
import com.tezov.crypter.data_transformation.StringDecoder;
import com.tezov.crypter.data_transformation.StringEncoder;
import com.tezov.crypter.navigation.NavigationArguments;
import com.tezov.crypter.pager.PagerCipherTextTabManager;
import com.tezov.crypter.pager.page.CipherTextInBinder;
import com.tezov.crypter.pager.page.CipherTextOutBinder;
import com.tezov.crypter.user.UserAuth;
import com.tezov.lib_java_android.application.SharedPreferences;
import com.tezov.lib_java.async.notifier.observer.event.ObserverEventE;
import com.tezov.lib_java.async.notifier.observer.state.ObserverStateE;
import com.tezov.lib_java.async.notifier.observer.value.ObserverValueE;
import com.tezov.lib_java_android.authentification.defAuthMethod;
import com.tezov.lib_java.cipher.key.KeySim;
import com.tezov.lib_java.generator.uid.UUID;
import com.tezov.lib_java.toolbox.Compare;
import com.tezov.lib_java_android.toolbox.PostToHandler;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.type.runnable.RunnableGroup;
import com.tezov.lib_java.type.runnable.RunnableW;
import com.tezov.lib_java_android.ui.dialog.modal.DialogModalProgressIndeterminate;
import com.tezov.lib_java_android.ui.misc.StateView;
import com.tezov.lib_java_android.ui.navigation.Navigate;

public class FragmentCipherText extends FragmentCipherBase{
protected Step step = Step.IDLE;
protected Operation operation = Operation.NONE;

protected StringEncoder stringEncoder = null;
protected StringDecoder stringDecoder = null;

private PagerCipherTextTabManager pagerManager = null;

private FragmentCipherText me(){
    return this;
}

@Override
protected int getLayoutId(){
    return R.layout.fragment_cipher_text;
}
@Nullable
@Override
public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
    View view = super.onCreateView(inflater, container, savedInstanceState);
    pagerManager = newInstance(this, view);
    return view;
}
@Override
public void onPrepare(boolean hasBeenReconstructed){
    super.onPrepare(hasBeenReconstructed);
    onNewNavigationArguments(NavigationArguments.get(this));
}
@Override
public boolean onNewNavigationArguments(com.tezov.lib_java_android.ui.navigation.NavigationArguments arg){
    NavigationArguments arguments = (NavigationArguments)arg;
    if(arguments.isTargetMe(getClass())){
        if(operation == Operation.NONE){
            pagerManager.showPage(ViewType.IN, new RunnableW(){
                @Override
                public void runSafe(){
                    pagerManager.onNewNavigationArguments((NavigationArguments)arg);
                }
            });
            return true;
        }
    }
    return super.onNewNavigationArguments(arg);
}
@Override
public void onOpen(boolean hasBeenReconstructed, boolean hasBeenRestarted){
    super.onOpen(hasBeenReconstructed, hasBeenRestarted);
    setToolbarTittle(R.string.frg_cipher_text_title);
}

@Override
protected void onDisabledButtons(StateView stateView){
    pagerManager.onDisabledButtons(stateView);
}
@Override
protected void onEnabledButtons(){
    pagerManager.enableButtons(operation);
}
@Override
protected void onPasswordChanged(StateView stateView){
    pagerManager.onPasswordChanged(stateView);
    super.onPasswordChanged(stateView);
}

public void setStep(Step newStep){
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
    pagerManager.setStep(previousStep, step, operation);
    enableButtons();
}
private void stepStart(Step previousStep){
    if(previousStep == Step.START){
        return;
    }
    pagerManager.setStep(previousStep, step, operation);
}
private void stepStarted(Step previousStep){
    if(previousStep == Step.STARTED){
        return;
    }
    pagerManager.setStep(previousStep, step, operation);
}
private void stepSucceed(Step previousStep){
    if(previousStep == Step.SUCCEED){
        return;
    }
    ActivityMain activityMain = (ActivityMain)getActivity();
    activityMain.showInterstitial().observe(new ObserverStateE(this){
        @Override
        public void onComplete(){
            pagerManager.setStep(previousStep, step, operation);
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
    pagerManager.setStep(previousStep, step, operation);
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
    pagerManager.setStep(previousStep, step, operation);
    enableButtons();
    operation = Operation.NONE;
}

private String getDataIn(){
    CipherTextInBinder.DataTextIn data = pagerManager.getData(ViewType.IN);
    if(data == null){
        return null;
    } else {
        return data.getText();
    }
}
public void setDataIn(CipherTextInBinder.DataTextIn data, boolean moveToIn){
    if(moveToIn){
        pagerManager.showPage(ViewType.IN, new RunnableW(){
            @Override
            public void runSafe(){
                pagerManager.setData(ViewType.IN, data, true);
            }
        });
    } else {
        pagerManager.setData(ViewType.IN, data, true);
    }
}
public void clearDataIn(){
    pagerManager.setData(ViewType.IN, null, true);
}

public CipherTextOutBinder.DataTextOut getDataOut(){
    return pagerManager.getData(ViewType.OUT);
}
public void setDataOut(CipherTextOutBinder.DataTextOut data, boolean moveToOut){
    if(moveToOut){
        pagerManager.showPage(ViewType.OUT, new RunnableW(){
            @Override
            public void runSafe(){
                pagerManager.setData(ViewType.OUT, data, true);
            }
        });
    } else {
        pagerManager.setData(ViewType.OUT, data, true);
    }
}
public void clearDataOut(){
    pagerManager.setData(ViewType.OUT, null, true);
}

@Override
protected boolean canStart(){
    return super.canStart() && (getDataIn() != null);
}
public void startEncrypt(){
    if(!canStart()){
        if(getDataIn() == null){

        }
        if(!isAlias && !frmPassword.isValid()){
            frmPassword.showError();
        }
        if(isAlias && !frmAlias.isValid()){
            frmAlias.showError();
        }
        setStep(Step.IDLE);
        return;
    }
    operation = Operation.ENCRYPT;
    setStep(Step.START);
    RunnableGroup gr = new RunnableGroup(this).name("Encrypt");
    int LABEL_ENCRYPT = gr.label();
    int KEY_DIALOG_PROGRESS = gr.key();
    if(isAlias && !UserAuth.isKeystoreOpened()){
        gr.add(new RunnableGroup.Action(){
            @Override
            public void runSafe(){
                Application.userAuth().signIn(null).observe(new ObserverValueE<defAuthMethod.State.Is>(this){
                    @Override
                    public void onComplete(defAuthMethod.State.Is state){
                        if(Application.userAuth().isAuthenticated()){
                            skipUntilLabel(LABEL_ENCRYPT);
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
    gr.add(new RunnableGroup.Action(LABEL_ENCRYPT){
        @Override
        public void runSafe(){
            DialogModalProgressIndeterminate.State state = new DialogModalProgressIndeterminate.State();
            DialogModalProgressIndeterminate.Param param = state.obtainParam();
            param.setTitle(R.string.lbl_processing);
            Navigate.To(DialogModalProgressIndeterminate.class, state).observe(new ObserverValueE<>(this){
                @Override
                public void onComplete(DialogModalProgressIndeterminate dialog){
                    put(KEY_DIALOG_PROGRESS, dialog);
                    next();
                }
                @Override
                public void onException(DialogModalProgressIndeterminate dialogProgressIndeterminate, Throwable e){
                    putException(e);
                    done();
                }
            });
        }
    }.name("open dialog progress"));
    gr.add(new RunnableGroup.Action(){
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
            }
            else {
                UUID guid = AppInfo.getGUID();
                KeySim.Transformation keyTransformation = KeySim.Transformation.find(sp.getInt(SP_ENCRYPT_KEY_TRANSFORMATION_INT));
                KeySim.Length keyLength = KeySim.Length.findWithId(sp.getInt(SP_ENCRYPT_KEY_LENGTH_INT));
                itemKeyRing = ItemKeyRing.obtain().generateKey(PasswordCipherL2.fromClear(frmPassword.getChars()), guid, keyTransformation, keyLength).generateRing();
                itemKey = ItemKey.obtain().clear().generate(null, guid);
                itemKey.setEncryptStrictMode(false);
                itemKey.setEncryptSignText(Compare.isTrue(sp.getBoolean(SP_ENCRYPT_SIGN_TEXT_BOOL)));
                itemKey.setEncryptAddDeepLinkToText(Compare.isTrue(sp.getBoolean(SP_ENCRYPT_ADD_DEEPLINK_TO_TEXT_BOOL)));
            }
            StringEncoder stringEncoder = new StringEncoder();
            me().stringEncoder = stringEncoder;
            stringEncoder.setItemKey(itemKey, itemKeyRing);
            DialogModalProgressIndeterminate.State state = new DialogModalProgressIndeterminate.State();
            DialogModalProgressIndeterminate.Param param = state.obtainParam();
            param.setTitle(R.string.lbl_processing);
            stringEncoder.observe(new ObserverEventE<>(this, FileEncoder.Step.FINALISE){
                @Override
                public void onComplete(FileEncoder.Step step, Integer value){
                    CipherTextOutBinder.DataTextOut data = new CipherTextOutBinder.DataTextOut();
                    data.setText(stringEncoder.getOutString());
                    data.setItemKey(itemKey);
                    data.setSignatureApp(AppInfo.getSignature());
                    data.setEncryptedDate(stringEncoder.getEncryptedDateString());
                    setDataOut(data, true);
                    me().stringEncoder = null;
                    next();
                }
                @Override
                public void onException(FileEncoder.Step step, Integer value, Throwable e){
DebugException.start().log(e).end();
                    ItemKeyRing itemKeyRing = stringEncoder.getItemKeyRing();
                    if(itemKeyRing != null){
                        itemKeyRing.clear();
                    }
                    me().stringEncoder = null;
                    putException(e);
                    next();
                }
            });
            stringEncoder.encode(getDataIn());
        }
    }.name("encrypt"));
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            DialogModalProgressIndeterminate dialog = get(KEY_DIALOG_PROGRESS);
            dialog.observe(new ObserverEvent<>(this, Event.ON_CLOSE){
                @Override
                public void onComplete(Event.Is is, Object object){
                    next();
                }
            });
            dialog.close();
        }
    }.name("close dialog"));
    gr.setOnDone(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            if(getException() != null){
                setStep(Step.FAILED);
            }
            else{
                setStep(Step.SUCCEED);
            }
        }
    });
    gr.start();
}
public void startDecrypt(){
    if(!canStart()){
        if(getDataIn() == null){

        }
        if(!isAlias && !frmPassword.isValid()){
            frmPassword.showError();
        }
        if(isAlias && !frmAlias.isValid()){
            frmAlias.showError();
        }
        setStep(Step.IDLE);
        return;
    }
    operation = Operation.DECRYPT;
    setStep(Step.START);
    RunnableGroup gr = new RunnableGroup(this).name("decrypt");
    int LABEL_DECRYPT = gr.label();
    int KEY_DIALOG_PROGRESS = gr.key();
    if(isAlias && !UserAuth.isKeystoreOpened()){
        gr.add(new RunnableGroup.Action(){
            @Override
            public void runSafe(){
                Application.userAuth().signIn(null).observe(new ObserverValueE<defAuthMethod.State.Is>(this){
                    @Override
                    public void onComplete(defAuthMethod.State.Is state){
                        if(Application.userAuth().isAuthenticated()){
                            skipUntilLabel(LABEL_DECRYPT);
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
    gr.add(new RunnableGroup.Action(LABEL_DECRYPT){
        @Override
        public void runSafe() throws Throwable{
            DialogModalProgressIndeterminate.State state = new DialogModalProgressIndeterminate.State();
            DialogModalProgressIndeterminate.Param param = state.obtainParam();
            param.setTitle(R.string.lbl_processing);
            Navigate.To(DialogModalProgressIndeterminate.class, state).observe(new ObserverValueE<>(this){
                @Override
                public void onComplete(DialogModalProgressIndeterminate dialog){
                    put(KEY_DIALOG_PROGRESS, dialog);
                    next();
                }
                @Override
                public void onException(DialogModalProgressIndeterminate dialogProgressIndeterminate, Throwable e){
                    putException(e);
                    done();
                }
            });
        }
    }.name("open dialog progress"));
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            StreamDecoder.ItemKeyMaker itemKeyMaker = new ItemKeyMaker(isAlias, frmPassword.getChars(), getDataKey(true));
            StringDecoder stringDecoder = new StringDecoder();
            me().stringDecoder = stringDecoder;
            stringDecoder.setItemKeyMaker(itemKeyMaker);
            DialogModalProgressIndeterminate.State state = new DialogModalProgressIndeterminate.State();
            DialogModalProgressIndeterminate.Param param = state.obtainParam();
            param.setTitle(R.string.lbl_processing);
            stringDecoder.observe(new ObserverEventE<>(this, FileDecoder.Step.FINALISE){
                @Override
                public void onComplete(FileDecoder.Step step, Integer value){
                    ItemKey itemKey = itemKeyMaker.getItemKey();
                    CipherTextOutBinder.DataTextOut data = new CipherTextOutBinder.DataTextOut();
                    data.setText(stringDecoder.getOutString());
                    data.setItemKey(itemKey);
                    data.setSignatureApp(itemKeyMaker.getSignatureApp());
                    data.setEncryptedDate(stringDecoder.getEncryptedDateString());
                    setDataOut(data, true);
                    me().stringDecoder = null;
                    next();
                }
                @Override
                public void onException(FileDecoder.Step step, Integer value, Throwable e){
DebugException.start().log(e).end();
                    ItemKeyRing itemKeyRing = stringDecoder.getItemKeyRing();
                    if(itemKeyRing != null){
                        itemKeyRing.clear();
                    }
                    me().stringDecoder = null;
                    putException(e);
                    next();
                }
            });
            stringDecoder.decode(getDataIn());
        }
    }.name("decrypt"));
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            DialogModalProgressIndeterminate dialog = get(KEY_DIALOG_PROGRESS);
            dialog.observe(new ObserverEvent<>(this, Event.ON_CLOSE){
                @Override
                public void onComplete(Event.Is is, Object object){
                    next();
                }
            });
            dialog.close();
        }
    }.name("close dialog"));
    gr.setOnDone(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            if(getException() != null){
                setStep(Step.FAILED);
            }
            else{
                setStep(Step.SUCCEED);
            }
        }
    });
    gr.start();
}

@Override
public boolean onBackPressed(){
    if(pagerManager.getCurrentViewType() == ViewType.OUT){
        pagerManager.showPage(ViewType.IN, null);
        return true;
    } else {
        return super.onBackPressed();
    }
}
public enum Step{
    IDLE, START, STARTED, FAILED, SUCCEED, ABORT, ABORTED
}
public enum Operation{
    NONE, ENCRYPT, DECRYPT
}

}
