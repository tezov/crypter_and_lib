/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.form.component.dialog;

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
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.tezov.lib_java.async.notifier.observer.event.ObserverEvent;
import com.tezov.lib_java.async.notifier.observer.value.ObserverValue;
import com.tezov.lib_java.async.notifier.task.TaskValue;
import com.tezov.lib_java_android.wrapperAnonymous.ViewOnClickListenerW;
import com.tezov.lib_java.type.defEnum.Event;
import com.tezov.lib_java_android.ui.component.plain.EditTextLayout;
import com.tezov.lib_java_android.ui.dialog.DialogNavigable;
import com.tezov.lib_java_android.ui.form.component.plain.FormEditText;
import com.tezov.lib_java_android.ui.misc.AttributeReader;

public class FormEditTextDialog extends FormEditText implements defFormComponentDialog<String>{
private State state = null;
private AttributeReader attributes = null;
private boolean hasFocus = false;

public FormEditTextDialog(Context context){
    super(context);
    init(context, null, 0);
}

public FormEditTextDialog(Context context, AttributeSet attrs){
    super(context, attrs);
    init(context, attrs, 0);
}

public FormEditTextDialog(Context context, AttributeSet attrs, int defStyleAttr){
    super(context, attrs, defStyleAttr);
    init(context, attrs, defStyleAttr);
}

protected FormEditTextDialog me(){
    return this;
}

@Override
final public State getState(){
    return state;
}

@Override
final public void setState(State state){
    this.state = state;
}

@Override
public AttributeReader getAttribute(Class type){
    if(type == defFormComponentDialog.class){
        return attributes;
    }
    return super.getAttribute(type);
}

@Override
public void setAttribute(Class type, AttributeReader attributes){
    if(type == defFormComponentDialog.class){
        this.attributes = attributes;
    } else {
        super.setAttribute(type, attributes);
    }
}

private void init(Context context, AttributeSet attrs, int defStyleAttr){
    addOnClickListener(new ViewOnClickListenerW(){
        @Override
        public void onClicked(View v){
            TaskValue.Observable observable = show();
            if(observable != null){
                EditTextLayout textInputLayout = getTextLayout();
                if(textInputLayout != null){
                    hasFocus = true;
                    textInputLayout.refreshDrawableState();
                    observable.observe(new ObserverValue<DialogNavigable>(this){
                        @Override
                        public void onComplete(DialogNavigable dialog){
                            dialog.observe(new ObserverEvent<Event.Is, Object>(this, Event.ON_CLOSE){
                                @Override
                                public void onComplete(Event.Is is, Object object){
                                    hasFocus = false;
                                    textInputLayout.refreshDrawableState();
                                }
                            });
                        }
                    });
                }
            }
        }
    });
    setFocusable(false);
    setClickable(false);
    if(attrs == null){
        return;
    }
    defFormComponentDialog.super.parseAttribute(context, defFormComponentDialog.class, attrs);
}

@Override
public boolean hasFocus(){
    return hasFocus;
}

@Override
protected void observeFocusLost(){
    // inhibit super
}

@Override
public void onDetachedFromWindow(){
    super.onDetachedFromWindow();
    defFormComponentDialog.super.onDetachedFromWindow();
}

}
