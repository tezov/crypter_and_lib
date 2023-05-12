/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.type.runnable;

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
import com.tezov.lib_java.type.ref.Ref;
import com.tezov.lib_java.type.ref.WR;

public abstract class RunnableCommand<BOSS> extends RunnableW{
private final WR<BOSS> bossWR;

public RunnableCommand(BOSS boss){
    this.bossWR = WR.newInstance(boss);
}
public BOSS getBoss(){
    return Ref.get(bossWR);
}
public boolean hasBoss(){
    return Ref.isNotNull(bossWR);
}

@Override
public void runSafe(){
    if(hasBoss()){
        execute();
    }
}

protected abstract void execute();

}
