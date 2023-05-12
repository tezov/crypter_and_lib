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
import com.tezov.lib_java.wrapperAnonymous.FunctionW;
import com.tezov.lib_java.wrapperAnonymous.PredicateW;
import com.tezov.lib_java.wrapperAnonymous.SupplierW;

import java.util.ArrayList;
import java.util.List;

import java9.util.stream.Collectors;
import java9.util.stream.StreamSupport;

public class UtilsList{
public final static int NULL_INDEX = -1;

private UtilsList(){
}

public static <V> SupplierW<List<V>> GET_DEFAULT_LIST(){
    return ArrayList::new;
}
public static <V> List<V> sort(List<V> list, ComparatorW<V> comparator){
    return sort(list, comparator, GET_DEFAULT_LIST());
}
public static <V> List<V> sort(List<V> list, ComparatorW<V> comparator, SupplierW<List<V>> supplier){
    return StreamSupport.stream(list).sorted(comparator).collect(Collectors.toCollection(supplier));
}

public static <V extends Comparable<V>> List<V> sort(List<V> list){
    ComparatorW<V> comparator = new ComparatorW<V>(){
        @Override
        public int compare(V v1, V v2){
            return v1.compareTo(v2);
        }
    };
    return sort(list, comparator);
}
public static <V> List<V> filter(List<V> list, PredicateW<V> predicate){
    return filter(list, predicate, GET_DEFAULT_LIST());
}
public static <V> List<V> filter(List<V> list, PredicateW<V> predicate, SupplierW<List<V>> supplier){
    return StreamSupport.stream(list).filter(predicate).collect(Collectors.toCollection(supplier));
}

public static <O, I> List<O> map(List<I> list, FunctionW<I, O> mapper){
    return map(list, mapper, GET_DEFAULT_LIST());
}
public static <O, I> List<O> map(List<I> list, FunctionW<I, O> mapper, SupplierW<List<O>> supplier){
    return StreamSupport.stream(list).map(mapper).collect(Collectors.toCollection(supplier));
}


}
