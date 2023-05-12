/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.socket;

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.util.UtilsString;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java.application.AppConfig;
import com.tezov.lib_java.application.AppConfigKey;
import com.tezov.lib_java.application.AppRandomNumber;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.data.validator.ValidatorPortDynamic;
import com.tezov.lib_java.data.validator.ValidatorPortUser;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.util.UtilsBytes;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;

public class UtilsSocket{
public static final int DEFAULT_BUFFER_SIZE =
        (int)UnitByte.o.convert(AppConfig.getFloat(AppConfigKey.TRANSFER_FILE_BUFFER_SIZE_Ko.getId()), UnitByte.Ko);

public static int randomPortDynamic(){
    return AppRandomNumber.nextInt(ValidatorPortDynamic.MIN, ValidatorPortDynamic.MAX);
}
public static int randomPortUser(){
    return AppRandomNumber.nextInt(ValidatorPortUser.MIN, ValidatorPortUser.MAX);
}
public static InetAddress randomMulticastAddress(){
    try{
        return InetAddress.getByName(AppRandomNumber.nextInt(224, 239) + "." + AppRandomNumber.nextInt(0, 255) + "." + AppRandomNumber.nextInt(0, 255) + "." + AppRandomNumber.nextInt(0, 255));
    } catch(Throwable e){

DebugException.start().log(e).end();

        return null;
    }
}

public static void receive(InputStream source, OutputStream destination, int available) throws IOException{
    receive(source, destination, available, DEFAULT_BUFFER_SIZE);
}
public static void receive(InputStream source, OutputStream destination, int available, int bufferSize) throws IOException{
    int length;
    byte[] buffer = UtilsBytes.obtain(bufferSize);
    while((length = source.read(buffer, 0, Math.min(available, bufferSize))) > 0){
        destination.write(buffer, 0, length);
        available -= length;
    }
    destination.flush();
    if(available != 0){
        throw new IOException("Did not receive all bytes");
    }
}

}
