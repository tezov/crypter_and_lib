/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.cipher.key;

import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.debug.DebugException;
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
import com.tezov.lib_java.application.AppRandomNumber;
import com.tezov.lib_java.buffer.ByteBuffer;
import com.tezov.lib_java.cipher.dataInput.MacSigner;
import com.tezov.lib_java.cipher.dataOuput.MacAuthenticator;
import com.tezov.lib_java.cipher.definition.defAuthenticator;
import com.tezov.lib_java.cipher.definition.defMacKey;
import com.tezov.lib_java.cipher.definition.defSigner;
import com.tezov.lib_java.toolbox.Nullify;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java.type.primitive.ByteTo;
import com.tezov.lib_java.type.primitive.string.StringBase64To;
import com.tezov.lib_java.util.UtilsString;

import java.security.Key;

import javax.security.auth.DestroyFailedException;

public final class KeyMac extends SecretKey implements defMacKey{
private Transformation transformation = null;
private byte[] Tag = null;

public KeyMac(){

}

public static KeyMac fromSpec(PasswordCipher password, String spec){
    return fromSpec(password, StringBase64To.Bytes(spec));
}
public static KeyMac fromSpec(PasswordCipher password, String spec, boolean erasePrivateData){
    return fromSpec(password, StringBase64To.Bytes(spec), erasePrivateData);
}
public static KeyMac fromSpec(PasswordCipher password, byte[] specBytes){
    return fromSpec(password, specBytes, true);
}
public static KeyMac fromSpec(PasswordCipher password, byte[] specBytes, boolean erasePrivateData){
    KeyMac key = new KeyMac().fromSpecBytes(specBytes).rebuild(password);
    Nullify.array(specBytes);
    if(erasePrivateData){
        return key.erasePrivateData();
    } else {
        return key;
    }
}

public static KeyMac fromKey(String key){
    return fromKey(StringBase64To.Bytes(key));
}
public static KeyMac fromKey(String key, boolean erasePrivateData){
    return fromKey(StringBase64To.Bytes(key), erasePrivateData);
}
public static KeyMac fromKey(byte[] keyBytes){
    return fromKey(keyBytes, true);
}
public static KeyMac fromKey(byte[] keyBytes, boolean erasePrivateData){
    KeyMac key = new KeyMac().fromKeyBytes(keyBytes);
    Nullify.array(keyBytes);
    if(erasePrivateData){
        return key.erasePrivateData();
    } else {
        return key;
    }
}

@Override
public Key getKey(){
    return this;
}
public KeyMac generate(PasswordCipher password){
    return generate(password, DEFAULT_TRANSFORMATION);
}
public KeyMac generate(PasswordCipher password, Transformation transformation){
    this.transformation = transformation;
    return generate(password, transformation.getLengthByte());
}
@Override
public String getTransformationAlgorithm(){
    return transformation.value;
}
public Transformation getTransformation(){
    return transformation;
}
public KeyMac setTransformation(Transformation transformation){
    this.transformation = transformation;
    return this;
}
@Override
public byte[] getTag(){
    return Tag;
}
public KeyMac setTag(byte[] tag){
    Tag = tag;
    return this;
}
@Override
public byte[] randomIv(){
    return randomIv(length);
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
    transformation = Transformation.find(byteBuffer.getInt());
}
@Override
public KeyMac copy(){
    return new KeyMac().fromKeyBytes(keyToBytes());
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

public final static Transformation DEFAULT_TRANSFORMATION = AppConfig.getKeyMacTransformation(AppConfigKey.CIPHER_DEFAULT_KEY_MAC_TRANSFORMATION.getId());

public enum Transformation{
    HMAC_SHA1(0, "HmacSHA1", 128),
    HMAC_SHA256(1, "HmacSHA256", 256),
    HMAC_SHA512(2, "HmacSHA512", 512);
    private final int id;
    private final String value;
    private final int length_bit;
    Transformation(int id, String value, int length){
        this.id = id;
        this.value = value;
        this.length_bit = length;
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
    public static Transformation findWithLength(int length){
        return findWithLengthBit(length * ByteTo.SIZE);
    }
    public static Transformation findWithLengthBit(int length){
        Transformation[] values = values();
        for(Transformation k: values){
            if(k.length_bit == length){
                return k;
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
    public int getLengthBit(){
        return length_bit;
    }
    public int getLengthByte(){
        return length_bit / ByteTo.SIZE;
    }
}

}
