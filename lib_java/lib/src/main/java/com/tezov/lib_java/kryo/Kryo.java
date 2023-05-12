/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.kryo;

import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.util.UtilsString;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugTrack;


public class Kryo extends com.esotericsoftware.kryo.Kryo{
private static final int BUFFER_MIN = 1024;
private static final int BUFFER_MAX = BUFFER_MIN * 10;

public Kryo(){
    setAutoReset(true);
}

@Override
public <T> T copy(T object){
    if(object == null){
        return null;
    }
    if(object.getClass().isAnonymousClass()){
        return null;
    }

DebugLog.start().send(this, "Copy of " + DebugTrack.getFullSimpleName(object)).end();

    try{
        return super.copy(object);
    } catch(java.lang.Throwable e){

DebugException.start().logHidden(e).end();

        return null;
    }
}

public byte[] toBytes(Object o){
    byte[] bytes;
    try(Output output = new Output(BUFFER_MIN, BUFFER_MAX)){
        writeObject(output, o);
        bytes = output.toBytes();
    } catch(KryoException e){
        String message = e.getMessage();

        if((message != null) && ((message.contains("Buffer overflow")) || (message.contains("ConcurrentModificationException")))){
DebugException.start().logHidden(e).end();
        } else {
DebugException.start().log(e).end();
        }


        bytes = null;
    }
    return bytes;
}

public <T> T fromBytes(byte[] b, Class type){
    Object obj = null;
    try(Input input = new Input(b)){
        obj = readObject(input, type);
    } catch(Throwable e){

DebugException.start().log(e).end();

    }
    return (T)obj;
}

}
