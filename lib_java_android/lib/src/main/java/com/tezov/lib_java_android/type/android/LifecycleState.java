/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.type.android;

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
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import androidx.fragment.app.Fragment;

public enum LifecycleState{
    INITIALIZED(androidx.lifecycle.Lifecycle.State.INITIALIZED), CREATED(androidx.lifecycle.Lifecycle.State.CREATED), STARTED(androidx.lifecycle.Lifecycle.State.STARTED), RESUMED(
            androidx.lifecycle.Lifecycle.State.RESUMED), DESTROYED(androidx.lifecycle.Lifecycle.State.DESTROYED);
androidx.lifecycle.Lifecycle.State androidState;

LifecycleState(androidx.lifecycle.Lifecycle.State androidState){
    this.androidState = androidState;
}

public static LifecycleState get(androidx.lifecycle.LifecycleOwner lifecycleOwner){
    return get(lifecycleOwner.getLifecycle());
}

public static LifecycleState get(androidx.lifecycle.Lifecycle lifecycle){
    LifecycleState[] states = LifecycleState.values();
    androidx.lifecycle.Lifecycle.State currentState = lifecycle.getCurrentState();
    for(LifecycleState state: states){
        if(state.androidState == currentState){
            return state;
        }
    }
    return null;
}

public boolean isAtLeast(LifecycleState state){
    return this.ordinal() >= state.ordinal();
}
}
