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
import com.tezov.lib_java.cipher.definition.defDecoderBytes;
import com.tezov.lib_java.cipher.key.KeyXor;
import com.tezov.lib_java.file.UtilsStream;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.primitive.BytesTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.util.UtilsBytes;

import java.io.IOException;
import java.io.InputStream;

import static com.tezov.lib_java.file.UtilsStream.NULL_LENGTH;

public class DecoderBytesXor implements defDecoderBytes{
private final KeyXor key;
private ByteBufferPacker packer = null;

public DecoderBytesXor(KeyXor key){
DebugTrack.start().create(this).end();
    this.key = key;
}

private static byte decrypt(byte iv, byte key, byte byteToEncrypt){
    return (byte)((byteToEncrypt - iv) ^ key);
}
private static byte decrypt(byte[] iv, byte[] key, int keyOffset, byte byteToEncrypt){
    return decrypt(iv[keyOffset % iv.length], key[keyOffset % (key.length - 1)], byteToEncrypt);
}
private static void decrypt(byte[] iv, byte[] key, int keyOffset, byte[] bytesToDecrypt, int bytesToDecryptOffset, int length){
    for(int end = length + keyOffset, indexKey = keyOffset; indexKey < end; indexKey++){
        bytesToDecrypt[bytesToDecryptOffset] = decrypt(iv, key, indexKey, bytesToDecrypt[bytesToDecryptOffset]);
        bytesToDecryptOffset++;
    }
}

@Override
public DecoderBytesXor setPacker(ByteBufferPacker packer){
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
        byte[] iv = chunk.getBytes();
        bytes = chunk.getBytes();
        decrypt(iv, key.getEncoded(), 0, bytes, 0, bytes.length);
        return bytes;
    } catch(Throwable e){

DebugException.start().log(e).end();

        return null;
    }
}
@Override
public boolean decode(UtilsStream.StreamLinker stream){
    DecryptInputStream cipherIn = null;
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
            throw new Throwable("failed to read size");
        }
        int inputSize = BytesTo.Int(intByte);
        cipherIn = new DecryptInputStream(stream.getInNotClosableLimitAvailability(inputSize), key, iv);
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

private static class DecryptInputStream extends InputStream{
    private final InputStream in;
    private int lengthRead = 0;
    private final byte[] keyBytes;
    private final byte[] iv;
    private final byte[] oneByteBuffer;
    public DecryptInputStream(InputStream in, KeyXor key, byte[] iv){
DebugTrack.start().create(this).end();
        this.in = in;
        this.keyBytes = key.getEncoded();
        this.iv = iv;
        oneByteBuffer = new byte[1];
    }
    @Override
    public int read() throws IOException{
        if(read(oneByteBuffer, 0, 1) != NULL_LENGTH){
            return oneByteBuffer[0];
        } else {
            return NULL_LENGTH;
        }
    }
    @Override
    public int read(byte[] b) throws IOException{
        return read(b, 0, b.length);
    }
    @Override
    public int read(byte[] b, int off, int len) throws IOException{
        len = in.read(b, off, len);
        decrypt(iv, keyBytes, lengthRead, b, off, len);
        lengthRead += len;
        return len;
    }
    @Override
    public long skip(long n) throws IOException{
        return in.skip(n);
    }
    @Override
    public int available() throws IOException{
        return in.available();
    }
    @Override
    public synchronized void mark(int readlimit){
        in.mark(readlimit);
    }
    @Override
    public synchronized void reset() throws IOException{
        in.reset();
    }
    @Override
    public boolean markSupported(){
        return in.markSupported();
    }
    @Override
    public void close() throws IOException{
        in.close();
    }
    @Override
    protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
        super.finalize();
    }


}

}
