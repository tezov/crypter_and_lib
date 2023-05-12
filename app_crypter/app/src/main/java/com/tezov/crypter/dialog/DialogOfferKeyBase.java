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
import static com.tezov.crypter.application.SharePreferenceKey.SP_DECRYPT_DELETE_FILE_BOOL;
import static com.tezov.crypter.application.SharePreferenceKey.SP_DECRYPT_OVERWRITE_FILE_BOOL;
import static com.tezov.crypter.application.SharePreferenceKey.SP_ENCRYPT_ADD_DEEPLINK_TO_TEXT_BOOL;
import static com.tezov.crypter.application.SharePreferenceKey.SP_ENCRYPT_ADD_TIME_AND_DATE_TO_FILE_NAME_BOOL;
import static com.tezov.crypter.application.SharePreferenceKey.SP_ENCRYPT_DELETE_FILE_ORIGINAL_BOOL;
import static com.tezov.crypter.application.SharePreferenceKey.SP_ENCRYPT_FILE_NAME_BOOL;
import static com.tezov.crypter.application.SharePreferenceKey.SP_ENCRYPT_OVERWRITE_FILE_BOOL;
import static com.tezov.crypter.application.SharePreferenceKey.SP_ENCRYPT_SIGN_TEXT_BOOL;
import static com.tezov.crypter.application.SharePreferenceKey.SP_ENCRYPT_STRICT_MODE_BOOL;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;

import com.tezov.crypter.R;
import com.tezov.crypter.application.Application;
import com.tezov.crypter.data.dbItem.dbKey;
import com.tezov.crypter.data.item.ItemKey;
import com.tezov.crypter.data.item.ItemKeyRing;
import com.tezov.lib_java_android.application.SharedPreferences;
import com.tezov.lib_java.async.notifier.observer.event.ObserverEvent;
import com.tezov.lib_java.async.notifier.observer.value.ObserverValueE;
import com.tezov.lib_java.toolbox.Compare;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java_android.wrapperAnonymous.ViewOnClickListenerW;
import com.tezov.lib_java.type.defEnum.Event;
import com.tezov.lib_java_android.ui.component.plain.CheckBox;
import com.tezov.lib_java_android.ui.component.plain.FocusCemetery;
import com.tezov.lib_java_android.ui.dialog.modal.DialogModalRequest;
import com.tezov.lib_java_android.ui.form.component.plain.FormEditText;
import com.tezov.lib_java.data.validator.ValidatorNotEmpty;
import com.tezov.lib_java_android.ui.navigation.Navigate;

