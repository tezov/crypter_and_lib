/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.notification.prebuild;

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.util.UtilsString;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import androidx.fragment.app.Fragment;

import com.tezov.lib_java_android.application.AppConfigKey;

import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.tezov.lib_java_android.R;
import com.tezov.lib_java_android.application.AppContext;
import com.tezov.lib_java_android.application.AppConfig;
import com.tezov.lib_java.async.Handler;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.NormLong;
import com.tezov.lib_java.type.runnable.RunnableW;
import com.tezov.lib_java_android.ui.notification.NotificationHelper;

public class NotificationBarProgressTransfer{
private final static int MIN_UPDATE_DELAY_ms = AppConfig.getInt(AppConfigKey.NOTIFICATION_MIN_UPDATE_DELAY_ms.getId());
private final static int PROGRESS_BAR_MAX = 100;
private final NormLong normInt;
private long lastUpdate = Clock.MilliSecond.now() - MIN_UPDATE_DELAY_ms;
private RunnableW notificationSendRunnable = null;
private NotificationHelper.MessageView messageView;

public NotificationBarProgressTransfer(String groupId, String channelId){
    this(groupId, channelId, null);
}
public NotificationBarProgressTransfer(String groupId, String channelId, Integer notificationId){
DebugTrack.start().create(this).end();
    this.normInt = new NormLong();
    notificationBuild(groupId, channelId, notificationId);
}
private void notificationBuild(String groupId, String channelId, Integer notificationId){
    RemoteViews contentView = new RemoteViews(AppContext.getPackageName(), R.layout.notification_bar_progress);
    messageView = NotificationHelper.contentView(groupId, channelId, notificationId).setSmallIcon(R.drawable.ic_edit_24dp).setSmallView(contentView).setOnGoing(true);
    messageView.setCategory(NotificationCompat.CATEGORY_PROGRESS).make();
}

public NotificationHelper.MessageView getMessageView(){
    return messageView;
}

public NotificationBarProgressTransfer setTitle(String title){
    messageView.getSmallView().setTextViewText(R.id.lbl_title, title);
    return this;
}
public NotificationBarProgressTransfer setSeparator(String separator){
    messageView.getSmallView().setTextViewText(R.id.lbl_progress_sep, separator);
    return this;
}
public NotificationBarProgressTransfer setUnit(String unit){
    messageView.getSmallView().setTextViewText(R.id.lbl_progress_unit, unit);
    return this;
}
public NotificationBarProgressTransfer setMax(long value){
    this.normInt.setBase(value);
    messageView.getSmallView().setTextViewText(R.id.lbl_progress_max, valueToString(value));
    return this;
}
public NotificationBarProgressTransfer setCurrent(long value){
    RemoteViews view = messageView.getSmallView();
    value = Math.min(value, normInt.getBase());
    float norm = normInt.getNorm(value);
    int progressValue = (int)(norm * PROGRESS_BAR_MAX);
    view.setTextViewText(R.id.lbl_progress, valueToString(value));
    view.setProgressBar(R.id.bar_progress, PROGRESS_BAR_MAX, progressValue, false);
    return this;
}
protected String valueToString(long value){
    return String.valueOf(value);
}

public void send(){
    if(notificationSendRunnable == null){
        long diff = Clock.MilliSecond.now() - lastUpdate;
        if(diff > MIN_UPDATE_DELAY_ms){
            notificationSend();
        } else {
            notificationSendRunnable = new RunnableW(){
                @Override
                public void runSafe(){
                    notificationSendRunnable = null;
                    notificationSend();
                }
            };
            Handler.SECONDARY().post(this, MIN_UPDATE_DELAY_ms - diff, notificationSendRunnable);
        }
    }
}

private void notificationSend(){
    Handler.SECONDARY().post(this, new RunnableW(){
        @Override
        public void runSafe(){
            lastUpdate = Clock.MilliSecond.now();
            messageView.send();
        }
    });
}

public void cancel(){
    if(notificationSendRunnable != null){
        Handler.SECONDARY().cancel(this, notificationSendRunnable);
        notificationSendRunnable = null;
    }
    Handler.SECONDARY().post(this, new RunnableW(){
        @Override
        public void runSafe(){
            messageView.cancel();
        }
    });
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

}
