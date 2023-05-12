/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.database.sqlLite.filter.chunk;

import com.tezov.lib_java.debug.DebugLog;
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
import androidx.annotation.NonNull;

import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java.type.collection.ListOrObject;
import com.tezov.lib_java.wrapperAnonymous.FunctionW;
import com.tezov.lib_java.util.UtilsString;

import java.util.ArrayList;
import java.util.List;

public class ChunkProcessorList extends ChunkList{
private final static String DELIMITER = " AND ";
private ListOrObject<defChunkProcessor> processors;

public ChunkProcessorList(){
    clear();
}

@Override
public ChunkProcessorList clear(){
    super.clear();
    processors = new ListOrObject<>();
    return this;
}

public void add(defChunkProcessor p){
    processors.add(p);
    addChunk(p.toChunk());
}

public void update(defChunkProcessor p){
    int index = processors.indexOf(p);
    updateChunk(index, p.toChunk());
}

public void remove(defChunkProcessor p){
    int index = processors.indexOf(p);
    removeChunk(index);
    processors.remove(p);
}

@Override
public boolean isEmpty(){
    return processors.isEmpty();
}

public <F extends defChunkProcessor> ListOrObject<F> processors(){
    return (ListOrObject<F>)processors;
}

@Override
public Chunk toChunk(){
    List<String> values = new ArrayList<>();
    String statement = UtilsString.join(DELIMITER, listChunk(), new FunctionW<Chunk, String>(){
        @Override
        public String apply(Chunk c){
            if(c.getValues() != null){
                values.addAll(c.getValues());
            }
            return c.getStatement();
        }
    }).toString();
    return new Chunk().setStatement(statement).addValues(values);
}

@Override
@NonNull
public DebugString toDebugString(){
    DebugString data = new DebugString();
    if(processors != null){
        data.append("Processors", processors).append("\n").append(super.toDebugString());
    } else {
        data.append("[chunk list:null]");
    }
    return data;
}

}
