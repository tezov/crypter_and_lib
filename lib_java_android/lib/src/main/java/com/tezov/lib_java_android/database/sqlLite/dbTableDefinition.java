/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.database.sqlLite;

import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.util.UtilsString;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import androidx.fragment.app.Fragment;
import com.tezov.lib_java.cipher.definition.defEncoder;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Nullify;
import com.tezov.lib_java.toolbox.Reflection;
import com.tezov.lib_java.type.collection.ListEntry;
import com.tezov.lib_java.type.primaire.Entry;
import com.tezov.lib_java.type.primaire.Field;
import com.tezov.lib_java.type.ref.WR;
import com.tezov.lib_java.util.UtilsList;
import com.tezov.lib_java.wrapperAnonymous.ComparatorW;
import com.tezov.lib_java.wrapperAnonymous.FunctionW;
import com.tezov.lib_java_android.database.TableDescription;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class dbTableDefinition{
public final static Field PRIMARY_KEY_FIELD;

static{
    PRIMARY_KEY_FIELD = new Field(Long.class, "_id");
    PRIMARY_KEY_FIELD.setIndex(0);
}

private final List<WR<Ref>> refs;
private final TableDescription tableDescription;
private final defEncoder encoderField;
private final ListEntry<dbField.Is, FieldDetails> fieldsDetails;
private final List<Field> fields;

public dbTableDefinition(TableDescription tableDescription, defEncoder encoderField){
DebugTrack.start().create(this).end();
    this.tableDescription = tableDescription;
    this.encoderField = encoderField;
    this.refs = new ArrayList<>();
    this.fieldsDetails = fieldsDetailsBuild();
    this.fields = new ArrayList<>();
    for(Entry<dbField.Is, FieldDetails> e: this.fieldsDetails){
        this.fields.add(e.value.field);
    }
}

private void alterFieldsDetails(ListEntry<dbField.Is, FieldDetails> fields){
    Iterator<Entry<dbField.Is, FieldDetails>> iterator = fields.iterator();
    for(int i = 1; iterator.hasNext(); i++){
        FieldDetails field = iterator.next().value;
        field.setIndex(i);
        TypeDB typeDB = field.getTypeDB();
        Class typeJava = field.getTypeJava();
        String extra = field.getExtra();
        switch(typeDB){
            case PRIMARY_KEY:{

DebugException.start()
                        .explode("Wrong type for " + field.getName() + ", PRIMARY_KEY is reserved and can not be used." + " Name:" + field.getName() + " typeDB:" + typeDB.name() + "TypeJava:" +
                                 typeJava.getSimpleName() + " extra:" + extra)

                        .end();


            }
            break;
            case ID:{

                if(!CompareType.UID.equal(typeJava)){
DebugException.start()
                            .explode("Wrong type for " + field.getName() + ",should have defUid interface." + " Name:" + field.getName() + "typeDB:" + typeDB.name() + " TypeJava:" +
                                     typeJava.getSimpleName() + " extra:" + extra)

                            .end();
                }

                field.setTypeJava(PRIMARY_KEY.class);
            }
            break;
            case FOREIGN_UID:{
                if(!CompareType.UID.equal(typeJava)){
DebugException.start()
                            .explode("Wrong type for " + field.getName() + ",should have defUid interface." + " Name:" + field.getName() + " typeDB:" + typeDB.name() + " TypeJava:" +
                                     typeJava.getSimpleName() + " extra:" + extra)

                            .end();
                }
            }
            break;
            case INT:{

                if(!CompareType.INT.equal(typeJava) && !CompareType.LONG.equal(typeJava)){
DebugException.start()
                            .explode("Wrong type for " + field.getName() + ",should be one of (Integer, Long) type." + " Name:" + field.getName() + " typeDB:" + typeDB.name() + " TypeJava:" +
                                     typeJava.getSimpleName() + " extra:" + extra)

                            .end();
                }


            }
            break;
            case REAL:{

                if(!CompareType.FLOAT.equal(typeJava)){
DebugException.start()
                            .explode("Wrong type for " + field.getName() + ", should be Float type." + " Name:" + field.getName() + " typeDB:" + typeDB.name() + " TypeJava:" +
                                     typeJava.getSimpleName() + " extra:" + extra)

                            .end();
                }


            }
            break;
            case BOOLEAN:{

                if(!CompareType.BOOLEAN.equal(typeJava)){
DebugException.start()
                            .explode("Wrong type for " + field.getName() + ", should be Boolean type." + " Name:" + field.getName() + " typeDB:" + typeDB.name() + " TypeJava:" +
                                     typeJava.getSimpleName() + " extra:" + extra)

                            .end();
                }


            }
            break;
            case DELETED:{

                if(!CompareType.BOOLEAN.equal(typeJava)){
DebugException.start()
                            .explode("Wrong type for " + field.getName() + ",should be Boolean type." + " Name:" + field.getName() + " typeDB:" + typeDB.name() + " TypeJava:" +
                                     typeJava.getSimpleName() + " extra:" + extra)

                            .end();
                }


            }
            break;
            case TEXT:{

                if(!CompareType.STRING.equal(typeJava) && !CompareType.INT.equal(typeJava) && !CompareType.LONG.equal(typeJava) && !CompareType.FLOAT.equal(typeJava) &&
                   !CompareType.BOOLEAN.equal(typeJava) && !CompareType.UID.equal(typeJava)){
DebugException.start()
                            .explode("Wrong type for " + field.getName() + ",should be one of (String, Integer, Long, Float, " + "Boolean, id) type." + " Name:" + field.getName() + " typeDB:" +
                                     typeDB.name() + "TypeJava:" + typeJava.getSimpleName() + " extra:" + extra)

                            .end();
                }


            }
            break;
            case BLOB:{
                field.setTypeJava(byte[].class);
            }
            break;
            case OBJECT_TO_STRING:{
                field.setTypeJava(String.class);
            }
            break;
        }
    }
}

private dbTableDefinition me(){
    return this;
}

private ListEntry<dbField.Is, FieldDetails> fieldsDetailsBuild(){
    ListEntry<dbField.Is, FieldDetails> fields = new ListEntry<dbField.Is, FieldDetails>();
    FunctionW<String, String> encoderName = new FunctionW<String, String>(){
        @Override
        public String apply(String s){
            if(encoderField != null){
                return (String)encoderField.encode(s, String.class);
            } else {
                return s;
            }
        }
    };
    Class<? extends dbField> typeField = tableDescription.getFieldType();
    java.lang.reflect.Field[] reflectFields = typeField.getDeclaredFields();
    for(java.lang.reflect.Field reflectField: reflectFields){
        if(reflectField.isAnnotationPresent(dbFieldAnnotation.class)){
            dbField.Is field = Reflection.findMember(reflectField, null);
            FieldDetails fieldDetails = new FieldDetails(field, reflectField, encoderName);
            fields.put(field, fieldDetails);
        }
    }
    java.lang.reflect.Field[] superReflectFields = dbField.class.getDeclaredFields();
    for(java.lang.reflect.Field reflectField: superReflectFields){
        if(reflectField.isAnnotationPresent(dbFieldAnnotation.class)){
            dbField.Is field = Reflection.findMember(reflectField, null);
            FieldDetails fieldDetails = new FieldDetails(field, reflectField, encoderName);
            fields.put(field, fieldDetails);
        }
    }
    fields = sortFieldsDetails(fields);
    alterFieldsDetails(fields);
    return fields;
}

private ListEntry<dbField.Is, FieldDetails> sortFieldsDetails(ListEntry<dbField.Is, FieldDetails> fields){
    ComparatorW<Entry<dbField.Is, FieldDetails>> criteria = new ComparatorW<Entry<dbField.Is, FieldDetails>>(){
        @Override
        public int compare(Entry<dbField.Is, FieldDetails> e1, Entry<dbField.Is, FieldDetails> e2){
            return Integer.compare(e1.value.getOrder(), e2.value.getOrder());
        }
    };
    return (ListEntry<dbField.Is, FieldDetails>)UtilsList.sort(fields, criteria, ListEntry::new);
}

final public void toDebugLog(){
DebugLog.start().send(toDebugString()).end();
}

public boolean containField(dbField.Is field){
    return fieldsDetails.getValue(field) != null;
}

private String statement_CREATE(Ref ref){
    StringBuilder data = new StringBuilder();
    if(encoderField == null){
        data.append("CREATE TABLE ").append(ref.getName()).append(" (");
        data.append(TypeDB.PRIMARY_KEY.statement(PRIMARY_KEY_FIELD.getName()));
        for(Entry<dbField.Is, FieldDetails> e: fieldsDetails){
            data.append(e.value.statement());
        }
        data.delete(data.length() - 2, data.length());
        data.append(")");
    } else {
        data.append("CREATE TABLE ").append(ref.getName()).append(" (");
        data.append(TypeDB.PRIMARY_KEY.statement(PRIMARY_KEY_FIELD.getName()));
        for(Entry<dbField.Is, FieldDetails> e: fieldsDetails){
            if((e.value.typeDB == TypeDB.PRIMARY_KEY) || (e.value.typeDB == TypeDB.ID)){
                data.append(e.value.statement());
            } else {
                data.append(e.value.statementText());
            }
        }
        data.delete(data.length() - 2, data.length());
        data.append(")");
    }
    return data.toString();
}

private String statement_DROP(Ref ref){
    return "DROP TABLE " + ref.getName();
}

private String statement_IS_EXIST(Ref ref){
    return "SELECT DISTINCT tbl_name FROM sqlite_master WHERE tbl_name IS '" + ref.getName() + "' AND type IS 'table'";
}

public Ref findRef(String tableNamePrefix){
    for(WR<Ref> r: refs){
        if(tableNamePrefix.equals(r.get().tableNamePrefix)){
            return r.get();
        }
    }
    return null;
}

private boolean isRefExist(String tableNamePrefix){
    return findRef(tableNamePrefix) != null;
}

public Ref newRef(String tableNamePrefix){
    return new Ref(tableNamePrefix);
}

public DebugString toDebugString(){
    DebugString data = new DebugString();
    for(Entry<dbField.Is, FieldDetails> e: fieldsDetails){
        data.append("[" + e.value.getName() + ":" + e.value.getTypeDB().name() + ":" + DebugTrack.getFullSimpleName(e.value.field.getType()) + "]");
    }
    return data;
}

@Override
protected void finalize() throws Throwable{
    refs.clear();
DebugTrack.start().destroy(this).end();
    super.finalize();
}

public enum TypeDB{
    PRIMARY_KEY("INTEGER PRIMARY KEY AUTOINCREMENT"), ID("BLOB NOT NULL UNIQUE"), FOREIGN_UID("BLOB NOT NULL"), TEXT("TEXT COLLATE NOCASE"), INT("INTEGER"), REAL("REAL"), BOOLEAN("TEXT"), DELETED(
            "TEXT"), BLOB("BLOB"), OBJECT_TO_STRING("TEXT COLLATE NOCASE");
    private final String constraint;
    TypeDB(String constraint){
        this.constraint = constraint;
    }
    public String getConstraint(){
        return constraint;
    }
    public String statement(String fieldName){
        return fieldName + " " + getConstraint() + ", ";
    }
    public String statementText(String fieldName){
        return fieldName + " TEXT, ";
    }
    public String statement(String fieldName, String extra){
        if(extra == null){
            return statement(fieldName);
        }
        if(this != FOREIGN_UID){
DebugException.start().log("Extra annotation for not foreign_uid ignored --> " + fieldName + ":" + super.name() + ":" + extra).end();
            return statement(fieldName);
        }
        String[] data = extra.split(",");

        if(data.length != 2){
DebugException.start().log(" Wrong number of value for foreign_uid extra annotation, should be. it " + "since " + data.length).end();
        }


        String foreignFieldTableName = data[0]; //NOW
        String foreignFieldName = data[1];
        String statement = statement(fieldName);
        //statement += "FOREIGN KEY("+ field +") REFERENCES " + foreignFieldTableName + "("+
        // foreignFieldName +")"  +  ", ";
        return statement;
    }
}

public static class PRIMARY_KEY{}

public static class FieldDetails{
    private final int order;
    private final TypeDB typeDB;
    private final String extra;
    private final Field field;
    FieldDetails(dbField.Is field, java.lang.reflect.Field reflectField, FunctionW<String, String> fieldEncoder){
DebugTrack.start().create(this).end();
        dbFieldAnnotation annotation = reflectField.getAnnotation(dbFieldAnnotation.class);
        order = annotation.order();
        typeDB = annotation.typeDB();
        Class type = annotation.typeJava();
        extra = Nullify.string(annotation.extra());
        String name = field.name();
        if(typeDB != TypeDB.ID){
            name = fieldEncoder.apply(name);
        }
        this.field = new Field(type, name);
    }
    public int getOrder(){
        return order;
    }

    public TypeDB getTypeDB(){
        return typeDB;
    }

    public Class getTypeJava(){
        return field.getType();
    }

    public void setTypeJava(Class type){
        field.setType(type);
    }

    public String getExtra(){
        return extra;
    }

    public Field getField(){
        return field;
    }

    public void setIndex(int index){
        field.setIndex(index);
    }

    public String getName(){
        return field.getName();
    }

    String statement(){
        return typeDB.statement(field.getName(), extra);
    }
    String statementText(){
        return typeDB.statementText(field.getName());
    }

    public DebugString toDebugString(){
        DebugString data = new DebugString();
        data.append("order", order);
        data.append("field", field);
        data.append("typeDB", typeDB);
        data.append("extra", extra);
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

public class Ref{
    private final String tableNamePrefix;
    private final String name;

    protected Ref(String tableNamePrefix){

        if(isRefExist(tableNamePrefix)){
DebugException.start().explode("table name prefix duplicate illegal").end();
        }

DebugTrack.start().create(this).end();
        this.tableNamePrefix = tableNamePrefix;
        this.name = tableDescription.name(tableNamePrefix);
        refs.add(WR.newInstance(this));
    }

    public TableDescription getTableDescription(){
        return tableDescription;
    }

    public defEncoder getEncoderField(){
        return encoderField;
    }

    public String getName(){
        return name;
    }

    public String getNameEncoded(){
        if(encoderField != null){
            return (String)encoderField.encode(name, String.class);
        } else {
            return name;
        }
    }

    public boolean containField(dbField.Is field){
        return me().containField(field);
    }

    public Field field(dbField.Is field){
        if(field == dbField.PRIMARY_KEY){
            return PRIMARY_KEY_FIELD;
        }
        FieldDetails details = fieldsDetails.getValue(field);
        if(details != null){
            return details.field;
        } else {
            return null;
        }
    }

    public String fieldName(dbField.Is field){
        return field(field).getName();
    }

    public Class fieldType(dbField.Is field){
        return field(field).getType();
    }

    public List<Field> getFields(){
        return fields;
    }

    public Field field(String name){
        if(name.equals(PRIMARY_KEY_FIELD.getName())){
            return PRIMARY_KEY_FIELD;
        }
        for(Field field: fields){
            if(field.getName().equals(name)){
                return field;
            }
        }

DebugException.start().explode("Field name " + name + " doesn't exist in table " + getName()).end();


        return null;
    }

    public String CREATE(){
        return me().statement_CREATE(this);
    }

    public String DROP(){
        return me().statement_DROP(this);
    }

    public String IS_EXIST(){
        return me().statement_IS_EXIST(this);
    }

    public DebugString toDebugString(){
        return me().toDebugString();
    }

    final public void toDebugLog(){
        me().toDebugLog();
    }

    @Override
    protected void finalize() throws Throwable{
        refs.remove(WR.newInstance(this));
DebugTrack.start().destroy(this).end();
        super.finalize();
    }

}

}
