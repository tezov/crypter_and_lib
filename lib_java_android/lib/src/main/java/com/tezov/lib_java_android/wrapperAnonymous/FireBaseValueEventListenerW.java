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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.tezov.lib_java.async.Handler;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.runnable.RunnableW;

public abstract class FireBaseValueEventListenerW implements ValueEventListener{
private final Handler handler;
private boolean enabled = true;

public FireBaseValueEventListenerW(){
    this(null);
}

public FireBaseValueEventListenerW(Handler handler){
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
final public void onDataChange(@NonNull DataSnapshot snapshot){
    if(isEnabled()){
        if(handler != null){
            handler.post(this, new RunnableW(){
                @Override
                public void runSafe(){
                    onChange(snapshot);
                }
            });
        } else {
            onChange(snapshot);
        }
    }
}

public abstract void onChange(@NonNull DataSnapshot snapshot);

@Override
final public void onCancelled(@NonNull DatabaseError databaseError){
    if(isEnabled()){
        if(handler != null){
            handler.post(this, new RunnableW(){
                @Override
                public void runSafe(){
                    onCancel(databaseError);
                }
            });
        } else {
            onCancel(databaseError);
        }
    }
}

public abstract void onCancel(@NonNull DatabaseError databaseError);

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

}
