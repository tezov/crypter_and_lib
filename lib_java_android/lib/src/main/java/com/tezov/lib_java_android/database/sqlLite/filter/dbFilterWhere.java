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
import com.tezov.lib_java.toolbox.Clock;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import androidx.fragment.app.Fragment;

import static com.tezov.lib_java_android.database.sqlLite.filter.dbSign.EQUAL;

import com.tezov.lib_java_android.database.sqlLite.dbField;
import com.tezov.lib_java_android.database.sqlLite.dbTableDefinition;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.Chunk;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkProcessorList;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.defChunkProcessor;
import com.tezov.lib_java.generator.uid.defUid;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Nullify;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.wrapperAnonymous.FunctionW;
import com.tezov.lib_java.type.collection.ListEntry;
import com.tezov.lib_java.type.collection.ListOrObject;
import com.tezov.lib_java.type.primaire.Entry;
import com.tezov.lib_java.type.primitive.BytesTo;
import com.tezov.lib_java.util.UtilsNull;
import com.tezov.lib_java.util.UtilsString;

import java.util.ArrayList;
import java.util.List;

public class dbFilterWhere implements defChunkProcessor{
private final static String PREFIX = "WHERE ";
private final static String DELIMITER = " AND ";
private final dbFilter filter;
private boolean isCopy;
private ListEntry<Type.Is, ChunkProcessorList> chunkLists;
private Chunk builtChunk;

public dbFilterWhere(dbFilter filter){
    trackClass();
    this.filter = filter;
    clear();
}

private dbFilterWhere(dbFilterWhere filterWhere, dbFilter filter){
    trackClass();
    this.filter = filter;
    isCopy = true;
    chunkLists = filterWhere.chunkLists;
    builtChunk = filterWhere.builtChunk;
}

@Override
final public void toDebugLog(){
DebugLog.start().send(toDebugString()).end();
}

final public void toDebugLogStatement(){
    if(chunkLists != null){
        toChunk().toDebugLogStatement();
    } else {
DebugLog.start().send("statements is null").end();
    }
}

private void trackClass(){
DebugTrack.start().create(this).end();
}

public dbFilterWhere clear(){
    isCopy = false;
    chunkLists = null;
    builtChunk = null;
    return this;
}

public dbFilterWhere getShallowCopy(dbFilter dbFilter){
    return new dbFilterWhere(this, dbFilter);
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
    List<String> values = new ArrayList<>();
    String statement = UtilsString.join(PREFIX, DELIMITER, chunkLists, new FunctionW<Entry<Type.Is, ChunkProcessorList>, String>(){
        @Override
        public String apply(Entry<Type.Is, ChunkProcessorList> e){
            Chunk c = e.value.toChunk();
            if(c.getValues() != null){
                values.addAll(c.getValues());
            }
            return c.getStatement();
        }
    }).toString();
    builtChunk = new Chunk().setStatement(statement);
    List<String> valuesAdded = Nullify.collection(values);
    if(valuesAdded != null){
        builtChunk.addValues(valuesAdded);
    }
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

public void where(dbField.Is field, dbSign.Is sign, Object value, boolean retain){
    if(retain && (chunkLists != null) && isCopy){
DebugException.start().log("WHERE : This instance is shallow copy made with method shallowCopyFrom, this action will affect all copy included the original instance").end();
    }
    ChunkProcessorList chunkList = obtainChunkList(retain ? Type.RETAINED : Type.NOT_RETAINED);
    CompoundProcessor d = findCompoundProcessor(chunkList.processors(), field, sign);
    if(d == null){
        if(value != null){
            d = new CompoundProcessor(field, sign, value);
            chunkList.add(d);
        }
    } else {
        if(value == null){
            chunkList.remove(d);
        } else {
            d.value = value;
            chunkList.update(d);
        }
    }
    if(chunkList.isEmpty()){
        chunkLists.removeKey(retain ? Type.RETAINED : Type.NOT_RETAINED);
    }
    invalidate();
}
public void where(dbField.Is field, Object value, boolean retain){
    if(value instanceof dbSign.Is){
        where(field, (dbSign.Is)value, UtilsNull.NULL_OBJECT, retain);
    } else {
        where(field, EQUAL, value, retain);
    }
}
private CompoundProcessor findCompoundProcessor(List<CompoundProcessor> processors, dbField.Is field, dbSign.Is sign){
    if(processors != null){
        for(CompoundProcessor d: processors){
            if((d.field == field) && (d.sign == sign)){
                return d;
            }
        }
    }
    return null;
}

public void whereWeak(dbField.Is field, dbSign.Is sign, Object value){
    CompoundProcessor d = findCompoundProcessor(field, sign);
    if(d == null){
        if(value != null){
            ChunkProcessorList  chunkList = obtainChunkList(Type.NOT_RETAINED);
            d = new CompoundProcessor(field, sign, value);
            chunkList.add(d);
        }
    } else {
        if(d.value != value){
DebugException.start().log("WHERE WEAK: field " + field + " sign "+ sign +" exist as retained but value mismatches").end();
        }
    }
    invalidate();
}
public void whereWeak(dbField.Is field, Object value){
    if(value instanceof dbSign.Is){
        whereWeak(field, (dbSign.Is)value, UtilsNull.NULL_OBJECT);
    } else {
        whereWeak(field, EQUAL, value);
    }
}
private CompoundProcessor findCompoundProcessor(dbField.Is field, dbSign.Is sign){
    if(chunkLists != null){
        for(Entry<Type.Is, ChunkProcessorList> e:chunkLists){
            ListOrObject<CompoundProcessor> processors = e.value.processors();
            CompoundProcessor cp = findCompoundProcessor(processors, field, sign);
            if(cp != null){
                return cp;
            }
        }
    }
    return null;
}

public <UID extends defUid> void uidIn(dbField.Is field, List<UID> uidIn){
    ChunkProcessorList chunkList = obtainChunkList(Type.UID_IN);
    chunkList.clear().add(new ListUidProcessor(field, (List<defUid>)uidIn));
    invalidate();
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

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

public interface Type extends com.tezov.lib_java_android.database.sqlLite.filter.chunk.Type{
    Is UID_IN = new Is("UID_IN");

}

private class CompoundProcessor implements defChunkProcessor{
    dbField.Is field;
    dbSign.Is sign;
    Object value;

    CompoundProcessor(dbField.Is field, dbSign.Is sign, Object value){
DebugTrack.start().create(this).end();
        this.field = field;
        this.sign = sign;
        this.value = value;
    }

    @Override
    public Chunk toChunk(){
        Entry<Class, String> e = filter().getFieldDetail(field);
        Class type = e.key;
        String statement = e.value + sign.getSign();
        String value;
        if(this.value == UtilsNull.NULL_OBJECT){
            value = null;
        } else if((CompareType.LONG.equal(type)) || (CompareType.FLOAT.equal(type)) || (CompareType.INT.equal(type))){

            if(!(this.value instanceof Number)){
DebugException.start().explode("Not a number: " + this.value).end();
            }

            statement += this.value;
            value = null;
        } else if(CompareType.STRING.equal(type)){
            if(sign != dbSign.LIKE){
                statement += TOKEN_QUESTION;
            } else {
                statement += TOKEN_PERCENT;
            }
            if(this.value == null){
                value = "";
            } else {
                value = (String)this.value;
            }
        } else if(CompareType.BOOLEAN.equal(type)){

            if(!(this.value instanceof Boolean)){
DebugException.start().explode("Not a boolean: " + this.value).end();
            }

            statement += TOKEN_STRING.replace(TOKEN_QUESTION, ((boolean)this.value) ? "1" : "0");
            value = null;
        } else if(CompareType.UID.equal(type) || (type == dbTableDefinition.PRIMARY_KEY.class)){

            if(!(this.value instanceof defUid)){
DebugException.start().explode("Not an Uid: " + this.value).end();
            }
            String data = TOKEN_BLOB.replace(TOKEN_QUESTION, ((defUid)this.value).toHexString());
            if(sign == dbSign.LIKE){
                data = TOKEN_PERCENT.replace(TOKEN_QUESTION, data);
            }
            statement += data;
            value = null;
        } else if(CompareType.BYTES.equal(type)){
            if(!(this.value instanceof byte[])){
DebugException.start().explode("Not a byte array: " + this.value).end();
            }
            String data = TOKEN_BLOB.replace(TOKEN_QUESTION, BytesTo.StringHex((byte[])this.value));
            if(sign == dbSign.LIKE){
                data = TOKEN_PERCENT.replace(TOKEN_QUESTION, data);
            }
            statement += data;
            value = null;
        } else {
DebugException.start().explode("type unknown fields:" + field.name() + " type:" + type.getSimpleName()).end();
            value = null;
        }
        return new Chunk().setStatement(statement).addValue(value);
    }

    @Override
    public DebugString toDebugString(){
        DebugString data = new DebugString();
        data.append("field", field);
        data.append("sign", sign);
        data.append("value", value);
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

private class ListUidProcessor implements defChunkProcessor{ // IMPROVE All field, non only uid
    dbField.Is field;
    List<defUid> uidIn;

    ListUidProcessor(dbField.Is field, List<defUid> uidIn){
DebugTrack.start().create(this).end();
        this.field = field;
        this.uidIn = uidIn;
    }

    @Override
    public Chunk toChunk(){
        Chunk chunk = new Chunk();
        StringBuilder statement = new StringBuilder();
        statement.append(filter().getFieldName(field)).append(" IN(").append(UtilsString.join(",", uidIn, new FunctionW<defUid, String>(){
            @Override
            public String apply(defUid uid){
                return TOKEN_BLOB.replace(TOKEN_QUESTION, uid.toHexString());
            }
        })).append(")");
        chunk.setStatement(statement.toString());
        return chunk;
    }

    @Override
    public DebugString toDebugString(){
        DebugString data = new DebugString();
        data.append("uid list", uidIn);
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

