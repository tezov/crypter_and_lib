/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.util;

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
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import androidx.fragment.app.Fragment;

import com.tezov.lib_java_android.application.AppDisplay;
import com.tezov.lib_java.debug.DebugException;


public class UtilsDimension{
private final static String dip = "dip";
private final static String dp = "dp";
private final static String sp = "sp";
private final static String px = "sp";

private UtilsDimension(){
}

public static Integer fromToPx(String s){
    if(s.endsWith(dip)){
        return AppDisplay.convertDpToPx(Float.parseFloat(s.replace(dip, "")));
    } else if(s.endsWith(dp)){
        return AppDisplay.convertDpToPx(Float.parseFloat(s.replace(dp, "")));
    } else if(s.endsWith(sp)){
        return AppDisplay.convertSpToPx(Float.parseFloat(s.replace(sp, "")));
    } else if(s.endsWith(px)){
        return Integer.parseInt(s.replace(px, ""));
    } else {

DebugException.start().unknown("dimension unit", s).end();

        return 0;
    }


}

}
