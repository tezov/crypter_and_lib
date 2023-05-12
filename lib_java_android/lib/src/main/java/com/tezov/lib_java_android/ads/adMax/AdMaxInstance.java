/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ads.adMax;

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.util.UtilsString;
import java.util.Set;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import androidx.fragment.app.Fragment;
import com.facebook.ads.AdSettings;
import com.tezov.lib_java.debug.DebugTrack;
import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkConfiguration;
import com.tezov.lib_java.type.collection.ListOrObject;
import com.tezov.lib_java.type.runnable.RunnableW;

import java.util.LinkedList;
import java.util.List;

public class AdMaxInstance {
private boolean isInitialized = false;
private List<RunnableW> pendings = null;
public AdMaxInstance(){
DebugTrack.start().create(this).end();
}
public AdMaxInstance onMainActivityStart(android.content.Context context){
//FACEBOOK AUDIENCE
    AdSettings.setDataProcessingOptions(new String[] {}); //not limited
//    AdSettings.setDataProcessingOptions( new String[] {"LDU"}, 1, 1000 ); //limited
    AppLovinSdk.getInstance(context).setMediationProvider("max");
    AppLovinSdk.initializeSdk( context, new AppLovinSdk.SdkInitializationListener() {
        @Override
        public void onSdkInitialized(AppLovinSdkConfiguration configuration){
            isInitialized = true;
            runPendings();
        }
    } );
    return this;
}
public boolean isInitialized(){
    synchronized(this){
        return isInitialized;
    }
}
public void post(RunnableW r){
    synchronized(this){
        if(isInitialized && (pendings == null)){
            r.run();
        } else {
            if(pendings == null){
                pendings = new ListOrObject<>(LinkedList::new);
            }
            pendings.add(r);
        }
    }
}
private void runPendings(){
    synchronized(this){
        if(pendings != null){
            RunnableW.run(pendings, false);
            pendings = null;
        }
    }
}
public void clearPendings(){
    synchronized(this){
        if(pendings != null){
            pendings.clear();
            pendings = null;
        }
    }
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

}
