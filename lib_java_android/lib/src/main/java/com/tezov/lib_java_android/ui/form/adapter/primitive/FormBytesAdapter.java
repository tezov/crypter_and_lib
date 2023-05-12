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
import static com.tezov.lib_java.type.primitive.string.StringHexTo.HEX_PREFIX;

import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.type.primaire.Point;
import com.tezov.lib_java.type.primaire.Position;
import com.tezov.lib_java.type.primitive.BytesTo;
import com.tezov.lib_java.type.primitive.FloatTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.primitive.LongTo;
import com.tezov.lib_java.type.primitive.string.StringHexTo;
import com.tezov.lib_java_android.ui.form.adapter.FormAdapter;
import com.tezov.lib_java_android.ui.form.adapter.FormManager;

public class FormBytesAdapter extends FormAdapter<byte[]>{
private boolean addPrefix = true;

public FormBytesAdapter(FormManager.Target.Is target){
    super(target);
}

public void setAddPrefix(boolean flag){
    this.addPrefix = flag;
}

@Override
public Class<byte[]> getEntryType(){
    return byte[].class;
}

@Override
public String valueToString(){
    if(isNULL()){
        return null;
    }
    return (addPrefix ? HEX_PREFIX : "") + BytesTo.StringHex(getValue());
}

@Override
public <T> boolean isAcceptedType(Class<T> type){
    return (type == String.class) || (type == Integer.class) || (type == Long.class) || (type == Float.class) || (type == Point.class) || (type == Position.class) || (type == byte.class) ||
           (type == byte[].class);
}

@Override
public <T> boolean setValue(Class<T> type, T object){
    if(type == String.class){
        if(object != null){
            byte[] b = StringHexTo.Bytes((String)object);
            if(b != null){
                setValue(b);
                return true;
            } else {
DebugException.start().logHidden("set String is not hexadecimal value").end();
                return false;
            }
        } else {
            setValue(null);
            return true;
        }
    } else if(type == Integer.class){
        setValue(IntTo.Bytes((Integer)object));
        return true;
    } else if(type == Long.class){
        setValue(LongTo.Bytes((Long)object));
        return true;
    } else if(type == Float.class){
        setValue(FloatTo.Bytes((Float)object));
        return true;
    } else if(type == Point.class){
        setValue(((Point)object).toBytes());
        return true;
    } else if(type == Position.class){
        setValue(((Position)object).toBytes());
        return true;
    } else if(type == byte.class){
        setValue(new byte[]{(Byte)object});
        return true;
    } else if(type == byte[].class){
        setValue((byte[])object);
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
        return (T)BytesTo.Int(getValue());
    } else if(type == Long.class){
        return (T)BytesTo.Long(getValue());
    } else if(type == Float.class){
        return (T)BytesTo.Float(getValue());
    } else if(type == Point.class){
        return (T)Point.fromBytes(getValue());
    } else if(type == Position.class){
        return (T)Position.fromBytes(getValue());
    } else if(type == byte.class){
        byte[] bytes = getValue();
        if((bytes == null) || (bytes.length < 1)){
            return null;
        } else {
            return (T)(Byte)bytes[0];
        }
    } else if(type == byte[].class){
        return (T)getValue();
    } else {

DebugException.start().unknown("type", type.getName()).end();

        return null;
    }
}

}
