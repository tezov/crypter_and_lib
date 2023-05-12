/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.application;

import static com.tezov.lib_java.application.AppConfigKey.SPEC_CONFIG;

import android.util.Log;

import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.util.UtilsString;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java.buffer.ByteBufferInput;
import com.tezov.lib_java.buffer.ByteBufferOutput;
import com.tezov.lib_java.cipher.holder.CipherHolderXor;
import com.tezov.lib_java.cipher.key.KeyMac;
import com.tezov.lib_java.cipher.key.KeyMutual;
import com.tezov.lib_java.cipher.key.KeyObfusc;
import com.tezov.lib_java.cipher.key.KeySim;
import com.tezov.lib_java.cipher.key.KeyXor;
import com.tezov.lib_java.cipher.misc.PasswordCipher;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.file.Directory;
import com.tezov.lib_java.toolbox.Compare;
import com.tezov.lib_java.type.collection.ListEntry;
import com.tezov.lib_java.type.primaire.Entry;
import com.tezov.lib_java.type.primitive.ByteTo;
import com.tezov.lib_java.type.primitive.BytesTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.primitive.string.StringBase64To;
import com.tezov.lib_java.type.primitive.string.StringCharTo;
import com.tezov.lib_java.util.UtilsBytes;

import java.io.BufferedInputStream;
import java.util.List;


public class AppConfig{
private static AppConfigKey.Adapter keyAdapter = null;
private static ListEntry<Integer, String> properties = null;

protected AppConfig(){
}

public static void setKeyAdapter(AppConfigKey.Adapter keyAdapter){
    AppConfig.keyAdapter = keyAdapter;
}
public static void setProperties(ListEntry<Integer, String> properties){
    AppConfig.properties = properties;
}
public static ListEntry<Integer, String> getProperties(){
    return properties;
}

public static String getString(Integer key){
    return properties.getValue(key);
}
public static Long getLong(Integer key){
    String s = getString(key);
    if(s == null){
        return null;
    } else {
        return Long.parseLong(s);
    }
}
public static Integer getInt(Integer key){
    String s = getString(key);
    if(s == null){
        return null;
    } else {
        return Integer.parseInt(s);
    }
}
public static Float getFloat(Integer key){
    String s = getString(key);
    if(s == null){
        return null;
    } else {
        return Float.parseFloat(s);
    }
}
public static Boolean getBoolean(Integer key){
    String s = getString(key);
    if(s == null){
        return null;
    } else {
        return Boolean.parseBoolean(s);
    }
}
public static Directory getDirectory(Integer key){
    String s = getString(key);
    if(s == null){
        return null;
    } else {
        return Directory.from(s);
    }
}
public static KeyObfusc.Transformation getKeyObfuscTransformation(Integer key){
    String s = getString(key);
    if(s == null){
        return null;
    } else {
        return KeyObfusc.Transformation.find(s);
    }
}
public static KeyXor.Length getKeyXorLength(Integer key){
    Integer s = getInt(key);
    if(s == null){
        return null;
    } else {
        return KeyXor.Length.findWithLengthBit(s);
    }
}
public static KeyMac.Transformation getKeyMacTransformation(Integer key){
    String s = getString(key);
    if(s == null){
        return null;
    } else {
        return KeyMac.Transformation.find(s);
    }
}
public static KeySim.Transformation getKeySimTransformation(Integer key){
    String s = getString(key);
    if(s == null){
        return null;
    } else {
        return KeySim.Transformation.find(s);
    }
}
public static KeySim.Length getKeySimLength(Integer key){
    Integer s = getInt(key);
    if(s == null){
        return null;
    } else {
        return KeySim.Length.findWithLengthBit(s);
    }
}
public static com.tezov.lib_java.cipher.key.rsa.KeyAsymPrivate.Transformation getKeyRsaTransformation(Integer key){
    String s = getString(key);
    if(s == null){
        return null;
    } else {
        return com.tezov.lib_java.cipher.key.rsa.KeyAsymPrivate.Transformation.find(s);
    }
}
public static com.tezov.lib_java.cipher.key.rsa.KeyAsymPrivate.Length getKeyRsaLength(Integer key){
    Integer s = getInt(key);
    if(s == null){
        return null;
    } else {
        return com.tezov.lib_java.cipher.key.rsa.KeyAsymPrivate.Length.findWithLengthBit(s);
    }
}
public static com.tezov.lib_java.cipher.key.ecdh.KeyAsymPrivate.Curve getKeyAgreementCurve(Integer key){
    String s = getString(key);
    if(s == null){
        return null;
    } else {
        return com.tezov.lib_java.cipher.key.ecdh.KeyAsymPrivate.Curve.findWithCurveName(s);
    }
}

public static class Builder{
    ListEntry<Integer, String> properties;
    ByteBufferInput inEncrypted = null;
    CipherHolderXor cipher = null;
    public Builder(){
        this.properties = new ListEntry<>();
    }
    public Builder buildProperties_Convert(ListEntry<String, String> properties){
        for(Entry<String, String> e: properties){
            Integer key = keyAdapter.toIndex(e.key);
            if((key == null)){
DebugException.start().log("key " + e.key + " not found").end();
            }
            else{
                this.properties.put(key, e.value);
            }
        }
        return this;
    }
    public Builder buildProperties_ExtractUnencrypted(String propertiesEncrypted){
        inEncrypted = ByteBufferInput.wrapPacked(StringBase64To.Bytes(propertiesEncrypted));
        int unencryptedLength = inEncrypted.getInt();
        byte[] alter = inEncrypted.getBytes();
        for(int i=0; i<unencryptedLength; i++){
            Integer keyId = inEncrypted.getInt();
            String keyName = BytesTo.StringChar(UtilsBytes.xor(inEncrypted.getBytes(), alter));
            properties.put(keyId, keyName);
        }
        return this;
    }
    public Builder buildCipher(PasswordCipher password){
        String spec = properties.getValue(SPEC_CONFIG.getId());
        cipher = AppConfig.buildCipher(password, spec);
        return this;
    }
    public Builder buildProperties_ExtractEncrypted(){
        ByteBufferInput inProperties = ByteBufferInput.wrap(cipher.getDecoderValue().decode(inEncrypted.getBytes()));
        while(inProperties.remaining() > 0){
            Integer key = inProperties.getInt();
            String value = inProperties.getString();
            properties.put(key, value);
        }
        return this;
    }
    public ListEntry<Integer, String> getProperties(){
        return properties;
    }
    public CipherHolderXor getCipher(){
        return cipher;
    }
}
public static class Generator{
    private final static int ALTER_LENGTH = 32;
    ListEntry<Integer, String> properties;
    List<Integer> unencryptedKeys;
    List<Integer> excludeKeys;
    PasswordCipher password = null;
    String propertiesEncrypted = null;

