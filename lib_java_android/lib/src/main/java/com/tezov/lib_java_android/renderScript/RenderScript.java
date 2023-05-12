/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.renderScript;

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

import com.tezov.lib_java_android.application.AppConfigKey;
import com.tezov.lib_java_android.application.AppContext;
import com.tezov.lib_java_android.application.AppConfig;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.type.ref.Ref;
import com.tezov.lib_java.type.ref.SRwAR;
import com.tezov.lib_java.wrapperAnonymous.ConsumerW;

public class RenderScript{
private static SRwAR<android.renderscript.RenderScript> rsSRwAR = null;
public static android.renderscript.RenderScript getAndLock(){
    if(Ref.isNull(rsSRwAR)){
        android.renderscript.RenderScript rs = android.renderscript.RenderScript.create(AppContext.get());
        rs.setMessageHandler(new Message());
        rsSRwAR = new SRwAR<>(rs, AppConfig.getLong(AppConfigKey.RS_SCRIPT_DELAY_DESTROY_second.getId()), new ConsumerW<android.renderscript.RenderScript>(){
            @Override
            public void accept(android.renderscript.RenderScript rs){
                rs.destroy();
            }
        });
    }
    rsSRwAR.lock(true);
    return rsSRwAR.get();
}
public static void unLock(){
    rsSRwAR.lock(false);
}
private static class Message extends android.renderscript.RenderScript.RSMessageHandler{
    @Override
    public void run(){ // IMPROVE decoded by Buffer (type, length, value)
        int length = mLength / 4;
        int remaining = mLength % 4;
        StringBuilder data = new StringBuilder();
        for(int i = 0; i < length; i++){
            int value = mData[i];
            data.append((char)(value & 0xFF));
            data.append((char)((value & 0xFF00) >> 8));
            data.append((char)((value & 0xFF0000) >> 16));
            data.append((char)((value & 0xFF000000) >> 24));
        }
        if(remaining != 0){
            int value = mData[length];
            int c = (value & 0xFF);
            if(c != 0){
                data.append((char)c);
            }
            c = (value & 0xFF00) >> 8;
            if(c != 0){
                data.append((char)c);
            }
            c = (value & 0xFF0000) >> 16;
            if(c != 0){
                data.append((char)c);
            }
        }
DebugLog.start().send(data.toString()).end();
    }

}

}
