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
import com.tezov.lib_java.cipher.dataAdapter.string.DataStringAdapter;
import com.tezov.lib_java.cipher.dataAdapter.string.DataStringEncoderAdapter;
import com.tezov.lib_java.buffer.ByteBuffer;
import com.tezov.lib_java.cipher.definition.defMacKey;
import com.tezov.lib_java.cipher.definition.defSigner;
import com.tezov.lib_java.cipher.misc.MacW;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.primitive.IntTo;

import java.io.IOException;
import java.io.OutputStream;

public class MacSigner implements defSigner{
private MacW macW = null;
private byte[] iv = null;
private DataStringEncoderAdapter dataAdapter;
private ByteBufferPacker packer = null;

public MacSigner(defMacKey key){
    this(key, DataStringAdapter.forEncoder());
}
public MacSigner(defMacKey key, DataStringAdapter.Format format){
    this(key, DataStringAdapter.forEncoder(format));
}
public MacSigner(defMacKey key, DataStringEncoderAdapter dataAdapter){
    try{
DebugTrack.start().create(this).end();
        this.macW = MacW.getInstance(key);
        this.dataAdapter = dataAdapter;
    } catch(java.lang.Throwable e){

DebugException.start().log(e).end();

    }
}

@Override
public byte[] getIv(){
    return iv;
}
@Override
public defSigner setIv(byte[] iv){
    this.iv = iv;
    return this;
}
@Override
public defSigner setRandomIv(){
    return setIv(macW.getKey().randomIv());
}
@Override
public MacSigner setPacker(ByteBufferPacker packer){
    this.packer = packer;
    return this;
}

@Override
public byte[] sign(byte[] bytes, int offset, int length){
    if(bytes == null){
        return null;
    } else {
        return sign(bytes, offset, length, iv);
    }
}
@Override
public byte[] sign(byte[] bytes, int offset, int length, byte[] iv){
    try{
        macW.init(iv);
        macW.update(bytes, offset, length);
        byte[] bytesMac = macW.doFinal();
        ByteBuffer chunk = ByteBuffer.obtain(ByteBuffer.BYTES_SIZE(macW.getIv()) + ByteBuffer.BYTES_SIZE(bytes) + ByteBuffer.BYTES_SIZE(bytesMac));
        chunk.put(macW.getIv());
        chunk.put(bytes);
        chunk.put(bytesMac);
        bytes = chunk.array();
        if(packer != null){
            bytes = packer.unpack(bytes);
        }
        return bytes;
    } catch(java.lang.Throwable e){

DebugException.start().log(e).end();

        return null;
    }
}

@Override
public MacOutputStream sign(OutputStream out){
    return new MacOutputStream(out, this);
}

@Override
public byte[] sign(String data){
    return sign(data, iv);
}
@Override
public byte[] sign(String data, byte[] iv){
    return sign(dataAdapter.fromIn(data), iv);
}

@Override
public String signToString(String data){
    return dataAdapter.toOut(sign(dataAdapter.fromIn(data)));
}
@Override
public String signToString(String data, byte[] iv){
    return dataAdapter.toOut(sign(dataAdapter.fromIn(data), iv));
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

public static class MacOutputStream extends OutputStream{
    protected OutputStream out;
    private final MacSigner signer;
    private final byte[] oneByteBuffer;
    public MacOutputStream(OutputStream out, MacSigner signer){
DebugTrack.start().create(this).end();
        this.out = out;
        this.signer = signer;
        oneByteBuffer = new byte[1];
    }
    public MacOutputStream init() throws IOException{
        return init(null);
    }
    public MacOutputStream init(byte[] iv) throws IOException{
        signer.macW.init(iv);
        iv = signer.macW.getIv();
        out.write(IntTo.Bytes(iv.length));
        out.write(iv);
        return this;
    }
    @Override
    public void write(int b) throws IOException{
        oneByteBuffer[0] = (byte)b;
        write(oneByteBuffer, 0, 1);
    }
    @Override
    public void write(byte[] b) throws IOException{
        write(b, 0, b.length);
    }
    @Override
    public void write(byte[] b, int off, int len) throws IOException{
        out.write(b, off, len);
        signer.macW.update(b, off, len);
    }
    @Override
    public void close() throws IOException{
        if(out == null){
            return;
        }
        IOException macError = null;
        try{
            byte[] mac = signer.macW.doFinal();
            out.write(IntTo.Bytes(mac.length));
            out.write(mac);
        } catch(IOException e){
            macError = e;
        }
        out.close();
        out = null;
        if(macError != null){
            throw macError;
        }
    }
    @Override
    public void flush() throws IOException{
        out.flush();
    }
    @Override
    protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
        super.finalize();
    }

}

}
