/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.dialog.modal;

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
import android.widget.Button;

import androidx.annotation.Nullable;

import com.tezov.lib_java_android.R;
import com.tezov.lib_java_android.application.AppContext;
import com.tezov.lib_java_android.wrapperAnonymous.ViewOnClickListenerW;
import com.tezov.lib_java.type.defEnum.Event;
import com.tezov.lib_java_android.ui.component.plain.CheckBox;
import com.tezov.lib_java_android.ui.component.plain.WebViewHtmlResource;
import com.tezov.lib_java_android.ui.dialog.DialogBase;
import com.tezov.lib_java_android.ui.dialog.DialogNavigable;

public class DialogModalWebview extends DialogNavigable{

private WebViewHtmlResource webView = null;
private Button btnConfirm = null;
private Button btnCancel = null;
private CheckBox checkbox = null;

@Override
protected State newState(){
    return new State();
}
@Override
public State getState(){
    return (State)super.getState();
}
@Override
public Param obtainParam(){
    return super.obtainParam();
}
@Override
public Param getParam(){
    return super.getParam();
}

@Nullable
@Override
public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
    super.onCreateView(inflater, container, savedInstanceState);
    Param param = getParam();
    View view = inflater.inflate(R.layout.dialog_modal_webview, container, false);
    btnConfirm = view.findViewById(R.id.btn_confirm);
    if(param.getConfirmButtonText() != null){
        btnConfirm.setText(param.getConfirmButtonText());
        btnConfirm.setOnClickListener(new ViewOnClickListenerW(){
            @Override
            public void onClicked(View v){
                setButtonsEnable(false);
                onConfirm();
            }
        });
    }
    else{
        btnConfirm.setVisibility(View.GONE);
    }
    btnCancel = view.findViewById(R.id.btn_cancel);
    if(param.getCancelButtonText() != null){
        btnCancel.setText(param.getCancelButtonText());
        btnCancel.setOnClickListener(new ViewOnClickListenerW(){
            @Override
            public void onClicked(View v){
                setButtonsEnable(false);
                onCancel();
            }
        });
    }
    else{
        btnCancel.setVisibility(View.GONE);
    }
    checkbox = view.findViewById(R.id.checkbox);
    if(param.checkBoxText != null){
        checkbox.setText(param.checkBoxText);
        checkbox.setChecked(param.checkBoxInitialValue);
        checkbox.setOnClickListener(new ViewOnClickListenerW(){
            @Override
            public void onClicked(View v){
                getParam().checkBoxInitialValue = checkbox.isChecked();
                onCheckBoxChange(checkbox.isChecked());
            }
        });
    } else {
        checkbox.setVisibility(View.GONE);
    }
    webView = view.findViewById(R.id.wbv_content);
    webView.setRawFileId(param.rawFileId).loadData();
    return view;
}

public Button getBtnConfirm(){
    return btnConfirm;
}
public Button getBtnCancel(){
    return btnCancel;
}
public CheckBox getCheckbox(){
    return checkbox;
}

public void setButtonsEnable(boolean flag){
    btnConfirm.setEnabled(flag);
    btnCancel.setEnabled(flag);
    checkbox.setEnabled(flag);
}

protected void onConfirm(){
    postConfirm();
    close();
}
protected void postConfirm(){
    post(Event.ON_CONFIRM, null);
}
protected void postException(Throwable e){
    post(Event.ON_EXCEPTION, e);
}

protected void onCancel(){
    postCancel();
    close();
}
protected void postCancel(){
    post(Event.ON_CANCEL, null);
}

protected void onCheckBoxChange(boolean flag){
    post(Event.ON_CHANGE, flag);
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
    @Override
    protected Method newMethod(){
        return new Method();
    }
    @Override
    public Method obtainMethod(){
        return (Method)super.obtainMethod();
    }

}

public static class Param extends DialogNavigable.Param{
    public String checkBoxText = null;
    public boolean checkBoxInitialValue = false;
    public Integer rawFileId = null;

    public String getCheckBoxText(){
        return checkBoxText;
    }
    public Param setCheckBoxText(int resourceId){
        return setCheckBoxText(AppContext.getResources().getString(resourceId));
    }
    public Param setCheckBoxText(String text){
        this.checkBoxText = text;
        return this;
    }

    public boolean isChecked(){
        return checkBoxInitialValue;
    }
    public Param setChecked(boolean flag){
        this.checkBoxInitialValue = flag;
        return this;
    }

    public Integer getRawFileId(){
        return rawFileId;
    }
    public Param setRawFileId(Integer rawFileId){
        this.rawFileId = rawFileId;
        return this;
    }

    @Override
    public Method obtainMethod(){
        return super.obtainMethod();
    }

}

public static class Method extends DialogBase.Method{
    @Override
    public DialogModalWebview getOwner(){
        return (DialogModalWebview)super.getOwner();
    }

}

}

