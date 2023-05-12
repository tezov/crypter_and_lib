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

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatButton;

import com.tezov.lib_java_android.toolbox.PostToHandler;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.runnable.RunnableW;

public class Button extends AppCompatButton{

public Button(android.content.Context context){
    super(context);
    init(context, null, NO_ID);
}
public Button(android.content.Context context, AttributeSet attrs){
    super(context, attrs);
    init(context, attrs, NO_ID);
}
public Button(android.content.Context context, AttributeSet attrs, int defStyleAttr){
    super(context, attrs, defStyleAttr);
    init(context, attrs, defStyleAttr);
}

private void init(android.content.Context context, AttributeSet attrs, int defStyleAttr){
DebugTrack.start().create(this).end();
    setFocusable(false);
    setFocusableInTouchMode(false);
}

@Override
public void setEnabled(boolean flag){
    super.setEnabled(flag);
    setClickable(flag);
}
@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

private static class IconDetail{
    String text = null;
    ColorStateList backgroundColor = null;
    Drawable icon = null;

    void update(AppCompatButton button){
        PostToHandler.of(button, new RunnableW(){
            @Override
            public void runSafe(){
                if(text != null){
                    button.setText(text);
                }
                if(backgroundColor != null){
                    button.setBackgroundTintList(backgroundColor);
                }
                if(icon != null){
                    button.setCompoundDrawables(icon, null, null, null);
                }
            }
        });
    }

}

}
