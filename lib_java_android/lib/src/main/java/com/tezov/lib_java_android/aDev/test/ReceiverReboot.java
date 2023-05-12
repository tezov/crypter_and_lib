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
import androidx.fragment.app.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ReceiverReboot extends BroadcastReceiver{
@Override
public void onReceive(Context context, Intent intent){
    if(Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())){
        Log.d(">>:", "REBOOT:" + System.currentTimeMillis());
//            ReceiverAlarm.setAlarm(context, requestCode, delay);
    }
}

//    <uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
//            <receiver android:name=".ReceiverReboot" android:enabled="true" android:exported="false">
//            <intent-filter>
//                <action android:name="android.intent.action.BOOT_COMPLETED" />
//                <action android:name="android.intent.action.QUICKBOOT_POWERON" />
//            </intent-filter>
//        </receiver>
}
