/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.navigation;

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

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugString;

public class NavigationOption{
private boolean keepInStack_NavTo = true;
private boolean keepInStack_NavBack = false;
private boolean releaseRef = false;
private boolean single = false;
private boolean redirectIntent = false;
private boolean postCancel_NavBack = false;

public boolean isKeptInStack_NavTo(){
    return keepInStack_NavTo;
}
public NavigationOption setKeepInStack_NavTo(boolean flag){
    this.keepInStack_NavTo = flag;
    return this;
}

public boolean isKeptInStack_NavBack(){
    return keepInStack_NavBack;
}
public NavigationOption setKeepInStack_NavBack(boolean flag){
    this.keepInStack_NavBack = flag;
    return this;
}

public boolean isKeptInStack(){
    return keepInStack_NavBack && keepInStack_NavTo;
}
public NavigationOption setKeepInStack(boolean flag){
    this.keepInStack_NavBack = flag;
    this.keepInStack_NavTo = flag;
    return this;
}

public boolean isReleasedRef(){
    return releaseRef;
}
public NavigationOption setReleaseRef(boolean flag){
    this.releaseRef = flag;
    return this;
}

public boolean isSingle(){
    return single;
}
public NavigationOption setSingle(boolean flag){
    this.single = flag;
    return this;
}

public boolean isRedirectIntent(){
    return redirectIntent;
}
public NavigationOption setRedirectIntent(boolean copySourceIntent){
    this.redirectIntent = copySourceIntent;
    return this;
}

public NavigationOption setPostCancel_NavBack(boolean flag){
    this.postCancel_NavBack = flag;
    return this;
}
public boolean mustPostCancel_NavBack(){
    return postCancel_NavBack;
}

public DebugString toDebugString(){
    DebugString data = new DebugString();
    data.append("keepInStack_NavTo", keepInStack_NavTo);
    data.append("keepInStack_NavBack", keepInStack_NavBack);
    data.append("releaseRef", releaseRef);
    data.append("single", single);
    return data;
}

final public void toDebugLog(){
DebugLog.start().send(toDebugString()).end();
}

}
