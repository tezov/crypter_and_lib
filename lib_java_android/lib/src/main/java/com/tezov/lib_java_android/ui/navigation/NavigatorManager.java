/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.navigation;

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
import com.tezov.lib_java_android.BuildConfig;

import static com.tezov.lib_java_android.ui.navigation.NavigatorManager.Event.NAVIGATE_BACK_CONFIRMED;
import static com.tezov.lib_java_android.ui.navigation.NavigatorManager.Event.NAVIGATE_TO_BOUND;
import static com.tezov.lib_java_android.ui.navigation.NavigatorManager.Event.NAVIGATE_TO_CONFIRMED;
import static com.tezov.lib_java_android.ui.navigation.NavigatorManager.Event.ON_NAVIGATE_BACK;
import static com.tezov.lib_java_android.ui.navigation.NavigatorManager.Event.ON_NAVIGATE_TO;
import static com.tezov.lib_java_android.ui.navigation.NavigatorManager.NavigatorKey.ACTIVITY;
import static com.tezov.lib_java_android.ui.navigation.NavigatorManager.NavigatorKey.DIALOG;
import static com.tezov.lib_java_android.ui.navigation.NavigatorManager.NavigatorKey.FRAGMENT;
import static com.tezov.lib_java_android.ui.navigation.navigator.Navigator.NavigateType.BACK;
import static com.tezov.lib_java_android.ui.navigation.navigator.Navigator.NavigateType.CLOSE;
import static com.tezov.lib_java_android.ui.navigation.navigator.Navigator.NavigateType.TO;
import static com.tezov.lib_java_android.ui.navigation.stack.StackEntry.Step.CONSTRUCTED;
import static com.tezov.lib_java_android.ui.navigation.stack.StackEntry.Step.PAUSED;

import com.tezov.lib_java_android.application.AppContext;
import com.tezov.lib_java_android.application.AppDisplay;
import com.tezov.lib_java.async.Handler;
import com.tezov.lib_java.async.notifier.Notifier;
import com.tezov.lib_java.async.notifier.observable.ObservableEvent;
import com.tezov.lib_java.async.notifier.observer.event.ObserverEvent;
import com.tezov.lib_java.async.notifier.observer.value.ObserverValue;
import com.tezov.lib_java.toolbox.Iterable;
import com.tezov.lib_java.toolbox.Reflection;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java_android.type.android.LifecycleEvent;
import com.tezov.lib_java.wrapperAnonymous.FunctionW;
import com.tezov.lib_java.type.collection.Arguments;
import com.tezov.lib_java.type.collection.ListEntry;
import com.tezov.lib_java.type.defEnum.EnumBase;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.runnable.RunnableQueue;
import com.tezov.lib_java.type.runnable.RunnableSubscription;
import com.tezov.lib_java.type.runnable.RunnableW;
import com.tezov.lib_java_android.ui.activity.ActivityBase;
import com.tezov.lib_java_android.ui.activity.ActivityNavigable;
import com.tezov.lib_java_android.ui.dialog.DialogNavigable;
import com.tezov.lib_java_android.ui.fragment.FragmentNavigable;
import com.tezov.lib_java_android.ui.navigation.destination.DestinationDetails;
import com.tezov.lib_java_android.ui.navigation.destination.DestinationManager;
import com.tezov.lib_java_android.ui.navigation.navigator.Navigator;
import com.tezov.lib_java_android.ui.navigation.stack.Stack;
import com.tezov.lib_java_android.ui.navigation.stack.StackEntry;

