/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.socket.prebuild.datagram;

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugTrack;
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
import com.tezov.lib_java.buffer.ByteBuffer;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java.type.primitive.BytesTo;
import com.tezov.lib_java.type.primitive.string.StringCharTo;

public class DatagramMessage extends Datagram{
private byte[] payload = null;

@Override
public DatagramRegister.Is myType(){
    return DatagramRegister.DATAGRAM_MESSAGE;
}

@Override
public DatagramMessage init(){
    super.init();
    payload = null;
    return this;
}

public String getMessageString(){
    return BytesTo.StringChar(payload);
}
public Datagram setMessage(String payload){
    this.payload = StringCharTo.Bytes(payload);
    return this;
}

public byte[] getMessageBytes(){
    return payload;
}
public Datagram setMessage(byte[] payload){
    this.payload = payload;
    return this;
}

@Override
protected int getLength(){
    return super.getLength() + ByteBuffer.BYTES_SIZE(payload);
}
@Override
protected ByteBuffer toByteBuffer(){
    ByteBuffer buffer = super.toByteBuffer();
    buffer.put(payload);
    return buffer;
}
@Override
public boolean fromByteBuffer(ByteBuffer byteBuffer){
    if(super.fromByteBuffer(byteBuffer)){
        payload = byteBuffer.getBytes();
        return true;
    } else {
        return false;
    }
}

@Override
public DebugString toDebugString(){
    DebugString data = super.toDebugString();
    data.append("payloadBytes", getMessageBytes());
    data.append("payloadString", getMessageString());
    return data;
}

}
