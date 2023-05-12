/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.crypter.data.dbItem;

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
import static com.tezov.crypter.application.AppConfig.KEY_AGREEMENT_RETAIN_DELAY_ms;

import com.tezov.crypter.application.Application;
import com.tezov.crypter.data.item.ItemKeyAgreement;
import com.tezov.crypter.data.table.dbItemHolder.dbItemKeyAgreementUpdater;
import com.tezov.crypter.data.table.dbItemHolder.dbItemUpdater;
import com.tezov.lib_java_android.database.prebuild.trash.item.ItemTrash;
import com.tezov.lib_java_android.database.sqlLite.dbItem.dbItem;
import com.tezov.lib_java.generator.uid.defUid;
import com.tezov.lib_java.toolbox.Clock;

public class dbKeyAgreement extends dbItem<ItemKeyAgreement>{
final public static dbItemKeyAgreementUpdater updater = new dbItemKeyAgreementUpdater();

public dbKeyAgreement(){
}
public dbKeyAgreement(ItemKeyAgreement item){
    super(item);
}
private static Class<dbKeyAgreement> myClass(){
    return dbKeyAgreement.class;
}
@Override
protected dbItemUpdater<ItemKeyAgreement> getUpdater(){
    return updater;
}
@Override
protected ItemKeyAgreement newItem(){
    return ItemKeyAgreement.obtain().clear();
}
@Override
protected dbItem<ItemKeyAgreement> newData(){
    return new dbKeyAgreement(null);
}
@Override
public dbKeyAgreement copy(){
    return (dbKeyAgreement)super.copy();
}

@Override
public boolean offer(){
    ItemKeyAgreement item = getItem();
    if(item.canOffer()){
        item.setTimestamp(Clock.MilliSecond.now() + KEY_AGREEMENT_RETAIN_DELAY_ms);
        Application.lockerTables().lock(myClass());
        boolean result = super.offer();
        Application.lockerTables().unlock(myClass());
        return result;
    }
    return false;
}
@Override
public dbItem<ItemKeyAgreement> putToTrash(defUid requester, ItemTrash.Type.Is type){
    return null;
}
@Override
public dbItem<ItemKeyAgreement> restoreFromTrash(){
    return null;
}


}
