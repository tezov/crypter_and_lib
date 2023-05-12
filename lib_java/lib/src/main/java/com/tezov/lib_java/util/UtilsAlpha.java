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
import com.tezov.lib_java.type.NormInt;

public class UtilsAlpha{
private final static int base = 255;

public static int get(float alpha){
    return new NormInt(base).getRaw(alpha);
}

public static float get(int alpha){
    return new NormInt(base).getNorm(alpha);
}

public static int color(int color, float alpha){
    return color(color, get(alpha));
}

public static int color(int color, int alpha){
    alpha = (alpha << 24) | 0x00FFFFFF;
    return color & alpha;
}

}
