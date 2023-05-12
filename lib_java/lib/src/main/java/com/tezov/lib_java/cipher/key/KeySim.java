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

import com.tezov.lib_java.cipher.dataAdapter.string.DataStringAdapter;
import com.tezov.lib_java.application.AppRandomNumber;
import com.tezov.lib_java.buffer.ByteBuffer;
import com.tezov.lib_java.cipher.dataInput.Encoder;
import com.tezov.lib_java.cipher.dataOuput.Decoder;
import com.tezov.lib_java.cipher.definition.defCipherKey;
import com.tezov.lib_java.cipher.definition.defDecoder;
import com.tezov.lib_java.cipher.definition.defEncoder;
import com.tezov.lib_java.toolbox.Nullify;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java.type.primitive.ByteTo;
import com.tezov.lib_java.type.primitive.string.StringBase64To;
import com.tezov.lib_java.util.UtilsString;

import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.spec.GCMParameterSpec;
import javax.security.auth.DestroyFailedException;

import static com.tezov.lib_java.cipher.key.KeySim.Transformation.AES_GCM_NO_PAD;

public class KeySim extends SecretKey implements defCipherKey{
private static final int GCM_TAG_LENGTH = 128;
protected Transformation transformation = null;

public KeySim(){

}

public static KeySim fromSpec(PasswordCipher password, String spec){
    return fromSpec(password, StringBase64To.Bytes(spec));
}
public static KeySim fromSpec(PasswordCipher password, String spec, boolean erasePrivateData){
    return fromSpec(password, StringBase64To.Bytes(spec), erasePrivateData);
}
public static KeySim fromSpec(PasswordCipher password, byte[] specBytes){
    return fromSpec(password, specBytes, true);
}
public static KeySim fromSpec(PasswordCipher password, byte[] specBytes, boolean erasePrivateData){
    KeySim key = new KeySim().fromSpecBytes(specBytes).rebuild(password);
    Nullify.array(specBytes);
    if(erasePrivateData){
        return key.erasePrivateData();
    } else {
        return key;
    }
}

public static KeySim fromKey(String key){
    return fromKey(StringBase64To.Bytes(key));
}
public static KeySim fromKey(String key, boolean erasePrivateData){
    return fromKey(StringBase64To.Bytes(key), erasePrivateData);
}
public static KeySim fromKey(byte[] keyBytes){
    return fromKey(keyBytes, true);
}
public static KeySim fromKey(byte[] keyBytes, boolean erasePrivateData){
    KeySim key = new KeySim().fromKeyBytes(keyBytes);
    Nullify.array(keyBytes);
    if(erasePrivateData){
        return key.erasePrivateData();
    } else {
        return key;
    }
}

public KeySim generate(PasswordCipher password){
    return generate(password, DEFAULT_TRANSFORMATION);
}
public KeySim generate(PasswordCipher password, Transformation transformation){
    return generate(password, transformation, DEFAULT_LENGTH);
}
public KeySim generate(PasswordCipher password, Transformation transformation, Length length){
    this.transformation = transformation;
    return generate(password, length.getValueByte());
}
public KeySim build(PasswordCipher password, Transformation transformation, byte[] salt, int iterationCount, int length){
    this.transformation = transformation;
    this.salt = salt;
    this.iterationCount = iterationCount;
    this.length = length;
    return super.rebuild(password);
}
@Override
public AlgorithmParameterSpec getCipherAlgorithmSpec(byte[] iv, int cipherBlockSize){
    if(transformation == AES_GCM_NO_PAD){
        iv = defCipherKey.super.initIv(iv, cipherBlockSize);
        return new GCMParameterSpec(GCM_TAG_LENGTH, iv);
    } else {
        return defCipherKey.super.getCipherAlgorithmSpec(iv, cipherBlockSize);
    }
}
@Override
public String getTransformationAlgorithm(){
    return transformation.getValue();
}
@Override
public Key getKey(){
    return key;
}
public Transformation getTransformation(){
    return transformation;
}
public KeySim setTransformation(Transformation transformation){
    this.transformation = transformation;
    return this;
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
public KeySim copy(){
    return new KeySim().fromKeyBytes(keyToBytes());
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

@Override
public boolean equals(Object obj){
    if(obj instanceof KeySim){
        KeySim second = (KeySim)obj;
        boolean result = super.equals(obj);
        if((transformation!=null) && (second.transformation != null)){
            result &= transformation.getId() == second.transformation.getId();
        }
        else if((transformation!=null)){
            result = false;
        }
        return result;
    }
    else return false;
}

public final static Transformation DEFAULT_TRANSFORMATION = AppConfig.getKeySimTransformation(AppConfigKey.CIPHER_DEFAULT_KEY_SIM_TRANSFORMATION.getId());
public final static Length DEFAULT_LENGTH = AppConfig.getKeySimLength(AppConfigKey.CIPHER_DEFAULT_KEY_SIM_LENGTH.getId());

public enum Transformation{
    AES_CTR_NO_PAD(0, "AES/CTR/NoPadding"), // don't need padding
    AES_CBC_PKCS5(1, "AES/CBC/PKCS5Padding"), // need padding
    AES_GCM_NO_PAD(2, "AES/GCM/NoPadding"); // = cipher + auth on cipher retrofit.data
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
public enum Length{
    L128(0, 128), L192(1, 192), L256(2, 256);
    private final int id;
    private final int value_bit;
    Length(int id, int value_bit){
        this.id = id;
        this.value_bit = value_bit;
    }
    public static Length findWithLength(int length){
        return findWithLengthBit(length * ByteTo.SIZE);
    }
    public static Length findWithLengthBit(int length){
        Length[] values = values();
        for(Length k: values){
            if(k.value_bit == length){
                return k;
            }
        }
        return null;
    }
    public static Length findWithId(int id){
        Length[] values = values();
        for(Length k: values){
            if(k.id == id){
                return k;
            }
        }
        return null;
    }
    public int getValueBit(){
        return value_bit;
    }
    public int getValueByte(){
        return value_bit / ByteTo.SIZE;
    }
    public int getId(){
        return id;
    }
}

}
