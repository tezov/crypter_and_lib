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
import com.tezov.lib_java.debug.DebugLog;

import com.tezov.lib_java.toolbox.Nullify;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.primaire.Entry;
import com.tezov.lib_java.wrapperAnonymous.PredicateW;
import com.tezov.lib_java.wrapperAnonymous.SupplierW;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import static com.tezov.lib_java.util.UtilsList.NULL_INDEX;

public class ListEntry<KEY, VALUE> implements List<Entry<KEY, VALUE>>{
private final List<Entry<KEY, VALUE>> list;

public ListEntry(){
    this(ArrayList::new);
}
public ListEntry(SupplierW<List<Entry<KEY, VALUE>>> supplier){
    this(supplier.get());
}
private ListEntry(List<Entry<KEY, VALUE>> list){
DebugTrack.start().create(this).end();
    this.list = list;
}

public boolean hasKey(KEY key){
    return getEntry(key) != null;
}

public boolean isUnique(KEY key){
    boolean found = false;
    for(Entry<KEY, VALUE> p: list){
        if(key.equals(p.key)){
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
public int indexOf( Object entry){
    Iterator<Entry<KEY, VALUE>> iterator = iterator();
    for(int i = 0; iterator.hasNext(); i++){
        if(iterator.next().equals(entry)){
            return i;
        }
    }
    return NULL_INDEX;
}

public int indexOfValue(VALUE value){
    return indexOfValue(value, 0);
}
public int indexOfValue(VALUE value, int offset){
    Iterator<Entry<KEY, VALUE>> iterator = listIterator(offset);
    for(int i = 0; iterator.hasNext(); i++){
        if(iterator.next().value.equals(value)){
            return i;
        }
    }
    return NULL_INDEX;
}

public int indexOfKey(KEY key){
    return indexOfKey(key, 0);
}

public int indexOfKey(KEY key, int offset){
    Iterator<Entry<KEY, VALUE>> iterator = listIterator(offset);
    for(int i = 0; iterator.hasNext(); i++){
        if(iterator.next().key.equals(key)){
            return i;
        }
    }
    return NULL_INDEX;
}

public int indexOf(PredicateW<Entry<KEY, VALUE>> predicate, int offset){
    Iterator<Entry<KEY, VALUE>> iterator = list.listIterator(offset);
    for(int i = 0; iterator.hasNext(); i++){
        if(predicate.test(iterator.next())){
            return i;
        }
    }
    return NULL_INDEX;
}

@Override
public int lastIndexOf( Object o){

DebugException.start().notImplemented().end();

    return NULL_INDEX;
}

@Override
public Entry<KEY, VALUE> get(int index){
    return list.get(index);
}

public KEY getKey(VALUE value){
    return getKey(value, 0);
}
public KEY getKey(VALUE value, int offset){
    int index = indexOfValue(value, offset);
    if(index == NULL_INDEX){
        return null;
    }
    return getKeyAt(index);
}
public KEY getKeyAt(int index){
    return list.get(index).key;
}
public List<KEY> getKeys(){
    List<KEY> values = new ArrayList<>(list.size());
    Iterator<KEY> iterator = iteratorKeys();
    while(iterator.hasNext()){
        values.add(iterator.next());
    }
    return Nullify.collection(values);
}

public VALUE getValue(KEY key){
    Entry<KEY, VALUE> p = getEntry(key);
    if(p == null){
        return null;
    }
    return p.value;
}
public VALUE getValueAt(int index){
    return list.get(index).value;
}
public List<VALUE> getValues(){
    List<VALUE> values = new ArrayList<>(list.size());
    Iterator<VALUE> iterator = iteratorValues();
    while(iterator.hasNext()){
        values.add(iterator.next());
    }
    return Nullify.collection(values);
}

public Entry<KEY, VALUE> getEntry(KEY key){
    for(Entry<KEY, VALUE> p: list){
        if(key.equals(p.key)){
            return p;
        }
    }
    return null;
}

public Entry<KEY, VALUE> getEntry(int index){
    return list.get(index);
}

@Override
public boolean add(Entry<KEY, VALUE> entry){
    return list.add(entry);
}

public void add(KEY key, VALUE value){
    list.add(new Entry<>(key, value));
}

public boolean put(KEY key, VALUE value){
    Entry<KEY, VALUE> p = getEntry(key);
    if(p == null){
        p = new Entry<>(key, value);
        list.add(p);
        return true;
    } else {
        p.value = value;
        return false;
    }
}

@Override
public Entry<KEY, VALUE> set(int index, Entry<KEY, VALUE> entry){

DebugException.start().notImplemented().end();

    return null;
}

@Override
public void add(int index, Entry<KEY, VALUE> entry){

DebugException.start().notImplemented().end();

}

@Override
public Entry<KEY, VALUE> remove(int index){
    return list.remove(index);
}

public VALUE removeKey(KEY key){
    return removeKey(key, 0);
}

public VALUE removeKey(KEY key, int offset){
    int index = indexOfKey(key, offset);
    if(index == NULL_INDEX){
        return null;
    }
    Entry<KEY, VALUE> entry = list.remove(index);
    if(entry == null){
        return null;
    }
    return entry.value;
}

public KEY removeValue(VALUE value){
    return removeValue(value, 0);
}

public KEY removeValue(VALUE value, int offset){
    int index = indexOfValue(value, offset);
    if(index == NULL_INDEX){
        return null;
    }
    Entry<KEY, VALUE> entry = list.remove(index);
    if(entry == null){
        return null;
    }
    return entry.key;
}

@Override
public boolean remove( Object entry){

DebugException.start().notImplemented().end();

    return false;
}

@Override
public void clear(){
    list.clear();
}

@Override
public boolean contains( Object object){

DebugException.start().notImplemented().end();

    return false;
}

@Override
public boolean containsAll(Collection<?> c){

DebugException.start().notImplemented().end();

    return false;
}

@Override
public boolean addAll(Collection<? extends Entry<KEY, VALUE>> c){
    return list.addAll(c);
}

@Override
public boolean addAll(int index, Collection<? extends Entry<KEY, VALUE>> c){

DebugException.start().notImplemented().end();

    return false;
}

@Override
public boolean retainAll(Collection<?> c){

DebugException.start().notImplemented().end();

    return false;
}

@Override
public boolean removeAll(Collection<?> c){

DebugException.start().notImplemented().end();

    return false;
}


@Override
public List<Entry<KEY, VALUE>> subList(int fromIndex, int toIndex){

DebugException.start().notImplemented().end();

    return null;
}


@Override
public ListIterator<Entry<KEY, VALUE>> listIterator(){
    return list.listIterator();
}


@Override
public ListIterator<Entry<KEY, VALUE>> listIterator(int index){
    return list.listIterator(index);
}

@Override
public Iterator<Entry<KEY, VALUE>> iterator(){
    return list.iterator();
}

public Iterator<KEY> iteratorKeys(){
    return new Iterator<KEY>(){
        final Iterator<Entry<KEY, VALUE>> iterator = iterator();

        @Override
        public boolean hasNext(){
            return iterator.hasNext();
        }

        @Override
        public KEY next(){
            return iterator.next().key;
        }

        @Override
        public void remove(){
            iterator.remove();
        }
    };
}

public Iterator<VALUE> iteratorValues(){
    return new Iterator<VALUE>(){
        final Iterator<Entry<KEY, VALUE>> iterator = iterator();

        @Override
        public boolean hasNext(){
            return iterator.hasNext();
        }

        @Override
        public VALUE next(){
            return iterator.next().value;
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

public Iterable<VALUE> iterableValues(){
    return this::iteratorValues;
}


@Override
public Object[] toArray(){
    return list.toArray();
}

@Override
public <T> T[] toArray( T[] a){
    return list.toArray(a);
}

public String toDebugString(){
    DebugString data = new DebugString();
    for(Entry<KEY, VALUE> e: this){
        data.append(e.key, e.value);
    }
    return data.toString();
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
