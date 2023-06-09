/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.crypter.application;

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
public enum AppConfigKey{
    DATABASE_VERSION(1),
    FILE_ENCRYPTED_VERSION(2),
    CIPHER_TABLES_MODE(3),
    KEY_AGREEMENT_RETAIN_DELAY_days(4),
    AD_SUGGEST_PAID_VERSION_MODULO(5),
    ADMOB_TRIAL_TIME_OVER(6),
    ADMOB_TRIAL_TIME_INTERSTITIAL_days(7),
    ADMOB_TRIAL_TIME_BANNER_days(8),
    ADMOB_INTERSTITIAL_DELAY_START_s(9),
    ADMOB_INTERSTITIAL_DELAY_CYCLIC_mn(10),
    ADMOB_FAIL_MAX_TIME_ALLOWED_days(11),
    ADMOB_FAIL_MAX_COUNT_ALLOWED(12),
    ADMOB_FAIL_UNTRUST_MIN_COUNT_RETABLISH(13),
    ADMOB_FAIL_UNTRUST_MAX_TIME_ALLOWED_mn(14);
int id;
AppConfigKey(int id){
    this.id = id + com.tezov.lib_java_android.application.AppConfigKey.getLastIndex();
}
public static AppConfigKey find(int id){
    for(AppConfigKey k: values()){
        if(k.id == id){
            return k;
        }
    }
    return null;
}
public static String getName(int id){
    AppConfigKey key = find(id);
    if(key != null){
        return key.name();
    } else {
        return null;
    }
}
public static AppConfigKey find(String name){
    for(AppConfigKey k: values()){
        if(k.name().equals(name)){
            return k;
        }
    }
    return null;
}
public static Integer getId(String name){
    AppConfigKey key = find(name);
    if(key != null){
        return key.id;
    } else {
        return null;
    }
}
public int getId(){
    return id;
}

public static class ConfigKeyAdapter extends com.tezov.lib_java_android.application.AppConfigKey.Adapter{
    @Override
    public Integer toIndex(String name){
        AppConfigKey key = find(name);
        if(key != null){
            return key.id;
        } else {
            return super.toIndex(name);
        }
    }
    @Override
    public String fromIndex(Integer index){
        AppConfigKey key = find(index);
        if(key != null){
            return key.name();
        } else {
            return super.fromIndex(index);
        }
    }

}
}
