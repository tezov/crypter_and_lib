/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.kryo;

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
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.tezov.lib_java.generator.uid.UUID;
import com.tezov.lib_java.debug.DebugTrack;

public class SerializerUUID extends Serializer<UUID>{
public SerializerUUID(){
DebugTrack.start().create(this).end();
}

@Override
public void write(Kryo kryo, Output output, UUID uid){
    byte[] bytes = uid.toBytes();
    if(bytes == null){
        output.writeInt(-1);
    } else {
        output.writeInt(bytes.length);
        output.writeBytes(bytes);
    }
}

@Override
public UUID read(Kryo kryo, Input input, Class<UUID> type){
    int length = input.readInt();
    if(length != -1){
        byte[] bytes = input.readBytes(length);
        return UUID.fromBytes(bytes);
    } else {
        return null;
    }
}

@Override
public UUID copy(Kryo kryo, UUID original){
    return UUID.fromUID(original.getUUID());
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

}
