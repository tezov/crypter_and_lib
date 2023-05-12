/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.application;

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

import androidx.annotation.ChecksSdkIntAtLeast;
import android.os.Build;

public class VersionSDK{
private VersionSDK(){
}

//21
@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.LOLLIPOP)
public static boolean isSupEqualTo21_LOLLIPOP(){
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
}

//23
@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.M)
public static boolean isSupEqualTo23_MARSHMALLOW(){
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
}
//24
@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.N)
public static boolean isSupEqualTo24_NOUGAT(){
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N;
}
//26
@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.O)
public static boolean isSupEqualTo26_OREO(){
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
}

//28
@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.P)
public static boolean isSupEqualTo28_P(){
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.P;
}

//29
@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.Q)
public static boolean isSupEqualTo29_Q(){
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q;
}

//30
@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.R)
public static boolean isSupEqualTo30_R(){
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.R;
}

public static int getVersion(){
    return Build.VERSION.SDK_INT;
}


}
