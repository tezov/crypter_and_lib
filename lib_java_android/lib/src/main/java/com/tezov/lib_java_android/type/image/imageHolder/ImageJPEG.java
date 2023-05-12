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
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import androidx.fragment.app.Fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.Image;

import com.tezov.lib_java_android.application.Application;
import com.tezov.lib_java.async.notifier.observer.value.ObserverValueE;
import com.tezov.lib_java.async.notifier.task.TaskState;
import com.tezov.lib_java.async.notifier.task.TaskValue;
import com.tezov.lib_java.buffer.ByteBuffer;
import com.tezov.lib_java.file.File;
import com.tezov.lib_java.file.FileW;
import com.tezov.lib_java.debug.DebugException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageJPEG extends ImageHolder{
public final static String FILE_EXTENSION_JPG = "jpg";

public ImageJPEG(Image image){
    super(null, 0, 0);

    if(image.getFormat() != android.graphics.ImageFormat.JPEG){
DebugException.start().log("Format not supported. Image need to be ImageFormat.JPEG").end();
    }

    setSize(image.getCropRect().width(), image.getCropRect().height());
    setByteBuffer(imageToBytes(image));
}

public ImageJPEG(ByteBuffer buffer, int width, int height){
    super(buffer, width, height);
}

private static Class<ImageJPEG> myClass(){
    return ImageJPEG.class;
}

public static ImageJPEG load(FileW file) throws IOException{

    if(!file.getExtension().equals(FILE_EXTENSION_JPG)){
DebugException.start().log("file extension is not correct." + " Expected:" + FILE_EXTENSION_JPG + " present:" + file.getExtension()).end();
    }


    return load(file.read());
}

public static TaskValue<ImageJPEG>.Observable load(File file){
    TaskValue<ImageJPEG> task = new TaskValue<>();
    Application.fileQueue().read(file, FILE_EXTENSION_JPG).observe(new ObserverValueE<ByteBuffer>(myClass()){
        @Override
        public void onComplete(ByteBuffer byteBuffer){
            task.notifyComplete(load(byteBuffer));
        }

        @Override
        public void onException(ByteBuffer byteBuffer, java.lang.Throwable e){
            task.notifyException(null, e);
        }
    });
    return task.getObservable();
}

public static ImageJPEG load(ByteBuffer byteBuffer){
    BitmapFactory.Options opt = new BitmapFactory.Options();
    opt.inJustDecodeBounds = true;
    BitmapFactory.decodeByteArray(byteBuffer.array(), byteBuffer.position(), byteBuffer.limit(), opt);
    return new ImageJPEG(byteBuffer, opt.outWidth, opt.outHeight);
}

private ByteBuffer imageToBytes(Image image){
    Image.Plane[] planes = image.getPlanes();
    java.nio.ByteBuffer buffer = planes[0].getBuffer();
    int offset;
    if(buffer.hasArray()){
        offset = buffer.arrayOffset();
    } else {
        offset = 0;
    }
    ByteBuffer byteBuffer = ByteBuffer.obtain(buffer.limit() - offset);
    buffer.get(byteBuffer.array());
    return byteBuffer;
}

@Override
public ImageFormat getFormat(){
    return ImageFormat.JPEG;
}

@Override
public boolean isCompressed(){
    return true;
}

@Override
public Bitmap toBitmap(){
    ByteBuffer byteBuffer = toByteBuffer();
    return BitmapFactory.decodeByteArray(byteBuffer.array(), byteBuffer.position(), byteBuffer.limit());
}

@Override
public ImageBitmap toImageBitmap(){
    return new ImageBitmap(toByteBuffer());
}

@Override
public ImageJPEG toImageJpeg(int quality){
    return this;
}

@Override
public TaskState.Observable save(File file){
    return Application.fileQueue().write(file, toByteBuffer(), FILE_EXTENSION_JPG);
}

@Override
public ImageHolder rotate(int angle){
    if(angle == 0){
        return this;
    }
    ByteBuffer byteBuffer = toByteBuffer();
    Matrix matrix = new Matrix();
    matrix.preRotate(angle);
    Bitmap b = BitmapFactory.decodeByteArray(byteBuffer.array(), byteBuffer.position(), byteBuffer.limit());
    setByteBuffer(null);
    Bitmap bRotated = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, true);
    setSize(bRotated.getWidth(), bRotated.getHeight());
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    bRotated.compress(Bitmap.CompressFormat.JPEG, 100, os);
    b.recycle();
    bRotated.recycle();
    setByteBuffer(ByteBuffer.wrap(os.toByteArray()));
    try{
        os.close();
    } catch(java.lang.Throwable e){

DebugException.start().log(e).end();

    }
    return this;
}

}
