/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.cipher.holder;

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.util.UtilsString;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.cipher.dataAdapter.string.DataStringAdapter;
import com.tezov.lib_java.cipher.definition.defDecoder;
import com.tezov.lib_java.cipher.definition.defEncoder;
import com.tezov.lib_java.cipher.key.SecretKey;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.primitive.BytesTo;

import javax.security.auth.DestroyFailedException;

public abstract class CipherHolder{
protected SecretKey keyKey = null;
protected defEncoder<?,?> encoderKey = null;
protected defDecoder<?,?> decoderKey = null;
protected SecretKey keyValue = null;
protected defEncoder<?,?> encoderValue = null;
protected defDecoder<?,?> decoderValue = null;

public CipherHolder(){
DebugTrack.start().create(this).end();
}

protected <K extends SecretKey> K getKeyKey(){
    @SuppressWarnings("unchecked") K r = (K)keyKey;
    return r;
}
public <C extends CipherHolder> C setKeyKey(SecretKey keyKey, DataStringAdapter.Format format){
    this.keyKey = keyKey;
    createEncoderKey(format);
    @SuppressWarnings("unchecked") C r = (C)this;
    return r;
}

protected <K extends SecretKey> K getKeyValue(){
    @SuppressWarnings("unchecked") K r = (K)keyValue;
    return r;
}
public <C extends CipherHolder> C setKeyValue(SecretKey keyValue, DataStringAdapter.Format format){
    this.keyValue = keyValue;
    createEncoderValue(format);
    @SuppressWarnings("unchecked") C r = (C)this;
    return r;
}

protected abstract void createEncoderKey(DataStringAdapter.Format format);
public defEncoder<?,?> getEncoderKey(){
    return encoderKey;
}
public defDecoder<?,?> getDecoderKey(){
    return decoderKey;
}
public <IN, OUT> OUT encodeKey(IN key){
    @SuppressWarnings("unchecked") OUT r = encodeKey(key, (Class<IN>)(key!=null? key.getClass():null));
    return r;
}
public <IN, OUT> OUT encodeKey(IN key, Class<IN> type){
    @SuppressWarnings("unchecked") OUT r = ((defEncoder<IN, OUT>)encoderKey).encode(key, type);
    return r;
}
public <IN, OUT> OUT decodeKey(IN key){
    @SuppressWarnings("unchecked") OUT r = decodeKey(key, (Class<OUT>)(key!=null? key.getClass():null));
    return r;
}
public <IN, OUT> OUT decodeKey(IN key, Class<OUT> type){
    @SuppressWarnings("unchecked") OUT r = ((defDecoder<IN, OUT>)decoderKey).decode(key, type);
    return r;
}

protected abstract void createEncoderValue(DataStringAdapter.Format format);
public defEncoder<?,?> getEncoderValue(){
    return encoderValue;
}
public defDecoder<?,?> getDecoderValue(){
    return decoderValue;
}
public <IN, OUT> OUT encodeValue(IN key){
    @SuppressWarnings("unchecked") OUT r = encodeValue(key, (Class<IN>)(key!=null? key.getClass():null));
    return r;
}
public <IN, OUT> OUT encodeValue(IN key, Class<IN> type){
    @SuppressWarnings("unchecked") OUT r = ((defEncoder<IN, OUT>)encoderValue).encode(key, type);
    return r;
}
public <IN, OUT> OUT decodeValue(IN key){
    @SuppressWarnings("unchecked") OUT r = decodeValue(key, (Class<OUT>)(key!=null? key.getClass():null));
    return r;
}
public <IN, OUT> OUT decodeValue(IN key, Class<OUT> type){
    @SuppressWarnings("unchecked") OUT r = ((defDecoder<IN, OUT>)decoderValue).decode(key, type);
    return r;
}

protected abstract byte[] specToBytes();
final public String specToString(){
    return BytesTo.StringBase64(specToBytes());
}

public void destroy(){
    try{
        encoderKey = null;
        decoderKey = null;
        if(keyKey != null){
            keyKey.destroy();
            keyKey = null;
        }
        encoderValue = null;
        decoderValue = null;
        if(keyValue != null){
            keyValue.destroy();
            keyValue = null;
        }
    } catch(DestroyFailedException e){
DebugException.start().log(e).end();
    }
}
@Override
protected void finalize() throws Throwable{
    destroy();
DebugTrack.start().destroy(this).end();
    super.finalize();
}

}
