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
import static com.tezov.crypter.application.SharePreferenceKey.SP_ENCRYPT_KEY_LENGTH_INT;
import static com.tezov.crypter.application.SharePreferenceKey.SP_ENCRYPT_KEY_TRANSFORMATION_INT;

import android.os.Bundle;
import android.view.View;
import android.widget.Spinner;

import androidx.annotation.Nullable;

import com.tezov.crypter.R;
import com.tezov.crypter.application.Application;
import com.tezov.crypter.data.dbItem.dbKey;
import com.tezov.crypter.data.item.ItemKey;
import com.tezov.crypter.data.item.ItemKeyRing;
import com.tezov.crypter.data.item.UtilsKey;
import com.tezov.crypter.data_transformation.PasswordCipherL2;
import com.tezov.lib_java_android.application.AppKeyboard;
import com.tezov.lib_java_android.application.SharedPreferences;
import com.tezov.lib_java.cipher.key.KeySim;
import com.tezov.lib_java_android.ui.form.component.plain.FormEditText;
import com.tezov.lib_java.data.validator.ValidatorNotEmpty;

public class DialogCreateKey extends DialogOfferKeyBase{
private FormEditText frmPassword = null;
private Spinner spnKeyAlgo = null;
private Spinner spnKeyLength = null;

@Override
protected int getFrameLayoutId(){
    return R.layout.dialog_create_key;
}
@Override
protected void onFrameMerged(View view, @Nullable Bundle savedInstanceState){
    super.onFrameMerged(view, savedInstanceState);
    frmPassword = view.findViewById(R.id.frm_password);
    frmPassword.setValidator(new ValidatorNotEmpty<>());
    frmPassword.setText(null);
    spnKeyAlgo = view.findViewById(R.id.spn_key_algo);
    spnKeyLength = view.findViewById(R.id.spn_key_length);
    AppKeyboard.show(frmAlias);
}
@Override
public void onPrepare(boolean hasBeenReconstructed){
    super.onPrepare(hasBeenReconstructed);
    SharedPreferences sp = Application.sharedPreferences();
    spnKeyAlgo.setSelection(sp.getInt(SP_ENCRYPT_KEY_TRANSFORMATION_INT));
    spnKeyLength.setSelection(sp.getInt(SP_ENCRYPT_KEY_LENGTH_INT));
}
@Override
protected boolean canConfirm(){
    return super.canConfirm() && frmPassword.isValid();
}

@Override
protected void showConfirmError(){
    super.showConfirmError();
    if(!frmPassword.isValid()){
        frmPassword.showError();
    }
}
@Override
protected void beforeConfirm(String alias){
    KeySim.Transformation keyTransformation = KeySim.Transformation.find(spnKeyAlgo.getSelectedItemPosition());
    KeySim.Length keyLength = KeySim.Length.findWithId(spnKeyLength.getSelectedItemPosition());
    getParam().dataKey = UtilsKey.generate(alias, PasswordCipherL2.fromClear(frmPassword.getChars()), keyTransformation, keyLength);
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
protected Object getConfirmData(){
    return getParam().dataKey;
}

}
