/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.type.android.wrapper;

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

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.util.Size;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.tezov.lib_java.debug.DebugTrack;

public class SurfaceViewW extends android.view.SurfaceView{
private boolean isCreated = false;

public SurfaceViewW(Context context){
    super(context);
DebugTrack.start().create(this).end();
    setBackgroundColor(Color.TRANSPARENT);
    setFormat(PixelFormat.TRANSPARENT);
    getHolder().addCallback(new SurfaceHolder.Callback(){
        @Override
        public void surfaceCreated(SurfaceHolder holder){
            isCreated = true;
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height){

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder){
            isCreated = false;
        }
    });
}

public Surface getSurface(){
    return getHolder().getSurface();
}

public void addCallback(SurfaceHolder.Callback callback){
    getHolder().addCallback(callback);
}

public void removeCallback(SurfaceHolder.Callback callback){
    getHolder().removeCallback(callback);
}

public void setFormat(int format){
    getHolder().setFormat(format);
}

public boolean isCreated(){
    return isCreated;
}

public void show(boolean flag){
    if(flag){
        setAlpha(1.0f);
    } else {
        setAlpha(0.0f);
    }
}

public void toggleVisibility(){
    if(getAlpha() == 1.0f){
        setAlpha(0.0f);
    } else {
        setAlpha(0.0f);
    }
}

public void setSize(int width, int height){
    ViewGroup.LayoutParams param = getLayoutParams();
    if(param == null){
        param = new FrameLayout.LayoutParams(width, height);
    } else {
        param.width = width;
        param.height = height;
    }
    setLayoutParams(param);
    getHolder().setFixedSize(width, height);
}

public Size getSize(){
    return new Size(getWidth(), getHeight());
}

public void setSize(Size size){
    setSize(size.getWidth(), size.getHeight());
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

}
