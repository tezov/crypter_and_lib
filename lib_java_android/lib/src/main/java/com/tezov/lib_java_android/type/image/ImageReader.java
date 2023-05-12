/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.type.image;

import com.tezov.lib_java.debug.DebugLog;
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

import android.media.Image;
import android.os.Build;
import android.os.Handler;
import android.view.Surface;

import androidx.annotation.RequiresApi;

import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java_android.type.image.imageHolder.ImageHolder;
import com.tezov.lib_java_android.type.image.imageHolder.ImageJPEG;
import com.tezov.lib_java_android.type.image.imageHolder.ImageNV21;
import com.tezov.lib_java.type.primaire.Size;
import com.tezov.lib_java_android.wrapperAnonymous.ImageReaderOnImageAvailableListenerW;

public class ImageReader{
private final android.media.ImageReader reader;
private int orientation = 0;

public ImageReader(int width, int height, ImageFormat format, int maxImages){
DebugTrack.start().create(this).end();
    reader = android.media.ImageReader.newInstance(width, height, format.value, maxImages);
}

public static ImageReader newInstance(Size size, ImageFormat format, int maxImages){
    return newInstance(size.getWidth(), size.getHeight(), format, maxImages);
}

public static ImageReader newInstance(int width, int height, ImageFormat format, int maxImages){
    return new ImageReader(width, height, format, maxImages);
}

public void setOrientation(int value){
    this.orientation = value;
}

public int getWidth(){
    return reader.getWidth();
}

public int getHeight(){
    return reader.getHeight();
}

public int getImageFormat(){
    return reader.getImageFormat();
}

public int getMaxImages(){
    return reader.getMaxImages();
}

public Surface getSurface(){
    return reader.getSurface();
}

public void setOnImageAvailableListener(ImageReaderOnImageAvailableListenerW listener, Handler handler){
    reader.setOnImageAvailableListener(listener, handler);
}

public void close(){
    reader.close();
}

@RequiresApi(api = Build.VERSION_CODES.P)
public void discardFreeBuffers(){
    reader.discardFreeBuffers();
}

public Image acquireLatestImage(){
    return reader.acquireLatestImage();
}
public Image acquireNextImage(){
    return reader.acquireNextImage();
}
public ImageHolder acquireNextImageHolder(){
    ImageHolder imageHolder = null;
    Image image = reader.acquireNextImage();
    if(image != null){
        int format = image.getFormat();
        if(format == android.graphics.ImageFormat.YUV_420_888){
            imageHolder = new ImageNV21(image);
        } else if(format == android.graphics.ImageFormat.JPEG){
            imageHolder = new ImageJPEG(image);
        } else {
DebugException.start().log("Format not supported").end();
        }

        image.close();
        return imageHolder.rotate(orientation);
    } else {
        return null;
    }
}
public void discardNextImage(){
    Image image = reader.acquireNextImage();
    if(image != null){
        image.close();
    }
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
}


public enum ImageFormat{
    JPEG(android.graphics.ImageFormat.JPEG), NV21(android.graphics.ImageFormat.YUV_420_888);
    int value;
    ImageFormat(int value){
        this.value = value;
    }
    public int getValue(){
        return value;
    }
}


}
