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
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import javax.security.auth.DestroyFailedException;
import javax.security.auth.Destroyable;

import static com.tezov.lib_java.cipher.key.ecdh.KeyAsymPrivate.KEY_ALGORITHM;

final public class KeyAsymPublic implements PublicKey, Destroyable{
private PublicKey key = null;

KeyAsymPublic(){
DebugTrack.start().create(this).end();
}
KeyAsymPublic setKey(PublicKey key){
    this.key = key;
    return this;
}

public static KeyAsymPublic fromKey(String key){
    return fromKey(StringBase58To.Bytes(key));
}
public static KeyAsymPublic fromKey(byte[] keyBytes){
    KeyAsymPublic key = new KeyAsymPublic().fromBytes(keyBytes);
    Nullify.array(keyBytes);
    return key;
}

public Key getKey(){
    return key;
}

@Override
public String getAlgorithm(){
    return key.getAlgorithm();
}
@Override
public String getFormat(){
    return key.getFormat();
}
@Override
public byte[] getEncoded(){
    return key.getEncoded();
}

public int byteBufferLength(){
    return ByteBuffer.BYTES_SIZE(key.getEncoded());
}
private ByteBuffer toByteBuffer(){
    ByteBuffer byteBuffer = ByteBuffer.obtain(byteBufferLength());
    byteBuffer.put(key.getEncoded());
    return byteBuffer;
}
private void fromByteBuffer(ByteBuffer byteBuffer){
    try{
        KeyFactory factory = SecureProvider.keyPairFactory(KEY_ALGORITHM);
        key = factory.generatePublic(new X509EncodedKeySpec(byteBuffer.getBytes()));
    } catch(Throwable e){

DebugException.start().log(e).end();
        destroyNoThrow();
    }
}

private KeyAsymPublic fromBytes(byte[] keyToBytes){
    fromByteBuffer(ByteBuffer.wrapPacked(keyToBytes));
    return this;
}

public byte[] toBytes(){
    return toByteBuffer().arrayPacked();
}
public String toStringBase58(){
    return BytesTo.StringBase58(toBytes());
}

public DebugString toDebugString(){
    DebugString data = new DebugString();
    data.append("format", key.getFormat());
    data.append("encoded", key.getEncoded());
    return data;
}
public void toDebugLog(){
DebugLog.start().send(toDebugString()).end();
}

@Override
public void destroy() throws DestroyFailedException{
    if(key != null){
        key = null;
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
    return (key == null);
}

@Override
protected void finalize() throws Throwable{
    destroy();
DebugTrack.start().destroy(this).end();
    super.finalize();
}

}
