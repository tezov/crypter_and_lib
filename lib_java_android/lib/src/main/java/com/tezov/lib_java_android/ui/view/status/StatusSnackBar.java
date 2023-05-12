/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.view.status;

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
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.tezov.lib_java_android.R;
import com.tezov.lib_java_android.application.AppContext;
import com.tezov.lib_java.application.AppUIDGenerator;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java_android.wrapperAnonymous.ViewOnClickListenerW;
import com.tezov.lib_java.util.UtilsAlpha;
import com.tezov.lib_java_android.util.UtilsTextView;
import com.tezov.lib_java_android.util.UtilsView;

public class StatusSnackBar extends BaseTransientBottomBar<StatusSnackBar>{
private final static float ALPHA_BORDER_COLOR = 0.80f;
private final static float ALPHA_BACKGROUND_COLOR = 0.25f;

private StatusSnackBar(ViewGroup parent, SnackContent content){
    super(parent, content.getView(), content);
    content.attach(this);
    view.setId(AppUIDGenerator.nextInt());
    view.setBackgroundColor(ContextCompat.getColor(view.getContext(), android.R.color.transparent));
    view.setPadding(0, 0, 0, 0);
    ViewGroup.LayoutParams params = view.getLayoutParams();
    if(params instanceof FrameLayout.LayoutParams){
        FrameLayout.LayoutParams viewParams = (FrameLayout.LayoutParams)params;
        viewParams.gravity = Gravity.BOTTOM;
        viewParams.width = MATCH_PARENT;
        viewParams.height = AppContext.getResources().resolveDimension(R.attr.actionBarSize).intValue();
        view.setLayoutParams(viewParams);
    } else if(params instanceof androidx.constraintlayout.widget.ConstraintLayout.LayoutParams){
        androidx.constraintlayout.widget.ConstraintLayout.LayoutParams viewParams = (ConstraintLayout.LayoutParams)params;
        ConstraintSet set = new ConstraintSet();
        set.constrainWidth(view.getId(), MATCH_PARENT);
        set.constrainHeight(view.getId(), AppContext.getResources().resolveDimension(R.attr.actionBarSize).intValue());
        set.connect(view.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);
        set.applyToLayoutParams(view.getId(), viewParams);
    } else {
DebugException.start().unknown("type", params.getClass()).end();
    }
    setAnimationMode(ANIMATION_MODE_SLIDE);
}

public static class Builder{
    private int layout = R.layout.status_snackbar_default;
    private long duration = StatusParam.DELAY_INFO_SHORT_ms;
    private StatusParam.Color.Is color = null;
    private String message = null;
    private Drawable imgAction = null;
    private String lblAction = null;
    private ViewOnClickListenerW listenerAction = null;
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
    public Builder setActionLabel(String label){
        this.lblAction = label;
        return this;
    }
    public Builder setActionLabel(int resourceId){
        this.lblAction = AppContext.getResources().getString(resourceId);
        return this;
    }
    public Builder setActionImage(int drawableId){
        this.imgAction = AppContext.getResources().getDrawable(drawableId);
        return this;
    }
    public Builder setActionImage(Drawable img){
        this.imgAction = img;
        return this;
    }
    public Builder setActionListener(ViewOnClickListenerW listener){
        this.listenerAction = listener;
        return this;
    }
    public Builder setAction(String label, Drawable img, ViewOnClickListenerW listener){
        this.lblAction = label;
        this.imgAction = img;
        this.listenerAction = listener;
        return this;
    }
    public StatusSnackBar build(boolean lookForCoordinatorLayout){
        return build(AppContext.getActivity().getViewRoot(), lookForCoordinatorLayout);
    }
    public StatusSnackBar build(ViewGroup root, boolean lookForCoordinatorLayout){
        if(lookForCoordinatorLayout){
            root = UtilsView.findCoordinatorRoot(root);
        }
        SnackContent content = new SnackContent(root.getContext(), layout);
        if(message != null){
            content.setMessage(message);
        }
        if(imgAction != null){
            content.setActionImage(imgAction);
        }
        if(color != null){
            content.setColor(color);
        }
        if(lblAction != null){
            content.setActionLabel(lblAction);
        }
        if(listenerAction != null){
            content.setActionOnClickListener(listenerAction);
        }
        return new StatusSnackBar(root, content).setDuration((int)duration);
    }

}

private static class SnackContent implements com.google.android.material.snackbar.ContentViewCallback{
    View view;
    StatusSnackBar statusSnackBar = null;
    TextView lblMessage;
    FrameLayout imgAction;
    TextView lblAction;
    SnackContent(@NonNull android.content.Context context, int layout){
        view = LayoutInflater.from(context).inflate(layout, null, false);
        this.lblMessage = view.findViewById(R.id.lbl_message);
        this.imgAction = view.findViewById(R.id.img_action);
        this.lblAction = view.findViewById(R.id.lbl_action);
        FrameLayout imgCancel = view.findViewById(R.id.img_cancel);
        imgCancel.setOnClickListener(new ViewOnClickListenerW(){
            @Override
            public void onClicked(View v){
                statusSnackBar.dismiss();
            }
        });
    }
    void attach(StatusSnackBar statusSnackBar){
        this.statusSnackBar = statusSnackBar;
    }
    SnackContent setMessage(String s){
        UtilsTextView.setAndTruncate(lblMessage, s, 2);
        return this;
    }
    SnackContent setActionImage(Drawable d){
        imgAction.setBackground(d);
        return this;
    }
    SnackContent setActionLabel(String s){
        lblAction.setText(s);
        return this;
    }
    SnackContent setColor(StatusParam.Color.Is color){
        LayerDrawable drawable = (LayerDrawable)view.getBackground();
        int backgroundColor = UtilsAlpha.color(color.background(), ALPHA_BACKGROUND_COLOR);
        DrawableCompat.wrap(drawable.findDrawableByLayerId(R.id.shp_solid_color)).setTint(backgroundColor);
        int borderColor = UtilsAlpha.color(color.border(), ALPHA_BORDER_COLOR);
        DrawableCompat.wrap(drawable.findDrawableByLayerId(R.id.shp_stroke_color)).setTint(borderColor);

        this.lblMessage.setTextColor(color.title());
        this.lblAction.setTextColor(color.background());
        Drawable imgAction = this.imgAction.getBackground();
        if(imgAction != null){
            DrawableCompat.wrap(imgAction).setTint(backgroundColor);
        }
        return this;
    }
    SnackContent setActionOnClickListener(ViewOnClickListenerW listener){
        if(listener == null){
            imgAction.setOnClickListener(null);
        } else {
            imgAction.setOnClickListener(new ViewOnClickListenerW(){
                @Override
                public void onClicked(View v){
                    listener.onClicked(v);
                    statusSnackBar.dismiss();
                }
            });
        }
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

}

}

