/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.generator;

import com.tezov.lib_java.debug.DebugLog;
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
import com.tezov.lib_java.debug.DebugTrack;

public class NumberGenerator{
private long last;

public NumberGenerator(long first){
DebugTrack.start().create(this).end();
    last = first;
}

public NumberGenerator(){
    this(0);
}

synchronized public long nextLong(){
    synchronized(this){
        return ++last;
    }
}

synchronized public int nextInt(){
    synchronized(this){
        ++last;
        if(last > Integer.MAX_VALUE){
            last = 1;
            return 1;
        }
        return (int)last;
    }
}

synchronized public long getLast(){
    synchronized(this){
        return last;
    }
}

synchronized public void setLast(long last){
    synchronized(this){
        this.last = last;
    }
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

}
