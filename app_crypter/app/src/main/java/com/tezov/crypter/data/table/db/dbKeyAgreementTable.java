/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.crypter.data.table.db;

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.util.UtilsString;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import androidx.fragment.app.Fragment;
import static com.tezov.lib_java_android.database.sqlLite.dbTableDefinition.PRIMARY_KEY_FIELD;

import com.tezov.crypter.data.item.ItemKeyAgreement;
import com.tezov.lib_java_android.database.sqlLite.dbTable;
import com.tezov.lib_java_android.definition.defCreatable;
import com.tezov.lib_java.generator.uid.UUIDGenerator;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.wrapperAnonymous.PredicateThrowW;

public class dbKeyAgreementTable extends dbTable<ItemKeyAgreement>{
private final static int REMOVE_EXPIRED_SELECT_LENGTH = 20;
private final static int ENTRY_MAX = 60;
private final static int ENTRY_MAX_OVER_REMOVE = 20;

private dbKeyAgreementTable me(){
    return this;
}

@Override
public defCreatable<ItemKeyAgreement> factory(){
    return ItemKeyAgreement.getFactory();
}
@Override
public UUIDGenerator getUidGenerator(){
    return ItemKeyAgreement.getUidGenerator();
}

@Override
protected dbTable<ItemKeyAgreement>.Ref createRef(){
    return new dbKeyAgreementTable.Ref();
}

public class Ref extends dbTable<ItemKeyAgreement>.Ref{
    protected Ref(){
        super();
    }
    public void removeExpired(){
        synchronized(me()){
            int end = size();
            if(end > ENTRY_MAX){
                String sql = "DELETE FROM " + getTableDefinition().getName() + " WHERE " + PRIMARY_KEY_FIELD.getName() + " IN (SELECT " + PRIMARY_KEY_FIELD.getName() + " FROM " +
                             getTableDefinition().getName() + " ORDER BY " + PRIMARY_KEY_FIELD.getName() + " ASC LIMIT " + ENTRY_MAX_OVER_REMOVE + ");";
                db.execSQL(sql, null);
            }
            forEachDeleteNoThrow(new PredicateThrowW<>(){
                final long timestamp = Clock.MilliSecond.now();
                @Override
                public boolean test(ItemKeyAgreement item){
                    return (item.getTimestamp() < timestamp) && (remove(item.getUid()) != null);
                }
            }, REMOVE_EXPIRED_SELECT_LENGTH);
        }
    }

}

}
