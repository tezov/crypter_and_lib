/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.activity;

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
import com.tezov.lib_java.debug.DebugLog;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.tezov.lib_java_android.application.AppContext;
import com.tezov.lib_java_android.application.AppPermission;
import com.tezov.lib_java.async.notifier.observer.value.ObserverValue;
import com.tezov.lib_java.async.notifier.task.TaskValue;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.collection.ListEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class ActivityBase extends AppCompatActivity{
private ActivityResultLauncher<Intent> activityResultLauncher = null;
private ActivityResultLauncher<String[]> activityPermissionLauncher = null;
private ViewGroup viewRoot = null;

protected ActivityBase(){
DebugTrack.start().create(this).end();
}

public ViewGroup getViewRoot(){
    return viewRoot;
}

protected abstract int getLayoutId();

@Override
protected void onNewIntent(Intent sourceIntent){
    super.onNewIntent(sourceIntent);
    setIntent(sourceIntent);
}

public void onApplicationRestarted(){

}
public void onApplicationPause(){

}

public boolean listenKeyboard(){
    return false;
}

@Override
protected void onCreate(Bundle savedInstanceState){
    super.onCreate(savedInstanceState);
    DebugLog.start().track(this).end();
    AppContext.setActivity(this);
    setContentView(getLayoutId());
    viewRoot = super.findViewById(Window.ID_ANDROID_CONTENT);
    activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), RequestForResult::onResult);
    activityPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), RequestForPermission::onResult);
}

@Override
public <T extends View> T findViewById(int id){
    return viewRoot.findViewById(id);
}
@Override
protected void onSaveInstanceState(Bundle savedInstanceState){
    super.onSaveInstanceState(savedInstanceState);
DebugLog.start().track(this).end();
}

@Override
public void onRestoreInstanceState(@Nullable Bundle savedInstanceState){
    super.onRestoreInstanceState(savedInstanceState);
DebugLog.start().track(this).end();
//    if(savedInstanceState != null){
//        for(String key: savedInstanceState.keySet()){
//            DebugLog.start().send(this, "key:" + key).end();
//        }
//    }

}

@Override
protected void onRestart(){
    super.onRestart();
DebugLog.start().track(this).end();
}

@Override
protected void onStart(){
    super.onStart();
DebugLog.start().track(this).end();

}

@Override
protected void onResume(){
    super.onResume();
DebugLog.start().track(this).end();
    AppContext.setActivity(this);
}

@Override
protected void onPause(){
    super.onPause();
DebugLog.start().track(this).end();
}

@Override
protected void onStop(){
    super.onStop();
DebugLog.start().track(this).end();
}

@Override
public void finish(){
    super.finish();
DebugLog.start().track(this).end();
}

@Override
public void finishAffinity(){
    super.finishAffinity();
DebugLog.start().track(this).end();
}

@Override
protected void onDestroy(){
    super.onDestroy();
DebugLog.start().track(this).end();
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}
private void launchForResult(Intent intent){
    activityResultLauncher.launch(intent);
}
private void launchForPermission(List<String> permissions){
    activityPermissionLauncher.launch(permissions.toArray(new String[0]));
}

public abstract static class RequestForResult{
    private static RequestForResult onGoing = null;
    private Intent intent = null;
    public RequestForResult(){
DebugTrack.start().create(this).end();
    }
    private static void onResult(ActivityResult result){
        if(onGoing != null){
            RequestForResult request = onGoing;
            onGoing = null;
            request.onActivityResult(result.getResultCode(), result.getData());
        }
    }
    protected Intent getIntent(){
        return intent;
    }
    public void setIntent(Intent intent){
        this.intent = intent;
    }
    protected abstract void onActivityResult(int resultCode, Intent data);
    synchronized public <R extends RequestForResult> R start(){
        if(onGoing != null){
DebugException.start().explode("busy").end();
            return (R)this;
        }
        if(intent == null){
DebugException.start().explode("intent is null").end();
            return (R)this;
        }
        onGoing = this;
        AppContext.getActivity().launchForResult(intent);
        return (R)this;
    }
    @Override
    protected void finalize() throws Throwable{
        DebugTrack.start().destroy(this).end();
        super.finalize();
    }

}

public static class RequestForPermission{
    private static RequestForPermission onGoing = null;
    private TaskValue<ListEntry<String, Boolean>> task = null;
    private final List<String> requestPermissions;
    private final ListEntry<String, Boolean> resultPermissions;
    public RequestForPermission(){
        DebugTrack.start().create(this).end();
        requestPermissions = new ArrayList<>();
        resultPermissions = new ListEntry<>();
    }
    private static void onResult(Map<String, Boolean> results){
        if(onGoing != null){
            RequestForPermission request = onGoing;
            onGoing = null;
            for(Map.Entry<String, Boolean> e: results.entrySet()){
                request.resultPermissions.put(e.getKey(), e.getValue());
            }
            request.task.notifyComplete(request.resultPermissions);
        }
    }
    public <R extends RequestForPermission> R observe(ObserverValue<ListEntry<String, Boolean>> observer){
        if((task == null) || (task.isCanceled())){
            task = new TaskValue<>();
        }
        task.observe(observer);
        return (R)this;
    }
    public <R extends RequestForPermission> R add(String permission){
        if(AppPermission.isGranted(permission)){
            resultPermissions.put(permission, true);
        } else {
            requestPermissions.add(permission);
        }
        return (R)this;
    }
    public List<String> getRequestPermissions(){
        return requestPermissions;
    }
    public ListEntry<String, Boolean> getResultPermissions(){
        return resultPermissions;
    }
    synchronized public <R extends RequestForPermission> R start(){
        if(!requestPermissions.isEmpty()){
            if(onGoing != null){
DebugException.start().explode("busy").end();
                return (R)this;
            }
            onGoing = this;
            AppContext.getActivity().launchForPermission(requestPermissions);
        } else if(task != null){
            task.notifyComplete(resultPermissions);
        }
        return (R)this;
    }
    @Override
    protected void finalize() throws Throwable{
        DebugTrack.start().destroy(this).end();
        super.finalize();
    }

}


}
