/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.cipher.dataInput;

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.file.UtilsStream;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.util.UtilsString;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java.buffer.ByteBufferPacker;
import com.tezov.lib_java.cipher.definition.defEncoderBytes;
import com.tezov.lib_java.buffer.ByteBuffer;
import com.tezov.lib_java.cipher.SecureProvider;
import com.tezov.lib_java.cipher.definition.defCipherKey;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.primitive.IntTo;

import static com.tezov.lib_java.file.UtilsStream.StreamLinker;
import static javax.crypto.Cipher.ENCRYPT_MODE;

public class EncoderBytesCipher implements defEncoderBytes{
private javax.crypto.Cipher cipher = null;
private defCipherKey key = null;
private byte[] iv = null;
private ByteBufferPacker packer = null;

public EncoderBytesCipher(defCipherKey key){
    try{
DebugTrack.start().create(this).end();
        cipher = SecureProvider.cipher(key.getTransformationAlgorithm());
        this.key = key;
    } catch(Throwable e){

DebugException.start().log(e).end();

    }
}

@Override
public byte[] getIv(){
    return iv;
}
@Override
public EncoderBytesCipher setIv(byte[] iv){
    this.iv = iv;
    return this;
}
@Override
public EncoderBytesCipher setRandomIv(){
    return setIv(key.randomIv(cipher.getBlockSize()));
}
@Override
public EncoderBytesCipher setPacker(ByteBufferPacker packer){
    this.packer = packer;
    return this;
}

@Override
public byte[] encode(byte[] bytes, int offset, int length){
    if(bytes == null){
        return null;
    } else {
        return encode(bytes, offset, length, iv);
    }
}
@Override
public byte[] encode(byte[] bytes, int offset, int length, byte[] iv){
    try{
        key.init(cipher, ENCRYPT_MODE, iv);
        iv = cipher.getIV();
        bytes = cipher.doFinal(bytes, offset, length);
        ByteBuffer chunk = ByteBuffer.obtain(ByteBuffer.BYTES_SIZE(iv) + ByteBuffer.BYTES_SIZE(bytes));
        chunk.put(iv);
        chunk.put(bytes);
        bytes = chunk.array();
        if(packer != null){
            bytes = packer.pack(bytes);
        }
        return bytes;
    } catch(Throwable e){

DebugException.start().log(e).end();

        return null;
    }
}

@Override
public boolean encode(StreamLinker stream){
    return encode(stream, iv);
}
@Override
public boolean encode(StreamLinker stream, byte[] iv){
    javax.crypto.CipherOutputStream cipherOut = null;
    try{
        key.init(cipher, ENCRYPT_MODE, iv);
        iv = cipher.getIV();
        stream.write(IntTo.Bytes(iv.length));
        stream.write(iv);
        stream.write(IntTo.Bytes(stream.available()));
        cipherOut = new javax.crypto.CipherOutputStream(stream.getOutNotClosable(), cipher);
        stream.transfer(cipherOut);
        cipherOut.close();
        return true;
    } catch(Throwable e){
        UtilsStream.close(cipherOut);

DebugException.start().log(e).end();

        return false;
    }
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

}
