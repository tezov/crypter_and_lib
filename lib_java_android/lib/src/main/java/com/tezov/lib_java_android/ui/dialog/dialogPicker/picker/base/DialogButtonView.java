/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.dialog.dialogPicker.picker.base;

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
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.widget.ImageViewCompat;

import com.tezov.lib_java_android.R;
import com.tezov.lib_java_android.application.AppContext;
import com.tezov.lib_java_android.application.AppDisplay;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.collection.ListEntry;
import com.tezov.lib_java.type.collection.ListOrObject;
import com.tezov.lib_java_android.type.primaire.Color;
import com.tezov.lib_java.type.primaire.Entry;
import com.tezov.lib_java_android.ui.misc.AttributeReader;


public class DialogButtonView extends FrameLayout{
final static protected int[] ATTR_INDEX = R.styleable.DialogPickerButton_lib;
final private static int RES_LAYOUT_BUTTON = R.layout.dialog_picker_button;
final private static int RES_DRAWABLE_BACKGROUND_BUTTON = R.drawable.dialog_button_background_icon_48dp;
final private static int RES_BTN_TOP_LEFT = R.id.top_left;
final private static int RES_BTN_TOP_RIGHT = R.id.top_right;
final private static int RES_BTN_BOTTOM_LEFT = R.id.bottom_left;
final private static int RES_BTN_BOTTOM_RIGHT = R.id.bottom_right;
final private static int RES_BTN_OPTION = R.id.option;
final private static int RES_ID_BACKGROUND = R.id.background;
final private static int RES_ID_JOINT = R.id.joint;
final private static int RES_ID_ICON = R.id.icon;
final private static int RES_ID_ICON_FOREGROUND = R.id.icon_foreground;
final private static float FOREGROUND_ALPHA = 0.90f;
final private static int FOREGROUND_COLOR = R.color.Black;
private final ListEntry<Integer, DialogButtonAction.Is> actions = new ListEntry<Integer, DialogButtonAction.Is>(ListOrObject::new);
private ButtonPosition position = null;

public DialogButtonView(Context context){
    super(context);
    init(context, null, 0, 0);
}

public DialogButtonView(Context context, @Nullable AttributeSet attrs){
    super(context, attrs);
    init(context, attrs, 0, 0);
}

public DialogButtonView(Context context, @Nullable AttributeSet attrs, int defStyleAttr){
    super(context, attrs, defStyleAttr);
    init(context, attrs, defStyleAttr, 0);
}

public DialogButtonView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes){
    super(context, attrs, defStyleAttr, defStyleRes);
    init(context, attrs, defStyleAttr, defStyleRes);
}

private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes){
DebugTrack.start().create(this).end();
    if(attrs == null){
        return;
    }
    AttributeReader attributes = new AttributeReader().parse(context, ATTR_INDEX, attrs);
    Integer positionOrdinal = attributes.asInteger(R.styleable.DialogPickerButton_lib_position);
    if(positionOrdinal != null){
        position = ButtonPosition.find(positionOrdinal);
    } else {
DebugException.start().explode("Attribute app:position is wrong or absent for DialogButtonCorner in XML layout").end();
    }

    addButtonContainer(getContext());
}

protected void addButtonContainer(Context context){
}

protected ViewGroup getContainer(){
    return this;
}

protected int getButtonLayoutId(){
    return RES_LAYOUT_BUTTON;
}

protected View getButton(int id){
    for(int count = getContainer().getChildCount(), i = 0; i < count; i++){
        View view = getContainer().getChildAt(i);
        if(view.getId() == id){
            return view;
        }
    }
    return null;
}

protected View obtainButton(int id){
    View view = findViewById(id);
    if(view != null){
        return view;
    }
    view = inflate(getContext(), getButtonLayoutId(), null);
    view.setId(id);
    setPosition(view, position);
    setIconForeground(view, FOREGROUND_ALPHA, Color.fromResource(FOREGROUND_COLOR));
    getContainer().addView(view);
    return view;
}

