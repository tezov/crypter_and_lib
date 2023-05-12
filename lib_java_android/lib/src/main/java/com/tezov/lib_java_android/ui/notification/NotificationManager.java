/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.notification;

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.util.UtilsString;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import androidx.fragment.app.Fragment;

import android.app.Notification;
import android.os.Build;
import android.service.notification.StatusBarNotification;

import androidx.annotation.RequiresApi;

import com.tezov.lib_java_android.application.AppContext;
import com.tezov.lib_java.debug.DebugTrack;

import java.util.ArrayList;
import java.util.List;

public abstract class NotificationManager{
private final List<Creator> notifications;

public NotificationManager(){
DebugTrack.start().create(this).end();
    this.notifications = new ArrayList<>();
    createChannel();
}

protected abstract void createChannel();

public <T> T add(String channelId, NotificationManager.Creator<T> n){
    return add(null, channelId, n);
}

public <T> T add(String groupId, String channelId, NotificationManager.Creator<T> n){
    notifications.add(n);
    return n.create(groupId, channelId);
}

public void destroy(){
    for(NotificationManager.Creator n: notifications){
        n.destroy();
    }
    notifications.clear();
}

public <T> T getNotification(Class<T> type){
    for(NotificationManager.Creator n: notifications){
        if(n.getNotificationType() == type){
            return (T)n.getNotification();
        }
    }
    return null;
}

private android.app.NotificationManager manager(){
    return AppContext.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
}

public int notify(NotificationHelper.Message builder){
    Notification notification = builder.getNotification();
    int notificationId = builder.getNotificationId();
    manager().notify(notificationId, notification);
    return notificationId;
}

public int notify(NotificationHelper.MessageView builder){
    Notification notification = builder.getNotification();
    int notificationId = builder.getNotificationId();
    manager().notify(notificationId, notification);
    return notificationId;
}

public void cancel(Integer notificationId){
    manager().cancel(notificationId);
}

@RequiresApi(api = Build.VERSION_CODES.M)
public StatusBarNotification[] getActiveNotifications(){
    return manager().getActiveNotifications();
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

public interface Creator<T>{
    Class<T> getNotificationType();

    T getNotification();

    T create(String groupId, String channelId);

    void destroy();

}

}
