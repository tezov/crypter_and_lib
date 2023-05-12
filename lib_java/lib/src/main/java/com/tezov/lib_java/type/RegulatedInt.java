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


public class RegulatedInt{
private final String name;
private int base;
private int target;
private boolean negate = false;
private float gain;
private float integral;
private float perturbationRatio = 0.0f;

private Integer minCommand = null;
private Integer maxCommand = null;
private Float command = null;
private long errorSum = 0;

private Integer deadMinTarget = null;
private Integer deadMaxTarget = null;

public RegulatedInt(String name, int base, int target, float gain, float integral){
DebugTrack.start().create(this).end();
    this.name = name;
    this.base = base;
    this.target = target;
    this.gain = gain;
    this.integral = integral;
}

public RegulatedInt setBase(int value){
    this.base = value;
    return this;
}

public RegulatedInt setTarget(int value){
    this.target = value;
    return this;
}

public RegulatedInt negate(boolean flag){
    this.negate = flag;
    return this;
}

public RegulatedInt setGain(float value){
    this.gain = value;
    return this;
}

public RegulatedInt setIntegral(float value){
    this.integral = value;
    return this;
}

public RegulatedInt setPerturbationRatio(float value){
    this.perturbationRatio = value;
    return this;
}

public RegulatedInt setMinCommand(Integer value){
    this.minCommand = value;
    return this;
}

public RegulatedInt setMaxCommand(Integer value){
    this.maxCommand = value;
    return this;
}

public RegulatedInt setDeadMinTarget(Integer value){
    this.deadMinTarget = value;
    return this;
}

public RegulatedInt setDeadMaxTarget(Integer value){
    this.deadMaxTarget = value;
    return this;
}

public Integer getCommand(){
    return command.intValue();
}

public RegulatedInt update(){
    return update(target);
}

public RegulatedInt update(int current){
    command = (float)(base);
    //PERTURBATION
    int perturbation;
    if(perturbationRatio != 0.0f){
        perturbation = AppRandomNumber.nextInt((int)(base * perturbationRatio));
        if(AppRandomNumber.nextFlip()){
            perturbation = -perturbation;
        }
    } else {
        perturbation = 0;
    }
    command += (float)(perturbation);

    //GAIN WITH DEAD ZONE
    if(((deadMinTarget == null) || (current < deadMinTarget)) || ((deadMaxTarget == null) || (current > deadMaxTarget))){
        int error = current - target;
        float correctionGain = error * gain;
        if(negate){
            correctionGain = -correctionGain;
        }
        command += correctionGain;

        errorSum += error;
    }

    //INTEGRAL
    float correctionIntegral = errorSum * integral;
    if(negate){
        correctionIntegral = -correctionIntegral;
    }
    command += correctionIntegral;

    //BOUNDARIES
    if((minCommand != null) && (command < minCommand)){
        command = Float.valueOf(minCommand);
    } else {
        if((maxCommand != null) && (command > maxCommand)){
            command = Float.valueOf(maxCommand);
        }
    }
    return this;
}

public DebugString toDebugString(){
    DebugString data = new DebugString();
    data.append("name", name);
    data.append("base", base);
    data.append("target", target);
    data.append("negate", negate);
    data.append("gain", gain);
    data.append("integral", integral);
    data.append("perturbationRatio", perturbationRatio);
    data.append("minCommand", minCommand);
    data.append("maxCommand", maxCommand);
    data.append("command", command);
    data.append("errorSum", errorSum);
    data.append("deadMinTarget", deadMinTarget);
    data.append("deadMaxTarget", deadMaxTarget);
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