public abstract class DialogOfferKeyBase extends DialogModalRequest{
protected FormEditText frmAlias = null;

protected CheckBox chkEncryptFileName = null;
protected CheckBox chkEncryptAddDateAndTimeToFileName = null;
protected CheckBox chkEncryptOverwriteFile = null;
protected CheckBox chkEncryptDeleteFileOriginal = null;
protected CheckBox chkEncryptStrictMode = null;
protected CheckBox chkDecryptOverwrite = null;
protected CheckBox chkDecryptDeleteEncryptedFile = null;
protected CheckBox chkEncryptSignText = null;
protected CheckBox chkEncryptAddDeeplinkToText = null;
protected CheckBox chkPasswordNullify = null;

@Override
public Param obtainParam(){
    return (Param)super.obtainParam();
}
@Override
public Param getParam(){
    return (Param)super.getParam();
}

@Override
protected boolean enableScrollbar(){
    return true;
}
@Override
public void setButtonsEnable(boolean flag){
    super.setButtonsEnable(flag);
    frmAlias.setClickableIcon(flag);
}
@Override
protected void onFrameMerged(View view, @Nullable Bundle savedInstanceState){
    frmAlias = view.findViewById(R.id.frm_alias);
    frmAlias.setValidator(new ValidatorNotEmpty<>());
    View viewOptions = view.findViewById(R.id.container_option_buttons);
    Button btnOptionStart = view.findViewById(R.id.btn_option_start);
    Button btnOptionEnd = view.findViewById(R.id.btn_option_end);
    ViewOnClickListenerW onClickOptionListener = new ViewOnClickListenerW(){
        @Override
        public void onClicked(View v){
            if(viewOptions.getVisibility() == View.VISIBLE){
                viewOptions.setVisibility(View.GONE);
                btnOptionStart.setActivated(false);
                btnOptionEnd.setActivated(false);
            } else {
                viewOptions.setVisibility(View.VISIBLE);
                btnOptionStart.setActivated(true);
                btnOptionEnd.setActivated(true);
                FocusCemetery.request(getView());
            }
        }
    };
    btnOptionEnd.setOnClickListener(onClickOptionListener);
    btnOptionStart.setOnClickListener(onClickOptionListener);
    chkEncryptOverwriteFile = view.findViewById(R.id.chk_encrypt_overwrite_file);
    chkEncryptFileName = view.findViewById(R.id.chk_encrypt_file_name);
    chkEncryptDeleteFileOriginal = view.findViewById(R.id.chk_encrypt_delete_file_original);
    chkEncryptAddDateAndTimeToFileName = view.findViewById(R.id.chk_encrypt_add_date_and_time_to_file_name);
    chkEncryptAddDateAndTimeToFileName = view.findViewById(R.id.chk_encrypt_add_date_and_time_to_file_name);
    chkDecryptOverwrite = view.findViewById(R.id.chk_decrypt_overwrite_file);
    chkDecryptDeleteEncryptedFile = view.findViewById(R.id.chk_decrypt_delete_file_encrypted);
    chkEncryptSignText = view.findViewById(R.id.chk_encrypt_sign_text);
    chkEncryptAddDeeplinkToText = view.findViewById(R.id.chk_encrypt_add_deeplink_to_text);
    chkPasswordNullify = view.findViewById(R.id.chk_password_nullify);
    chkEncryptStrictMode = view.findViewById(R.id.chk_encrypt_strict_mode);
    chkEncryptStrictMode.setOnClickListener(new ViewOnClickListenerW(){
        @Override
        public void onClicked(View view){
            dbKey dataKey = getParam().dataKey;
            ItemKey itemKey = null;
            if(dataKey != null){
                itemKey = dataKey.getItem();
            }
            if(chkEncryptStrictMode.isChecked()){
                openDialog_StrictModeConfirm();
            } else if((itemKey == null) || itemKey.hasPassword()){
                chkPasswordNullify.setVisibility(View.VISIBLE);
            }
        }
    });
}
private void openDialog_StrictModeConfirm(){
    Navigate.To(DialogStrictModeConfirm.class, DialogStrictModeConfirm.newStateDefault()).observe(new ObserverValueE<DialogStrictModeConfirm>(this){
        @Override
        public void onComplete(DialogStrictModeConfirm dialog){
            dialog.observe(new ObserverEvent<>(this, Event.ON_CONFIRM){
                @Override
                public void onComplete(Event.Is event, Object o){
                    chkPasswordNullify.setVisibility(View.GONE);
                }
            });
            dialog.observe(new ObserverEvent<>(this, Event.ON_CANCEL){
                @Override
                public void onComplete(Event.Is event, Object o){
                    chkEncryptStrictMode.setChecked(false);
                }
            });
        }
        @Override
        public void onException(DialogStrictModeConfirm dialog, Throwable e){
DebugException.start().log(e).end();
        }
    });
}

@Override
public void onPrepare(boolean hasBeenReconstructed){
    super.onPrepare(hasBeenReconstructed);
    Param param = getParam();
    dbKey dataKey = param.dataKey;
    if(dataKey == null){
        frmAlias.setText(null);
        SharedPreferences sp = Application.sharedPreferences();
        chkEncryptFileName.setChecked(Compare.isTrue(sp.getBoolean(SP_ENCRYPT_FILE_NAME_BOOL)));
        chkEncryptAddDateAndTimeToFileName.setChecked(Compare.isTrue(sp.getBoolean(SP_ENCRYPT_ADD_TIME_AND_DATE_TO_FILE_NAME_BOOL)));
        chkEncryptDeleteFileOriginal.setChecked(Compare.isTrue(sp.getBoolean(SP_ENCRYPT_DELETE_FILE_ORIGINAL_BOOL)));
        chkEncryptOverwriteFile.setChecked(Compare.isTrue(sp.getBoolean(SP_ENCRYPT_OVERWRITE_FILE_BOOL)));
        boolean mustEncryptStrictMode = Compare.isTrue(sp.getBoolean(SP_ENCRYPT_STRICT_MODE_BOOL));
        chkEncryptStrictMode.setChecked(mustEncryptStrictMode);
        if(mustEncryptStrictMode){
            chkPasswordNullify.setVisibility(View.GONE);
        }
        chkDecryptOverwrite.setChecked(Compare.isTrue(sp.getBoolean(SP_DECRYPT_OVERWRITE_FILE_BOOL)));
        chkDecryptDeleteEncryptedFile.setChecked(Compare.isTrue(sp.getBoolean(SP_DECRYPT_DELETE_FILE_BOOL)));
        chkEncryptSignText.setChecked(Compare.isTrue(sp.getBoolean(SP_ENCRYPT_SIGN_TEXT_BOOL)));
        chkEncryptAddDeeplinkToText.setChecked(Compare.isTrue(sp.getBoolean(SP_ENCRYPT_ADD_DEEPLINK_TO_TEXT_BOOL)));
    } else {
        ItemKey itemKey = dataKey.getItem();
        frmAlias.setText(itemKey.getAlias());
        chkEncryptOverwriteFile.setChecked(itemKey.mustEncryptOverwriteFile());
        chkEncryptFileName.setChecked(itemKey.mustEncryptFileName());
        chkEncryptAddDateAndTimeToFileName.setChecked(itemKey.mustEncryptAddTimeAndTimeToFileName());
        chkEncryptDeleteFileOriginal.setChecked(itemKey.mustEncryptDeleteOriginalFile());
        chkEncryptStrictMode.setChecked(itemKey.mustEncryptStrictMode());
        if(!itemKey.hasPassword() || itemKey.mustEncryptStrictMode()){
            chkPasswordNullify.setVisibility(View.GONE);
        }
        chkDecryptOverwrite.setChecked(itemKey.mustDecryptOverwriteFile());
        chkDecryptDeleteEncryptedFile.setChecked(itemKey.mustDecryptDeleteEncryptedFile());
        chkEncryptSignText.setChecked(itemKey.mustEncryptSigneText());
        chkEncryptAddDeeplinkToText.setChecked(itemKey.mustEncryptAddDeepLinkToText());
    }
}
protected boolean canConfirm(){
    FocusCemetery.request(getView());
    return frmAlias.isValid();
}
protected void showConfirmError(){
    if(!frmAlias.isValid()){
        frmAlias.showError();
    }
}

@Override
protected void onConfirm(){
    getBtnConfirm().setEnabled(false);
    if(!canConfirm()){
        showConfirmError();
        setButtonsEnable(true);
    } else {
        String alias = frmAlias.getValue();
        beforeConfirm(alias);
        Param param = getParam();
        dbKey dataKey = param.dataKey;
        ItemKey itemKey = dataKey.getItem();
        itemKey.setEncryptAddTimeAndTimeToFileName(chkEncryptAddDateAndTimeToFileName.isChecked());
        itemKey.setEncryptDeleteOriginalFile(chkEncryptDeleteFileOriginal.isChecked());
        itemKey.setEncryptFileName(chkEncryptFileName.isChecked());
        itemKey.setEncryptOverwriteFile(chkEncryptOverwriteFile.isChecked());
        itemKey.setDecryptDeleteEncryptedFile(chkDecryptDeleteEncryptedFile.isChecked());
        itemKey.setDecryptOverwriteFile(chkDecryptOverwrite.isChecked());
        itemKey.setEncryptSignText(chkEncryptSignText.isChecked());
        itemKey.setEncryptAddDeepLinkToText(chkEncryptAddDeeplinkToText.isChecked());
        if(onConfirmed(dataKey, itemKey, dataKey.getDataKeyRing().getItem())){
            postConfirm();
            close();
        }
    }
}
protected abstract void beforeConfirm(String alias);

protected abstract boolean onConfirmed(dbKey dataKey, ItemKey itemKey, ItemKeyRing itemKeyRing);

public static class State extends DialogModalRequest.State{
    @Override
    protected Param newParam(){
        return new Param();
    }
    @Override
    public Param obtainParam(){
        return (Param)super.obtainParam();
    }

}

public static class Param extends DialogModalRequest.Param{
    public dbKey dataKey = null;
    public dbKey getDataKey(){
        return dataKey;
    }
    public Param setDataKey(dbKey dataKey){
        this.dataKey = dataKey;
        return this;
    }

}

}
