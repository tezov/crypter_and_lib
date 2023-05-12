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
import com.tezov.lib_java.util.UtilsBytes;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.type.primitive.string.StringBase45To;
import com.tezov.lib_java.type.primitive.string.StringBase49To;
import com.tezov.lib_java.type.primitive.string.StringBase58To;
import com.tezov.lib_java.type.primitive.string.StringBase64To;
import com.tezov.lib_java.type.primitive.string.StringCharTo;
import com.tezov.lib_java.type.primitive.string.StringHexCharTo;
import com.tezov.lib_java.type.primitive.string.StringHexTo;

public class BytesTo{

private BytesTo(){
}

public static char[] Chars(byte[] b){
    if(b == null){
        return null;
    }
    char[] c = new char[b.length];
    for(int i=0; i<b.length;i++){
        c[i] = (char)(((char)b[i]) & 0x00FF);
    }
    return c;
}

public static Long Long(byte[] b, int offset){
    if(b == null){
        return null;
    }
    long result = 0;
    for(int end = Math.min(b.length - offset, LongTo.BYTES), i = 0; i < end; i++){
        result <<= ByteTo.SIZE;
        result |= (b[i + offset] & 0xFF);
    }
    return result;
}
public static Long Long(byte[] b){
    return Long(b, 0);
}
public static Double Double(byte[] b, int offset){
    if(b == null){
        return null;
    }
    if((b.length - offset) < Double.BYTES){
        return null;
    }
    int result = 0;
    result |= (b[offset] & 0xFF);
    result <<= ByteTo.SIZE;
    result |= (b[1 + offset] & 0xFF);
    result <<= ByteTo.SIZE;
    result |= (b[2 + offset] & 0xFF);
    result <<= ByteTo.SIZE;
    result |= (b[3 + offset] & 0xFF);
    result <<= ByteTo.SIZE;
    result |= (b[4 + offset] & 0xFF);
    result <<= ByteTo.SIZE;
    result |= (b[5 + offset] & 0xFF);
    result <<= ByteTo.SIZE;
    result |= (b[6 + offset] & 0xFF);
    result <<= ByteTo.SIZE;
    result |= (b[7 + offset] & 0xFF);
    return Double.longBitsToDouble(result);
}
public static Double Double(byte[] b){
    return Double(b, 0);
}

public static Integer Int(byte[] b, int offset){
    if(b == null){
        return null;
    }
    int result = 0;
    for(int end = Math.min(b.length - offset, IntTo.BYTES), i = 0; i < end; i++){
        result <<= ByteTo.SIZE;
        result |= (b[i + offset] & 0xFF);
    }
    return result;
}
public static Integer Int(byte[] b){
    return Int(b, 0);
}

public static Float Float(byte[] b, int offset){
    if(b == null){
        return null;
    }
    if((b.length - offset) < FloatTo.BYTES){
        return null;
    }
    int result = 0;
    result |= (b[offset] & 0xFF);
    result <<= ByteTo.SIZE;
    result |= (b[1 + offset] & 0xFF);
    result <<= ByteTo.SIZE;
    result |= (b[2 + offset] & 0xFF);
    result <<= ByteTo.SIZE;
    result |= (b[3 + offset] & 0xFF);
    return Float.intBitsToFloat(result);
}
public static Float Float(byte[] b){
    return Float(b, 0);
}

public static Boolean Boolean(byte[] b, int offset){
    if(b == null){
        return null;
    }
    if((b.length - offset) < ByteTo.BYTES){
        return null;
    }
    return b[offset] == 1;
}
public static Boolean Boolean(byte[] b){
    return Boolean(b, 0);
}

public static String StringHex(byte[] b){
    if(b == null){
        return null;
    } else {
        return StringHexTo.decode(b);
    }
}
public static String StringHexChar(byte[] b){
    if(b == null){
        return null;
    } else {
        return StringHexCharTo.decode(b);
    }
}
public static String StringChar(byte[] b){
    if(b == null){
        return null;
    } else {
        return StringCharTo.decode(b);
    }
}
public static String StringBase64(byte[] b){
    if(b == null){
        return null;
    } else {
        return StringBase64To.encode(b);
    }
}
public static String StringBase58(byte[] b){
    if(b == null){
        return null;
    } else {
        return StringBase58To.encode(b);
    }
}
public static String StringBase49(byte[] b){
    if(b == null){
        return null;
    } else {
        return StringBase49To.encode(b);
    }
}
public static String StringBase45(byte[] b){
    if(b == null){
        return null;
    } else {
        return StringBase45To.encode(b);
    }
}

public static void toDebugLogHex(byte[] bytes){
DebugLog.start().send(StringHex(bytes)).end();
}
public static void toDebugLogChar(byte[] bytes){
DebugLog.start().send(StringChar(bytes)).end();
}

public static byte[] complement(byte[] bytes){
    return UtilsBytes.xor(bytes, (byte)0xff);
}

}
