/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.generator.uid;

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
import com.tezov.lib_java.generator.NumberGenerator;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.primitive.BytesTo;
import com.tezov.lib_java.type.primitive.string.StringHexTo;

public class UIDGenerator implements defUIDGenerator{
protected NumberGenerator numberGenerator;

protected UIDGenerator(){
    trackClassCreate();
    this.numberGenerator = new NumberGenerator();
}

protected UIDGenerator(long firstUID){
    trackClassCreate();
    this.numberGenerator = new NumberGenerator(firstUID);
}

public static UIDGenerator newInstance(){
    return new UIDGenerator();
}

public static UIDGenerator newInstance(long firstUID){
    return new UIDGenerator(firstUID);
}

public static UIDGenerator newInstance(byte[] bytesFirstUID){
    return new UIDGenerator(BytesTo.Long(bytesFirstUID));
}

public static UIDGenerator newInstance(String hexStringFirstUID){
    return newInstance(StringHexTo.Bytes(hexStringFirstUID));
}

private void trackClassCreate(){
DebugTrack.start().create(this).end();
}

@Override
public Uid next(){
    synchronized(this){
        return Uid.newInstance(numberGenerator.nextLong());
    }
}

@Override
public Uid make(byte[] data){
    synchronized(this){
        return Uid.fromBytes(data);
    }
}

@Override
public Uid make(String data){
    synchronized(this){
        return Uid.fromHexString(data);
    }
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

}
