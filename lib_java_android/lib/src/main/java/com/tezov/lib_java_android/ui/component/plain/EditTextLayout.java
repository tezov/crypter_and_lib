/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.component.plain;

import com.tezov.lib_java.debug.DebugLog;
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

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputLayout;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java_android.util.UtilsView;

import static com.tezov.lib_java_android.util.UtilsView.Direction.DOWN;

public class EditTextLayout extends TextInputLayout{
private Drawable[] memDrawables = null;

public EditTextLayout(Context context){
    super(context);
    init(context, null, NO_ID);
}
public EditTextLayout(Context context, AttributeSet attrs){
    super(context, attrs);
    init(context, attrs, NO_ID);
}
public EditTextLayout(Context context, AttributeSet attrs, int defStyleAttr){
    super(context, attrs, defStyleAttr);
    init(context, attrs, defStyleAttr);
}

private void init(Context context, AttributeSet attrs, int defStyleAttr){
DebugTrack.start().create(this).end();
}

public EditText getEditText(){
    return UtilsView.findFirst(EditText.class, this, DOWN);
}


@Override
public void setError(@Nullable CharSequence errorText){
    EditText editText = getEditText();
    if(editText != null){
        if(errorText != null){
            if(memDrawables == null){
                memDrawables = editText.getCompoundDrawables();
            }
            super.setError(errorText);
        } else {
            super.setError(null);
            if(memDrawables != null){
                editText.setCompoundDrawables(memDrawables);
                memDrawables = null;
            }
        }
    } else {
        super.setError(errorText);
    }
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

}
