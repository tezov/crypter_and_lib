/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.type.collection;

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
import com.tezov.lib_java.wrapperAnonymous.SupplierW;

import java.util.Iterator;

public class Snapper<TYPE>{
private final BufferQueue<TYPE> snapBuffer;
private final SupplierW<TYPE> supplier;

public Snapper(SupplierW<TYPE> supplier){
    this(supplier, 0);
}

public Snapper(SupplierW<TYPE> supplier, int maxBitmapBufferSize){
DebugTrack.start().create(this).end();
    this.supplier = supplier;
    snapBuffer = new BufferQueue<>(maxBitmapBufferSize);
}

public void setMaxSize(int maxSize){
    snapBuffer.setMaxSize(maxSize);
}

public TYPE poll(){
    return snapBuffer.poll();
}

public TYPE peek(){
    return snapBuffer.peek();
}

public TYPE get(int index){
    return snapBuffer.get(index);
}

public boolean isEmpty(){
    return snapBuffer.isEmpty();
}

public boolean isFull(){
    return snapBuffer.isFull();
}

public int size(){
    return snapBuffer.size();
}

public Iterator<TYPE> iterator(){
    return snapBuffer.iterator();
}

public TYPE snap(){
    TYPE t = supplier.get();
    if(t != null){
        snapBuffer.offer(t);
        return t;
    } else {
        return null;
    }
}

public void clear(){
    snapBuffer.clear();
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

}
