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
import com.tezov.lib_java.cipher.dataAdapter.string.DataStringAdapter;
import com.tezov.lib_java.cipher.dataAdapter.string.DataStringDecoderAdapter;
import com.tezov.lib_java.buffer.ByteBuffer;
import com.tezov.lib_java.cipher.definition.defAuthenticator;
import com.tezov.lib_java.cipher.definition.defMacKey;
import com.tezov.lib_java.cipher.misc.MacW;
import com.tezov.lib_java.file.UtilsStream;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.primitive.BytesTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.primitive.string.StringBase64To;
import com.tezov.lib_java.util.UtilsBytes;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;

public class MacAuthenticator implements defAuthenticator{
private MacW macW = null;
private DataStringDecoderAdapter dataAdapter;
private ByteBufferPacker packer = null;

public MacAuthenticator(defMacKey key){
    this(key, DataStringAdapter.forDecoder());
}
public MacAuthenticator(defMacKey key, DataStringAdapter.Format format){
    this(key, DataStringAdapter.forDecoder(format));
}
public MacAuthenticator(defMacKey key, DataStringDecoderAdapter dataAdapter){
    try{
DebugTrack.start().create(this).end();
        this.macW = MacW.getInstance(key);
        this.dataAdapter = dataAdapter;
    } catch(java.lang.Throwable e){

DebugException.start().log(e).end();

    }
}

public String auth(String cipherText){
    if(cipherText == null){
        return null;
    } else {
        return BytesTo.StringChar(auth(StringBase64To.Bytes(cipherText)));
    }
}

@Override
public MacAuthenticator setPacker(ByteBufferPacker packer){
    this.packer = packer;
    return this;
}

@Override
public byte[] auth(byte[] bytes, int offset, int length){
    try{
        if(packer != null){
            bytes = packer.unpack(bytes, offset, length);
            offset = 0;
            length = bytes.length;
        }
        ByteBuffer chunk = ByteBuffer.wrap(bytes, offset, length);
        macW.init(chunk.getBytes());
        bytes = chunk.getBytes();
        macW.update(bytes);
        if(!MessageDigest.isEqual(macW.doFinal(), chunk.getBytes())){
            return null;
        } else {
            return bytes;
        }
    } catch(java.lang.Throwable e){

DebugException.start().log(e).end();

        return null;
    }
}

@Override
public MacInputStream auth(InputStream in){
    return new MacInputStream(in, this);
}

@Override
public String authToString(byte[] bytes){
    return dataAdapter.toOut(auth(bytes));
}
@Override
public String authToString(String data){
    return dataAdapter.toOut(auth(dataAdapter.fromIn(data)));
}


@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

public static class MacInputStream extends InputStream{
    protected InputStream in;
    private final MacAuthenticator authenticator;
    private final byte[] oneByteBuffer;
    protected MacInputStream(InputStream in, MacAuthenticator authenticator){
DebugTrack.start().create(this).end();
        this.in = in;
        this.authenticator = authenticator;
        oneByteBuffer = new byte[1];
    }
    public MacInputStream init() throws IOException{
        byte[] intBytes = UtilsBytes.obtain(IntTo.BYTES);
        int readLength = in.read(intBytes);
        if(readLength != intBytes.length){
            throw new IOException("failed to read iv length bytes");
        }
        int ivLength = BytesTo.Int(intBytes);
        if(ivLength < 0){
            throw new IOException("incorrect iv length");
        }
        byte[] iv = UtilsBytes.obtain(ivLength);
        readLength = in.read(iv);
        if(readLength != ivLength){
            throw new IOException("failed to retrieve iv");
        }
        authenticator.macW.init(iv);
        return this;
    }
    @Override
    public int read() throws IOException{
        int l = read(oneByteBuffer, 0, 1);
        if(l != UtilsStream.NULL_LENGTH){
            return oneByteBuffer[0];
        } else {
            return UtilsStream.NULL_LENGTH;
        }
    }
    @Override
    public int read(byte[] b) throws IOException{
        return read(b, 0, b.length);
    }
    @Override
    public int read(byte[] b, int off, int len) throws IOException{
        int l = in.read(b, off, len);
        if(l != UtilsStream.NULL_LENGTH){
            authenticator.macW.update(b, off, l);
        }
        return l;
    }
    @Override
    public void close() throws IOException{
        if(in == null){
            return;
        }
        IOException macError = null;
        try{
            byte[] intBytes = UtilsBytes.obtain(IntTo.BYTES);
            int readLength = in.read(intBytes);
            if(readLength != intBytes.length){
                throw new IOException("failed to read mac length bytes");
            }
            int macLength = BytesTo.Int(intBytes);
            if(macLength < 0){
                throw new IOException("incorrect mac length");
            }
            byte[] mac = UtilsBytes.obtain(macLength);
            readLength = in.read(mac);
            if(readLength != macLength){
                throw new IOException("failed to retrieve mac");
            }
            byte[] macComputed = authenticator.macW.doFinal();
            if(!MessageDigest.isEqual(macComputed, mac)){
                throw new IOException("auth failed");
            }
        } catch(IOException e){
            macError = e;
        }
        in.close();
        in = null;
        if(macError != null){
            throw macError;
        }
    }
    @Override
    public synchronized void mark(int readlimit){
    }
    @Override
    public synchronized void reset() throws IOException{
        throw new IOException("mark/reset not supported");
    }
    @Override
    public boolean markSupported(){
        return false;
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
    protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
        super.finalize();
    }

}

}
