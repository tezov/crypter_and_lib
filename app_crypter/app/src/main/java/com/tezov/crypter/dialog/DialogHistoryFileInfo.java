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
import com.tezov.lib_java_android.application.AppContext;

import androidx.fragment.app.Fragment;
import static android.view.View.GONE;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.tezov.crypter.R;
import com.tezov.crypter.data.misc.ClockFormat;
import com.tezov.lib_java_android.ui.dialog.DialogBase;
import com.tezov.lib_java_android.ui.dialog.DialogNavigable;
import com.tezov.lib_java_android.ui.dialog.modal.DialogModalRequest;

public class DialogHistoryFileInfo extends DialogNavigable{

@Override
protected int getWidth(){
    return ViewGroup.LayoutParams.WRAP_CONTENT;
}
@Override
public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
    super.onCreateView(inflater, container, savedInstanceState);
    View view = inflater.inflate(R.layout.dialog_history_info, container, false);
    Param param = getParam();
    TextView createdDateView = view.findViewById(R.id.lbl_created_date);
    createdDateView.setText(ClockFormat.longToDateTime_FULL(param.timestamp));
    TextView signatureKeyView = view.findViewById(R.id.lbl_signature_key);
    if(param.signatureKey != null){
        signatureKeyView.setText(param.signatureKey);
    } else {
        signatureKeyView.setVisibility(GONE);
    }
    TextView signatureAppView = view.findViewById(R.id.lbl_signature_app);
    if(param.signatureApp != null){
        signatureAppView.setText(param.signatureApp);
    } else {
        signatureAppView.setVisibility(GONE);
    }
    TextView operation = view.findViewById(R.id.lbl_operation);
    operation.setText(param.operation);
    TextView result = view.findViewById(R.id.lbl_result);
    result.setText(param.result);
    return view;
}

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
    public Long timestamp = null;
    public String signatureKey = null;
    public String signatureApp = null;
    public String operation = null;
    public String result = null;

    public Long getTimestamp(){
        return timestamp;
    }
    public Param setTimestamp(Long timestamp){
        this.timestamp = timestamp;
        return this;
    }

    public String getSignatureKey(){
        return signatureKey;
    }
    public Param setSignatureKey(String signature){
        this.signatureKey = signature;
        return this;
    }

    public String getSignatureApp(){
        return signatureApp;
    }
    public Param setSignatureApp(String signature){
        this.signatureApp = signature;
        return this;
    }

    public String getOperation(){
        return operation;
    }
    public DialogBase.Param setOperation(int resourceId){
        return setOperation(AppContext.getResources().getString(resourceId));
    }
    public DialogBase.Param setOperation(String operation){
        this.operation = operation;
        return this;
    }

    public String getResult(){
        return result;
    }
    public DialogBase.Param setResult(int resourceId){
        return setResult(AppContext.getResources().getString(resourceId));
    }
    public DialogBase.Param setResult(String result){
        this.result = result;
        return this;
    }

}


}
