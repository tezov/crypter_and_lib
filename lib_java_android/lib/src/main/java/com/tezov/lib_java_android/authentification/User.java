/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.authentification;

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
import com.tezov.lib_java.cipher.dataAdapter.string.DataStringAdapter;
import com.tezov.lib_java.cipher.dataInput.EncoderBytes;
import com.tezov.lib_java.cipher.dataInput.Encoder;
import com.tezov.lib_java.cipher.key.KeyMutual;
import com.tezov.lib_java.cipher.key.KeySim;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java.debug.DebugTrack;

public class User{
private final String vid;
private final KeyMutual key;

public User(String vid, KeyMutual key){
DebugTrack.start().create(this).end();
    this.vid = vid;
    this.key = key;
}

public String getVid(){
    return vid;
}

public String getKeyId(){
    return key.getIdStringHex();
}
public byte[] getKeyIv(){
    return key.getIv();
}

public KeySim getKey(){
    return key.copy();
}

public byte[] encode(byte[] data){
    EncoderBytes encoder = EncoderBytes.newEncoder(key);
    return encoder.encode(data, getKeyIv());
}
public String encode(String data){
    Encoder<String, String> encoder = Encoder.newEncoder(key, DataStringAdapter.forDecoder());
    return encoder.encode(data, getKeyIv());
}

public DebugString toDebugString(){
    DebugString data = new DebugString();
    data.append("vid", vid);
    data.append("key", key);
    return data;
}
final public void toDebugLog(){
DebugLog.start().send(toDebugString()).end();
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

}
