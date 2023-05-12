/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.navigation.stack;

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

import static com.tezov.lib_java_android.type.android.LifecycleEvent.Event.DESTROY;
import static com.tezov.lib_java_android.type.android.LifecycleEvent.Event.PAUSE;
import static com.tezov.lib_java_android.type.android.LifecycleEvent.Event.RESUME;
import static com.tezov.lib_java_android.type.android.LifecycleEvent.Event.STOP;
import static com.tezov.lib_java_android.ui.navigation.NavigatorManager.NavigatorKey;
import static com.tezov.lib_java_android.ui.navigation.stack.StackEntry.Step.CONSTRUCTED;
import static com.tezov.lib_java_android.ui.navigation.stack.StackEntry.Step.DESTROYED;
import static com.tezov.lib_java_android.ui.navigation.stack.StackEntry.Step.INITIALIZED;
import static com.tezov.lib_java_android.ui.navigation.stack.StackEntry.Step.PAUSED;
import static com.tezov.lib_java_android.ui.navigation.stack.StackEntry.Step.RECONSTRUCTED;
import static com.tezov.lib_java_android.ui.navigation.stack.StackEntry.Step.RESTARTED;
import static com.tezov.lib_java_android.ui.navigation.stack.StackEntry.Step.STOPPED;

import androidx.lifecycle.Lifecycle;

import com.tezov.lib_java.generator.NumberGenerator;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java_android.type.android.LifecycleEvent;
import com.tezov.lib_java.type.collection.Arguments;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.ref.SR;
import com.tezov.lib_java.type.runnable.RunnableSubscription;
import com.tezov.lib_java_android.ui.navigation.defNavigable;
import com.tezov.lib_java_android.ui.navigation.destination.DestinationDetails;

