/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.aDev.test;

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
import com.tezov.lib_java_android.util.UtilsIntent;

import androidx.fragment.app.Fragment;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import static android.content.Context.ALARM_SERVICE;

public class ReceiverAlarm extends BroadcastReceiver{
public final static String action = "com.tezov.tracker_gps.ALARM";
public final static int requestCode = 234324243;
public final static long delay = 5000;

private static PowerManager.WakeLock wl = null;

private static Class<ReceiverAlarm> myClass(){
    return ReceiverAlarm.class;
}
public static void setAlarm(Context context, int requestCode, long delay){
    setAlarm(context, requestCode, delay, TimeUnit.MILLISECONDS);
}
public static void setAlarm(Context context, int requestCode, long delay, TimeUnit unit){
    Intent intent = new Intent(context, myClass());
    intent.setAction(action);
    PendingIntent pendingIntent = UtilsIntent.getBroadcast_UPDATE(context, requestCode, intent);
    AlarmManager alarmManager = (AlarmManager)context.getSystemService(ALARM_SERVICE);
    alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + TimeUnit.MILLISECONDS.convert(delay, unit), pendingIntent);
}
public static void acquireWaveLock(Context context, long timeOut_ms){
    acquireWaveLock(context, timeOut_ms, TimeUnit.MILLISECONDS);
}
public static void acquireWaveLock(Context context, long timeOut, TimeUnit unit){
    if(wl != null){
        throw new RuntimeException();
    }
    PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
    wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "tracker_gps:record");
    wl.acquire(TimeUnit.MILLISECONDS.convert(timeOut, unit));
}
public static void releaseWaveLock(){
    if(wl == null){
        throw new RuntimeException();
    }
    wl.release();
    wl = null;
}
public static boolean isAlarmExist(Context context, int requestCode){
    Intent intent = new Intent(context, myClass());
    intent.setAction(action);
    PendingIntent pendingIntent = UtilsIntent.getBroadcast_EXIST(context, requestCode, intent);
    return pendingIntent != null;
}
@Override
public void onReceive(Context context, Intent intent){
    if(action.equals(intent.getAction())){
        Log.d(">>:", "RECEIVED:" + System.currentTimeMillis());
        setAlarm(context, requestCode, delay);
    }
}
public void cancelAlarm(Context context, int requestCode){
    Intent intent = new Intent(context, myClass());
    intent.setAction(action);
    PendingIntent pendingIntent = UtilsIntent.getBroadcast_CANCEL(context, requestCode, intent);
    if(pendingIntent != null){
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }
}


//    <uses-permission android:name="android.permission.WAKE_LOCK" />
//        <receiver android:name=".ReceiverAlarm" android:enabled="true" android:exported="false">
//            <intent-filter>
//                <action android:name="com.tezov.tracker_gps.ALARM" />
//            </intent-filter>
//        </receiver>

}

