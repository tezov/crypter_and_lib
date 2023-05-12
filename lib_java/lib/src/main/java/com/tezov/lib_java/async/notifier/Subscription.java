/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.async.notifier;

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
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugTrack;

public abstract class Subscription<REF>{
protected REF ref;

public Subscription(){
    this(null);
}

public Subscription(REF ref){
DebugTrack.start().create(this).end();
    this.ref = ref;
}

public <S extends Subscription<REF>> S attach(REF ref){
    this.ref = ref;
    return (S)this;
}

protected boolean isRefNull(){
    return ref == null;
}

protected REF getRef(){
    return ref;
}

public abstract boolean unsubscribe();

public boolean isCanceled(){

DebugException.start().notImplemented().end();

    return false;
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

}
