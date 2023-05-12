/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.cipher.dataInput;

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
import com.tezov.lib_java.cipher.definition.defCipherKey;
import com.tezov.lib_java.cipher.definition.defDataAdapterEncoder;
import com.tezov.lib_java.cipher.key.KeyXor;
import com.tezov.lib_java.cipher.definition.defEncoder;
import com.tezov.lib_java.cipher.definition.defEncoderBytes;

import static com.tezov.lib_java.file.UtilsStream.close;

public class Encoder<IN, OUT> extends EncoderBytes implements defEncoder<IN, OUT>{
private final defDataAdapterEncoder<IN, OUT> dataAdapter;

private Encoder(defEncoderBytes encoder, defDataAdapterEncoder<IN, OUT> dataAdapter){
    super(encoder);
    this.dataAdapter = dataAdapter;
}

public static <IN, OUT> Encoder<IN, OUT> newEncoder(defCipherKey key, defDataAdapterEncoder<IN, OUT> dataAdapter){
    EncoderBytesCipher encoder = new EncoderBytesCipher(key);
    return new Encoder<>(encoder, dataAdapter);
}
public static <IN, OUT> Encoder<IN, OUT> newEncoder(KeyXor key, defDataAdapterEncoder<IN, OUT> dataAdapter){
    EncoderBytesXor encoder = new EncoderBytesXor(key);
    return new Encoder<>(encoder, dataAdapter);
}

@Override
public defDataAdapterEncoder<IN, OUT> getDataAdapter(){
    return dataAdapter;
}

@Override
public OUT encode(IN data){
    return encode(data, (Class<IN>)(data!=null?data.getClass():null));
}
@Override
public OUT encode(IN data, Class<IN> type){
    return dataAdapter.toOut(encode(dataAdapter.fromIn(data, type)));
}

@Override
public OUT encode(IN data, byte[] iv){
    return encode(data, (Class<IN>)(data!=null?data.getClass():null), iv);
}
@Override
public OUT encode(IN data, Class<IN> type, byte[] iv){
    return dataAdapter.toOut(encode(dataAdapter.fromIn(data, type), iv));
}

}
