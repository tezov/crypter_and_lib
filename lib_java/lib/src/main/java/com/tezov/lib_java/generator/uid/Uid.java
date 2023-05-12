/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.generator.uid;

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
import com.tezov.lib_java.type.primitive.BytesTo;
import com.tezov.lib_java.type.primitive.LongTo;
import com.tezov.lib_java.type.primitive.string.StringHexTo;

public class Uid extends UidBase{
protected Long uid_Long = null;

protected Uid(byte[] uid){
    super(uid);
}

protected Uid(long uid){
    super(LongTo.Bytes((uid)));
    this.uid_Long = uid;
}

protected static Uid newInstance(long uid){
    return new Uid(uid);
}

public static Uid fromBytes(byte[] bytes){
    return new Uid(bytes);
}

public static Uid fromHexString(String hexString){
    if(hexString == null){
        return null;
    }
    return fromBytes(StringHexTo.Bytes(hexString));
}

public Long getLong(){
    if(uid_Long == null){
        uid_Long = BytesTo.Long(uid_Bytes);
    }
    return uid_Long;
}

}
