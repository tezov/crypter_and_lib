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
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import androidx.fragment.app.Fragment;
import com.tezov.lib_java.type.collection.ListOrObject;
import com.tezov.lib_java_android.database.sqlLite.dbField;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.Chunk;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkProcessorList;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.Type;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.defChunkProcessor;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.collection.ListEntry;
import com.tezov.lib_java.type.primaire.Entry;
import com.tezov.lib_java.wrapperAnonymous.FunctionW;
import com.tezov.lib_java.util.UtilsString;

import java.util.List;

public class dbFilterOrder implements defChunkProcessor{
private final static String PREFIX = "ORDER BY ";
private final static String DELIMITER = " , ";
private final dbFilter filter;
private boolean isCopy;
private ListEntry<Type.Is, ChunkProcessorList> chunkLists;
private Chunk builtChunk;

public dbFilterOrder(dbFilter filter){
    trackClass();
    this.filter = filter;
    clear();
}

private dbFilterOrder(dbFilterOrder filterOrder, dbFilter filter){
    trackClass();
    this.filter = filter;
    isCopy = true;
    chunkLists = filterOrder.chunkLists;
    builtChunk = filterOrder.builtChunk;
}

@Override
final public void toDebugLog(){
DebugLog.start().send(toDebugString()).end();
}

private void trackClass(){
DebugTrack.start().create(this).end();
}

public dbFilterOrder clear(){
    isCopy = false;
    chunkLists = null;
    builtChunk = null;
    return this;
}

public dbFilterOrder getShallowCopy(dbFilter dbFilter){
    return new dbFilterOrder(this, dbFilter);
}

private dbFilter filter(){
    return filter;
}

private ChunkProcessorList obtainChunkList(Type.Is type){
    if(chunkLists == null){
        chunkLists = new ListEntry<>(ListOrObject::new);
    }
    ChunkProcessorList chunkList = chunkLists.getValue(type);
    if(chunkList == null){
        chunkList = new ChunkProcessorList();
        chunkLists.add(type, chunkList);
    }
    return chunkList;
}

public boolean isBuilt(){
    return (builtChunk != null) || !hasChunk();
}

public void invalidate(){
    builtChunk = null;
}

public void remove(Type.Is type){
    if((chunkLists != null) && chunkLists.removeKey(type) != null){
        invalidate();
    }
}

private void build(){
    String statement = UtilsString.join(PREFIX, DELIMITER, chunkLists, new FunctionW<Entry<Type.Is, ChunkProcessorList>, String>(){
        @Override
        public String apply(Entry<Type.Is, ChunkProcessorList> e){
            Chunk c = e.value.toChunk();
            return c.getStatement();
        }
    }).toString();
    builtChunk = new Chunk().setStatement(statement);
}

public boolean hasChunk(){
    return (chunkLists != null) && (!chunkLists.isEmpty());
}

@Override
public Chunk toChunk(){
    if(!isBuilt()){
        build();
    }
    return builtChunk;
}

public void order(dbField.Is field, Direction direction, boolean retain){
    if(retain && (chunkLists != null) && isCopy){
DebugException.start().log("ORDER : This instance is shallow copy made with method shallowCopyFrom, this action will affect all copy included the original instance").end();
    }
    ChunkProcessorList chunkList = obtainChunkList(retain ? Type.RETAINED : Type.NOT_RETAINED);
    CompoundProcessor d = findCompoundProcessor(chunkList.processors(), field);
    if(d == null){
        if(direction != null){
            d = new CompoundProcessor(field, direction);
            chunkList.add(d);
        }
    } else {
        if(direction == null){
            chunkList.remove(d);
        } else {
            d.direction = direction;
            chunkList.update(d);
        }
    }
    if(chunkList.isEmpty()){
        chunkLists.removeKey(retain ? Type.RETAINED : Type.NOT_RETAINED);
    }
    invalidate();
}
private CompoundProcessor findCompoundProcessor(List<CompoundProcessor> processors, dbField.Is field){
    if(processors != null){
        for(CompoundProcessor d: processors){
            if(d.field == field){
                return d;
            }
        }
    }
    return null;
}

public void orderWeak(dbField.Is field, Direction direction){
    CompoundProcessor d = findCompoundProcessor(field);
    if(d == null){
        if(direction != null){
            ChunkProcessorList  chunkList = obtainChunkList(Type.NOT_RETAINED);
            d = new CompoundProcessor(field, direction);
            chunkList.add(d);
        }
    } else {
        if(d.direction != direction){
DebugException.start().log("ORDER WEAK: field " + field + " exist as retained but direction mismatches").end();
        }
    }
    invalidate();
}
private CompoundProcessor findCompoundProcessor(dbField.Is field){
    if(chunkLists != null){
        for(Entry<Type.Is, ChunkProcessorList> e:chunkLists){
            ListOrObject<CompoundProcessor> processors = e.value.processors();
            CompoundProcessor cp = findCompoundProcessor(processors, field);
            if(cp != null){
                return cp;
            }
        }
    }
    return null;
}

@Override
public DebugString toDebugString(){
    DebugString data = new DebugString();
    data.append("isCopy", isCopy);
    if((chunkLists != null) && (!chunkLists.isEmpty())){
        data.append("chunk list", chunkLists);
    } else {
        data.append("[chunk list:null]");
    }
    return data;
}

final public void toDebugLogStatement(){
    if(chunkLists != null){
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


public enum Direction{
    ASC, DESC
}

private class CompoundProcessor implements defChunkProcessor{
    dbField.Is field;
    Direction direction;
    CompoundProcessor(dbField.Is field, Direction direction){
DebugTrack.start().create(this).end();
        this.field = field;
        this.direction = direction;
    }
    @Override
    public Chunk toChunk(){
        String statement = filter().getFieldName(field) + " " + direction.name();
        return new Chunk().setStatement(statement);
    }
    @Override
    public DebugString toDebugString(){
        DebugString data = new DebugString();
        data.append("field", field);
        data.append("direction", direction);
        return data;
    }
    @Override
    final public void toDebugLog(){
DebugLog.start().send(toDebugString()).end();
    }
    @Override
    protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
        super.finalize();
    }
}

}
