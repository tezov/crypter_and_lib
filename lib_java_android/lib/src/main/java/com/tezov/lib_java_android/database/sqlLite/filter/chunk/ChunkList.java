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
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import androidx.fragment.app.Fragment;

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java.wrapperAnonymous.FunctionW;
import com.tezov.lib_java.util.UtilsString;

import java.util.ArrayList;
import java.util.List;

public class ChunkList implements defChunkProcessor{
private final static String DELIMITER = " AND ";
private List<Chunk> chunks;

public ChunkList(){
    clear();
}

public ChunkList clear(){
    chunks = new ArrayList<>();
    return this;
}

public List<Chunk> listChunk(){
    return chunks;
}

public void addChunk(Chunk c){
    chunks.add(c);
}

public void updateChunk(int index, Chunk c){
    chunks.set(index, c);
}

public void removeChunk(int index){
    chunks.remove(index);
}

public boolean isEmpty(){
    return chunks.isEmpty();
}

@Override
public Chunk toChunk(){
    List<String> values = new ArrayList<>();
    String statement = UtilsString.join(DELIMITER, chunks, new FunctionW<Chunk, String>(){
        @Override
        public String apply(Chunk c){
            values.addAll(c.getValues());
            return c.getStatement();
        }
    }).toString();
    return new Chunk().setStatement(statement).addValues(values);
}

@Override
public DebugString toDebugString(){
    DebugString data = new DebugString();
    if(chunks != null){
        data.append("chunk list", chunks);
    } else {
        data.append("[chunk list:null]");
    }
    return data;
}

@Override
final public void toDebugLog(){
DebugLog.start().send(toDebugString()).end();
}

final public void toDebugLogStatement(){
    if(chunks != null){
        toChunk().toDebugLogStatement();
    } else {
DebugLog.start().send("statement is null").end();
    }
}

}
