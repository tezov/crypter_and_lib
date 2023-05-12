/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.crypter.application;

import com.tezov.crypter.data.item.ItemHistory;
import com.tezov.crypter.data.table.db.dbHistoryTable;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.util.UtilsString;
import java.util.LinkedList;
import java.util.Set;

import com.tezov.lib_java_android.ads.adMax.AdMaxInstance;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import com.tezov.lib_java_android.application.AppContext;

import androidx.fragment.app.Fragment;
import static com.tezov.crypter.application.SharePreferenceKey.SP_ALIAS_FORGET_BOOLEAN;
import static com.tezov.crypter.application.SharePreferenceKey.SP_ALIAS_LOAD_BOOLEAN;
import static com.tezov.crypter.application.SharePreferenceKey.SP_CIPHER_TEXT_REMEMBER_FORMAT_STRING;
import static com.tezov.crypter.application.SharePreferenceKey.SP_DECRYPT_DELETE_FILE_BOOL;
import static com.tezov.crypter.application.SharePreferenceKey.SP_DECRYPT_OVERWRITE_FILE_BOOL;
import static com.tezov.crypter.application.SharePreferenceKey.SP_ENCRYPT_ADD_DEEPLINK_TO_TEXT_BOOL;
import static com.tezov.crypter.application.SharePreferenceKey.SP_ENCRYPT_ADD_TIME_AND_DATE_TO_FILE_NAME_BOOL;
import static com.tezov.crypter.application.SharePreferenceKey.SP_ENCRYPT_DELETE_FILE_ORIGINAL_BOOL;
import static com.tezov.crypter.application.SharePreferenceKey.SP_ENCRYPT_FILE_NAME_BOOL;
import static com.tezov.crypter.application.SharePreferenceKey.SP_ENCRYPT_KEY_LENGTH_INT;
import static com.tezov.crypter.application.SharePreferenceKey.SP_ENCRYPT_KEY_TRANSFORMATION_INT;
import static com.tezov.crypter.application.SharePreferenceKey.SP_ENCRYPT_OVERWRITE_FILE_BOOL;
import static com.tezov.crypter.application.SharePreferenceKey.SP_ENCRYPT_SHARE_SUBJECT_PREFIX_STRING;
import static com.tezov.crypter.application.SharePreferenceKey.SP_ENCRYPT_SIGN_TEXT_BOOL;
import static com.tezov.crypter.application.SharePreferenceKey.SP_ENCRYPT_STRICT_MODE_BOOL;
import static com.tezov.crypter.application.SharePreferenceKey.SP_HISTORY_FILE_DELETE_ON_CLOSE_BOOLEAN;
import static com.tezov.crypter.application.SharePreferenceKey.SP_KEYSTORE_AUTO_CLOSE_DELAY_INT;
import static com.tezov.crypter.application.SharePreferenceKey.SP_KEYSTORE_KEEP_OPEN_BOOLEAN;
import static com.tezov.crypter.application.SharePreferenceKey.SP_KEYSTORE_KEEP_OPEN_DELAY_INT;
import static com.tezov.crypter.application.SharePreferenceKey.SP_KEY_SHARE_REMEMBER_FORMAT_STRING;
import static com.tezov.crypter.application.SharePreferenceKey.SP_NAVIGATION_LAST_DESTINATION_STRING;
import static com.tezov.crypter.application.SharePreferenceKey.SP_OWNED_NO_ADS_INT;
import static com.tezov.crypter.data.table.Descriptions.HISTORY;
import static com.tezov.crypter.navigation.NavigationHelper.DestinationKey.INFO;
import static com.tezov.lib_java_android.application.SharePreferenceKey.makeKey;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.tezov.crypter.R;
import com.tezov.crypter.data.item.ItemKey;
import com.tezov.crypter.data.table.dbHolder.dbTableHolder;
import com.tezov.crypter.data.table.dbHolder.dbTableHolderCipher;
import com.tezov.crypter.dialog.DialogMenuFormat;
import com.tezov.crypter.navigation.NavigationHelper;
import com.tezov.crypter.user.UserAuth;
import com.tezov.lib_java_android.application.AppConfig;
import com.tezov.lib_java_android.application.AppInfo;
import com.tezov.lib_java_android.application.ApplicationSystem;
import com.tezov.lib_java_android.application.ConnectivityManager;
import com.tezov.lib_java_android.application.SharedPreferences;
import com.tezov.lib_java.async.LockThread;
import com.tezov.lib_java.generator.uid.defUid;
import com.tezov.lib_java.toolbox.Compare;
import com.tezov.lib_java.toolbox.Nullify;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java_android.wrapperAnonymous.ActivityLifecycleCallbacksW;
import com.tezov.lib_java_android.ui.misc.TransitionManagerAnimation;

