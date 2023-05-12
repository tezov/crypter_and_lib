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
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java.debug.DebugTrack;

public class NormInt{
private int base;
private boolean revert = false;
private boolean negate = false;

public NormInt(){
    this(0);
}

public NormInt(int base){
DebugTrack.start().create(this).end();
    this.base = base;
}

public boolean isBaseNotNull(){
    return base != 0;
}

public int getBase(){
    return base;
}

public NormInt setBase(int base){
    this.base = base;
    return this;
}

public NormInt revert(boolean flag){
    this.revert = flag;
    return this;
}

public NormInt negate(boolean flag){
    this.negate = flag;
    return this;
}

public float getNorm(int raw){
    if(negate){
        raw = Math.abs(raw);
    }
    if(!revert){
        return ((float)raw) / ((float)base);
    } else {
        return 1.0f - ((float)raw) / ((float)base);
    }
}

public int getRaw(float norm){
    int raw;
    if(!revert){
        raw = (int)(base * norm);
    } else {
        raw = (int)(base * (1.0f - norm));
    }
    if(!negate){
        return raw;
    } else {
        return raw * -1;
    }
}

public DebugString toDebugString(){
    DebugString data = new DebugString();
    data.append("base", base);
    data.append("revert", revert);
    data.append("negate", negate);
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
