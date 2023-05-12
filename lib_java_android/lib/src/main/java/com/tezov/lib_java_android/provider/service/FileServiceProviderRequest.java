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

import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;

import com.tezov.lib_java_android.AidlFileProviderCallback;
import com.tezov.lib_java_android.AidlFileProviderRequest;
import com.tezov.lib_java_android.application.AppContext;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugTrack;

//USELESS -> Use FileContentProviderServer better. This one good for example
public class FileServiceProviderRequest{
private AidlFileProviderRequest aidl = null;
private AidlFileProviderCallback callback = null;

public FileServiceProviderRequest(){
DebugTrack.start().create(this).end();
}
public boolean wrap(IBinder binder){
    this.aidl = AidlFileProviderRequest.Stub.asInterface(binder);

    if(aidl == null){
DebugException.start().explode("aidl is null").end();
    }

    return aidl != null;
}

public void setCallback(AidlFileProviderCallback callback){
    this.callback = callback;
}

public void bind(String packageName, android.content.ServiceConnection serviceConnection){
    Intent intent = new Intent();
    intent.setAction(packageName + FileServiceProvider.ACTION);
    intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
    intent.setComponent(new ComponentName(packageName, FileServiceProvider.SERVICE_TYPE));
    AppContext.bindService(intent, serviceConnection, android.content.Context.BIND_AUTO_CREATE);
}
public void unbind(android.content.ServiceConnection serviceConnection){
    AppContext.unbindService(serviceConnection);
}

public int getVersion(){
    return AidlFileProviderRequest.VERSION;
}

public void links(String directoryLink, String patternPath, String patternFileName, boolean recursive){
    try{
        aidl.links(callback, directoryLink, patternPath, patternFileName, recursive);
    } catch(Throwable e){

DebugException.start().log(e).end();

    }
}
public void file(String fileLink){
    try{
        aidl.file(callback, fileLink);
    } catch(Throwable e){

DebugException.start().log(e).end();

    }
}
@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}


//SERVER SIDE

//<permission
//android:name="${package_name_lite}.permission.FILE_SERVICE_PROVIDER"
//android:description="@string/fsp_permission_description"
//android:icon="@drawable/ic_forward_24dp"
//android:label="Service StorageFile provider permission"
//android:permissionGroup="Manifest.permission_group.STORAGE"
//android:protectionLevel="signature" />

//<service
//android:name=".provider.service.FileServiceProvider"
//android:enabled="true"
//android:exported="true"
//android:permission="${package_name_lite}.permission.FILE_SERVICE_PROVIDER">
//<intent-filter>
//<action android:name="${package_name_lite}.action.FILE_SERVICE_PROVIDER" />
//</intent-filter>
//</service>

//CLIENT SIDE
//<uses-permission android:name="${package_name_lite}.permission.FILE_SERVICE_PROVIDER" />

//private void test_service_provider(){
//    FileServiceProviderCallback fileProviderCallback = new FileServiceProviderCallback();
//    String UNBIND = "UNBIND";
//    String END = "END";
//    int EXCEPTION = AppUIDGenerator.nextInt();
//    int LINKS = AppUIDGenerator.nextInt();
//    RunnableGroup gr = new RunnableGroup().name("retrieve user files from lite");
//    gr.add(new RunnableGroup.Action().name("bind"){
//        @Override
//        public void run(){
//            String packageNameLite = AppContext.getString(R.string.package_name_lite);
//            fileProviderCallback.bind(packageNameLite).observe(new ObserverStateE(this){
//                @Override
//                public void onComplete(){
//                    endRunnable();
//                }
//                @Override
//                public void onException(Exception e){
//                    getParent().getArguments().put(EXCEPTION, e);
//                    getParent().skipUntil(END);
//                }
//            });
//        }
//    });
//    gr.add(new RunnableGroup.Action().name("get com.tezov.lib.database links"){
//        @Override
//        public void run(){
//            ;
//            fileProviderCallback.links(Directory.toLinkString(PRIVATE_DATA), "shared_prefs/", "SHARE_PREFERENCE\\.xml", true).observe(new ObserverValueE<List<String>>(this){
//                @Override
//                public void onComplete(List<String> links){
//                    getParent().getArguments().put(LINKS, links);
//                    endRunnable();
//                }
//                @Override
//                public void onException(List<String> strings, Exception e){
//                    getParent().getArguments().put(EXCEPTION, e);
//                    getParent().skipUntil(UNBIND);
//                }
//            });
//        }
//    });
//    gr.add(new RunnableGroup.Action().name("get com.tezov.lib.database files"){
//        @Override
//        public void run(){
//            List<String> links = getParent().getArguments().get(LINKS);
////            for(String link:links){
//            String link = links.get(0);
//            DebugLog.start().send(link).end();
//            fileProviderCallback.file(link).observe(new ObserverValueE<byte[]>(this){
//                @Override
//                public void onComplete(byte[] fileBytes){
//                    DebugLog.start().send(BytesTo.StringChar(fileBytes)).end();
//                    endRunnable();
//                }
//                @Override
//                public void onException(byte[] fileBytes, Exception e){
//                    getParent().getArguments().put(EXCEPTION, e);
//                    getParent().skipUntil(UNBIND);
//                }
//            });
////            }
////            endRunnable();
//        }
//    });
//    gr.add(new RunnableEvent(UNBIND){
//        @Override
//        public void run(){
//            fileProviderCallback.unbind().observe(new ObserverState(this){
//                @Override
//                public void onComplete(){
//                    getParent().skipUntil(END);
//                }
//            });
//        }
//    });
//    gr.add(new RunnableEvent(END){
//        @Override
//        public void run(){
//            Exception e = getParent().getArguments().get(EXCEPTION);
//            if(e == null){
//                DebugLog.start().here().end();
//            } else {
//                DebugLog.start().send(e).end();
//            }
//            endRunnable();
//        }
//    });
//    gr.run();
//}


}
