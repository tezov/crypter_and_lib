/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.cipher.definition;

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
import com.tezov.lib_java.buffer.ByteBufferPacker;
import com.tezov.lib_java.cipher.dataInput.MacSigner;

import java.io.OutputStream;

public interface defSigner{

byte[] getIv();
defSigner setIv(byte[] iv);
defSigner setRandomIv();
defSigner setPacker(ByteBufferPacker packer);

default byte[] sign(byte[] bytes){
    if(bytes == null){
        return null;
    } else {
        return sign(bytes, 0, bytes.length);
    }
}
byte[] sign(byte[] data, int offset, int length);
default byte[] sign(byte[] bytes, byte[] iv){
    if(bytes == null){
        return null;
    } else {
        return sign(bytes, 0, bytes.length, iv);
    }
}
byte[] sign(byte[] bytes, int offset, int length, byte[] iv);

byte[] sign(String data);
byte[] sign(String data, byte[] iv);
String signToString(String data);
String signToString(String data, byte[] iv);

MacSigner.MacOutputStream sign(OutputStream out);


}
