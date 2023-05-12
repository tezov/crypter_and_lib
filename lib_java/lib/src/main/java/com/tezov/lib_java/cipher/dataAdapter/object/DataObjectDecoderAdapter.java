/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.cipher.dataAdapter.object;

import com.tezov.lib_java.debug.DebugLog;
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
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.cipher.dataAdapter.string.DataStringAdapter;
import com.tezov.lib_java.debug.DebugException;

import static com.tezov.lib_java.cipher.dataAdapter.string.DataStringAdapter.Format.HEX;

public class DataObjectDecoderAdapter implements defDataAdapterDecoder<String, Object>{
protected DataStringAdapter stringAdapter;

DataObjectDecoderAdapter(DataStringAdapter.Format format){
DebugTrack.start().create(this).end();
    this.stringAdapter = DataStringAdapter.forDecoder(format);
}

public static DataObjectDecoderAdapter forDecoder(){
    return forDecoder(HEX);
}
public static DataObjectDecoderAdapter forDecoder(DataStringAdapter.Format format){
    return new DataObjectDecoderAdapter(format);
}

public DataStringAdapter.Format getFormat(){
    return stringAdapter.getFormat();
}

@Override
public Class<Object> getTypeOut(){
    return Object.class;
}
@Override
public byte[] fromString(String data){
    return stringAdapter.fromIn(data);
}
@Override
public String toString(byte[] bytes){
    return stringAdapter.toOut(bytes);
}

@Override
public byte[] fromIn(String data){
    return fromString(data);
}
@Override
public Object toOut(byte[] bytes, Class<Object> type){
    return DataObjectAdapter.stringToObject(toString(bytes), type);
}
@Override
public String toOut(byte[] bytes){
DebugException.start().notImplemented().end();
    return null;
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}


}
