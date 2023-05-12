/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.util;

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
import static androidx.lifecycle.Lifecycle.Event.ON_RESUME;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.OnLifecycleEvent;

import com.tezov.lib_java_android.application.AppContext;
import com.tezov.lib_java_android.type.android.LifecycleEvent;
import com.tezov.lib_java.type.runnable.RunnableSubscription;
import com.tezov.lib_java_android.ui.activity.ActivityBase;
import com.tezov.lib_java.file.UtilsFile;
import com.tezov.lib_java_android.application.VersionSDK;
import com.tezov.lib_java.async.notifier.task.TaskState;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugString;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;

import com.tezov.lib_java_android.file.UriW;
import com.tezov.lib_java.toolbox.Compare;
import com.tezov.lib_java.toolbox.Nullify;
import com.tezov.lib_java.type.collection.ListOrObject;

import java.util.ArrayList;

public class UtilsIntent{
private static Class<UtilsIntent> myClass(){
    return UtilsIntent.class;
}

public static ListOrObject<UriW> getUris(Intent intent, boolean retainPermission){
    int permissionFlags = 0;
    if(retainPermission){
        if(canRetainUriPermission(intent)){
            permissionFlags = intent.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
        else{
            retainPermission = false;
        }
    }
    ListOrObject<UriW> uris = new ListOrObject<>();
    ClipData clipData = intent.getClipData();
    if(clipData != null){
        for(int i = 0; i < clipData.getItemCount(); i++){
            ClipData.Item item = clipData.getItemAt(i);
            if(item.getUri() != null){
                Uri uri = item.getUri();
                if(retainPermission){
                    retainUriPermission(permissionFlags, uri);
                }
                uris.add(new UriW(uri,UriW.Type.INTENT));
            }
        }
    }
    if(intent.hasExtra(Intent.EXTRA_STREAM)){
        Bundle bundle = intent.getExtras();
        Object o = bundle.get(Intent.EXTRA_STREAM);
        if(o instanceof Parcelable[]){
            Parcelable[] parcelables = (Parcelable[])o;
            for(Parcelable parcelable: parcelables){
                Uri uri = (android.net.Uri)parcelable;
                if(retainPermission){
                    retainUriPermission(permissionFlags, uri);
                }
                uris.add(new UriW(uri, UriW.Type.INTENT));
            }
        }
    }
    Uri uri = intent.getData();
    if(uri != null){
        if(retainPermission){
            retainUriPermission(permissionFlags, uri);
        }
        uris.add(new UriW(uri, UriW.Type.INTENT));
    }
    return Nullify.collection(uris);
}
public static void retainUriPermission(Intent intent, UriW uri){
    if(canRetainUriPermission(intent)){
        retainUriPermission(intent, uri.get());
    }
}
public static void retainUriPermission(Intent intent, Uri uri){
    if(canRetainUriPermission(intent)){
        int permissionFlags = intent.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        retainUriPermission(permissionFlags, uri);
    }
}
@SuppressLint("WrongConstant")
private static void retainUriPermission(int permissionFlags, Uri uri){
    AppContext.getContentResolver().takePersistableUriPermission(uri, permissionFlags);
}
public static boolean canRetainUriPermission(Intent intent){
    return (intent.getFlags() & Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION) != 0;
}

public static boolean setUrisStream(Intent intent, ListOrObject<UriW> uris){
    if((uris == null) || (uris.isEmpty())){
        return false;
    } else if(uris.size() == 1){
        return setUriStream(intent, uris.get());
    } else {
        ArrayList<android.net.Uri> files = new ArrayList<>();
        String mineType = uris.get().getMimeType();
        boolean mineTypeMismatches = false;
        for(UriW uri: uris){
            String uriMineType = uri.getMimeType();
            files.add(uri.makeExportable().get());
            if(!Compare.equals(mineType, uriMineType)){
                mineTypeMismatches = true;
            }
        }
        if(mineTypeMismatches){
            mineType = UtilsFile.MINE_TYPE_OCTET_STREAM;
        }
        intent.setType(mineType);
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
        return true;
    }
}
public static boolean setUriStream(Intent intent, UriW uri){
    if(uri != null){
        String mineType = uri.getMimeType();
        intent.putExtra(Intent.EXTRA_STREAM, uri.makeExportable().get());
        intent.setType(mineType);
        return true;
    }
    return false;
}
public static boolean setUriData(Intent intent, UriW uri){
    if(uri != null){
        String uriMineType = uri.getMimeType();
        intent.setDataAndType(uri.makeExportable().get(), uriMineType);
        return true;
    }
    return false;
}

public static TaskState.Observable emailTo(String target, String subject, String body){
    TaskState task = new TaskState();
    try{
        StringBuilder mailto = new StringBuilder();
        mailto.append("mailto:").append(target).append("?");
        if(subject != null){
            mailto.append("subject=").append(Uri.encode(subject)).append("&");
        }
        if(body != null){
            mailto.append("body=").append(Uri.encode(body)).append("&");
        }
        mailto.replace(mailto.length() - 1, mailto.length(), "");
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SENDTO);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        intent.setData(Uri.parse(mailto.toString()));
        ActivityBase activity = AppContext.getActivity();
        LifecycleEvent.on(LifecycleEvent.Event.RESTART, activity, new RunnableSubscription(){
            @Override
            public void onComplete(){
                unsubscribe();
                task.notifyComplete();
            }
        });
        activity.startActivity(intent);
    }
    catch(Throwable e){
        task.notifyException(e);
    }
    return task.getObservable();
}
public static TaskState.Observable send(String subject, String text){
    TaskState task = new TaskState();
    try{
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        if(subject!=null){
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        }
        intent.putExtra(Intent.EXTRA_TEXT, text);
        intent.setType(UtilsFile.MINE_TYPE_PLAIN_TEXT);
        ActivityBase activity = AppContext.getActivity();
        LifecycleEvent.on(LifecycleEvent.Event.RESTART, activity, new RunnableSubscription(){
            @Override
            public void onComplete(){
                unsubscribe();
                task.notifyComplete();
            }
        });
        activity.startActivity(intent);
    }
    catch(Throwable e){
        task.notifyException(e);
    }
    return task.getObservable();
}
public static TaskState.Observable openLink(String link){
    return openLink(Uri.parse(link));
}
public static TaskState.Observable openLink(Uri uri){
    TaskState task = new TaskState();
    try{
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        ActivityBase activity = AppContext.getActivity();
        LifecycleEvent.on(LifecycleEvent.Event.RESTART, activity, new RunnableSubscription(){
            @Override
            public void onComplete(){
                unsubscribe();
                task.notifyComplete();
            }
        });
        activity.startActivity(intent);
    }
    catch(Throwable e){
        task.notifyException(e);
    }
    return task.getObservable();
}

public static void toDebugLogFlags(Intent intent){
    DebugString data = new DebugString();
    int flags = intent.getFlags();
    if((flags&Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS) !=0) data.append("EXCLUDE_FROM_RECENTS").nextLine();
    if((flags&Intent.FLAG_ACTIVITY_FORWARD_RESULT) !=0) data.append("FORWARD_RESULT").nextLine();
    if((flags&Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY) !=0) data.append("LAUNCHED_FROM_HISTORY").nextLine();
    if((flags&Intent.FLAG_ACTIVITY_MULTIPLE_TASK) !=0) data.append("MULTIPLE_TASK").nextLine();
    if((flags&Intent.FLAG_ACTIVITY_NEW_DOCUMENT) !=0) data.append("NEW_DOCUMENT").nextLine();
    if((flags&Intent.FLAG_ACTIVITY_NEW_TASK) !=0) data.append("NEW_TASK").nextLine();
    if((flags&Intent.FLAG_ACTIVITY_NO_ANIMATION) !=0) data.append("NO_ANIMATION").nextLine();
    if((flags&Intent.FLAG_ACTIVITY_NO_HISTORY) !=0) data.append("NO_HISTORY").nextLine();
    if((flags&Intent.FLAG_ACTIVITY_NO_USER_ACTION) !=0) data.append("NO_USER_ACTION").nextLine();
    if((flags&Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP) !=0) data.append("PREVIOUS_IS_TOP").nextLine();
    if((flags&Intent.FLAG_ACTIVITY_REORDER_TO_FRONT) !=0) data.append("REORDER_TO_FRONT").nextLine();
    if((flags&Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED) !=0) data.append("RESET_TASK_IF_NEEDED").nextLine();
    if((flags&Intent.FLAG_ACTIVITY_RETAIN_IN_RECENTS) !=0) data.append("RETAIN_IN_RECENTS").nextLine();
    if((flags&Intent.FLAG_ACTIVITY_SINGLE_TOP) !=0) data.append("SINGLE_TOP").nextLine();
    if((flags&Intent.FLAG_ACTIVITY_TASK_ON_HOME) !=0) data.append("TASK_ON_HOME").nextLine();
    if((flags&Intent.FLAG_DEBUG_LOG_RESOLUTION) !=0) data.append("DEBUG_LOG_RESOLUTION").nextLine();
    if((flags&Intent.FLAG_EXCLUDE_STOPPED_PACKAGES) !=0) data.append("EXCLUDE_STOPPED_PACKAGES").nextLine();
    if((flags&Intent.FLAG_FROM_BACKGROUND) !=0) data.append("FROM_BACKGROUND").nextLine();
    if((flags&Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION) !=0) data.append("GRANT_PERSISTABLE_URI_PERMISSION").nextLine();
    if((flags&Intent.FLAG_GRANT_PREFIX_URI_PERMISSION) !=0) data.append("GRANT_PREFIX_URI_PERMISSION").nextLine();
    if((flags&Intent.FLAG_GRANT_READ_URI_PERMISSION) !=0) data.append("GRANT_READ_URI_PERMISSION").nextLine();
    if((flags&Intent.FLAG_GRANT_WRITE_URI_PERMISSION) !=0) data.append("GRANT_WRITE_URI_PERMISSION").nextLine();
    if((flags&Intent.FLAG_INCLUDE_STOPPED_PACKAGES) !=0) data.append("INCLUDE_STOPPED_PACKAGES").nextLine();
    if((flags&Intent.FLAG_RECEIVER_FOREGROUND) !=0) data.append("RECEIVER_FOREGROUND").nextLine();
    if((flags&Intent.FLAG_RECEIVER_NO_ABORT) !=0) data.append("RECEIVER_NO_ABORT").nextLine();
    if((flags&Intent.FLAG_RECEIVER_REGISTERED_ONLY) !=0) data.append("RECEIVER_REGISTERED_ONLY").nextLine();
    if((flags&Intent.FLAG_RECEIVER_REPLACE_PENDING) !=0) data.append("RECEIVER_REPLACE_PENDING").nextLine();
    if(!VersionSDK.isSupEqualTo21_LOLLIPOP()){
        if((flags&Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET) !=0) data.append("CLEAR_WHEN_TASK_RESET").nextLine();
    }
    if(VersionSDK.isSupEqualTo24_NOUGAT()){
        if((flags&Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT) !=0) data.append("LAUNCH_ADJACENT").nextLine();
    }
    if(VersionSDK.isSupEqualTo26_OREO()){
        if((flags&Intent.FLAG_RECEIVER_VISIBLE_TO_INSTANT_APPS) !=0) data.append("RECEIVER_VISIBLE_TO_INSTANT_APPS").nextLine();
    }
    if(VersionSDK.isSupEqualTo28_P()){
        if((flags&Intent.FLAG_ACTIVITY_MATCH_EXTERNAL) !=0) data.append("MATCH_EXTERNAL").nextLine();
    }
    if(VersionSDK.isSupEqualTo29_Q()){
        if((flags&Intent.FLAG_DIRECT_BOOT_AUTO) !=0) data.append("DIRECT_BOOT_AUTO").nextLine();
    }
    if(VersionSDK.isSupEqualTo30_R()){
        if((flags&Intent.FLAG_ACTIVITY_REQUIRE_DEFAULT) !=0) data.append("REQUIRE_DEFAULT").nextLine();
        if((flags&Intent.FLAG_ACTIVITY_REQUIRE_NON_BROWSER) !=0) data.append("REQUIRE_NON_BROWSER").nextLine();
    }
DebugLog.start().send(data).end();
}

public static PendingIntent getBroadcast_UPDATE(Context context, int requestCode, Intent intent){
    if(VersionSDK.isSupEqualTo23_MARSHMALLOW()){
        return PendingIntent.getBroadcast(context, requestCode, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }
    else{
        return PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}

public static PendingIntent getBroadcast_CANCEL(Context context, int requestCode, Intent intent){
        if(VersionSDK.isSupEqualTo23_MARSHMALLOW()){
            return PendingIntent.getBroadcast(context, requestCode, intent,
                    PendingIntent.FLAG_NO_CREATE  | PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        }
        else{
            return PendingIntent.getBroadcast(context, requestCode, intent,
                    PendingIntent.FLAG_NO_CREATE  | PendingIntent.FLAG_CANCEL_CURRENT);
        }
    }

public static PendingIntent getBroadcast_EXIST(Context context, int requestCode, Intent intent){
    if(VersionSDK.isSupEqualTo23_MARSHMALLOW()){
        return PendingIntent.getBroadcast(context, requestCode, intent,
                PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE);
    }
    else{
        return PendingIntent.getBroadcast(context, requestCode, intent,
                PendingIntent.FLAG_NO_CREATE);
    }
}

}
