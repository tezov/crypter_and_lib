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
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.primaire.Size;

public class TextureViewW extends android.view.TextureView{
private Surface surface = null;

public TextureViewW(Context context){
    super(context);
    init(context, null, 0, 0);
}

public TextureViewW(Context context, AttributeSet attrs){
    super(context, attrs);
    init(context, attrs, 0, 0);
}

public TextureViewW(Context context, AttributeSet attrs, int defStyleAttr){
    super(context, attrs, defStyleAttr);
    init(context, attrs, defStyleAttr, 0);
}

public TextureViewW(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes){
    super(context, attrs, defStyleAttr, defStyleRes);
    init(context, attrs, defStyleAttr, defStyleRes);
}

private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes){
DebugTrack.start().create(this).end();
}

public void destroySurface(){
    surface = null;
}

public void createSurface(Size size){
    SurfaceTexture surfaceTexture = getSurfaceTexture();
    surfaceTexture.setDefaultBufferSize(size.getWidth(), size.getHeight());
    surface = new Surface(surfaceTexture);
}

public Surface getSurface(){
    return surface;
}

@Override
public boolean isAvailable(){
    return super.isAvailable() && surface != null;
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
