/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.provider.service;

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

import android.content.ComponentName;
import android.os.IBinder;

import com.tezov.lib_java_android.AidlFileProviderCallback;
import com.tezov.lib_java.async.notifier.task.TaskState;
import com.tezov.lib_java.debug.DebugTrack;

//IMPROVE TIMEOUT BOUND
public class FileServiceProviderConnection implements android.content.ServiceConnection{
private TaskState task = null;
private FileServiceProviderRequest serviceRequest = null;
public FileServiceProviderConnection(){
DebugTrack.start().create(this).end();
}
public TaskState.Observable bind(String packageName, AidlFileProviderCallback callback){
    TaskState task = new TaskState();
    if(isBound()){
        task.notifyException("already bound");
    } else if(isBusy()){
        task.notifyException("is busy");
    } else {
        this.task = task;
        serviceRequest = new FileServiceProviderRequest();
        serviceRequest.setCallback(callback);
        serviceRequest.bind(packageName, this);
    }
    return task.getObservable();
}
public TaskState.Observable unbind(){
    TaskState task = new TaskState();
    if(!isBound()){
        task.notifyException("not bound");
    } else if(isBusy()){
        task.notifyException("is busy");
    } else {
        serviceRequest.setCallback(null);
        serviceRequest.unbind(this);
        destroy();
        task.notifyComplete();
    }
    return task.getObservable();
}
public boolean isBusy(){
    return task != null;
}
public boolean isBound(){
    return serviceRequest != null;
}

@Override
public void onServiceConnected(ComponentName name, IBinder binder){
    serviceRequest.wrap(binder);
    TaskState task = this.task;
    this.task = null;
    task.notifyComplete();
}
@Override
public void onServiceDisconnected(ComponentName name){
    destroy();
}
@Override
public void onBindingDied(ComponentName name){
    destroy();
}
@Override
public void onNullBinding(ComponentName name){
    destroy();
}
public FileServiceProviderRequest request(){
    return serviceRequest;
}
public void destroy(){
    if(serviceRequest != null){
        serviceRequest = null;
    }
    if(task != null){
        TaskState task = this.task;
        this.task = null;
        task.notifyException("destroyed");
    }
}
@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    if(isBound()){
        unbind();
    }
    super.finalize();
}

}
