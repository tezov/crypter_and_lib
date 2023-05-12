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
import com.tezov.lib_java.type.defEnum.EnumBase;

public interface DatagramRegister{
class Is extends EnumBase.Is{
    private final Class<? extends Datagram> type;
    public Is(String value,Class<? extends Datagram> type){
        super(value);
        this.type = type;
    }
    public Class<? extends Datagram> getType(){
        return type;
    }
}
Is DATAGRAM = new Is("DATAGRAM",Datagram.class);
Is DATAGRAM_MESSAGE = new Is("DATAGRAM_MESSAGE", DatagramMessage.class);
Is DATAGRAM_BEACON = new Is("DATAGRAM_BEACON", DatagramBeacon.class);
Is DATAGRAM_ANSWER = new Is("DATAGRAM_ANSWER", DatagramAnswer.class);
Is DATAGRAM_REQUEST = new Is("DATAGRAM_REQUEST", DatagramRequest.class);
static Is find(String name){
    return Is.findTypeOf(Is.class, name);
}

}


