/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.cipher.dataAdapter.object;

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
import com.tezov.lib_java.cipher.definition.defDataAdapterEncoder;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.cipher.dataAdapter.string.DataStringAdapter;

import static com.tezov.lib_java.cipher.dataAdapter.string.DataStringAdapter.Format.HEX;

public class DataObjectEncoderAdapter implements defDataAdapterEncoder<Object, String>{
protected DataStringAdapter stringAdapter;

DataObjectEncoderAdapter(DataStringAdapter.Format format){
    super();
DebugTrack.start().create(this).end();
    this.stringAdapter = DataStringAdapter.forEncoder(format);
}

public static DataObjectEncoderAdapter forEncoder(){
    return forEncoder(HEX);
}
public static DataObjectEncoderAdapter forEncoder(DataStringAdapter.Format format){
    return new DataObjectEncoderAdapter(format);
}

public DataStringAdapter.Format getFormat(){
    return stringAdapter.getFormat();
}

@Override
public Class<Object> getTypeIn(){
    return Object.class;
}
@Override
public byte[] fromIn(Object data, Class<Object> type){
    return fromString(DataObjectAdapter.objectToString(data, type));
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
public String toOut(byte[] bytes){
    return toString(bytes);
}
@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}







}
