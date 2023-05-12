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
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

import com.tezov.lib_java_android.application.Application;
import com.tezov.lib_java.async.notifier.observer.value.ObserverValueE;
import com.tezov.lib_java.async.notifier.task.TaskState;
import com.tezov.lib_java.async.notifier.task.TaskValue;
import com.tezov.lib_java.buffer.ByteBuffer;
import com.tezov.lib_java.file.File;
import com.tezov.lib_java.file.FileW;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java_android.type.primaire.Color;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageBitmap extends ImageHolder{
public final static String FILE_EXTENSION_BMP = "png";
private Bitmap bitmap;

public ImageBitmap(Bitmap bitmap){
    super(null, bitmap.getWidth(), bitmap.getHeight());
    this.bitmap = bitmap;
}

public ImageBitmap(ByteBuffer buffer){
    super(buffer, 0, 0);
    bitmap = BitmapFactory.decodeByteArray(buffer.array(), buffer.position(), buffer.limit());
    setSize(bitmap.getWidth(), bitmap.getHeight());
}

public ImageBitmap(ByteBuffer buffer, int width, int height){
    super(buffer, width, height);
    this.bitmap = null;
}

private static Class<ImageBitmap> myClass(){
    return ImageBitmap.class;
}

public static ImageBitmap from(ImageView image){
    BitmapDrawable bitmapDrawable = (BitmapDrawable)image.getDrawable();
    if(bitmapDrawable == null){
        return null;
    }
    Bitmap bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();
    if(bitmap == null){
        return null;
    }
    return new ImageBitmap(bitmap);
}

public static ImageBitmap load(FileW file) throws IOException{

    if(!file.getExtension().equals(FILE_EXTENSION_BMP)){
DebugException.start().log("file extension is not correct." + " Expected:" + FILE_EXTENSION_BMP + " present:" + file.getExtension()).end();
    }


    return load(file.read());
}

public static TaskValue<ImageBitmap>.Observable load(File file){
    TaskValue<ImageBitmap> task = new TaskValue<>();
    Application.fileQueue().read(file, FILE_EXTENSION_BMP).observe(new ObserverValueE<ByteBuffer>(myClass()){
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

public static ImageBitmap load(ByteBuffer byteBuffer){
    BitmapFactory.Options opt = new BitmapFactory.Options();
    opt.inJustDecodeBounds = true;
    BitmapFactory.decodeByteArray(byteBuffer.array(), byteBuffer.arrayOffset() + byteBuffer.position(), byteBuffer.arrayOffset() + byteBuffer.limit(), opt);
    return new ImageBitmap(byteBuffer, opt.outWidth, opt.outHeight);
}

public static ImageBitmap random(int width, int height){
    return random(width, height, Bitmap.Config.RGB_565);
}

public static ImageBitmap random(int width, int height, Bitmap.Config format){
    Bitmap bitmap = Bitmap.createBitmap(width, height, format);
    Canvas canvas = new Canvas(bitmap);
    canvas.drawColor(Color.random().getARGB());
    return new ImageBitmap(bitmap);
}

private Bitmap getBitmap(){
    if(bitmap == null){
        return toBitmap();
    } else {
        return bitmap;
    }
}

@Override
public boolean isCompressed(){
    return hasByteBuffer();
}

@Override
public ImageFormat getFormat(){
    return ImageFormat.BITMAP;
}

@Override
public Bitmap toBitmap(){
    if(bitmap == null){
        ByteBuffer buffer = super.toByteBuffer();
        bitmap = BitmapFactory.decodeByteArray(buffer.array(), buffer.position(), buffer.limit());
        setSize(bitmap.getWidth(), bitmap.getHeight());
        setByteBuffer(null);
    }
    return bitmap;
}

@Override
public ImageBitmap toImageBitmap(){
    return this;
}

@Override
public ByteBuffer toByteBuffer(){
    if(hasByteBuffer()){
        return super.toByteBuffer();
    }
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    getBitmap().compress(Bitmap.CompressFormat.PNG, 100, os);
    ByteBuffer buffer = ByteBuffer.wrap(os.toByteArray());
    try{
        os.close();
    } catch(java.lang.Throwable e){
DebugException.start().log(e).end();
    }
    setByteBuffer(buffer);
    bitmap.recycle();
    bitmap = null;
    return buffer.rewind();
}

@Override
public ImageHolder rotate(int angle){
    if(angle == 0){
        return this;
    }
    Matrix matrix = new Matrix();
    matrix.setRotate(angle);
    Bitmap bRotated = Bitmap.createBitmap(getBitmap(), 0, 0, getWidth(), getHeight(), matrix, true);
    bitmap.recycle();
    this.bitmap = bRotated;
    setSize(bitmap.getWidth(), bitmap.getHeight());
    return this;
}

@Override
public TaskState.Observable save(File file){
    return Application.fileQueue().write(file, toByteBuffer(), FILE_EXTENSION_BMP);
}

@Override
public ImageJPEG toImageJpeg(int quality){
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    getBitmap().compress(Bitmap.CompressFormat.JPEG, quality, os);
    ImageJPEG image = new ImageJPEG(ByteBuffer.wrap(os.toByteArray()), getWidth(), getHeight());
    try{
        os.close();
    } catch(java.lang.Throwable e){

DebugException.start().log(e).end();

    }
    return image;
}

}
