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
import com.tezov.lib_java_android.ui.form.adapter.FormAdapter;
import com.tezov.lib_java_android.ui.form.adapter.FormManager;

import org.apache.commons.lang3.math.NumberUtils;

public class FormIntegerAdapter extends FormAdapter<Integer>{
public FormIntegerAdapter(FormManager.Target.Is target){
    super(target);
}

@Override
public Class<Integer> getEntryType(){
    return Integer.class;
}

@Override
public String valueToString(){
    if(isNULL()){
        return null;
    }
    return Integer.toString(getValue());
}

@Override
public <T> boolean isAcceptedType(Class<T> type){
    return (type == String.class) || (type == Integer.class);
}

@Override
public <T> boolean setValue(Class<T> type, T object){
    if(type == String.class){
        if(object != null){
            if(NumberUtils.isCreatable((String)object)){
                setValue(Integer.valueOf((String)object));
                return true;
            } else {
DebugException.start().logHidden("set String is not a numeric value").end();
                return false;
            }

        } else {
            setValue(null);
            return true;
        }
    } else if(type == Integer.class){
        setValue((Integer)object);
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
    } else if(type == Integer.class){
        return (T)getValue();
    } else {

DebugException.start().unknown("type", type.getName()).end();

        return null;
    }
}

}
