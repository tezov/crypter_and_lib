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
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java.toolbox.Compare;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java.debug.DebugTrack;

import java.text.DecimalFormatSymbols;

public class RangeInt{
private final Integer min;
private final boolean minInclude;
private final Integer max;
private final boolean maxInclude;

public RangeInt(Integer min, Integer max){
    this(min, min != null, max, max != null);
}

public RangeInt(Integer min, boolean minInclude, Integer max, boolean maxInclude){
DebugTrack.start().create(this).end();
    this.min = min;
    this.minInclude = minInclude;
    this.max = max;
    this.maxInclude = maxInclude;
}

public Integer getMin(){
    return min;
}
public Integer getMax(){
    return max;
}

public boolean isMinInclude(){
    return minInclude;
}
public boolean isMaxInclude(){
    return maxInclude;
}
public boolean isInside(int value){
    boolean minCheck = (min == null) || (minInclude && (min <= value)) || (!minInclude && (min < value));
    boolean maxCheck = (max == null) || (maxInclude && (max >= value)) || (!maxInclude && (max > value));
    return minCheck && maxCheck;
}

public int length(){
    int length = (max - min) + 1;
    if(!minInclude){
        length-=1;
    }
    if(!maxInclude && (length>0)){
        length-=1;
    }
    return length;
}

@Override
public boolean equals(Object obj){
    if(!(obj instanceof RangeInt)){
        return false;
    }
    RangeInt second = (RangeInt)obj;
    return Compare.equals(this.min, second.min) && Compare.equals(this.max, second.max);
}

@Override
public String toString(){
    String infinity = DecimalFormatSymbols.getInstance().getInfinity();
    return (minInclude ? "[" : "]") + (min != null ? min : infinity) + "," + (max != null ? max : infinity) + (maxInclude ? "]" : "[");
}

public DebugString toDebugString(){
    DebugString data = new DebugString();
    data.append("minInclude", minInclude);
    data.append("min", min);
    data.append("maxInclude", maxInclude);
    data.append("max", max);
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
