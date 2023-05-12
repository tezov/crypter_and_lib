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

import com.tezov.lib_java.toolbox.Reflection;
import com.tezov.lib_java.type.collection.ListOrObject;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.widget.TextViewCompat;

import com.tezov.lib_java.toolbox.Compare;
import com.tezov.lib_java_android.wrapperAnonymous.EditTextOnClickIconListener;

public class EditTextWithIcon extends com.tezov.lib_java_android.ui.component.plain.EditText{
public EditTextWithIcon(Context context){
    super(context);
    init(context, null, NO_ID);
}
public EditTextWithIcon(Context context, AttributeSet attrs){
    super(context, attrs);
    init(context, attrs, NO_ID);
}
public EditTextWithIcon(Context context, AttributeSet attrs, int defStyleAttr){
    super(context, attrs, defStyleAttr);
    init(context, attrs, defStyleAttr);
}

private void init(Context context, AttributeSet attrs, int defStyleAttr){

}

@Override
protected Class<?> getRootClass(Class<?> type){
    type = super.getRootClass(type);
    if(type == null){
        if(Reflection.isInstanceOf(type, EditTextOnClickIconListener.class)){
            return EditTextOnClickIconListener.class;
        }
    }
    return type;
}

public void addOnClickIconListener(EditTextOnClickIconListener l){
    addListener(l);
    l.attach(this);
    if(onTouchListener == null){
        setTouchable(isClickable());
    }
}
public void removeOnClickIconListener(EditTextOnClickIconListener l){
    removeListener(l);
}
public boolean isClickableIcon(){
    if(!isTouchable()){
        return false;
    }
    ListOrObject<EditTextOnClickIconListener> l = getListener(EditTextOnClickIconListener.class);
    if(l != null){
        for(EditTextOnClickIconListener listener: l){
            if(listener.isEnabled()){
                return true;
            }
        }
    }
    return false;
}
public void setClickableIcon(boolean flag){
    ListOrObject<EditTextOnClickIconListener> l = getListener(EditTextOnClickIconListener.class);
    if(l != null){
       for(EditTextOnClickIconListener listener: l){
            listener.setEnabled(flag);
        }
    }
}

@Override
protected void onTouchEventIntercepted(boolean flag){
    super.onTouchEventIntercepted(flag);
    setClickableIcon(!flag);
}
@Override
protected boolean onTouched(View view, MotionEvent motionEvent){
    boolean returnValue = super.onTouched(view, motionEvent);
    ListOrObject<EditTextOnClickIconListener> l = getListener(EditTextOnClickIconListener.class);
    if(l != null){
        for(EditTextOnClickIconListener listener: l){
            returnValue |= listener.onTouch(view, motionEvent);
        }
    }
    return returnValue;
}
private Drawable[] replaceCompoundDrawable(IconPosition position, Drawable drawable){
    Drawable[] drawables = TextViewCompat.getCompoundDrawablesRelative(this);
    Drawable oldDrawable = drawables[position.value];
    if(Compare.equals(oldDrawable, drawable)){
        return null;
    } else {
        drawables[position.value] = drawable;
        return drawables;
    }
}
public void setCompoundDrawable(IconPosition position, Drawable drawable){
    Drawable[] drawables = replaceCompoundDrawable(position, drawable);
    if(drawables != null){
        setCompoundDrawables(drawables);
    }
}

public enum IconPosition{
    START(0), TOP(1), END(2), BOTTOM(3);
    int value;
    IconPosition(int value){
        this.value = value;
    }
    public int getValue(){
        return value;
    }
    public static IconPosition find(int ordinalFromXML){
        for(IconPosition p: values()){
            if(p.value == ordinalFromXML){
                return p;
            }
        }
        return null;
    }
}

}
