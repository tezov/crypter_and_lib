/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.type.image.imageHolder;

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.util.UtilsString;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import androidx.fragment.app.Fragment;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.Image;

import com.tezov.lib_java_android.application.Application;
import com.tezov.lib_java_android.application.VersionSDK;
import com.tezov.lib_java.async.notifier.observer.value.ObserverValueE;
import com.tezov.lib_java.async.notifier.task.TaskState;
import com.tezov.lib_java.async.notifier.task.TaskValue;
import com.tezov.lib_java.buffer.ByteBuffer;
import com.tezov.lib_java.file.File;
import com.tezov.lib_java.file.FileW;
import com.tezov.lib_java_android.renderScript.Script_NV21;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.util.UtilsBytes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImageNV21 extends ImageHolder{
public final static String FILE_EXTENSION_NV21 = "nv21";

public ImageNV21(Image image){
    super(null, 0, 0);

    if(image.getFormat() != android.graphics.ImageFormat.YUV_420_888){
DebugException.start().log("Format not supported, Image need to be ImageFormat.YUV_420_888").end();
    }

    setSize(image.getCropRect().width(), image.getCropRect().height());
    setByteBuffer(imageToBytes(image));
}

public ImageNV21(ByteBuffer buffer, int width, int height){
    super(buffer, width, height);
}

private static Class<ImageNV21> myClass(){
    return ImageNV21.class;
}

private static MetaData metaData(ByteBuffer byteBuffer){
    return new MetaData(byteBuffer);
}

private static List<ByteBuffer> buildReadBuffer(){
    ByteBuffer meta = ByteBuffer.obtain(MetaData.computeLength());
    List<ByteBuffer> byteBuffers = new ArrayList<>();
    byteBuffers.add(meta);
    return byteBuffers;
}

public static ImageNV21 load(FileW file) throws IOException{

    if(!file.getExtension().equals(FILE_EXTENSION_NV21)){
DebugException.start().log("file extension is not correct." + " Expected:" + FILE_EXTENSION_NV21 + " present:" + file.getExtension()).end();
    }


    return load(file.read(buildReadBuffer(), true));
}

public static TaskValue<ImageNV21>.Observable load(File file){
    TaskValue<ImageNV21> task = new TaskValue<>();
    Application.fileQueue().read(file, buildReadBuffer(), FILE_EXTENSION_NV21).observe(new ObserverValueE<List<ByteBuffer>>(myClass()){
        @Override
        public void onComplete(List<ByteBuffer> byteBuffers){
            task.notifyComplete(load(byteBuffers));
        }

        @Override
        public void onException(List<ByteBuffer> byteBuffers, java.lang.Throwable e){
            task.notifyException(null, e);
        }
    });
    return task.getObservable();
}

private static ImageNV21 load(List<ByteBuffer> byteBuffers){
    MetaData metaData = metaData(byteBuffers.get(0));
    return new ImageNV21(byteBuffers.get(1), metaData.width, metaData.height);
}

private ByteBuffer imageToBytes(Image image){
    //IMPROVE TO RENDER_SCRIPT
    // NEXT_TODO < API 24 green ?
    Rect crop = image.getCropRect();
    int width = getWidth();
    int height = getHeight();
    Image.Plane[] planes = image.getPlanes();
    ByteBuffer byteBuffer = ByteBuffer.obtain((width * height * 3) >> 1);
    byte[] data = byteBuffer.array();
    byte[] rowData = UtilsBytes.obtain(planes[0].getRowStride());
    int channelOffset = 0;
    int outputStride = 1;
    for(int i = 0; i < 3; i++){
        switch(i){
            case 0:
                channelOffset = 0;
                outputStride = 1;
                break;
            case 1:
                channelOffset = width * height + 1;
                outputStride = 2;
                break;
            case 2:
                channelOffset = width * height;
                outputStride = 2;
                break;
        }
        java.nio.ByteBuffer buffer = planes[i].getBuffer();
        int rowStride = planes[i].getRowStride();
        int pixelStride = planes[i].getPixelStride();
        int shift = (i == 0) ? 0 : 1;
        int w = width >> shift;
        int h = height >> shift;
        buffer.position(rowStride * (crop.top >> shift) + pixelStride * (crop.left >> shift));
        for(int row = 0; row < h; row++){
            int length;
            if(pixelStride == 1 && outputStride == 1){
                length = w;
                buffer.get(data, channelOffset, length);
                channelOffset += length;
            } else {
                length = (w - 1) * pixelStride + 1;
                buffer.get(rowData, 0, length);
                for(int col = 0; col < w; col++){
                    data[channelOffset] = rowData[col * pixelStride];
                    channelOffset += outputStride;
                }
            }
            if(row < h - 1){
                buffer.position(buffer.position() + rowStride - length);
            }
        }
    }
    return byteBuffer;
}

@Override
public ImageFormat getFormat(){
    return ImageFormat.NV21;
}

@Override
public boolean isCompressed(){
    return false;
}

private ByteBuffer metaData(){
    return new MetaData(this).toByteBuffer();
}

@Override
public ImageHolder rotate(int angle){
    if(angle == 0){
        return this;
    }
    Script_NV21.ResultRotate result;
    if(VersionSDK.isSupEqualTo24_NOUGAT()){ // IMPROVE RS avec API 21 ?
        result = Script_NV21.rotate(Script_NV21.requestRotate(toByteBuffer(), getWidth(), getHeight(), angle));
    } else {
        result = Script_NV21.javaRotate(Script_NV21.requestRotate(toByteBuffer(), getWidth(), getHeight(), angle));
    }
    if(result.isSwapped()){
        setSize(result.getWidth(), result.getHeight());
    }
    return this;
}

@Override
public Bitmap toBitmap(){
    return Script_NV21.toBitmap(toByteBuffer(), getWidth(), getHeight());
}

@Override
public ImageBitmap toImageBitmap(){
    return new ImageBitmap(toBitmap());
}

@Override
public ImageJPEG toImageJpeg(int quality){
    YuvImage yuvImage = new YuvImage(toByteBuffer().array(), android.graphics.ImageFormat.NV21, getWidth(), getHeight(), null);
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    yuvImage.compressToJpeg(new Rect(0, 0, getWidth(), getHeight()), quality, os);
    return new ImageJPEG(ByteBuffer.wrap(os.toByteArray()), getWidth(), getHeight());
}

@Override
public TaskState.Observable save(File file){
    return Application.fileQueue().write(file, new ByteBuffer[]{metaData(), toByteBuffer()}, FILE_EXTENSION_NV21);
}

private static class MetaData{
    int width;
    int height;
    int length;

    MetaData(ImageNV21 i){
        this.width = i.getWidth();
        this.height = i.getHeight();
        this.length = computeLength();
    }

    MetaData(ByteBuffer byteBuffer){
        this.width = byteBuffer.getInt();
        this.height = byteBuffer.getInt();
        this.length = computeLength();
    }

    static int computeLength(){
        return ByteBuffer.INT_SIZE(2);
    }

    ByteBuffer toByteBuffer(){
        ByteBuffer byteBuffer = ByteBuffer.obtain(length);
        byteBuffer.put(width);
        byteBuffer.put(height);
        return byteBuffer.rewind();
    }

}

}
