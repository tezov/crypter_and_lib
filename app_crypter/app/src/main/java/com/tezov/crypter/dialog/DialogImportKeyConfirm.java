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
import android.view.View;
import android.widget.TextView;

import com.tezov.crypter.R;
import com.tezov.crypter.data.dbItem.dbKey;
import com.tezov.crypter.data.item.ItemKey;
import com.tezov.crypter.data.item.ItemKeyRing;
import com.tezov.crypter.data.item.UtilsKey;
import com.tezov.crypter.data.item.UtilsKeyAgreement;
import com.tezov.lib_java.generator.uid.defUid;

public class DialogImportKeyConfirm extends DialogOfferKeyBase{

@Override
public Param obtainParam(){
    return (Param)super.obtainParam();
}
@Override
public Param getParam(){
    return (Param)super.getParam();
}

@Override
protected int getFrameLayoutId(){
    return R.layout.dialog_import_key_confirm;
}
@Override
public void onPrepare(boolean hasBeenReconstructed){
    super.onPrepare(hasBeenReconstructed);
    Param param = getParam();
    dbKey dataKey = param.dataKey;
    ItemKey itemKey = dataKey.getItem();
    ItemKeyRing itemKeyRing = dataKey.getDataKeyRing().getItem();
    View view = getView();
    TextView createdDate = view.findViewById(R.id.lbl_created_date);
    createdDate.setText(itemKey.getCreatedDateString());
    TextView isOwnerLabel = view.findViewById(R.id.lbl_is_owner);
    boolean isOwnerKey = UtilsKey.isOwner(itemKey, itemKeyRing);
    isOwnerLabel.setText(isOwnerKey ? R.string.lbl_key_edit_yes : R.string.lbl_key_edit_no);
    TextView signatureKey = view.findViewById(R.id.lbl_signature_key);
    signatureKey.setText(itemKey.getSignatureKey());
    TextView signatureApp = view.findViewById(R.id.lbl_signature_app);
    signatureApp.setText(param.getSignatureApp());
    if(!isOwnerKey){
        chkEncryptStrictMode.setEnabled(false);
        chkEncryptFileName.setEnabled(false);
        chkEncryptAddDateAndTimeToFileName.setEnabled(false);
        chkEncryptSignText.setEnabled(false);
        chkEncryptAddDeeplinkToText.setEnabled(false);
    }
}

@Override
protected void beforeConfirm(String alias){
    dbKey dataKey = getParam().dataKey;
    dataKey.getItem().setAlias(alias);
}
@Override
protected boolean onConfirmed(dbKey dataKey, ItemKey itemKey, ItemKeyRing itemKeyRing){
    itemKey.setEncryptStrictMode(chkEncryptStrictMode.isChecked());
    if(chkPasswordNullify.isChecked() || chkEncryptStrictMode.isChecked()){
        dataKey.nullifyPassword();
    }
    boolean result = dataKey.insert();
    if(result){
        UtilsKeyAgreement.remove(getParam().keyAgreementUsedUid);
    }
    return result;
}
@Override
protected Object getConfirmData(){
    return getParam().dataKey;
}

public static class State extends DialogOfferKeyBase.State{
    @Override
    protected Param newParam(){
        return new Param();
    }
    @Override
    public Param obtainParam(){
        return (Param)super.obtainParam();
    }

}

public static class Param extends DialogOfferKeyBase.Param{
    public String signatureApp = null;
    public defUid keyAgreementUsedUid = null;
    public String getSignatureApp(){
        return signatureApp;
    }
    public Param setSignatureApp(String dataKey){
        this.signatureApp = dataKey;
        return this;
    }
    public defUid getKeyAgreementUsedUid(){
        return keyAgreementUsedUid;
    }
    public Param setKeyAgreementUsedUid(defUid keyAgreementUsedUid){
        this.keyAgreementUsedUid = keyAgreementUsedUid;
        return this;
    }

}

}
