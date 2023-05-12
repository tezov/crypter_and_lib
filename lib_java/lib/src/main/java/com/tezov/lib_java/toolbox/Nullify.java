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
import java.util.Collection;
import java.util.Map;

public class Nullify{
private Nullify(){
}

public static <I, C extends Collection<I>> C collection(C c){
    if((c == null) || (c.size() == 0)){
        return null;
    }
    else {
        return c;
    }
}
public static <K, V, M extends Map<K, V>> M map(M m){
    if((m == null) || (m.size() == 0)){
        return null;
    }
    else {
        return m;
    }
}
public static String string(String s){
    if((s == null) || s.equals("")){
        return null;
    } else {
        return s;
    }
}
public static String string(CharSequence s){
    if((s == null) || s.toString().equals("")){
        return null;
    }
    else {
        return s.toString();
    }
}
public static byte[] array(byte[] b){
    if(b != null){
        Arrays.fill(b, (byte)0);
    }
    return null;
}
public static char[] array(char[] c){
    if(c != null){
        Arrays.fill(c, (char)0);
    }
    return null;
}

}
