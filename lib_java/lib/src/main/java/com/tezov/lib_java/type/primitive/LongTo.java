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
import com.tezov.lib_java.util.UtilsBytes;

public class LongTo{
public static int BYTES = Long.SIZE / ByteTo.SIZE;

private LongTo(){
}

public static byte[] Bytes(long l){
    byte[] result = UtilsBytes.obtain(BYTES);
    for(int i = (BYTES - 1); i >= 0; i--){
        result[i] = (byte)(l & 0xFF);
        l >>= ByteTo.SIZE;
    }
    return result;
}

public static String StringHex(Long l){
    if(l == null){
        return null;
    }
    return BytesTo.StringHex(Bytes(l));
}

public void toDebugLogHex(Long l){
DebugLog.start().send("0X" + StringHex(l)).end();
}

}
