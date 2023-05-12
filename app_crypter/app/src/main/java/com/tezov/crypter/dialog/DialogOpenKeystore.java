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
import android.os.Bundle;
import android.view.View;

import com.tezov.crypter.R;
import com.tezov.crypter.application.AppInfo;
import com.tezov.crypter.application.Application;
import com.tezov.crypter.data_transformation.PasswordCipherL2;
import com.tezov.crypter.misc.ValidatorCredential;
import com.tezov.lib_java_android.application.AppKeyboard;
import com.tezov.lib_java.async.notifier.observer.value.ObserverValueE;
import com.tezov.lib_java_android.authentification.defAuthMethod;
import com.tezov.lib_java_android.toolbox.PostToHandler;
import com.tezov.lib_java.type.runnable.RunnableW;
import com.tezov.lib_java_android.ui.component.plain.FocusCemetery;
import com.tezov.lib_java_android.ui.dialog.modal.DialogModalRequest;
import com.tezov.lib_java_android.ui.form.component.plain.FormEditText;

public class DialogOpenKeystore extends DialogModalRequest{
private FormEditText frmPassword = null;
@Override
protected int getFrameLayoutId(){
    return R.layout.dialog_open_keystore;
}
@Override
protected void onFrameMerged(View view, Bundle savedInstanceState){
    frmPassword = view.findViewById(R.id.frm_password);
    frmPassword.setValidator(new ValidatorCredential());
    frmPassword.setText(null);
    AppKeyboard.show(frmPassword);
}

@Override
protected void onConfirm(){
    FocusCemetery.request(getView());
    if(!frmPassword.isValid()){
        frmPassword.showError();
        setButtonsEnable(true);
    } else {
        PasswordCipherL2 password = PasswordCipherL2.fromCiphered(frmPassword.getChars()).scramble(AppInfo.getGUID().toBytes());
        Application.userAuth().signIn(password).observe(new ObserverValueE<defAuthMethod.State.Is>(this){
            @Override
            public void onComplete(defAuthMethod.State.Is state){
                if(Application.userAuth().isAuthenticated()){
                    postConfirm();
                } else {
                    showError();
                }
            }
            @Override
            public void onException(defAuthMethod.State.Is state, Throwable e){
                showError();
            }
            void showError(){
                PostToHandler.of(getView(), new RunnableW(){
                    @Override
                    public void runSafe(){
                        frmPassword.showError(true);
                        setButtonsEnable(true);
                    }
                });
            }
        });
    }
}


}
