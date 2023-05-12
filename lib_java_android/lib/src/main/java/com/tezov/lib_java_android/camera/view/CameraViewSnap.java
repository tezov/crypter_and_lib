/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.camera.view;

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

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import com.tezov.lib_java_android.type.image.imageHolder.ImageBitmap;
import com.tezov.lib_java_android.type.image.imageHolder.ImageHolder;

public class CameraViewSnap extends CameraView{
private ImageHolder imageHolder = null;

public CameraViewSnap(Context context){
    super(context);
    init(context, null, 0, 0);
}
public CameraViewSnap(Context context, @Nullable AttributeSet attrs){
    super(context, attrs);
    init(context, attrs, 0, 0);
}
public CameraViewSnap(Context context, @Nullable AttributeSet attrs, int defStyleAttr){
    super(context, attrs, defStyleAttr);
    init(context, attrs, defStyleAttr, 0);
}
public CameraViewSnap(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes){
    super(context, attrs, defStyleAttr, defStyleRes);
    init(context, attrs, defStyleAttr, defStyleRes);
}

private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes){
}

public ImageHolder pollSnap(){
    ImageHolder tmp = imageHolder;
    imageHolder = null;
    return tmp;
}
public ImageHolder peekSnap(){
    if(imageHolder != null){
        return imageHolder;
    } else {
        return ImageBitmap.from(getImageView());
    }
}

public void drawSnap(){
    if(imageHolder != null){
        setImageBitmap(imageHolder.toBitmap());
    }
}

public boolean hasSnap(){
    return imageHolder != null;
}

public void clearSnap(){
    imageHolder = null;
}

@Override
public void onImageAvailable(){
    setBusy(true);
    imageHolder = acquireNextImage();
    post(Event.ACQUIRED);
}

}
