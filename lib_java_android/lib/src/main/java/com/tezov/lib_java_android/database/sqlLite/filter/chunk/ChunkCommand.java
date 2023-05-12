/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.database.sqlLite.filter.chunk;

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

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugString;

public class ChunkCommand extends Chunk{
private String command;

public ChunkCommand(){
    this.command = null;
}

public String getCommand(){
    return command;
}

public ChunkCommand setCommand(String command){
    this.command = command;
    return this;
}

public String getSql(){
    if(getStatement() != null){
        return command + getStatement();
    } else {
        return command;
    }
}

@Override
public DebugString toDebugString(){
    DebugString data = super.toDebugString();
    data.append("command", command);
    return data;
}

final public void toDebugLogCommand(){
DebugLog.start().send(command).end();
}

final public void toDebugLogSql(){
DebugLog.start().send(getSql()).end();
}

}
