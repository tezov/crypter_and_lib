/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.cipher.dataAdapter.object;

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.util.UtilsString;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;

import com.tezov.lib_java.cipher.dataAdapter.string.DataStringAdapter;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.type.primitive.BytesTo;
import com.tezov.lib_java.type.primitive.string.StringHexTo;

import static com.tezov.lib_java.cipher.dataAdapter.string.DataStringAdapter.Format.HEX;

public abstract class DataObjectAdapter{
public static DataObjectEncoderAdapter forEncoder(){
    return forEncoder(HEX);
}
public static DataObjectEncoderAdapter forEncoder(DataStringAdapter.Format format){
    return new DataObjectEncoderAdapter(format);
}
public static DataObjectDecoderAdapter forDecoder(){
    return forDecoder(HEX);
}
public static DataObjectDecoderAdapter forDecoder(DataStringAdapter.Format format){
    return new DataObjectDecoderAdapter(format);
}
public static String objectToString(Object o, Class type){
    if(o == null){
        return null;
    }
    else if(CompareType.STRING.equal(type)){
        return (String)o;
    }
    else if(CompareType.LONG.equal(type)){
        return Long.toString((long)o);
    }
    else if(CompareType.INT.equal(type)){
        return Integer.toString((int)o);
    }
    else if(CompareType.BOOLEAN.equal(type)){
        return ((boolean)o) ? "1" : "0";
    }
    else if(CompareType.FLOAT.equal(type)){
        return Float.toString((float)o);
    }
    else if(CompareType.UID.equal(type)){
        return BytesTo.StringHex((byte[])o);
    }
    else {
        return BytesTo.StringHex((byte[])o);
    }
}
public static Object stringToObject(String s, Class type){
    if(s == null){
        return null;
    }
    else if(CompareType.STRING.equal(type)){
        return s;
    } else if(CompareType.LONG.equal(type)){
        return Long.valueOf(s);
    } else if(CompareType.INT.equal(type)){
        return Integer.valueOf(s);
    } else if(CompareType.BOOLEAN.equal(type)){
        return s.equals("1");
    } else if(CompareType.FLOAT.equal(type)){
        return Float.valueOf(s);
    } else if(CompareType.UID.equal(type)){
        return StringHexTo.Bytes(s);
    } else {
        return StringHexTo.Bytes(s);
    }
}


}
