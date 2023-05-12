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
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java.util.UtilsString;

public enum UnitLiter implements defEnumUnit<UnitLiter>{
    ml(1L){
        @Override
        public long convert(long value){
            return value;
        }

        @Override
        public long convert(long value, UnitLiter unit){
            return value * unit.value;
        }
    }, cl(10L), dl(100L), L(1000L);
public final static String SEPARATOR = UtilsString.NUMBER_SEPARATOR;
long value;

UnitLiter(long value){
    this.value = value;
}
public static UnitLiter find(int value){
    UnitLiter[] values = values();
    for(UnitLiter u: values){
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
public long convert(long value, UnitLiter unit){
    return (value * unit.value) / this.value;
}
@Override
public float convert(float value){
    return value / this.value;
}
@Override
public float convert(float value, UnitLiter unit){
    return (value * unit.value) / this.value;
}

}
