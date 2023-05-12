/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.application;

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
import com.tezov.lib_java.kryo.Kryo;
import com.tezov.lib_java.kryo.KryoSerializers;
import com.tezov.lib_java.debug.DebugException;

final public class AppKryo{
static private KryoSerializers serializers = null;
static private final ThreadLocal<Kryo> kryos = new ThreadLocal<Kryo>(){
    @Override
    protected Kryo initialValue(){
        Kryo kryo = new Kryo();
        if(serializers == null){
            serializers = new KryoSerializers();
        }
        serializers.add(kryo);
        return kryo;
    }
};

private AppKryo(){
}

private static Kryo kryo(){
    return kryos.get();
}

public static <T> T copy(T object){
    return kryo().copy(object);
}

public static byte[] toBytes(Object o){
    return kryo().toBytes(o);
}

public static <T> T fromBytes(byte[] b, Class type){
    return kryo().fromBytes(b, type);
}

synchronized public void setSerializers(KryoSerializers serializers){

    if(serializers != null){
DebugException.start().explode("Must be set before any use of com.tezov.lib.kryo. OnCreate Application for example").end();
    }

    AppKryo.serializers = serializers;
}

}
