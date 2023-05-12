/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.wrapperAnonymous;

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

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tezov.lib_java.debug.DebugTrack;

public abstract class ActivityLifecycleCallbacksW implements Application.ActivityLifecycleCallbacks{
public ActivityLifecycleCallbacksW(){
DebugTrack.start().create(this).end();
}
@Override
public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState){

}
@Override
public void onActivityStarted(@NonNull Activity activity){

}
@Override
public void onActivityResumed(@NonNull Activity activity){

}
@Override
public void onActivityPaused(@NonNull Activity activity){

}
@Override
public void onActivityStopped(@NonNull Activity activity){

}
@Override
public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState){

}
@Override
public void onActivityDestroyed(@NonNull Activity activity){

}
@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

}
