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
import com.tezov.crypter.data.misc.ClockFormat;
import com.tezov.lib_java.buffer.ByteBuffer;
import com.tezov.lib_java.cipher.dataOuput.DecoderBytes;
import com.tezov.lib_java.file.UtilsStream;
import com.tezov.lib_java.generator.uid.UUID;
import com.tezov.lib_java.toolbox.Compare;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.type.primitive.BytesTo;
import com.tezov.lib_java.type.primitive.string.StringBase58To;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;


public class StringDecoder extends StreamDecoder{
private ByteArrayInputStream in = null;
private ByteArrayOutputStream out = null;
private Long timestamp = null;

public StringDecoder(){

}
public ByteArrayOutputStream getOut(){
    return out;
}
public StringDecoder setOut(ByteArrayOutputStream out){
    this.out = out;
    return this;
}
public String getOutString(){
    return BytesTo.StringChar(out.toByteArray());
}
public Long getTimestamp(){
    return timestamp;
}
public String getEncryptedDateString(){
    return ClockFormat.longToDateTime_FULL(timestamp);
}
public void decode(String data){
    try{
        this.in = new ByteArrayInputStream(StringBase58To.Bytes(data));
        if(out == null){
            out = new ByteArrayOutputStream();
        }
        decode(in, out);
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
protected boolean acceptCipherCode(Integer code){
    return super.acceptCipherCode(code) || Compare.equals(code, StreamCipherCode.TEXT_NOT_SIGNED);
}
@Override
protected void buildItemKey(ByteBuffer bufferHeader) throws Throwable{
    if(Compare.equals(itemKeyMaker.cipherCode, StreamCipherCode.DEFAULT)){
        super.buildItemKey(bufferHeader);
    } else {
        itemKeyMaker.guidApp = UUID.fromBytes(bufferHeader.getBytes());
        itemKeyMaker.guidKey = UUID.fromBytes(bufferHeader.getBytes());
        itemKeyMaker.specKey = bufferHeader.getBytes();
        timestamp = bufferHeader.getLong();
        itemKeyMaker.build();
    }
}
@Override
protected ByteBuffer createDecoder(ByteBuffer bufferHeader) throws Throwable{
    if(Compare.equals(itemKeyMaker.cipherCode, StreamCipherCode.DEFAULT)){
        return super.createDecoder(bufferHeader);
    } else {
        authenticator = null;
        decoderHeader = null;
        decoderData = DecoderBytes.newDecoder(itemKeyMaker.itemKeyRing.getKeyKey());
        return bufferHeader;
    }
}
@Override
protected void retrieveExtraData(ByteBuffer bufferHeaderData) throws Throwable{
    if(Compare.equals(itemKeyMaker.cipherCode, StreamCipherCode.DEFAULT)){
        byte[] bytes = bufferHeaderData.getBytes();
        timestamp = BytesTo.Long(bytes);
    }
}

@Override
protected void decodeFile() throws Throwable{
    OutputStream out = new UtilsStream.OutputStreamCheckCrc(stream.getOut());
    stream.setOut(out);
    if(authenticator != null){
        stream.setIn(authenticator.auth(stream.getIn()).init());
    }
    if(!decoderData.decode(stream)){
        throw new Throwable("failed to decode");
    }
}

}
