/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.wrapperAnonymous;

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
import androidx.annotation.NonNull;

import com.tezov.lib_java.debug.DebugTrack;

public abstract class CameraStateListenerW extends android.hardware.camera2.CameraDevice.StateCallback{
public CameraStateListenerW(){
DebugTrack.start().create(this).end();
}

@Override
public void onOpened(@NonNull android.hardware.camera2.CameraDevice camera){

}

@Override
public void onClosed(@NonNull android.hardware.camera2.CameraDevice camera){
}

@Override
public void onDisconnected(@NonNull android.hardware.camera2.CameraDevice camera){
}

@Override
public void onError(@NonNull android.hardware.camera2.CameraDevice camera, int error){
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
}

}
