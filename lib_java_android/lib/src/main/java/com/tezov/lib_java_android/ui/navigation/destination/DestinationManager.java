/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.navigation.destination;

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.util.UtilsString;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import androidx.fragment.app.Fragment;

import static com.tezov.lib_java_android.ui.navigation.NavigationArguments.How;
import static com.tezov.lib_java_android.ui.navigation.NavigationArguments.How.CREATE;
import static com.tezov.lib_java_android.ui.navigation.NavigationArguments.How.GET;
import static com.tezov.lib_java_android.ui.navigation.NavigationArguments.How.OBTAIN;
import static com.tezov.lib_java_android.ui.navigation.NavigationArguments.How.TAKE;
import static com.tezov.lib_java_android.ui.navigation.NavigatorManager.DestinationKey;
import static com.tezov.lib_java_android.ui.navigation.NavigatorManager.NavigatorKey;

import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.wrapperAnonymous.FunctionW;
import com.tezov.lib_java.type.collection.Arguments;
import com.tezov.lib_java.type.collection.ListEntry;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java_android.ui.navigation.NavigatorManager;
import com.tezov.lib_java_android.ui.navigation.defNavigable;
import com.tezov.lib_java_android.ui.navigation.stack.Stack;
import com.tezov.lib_java_android.ui.navigation.stack.StackEntry;

import java.util.ArrayList;
import java.util.List;

