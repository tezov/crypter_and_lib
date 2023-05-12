/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.type.collection;

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
import com.tezov.lib_java.type.primaire.Entry;

import java.util.Iterator;

public class Arguments<KEY> implements Iterable<Entry<KEY, Object>>{
private final ListEntry<KEY, Object> args;

public Arguments(){
DebugTrack.start().create(this).end();
    args = new ListEntry<KEY, Object>(ListOrObject::new);
}

public Arguments<KEY> put(KEY key, Object argument){
    args.put(key, argument);
    return this;
}
public Arguments<KEY> clear(){
    args.clear();
    return this;
}

public <T> T getValue(KEY key){
    return (T)args.getValue(key);
}
public <T> T removeKey(KEY key){
    return (T)args.removeKey(key);
}

public DebugString toDebugString(){
    DebugString data = new DebugString();
    for(Entry<KEY, Object> entry: args){
        data.append(entry.key, entry.value);
    }
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

@Override
public Iterator<Entry<KEY, Object>> iterator(){
    return args.iterator();
}



}
