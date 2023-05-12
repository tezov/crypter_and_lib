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

import android.view.MotionEvent;

import com.tezov.lib_java.debug.DebugTrack;

public abstract class OnGestureListenerW implements android.view.GestureDetector.OnGestureListener{

public OnGestureListenerW(){
DebugTrack.start().create(this).end();
}

@Override
public boolean onDown(MotionEvent e){
    return true;
}

@Override
public boolean onSingleTapUp(MotionEvent e){
    return false;
}

@Override
public void onLongPress(MotionEvent e){
}

@Override
public void onShowPress(MotionEvent e){

}

@Override
public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY){
    return false;
}

@Override
public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY){
    return false;
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

}