import java.util.List;

public class Application extends com.tezov.lib_java_android.application.Application{
public final static String SKU_NO_ADS = AppContext.getResources().getString(R.string.billing_sku_no_ads);
private static dbTableHolder tableHolder = null;
private static dbTableHolderCipher tableHolderCipher = null;
private static LockThread<ApplicationSystem> lockTablesThread = null;
private static AdMaxInstance adMob = null;
private static ActivityLifecycleCallbacksW callbacksListener = null;

private static Class<Application> myClass(){
    return Application.class;
}

public static void onMainActivityStart(ApplicationSystem app, Intent source, boolean isRestarted){
    com.tezov.lib_java_android.application.Application.onMainActivityStart(app, source, isRestarted);
    if(!isRestarted){
        Context context = app.getApplicationContext();
        if(state == null){
            state = new State();
        }
        state.onMainActivityStart(app, source);
        transitionManager = new TransitionManagerAnimation();
        navigationHelper = new NavigationHelper();
        sharedPreferences(AppConfig.newSharedPreferencesEncrypted());
        if(AppInfo.isFirstLaunch()){
            setDefaultSharePreference();
        }
        connectivityManager = new ConnectivityManager();
        userAuth = new UserAuth();
        lockTablesThread = new LockThread<>(app);
        lockTablesThread.lock(myClass());
        tableHolder = new dbTableHolder();
        tableHolderCipher = new dbTableHolderCipher();
        lockTablesThread.unlock(myClass());
        adMob = new AdMaxInstance().onMainActivityStart(context);
        callbacksListener = new ActivityLifecycleCallbacksW(){
            boolean isStarted = false;
            @Override
            public void onActivityResumed(@NonNull Activity activity){
                if(isStarted && (AppContext.getActivity() == activity)){
                    isStarted = false;
                    ReceiverAlarmKeystore.cancel(activity.getApplicationContext());
                }
            }
            @Override
            public void onActivityStopped(@NonNull Activity activity){
                if(!isStarted  && (AppContext.getActivity() == activity)){
                    isStarted = true;
                    UserAuth.updateSessionTimestamp();
                    ReceiverAlarmKeystore.start(activity.getApplicationContext());
                }
            }
        };
        app.registerActivityLifecycleCallbacks(callbacksListener);
    }
}
public static void onApplicationPause(ApplicationSystem app){
    com.tezov.lib_java_android.application.Application.onApplicationPause(app);
}
public static void onApplicationClose(ApplicationSystem app){
    if(callbacksListener != null){
        app.unregisterActivityLifecycleCallbacks(callbacksListener);
        callbacksListener = null;
    }
    if(adMob != null){
        adMob.clearPendings();
        adMob = null;
    }
    if(lockTablesThread != null){
        lockerTables().lock(myClass());
        if(Compare.isTrue(sharedPreferences.getBoolean(SP_HISTORY_FILE_DELETE_ON_CLOSE_BOOLEAN))){
            dbHistoryTable.Ref table = Application.tableHolder().handle().getMainRef(HISTORY);
            table.remove(ItemHistory.Type.FILE);
        }
        if(tableHolder != null){
            if(tableHolder.isOpen()){
                tableHolder.close();
            }
            tableHolder = null;
        }
        if(tableHolderCipher != null){
            if(tableHolderCipher.isOpen()){
                tableHolderCipher.close();
            }
            tableHolderCipher = null;
        }
        lockerTables().unlock(myClass());
        lockTablesThread = null;
    }
    userAuth = null;
    if(connectivityManager != null){
        connectivityManager.unregisterReceiver(true);
        connectivityManager = null;
    }
    sharedPreferences = null;
    navigationHelper = null;
    transitionManager = null;
    if(state != null){
        state.onApplicationClose(app);
    }
    com.tezov.lib_java_android.application.Application.onApplicationClose(app);
}

public static UserAuth userAuth(){
    return (UserAuth)userAuth;
}
public static LockThread<ApplicationSystem> lockerTables(){
    return lockTablesThread;
}
public static dbTableHolder tableHolder(){
    if(!tableHolder.isOpen()){
        Throwable e = tableHolder.open();
        if(e != null){
DebugException.start().log(e).end();
        }
    }
    return tableHolder;
}
public static dbTableHolderCipher tableHolderCipher(){
    return tableHolderCipher;
}

private static void setDefaultSharePreference(){
    SharedPreferences sp = sharedPreferences();
    sp.put(SP_ENCRYPT_SHARE_SUBJECT_PREFIX_STRING, (String)null);
    sp.put(SP_NAVIGATION_LAST_DESTINATION_STRING, INFO.name());
    sp.put(SP_KEY_SHARE_REMEMBER_FORMAT_STRING, DialogMenuFormat.Format.FILE_TEXT.name());
    sp.put(SP_CIPHER_TEXT_REMEMBER_FORMAT_STRING, DialogMenuFormat.Format.TEXT.name());

    ItemKey item = ItemKey.obtain().clear();
    sp.put(SP_ENCRYPT_OVERWRITE_FILE_BOOL, item.mustEncryptOverwriteFile());
    sp.put(SP_ENCRYPT_FILE_NAME_BOOL, item.mustEncryptFileName());
    sp.put(SP_ENCRYPT_ADD_TIME_AND_DATE_TO_FILE_NAME_BOOL, item.mustEncryptAddTimeAndTimeToFileName());
    sp.put(SP_ENCRYPT_DELETE_FILE_ORIGINAL_BOOL, item.mustEncryptDeleteOriginalFile());
    sp.put(SP_ENCRYPT_STRICT_MODE_BOOL, item.mustEncryptStrictMode());
    sp.put(SP_DECRYPT_OVERWRITE_FILE_BOOL, item.mustDecryptOverwriteFile());
    sp.put(SP_DECRYPT_DELETE_FILE_BOOL, item.mustDecryptDeleteEncryptedFile());
    sp.put(SP_ENCRYPT_SIGN_TEXT_BOOL, item.mustEncryptSigneText());
    sp.put(SP_ENCRYPT_ADD_DEEPLINK_TO_TEXT_BOOL, item.mustEncryptAddDeepLinkToText());

    sp.put(SP_KEYSTORE_AUTO_CLOSE_DELAY_INT, 5);
    sp.put(SP_KEYSTORE_KEEP_OPEN_BOOLEAN, false);
    sp.put(SP_KEYSTORE_KEEP_OPEN_DELAY_INT, 7);
    sp.put(SP_ALIAS_FORGET_BOOLEAN, true);
    sp.put(SP_ALIAS_LOAD_BOOLEAN, false);
    sp.put(SP_HISTORY_FILE_DELETE_ON_CLOSE_BOOLEAN, false);

    sp.put(SP_ENCRYPT_KEY_TRANSFORMATION_INT, 2);
    sp.put(SP_ENCRYPT_KEY_LENGTH_INT, 2);
}
public static AdMaxInstance adMob(){
    return adMob;
}
public static boolean isOwnedNoAds(){
    SharedPreferences sp = Application.sharedPreferences();
    return Compare.isTrue(sp.getBoolean(makeKey(SP_OWNED_NO_ADS_INT, getState().sessionUid().toHexString())));
}
public static void setOwnedNoAds(boolean flag){
    SharedPreferences sp = Application.sharedPreferences();
    List<String> previous = sp.findKeyStartWith(SP_OWNED_NO_ADS_INT);
    if(previous != null){
        for(String key: previous){
            sp.remove(key);
        }
    }
    sp.put(makeKey(SP_OWNED_NO_ADS_INT, getState().sessionUid().toHexString()), flag);
}

public static State getState(){
    return com.tezov.lib_java_android.application.Application.getState();
}
public static class State extends com.tezov.lib_java_android.application.Application.State{
    private boolean isAlias;
    private defUid aliasUid;
    private char[] password;
    public State(){
        clear();
    }
    @Override
    public void onMainActivityStart(ApplicationSystem app, Intent source){
        super.onMainActivityStart(app, source);
    }
    @Override
    public void onApplicationClose(ApplicationSystem app){
        super.onApplicationClose(app);
    }
    public State clear(){
        isAlias = false;
        aliasUid = null;
        if(password != null){
            Nullify.array(password);
            password = null;
        }
        return this;
    }

    public boolean isAlias(){
        return isAlias && (aliasUid != null);
    }
    public State setAlias(defUid uid){
        aliasUid = uid;
        return this;
    }
    public State switchedToAlias(){
        isAlias = true;
        return this;
    }
    public boolean isPassword(){
        return !isAlias && (password != null);
    }
    public State switchedToPassword(){
        isAlias = false;
        return this;
    }
    public defUid getAliasUid(){
        return aliasUid;
    }
    public String getPassword(){
        if(password == null){
            return null;
        } else {
            return new String(password);
        }
    }
    public State setPassword(char[] password){
        this.password = password;
        return this;
    }

}

}
