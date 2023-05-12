/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.navigation.destination;

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
import com.tezov.lib_java_android.ui.misc.TransitionManager;
import com.tezov.lib_java_android.ui.navigation.NavigationOption;
import com.tezov.lib_java_android.ui.navigation.NavigatorManager;
import com.tezov.lib_java_android.ui.navigation.defNavigable;

public class DestinationDetails{
protected NavigatorManager.DestinationKey.Is destinationKey;
protected Class<? extends defNavigable> source;
protected Class<? extends defNavigable> current;
protected Class<? extends defNavigable> target;
protected TransitionManager.Name.Is transition = null;

protected NavigationOption option = null;

public DestinationDetails(Class<? extends defNavigable> source, Class<? extends defNavigable> current, Class<? extends defNavigable> target, NavigatorManager.DestinationKey.Is destinationKey){
DebugTrack.start().create(this).end();
    this.source = source;
    this.current = current;
    this.target = target;
    this.destinationKey = destinationKey;
}

public DestinationDetails obtainOption(NavigationOption option){
    this.option = option;
    return this;
}
public NavigationOption obtainOption(){
    if(option == null){
        option = new NavigationOption();
    }
    return option;
}
public NavigationOption getOption(){
    return option;
}
public DestinationDetails setOption(NavigationOption option){
    this.option = option;
    return this;
}

public NavigatorManager.DestinationKey.Is getKey(){
    return destinationKey;
}
public <T extends defNavigable> Class<T> getSource(){
    return (Class<T>)source;
}
public <T extends defNavigable> Class<T> getCurrent(){
    return (Class<T>)current;
}
public <T extends defNavigable> Class<T> getTarget(){
    return (Class<T>)target;
}
public TransitionManager.Name.Is getTransition(){
    return transition;
}

public DestinationDetails setTransition(TransitionManager.Name.Is transition){
    this.transition = transition;
    return this;
}

public DebugString toDebugString(){
    DebugString data = new DebugString();
    data.append("destinationKey", destinationKey);
    data.appendFullSimpleName("source", source);
    data.appendFullSimpleName("current", current);
    data.appendFullSimpleName("target", target);
    data.append("transition", transition);
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

}
