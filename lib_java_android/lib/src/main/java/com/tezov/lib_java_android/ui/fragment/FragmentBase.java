/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.fragment;

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
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java_android.wrapperAnonymous.ViewOnTouchListenerW;

public abstract class FragmentBase extends Fragment{

protected FragmentBase(){
DebugTrack.start().create(this).end();
}

@Override
public void onAttach(android.content.Context context){
    super.onAttach(context);
DebugLog.start().track(this).end();
}

@Override
public void onCreate(@Nullable Bundle savedInstanceState){
    super.onCreate(savedInstanceState);
DebugLog.start().track(this).end();
}

protected abstract int getLayoutId();

@Nullable
@Override
public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
    return inflater.inflate(getLayoutId(), container, false);
}

@Override
public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
    super.onViewCreated(view, savedInstanceState);
DebugLog.start().track(this).end();
    view.setOnTouchListener(new ViewOnTouchListenerW(){
        @Override
        public boolean onTouched(View v, MotionEvent event){
            return onTouchEvent(event);
        }
    });
}

public boolean onTouchEvent(MotionEvent event){
    return false;
}

@Override
public void onViewStateRestored(@Nullable Bundle savedInstanceState){
    super.onViewStateRestored(savedInstanceState);
DebugLog.start().track(this).end();

//    if(savedInstanceState != null){
//        for(String key: savedInstanceState.keySet()){
//            DebugLog.start().send(this, "key:" + key).end();
//        }
//    }

}

@Override
public void onStart(){
    super.onStart();
DebugLog.start().track(this).end();
}

@Override
public void onResume(){
    super.onResume();
DebugLog.start().track(this).end();
}

@Override
public void onPause(){
    super.onPause();
DebugLog.start().track(this).end();
}

@Override
public void onSaveInstanceState(Bundle outState){
    super.onSaveInstanceState(outState);
DebugLog.start().track(this).end();
}

@Override
public void onStop(){
    super.onStop();
DebugLog.start().track(this).end();
}

@Override
public void onDetach(){
    super.onDetach();
DebugLog.start().track(this).end();
}

@Override
public void onDestroy(){
    super.onDestroy();
DebugLog.start().track(this).end();
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

}
