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

public class Field{
private Integer index;
private String name;
private Class type;

public Field(Class type, String name){
DebugTrack.start().create(this).end();
    this.type = type;
    this.name = name;
    this.index = null;
}

public Integer getIndex(){
    return index;
}

public void setIndex(Integer index){
    this.index = index;
}

public String getName(){
    return name;
}

public void setName(String name){
    this.name = name;
}

public Class getType(){
    return type;
}

public void setType(Class type){
    this.type = type;
}

public DebugString toDebugString(){
    DebugString data = new DebugString();
    data.append("index", index);
    data.append("name", name);
    data.appendFullSimpleName("type", type);
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
