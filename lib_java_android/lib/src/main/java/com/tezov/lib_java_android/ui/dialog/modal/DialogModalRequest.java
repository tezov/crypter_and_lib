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
import static com.tezov.lib_java_android.application.AppResources.NULL_ID;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.tezov.lib_java_android.R;
import com.tezov.lib_java_android.application.AppContext;
import com.tezov.lib_java_android.toolbox.PostToHandler;
import com.tezov.lib_java_android.wrapperAnonymous.ViewOnClickListenerW;
import com.tezov.lib_java.type.defEnum.Event;
import com.tezov.lib_java.type.runnable.RunnableW;
import com.tezov.lib_java_android.ui.dialog.DialogBase;
import com.tezov.lib_java_android.ui.dialog.DialogNavigable;
import com.tezov.lib_java_android.ui.layout.ConstraintLayout;

import java9.util.function.BiConsumer;

public abstract class DialogModalRequest extends DialogNavigable{

private Button btnConfirm = null;
private Button btnCancel = null;

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

protected boolean enableScrollbar(){
    return false;
}

@Nullable
@Override
public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
    super.onCreateView(inflater, container, savedInstanceState);
    Param param = getParam();
    View view = inflater.inflate(enableScrollbar() ? R.layout.dialog_modal_request_scrollbar : R.layout.dialog_modal_request, container, false);
    TextView lblTitle = view.findViewById(R.id.lbl_title);
    lblTitle.setText(param.getTitle());
    lblTitle.setCompoundDrawablesWithIntrinsicBounds(param.titleIcon, null, null, null);
    btnConfirm = view.findViewById(R.id.btn_confirm);
    if(param.getConfirmButtonText() != null){
        btnConfirm.setText(param.getConfirmButtonText());
    } else {
        btnConfirm.setVisibility(View.GONE);
    }
    btnConfirm.setOnClickListener(new ViewOnClickListenerW(){
        @Override
        public void onClicked(View v){
            setButtonsEnable(false);
            onConfirm();
        }
    });
    btnCancel = view.findViewById(R.id.btn_cancel);
    if(param.getCancelButtonText() != null){
        btnCancel.setText(param.getCancelButtonText());
    } else {
        btnCancel.setVisibility(View.GONE);
    }
    btnCancel.setOnClickListener(new ViewOnClickListenerW(){
        @Override
        public void onClicked(View v){
            setButtonsEnable(false);
            onCancel();
        }
    });
    ConstraintLayout frame = view.findViewById(R.id.container_frame);
    mergeFrameView(LayoutInflater.from(getContext()), frame, savedInstanceState);
    return view;
}

protected ConstraintLayout getFrame(){
    return getView().findViewById(R.id.container_frame);
}

public Button getBtnConfirm(){
    return btnConfirm;
}
public Button getBtnCancel(){
    return btnCancel;
}

public void setButtonsVisibility(int visibility){
    btnConfirm.setVisibility(visibility);
    btnCancel.setVisibility(visibility);
}
public void setButtonsEnable(boolean flag){
    btnConfirm.setEnabled(flag);
    btnCancel.setEnabled(flag);
}

protected int getFrameLayoutId(){
    return NULL_ID;
}
protected void mergeFrameView(LayoutInflater inflater, ConstraintLayout frame, Bundle savedInstanceState){
    View view = inflater.inflate(getFrameLayoutId(), frame, true);
    onFrameMerged(view, savedInstanceState);
}
protected void onFrameMerged(View view, Bundle savedInstanceState){

}

protected void replaceFrameView(int layoutId){
    replaceFrameView(layoutId, null, null);
}
protected void replaceFrameView(int layoutId, BiConsumer<View, Void> onReplaced){
    replaceFrameView(layoutId, onReplaced, null);
}
protected <T> void replaceFrameView(int layoutId, BiConsumer<View, T> onReplaced, T t){
    PostToHandler.of(getView(), new RunnableW(){
        @Override
        public void runSafe(){
            ConstraintLayout frame = getFrame();
            frame.removeAllViewsInLayout();
            View view = LayoutInflater.from(getContext()).inflate(layoutId, frame, true);
            if(onReplaced != null){
                onReplaced.accept(view, t);
            }
        }
    });
}

protected Object getConfirmData(){
    return null;
}

protected void onConfirm(){
    postConfirm();
    close();
}
protected void postConfirm(Object object){
    post(Event.ON_CONFIRM, object);
}
protected void postConfirm(){
    postConfirm(getConfirmData());
}
protected void postException(){
    post(Event.ON_EXCEPTION, getConfirmData());
}

protected void onCancel(){
    postCancel();
    close();
}
protected void postCancel(){
    post(Event.ON_CANCEL, null);
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
    public Drawable titleIcon = null;
    public Drawable getTitleIcon(){
        return titleIcon;
    }
    public Param setTitleIcon(int resourceId){
        return setTitleIcon(AppContext.getResources().getDrawable(resourceId));
    }
    public Param setTitleIcon(Drawable titleIcon){
        this.titleIcon = titleIcon;
        return this;
    }

    @Override
    public Method obtainMethod(){
        return super.obtainMethod();
    }

}
public static class Method extends DialogBase.Method{
    @Override
    public DialogModalRequest getOwner(){
        return (DialogModalRequest)super.getOwner();
    }

}

}

