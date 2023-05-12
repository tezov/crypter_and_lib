/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.type.collection;

import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.util.UtilsString;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java.debug.DebugException;

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.wrapperAnonymous.FunctionW;
import com.tezov.lib_java.wrapperAnonymous.PredicateW;
import com.tezov.lib_java.wrapperAnonymous.SupplierW;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import static com.tezov.lib_java.util.UtilsList.NULL_INDEX;

public class ListKey<KEY, VALUE> implements List<VALUE>{
private final List<VALUE> list;
private final FunctionW<VALUE, KEY> getKeyFunction;

public ListKey(FunctionW<VALUE, KEY> getKey){
    this(ArrayList::new, getKey);
}
public ListKey(SupplierW<List<VALUE>> supplier, FunctionW<VALUE, KEY> getKey){
    this(supplier.get(), getKey);
}
private ListKey(List<VALUE> list, FunctionW<VALUE, KEY> getKey){
DebugTrack.start().create(this).end();
    this.getKeyFunction = getKey;
    this.list = list;
}

private KEY getKey(VALUE v){
    return getKeyFunction.apply(v);
}

public boolean hasKey(KEY key){
    return getValue(key) != null;
}

public boolean hasValue(VALUE value){
    return hasKey(getKeyFunction.apply(value));
}

public boolean isUnique(KEY key){
    boolean found = false;
    for(VALUE v: list){
        if(key.equals(getKey(v))){
            if(found){
                return false;
            }
            found = true;
        }
    }
    return true;
}

@Override
public boolean isEmpty(){
    return list.isEmpty();
}

@Override
public int size(){
    return list.size();
}

@Override
public int indexOf(Object value){
    Iterator<VALUE> iterator = iterator();
    for(int i = 0; iterator.hasNext(); i++){
        if(iterator.next().equals(value)){
            return i;
        }
    }
    return NULL_INDEX;
}

public int indexOfValue(VALUE value){
    return indexOfValue(value, 0);
}

public int indexOfValue(VALUE value, int offset){
    Iterator<VALUE> iterator = listIterator(offset);
    for(int i = 0; iterator.hasNext(); i++){
        if(iterator.next().equals(value)){
            return i;
        }
    }
    return NULL_INDEX;
}

public int indexOfKey(KEY key){
    Iterator<VALUE> iterator = iterator();
    for(int i = 0; iterator.hasNext(); i++){
        if(getKey(iterator.next()).equals(key)){
            return i;
        }
    }
    return NULL_INDEX;
}

public int indexOfValue(PredicateW<VALUE> predicate){
    Iterator<VALUE> iterator = list.iterator();
    for(int i = 0; iterator.hasNext(); i++){
        if(predicate.test(iterator.next())){
            return i;
        }
    }
    return NULL_INDEX;
}

@Override
public int lastIndexOf(Object o){
    return list.lastIndexOf(o);
}

@Override
public VALUE get(int index){
    return list.get(index);
}

public VALUE getValue(KEY key){
    for(VALUE v: list){
        if(key.equals(getKey(v))){
            return v;
        }
    }
    return null;
}

@Override
public boolean add(VALUE v){
    if(v == null){
DebugException.start().log("value is null").end();
    }
    return list.add(v);
}

public boolean put(VALUE v){
    if(v == null){
DebugException.start().log("value is null").end();
    }
    int index = indexOfValue(v);
    if(index == NULL_INDEX){
        list.add(v);
        return false;
    } else {
        list.set(index, v);
        return true;
    }
}

@Override
public VALUE set(int index, VALUE v){
    if(v == null){
DebugException.start().log("value is null").end();
    }
    return list.set(index, v);
}

@Override
public void add(int index, VALUE v){
    if(v == null){
DebugException.start().log("value is null").end();
    }
    list.add(index, v);
}

@Override
public VALUE remove(int index){
    return list.remove(index);
}

public VALUE removeKey(KEY key){
    int index = indexOfKey(key);
    if(index == NULL_INDEX){
        return null;
    } else {
        return list.remove(index);
    }
}

public KEY removeValue(VALUE value){
    return removeValue(value, 0);
}
public KEY removeValue(VALUE value, int offset){
    int index = indexOfValue(value, offset);
    if(index == NULL_INDEX){
        return null;
    }
    return getKey(list.remove(index));
}

@Override
public boolean remove(Object value){
    return list.remove(value);
}

@Override
public void clear(){
    list.clear();
}

public boolean containsKey(KEY key){
    return indexOfKey(key) != NULL_INDEX;
}

@Override
public boolean contains(Object object){
    return list.contains(object);
}

@Override
public boolean containsAll( Collection<?> c){
    return list.containsAll(c);
}

@Override
public boolean addAll( Collection<? extends VALUE> c){
    return list.addAll(c);
}

@Override
public boolean addAll(int index,  Collection<? extends VALUE> c){
    return list.addAll(index, c);
}

@Override
public boolean retainAll( Collection<?> c){
    return list.retainAll(c);
}

@Override
public boolean removeAll( Collection<?> c){
    return list.removeAll(c);
}


@Override
public List<VALUE> subList(int fromIndex, int toIndex){
    return list.subList(fromIndex, toIndex);
}


@Override
public ListIterator<VALUE> listIterator(){
    return list.listIterator();
}


@Override
public ListIterator<VALUE> listIterator(int index){
    return list.listIterator(index);
}

@Override
public Iterator<VALUE> iterator(){
    return list.iterator();
}

public Iterator<KEY> iteratorKeys(){
    return new Iterator<KEY>(){
        final Iterator<VALUE> iterator = iterator();

        @Override
        public boolean hasNext(){
            return iterator.hasNext();
        }

        @Override
        public KEY next(){
            return getKey(iterator.next());
        }

        @Override
        public void remove(){
            iterator.remove();
        }
    };
}
public Iterable<KEY> iterableKeys(){
    return this::iteratorKeys;
}


@Override
public Object[] toArray(){
    return list.toArray();
}

@Override
public <T> T[] toArray(T[] a){
    return list.toArray(a);
}

final public void toDebugLog(){
    for(VALUE v: this){
DebugLog.start().send(getKey(v) + ":" + v.toString()).end();
    }
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

}
