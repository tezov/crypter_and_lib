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
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Transaction;
import com.tezov.lib_java.async.Handler;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.runnable.RunnableW;

public abstract class FirebaseTransactionW implements Transaction.Handler{
private final Handler handler;

public FirebaseTransactionW(){
    this(null);
}

public FirebaseTransactionW(Handler handler){
DebugTrack.start().create(this).end();
    this.handler = handler;
}

public boolean isValid(){
    return true;
}

@Override
final public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData){
    if(!isValid()){
        return;
    }
    if(handler != null){
        handler.post(this, new RunnableW(){
            @Override
            public void runSafe(){
                onDone(error, committed, currentData);
            }
        });
    } else {
        onDone(error, committed, currentData);
    }
}

public abstract void onDone(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData);

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

}
