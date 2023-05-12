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

import com.tezov.lib_java.type.primaire.Pair;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class UtilsNumber{
private UtilsNumber(){
}

public static Pair<Integer, Integer> floatToInt(float value){
    return floatToInt(value, String.valueOf((int)value).length());
}
public static Pair<Integer, Integer> floatToInt(float value, int numberDecimal){
    BigDecimal b = new BigDecimal(value).setScale(numberDecimal, RoundingMode.HALF_UP);
    int integer = b.intValue();
    int decimal = b.remainder(BigDecimal.ONE).movePointRight(numberDecimal).intValue();
    return new Pair<>(integer, decimal);
}

public static float intToFloat(Pair<Integer, Integer> p){
    return intToFloat(p, Integer.toString(p.second).length());
}
public static float intToFloat(Pair<Integer, Integer> p, int numberDecimal){
    return intToFloat(p.first, p.second, numberDecimal);
}
public static float intToFloat(int integer, int decimal){
    return intToFloat(integer, decimal, String.valueOf(decimal).length());
}
public static float intToFloat(int integer, int decimal, int numberDecimal){
    BigDecimal b = new BigDecimal(integer);
    b = b.add(new BigDecimal(decimal).movePointLeft(numberDecimal));
    return b.floatValue();
}

public static Pair<Long, Integer> floatToLong(float value){
    return floatToLong(value, String.valueOf(value).length());
}
public static Pair<Long, Integer> floatToLong(float value, int numberDecimal){
    BigDecimal b = new BigDecimal(value).setScale(numberDecimal, RoundingMode.HALF_UP);
    long integer = b.longValue();
    int decimal = b.remainder(BigDecimal.ONE).movePointRight(numberDecimal).intValue();
    return new Pair<>(integer, decimal);
}


}
