/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.cipher.key.ecdh;

import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.util.UtilsString;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java.application.AppConfig;
import com.tezov.lib_java.application.AppConfigKey;
import com.tezov.lib_java.toolbox.Compare;

import com.tezov.lib_java.type.primitive.string.StringBase58To;
import com.tezov.lib_java.cipher.SecureProvider;
import com.tezov.lib_java.toolbox.Nullify;
import com.tezov.lib_java.buffer.ByteBuffer;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.primitive.BytesTo;

import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.security.auth.DestroyFailedException;
import javax.security.auth.Destroyable;

public final class KeyAsymPrivate implements PrivateKey, Destroyable{
static final String KEY_ALGORITHM = "EC";
private PrivateKey keyPrivate = null;
private PublicKey keyPublic = null;
private Curve curve = null;

private KeyAsymPrivate(){
DebugTrack.start().create(this).end();
}
private KeyAsymPrivate setKey(PrivateKey keyPrivate, PublicKey keyPublic, Curve curve){
    this.keyPrivate = keyPrivate;
    this.keyPublic = keyPublic;
    this.curve = curve;
    return this;
}

public static KeyAsymPrivate generate(){
    return generate(DEFAULT_CURVE);
}
public static KeyAsymPrivate generate(Curve curve){
    try{
        ECGenParameterSpec spec = new ECGenParameterSpec(curve.getCurveName());
        SecureRandom secureRandom = SecureProvider.randomGenerator();
        java.security.KeyPairGenerator keyPairGen = SecureProvider.keyPairGenerator(KEY_ALGORITHM);
        keyPairGen.initialize(spec, secureRandom);
        KeyPair pair = keyPairGen.generateKeyPair();
        return new KeyAsymPrivate().setKey(pair.getPrivate(), pair.getPublic(), curve);
    } catch(Throwable e){
DebugException.start().log(e).end();
        return null;
    }
}

public static KeyAsymPrivate fromKey(String key){
    return fromKey(StringBase58To.Bytes(key));
}
public static KeyAsymPrivate fromKey(byte[] keyBytes){
    KeyAsymPrivate key = new KeyAsymPrivate().fromBytes(keyBytes);
    Nullify.array(keyBytes);
    return key;
}

public Key getKey(){
    return keyPrivate;
}

public KeyAsymPublic getKeyAsymPublic(){
    return new KeyAsymPublic().setKey(keyPublic);
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
public String getCurveName(){
    return curve.getCurveName();
}

public int byteBufferLength(){
    return ByteBuffer.INT_SIZE() + ByteBuffer.BYTES_SIZE(keyPrivate.getEncoded()) + ByteBuffer.BYTES_SIZE(keyPublic.getEncoded());
}
private ByteBuffer toByteBuffer(){
    ByteBuffer byteBuffer = ByteBuffer.obtain(byteBufferLength());
    byteBuffer.put(curve.getId());
    byteBuffer.put(keyPrivate.getEncoded());
    byteBuffer.put(keyPublic.getEncoded());
    return byteBuffer;
}
private void fromByteBuffer(ByteBuffer byteBuffer){
    try{
        curve = Curve.findWithId(byteBuffer.getInt());
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
public String toStringBase58(){
    return BytesTo.StringBase58(toBytes());
}

@Override
public boolean equals(Object obj){
    if(obj instanceof KeyAsymPrivate){
        KeyAsymPrivate second = (KeyAsymPrivate)obj;
        boolean result = Compare.equals(keyPrivate.getEncoded(), second.keyPrivate.getEncoded());
        result &= Compare.equals(keyPrivate.getAlgorithm(), second.keyPrivate.getAlgorithm());
        result &= Compare.equals(keyPrivate.getFormat(), second.keyPrivate.getFormat());
        result &= Compare.equals(keyPublic.getEncoded(), second.keyPublic.getEncoded());
        result &= Compare.equals(keyPublic.getAlgorithm(), second.keyPublic.getAlgorithm());
        result &= Compare.equals(keyPublic.getFormat(), second.keyPublic.getFormat());
        if((curve!=null) && (second.curve != null)){
            result &= curve.getId() == second.curve.getId();
        }
        else if((curve!=null)){
            result = false;
        }
        return result;
    }
    return false;
}
public DebugString toDebugString(){
    DebugString data = new DebugString();
    data.append("curve", curve.getCurveName());
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
    curve = null;
}
public void destroyNoThrow(){
    try{
        destroy();
    } catch(Throwable e){

    }
}
@Override
public boolean isDestroyed(){
    return (keyPrivate == null) && (keyPublic == null) && (curve == null);
}

@Override
protected void finalize() throws Throwable{
    destroy();
DebugTrack.start().destroy(this).end();
    super.finalize();
}

public final static Curve DEFAULT_CURVE = AppConfig.getKeyAgreementCurve(AppConfigKey.CIPHER_DEFAULT_KEY_AGREEMENT_CURVE.getId());

public enum Curve{
    SECP_128_R1(0, "secp128r1"),
    SECP_192_R1(0, "secp192r1"),
    SECP_224_R1(0, "secp224r1"),
    SECP_256_R1(1, "secp256r1"),
    SECP_384_R1(2, "secp384r1"),
    SECP_521_R1(3, "secp521r1");
    private final int id;
    private final String curveName;
    Curve(int id, String curveName){
        this.id = id;
        this.curveName = curveName;
    }
    public String getCurveName(){
        return curveName;
    }
    public int getId(){
        return id;
    }
    public static Curve findWithCurveName(String curveName){
        Curve[] values = values();
        for(Curve k: values){
            if(k.curveName.equals(curveName)){
                return k;
            }
        }
        return null;
    }
    public static Curve findWithId(int id){
        Curve[] values = values();
        for(Curve k: values){
            if(k.id == id){
                return k;
            }
        }
        return null;
    }
    public static Curve findWithLengthBit(int length){
        Curve[] values = values();
        for(Curve k: values){
            if(k.getValueBit() == length){
                return k;
            }
        }
        return null;
    }
    public int getValueBit(){
        switch(this){
            case SECP_128_R1: return 128;
            case SECP_192_R1: return 192;
            case SECP_224_R1: return 224;
            case SECP_256_R1:return 256;
            case SECP_384_R1:return 384;
            case SECP_521_R1:return 521;
            default: {
DebugException.start().notImplemented().end();
                return 0;
            }
        }
    }
}

}
