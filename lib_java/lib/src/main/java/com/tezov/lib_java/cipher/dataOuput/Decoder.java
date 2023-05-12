/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.cipher.dataOuput;

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.util.UtilsString;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java.cipher.definition.defDataAdapterDecoder;
import com.tezov.lib_java.cipher.definition.defDecoder;
import com.tezov.lib_java.cipher.definition.defCipherKey;
import com.tezov.lib_java.cipher.definition.defDecoderBytes;
import com.tezov.lib_java.cipher.key.KeyXor;

public class Decoder<IN, OUT> extends  DecoderBytes implements defDecoder<IN, OUT>{
private final defDataAdapterDecoder<IN, OUT> dataAdapter;

private Decoder(defDecoderBytes decoder, defDataAdapterDecoder<IN, OUT> dataAdapter){
    super(decoder);
    this.dataAdapter = dataAdapter;
}

public static <IN, OUT> Decoder<IN, OUT> newDecoder(defCipherKey key, defDataAdapterDecoder<IN, OUT> dataAdapter){
    DecoderBytesCipher decoder = new DecoderBytesCipher(key);
    return new Decoder<>(decoder, dataAdapter);
}
public static <IN, OUT> Decoder<IN, OUT> newDecoder(KeyXor key, defDataAdapterDecoder<IN, OUT> dataAdapter){
    DecoderBytesXor decoder = new DecoderBytesXor(key);
    return new Decoder<>(decoder, dataAdapter);
}

@Override
public defDataAdapterDecoder<IN, OUT> getDataAdapter(){
    return dataAdapter;
}
@Override
public OUT decode(IN data){
    return decode(data, (Class<OUT>)(data!=null?data.getClass():null));
}
@Override
public OUT decode(IN data, Class<OUT> type){
    return dataAdapter.toOut(decode(dataAdapter.fromIn(data)), type);
}

}
