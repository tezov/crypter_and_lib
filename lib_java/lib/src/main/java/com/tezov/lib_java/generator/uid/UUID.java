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

public class UUID extends UidBase{
protected java.util.UUID uuid = null;

protected UUID(byte[] uid){
    super(uid);
}

protected UUID(java.util.UUID uid){
    super(UUID.toBytes(uid));
    this.uuid = uid;
}

public static UUID fromUID(java.util.UUID uid){
    if(uid == null){
        return null;
    }
    return new UUID(uid);
}

public static UUID fromBytes(byte[] bytes){
    if(bytes == null){
        return null;
    }
    else{
        return new UUID(bytes);
    }
}

public static UUID fromHexString(String hexString){
    if(hexString == null){
        return null;
    }
    else{
        return fromBytes(StringHexTo.Bytes(hexString));
    }
}

public static byte[] toBytes(java.util.UUID uid){
    byte[] least = LongTo.Bytes(uid.getLeastSignificantBits());
    byte[] most = LongTo.Bytes(uid.getMostSignificantBits());
    byte[] concat = new byte[most.length + least.length];
    System.arraycopy(most, 0, concat, 0, most.length);
    System.arraycopy(least, 0, concat, most.length, least.length);
    return concat;
}

public java.util.UUID getUUID(){
    if(uuid == null){
        long most = BytesTo.Long(uid_Bytes);
        long least = BytesTo.Long(uid_Bytes, 8);
        uuid = new java.util.UUID(most, least);
    }
    return uuid;
}

}
