/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.toolbox;

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
import com.tezov.lib_java.generator.uid.defUid;
import com.tezov.lib_java.type.defEnum.EnumBase;

public enum CompareType{
    // immutable
    LONG(long.class, Long.class), INT(int.class, Integer.class), SHORT(short.class, Short.class), FLOAT(float.class, Float.class), DOUBLE(double.class, Double.class), BOOLEAN(boolean.class,
            Boolean.class), CHAR(char.class, Character.class), STRING(null, String.class), BYTE(byte.class, Byte.class), VOID(void.class, Void.class), // immutable implemented
    UID(null, defUid.class), // immutable extended
    ENUM_BASE(null, EnumBase.Is.class), // mutable
    BYTES(byte[].class, null);
private final static int IMMUTABLE_NUMBER = 10;
private final static int IMMUTABLE_IMPLEMENTED_NUMBER = 1;
private final static int IMMUTABLE_EXTENDED_NUMBER = 1;
Class type;
Class typeBox;

CompareType(Class type, Class typeBox){
    this.type = type;
    this.typeBox = typeBox;
}

public static boolean isImmutable(Class typeToCompare){
    if(typeToCompare.isPrimitive()){
        return true;
    }
    CompareType[] compareTypes = values();
    int i = 0;
    for(; i < IMMUTABLE_NUMBER; i++){
        if((typeToCompare == compareTypes[i].type) || (typeToCompare == compareTypes[i].typeBox)){
            return true;
        }
    }
    for(int end = (i + IMMUTABLE_IMPLEMENTED_NUMBER); i < end; i++){
        if(Reflection.hasInterface(typeToCompare, compareTypes[i].typeBox)){
            return true;
        }
    }
    for(int end = (i + IMMUTABLE_EXTENDED_NUMBER); i < end; i++){
        if(Reflection.hasSuperClass(typeToCompare, compareTypes[i].typeBox)){
            return true;
        }
    }
    return false;
}

public boolean equal(Class typeToCompare){
    if(this == UID){
        return Reflection.hasInterface(typeToCompare, typeBox);
    } else if(this == ENUM_BASE){
        return Reflection.hasSuperClass(typeToCompare, typeBox);
    } else {
        return (typeToCompare == type) || (typeToCompare == typeBox);
    }
}

public boolean equal(String typeToCompare){
    if((typeBox != null) && (typeToCompare.equals(typeBox.getName()))){
        return true;
    } else {
        return (type != null) && (typeToCompare.equals(type.getName()));
    }
}
}
