/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.buffer;

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.util.UtilsString;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java.toolbox.Compare;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.primitive.BytesTo;
import com.tezov.lib_java.type.primitive.FloatTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.primitive.LongTo;
import com.tezov.lib_java.util.UtilsBytes;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ByteBufferInput extends InputStream{
ByteArrayInputStream bis;

private ByteBufferInput(byte[] bytes){
DebugTrack.start().create(this).end();
    try{
        bis = new ByteArrayInputStream(bytes);
    } catch(java.lang.Throwable e){

DebugException.start().log(e).end();

    }
}

public static ByteBufferInput wrap(byte[] bytes){
    return new ByteBufferInput(bytes);
}
public static ByteBufferInput wrapPacked(byte[] bytes){
    return new ByteBufferInput(ByteBufferPacker.unpackData(bytes));
}

private boolean getNullFlag(){
    return Compare.isTrue(BytesTo.Boolean(readBytes(1)));
}

private byte[] readBytes(int length){
    try{
        byte[] bytes = UtilsBytes.obtain(length);
        bis.read(bytes);
        return bytes;
    } catch(java.lang.Throwable e){

DebugException.start().log(e).end();

        return null;
    }
}
private byte readByte(){
    try{
        return (byte)bis.read();
    } catch(java.lang.Throwable e){
        return 0;
    }
}

public byte[] getRaw(){
    int length = BytesTo.Int(readBytes(IntTo.BYTES));
    if(length == 0){
        return null;
    }
    return readBytes(length);
}

public boolean isFlagNull(){
    return readByte() == 0x00;
}
public boolean isFlagNotNull(){
    return readByte() == 0x01;
}

public byte[] getBytes(){
    return getRaw();
}

public Byte getByte(){
    boolean isNotNull = getNullFlag();
    if(isNotNull){
        try{
            return (byte)(bis.read() & 0xFF);
        } catch(java.lang.Throwable e){
DebugException.start().log(e).end();
            return null;
        }
    }
    return null;
}

public char[] getChars(){
    byte[] bytes = getRaw();
    if(bytes == null){
        return null;
    }
    return BytesTo.Chars(bytes);
}

public String getString(){
    byte[] bytes = getRaw();
    if(bytes == null){
        return null;
    }
    return BytesTo.StringChar(bytes);
}

public Integer getInt(){
    boolean isNotNull = getNullFlag();
    if(isNotNull){
        return BytesTo.Int(readBytes(IntTo.BYTES));
    }
    return null;
}

public Long getLong(){
    boolean isNotNull = getNullFlag();
    if(isNotNull){
        return BytesTo.Long(readBytes(LongTo.BYTES));
    }
    return null;
}

public Boolean getBoolean(){
    boolean isNotNull = getNullFlag();
    if(isNotNull){
        return BytesTo.Boolean(readBytes(1));
    }
    return null;
}

public Float getFloat(){
    boolean isNotNull = getNullFlag();
    if(isNotNull){
        return BytesTo.Float(readBytes(FloatTo.BYTES));
    }
    return null;
}

public int remaining(){
    return bis.available();
}

@Override
public int read(byte[] b) throws IOException{
    return bis.read(b);
}

@Override
public int read(byte[] b, int off, int len) throws IOException{
    return bis.read(b, off, len);
}

@Override
public long skip(long n) throws IOException{
    return bis.skip(n);
}

@Override
public int available() throws IOException{
    return bis.available();
}

@Override
public synchronized void mark(int readlimit){
    bis.mark(readlimit);
}

@Override
public synchronized void reset() throws IOException{
    bis.reset();
}

@Override
public boolean markSupported(){
    return bis.markSupported();
}

@Override
public int read() throws IOException{
    return bis.read();
}

@Override
public void close(){
    if(bis == null){
        return;
    }
    try{
        bis.close();
        bis = null;
    } catch(java.lang.Throwable e){

DebugException.start().log(e).end();

    }
}

@Override
protected void finalize() throws Throwable{
    close();
DebugTrack.start().destroy(this).end();
    super.finalize();
}

}
