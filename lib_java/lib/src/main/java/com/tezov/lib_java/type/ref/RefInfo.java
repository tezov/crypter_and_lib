/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.type.ref;

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

public class RefInfo<T>{
protected Class<T> type;
protected int hashcode;

public RefInfo(T ref){
DebugTrack.start().create(this).end();
    if(ref instanceof Class){
        type = (Class<T>)ref;
    } else {
        type = (Class<T>)ref.getClass();
    }
    hashcode = ref.hashCode();
}

public Class<T> getType(){
    return type;
}

public boolean isTypeEqual(Object object){
    return this.type == object.getClass();
}

public boolean isTypeEqual(Class<T> type){
    return this.type == type;
}

public boolean isTypeNotEqual(Object object){
    return !isTypeEqual(object);
}

public boolean isTypeNotEqual(Class<T> type){
    return !isTypeEqual(type);
}

public boolean isTypeNotEqual(String trackClassFullName){
    return !isTypeEqual(trackClassFullName);
}

@Override
public int hashCode(){
    return hashcode;
}

public String hashCodeString(){
    return Integer.toHexString(hashcode);
}

@Override
public boolean equals(Object obj){
    if(obj == null){
        return false;
    } else {
        if(obj instanceof RefInfo){
            return hashcode == ((RefInfo)obj).hashcode;
        } else {
            return hashcode == obj.hashCode();
        }
    }
}

public DebugString toDebugString(){
    DebugString data = new DebugString();
    data.append("com/tezov/lib/type", DebugTrack.getFullSimpleName(getType()));
    data.append("hashcode", Integer.toHexString(hashcode));
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
