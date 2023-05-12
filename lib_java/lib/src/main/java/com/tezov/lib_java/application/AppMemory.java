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
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.util.UtilsString;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java.type.unit.UnitByte;

import static com.tezov.lib_java.type.unit.UnitByte.Mo;

public class AppMemory{
static long memoryAtOnCreate;

private AppMemory(){
}

public static void init(){
    memoryAtOnCreate = Runtime.getRuntime().totalMemory();
}

public static long now(){
    return Runtime.getRuntime().totalMemory();
}
public static float used(){
    return used(Mo);
}
public static float used(UnitByte unit){
    long bytesUsed = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
    return unit.convert(bytesUsed);
}

}
