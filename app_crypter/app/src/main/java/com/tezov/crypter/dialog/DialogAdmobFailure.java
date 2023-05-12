/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.crypter.dialog;

import com.tezov.crypter.application.Application;
import com.tezov.crypter.application.ApplicationSystem;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.toolbox.CompareType;
import java.util.Set;

import com.tezov.lib_java_android.application.AppContext;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import java.util.List;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import androidx.fragment.app.Fragment;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.util.UtilsString;
import com.tezov.lib_java.toolbox.Clock;
import java.util.LinkedList;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.primitive.ObjectTo;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.tezov.crypter.R;
import com.tezov.lib_java.async.notifier.observer.event.ObserverEvent;
import com.tezov.lib_java.async.notifier.observer.value.ObserverValueE;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.type.defEnum.Event;
import com.tezov.lib_java_android.ui.dialog.DialogNavigable;
import com.tezov.lib_java_android.ui.dialog.modal.DialogModalWebview;
import com.tezov.lib_java_android.ui.navigation.Navigate;

public class DialogAdmobFailure extends DialogModalWebview{

@NonNull
@Override
public Dialog onCreateDialog(Bundle savedInstanceState){
    setCancelable(false);
    return super.onCreateDialog(savedInstanceState);
}

public static State newStateDefault(){
    State state = new State();
    Param param = state.obtainParam();
    param.setRawFileId(R.raw.admob_failure)
            .setConfirmButtonText(R.string.btn_close);
    return state;
}
private static Class<DialogAdmobFailure> myClass(){
    return DialogAdmobFailure.class;
}
public static void open(){
    Navigate.To(DialogAdmobFailure.class, newStateDefault()).observe(new ObserverValueE<>(myClass()){
        @Override
        public void onComplete(DialogAdmobFailure dialogAdmobFailure){

        }
        @Override
        public void onException(DialogAdmobFailure dialogAdmobFailure, Throwable e){
            ApplicationSystem.closeForced(AppContext.getActivity(), false);
        }
    });
}

@Override
protected void onConfirm(){
    ApplicationSystem.closeForced(AppContext.getActivity(), false);
}

}