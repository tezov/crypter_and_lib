/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.type.primaire;

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
import com.tezov.lib_java.toolbox.Compare;

public class Pair<F, S>{
public F first;
public S second;

public Pair(F first, S second){
    this.first = first;
    this.second = second;
}

public boolean equals(Object o){
    if(o instanceof Pair){
        boolean result = Compare.equals(first, ((Pair<?, ?>)o).first);
        result &= Compare.equals(second, ((Pair<?, ?>)o).second);
        return result;
    } else {
        return false;
    }
}

}
