/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.form.adapter.primitive;

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
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java_android.type.primaire.Color;
import com.tezov.lib_java_android.ui.form.adapter.FormAdapter;
import com.tezov.lib_java_android.ui.form.adapter.FormManager;

import org.apache.commons.lang3.math.NumberUtils;

public class FormColorAdapter extends FormAdapter<Color>{
public FormColorAdapter(FormManager.Target.Is target){
    super(target);
}

@Override
public Class<Color> getEntryType(){
    return Color.class;
}

@Override
public String valueToString(){
    if(isNULL()){
        return null;
    }
    return getValue().toString();
}

@Override
public <T> boolean isAcceptedType(Class<T> type){
    return (type == String.class) || (type == Color.class) || (type == Integer.class);
}

@Override
public <T> boolean setValue(Class<T> type, T object){
    if(type == String.class){
        if(object != null){
            if(NumberUtils.isCreatable((String)object)){
                setValue(Color.fromARGB(Integer.parseInt((String)object)));
                return true;
            } else {
DebugException.start().logHidden("set String is not a numeric value").end();
                return false;
            }

        } else {
            setValue(null);
            return true;
        }
    } else if(type == Color.class){
        setValue((Color)object);
        return true;
    } else if(type == Integer.class){
        setValue(Color.fromARGB((Integer)object));
        return true;
    } else {
DebugException.start().unknown("type", type.getName()).end();
        return false;
    }

}

@Override
public <T> T getValue(Class<T> type){
    if(type == String.class){
        return (T)valueToString();
    } else if(type == Color.class){
        return (T)getValue();
    } else if(type == Integer.class){
        if(isNULL()){
            return null;
        } else {
            return (T)Integer.valueOf(getValue().getARGB());
        }
    } else {

DebugException.start().unknown("type", type.getName()).end();

        return null;
    }
}

}
