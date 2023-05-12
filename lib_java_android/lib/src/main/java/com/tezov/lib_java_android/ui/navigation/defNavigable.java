/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.navigation;

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
import androidx.lifecycle.LifecycleOwner;

import com.tezov.lib_java_android.ui.navigation.stack.StackEntry;

public interface defNavigable extends LifecycleOwner{
Long getBindID();

void setBindID(Long id);

void onPrepare(boolean hasBeenReconstructed);

void onOpen(boolean hasBeenReconstructed, boolean hasBeenRestarted);

void confirmConstructed(StackEntry entry);

void confirmDestroyed(StackEntry entry);

void removedFromStack();

boolean onNewNavigationArguments(NavigationArguments arg);

boolean requestViewUpdate(Integer what, NavigationArguments arg);

}
