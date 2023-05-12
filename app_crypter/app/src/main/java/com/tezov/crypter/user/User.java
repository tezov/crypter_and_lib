/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.crypter.user;

import com.tezov.lib_java.debug.DebugLog;
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
import com.tezov.lib_java.cipher.key.KeyMutual;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.primitive.BytesTo;

public class User extends com.tezov.lib_java_android.authentification.User{
private long timestamp;

private User(KeyMutual key){
    super(BytesTo.StringHex(key.getIv()), key);
}
public long getTimestamp(){
    return timestamp;
}

@Override
public DebugString toDebugString(){
    DebugString data = super.toDebugString();
    data.appendDate("timestamp", timestamp);
    return data;
}

public static class Builder{
    KeyMutual key;
    long timestamp = 0;
    public Builder(KeyMutual key){
DebugTrack.start().create(this).end();
        this.key = key;
    }
    public Builder setTimestamp(long timestamp){
        this.timestamp = timestamp;
        return this;
    }
    public User build(){
        User user = new User(key);
        user.timestamp = timestamp;
        return user;
    }

    @Override
    protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
        super.finalize();
    }

}

}
