/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.util;

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.util.UtilsString;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java.cipher.SecureProvider;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.type.unit.UnitByte;

import java.security.SecureRandom;

public class UtilsBytes{
private static final float MAX_BYTES_ARRAY_SIZE = UnitByte.o.convert(20f, UnitByte.Mo);

private UtilsBytes(){
}

public static byte[] reverse(byte[] bytes){
    int i = 0;
    int j = bytes.length - 1;
    byte tmp;
    while (j > i) {
        tmp = bytes[j];
        bytes[j] = bytes[i];
        bytes[i] = tmp;
        j--;
        i++;
    }
    return bytes;
}

public static byte[] removeLeadingZero(byte[] bytes){
    int countZero = 0;
    for(byte b: bytes){
        if(b != 0x00){
            break;
        }
        countZero++;
    }
    int length = bytes.length - countZero;
    if(length == 0){
        return new byte[]{(byte)0x00};
    }
    byte[] out = obtain(length);
    System.arraycopy(bytes, countZero, out, 0, length);
    return out;
}
public static void copyAndRepeat(byte[] src, byte[] dest){
    copyAndRepeat(src, dest, 0);
}
public static void copyAndRepeat(byte[] src, byte[] dest, int destOffset){
    for(int end = dest.length, i = destOffset; i < end; i++){
        dest[i] = src[i % src.length];
    }
}
public static void copy(byte[] src, byte[] dest){
    copy(src, 0, dest, 0, src.length);
}
public static void copy(byte[] src, byte[] dest, int destOffset){
    copy(src, 0, dest, destOffset, src.length);
}
public static void copy(byte[] src, int srcOffset, byte[] dest, int destOffset, int length){
    System.arraycopy(src, srcOffset, dest, destOffset, length);
}

public static byte[] random(int length){
    try{
        SecureRandom sr = SecureProvider.randomGenerator();
        byte[] bytes = obtain(length);
        sr.nextBytes(bytes);
        return bytes;

    } catch(java.lang.Throwable e){

DebugException.start().log(e).end();

        return null;
    }
}
public static Byte random(){
    try{
        SecureRandom sr = SecureProvider.randomGenerator();
        byte[] bytes = obtain(1);
        sr.nextBytes(bytes);
        return bytes[0];

    } catch(java.lang.Throwable e){
DebugException.start().log(e).end();
        return null;
    }
}

public static byte[] obtain(int length){
    if(length > MAX_BYTES_ARRAY_SIZE){
DebugException.start().log("Byte array over sized " + UnitByte.Ko.convert(length) + "Ko").end();
        return null;
    }
    return new byte[length];
}


public static byte[] xor(byte[] bytes, byte alter){
    for(int i = 0; i < bytes.length; i++){
        bytes[i] = (byte)(bytes[i] ^ alter);
    }
    return bytes;
}
public static byte[] xor(byte[] bytes, byte[] alter){
    for(int i = 0; i < bytes.length; i++){
        bytes[i] = (byte)(bytes[i] ^ alter[i % alter.length]);
    }
    return bytes;
}

}
