/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.crypter.data.misc;

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.util.UtilsString;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import androidx.fragment.app.Fragment;
import com.tezov.lib_java.toolbox.Clock;

import org.threeten.bp.ZoneId;

public class ClockFormat{
public static String longToDateTime_FULL(Long value){
    return longToDateTime_FULL(value, Clock.ZoneIdLocal());
}
public static String longToDateTime_FULL(Long value, ZoneId zoneId){
    String data = null;
    if((value != null) && (zoneId != null)){
        data = Clock.MilliSecondTo.DateAndTime.toString(value, zoneId, Clock.FormatDateAndTime.FULL);
    }
    return data;
}

}
