/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.type.runnable;

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.util.UtilsString;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.async.Handler;
import com.tezov.lib_java.generator.NumberGenerator;
import com.tezov.lib_java.toolbox.Iterable;
import com.tezov.lib_java.debug.DebugTrack;

import java.util.List;

public abstract class RunnableW implements Runnable{
private static final NumberGenerator whatGenerator = new NumberGenerator();
private final int what = whatGenerator.nextInt();

public RunnableW(){
DebugTrack.start().create(this).end();
}

public static void run(List<? extends RunnableW> runnables, boolean reverse){
    for(RunnableW runnable: Iterable.from(runnables, reverse)){
        runnable.run();
    }
}

public void beforeRun(){}
@Override
final public void run(){
    beforeRun();
    try{
        runSafe();
    }
    catch(java.lang.Throwable e){
        onException(e);
    }
    afterRun();
}
public void afterRun(){}

public abstract void runSafe() throws Throwable;
public void onException(java.lang.Throwable e){
DebugException.start().log(e).end();
}

public int what(){
    return what;
}

public boolean cancel(Object owner, Handler handler){
    return handler.cancel(owner, this);
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

}
