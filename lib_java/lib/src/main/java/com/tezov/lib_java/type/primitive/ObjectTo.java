/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.type.primitive;

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
import com.tezov.lib_java.debug.DebugLog;

public class ObjectTo{
private ObjectTo(){
}

public static int hashcodeIdentity(Object o){
    return System.identityHashCode(o);
}

public static String hashcodeIdentityString(Object o){
    if(o == null){
        return "object is null";
    }
    return "0x" + Integer.toHexString(System.identityHashCode(o));
}

public static String hashcodeString(Object o){
    if(o == null){
        return "object is null";
    }
    return "0x" + Integer.toHexString(o.hashCode());
}

public static void toDebugLogHashcodeIdentity(Object o){
DebugLog.start().send(Integer.toHexString(hashcodeIdentity(o))).end();
}

}
