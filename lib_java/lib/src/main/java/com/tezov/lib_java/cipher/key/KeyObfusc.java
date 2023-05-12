/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.cipher.key;

import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java.application.AppConfig;
import com.tezov.lib_java.application.AppConfigKey;
import com.tezov.lib_java.cipher.misc.PasswordCipher;
import com.tezov.lib_java.cipher.dataAdapter.string.DataStringAdapter;
import com.tezov.lib_java.cipher.SecureProvider;
import com.tezov.lib_java.application.AppRandomNumber;
import com.tezov.lib_java.buffer.ByteBuffer;
import com.tezov.lib_java.cipher.dataInput.Encoder;
import com.tezov.lib_java.cipher.dataOuput.Decoder;
import com.tezov.lib_java.cipher.definition.defCipherKey;
import com.tezov.lib_java.cipher.definition.defDecoder;
import com.tezov.lib_java.cipher.definition.defEncoder;
import com.tezov.lib_java.toolbox.Nullify;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java.type.primitive.string.StringBase64To;
import com.tezov.lib_java.util.UtilsString;

import java.security.Key;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.DestroyFailedException;

public class KeyObfusc extends SecretKey implements defCipherKey{
static final String KEY_ALGORITHM = "DES";
public static final int KEY_LENGTH = 8;
private Transformation transformation = null;

public KeyObfusc(){

}

public static KeyObfusc fromSpec(PasswordCipher password, String spec){
    return fromSpec(password, StringBase64To.Bytes(spec));
}
public static KeyObfusc fromSpec(PasswordCipher password, String spec, boolean erasePrivateData){
    return fromSpec(password, StringBase64To.Bytes(spec), erasePrivateData);
}
public static KeyObfusc fromSpec(PasswordCipher password, byte[] specBytes){
    return fromSpec(password, specBytes, true);
}
public static KeyObfusc fromSpec(PasswordCipher password, byte[] specBytes, boolean erasePrivateData){
    KeyObfusc key = new KeyObfusc().fromSpecBytes(specBytes).rebuild(password);
    Nullify.array(specBytes);
    if(erasePrivateData){
        return key.erasePrivateData();
    } else {
        return key;
    }
}

public static KeyObfusc fromKey(String key){
    return fromKey(StringBase64To.Bytes(key));
}
public static KeyObfusc fromKey(String key, boolean erasePrivateData){
    return fromKey(StringBase64To.Bytes(key));
}
public static KeyObfusc fromKey(byte[] keyBytes){
    return fromKey(keyBytes, true);
}
public static KeyObfusc fromKey(byte[] keyBytes, boolean erasePrivateData){
    KeyObfusc key = new KeyObfusc().fromKeyBytes(keyBytes);
    Nullify.array(keyBytes);
    if(erasePrivateData){
        return key.erasePrivateData();
    } else {
        return key;
    }
}

@Override
public Key getKey(){
    return key;
}
public KeyObfusc generate(PasswordCipher password){
    return generate(password, DEFAULT_TRANSFORMATION);
}
public KeyObfusc generate(PasswordCipher password, Transformation transformation){
    this.transformation = transformation;
    return generate(password, KEY_LENGTH);
}
@Override
public javax.crypto.SecretKey build(PasswordCipher password, byte[] salt, int iterationCount, int length){
    javax.crypto.SecretKey key = super.build(password, salt, iterationCount, length);
    try{
        SecretKeyFactory factory = SecureProvider.keyFactory(KEY_ALGORITHM);
        DESKeySpec keySpec = new DESKeySpec(key.getEncoded());
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
@Override
public String getTransformationAlgorithm(){
    return transformation.getValue();
}
public Transformation getTransformation(){
    return transformation;
}
public KeyObfusc setTransformation(Transformation transformation){
    this.transformation = transformation;
    return this;
}
@Override
public javax.crypto.SecretKey makeJavaxSecretKey(byte[] encodedKey){
    return new SecretKeySpec(encodedKey, KEY_ALGORITHM);
}
@Override
public int byteBufferLength(boolean includeKey){
    return ByteBuffer.INT_SIZE() + super.byteBufferLength(includeKey);
}
@Override
public boolean canMakeByteBuffer(){
    return (transformation != null) && super.canMakeByteBuffer();
}
@Override
protected ByteBuffer toByteBuffer(boolean includeKey){
    ByteBuffer byteBuffer = super.toByteBuffer(includeKey);
    byteBuffer.put(transformation.getId());
    return byteBuffer;
}
@Override
protected void fromByteBuffer(ByteBuffer byteBuffer, boolean includeKey){
    super.fromByteBuffer(byteBuffer, includeKey);
    transformation = KeyObfusc.Transformation.find(byteBuffer.getInt());
}
@Override
public KeyObfusc copy(){
    return new KeyObfusc().fromKeyBytes(keyToBytes());
}
@Override
public DebugString toDebugString(){
    DebugString data = super.toDebugString();
    data.append("transformation", transformation);
    return data;
}
@Override
public boolean isDestroyed(){
    return (transformation == null) && super.isDestroyed();
}
@Override
public void destroy() throws DestroyFailedException{
    transformation = null;
    super.destroy();
}


public final static Transformation DEFAULT_TRANSFORMATION = AppConfig.getKeyObfuscTransformation(AppConfigKey.CIPHER_DEFAULT_KEY_OBFUSC_TRANSFORMATION.getId());

public enum Transformation{
    DES_CTR_NO_PAD(0, "DES/CTR/NoPadding"),
    DES_CBC_PKCS5(1, "DES/CBC/PKCS5Padding");
    private final int id;
    private final String value;
    Transformation(int id, String value){
        this.id = id;
        this.value = value;
    }
    public static Transformation find(int id){
        Transformation[] values = values();
        for(Transformation t: values){
            if(t.id == id){
                return t;
            }
        }
        return null;
    }
    public static Transformation find(String value){
        Transformation[] values = values();
        for(Transformation t: values){
            if(t.value.equals(value)){
                return t;
            }
        }
        return null;
    }
    public String getValue(){
        return value;
    }
    public int getId(){
        return id;
    }
}

}
