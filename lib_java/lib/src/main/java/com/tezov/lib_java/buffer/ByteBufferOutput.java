/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.buffer;

import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.util.UtilsString;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java.type.primitive.CharsTo;

import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.primitive.BooleanTo;
import com.tezov.lib_java.type.primitive.BytesTo;
import com.tezov.lib_java.type.primitive.FloatTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.primitive.LongTo;
import com.tezov.lib_java.type.primitive.string.StringCharTo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ByteBufferOutput extends OutputStream{
public static final int BUFFER_INITIAL_SIZE = 1024;
private ByteArrayOutputStream bos;

private ByteBufferOutput(int length){
DebugTrack.start().create(this).end();
    try{
        bos = new ByteArrayOutputStream(length);
    } catch(java.lang.Throwable e){

DebugException.start().log(e).end();

    }
}

public static ByteBufferOutput obtain(){
    return new ByteBufferOutput(BUFFER_INITIAL_SIZE);
}

public static ByteBufferOutput obtain(int length){
    return new ByteBufferOutput(length);
}

private void putNullFlag(boolean flag){
    writeBytes(BooleanTo.Bytes(flag));
}

private void writeBytes(byte[] bytes){
    try{
        bos.write(bytes);
    } catch(java.lang.Throwable e){
DebugException.start().log(e).end();
    }
}
private void writeByte(byte b){
    try{
        bos.write(b);
    } catch(java.lang.Throwable e){
DebugException.start().log(e).end();
    }
}

private ByteBufferOutput putRaw(byte[] bytes){
    if((bytes == null) || (bytes.length == 0)){
        writeBytes(IntTo.Bytes(0));
    } else {
        writeBytes(IntTo.Bytes(bytes.length));
        writeBytes(bytes);
    }
    return this;
}

public ByteBufferOutput put(byte[] bytes){
    putRaw(bytes);
    return this;
}

public ByteBufferOutput put(Byte data){
    boolean isNotNull = data != null;
    putNullFlag(isNotNull);
    if(isNotNull){
        try{
            bos.write(data);
        } catch(java.lang.Throwable e){
DebugException.start().log(e).end();
        }
    }
    return this;
}

public ByteBufferOutput put(char[] data){
    putRaw(CharsTo.Bytes(data));
    return this;
}

public ByteBufferOutput flagNull(){
    writeByte((byte)0);
    return this;
}
public ByteBufferOutput flagNotNull(){
    writeByte((byte)1);
    return this;
}

public ByteBufferOutput put(String data){
    putRaw(StringCharTo.Bytes(data));
    return this;
}

public ByteBufferOutput put(Integer data){
    boolean isNotNull = data != null;
    putNullFlag(isNotNull);
    if(isNotNull){
        writeBytes(IntTo.Bytes(data));
    }
    return this;
}

public ByteBufferOutput put(Long data){
    boolean isNotNull = data != null;
    putNullFlag(isNotNull);
    if(isNotNull){
        writeBytes(LongTo.Bytes(data));
    }
    return this;
}

public ByteBufferOutput put(Boolean data){
    boolean isNotNull = data != null;
    putNullFlag(isNotNull);
    if(isNotNull){
        writeBytes(BooleanTo.Bytes(data));
    }
    return this;
}

public ByteBufferOutput put(Float data){
    boolean isNotNull = data != null;
    putNullFlag(isNotNull);
    if(isNotNull){
        writeBytes(FloatTo.Bytes(data));
    }
    return this;
}

public int length(){
    return bos.size();
}

public byte[] toBytes(){
    if(bos == null){
        return null;
    }
    return bos.toByteArray();
}
public byte[] toBytesPacked(){
    return ByteBufferPacker.packData(toBytes());
}

public ByteBufferOutput reset(){
    try{
        bos.reset();
    } catch(java.lang.Throwable e){

DebugException.start().log(e).end();

    }
    return this;
}

public ByteBufferOutput clear(){
    try{
        bos.reset();
    } catch(java.lang.Throwable e){

DebugException.start().log(e).end();

    }
    return this;
}

@Override
public void write(byte[] b) throws IOException{
    bos.write(b);
}

@Override
public void write(byte[] b, int off, int len) throws IOException{
    bos.write(b, off, len);
}

@Override
public void flush() throws IOException{
    bos.flush();
}

@Override
public void write(int b) throws IOException{
    bos.write(b);
}

@Override
public void close(){
    if(bos == null){
        return;
    }
    try{
        bos.close();
        bos = null;
    } catch(java.lang.Throwable e){

DebugException.start().log(e).end();

    }
}

@Override
public String toString(){
    return BytesTo.StringHex(toBytes());
}

public DebugString toDebugString(){
    DebugString data = new DebugString();
    data.append("bytes", toBytes());
    return data;
}

final public void toDebugLog(){
DebugLog.start().send(toDebugString()).end();
}

@Override
protected void finalize() throws Throwable{
    close();
DebugTrack.start().destroy(this).end();
    super.finalize();
}

}
