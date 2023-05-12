/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.type;

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
import java.util.Collection;

public class Extrema{
private Extrema(){
}

public static <T extends Comparable> Value<T> find(Collection<T> list){
    if((list == null) || (list.size() <= 0)){
        return null;
    }
    T min = null;
    T max = null;
    for(T t: list){
        if((min == null) && (max == null)){
            min = t;
            max = t;
            continue;
        }
        if(t.compareTo(min) <= -1){
            min = t;
        }
        if(t.compareTo(max) >= 1){
            max = t;
        }
    }
    return new Value<>(min, max);
}

public static class Value<T>{
    public T min;
    public T max;

    public Value(T min, T max){
        this.min = min;
        this.max = max;
    }

}

}
