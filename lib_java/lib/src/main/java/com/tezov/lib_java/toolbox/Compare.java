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
import java.util.Arrays;

import com.tezov.lib_java.type.ref.Ref;

import java.util.Collection;
import java.util.Map;

public class Compare{
private Compare(){
}

public static boolean isTrue(Boolean b){
    return (b != null) && b;
}
public static boolean isFalse(Boolean b){
    return (b != null) && !b;
}
public static boolean isTrueOrNull(Boolean b){
    return !isFalse(b);
}
public static boolean isFalseOrNull(Boolean b){
    return !isTrue(b);
}

public static boolean equals(Ref r1, Object o2){
    return Ref.equals(r1, o2);
}
public static boolean equals(Object o1, Ref r2){
    return Ref.equals(o1, r2);
}
public static boolean equals(Ref r1, Ref r2){
    return Ref.equals(r1, r2);
}
public static boolean equals(Object o1, Object o2){
    if(o1 != null){
        return o1.equals(o2);
    } else {
        return (o2 == null);
    }
}
public static boolean equals(byte[] o1, byte[] o2){
    return Arrays.equals(o1, o2);
}
public static boolean equals(char[] o1, char[] o2){
    return Arrays.equals(o1, o2);
}

public static boolean equalsAndNotNull(Ref r1, Object o2){
    return (r1 != null) && Ref.equals(r1, o2);
}
public static boolean equalsAndNotNull(Object o1, Ref r2){
    return (o1 != null) && Ref.equals(o1, r2);
}
public static boolean equalsAndNotNull(Ref r1, Ref r2){
    return (r1 != null) && Ref.equals(r1, r2);
}
public static boolean equalsAndNotNull(Object o1, Object o2){
    return (o1 != null) && o1.equals(o2);
}
public static boolean equalsAndNotNull(byte[] o1, byte[] o2){
    return (o1 != null) && Arrays.equals(o1, o2);
}
public static boolean equalsAndNotNull(char[] o1, char[] o2){
    return (o1 != null) && Arrays.equals(o1, o2);
}

public static boolean equalsOrNull(Ref r1, Object o2){
    return (r1 == null) || Ref.equals(r1, o2);
}
public static boolean equalsOrNull(Object o1, Ref r2){
    return (o1 == null) || Ref.equals(o1, r2);
}
public static boolean equalsOrNull(Ref r1, Ref r2){
    return (r1 == null) || Ref.equals(r1, r2);
}
public static boolean equalsOrNull(Object o1, Object o2){
    return (o1 == null) || o1.equals(o2);
}
public static boolean equalsOrNull(byte[] o1, byte[] o2){
    return (o1 == null) || Arrays.equals(o1, o2);
}
public static boolean equalsOrNull(char[] o1, char[] o2){
    return (o1 == null) || Arrays.equals(o1, o2);
}

public static boolean isNull(Boolean b){
    return b == null;
}
public static boolean isNull(String s){
    return Nullify.string(s) == null;
}
public static boolean isNull(Collection l){
    return Nullify.collection(l) == null;
}
public static boolean isNull(Map m){
    return Nullify.map(m) == null;
}


}
