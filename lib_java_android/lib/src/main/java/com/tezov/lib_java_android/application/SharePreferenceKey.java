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
import com.tezov.lib_java.type.primaire.Pair;
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

import com.tezov.lib_java.debug.DebugException;

public class SharePreferenceKey{
public final static String FILE_NAME_SHARE_PREFERENCE = "SHARE_PREFERENCE";

// sharedPreferences
public final static String APP_GUID = "APP_GUID";
public final static String SP_APP_FIRST_INSTALL_LONG = "APP_FIRST_INSTALL";
public final static String SP_APP_FIRST_LAUNCH_BOOL = "APP_FIRST_LAUNCH";
public final static String SP_APP_PREVIOUS_LAUNCH_BOOL = "APP_PREVIOUS_LAUNCH";
public final static String SP_APP_CURRENT_LAUNCH_BOOL = "APP_CURRENT_LAUNCH";

// sharedPreferences
public static final String KEY_TOKEN = "#";
public static String makeKey(String prefix, String key){
    return prefix + KEY_TOKEN + key;
}
public static Pair<String, String> splitKey(String key){
    String[] split = key.split(KEY_TOKEN);
    if(split.length != 2){
DebugException.start().log("key is not composite key did with makeKey(" + key + ")").end();
        return null;
    }
    return new Pair<>(split[0], split[1]);
}

}
