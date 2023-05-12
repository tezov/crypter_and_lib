/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.state;

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
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.ref.Ref;
import com.tezov.lib_java.type.ref.WR;

public abstract class State<P extends Param, M extends Method>{
private WR<Object> ownerWR = null;
private P param = null;
private M method = null;

protected State(){
DebugTrack.start().create(this).end();
}

public void attach(Object owner){
    synchronized(this){

        if(hasOwner()){
DebugException.start().log("owner is not null").end();
        }

        if(owner != null){
            ownerWR = WR.newInstance(owner);
        } else {
            ownerWR = null;
        }
    }
}

public void detach(Object owner){
    synchronized(this){
        if(hasOwner() && (getOwner() == owner)){
            ownerWR = null;
        }
    }
}

public boolean hasOwner(){
    return Ref.isNotNull(ownerWR);
}

public <T> T getOwner(){
    return (T)Ref.get(ownerWR);
}

public boolean hasParam(){
    return param != null;
}

protected P newParam(){
    return null;
}

public P obtainParam(){
    if(!hasParam()){
        param = newParam();
        param.attach(this);
    }
    return param;
}

public P getParam(){
    return param;
}

public void setParam(P p){
    param = p;
    if(param != null){
        param.attach(this);
    }
}

public boolean hasMethod(){
    return method != null;
}

protected M newMethod(){
    return null;
}

public M obtainMethod(){
    if(!hasMethod()){
        method = newMethod();
        method.attach(this);
    }
    return method;
}

public M getMethod(){
    return method;
}

public void setMethod(M m){
    method = m;
    if(method != null){
        method.attach(this);
    }
}

public DebugString toDebugString(){
    DebugString sb = new DebugString();
    sb.append("param", param);
    return sb;
}

final public void toDebugLog(){
DebugLog.start().send(toDebugString()).end();
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

}
