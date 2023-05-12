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

public class DoubleTo{
public static int BYTES = Double.SIZE / ByteTo.SIZE;

private DoubleTo(){
}

public static byte[] Bytes(double f){
    long intBits = Double.doubleToLongBits(f);
    return new byte[]{(byte)(intBits >> 56), (byte)(intBits >> 48), (byte)(intBits >> 40), (byte)(intBits >> 32), (byte)(intBits >> 24), (byte)(intBits >> 16), (byte)(intBits >> 8), (byte)(intBits)};
}

public static String StringHex(Double f){
    if(f == null){
        return null;
    }
    return BytesTo.StringHex(Bytes(f));
}

public void toDebugLogHex(Double f){
DebugLog.start().send("0X" + StringHex(f)).end();
}

}
