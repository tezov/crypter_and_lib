/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.state;

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
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.ref.Ref;
import com.tezov.lib_java.type.ref.WR;

public abstract class Method{
private WR<State<? extends Param, ? extends Method>> stateWR = null;

protected Method(){
DebugTrack.start().create(this).end();
}

public void attach(State<? extends Param, ? extends Method> state){
    stateWR = WR.newInstance(state);
}

public boolean hasState(){
    return Ref.isNotNull(stateWR);
}

public <S extends State<? extends Param, ? extends Method>> S getState(){
    return (S)Ref.get(stateWR);
}

public boolean hasOwner(){
    if(!hasState()){
        return false;
    }
    return getState().hasOwner();
}

public <T> T getOwner(){
    return getState().getOwner();
}

public <P extends Param> P obtainParam(){
    return (P)getState().obtainParam();
}

public <P extends Param> P getParam(){
    return (P)getState().getParam();
}

public DebugString toDebugString(){
    return new DebugString();
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
