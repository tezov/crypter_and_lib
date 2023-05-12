/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.type.primitive;

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
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.type.primitive.string.StringHexTo;
import com.tezov.lib_java.util.UtilsBytes;

public class IntTo{
public static int BYTES = Integer.SIZE / ByteTo.SIZE;
public static int MAX_DIGIT_POSITIVE = Integer.toString(Integer.MAX_VALUE).length();
public static int MAX_DIGIT_NEGATIVE = Integer.toString(Integer.MIN_VALUE).length() - 1;

private IntTo(){
}
public static byte[] Bytes(int i){
    byte[] result = UtilsBytes.obtain(BYTES);
    for(int j = (BYTES - 1); j >= 0; j--){
        result[j] = (byte)(i & 0xFF);
        i >>= ByteTo.SIZE;
    }
    return result;
}
public static String StringHex(Integer i){
    return StringHex(i, false);
}
public static String StringHex(Integer i, boolean addPrefix){
    if(i == null){
        return null;
    } else if(!addPrefix){
        return BytesTo.StringHex(Bytes(i));
    } else {
        return StringHexTo.HEX_PREFIX + BytesTo.StringHex(Bytes(i));
    }
}
public void toDebugLogHex(Integer i){
DebugLog.start().send(StringHex(i, true)).end();
}

}
