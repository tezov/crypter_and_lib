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
import java.util.Set;
import com.tezov.lib_java.toolbox.Compare;

import java.util.Deque;
import java.util.LinkedList;

public class ObservableH<EVENT, OBJECT> extends ObservableHBase<EVENT, OBJECT>{

@Override
protected Access createAccess(EVENT event){
    return new Access(event);
}

public class Access extends ObservableHBase.Access{
    EVENT event;
    boolean hasValue = false;
    private Queue queue = null;

    public Access(EVENT event){
        this.event = event;
    }

    @Override
    public EVENT getEvent(){
        return event;
    }

    @Override
    public boolean hasValue(){
        return hasValue;
    }

    private boolean updateValue(OBJECT object){
        hasValue = true;
        queue.set(object);
        me().notifyEvent(this);
        return true;
    }

    public boolean setValueIfDifferent(OBJECT value){
        if(!hasValue() || !Compare.equals(queue.peekLast(), value)){
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
            return queue.get();
        }
    }

    public void setValue(OBJECT object){
        if(!hasValue()){
            queue = new Queue();
        }
        updateValue(object);
    }

    public OBJECT getLastValue(){
        if(hasValue()){
            return queue.getLast();
        } else {
            return null;
        }
    }

    public int getHistorySize(){
        if(hasValue()){
            return queue.queue.size();
        } else {
            return 0;
        }
    }

    @Override
    public boolean clearHistory(){
        if(hasValue()){
            return queue.clearHistory();
        } else {
            return false;
        }
    }

    class Queue{
        Deque<OBJECT> queue;

        Queue(){
            this.queue = new LinkedList<>();
        }

        OBJECT peekLast(){
            if(queue.isEmpty()){
                return null;
            } else {
                return queue.peekLast();
            }
        }

        OBJECT get(){
            if(queue.isEmpty()){
                return null;
            } else {
                if(queue.size() == 1){
                    return queue.peekFirst();
                } else {
                    return queue.pollFirst();
                }
            }
        }

        OBJECT getLast(){
            clearHistory();
            return get();
        }

        void set(OBJECT o){
            queue.offerLast(o);
            if(queue.size() > historyMaxSize){
                queue.pollFirst();
            }
        }

        boolean clearHistory(){
            if(queue.isEmpty()){
                return false;
            }
            while(queue.size() > 1){
                queue.pollFirst();
            }
            return true;
        }

    }

}

}
