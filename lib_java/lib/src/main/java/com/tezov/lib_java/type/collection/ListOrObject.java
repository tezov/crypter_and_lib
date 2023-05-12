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

import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.wrapperAnonymous.PredicateW;
import com.tezov.lib_java.wrapperAnonymous.SupplierW;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Consumer;

import static com.tezov.lib_java.util.UtilsList.NULL_INDEX;
import static com.tezov.lib_java.util.UtilsNull.NULL_OBJECT;

public class ListOrObject<T> implements List<T>{
private final SupplierW<List<T>> listSupplier;
private Object object;
private boolean isList;

public ListOrObject(){
    this(ArrayList::new);
}
public ListOrObject(SupplierW<List<T>> supplier){
    this(NULL_OBJECT, false, supplier);
}
private ListOrObject(Object o, boolean isList,  SupplierW<List<T>> supplier){
DebugTrack.start().create(this).end();
    this.object = o;
    this.isList = isList;
    this.listSupplier = supplier;
}

public static <T> ListOrObject<T> with(T t){
    if(t == null){
        return null;
    }
    else {
        return new ListOrObject<>(t, (t instanceof List), null);
    }
}

private ListOrObject<T> me(){
    return this;
}

@Override
public boolean isEmpty(){
    return object == NULL_OBJECT;
}

@Override
public int size(){
    if(object == NULL_OBJECT){
        return 0;
    } else if(isList){
        return ((List)object).size();
    } else {
        return 1;
    }
}

@Override
public int indexOf(Object o){
    Iterator<T> iterator = iterator();
    for(int i = 0; iterator.hasNext(); i++){
        if(iterator.next().equals(o)){
            return i;
        }
    }
    return NULL_INDEX;
}

public int indexOf(PredicateW<T> predicate){
    Iterator<T> iterator = iterator();
    for(int i = 0; iterator.hasNext(); i++){
        if(predicate.test(iterator.next())){
            return i;
        }
    }
    return NULL_INDEX;
}

@Override
public int lastIndexOf(Object o){
    Iterator<T> iterator = iterator();
    int lastIndex = NULL_INDEX;
    for(int i = 0; iterator.hasNext(); i++){
        if(iterator.next().equals(o)){
            lastIndex = i;
        }
    }
    return lastIndex;
}

public T get(){
    if(object == NULL_OBJECT){
        return null;
    }
    else if(!isList){
        return (T)object;
    }
    else{
        return (T)((List)object).get(0);
    }
}
public ListOrObject<T> set(T t){
    if(isList){
DebugException.start().log("ListOrObject is a list, should not use set").end();
    }
    if(t == null){
        object = NULL_OBJECT;
    }
    else{
        object = t;
    }
    return this;
}

@Override
public T get(int index){
    if(isList){
        return ((List<T>)object).get(index);
    } else if((object != NULL_OBJECT) && (index == 0)){
        return (T)object;
    } else {
        throw new IndexOutOfBoundsException();
    }
}

@Override
public boolean add(T t){
    if(t == null){
DebugException.start().log("value is null").end();
    }
    if(object == NULL_OBJECT){
        object = t;
        return true;
    } else if(isList){
        return ((List)object).add(t);
    } else {
        List list = listSupplier.get();
        boolean result = true;
        result &= list.add(object);
        result &= list.add(t);
        object = list;
        isList = true;
        return result;
    }
}

@Override
public T set(int index, T t){
    if(t == null){
DebugException.start().log("value is null").end();
    }
    if(isList){
        return ((List<T>)object).set(index, t);
    } else if((object != NULL_OBJECT) && (index == 0)){
        T old = (T)object;
        object = t;
        return old;
    } else {
        throw new IndexOutOfBoundsException();
    }
}

@Override
public void add(int index, T t){
    if(t == null){
DebugException.start().log("value is null").end();
    }
    if(isList){
        ((List<T>)object).add(index, t);
    } else if(object != NULL_OBJECT){
        List list = listSupplier.get();
        if(index == 1){
            list.add(object);
            list.add(t);
        } else if(index == 0){
            list.add(t);
            list.add(object);
        }
        object = list;
        isList = true;
    } else if(index == 0){
        object = t;
    } else {
        throw new IndexOutOfBoundsException();
    }

}

@Override
public boolean remove(Object o){
    if(object == NULL_OBJECT){
        return false;
    } else if(isList){
        if(!((List)object).remove(o)){
            return false;
        }
        if(((List)object).size() == 1){
            object = ((List)object).get(0);
            isList = false;
        }
        return true;
    } else if(object.equals(o)){
        object = NULL_OBJECT;
        return true;
    } else {
        return false;
    }
}

@Override
public T remove(int index){
    T t = null;
    if(isList){
        t = ((List<T>)object).remove(index);
        if(((List)object).size() == 1){
            object = ((List)object).get(0);
            isList = false;
        }
    } else
        if(index == 0){
            t = (T)object;
            object = NULL_OBJECT;
        }
    return t;
}

@Override
public void clear(){
    object = NULL_OBJECT;
    isList = false;
}

@Override
public boolean contains(Object o){
    for(T t: this){
        if(t.equals(o)){
            return true;
        }
    }
    return false;
}

@Override
public boolean containsAll(Collection<?> c){
    if(isList){
        return ((List)object).containsAll(c);
    } else {
        for(Object p: c){
            if(!p.equals(object)){
                return false;
            }
        }
        return (object == NULL_OBJECT) || (c.size() == 1);
    }
}

@Override
public boolean addAll(Collection<? extends T> c){
    if(isList){
        return ((List)object).addAll(c);
    } else {
        int previousSize = size();
        for(T t: c){
            add(t);
        }
        return (previousSize + c.size()) == size();
    }
}

@Override
public boolean addAll(int index,  Collection<? extends T> c){
    if(isList){
        return ((List)object).addAll(index, c);
    } else {
        int previousSize = size();
        for(T t: c){
            add(index++, t);
        }
        return (previousSize + c.size()) == size();
    }
}

@Override
public boolean retainAll(Collection<?> c){
    if(isList){
        return ((List)object).retainAll(c);
    } else {
        for(Object p: c){
            if(p.equals(object)){
                return false;
            }
        }
        clear();
        return true;
    }
}

@Override
public boolean removeAll(Collection<?> c){
    if(isList){
        return ((List)object).removeAll(c);
    } else {
        for(Object p: c){
            if(p.equals(object)){
                clear();
                return true;
            }
        }
        return false;
    }
}


@Override
public List<T> subList(int fromIndex, int toIndex){
    if(isList){
        return ((List)object).subList(fromIndex, toIndex);
    } else if(object != NULL_OBJECT){
        if((fromIndex != 0) || (toIndex != 0)){
            throw new IndexOutOfBoundsException();
        }
        List<T> l = listSupplier.get();
        l.add((T)object);
        return l;
    } else if((fromIndex != NULL_INDEX) || (toIndex != NULL_INDEX)){

        throw new IndexOutOfBoundsException();
    }
    return listSupplier.get();
}

public class Itr implements Iterator<T> {
    int limit = me().size();
    int cursor = 0;
    int lastRet = -1;
    @Override
    public boolean hasNext() {
        return cursor < limit;
    }
    @Override
    public T next() {
        int i = cursor;
        if (i >= limit)
            throw new NoSuchElementException();
        if (i >= me().size())
            throw new ConcurrentModificationException();
        cursor++;
        return me().get(lastRet = i);
    }
    @Override
    public void remove() {
        if (lastRet < 0)
            throw new IllegalStateException();
        try {
            me().remove(lastRet);
            cursor = lastRet;
            lastRet = -1;
            limit--;
        } catch (IndexOutOfBoundsException ex) {
            throw new ConcurrentModificationException();
        }
    }
    @Override
    public void forEachRemaining(Consumer<? super T> consumer) {
        final int size = me().size();
        int i = cursor;
        if (i >= size) {
            return;
        }
        while (i != size) {
            consumer.accept(me().get(i++));
        }
        cursor = i;
        lastRet = i - 1;
    }
}
public class ListItr extends Itr implements ListIterator<T> {
    ListItr(int index) {
        super();
        cursor = index;
    }
    public boolean hasPrevious() {
        return cursor != 0;
    }
    public int nextIndex() {
        return cursor;
    }
    public int previousIndex() {
        return cursor - 1;
    }
    public T previous() {
        int i = cursor - 1;
        if (i < 0)
            throw new NoSuchElementException();
        if (i >= me().size())
            throw new ConcurrentModificationException();
        cursor = i;
        return me().get(lastRet = i);
    }
    public void set(T e) {
        if (lastRet < 0)
            throw new IllegalStateException();
        try {
            me().set(lastRet, e);
        } catch (IndexOutOfBoundsException ex) {
            throw new ConcurrentModificationException();
        }
    }
    public void add(T e) {
        try {
            int i = cursor;
            me().add(i, e);
            cursor = i + 1;
            lastRet = -1;
            limit++;
        } catch (IndexOutOfBoundsException ex) {
            throw new ConcurrentModificationException();
        }
    }
}


@Override
public Iterator<T> iterator(){
    return new Itr();
}


@Override
public ListIterator<T> listIterator(){
    return new ListItr(0);
}


@Override
public ListIterator<T> listIterator(int index){
    return new ListItr(index);
}

@Override
public Object[] toArray(){
    if(isList){
        return ((List)object).toArray();
    } else if(object != NULL_OBJECT){
        return new Object[]{object};
    } else {
        return new Object[]{};
    }
}

@Override
public <T> T[] toArray(T[] a){
    if(isList){
        return ((List<T>)object).toArray(a);
    } else {
        if(object != NULL_OBJECT){
            a = (T[])Array.newInstance(a.getClass().getComponentType(), 1);
            a[0] = (T)object;
        }
        return a;
    }
}

final public void toDebugLog(){
    for(T t: this){
DebugLog.start().send(t.toString()).end();
    }
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

}
