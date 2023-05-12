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
import com.tezov.lib_java.util.UtilsString;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java.debug.DebugTrack;

public class Transient<T extends Comparable>{
private T lastValid;
private T last;
private Long timeValidated;
private Long timeLastMaintained;
private Long delayToValidateLastInMillisecond = 0L;
private Long minMaintainedTimeToValidateLastInMillisecond = 10L;

private boolean changed = false;
private boolean rising = false;
private boolean falling = false;

protected Transient(T t){
DebugTrack.start().create(this).end();
    last = t;
    lastValid = t;
    timeValidated = Clock.MilliSecond.now();
    timeLastMaintained = timeValidated;
}

public static <T extends Comparable> Transient<T> newInstance(T t){
    return new Transient<>(t);
}

public Transient<T> delayToValidateLastInMillisecond(Long l){
    delayToValidateLastInMillisecond = l;
    return this;
}

public Transient<T> minMaintainedTimeToValidateLastInMillisecond(Long l){
    minMaintainedTimeToValidateLastInMillisecond = l;
    return this;
}

public T getLastValid(){
    return lastValid;
}

public T getLast(){
    return last;
}

protected void reset(){
    changed = false;
    rising = false;
    falling = false;
}

public Transient<T> update(T t){
    if((t.equals(last)) && (Clock.MilliSecond.now() - timeLastMaintained) > minMaintainedTimeToValidateLastInMillisecond){
        last = t;
    } else {
        timeLastMaintained = Clock.MilliSecond.now();
        return this;
    }
    if((!last.equals(lastValid)) && ((Clock.MilliSecond.now() - timeValidated) > delayToValidateLastInMillisecond)){
        changed = true;
        int result = last.compareTo(lastValid);
        if(result > 0){
            rising = true;
        } else {
            if(result < 0){
                falling = true;
            }
        }
        lastValid = last;
        timeValidated = Clock.MilliSecond.now();
    }
    return this;
}

public boolean hasChanged(){
    boolean temp = changed;
    reset();
    return temp;
}

public boolean hasRose(){
    boolean temp = rising;
    reset();
    return temp;
}

public boolean hasFell(){
    boolean temp = falling;
    reset();
    return temp;
}

public DebugString toDebugString(){
    DebugString data = new DebugString();
    data.append("lastValid", lastValid);
    data.append("last", last);
    data.append("timeValidated", timeValidated);
    data.append("timeLastMaintained", timeLastMaintained);
    data.append("delayToValidateLastInMillisecond", delayToValidateLastInMillisecond);
    data.append("minMaintainedTimeToValidateLastInMillisecond", minMaintainedTimeToValidateLastInMillisecond);
    data.append("changed", changed);
    data.append("rising", rising);
    data.append("falling", falling);
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
