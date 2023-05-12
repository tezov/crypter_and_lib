/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.component.preference.custom;

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
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.TypedValue;

import androidx.annotation.NonNull;
import androidx.annotation.StyleableRes;

public class UtilsPreference{
public static boolean getBoolean(@NonNull TypedArray a, @StyleableRes int index, @StyleableRes int fallbackIndex, boolean defaultValue){
    boolean val = a.getBoolean(fallbackIndex, defaultValue);
    return a.getBoolean(index, val);
}
public static int getAttr(@NonNull Context context, int attr, int fallbackAttr){
    TypedValue value = new TypedValue();
    context.getTheme().resolveAttribute(attr, value, true);
    if(value.resourceId != 0){
        return attr;
    }
    return fallbackAttr;
}
}
