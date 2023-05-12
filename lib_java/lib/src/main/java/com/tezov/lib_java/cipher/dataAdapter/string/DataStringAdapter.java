/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.cipher.dataAdapter.string;

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

import static com.tezov.lib_java.cipher.dataAdapter.string.DataStringAdapter.Format.HEX;

public abstract class DataStringAdapter implements defDataAdapterEncoder<String, String>, defDataAdapterDecoder<String, String>{
protected Format format;

DataStringAdapter(Format format){
DebugTrack.start().create(this).end();
    this.format = format;
}

public static DataStringEncoderAdapter forEncoder(){
    return forEncoder(HEX);
}
public static DataStringEncoderAdapter forEncoder(Format format){
    return new DataStringEncoderAdapter(format);
}

public static DataStringDecoderAdapter forDecoder(){
    return forDecoder(HEX);
}
public static DataStringDecoderAdapter forDecoder(Format format){
    return new DataStringDecoderAdapter(format);
}

public Format getFormat(){
    return format;
}

@Override
public Class<String> getTypeOut(){
    return String.class;
}
@Override
public Class<String> getTypeIn(){
    return String.class;
}
@Override
public byte[] fromIn(String data, Class<String> type){
    return fromString(data);
}
@Override
public byte[] fromIn(String data){
    return fromString(data);
}

@Override
public String toOut(byte[] bytes, Class<String> type){
    return toString(bytes);
}
@Override
public String toOut(byte[] bytes){
    return toString(bytes);
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

public enum Format{
    HEX, HEX_CHAR, BASE49, BASE58, BASE64, CHAR
}

}
