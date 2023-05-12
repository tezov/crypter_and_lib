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
import com.tezov.lib_java.application.AppRandomNumber;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java.debug.DebugTrack;


public class PerturbedInt{
private final String name;
private int base;
private float perturbationRatio = 0.0f;

private Integer minCommand = null;
private Integer maxCommand = null;
private Integer command = null;

public PerturbedInt(String name, int base){
DebugTrack.start().create(this).end();
    this.name = name;
    this.base = base;
}

public PerturbedInt setBase(int value){
    this.base = value;
    return this;
}

public PerturbedInt setPerturbationRatio(float value){
    this.perturbationRatio = value;
    return this;
}

public PerturbedInt setMinCommand(Integer value){
    this.minCommand = value;
    return this;
}

public PerturbedInt setMaxCommand(Integer value){
    this.maxCommand = value;
    return this;
}

public Integer getCommand(){
    return command;
}

public PerturbedInt update(){
    int perturbation;
    if(perturbationRatio != 0.0f){
        perturbation = AppRandomNumber.nextInt((int)(base * perturbationRatio));
        if(AppRandomNumber.nextFlip()){
            perturbation = -perturbation;
        }
    } else {
        perturbation = 0;
    }
    command = base + perturbation;
    if((minCommand != null) && (command < minCommand)){
        command = minCommand;
    } else {
        if((maxCommand != null) && (command > maxCommand)){
            command = maxCommand;
        }
    }
    return this;
}

public DebugString toDebugString(){
    DebugString data = new DebugString();
    data.append("name", name);
    data.append("base", base);
    data.append("perturbationRatio", perturbationRatio);
    data.append("minCommand", minCommand);
    data.append("maxCommand", maxCommand);
    data.append("command", command);
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
