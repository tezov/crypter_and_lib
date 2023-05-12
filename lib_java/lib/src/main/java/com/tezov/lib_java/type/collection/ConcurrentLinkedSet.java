/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.type.collection;

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.util.UtilsString;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java.debug.DebugException;

import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedDeque;

public class ConcurrentLinkedSet<V> extends ConcurrentLinkedDeque<V>{
@Override
public boolean add(V v){
    for(V vIn: this){
        if(vIn.equals(v)){

DebugException.start().log("Value already exist in collection").end();

            return false;
        }
    }
    return super.add(v);
}

@Override
public boolean remove(Object o){
    return super.remove(o);
}

@Override
public Iterator<V> iterator(){
    return super.iterator();
}

}