public class DestinationManager{

private final NavigatorManager navigatorManager;
private final ListEntry<NavigatorKey.Is, List<DestinationDetails>> destinations;

public DestinationManager(NavigatorManager navigatorManager){
DebugTrack.start().create(this).end();
    this.navigatorManager = navigatorManager;
    destinations = new ListEntry<>();
}

public NavigatorManager getNavigatorManager(){
    return navigatorManager;
}

public Stack getStack(){
    return navigatorManager.getStack();
}

public DestinationDetails addDestination(NavigatorKey.Is navigatorKey, Class<? extends defNavigable> source, Class<? extends defNavigable> current, Class<? extends defNavigable> target,
        DestinationKey.Is destination){
    DestinationDetails destinationDetails = new DestinationDetails(source, current, target, destination);
    List<DestinationDetails> destinations = this.destinations.getValue(navigatorKey);
    if(destinations == null){
        destinations = new ArrayList<>();
        this.destinations.add(navigatorKey, destinations);
    }
    destinations.add(destinationDetails);
    return destinationDetails;
}

public DestinationDetails addDestination(Class<? extends defNavigable> source, Class<? extends defNavigable> current, Class<? extends defNavigable> target){
    return addDestination(getNavigatorManager().getNavigatorKey(target), source, current, target, getNavigatorManager().identify(target));
}

public List<DestinationDetails> findDestinations(NavigatorKey.Is navigatorKey, DestinationDetails destination){
    List<DestinationDetails> destinations = this.destinations.getValue(navigatorKey);
    if(destinations == null){
        return null;
    }
    List<DestinationDetails> destinationsFiltered = new ArrayList<>();
    if(destination == null){
        destination = new DestinationDetails(null, null, null, null);
    }
    for(DestinationDetails d: destinations){
        if(((d.source == null) || (destination.current == null) || (d.source == destination.current)) && ((d.current == null) || (destination.target == null) || (d.current == destination.target)) &&
           (d.destinationKey != destination.destinationKey)){
            destinationsFiltered.add(d);
        }
    }
    if(destinationsFiltered.isEmpty()){
        return null;
    } else {
        return destinationsFiltered;
    }
}

public List<DestinationDetails> findDestinations(NavigatorKey.Is navigatorKey, boolean destinationKeyNotNull){
    return findDestinations(navigatorKey, getNavigatorManager().getLastDestination(null, destinationKeyNotNull));
}

public List<DestinationDetails> findDestinations(NavigatorKey.Is navigatorKey, Class<defNavigable> source, Class<defNavigable> current){
    return findDestinations(navigatorKey, new DestinationDetails(null, source, current, null));
}

public DestinationDetails findBestDestination(NavigatorKey.Is navigatorKey, DestinationDetails currentDestination, FunctionW<DestinationDetails, Boolean> criteria){
    List<DestinationDetails> destinationsFiltered = findDestinations(navigatorKey, currentDestination);
    if(destinationsFiltered == null){
        return null;
    }
    Class current;
    Class source;
    if(currentDestination != null){
        current = currentDestination.target;
        source = currentDestination.current;
    } else {
        current = null;
        source = null;
    }
    //Exact match
    for(DestinationDetails d: destinationsFiltered){
        if((d.source == source) && (d.current == current) && criteria.apply(d)){
            return d;
        }
    }
    //Whatever I came from
    if(current != null){
        for(DestinationDetails d: destinationsFiltered){
            if((d.source == null) && (d.current == current) && criteria.apply(d)){
                return d;
            }
        }
    }
    //Whatever I am
    if(source != null){
        for(DestinationDetails d: destinationsFiltered){
            if((d.source == source) && (d.current == null) && criteria.apply(d)){
                return d;
            }
        }
    }
    //I just want reach the target for god sake.
    for(DestinationDetails d: destinationsFiltered){
        if((d.source == null) && (d.current == null) && criteria.apply(d)){
            return d;
        }
    }
    return null;
}

public DestinationDetails findBestDestination(NavigatorKey.Is navigatorKey, DestinationDetails fromDestination, DestinationKey.Is destinationKey){
    return findBestDestination(navigatorKey, fromDestination, new FunctionW<DestinationDetails, Boolean>(){
        @Override
        public Boolean apply(DestinationDetails d){
            return d.destinationKey == destinationKey;
        }
    });
}

public <T extends defNavigable> void refBind(T t){
    StackEntry<T> entry;
    if(getStack().isEmpty()){
DebugException.start().explode("stack is empty").end();
        return;
    }
    if(t.getBindID() == null){
        entry = getStack().peekLast();
    } else {
        entry = getStack().get(t.getBindID());
        if(entry == null){
DebugException.start().explode("stackEntry id:" + t.getBindID() + " not found for " + t.getClass().getSimpleName()).end();
            return;
        }
    }
    entry.bind(t);
    getNavigatorManager().getNavigator(entry.getNavigatorKey()).onBind(entry);
}

public <T extends defNavigable> StackEntry.Step refStepLife(T t){
    StackEntry stackEntry = getStack().get(t.getBindID());
    if(stackEntry == null){

DebugException.start().explode("stackEntry id:" + t.getBindID() + " not found for " + t.getClass().getSimpleName() + ":" + ObjectTo.hashcodeString(t)).end();


        return null;
    }
    return stackEntry.getStep();
}

public <T extends defNavigable> boolean refHasBeenRestarted(T t){
    StackEntry stackEntry = getStack().get(t.getBindID());
    if(stackEntry == null){

DebugException.start().explode("stackEntry id:" + t.getBindID() + " not found for " + t.getClass().getSimpleName() + ":" + ObjectTo.hashcodeString(t)).end();


        return false;
    }
    return stackEntry.hasBeenRestarted();
}

public <T extends defNavigable> boolean refHasBeenReconstructed(T t){
    StackEntry stackEntry = getStack().get(t.getBindID());
    if(stackEntry == null){

DebugException.start().explode("stackEntry id:" + t.getBindID() + " not found for " + t.getClass().getSimpleName() + ":" + ObjectTo.hashcodeString(t)).end();


        return false;
    }
    return stackEntry.hasBeenReconstructed();
}

public <T extends defNavigable> Boolean isArgumentsChanged(T t){
    StackEntry stackEntry = getStack().get(t.getBindID());
    if(stackEntry == null){

DebugException.start().explode("stackEntry id:" + t.getBindID() + "not found for " + t.getClass().getSimpleName() + ":" + ObjectTo.hashcodeString(t)).end();


        return null;
    }
    return stackEntry.isArgumentsChanged();
}

public <T extends defNavigable, KEY> Arguments<KEY> arguments(T t, How how){
    if(how == CREATE){
        return new Arguments<>();
    }
    StackEntry entry;
    if(t != null){
        entry = getStack().get(t.getBindID());
    } else {
        entry = null;
    }
    if(entry == null){
DebugException.start().explode("entry id:" + t.getBindID() + " not found for " + t.getClass().getSimpleName() + ":" + ObjectTo.hashcodeString(t)).end();
        return null;
    }
    if(how == GET){
        return entry.getArguments();
    }
    if(how == OBTAIN){
        Arguments<KEY> arguments = entry.getArguments();
        if(arguments == null){
            arguments = new Arguments<>();
            entry.setArguments(arguments);
        }
        return arguments;
    }
    if(how == TAKE){
        Arguments<KEY> arguments = entry.getArguments();
        entry.setArguments(null);
        return arguments;
    }
DebugException.start().unknown("type", how).end();
    return null;
}

public <T extends defNavigable, KEY> void setArguments(T t, Arguments<KEY> arguments){
    StackEntry stackEntry = getStack().get(t.getBindID());
    if(stackEntry == null){

DebugException.start().explode("stackEntry id:" + t.getBindID() + "not found for " + t.getClass().getSimpleName() + ":" + ObjectTo.hashcodeString(t)).end();


        return;
    }
    stackEntry.setArguments(arguments);
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

}

