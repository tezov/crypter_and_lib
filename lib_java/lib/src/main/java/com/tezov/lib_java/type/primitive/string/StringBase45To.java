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
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.util.UtilsString;
import java.util.List;
import java.util.Set;
import com.tezov.lib_java.toolbox.Nullify;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.type.primitive.BytesTo;
import com.tezov.lib_java.type.primitive.LongTo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class StringBase45To{
public final static int[] ALPHANUMERIC_TABLE;
static{
    final Field SOURCE_ALPHANUMERIC_TABLE;
    int[] tmp;
    try{
        SOURCE_ALPHANUMERIC_TABLE = com.google.zxing.qrcode.encoder.Encoder.class.getDeclaredField("ALPHANUMERIC_TABLE");
        SOURCE_ALPHANUMERIC_TABLE.setAccessible(true);
        tmp = (int[])SOURCE_ALPHANUMERIC_TABLE.get(null);
    } catch(Throwable e){
DebugException.start().log(e).end();
        tmp = null;
    }
    ALPHANUMERIC_TABLE = tmp;
}
public static final int QR_PAYLOAD_NUMERIC_BASE = 45;
public static final char[] ALPHANUM_REVERSE_INDEX = new char[QR_PAYLOAD_NUMERIC_BASE];
static{
    final int len = ALPHANUMERIC_TABLE.length;
    for(int x = 0; x < len; x++){
        final int base45DigitValue = ALPHANUMERIC_TABLE[x];
        if(base45DigitValue > -1){
            ALPHANUM_REVERSE_INDEX[base45DigitValue] = (char)x;
        }
    }
}
public static final int LONG_USABLE_BYTES = LongTo.BYTES - 1;
public static final int[] BINARY_TO_BASE45_DIGIT_COUNT_CONVERSION = new int[]{0, 2, 3, 5, 6, 8, 9, 11, 12};
public static final int NUM_BASE45_DIGITS_PER_LONG = BINARY_TO_BASE45_DIGIT_COUNT_CONVERSION[LONG_USABLE_BYTES];
public static final Map<Integer, Integer> BASE45_TO_BINARY_DIGIT_COUNT_CONVERSION = new HashMap<>();
static{
    int len = BINARY_TO_BASE45_DIGIT_COUNT_CONVERSION.length;
    for(int x = 0; x < len; x++){
        int numB45Digits = BINARY_TO_BASE45_DIGIT_COUNT_CONVERSION[x];
        BASE45_TO_BINARY_DIGIT_COUNT_CONVERSION.put(numB45Digits, x);
    }
}

public static String encode(final byte[] inputData){
    return encode(new ByteArrayInputStream(inputData));
}
public static String encode(final InputStream in){
    try{
        final StringBuilder strOut = new StringBuilder();
        int data;
        long buf = 0;
        while(in.available() > 0){
            int numBytesStored = 0;
            while(numBytesStored < LONG_USABLE_BYTES && in.available() > 0){
                data = in.read();
                buf = (buf << 8) | data;
                numBytesStored++;
            }
            final StringBuilder outputChunkBuffer = new StringBuilder();
            final int numBase45Digits = BINARY_TO_BASE45_DIGIT_COUNT_CONVERSION[numBytesStored];
            int numB45DigitsProcessed = 0;
            while(numB45DigitsProcessed < numBase45Digits){
                final byte digit = (byte)(buf % QR_PAYLOAD_NUMERIC_BASE);
                buf = buf / QR_PAYLOAD_NUMERIC_BASE;
                outputChunkBuffer.append(ALPHANUM_REVERSE_INDEX[digit]);
                numB45DigitsProcessed++;
            }
            strOut.append(outputChunkBuffer.reverse());
        }
        return strOut.toString();
    } catch(Throwable e){
DebugException.start().log(e).end();
        return null;
    }
}

public static byte[] decode(final String inputStr){
    final byte[] buf = inputStr.getBytes();
    return decode(new ByteArrayInputStream(buf));
}
public static byte[] decode(final InputStream in){
    try{
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        long buf = 0;
        int x = 0;
        while(in.available() > 0){
            int numB45Digits = 0;
            while(numB45Digits < NUM_BASE45_DIGITS_PER_LONG && in.available() > 0){
                char c = (char)in.read();
                int digit = ALPHANUMERIC_TABLE[c];
                buf *= QR_PAYLOAD_NUMERIC_BASE;
                buf += digit;
                numB45Digits++;
            }
            final LinkedList<Byte> outputChunkBuffer = new LinkedList<>();
            final int numBytes = BASE45_TO_BINARY_DIGIT_COUNT_CONVERSION.get(numB45Digits);
            int numBytesProcessed = 0;
            while(numBytesProcessed < numBytes){
                final byte chunk = (byte)buf;
                buf = buf >> 8;
                outputChunkBuffer.push(chunk);
                numBytesProcessed++;
            }
            while(outputChunkBuffer.size() > 0){
                out.write(outputChunkBuffer.pop());
            }
        }
        out.flush();
        out.close();
        return out.toByteArray();
    }
    catch(Throwable e){
DebugException.start().log(e).end();
        return null;
    }
}

public static byte[] Bytes(String s){
    if(Nullify.string(s) == null){
        return null;
    } else {
        return StringBase45To.decode(s);
    }
}
public static String StringChar(String s){
    if(Nullify.string(s) == null){
        return null;
    } else {
        return BytesTo.StringChar(StringBase45To.decode(s));
    }
}

}