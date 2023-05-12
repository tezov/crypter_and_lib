/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.type;

import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.util.UtilsString;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.wrapperAnonymous.BiFunctionW;
import com.tezov.lib_java.wrapperAnonymous.FunctionW;

import java.util.List;

public class ExtremaAverage<T extends Comparable<T>>{
private T min = null;
private T max = null;
private T total = null;
private T average = null;

public ExtremaAverage(List<T> list, BiFunctionW<T, T, T> addition, FunctionW<T, T> average){
DebugTrack.start().create(this).end();
    if((list == null) || (list.size() <= 0)){
        return;
    }
    for(T t: list){
        total = addition.apply(total, t);
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
    this.average = average.apply(total);
}

public T getMin(){
    return min;
}

public T getMax(){
    return max;
}

public T getTotal(){
    return total;
}

public T getAverage(){
    return average;
}

public DebugString toDebugString(){
    DebugString data = new DebugString();
    data.append("min", min);
    data.append("max", max);
    data.append("total", total);
    data.append("average", average);
    return data;
}

final public void toDebugLog(){
DebugLog.start().send(toDebugString()).end();
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

}
