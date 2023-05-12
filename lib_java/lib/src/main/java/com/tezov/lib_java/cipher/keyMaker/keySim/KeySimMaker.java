/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.cipher.keyMaker.keySim;

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
import com.tezov.lib_java.cipher.definition.defDecoderBytes;
import com.tezov.lib_java.cipher.definition.defEncoderBytes;
import com.tezov.lib_java.cipher.misc.PasswordCipher;
import com.tezov.lib_java.cipher.dataInput.Encoder;
import com.tezov.lib_java.cipher.dataOuput.Decoder;
import com.tezov.lib_java.cipher.definition.defDataAdapterDecoder;
import com.tezov.lib_java.cipher.definition.defDataAdapterEncoder;
import com.tezov.lib_java.cipher.definition.defDecoder;
import com.tezov.lib_java.cipher.definition.defEncoder;
import com.tezov.lib_java.cipher.key.KeySim;

public abstract class KeySimMaker<IN, OUT> extends KeySimMakerBytes{
public KeySimMaker(PasswordCipher password){
    super(password);
}

@Override
public KeySimMaker<IN, OUT> build(){
    return (KeySimMaker<IN, OUT>)super.build();
}
@Override
public KeySimMaker<IN, OUT> reBuild(byte[] spec){
    return (KeySimMaker<IN, OUT>)super.reBuild(spec);
}

protected abstract defDataAdapterEncoder<IN, OUT> newAdapterEncoder();
protected abstract defDataAdapterDecoder<IN, OUT> newAdapterDecoder();

@Override
public KeySimMaker<IN, OUT> setLength(KeySim.Length length){
    return (KeySimMaker<IN, OUT>)super.setLength(length);
}
@Override
public KeySimMaker<IN, OUT> setTransformation(KeySim.Transformation transformation){
    return (KeySimMaker<IN, OUT>)super.setTransformation(transformation);
}

@Override
protected defEncoderBytes newEncoder(KeySim key){
    return Encoder.newEncoder(key, newAdapterEncoder());
}
@Override
protected defDecoderBytes newDecoder(KeySim key){
    return Decoder.newDecoder(key, newAdapterDecoder());
}

@Override
public defEncoder<IN, OUT> getEncoder(){
    return (defEncoder<IN, OUT>)super.getEncoder();
}
@Override
public defDecoder<IN, OUT> getDecoder(){
    return (defDecoder<IN, OUT>)super.getDecoder();
}

}
