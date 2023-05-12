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

import com.tezov.lib_java.type.collection.Snapper;
import com.tezov.lib_java_android.type.image.imageHolder.ImageBitmap;
import com.tezov.lib_java_android.type.image.imageHolder.ImageHolder;

import java.util.Iterator;

public class CameraViewSnapList extends CameraView{
private Snapper<ImageHolder> snapBuffer;
private boolean clearSnapBufferWhenDrawLast = true;

public CameraViewSnapList(Context context){
    super(context);
    init(context, null, 0, 0);
}

public CameraViewSnapList(Context context, @Nullable AttributeSet attrs){
    super(context, attrs);
    init(context, attrs, 0, 0);
}

public CameraViewSnapList(Context context, @Nullable AttributeSet attrs, int defStyleAttr){
    super(context, attrs, defStyleAttr);
    init(context, attrs, defStyleAttr, 0);
}

public CameraViewSnapList(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes){
    super(context, attrs, defStyleAttr, defStyleRes);
    init(context, attrs, defStyleAttr, defStyleRes);
}

private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes){
    snapBuffer = new Snapper<>(this::acquireNextImage, 1);
}

public CameraViewSnapList setMaxBufferSize(int size){
    snapBuffer.setMaxSize(size);
    return this;
}

public void clearSnapBufferWhenDrawLast(boolean flag){
    this.clearSnapBufferWhenDrawLast = flag;
}

public void showNextSnap(){
    drawNextSnap();
    show(Type.SNAPSHOT, true);
}

public void showLastSnap(){
    drawLastSnap();
    show(Type.SNAPSHOT, true);
}

public ImageHolder pollSnap(){
    return snapBuffer.poll();
}

public ImageHolder peekSnap(){
    ImageHolder b = snapBuffer.peek();
    if(b != null){
        return b;
    } else {
        return ImageBitmap.from(getImageView());
    }
}

public ImageHolder getSnap(int index){
    return snapBuffer.get(index);
}

public boolean hasSnap(){
    return !snapBuffer.isEmpty();
}

public int sizeSnap(){
    return snapBuffer.size();
}

public Iterator<ImageHolder> iteratorSnap(){
    return snapBuffer.iterator();
}

public void clearSnapBuffer(){
    snapBuffer.clear();
}

@Override
public void onImageAvailable(){
    if(snapBuffer.isFull()){
        setBusy(true);
    }
    snapBuffer.snap();
    post(Event.ACQUIRED);
}

public void drawNextSnap(){
    if(hasSnap()){
        ImageHolder b;
        if((snapBuffer.size() > 1) || clearSnapBufferWhenDrawLast){
            b = pollSnap();
        } else {
            b = peekSnap();
        }
        setImageBitmap(b.toBitmap());
    }
}

public void drawLastSnap(){
    if(hasSnap()){
        while(snapBuffer.size() > 1){
            pollSnap();
        }
        ImageHolder b;
        if(clearSnapBufferWhenDrawLast){
            b = pollSnap();
        } else {
            b = peekSnap();
        }
        setImageBitmap(b.toBitmap());
    }
}

}
