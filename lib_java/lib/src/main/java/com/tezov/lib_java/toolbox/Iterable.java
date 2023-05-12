/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.toolbox;

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.util.UtilsString;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java.debug.DebugTrack;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class Iterable{
public static <T> java.lang.Iterable<T> from(List<T> original, boolean reverse){
    if(reverse){
        return Reversed.from(original);
    } else {
        return original;
    }
}

public static class Reversed<T> implements java.lang.Iterable<T>{
    private final ListIterator<T> original;
    private Reversed(List<T> original, Integer index){
DebugTrack.start().create(this).end();
        if(index == null){
            index = original.size();
        }
        this.original = original.listIterator(index);
    }
    public static <T> Reversed<T> from(List<T> original){
        return new Reversed<>(original, null);
    }
    public static <T> Reversed<T> from(List<T> original, int index){
        return new Reversed<>(original, index);
    }

    @Override
    public Iterator<T> iterator(){
        return new IteratorReversed(original);
    }
    public ListIterator<T> listIterator(){
        return new IteratorReversed(original);
    }
    @Override
    protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
        super.finalize();
    }
    private class IteratorReversed implements ListIterator<T>{
        private final ListIterator<T> iterator;

        public IteratorReversed(ListIterator<T> iterator){
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext(){
            return iterator.hasPrevious();
        }

        @Override
        public T next(){
            return iterator.previous();
        }

        @Override
        public boolean hasPrevious(){
            return iterator.hasNext();
        }

        @Override
        public T previous(){
            return iterator.next();
        }

        @Override
        public int nextIndex(){
            return iterator.previousIndex();
        }

        @Override
        public int previousIndex(){
            return iterator.nextIndex();
        }

        @Override
        public void remove(){
            iterator.remove();
        }

        @Override
        public void set(T t){
            iterator.set(t);
        }

        @Override
        public void add(T t){
            iterator.add(t);
        }

    }

}

}
