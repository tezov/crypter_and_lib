/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.crypter.data_transformation;

import com.tezov.lib_java.buffer.ByteBufferBuilder;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import androidx.fragment.app.Fragment;
import static com.tezov.crypter.application.AppConfig.FILE_ENCRYPTED_NAME_LENGTH;
import static com.tezov.lib_java.toolbox.Clock.FormatDateAndTime.FULL_FILE_NAME;

import com.tezov.crypter.application.Environment;
import com.tezov.crypter.data.misc.ClockFormat;
import com.tezov.lib_java.buffer.ByteBuffer;
import com.tezov.lib_java.file.File;
import com.tezov.lib_java_android.file.UriW;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.type.primitive.string.StringCharTo;
import com.tezov.lib_java.util.UtilsString;

public class FileEncoder extends StreamEncoder{
private UriW uriIn = null;
private UriW uriOut = null;
private Long timestamp = null;

public FileEncoder(){

}
public UriW getUriOut(){
    return uriOut;
}
public FileEncoder setUriOut(UriW out){
    this.uriOut = out;
    return this;
}
public Long getTimestamp(){
    return timestamp;
}
public String getEncryptedDateString(){
    return ClockFormat.longToDateTime_FULL(timestamp);
}

public void encode(UriW uriIn, UriW uriOut){
    setUriOut(uriOut);
    encode(uriIn, null, null);

}
public void encode(UriW uriIn, Environment.MediaPath directoryOut, String extensionOut){
    try{
        timestamp = Clock.MilliSecond.now();
        this.uriIn = uriIn;
        if(uriOut == null){
            uriOut = obtainPendingUri(directoryOut, uriIn, extensionOut);
        }
        encode(uriIn.getInputStream(), uriOut.getOutputStream());
    } catch(Throwable e){
DebugException.start().log(e).end();
        onDone(e);
        onFinalise(e);
    }
}

@Override
protected void onDone(Throwable e){
    if(e != null){
        if(uriOut != null){
            uriOut.delete();
            uriOut = null;
        }
    }
    super.onDone(e);
}
@Override
protected void onFinalise(Throwable e){
    if(e == null){
        uriOut.pending(false);
        if(itemKey.mustEncryptDeleteOriginalFile()){
            this.uriIn.delete();
        }
    }
    uriIn = null;
    super.onFinalise(e);
}
@Override
protected byte[] getExtraData(){
    ByteBufferBuilder buffer = ByteBufferBuilder.obtain();
    buffer.put(uriIn.getFullName());
    buffer.put(timestamp);
    return buffer.arrayPacked();
}
private UriW obtainPendingUri(Environment.MediaPath directoryOut, UriW uriIn, String extension){
    String fileFullName = uriIn.getFullName();
    String fileEncryptedFullName;
    if(itemKey.mustEncryptFileName()){
        fileEncryptedFullName = UtilsString.randomBase49(FILE_ENCRYPTED_NAME_LENGTH);
    } else {
        fileEncryptedFullName = fileFullName;
    }
    if(itemKey.mustEncryptAddTimeAndTimeToFileName()){
        fileEncryptedFullName += "_" + Clock.DateAndTimeTo.string(Clock.DateAndTime.now(), FULL_FILE_NAME);
    }
    if(extension != null){
        fileEncryptedFullName += File.DOT_SEPARATOR + extension;
    }
    UriW uriOut;
    if(!itemKey.mustEncryptFileName() && itemKey.mustEncryptOverwriteFile()){
        uriOut = Environment.obtainClosestPendingUri(directoryOut, fileEncryptedFullName);
    } else {
        uriOut = Environment.obtainUniquePendingUri(directoryOut, fileEncryptedFullName);
    }
    return uriOut;
}

}
