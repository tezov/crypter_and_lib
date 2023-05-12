/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.crypter.data.item;

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
import static com.tezov.crypter.data.table.Descriptions.KEY;

import com.tezov.crypter.application.AppInfo;
import com.tezov.crypter.application.Application;
import com.tezov.crypter.data.dbItem.dbKey;
import com.tezov.crypter.data.table.db.dbKeyTable;
import com.tezov.crypter.data_transformation.PasswordCipherL2;
import com.tezov.lib_java.cipher.key.KeySim;
import com.tezov.lib_java.generator.uid.UUID;
import com.tezov.lib_java.generator.uid.defUid;

public class UtilsKey{
private static Class<UtilsKey> myCLass(){
    return UtilsKey.class;
}

public static dbKey generate(String alias, PasswordCipherL2 password, KeySim.Transformation transformation, KeySim.Length length){
    UUID guid = AppInfo.getGUID();
    ItemKey key = ItemKey.obtain().generate(alias, guid);
    ItemKeyRing itemKeyRing = ItemKeyRing.obtain().generateKey(password, guid, transformation, length);
    return new dbKey(key).setKeyRing(itemKeyRing);
}
public static boolean isOwner(dbKey dataKey){
    dataKey.loadKeyRing(false);
    ItemKey itemKey = dataKey.getItem();
    ItemKeyRing itemKeyRing = dataKey.getDataKeyRing().getItem();
    return isOwner(itemKey, itemKeyRing);
}
public static boolean isOwner(ItemKey itemKey, ItemKeyRing itemKeyRing){
    return itemKey.isOwner() && itemKeyRing.isOwner();
}

public static ItemKey get(defUid uid){
    dbKeyTable.Ref dbKeyTable = Application.tableHolder().handle().getMainRef(KEY);
    Application.lockerTables().lock(myCLass());
    ItemKey item = dbKeyTable.get(uid);
    Application.lockerTables().unlock(myCLass());
    return item;
}

}
