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

public class DatagramBeacon extends DatagramMessage{
private String who = null;
private String what = null;
private boolean isBack = false;

@Override
public DatagramRegister.Is myType(){
    return DatagramRegister.DATAGRAM_BEACON;
}

@Override
public DatagramBeacon init(){
    super.init();
    isBack = false;
    who = null;
    what = null;
    return this;
}

public boolean isBack(){
    return isBack;
}
public DatagramBeacon setBack(boolean flag){
    isBack = flag;
    return this;
}

public String getWho(){
    return who;
}
public DatagramBeacon setWho(String who){
    this.who = who;
    return this;
}
public String getWhat(){
    return what;
}
public DatagramBeacon setWhat(String what){
    this.what = what;
    return this;
}

public DatagramBeacon from(DatagramBeacon what){
    this.who = what.getOwnerId();
    this.what = what.getId();
    return this;
}

@Override
protected int getLength(){
    return super.getLength() + ByteBuffer.STRING_SIZE(who) + ByteBuffer.STRING_SIZE(what) + ByteBuffer.BOOLEAN_SIZE();
}
@Override
protected ByteBuffer toByteBuffer(){
    ByteBuffer buffer = super.toByteBuffer();
    buffer.put(isBack);
    buffer.put(who);
    buffer.put(what);
    return buffer;
}
@Override
public boolean fromByteBuffer(ByteBuffer byteBuffer){
    if(super.fromByteBuffer(byteBuffer)){
        isBack = byteBuffer.getBoolean();
        who = byteBuffer.getString();
        what = byteBuffer.getString();
        return true;
    } else {
        return false;
    }
}

@Override
public DebugString toDebugString(){
    DebugString data = super.toDebugString();
    data.append("isBack", isBack);
    data.append("who", who);
    data.append("what", what);
    return data;
}

}
