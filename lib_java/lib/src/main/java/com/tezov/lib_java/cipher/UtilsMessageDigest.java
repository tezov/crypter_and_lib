/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.cipher;

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
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.primitive.BytesTo;
import com.tezov.lib_java.type.primitive.string.StringCharTo;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public class UtilsMessageDigest{

public static String digest(Mode mode, String s){
    return BytesTo.StringHex(digestToBytes(mode, s));
}
public static String digestToStringHex(Mode mode, byte[] bytes){
    return BytesTo.StringHex(digest(mode, bytes));
}
public static byte[] digestToBytes(Mode mode, String s){
    return digest(mode, StringCharTo.Bytes(s));
}
public static byte[] digest(Mode mode, byte[] bytes){
    if(mode == Mode.NONE){
        return bytes;
    } else {
        try{
            MessageDigest md = SecureProvider.messageDigest(mode.name());
            md.update(bytes);
            return md.digest();
        } catch(Throwable e){
            return null;
        }
    }
}
public static Digester newDigester(Mode mode){
    try{
        return new Digester(mode);
    } catch(Throwable e){

DebugException.start().log(e).end();

        return null;
    }
}

public enum Mode{
    NONE, MD5, SHA1, SHA256, SHA512
}

public static class Digester{
    private final MessageDigest digester;
    public Digester(Mode mode) throws NoSuchAlgorithmException, NoSuchProviderException{
DebugTrack.start().create(this).end();
        this.digester = SecureProvider.messageDigest(mode.name());
    }
    public void update(byte[] bytes, int off, int len){
        digester.update(bytes, off, len);
    }
    public byte[] toBytes(){
        return digester.digest();
    }
    public String toStringHex(){
        return BytesTo.StringHex(toBytes());
    }
    @Override
    protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
        super.finalize();
    }

}

}
