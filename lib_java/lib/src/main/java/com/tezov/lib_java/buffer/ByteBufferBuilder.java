/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.buffer;

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.util.UtilsString;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java.util.UtilsNull.NOT_NULL;
import com.tezov.lib_java.util.UtilsNull.NULL;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.type.collection.ListEntry;
import com.tezov.lib_java.type.primaire.Entry;
import com.tezov.lib_java.type.primitive.string.StringCharTo;
import com.tezov.lib_java.util.UtilsBytes;

import java.util.ArrayList;

public class ByteBufferBuilder extends ByteBuffer{
private ListEntry<Class<?>, Object> datas;
private int length = 0;

protected ByteBufferBuilder(java.nio.ByteBuffer buffer){
    super(buffer);
    datas = new ListEntry<>(ArrayList::new);
}
public static ByteBufferBuilder obtain(){
    return new ByteBufferBuilder(null);
}

private ByteBufferBuilder add(Class<?> type, Object o){
    if(buffer != null){
DebugException.start().explode("butffer is already built, can not add any retrofit.data").end();
    }
    datas.add(type, o);
    return this;
}
private ByteBufferBuilder build(){
    if(buffer == null){
        buffer = java.nio.ByteBuffer.wrap(UtilsBytes.obtain(length));
        for(Entry<Class<?>, Object> e:datas){
            Class<?> type = e.key;
            if(type == byte[].class){
                super.put((byte[])e.value);
            }
            else if(type == Integer.class){
                super.put((Integer)e.value);
            }
            else if(type == Long.class){
                super.put((Long)e.value);
            }
            else if(type == Boolean.class){
                super.put((Boolean)e.value);
            }
            else if(type == Float.class){
                super.put((Float)e.value);
            }
            else if(type == Byte.class){
                super.put((Byte)e.value);
            }
            else if(type == char[].class){
                super.put((char[])e.value);
            }
            else if(type == NULL.class){
                super.flagNull();
            }
            else if(type == NOT_NULL.class){
                super.flagNotNull();
            }
            else if(type == ByteBuffer.class){
                if(e.value.getClass() == byte.class){
                    super.copy((byte)e.value);
                }
                else if(e.value.getClass() == byte[].class){
                    super.copy((byte[])e.value);
                }
                else{
DebugException.start().unknown("type", type).end();
                }
            }
            else{
DebugException.start().unknown("type", type).end();
            }
        }
        datas = null;
    }
    return this;
}

@Override
public ByteBufferBuilder flagNull(){
    length += FLAG_SIZE();
    return add(NULL.class, null);
}
@Override
public ByteBufferBuilder flagNotNull(){
    length += FLAG_SIZE();
    return add(NOT_NULL.class, null);
}

@Override
public ByteBufferBuilder put(byte[] data){
    length += BYTES_SIZE(data);
    return add(byte[].class, data);
}
@Override
public ByteBufferBuilder put(Byte data){
    length += BYTE_SIZE(data);
    return add(Byte.class, data);
}
@Override
public ByteBufferBuilder put(char[] data){
    length += CHARS_SIZE(data);
    return add(char[].class, data);
}
@Override
public ByteBufferBuilder put(String data){
    byte[] bytes;
    if(data == null){
        bytes = null;
    }
    else{
        bytes = StringCharTo.Bytes(data);
    }
    return put(bytes);
}
@Override
public ByteBufferBuilder put(Integer data){
    length += INT_SIZE();
    return add(Integer.class, data);
}
@Override
public ByteBufferBuilder put(Long data){
    length += LONG_SIZE();
    return add(Long.class, data);
}
@Override
public ByteBufferBuilder put(Float data){
    length += FLOAT_SIZE();
    return add(Float.class, data);
}
@Override
public ByteBufferBuilder put(Boolean data){
    length += BOOLEAN_SIZE();
    return add(Boolean.class, data);
}

@Override
public ByteBufferBuilder copy(byte b){
    length += 1;
    return add(ByteBuffer.class, b);
}
@Override
public ByteBufferBuilder copy(byte[] bytes){
    length += bytes.length;
    return add(ByteBuffer.class, bytes);
}

@Override
public ByteBufferBuilder clear(){
    datas.clear();
    if(buffer != null){
        buffer = null;
    }
    length = 0;
    return this;
}
@Override
public java.nio.ByteBuffer buffer(){
    build();
    return super.buffer();
}
@Override
public byte[] array(){
    build();
    return super.array();
}

}