    public Generator(ListEntry<Integer, String> properties){
        this.properties = properties;
        this.unencryptedKeys = new ArrayList<>();
        this.excludeKeys = new ArrayList<>();
    }
    public Generator addUnencryptedKey(Integer key){
        this.unencryptedKeys.add(key);
        return this;
    }
    public Generator addExcludeKey(Integer key){
        this.excludeKeys.add(key);
        return this;
    }
    public Generator generate(PasswordCipher password){
        try{
            this.password = password;
            String specConfig = null;
            if(properties.hasKey(SPEC_CONFIG.getId())){
                specConfig = properties.getValue(SPEC_CONFIG.getId());
            }
            CipherHolderXor cipher = AppConfig.buildCipher(password, specConfig);
            if(specConfig == null){
                specConfig = cipher.specToString();
                properties.put(SPEC_CONFIG.getId(), specConfig);
            }
            unencryptedKeys.add(SPEC_CONFIG.getId());
            ByteBufferOutput out = ByteBufferOutput.obtain();
            out.put(unencryptedKeys.size());
            byte[] alter = UtilsBytes.random(ALTER_LENGTH);
            out.put(alter);
            for(Integer id:unencryptedKeys){
                out.put(id);
                out.put(UtilsBytes.xor(StringCharTo.Bytes(properties.getValue(id)), alter));
            }
            ByteBufferOutput outProperties = ByteBufferOutput.obtain();
            for(Entry<Integer, String> e: properties){
                if(!excludeKeys.contains(e.key) && !unencryptedKeys.contains(e.key)){
                    outProperties.put(e.key);
                    outProperties.put(e.value);
                }
            }
            out.put(cipher.getEncoderValue().encode(outProperties.toBytes()));
            propertiesEncrypted = BytesTo.StringBase64(out.toBytesPacked());
        }
        catch(Throwable e){
DebugException.start().log(e).end();
        }
        return this;
    }
    public ListEntry<Integer, String> getProperties(){
        return properties;
    }
    public Generator verify(){
        //VERIFY
        Builder builder = new Builder();
        builder.buildProperties_ExtractUnencrypted(propertiesEncrypted)
            .buildCipher(password)
            .buildProperties_ExtractEncrypted();
        boolean succeed = true;
        ListEntry<Integer, String> propertiesDecrypted = builder.getProperties();
        for(Entry<Integer, String> e: properties){
            if(!excludeKeys.contains(e.key) && !Compare.equals(propertiesDecrypted.getValue(e.key), e.value)){
DebugLog.start().send(keyAdapter.fromIndex(e.key) + " mismatch:" + propertiesDecrypted.getValue(e.key) + " /// " + e.value).end();
                succeed = false;
            }
        }
        if(succeed){
DebugLog.start().send("VERIFICATION SUCCESS").end();
        }
        return this;
    }
    public void toDebugLog(){
DebugLog.start().send("SPEC_CONFIG : " + properties.getValue(SPEC_CONFIG.getId())).end();
        String specMutual;
        if(properties.hasKey(AppConfigKey.SPEC_MUTUAL.id)){
            specMutual = properties.getValue(AppConfigKey.SPEC_MUTUAL.id);
        } else {
            KeyMutual keyMutual = new KeyMutual().generate(password);
            specMutual = keyMutual.specToStringBase64();
        }
DebugLog.start().send("SPEC_MUTUAL : " + specMutual).end();
DebugLog.start().send("json config encrypted to cfg : " + propertiesEncrypted).end();
    }
}

protected static CipherHolderXor buildCipher(PasswordCipher password, String spec){
    if(spec == null){
        return new CipherHolderXor.Generator().setPasswordKeyAndValue(password).generate();

    } else {
        return new CipherHolderXor.Builder().setPasswordKeyAndValue(password).setSpec(spec).build();
    }
}
public static void toDebugLogProperties(){
DebugLog.start().send(properties).end();
}


}
