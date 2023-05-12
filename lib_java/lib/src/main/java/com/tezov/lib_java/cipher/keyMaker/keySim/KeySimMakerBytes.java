/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.cipher.keyMaker.keySim;

import com.tezov.lib_java.debug.DebugLog;
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
import com.tezov.lib_java.cipher.dataInput.EncoderBytesCipher;
import com.tezov.lib_java.cipher.dataOuput.DecoderBytesCipher;
import com.tezov.lib_java.cipher.definition.defDecoderBytes;
import com.tezov.lib_java.cipher.definition.defEncoderBytes;
import com.tezov.lib_java.cipher.key.KeySim;
import com.tezov.lib_java.cipher.misc.PasswordCipher;
import com.tezov.lib_java.debug.DebugTrack;

public class KeySimMakerBytes{
private PasswordCipher password;
private KeySim.Length length = KeySim.DEFAULT_LENGTH;
private KeySim.Transformation transformation = KeySim.DEFAULT_TRANSFORMATION;
private KeySim key = null;
private defEncoderBytes encoder = null;
private defDecoderBytes decoder = null;
public KeySimMakerBytes(PasswordCipher password){
DebugTrack.start().create(this).end();
    this.password = password;
}
public KeySimMakerBytes build(){
    key = new KeySim().generate(password, transformation, length);
    password = null;
    return this;
}
public KeySimMakerBytes reBuild(byte[] spec){
    key = KeySim.fromSpec(password, spec, false);
    password = null;
    return this;
}
public KeySimMakerBytes setLength(KeySim.Length length){
    this.length = length;
    return this;
}
public KeySimMakerBytes setTransformation(KeySim.Transformation transformation){
    this.transformation = transformation;
    return this;
}
protected defEncoderBytes newEncoder(KeySim key){
    return new EncoderBytesCipher(key);
}
public defEncoderBytes getEncoder(){
    if(encoder == null){
        encoder = newEncoder(key);
    }
    return encoder;
}
protected defDecoderBytes newDecoder(KeySim key){
    return new DecoderBytesCipher(key);
}
public defDecoderBytes getDecoder(){
    if(decoder == null){
        decoder = newDecoder(key);
    }
    return decoder;
}

public byte[] specToBytes(){
    return key.specToBytes();
}
@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

}
