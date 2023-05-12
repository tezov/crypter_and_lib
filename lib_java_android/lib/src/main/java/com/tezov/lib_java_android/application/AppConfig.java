/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.application;

import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;

import com.tezov.lib_java_android.BuildConfig;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.util.Log;

import com.tezov.lib_java.application.AppUUIDGenerator;
import com.tezov.lib_java.cipher.holder.CipherHolderXor;
import com.tezov.lib_java.cipher.misc.PasswordCipher;
import com.tezov.lib_java.generator.uid.UUID;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.type.collection.ListEntry;
import com.tezov.lib_java_android.util.UtilsResourceRaw;
import com.tezov.lib_java.util.UtilsString;

import static com.tezov.lib_java.application.AppConfigKey.CIPHER_DEFAULT_KEY_OBFUSC_TRANSFORMATION;
import static com.tezov.lib_java.application.AppConfigKey.CIPHER_DEFAULT_KEY_XOR_LENGTH;
import static com.tezov.lib_java.application.AppConfigKey.CIPHER_HOLDER_OBFUSC_TRANSFORMATION;
import static com.tezov.lib_java_android.application.AppConfigKey.DEBUG_LOG_CONFIG_ENCRYPTED;
import static com.tezov.lib_java_android.application.AppConfigKey.GUID;
import static com.tezov.lib_java.application.AppConfigKey.DEBUG_LOG_TRACK_HANDLER;
import static com.tezov.lib_java_android.application.AppConfigKey.DEBUG_LOG_ON_DEVICE;
import static com.tezov.lib_java_android.application.AppResources.NOT_A_RESOURCE;
import static com.tezov.lib_java_android.application.SharePreferenceKey.FILE_NAME_SHARE_PREFERENCE;
import static com.tezov.lib_java.application.AppConfigKey.SPEC_CONFIG;
import static com.tezov.lib_java.cipher.UtilsMessageDigest.Mode.SHA1;

public class AppConfig extends com.tezov.lib_java.application.AppConfig{
private final static String META_DATA_CONFIG_NAME = "com.tezov.lib.CONFIG";
private final static String SUFFIX_JSON = "_json";
private final static String SUFFIX_CFG = "_cfg";

public static void init(ApplicationSystem application){
    setKeyAdapter(application.configNewKeyAdapter());
    try{
        Builder builder = new Builder();
        if(BuildConfig.DEBUG_ONLY){
            Integer id = findMetaDataConfig(SUFFIX_JSON);
            if(id != null){
                buildPropertiesFromJson(builder, application, id);
            }
            else{
                id = findMetaDataConfig(SUFFIX_CFG);
                if(id != null){
                    buildPropertiesFromEncrypted(builder, application, id);
                }
            }
        }
        else{
            Integer id = findMetaDataConfig(SUFFIX_CFG);
            if(id != null){
                buildPropertiesFromEncrypted(builder, application, id);
            }
            else{
                id = findMetaDataConfig(SUFFIX_JSON);
                if(id != null){
                    buildPropertiesFromJson(builder, application, id);
                }
            }
        }
        if(AppConfig.getProperties() == null){
            throw new Throwable("properties are null, raw file not found");
        }
        if(AppConfig.getProperties().hasKey(SPEC_CONFIG.getId())){
            if(builder.getCipher() == null){
                builder.buildCipher(getPassword(application));
            }
            buildGuid(builder.getCipher());
        }
    }
    catch(Throwable e){
//        Log.d(DebugLog.TAG, e.getClass().getSimpleName() + ":" + e.getMessage());
    }
}
private static Integer findMetaDataConfig(String suffix) throws Throwable{
    Bundle bundle = AppContext.getMetaData();
    String configFileName = bundle.getString(META_DATA_CONFIG_NAME);
    if(configFileName == null){
        throw new Throwable("meta retrofit.data raw config file file name missing");
    }
    int id = AppContext.getResources().getIdentifier(AppResources.IdentifierType.raw, configFileName + suffix);
    return id == NOT_A_RESOURCE ? null : id;
}
private static PasswordCipher getPassword(ApplicationSystem application){
    return PasswordCipher.fromClear(application.configPassword());
}
private static void buildPropertiesFromJson(Builder builder, ApplicationSystem application, int id){
    ListEntry<String, String> in = UtilsResourceRaw.JsonToList(id);
    if(in == null){
        throw new NullPointerException("properties is null");
    }
    builder.buildProperties_Convert(in);
    setProperties(builder.getProperties());
}
private static void buildPropertiesFromEncrypted(Builder builder, ApplicationSystem application, int id){
    String filesData = UtilsResourceRaw.toString(id);
    buildPropertiesFromEncrypted(builder, application, filesData);
}
private static void buildPropertiesFromEncrypted(Builder builder, ApplicationSystem application, String filesData){
    if(filesData == null){
        throw new NullPointerException("properties is null");
    }
    builder.buildProperties_ExtractUnencrypted(filesData);
    setProperties(builder.getProperties());
    builder.buildCipher(getPassword(application))
        .buildProperties_ExtractEncrypted();
}
private static void buildGuid(CipherHolderXor cipher){
    SharedPreferences sp = new SharedPreferences(FILE_NAME_SHARE_PREFERENCE);
    sp.setCipherHolder(cipher);
    String uidString = sp.getString(SharePreferenceKey.APP_GUID);
    UUID guid;
    if(uidString == null){
        guid = AppUUIDGenerator.next();
        sp.put(SharePreferenceKey.APP_GUID, guid.toHexString());
    } else {
        guid = UUID.fromHexString(uidString);
    }
    getProperties().put(GUID.getId(), guid.toHexString());
}

public static void toDebugLogPropertiesToJsonEncrypted(ApplicationSystem application){
    Generator generator = new Generator(getProperties());
    generator
        .addUnencryptedKey(CIPHER_DEFAULT_KEY_OBFUSC_TRANSFORMATION.getId())
        .addUnencryptedKey(CIPHER_DEFAULT_KEY_XOR_LENGTH.getId())
        .addUnencryptedKey(CIPHER_HOLDER_OBFUSC_TRANSFORMATION.getId());
    generator
        .addExcludeKey(GUID.getId())
        .addExcludeKey(DEBUG_LOG_CONFIG_ENCRYPTED.getId())
        .addExcludeKey(DEBUG_LOG_ON_DEVICE.getId())
        .addExcludeKey(DEBUG_LOG_TRACK_HANDLER.getId());
    generator.generate(getPassword(application));
    setProperties(generator.getProperties());
    generator.verify();
DebugLog.start().send("DEV FINGER_PRINT_SHA1_DEV : " + UtilsString.insert(PackageSignature.get(SHA1), ':', 2)).end();
    generator.toDebugLog();
}
public static SharedPreferences newSharedPreferencesEncrypted(){
    SharedPreferences sharedPreferences = new SharedPreferences(FILE_NAME_SHARE_PREFERENCE);
    CipherHolderXor cipher = buildCipher(PasswordCipher.fromClear(
            AppConfig.getString(GUID.getId()).toCharArray()), AppConfig.getString(SPEC_CONFIG.getId()));
    sharedPreferences.setCipherHolder(cipher);
    return sharedPreferences;
}

}
