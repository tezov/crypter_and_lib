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
import com.tezov.lib_java.file.File;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java_android.ui.form.adapter.FormAdapter;
import com.tezov.lib_java_android.ui.form.adapter.FormManager;

public class FormFileAdapter extends FormAdapter<File>{
public FormFileAdapter(FormManager.Target.Is target){
    super(target);
}

@Override
public Class<File> getEntryType(){
    return File.class;
}

@Override
public String valueToString(){
    File file = getValue();
    if(file != null){
        return getValue().toLinkString();
    } else {
        return null;
    }
}

@Override
public <T> boolean isAcceptedType(Class<T> type){
    return (type == String.class) || (type == File.class);
}

@Override
public <T> boolean setValue(Class<T> type, T object){
    if(type == String.class){
        if(object == null){
            setValue(null);
        } else {
            setValue(File.from((String)object));
        }
        return true;
    } else if(type == File.class){
        if(object == null){
            setValue(null);
        } else {
            setValue((File)object);
        }
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
    } else if(type == File.class){
        return (T)getValue();
    } else {

DebugException.start().unknown("type", type.getName()).end();

        return null;
    }
}

}
