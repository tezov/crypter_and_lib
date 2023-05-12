/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.aDev.test;

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

import android.service.notification.StatusBarNotification;

import com.tezov.lib_java.debug.DebugLog;

public class NotificationListenerService extends android.service.notification.NotificationListenerService{
@Override
public void onListenerConnected(){

DebugLog.start().here().end();

}

@Override
public void onListenerDisconnected(){

DebugLog.start().here().end();

}

@Override
public void onNotificationPosted(StatusBarNotification sbn){

DebugLog.start().here().end();
DebugLog.start().send("ID :" + sbn.getId() + " \t " + sbn.getNotification().tickerText + " \t " + sbn.getPackageName()).end();

}

@Override
public void onNotificationRemoved(StatusBarNotification sbn){

DebugLog.start().send("ID :" + sbn.getId() + " \t " + sbn.getNotification().tickerText + " \t " + sbn.getPackageName()).end();

}

// need permission "android.permission.BIND_NOTIFICATION_LISTENER_SERVICE"
//<service
//    android:name = "com.tezov.lib.aDev.test.NotificationListenerService"
//    <intent-filter>
//        <action android:name = "android.service.notification.NotificationListenerService" />
//    </intent-filter>
//</service>

}
