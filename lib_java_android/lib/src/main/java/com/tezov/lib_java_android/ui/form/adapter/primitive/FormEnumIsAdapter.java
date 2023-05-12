/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.form.adapter.primitive;

import com.tezov.lib_java.debug.DebugLog;
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
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.defEnum.EnumBase;
import com.tezov.lib_java_android.ui.form.adapter.FormAdapter;
import com.tezov.lib_java_android.ui.form.adapter.FormManager;

public class FormEnumIsAdapter<IS extends EnumBase.Is> extends FormAdapter<IS>{
Class<IS> type;

public FormEnumIsAdapter(FormManager.Target.Is target, Class<IS> type){
    super(target);
    this.type = type;
}

@Override
public Class<IS> getEntryType(){
    return type;
}

@Override
public String valueToString(){
    IS is = getValue();
    if(is == null){
        return null;
    } else {
        return getValue().name();
    }
}

@Override
public <T> boolean isAcceptedType(Class<T> type){
    return (type == String.class) || (type == Integer.class) || (Reflection.isInstanceOf(type, this.type));
}

@Override
public <T> boolean setValue(Class<T> type, T object){
    if(type == String.class){
        IS is;
        if(object == null){
            is = null;
        } else {
            is = EnumBase.Is.findTypeOf(this.type, (String)object);
            if(is == null){
DebugException.start().explode(object + " not found inside " + DebugTrack.getFullSimpleName(this.type)).end();
            }
        }
        setValue(is);
        return true;
    } else if(type == Integer.class){
        IS is;
        if(object == null){
            is = null;
        } else {
            is = EnumBase.Is.findTypeOf(this.type, (Integer)object);
            if(is == null){
DebugException.start().explode(object + " not found inside " + DebugTrack.getFullSimpleName(this.type)).end();
            }
        }
        setValue(is);
        return true;
    } else if(Reflection.isInstanceOf(type, this.type)){
        setValue((IS)object);
        return true;
    } else {
DebugException.start().unknown("type", type.getName()).end();
        return false;
    }

}

@Override
public <T> T getValue(Class<T> type){
    if(type == String.class){
        IS is = getValue();
        if(is == null){
            return null;
        } else {
            return (T)getValue().name();
        }
    } else if(type == Integer.class){
        IS is = getValue();
        if(is == null){
            return null;
        } else {
            return (T)Integer.valueOf(getValue().ordinal());
        }
    } else if(Reflection.isInstanceOf(type, this.type)){
        return (T)getValue();
    } else {

DebugException.start().unknown("type", type.getName()).end();

        return null;
    }
}

}
