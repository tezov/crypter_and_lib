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
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.tezov.lib_java.async.Handler;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.runnable.RunnableW;

public abstract class FirebaseCompletionListenerW implements DatabaseReference.CompletionListener{
private final Handler handler;
private boolean enable = true;

public FirebaseCompletionListenerW(){
    this(null);
}

public FirebaseCompletionListenerW(Handler handler){
DebugTrack.start().create(this).end();
    this.handler = handler;
}

public boolean isEnabled(){
    return enable;
}

public void enable(boolean flag){
    this.enable = flag;
}

@Override
final public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref){
    if(!isEnabled()){
        return;
    }
    if(handler != null){
        handler.post(this, new RunnableW(){
            @Override
            public void runSafe(){
                onDone(error, ref);
            }
        });
    } else {
        onDone(error, ref);
    }
}

public abstract void onDone(@Nullable DatabaseError error, @NonNull DatabaseReference ref);

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

}
