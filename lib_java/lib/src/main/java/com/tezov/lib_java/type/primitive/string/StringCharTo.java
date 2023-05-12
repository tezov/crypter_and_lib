/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.type.primitive.string;

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugObject;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.type.primitive.CharsTo;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.util.UtilsString;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java.toolbox.Nullify;
import com.tezov.lib_java.type.primitive.BytesTo;
import com.tezov.lib_java.type.primitive.LongTo;

public class StringCharTo{
private final static long PRIME = 1125899906842597L;

private StringCharTo(){
}

public static byte[] encode(String s){
    return s.getBytes(StandardCharsets.UTF_8);
}
public static String decode(byte[] bytes){
    return new String(bytes, StandardCharsets.UTF_8);
}

public static long LongHashcode64(String string){
    long h = PRIME;
    int len = string.length();
    for(int i = 0; i < len; i++){
        h = 31 * h + string.charAt(i);
    }
    return h;
}
public static byte[] BytesHashcode64(String string){
    return LongTo.Bytes(LongHashcode64(string));
}
public static String StringHexHashcode64(String string){
    return LongTo.StringHex(LongHashcode64(string));
}
public static byte LongHashcode8(String string){
    return (byte)LongHashcode64(string);
}
public static byte[] Bytes(String s){
    if(Nullify.string(s) == null){
        return null;
    } else {
        return StringCharTo.encode(s);
    }
}
public static String StringHex(String s){
    if(Nullify.string(s) == null){
        return null;
    } else {
        return BytesTo.StringHex(StringCharTo.encode(s));
    }
}
public static String StringBase64(String s){
    if(Nullify.string(s) == null){
        return null;
    } else {
        return BytesTo.StringBase64(StringCharTo.encode(s));
    }
}
public static String StringBase58(String s){
    if(Nullify.string(s) == null){
        return null;
    } else {
        return BytesTo.StringBase58(StringCharTo.encode(s));
    }
}
public static String StringBase49(String s){
    if(Nullify.string(s) == null){
        return null;
    } else {
        return BytesTo.StringBase49(StringCharTo.encode(s));
    }
}
public static String StringBase45(String s){
    if(Nullify.string(s) == null){
        return null;
    } else {
        return BytesTo.StringBase45(StringCharTo.encode(s));
    }
}

public static String Complement(String s){
    return BytesTo.StringHex(BytesTo.complement(StringCharTo.Bytes(s)));
}

}
