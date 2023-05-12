/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.crypter.data_transformation;

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
import com.tezov.crypter.application.Environment;
import com.tezov.crypter.data.misc.ClockFormat;
import com.tezov.lib_java.buffer.ByteBuffer;
import com.tezov.lib_java_android.file.UriW;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.type.primitive.BytesTo;

import java.io.FileNotFoundException;
import java.io.OutputStream;

public class FileDecoder extends StreamDecoder{
private UriW uriIn = null;
private Environment.MediaPath directoryOut = null;
private UriW uriOut = null;
private String fileFullNameOut = null;
private Long timestamp = null;

public FileDecoder(){

}
public UriW getUriOut(){
    return uriOut;
}
public FileDecoder setUriOut(UriW out){
    this.uriOut = out;
    return this;
}
public void decode(UriW uriIn, Environment.MediaPath directoryOut){
    decode(uriIn, directoryOut, null);
}
public void decode(UriW uriIn, UriW uriOut){
    setUriOut(uriOut);
    decode(uriIn, null, null);
}
public void decode(UriW uriIn, Environment.MediaPath directoryOut, String fileFullNameOut){
    try{
        this.directoryOut = directoryOut;
        this.fileFullNameOut = fileFullNameOut;
        this.uriIn = uriIn;
        decode(uriIn.getInputStream(), null);
    } catch(Throwable e){
DebugException.start().log(e).end();
        onDone(e);
        onFinalise(e);
    }
}

public String getFileFullNameOut(){
    return fileFullNameOut;
}
public Long getTimestamp(){
    return timestamp;
}
public String getEncryptedDateString(){
    return ClockFormat.longToDateTime_FULL(timestamp);
}

@Override
protected void onFinalise(Throwable e){
    if(e != null){
        if(uriOut != null){
            uriOut.delete();
            uriOut = null;
        }
    } else {
        uriOut.pending(false);
        if(itemKeyMaker.itemKey.mustDecryptDeleteEncryptedFile()){
            uriIn.delete();
        }
    }
    uriIn = null;
    super.onFinalise(e);
}
@Override
protected void retrieveExtraData(ByteBuffer bufferHeader) throws Throwable{
    ByteBuffer buffer = ByteBuffer.wrapPacked(bufferHeader.getBytes());
    String fileFullNameDecoded = BytesTo.StringChar(buffer.getBytes());
    if(fileFullNameOut == null){
        fileFullNameOut = fileFullNameDecoded;
    }
    if(uriOut == null){
        uriOut = obtainPendingUri(fileFullNameOut);
    }
    timestamp = buffer.getLong();
}
@Override
protected OutputStream retrieveOutputStream() throws FileNotFoundException{
    return uriOut.getOutputStream();
}
private UriW obtainPendingUri(String fileFullName){
    UriW uriOut;
    if(itemKeyMaker.itemKey.mustDecryptOverwriteFile()){
        uriOut = Environment.obtainClosestPendingUri(directoryOut, fileFullName);
    } else {
        uriOut = Environment.obtainUniquePendingUri(directoryOut, fileFullName);
    }
    return uriOut;
}

}
