/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.cipher.key.rsa;

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
import com.tezov.lib_java.cipher.dataAdapter.string.DataStringAdapter;
import com.tezov.lib_java.application.AppRandomNumber;
import com.tezov.lib_java.cipher.SecureProvider;
import com.tezov.lib_java.cipher.dataInput.Encoder;
import com.tezov.lib_java.cipher.dataOuput.Decoder;
import com.tezov.lib_java.cipher.definition.defDecoder;
import com.tezov.lib_java.cipher.definition.defEncoder;
import com.tezov.lib_java.toolbox.Nullify;
import com.tezov.lib_java.type.primitive.ByteTo;
import com.tezov.lib_java.buffer.ByteBuffer;
import com.tezov.lib_java.cipher.definition.defCipherKey;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.primitive.BytesTo;
import com.tezov.lib_java.type.primitive.string.StringBase64To;
import com.tezov.lib_java.util.UtilsString;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.security.auth.DestroyFailedException;
import javax.security.auth.Destroyable;

public final class KeyAsymPrivate implements defCipherKey, PrivateKey, Destroyable{
static final String KEY_ALGORITHM = "RSA";
private PrivateKey keyPrivate = null;
private PublicKey keyPublic = null;
private Transformation transformation = null;

private KeyAsymPrivate(){
DebugTrack.start().create(this).end();
}
private KeyAsymPrivate setKey(PrivateKey keyPrivate, PublicKey keyPublic, Transformation transformation){
    this.keyPrivate = keyPrivate;
    this.keyPublic = keyPublic;
    this.transformation = transformation;
    return this;
}

public static KeyAsymPrivate generate(){
    return generate(DEFAULT_TRANSFORMATION);
}
public static KeyAsymPrivate generate(KeyAsymPrivate.Transformation transformation){
    return generate(transformation, DEFAULT_LENGTH);
}
public static KeyAsymPrivate generate(KeyAsymPrivate.Transformation transformation, Length keyLength){
    try{
        SecureRandom secureRandom = SecureProvider.randomGenerator();
        java.security.KeyPairGenerator keyPairGen = SecureProvider.keyPairGenerator(KEY_ALGORITHM);
        keyPairGen.initialize(keyLength.getValueBit(), secureRandom);
        KeyPair pair = keyPairGen.generateKeyPair();
        return new KeyAsymPrivate().setKey(pair.getPrivate(), pair.getPublic(), transformation);
    } catch(java.lang.Throwable e){
DebugException.start().log(e).end();
        return null;

    }
}

public static KeyAsymPrivate fromKey(String key){
    return fromKey(StringBase64To.Bytes(key));
}
public static KeyAsymPrivate fromKey(byte[] keyBytes){
    KeyAsymPrivate key = new KeyAsymPrivate().fromBytes(keyBytes);
    Nullify.array(keyBytes);
    return key;
}

@Override
public Key getKey(){
    return keyPrivate;
}

public KeyAsymPublic getKeyPublic(){
    return new KeyAsymPublic().setKey(keyPublic, transformation);
}

@Override
public byte[] randomIv(int length){
    return null;
}
@Override
public void init(Cipher cipher, int mode, byte[] iv) throws InvalidAlgorithmParameterException, InvalidKeyException{
    cipher.init(mode, getKey());
}
@Override
public String getAlgorithm(){
    return keyPrivate.getAlgorithm();
}
@Override
public String getFormat(){
    return keyPrivate.getFormat();
}
@Override
public byte[] getEncoded(){
    return keyPrivate.getEncoded();
}
@Override
public String getTransformationAlgorithm(){
    return transformation.getValue();
}

private int byteBufferLength(){
    return ByteBuffer.INT_SIZE() + ByteBuffer.BYTES_SIZE(keyPrivate.getEncoded()) + ByteBuffer.BYTES_SIZE(keyPublic.getEncoded());
}
private ByteBuffer toByteBuffer(){
    ByteBuffer byteBuffer = ByteBuffer.obtain(byteBufferLength());
    byteBuffer.put(transformation.getId());
    byteBuffer.put(keyPrivate.getEncoded());
    byteBuffer.put(keyPublic.getEncoded());
    return byteBuffer;
}
private void fromByteBuffer(ByteBuffer byteBuffer){
    try{
        transformation = Transformation.find(byteBuffer.getInt());
        KeyFactory factory = SecureProvider.keyPairFactory(KEY_ALGORITHM);
        keyPrivate = factory.generatePrivate(new PKCS8EncodedKeySpec(byteBuffer.getBytes()));
        keyPublic = factory.generatePublic(new X509EncodedKeySpec(byteBuffer.getBytes()));
    } catch(Throwable e){
DebugException.start().log(e).end();
        destroyNoThrow();
    }
}

private KeyAsymPrivate fromBytes(byte[] keyToBytes){
    fromByteBuffer(ByteBuffer.wrapPacked(keyToBytes));
    return this;
}

public byte[] toBytes(){
    return toByteBuffer().arrayPacked();
}
public String toStringBase64(){
    return BytesTo.StringBase64(toBytes());
}

public DebugString toDebugString(){
    DebugString data = new DebugString();
    data.append("transformation", transformation);
    data.append("algorithm", keyPrivate.getAlgorithm());
    data.append("format", keyPrivate.getFormat());
    data.append("encodedPrivate", keyPrivate.getEncoded());
    data.append("encodedPublic", keyPublic.getEncoded());
    return data;
}
public void toDebugLog(){
DebugLog.start().send(toDebugString()).end();
}

@Override
public void destroy() throws DestroyFailedException{
    if(keyPrivate != null){
        keyPrivate = null;
    }
    if(keyPublic != null){
        keyPublic = null;
    }
    transformation = null;
}
public void destroyNoThrow(){
    try{
        destroy();
    } catch(Throwable e){

    }
}
@Override
public boolean isDestroyed(){
    return (keyPrivate == null) && (keyPublic == null) && (transformation == null);
}

@Override
protected void finalize() throws Throwable{
    destroy();
DebugTrack.start().destroy(this).end();
    super.finalize();
}

public final static Transformation DEFAULT_TRANSFORMATION = AppConfig.getKeyRsaTransformation(AppConfigKey.CIPHER_DEFAULT_KEY_RSA_TRANSFORMATION.getId());
public final static Length DEFAULT_LENGTH = AppConfig.getKeyRsaLength(AppConfigKey.CIPHER_DEFAULT_KEY_RSA_LENGTH.getId());

public enum Transformation{
    RSA_ECB_PKCS1(0, "RSA/ECB/PKCS1Padding"),
    RSA_ECB_OAEP(1, "RSA/ECB/OAEPPadding"),
    RSA_ECB_OAEP_SHA1_MGF1(2, "RSA/ECB/OAEPWithSHA-1AndMGF1Padding"),
    RSA_ECB_OAEP_SHA256_MGF1(3, "RSA/ECB/OAEPWithSHA-256AndMGF1Padding"),
    RSA_ECB_OAEP_SHA512_MGF1(4, "RSA/ECB/OAEPWithSHA-512AndMGF1Padding");
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
    L1024(0, 1024), L2048(1, 2048), L4096(2, 4096);
    private final int id;
    private final int value_bit;
    Length(int id, int value_bit){
        this.id = id;
        this.value_bit = value_bit;
    }
    public static Length findWithLengthByte(int length){
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
}

}
