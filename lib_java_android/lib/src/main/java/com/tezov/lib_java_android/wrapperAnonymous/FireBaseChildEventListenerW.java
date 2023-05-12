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

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.tezov.lib_java.async.Handler;
import com.tezov.lib_java.async.notifier.Subscription;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.runnable.RunnableW;

public abstract class FireBaseChildEventListenerW implements ChildEventListener{
private final Handler handler;
private boolean enabled = true;
private Subscription subscription = null;
private boolean subscribeValid = true;
private boolean unsubscribeRequested = false;
private DatabaseReference path = null;

public FireBaseChildEventListenerW(){
    this(null);
}

public FireBaseChildEventListenerW(Handler handler){
DebugTrack.start().create(this).end();
    this.handler = handler;
}

public boolean isEnabled(){
    return enabled;
}

public void setEnabled(boolean flag){
    this.enabled = flag;
}

public void bind(Subscription subscription, DatabaseReference path){
    this.subscription = subscription;
    this.path = path;
}

public void addChildEvent(){
    path.addChildEventListener(this);
}

public void removeEvent(){
    path.removeEventListener(this);
}

public void unsubscribe(){
    unsubscribeRequested = true;
    if((subscription != null) && subscription.unsubscribe()){
        subscription = null;
    }
}

public boolean isSubscribeValid(){
    if(!subscribeValid){
        return false;
    }
    if(!unsubscribeRequested){
        return true;
    }
    subscribeValid = false;
    return true;
}

public boolean isSubscribeNotValid(){
    return !isSubscribeValid();
}

public boolean isCanceled(){
    if(subscription == null){
        return false;
    }
    return subscription.isCanceled();
}

@Override
final public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s){
    if(!isEnabled()){
        return;
    }
    if(handler != null){
        handler.post(this, new RunnableW(){
            @Override
            public void runSafe(){
                onAdded(dataSnapshot, s);
            }
        });
    } else {
        onAdded(dataSnapshot, s);
    }
}

public void onAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s){

}

@Override
final public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s){
    if(!isEnabled()){
        return;
    }
    if(handler != null){
        handler.post(this, new RunnableW(){
            @Override
            public void runSafe(){
                onChanged(dataSnapshot, s);
            }
        });
    } else {
        onChanged(dataSnapshot, s);
    }
}

public void onChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s){

}

@Override
final public void onChildRemoved(@NonNull DataSnapshot dataSnapshot){
    if(!isEnabled()){
        return;
    }
    if(handler != null){
        handler.post(this, new RunnableW(){
            @Override
            public void runSafe(){
                onRemoved(dataSnapshot);
            }
        });
    } else {
        onRemoved(dataSnapshot);
    }
}

public void onRemoved(@NonNull DataSnapshot dataSnapshot){

}

@Override
final public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s){
    if(!isEnabled()){
        return;
    }
    if(handler != null){
        handler.post(this, new RunnableW(){
            @Override
            public void runSafe(){
                onMoved(dataSnapshot, s);
            }
        });
    } else {
        onMoved(dataSnapshot, s);
    }
}

public void onMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s){

}

@Override
final public void onCancelled(@NonNull DatabaseError databaseError){
    if(!isEnabled()){
        return;
    }
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

public void onCancel(@NonNull DatabaseError databaseError){

}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

}
