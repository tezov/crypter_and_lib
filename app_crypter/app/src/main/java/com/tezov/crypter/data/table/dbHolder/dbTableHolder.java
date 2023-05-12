/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.crypter.data.table.dbHolder;

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
import static com.tezov.lib_java_android.database.adapter.holder.AdapterHolder.Mode.NONE;

import com.tezov.crypter.data.dbItem.dbHistory;
import com.tezov.crypter.data.dbItem.dbKey;
import com.tezov.crypter.data.table.Descriptions;
import com.tezov.crypter.data.table.context.dbDataUsersContext;
import com.tezov.lib_java.cipher.holder.CipherHolderCrypto;
import com.tezov.lib_java_android.database.sqlLite.adapter.AdapterHolderLocal;
import com.tezov.lib_java_android.database.sqlLite.holder.dbContext;

import java.util.Arrays;

public class dbTableHolder extends com.tezov.lib_java_android.database.sqlLite.holder.dbTableHolder{

public dbTableHolder(){
    addTableDescriptions(Arrays.asList(Descriptions.KEY, Descriptions.HISTORY));
}
@Override
public dbTablesOpener getTablesOpener(){
    return new dbTablesOpener(getContext(), getRootName());
}
@Override
public dbContext getContext(){
    return dbDataUsersContext.getInstance();
}
@Override
public String getRootName(){
    return "tables";
}

@Override
protected CipherHolderCrypto getCipherHolder(){
    return null;
}

@Override
protected AdapterHolderLocal getAdapterHolder(){
    return new AdapterHolderLocal(NONE, null);
}

@Override
protected Throwable afterOpen(){
    dbKey.updater.open();
    dbHistory.updater.open();
    return null;
}

@Override
protected Throwable afterClose(){
    dbHistory.updater.close();
    dbKey.updater.close();
    return null;
}

public void delete(){
    if(isOpen()){
        clear();
        close();
    }
    getContext().delete(getRootName());
}


}
