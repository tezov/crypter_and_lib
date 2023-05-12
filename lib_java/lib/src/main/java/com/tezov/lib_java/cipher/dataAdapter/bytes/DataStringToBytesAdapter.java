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
import com.tezov.lib_java.cipher.dataAdapter.string.DataStringAdapter;

import static com.tezov.lib_java.cipher.dataAdapter.string.DataStringAdapter.Format.HEX;

public class DataStringToBytesAdapter implements defDataAdapterEncoder<String, byte[]>, defDataAdapterDecoder<String, byte[]>{
private final DataStringAdapter stringAdapter;

DataStringToBytesAdapter(DataStringAdapter stringAdapter){
DebugTrack.start().create(this).end();
    this.stringAdapter = stringAdapter;
}

public static DataStringToBytesAdapter forEncoder(){
    return forEncoder(HEX);
}
public static DataStringToBytesAdapter forEncoder(DataStringAdapter.Format format){
    return new DataStringToBytesAdapter(DataStringAdapter.forEncoder(format));
}

public static DataStringToBytesAdapter forDecoder(){
    return forDecoder(HEX);
}
public static DataStringToBytesAdapter forDecoder(DataStringAdapter.Format format){
    return new DataStringToBytesAdapter(DataStringAdapter.forDecoder(format));
}

@Override
public byte[] fromIn(String data, Class<String> type){
    return fromString(data);
}
@Override
public Class<byte[]> getTypeOut(){
    return byte[].class;
}
@Override
public Class<String> getTypeIn(){
    return String.class;
}
@Override
public byte[] fromIn(String data){
    return fromString(data);
}
@Override
public byte[] fromString(String data){
    return stringAdapter.fromString(data);
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
    return stringAdapter.toString(bytes);
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

}
