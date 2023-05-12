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
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.DrawableCompat;

import com.tezov.lib_java_android.R;
import com.tezov.lib_java_android.application.AppContext;
import com.tezov.lib_java_android.toolbox.PostToHandler;
import com.tezov.lib_java.type.runnable.RunnableW;
import com.tezov.lib_java_android.ui.dialog.DialogBase;
import com.tezov.lib_java_android.ui.dialog.DialogNavigable;
import com.tezov.lib_java_android.ui.view.status.StatusParam;
import com.tezov.lib_java.util.UtilsAlpha;
import com.tezov.lib_java_android.util.UtilsTextView;

public class DialogModalProgressIndeterminate extends DialogNavigable{
private final static float ALPHA_BORDER_COLOR = 0.35f;

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
public Param obtainParam(){
    return super.obtainParam();
}
@Override
public Param getParam(){
    return super.getParam();
}

@Override
public Method obtainMethod(){
    return super.obtainMethod();
}
@Override
public Method getMethod(){
    return super.getMethod();
}
@Nullable
@Override
public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
    super.onCreateView(inflater, container, savedInstanceState);
    return inflater.inflate(R.layout.dialog_modal_progress_indeterminate, container, false);
}
@Override
public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
    super.onViewCreated(view, savedInstanceState);
    updateTitle();
    updateHelper();
    updateFrameColor();
}

private void updateTitle(){
    Param param = getParam();
    String title = param.getTitle();
    StatusParam.Color.Is color = param.getColor();
    View view = getView();
    TextView lbl = view.findViewById(R.id.lbl_title);
    if(title != null){
        lbl.setTextColor(color.title());
        lbl.setVisibility(VISIBLE);
        UtilsTextView.setAndTruncate(lbl, title, 1);
    } else {
        lbl.setVisibility(GONE);
    }
}
private void updateHelper(){
    Param param = getParam();
    String helper = param.getHelper();
    StatusParam.Color.Is color = param.getColor();
    View view = getView();
    View divider = view.findViewById(R.id.sep_header);
    TextView lbl = view.findViewById(R.id.lbl_helper);
    if(helper != null){
        lbl.setTextColor(color.helper());
        lbl.setVisibility(VISIBLE);
        divider.setVisibility(VISIBLE);
        UtilsTextView.setAndTruncate(lbl, helper, 1);
    } else {
        lbl.setVisibility(GONE);
        divider.setVisibility(GONE);
    }
}
private void updateFrameColor(){
    Param param = getParam();
    StatusParam.Color.Is color = param.getColor();
    View view = getView();
    LayerDrawable bg = (LayerDrawable)view.getBackground();
    Drawable frame = bg.findDrawableByLayerId(R.id.shp_stroke_color);
    DrawableCompat.wrap(frame).setTint(UtilsAlpha.color(color.border(), ALPHA_BORDER_COLOR));
}

public void updateHelper(int textResourceId){
    updateHelper(AppContext.getResources().getString(textResourceId));
}
public void updateHelper(String text){
    View view = getView();
    if(view != null){
        TextView lbl = getView().findViewById(R.id.lbl_helper);
        PostToHandler.of(lbl, new RunnableW(){
            @Override
            public void runSafe(){
                UtilsTextView.setAndTruncate(lbl, text, 1);
            }
        });
    } else {
        obtainParam().setHelper(text);
    }
}

@NonNull
@Override
public Dialog onCreateDialog(Bundle savedInstanceState){
    setCancelable(false);
    return super.onCreateDialog(savedInstanceState);
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
    public String helper = "...";
    public StatusParam.Color.Is color = StatusParam.Color.NEUTRAL;
    @Override
    public Method obtainMethod(){
        return super.obtainMethod();
    }
    public Param setTitle(int resourceId){
        this.title = AppContext.getResources().getString(resourceId);
        return this;
    }
    public String getHelper(){
        return helper;
    }
    public Param setHelper(String value){
        this.helper = value;
        return this;
    }
    public Param setHelper(int resourceId){
        this.helper = AppContext.getResources().getString(resourceId);
        return this;
    }
    public StatusParam.Color.Is getColor(){
        return color;
    }
    public Param setColor(StatusParam.Color.Is color){
        this.color = color;
        return this;
    }
}

public static class Method extends DialogBase.Method{
    @Override
    public DialogModalProgressIndeterminate getOwner(){
        return (DialogModalProgressIndeterminate)super.getOwner();
    }
    public void updateHelper(String text){
        if(hasOwner()){
            getOwner().updateHelper(text);
        }
    }
    public void updateHelper(int resourceTextId){
        if(hasOwner()){
            getOwner().updateHelper(resourceTextId);
        }
    }

}

}

