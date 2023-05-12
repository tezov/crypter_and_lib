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
import static com.tezov.crypter.data.table.Descriptions.KEY_AGREEMENT;

import com.tezov.crypter.application.Application;
import com.tezov.crypter.data.dbItem.dbKeyAgreement;
import com.tezov.crypter.data.table.db.dbKeyAgreementTable;
import com.tezov.lib_java.async.Handler;
import com.tezov.lib_java.cipher.key.ecdh.KeyAgreement;
import com.tezov.lib_java.generator.uid.defUid;
import com.tezov.lib_java.type.runnable.RunnableW;

public class UtilsKeyAgreement{
private static Class<UtilsKeyAgreement> myCLass(){
    return UtilsKeyAgreement.class;
}
public static defUid save(KeyAgreement keyAgreement){
    ItemKeyAgreement item = ItemKeyAgreement.obtain().clear().setKey(keyAgreement);
    dbKeyAgreement dataKeyAgreement = new dbKeyAgreement(item);
    dataKeyAgreement.offer();
    Handler.SECONDARY().post(myCLass(), new RunnableW(){
        @Override
        public void runSafe(){
            dbKeyAgreementTable.Ref dbKeyAgreementTable = Application.tableHolderCipher().handle().newRef(KEY_AGREEMENT);
            Application.lockerTables().lock(myCLass());
            dbKeyAgreementTable.removeExpired();
            Application.lockerTables().unlock(myCLass());
        }
    });
    return dataKeyAgreement.getItem().getUid();
}
public static KeyAgreement get(defUid uid){
    dbKeyAgreementTable.Ref dbKeyAgreementTable = Application.tableHolderCipher().handle().getMainRef(KEY_AGREEMENT);
    Application.lockerTables().lock(myCLass());
    ItemKeyAgreement item = dbKeyAgreementTable.get(uid);
    Application.lockerTables().unlock(myCLass());
    if(item == null){
        return null;
    } else {
        return item.getKey();
    }
}
public static void remove(defUid uid){
    if(uid != null){
        Handler.SECONDARY().post(myCLass(), new RunnableW(){
            @Override
            public void runSafe(){
                dbKeyAgreementTable.Ref dbKeyAgreementTable = Application.tableHolderCipher().handle().getMainRef(KEY_AGREEMENT);
                Application.lockerTables().lock(myCLass());
                dbKeyAgreementTable.remove(uid);
                Application.lockerTables().unlock(myCLass());
            }
        });
    }
}

}
