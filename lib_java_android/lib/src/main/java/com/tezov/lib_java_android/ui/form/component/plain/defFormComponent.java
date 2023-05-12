/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.form.component.plain;

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
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.tezov.lib_java_android.R;
import com.tezov.lib_java.definition.defEntry;
import com.tezov.lib_java.toolbox.Compare;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java_android.ui.misc.AttributeReader;

public interface defFormComponent<T> extends defEntry<T>{
int ENTRY_INPUT = 0; //value from enum attrs_lib_global_reference previous and next
int ENTRY_OUTPUT = 1;

int[] ATTR_INDEX = R.styleable.defFormComponent_lib;

static void linkPickers(defFormComponent output, defFormComponent input){
    if(output != null){
        output.setNext(input);
    } else if(input != null){
        input.setPrevious(null);
    }
}

int getId();

defEntry getEntry();

AttributeReader getAttribute(Class type);

void setAttribute(Class type, AttributeReader attributes);

default AttributeReader takeAttribute(Class type){
    AttributeReader attributes = getAttribute(type);
    setAttribute(type, null);
    return attributes;
}

default void parseAttribute(Context context, Class type, AttributeSet attrs){
    if(type == defFormComponent.class){
        AttributeReader attributes = new AttributeReader().parse(context, ATTR_INDEX, attrs);
        setAttribute(defFormComponent.class, attributes);
    }
}

default void linkPickers(AttributeReader attributes, View formContainer){
    if(attributes == null){
        return;
    }
    Integer previousFormID = null;
    if(Compare.isTrue(attributes.isReference(R.styleable.defFormComponent_lib_previous))){
        previousFormID = attributes.getReference(R.styleable.defFormComponent_lib_previous);
    } else {
        setComponentType(attributes.asInteger(R.styleable.defFormComponent_lib_previous));
    }
    Integer nextFormID = null;
    if(Compare.isTrue(attributes.isReference(R.styleable.defFormComponent_lib_next))){
        nextFormID = attributes.getReference(R.styleable.defFormComponent_lib_next);
    } else {
        setComponentType(attributes.asInteger(R.styleable.defFormComponent_lib_next));
    }
    defFormComponent formPicker;
    if(previousFormID != null){
        formPicker = formContainer.findViewById(previousFormID);
        if(formPicker != null){
            setPreviousForm(formPicker);
        } else {
DebugException.start().explode("ID not null but picker not found").end();
        }

    }
    if(nextFormID != null){
        formPicker = formContainer.findViewById(nextFormID);
        if(formPicker != null){
            setNextForm(formPicker);
        } else {
DebugException.start().explode("ID not null but picker not found").end();
        }

    }
}

default void build(View formContainer){
    AttributeReader attributes = takeAttribute(defFormComponent.class);
    linkPickers(attributes, formContainer);
}

Integer getComponentType();

void setComponentType(Integer type);

default boolean isInput(){
    return Compare.equals(getComponentType(), ENTRY_INPUT);
}

default boolean isOutput(){
    return Compare.equals(getComponentType(), ENTRY_OUTPUT);
}

boolean hasPrevious();

<F extends defFormComponent<?>> F getPrevious();

default void setPrevious(defFormComponent<?> formPicker){
    if(formPicker == null){
        setPreviousForm(null);
    } else {
        setPreviousForm(formPicker);
        formPicker.setNextForm(this);
    }
}

void setPreviousForm(defFormComponent<?> form);

boolean hasNext();

<F extends defFormComponent<?>> F getNext();

default void setNext(defFormComponent<?> formPicker){
    if(formPicker == null){
        setNextForm(null);
    } else {
        setNextForm(formPicker);
        formPicker.setPreviousForm(this);
    }
}

void setNextForm(defFormComponent<?> form);

boolean requestFocus();

default defFormComponent<?> next(){
    defFormComponent next = getNext();
    if(next == null){
        return null;
    }
    next.requestFocus();
    return next;
}

default defFormComponent<?> previous(){
    defFormComponent previous = getPrevious();
    if(previous == null){
        return null;
    }
    previous.requestFocus();
    return previous;
}

void showError();

}
