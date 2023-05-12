/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.crypter.application;

import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.debug.DebugLog;
import java.util.Set;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import java.util.List;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import androidx.fragment.app.Fragment;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.util.UtilsString;
import com.tezov.lib_java.toolbox.Clock;
import java.util.LinkedList;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.primitive.ObjectTo;
import static android.content.Context.ALARM_SERVICE;
import static com.tezov.crypter.application.SharePreferenceKey.SP_KEYSTORE_AUTO_CLOSE_DELAY_INT;
import static com.tezov.crypter.application.SharePreferenceKey.SP_KEYSTORE_KEEP_OPEN_BOOLEAN;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import com.tezov.crypter.fragment.FragmentCipherBase;
import com.tezov.crypter.user.UserAuth;
import com.tezov.lib_java_android.application.SharedPreferences;
import com.tezov.lib_java.generator.uid.UUID;
import com.tezov.lib_java.toolbox.Compare;
import com.tezov.lib_java.type.primitive.BytesTo;
import com.tezov.lib_java.type.primitive.string.StringCharTo;
import com.tezov.lib_java_android.ui.fragment.FragmentNavigable;
import com.tezov.lib_java_android.ui.navigation.Navigate;
import com.tezov.lib_java_android.util.UtilsIntent;

import java.util.concurrent.TimeUnit;

public class ReceiverAlarmKeystore extends BroadcastReceiver{
private final static String ACTION = "com.tezov.crypter.ALARM.keystore";
private final static String EXTRA_REQUEST_CODE = "RequestCode";
private final static int REQUEST_CODE_INVALID = -1;
private static Class<ReceiverAlarmKeystore> myClass(){
    return ReceiverAlarmKeystore.class;
}
private static int getRequestCode(){
    UUID sessionUid = Application.getState().sessionUid();
    if(sessionUid == null){
        return REQUEST_CODE_INVALID;
    } else {
        return BytesTo.Int(StringCharTo.BytesHashcode64(ACTION + sessionUid.toHexString()));
    }
}
public static void start(Context context){
    SharedPreferences sp = Application.sharedPreferences();
    Integer delayMinutes = sp.getInt(SP_KEYSTORE_AUTO_CLOSE_DELAY_INT);
    if(delayMinutes == null){
        return;
    }
    if(delayMinutes == 0){
        signOut();
    }
    else{
        long delay_ms = TimeUnit.MILLISECONDS.convert(delayMinutes, TimeUnit.MINUTES);
        int requestCode = getRequestCode();
        Intent intent = new Intent(context, myClass());
        intent.setAction(ACTION);
        intent.putExtra(EXTRA_REQUEST_CODE, requestCode);
        PendingIntent pendingIntent = UtilsIntent.getBroadcast_UPDATE(context, requestCode, intent);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + delay_ms, pendingIntent);
    }
}
public static void cancel(Context context){
    Intent intent = new Intent(context, myClass());
    intent.setAction(ACTION);
    PendingIntent pendingIntent = UtilsIntent.getBroadcast_CANCEL(context, getRequestCode(), intent);
    if(pendingIntent != null){
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }
}
public static boolean isAlarmExist(Context context){
    Intent intent = new Intent(context, myClass());
    intent.setAction(ACTION);
    PendingIntent pendingIntent = UtilsIntent.getBroadcast_EXIST(context, getRequestCode(), intent);
    return pendingIntent != null;
}
@Override
public void onReceive(Context context, Intent intent){
    int requestCode = getRequestCode();
    if((requestCode != REQUEST_CODE_INVALID) && (intent.getIntExtra(EXTRA_REQUEST_CODE, REQUEST_CODE_INVALID) == requestCode)){
        signOut();
    }
}
private static void signOut(){
    SharedPreferences sp = Application.sharedPreferences();
    UserAuth userAuth = Application.userAuth();
    if((userAuth != null) && !Compare.isTrue(sp.getBoolean(SP_KEYSTORE_KEEP_OPEN_BOOLEAN)) && UserAuth.isKeystoreOpened()){
        userAuth.signOut();
        FragmentNavigable fr = Navigate.getCurrentFragmentRef();
        if(fr instanceof FragmentCipherBase){
            fr.requestViewUpdate(FragmentCipherBase.NOTIFY_SIGNED_OUT, null);
        }
    }
}


}

