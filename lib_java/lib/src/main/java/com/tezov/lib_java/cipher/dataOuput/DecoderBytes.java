/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.cipher.dataOuput;

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
import com.tezov.lib_java.cipher.definition.defDecoderBytes;
import com.tezov.lib_java.cipher.key.KeyXor;
import com.tezov.lib_java.file.UtilsStream;
import com.tezov.lib_java.debug.DebugTrack;

public class DecoderBytes implements defDecoderBytes{
private final defDecoderBytes decoder;

protected DecoderBytes(defDecoderBytes decoder){
DebugTrack.start().create(this).end();
    this.decoder = decoder;
}

public static DecoderBytes newDecoder(defCipherKey key){
    DecoderBytesCipher decoder = new DecoderBytesCipher(key);
    return new DecoderBytes(decoder);
}
public static DecoderBytes newDecoder(KeyXor key){
    DecoderBytesXor decoder = new DecoderBytesXor(key);
    return new DecoderBytes(decoder);
}


@Override
public defDecoderBytes setPacker(ByteBufferPacker packer){
    decoder.setPacker(packer);
    return this;
}
@Override
public byte[] decode(byte[] cipherBytes){
    return decoder.decode(cipherBytes);
}
@Override
public byte[] decode(byte[] cipherBytes, int offset, int length){
    return decoder.decode(cipherBytes);
}

@Override
public boolean decode(UtilsStream.StreamLinker stream){
    return decoder.decode(stream);
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}
}
