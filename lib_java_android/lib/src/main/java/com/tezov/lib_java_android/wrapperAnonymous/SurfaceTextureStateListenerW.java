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

import android.graphics.SurfaceTexture;

import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java_android.type.android.wrapper.TextureViewW;

public abstract class SurfaceTextureStateListenerW implements TextureViewW.SurfaceTextureListener{
public SurfaceTextureStateListenerW(){
DebugTrack.start().create(this).end();
}

@Override
public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height){
}

@Override
public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height){
}

@Override
public boolean onSurfaceTextureDestroyed(SurfaceTexture surface){
    return false;
}

@Override
public void onSurfaceTextureUpdated(SurfaceTexture surface){
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
}

}
