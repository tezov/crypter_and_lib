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

public class DatagramRequest extends DatagramMessage{

@Override
public DatagramRegister.Is myType(){
    return DatagramRegister.DATAGRAM_REQUEST;
}

@Override
public DatagramRequest init(){
    super.init();
    return this;
}

@Override
protected int getLength(){
    return super.getLength();
}
@Override
protected ByteBuffer toByteBuffer(){
    return super.toByteBuffer();
}
@Override
public boolean fromByteBuffer(ByteBuffer byteBuffer){
    return super.fromByteBuffer(byteBuffer);
}


@Override
public DebugString toDebugString(){
    return super.toDebugString();
}

}
