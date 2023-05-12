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
import com.tezov.lib_java.util.UtilsString;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import androidx.fragment.app.Fragment;
import static com.tezov.crypter.application.AppConfig.FILE_ENCRYPTED_VERSION;
import static com.tezov.crypter.data_transformation.StreamCipherCode.TEXT_NOT_SIGNED;

import com.tezov.crypter.application.AppInfo;
import com.tezov.crypter.data.misc.ClockFormat;
import com.tezov.lib_java.buffer.ByteBuffer;
import com.tezov.lib_java.cipher.dataInput.EncoderBytes;
import com.tezov.lib_java.file.UtilsStream;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.type.primitive.BytesTo;
import com.tezov.lib_java.type.primitive.LongTo;
import com.tezov.lib_java.type.primitive.string.StringCharTo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class StringEncoder extends StreamEncoder{
private ByteArrayInputStream in = null;
private ByteArrayOutputStream out = null;
private Long timestamp = null;

public StringEncoder(){

}
public ByteArrayOutputStream getOut(){
    return out;
}
public StringEncoder setOut(ByteArrayOutputStream out){
    this.out = out;
    return this;
}
public String getOutString(){
    return BytesTo.StringBase58(out.toByteArray());
}

public Long getTimestamp(){
    return timestamp;
}
public String getEncryptedDateString(){
    return ClockFormat.longToDateTime_FULL(timestamp);
}

public void encode(String data){
    try{
        timestamp = Clock.MilliSecond.now();
        this.in = new ByteArrayInputStream(StringCharTo.Bytes(data));
        if(out == null){
            out = new ByteArrayOutputStream();
        }
        encode(in, out);
    } catch(Throwable e){
DebugException.start().log(e).end();
        onFinalise(e);
        onDone(e);
    }
}
@Override
protected void onFinalise(Throwable e){
    in = null;
    super.onFinalise(e);
}

@Override
protected byte[] getExtraData(){
    return LongTo.Bytes(timestamp);
}

@Override
protected void createEncoder() throws Throwable{
    if(itemKey.mustEncryptSigneText()){
        super.createEncoder();
    } else {
        signer = null;
        encoderHeader = null;
        encoderData = EncoderBytes.newEncoder(itemKeyRing.getKeyKey());
    }
}
@Override
protected byte[] createHeaderData() throws Throwable{
    if(itemKey.mustEncryptSigneText()){
        return super.createHeaderData();
    } else {
        byte[] specKey;
        if(itemKey.mustEncryptStrictMode()){
            specKey = null;
        } else {
            specKey = itemKeyRing.getKeyKey().specToBytes();
        }
        ByteBufferBuilder buffer = ByteBufferBuilder.obtain();
        buffer.put(FILE_ENCRYPTED_VERSION);
        buffer.put(TEXT_NOT_SIGNED);
        buffer.put(AppInfo.getGUID().toBytes());
        buffer.put(itemKey.getGuid().toBytes());
        buffer.put(specKey);
        buffer.put(timestamp);
        return buffer.arrayPacked();
    }
}
@Override
protected void encodeFile() throws Throwable{
    InputStream in = new UtilsStream.InputStreamAppendCrc(stream.getIn());
    stream.setIn(in);
    if(signer != null){
        stream.setOut(signer.sign(stream.getOut()).init());
    }
    if(!encoderData.encode(stream)){
        throw new Throwable("failed to encode");
    }
}

}
