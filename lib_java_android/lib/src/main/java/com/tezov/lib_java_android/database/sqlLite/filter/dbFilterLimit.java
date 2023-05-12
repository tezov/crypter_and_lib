/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.database.sqlLite.filter;

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

import com.tezov.lib_java_android.database.sqlLite.filter.chunk.Chunk;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.Type;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.defChunkProcessor;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java.debug.DebugTrack;

public class dbFilterLimit implements defChunkProcessor{
private final static String PREFIX = "LIMIT ";
private final static String DELIMITER = ",";

private final dbFilter filter;
private boolean isCopy;
private Chunk builtChunk;

private Integer offset;
private Integer limit;

public dbFilterLimit(dbFilter filter){
    trackClass();
    this.filter = filter;
    clear();
}

private dbFilterLimit(dbFilterLimit filterWhere, dbFilter filter){
    trackClass();
    this.filter = filter;
    isCopy = true;
    builtChunk = filterWhere.builtChunk;
    offset = filterWhere.offset;
    limit = filterWhere.limit;
}

private void trackClass(){
DebugTrack.start().create(this).end();
}

public dbFilterLimit clear(){
    isCopy = false;
    builtChunk = null;
    offset = null;
    limit = null;
    return this;
}

public dbFilterLimit getShallowCopy(dbFilter dbFilter){
    return new dbFilterLimit(this, dbFilter);
}

private dbFilter filter(){
    return filter;
}

public boolean isBuilt(){
    return (builtChunk != null) || !hasChunk();
}

public void invalidate(){
    builtChunk = null;
}

public void remove(Type.Is type){
    if(type == Type.NOT_RETAINED){
        if(limit != null){
            limit = null;
            offset = null;
            invalidate();
        }
    } else {
DebugException.start().unknown("type", type).end();
    }
}

private void build(){
    StringBuilder data = new StringBuilder();
    if(limit != null){
        if(offset == null){
            data.append(PREFIX).append(limit);
        } else {
            data.append(PREFIX).append(offset).append(DELIMITER).append(limit);
        }
    }
    builtChunk = new Chunk().setStatement(data.toString());
}

public boolean hasChunk(){
    return (limit != null);
}

@Override
public Chunk toChunk(){
    if(!isBuilt()){
        build();
    }
    return builtChunk;
}

public void first(){
    filter().forward();
    invalidate();
    offset = 0;
    limit = 1;
}
public void last(){
    filter().reverse();
    invalidate();
    offset = 0;
    limit = 1;
}
public void at(Integer index){
    filter().forward();
    invalidate();
    offset = index;
    limit = 1;
}
public void range(Integer index, Integer length){
    filter().forward();
    invalidate();
    length(length);
    offset(index);
}
private void offset(Integer offset){
    if((offset != null) && (offset < 0)){
        if(limit != null){
            limit = limit + offset;
            if(limit < 0){
                limit = 0;
            }
        }
        offset = 0;
    }
    this.offset = offset;
}
private void length(Integer length){
    if((length != null) && (length < 0)){
        length = 0;
    }
    this.limit = length;
}

@Override
public DebugString toDebugString(){
    DebugString data = new DebugString();
    data.append("isCopy", isCopy);
    if(builtChunk != null){
        data.append("builtChunk", builtChunk);
    } else {
        data.append("offset", offset);
        data.append("limit", limit);
    }
    return data;
}

@Override
final public void toDebugLog(){
DebugLog.start().send(toDebugString()).end();
}

final public void toDebugLogStatement(){
    if(limit != null){
        toChunk().toDebugLogStatement();
    } else {
DebugLog.start().send("statements is null").end();
    }
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

}
