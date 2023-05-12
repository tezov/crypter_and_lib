/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.database.prebuild.sync.table;

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugTrack;
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
import androidx.fragment.app.Fragment;

import android.database.Cursor;

import com.tezov.lib_java_android.database.prebuild.sync.item.ItemSync;
import com.tezov.lib_java_android.database.prebuild.sync.item.ItemTransactionSync;
import com.tezov.lib_java_android.database.sqlLite.dbTable;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import com.tezov.lib_java_android.database.sqlLite.holder.dbTablesHandle;
import com.tezov.lib_java_android.definition.defCreatable;
import com.tezov.lib_java.generator.uid.UIDGenerator;
import com.tezov.lib_java.generator.uid.defUid;

import java.util.List;

import static com.tezov.lib_java_android.database.prebuild.sync.item.ItemTransactionSync.Direction;
import static com.tezov.lib_java_android.database.prebuild.sync.table.DescriptionTransactionSync.Field.DIRECTION;
import static com.tezov.lib_java_android.database.prebuild.sync.table.DescriptionTransactionSync.Field.FAILED_MESSAGE;
import static com.tezov.lib_java_android.database.prebuild.sync.table.DescriptionTransactionSync.Field.SYNC_UID;
import static com.tezov.lib_java_android.database.prebuild.sync.table.DescriptionTransactionSync.Field.TIMESTAMP;
import static com.tezov.lib_java_android.database.prebuild.sync.table.DescriptionTransactionSync.Field.TRANSACTION_NAME;
import static com.tezov.lib_java_android.database.prebuild.sync.table.DescriptionTransactionSync.Field.TYPE_SYNC;
import static com.tezov.lib_java_android.database.sqlLite.dbField.PRIMARY_KEY;
import static com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder.Direction.ASC;
import static com.tezov.lib_java_android.database.sqlLite.filter.dbSign.NULL;

public class dbTransactionSync extends dbTable<ItemTransactionSync>{
private static UIDGenerator gen = null;
private dbTransactionSync me(){
    return this;
}
@Override
public void setDatabase(dbTablesHandle db){
    if(gen == null){
        String primaryKeyName = getTableDefinition().fieldName(PRIMARY_KEY);
        ChunkCommand statement = new ChunkCommand();
        statement.setCommand("SELECT " + primaryKeyName + " FROM " + getTableDefinition().getName());
        statement.setStatement(" ORDER BY " + primaryKeyName + " DESC LIMIT 1");
        Cursor cursor = db.rawQuery(statement);
        long lastUID = 0L;
        if(cursor.getCount() > 0){
            cursor.moveToFirst();
            lastUID = cursor.getLong(0);
        }
        cursor.close();
        gen = UIDGenerator.newInstance(lastUID);
    }
    super.setDatabase(db);
}

@Override
public defCreatable<ItemTransactionSync> factory(){
    return ItemTransactionSync.getFactory();
}

@Override
public UIDGenerator getUidGenerator(){
    return gen;
}

@Override
protected dbTable<ItemTransactionSync>.Ref createRef(){
    return new Ref();
}

public class Ref extends dbTable<ItemTransactionSync>.Ref{
    protected Ref(){
        super();
    }

    public ItemTransactionSync getFirst(String transactionName, Direction direction, ItemSync.Type type, defUid uid){
        synchronized(me()){
            return super.get(getFilterNoSync().where(TRANSACTION_NAME, transactionName, false)
                    .where(DIRECTION, direction.name(), false)
                    .where(TYPE_SYNC, type.name(), false)
                    .where(SYNC_UID, uid, false)
                    .where(FAILED_MESSAGE, NULL, false)
                    .order(TIMESTAMP, ASC, false));
        }
    }

    public List<ItemTransactionSync> select(String transactionName, Direction direction){
        synchronized(me()){
            return super.select(
                    getFilterNoSync().where(TRANSACTION_NAME, transactionName, false).where(DIRECTION, direction.name(), false).where(FAILED_MESSAGE, NULL, false).order(TIMESTAMP, ASC, false));
        }
    }

    public List<ItemTransactionSync> select(String transactionName){
        synchronized(me()){
            return super.select(getFilterNoSync().where(TRANSACTION_NAME, transactionName, false).where(FAILED_MESSAGE, NULL, false).order(TIMESTAMP, ASC, false));
        }
    }

}

}
