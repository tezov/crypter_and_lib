/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.async.notifier.observer.valuePair;

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.util.UtilsString;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java.async.notifier.observer.value.ObserverValue;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.type.primaire.Pair;

public abstract class ObserverValuePair<F, S> extends ObserverValue<Pair<F, S>>{
public ObserverValuePair(Object owner){
    super(owner);
}
@Override
final public void onComplete(Pair<F, S> p){
    if(p == null){
        onComplete(null, null);
    } else {
        onComplete(p.first, p.second);
    }
}
public void onComplete(F f, S s){

DebugException.start().notImplemented(getOwner()).end();

}

}
