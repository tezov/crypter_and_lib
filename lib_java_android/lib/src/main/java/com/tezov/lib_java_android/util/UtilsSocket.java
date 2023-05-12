/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.util;

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.util.UtilsString;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import androidx.fragment.app.Fragment;
import static com.tezov.lib_java.socket.UdpPacket.BROADCAST_ADDRESS_DEFAULT;

import com.tezov.lib_java_android.application.ConnectivityManager;
import com.tezov.lib_java.socket.UdpPacket;
import com.tezov.lib_java.debug.DebugException;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class UtilsSocket{

public static InetAddress getBroadCastAddress(){
    InetAddress inetAddress = null;
    if(ConnectivityManager.isConnected()){
        inetAddress = ConnectivityManager.getBroadcastIPv4Address();
    }
    if(inetAddress == null){
        try{
            inetAddress = InetAddress.getByName(BROADCAST_ADDRESS_DEFAULT);
        } catch(UnknownHostException e){

DebugException.start().log(e).end();

        }
    }
    return inetAddress;
}
public UdpPacket setRemoteAddressToBroadCast(UdpPacket udpPacket){
    udpPacket.setAddressRemote(getBroadCastAddress());
    return udpPacket;
}
public UdpPacket setRemoteAddressToBroadCastWithPort(UdpPacket udpPacket, int port){
    udpPacket.setAddressRemote(getBroadCastAddress());
    udpPacket.setPortRemote(port);
    return udpPacket;
}

}
