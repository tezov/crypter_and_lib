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
import com.tezov.lib_java.util.UtilsString;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.debug.DebugTrack;

import org.threeten.bp.LocalDate;

public class SerializerLocalDate extends Serializer<LocalDate>{
public SerializerLocalDate(){
DebugTrack.start().create(this).end();
}

@Override
public void write(Kryo kryo, Output output, LocalDate date){
    output.writeLong(Clock.DateTo.MilliSecond.with(date));
}

@Override
public LocalDate read(Kryo kryo, Input input, Class<LocalDate> type){
    return Clock.MilliSecondTo.Date.with(input.readLong());
}

@Override
public LocalDate copy(Kryo kryo, LocalDate original){
    long timeStamp = Clock.DateTo.MilliSecond.with(original);
    return Clock.MilliSecondTo.Date.with(timeStamp);
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

}
