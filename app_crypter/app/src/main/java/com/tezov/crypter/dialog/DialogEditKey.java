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
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import com.tezov.lib_java_android.application.AppContext;

import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.tezov.crypter.R;
import com.tezov.crypter.data.dbItem.dbKey;
import com.tezov.crypter.data.item.ItemKey;
import com.tezov.crypter.data.item.ItemKeyRing;
import com.tezov.crypter.data.item.UtilsKey;
import com.tezov.lib_java_android.application.AppKeyboard;
import com.tezov.lib_java.cipher.key.KeySim;

import java.util.List;

public class DialogEditKey extends DialogOfferKeyBase{


@Override
protected int getFrameLayoutId(){
    return R.layout.dialog_edit_key;
}
@Override
protected void onFrameMerged(View view, Bundle savedInstanceState){
    super.onFrameMerged(view, savedInstanceState);
    AppKeyboard.show(frmAlias);
}

@Override
public void onPrepare(boolean hasBeenReconstructed){
    super.onPrepare(hasBeenReconstructed);
    Param param = getParam();
    dbKey dataKey = param.dataKey;
    dataKey.loadKeyRing(true);
    ItemKey itemKey = dataKey.getItem();
    ItemKeyRing itemKeyRing = dataKey.getDataKeyRing().getItem();
    View view = getView();
    TextView created = view.findViewById(R.id.lbl_created_date);
    created.setText(itemKey.getCreatedDateString());
    TextView isOwnerLabel = view.findViewById(R.id.lbl_is_owner);
    boolean isOwnerKey = UtilsKey.isOwner(itemKey, itemKeyRing);
    isOwnerLabel.setText(isOwnerKey ? R.string.lbl_key_edit_yes : R.string.lbl_key_edit_no);
    TextView isCreatedByApp = view.findViewById(R.id.lbl_created_by_app);
    isCreatedByApp.setText(itemKey.isCreatedByApp() ? R.string.lbl_key_edit_yes : R.string.lbl_key_edit_no);
    TextView signatureKey = view.findViewById(R.id.lbl_signature_key);
    signatureKey.setText(itemKey.getSignatureKey());
    KeySim keyKey = itemKeyRing.getKeyKey();
    List<String> keyAlgo = AppContext.getResources().getStrings(R.array.encrypt_key_transformation);
    TextView lblKeyAlgo = view.findViewById(R.id.lbl_key_algo);
    String algo = keyAlgo.get(keyKey.getTransformation().getId());
    lblKeyAlgo.setText(algo);
    List<String> keyLength = AppContext.getResources().getStrings(R.array.encrypt_key_length);
    TextView lblKeyLength = view.findViewById(R.id.lbl_key_length);
    String length = keyLength.get(KeySim.Length.findWithLength(keyKey.getLength()).getId());
    lblKeyLength.setText(length);
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
    return dataKey.offer();
}
@Override
protected void onCancel(){
    Param param = getParam();
    dbKey dataKey = param.dataKey;
    dataKey.getDataKeyRing().getItem().clear();
    super.onCancel();
}


}
