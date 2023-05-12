/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.type.primaire;

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

public class Scale{
private float wS;
private float hS;

public Scale(){
    this(0, 0);
}

public Scale(float wS, float hS){
DebugTrack.start().create(this).end();
    this.wS = wS;
    this.hS = hS;
}

public static Scale wrap(android.util.Size size){
    if(size == null){
        return null;
    } else {
        return new Scale(size.getWidth(), size.getHeight());
    }
}

public float getW(){
    return wS;
}

public Scale setW(float wR){
    this.wS = wR;
    return this;
}

public float getH(){
    return hS;
}

public Scale setH(float hR){
    this.hS = hR;
    return this;
}

public DebugString toDebugString(){
    DebugString data = new DebugString();
    data.append("wS", wS);
    data.append("hS", hS);
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
