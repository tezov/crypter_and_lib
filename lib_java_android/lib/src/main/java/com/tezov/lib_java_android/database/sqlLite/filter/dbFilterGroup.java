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
import com.tezov.lib_java.toolbox.Nullify;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.collection.ListEntry;
import com.tezov.lib_java.type.primaire.Entry;
import com.tezov.lib_java.wrapperAnonymous.FunctionW;
import com.tezov.lib_java.util.UtilsString;

import java.util.List;

public final class dbFilterGroup implements defChunkProcessor{
private final static String PREFIX = "GROUP BY ";
private final static String DELIMITER = " , ";
private final dbFilter filter;
private boolean isCopy;
private ListEntry<Type.Is, ChunkProcessorList> chunkLists;
private Chunk builtChunk;

public dbFilterGroup(dbFilter filter){
    trackClass();
    this.filter = filter;
    clear();
}

private dbFilterGroup(dbFilterGroup filterGroup, dbFilter filter){
    trackClass();
    this.filter = filter;
    isCopy = true;
    chunkLists = filterGroup.chunkLists;
    builtChunk = filterGroup.builtChunk;
}

@Override
public void toDebugLog(){
DebugLog.start().send(toDebugString()).end();
}

private void trackClass(){
DebugTrack.start().create(this).end();
}

public dbFilterGroup clear(){
    isCopy = false;
    chunkLists = null;
    builtChunk = null;
    return this;
}

public dbFilterGroup getShallowCopy(dbFilter dbFilter){
    return new dbFilterGroup(this, dbFilter);
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
        chunkLists = Nullify.collection(chunkLists);
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

public void group(dbField.Is field, boolean flag, boolean retain){
    if(retain && (chunkLists != null) && isCopy){
DebugException.start().log("GROUP : This instance is shallow copy made with method shallowCopyFrom, this action will affect all copy included the original instance").end();
    }
    ChunkProcessorList chunkList = obtainChunkList(retain ? Type.RETAINED : Type.NOT_RETAINED);
    CompoundProcessor d = findCompoundProcessor(chunkList.processors(), field);
    if(flag && (d == null)){
        d = new CompoundProcessor(field);
        chunkList.add(d);
    } else {
        if(!flag && (d != null)){
            chunkList.remove(d);
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

public void groupWeak(dbField.Is field, boolean flag){
    CompoundProcessor d = findCompoundProcessor(field);
    if(d == null){
        if(flag){
            ChunkProcessorList  chunkList = obtainChunkList(Type.NOT_RETAINED);
            d = new CompoundProcessor(field);
            chunkList.add(d);
        }
    } else {
        if(!flag){
DebugException.start().log("GROUP WEAK: field " + field + " exist as retained but flag mismatches").end();
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

public void toDebugLogStatement(){
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

private class CompoundProcessor implements defChunkProcessor{
    dbField.Is field;

    CompoundProcessor(dbField.Is field){
DebugTrack.start().create(this).end();
        this.field = field;
    }

    @Override
    public Chunk toChunk(){
        return new Chunk().setStatement(filter().getFieldName(field));
    }

    @Override
    public DebugString toDebugString(){
        DebugString data = new DebugString();
        data.append("field", field);
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
