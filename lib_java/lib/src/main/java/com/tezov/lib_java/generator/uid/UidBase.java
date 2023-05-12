/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.generator.uid;

import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.util.UtilsString;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java.toolbox.Compare;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.primitive.BytesTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.primitive.string.StringHexTo;

public class UidBase implements defUid, Comparable<defUid>{
protected byte[] uid_Bytes;
protected String uid_HexString;

protected UidBase(byte[] uid){
    trackClassCreate();
    this.uid_Bytes = uid;
    this.uid_HexString = BytesTo.StringHex(uid);
}
protected UidBase(String uid){
    trackClassCreate();
    this.uid_Bytes = StringHexTo.Bytes(uid);
    this.uid_HexString = uid;
}

public static UidBase fromBytes(byte[] bytes){
    if(bytes == null){
        return null;
    } else {
        return new UidBase(bytes);
    }
}
public static UidBase fromHexString(String hexString){
    if(hexString == null){
        return null;
    } else {
        return new UidBase(hexString);
    }
}
public static UidBase fromBytes(Integer value){
    if(value == null){
        return null;
    } else {
        return new UidBase(IntTo.Bytes(value));
    }
}
private void trackClassCreate(){
DebugTrack.start().create(this).end();
}

@Override
public byte[] toBytes(){
    return uid_Bytes;
}
@Override
public String toHexString(){
    return uid_HexString;
}
@Override
public int getLength(){
    return uid_Bytes.length;
}

public DebugString toDebugString(){
    return new DebugString().append(toHexString());
}
final public void toDebugLog(){
DebugLog.start().send(toDebugString()).end();
}

@Override
public int compareTo(defUid object){
    if(object == null){
        return 1;
    }
    else{
        String first = toHexString();
        String second = object.toHexString();
        return first.compareTo(second);
    }
}

@Override
public boolean equals(Object obj){
    if(!(obj instanceof UidBase)){
        return false;
    } else {
        return Compare.equals(uid_Bytes, ((UidBase)obj).uid_Bytes);
    }
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

}