protected boolean onParamsCreated(ViewGroup.LayoutParams params){
    FrameLayout.LayoutParams layoutParam = (LayoutParams)params;
    if(position == ButtonPosition.TOP_LEFT){
        layoutParam.gravity = Gravity.START | Gravity.TOP;
        return true;
    }
    if(position == ButtonPosition.BOTTOM_LEFT){
        layoutParam.gravity = Gravity.START | Gravity.BOTTOM;
        return true;
    }
    if(position == ButtonPosition.TOP_RIGHT){
        layoutParam.gravity = Gravity.END | Gravity.TOP;
        return true;
    }
    if(position == ButtonPosition.BOTTOM_RIGHT){
        layoutParam.gravity = Gravity.END | Gravity.BOTTOM;
        return true;
    }
    if(position == ButtonPosition.OPTION){
        layoutParam.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        return true;
    }
    return false;
}

@Override
public void setLayoutParams(ViewGroup.LayoutParams params){
    if(getLayoutParams() == null){
        onParamsCreated(params);
    }
    super.setLayoutParams(params);
}

public ButtonPosition getPosition(){
    return position;
}

protected boolean setPosition(View buttonView, ButtonPosition position){
    ImageView background = buttonView.findViewById(RES_ID_BACKGROUND);
    ImageView joint = buttonView.findViewById(RES_ID_JOINT);
    ImageView icon = buttonView.findViewById(RES_ID_ICON);
    ImageView icon_foreground = buttonView.findViewById(RES_ID_ICON_FOREGROUND);

    if(position == ButtonPosition.TOP_LEFT){
        background.setTranslationX(AppDisplay.convertDpToPx(0));
        joint.setImageResource(R.drawable.dialog_button_join_right_48dp);
        icon.setTranslationX(AppDisplay.convertDpToPx(0));
        icon_foreground.setTranslationX(AppDisplay.convertDpToPx(0));
        return true;
    }
    if(position == ButtonPosition.BOTTOM_LEFT){
        background.setTranslationX(AppDisplay.convertDpToPx(0));
        joint.setImageResource(R.drawable.dialog_button_join_right_48dp);
        icon.setTranslationX(AppDisplay.convertDpToPx(0));
        icon_foreground.setTranslationX(AppDisplay.convertDpToPx(0));
        return true;
    }
    if(position == ButtonPosition.TOP_RIGHT){
        background.setTranslationX(AppDisplay.convertDpToPx(6));
        joint.setImageResource(R.drawable.dialog_button_join_left_48dp);
        icon.setTranslationX(AppDisplay.convertDpToPx(6));
        icon_foreground.setTranslationX(AppDisplay.convertDpToPx(6));
        return true;
    }
    if(position == ButtonPosition.BOTTOM_RIGHT){
        background.setTranslationX(AppDisplay.convertDpToPx(6));
        joint.setImageResource(R.drawable.dialog_button_join_left_48dp);
        icon.setTranslationX(AppDisplay.convertDpToPx(6));
        icon_foreground.setTranslationX(AppDisplay.convertDpToPx(6));
        return true;
    }
    if(position == ButtonPosition.OPTION){
        ((ViewGroup)buttonView).removeView(joint);
        return true;
    }
    return false;
}

public void setIconForeground(int id, float alpha, int resourceForegroundColor){
    setIconForeground(getButton(id), alpha, Color.fromResource(resourceForegroundColor));
}

public void setIconForeground(View buttonView, float alpha, Color foregroundColor){
    ImageView iconForeground = buttonView.findViewById(RES_ID_ICON_FOREGROUND);
    ImageViewCompat.setImageTintList(iconForeground, ColorStateList.valueOf(foregroundColor.getARGB()));
    iconForeground.setAlpha(alpha);
}

