/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.cipher.key;

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
import com.tezov.lib_java.application.AppConfig;
import com.tezov.lib_java.application.AppConfigKey;
import com.tezov.lib_java.cipher.misc.PasswordCipher;
import com.tezov.lib_java.toolbox.Nullify;
import com.tezov.lib_java.type.primitive.ByteTo;
import com.tezov.lib_java.type.primitive.string.StringBase64To;

public class KeyXor extends SecretKey{

public KeyXor(){
}

public static KeyXor fromSpec(PasswordCipher password, String spec){
    return fromSpec(password, StringBase64To.Bytes(spec));
}
public static KeyXor fromSpec(PasswordCipher password, String spec, boolean erasePrivateData){
    return fromSpec(password, StringBase64To.Bytes(spec), erasePrivateData);
}
public static KeyXor fromSpec(PasswordCipher password, byte[] specBytes){
    return fromSpec(password, specBytes, true);
}
public static KeyXor fromSpec(PasswordCipher password, byte[] specBytes, boolean erasePrivateData){
    KeyXor key = new KeyXor().fromSpecBytes(specBytes).rebuild(password);
    Nullify.array(specBytes);
    if(erasePrivateData){
        return key.erasePrivateData();
    } else {
        return key;
    }
}

public static KeyXor fromKey(String key){
    return fromKey(StringBase64To.Bytes(key));
}
public static KeyXor fromKey(String key, boolean erasePrivateData){
    return fromKey(StringBase64To.Bytes(key), erasePrivateData);
}
public static KeyXor fromKey(byte[] keyBytes){
    return fromKey(keyBytes, true);
}
public static KeyXor fromKey(byte[] keyBytes, boolean erasePrivateData){
    KeyXor key = new KeyXor().fromKeyBytes(keyBytes);
    Nullify.array(keyBytes);
    if(erasePrivateData){
        return key.erasePrivateData();
    } else {
        return key;
    }
}

public KeyXor generate(PasswordCipher password){
    return generate(password, DEFAULT_LENGTH);
}
public KeyXor generate(PasswordCipher password, Length length){
    return generate(password, length.getValueByte());
}
@Override
public KeyXor copy(){
    return new KeyXor().fromKeyBytes(keyToBytes());
}

public final static Length DEFAULT_LENGTH = AppConfig.getKeyXorLength(AppConfigKey.CIPHER_DEFAULT_KEY_XOR_LENGTH.getId());

public enum Length{
    L32(0, 32),
    L64(1, 64),
    L128(2, 128),
    L192(3, 192),
    L256(4, 256);
    private final int id;
    private final int value_bit;
    Length(int id, int value){
        this.id = id;
        this.value_bit = value;
    }
    public static Length findWithLength(int length){
        return findWithLengthBit(length * ByteTo.SIZE);
    }
    public static Length findWithLengthBit(int length){
        Length[] values = values();
        for(Length k: values){
            if(k.value_bit == length){
                return k;
            }
        }
        return null;
    }
    public static Length findWithId(int id){
        Length[] values = values();
        for(Length k: values){
            if(k.id == id){
                return k;
            }
        }
        return null;
    }
    public int getValueBit(){
        return value_bit;
    }
    public int getValueByte(){
        return value_bit / ByteTo.SIZE;
    }
    public int getId(){
        return id;
    }
}


}
