/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.crypter.misc;

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
import com.tezov.crypter.data.item.ItemKey;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java_android.ui.form.adapter.FormAdapter;

public class FormItemKeyAdapter extends FormAdapter<ItemKey>{

public FormItemKeyAdapter(){
    super(null);
}

@Override
public Class<ItemKey> getEntryType(){
    return ItemKey.class;
}

protected String getField(){
    return getValue().getAlias();
}

protected void setField(String value){
    getValue().setAlias(value);
}

@Override
public String valueToString(){
    if(isNULL()){
        return null;
    } else {
        return getField();
    }
}

@Override
public <T> boolean isAcceptedType(Class<T> type){
    return (type == String.class) || (type == ItemKey.class);
}

@Override
public <T> boolean setValue(Class<T> type, T object){
    if(type == String.class){
        if(isNotNULL()){
            setField((String)object);
            setValue(getValue());
            return true;
        }
        if(object == null){
            setValue(null);
            return true;
        } else {
DebugException.start().explode("set on null object illegal").end();
            return false;
        }
    }
    if(type == ItemKey.class){
        if(object == null){
            setValue(null);
        } else {
            setValue(((ItemKey)object).copy());
        }
        return true;
    }

DebugException.start().unknown("type", type.getName()).end();
    return false;
}

@Override
public <T> T getValue(Class<T> type){
    if(type == String.class){
        return (T)valueToString();
    }
    if(type == ItemKey.class){
        return (T)getValue();
    }

DebugException.start().unknown("type", type.getName()).end();


    return null;
}


}
