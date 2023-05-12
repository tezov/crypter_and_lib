/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.cipher.dataAdapter.bytes;

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.util.UtilsString;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java.cipher.definition.defDataAdapterDecoder;
import com.tezov.lib_java.cipher.definition.defDataAdapterEncoder;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.primitive.BytesTo;
import com.tezov.lib_java.type.primitive.string.StringCharTo;

public class DataBytesAdapter implements defDataAdapterEncoder<byte[], byte[]>, defDataAdapterDecoder<byte[], byte[]>{

public DataBytesAdapter(){
DebugTrack.start().create(this).end();
}
@Override
public Class<byte[]> getTypeOut(){
    return byte[].class;
}
@Override
public Class<byte[]> getTypeIn(){
    return byte[].class;
}

@Override
public byte[] fromIn(byte[] data, Class<byte[]> type){
    return data;
}
@Override
public byte[] fromIn(byte[] data){
    return data;
}
@Override
public byte[] fromString(String data){
    return StringCharTo.Bytes(data);
}

@Override
public byte[] toOut(byte[] bytes, Class<byte[]> type){
    return bytes;
}
@Override
public byte[] toOut(byte[] bytes){
    return bytes;
}
@Override
public String toString(byte[] bytes){
    return BytesTo.StringChar(bytes);
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}
}
