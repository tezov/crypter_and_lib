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

import android.content.Context;
import android.content.Intent;

import com.tezov.lib_java.debug.DebugLog;

public class BroadcastReceiver extends android.content.BroadcastReceiver{
@Override
public void onReceive(Context context, Intent intent){
DebugLog.start().here().end();
}

//SERVER SIDE
//    <permission android:name="com.tezov.cavi.permission.BROADCAST_RECEIVER"
//    android:label="Broacast receiver permission"
//    android:description="description"
//    android:permissionGroup="?"
//    android:protectionLevel="normal" />

//<receiver android:name=".BroadcastReceiver"
//    android:enabled="true"
//    android:exported="true"
//    android:permission="com.tezov.cavi.permission.BROADCAST_RECEIVER">
//        <intent-filter>
//            <action android:name="com.tezov.cavi.action.BROADCAST_RECEIVER" />
//        </intent-filter>
//</receiver>

//CLIENT SIDE
//    <uses-permission android:name="com.tezov.cavi.permission.BROADCAST_RECEIVER"/>

// Request permission and then (cf-FileContentProviderServer)
//    String packageNameReceiver = AppContext.getString(R.string.package_name_receiver);
//    String ACTION = "com.tezov.cavi.action.BROADCAST_RECEIVER";
//    Intent intent = new Intent();
//        intent.setAction(ACTION);
//        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
//        intent.setComponent(new ComponentName(packageNameReceiver, ".BroadcastReceiver"));
//    sendBroadcast(intent);

}
