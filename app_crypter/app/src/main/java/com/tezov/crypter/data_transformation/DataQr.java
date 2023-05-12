/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.crypter.data_transformation;

import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.debug.DebugLog;
import java.util.Set;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import androidx.fragment.app.Fragment;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.util.UtilsString;
import com.tezov.lib_java.toolbox.Clock;
import java.util.LinkedList;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.async.notifier.observer.state.ObserverStateE;
import com.tezov.lib_java.async.notifier.observer.value.ObserverValueE;
import com.tezov.lib_java.async.notifier.task.TaskState;

import android.graphics.Bitmap;

import com.tezov.lib_java_android.application.AppDisplay;
import com.tezov.lib_java.async.notifier.task.TaskValue;
import com.tezov.lib_java.buffer.Splitter;
import com.tezov.lib_java.file.Directory;
import com.tezov.lib_java_android.file.StorageFile;
import com.tezov.lib_java_android.file.UriW;
import com.tezov.lib_java.file.UtilsStream;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.primitive.BytesTo;
import com.tezov.lib_java.type.primitive.string.StringBase58To;
import com.tezov.lib_java_android.util.UtilsQrCode;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class DataQr{
public final static String EXTENSION_GIF = "gif";
public final static int QR_MAX_SIZE = AppDisplay.convertDpToPx(400);
public final static int SPLIT_LENGTH = 150;
public final static long DELAY_NEXT_ms = 400;
private int index;
private Integer size;
private List<String> datas;
private Bitmap bitmap = null;

public DataQr(){
    this(null, null);
}
public DataQr(String dataBase58){
    this(dataBase58, null);
}
public DataQr(String dataBase58, Integer size){
DebugTrack.start().create(this).end();
    this.size = size;
    setDataBase58(dataBase58);
}

public String getDataBase58(){
    if(datas == null){
        return null;
    } else {
        return BytesTo.StringBase58(Splitter.joinString(datas));
    }
}
public DataQr setDataBase58(String data){
    if(data != null){
        byte[] dataBytes = StringBase58To.Bytes(data);
        datas = Splitter.splitToString(dataBytes, DataQr.SPLIT_LENGTH);
        index = -1;
    }
    return this;
}
public List<String> getDataList(){
    return datas;
}

private int getQrSize(){
    if(size == null){
        size = (int)(AppDisplay.getSize().getWidth() * .9f);
        if(size > DataQr.QR_MAX_SIZE){
            size = DataQr.QR_MAX_SIZE;
        }
    }
    return size;

}
public DataQr clear(){
    index = -1;
    datas = null;
    bitmap = null;
    size = null;
    return this;
}
public void next(){
    bitmap = null;
    index++;
    if(index >= datas.size()){
        index = 0;
    }
}
public void first(){
    bitmap = null;
    index = 0;
}
public int getIndex(){
    return index;
}
public DataQr setIndex(int index){
    this.index = index;
    return this;
}
public int getSize(){
    return datas.size();
}
public DataQr setSize(Integer size){
    bitmap = null;
    this.size = size;
    return this;
}
public Bitmap getBitmap(){
    if(bitmap == null){
        bitmap = UtilsQrCode.toBitmap(datas.get(index), getQrSize());
    }
    return bitmap;
}

public TaskValue<UriW>.Observable toGif(Directory cacheDirectory, String fileName){
    UriW uri = StorageFile.obtainUniqueUri(cacheDirectory, fileName + com.tezov.lib_java.file.File.DOT_SEPARATOR + EXTENSION_GIF);
    return toGif(uri);
}
public TaskValue<UriW>.Observable toGif(UriW uri){
    TaskValue<UriW> task = new TaskValue<>();
    try{
        OutputStream outputStream = uri.getOutputStream();
        toGif(outputStream).observe(new ObserverStateE(this){
            @Override
            public void onComplete(){
                UtilsStream.close(outputStream);
                uri.pending(false);
                task.notifyComplete(uri);
            }
            @Override
            public void onException(Throwable e){
DebugException.start().log(e).end();
                UtilsStream.close(outputStream);
                uri.delete();
                task.notifyException(e);
            }
        });
    } catch(Throwable e){
DebugException.start().log(e).end();
        uri.delete();
        task.notifyException(e);
    }
    return task.getObservable();
}
public TaskState.Observable toGif(OutputStream outputStream){
    TaskState task = new TaskState();
    try{
        GifEncoder encoder = new GifEncoder();
        encoder.setDelay((int)DELAY_NEXT_ms);
        encoder.setRepeat(0);
        encoder.setQuality(20);
        encoder.start(outputStream);
        for(String data: datas){
            Bitmap bitmap = UtilsQrCode.toBitmap(data, QR_MAX_SIZE);
            encoder.addFrame(bitmap);
        }
        encoder.finish();
        task.notifyComplete();
    } catch(Throwable e){
DebugException.start().log(e).end();
        task.notifyException(e);
    }
    return task.getObservable();
}

public TaskValue<String>.Observable fromGif(UriW uri, boolean tryPureBarcode){
    TaskValue<String> task = new TaskValue<>();
    try{
        InputStream inputStream  = uri.getInputStream();
        fromGif(inputStream, tryPureBarcode).observe(new ObserverValueE<String>(this){
            @Override
            public void onComplete(String s){
                UtilsStream.close(inputStream);
                task.notifyComplete(s);
            }
            @Override
            public void onException(String s, Throwable e){
DebugException.start().log(e).end();
                UtilsStream.close(inputStream);
                task.notifyException(e);
            }
        });
    } catch(Throwable e){
DebugException.start().log(e).end();
        task.notifyException(e);
    }
    return task.getObservable();

}
public TaskValue<String>.Observable fromGif(InputStream inputStream, boolean tryPureBarcode){
    TaskValue<String> task = new TaskValue<>();
    try{
        GifDecoder decoder = new GifDecoder();
        decoder.read(inputStream, inputStream.available());
        int frame = decoder.getFrameCount();
        datas = new ArrayList<>();
        for(int i = 0; i < frame; i++){
            decoder.advance();
            String data = UtilsQrCode.fromBitmapToString(decoder.getNextFrame(), tryPureBarcode);
            if(data == null){
                throw new Throwable("fail to retrieve data of frame " + i);
            }
            datas.add(data);
        }
        task.notifyComplete(getDataBase58());
    } catch(Throwable e){
DebugException.start().log(e).end();
        task.notifyException(e);
    }
    return task.getObservable();
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

}
