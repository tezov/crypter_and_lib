/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.type.primitive.string;

import com.tezov.lib_java.debug.DebugLog;
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
import com.tezov.lib_java.type.primitive.BytesTo;

import java.nio.charset.StandardCharsets;

public class StringHexTo{
public final static byte[] HEX_CHAR_ARRAY = "0123456789ABCDEF".getBytes(StandardCharsets.UTF_8);
public static final String HEX_PREFIX = "0x";

private StringHexTo(){
}

private static int digit(char c){
    if('0' <= c && c <= '9'){
        return c - '0';
    } else if('A' <= c && c <= 'F'){
        return 10 + (c - 'A');
    } else {
        return -1;
    }
}
public static byte[] encode(String s){
    if(s.startsWith(HEX_PREFIX)){
        s = s.substring(2);
    }
    if(s.length() == 0){
        return null;
    }
    int len = s.length();
    int parity = len % 2;
    byte[] data = new byte[(len / 2) + parity];
    int end = len - parity;
    for(int i = 0; i < end; i += 2){
        data[i >>> 1] = (byte)(digit(s.charAt(i + 1)) + (digit(s.charAt(i)) << 4));
    }
    if(end < len){
        data[end >>> 1] = (byte)(digit(s.charAt(end)) << 4);
    }
    return data;
}
public static String decode(byte[] b){
    byte[] hexChars = new byte[b.length * 2];
    for(int j = 0; j < b.length; j++){
        int v = b[j] & 0xFF;
        hexChars[j * 2] = HEX_CHAR_ARRAY[v >>> 4];
        hexChars[j * 2 + 1] = HEX_CHAR_ARRAY[v & 0x0F];
    }
    return new String(hexChars, StandardCharsets.ISO_8859_1);
}
public static String decode(byte b){
    byte[] hexChars = new byte[2];
    int v = b & 0xFF;
    hexChars[0] = HEX_CHAR_ARRAY[v >>> 4];
    hexChars[1] = HEX_CHAR_ARRAY[v & 0x0F];
    return new String(hexChars, StandardCharsets.ISO_8859_1);
}

public static byte[] Bytes(String s){
    if(s == null){
        return null;
    } else {
        return StringHexTo.encode(s);
    }
}

public static Integer Int(String s){
    if(s == null){
        return null;
    } else {
        return BytesTo.Int(Bytes(s));
    }
}

public static String StringChar(String s){
    if(s == null){
        return null;
    } else {
        return BytesTo.StringChar(StringHexTo.Bytes(s));
    }
}

public static String Complement(String s){
    return BytesTo.StringChar(BytesTo.complement(StringHexTo.Bytes(s)));
}

}
