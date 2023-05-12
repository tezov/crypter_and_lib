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
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.util.UtilsString;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import androidx.fragment.app.Fragment;
import static com.tezov.crypter.data.table.description.DescriptionHistory.Field.TYPE;

import com.tezov.crypter.data.item.ItemHistory;
import com.tezov.lib_java_android.database.sqlLite.dbTable;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilter;
import com.tezov.lib_java_android.definition.defCreatable;
import com.tezov.lib_java.generator.uid.UUIDGenerator;

public class dbHistoryTable extends dbTable<ItemHistory>{
private dbHistoryTable me(){
    return this;
}

@Override
public defCreatable<ItemHistory> factory(){
    return ItemHistory.getFactory();
}
@Override
public UUIDGenerator getUidGenerator(){
    return ItemHistory.getUidGenerator();
}

@Override
protected dbTable<ItemHistory>.Ref createRef(){
    return new dbHistoryTable.Ref();
}

public class Ref extends dbTable<ItemHistory>.Ref{
    protected Ref(){
        super();
    }

    synchronized public void remove(ItemHistory.Type type){
        synchronized(me()){
            dbFilter filter = getNullFilterNoSync().where(TYPE, type.name(), false);
            remove(filter);
        }
    }
    public int size(ItemHistory.Type type){
        synchronized(me()){
            dbFilter filter = getNullFilterNoSync().where(TYPE, type.name(), false);
            return size(filter);
        }


    }

}

}
