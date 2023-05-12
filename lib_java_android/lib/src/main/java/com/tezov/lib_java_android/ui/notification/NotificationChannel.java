/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.notification;

import com.tezov.lib_java.debug.DebugLog;
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

import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.tezov.lib_java_android.application.AppContext;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugTrack;

import java.util.List;

public class NotificationChannel{
private NotificationChannel(){
}

public static android.app.NotificationManager manager(){
    return AppContext.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
}

@RequiresApi(api = Build.VERSION_CODES.O)
public static BuilderGroup builderGroup(){
    return new BuilderGroup();
}

@RequiresApi(api = Build.VERSION_CODES.O)
public static BuilderChannel builder(){
    return new BuilderChannel();
}

public static List<android.app.NotificationChannel> getChannels(){
    if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.O){
        return null;
    }
    return manager().getNotificationChannels();
}

public static android.app.NotificationChannel getChannel(String id){
    if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.O){
        return null;
    }
    List<android.app.NotificationChannel> channels = manager().getNotificationChannels();
    for(android.app.NotificationChannel c: channels){
        if(id.equals(c.getId())){
            return c;
        }
    }
    return null;
}

public static boolean isChannelExist(String id){
    return getChannel(id) != null;
}

public static List<NotificationChannelGroup> getGroups(){
    if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.O){
        return null;
    }
    return manager().getNotificationChannelGroups();
}

public static NotificationChannelGroup getGroup(String id){
    if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.O){
        return null;
    }
    List<NotificationChannelGroup> groups = manager().getNotificationChannelGroups();
    for(NotificationChannelGroup g: groups){
        if(id.equals(g.getId())){
            return g;
        }
    }
    return null;
}

public static boolean isGroupExist(String id){
    return getGroup(id) != null;
}

public static NotificationChannelGroup getGroup(android.app.NotificationChannel channel){
    if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.O){
        return null;
    }
    return getGroup(channel.getGroup());
}

@RequiresApi(api = Build.VERSION_CODES.O)
public static class BuilderGroup{
    private String groupId = null;
    private String groupName = null;
    private String groupDescription = null;

    protected BuilderGroup(){
DebugTrack.start().create(this).end();
    }

    public BuilderGroup setGroup(String id, String name){
        this.groupId = id;
        this.groupName = name;
        return this;
    }

    public BuilderGroup setGroupDescription(String description){
        this.groupDescription = description;
        return this;
    }

    public void build(){
        if(groupId != null){
            NotificationChannelGroup group = getGroup(groupId);
            if(group == null){
                group = new NotificationChannelGroup(groupId, groupName);
                if(groupDescription != null){
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
                        group.setDescription(groupDescription);
                    }
                }
                manager().createNotificationChannelGroup(group);
            } else {

                if((group.getName() != null) && (!group.getName().toString().equals(groupName))){
DebugException.start().log("group id " + groupId + " name " + group.getName() + " already exist but groupName set is different (" + groupName + ")").end();
                }
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
                    if((group.getDescription() != null) && (!group.getDescription().equals(groupDescription))){
DebugException.start()
                                .log("group id " + groupId + " description " + group.getDescription() + " already exist but groupDescription set is different (" + groupDescription + ")")

                                .end();
                    }
                }
            }

        }
    }

    @Override
    protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
        super.finalize();
    }

}

@RequiresApi(api = Build.VERSION_CODES.O)
public static class BuilderChannel{
    private String groupId = null;
    private String channelId = null;
    private String channelName = null;
    private String channelDescription = null;
    private boolean enableLight = false;
    private Integer lightColor = Color.GREEN;
    private boolean enableVibration = false;
    private long[] vibrationPattern = new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400};
    private int importance = NotificationManager.IMPORTANCE_DEFAULT;

    protected BuilderChannel(){
DebugTrack.start().create(this).end();
    }

    public BuilderChannel setGroup(String id){
        this.groupId = id;
        return this;
    }

    public BuilderChannel setChannel(String channelId, String name, String description, int importance){
        this.channelId = channelId;
        this.channelName = name;
        this.channelDescription = description;
        this.importance = importance;
        return this;
    }

    public BuilderChannel lightEnable(boolean flag){
        this.enableLight = flag;
        return this;
    }

    public BuilderChannel lightColor(int color){
        this.lightColor = color;
        return this;
    }

    public BuilderChannel vibrationEnable(boolean flag){
        this.enableVibration = flag;
        return this;
    }

    public BuilderChannel vibrationPattern(long[] pattern){
        this.enableVibration = true;
        this.vibrationPattern = pattern;
        return this;
    }

    public void build(){
        android.app.NotificationChannel channel = getChannel(channelId);
        if(channel == null){
            channel = new android.app.NotificationChannel(channelId, channelName, importance);
            channel.setDescription(channelDescription);
            if(enableLight){
                channel.enableLights(true);
                channel.setLightColor(lightColor);
            } else {
                channel.enableLights(false);
            }
            if(enableVibration){
                channel.enableVibration(true);
                channel.setVibrationPattern(vibrationPattern);
            } else {
                channel.enableVibration(false);
            }
            if(groupId != null){
                channel.setGroup(groupId);
            }

            manager().createNotificationChannel(channel);
        } else {
            //TEST
            //                if((channel.getGroup() != null) && (!channel.getGroup().equals(groupId)))
            //                    Exception.produce("channel id "+channelId+" name "+channel.getName() +
            //                            " has group " + channel.getGroup() + " but groupId set is different
            //                            (" + groupId + ")").log();
            //                if((channel.getName() != null) && (!channel.getName().toString().equals
            //                (channelName)))
            //                    Exception.produce("channel id "+channelId+" name "+channel.getName() +
            //                            " already exist but groupName set is different (" + channelName +
            //                            ")").log();
            //                if((channel.getDescription() != null) && (!channel.getDescription().equals
            //                (channelDescription)))
            //                    Exception.produce("channel id "+channelId+" description "+channel
            //                    .getDescription() +
            //                            " already exist but channelDescription set is different (" +
            //                            channelDescription + ")").log();
        }
    }

    @Override
    protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
        super.finalize();
    }

}

}

