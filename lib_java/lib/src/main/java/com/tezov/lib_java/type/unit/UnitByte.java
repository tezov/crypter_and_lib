/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.type.unit;

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
public enum UnitByte implements defEnumUnit<UnitByte>{
    o(1L){
        @Override
        public long convert(long value){
            return value;
        }

        @Override
        public long convert(long value, UnitByte unit){
            return value * unit.value;
        }
    }, Ko(1024L), Mo(Ko.value * Ko.value), Go(Ko.value * Ko.value * Ko.value);
public final static String SEPARATOR = ".";
long value;

UnitByte(long value){
    this.value = value;
}
public static UnitByte find(int value){
    UnitByte[] values = values();
    for(UnitByte u: values){
        if(u.value == value){
            return u;
        }
    }
    return null;
}
public long getValue(){
    return value;
}
@Override
public String getSeparator(){
    return SEPARATOR;
}
@Override
public long convert(long value){
    return value / this.value;
}
@Override
public long convert(long value, UnitByte unit){
    return (value * unit.value) / this.value;
}
@Override
public float convert(float value){
    return value / this.value;
}
@Override
public float convert(float value, UnitByte unit){
    return (value * unit.value) / this.value;
}

}
