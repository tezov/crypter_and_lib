/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.cipher.key;

import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.util.UtilsString;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java.toolbox.Compare;

import com.tezov.lib_java.cipher.misc.PasswordCipher;
import com.tezov.lib_java.cipher.SecureProvider;
import com.tezov.lib_java.buffer.ByteBuffer;
import com.tezov.lib_java.definition.defCopyable;
import com.tezov.lib_java.toolbox.Nullify;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.primitive.ByteTo;
import com.tezov.lib_java.type.primitive.BytesTo;
import com.tezov.lib_java.type.primitive.string.StringBase64To;
import com.tezov.lib_java.util.UtilsBytes;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.DestroyFailedException;

public class SecretKey implements javax.crypto.SecretKey, defCopyable<SecretKey>{
private static final String SECRET_KEY_ALGORITHM = "PBKDF2WithHmacSHA1";
private static final int ITERATION_BOUND = 15000;
private static final int ITERATION_MIN = 5000;

protected javax.crypto.SecretKey key = null;
protected byte[] salt = null;
protected Integer iterationCount = null;
protected Integer length = null;

public SecretKey(){
DebugTrack.start().create(this).end();
}
public static byte[] randomBytes(int length) throws NoSuchAlgorithmException{
    SecureRandom random = SecureProvider.randomGenerator();
    byte[] bytes = UtilsBytes.obtain(length);
    random.nextBytes(bytes);
    return bytes;
}
public static int randomIteration() throws NoSuchAlgorithmException{
    return randomIteration(ITERATION_MIN, ITERATION_BOUND);
}
public static int randomIteration(int min, int bound) throws NoSuchAlgorithmException{
    SecureRandom secureRandom = SecureProvider.randomGenerator();
    return secureRandom.nextInt(bound) + min;
}

public static SecretKey fromSpec(PasswordCipher password, String spec){
    return fromSpec(password, StringBase64To.Bytes(spec));
}
public static SecretKey fromSpec(PasswordCipher password, byte[] specBytes){
    SecretKey key = new SecretKey().fromSpecBytes(specBytes).rebuild(password);
    Nullify.array(specBytes);
    return key.erasePrivateData();
}
public static SecretKey fromKey(String key){
    return fromKey(StringBase64To.Bytes(key));
}
public static SecretKey fromKey(byte[] keyBytes){
    SecretKey key = new SecretKey().fromKeyBytes(keyBytes);
    Nullify.array(keyBytes);
    return key.erasePrivateData();
}

public javax.crypto.SecretKey makeJavaxSecretKey(byte[] encodedKey){
    return new SecretKeySpec(encodedKey, "PBKDF2");
}
public <K extends SecretKey> K generate(PasswordCipher password, int length){
    try{
        this.salt = randomBytes(length);
        this.iterationCount = randomIteration();
        this.length = length;
        key = build(password, salt, iterationCount, length);
        @SuppressWarnings("unchecked") K r = (K)this;
        return r;
    } catch(Throwable e){

DebugException.start().log(e).end();

        try{
            destroy();
        } catch(Throwable ed){

DebugException.start().log(ed).end();

        }
        return null;
    }
}
public <K extends SecretKey> K rebuild(PasswordCipher password){
    key = build(password, salt, iterationCount, length);
    @SuppressWarnings("unchecked") K r = (K)this;
    return r;
}
public javax.crypto.SecretKey build(PasswordCipher password, byte[] salt, int iterationCount, int length){
    try{
        PBEKeySpec keySpec = new PBEKeySpec(password.get(), salt, iterationCount, length * ByteTo.SIZE);
        SecretKeyFactory factory = SecureProvider.keyFactory(SECRET_KEY_ALGORITHM);
        return factory.generateSecret(keySpec);
    } catch(Throwable e){

DebugException.start().log(e).end();

        try{
            destroy();
        } catch(Throwable ed){

DebugException.start().log(ed).end();

        }
        return null;
    }
}
public byte[] getSalt(){
    return salt.clone();
}
public Integer getIterationCount(){
    return iterationCount;
}
public Integer getLength(){
    return length;
}

public byte[] randomIv(int length){
    try{
        return randomBytes(length);
    } catch(Throwable e){

DebugException.start().log(e).end();

        return null;
    }
}

@Override
public String getAlgorithm(){
    if(key == null){
        return null;
    } else {
        return key.getAlgorithm();
    }
}
@Override
public String getFormat(){
    if(key == null){
        return null;
    } else {
        return key.getFormat();
    }
}
@Override
public byte[] getEncoded(){
    if(key == null){
        return null;
    } else {
        return key.getEncoded();
    }
}

public <K extends SecretKey> K erasePrivateData(){
    salt = Nullify.array(salt);
    iterationCount = null;
    @SuppressWarnings("unchecked") K r = (K)this;
    return r;

}
@Override
public void destroy() throws DestroyFailedException{
    erasePrivateData();
    length = null;
    if(key != null){
        key = null;
    }
}
public void destroyNoThrow(){
    try{
        destroy();
    } catch(Throwable e){

    }
}

@Override
public boolean isDestroyed(){
    return (salt == null) && (iterationCount == null) && (length == null) && (key == null);
}

public int byteBufferLength(boolean includeKey){
    int bl = ByteBuffer.BYTES_SIZE(salt) + ByteBuffer.INT_SIZE(2);
    if(includeKey){
        bl += ByteBuffer.BYTES_SIZE(getEncoded());
    }
    return bl;
}
public boolean canMakeByteBuffer(){
    return (salt != null) && (iterationCount != null) && (length != null);
}
protected ByteBuffer toByteBuffer(boolean includeKey){
    ByteBuffer byteBuffer = ByteBuffer.obtain(byteBufferLength(includeKey));
    if(includeKey){
        byteBuffer.put(getEncoded());
    }
    else if(!canMakeByteBuffer()){
DebugException.start().log("private retrofit.data erased, can't build byteBuffer").end();
    }
    byteBuffer.put(salt);
    byteBuffer.put(iterationCount);
    byteBuffer.put(length);
    return byteBuffer;
}

protected void fromByteBuffer(ByteBuffer byteBuffer, boolean includeKey){
    if(includeKey){
        key = makeJavaxSecretKey(byteBuffer.getBytes());
    }
    salt = byteBuffer.getBytes();
    iterationCount = byteBuffer.getInt();
    length = byteBuffer.getInt();
}
protected <K extends SecretKey> K fromSpecBytes(byte[] bytes){
    fromByteBuffer(ByteBuffer.wrapPacked(bytes), false);
    @SuppressWarnings("unchecked") K r = (K)this;
    return r;
}
protected <K extends SecretKey> K fromKeyBytes(byte[] bytes){
    fromByteBuffer(ByteBuffer.wrapPacked(bytes), true);
    @SuppressWarnings("unchecked") K r = (K)this;
    return r;
}

public byte[] specToBytes(){
    return toByteBuffer(false).arrayPacked();
}
public String specToStringBase64(){
    return BytesTo.StringBase64(specToBytes());
}

public byte[] keyToBytes(){
    return toByteBuffer(true).arrayPacked();
}
public String keyToStringBase64(){
    return BytesTo.StringBase64(keyToBytes());
}

@Override
public SecretKey copy(){
    return new SecretKey().fromKeyBytes(keyToBytes());
}

@Override
public boolean equals(Object obj){
    if(obj instanceof SecretKey){
        SecretKey second = (SecretKey)obj;
        boolean result = Compare.equals(key.getEncoded(), second.key.getEncoded());
        result &= Compare.equals(key.getAlgorithm(), second.key.getAlgorithm());
        result &= Compare.equals(key.getFormat(), second.key.getFormat());
        result &= Compare.equals(salt, second.salt);
        result &= Compare.equals(iterationCount, second.iterationCount);
        result &= Compare.equals(length, second.length);
        return result;
    }
    else return false;
}
public DebugString toDebugString(){
    DebugString data = new DebugString();
    data.append("length", length);
    data.append("iterationCount", iterationCount);
    data.append("salt", salt);
    if(key != null){
        data.append("algorithm", key.getAlgorithm());
        data.append("format", key.getFormat());
        data.append("encoded", key.getEncoded());
    } else {
        data.append("key", null);
    }
    return data;
}
final public void toDebugLog(){
DebugLog.start().send(toDebugString()).end();
}

@Override
protected void finalize() throws Throwable{
    destroy();
DebugTrack.start().destroy(this).end();
    super.finalize();
}

}
