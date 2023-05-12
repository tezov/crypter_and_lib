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
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.util.UtilsString;
import java.util.Set;
import com.tezov.lib_java.wrapperAnonymous.SupplierW;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class BufferQueue<E> implements java.util.Queue<E>{
private List<E> list;
private int maxSize;

public BufferQueue(int maxSize){
    this(LinkedList::new, maxSize);
}
public BufferQueue(SupplierW<List<E>> supplier, int maxSize){
    this.list = supplier.get();
    this.maxSize = maxSize;
}

public void setMaxSize(int maxSize){
    this.maxSize = maxSize;
    reduce();
}
private void reduce(){
    int overSize = (list.size() - maxSize);
    if(overSize > 0){
        list = list.subList(overSize, (list.size() - 1));
    }
}

@Override
public int size(){
    return list.size();
}

@Override
public boolean isEmpty(){
    return list.isEmpty();
}

public boolean isFull(){
    return list.size() >= maxSize;
}

@Override
public boolean contains(Object o){
    return list.contains(o);
}

@Override
public Iterator<E> iterator(){
    return list.iterator();
}

@Override
public Object[] toArray(){
    return list.toArray();
}

@Override
public <T> T[] toArray(T[] a){
    return list.toArray(a);
}

@Override
public boolean add(E e){
    if(list.size() >= maxSize){
        remove();
    }
    return list.add(e);
}

@Override
public boolean remove(Object o){
    return list.remove(o);
}

@Override
public boolean containsAll(Collection<?> c){
    return list.containsAll(c);
}

@Override
public boolean addAll(Collection<? extends E> c){
    boolean result = list.addAll(c);
    reduce();
    return result;
}

@Override
public boolean removeAll(Collection<?> c){
    return list.removeAll(c);
}

@Override
public boolean retainAll(Collection<?> c){
    return list.retainAll(c);
}

@Override
public void clear(){
    list.clear();
}

@Override
public boolean offer(E e){
    if(list.size() >= maxSize){
        list.remove(0);
    }
    return list.add(e);
}

@Override
public E remove(){
    return list.remove(0);
}

@Override
public E poll(){
    if(list.isEmpty()){
        return null;
    }
    return list.remove(0);
}

@Override
public E element(){
    return list.get(0);
}

@Override
public E peek(){
    if(list.isEmpty()){
        return null;
    }
    return list.get(0);
}

public E get(int index){
    return list.get(index);
}

}
