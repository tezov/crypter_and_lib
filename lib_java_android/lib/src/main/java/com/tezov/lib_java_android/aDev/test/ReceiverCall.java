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
import android.database.Cursor;
import android.provider.CallLog;

import java.util.Date;

public class ReceiverCall extends BroadcastReceiver{
public static String getCallDetails(Context context){
    StringBuilder sb = new StringBuilder();
    String[] projection = new String[]{CallLog.Calls.CACHED_NAME, CallLog.Calls.NUMBER, CallLog.Calls.TYPE, CallLog.Calls.DATE, CallLog.Calls.DURATION};
    Cursor cursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, projection, null, null, null);
    if(cursor != null){
        int name = cursor.getColumnIndex(CallLog.Calls.CACHED_NAME);
        int number = cursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = cursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = cursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = cursor.getColumnIndex(CallLog.Calls.DURATION);
        sb.append("Call Details :");
        while(cursor.moveToNext()){
            String callName = cursor.getString(name);
            String phNumber = cursor.getString(number);
            String callType = cursor.getString(type);
            String callDate = cursor.getString(date);
            Date callDayTime = new Date(Long.parseLong(callDate));
            String callDuration = cursor.getString(duration);
            String dir = null;
            int dirCode = Integer.parseInt(callType);
            switch(dirCode){
                case CallLog.Calls.OUTGOING_TYPE:
                    dir = "OUTGOING";
                    break;

                case CallLog.Calls.INCOMING_TYPE:
                    dir = "INCOMING";
                    break;

                case CallLog.Calls.MISSED_TYPE:
                    dir = "MISSED";
                    break;
            }
            sb.append("\nName:--- " + callName + "\nPhone Number:--- " + phNumber + " \nCall Name:--- " + dir + " \nCall Date:--- " + callDayTime + " \nCall duration in sec :--- " + callDuration);
            sb.append("\n----------------------------------");
        }
        cursor.close();
    }
    return sb.toString();

}
@Override
public void onReceive(Context context, Intent intent){
//    if("android.intent.action.NEW_OUTGOING_CALL".equals(intent.getAction())){
//        Log.d(">>:", "ACTION_NEW_OUTGOING_CALL:" + System.currentTimeMillis());
//        Log.d(">>:", "OUTGOING CALL NUMBER" + intent.getExtras().getString(Intent.EXTRA_PHONE_NUMBER));
//    }
//    if("android.intent.action.PHONE_STATE".equals(intent.getAction())){
//        Log.d(">>:", "ACTION :" + intent.getAction() + " " + System.currentTimeMillis());
//        String stateStr = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
//        String number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
//        Log.d(">>:", "CALL NUMBER " + number);
//        Log.d(">>:", "STATE " + stateStr);
//    }
}

//    <uses-permission android:name="android.permission.READ_CALL_LOG" />
//    <uses-permission android:name="android.permission.READ_PHONE_STATE" />
//    <receiver android:name=".ReceiverCall">
//            <intent-filter>
//                <action android:name="android.intent.action.PHONE_STATE"/>
//                <action android:name="android.intent.action.NEW_OUTGOING_CALL" />
//            </intent-filter>
//        </receiver>
}
