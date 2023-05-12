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
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import androidx.fragment.app.Fragment;

import android.os.RemoteException;

import com.tezov.lib_java.async.notifier.task.TaskState;
import com.tezov.lib_java.async.notifier.task.TaskValue;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.ref.WR;

import java.util.List;

//IMPROVE TIMEOUT REQUEST
public class FileServiceProviderCallback{
private final FileServiceProviderConnection serviceConnection;
private TaskValue task = null;
public FileServiceProviderCallback(){
DebugTrack.start().create(this).end();
    serviceConnection = new FileServiceProviderConnection();
}
public int getVersion(){
    return AidlFileProviderCallback.VERSION;
}

public TaskState.Observable bind(String packageName){
    if(isBusy()){
        TaskState task = new TaskState();
        task.notifyException("is busy");
        return task.getObservable();
    } else {
        return serviceConnection.bind(packageName, new AidlFileProviderCallback(this));
    }
}
public TaskState.Observable unbind(){
    if(isBusy()){
        TaskState task = new TaskState();
        task.notifyException("is busy");
        return task.getObservable();
    } else {
        return serviceConnection.unbind();
    }
}

public boolean isBusy(){
    return task != null;
}

public TaskValue<List<String>>.Observable links(String directoryLink, String patternPath, String patternFileName, boolean recursive){
    TaskValue<List<String>> task = new TaskValue<>();
    if(!serviceConnection.isBound()){
        task.notifyException(null, "not bound");
    } else if(isBusy()){
        task.notifyException(null, "is busy");
    } else {
        this.task = task;
        serviceConnection.request().links(directoryLink, patternPath, patternFileName, recursive);
    }
    return task.getObservable();
}
public TaskValue<byte[]>.Observable file(String fileLink){
    TaskValue<byte[]> task = new TaskValue<>();
    if(!serviceConnection.isBound()){
        task.notifyException(null, "not bound");
    } else if(isBusy()){
        task.notifyException(null, "is busy");
    } else {
        this.task = task;
        serviceConnection.request().file(fileLink);
    }
    return task.getObservable();
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    if(task != null){
        TaskValue task = this.task;
        this.task = null;
        task.notifyException(null, "destroyed");
    }
    super.finalize();
}

private static class AidlFileProviderCallback extends com.tezov.lib_java_android.AidlFileProviderCallback.Stub{
    private final WR<FileServiceProviderCallback> aidlFileCallbackHelperWR;
    public AidlFileProviderCallback(FileServiceProviderCallback fileProviderCallback){
DebugTrack.start().create(this).end();
        this.aidlFileCallbackHelperWR = WR.newInstance(fileProviderCallback);
    }
    private void helperNotifyComplete(Object o){
        FileServiceProviderCallback helper = aidlFileCallbackHelperWR.get();
        TaskValue task = helper.task;
        helper.task = null;
        task.notifyComplete(o);
    }
    @Override
    public void links(List<String> fileLinks) throws RemoteException{
        helperNotifyComplete(fileLinks);
    }
    @Override
    public void file(byte[] fileBytes) throws RemoteException{
        helperNotifyComplete(fileBytes);
    }
    @Override
    protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
        super.finalize();
    }

}

}
