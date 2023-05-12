/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.wrapperAnonymous;

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

import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;

import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java_android.ui.component.plain.EditTextWithIcon;

public abstract class EditTextOnClickIconListener extends ViewOnTouchListenerW{
private final EditTextWithIcon.IconPosition position;
private EditTextWithIcon editText;
public EditTextOnClickIconListener(EditTextWithIcon.IconPosition position){
    this.position = position;
}
public void attach(EditTextWithIcon editText){
    this.editText = editText;
}
public EditTextWithIcon getEditText(){
    return editText;
}
@Override
final public boolean onTouched(View v, MotionEvent event){
    if(event.getAction() == MotionEvent.ACTION_DOWN){
        boolean intercept = false;
        Drawable drawableListened = editText.getCompoundDrawables()[position.getValue()];
        if(drawableListened == null){
            return false;
        }
        if(position == EditTextWithIcon.IconPosition.END){
            if(event.getX() >= (editText.getWidth() - editText.getPaddingEnd() - drawableListened.getBounds().width())){
                intercept = true;
            }
        } else if(position == EditTextWithIcon.IconPosition.START){
            if(event.getX() <= (drawableListened.getBounds().width() + editText.getPaddingStart())){
                intercept = true;
            }
        } else {
DebugException.start().log("position unknown " + position.name()).end();
        }
        if(intercept){
            onDown();
            return true;
        } else {
            return false;
        }
    } else if(event.getAction() == MotionEvent.ACTION_UP){
        boolean intercept = false;
        Drawable drawableListened = editText.getCompoundDrawables()[position.getValue()];
        if(drawableListened == null){
            return false;
        }
        if(position == EditTextWithIcon.IconPosition.END){
            if(event.getX() >= (editText.getWidth() - editText.getPaddingEnd() - drawableListened.getBounds().width())){
                intercept = true;
            }
        } else if(position == EditTextWithIcon.IconPosition.START){
            if(event.getX() <= (drawableListened.getBounds().width() + editText.getPaddingStart())){
                intercept = true;
            }
        } else {
DebugException.start().log("position unknown " + position.name()).end();
        }
        if(intercept){
            onUp();
            return true;
        } else {
            return false;
        }
    }
    return false;
}
public void onDown(){}
public void onUp(){}

}
