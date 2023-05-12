/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.application;

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.util.UtilsString;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;

import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.cipher.SecureProvider;

import java.security.SecureRandom;

public class AppRandomNumber{
private static final SecureRandom rand;

static{
    SecureRandom r;
    try{
        r = SecureProvider.randomGenerator();
    } catch(java.lang.Throwable e){
DebugException.start().log(e).end();
        r = null;
    }
    rand = r;
}

private AppRandomNumber(){
}

synchronized public static long nextLong(){
    return rand.nextLong();
}
synchronized public static long nextLong(long bound){
    if(bound <= 0){
        throw new IllegalArgumentException("bound must be positive");
    }
    if((bound & -bound) == bound){
        return (int)((bound * rand.nextLong()) >> 63);
    }
    long bits, val;
    do{
        bits = (rand.nextLong() << 1) >>> 1;
        val = bits % bound;
    } while(bits - val + (bound - 1) < 0L);
    return val;
}
synchronized public static long nextLong(long min, long max){
    long bound = max - min;
    if((max <= 0) || (bound < 0)){
        throw new IllegalArgumentException("max must positive and diff must be positive");
    }
    return nextLong(bound) + min;
}

synchronized public static int nextInt(){
    return rand.nextInt();
}
synchronized public static int nextInt(int bound){
    if(bound <= 0){
        throw new IllegalArgumentException("bound must be positive");
    }
    return rand.nextInt(bound);
}
synchronized public static int nextInt(int min, int max){
    int bound = max - min;
    if((max <= 0) || (bound < 0)){
        throw new IllegalArgumentException("max must positive and diff must be positive");
    }
    return rand.nextInt(bound) + min;
}

synchronized public static boolean nextFlip(int dividend){
    return nextInt(dividend) == 0;
}
synchronized public static boolean nextFlip(){
    return nextInt(2) == 0;
}

}
