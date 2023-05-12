/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.view.status;

import com.tezov.lib_java.debug.DebugLog;
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
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import android.graphics.drawable.LayerDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.tezov.lib_java_android.R;
import com.tezov.lib_java.application.AppUIDGenerator;
import com.tezov.lib_java_android.application.AppContext;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.util.UtilsAlpha;
import com.tezov.lib_java_android.util.UtilsTextView;
import com.tezov.lib_java_android.util.UtilsView;

public class StatusToast extends BaseTransientBottomBar<StatusToast>{
private final static float ALPHA_BORDER_COLOR = 0.80f;
private final static float ALPHA_BACKGROUND_COLOR = 0.25f;

private StatusToast(ViewGroup parent, ToastContent content){
    super(parent, content.getView(), content);
    view.setId(AppUIDGenerator.nextInt());
    view.setBackgroundColor(ContextCompat.getColor(view.getContext(), android.R.color.transparent));
    view.setPadding(0, 0, 0, 0);
    ViewGroup.LayoutParams params = view.getLayoutParams();
    if(params instanceof FrameLayout.LayoutParams){
        FrameLayout.LayoutParams viewParams = (FrameLayout.LayoutParams)params;
        viewParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
        viewParams.width = viewParams.height = WRAP_CONTENT;
        view.setLayoutParams(viewParams);
    }
    else if(params instanceof ConstraintLayout.LayoutParams){
        ConstraintLayout.LayoutParams viewParams = (ConstraintLayout.LayoutParams)params;
        ConstraintSet set = new ConstraintSet();
        set.constrainWidth(view.getId(), WRAP_CONTENT);
        set.constrainHeight(view.getId(), WRAP_CONTENT);
        set.connect(view.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0);
        set.connect(view.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0);
        set.connect(view.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0);
        set.connect(view.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);
        set.applyToLayoutParams(view.getId(), viewParams);
    }
    else {
DebugException.start().unknown("type", params.getClass()).end();
    }
    setAnimationMode(ANIMATION_MODE_FADE);
}

public static class Builder{
    private int layout = R.layout.status_toast_default;
    private long duration = StatusParam.DELAY_INFO_SHORT_ms;
    private StatusParam.Color.Is color = null;
    private String message = null;
    public Builder setLayout(int layout){
        this.layout = layout;
        return this;
    }
    public Builder setDuration(long duration){
        this.duration = duration;
        return this;
    }
    public Builder setColor(StatusParam.Color.Is color){
        this.color = color;
        return this;
    }
    public Builder setMessage(String message){
        this.message = message;
        return this;
    }
    public StatusToast build(boolean attachToRoot){
        if(attachToRoot){
            return build(AppContext.getActivity().getViewRoot());
        } else {
            return build(UtilsView.findMasterRoot());
        }
    }
    public StatusToast build(ViewGroup root){
        ToastContent content = new ToastContent(root.getContext(), layout);
        if(message != null){
            content.setMessage(message);
        }
        if(color != null){
            content.setColor(color);
        }
        return new StatusToast(root, content).setDuration((int)duration);
    }

}

private static class ToastContent implements com.google.android.material.snackbar.ContentViewCallback{
    View view;
    TextView lblMessage;
    ToastContent(android.content.Context context, int layout){
DebugTrack.start().create(this).end();
        this.view = LayoutInflater.from(context).inflate(layout, null, false);
        this.lblMessage = view.findViewById(R.id.lbl_message);
    }
    ToastContent setMessage(String s){
        UtilsTextView.setAndTruncate(lblMessage, s, 2);
        return this;
    }
    ToastContent setColor(StatusParam.Color.Is color){
        LayerDrawable drawable = (LayerDrawable)view.getBackground();
        int backgroundColor = UtilsAlpha.color(color.background(), ALPHA_BACKGROUND_COLOR);
        DrawableCompat.wrap(drawable.findDrawableByLayerId(R.id.shp_solid_color)).setTint(backgroundColor);
        int borderColor = UtilsAlpha.color(color.border(), ALPHA_BORDER_COLOR);
        DrawableCompat.wrap(drawable.findDrawableByLayerId(R.id.shp_stroke_color)).setTint(borderColor);
        lblMessage.setTextColor(color.message());
        return this;
    }
    View getView(){
        return view;
    }
    @Override
    public void animateContentIn(int delay, int duration){
    }
    @Override
    public void animateContentOut(int delay, int duration){
    }
    @Override
    protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
        super.finalize();
    }

}

}

