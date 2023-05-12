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
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import androidx.fragment.app.Fragment;

import android.app.Notification;
import android.os.Build;
import android.service.notification.StatusBarNotification;
import android.widget.RemoteViews;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.tezov.lib_java_android.application.Application;
import com.tezov.lib_java_android.application.AppContext;
import com.tezov.lib_java.generator.NumberGenerator;
import com.tezov.lib_java.debug.DebugTrack;

public class NotificationHelper{
private final static String SORT_KEY_FIRST = "a";
private final static String SORT_KEY_DEFAULT = "m";
private final static NumberGenerator idGenerator = new NumberGenerator();

private NotificationHelper(){
}

private static int nextId(){
    return idGenerator.nextInt();
}

private static NotificationManager manager(){
    return Application.notificationManager();
}

@RequiresApi(api = Build.VERSION_CODES.M)
public static android.app.Notification getNotification(int id){
    StatusBarNotification[] notifications = manager().getActiveNotifications();
    if(notifications != null){
        for(StatusBarNotification n: notifications){
            if(n.getId() == id){
                return n.getNotification();
            }
        }
    }
    return null;
}

@RequiresApi(api = Build.VERSION_CODES.M)
public static boolean isVisible(int id){
    return getNotification(id) != null;
}

public static boolean areEnable(){
    return NotificationManagerCompat.from(AppContext.get()).areNotificationsEnabled();
}

public static void cancel(int id){
    NotificationManagerCompat.from(AppContext.get()).cancel(id);
}

public static void cancelAll(){
    NotificationManagerCompat.from(AppContext.get()).cancelAll();
}

public static Message message(String groupId, String channelId, Integer notificationId){
    return new Message(groupId, channelId, notificationId);
}

public static Message message(String channelId, Integer notificationId){
    return new Message(null, channelId, notificationId);
}

public static Message message(String groupId, String channelId){
    return new Message(groupId, channelId, null);
}

public static Message message(String channelId){
    return new Message(null, channelId, null);
}

public static MessageView contentView(String groupId, String channelId, Integer notificationID){
    return new MessageView(groupId, channelId, notificationID);
}

public static MessageView contentView(String channelId, Integer notificationID){
    return new MessageView(null, channelId, notificationID);
}

public static MessageView contentView(String channelId){
    return new MessageView(null, channelId, null);
}

public static MessageView contentView(String groupId, String channelId){
    return new MessageView(groupId, channelId, null);
}

public abstract static class Builder{
    public String groupId;
    public String channelId;
    public Integer notificationId;
    public String category = null;
    public String title = null;
    public String subTitle = null;
    public String text = null;
    public Integer number = null;
    public Integer smallIconId = null;
    public Integer color = null;

    public RemoteViews smallView = null;
    public RemoteViews bigView = null;

    public boolean autoCancel = false;
    public boolean onGoing = false;
    public Integer defaults = null;
    public boolean pinFirst = false;

    protected Builder(String groupId, String channelId, Integer notificationId){
DebugTrack.start().create(this).end();
        this.groupId = groupId;
        this.channelId = channelId;
        this.notificationId = notificationId;
    }

    public String getGroupId(){
        return groupId;
    }

    public <B extends Builder> B setGroupId(String groupId){
        this.groupId = groupId;
        return (B)this;
    }

    public String getChannelId(){
        return channelId;
    }

    public <B extends Builder> B setChannelId(String channelId){
        this.channelId = channelId;
        return (B)this;
    }

    public Integer getNotificationId(){
        return notificationId;
    }

    public <B extends Builder> B setNotificationId(Integer notificationId){
        this.notificationId = notificationId;
        return (B)this;
    }

    public <B extends Builder> B setCategory(String category){
        this.category = category;
        return (B)this;
    }

    public <B extends Builder> B setAutoCancel(boolean autoCancel){
        this.autoCancel = autoCancel;
        return (B)this;
    }

    public <B extends Builder> B setOnGoing(boolean flag){
        this.onGoing = flag;
        return (B)this;
    }

    public <B extends Builder> B setDefaults(Integer defaults){
        this.defaults = defaults;
        return (B)this;
    }

    public <B extends Builder> B pinFirst(boolean flag){
        this.pinFirst = flag;
        return (B)this;
    }

    public Notification getNotification(){
        return make().build();
    }

    public NotificationCompat.Builder make(){
        NotificationCompat.Builder n = onMake(new NotificationCompat.Builder(AppContext.get(), channelId));
        n.setSmallIcon(smallIconId);
        n.setOngoing(onGoing);
        n.setAutoCancel(autoCancel);
        if(defaults != null){
            n.setDefaults(defaults);
        }
        if(category != null){
            n.setCategory(category);
        }
        if(groupId != null){
            n.setGroup(groupId);
        }
        if(pinFirst){
            n.setSortKey(SORT_KEY_FIRST);
        } else {
            n.setSortKey(SORT_KEY_DEFAULT);
        }
        if(notificationId == null){
            notificationId = nextId();
        }
        return n;
    }

    public abstract NotificationCompat.Builder onMake(NotificationCompat.Builder n);

    public abstract int send();

    public void cancel(){
        manager().cancel(notificationId);
    }

    @Override
    protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
        super.finalize();
    }

}

public static class Message extends Builder{
    protected Message(String groupId, String channelId, Integer notificationId){
        super(groupId, channelId, notificationId);
    }

    public Message setSmallIcon(int id){
        this.smallIconId = id;
        return this;
    }

    public Message setMessage(String title, String subTitle, String text){
        this.title = title;
        this.subTitle = subTitle;
        this.text = text;
        return this;
    }

    @Override
    public NotificationCompat.Builder onMake(NotificationCompat.Builder n){
        setAutoCancel(true);
        n.setContentTitle(title);
        n.setContentText(text);
        n.setSubText(subTitle);
        return n;
    }

    @Override
    public int send(){
        return manager().notify(this);
    }

}

public static class MessageView extends Builder{
    protected MessageView(String groupId, String channelId, Integer notificationId){
        super(groupId, channelId, notificationId);
    }

    public MessageView setSmallIcon(int id){
        this.smallIconId = id;
        return this;
    }

    public RemoteViews getSmallView(){
        return smallView;
    }

    public MessageView setSmallView(RemoteViews smallView){
        this.smallView = smallView;
        return this;
    }

    @Override
    public NotificationCompat.Builder onMake(NotificationCompat.Builder n){
        n.setStyle(new NotificationCompat.DecoratedCustomViewStyle());
        n.setCustomContentView(smallView);
        return n;
    }

    @Override
    public int send(){
        return manager().notify(this);
    }

}

}
