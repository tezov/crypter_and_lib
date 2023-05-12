/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ads.adMax;

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
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import androidx.fragment.app.Fragment;
import com.tezov.lib_java.debug.DebugException;
import com.applovin.mediation.MaxError;
import com.tezov.lib_java.wrapperAnonymous.ConsumerW;
import com.tezov.lib_java_android.ui.misc.DelayState;

import java.util.concurrent.TimeUnit;

public class AdMaxInterstitialCyclicLoadwState extends AdMaxInterstitialCyclicLoad {
private final DelayState delayState;
private boolean isStarted = false;

public AdMaxInterstitialCyclicLoadwState(String unitAd, long delayCyclic_ms){
    this(unitAd, delayCyclic_ms, null);
}
public AdMaxInterstitialCyclicLoadwState(String unitAd, long delayCyclic, TimeUnit unit){
    super(unitAd, delayCyclic, unit);
    delayState = new DelayState();
}

@Override
public AdMaxInterstitialCyclicLoad start(long delay_ms){
    isStarted = true;
    delayState.setTarget(delay_ms).reset();
    return super.start(delay_ms);
}
@Override
public AdMaxInterstitialCyclicLoad start(long delay, TimeUnit unit){
    isStarted = true;
    delayState.setTarget(TimeUnit.MILLISECONDS.convert(delay, unit)).reset();
    return super.start(delay, unit);
}

public boolean isStarted(){
    return isStarted;
}

public void resume(){
    if(!isStarted()){
        start(getDelayCyclic_ms());
    } else if(!isRunning() && !isReadyToBeShown()){
        delayState.start(new ConsumerW<Long>(){
            @Override
            public void accept(Long delay){
                if(delay != null){
                    startSuper(delay);
                } else {
                    delayState.setTarget(getDelayCyclic_ms()).reset();
                    startSuper(delayState.getTarget());
                }
            }
        });
    }
}
private void startSuper(long delay_ms){
    super.start(delay_ms);
}

public void pause(){
    super.stop();
}

@Override
public void stop(){
    isStarted = false;
    super.stop();
}
@Override
protected void onStopped(){
    delayState.pause();
    super.onStopped();
}

@Override
protected void onClosed(){
    super.onClosed();
    if(isRunning()){
        delayState.reset();
    }
}

@Override
protected void onFailedLoad(MaxError adError){
    super.onFailedLoad(adError);
DebugException.start().log(adError.getMessage()).end();
}


}
