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
public class UtilsNull{
public final static NULL NULL_OBJECT = new NULL();
public final static NOT_NULL NOT_NULL_OBJECT = new NOT_NULL();

public static class NULL{
    @Override
    public boolean equals(Object obj){
        return obj instanceof NULL;
    }
    @Override
    public int hashCode(){
        return 0;
    }
    @Override
    public String toString(){
        return "NULL";
    }

}
public static class NOT_NULL{
    @Override
    public boolean equals(Object obj){
        return obj instanceof NOT_NULL;
    }
    @Override
    public int hashCode(){
        return 1;
    }
    @Override
    public String toString(){
        return "NOT NULL";
    }
}



}
