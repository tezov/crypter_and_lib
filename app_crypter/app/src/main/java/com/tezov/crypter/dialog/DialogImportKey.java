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
import androidx.fragment.app.Fragment;
import static com.tezov.crypter.data_transformation.KeyAgreementPacket.DATA_INVALID;
import static com.tezov.crypter.data_transformation.KeyAgreementPacket.KEY_PUBLIC_REQUESTER;
import static com.tezov.crypter.data_transformation.KeyAgreementPacket.KEY_SHARE_SENDER;

import android.view.View;

import com.tezov.crypter.R;
import com.tezov.crypter.data.dbItem.dbKey;
import com.tezov.crypter.data.item.ItemKey;
import com.tezov.crypter.data.item.ItemKeyRing;
import com.tezov.crypter.data.item.UtilsKey;
import com.tezov.crypter.data.item.UtilsKeyAgreement;
import com.tezov.crypter.data_transformation.KeyAgreementPacket;
import com.tezov.crypter.misc.ValidatorImportKeyPublic;
import com.tezov.crypter.misc.ValidatorImportKeyShared;
import com.tezov.lib_java.async.notifier.observer.event.ObserverEvent;
import com.tezov.lib_java.async.notifier.observer.state.ObserverStateE;
import com.tezov.lib_java.async.notifier.observer.value.ObserverValueE;
import com.tezov.lib_java.async.notifier.task.TaskState;
import com.tezov.lib_java.buffer.ByteBuffer;
import com.tezov.lib_java.cipher.key.ecdh.KeyAgreement;
import com.tezov.lib_java.generator.uid.defUid;
import com.tezov.lib_java.toolbox.Compare;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.type.defEnum.Event;
import com.tezov.lib_java_android.ui.navigation.Navigate;

