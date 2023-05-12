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

import com.tezov.lib_java.toolbox.Compare;

public class ObservableEventE<EVENT, OBJECT> extends ObservableEvent<EVENT, OBJECT>{
@Override
protected Access createAccess(EVENT event){
    return new Access(event);
}

public class Access extends ObservableEvent<EVENT, OBJECT>.Access{
    Throwable exception = null;

    public Access(EVENT event){
        super(event);
    }

    private boolean updateValue(OBJECT value, Throwable e){
        hasValue = true;
        this.value = value;
        exception = e;
        me().notifyEvent(this);
        return true;
    }

    @Override
    public void setValue(OBJECT value){
        updateValue(value, null);
    }

    @Override
    public boolean setValueIfDifferent(OBJECT value){
        if(!Compare.equals(this.value, value) || !hasValue()){
            return updateValue(value, null);
        }
        return false;
    }

    public boolean setExceptionIfDifferent(Throwable e){
        if(!Compare.equals(this.exception, e) || !hasValue()){
            return updateValue(null, e);
        }
        return false;
    }

    public Throwable getException(){
        return this.exception;
    }

    public void setException(Throwable e){
        updateValue(null, e);
    }

    public void set(OBJECT value, Throwable e){
        updateValue(value, e);
    }

    public boolean setIfDifferent(OBJECT value, Throwable e){
        if((!Compare.equals(this.value, value) && !Compare.equals(this.exception, e)) || !hasValue()){
            return updateValue(value, e);
        }
        return false;
    }

}

}
