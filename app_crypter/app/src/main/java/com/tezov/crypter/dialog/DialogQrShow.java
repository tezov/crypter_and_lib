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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.tezov.crypter.R;
import com.tezov.crypter.data_transformation.DataQr;
import com.tezov.lib_java_android.application.AppContext;
import com.tezov.lib_java.async.Handler;
import com.tezov.lib_java_android.wrapperAnonymous.ViewOnClickListenerW;
import com.tezov.lib_java.type.runnable.RunnableW;
import com.tezov.lib_java_android.ui.component.plain.ButtonIconMaterial;
import com.tezov.lib_java_android.ui.dialog.DialogNavigable;

public class DialogQrShow extends DialogNavigable{
private ImageView imageView = null;
private TextView lblPart = null;
private boolean run = true;
private RunnableW runnable = null;

@Override
protected State newState(){
    return new State();
}

@Override
public State getState(){
    return (State)super.getState();
}

@Override
public State obtainState(){
    return super.obtainState();
}
@Override
public Param getParam(){
    return super.getParam();
}
@Nullable
@Override
public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
    super.onCreateView(inflater, container, savedInstanceState);
    View view = inflater.inflate(R.layout.dialog_qr_show, container, false);
    Param param = getParam();
    TextView lblTitle = view.findViewById(R.id.lbl_title);
    lblTitle.setText(param.getTitle());
    TextView lblType = view.findViewById(R.id.lbl_type);
    lblType.setText(param.getType());
    if(param.getSignatureKey() != null){
        TextView signature = view.findViewById(R.id.lbl_signature_key);
        signature.setText(param.getSignatureKey());
        signature.setVisibility(View.VISIBLE);
    }
    if(param.getSignatureApp() != null){
        TextView signature = view.findViewById(R.id.lbl_signature_app);
        signature.setText(param.getSignatureApp());
        signature.setVisibility(View.VISIBLE);
    }
    lblPart = view.findViewById(R.id.lbl_part);
    ButtonIconMaterial btnAction = view.findViewById(R.id.btn_close);
    btnAction.setOnClickListener(new ViewOnClickListenerW(){
        @Override
        public void onClicked(View v){
            close();
        }
    });
    return view;
}

@Override
public void onPrepare(boolean hasBeenReconstructed){
    super.onPrepare(hasBeenReconstructed);
    View view = getView();
    imageView = view.findViewById(R.id.img_qr);
    computeParts();
}
private void stop(){
    run = false;
}
private void run(){
    run = true;
    showNextData();
}

private void incrementDataIndex(){
    incrementDataIndex(getParam().datas);
}
private void incrementDataIndex(DataQr data){
    data.next();
    imageView.setImageBitmap(data.getBitmap());
    lblPart.setText(String.valueOf(data.getIndex() + 1));
}
private void computeParts(){
    DataQr data = getParam().datas;
    TextView lblParts = getView().findViewById(R.id.lbl_parts);
    lblParts.setText(String.valueOf(data.getSize()));
    incrementDataIndex(data);
}
private void showNextData(){
    if(run){
        if(runnable == null){
            runnable = new RunnableW(){
                @Override
                public void runSafe(){
                    if(run){
                        incrementDataIndex();
                    }
                    showNextData();
                }
            };
        }
        if(!Handler.MAIN().hasRunnable(runnable)){
            Handler.MAIN().post(this, DataQr.DELAY_NEXT_ms, runnable);
        }
    } else {
        if(runnable != null){
            Handler.MAIN().cancel(this, runnable);
            runnable = null;
        }
    }
}

@Override
public void onPause(){
    super.onPause();
    stop();
}
@Override
public void onOpen(boolean hasBeenReconstructed, boolean hasBeenRestarted){
    super.onOpen(hasBeenReconstructed, hasBeenRestarted);
    run();
}

public static class State extends DialogNavigable.State{
    @Override
    protected Param newParam(){
        return new Param();
    }
    @Override
    public Param obtainParam(){
        return (Param)super.obtainParam();
    }

}

public static class Param extends DialogNavigable.Param{
    public DataQr datas = null;
    public String type = null;
    public String signatureKey = null;
    public String signatureApp = null;

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

    public DataQr getData(){
        return datas;
    }
    public Param setData(DataQr datas){
        this.datas = datas;
        return this;
    }

    public String getType(){
        return type;
    }
    public Param setType(int resourceId){
        return setType(AppContext.getResources().getString(resourceId));
    }
    public Param setType(String type){
        this.type = type;
        return this;
    }

}

}