/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.provider.service;

import com.tezov.lib_java.debug.DebugLog;
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
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import com.tezov.lib_java_android.AidlFileProviderCallback;
import com.tezov.lib_java_android.AidlFileProviderRequest;
import com.tezov.lib_java_android.application.Application;
import com.tezov.lib_java_android.application.AppContext;
import com.tezov.lib_java.async.notifier.observer.value.ObserverValueE;
import com.tezov.lib_java.buffer.ByteBuffer;
import com.tezov.lib_java.file.Directory;
import com.tezov.lib_java.file.File;
import com.tezov.lib_java.file.FileTree;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugTrack;

//NOW NOTIFICATION FOR FOREGROUND SERVICE ACTIVE
public class FileServiceProvider extends Service{
public static final String SERVICE_TYPE = FileServiceProvider.class.getName();
public static final String PERMISSION = ".permission.FILE_SERVICE_PROVIDER";
public static final String ACTION = ".action.FILE_SERVICE_PROVIDER";

private final AidlFileProviderRequestStub binder;
public FileServiceProvider(){
    binder = new AidlFileProviderRequestStub();
}
private FileServiceProvider me(){
    return this;
}
private boolean isActionValid(Intent intent){
    boolean result = (AppContext.getPackageName() + ACTION).equals(intent.getAction());

    if(!result){
DebugException.start().log("Incorrect action = " + intent.getAction() + " instead of " + ACTION).end();
    }

    return result;
}

@Override
public IBinder onBind(Intent intent){
    if(isActionValid(intent)){
        startForegroundService();
        return binder.asBinder();
    } else {
        return null;
    }
}
private void startForegroundService(){

}
@Override
public boolean onUnbind(Intent intent){
    if(isActionValid(intent)){
        stopForegroundService();
    }
    return false;
}
private void stopForegroundService(){
    stopSelf();
}

protected class AidlFileProviderRequestStub extends AidlFileProviderRequest.Stub{
    public AidlFileProviderRequestStub(){
DebugTrack.start().create(this).end();
    }
    @Override
    public void links(AidlFileProviderCallback callback, String directoryLink, String patternPath, String patternFileName, boolean recursive) throws RemoteException{
        if(callback == null){
            throw new RemoteException("callback is null");
        }
        if(directoryLink == null){
            throw new RemoteException("directoryLink is null");
        }
        FileTree fileTree = new FileTree(Directory.from(directoryLink), recursive).setPatternPath(patternPath).setPatternFileName(patternFileName);
        try{
            fileTree.build();
        } catch(Throwable e){
            String message = e.getMessage();
            throw new RemoteException(e.getClass().getSimpleName() + (message != null ? message : ""));
        }
        try{
            callback.links(fileTree.getFileLinks());
        } catch(RemoteException e){

DebugException.start().log(e).end();

        }
    }
    @Override
    public void file(AidlFileProviderCallback callback, String fileLink) throws RemoteException{
        if(callback == null){
            throw new RemoteException("callback is null");
        }
        if(fileLink == null){
            throw new RemoteException("fileLink is null");
        }
        File file = File.from(fileLink);
        Application.fileQueue().read(file).observe(new ObserverValueE<ByteBuffer>(me()){
            @Override
            public void onComplete(ByteBuffer byteBuffer){
                try{
                    if(byteBuffer != null){
                        callback.file(byteBuffer.array());
                    } else {
                        callback.file(null);
                    }
                } catch(RemoteException e){

DebugException.start().log(e).end();

                }
            }
            @Override
            public void onException(ByteBuffer byteBuffer, Throwable e){

DebugException.start().log(e).end();

                try{
                    callback.file(null);
                } catch(RemoteException re){

DebugException.start().log(re).end();

                }
            }
        });
    }
    @Override
    protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
        super.finalize();
    }

}

}
