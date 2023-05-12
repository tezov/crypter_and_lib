/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.async.notifier.observable;

import com.tezov.lib_java.debug.DebugLog;
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
import com.tezov.lib_java.async.notifier.Notifier;
import com.tezov.lib_java.toolbox.Compare;

public class ObservableValue<OBJECT> extends ObservableBase<Void, ObservableValue<OBJECT>.Access>{

@Override
protected Access createAccess(Void event){
    return new Access();
}

public class Access extends Notifier.defObservable.Access<Void>{
    OBJECT value = null;
    boolean hasValue = false;

    public Access(){
    }

    @Override
    public Void getEvent(){
        return null;
    }

    @Override
    public boolean hasValue(){
        return hasValue;
    }

    private boolean updateValue(OBJECT value){
        hasValue = true;
        this.value = value;
        me().notifyEvent(this);
        return true;
    }

    public boolean setValueIfDifferent(OBJECT value){
        if(!Compare.equals(this.value, value) || !hasValue()){
            return updateValue(value);
        } else {
            return false;
        }
    }

    @Override
    public OBJECT getValue(){
        if(!hasValue()){
            return null;
        } else {
            return value;
        }
    }

    public void setValue(OBJECT value){
        updateValue(value);
    }

}

}
