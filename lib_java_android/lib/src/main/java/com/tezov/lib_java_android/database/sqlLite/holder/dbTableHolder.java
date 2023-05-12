/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.database.sqlLite.holder;

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.util.UtilsString;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import androidx.fragment.app.Fragment;

import com.tezov.lib_java.cipher.definition.defEncoder;
import com.tezov.lib_java.cipher.holder.CipherHolderCrypto;
import com.tezov.lib_java_android.database.TableDescription;
import com.tezov.lib_java_android.database.adapter.definition.defContentValuesTo;
import com.tezov.lib_java_android.database.adapter.definition.defParcelTo;
import com.tezov.lib_java_android.database.sqlLite.adapter.AdapterHolderLocal;
import com.tezov.lib_java_android.database.sqlLite.adapter.defCursorTo;
import com.tezov.lib_java_android.database.sqlLite.dbTable;
import com.tezov.lib_java.toolbox.Reflection;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.collection.ListKey;
import com.tezov.lib_java.type.collection.ListOrObject;
import com.tezov.lib_java.wrapperAnonymous.FunctionW;

import java.util.List;

public abstract class dbTableHolder{
private final ListKey<String, TableDescription> tableDescriptions;
private dbTablesHandle dbHandle = null;

public dbTableHolder(){
DebugTrack.start().create(this).end();
    this.tableDescriptions = new ListKey<String, TableDescription>(new FunctionW<TableDescription, String>(){
        @Override
        public String apply(TableDescription description){
            return description.name();
        }
    });
}
public dbTablesHandle handle(){
    return dbHandle;
}

public List<TableDescription> getTableDescriptions(){
    return tableDescriptions;
}
public TableDescription getTableDescription(String name){
    return tableDescriptions.getValue(name);
}
public <H extends dbTableHolder> H addTableDescriptions(TableDescription description){
    tableDescriptions.add(description);
    return (H)this;
}
public <H extends dbTableHolder> H addTableDescriptions(List<TableDescription> descriptions){
    tableDescriptions.addAll(descriptions);
    return (H)this;
}

public abstract dbContext getContext();

public abstract dbTablesOpener getTablesOpener();

public abstract String getRootName();

protected abstract CipherHolderCrypto getCipherHolder();

protected abstract AdapterHolderLocal getAdapterHolder();

public boolean isOpen(){
    return dbHandle != null;
}

protected java.lang.Throwable beforeOpen(){
    return null;
}
protected java.lang.Throwable afterOpen(){
    return null;
}
public java.lang.Throwable open(){
    Throwable e = beforeOpen();
    if(e != null){
        return e;
    }
    e = openTables();
    if(e != null){
        return e;
    }
    e = afterOpen();
    return e;
}
protected java.lang.Throwable openTables(){
    ListKey<String, dbTable> dbTables = new ListKey<String, dbTable>(ListOrObject::new, new FunctionW<dbTable, String>(){
        @Override
        public String apply(dbTable table){
            return table.getName();
        }
    });
    AdapterHolderLocal adapters = getAdapterHolder();
    defParcelTo parcelTo = adapters.getParcelTo();
    defCursorTo cursorTo = adapters.getCursorTo();
    defContentValuesTo contentValuesTo = adapters.getContentValuesTo();

    CipherHolderCrypto cipherHolder = getCipherHolder();
    defEncoder encoderKey = cipherHolder != null ? cipherHolder.getEncoderKey() : null;

    for(TableDescription t: tableDescriptions){
        dbTable table = Reflection.newInstance(t.getLocalTableType())
                .setTableDescription(t, encoderKey)
                .setParcelTo(parcelTo)
                .setCursorTo(cursorTo)
                .setContentValuesTo(contentValuesTo);
        dbTables.add(table);
    }
    dbTablesOpener opener = getTablesOpener();
    opener.setTables(dbTables);
    dbHandle = new dbTablesHandle().open(opener);
    return null;
}

protected java.lang.Throwable beforeClose(){
    return null;
}
protected java.lang.Throwable afterClose(){
    return null;
}
public java.lang.Throwable close(){
    Throwable e = beforeClose();
    if(e != null){
        return e;
    }
    e = closeTables();
    if(e != null){
        return e;
    }
    e = afterClose();
    return e;
}
protected java.lang.Throwable closeTables(){
    if(dbHandle != null){
        dbHandle.close();
        dbHandle = null;
    }
    return null;
}

public void clear(){
    for(TableDescription t: tableDescriptions){
        dbHandle.getMainRef(t).clear();
    }
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}


}
