/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.application;

import com.tezov.lib_java.debug.DebugLog;
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

import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.collection.ListEntry;
import com.tezov.lib_java.type.primaire.Entry;
import com.tezov.lib_java_android.ui.activity.ActivityBase;

public class AppPermission{
private AppPermission(){
}

public static boolean isGranted(String permission){
    return ActivityCompat.checkSelfPermission(AppContext.get(), permission) == PackageManager.PERMISSION_GRANTED;
}
public static boolean[] isGranted(String[] permissions){
    boolean[] results = new boolean[permissions.length];
    for(int i = 0; i < results.length; i++){
        results[i] = isGranted(permissions[i]);
    }
    return results;
}

public static boolean allTrue(ListEntry<String, Boolean> permissions){
    boolean allTrue = true;
    for(Entry<String, Boolean> e: permissions){
        allTrue &= e.value;
    }
    return allTrue;
}

public static ActivityBase.RequestForPermission request(){
    return new ActivityBase.RequestForPermission();
}
public static Check check(){
    return new Check();
}

public static class Check{
    private final ListEntry<String, Boolean> resultPermissions;
    public Check(){
DebugTrack.start().create(this).end();
        resultPermissions = new ListEntry<>();
    }
    public Check add(String permission){
        resultPermissions.add(permission, AppPermission.isGranted(permission));
        return this;
    }
    public ListEntry<String, Boolean> result(){
        return resultPermissions;
    }
    @Override
    protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
        super.finalize();
    }

}

}
