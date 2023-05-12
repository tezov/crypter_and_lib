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
import androidx.fragment.app.Fragment;

import com.tezov.lib_java_android.database.sqlLite.dbField;
import com.tezov.lib_java_android.database.sqlLite.dbTableDefinition;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.Chunk;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.Type;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder.Direction;
import com.tezov.lib_java.generator.uid.defUid;
import com.tezov.lib_java.toolbox.Iterable;
import com.tezov.lib_java.toolbox.Nullify;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.collection.ListEntry;
import com.tezov.lib_java.type.collection.ListOrObject;
import com.tezov.lib_java.type.primaire.Entry;
import com.tezov.lib_java.type.primaire.Field;
import com.tezov.lib_java.wrapperAnonymous.FunctionW;
import com.tezov.lib_java.util.UtilsString;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.tezov.lib_java_android.database.sqlLite.dbField.PRIMARY_KEY;
import static com.tezov.lib_java_android.database.sqlLite.dbField.UID;
import static com.tezov.lib_java_android.database.sqlLite.filter.dbSign.INF_EQUAL;
import static com.tezov.lib_java_android.database.sqlLite.filter.dbSign.SUP_EQUAL;

public final class dbFilter{
private final static String ALIAS_AS = " AS ";
private final static String ALIAS_TOKEN = ".";
private final dbFilterWhere filterWhere;
private final dbFilterGroup filterGroup;
private final dbFilterOrder filterOrder;
private final dbFilterLimit filterLimit;
private final ListEntry<String, DefinitionDetails> definitions;
private boolean isCopy;
private ChunkCommand builtChunk = null;
private dbField.Is primaryField;
private Direction primaryDirection;

public dbFilter(){
    trackClass();
    this.primaryField = PRIMARY_KEY;
    this.primaryDirection = Direction.ASC;
    this.definitions = new ListEntry<String, DefinitionDetails>(ListOrObject::new);
    filterWhere = new dbFilterWhere(this);
    filterGroup = new dbFilterGroup(this);
    filterOrder = new dbFilterOrder(this);
    filterLimit = new dbFilterLimit(this);
    clear();
}

private dbFilter(dbFilter filter){
    trackClass();
    this.primaryField = filter.primaryField;
    this.primaryDirection = filter.primaryDirection;
    this.definitions = filter.definitions;
    isCopy = true;
    builtChunk = filter.builtChunk;
    filterWhere = filter.filterWhere.getShallowCopy(this);
    filterOrder = filter.filterOrder.getShallowCopy(this);
    filterGroup = filter.filterGroup.getShallowCopy(this);
    filterLimit = filter.filterLimit.getShallowCopy(this);
}

private void trackClass(){
DebugTrack.start().create(this).end();
}

public dbField.Is getPrimaryField(){
    return primaryField;
}
public Direction getPrimaryDirection(){
    return primaryDirection;
}
public dbFilter setPrimary(dbField.Is field, Direction direction){
    if(field == null){
        field = PRIMARY_KEY;
    }
    this.primaryField = field;
    if(direction == null){
        direction = Direction.ASC;
    }
    this.primaryDirection = direction;
    return this;
}

public dbFilter addDefinition(dbTableDefinition.Ref definition){
    ForeignLink foreignLink = null;
    if(definitions.size() > 0){
        String foreignFieldName = definition.getName() + "_" + UID.name();
        Iterator<Entry<String, DefinitionDetails>> it = Iterable.Reversed.from(definitions).iterator();
        for(int i = definitions.size() - 1; it.hasNext(); i--){
            dbTableDefinition.Ref definitionParent = it.next().value.definition;
            List<dbField.Is> fields = dbField.Is.getValuesInstanceOf(dbField.Is.class);
            for(dbField.Is field: fields){
                if(!field.name().equals(foreignFieldName)){
                    continue;
                }
                if(!definitionParent.containField(field)){
                    continue;
                }
                foreignLink = new ForeignLink(i, field);
                break;
            }
            if(foreignLink != null){
                break;
            }
        }

        if(foreignLink == null){
DebugException.start().explode("foreign link not found").end();
        }

    }
    DefinitionDetails details = new DefinitionDetails(definition);
    if(foreignLink != null){
        details.setForeignLink(foreignLink);
    }
    definitions.put(definition.getName(), details);
    return this;
}

private DefinitionDetails details(int index){
    return definitions.getValueAt(index);
}

private DefinitionDetails details(String alias){
    return definitions.getValue(alias);
}

private boolean isSingle(){
    return definitions.size() == 1;
}

public dbTableDefinition.Ref table(){
    return details(0).definition;
}

public dbTableDefinition.Ref table(int index){
    return details(index).definition;
}

public dbTableDefinition.Ref table(String alias){
    return details(alias).definition;
}

public String tableAlias(){
    return definitions.getKeyAt(0);
}

public String tableAlias(int index){
    return definitions.getKeyAt(index);
}

public List<Field> fields(int tableIndex){
    return table(tableIndex).getFields();
}

public List<Field> fields(String tableAlias){
    return table(tableAlias).getFields();
}

public List<Field> fields(){
    if(isSingle()){
        return fields(0);
    } else {
        List<Field> fields = new ArrayList<>();
        for(Entry<String, DefinitionDetails> e: definitions){
            fields.addAll(e.value.definition.getFields());
        }
        return Nullify.collection(fields);
    }
}

private String aliasTransform(String value){
    return value.toLowerCase();
}

private String tableNameTransform(String tableName, String alias){
    return tableName + ALIAS_AS + aliasTransform(alias);
}

private String fieldNameTransform(String fieldName, String alias){
    return aliasTransform(alias) + ALIAS_TOKEN + fieldName;
}

public String tableName(){
    return table().getName();
}

public String tableName(int tableIndex){
    if(isSingle()){
        return table(tableIndex).getName();
    } else {
        Entry<String, DefinitionDetails> e = definitions.get(tableIndex);
        return tableNameTransform(e.value.definition.getName(), e.key);
    }
}

public String tableName(String tableAlias){
    if(isSingle()){
        return table(tableAlias).getName();
    } else {
        return tableNameTransform(table(tableAlias).getName(), tableAlias);
    }
}

public String getFieldName(int tableIndex, dbField.Is key){
    if(isSingle()){
        return table(tableIndex).fieldName(key);
    } else {
        Entry<String, DefinitionDetails> e = definitions.get(tableIndex);
        return fieldNameTransform(e.value.definition.fieldName(key), e.key);
    }
}

public String getFieldName(String tableAlias, dbField.Is key){
    if(isSingle()){
        return table(tableAlias).fieldName(key);
    } else {
        return fieldNameTransform(table(tableAlias).fieldName(key), tableAlias);
    }
}

public String getFieldName(dbField.Is key){
    if(isSingle()){
        return table().fieldName(key);
    } else {
        for(Entry<String, DefinitionDetails> e: definitions){
            Field field = e.value.definition.field(key);
            if(field != null){
                return fieldNameTransform(field.getName(), e.key);
            }
        }
    }
    return null;
}

public Entry<Class, String> getFieldDetail(dbField.Is key){
    if(isSingle()){
        Field field = table().field(key);
        return new Entry<>(field.getType(), field.getName());
    } else {
        for(Entry<String, DefinitionDetails> e: definitions){
            Field field = e.value.definition.field(key);
            if(field != null){
                return new Entry<>(field.getType(), fieldNameTransform(field.getName(), e.key));
            }
        }
    }

DebugException.start().explode("field not found").end();

    return null;
}

public dbFilter clear(){
    isCopy = false;
    builtChunk = null;

    filterWhere.clear();
    filterOrder.clear();
    filterGroup.clear();
    filterLimit.clear();

    return this;
}

public dbFilter getShallowCopy(){
    return new dbFilter(this);
}

public boolean isBuilt(){
    return (builtChunk != null);
}

public void invalidate(){
    //        boolean isBuild = filterWhere.isBuilt() && filterGroup.isBuilt()
    //                && filterOrder.isBuilt() && filterLimit.isBuilt(); //IMPROVE make it work...
    builtChunk = null;
}

public void invalidateAll(){
    builtChunk = null;
    filterWhere.invalidate();
    filterOrder.invalidate();
    filterGroup.invalidate();
    filterLimit.invalidate();
}

private void build(){
    List<Chunk> chunkLists = new ArrayList<>();
    if(filterWhere.hasChunk()){
        chunkLists.add(filterWhere.toChunk());
    }
    if(filterGroup.hasChunk()){
        chunkLists.add(filterGroup.toChunk());
    }
    if(filterOrder.hasChunk()){
        chunkLists.add(filterOrder.toChunk());
    }
    if(filterLimit.hasChunk()){
        chunkLists.add(filterLimit.toChunk());
    }
    String statement;
    List<String> values;
    if(chunkLists.size() > 0){
        values = new ArrayList<>();
        statement = UtilsString.join(" ", " ", chunkLists, new FunctionW<Chunk, String>(){
            @Override
            public String apply(Chunk c){
                if(c.getValues() != null){
                    values.addAll(c.getValues());
                }
                return c.getStatement();
            }
        }).toString();
    } else {
        statement = null;
        values = null;
    }
    builtChunk = new ChunkCommand();
    builtChunk.setStatement(statement);
    List<String> valuesAdded = Nullify.collection(values);
    if(valuesAdded != null){
        builtChunk.addValues(valuesAdded);
    }
    filterWhere.remove(Type.NOT_RETAINED);
    filterOrder.remove(Type.NOT_RETAINED);
    filterGroup.remove(Type.NOT_RETAINED);
    filterLimit.remove(Type.NOT_RETAINED);
}

public ChunkCommand toChunk(){
    if(!isBuilt()){
        build();
    }
    return builtChunk;
}

// FILTER
public dbFilter where(dbField.Is field, dbSign.Is sign, Object value, boolean retain){
    filterWhere.where(field, sign, value, retain);
    return this;
}
public dbFilter where(dbField.Is field, Object value, boolean retain){
    filterWhere.where(field, value, retain);
    return this;
}
public dbFilter whereWeak(dbField.Is field, dbSign.Is sign, Object value){
    filterWhere.whereWeak(field, sign, value);
    return this;
}
public dbFilter whereWeak(dbField.Is field, Object value){
    filterWhere.whereWeak(field, value);
    return this;
}

public dbFilter order(dbField.Is field, Direction direction, boolean retain){
    filterOrder.order(field, direction, retain);
    return this;
}
public dbFilter orderWeak(dbField.Is field, Direction direction){
    filterOrder.orderWeak(field, direction);
    return this;
}

public dbFilter group(dbField.Is field, boolean flag, boolean retain){
    filterGroup.group(field, flag, retain);
    return this;
}
public dbFilter groupWeak(dbField.Is field, boolean flag){
    filterGroup.groupWeak(field, flag);
    return this;
}

public dbFilter uid(defUid uid){
    filterWhere.whereWeak(UID, uid);
    return this;
}
public dbFilter between(defUid uidStart, defUid uidStop){
    filterWhere.whereWeak(UID, SUP_EQUAL, uidStart);
    filterWhere.whereWeak(UID, INF_EQUAL, uidStop);
    return this;
}
public <UID extends defUid> dbFilter uidIn(dbField.Is field, List<UID> uidIn){
    filterWhere.uidIn(field, uidIn);
    return this;
}
public dbFilter forward(){
    filterOrder.orderWeak(getPrimaryField(), getPrimaryDirection());
    return this;
}
public dbFilter reverse(){
    Direction direction = getPrimaryDirection() == Direction.ASC? Direction.DESC : Direction.ASC;
    filterOrder.orderWeak(getPrimaryField(), direction);
    return this;
}
public dbFilter indexOf(Object value){
    dbField.Is field = getPrimaryField();
    Direction direction = getPrimaryDirection();
    filterOrder.orderWeak(field, direction);
    if(direction == Direction.ASC){
        filterWhere.whereWeak(field, INF_EQUAL, value);
    }
    else if (direction == Direction.DESC){
        filterWhere.whereWeak(field, SUP_EQUAL, value);
    }
    return this;
}
public dbFilter first(){
    filterLimit.first();
    return this;
}
public dbFilter last(){
    filterLimit.last();
    return this;
}
public dbFilter at(Integer index){
    filterLimit.at(index);
    return this;
}
public dbFilter range(Integer index, Integer length){
    filterLimit.range(index, length);
    return this;
}

// STATEMENT
private void appendInnerJoin(StringBuilder command){
    Iterator<Entry<String, DefinitionDetails>> it = definitions.iterator();
    it.next(); //ignore first
    while(it.hasNext()){
        Entry<String, DefinitionDetails> e = it.next();
        DefinitionDetails details = e.value;
        command.append(" INNER JOIN ").append(tableNameTransform(details.definition.getName(), e.key));
        command.append(" on ").append(getFieldName(details.foreignLink.tableIndex, details.foreignLink.field));
        command.append(" = ").append(fieldNameTransform(details.definition.fieldName(UID), e.key));
    }
}

private String stringFields(){
    String alias = tableAlias(0);
    return fieldNameTransform(table().fieldName(PRIMARY_KEY), alias) + " , " + UtilsString.join(" , ", fields(0), new FunctionW<Field, String>(){
        @Override
        public String apply(Field field){
            return fieldNameTransform(field.getName(), alias);
        }
    }).toString();
}

public ChunkCommand statementSize(){
    ChunkCommand chunk = toChunk();
    if(isSingle()){
        chunk.setCommand("SELECT " + getFieldName(PRIMARY_KEY) + " FROM " + tableName());
    } else {
        StringBuilder command = new StringBuilder();
        command.append("SELECT ").append(getFieldName(PRIMARY_KEY)).append(" FROM ").append(tableName(0));
        appendInnerJoin(command);
        chunk.setCommand(command.toString());
    }
    return chunk;
}

public ChunkCommand statementSelect(){
    ChunkCommand chunk = toChunk();
    if(isSingle()){
        chunk.setCommand("SELECT * FROM " + tableName());
    } else {
        StringBuilder command = new StringBuilder();
        command.append("SELECT ").append(stringFields()).append(" FROM ").append(tableName(0));
        appendInnerJoin(command);
        chunk.setCommand(command.toString());
    }
    return chunk;
}

public ChunkCommand statementSelectField(dbField.Is field){
    ChunkCommand chunk = toChunk();
    if(isSingle()){
        chunk.setCommand("SELECT " + getFieldName(field) + " FROM " + tableName());
    } else {
        StringBuilder command = new StringBuilder();
        command.append("SELECT ").append(getFieldName(field)).append(" FROM ").append(tableName(0));
        appendInnerJoin(command);
        chunk.setCommand(command.toString());
    }
    return chunk;
}

public ChunkCommand statementRemove(){
    ChunkCommand chunk = toChunk();
    if(isSingle()){
        chunk.setCommand("DELETE FROM " + tableName());
        return chunk;
    } else {
DebugException.start().explode("incorrect definitions size, must be 1").end();
    }

    return null;
}

public DebugString toDebugString(){
    DebugString data = new DebugString();
    data.append("isCopy", isCopy);
    data.append("primaryField", primaryField);
    data.append("primaryField", primaryField);
    if(builtChunk != null){
        data.append("builtChunk", builtChunk);
    } else {
        data.append("\n").append("filterWhere", filterWhere);
        data.append("\n").append("filterGroup", filterGroup);
        data.append("\n").append("filterOrder", filterOrder);
        data.append("\n").append("filterLimit", filterLimit);
    }
    return data;
}

public void toDebugLog(){
DebugLog.start().send(toDebugString()).end();
}

public void toDebugLogSql(){
    ChunkCommand chunk = toChunk();
    chunk.toDebugLogSql();
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

private static class ForeignLink{
    Integer tableIndex;
    dbField.Is field;

    ForeignLink(Integer tableIndex, dbField.Is field){
        this.tableIndex = tableIndex;
        this.field = field;
    }

}

private static class DefinitionDetails{
    dbTableDefinition.Ref definition;
    ForeignLink foreignLink;

    DefinitionDetails(dbTableDefinition.Ref definition){
        this.definition = definition;
        this.foreignLink = null;
    }

    void setForeignLink(ForeignLink foreignLink){
        this.foreignLink = foreignLink;
    }

}

}
