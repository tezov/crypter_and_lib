/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.debug;

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

import com.tezov.lib_java.application.AppMemory;
import com.tezov.lib_java.async.Handler;
import com.tezov.lib_java.type.collection.ListEntry;
import com.tezov.lib_java.type.collection.ListOrObject;
import com.tezov.lib_java.type.primaire.Entry;
import com.tezov.lib_java.type.runnable.RunnableW;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugTrack;

import java.util.Locale;

public class DebugMemoryLog{
private static final String SEP = " #";
private final ListEntry<Class, Integer> peekClassCounters;
private int lastCountAll = -1;

public DebugMemoryLog(){
    peekClassCounters = new ListEntry<Class, Integer>(ListOrObject::new);
}

public DebugMemoryLog peek(Class type){
    peekClassCounters.put(type, null);
    return this;
}

public void toDebugLog(){
    toDebugLog(null);
}

public void toDebugLog(String message){
    Handler.DEBUG().post(new RunnableW(){
        @Override
        public void runSafe(){
            StringBuilder data = new StringBuilder();
            int countAll = DebugTrack.count();
            data.append(String.format(Locale.US, "%5.2fMo", AppMemory.used()));
            if(lastCountAll == -1){
                lastCountAll = countAll;
            }
            int allDiff = (countAll - lastCountAll);
            data.append(SEP + "All:" + String.format(Locale.US, "%5d %4d%s", countAll, Math.abs(allDiff), (allDiff < 0 ? "-" : "+")));
            lastCountAll = countAll;
            for(Entry<Class, Integer> e: peekClassCounters){
                int countPeekClass = DebugTrack.count(e.key);
                if(e.value == null){
                    e.value = countPeekClass;
                }
                int diff = countPeekClass - e.value;
                data.append(SEP + DebugTrack.getFullSimpleName(e.key) + ":" + String.format(Locale.US, "%3d %3d%s", countPeekClass, Math.abs(diff), (diff < 0 ? "-" : "+")));
                e.value = countPeekClass;
            }
DebugLog.start().send(data + SEP + (message != null ? message : "")).end();
        }
    });
}

}