public class DialogImportKey extends DialogIEKeyBase{
private defUid keyAgreementUidForeign = null;

private static Class<DialogImportKey> myClass(){
    return DialogImportKey.class;
}

@Override
public Param obtainParam(){
    return (Param)super.obtainParam();
}
@Override
public Param getParam(){
    return (Param)super.getParam();
}

@Override
public void onPrepare(boolean hasBeenReconstructed){
    super.onPrepare(hasBeenReconstructed);
    frmKeyPublic.setValidator(new ValidatorImportKeyPublic());
    frmKeyShared.setValidator(new ValidatorImportKeyShared());
    generateKeyAgreement();
}

private void generateKeyAgreement(){
    Param param = getParam();
    KeyAgreement keyAgreement = param.keyAgreement;
    if(keyAgreement == null){
        keyAgreement = KeyAgreement.generate();
        param.keyAgreement = keyAgreement;
    }
    defUid uid = UtilsKeyAgreement.save(keyAgreement);
    KeyAgreementPacket packet = new KeyAgreementPacket();
    packet.setCodeOperation(KEY_PUBLIC_REQUESTER).setUid(uid).setPayload(keyAgreement.getKeyAsymPublic().toStringBase58());
    frmKeyPublic.setText(packet.toString(true));
    frmKeyPublic.moveToEnd();
}
private void buildKeyShared(){
    boolean succeed = false;
    dbKey dataKey = null;
    try{
        if(!frmKeyPublic.isValid()){
            frmKeyPublic.showError();
            throw new Throwable("string key public invalid");
        }
        if(!frmKeyShared.isValid()){
            frmKeyShared.showError();
            throw new Throwable("string key shared invalid");
        }
        KeyAgreement keyAgreement = UtilsKeyAgreement.get(keyAgreementUidForeign);
        if(keyAgreement == null){
            frmKeyShared.showError(ValidatorImportKeyShared.STRING_RETRIEVE_KEY_AGREEMENT_FAILED, null);
            throw new Throwable("failed to retrieve key agreement");
        }
        KeyAgreement.Verifier<String, byte[]> verifier = keyAgreement.newVerifierStringToBytes();
        verifier.load(frmKeyShared.getValue());
        byte[] keyShared = verifier.verify();
        if(keyShared == null){
            frmKeyShared.showError(ValidatorImportKeyShared.STRING_VERIFY_FAILED, null);
            throw new Throwable("failed to verify");
        }
        ByteBuffer buffer = ByteBuffer.wrap(keyShared);
        byte[] itemKeyBytes = buffer.getBytes();
        byte[] itemKeyRingBytes = buffer.getBytes();
        ItemKey itemKey = ItemKey.obtain().clear().fromBytes(itemKeyBytes);
        ItemKeyRing itemKeyRing = ItemKeyRing.obtain().fromBytes(itemKeyRingBytes);
        if(!Compare.equalsAndNotNull(itemKey.getKeyRingUid(), itemKeyRing.getUid())){
            frmKeyShared.showError(ValidatorImportKeyShared.STRING_VERIFY_FAILED, null);
            throw new Throwable("itemKeyRing.uidKeyRing and itemKeyRing.uid mismatch");
        }
        ItemKey itemKeyInTable = UtilsKey.get(itemKey.getUid());
        if(itemKeyInTable != null){
            UtilsKeyAgreement.remove(keyAgreementUidForeign);
            frmKeyShared.showError(ValidatorImportKeyShared.STRING_ALREADY_EXIST, ": " + itemKeyInTable.getAlias());
            throw new Throwable("itemKeyRing.uid already exist");
        }
        dataKey = new dbKey(null);
        dataKey.setItem(itemKey, false);
        dataKey.setKeyRing(itemKeyRing);
        succeed = true;
    } catch(Throwable e){
DebugException.start().log(e).end();
    }
    if(succeed){
        openDialogConfirm(dataKey).observe(new ObserverStateE(this){
            @Override
            public void onComplete(){
                close();
            }
            @Override
            public void onException(Throwable e){
DebugException.start().log(e).end();
            }
            @Override
            public void onCancel(){
            }
        });
    }
}
private boolean updateKeyShared(String s){
    KeyAgreementPacket packet = new KeyAgreementPacket();
    packet.fromString(s, KEY_SHARE_SENDER);
    if(packet.isValid()){
        keyAgreementUidForeign = packet.getUid();
        lblSignatureAppRemote.setVisibility(View.VISIBLE);
        lblSignatureAppRemote.setText(packet.getSignatureApp());
        frmKeyShared.setText(packet.getPayload());
        frmKeyShared.moveToEnd();
        return true;
    } else {
        keyAgreementUidForeign = null;
        lblSignatureAppRemote.setVisibility(View.GONE);
        lblSignatureAppRemote.setText(null);
        frmKeyShared.setText(DATA_INVALID);
        return false;
    }
}

@Override
protected int getLayoutId(){
    return R.layout.dialog_import_key;
}

@Override
protected int getTypeResourceId(){
    return R.string.lbl_type_public_key;
}
@Override
protected String getDataToShare(){
    return frmKeyPublic.getValue();
}
@Override
protected dbKey getDataKey(){
    return null;
}
@Override
protected void onSetValueKeyShared(){
    if(frmKeyShared.getValue() != null){
        buildKeyShared();
    }
}
@Override
protected void onSetValueKeyPublic(){

}

@Override
protected boolean onPastedFromClipboard(String data){
    return updateKeyShared(data);
}
@Override
protected boolean onScanned(String data){
    return updateKeyShared(data);
}
@Override
protected boolean onSelected(String data){
    return updateKeyShared(data);
}

private TaskState.Observable openDialogConfirm(dbKey data){
    TaskState task = new TaskState();
    DialogImportKeyConfirm.State state = new DialogImportKeyConfirm.State();
    DialogImportKeyConfirm.Param param = state.obtainParam();
    param.setSignatureApp(lblSignatureAppRemote.getText().toString())
            .setKeyAgreementUsedUid(keyAgreementUidForeign)
            .setDataKey(data)
            .setTitle(R.string.lbl_import_key_title)
            .setCancelButtonText(R.string.btn_cancel)
            .setConfirmButtonText(R.string.btn_import);
    Navigate.To(DialogImportKeyConfirm.class, state).observe(new ObserverValueE<DialogImportKeyConfirm>(this){
        @Override
        public void onComplete(DialogImportKeyConfirm dialog){
            observeDialogConfirm(task, dialog);
        }
        @Override
        public void onException(DialogImportKeyConfirm dialog, Throwable e){
            task.notifyException(e);
        }
    });
    return task.getObservable();
}
private void observeDialogConfirm(TaskState task, DialogImportKeyConfirm dialog){
    dialog.observe(new ObserverEvent<Event.Is, Object>(this, Event.ON_CONFIRM){
        @Override
        public void onComplete(Event.Is event, Object o){
            post(Event.ON_CONFIRM, o);
            task.notifyComplete();
        }
    });
    dialog.observe(new ObserverEvent<Event.Is, Object>(this, Event.ON_CANCEL){
        @Override
        public void onComplete(Event.Is event, Object o){
            frmKeyShared.setText(null);
            task.cancel();
            task.notifyCanceled();
        }
    });
}

@Override
protected void onStep(Step step, Throwable e){
    enableButtonsRetrieve(step);
    enableButtonsShare(step);
}

public static class State extends DialogIEKeyBase.State{
    @Override
    protected Param newParam(){
        return new Param();
    }
    @Override
    public Param obtainParam(){
        return (Param)super.obtainParam();
    }

}

public static class Param extends DialogIEKeyBase.Param{
    public KeyAgreement keyAgreement = null;
    public KeyAgreement getKeyAgreement(){
        return keyAgreement;
    }

}

}
