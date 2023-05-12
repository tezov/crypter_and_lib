/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.cipher.dataOuput;

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.util.UtilsString;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java.buffer.ByteBufferPacker;
import com.tezov.lib_java.buffer.ByteBuffer;
import com.tezov.lib_java.cipher.SecureProvider;
import com.tezov.lib_java.cipher.definition.defCipherKey;
import com.tezov.lib_java.cipher.definition.defDecoderBytes;
import com.tezov.lib_java.file.UtilsStream;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.primitive.BytesTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.util.UtilsBytes;

import static javax.crypto.Cipher.DECRYPT_MODE;
import static javax.crypto.Cipher.ENCRYPT_MODE;

public class DecoderBytesCipher implements defDecoderBytes{
private javax.crypto.Cipher cipher = null;
private defCipherKey key = null;
private ByteBufferPacker packer = null;

public DecoderBytesCipher(defCipherKey key){
    try{
DebugTrack.start().create(this).end();
        cipher = SecureProvider.cipher(key.getTransformationAlgorithm());
        this.key = key;
    } catch(Throwable e){

DebugException.start().log(e).end();

    }
}

@Override
public DecoderBytesCipher setPacker(ByteBufferPacker packer){
    this.packer = packer;
    return this;
}

@Override
public byte[] decode(byte[] bytes, int offset, int length){
    try{
        if(packer != null){
            bytes = packer.unpack(bytes, offset, length);
            offset = 0;
            length = bytes.length;
        }
        ByteBuffer chunk = ByteBuffer.wrap(bytes, offset, length);
        key.init(cipher, DECRYPT_MODE, chunk.getBytes());
        bytes =  cipher.doFinal(chunk.getBytes());
        return bytes;
    } catch(Throwable e){

//DebugException.pop().produce(e).log().pop();

        return null;
    }
}
@Override
public boolean decode(UtilsStream.StreamLinker stream){
    javax.crypto.CipherInputStream cipherIn = null;
    try{
        byte[] intByte = UtilsBytes.obtain(IntTo.BYTES);
        if(stream.read(intByte) != intByte.length){
            throw new Throwable("failed to read iv length");
        }
        byte[] iv = UtilsBytes.obtain(BytesTo.Int(intByte));
        if(stream.read(iv) != iv.length){
            throw new Throwable("failed to read iv");
        }
        if(stream.read(intByte) != intByte.length){
            throw new Throwable("failed to read input size");
        }
        key.init(cipher, ENCRYPT_MODE, iv);
        int inputSize = BytesTo.Int(intByte);
        int cipherOutputSize = cipher.getOutputSize(inputSize);
        key.init(cipher, DECRYPT_MODE, iv);
        cipherIn = new javax.crypto.CipherInputStream(stream.getInNotClosableLimitAvailability(cipherOutputSize), cipher);
        stream.transfer(cipherIn, inputSize);
        cipherIn.close();
        return true;
    } catch(Throwable e){
        UtilsStream.close(cipherIn);

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
