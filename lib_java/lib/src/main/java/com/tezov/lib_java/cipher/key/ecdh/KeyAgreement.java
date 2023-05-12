/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.cipher.key.ecdh;

import com.tezov.lib_java.buffer.ByteBufferBuilder;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.util.UtilsString;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java.cipher.dataInput.EncoderBytes;
import com.tezov.lib_java.cipher.dataOuput.DecoderBytes;
import com.tezov.lib_java.cipher.definition.defDecoderBytes;
import com.tezov.lib_java.debug.DebugException;

import com.tezov.lib_java.cipher.misc.PasswordCipher;
import com.tezov.lib_java.toolbox.Nullify;
import com.tezov.lib_java.cipher.dataAdapter.string.DataStringAdapter;
import com.tezov.lib_java.cipher.definition.defDataAdapterDecoder;
import com.tezov.lib_java.cipher.definition.defDataAdapterEncoder;
import com.tezov.lib_java.cipher.dataAdapter.bytes.DataBytesToStringAdapter;
import com.tezov.lib_java.cipher.dataAdapter.bytes.DataStringToBytesAdapter;
import com.tezov.lib_java.cipher.definition.defEncoderBytes;

import java.security.NoSuchProviderException;

import com.tezov.lib_java.application.AppRandomNumber;
import com.tezov.lib_java.buffer.ByteBuffer;
import com.tezov.lib_java.cipher.SecureProvider;
import com.tezov.lib_java.cipher.key.KeySim;
import com.tezov.lib_java.toolbox.Compare;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugObject;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.primitive.BytesTo;
import com.tezov.lib_java.type.primitive.string.StringBase58To;
import com.tezov.lib_java.util.UtilsBytes;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Signature;

import javax.security.auth.DestroyFailedException;
import javax.security.auth.Destroyable;

