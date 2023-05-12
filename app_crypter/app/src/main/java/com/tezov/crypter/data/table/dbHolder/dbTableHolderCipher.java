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
import static com.tezov.crypter.application.AppConfig.CIPHER_TABLES_MODE;
import static com.tezov.crypter.application.SharePreferenceKey.SP_USER_TABLES_SPEC_STRING;
import static com.tezov.crypter.application.SharePreferenceKey.SP_USER_TABLES_TRIAL_KEY_STRING;
import static com.tezov.crypter.application.SharePreferenceKey.SP_USER_TABLES_TRIAL_VALUE_STRING;
import static com.tezov.lib_java_android.database.adapter.holder.AdapterHolder.Mode.NONE;

import com.tezov.crypter.application.Application;
import com.tezov.crypter.data.dbItem.dbKeyAgreement;
import com.tezov.crypter.data.dbItem.dbKeyRing;
import com.tezov.crypter.data.table.Descriptions;
import com.tezov.crypter.data.table.context.dbDataUsersContext;
import com.tezov.lib_java_android.application.SharedPreferences;
import com.tezov.lib_java_android.authentification.User;
import com.tezov.lib_java.buffer.ByteBufferPacker;
import com.tezov.lib_java.cipher.dataAdapter.string.DataStringAdapter;
import com.tezov.lib_java.cipher.holder.CipherHolderCrypto;
import com.tezov.lib_java.cipher.misc.PasswordCipher;
import com.tezov.lib_java_android.database.sqlLite.adapter.AdapterHolderLocal;
import com.tezov.lib_java_android.database.sqlLite.holder.dbContext;
import com.tezov.lib_java_android.database.sqlLite.holder.dbTableHolder;

import java.util.Arrays;

public class dbTableHolderCipher extends dbTableHolder{
private CipherHolderCrypto cipherHolder = null;

public dbTableHolderCipher(){
    addTableDescriptions(Arrays.asList(Descriptions.KEY_RING, Descriptions.KEY_AGREEMENT));
}
private User getUser(){
    return Application.userAuth().getUser();
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
    return "tablesCipher";
}

@Override
protected CipherHolderCrypto getCipherHolder(){
    return cipherHolder;
}

@Override
protected AdapterHolderLocal getAdapterHolder(){
    return new AdapterHolderLocal(CIPHER_TABLES_MODE, cipherHolder);
}

private void generateCipher(){
    SharedPreferences sp = Application.sharedPreferences();
    cipherHolder = new CipherHolderCrypto.Generator().setPasswordKeyAndValue(PasswordCipher.fromClear(getUser().getKeyId().toCharArray()))
            .setFormatKey(DataStringAdapter.Format.BASE49)
            .setFormatValue(DataStringAdapter.Format.BASE64)
            .setPacker(new ByteBufferPacker())
            .generate();
    String specKeys = cipherHolder.specToString();
    String trialData = getUser().getVid();
    String trialKey = cipherHolder.encodeKey(trialData);
    String trialValue = cipherHolder.encodeValue(trialData);
    sp.put(SP_USER_TABLES_SPEC_STRING, specKeys);
    sp.put(SP_USER_TABLES_TRIAL_KEY_STRING, trialKey);
    sp.put(SP_USER_TABLES_TRIAL_VALUE_STRING, trialValue);
}
private boolean rebuildCipher(){
    SharedPreferences sp = Application.sharedPreferences();
    String specKeys = sp.getString(SP_USER_TABLES_SPEC_STRING);
    String trialKey = sp.getString(SP_USER_TABLES_TRIAL_KEY_STRING);
    String trialValue = sp.getString(SP_USER_TABLES_TRIAL_VALUE_STRING);
    String trialData = getUser().getVid();
    if((specKeys != null) && (trialKey != null) && (trialValue != null)){
        cipherHolder = new CipherHolderCrypto.Builder().setPasswordKeyAndValue(PasswordCipher.fromClear(getUser().getKeyId().toCharArray()))
                .setSpec(specKeys)
                .setPacker(new ByteBufferPacker())
                .build();
        trialKey = cipherHolder.decodeKey(trialKey);
        trialValue = cipherHolder.decodeValue(trialValue);
        if(trialData.equals(trialKey) && trialData.equals(trialValue)){
            return true;
        } else {
            cipherHolder.destroy();
            cipherHolder = null;
            sp.remove(SP_USER_TABLES_SPEC_STRING);
            sp.remove(SP_USER_TABLES_TRIAL_KEY_STRING);
            sp.remove(SP_USER_TABLES_TRIAL_VALUE_STRING);
        }
    }
    return false;
}
@Override
protected Throwable beforeOpen(){
    if(CIPHER_TABLES_MODE != NONE){
        if(!rebuildCipher()){
            generateCipher();
        }
    }
    return super.beforeOpen();
}

@Override
protected Throwable afterOpen(){
    dbKeyAgreement.updater.open();
    dbKeyRing.updater.open();
    return null;
}
@Override
protected Throwable afterClose(){
    if(cipherHolder != null){
        cipherHolder.destroy();
        cipherHolder = null;
    }
    dbKeyRing.updater.close();
    dbKeyAgreement.updater.close();
    return null;
}

public void delete(){
    getContext().delete(getRootName());
    SharedPreferences sp = Application.sharedPreferences();
    sp.remove(SP_USER_TABLES_SPEC_STRING);
    sp.remove(SP_USER_TABLES_TRIAL_KEY_STRING);
    sp.remove(SP_USER_TABLES_TRIAL_VALUE_STRING);
}


}
