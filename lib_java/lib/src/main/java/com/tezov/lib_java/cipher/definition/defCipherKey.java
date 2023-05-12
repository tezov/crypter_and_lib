/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.cipher.definition;

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
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;

public interface defCipherKey{

String getTransformationAlgorithm();

Key getKey();

byte[] randomIv(int length);

default void init(Cipher cipher, int mode, byte[] iv) throws InvalidAlgorithmParameterException, InvalidKeyException{
    cipher.init(mode, getKey(), getCipherAlgorithmSpec(iv, cipher.getBlockSize()));
}

default byte[] initIv(byte[] iv, int cipherBlockSize){
    if(iv == null){
        iv = randomIv(cipherBlockSize);
    }
    return iv;
}

default AlgorithmParameterSpec getCipherAlgorithmSpec(byte[] iv, int cipherBlockSize){
    return new IvParameterSpec(initIv(iv, cipherBlockSize));
}

}
