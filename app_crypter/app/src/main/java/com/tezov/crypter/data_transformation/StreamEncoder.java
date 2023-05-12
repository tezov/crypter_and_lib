/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.crypter.data_transformation;

import com.tezov.lib_java.async.notifier.observer.event.ObserverEvent;
import com.tezov.lib_java.buffer.ByteBufferBuilder;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.type.primitive.ObjectTo;
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
import static com.tezov.crypter.application.AppConfig.FILE_ENCRYPTED_VERSION;

import com.tezov.crypter.application.AppInfo;
import com.tezov.crypter.application.WakeLock;
import com.tezov.crypter.data.item.ItemKey;
import com.tezov.crypter.data.item.ItemKeyRing;
import com.tezov.lib_java.async.notifier.Notifier;
import com.tezov.lib_java.async.notifier.observable.ObservableEventE;
import com.tezov.lib_java.async.notifier.observer.event.ObserverEventE;
import com.tezov.lib_java.buffer.ByteBuffer;
import com.tezov.lib_java.buffer.ByteBufferPacker;
import com.tezov.lib_java.cipher.dataInput.EncoderBytes;
import com.tezov.lib_java.cipher.dataInput.MacSigner;
import com.tezov.lib_java.cipher.definition.defEncoderBytes;
import com.tezov.lib_java.cipher.definition.defSigner;
import com.tezov.lib_java.file.UtilsStream;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.primitive.BytesTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.util.UtilsBytes;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StreamEncoder{
public final static int ALTER_LENGTH = 4;
protected final static int TRIAL_DATA_LENGTH = 16;
protected ItemKey itemKey = null;
protected ItemKeyRing itemKeyRing = null;
protected defSigner signer = null;
protected defEncoderBytes encoderHeader = null;
protected defEncoderBytes encoderData = null;
protected UtilsStream.StreamLinker stream = null;
private final Notifier<Step> notifier;

public StreamEncoder(){
DebugTrack.start().create(this).end();
    notifier = new Notifier<>(new ObservableEventE<Step, Integer>(), false);
}
private StreamEncoder me(){
    return this;
}

public Notifier.Subscription observe(ObserverEvent<Step, Integer> observer){
    return notifier.register(observer);
}
public Notifier.Subscription observe(ObserverEventE<Step, Integer> observer){
    return notifier.register(observer);
}
public void unObserve(Object owner){
    notifier.unregister(owner);
}
public void unObserveAll(){
    notifier.unregisterAll();
}
private void post(Step event, Integer value){
    ObservableEventE<Step, Integer>.Access access = notifier.obtainAccess(this, event);
    access.setValue(value);
}
private void post(Step event, Throwable e){
    ObservableEventE<Step, Integer>.Access access = notifier.obtainAccess(this, event);
    access.setException(e);
}

public StreamEncoder setItemKey(ItemKey itemKey, ItemKeyRing itemKeyRing){
    this.itemKey = itemKey;
    this.itemKeyRing = itemKeyRing;
    return this;
}
public ItemKey getItemKey(){
    return itemKey;
}
public ItemKeyRing getItemKeyRing(){
    return itemKeyRing;
}

public void encode(InputStream in, OutputStream out){
    try{
        stream = new UtilsStream.StreamLinkerFileProgress(in, out);
        createEncoder();
        encoderHeader();
        onStart();
        WakeLock.acquire(in.available(), WakeLock.Type.ENCRYPT);
        encodeFile();
        WakeLock.release();
        stream = UtilsStream.close(stream);
        signer = null;
        encoderHeader = null;
        encoderData = null;
        onDone(null);
        onFinalise(null);
    } catch(Throwable e){
DebugException.start().log(e).end();
        WakeLock.release();
        stream = UtilsStream.close(stream);
        signer = null;
        encoderHeader = null;
        encoderData = null;
        onDone(e);
        onFinalise(e);
    }
}
protected void onStart(){
    post(Step.START, (Integer)null);
}
protected void onProgress(int value){
    post(Step.PROGRESS, value);
}
protected void onDone(Throwable e){
    post(Step.DONE, e);
}
protected void onFinalise(Throwable e){
    post(Step.FINALISE, e);
}
protected byte[] getExtraData(){
    return null;
}
protected Integer getCipherCode(){
    return StreamCipherCode.DEFAULT;
}

protected void createEncoder() throws Throwable{
    signer = new MacSigner(itemKeyRing.getKeyMac());
    encoderHeader = EncoderBytes.newEncoder(itemKeyRing.getKeyHeader());
    encoderData = EncoderBytes.newEncoder(itemKeyRing.getKeyData());
}
protected byte[] createHeaderData() throws Throwable{
    byte[] trialData = UtilsBytes.random(TRIAL_DATA_LENGTH);
    //PACK SPEC KEY
    ByteBufferBuilder bufferHeaderDataEncrypted = ByteBufferBuilder.obtain();
    bufferHeaderDataEncrypted.put(BytesTo.complement(trialData.clone()));
    bufferHeaderDataEncrypted.put(itemKeyRing.getKeyData().specToBytes());
    bufferHeaderDataEncrypted.put(getExtraData());
    byte[] headerDataEncrypted = encoderHeader.encode(bufferHeaderDataEncrypted.arrayPacked());
    //SIGN SPEC KEY
    ByteBufferBuilder bufferHeaderEncryptedSignedData = ByteBufferBuilder.obtain();
    bufferHeaderEncryptedSignedData.put(itemKeyRing.getKeyHeader().specToBytes());
    bufferHeaderEncryptedSignedData.put(headerDataEncrypted);
    bufferHeaderEncryptedSignedData.put(trialData);
    byte[] headerDataEncryptedSigned = signer.sign(bufferHeaderEncryptedSignedData.arrayPacked());
    //PACK HEADER DATA
    byte[] uidKeyBytes;
    byte[] aliasEncoded;
    if(itemKey.getUid() != null){
        uidKeyBytes = itemKey.getUid().toBytes();
        aliasEncoded = itemKey.getAliasEncoded();
    } else {
        uidKeyBytes = null;
        aliasEncoded = null;
    }
    byte[] specKey;
    if(itemKey.mustEncryptStrictMode()){
        specKey = null;
    }
    else {
        specKey = itemKeyRing.getKeyKey().specToBytes();
    }
    ByteBufferBuilder bufferHeader = ByteBufferBuilder.obtain();
    bufferHeader.put(FILE_ENCRYPTED_VERSION);
    bufferHeader.put(getCipherCode());
    bufferHeader.put(AppInfo.getGUID().toBytes());
    bufferHeader.put(uidKeyBytes);
    bufferHeader.put(aliasEncoded);
    bufferHeader.put(itemKey.getGuid().toBytes());
    bufferHeader.put(specKey);
    bufferHeader.put(itemKeyRing.getKeyMac().specToBytes());
    bufferHeader.put(headerDataEncryptedSigned);
    return bufferHeader.arrayPacked();

}
private void encoderHeader() throws Throwable{
    byte[] alter = UtilsBytes.random(ALTER_LENGTH);
    if(alter == null){
        throw new Throwable("alter is null");
    }
    ByteBufferBuilder bufferHeaderAltered = ByteBufferBuilder.obtain();
    bufferHeaderAltered.put(alter);
    bufferHeaderAltered.put(UtilsBytes.xor(createHeaderData(), alter));
    byte[] headerAltered = bufferHeaderAltered.array();
    byte[] headerAlteredLengthPacked = ByteBufferPacker.packData(IntTo.Bytes(headerAltered.length));
    stream.write(headerAlteredLengthPacked.length & 0xFF);
    stream.write(headerAlteredLengthPacked);
    stream.write(BytesTo.complement(UtilsBytes.reverse(headerAlteredLengthPacked)));
    stream.write(headerAltered);
}
protected void encodeFile() throws Throwable{
    InputStream in = new UtilsStream.InputStreamProgressAppendCrc(stream.getIn()){
        @Override
        public void onProgress(int current, int max){
            int value = (int)(((float)current / (float)max) * 100f);
            me().onProgress(value);
        }
        @Override
        public int read(byte[] b, int off, int len) throws IOException{
            return super.read(b, off, len);
        }
    };
    stream.setIn(in);
    if(signer != null){
        stream.setOut(signer.sign(stream.getOut()).init());
    }
    if(!encoderData.encode(stream)){
        throw new Throwable("failed to encode");
    }
}

public void abort(){
    stream = UtilsStream.close(stream);
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

public enum Step{
    START, PROGRESS, DONE, FINALISE
}

}