public class StackEntry<T extends defNavigable>{
private static final NumberGenerator stackGeneratorID = new NumberGenerator();
private final long bindID;
private final NavigatorKey.Is navigatorKey;
private final SR<T> refSR;
private final DestinationDetails destinationDetails;
private Arguments<?> arguments = null;
private Step step = INITIALIZED;
private StackEntry<?> source;
private LifecycleEvent.Subscription[] lifecycleListeners = null;
private boolean isArgumentsChanged = false;
private boolean removedFromStack = false;

StackEntry(NavigatorKey.Is navigatorKey, DestinationDetails destinationDetails, StackEntry<?> source){
DebugTrack.start().create(this).end();
    this.bindID = nextId();
    this.navigatorKey = navigatorKey;
    this.destinationDetails = destinationDetails;
    this.source = source;
    this.refSR = new SR<>(null);
}

private static int nextId(){
    return stackGeneratorID.nextInt();
}

void updateSource(StackEntry<?> source){
    this.source = source;
    isArgumentsChanged = false;
}

private StackEntry<T> me(){
    return this;
}

public long getBindID(){
    return bindID;
}

public NavigatorKey.Is getNavigatorKey(){
    return navigatorKey;
}

public T getRef(){
    return refSR.get();
}

public Class<T> getRefType(){
    return destinationDetails.getTarget();
}

public boolean isArgumentsChanged(){
    return isArgumentsChanged;
}

public StackEntry<T> bindArguments(Arguments<?> arg){
    if(arguments != arg){
        this.isArgumentsChanged = true;
        arguments = arg;
DebugLog.start().send(this, navigatorKey.name() + " ID:" + bindID + " UPDATE DEFAULT ARGUMENTS " + ObjectTo.hashcodeIdentityString(arg)).end();
    }
    return this;
}

public Arguments<?> getArguments(){
    return arguments;
}
public void setArguments(Arguments<?> arg){
    arguments = arg;
DebugLog.start().send(this, navigatorKey.name() + " ID:" + bindID + " SET ARGUMENTS " + ObjectTo.hashcodeIdentityString(arg)).end();
}

public DestinationDetails getDestination(){
    return destinationDetails;
}

public boolean hasBeenRestarted(){
    return step == RESTARTED;
}

public boolean hasBeenReconstructed(){
    return step == RECONSTRUCTED;
}

public StackEntry<?> getSource(){
    return source;
}

public Step getStep(){
    return step;
}

private void setState(Step step){
    this.step = step;

DebugLog.start().send(this, navigatorKey.name() + " ID:" + bindID + " STATE: " + step.name() + " " + DebugTrack.getFullSimpleName(this.refSR.getType())).end();

}

private void listenLifeCycle(T ref){
    this.refSR.set(ref);
    if(ref.getLifecycle().getCurrentState() == Lifecycle.State.INITIALIZED){
        if(step == INITIALIZED){
            setState(CONSTRUCTED);
        } else if(step == DESTROYED){
            setState(RECONSTRUCTED);
        } else {
DebugException.start().explode("onStart: incorrect previous step " + step.name()).end();
        }


    } else if(ref.getLifecycle().getCurrentState() == Lifecycle.State.RESUMED){
        if(step == INITIALIZED){
            setState(CONSTRUCTED);
        } else {
DebugException.start().explode("onStart: incorrect previous step " + step.name()).end();
        }

    }
    if(lifecycleListeners == null){
        lifecycleListeners = new LifecycleEvent.Subscription[4];
    } else {
DebugException.start().log("lifecycleListeners not null").end();
    }

    lifecycleListeners[0] = LifecycleEvent.on(RESUME, ref, new RunnableSubscription(){
        @Override
        public void onComplete(){
            if((step == PAUSED) || (step == STOPPED)){
                setState(RESTARTED);
            } else if((step == CONSTRUCTED) || (step == RECONSTRUCTED)){

            } else {
DebugException.start().explode("onResume: incorrect previous step " + step.name()).end();
            }

            if(hasBeenRestarted()){
                ref.onOpen(false, true);
            } else if(hasBeenReconstructed()){
                ref.onPrepare(true);
                ref.onOpen(true, false);
            } else {
                ref.onPrepare(false);
                ref.onOpen(false, false);
            }
            if(step == CONSTRUCTED){
                ref.confirmConstructed(me());
            }
        }
    });
    lifecycleListeners[1] = LifecycleEvent.on(PAUSE, ref, new RunnableSubscription(){
        @Override
        public void onComplete(){
            if((step == CONSTRUCTED) || (step == RECONSTRUCTED) || (step == RESTARTED)){
                setState(PAUSED);
            } else {
DebugException.start().explode("onPause: incorrect previous step " + step.name()).end();
            }

        }
    });
    lifecycleListeners[2] = LifecycleEvent.on(STOP, ref, new RunnableSubscription(){
        @Override
        public void onComplete(){
            if((step == CONSTRUCTED) || (step == RECONSTRUCTED) || (step == RESTARTED) || (step == PAUSED)){
                setState(STOPPED);
            } else {

DebugException.start().explode("onStop: incorrect previous step " + step.name()).end();
            }

        }
    });
    lifecycleListeners[3] = LifecycleEvent.on(DESTROY, ref, new RunnableSubscription(){
        @Override
        public void onComplete(){
            unsubscribe();
            if((step == CONSTRUCTED) || (step == RECONSTRUCTED) || (step == RESTARTED) || (step == PAUSED) || (step == STOPPED)){
                setState(DESTROYED);
            } else {

DebugException.start().explode("onDestroy: incorrect previous step " + step.name()).end();
            }
            ref.confirmDestroyed(me());
            unbind();
            if(removedFromStack){
                ref.removedFromStack();
            }
        }
    });
}

public boolean isBound(){
    return refSR != null;
}

public void bind(T ref){

    if(this.refSR.isNotNull()){
DebugException.start()
                .explode(navigatorKey.name() + " ID:" + bindID + " ref is not null: " + DebugTrack.getFullSimpleName(this.refSR.getType()) + " Step ref:" +
                         getRef().getLifecycle().getCurrentState().name() + " received: " + DebugTrack.getFullSimpleName(ref))
                .end();
    }
    if((this.refSR.hasInfo()) && (ref != null) && (this.refSR.info().isTypeNotEqual(ref))){
DebugException.start()
                .explode(navigatorKey.name() + " ID:" + bindID + " type do not match. Expected: " + DebugTrack.getFullSimpleName(this.refSR.getType()) + " received: " +
                         DebugTrack.getFullSimpleName(ref))
                .end();
    }


    ref.setBindID(bindID);
    listenLifeCycle(ref);

DebugLog.start().send(this, navigatorKey.name() + " ID:" + bindID + " BIND " + DebugTrack.getFullSimpleName(this.refSR.getType() + ":" + this.refSR.hashCode()) + ":" +
                        (getArguments() != null ? ObjectTo.hashcodeIdentityString(getArguments()) + " CHANGED " + isArgumentsChanged : "Arguments is null")).end();

}

public void unbind(){

    if(this.refSR.isNull()){
DebugException.start().log(navigatorKey.name() + " ID:" + bindID + " ref is null: " + DebugTrack.getFullSimpleName(this.refSR.getType())).end();
    }
DebugLog.start().send(this, navigatorKey.name() + " ID:" + bindID + " UNBIND " + DebugTrack.getFullSimpleName(this.refSR.getType()) + ":" + this.refSR.hashCode()).end();

    if(lifecycleListeners != null){
        for(LifecycleEvent.Subscription s: lifecycleListeners){
            s.unsubscribe();
        }
        lifecycleListeners = null;
    }
    this.refSR.set(null);
}

public void removedFromStack(){
    removedFromStack = true;
}

public DebugString toDebugString(){
    DebugString data = new DebugString();
    data.append("bindID", bindID);
    data.append("navigatorKey", navigatorKey);
    data.append("hasBeenReconstructed", hasBeenReconstructed());
    data.append("hasBeenRestarted", hasBeenRestarted());
    data.append("isArgumentsDefaultChanged", isArgumentsChanged);
    data.appendHashcodeString("argumentsDefault", getArguments());
    data.appendFullSimpleName("ref", refSR.get());
    if(destinationDetails != null){
        data.append("destinationDetails.getKey()", destinationDetails.getKey());
    } else {
        data.append("destinationDetails", null);
    }
    if((source != null) && (source.getDestination() != null)){
        data.append("source.getDestination().getKey()", source.getDestination().getKey());
    } else if(source != null){
        data.append("source.getDestination()", source.getDestination());
    } else {
        data.append("source", null);
    }
    return data;
}

final public void toDebugLog(){
DebugLog.start().send(toDebugString()).end();
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}


public enum Step{
    INITIALIZED, CONSTRUCTED, RECONSTRUCTED, RESTARTED, PAUSED, STOPPED, DESTROYED;
    public boolean isAtLeast(Step step){
        return ordinal() >= step.ordinal();
    }
}

}
