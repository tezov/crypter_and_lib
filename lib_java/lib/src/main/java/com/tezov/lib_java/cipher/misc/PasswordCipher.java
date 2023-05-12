/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.cipher.misc;

import com.tezov.lib_java.buffer.ByteBufferBuilder;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.util.UtilsString;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java.application.AppContext;
import com.tezov.lib_java.toolbox.Compare;
import com.tezov.lib_java.toolbox.Nullify;

import com.tezov.lib_java.buffer.ByteBuffer;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.primitive.string.StringCharTo;
import com.tezov.lib_java.cipher.SecureProvider;
import com.tezov.lib_java.cipher.UtilsMessageDigest;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.type.primitive.ByteTo;
import com.tezov.lib_java.type.primitive.BytesTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.util.UtilsBytes;

import java.util.Arrays;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import static com.tezov.lib_java.cipher.UtilsMessageDigest.Mode.SHA512;

public class PasswordCipher{
private static final String SECRET_KEY_ALGORITHM = "PBKDF2WithHmacSHA1";
private static final int SECRET_KEY_LENGTH = 32;
private static final int ITERATION_MAX = 25000;
private static final int ITERATION_MIN = 5000;

protected char[] ciphered = null;

protected PasswordCipher(){
DebugTrack.start().create(this).end();
}

public static PasswordCipher fromClear(char[] password){
    PasswordCipher pc = new PasswordCipher();
    pc.ciphered = pc.scramble(password);
    return pc;
}
public static PasswordCipher fromCiphered(char[] password){
    PasswordCipher pc = new PasswordCipher();
    pc.ciphered = password;
    return pc;
}

protected int buildIterationCount(byte[] salt){
    int intLength = IntTo.BYTES;
    byte[] iterationCountBytes = new byte[intLength];
    Arrays.fill(iterationCountBytes, (byte)0x00);
    iterationCountBytes[intLength - 1] = salt[0];
    iterationCountBytes[intLength - 2] = salt[salt.length - 1];
    int iterationCount = BytesTo.Int(iterationCountBytes);
    while(iterationCount > ITERATION_MAX){
        iterationCount = iterationCount / 2;
    }
    while(iterationCount < ITERATION_MIN){
        iterationCount = iterationCount * 2;
    }
    return iterationCount;
}
protected javax.crypto.SecretKey buildKey(char[] password, byte[] salt){
    try{
        PBEKeySpec keySpec = new PBEKeySpec(password, salt, buildIterationCount(salt), SECRET_KEY_LENGTH * ByteTo.SIZE);
        SecretKeyFactory factory = SecureProvider.keyFactory(SECRET_KEY_ALGORITHM);
        return factory.generateSecret(keySpec);
    } catch(Throwable e){
DebugException.start().log(e).end();
        return null;
    }
}

protected char[] scramble(char[] password, byte[] salt){
    javax.crypto.SecretKey key = buildKey(password, salt);
    Nullify.array(password);
    ByteBufferBuilder buffer = ByteBufferBuilder.obtain();
    buffer.put(AppContext.getApplicationId());
    buffer.put(salt);
    buffer.put(key.getEncoded());
    buffer.put(BytesTo.complement(salt.clone()));
    byte[] b = UtilsMessageDigest.digest(SHA512, buffer.array());
    char[] c = BytesTo.Chars(b);
    Nullify.array(b);
    return c;
}
protected char[] scramble(char[] password){
    return scramble(password, UtilsBytes.xor(StringCharTo.Bytes(AppContext.getApplicationId()), (byte)0x55));
}

public char[] get(){
    if(ciphered == null){
DebugException.start().explode("ciphered is null").end();
    }
    return ciphered;
}

@Override
public boolean equals(Object obj){
    return (obj instanceof PasswordCipher) && Compare.equals(((PasswordCipher)obj).ciphered, ciphered);
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    Nullify.array(ciphered);
    super.finalize();
}

}
