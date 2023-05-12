/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.type.image.imageHolder;

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

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.tezov.lib_java_android.application.AppContext;
import com.tezov.lib_java.async.notifier.task.TaskState;
import com.tezov.lib_java.async.notifier.task.TaskValue;
import com.tezov.lib_java.buffer.ByteBuffer;
import com.tezov.lib_java.file.File;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.primaire.Size;

import java.util.Locale;

import static com.tezov.lib_java_android.type.image.imageHolder.ImageBitmap.FILE_EXTENSION_BMP;
import static com.tezov.lib_java_android.type.image.imageHolder.ImageJPEG.FILE_EXTENSION_JPG;
import static com.tezov.lib_java_android.type.image.imageHolder.ImageNV21.FILE_EXTENSION_NV21;

public abstract class ImageHolder{
private ByteBuffer buffer;
private int width;
private int height;

protected ImageHolder(ByteBuffer buffer, int width, int height){
DebugTrack.start().create(this).end();
    this.buffer = buffer;
    this.width = width;
    this.height = height;
}

public static <IB extends ImageHolder> TaskValue<IB>.Observable load(File file){
    if(file == null){
        return null;
    }
    String extension = file.getExtension();
    if(extension.equals(FILE_EXTENSION_BMP)){
        return (TaskValue<IB>.Observable)ImageBitmap.load(file);
    }
    if(extension.equals(FILE_EXTENSION_JPG)){
        return (TaskValue<IB>.Observable)ImageJPEG.load(file);
    }
    if(extension.equals(FILE_EXTENSION_NV21)){
        return (TaskValue<IB>.Observable)ImageNV21.load(file);
    }

DebugException.start().unknown("format", extension).end();


    return null;
}

protected void setSize(int width, int height){
    this.width = width;
    this.height = height;
}

protected void swapSize(){
    int tmp = this.width;
    this.width = height;
    this.height = tmp;
}

public abstract ImageFormat getFormat();

public int getWidth(){
    return width;
}

public int getHeight(){
    return height;
}

public Size getSize(){
    return new Size(getWidth(), getHeight());
}

public float getRatio(){
    return ((float)height) / ((float)width);
}

public abstract boolean isCompressed();

public boolean hasByteBuffer(){
    return buffer != null;
}

public ByteBuffer toByteBuffer(){
    return buffer.rewind();
}

protected void setByteBuffer(ByteBuffer buffer){
    this.buffer = buffer;
}

public abstract ImageHolder rotate(int angle);

public Drawable toDrawable(){
    return new BitmapDrawable(AppContext.getResources().get(), toBitmap());
}

public abstract Bitmap toBitmap();

public abstract ImageBitmap toImageBitmap();

public abstract ImageJPEG toImageJpeg(int quality);

public abstract TaskState.Observable save(File file);

public <H extends ImageHolder> H to(ImageFormat format){
    if(format == ImageFormat.BITMAP){
        return (H)toImageBitmap();
    }
    if(format == ImageFormat.JPEG){
        return (H)toImageJpeg(100);
    }

DebugException.start().unknown("format", format).end();

    return null;
}

public DebugString toDebugString(){
    DebugString data = new DebugString();
    data.append("width", width);
    data.append("height", height);
    data.append("ratio", String.format(Locale.US, "%.2f", getRatio()));
    return data;
}

final public void toDebugLog(){
DebugLog.start().send(toDebugString()).end();
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

}