public abstract class NavigatorManager{
private static final int RETRY_MAX_ATTEMPT = 5;
private static final int RETRY_DELAY_ATTEMPT_MS = 1000;
private final DestinationManager destinationManager;
private final Notifier<NavigatorKey.Is> notifier;
private final ListEntry<NavigatorKey.Is, Navigator> navigators;
private final Stack stack;
private final RunnableQueue<RunnableW> queries;

protected NavigatorManager(){
DebugTrack.start().create(this).end();
    queries = new RunnableQueue<>(this, Handler.MAIN());
    destinationManager = new DestinationManager(this);
    navigators = new ListEntry<>();
    stack = new Stack();
    notifier = new Notifier<>(new ObservableEvent<NavigatorKey.Is, Event>(), false);
}

//     Map Object Class to NavigatorKey
final public NavigatorKey.Is getNavigatorKey(defNavigable navigable){
    return getNavigatorKey(navigable.getClass());
}

public NavigatorKey.Is getNavigatorKey(Class<? extends defNavigable> type){
    if(Reflection.isInstanceOf(type, ActivityNavigable.class)){
        return ACTIVITY;
    }
    if(Reflection.isInstanceOf(type, FragmentNavigable.class)){
        return FRAGMENT;
    }
    if(Reflection.isInstanceOf(type, DialogNavigable.class)){
        return DIALOG;
    }
    return null;
}

//     Map Destination to NavigatorKey
public abstract NavigatorKey.Is getNavigatorKey(DestinationKey.Is destination);

//     Map identify defNavigable to Destination
public abstract <I extends DestinationKey.Is> I identify(Class<? extends defNavigable> type);

private NavigatorManager me(){
    return this;
}

public DestinationManager getDestinationManager(){
    return destinationManager;
}

private void post(NavigatorKey.Is navigatorKey, Event event){
    //        if(navigatorKey == DIALOG){
    //            DebugLog.send(navigatorKey.name() + ":" + event.name()
    //                    + ":" + TrackClass.getFullSimpleName((Object)getLastRef(null, false)));
    //        }
    ObservableEvent<NavigatorKey.Is, Event>.Access access = notifier.obtainAccess(this, navigatorKey);
    access.setValue(event);
}

public Notifier.Subscription observe(ObserverEvent<NavigatorKey.Is, Event> observer){
    return notifier.register(observer);
}

public void unObserve(Object owner){
    notifier.unregister(owner);
}

public void addNavigator(Navigator navigator){
    navigators.put(navigator.getKey(), navigator);
    navigator.attach(destinationManager);
    if(navigator.getKey() == ACTIVITY){
        ActivityBase activity = AppContext.getActivity();
        if(activity instanceof ActivityNavigable){
            initStack((ActivityNavigable)activity);
        } else {
            AppContext.observeOnActivityChange(new ObserverValue<ActivityBase>(this){
                @Override
                public void onComplete(ActivityBase activity){
                    if(activity instanceof ActivityNavigable){
                        unsubscribe();
                        initStack((ActivityNavigable)activity);
                    }
                }
            });
        }
    }
}
private void initStack(ActivityNavigable activity){
    DestinationKey.Is destinationKey = identify(activity.getClass());

    if(destinationKey == null){
DebugException.start().explode("InitStack, can not identify destinationKey for " + DebugTrack.getFullSimpleName(activity)).end();
    }

    DestinationDetails destinationDetails = destinationManager.findBestDestination(ACTIVITY, null, destinationKey);

    if(destinationDetails == null){
DebugException.start().explode("InitStack, can not find destinationDetails for " + destinationKey).end();
    }

    stack.push(ACTIVITY, destinationDetails);
}

public <N extends Navigator> N getNavigator(NavigatorKey.Is navigatorKey){
    return (N)navigators.getValue(navigatorKey);
}

public Stack getStack(){
    return stack;
}

public int count(NavigatorKey.Is navigatorKey){
    int count = 0;
    for(StackEntry e: getStack().list()){
        if(e.getNavigatorKey() == navigatorKey){
            count++;
        }
    }
    return count;
}

public boolean isLast(NavigatorKey.Is navigatorKey){
    return count(navigatorKey) == 1;
}

public boolean isEmpty(){
    return getStack().isEmpty();
}

public StackEntry getEntry(NavigatorManager.DestinationKey.Is key){
    return stack.get(key);
}

public StackEntry getLastEntry(NavigatorKey.Is navigatorKey, boolean destinationKeyNotNull){
    if(destinationKeyNotNull){
        for(StackEntry e: Iterable.Reversed.from(stack.list())){
            if((navigatorKey != null) && (navigatorKey != e.getNavigatorKey())){
                continue;
            }
            if(e.getDestination().getKey() != null){
                return e;
            }
        }
    } else {
        for(StackEntry e: Iterable.Reversed.from(stack.list())){
            if((navigatorKey == null) || (navigatorKey == e.getNavigatorKey())){
                return e;
            }
        }
    }
    return null;
}

public <N extends NavigatorKey.Is> N getLastNavigatorKey(boolean destinationKeyNotNull){
    StackEntry e = getLastEntry(null, destinationKeyNotNull);
    if(e == null){
        return null;
    } else {
        return (N)e.getNavigatorKey();
    }
}

public <N extends Navigator> N getLastNavigator(boolean destinationKeyNotNull){
    if(stack.isEmpty()){
        return null;
    }
    return getNavigator(getLastNavigatorKey(destinationKeyNotNull));
}

public <N extends defNavigable> N getLastRef(NavigatorKey.Is navigatorKey, boolean destinationKeyNotNull){
    StackEntry e = getLastEntry(navigatorKey, destinationKeyNotNull);
    if(e == null){
        return null;
    } else {
        return (N)e.getRef();
    }
}

public DestinationDetails getDestination(defNavigable t){
    StackEntry e = stack.get(t.getBindID());
    if(e != null){
        return e.getDestination();
    }
    return null;
}

public DestinationDetails getLastDestination(NavigatorKey.Is navigatorKey, boolean destinationKeyNotNull){
    StackEntry e = getLastEntry(navigatorKey, destinationKeyNotNull);
    if(e == null){
        return null;
    } else {
        return e.getDestination();
    }
}

public DestinationDetails getLastDestinationSource(NavigatorKey.Is navigatorKey, boolean keyNotNull){
    if(stack.isEmpty()){
        return null;
    }
    StackEntry lastEntry = stack.getLast();
    if(keyNotNull){
        if(((navigatorKey == null) || (navigatorKey == lastEntry.getNavigatorKey())) && ((lastEntry.getSource() != null) && (lastEntry.getSource().getDestination().getKey() != null))){
            return lastEntry.getSource().getDestination();
        }
        for(StackEntry e: Iterable.Reversed.from(stack.list(), stack.size() - 1)){
            if((navigatorKey != null) && (navigatorKey != e.getNavigatorKey())){
                continue;
            }
            if(e.getDestination().getKey() != null){
                return e.getDestination();
            }
        }
    } else {
        if((navigatorKey == null) || (navigatorKey == lastEntry.getNavigatorKey())){
            return lastEntry.getSource().getDestination();
        }
        for(StackEntry e: Iterable.Reversed.from(stack.list(), stack.size() - 1)){
            if(navigatorKey == e.getNavigatorKey()){
                return lastEntry.getDestination();
            }
        }
    }
    return null;
}

public DestinationDetails getDestinationSource(defNavigable t, NavigatorKey.Is navigatorKey, boolean keyNotNull){
    Integer index = stack.indexOf(t.getBindID());
    if(index == null){
        return null;
    }
    StackEntry lastEntry = stack.get(index);
    if(keyNotNull){
        if(((navigatorKey == null) || (navigatorKey == lastEntry.getNavigatorKey())) && ((lastEntry.getSource() != null) && (lastEntry.getSource().getDestination().getKey() != null))){
            return lastEntry.getSource().getDestination();
        }
        for(StackEntry e: Iterable.Reversed.from(stack.list(), index)){
            if((navigatorKey != null) && (navigatorKey != e.getNavigatorKey())){
                continue;
            }
            if(e.getDestination().getKey() != null){
                return e.getDestination();
            }
        }
        return null;
    } else {
        if((navigatorKey == null) || (navigatorKey == lastEntry.getNavigatorKey())){
            return lastEntry.getSource().getDestination();
        }
        for(StackEntry e: Iterable.Reversed.from(stack.list(), index)){
            if(navigatorKey == e.getNavigatorKey()){
                return e.getDestination();
            }
        }
        return null;
    }
}

private void addQuery(NavigatorKey.Is navigatorKey, RunnableW runnable){
    synchronized(queries){
        queries.add(runnable);
        if(!queries.isBusy()){
            AppDisplay.disableTouchScreen(this);
            AppDisplay.disableRotation(this);
            queries.next();
        }
    }
}
private void confirmQuery(NavigatorKey.Is navigatorKey){
    synchronized(queries){
        if(!queries.isEmpty()){
            queries.next();
        } else {
            queries.done();
            AppDisplay.enableRotation(this);
            AppDisplay.enableTouchScreen(this);
        }
    }
}

public void navigateTo(NavigatorKey.Is navigatorKey, DestinationKey.Is destinationKey, NavigationOption option){
    navigateTo(navigatorKey, destinationKey, option, false, null);
}
public void navigateTo(NavigatorKey.Is navigatorKey, DestinationKey.Is destinationKey, NavigationOption option, boolean bindArguments, Arguments arguments){
    addQuery(navigatorKey, new RunnableW(){
        @Override
        public void runSafe(){
            Navigator navigator = getNavigator(navigatorKey);
            DestinationDetails destination;
            StackEntry e = getLastEntry(null, false);
            if(e.getNavigatorKey() != DIALOG){
                destination = destinationManager.findBestDestination(navigatorKey, e.getDestination(), destinationKey);
            } else {
                destination = destinationManager.findBestDestination(navigatorKey, getLastDestination(null, true), destinationKey);
            }
            if(destination == null){
                if(BuildConfig.DEBUG_ONLY){
                    String target = destinationKey != null ? destinationKey.name() : "targetKey is " + "null";
                    String current;
                    if(e.getDestination() != null){
                        current = e.getDestination().getKey() != null ? e.getDestination().getKey().name() : DebugTrack.getFullSimpleName(e.getRef());
                    } else {
                        current = null;
                    }
DebugException.start().log("Destination not found, from " + current + " to " + target).end();
                }
                confirmQuery(navigatorKey);
                return;
            }
            navigateTo(navigator, destination, option, bindArguments, arguments);
        }
    });
}

public void navigateTo(NavigatorKey.Is navigatorKey, Class<defNavigable> target, NavigationOption option){
    navigateTo(navigatorKey, target, option, false, null);
}
public void navigateTo(NavigatorKey.Is navigatorKey, Class<defNavigable> target, NavigationOption option, boolean bindArguments, Arguments arguments){
    addQuery(navigatorKey, new RunnableW(){
        @Override
        public void runSafe(){
            Navigator navigator = getNavigator(navigatorKey);
            DestinationDetails destination = destinationManager.findBestDestination(navigatorKey, getLastDestination(null, false), new FunctionW<DestinationDetails, Boolean>(){
                @Override
                public Boolean apply(DestinationDetails d){
                    return d.getTarget() == target;
                }
            });
            if((destination == null) && (navigatorKey == DIALOG)){
                destination = new DestinationDetails(null, null, target, null);
            }
            if(destination == null){
                if(BuildConfig.DEBUG_ONLY){
                    StackEntry e = getLastEntry(null, false);
                    String current = e.getDestination().getKey() != null ? e.getDestination().getKey().name() : DebugTrack.getFullSimpleName(e.getRef());
DebugException.start().log("Destination not found, from " + current + " to " + DebugTrack.getFullSimpleName(target)).end();

                }
                confirmQuery(navigatorKey);
                return;
            }
            navigateTo(navigator, destination, option, bindArguments, arguments);
        }
    });
}
private void navigateTo(Navigator navigator, DestinationDetails destinationDetails, NavigationOption option, boolean bindArguments, Arguments arguments){
    Handler.MAIN().post(me(), new NavigateTo(navigator, destinationDetails, option, bindArguments, arguments));
}
public void confirmBound(Navigator navigator){
    post(navigator.getKey(), NAVIGATE_TO_BOUND);
}

public void navigateClose(NavigatorManager.DestinationKey.Is key, NavigationOption option){
    StackEntry entry = getEntry(key);
    if(entry == null){
        return;
    }
    if(entry.equals(stack.peekLast())){
        navigateBackStack(false, null, null);
    } else {
        addQuery(entry.getNavigatorKey(), new NavigateClose(getNavigator(entry.getNavigatorKey()), entry, option));
    }
}
public void navigateBackStack(boolean bindArguments, Arguments arguments, NavigationOption option){
    addQuery(null, new NavigateBack(getLastNavigator(false), option, bindArguments, arguments));
}
public void navigateBackStackOrRemove(Navigator navigator, long id, NavigationOption option){
    StackEntry entry = stack.get(id);
    if(entry == null){
        return;
    }
    if(stack.peekLast() != entry){
        stack.remove(entry);
        toDebugLogRemoveEntry(entry);
    } else {
        addQuery(navigator.getKey(), new RunnableW(){
            @Override
            public void runSafe(){
                StackEntry entry = stack.get(id);
                if(entry != null){
                    if(stack.peekLast() == entry){
                        navigateBackStack(false, null, option);
                    } else {
                        stack.remove(entry);
                        toDebugLogRemoveEntry(entry);
                    }
                }
                confirmQuery(navigator.getKey());
            }
        });
    }
}

public void confirmNavigate(Navigator.NavigateType type, Navigator navigator){
    switch(type){
        case TO:{
            toDebugLogNavigateReached();
            post(navigator.getKey(), NAVIGATE_TO_CONFIRMED);
            confirmQuery(navigator.getKey());
        }
        break;
        case BACK:{
            toDebugLogNavigateReached();
            post(navigator.getKey(), NAVIGATE_BACK_CONFIRMED);
            confirmQuery(navigator.getKey());
        }
        break;
        case CLOSE:{
            confirmQuery(navigator.getKey());
        }
        break;
    }
}

public void navigateCloseApplication(){
    StackEntry entry = getLastEntry(ACTIVITY, false);
    if(entry == null){
DebugException.start().log("did not find any activity").end();
        return;
    }
    int indexOf = stack.indexOf(entry);
    if(indexOf != 0){
DebugException.start().log("activity is not the first entry").end();
        return;
    }
    addQuery(ACTIVITY, new NavigateClose(getNavigator(ACTIVITY), entry, null));
}

public void removeEntry(long id, NavigationOption option){
    StackEntry entry = stack.remove(id);
    toDebugLogRemoveEntry(entry);
}
public void moveEntry(long idToMove, long afterId, NavigationOption option){
    StackEntry entry = stack.move(idToMove, afterId);
    toDebugLogMoveEntry(entry, afterId);
}
public void insertEntry(long afterId, StackEntry entry, NavigationOption option){
    stack.insert(entry, afterId);
    toDebugLogInsertEntry(entry, afterId);
}

public DebugString toStringDebugStackPath(){
    DebugString data = new DebugString();
    for(StackEntry e: stack.list()){
        data.append(":");
        if(e.getDestination().getKey() != null){
            data.append(e.getDestination().getKey().name());
        } else {
            if(e.isBound()){
                data.append(DebugTrack.getFullSimpleName(e.getRefType()));
            }
        }
    }
    return data;
}
final public void toDebugLogStack(){
    for(StackEntry e: stack.list()){
        StringBuilder data = new StringBuilder();
        data.append(":").append(e.getBindID());
        if(e.getDestination().getKey() != null){
            data.append(":").append(e.getDestination().getKey().name());
        } else {
            data.append(":").append(DebugTrack.getFullSimpleName(e.getRefType()));
        }
        data.append(":").append(ObjectTo.hashcodeString(e.getRef()));
DebugLog.start().send(data.toString()).end();
    }
}
final public void toDebugLogPath(){
DebugLog.start().send(toStringDebugStackPath()).end();
}
private void toDebugLogNavigateStart(NavigatorKey.Is targetNavigatorKey, DestinationDetails targetDestinationDetails){
    if(!DebugLog.isEnable(this)){
        return;
    }
    StackEntry e = getLastEntry(null, false);
    if(e == null){
DebugLog.start().send(this, "END OF APPLICATION").end();
        return;
    }
    StringBuilder data = new StringBuilder();
    data.append("ID:").append(e.getBindID());
    if(targetDestinationDetails != null){
        data.append(" REQUEST NAVIGATE");
    } else {
        data.append(" REQUEST NAVIGATE BACK");
    }
    data.append(" FROM ").append(e.getNavigatorKey().name());
    if(e.getDestination().getKey() != null){
        data.append(":").append(e.getDestination().getKey().name());
    }
    data.append(":").append(DebugTrack.getFullSimpleName(e.getDestination().getTarget()));
    if(targetDestinationDetails != null){
        data.append(" TO ").append(targetNavigatorKey.name());
        if(targetDestinationDetails.getKey() != null){
            data.append(":").append(targetDestinationDetails.getKey().name());
        }
        data.append(":").append(targetDestinationDetails.getTarget().getSimpleName());
    }
DebugLog.start().send(this, data.toString()).end();
}
private void toDebugLogNavigateReached(){
    if(!DebugLog.isEnable(this)){
        return;
    }
    StackEntry e = getLastEntry(null, false);
    StringBuilder data = new StringBuilder();
    data.append("ID:").append(e.getBindID());
    data.append(" REACHED ");
    data.append(e.getNavigatorKey().name());
    if(e.getDestination().getKey() != null){
        data.append(":").append(e.getDestination().getKey().name());
    }
    data.append(":").append(DebugTrack.getFullSimpleNameWithHashcode(e.getRef()));
DebugLog.start().send(this, data.toString()).end();
}
private void toDebugLogRemoveEntry(StackEntry e){
    if(!DebugLog.isEnable(this)){
        return;
    }
    Navigator navigator = getNavigator(e.getNavigatorKey());
    StringBuilder data = new StringBuilder();
    data.append("ID:").append(e.getBindID());
    data.append(" ENTRY REMOVED ");
    data.append(navigator.getKey().name());
    data.append(":").append(DebugTrack.getFullSimpleName(e.getRef()));
    data.append(":").append(ObjectTo.hashcodeString(e.getRef()));
DebugLog.start().send(this, data.toString()).end();
}
private void toDebugLogMoveEntry(StackEntry e, long afterId){
    if(!DebugLog.isEnable(this)){
        return;
    }
    Navigator navigator = getNavigator(e.getNavigatorKey());
    StringBuilder data = new StringBuilder();
    data.append("ID:").append(e.getBindID());
    data.append(" ENTRY MOVED ");
    data.append(navigator.getKey().name());
    data.append(":").append(DebugTrack.getFullSimpleName(e.getRef()));
    data.append(":").append(ObjectTo.hashcodeString(e.getRef()));
    data.append(" BEFORE ID: ").append(afterId);
DebugLog.start().send(this, data.toString()).end();
}
private void toDebugLogInsertEntry(StackEntry e, long afterId){
    if(!DebugLog.isEnable(this)){
        return;
    }
    Navigator navigator = getNavigator(e.getNavigatorKey());
    StringBuilder data = new StringBuilder();
    data.append("ID:").append(e.getBindID());
    data.append("## ENTRY INSERTED ");
    data.append(navigator.getKey().name());
    data.append(":").append(DebugTrack.getFullSimpleName(e.getRef()));
    data.append(":").append(ObjectTo.hashcodeString(e.getRef()));
    data.append(" BEFORE ID: ").append(afterId);
DebugLog.start().send(this, data.toString()).end();
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

public enum Event{
    ON_NAVIGATE_TO, NAVIGATE_TO_BOUND, NAVIGATE_TO_CONFIRMED, ON_NAVIGATE_BACK, NAVIGATE_BACK_CONFIRMED,
}

public interface DestinationKey{
    static Is find(String name){
        return Is.findTypeOf(Is.class, name);
    }

    class Is extends EnumBase.Is{
        public Is(String name){
            super(name);
        }

    }

}

public interface NavigatorKey{
    Is ACTIVITY = new Is("ACTIVITY");
    Is FRAGMENT = new Is("FRAGMENT");
    Is DIALOG = new Is("DIALOG");

    class Is extends EnumBase.Is{
        protected Is(String name){
            super(name);
        }

    }

}

private class NavigateTo extends RunnableW{
    int failureCounter = 0;
    int failureMax = RETRY_MAX_ATTEMPT;
    Navigator navigator;
    DestinationDetails destinationDetails;
    NavigationOption option;
    boolean bindArguments;
    Arguments arguments;
    NavigateTo(Navigator navigator, DestinationDetails destinationDetails, NavigationOption option, boolean bindArguments, Arguments arguments){
        this.navigator = navigator;
        this.destinationDetails = destinationDetails;
        this.option = option;
        this.bindArguments = bindArguments;
        this.arguments = arguments;
    }
    boolean attempt(){
        if(AppContext.isChangingConfigurations()){
DebugException.start().log("Failed to navigate to " + destinationDetails.getTarget() + "(" + failureCounter + ") configuration changing").end();
            return false;
        }
        StackEntry entry = getLastEntry(null, false);
        if(!(entry.getStep().isAtLeast(CONSTRUCTED))){

DebugException.start().log("Failed to navigate to (" + failureCounter + ") ref is not construct yet").end();


            return false;
        }
        if(entry.getRef() != null){
            LifecycleEvent.on(LifecycleEvent.Event.RESUME, entry.getRef(), new RunnableSubscription(){
                @Override
                public void onComplete(){
                    unsubscribe();
                    navigate();
                }
            });
        } else {
            navigate();
        }
        return true;
    }
    void navigate(){
        toDebugLogNavigateStart(navigator.getKey(), destinationDetails);
        me().post(navigator.getKey(), ON_NAVIGATE_TO);
        StackEntry entry = stack.push(navigator.getKey(), destinationDetails);
        if(bindArguments){
            entry.bindArguments(arguments);
        }
        navigator.navigate(TO, stack.peekLast(), entry, option);
    }
    @Override
    public void runSafe(){
        if(attempt()){
            return;
        }
        failureCounter++;
        if(failureCounter < failureMax){
            Handler.MAIN().post(me(), RETRY_DELAY_ATTEMPT_MS, this);
        } else {
DebugException.start().explode("Failed to navigate to " + destinationDetails.getTarget() + "(" + failureCounter + ") max attempt reached").end();
        }
    }

}
private class NavigateBack extends RunnableW{
    int failureCounter = 0;
    int failureMax = RETRY_MAX_ATTEMPT;
    Navigator navigator;
    NavigationOption option;
    boolean bindArguments;
    Arguments arguments;
    NavigateBack(Navigator navigator, NavigationOption option, boolean bindArguments, Arguments arguments){
        this.navigator = navigator;
        this.option = option;
        this.bindArguments = bindArguments;
        this.arguments = arguments;
    }
    boolean attempt(){
        if(stack.isEmpty()){
            confirmQuery(null);
            return true;
        } else {
            if(AppContext.isChangingConfigurations()){
DebugException.start().log("Failed to navigate to back (" + failureCounter + ") configuration changing").end();
                return false;
            }
            StackEntry entry = getLastEntry(null, false);
            if(!(entry.getStep().isAtLeast(CONSTRUCTED))){
DebugException.start().log("Failed to navigate to back (" + failureCounter + ") ref is not construct yet").end();
                return false;
            }
            if(entry.getStep().isAtLeast(PAUSED)){
                navigate();
            } else {
                LifecycleEvent.on(LifecycleEvent.Event.RESUME, entry.getRef(), new RunnableSubscription(){
                    @Override
                    public void onComplete(){
                        unsubscribe();
                        navigate();
                    }
                });
            }
            return true;
        }
    }
    void navigate(){
        toDebugLogNavigateStart(navigator.getKey(), null);
        me().post(navigator.getKey(), ON_NAVIGATE_BACK);
        StackEntry current = stack.pop();
        StackEntry target = stack.peekLast();
        if(target == null){
DebugException.start().log("target is null").end();
        }
        if(bindArguments){
            target.bindArguments(arguments);
        }
        boolean done = navigator.navigate(BACK, current, target, option);
        if(done){
            stack.popConfirmed(current, target);
        }
        else {
            confirmQuery(navigator.getKey());
            if(target.getNavigatorKey() == ACTIVITY){
                stack.push(current);
                AppContext.getApplication().closeMainActivity();
            }
            else{
                navigateBackStack(bindArguments, arguments, option);
            }

        }
    }
    @Override
    public void runSafe(){
        if(attempt()){
            return;
        }
        failureCounter++;
        if(failureCounter < failureMax){
            Handler.MAIN().post(me(), RETRY_DELAY_ATTEMPT_MS, this);
        } else {
DebugException.start().explode("Failed to navigate to back (" + failureCounter + ") max attempt reached").end();
        }
    }

}
private class NavigateClose extends RunnableW{
    int failureCounter = 0;
    int failureMax = RETRY_MAX_ATTEMPT;
    Navigator navigator;
    StackEntry entry;
    NavigationOption option;
    NavigateClose(Navigator navigator, StackEntry entry, NavigationOption option){
        this.navigator = navigator;
        this.entry = entry;
        this.option = option;
    }
    boolean attempt(){
        if(AppContext.isChangingConfigurations()){
DebugException.start().log("Failed to navigate to " + entry.getDestination().getCurrent() + "(" + failureCounter + ") configuration changing").end();
            return false;
        }
        if(!(entry.getStep().isAtLeast(CONSTRUCTED))){
DebugException.start().log("Failed to navigate to (" + failureCounter + ") ref is not construct yet").end();
            return false;
        }
        navigate();
        return true;
    }
    void navigate(){
        toDebugLogRemoveEntry(entry);
        if(!navigator.navigate(CLOSE, null, entry, option)){
            confirmNavigate(CLOSE, navigator);
        }
    }
    @Override
    public void runSafe(){
        if(attempt()){
            return;
        }
        failureCounter++;
        if(failureCounter < failureMax){
            Handler.MAIN().post(me(), RETRY_DELAY_ATTEMPT_MS, this);
        } else {
DebugException.start().explode("Failed to navigate to " + entry.getDestination().getCurrent() + "(" + failureCounter + ") max attempt reached").end();
        }

    }

}

}
