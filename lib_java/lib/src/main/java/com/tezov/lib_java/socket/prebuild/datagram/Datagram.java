/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.socket.prebuild.datagram;

import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java.toolbox.Reflection;
import com.tezov.lib_java.buffer.ByteBuffer;
import com.tezov.lib_java.generator.NumberGenerator;
import com.tezov.lib_java.socket.UdpListener;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.data.validator.ValidatorIpv4;
import com.tezov.lib_java.data.validator.ValidatorPort;
import com.tezov.lib_java.util.UtilsString;

import java.net.InetAddress;

import static com.tezov.lib_java.toolbox.Clock.FormatTime.H24_FULL;

public class Datagram{
public final static int DATAGRAM_MAX_LENGTH = UdpListener.BUFFER_LENGTH_DEFAULT;
public final static int VERSION = 1;
private final static NumberGenerator INDEX_GENERATOR = new NumberGenerator();
private final static int DATAGRAM_ID_LENGTH = 8;
private int version = VERSION;
private long index = 0;
private String time = null;
private String id = null;
private String ownerId = null;
private String ownerAddress = null;
private Integer ownerPort = null;

public Datagram(){
DebugTrack.start().create(this).end();
}

public static <D extends Datagram> D from(byte[] bytes){
    ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
    String type = byteBuffer.getString();
    DatagramRegister.Is typeRegistered = DatagramRegister.find(type);
    if(typeRegistered == null){
        return null;
    } else {
        Datagram datagram = Reflection.newInstance(typeRegistered.getType());
        datagram.fromByteBuffer(byteBuffer.rewind());
        return (D)datagram;
    }
}
public DatagramRegister.Is myType(){
    return DatagramRegister.DATAGRAM;
}
public <D extends Datagram> D init(){
    this.index = INDEX_GENERATOR.nextLong();
    this.time = Clock.Time.now(H24_FULL);
    this.id = UtilsString.randomHex(DATAGRAM_ID_LENGTH);
    this.ownerId = null;
    this.ownerAddress = null;
    this.ownerPort = null;
    return (D)this;
}
public boolean isLengthValid(){
    return (getLength() <= DATAGRAM_MAX_LENGTH);
}
protected int getLength(){
    return ByteBuffer.STRING_SIZE(myType().name()) +
           ByteBuffer.INT_SIZE(2) + ByteBuffer.LONG_SIZE() +
           ByteBuffer.STRING_SIZE(time) +
           ByteBuffer.STRING_SIZE(id) +
           ByteBuffer.STRING_SIZE(ownerId) +
           ByteBuffer.STRING_SIZE(ownerAddress);
}
protected ByteBuffer toByteBuffer(){
    ByteBuffer buffer = ByteBuffer.obtain(getLength());
    buffer.put(myType().name());
    buffer.put(version);
    buffer.put(index);
    buffer.put(time);
    buffer.put(id);
    buffer.put(ownerId);
    buffer.put(ownerAddress);
    buffer.put(ownerPort);
    return buffer;
}
final public byte[] toBytes(){
    return toByteBuffer().array();
}

public boolean fromByteBuffer(ByteBuffer byteBuffer){
    if(myType().name().equals(byteBuffer.getString())){
        version = byteBuffer.getInt();
        index = byteBuffer.getLong();
        time = byteBuffer.getString();
        id = byteBuffer.getString();
        ownerId = byteBuffer.getString();
        ownerAddress = byteBuffer.getString();
        ownerPort = byteBuffer.getInt();
        return true;
    }
    return false;
}
public <D extends Datagram> D fromBytes(byte[] bytes){
    fromByteBuffer(ByteBuffer.wrap(bytes));
    return (D)this;
}

public int getVersion(){
    return version;
}
public long getIndex(){
    return index;
}
public String getTime(){
    return time;
}
public String getId(){
    return id;
}

public <D extends Datagram> D setOwnerId(String ownerId, InetAddress address, Integer port){
    return setOwnerId(ownerId, address!=null?address.getHostAddress():null, port);
}
public <D extends Datagram> D setOwnerId(String ownerId, String address, Integer port){
    this.ownerId = ownerId;
    this.ownerAddress = address;
    this.ownerPort = port;
    return (D)this;
}
public String getOwnerId(){
    return ownerId;
}
public String getOwnerAddress(){
    return ownerAddress;
}
public Integer getOwnerPort(){
    return ownerPort;
}

public boolean isIpv4Valid(){
    return isIpv4Valid(ownerAddress) && isPortValid(ownerPort);
}

public static boolean isIpv4Valid(String address){
    ValidatorIpv4 validatorIpv4 = new ValidatorIpv4();
    return validatorIpv4.isValid(address);
}
public static boolean isPortValid(Integer port){
    ValidatorPort validatorPort = new ValidatorPort();
    return validatorPort.isValid(port);
}

public DebugString toDebugString(){
    DebugString data = new DebugString();
    data.append("myType()", myType().name());
    data.append("index", index);
    data.append("version", version);
    data.append("time", time);
    data.append("id", id);
    data.append("ownerId", ownerId);
    data.append("ownerAddress", ownerAddress);
    data.append("ownerPort", ownerPort);
    return data;
}
final public void toDebugLog(){
DebugLog.start().send(toDebugString()).end();
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

}
