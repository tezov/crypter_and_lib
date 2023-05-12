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
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java.toolbox.Nullify;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.type.primitive.BytesTo;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class StringBase58To{
public static final char[] ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz".toCharArray();
private static final int[] INDEXES = new int[128];
static {
    Arrays.fill(INDEXES, -1);
    for (int i = 0; i < ALPHABET.length; i++) {
        INDEXES[ALPHABET[i]] = i;
    }
}
public static String encode(byte[] input) {
    if (input.length == 0) {
        return "";
    }
    input = copyOfRange(input, 0, input.length);
    int zeroCount = 0;
    while (zeroCount < input.length && input[zeroCount] == 0) {
        ++zeroCount;
    }
    byte[] temp = new byte[input.length * 2];
    int j = temp.length;
    int startAt = zeroCount;
    while (startAt < input.length) {
        byte mod = divmod58(input, startAt);
        if (input[startAt] == 0) {
            ++startAt;
        }
        temp[--j] = (byte)ALPHABET[mod];
    }
    while (j < temp.length && temp[j] == ALPHABET[0]) {
        ++j;
    }
    while (--zeroCount >= 0) {
        temp[--j] = (byte) ALPHABET[0];
    }
    byte[] output = copyOfRange(temp, j, temp.length);
    return new String(output, StandardCharsets.UTF_8);
}
public static byte[] decode(String input) {
    byte[] input58 = new byte[input.length()];
    for (int i = 0; i < input.length(); ++i) {
        char c = input.charAt(i);
        int digit58 = -1;
        if (c < 128) {
            digit58 = INDEXES[c];
        }
        if (digit58 < 0) {
DebugException.start().log("Illegal character " + c + " at " + i).end();
            return null;
        }
        input58[i] = (byte) digit58;
    }
    int zeroCount = 0;
    while (zeroCount < input58.length && input58[zeroCount] == 0) {
        ++zeroCount;
    }
    byte[] temp = new byte[input.length()];
    int j = temp.length;
    int startAt = zeroCount;
    while (startAt < input58.length) {
        byte mod = divmod256(input58, startAt);
        if (input58[startAt] == 0) {
            ++startAt;
        }
        temp[--j] = mod;
    }
    while (j < temp.length && temp[j] == 0) {
        ++j;
    }
    return copyOfRange(temp, j - zeroCount, temp.length);
}
private static byte divmod58(byte[] number, int startAt) {
    int remainder = 0;
    for (int i = startAt; i < number.length; i++) {
        int digit256 = (int) number[i] & 0xFF;
        int temp = remainder * 256 + digit256;
        number[i] = (byte) (temp / 58);
        remainder = temp % 58;
    }
    return (byte) remainder;
}

private static byte divmod256(byte[] number58, int startAt) {
    int remainder = 0;
    for (int i = startAt; i < number58.length; i++) {
        int digit58 = (int) number58[i] & 0xFF;
        int temp = remainder * 58 + digit58;
        number58[i] = (byte) (temp / 256);
        remainder = temp % 256;
    }
    return (byte) remainder;
}
private static byte[] copyOfRange(byte[] source, int from, int to) {
    byte[] range = new byte[to - from];
    System.arraycopy(source, from, range, 0, range.length);
    return range;
}

public static byte[] Bytes(String s){
    if(Nullify.string(s) == null){
        return null;
    }
    else return StringBase58To.decode(s);
}
public static String StringChar(String s){
    if(Nullify.string(s) == null){
        return null;
    } else {
        return BytesTo.StringChar(StringBase58To.decode(s));
    }
}

}