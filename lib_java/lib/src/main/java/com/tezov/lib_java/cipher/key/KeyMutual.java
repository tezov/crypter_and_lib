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
import com.tezov.lib_java.application.AppRandomNumber;
import com.tezov.lib_java.buffer.ByteBuffer;
import com.tezov.lib_java.cipher.dataAdapter.string.DataStringAdapter;
import com.tezov.lib_java.cipher.dataInput.Encoder;
import com.tezov.lib_java.cipher.dataInput.EncoderBytes;
import com.tezov.lib_java.cipher.dataOuput.Decoder;
import com.tezov.lib_java.cipher.dataOuput.DecoderBytes;
import com.tezov.lib_java.cipher.definition.defDecoderBytes;
import com.tezov.lib_java.cipher.definition.defEncoderBytes;
import com.tezov.lib_java.cipher.misc.PasswordCipher;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java.toolbox.Compare;
import com.tezov.lib_java.toolbox.Nullify;
import com.tezov.lib_java.type.primitive.BytesTo;
import com.tezov.lib_java.type.primitive.string.StringBase64To;
import com.tezov.lib_java.util.UtilsString;

import javax.security.auth.DestroyFailedException;

public class KeyMutual extends KeySim{
private final static int REF_KEY_PASSWORD_LENGTH = 8;
private final static Transformation REF_KEY_TRANSFORMATION = Transformation.AES_CTR_NO_PAD;
private byte[] refKeyBytes = null;
private byte[] ref = null;
private byte[] iv = null;
private byte[] id = null;

public KeyMutual(){

}

public static KeyMutual fromSpec(PasswordCipher password, String spec){
    return fromSpec(password, StringBase64To.Bytes(spec));
}
public static KeyMutual fromSpec(PasswordCipher password, String spec, boolean erasePrivateData){
    return fromSpec(password, StringBase64To.Bytes(spec), erasePrivateData);
}
public static KeyMutual fromSpec(PasswordCipher password, byte[] specBytes){
    return fromSpec(password, specBytes, true);
}
public static KeyMutual fromSpec(PasswordCipher password, byte[] specBytes, boolean erasePrivateData){
    KeyMutual key = new KeyMutual().fromSpecBytes(specBytes).rebuild(password);
    Nullify.array(specBytes);
    if(erasePrivateData){
        return key.erasePrivateData();
    } else {
        return key;
    }
}

public static KeyMutual fromKey(String key){
    return fromKey(StringBase64To.Bytes(key));
}
public static KeyMutual fromKey(String key, boolean erasePrivateData){
    return fromKey(StringBase64To.Bytes(key), erasePrivateData);
}
public static KeyMutual fromKey(byte[] keyBytes){
    return fromKey(keyBytes, true);
}
public static KeyMutual fromKey(byte[] keyBytes, boolean erasePrivateData){
    KeyMutual key = new KeyMutual().fromKeyBytes(keyBytes);
    Nullify.array(keyBytes);
    if(erasePrivateData){
        return key.erasePrivateData();
    } else {
        return key;
    }
}

public static KeyMutual updatePassword(PasswordCipher currentPassword, PasswordCipher newPassword, String spec){
    return updatePassword(currentPassword, newPassword, StringBase64To.Bytes(spec));
}
public static KeyMutual updatePassword(PasswordCipher currentPassword, PasswordCipher newPassword, byte[] specBytes){
    KeyMutual key = KeyMutual.fromSpec(currentPassword, specBytes, false);
    KeyMutual newKey = new KeyMutual().generateSuper(newPassword, key.getTransformation(), Length.findWithLength(key.length));
    KeySim newRefKey = new KeySim().generate(PasswordCipher.fromClear(UtilsString.randomHex(REF_KEY_PASSWORD_LENGTH).toCharArray()), REF_KEY_TRANSFORMATION, Length.findWithLength(key.length));
    newKey.refKeyBytes = newRefKey.keyToBytes();
    defDecoderBytes decoder = DecoderBytes.newDecoder(newRefKey);
    newKey.ref = decoder.decode(key.id);
    defEncoderBytes encoder = EncoderBytes.newEncoder(newRefKey);
    newKey.id = encoder.encode(newKey.ref, key.iv);
    newKey.iv = key.iv;
    if(Compare.equals(key.id, newKey.id)){
        return key;
    } else {
        return null;
    }
}

@Override
public KeyMutual generate(PasswordCipher password){
    return (KeyMutual)super.generate(password);
}
@Override
public KeyMutual generate(PasswordCipher password, Transformation transformation){
    return (KeyMutual)super.generate(password, transformation);
}
@Override
public KeyMutual generate(PasswordCipher password, Transformation transformation, Length length){
    generateSuper(password, transformation, length);
    try{
        KeySim refKey = new KeySim().generate(PasswordCipher.fromClear(UtilsString.randomHex(REF_KEY_PASSWORD_LENGTH).toCharArray()), REF_KEY_TRANSFORMATION, length);
        this.refKeyBytes = refKey.keyToBytes();
        this.ref = randomBytes(length.getValueByte());
        defEncoderBytes encoder = EncoderBytes.newEncoder(refKey);
        this.id = encoder.setRandomIv().encode(this.ref);
        this.iv = encoder.getIv();
        return this;
    } catch(Throwable e){
DebugException.start().log(e).end();
        destroyNoThrow();
        return null;
    }
}
private KeyMutual generateSuper(PasswordCipher password, Transformation transformation, Length length){
    super.generate(password, transformation, length);
    return this;
}
@Override
public KeyMutual rebuild(PasswordCipher password){
    super.rebuild(password);
    KeySim refKey = KeySim.fromKey(refKeyBytes.clone());
    this.id = EncoderBytes.newEncoder(refKey).encode(ref, iv);
    return this;
}
public byte[] getIv(){
    return iv;
}
public String getIdStringHex(){
    return BytesTo.StringHex(getId());
}
public String getIdStringBase64(){
    return BytesTo.StringBase64(getId());
}
public byte[] getId(){
    return id;
}
@Override
public int byteBufferLength(boolean includeKey){
    int bl = ByteBuffer.BYTES_SIZE(refKeyBytes) + ByteBuffer.BYTES_SIZE(ref) + ByteBuffer.BYTES_SIZE(iv) + super.byteBufferLength(includeKey);
    if(includeKey){
        bl += ByteBuffer.BYTES_SIZE(id);
    }
    return bl;
}
@Override
public boolean canMakeByteBuffer(){
    return (refKeyBytes != null) && (ref != null) && (iv != null) && super.canMakeByteBuffer();
}
@Override
protected ByteBuffer toByteBuffer(boolean includeKey){
    ByteBuffer byteBuffer = super.toByteBuffer(includeKey);
    if(includeKey){
        byteBuffer.put(id);
    }
    byteBuffer.put(refKeyBytes);
    byteBuffer.put(ref);
    byteBuffer.put(iv);
    return byteBuffer;
}
@Override
protected void fromByteBuffer(ByteBuffer byteBuffer, boolean includeKey){
    super.fromByteBuffer(byteBuffer, includeKey);
    if(includeKey){
        id = byteBuffer.getBytes();
    }
    refKeyBytes = byteBuffer.getBytes();
    ref = byteBuffer.getBytes();
    iv = byteBuffer.getBytes();
}
@Override
public KeyMutual copy(){
    return new KeyMutual().fromKeyBytes(keyToBytes());
}
@Override
public DebugString toDebugString(){
    DebugString data = super.toDebugString();
    data.append("refKeyBytes", refKeyBytes);
    data.append("ref", ref);
    data.append("iv", iv);
    data.append("id", id);
    return data;
}
@Override
public <K extends SecretKey> K erasePrivateData(){
    refKeyBytes = Nullify.array(refKeyBytes);
    ref = Nullify.array(ref);
    return super.erasePrivateData();
}
@Override
public boolean isDestroyed(){
    return (id == null) && (refKeyBytes == null) && (ref == null) && (iv == null) && super.isDestroyed();
}
@Override
public void destroy() throws DestroyFailedException{
    id = Nullify.array(id);
    iv = Nullify.array(iv);
    super.destroy();
}

}
