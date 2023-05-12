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

public class DatagramAnswer extends DatagramMessage{
private boolean isAccepted = false;
private String who = null;
private String what = null;

@Override
public DatagramRegister.Is myType(){
    return DatagramRegister.DATAGRAM_ANSWER;
}
@Override
public DatagramAnswer init(){
    super.init();
    isAccepted = false;
    who = null;
    what = null;
    return this;
}
public boolean isAccepted(){
    return isAccepted;
}
public DatagramAnswer setAccepted(boolean flag){
    this.isAccepted = flag;
    return this;
}

public String getWho(){
    return who;
}
public DatagramAnswer setWho(String who){
    this.who = who;
    return this;
}
public String getWhat(){
    return what;
}
public DatagramAnswer setWhat(String what){
    this.what = what;
    return this;
}

public DatagramAnswer from(Datagram what){
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
    buffer.put(isAccepted);
    buffer.put(who);
    buffer.put(what);
    return buffer;
}
@Override
public boolean fromByteBuffer(ByteBuffer byteBuffer){
    if(super.fromByteBuffer(byteBuffer)){
        isAccepted = byteBuffer.getBoolean();
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
    data.append("isAccepted", isAccepted);
    data.append("who", who);
    data.append("what", what);
    return data;
}

}
