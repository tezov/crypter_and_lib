/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.util;

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.util.UtilsString;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java.wrapperAnonymous.ComparatorW;
import com.tezov.lib_java.wrapperAnonymous.PredicateW;

import java.util.Arrays;
import java.util.List;

import java9.util.Objects;
import java9.util.PrimitiveIterator;
import java9.util.stream.Collectors;
import java9.util.stream.StreamSupport;

public class UtilsArray{
private UtilsArray(){
}

public static <V> V[] sort(V[] list, ComparatorW<V> comparator){
    return (V[])StreamSupport.stream(Arrays.asList(list)).sorted(comparator).collect(Collectors.toList()).toArray();
}

public static <V extends Comparable<V>> V[] sort(V[] list){
    ComparatorW<V> comparator = new ComparatorW<V>(){
        @Override
        public int compare(V v1, V v2){
            return v1.compareTo(v2);
        }
    };
    return sort(list, comparator);
}

public static <V> List<V> filter(List<V> list, PredicateW<V> predicate){
    return StreamSupport.stream(list).filter(predicate).collect(Collectors.toList());
}
public static PrimitiveIterator<Byte, ByteConsumer> getIterator(byte[] bytes){
    return new PrimitiveIterator<Byte, ByteConsumer>(){
        int index = 0;
        @Override
        public void forEachRemaining(ByteConsumer action){
            action.accept(next());
        }
        @Override
        public boolean hasNext(){
            return bytes.length > index;
        }
        @Override
        public Byte next(){
            return bytes[index++];
        }
    };
}
public interface ByteConsumer{
    void accept(byte value);

    default ByteConsumer andThen(ByteConsumer after){
        Objects.requireNonNull(after);
        return (byte t)->{
            accept(t);
            after.accept(t);
        };
    }

}

}
