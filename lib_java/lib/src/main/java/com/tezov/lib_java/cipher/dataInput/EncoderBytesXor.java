/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.cipher.dataInput;

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
import com.tezov.lib_java.cipher.definition.defEncoderBytes;
import com.tezov.lib_java.cipher.key.KeyXor;
import com.tezov.lib_java.file.UtilsStream;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.primitive.IntTo;

import java.io.IOException;
import java.io.OutputStream;

public class EncoderBytesXor implements defEncoderBytes{
private final KeyXor key;
private byte[] iv = null;
private ByteBufferPacker packer = null;

public EncoderBytesXor(KeyXor key){
DebugTrack.start().create(this).end();
    this.key = key;
}

//private void copyAndRepeatShake(byte[] src, byte[] dest, byte xor){
//    byte splitter = src[0];
//    for(int end = dest.length, i = 0; i < end; i++){
//        byte value = src[i % src.length];
//        if(splitter % 2 == 0){
//            value = (byte)(value ^ xor);
//        }
//        dest[i] = value;
//        splitter = (byte)(splitter ^ value);
//    }
//}

private static byte encrypt(byte iv, byte key, byte byteToEncrypt){
    return (byte)((byteToEncrypt ^ key) + iv);
}
private static byte encrypt(byte[] iv, byte[] key, int keyOffset, byte byteToEncrypt){
    return encrypt(iv[keyOffset % iv.length], key[keyOffset % (key.length - 1)], byteToEncrypt);
}
private static void encrypt(byte[] iv, byte[] key, int keyOffset, byte[] bytesToEncrypt, int bytesToEncryptOffset, int length){
    for(int end = length + keyOffset, indexKey = keyOffset; indexKey < end; indexKey++){
        bytesToEncrypt[bytesToEncryptOffset] = encrypt(iv, key, indexKey, bytesToEncrypt[bytesToEncryptOffset]);
        bytesToEncryptOffset++;
    }
}

@Override
public byte[] getIv(){
    return iv;
}
@Override
public EncoderBytesXor setIv(byte[] iv){
    this.iv = iv;
    return this;
}
@Override
public EncoderBytesXor setRandomIv(){
    return setIv(key.randomIv(key.getLength()));
}
@Override
public EncoderBytesXor setPacker(ByteBufferPacker packer){
    this.packer = packer;
    return this;
}

@Override
public byte[] encode(byte[] bytes, int offset, int length){
    return encode(bytes, offset, length, iv);
}
@Override
public byte[] encode(byte[] bytes, int offset, int length, byte[] iv){
    try{
        if(iv == null){
            iv = key.randomIv(key.getLength());
        }
        encrypt(iv, key.getEncoded(), 0, bytes, offset, length);
        ByteBuffer chunk = ByteBuffer.obtain(ByteBuffer.BYTES_SIZE(iv) + ByteBuffer.BYTES_SIZE(bytes));
        chunk.put(iv);
        chunk.put(bytes);
        bytes = chunk.array();
        if(packer != null){
            bytes = packer.unpack(bytes);
        }
        return bytes;
    } catch(Throwable e){

DebugException.start().log(e).end();

        return null;
    }
}

@Override
public boolean encode(UtilsStream.StreamLinker stream){
    return encode(stream, iv);
}
@Override
public boolean encode(UtilsStream.StreamLinker stream, byte[] iv){
    EncryptOutputStream cipherOut = null;
    try{
        if(iv == null){
            iv = key.randomIv(key.getLength());
        }
        stream.write(IntTo.Bytes(iv.length));
        stream.write(iv);
        stream.write(IntTo.Bytes(stream.available()));
        cipherOut = new EncryptOutputStream(stream.getOutNotClosable(), key, iv);
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

private static class EncryptOutputStream extends OutputStream{
    private final OutputStream out;
    private int lengthWrote = 0;
    private final byte[] key;
    private final byte[] iv;
    public EncryptOutputStream(OutputStream out, KeyXor key, byte[] iv){
DebugTrack.start().create(this).end();
        this.out = out;
        this.key = key.getEncoded();
        this.iv = iv;
    }
    @Override
    public void write(int b) throws IOException{
        out.write(encrypt(iv, key, lengthWrote, (byte)b));
        lengthWrote += 1;
    }
    @Override
    public void write(byte[] b) throws IOException{
        encrypt(iv, key, lengthWrote, b, 0, b.length);
        out.write(b, 0, b.length);
        lengthWrote += b.length;
    }
    @Override
    public void write(byte[] b, int off, int len) throws IOException{
        encrypt(iv, key, lengthWrote, b, off, len);
        out.write(b, off, len);
        lengthWrote += len;
    }
    @Override
    public void flush() throws IOException{
        out.flush();
    }
    @Override
    public void close() throws IOException{
        out.close();
    }
    @Override
    protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
        super.finalize();
    }

}

}
