/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.crypter.application;

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
import android.app.Activity;
import android.content.Intent;

import com.tezov.crypter.activity.ActivityLauncher;
import com.tezov.lib_java.async.Handler;
import com.tezov.lib_java.async.notifier.task.TaskState;
import com.tezov.lib_java.type.runnable.RunnableW;

import activity.ActivityMain_bt;
import application.Application_bt;

public abstract class ApplicationSystem extends com.tezov.lib_java_android.application.ApplicationSystem{
private final static long CLEAN_CACHE_DELAY_ms = 300;

@Override
public Class<? extends Activity> launcherActivityType(){
    return ActivityLauncher.class;
}
@Override
public Class<? extends Activity> mainActivityType(){
    return ActivityMain_bt.class;
}
@Override
protected TaskState.Observable onMainActivityStart(Intent source, boolean isRestarted){
    Application_bt.onMainActivityStart(this, source, isRestarted);
    return TaskState.Complete();
}
@Override
protected void onApplicationPause(){
    Application_bt.onApplicationPause(this);
    super.onApplicationPause();
}
@Override
protected void onApplicationClose(){
    Handler.newHandler(this, false).post(this, CLEAN_CACHE_DELAY_ms, new RunnableW(){
        @Override
        public void runSafe(){
            Environment.cleanCache();
        }
    });
    Application_bt.onApplicationClose(this);
    super.onApplicationClose();
}

@Override
protected com.tezov.lib_java_android.application.AppConfigKey.Adapter configNewKeyAdapter(){
    return new AppConfigKey.ConfigKeyAdapter();
}

}
