/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.cipher.misc;

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
import com.tezov.lib_java.cipher.SecureProvider;

import java.security.NoSuchProviderException;

import com.tezov.lib_java.cipher.definition.defMacKey;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class MacW{
private final defMacKey key;
private final javax.crypto.Mac mac;
private byte[] iv = null;
private MacW(defMacKey key) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException{
    this.key = key;
    mac = SecureProvider.mac(key.getTransformationAlgorithm());
    mac.init(key.getKey());
}
public static MacW getInstance(defMacKey key) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException{
    return new MacW(key);
}
public defMacKey getKey(){
    return key;
}
public byte[] getIv(){
    return iv;
}
public void init(byte[] iv){
    if(iv == null){
        this.iv = key.randomIv();
    } else {
        this.iv = iv;
    }
    mac.reset();
    mac.update(this.iv);
    mac.update(key.getTag());
}
public void update(byte b){
    mac.update(b);
}
public void update(byte[] bytes){
    mac.update(bytes);
}
public void update(byte[] bytes, int offset, int length){
    mac.update(bytes, offset, length);
}
public byte[] doFinal(){
    return mac.doFinal();
}

}
