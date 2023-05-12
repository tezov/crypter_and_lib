/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.buffer;

import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.util.UtilsString;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java.type.primitive.CharsTo;
import com.tezov.lib_java.cipher.definition.defEncoder;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.primitive.BooleanTo;
import com.tezov.lib_java.type.primitive.ByteTo;
import com.tezov.lib_java.type.primitive.BytesTo;
import com.tezov.lib_java.type.primitive.FloatTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.primitive.LongTo;
import com.tezov.lib_java.type.primitive.string.StringCharTo;
import com.tezov.lib_java.util.UtilsBytes;

import java.nio.Buffer;
import java.util.Arrays;

public class ByteBuffer{
private final static int BYTE_LENGTH_OFFSET = IntTo.BYTES;
protected java.nio.ByteBuffer buffer;

protected ByteBuffer(java.nio.ByteBuffer buffer){
DebugTrack.start().create(this).end();
    this.buffer = buffer;
}
private ByteBuffer(byte[] bytes){
    this(java.nio.ByteBuffer.wrap(bytes));
}

public static int FLAG_SIZE(){
    return ByteTo.BYTES;
}
public static int FLAG_SIZE(int length){
    return FLAG_SIZE() * length;
}
public static int BYTES_SIZE(int length){
    return BYTE_LENGTH_OFFSET + length;
}
public static int BYTES_SIZE(byte[] bytes){
    return BYTES_SIZE(bytes != null ? bytes.length : 0);
}
public static int BYTE_SIZE(){
    return ByteTo.BYTES + ByteTo.BYTES;
}
public static int BYTE_SIZE(int length){
    return BYTE_SIZE() * length;
}
public static int CHARS_SIZE(int length){
    return BYTE_LENGTH_OFFSET + (length*2);
}
public static int CHARS_SIZE(char[] chars){
    return CHARS_SIZE(chars != null ? chars.length : 0);
}
public static int STRING_SIZE(int length){
    return BYTE_LENGTH_OFFSET + length;
}
public static int STRING_SIZE(String s){
    if(s == null){
        return BYTE_LENGTH_OFFSET;
    }
    else{
        byte[] bytes = StringCharTo.Bytes(s);
        return BYTE_LENGTH_OFFSET + bytes.length;
    }
}
public static int INT_SIZE(){
    return ByteTo.BYTES + IntTo.BYTES;
}
public static int INT_SIZE(int length){
    return INT_SIZE() * length;
}
public static int LONG_SIZE(){
    return ByteTo.BYTES + LongTo.BYTES;
}
public static int LONG_SIZE(int length){
    return LONG_SIZE() * length;
}
public static int FLOAT_SIZE(){
    return ByteTo.BYTES + FloatTo.BYTES;
}
public static int FLOAT_SIZE(int length){
    return FLOAT_SIZE() * length;
}
public static int BOOLEAN_SIZE(){
    return ByteTo.BYTES + ByteTo.BYTES;
}
public static int BOOLEAN_SIZE(int length){
    return BOOLEAN_SIZE() * length;
}

public static ByteBuffer wrapPacked(byte[] bytes, int offset, int length){
    return wrap(ByteBufferPacker.unpackData(bytes)).position(offset).limit(length).slice();
}
public static ByteBuffer wrapPacked(byte[] bytes){
    return new ByteBuffer(ByteBufferPacker.unpackData(bytes));
}
public static ByteBuffer wrap(byte[] bytes, int offset, int length){
    return wrap(bytes).position(offset).limit(length).slice();
}
public static ByteBuffer wrap(byte[] bytes){
    return new ByteBuffer(bytes);
}
public static ByteBuffer obtain(int length){
    return wrap(UtilsBytes.obtain(length));
}
public static ByteBuffer wrap(Buffer buffer){
    if(!buffer.hasArray()){
        return null;
    }
    Object o = buffer.array();
    if(!(o instanceof byte[])){
        return null;
    }
    byte[] bytes = (byte[])o;
    return wrap(bytes).position(buffer.arrayOffset()).limit(buffer.capacity() + buffer.arrayOffset()).slice().limit(buffer.limit());
}
public static ByteBuffer wrap(java.nio.ByteBuffer byteBuffer){
    return new ByteBuffer(byteBuffer);
}

private void putNullFlag(boolean flag){
    buffer.put(BooleanTo.Byte(flag));
}
private boolean getNullFlag(){
    return ByteTo.Boolean(buffer.get());
}
public int peekLength(){
    int position = buffer.position();
    int length = buffer.getInt();
    buffer.position(position);
    return length;
}

private ByteBuffer putRaw(byte[] bytes){
    if((bytes == null) || (bytes.length == 0)){
        buffer.putInt(0);
    } else {
        buffer.putInt(bytes.length);
        buffer.put(bytes);
    }
    return this;
}

private byte[] getRaw(){
    int length = buffer.getInt();
    if(length == 0){
        return null;
    }
    if(length > (buffer.capacity() - buffer.position()) || (length < 0)){
        return null;
    } else {
        byte[] bytes = UtilsBytes.obtain(length);
        buffer.get(bytes);
        return bytes;
    }
}

public ByteBuffer flagNull(){
    buffer.put((byte)0);
    return this;
}
public boolean isFlagNull(){
    return buffer.get() == 0x00;
}

public ByteBuffer flagNotNull(){
    buffer.put((byte)1);
    return this;
}
public boolean isFlagNotNull(){
    return buffer.get() == 0x01;
}

public ByteBuffer put(byte[] data){
    putRaw(data);
    return this;
}
public byte[] getBytes(){
    return getRaw();
}

public ByteBuffer put(Byte data){
    boolean isNotNull = data != null;
    putNullFlag(isNotNull);
    if(isNotNull){
        buffer.put(data);
    }
    return this;
}
public Byte getByte(){
    boolean isNotNull = getNullFlag();
    if(isNotNull){
        return buffer.get();
    }
    return null;
}

public ByteBuffer put(char[] data){
    putRaw(CharsTo.Bytes(data));
    return this;
}
public char[] getChars(){
    byte[] bytes = getRaw();
    if(bytes == null){
        return null;
    } else {
        return BytesTo.Chars(bytes);
    }
}

public ByteBuffer put(String data){
    putRaw(StringCharTo.Bytes(data));
    return this;
}
public String getString(){
    byte[] bytes = getRaw();
    if(bytes == null){
        return null;
    } else {
        return BytesTo.StringChar(bytes);
    }
}

public ByteBuffer put(Integer data){
    boolean isNotNull = data != null;
    putNullFlag(isNotNull);
    if(isNotNull){
        buffer.putInt(data);
    }
    return this;
}
public Integer getInt(){
    boolean isNotNull = getNullFlag();
    if(isNotNull){
        return buffer.getInt();
    }
    return null;
}
public ByteBuffer put(Long data){
    boolean isNotNull = data != null;
    putNullFlag(isNotNull);
    if(isNotNull){
        buffer.putLong(data);
    }
    return this;
}
public Long getLong(){
    boolean isNotNull = getNullFlag();
    if(isNotNull){
        return buffer.getLong();
    }
    return null;
}
public ByteBuffer put(Float data){
    boolean isNotNull = data != null;
    putNullFlag(isNotNull);
    if(isNotNull){
        buffer.putFloat(data);
    }
    return this;
}
public Float getFloat(){
    boolean isNotNull = getNullFlag();
    if(isNotNull){
        return buffer.getFloat();
    }
    return null;
}
public ByteBuffer put(Boolean data){
    boolean isNotNull = data != null;
    putNullFlag(isNotNull);
    if(isNotNull){
        buffer.put(data ? (byte)1 : (byte)0);
    }
    return this;
}
public Boolean getBoolean(){
    boolean isNotNull = getNullFlag();
    if(isNotNull){
        return ByteTo.Boolean(buffer.get());
    }
    return null;
}

public ByteBuffer copy(byte b){
    buffer.put(b);
    return this;
}
public ByteBuffer copy(byte[] bytes){
    buffer.put(bytes);
    return this;
}

public int count(){
    if(buffer.hasArray()){
        return buffer.limit() - buffer.arrayOffset();
    } else {
        return buffer.limit();
    }
}
public int capacity(){
    return buffer.capacity();
}
public int remaining(){
    return buffer.remaining();
}
public ByteBuffer slice(){
    return wrap(buffer.slice());
}
public int arrayOffset(){
    return buffer.arrayOffset();
}
public ByteBuffer position(int position){
    buffer.position(position);
    return this;
}
public int position(){
    return buffer.position();
}
public ByteBuffer limit(int limit){
    buffer.limit(limit);
    return this;
}
public int limit(){
    return buffer.limit();
}
public ByteBuffer rewind(){
    buffer.rewind();
    return this;
}
public ByteBuffer clear(){
    buffer.clear();
    return this;
}

public java.nio.ByteBuffer buffer(){
    return buffer;
}

public byte[] array(){
    return buffer.array();
}
public byte[] array(defEncoder encoder){
    return encoder.encode(array());
}

public byte[] arrayRemaining(){
    return array(remaining());
}
public byte[] array(int count){
    byte[] bytes = Arrays.copyOfRange(array(), position(), position() + count);
    position(position() + count);
    return bytes;
}
public DebugString toDebugString(){
    DebugString data = new DebugString();
    if(buffer.hasArray()){
        data.append("arrayOffset", arrayOffset());
    } else {
        data.append("[has no array]");
    }
    data.append("position", position());
    data.append("remaining", remaining());
    data.append("limit", limit());
    data.append("capacity", capacity());
    return data;
}

public byte[] arrayPacked(){
    return ByteBufferPacker.packData(array());
}

final public void toDebugLog(){
DebugLog.start().send(toDebugString()).end();
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}


}
