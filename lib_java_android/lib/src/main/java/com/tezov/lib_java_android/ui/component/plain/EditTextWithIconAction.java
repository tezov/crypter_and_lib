/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.component.plain;

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
import com.tezov.lib_java_android.application.AppContext;
import com.tezov.lib_java_android.wrapperAnonymous.EditTextOnTextChangedListenerW;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.method.PasswordTransformationMethod;
import android.text.method.TransformationMethod;
import android.util.AttributeSet;

import androidx.core.graphics.drawable.DrawableCompat;

import com.tezov.lib_java_android.R;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java_android.wrapperAnonymous.EditTextOnClickIconListener;
import com.tezov.lib_java_android.ui.misc.AttributeReader;

public class EditTextWithIconAction extends EditTextWithIcon{
final static private int[] ATTR_INDEX = R.styleable.EditTextIconIconAction_lib;

private IconAction mode = null;
private Drawable icon = null;
private IconPosition iconPosition = null;
private Integer iconColor = null;
private EditTextOnClickIconListener onClickIconListener = null;
private EditTextOnTextChangedListenerW textChangeListener = null;

public EditTextWithIconAction(Context context){
    super(context);
    init(context, null, NO_ID);
}
public EditTextWithIconAction(Context context, AttributeSet attrs){
    super(context, attrs);
    init(context, attrs, NO_ID);
}
public EditTextWithIconAction(Context context, AttributeSet attrs, int defStyleAttr){
    super(context, attrs, defStyleAttr);
    init(context, attrs, defStyleAttr);
}
private EditTextWithIconAction me(){
    return this;
}

private void init(Context context, AttributeSet attrs, int defStyleAttr){
    if(attrs == null){
        return;
    }
    IconAction mode = IconAction.CLEAR;
    Drawable icon = null;
    IconPosition iconPosition = null;
    Integer iconColor = null;
    AttributeReader attributes = new AttributeReader().setAttrsIndex(ATTR_INDEX).parse(context, attrs);
    Integer modeInt = attributes.asInteger(R.styleable.EditTextIconIconAction_lib_icon_mode);
    if(modeInt != null){
        mode = IconAction.find(modeInt);
    }
    if(mode != IconAction.NONE){
        icon = attributes.asDrawable(R.styleable.EditTextIconIconAction_lib_icon);
        iconColor = attributes.asColorARGB(R.styleable.EditTextIconIconAction_lib_icon_tint);
        Integer positionInt = attributes.asInteger(R.styleable.EditTextIconIconAction_lib_icon_position);
        if(positionInt != null){
            iconPosition = IconPosition.find(positionInt);
        }
    }
    setIconMode(mode, icon, iconPosition, iconColor);
}

public IconAction getMode(){
    return mode;
}
public void setIconMode(IconAction mode){
    setIconMode(mode, null, iconPosition, iconColor);
}
public void setIconMode(IconAction mode, Drawable icon){
    setIconMode(mode, icon, iconPosition, iconColor);
}
public void setIconMode(IconAction mode, Drawable icon, IconPosition iconPosition, Integer iconColor){
    if((mode == null) || mode == IconAction.NONE){
        if(this.mode == IconAction.PASSWORD){
            replaceTransformationMethod(null);
        }
        if(this.iconPosition != null){
            setCompoundDrawable(this.iconPosition, null);
            this.icon = null;
            this.iconPosition = null;
        }
        if(this.onClickIconListener != null){
            addOnClickIconListener(null);
            this.onClickIconListener = null;
        }
        if(this.textChangeListener != null){
            removeTextChangedListener(textChangeListener);
            this.textChangeListener = null;
        }
        this.mode = IconAction.NONE;
    }
    else {
        this.mode = mode;
        if(iconPosition == null){
            iconPosition = IconPosition.END;
        }
        this.iconPosition = iconPosition;
        if(icon == null){
            if(mode == IconAction.CLEAR){
                icon = AppContext.getResources().getDrawable(R.drawable.ic_cancel_24dp);
            } else if(mode == IconAction.ACTION){
                icon = AppContext.getResources().getDrawable(R.drawable.ic_click_24dp);
            } else if(mode == IconAction.PASSWORD){
                icon = AppContext.getResources().getDrawable(R.drawable.ic_eye_dp24);
            } else {
DebugException.start().unknown("mode", mode.name()).end();
            }
        }
        this.icon = icon;
        if(iconColor == null){
            iconColor = AppContext.getResources().resolveColorARGB(R.attr.colorHint);
            if(iconColor == null){
                iconColor = AppContext.getResources().getColorARGB(R.color.LightGray);
            }
        }
        this.iconColor = iconColor;
        DrawableCompat.wrap(icon).setTint(iconColor);
        if(onClickIconListener == null){
            onClickIconListener = new EditTextOnClickIconListener(iconPosition){
                @Override
                public void onUp(){
                    me().onClickActionUp(me().mode);
                }
                @Override
                public void onDown(){
                    me().onClickActionDown(me().mode);
                }
            };
            addOnClickIconListener(onClickIconListener);
        }
        if(textChangeListener == null){
            textChangeListener = new EditTextOnTextChangedListenerW(){
                @Override
                public void onTextChanged(EditText editText, Editable es){
                    me().onTextChanged(me().mode, es);
                }
            };
            addTextChangedListener(textChangeListener);
        }
        if(mode == IconAction.PASSWORD){
            replaceTransformationMethod(PasswordTransformationMethod.getInstance());
        } else {
            replaceTransformationMethod(null);
        }
        setCompoundDrawable(iconPosition, icon);
    }
}

public void replaceTransformationMethod(TransformationMethod method){
    int position = getSelectionStart();
    setTransformationMethod(method);
    setSelection(position);
}

protected void onClickActionUp(IconAction mode){
    if(mode == IconAction.CLEAR){
        setText(null);
    } else if(mode == IconAction.ACTION){

    } else if(mode == IconAction.PASSWORD){
        replaceTransformationMethod(PasswordTransformationMethod.getInstance());
    } else {
DebugException.start().unknown("mode", mode.name()).end();
    }
}
protected void onClickActionDown(IconAction mode){
    if((mode == IconAction.CLEAR) || (mode == IconAction.ACTION)){

    } else if(mode == IconAction.PASSWORD){
        replaceTransformationMethod(null);
    } else {
DebugException.start().unknown("mode", mode.name()).end();
    }

}
protected void onTextChanged(IconAction mode, Editable s){
    if((mode == IconAction.CLEAR) || (mode == IconAction.PASSWORD)){
        if(s.length() > 0){
            setCompoundDrawable(iconPosition, icon);
        } else {
            setCompoundDrawable(iconPosition, null);
        }
    } else if(mode == IconAction.ACTION){

    } else {
DebugException.start().unknown("mode", mode.name()).end();
    }
}
public enum IconAction{
    NONE(0), CLEAR(1), ACTION(2), PASSWORD(3);
    int value;
    IconAction(int value){
        this.value = value;
    }
    public static IconAction find(int ordinalFromXML){
        for(IconAction p: values()){
            if(p.value == ordinalFromXML){
                return p;
            }
        }
        return null;
    }
}

}

