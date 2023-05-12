/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.async.notifier.observable;

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.util.UtilsString;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java.async.notifier.Notifier;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.collection.ListOrObject;
import com.tezov.lib_java.type.ref.Ref;
import com.tezov.lib_java.type.ref.WR;

import java.util.List;

public abstract class ObservableBase<EVENT, ACCESS extends Notifier.defObservable.Access<EVENT>> implements Notifier.defObservable<EVENT>{
private final ListOrObject<ACCESS> accessList = new ListOrObject<>();
private WR<Notifier> notifierWR = null;

public ObservableBase(){
DebugTrack.start().create(this).end();
}

protected ObservableBase me(){
    return this;
}

protected void notifyEvent(ACCESS access){
    Notifier notifier = getNotifier();
    if(notifier != null){
        notifier.notifyEvent(access);
    }

}

@Override
public void attachTo(Notifier notifier){
    this.notifierWR = WR.newInstance(notifier);
}

protected Notifier getNotifier(){
    return Ref.get(notifierWR);
}

@Override
public boolean isValid(Access<EVENT> access){
    synchronized(this){
        return obtainAccess(access.getEvent()) != null;
    }
}
@Override
public boolean hasAccess(EVENT event){
    synchronized(this){
        return !accessList.isEmpty() && (accessList.get().getEvent() == event);
    }
}
@Override
public <A extends Access<EVENT>> A obtainAccess(EVENT event){
    synchronized(this){
        if(!hasAccess(event)){
            accessList.set(createAccess(event));
        }
        return (A)accessList.get();
    }
}
@Override
public <A extends Access<EVENT>> List<A> getAccessList(){
    synchronized(this){
        return (List<A>)accessList;
    }
}
@Override
public void clearAccess(){
    synchronized(this){
        accessList.clear();
    }
}

protected abstract ACCESS createAccess(EVENT event);

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

}
