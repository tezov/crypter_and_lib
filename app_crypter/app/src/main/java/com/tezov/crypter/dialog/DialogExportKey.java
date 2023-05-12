/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.crypter.dialog;

import com.tezov.lib_java.buffer.ByteBufferBuilder;
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

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.tezov.crypter.R;
import com.tezov.crypter.data.dbItem.dbKey;
import com.tezov.crypter.data.item.ItemKey;
import com.tezov.crypter.data.item.ItemKeyRing;
import com.tezov.crypter.data.item.UtilsKey;
import com.tezov.crypter.data_transformation.KeyAgreementPacket;
import com.tezov.crypter.misc.ValidatorExportKeyPublic;
import com.tezov.crypter.misc.ValidatorExportKeyShared;
import com.tezov.lib_java.buffer.ByteBuffer;
import com.tezov.lib_java.cipher.key.ecdh.KeyAgreement;
import com.tezov.lib_java.generator.uid.defUid;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java_android.wrapperAnonymous.ViewOnClickListenerW;
import com.tezov.lib_java_android.ui.component.plain.CheckBox;
import com.tezov.lib_java_android.ui.misc.StateView;

public class DialogExportKey extends DialogIEKeyBase{
private CheckBox chkTransferOwnership = null;
private defUid keyAgreementUidForeign = null;
@Override
public Param obtainParam(){
    return (Param)super.obtainParam();
}
@Override
public Param getParam(){
    return (Param)super.getParam();
}
@Override
public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
    View view = super.onCreateView(inflater, container, savedInstanceState);
    TextView lblNameKey = view.findViewById(R.id.lbl_name_key);
    lblNameKey.setText(getParam().dataKey.getItem().getAlias());
    chkTransferOwnership = view.findViewById(R.id.chk_transfer_ownership);
    chkTransferOwnership.setOnClickListener(new ViewOnClickListenerW(){
        @Override
        public void onClicked(View v){
            if(frmKeyPublic.isValid() && frmKeyShared.isValid()){
                buildKeyShared();
            }
        }
    });
    TextView signatureKey = view.findViewById(R.id.lbl_signature_key);
    signatureKey.setText(getParam().getDataKey().getItem().getSignatureKey());
    return view;
}

@Override
protected void onDisabledButtonsRetrieve(StateView stateView){
    super.onDisabledButtonsRetrieve(stateView);
    stateView.clickableIcon(frmKeyPublic);
}
@Override
protected int getLayoutId(){
    return R.layout.dialog_export_key;
}
@Override
public void onPrepare(boolean hasBeenReconstructed){
    super.onPrepare(hasBeenReconstructed);
    frmKeyPublic.setValidator(new ValidatorExportKeyPublic());
    frmKeyShared.setValidator(new ValidatorExportKeyShared());
}
@Override
protected int getTypeResourceId(){
    return R.string.lbl_type_encrypted_key;
}
@Override
protected String getDataToShare(){
    return frmKeyShared.getValue();
}
@Override
protected dbKey getDataKey(){
    return getParam().getDataKey();
}
@Override
protected boolean onScanned(String data){
    return updateKeyPublic(data);
}
@Override
protected boolean onPastedFromClipboard(String data){
    return updateKeyPublic(data);
}
@Override
protected boolean onSelected(String data){
    return updateKeyPublic(data);
}
@Override
protected void onSetValueKeyShared(){

}
@Override
protected void onSetValueKeyPublic(){
    if(frmKeyPublic.getValue() == null){
        frmKeyShared.setText(null);
        disableButtonsShare();
    } else {
        buildKeyShared();
    }
}

private void buildKeyShared(){
    Param param = getParam();
    dbKey dbKey = param.getDataKey();
    try{
        if(!frmKeyPublic.isValid()){
            frmKeyPublic.showError();
            throw new Throwable("string key public invalid");
        }
        dbKey.loadKeyRing(true);
        ItemKey itemKey = dbKey.getItem();
        ItemKeyRing itemKeyRing = dbKey.getDataKeyRing().getItem();
        boolean withOwnership = chkTransferOwnership.isChecked();
        if(!UtilsKey.isOwner(itemKey, itemKeyRing)){
            frmKeyShared.showError(ValidatorExportKeyShared.STRING_NOT_OWNER, null);
            throw new Throwable("do not own the key");
        }
        ByteBufferBuilder buffer = ByteBufferBuilder.obtain();
        buffer.put(itemKey.toBytes(withOwnership));
        buffer.put(itemKeyRing.toBytes(withOwnership));
        KeyAgreement keyAgreement = KeyAgreement.generate();
        if(!keyAgreement.build(frmKeyPublic.getValue())){
            frmKeyPublic.showError(ValidatorExportKeyPublic.STRING_INVALID, null);
            throw new Throwable("generate key public invalid");
        }
        param.keyAgreement = keyAgreement;
        KeyAgreement.Signer<byte[], String> signer = keyAgreement.newSignerBytesToString();
        String keyShared = signer.sign(buffer.array());
        KeyAgreementPacket packet = new KeyAgreementPacket();
        packet.setUid(keyAgreementUidForeign).setCodeOperation(KEY_SHARE_SENDER).setPayload(keyShared);
        frmKeyShared.setText(packet.toString(true));
        frmKeyShared.moveToEnd();
        if(!frmKeyShared.isValid()){
            frmKeyShared.showError(ValidatorExportKeyShared.STRING_FAIL_TO_ENCRYPT, null);
            throw new Throwable("build key shared sign");
        }
    } catch(Throwable e){
DebugException.start().log(e).end();
    }
    dbKey.unloadKeyRing();
}
private boolean updateKeyPublic(String s){
    KeyAgreementPacket packet = new KeyAgreementPacket();
    packet.fromString(s, KEY_PUBLIC_REQUESTER);
    if(packet.isValid()){
        keyAgreementUidForeign = packet.getUid();
        lblSignatureAppRemote.setVisibility(View.VISIBLE);
        lblSignatureAppRemote.setText(packet.getSignatureApp());
        frmKeyPublic.setText(packet.getPayload());
        frmKeyPublic.moveToEnd();
        return true;
    } else {
        keyAgreementUidForeign = null;
        lblSignatureAppRemote.setVisibility(View.GONE);
        lblSignatureAppRemote.setText(null);
        frmKeyPublic.setText(DATA_INVALID);
        frmKeyShared.setText(null);
        return false;
    }
}

@Override
protected void onStep(Step step, Throwable e){
    enableButtonsRetrieve(step);
    boolean isRetrieveStep = (step == Step.PASTED) || (step == Step.SCANNED) || (step == Step.SELECTED);
    if(!isRetrieveStep || ((e == null) && (frmKeyShared.getValue() != null))){
        enableButtonsShare(step);
    }
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
    public dbKey dataKey = null;
    public KeyAgreement keyAgreement = null;
    public KeyAgreement getKeyAgreement(){
        return keyAgreement;
    }
    public dbKey getDataKey(){
        return dataKey;
    }
    public Param setDataKey(dbKey dataKey){
        this.dataKey = dataKey;
        return this;
    }

}

}
