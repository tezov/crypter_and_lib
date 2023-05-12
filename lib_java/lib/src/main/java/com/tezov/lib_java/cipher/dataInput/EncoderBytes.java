/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.cipher.dataInput;

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
import com.tezov.lib_java.buffer.ByteBufferPacker;
import com.tezov.lib_java.cipher.definition.defCipherKey;
import com.tezov.lib_java.cipher.definition.defEncoderBytes;
import com.tezov.lib_java.cipher.key.KeyXor;
import com.tezov.lib_java.file.UtilsStream;
import com.tezov.lib_java.debug.DebugTrack;

public class EncoderBytes implements defEncoderBytes{
private final defEncoderBytes encoder;

protected EncoderBytes(defEncoderBytes encoder){
DebugTrack.start().create(this).end();
    this.encoder = encoder;
}

public static EncoderBytes newEncoder(defCipherKey key){
    EncoderBytesCipher encoder = new EncoderBytesCipher(key);
    return new EncoderBytes(encoder);
}
public static EncoderBytes newEncoder(KeyXor key){
    EncoderBytesXor encoder = new EncoderBytesXor(key);
    return new EncoderBytes(encoder);
}

@Override
public byte[] getIv(){
    return encoder.getIv();
}
@Override
public defEncoderBytes setIv(byte[] iv){
    return encoder.setIv(iv);
}
@Override
public defEncoderBytes setRandomIv(){
    return encoder.setRandomIv();
}
@Override
public defEncoderBytes setPacker(ByteBufferPacker packer){
    encoder.setPacker(packer);
    return this;
}

@Override
public byte[] encode(byte[] bytes, int offset, int length){
    return encoder.encode(bytes, offset, length);
}
@Override
public byte[] encode(byte[] bytes, int offset, int length, byte[] iv){
    return encoder.encode(bytes, offset, length, iv);
}
@Override
public boolean encode(UtilsStream.StreamLinker stream){
    return encoder.encode(stream);
}
@Override
public boolean encode(UtilsStream.StreamLinker stream, byte[] iv){
    return encoder.encode(stream, iv);
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

}
