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
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java.wrapperAnonymous.ComparatorW;

import java.util.LinkedHashMap;
import java.util.Map;

import java9.util.stream.Collectors;
import java9.util.stream.StreamSupport;

public class UtilsMap{
private UtilsMap(){
}

public static <K, V> LinkedHashMap<K, V> sort(Map<K, V> map, ComparatorW<Map.Entry<K, V>> comparator){
    return StreamSupport.stream(map.entrySet()).sorted(comparator).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2)->v2, LinkedHashMap::new));
}

public static <K extends Comparable<K>, V> LinkedHashMap<K, V> sortByKey(Map<K, V> map){
    ComparatorW<Map.Entry<K, V>> comparator = new ComparatorW<Map.Entry<K, V>>(){
        @Override
        public int compare(Map.Entry<K, V> e1, Map.Entry<K, V> e2){
            return e1.getKey().compareTo(e2.getKey());
        }
    };
    return sort(map, comparator);
}

public static <K, V extends Comparable<V>> LinkedHashMap<K, V> sortByValue(Map<K, V> map){
    ComparatorW<Map.Entry<K, V>> comparator = new ComparatorW<Map.Entry<K, V>>(){
        @Override
        public int compare(Map.Entry<K, V> e1, Map.Entry<K, V> e2){
            return e1.getValue().compareTo(e2.getValue());
        }
    };
    return sort(map, comparator);
}

}
