/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.crypter.data_transformation;

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugException;
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
import static com.tezov.crypter.data_transformation.StreamEncoder.ALTER_LENGTH;

import com.tezov.crypter.application.AppInfo;
import com.tezov.crypter.application.WakeLock;
import com.tezov.crypter.data.item.ItemKey;
import com.tezov.crypter.data.item.ItemKeyRing;
import com.tezov.lib_java.async.notifier.Notifier;
import com.tezov.lib_java.async.notifier.observable.ObservableEventE;
import com.tezov.lib_java.async.notifier.observer.event.ObserverEventE;
import com.tezov.lib_java.buffer.ByteBuffer;
import com.tezov.lib_java.buffer.ByteBufferPacker;
import com.tezov.lib_java.cipher.dataOuput.DecoderBytes;
import com.tezov.lib_java.cipher.dataOuput.MacAuthenticator;
import com.tezov.lib_java.cipher.definition.defAuthenticator;
import com.tezov.lib_java.cipher.definition.defDecoderBytes;
import com.tezov.lib_java.file.UtilsStream;
import com.tezov.lib_java.generator.uid.UUID;
import com.tezov.lib_java.toolbox.Compare;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.primitive.BytesTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.util.UtilsBytes;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;

public class StreamDecoder{
protected ItemKeyMaker itemKeyMaker = null;
protected defAuthenticator authenticator = null;
protected defDecoderBytes decoderHeader = null;
protected defDecoderBytes decoderData = null;
protected UtilsStream.StreamLinker stream = null;
private final Notifier<Step> notifier;

public StreamDecoder(){
DebugTrack.start().create(this).end();
    notifier = new Notifier<>(new ObservableEventE<Step, Integer>(), false);
}
private StreamDecoder me(){
    return this;
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
public StreamDecoder setItemKeyMaker(ItemKeyMaker itemKeyMaker){
    this.itemKeyMaker = itemKeyMaker;
    return this;
}
public ItemKey getItemKey(){
    return itemKeyMaker.itemKey;
}
public ItemKeyRing getItemKeyRing(){
    return itemKeyMaker.itemKeyRing;
}
public void decode(InputStream in, OutputStream out){
    try{
        stream = new UtilsStream.StreamLinkerFileProgress(in, out);
        ByteBuffer bufferHeader = retrieveHeader();
        identifyEncryption(bufferHeader);
        buildItemKey(bufferHeader);
        bufferHeader = createDecoder(bufferHeader);
        retrieveExtraData(bufferHeader);
        stream.setOut(retrieveOutputStream());
        post(Step.START, (Integer)null);
        WakeLock.acquire(in.available(), WakeLock.Type.DECRYPT);
        decodeFile();
        WakeLock.release();
        stream = UtilsStream.close(stream);
        authenticator = null;
        decoderHeader = null;
        decoderData = null;
        onDone(null);
        onFinalise(null);
    } catch(Throwable e){
        WakeLock.release();
        stream = UtilsStream.close(stream);
        authenticator = null;
        decoderHeader = null;
        decoderData = null;
        onDone(e);
        onFinalise(e);
    }
}
protected OutputStream retrieveOutputStream() throws FileNotFoundException{
    return stream.getOut();
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

private ByteBuffer retrieveHeader() throws Throwable{
    //READ HEADER LENGTH
    byte headerLengthPackedByte = (byte)stream.read();
    if(headerLengthPackedByte > IntTo.BYTES){
        throw new Throwable("failed to read headerLengthPackedByte");
    }
    byte[] headerLengthPacked_1 = UtilsBytes.obtain(headerLengthPackedByte);
    byte[] headerLengthPacked_2 = UtilsBytes.obtain(headerLengthPackedByte);
    stream.read(headerLengthPacked_1);
    stream.read(headerLengthPacked_2);
    headerLengthPacked_2 = UtilsBytes.reverse(BytesTo.complement(headerLengthPacked_2));
    if(!Compare.equals(headerLengthPacked_1, headerLengthPacked_2)){
        throw new Throwable("failed to read headerLengthPacked");
    }
    int headerAlteredLength = BytesTo.Int(ByteBufferPacker.unpackData(headerLengthPacked_1));
    if(headerAlteredLength < 0){
        throw new Throwable("incorrect headerLength length");
    }
    //READ HEADER DATA
    byte[] headerAltered = UtilsBytes.obtain(headerAlteredLength);
    int readLength = stream.read(headerAltered);
    if(readLength != headerAlteredLength){
        throw new Throwable("failed to retrieve header");
    }
    ByteBuffer bufferHeaderAltered = ByteBuffer.wrap(headerAltered);
    byte[] alter = bufferHeaderAltered.getBytes();
    if(alter.length != ALTER_LENGTH){
        throw new Throwable("wrong alter length");
    }
    byte[] header = UtilsBytes.xor(bufferHeaderAltered.getBytes(), alter);
    return ByteBuffer.wrapPacked(header);
}
protected boolean acceptVersion(int version){
    return version <= FILE_ENCRYPTED_VERSION;
}
protected boolean acceptCipherCode(Integer code){
    return Compare.equals(StreamCipherCode.DEFAULT, code);
}
protected void identifyEncryption(ByteBuffer bufferHeader) throws Throwable{
    itemKeyMaker.version = bufferHeader.getInt();
    if(!acceptVersion(itemKeyMaker.version)){
        throw new Throwable("version denied");
    }
    itemKeyMaker.cipherCode = bufferHeader.getInt();
    if(!acceptCipherCode(itemKeyMaker.cipherCode)){
        throw new Throwable("cipher code denied");
    }
}
protected void buildItemKey(ByteBuffer bufferHeader) throws Throwable{
    itemKeyMaker.guidApp = UUID.fromBytes(bufferHeader.getBytes());
    itemKeyMaker.uidKey = UUID.fromBytes(bufferHeader.getBytes());
    itemKeyMaker.aliasEncoded = bufferHeader.getBytes();
    itemKeyMaker.guidKey = UUID.fromBytes(bufferHeader.getBytes());
    itemKeyMaker.specKey = bufferHeader.getBytes();
    itemKeyMaker.build();
}
protected ByteBuffer createDecoder(ByteBuffer bufferHeader) throws Throwable{
    //AUTH SPEC KEY
    byte[] specMac = bufferHeader.getBytes();
    byte[] headerDataEncryptedSigned = bufferHeader.getBytes();
    itemKeyMaker.itemKeyRing.rebuildKeyMac(specMac);
    authenticator = new MacAuthenticator(itemKeyMaker.itemKeyRing.getKeyMac());
    byte[] headerDataEncryptedAuth = authenticator.auth(headerDataEncryptedSigned);
    if(headerDataEncryptedAuth == null){
        throw new Throwable("failed to authenticate header");
    }
    //DECRYPT SPEC KEY
    ByteBuffer bufferHeaderDataEncrypted = ByteBuffer.wrapPacked(headerDataEncryptedAuth);
    byte[] specHeader = bufferHeaderDataEncrypted.getBytes();
    byte[] headerDataEncrypted = bufferHeaderDataEncrypted.getBytes();
    byte[] trialData = bufferHeaderDataEncrypted.getBytes();
    itemKeyMaker.itemKeyRing.rebuildKeyHeader(specHeader);
    decoderHeader = DecoderBytes.newDecoder(itemKeyMaker.itemKeyRing.getKeyHeader());
    byte[] headerDataDecrypted = decoderHeader.decode(headerDataEncrypted);
    // READ SPEC KEY
    ByteBuffer bufferHeaderData = ByteBuffer.wrapPacked(headerDataDecrypted);
    if(!Compare.equals(BytesTo.complement(trialData), bufferHeaderData.getBytes())){
        throw new Throwable("failed trial");
    }
    byte[] specData = bufferHeaderData.getBytes();
    itemKeyMaker.itemKeyRing.rebuildKeyData(specData);
    decoderData = DecoderBytes.newDecoder(itemKeyMaker.itemKeyRing.getKeyData());
    return bufferHeaderData;
}
protected void retrieveExtraData(ByteBuffer bufferHeaderData) throws Throwable{

}
protected void decodeFile() throws Throwable{
    OutputStream out = new UtilsStream.OutputStreamProgressCheckCrc(stream.getOut()){
        @Override
        public void onProgress(int current, int max){
            int value = (int)(((float)current / (float)max) * 100f);
            me().onProgress(value);
        }
    };
    stream.setOut(out);
    if(authenticator != null){
        stream.setIn(authenticator.auth(stream.getIn()).init());
    }
    if(!decoderData.decode(stream)){
        throw new Throwable("failed to decode");
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

public abstract static class ItemKeyMaker{
    protected Integer version;
    protected Integer cipherCode;
    protected UUID guidApp;
    protected UUID guidKey;
    protected UUID uidKey;
    protected byte[] aliasEncoded;
    protected byte[] specKey;
    protected ItemKey itemKey = null;
    protected ItemKeyRing itemKeyRing = null;
    public ItemKeyMaker(){
DebugTrack.start().create(this).end();
        clear();
    }
    public Integer getVersion(){
        return version;
    }
    public Integer getCipherCode(){
        return cipherCode;
    }
    public UUID getGuidApp(){
        return guidApp;
    }
    public String getSignatureApp(){
        if(guidApp == null){
            return null;
        } else {
            return AppInfo.toSignature(guidApp);
        }
    }
    public UUID getGuidKey(){
        return guidKey;
    }
    public UUID getUidKey(){
        return uidKey;
    }
    public byte[] getAliasEncoded(){
        return aliasEncoded;
    }
    public byte[] getSpecKey(){
        return specKey;
    }
    protected void setItemKey(ItemKey itemKey, ItemKeyRing itemKeyRing){
        this.itemKey = itemKey;
        this.itemKeyRing = itemKeyRing;
    }
    public ItemKey getItemKey(){
        return itemKey;
    }
    public ItemKeyMaker setItemKey(ItemKey itemKey){
        this.itemKey = itemKey;
        return this;
    }
    public ItemKeyRing getItemKeyRing(){
        return itemKeyRing;
    }
    public ItemKeyMaker setItemKeyRing(ItemKeyRing itemKeyRing){
        this.itemKeyRing = itemKeyRing;
        return this;
    }
    protected abstract void rebuildKey() throws Throwable;
    protected void build() throws Throwable{
        try{
            rebuildKey();
        } catch(Throwable e){
            clear();
            throw e;
        }
    }
    private void clear(){
        version = null;
        cipherCode = null;
        guidApp = null;
        guidKey = null;
        uidKey = null;
        aliasEncoded = null;
        specKey = null;
    }
    @Override
    protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
        super.finalize();
    }

}

}
