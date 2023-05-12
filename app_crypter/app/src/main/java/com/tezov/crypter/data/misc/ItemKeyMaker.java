/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.crypter.data.misc;

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
import static com.tezov.crypter.application.SharePreferenceKey.SP_DECRYPT_DELETE_FILE_BOOL;
import static com.tezov.crypter.application.SharePreferenceKey.SP_DECRYPT_OVERWRITE_FILE_BOOL;

import com.tezov.crypter.application.Application;
import com.tezov.crypter.data.dbItem.dbKey;
import com.tezov.crypter.data.item.ItemKey;
import com.tezov.crypter.data.item.ItemKeyRing;
import com.tezov.crypter.data_transformation.PasswordCipherL2;
import com.tezov.crypter.data_transformation.StreamDecoder;
import com.tezov.lib_java_android.application.SharedPreferences;
import com.tezov.lib_java.toolbox.Compare;

public class ItemKeyMaker extends StreamDecoder.ItemKeyMaker{
private final boolean isAlias;
private dbKey dataKey;
private char[] password;
public ItemKeyMaker(boolean isAlias, char[] password, dbKey dataKey){
    if(isAlias){
        this.isAlias = true;
        this.password = null;
        this.dataKey = dataKey;
    } else {
        this.isAlias = false;
        this.password = password;
        this.dataKey = null;
    }
}
@Override
protected void rebuildKey() throws Throwable{
    ItemKeyRing itemKeyRing;
    ItemKey itemKey;
    if(isAlias){
        itemKeyRing = dataKey.getDataKeyRing().getItem();
        itemKey = dataKey.getItem();
        byte[] specKey = getSpecKey();
        if((specKey != null) && !Compare.equals(itemKeyRing.getKeyKey().specToBytes(), specKey)){
            if(!itemKeyRing.hasPassword()){
                throw new Throwable("specKey mismatch");
            } else {
                itemKeyRing.rebuildKeyKey(getGuidKey(), getSpecKey());
                itemKeyRing.setUid(null);
            }
        }
        dataKey = null;
    } else {
        SharedPreferences sp = Application.sharedPreferences();
        itemKey = ItemKey.obtain().clear().generate(null, getGuidKey());
        itemKeyRing = ItemKeyRing.obtain().clear();
        byte[] specKey = getSpecKey();
        if(specKey == null){
            throw new Throwable("encryption done with strict mode");
        }
        itemKeyRing.rebuildKeyKey(PasswordCipherL2.fromClear(password), getGuidKey(), specKey);
        itemKey.setDecryptDeleteEncryptedFile(Compare.isTrue(sp.getBoolean(SP_DECRYPT_DELETE_FILE_BOOL)));
        itemKey.setDecryptOverwriteFile(Compare.isTrue(sp.getBoolean(SP_DECRYPT_OVERWRITE_FILE_BOOL)));
        password = null;
    }
    setItemKey(itemKey, itemKeyRing);
}

}