public class KeyAgreement implements Destroyable{

private static final String KEY_AGREEMENT_ALGORITHM = "ECDH";
private static final String KEY_SIGNATURE_ALGORITHM = "ECDSA";
private KeyAsymPrivate keyPrivate = null;
private KeyAsymPublic keyPublicForeign = null;
private KeySim keySim = null;

private KeyAgreement(){
DebugTrack.start().create(this).end();
}

public static KeyAgreement generate(){
    return generate(KeyAsymPrivate.DEFAULT_CURVE);
}
public static KeyAgreement generate(KeyAsymPrivate.Curve curve){
    KeyAgreement keyAgreement = new KeyAgreement();
    keyAgreement.keyPrivate = KeyAsymPrivate.generate(curve);
    keyAgreement.keySim = null;
    keyAgreement.keyPublicForeign = null;
    return keyAgreement;
}

public static KeyAgreement fromKey(String key){
    return fromKey(StringBase58To.Bytes(key));
}
public static KeyAgreement fromKey(byte[] keyBytes){
    ByteBuffer buffer = ByteBuffer.wrap(keyBytes);
    KeyAgreement keyAgreement = new KeyAgreement();
    keyAgreement.keyPrivate = KeyAsymPrivate.fromKey(buffer.getBytes());
    byte[] keyPublicForeign = buffer.getBytes();
    if(keyPublicForeign == null){
        keyAgreement.keyPublicForeign = null;
        keyAgreement.keySim = null;
    }
    else{
        keyAgreement.keyPublicForeign = KeyAsymPublic.fromKey(keyPublicForeign);
        byte[] keySimBytes = buffer.getBytes();
        if(keySimBytes == null){
            keyAgreement.keySim = null;
        }
        else{
            keyAgreement.keySim = KeySim.fromKey(keySimBytes);
        }
    }
    Nullify.array(keyBytes);
    return keyAgreement;
}

public boolean build(String keyPublicForeign){
    return build(keyPublicForeign, KeySim.DEFAULT_TRANSFORMATION);
}
public boolean build(byte[] keyPublicForeign){
    return build(keyPublicForeign, KeySim.DEFAULT_TRANSFORMATION);
}
public boolean build(String keyPublicForeign, KeySim.Transformation transformation){
    return build(keyPublicForeign, transformation, KeySim.DEFAULT_LENGTH);
}
public boolean build(byte[] keyPublicForeign, KeySim.Transformation transformation){
    return build(keyPublicForeign, transformation, KeySim.DEFAULT_LENGTH);
}
public boolean build(String keyPublicForeign, KeySim.Transformation transformation, KeySim.Length length){
    return build(StringBase58To.Bytes(keyPublicForeign), transformation, length);
}
public boolean build(byte[] keyPublicForeign, KeySim.Transformation transformation, KeySim.Length length){
    try{
        PasswordCipher password = buildPassword(keyPublicForeign);
        keySim = new KeySim().generate(password, transformation, length);
        return true;
    } catch(Throwable e){
DebugException.start().log(e).end();
        return false;
    }
}
private PasswordCipher buildPassword(byte[] keyPublicForeign) throws NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException{
    this.keyPublicForeign = KeyAsymPublic.fromKey(keyPublicForeign);
    SecureRandom secureRandom = SecureProvider.randomGenerator();
    javax.crypto.KeyAgreement keyAgreement = SecureProvider.keyAgreement(KEY_AGREEMENT_ALGORITHM);
    keyAgreement.init(keyPrivate, secureRandom);
    keyAgreement.doPhase(this.keyPublicForeign, true);
    return PasswordCipher.fromClear(BytesTo.StringHex(keyAgreement.generateSecret()).toCharArray());
}

public boolean rebuild(String keyPublicForeign, byte[] keySimSpec){
    return rebuild(StringBase58To.Bytes(keyPublicForeign), keySimSpec);
}
private boolean rebuild(byte[] keyPublicForeign, byte[] keySimSpec){
    try{
        PasswordCipher password = buildPassword(keyPublicForeign);
        keySim = KeySim.fromSpec(password, keySimSpec);
        return true;
    } catch(Throwable e){
DebugException.start().log(e).end();
        return false;
    }
}

public KeySim getKeySim(){
    return keySim;
}
public KeyAsymPrivate getKeyAsymPrivate(){
    return keyPrivate;
}
public KeyAsymPublic getKeyAsymPublic(){
    return keyPrivate.getKeyAsymPublic();
}
public KeyAsymPublic getKeyAsymPublicForeign(){
    return keyPublicForeign;
}

public boolean canMakeByteBuffer(){
    return (keyPrivate != null) && ((keySim == null) || keySim.canMakeByteBuffer());
}
public byte[] toBytes(){
    ByteBufferBuilder buffer = ByteBufferBuilder.obtain();
    buffer.put(keyPrivate.toBytes());
    buffer.put(keyPublicForeign!=null?keyPublicForeign.toBytes():null);
    buffer.put(keySim!=null?keySim.keyToBytes():null);
    return buffer.array();
}
public String toStringBase58(){
    return BytesTo.StringBase58(toBytes());
}

public DebugString toDebugString(){
    DebugString data = new DebugString();
    data.append("keyPrivate", keyPrivate);
    data.append("keyPublicForeign", keyPublicForeign);
    data.append("keySim", keySim);
    return data;
}
final public void toDebugLog(){
DebugLog.start().send(toDebugString()).end();
}

@Override
public boolean equals(Object obj){
    if(obj instanceof KeyAgreement){
        KeyAgreement second = (KeyAgreement)obj;
        boolean result = Compare.equals(keyPrivate, second.keyPrivate);
        result &= Compare.equals(keySim, second.keySim);
        return result;
    }
    else return false;
}
@Override
public void destroy() throws DestroyFailedException{
    if(keyPrivate != null){
        keyPrivate = null;
    }
    if(keySim != null){
        keySim.destroy();
        keySim = null;
    }
    if(keyPublicForeign != null){
        keyPublicForeign = null;
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
    return (keyPrivate == null) && (keySim == null) && (keyPublicForeign == null);
}

@Override
protected void finalize() throws Throwable{
    destroy();
DebugTrack.start().destroy(this).end();
    super.finalize();
}


public SignerBytes newSignerBytes(){
    return new SignerBytes(this);
}
public Signer<String, String> newSignerString(){
    return new Signer<>(this, DataStringAdapter.forEncoder(DataStringAdapter.Format.BASE58));
}
public Signer<byte[], String> newSignerBytesToString(){
    return new Signer<>(this, DataBytesToStringAdapter.forEncoder(DataStringAdapter.Format.BASE58));
}
public <IN, OUT> Signer<IN, OUT> newSigner(defDataAdapterEncoder<IN, OUT> dataAdapter){
    return new Signer<>(this, dataAdapter);
}
public static class SignerBytes{
    private KeyAgreement keyAgreement;
    private byte[] header = null;
    public SignerBytes(KeyAgreement keyAgreement){
DebugTrack.start().create(this).end();
        this.keyAgreement = keyAgreement;
    }
    public <S extends SignerBytes> S setKeyAgreement(KeyAgreement keyAgreement){
        this.keyAgreement = keyAgreement;
        return (S)this;
    }
    public <S extends SignerBytes> S setHeaderBytes(byte[] header){
        this.header = header;
        return (S)this;
    }
    public byte[] pack(byte[] bytesToSign){
        return pack(bytesToSign, true);
    }
    public byte[] pack(byte[] bytesToSign, boolean includePublicKey){
        try{
            KeySim keySim = keyAgreement.keySim;
            KeyAsymPrivate keyPrivate = keyAgreement.keyPrivate;
            KeyAsymPublic keyPublicForeign = keyAgreement.keyPublicForeign;
            defEncoderBytes encoder = EncoderBytes.newEncoder(keySim);
            byte[] keySimSpec = keySim.specToBytes();
            byte[] bytesEncrypted = encoder.encode(bytesToSign);
            Signature signature = Signature.getInstance(KEY_SIGNATURE_ALGORITHM);
            signature.initSign(keyPrivate);
            signature.update(keySimSpec);
            signature.update(bytesEncrypted);
            KeyAsymPublic keyAsymPublic = keyPrivate.getKeyAsymPublic();
            ByteBufferBuilder byteBuffer = ByteBufferBuilder.obtain();
            if(header != null){
                byteBuffer.put(header);
            }
            byteBuffer.put(keyPublicForeign.toBytes());
            byteBuffer.put(includePublicKey ? keyAsymPublic.toBytes() : null);
            byteBuffer.put(keySimSpec);
            byteBuffer.put(bytesEncrypted);
            byteBuffer.put(signature.sign());
            return byteBuffer.arrayPacked();
        } catch(Throwable e){
DebugException.start().log(e).end();
            return null;
        }
    }
    @Override
    protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
        super.finalize();
    }
}
public static class Signer<IN, OUT> extends SignerBytes{
    private final defDataAdapterEncoder<IN, OUT> dataAdapter;
    public Signer(KeyAgreement keyAgreement, defDataAdapterEncoder<IN, OUT> dataAdapter){
        super(keyAgreement);
        this.dataAdapter = dataAdapter;
    }
    public Signer<IN, OUT> setHeader(IN header){
        super.setHeaderBytes(dataAdapter.fromIn(header));
        return this;
    }
    public OUT sign(IN data){
        return sign(data, (Class<IN>)(data!=null?data.getClass():null));
    }
    public OUT sign(IN data, Class<IN> type){
        return dataAdapter.toOut(pack(dataAdapter.fromIn(data, type)));
    }
    public OUT sign(IN data, boolean includePublicKey){
        return sign(data, (Class<IN>)(data!=null?data.getClass():null), includePublicKey);
    }
    public OUT sign(IN data, Class<IN> type, boolean includePublicKey){
        return dataAdapter.toOut(pack(dataAdapter.fromIn(data, type), includePublicKey));
    }
}

public VerifierBytes newVerifierBytes(){
    return new VerifierBytes(this);
}
public Verifier<String, String> newVerifierString(){
    return new Verifier<>(this, DataStringAdapter.forDecoder(DataStringAdapter.Format.BASE58));
}
public Verifier<String, byte[]> newVerifierStringToBytes(){
    return new Verifier<>(this, DataStringToBytesAdapter.forDecoder(DataStringAdapter.Format.BASE58));
}
public <IN, OUT> Verifier<IN, OUT> newVerifier(defDataAdapterDecoder<IN, OUT> dataAdapter){
    return new Verifier<>(this, dataAdapter);
}
public static class VerifierBytes{
    private KeyAgreement keyAgreement;
    private ByteBuffer byteBuffer = null;
    public VerifierBytes(KeyAgreement keyAgreement){
DebugTrack.start().create(this).end();
        this.keyAgreement = keyAgreement;
    }
    public <V extends VerifierBytes> V setKeyAgreement(KeyAgreement keyAgreement){
        this.keyAgreement = keyAgreement;
        return (V)this;
    }
    public <V extends VerifierBytes> V load(byte[] bytesSigned){
        this.byteBuffer = ByteBuffer.wrapPacked(bytesSigned);
        return (V)this;
    }
    public byte[] getHeaderBytes(){
        return byteBuffer.getBytes();
    }
    public byte[] unpack(){
        return unpack((byte[])null);
    }
    public byte[] unpack(String keyPublicForeign){
        return unpack(StringBase58To.Bytes(keyPublicForeign));
    }
    public byte[] unpack(byte[] keyPublicForeignBytes){
        try{
             KeyAsymPrivate keyPrivate = keyAgreement.keyPrivate;
            if(!Compare.equals(byteBuffer.getBytes(), keyPrivate.getKeyAsymPublic().toBytes())){
                throw new Throwable("keyPublic mismatch");
            }
            KeyAsymPublic keyPublicForeign;
            if(keyPublicForeignBytes == null){
                keyPublicForeign = KeyAsymPublic.fromKey(byteBuffer.getBytes());
            } else {
                keyPublicForeign = KeyAsymPublic.fromKey(keyPublicForeignBytes);
                if(!Compare.equalsOrNull(byteBuffer.getBytes(), keyPublicForeign.toBytes())){
                    throw new Throwable("keyPublicForeign mismatch");
                }
            }
            byte[] keySimSpec = byteBuffer.getBytes();
            byte[] bytesEncrypted = byteBuffer.getBytes();
            Signature signature = Signature.getInstance(KEY_SIGNATURE_ALGORITHM);
            signature.initVerify(keyPublicForeign);
            signature.update(keySimSpec);
            signature.update(bytesEncrypted);
            if(!signature.verify(byteBuffer.getBytes())){
                throw new Throwable("verify failed");
            }
            if(!keyAgreement.rebuild(keyPublicForeign.toBytes(), keySimSpec)){
                throw new Throwable("rebuild failed");
            }
            KeySim keySim = keyAgreement.keySim;
            defDecoderBytes decoder = DecoderBytes.newDecoder(keySim);
            return decoder.decode(bytesEncrypted);
        } catch(Throwable e){
DebugException.start().log(e).end();
            return null;
        }
    }
    @Override
    protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
        super.finalize();
    }
}
public static class Verifier<IN, OUT> extends VerifierBytes{
    defDataAdapterDecoder<IN, OUT> dataAdapter;
    Class<OUT> type = null;
    public Verifier(KeyAgreement keyAgreement, defDataAdapterDecoder<IN, OUT> dataAdapter){
        super(keyAgreement);
        this.dataAdapter = dataAdapter;
    }
    public Verifier<IN, OUT> load(IN bytesSigned){
        super.load(dataAdapter.fromIn(bytesSigned));
        if(bytesSigned != null){
            type = (Class<OUT>)bytesSigned.getClass();
        }
        return this;
    }
    public OUT getHeader(){
        return dataAdapter.toOut(super.getHeaderBytes());
    }

    public OUT verify(){
        return verify(type);
    }
    public OUT verify(Class<OUT> type){
        return dataAdapter.toOut(unpack(), type);
    }

    public OUT verify(String keyPublicForeign){
        return verify(type, keyPublicForeign);
    }
    public OUT verify(Class<OUT> type, String keyPublicForeign){
        return dataAdapter.toOut(unpack(keyPublicForeign), type);
    }

    public OUT verify(byte[] keyPublicForeign){
        return verify(type, keyPublicForeign);
    }
    public OUT verify(Class<OUT> type, byte[] keyPublicForeign){
        return dataAdapter.toOut(unpack(keyPublicForeign), type);
    }
}

}
