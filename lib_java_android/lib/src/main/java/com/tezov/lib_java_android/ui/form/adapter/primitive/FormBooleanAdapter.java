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
import com.tezov.lib_java.wrapperAnonymous.FunctionW;
import com.tezov.lib_java_android.ui.form.adapter.FormAdapter;
import com.tezov.lib_java_android.ui.form.adapter.FormManager;

public class FormBooleanAdapter extends FormAdapter<Boolean>{
FunctionW<Integer, Boolean> intToBool;
FunctionW<Boolean, Integer> boolToInt;

public FormBooleanAdapter(FormManager.Target.Is target){
    this(target, null, null);
}

public FormBooleanAdapter(FormManager.Target.Is target, FunctionW<Boolean, Integer> boolToInt, FunctionW<Integer, Boolean> intToBool){
    super(target);
    if(boolToInt != null){
        this.boolToInt = boolToInt;
    } else {
        this.boolToInt = new FunctionW<Boolean, Integer>(){
            @Override
            public Integer apply(Boolean b){
                if(b == null){
                    return 0;
                }
                if(!b){
                    return 1;
                } else {
                    return 2;
                }
            }
        };
    }
    if(intToBool != null){
        this.intToBool = intToBool;
    } else {
        this.intToBool = new FunctionW<Integer, Boolean>(){
            @Override
            public Boolean apply(Integer i){
                if(i == 0){
                    return null;
                } else {
                    return i % 2 == 0;
                }
            }
        };
    }
}

@Override
public Class<Boolean> getEntryType(){
    return Boolean.class;
}

@Override
public String valueToString(){
    if(isNULL()){
        return null;
    } else {
        return Boolean.toString(getValue());
    }
}

@Override
public <T> boolean isAcceptedType(Class<T> type){
    return (type == String.class) || (type == Boolean.class) || (type == Integer.class);
}

@Override
public <T> boolean setValue(Class<T> type, T object){
    if(object == null){
        setValue(null);
        return true;
    } else if(type == String.class){
        setValue(Boolean.valueOf((String)object));
        return true;
    } else if(type == Boolean.class){
        setValue((Boolean)object);
        return true;
    } else if(type == Integer.class){
        setValue(intToBool.apply((Integer)object));
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
    } else if(type == Boolean.class){
        return (T)getValue();
    } else if(type == Integer.class){
        return (T)boolToInt.apply(getValue());
    } else {

DebugException.start().unknown("type", type.getName()).end();

        return null;
    }
}

}
