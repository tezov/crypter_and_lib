/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.application;

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
import com.tezov.lib_java.debug.DebugTrack;

public enum AppConfigKey{
DEBUG_LOG_TRACK_HANDLER(-1),
SPEC_CONFIG(1),
SPEC_MUTUAL(2),
CIPHER_DEFAULT_KEY_OBFUSC_TRANSFORMATION(3),
CIPHER_DEFAULT_KEY_XOR_LENGTH(4),
CIPHER_DEFAULT_KEY_MAC_TRANSFORMATION(5),
CIPHER_DEFAULT_KEY_SIM_TRANSFORMATION(6),
CIPHER_DEFAULT_KEY_SIM_LENGTH(7),
CIPHER_DEFAULT_KEY_RSA_TRANSFORMATION(8),
CIPHER_DEFAULT_KEY_RSA_LENGTH(9),
CIPHER_DEFAULT_KEY_AGREEMENT_CURVE(10),
CIPHER_HOLDER_OBFUSC_TRANSFORMATION(11),
CIPHER_HOLDER_SIM_TRANSFORMATION(12),
UDP_LISTENER_PACKET_BUFFER_LENGTH_o( 13),
SOCKET_MESSENGER_BEACON_DELAY_ms(14),
SOCKET_TIMEOUT_HANDSHAKE_ms(15),
SOCKET_MESSENGER_NO_ACK_DELAY_RETRY_ms(16),
RECEIVE_SOCKET_BUFFER_SIZE_Ko(17),
TRANSFER_FILE_BUFFER_SIZE_Ko(18);
int id;
AppConfigKey(int id){
    this.id = id;
}
public static int getFirstIndex(){
    return -1;
}
public static int getLastIndex(){
    return 18;
}
public static AppConfigKey find(int id){
    AppConfigKey[] values = values();
    for(AppConfigKey k: values){
        if(k.id == id){
            return k;
        }
    }
    return null;
}
public static String getName(int id){
    AppConfigKey key = find(id);
    if(key != null){
        return key.name();
    } else {
        return null;
    }
}
public static AppConfigKey find(String name){
    AppConfigKey[] values = values();
    for(AppConfigKey k: values){
        if(k.name().equals(name)){
            return k;
        }
    }
    return null;
}
public static Integer getId(String name){
    AppConfigKey key = find(name);
    if(key != null){
        return key.id;
    } else {
        return null;
    }
}
public int getId(){
    return id;
}
public static class Adapter{
    public Adapter(){
DebugTrack.start().create(this).end();
    }
    public Integer toIndex(String name){
        AppConfigKey key = find(name);
        if(key != null){
            return key.id;
        } else {
            return null;
        }
    }
    public String fromIndex(Integer index){
        AppConfigKey key = find(index);
        if(key != null){
            return key.name();
        } else {
            return null;
        }
    }
    @Override
    protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
        super.finalize();
    }
}
}
