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

import com.google.android.gms.tasks.OnSuccessListener;
import com.tezov.lib_java.async.Handler;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.runnable.RunnableW;

public abstract class TaskOnSuccessListenerW<T> implements OnSuccessListener<T>{
private final Handler handler;
private boolean enabled = true;

public TaskOnSuccessListenerW(){
    this(null);
}

public TaskOnSuccessListenerW(Handler handler){
DebugTrack.start().create(this).end();
    this.handler = handler;
}

public boolean isEnabled(){
    return enabled;
}

public void setEnabled(boolean flag){
    this.enabled = flag;
}

@Override
final public void onSuccess(T t){
    if(!isEnabled()){
        return;
    }
    if(handler != null){
        handler.post(this, new RunnableW(){
            @Override
            public void runSafe(){
                onSucceed(t);
            }
        });
    } else {
        onSucceed(t);
    }
}

public abstract void onSucceed(T t);

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

}