private void setForegroundVisible(View buttonView, boolean flag){
    ImageView foreground = buttonView.findViewById(RES_ID_ICON_FOREGROUND);
    if(flag){
        foreground.setVisibility(View.VISIBLE);
    } else {
        foreground.setVisibility(View.INVISIBLE);
    }
}

public DialogButtonAction.Is getAction(int id){
    for(Entry<Integer, DialogButtonAction.Is> action: actions){
        if(action.key == id){
            return action.value;
        }
    }
    return null;
}

public void newButton(int id, @Nullable OnClickListener l){
    View buttonView = obtainButton(id);
    buttonView.setOnClickListener(l);
    showIcon(buttonView.getId(), false);
    setForegroundVisible(buttonView, false);
    buttonView.setClickable(false);
}

public void setAction(int id, DialogButtonAction.Is action){
    actions.put(id, action);
    View buttonView = getButton(id);
    ImageView icon = buttonView.findViewById(RES_ID_ICON);
    icon.setImageResource(action.getImageId());
    ImageViewCompat.setImageTintList(icon, ColorStateList.valueOf(Color.fromResource(action.getImageColorId()).getARGB()));
    Integer backgroundColorId = action.getBackgroundColorId();
    if(backgroundColorId == null){
        return;
    }
    Drawable drawable = AppContext.getResources().getDrawable(RES_DRAWABLE_BACKGROUND_BUTTON);
    DrawableCompat.wrap(drawable).setTint(AppContext.getResources().getColorARGB(backgroundColorId));
    icon.setBackground(drawable);
}

public void showIcon(boolean flag){
    for(int count = getContainer().getChildCount(), i = 0; i < count; i++){
        View view = getContainer().getChildAt(i);
        showIcon(view.getId(), flag);
    }
}

public void showIcon(int id, boolean flag){
    View buttonView = getButton(id);
    if(position == ButtonPosition.OPTION){
        if(flag){
            buttonView.setVisibility(View.VISIBLE);
        } else {
            buttonView.setVisibility(View.GONE);
        }
    } else {
        ImageView icon = buttonView.findViewById(RES_ID_ICON);
        if(flag){
            icon.setVisibility(View.VISIBLE);
        } else {
            icon.setVisibility(View.INVISIBLE);
        }
    }
}

public void setClickable(int id, boolean clickable){
    View buttonView = getButton(id);
    setClickable(buttonView, clickable);
}

private void setClickable(View buttonView, boolean clickable){
    setForegroundVisible(buttonView, !clickable);
    buttonView.setClickable(clickable);
}

public boolean isClickable(int id){
    return isClickable(getButton(id));
}

private boolean isClickable(View buttonView){
    return buttonView.isClickable();
}

public boolean callOnClick(int id){
    return callOnClick(getButton(id));
}

public boolean callOnClick(View buttonView){
    return buttonView.callOnClick();
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}


public enum ButtonPosition{
    TOP_LEFT(0, RES_BTN_TOP_LEFT), TOP_RIGHT(1, RES_BTN_TOP_RIGHT), BOTTOM_LEFT(2, RES_BTN_BOTTOM_LEFT), BOTTOM_RIGHT(3, RES_BTN_BOTTOM_RIGHT), OPTION(4, RES_BTN_OPTION);
    private final int positionFromStyleableEnum;
    private final int resourceID;
    ButtonPosition(int positionFromStyleableEnum, int resourceID){
        this.positionFromStyleableEnum = positionFromStyleableEnum;
        this.resourceID = resourceID;
    }
    public static ButtonPosition find(int ordinalFromXML){
        ButtonPosition[] values = values();
        for(ButtonPosition value: values){
            if(value.positionFromStyleableEnum == ordinalFromXML){
                return value;
            }
        }

DebugException.start().explode("position class to xml enum " + ordinalFromXML + " not found").end();

        return null;
    }
    public DialogButtonView findView(View view){
        return view.findViewById(resourceID);
    }
}

}
