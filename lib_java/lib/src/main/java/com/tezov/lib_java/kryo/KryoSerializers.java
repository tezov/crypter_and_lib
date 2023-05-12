/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.kryo;

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
import com.tezov.lib_java.generator.uid.UUID;
import com.tezov.lib_java.type.defEnum.Event;

import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.LocalTime;

public class KryoSerializers{
public void add(Kryo kryo){
    kryo.addDefaultSerializer(LocalDateTime.class, new SerializerLocalDateTime());
    kryo.addDefaultSerializer(LocalDate.class, new SerializerLocalDate());
    kryo.addDefaultSerializer(LocalTime.class, new SerializerLocalTime());
    kryo.addDefaultSerializer(UUID.class, new SerializerUUID());
    kryo.addDefaultSerializer(Event.Is.class, new SerializerEnumBaseIs());
}

}
