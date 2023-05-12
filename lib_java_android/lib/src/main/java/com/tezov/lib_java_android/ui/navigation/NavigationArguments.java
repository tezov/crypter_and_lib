/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.navigation;

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

import static com.tezov.lib_java_android.ui.navigation.NavigationArguments.ArgumentKey.EXTRA;
import static com.tezov.lib_java_android.ui.navigation.NavigationArguments.How.CREATE;
import static com.tezov.lib_java_android.ui.navigation.NavigationArguments.How.GET;
import static com.tezov.lib_java_android.ui.navigation.NavigationArguments.How.OBTAIN;

import com.tezov.lib_java_android.application.Application;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.collection.Arguments;
import com.tezov.lib_java.type.defEnum.EnumBase;
import com.tezov.lib_java.type.ref.WR;
import com.tezov.lib_java_android.ui.navigation.destination.DestinationManager;
import com.tezov.lib_java_android.ui.state.State;

public class NavigationArguments{
private final WR<defNavigable> refWR;
private final Arguments<ArgumentKey.Is> arguments;

protected NavigationArguments(defNavigable ref, Arguments<ArgumentKey.Is> arguments){
DebugTrack.start().create(this).end();
    if(ref != null){
        refWR = WR.newInstance(ref);
    } else {
        refWR = null;
    }
    this.arguments = arguments;
}

public static NavigationArguments create(){
    DestinationManager destinationManager = Application.navigationHelper().getDestinationManager();
    return new NavigationArguments(null, destinationManager.arguments(null, CREATE));
}
public static NavigationArguments obtain(defNavigable ref){
    DestinationManager destinationManager = Application.navigationHelper().getDestinationManager();
    return new NavigationArguments(ref, destinationManager.arguments(ref, OBTAIN));
}
public static NavigationArguments get(defNavigable ref){
    DestinationManager destinationManager = Application.navigationHelper().getDestinationManager();
    return new NavigationArguments(ref, destinationManager.arguments(ref, GET));
}
public static NavigationArguments wrap(Arguments<ArgumentKey.Is> arguments){
    return new NavigationArguments(null, arguments);
}

public static boolean exist(NavigationArguments arguments){
    return (arguments != null) && arguments.exist();
}

protected defNavigable ref(){
    return refWR.get();
}
public boolean hasChanged(){
    return exist() && Application.navigationHelper().isArgumentsChanged(ref());
}
public boolean exist(){
    return arguments != null;
}

public Arguments<ArgumentKey.Is> getArguments(){
    return arguments;
}
public <VALUE> NavigationArguments put(ArgumentKey.Is key, VALUE value){
    arguments.put(key, value);
    return this;
}
public <VALUE> VALUE get(ArgumentKey.Is key){
    return arguments.getValue(key);
}

public <KEY, VALUE> VALUE getExtra(KEY key){
    Arguments<KEY> arguments = this.get(EXTRA);
    if(arguments == null){
        return null;
    }
    return arguments.getValue(key);
}
public <KEY, VALUE> NavigationArguments setExtra(KEY key, VALUE value){
    Arguments<KEY> arguments = this.get(EXTRA);
    if(arguments == null){
        arguments = new Arguments<>();
        this.put(EXTRA, arguments);
    }
    arguments.put(key, value);
    return this;
}
public <S extends State> S getState(){
    return this.get(ArgumentKey.STATE);
}
public NavigationArguments setState(State state){
    this.put(ArgumentKey.STATE, state);
    return this;
}

public DebugString toDebugString(){
    DebugString data = new DebugString();
    data.append("arguments", arguments);
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

public enum How{
    CREATE, GET, OBTAIN, TAKE
}
public interface ArgumentKey{
    Is STATE = new Is("STATE");
    Is EXTRA = new Is("EXTRA");
    class Is extends EnumBase.Is{
        public Is(String name){
            super(name);
        }
    }
}

}
