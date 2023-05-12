/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.application;

import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import java.util.LinkedList;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import androidx.fragment.app.Fragment;

import com.tezov.lib_java.cipher.holder.CipherHolderXor;
import com.tezov.lib_java.util.UtilsString;

import java.util.HashSet;
import java.util.Set;

import com.tezov.lib_java.file.Directory;
import com.tezov.lib_java.file.File;
import com.tezov.lib_java.generator.uid.UUID;
import com.tezov.lib_java.generator.uid.Uid;
import com.tezov.lib_java.generator.uid.defUid;
import com.tezov.lib_java.toolbox.Nullify;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.type.primitive.BytesTo;
import com.tezov.lib_java.type.primitive.string.StringHexTo;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static com.tezov.lib_java.file.StoragePackage.Type.PRIVATE_SHARE_PREFERENCE;

public class SharedPreferences{
private final android.content.SharedPreferences sharedPreferences;
private final String name;
private CipherHolderXor cipher = null;

public SharedPreferences(String name){
    this(name, android.content.Context.MODE_PRIVATE);
}
public SharedPreferences(String name, int mode){
    this.name = name;
    sharedPreferences = AppContext.getSharedPreferences(name, mode);
}
public static Directory directory(){
    return new Directory(PRIVATE_SHARE_PREFERENCE);
}

public SharedPreferences setCipherHolder(CipherHolderXor cipher){
    this.cipher = cipher;
    return this;
}

public String encodeKey(String key){
    if(cipher == null){
        return key;
    } else {
        return cipher.getEncoderKey().encode(key);
    }
}
public String decodeKey(String key){
    if(cipher == null){
        return key;
    } else {
        return cipher.getDecoderKey().decode(key);
    }
}
public String encodeValue(String value){
    if(cipher == null){
        return value;
    } else {
        return cipher.getEncoderValue().encode(value);
    }
}
public String decodeValue(String value){
    if(cipher == null){
        return value;
    } else {
        return cipher.getDecoderValue().decode(value);
    }
}

public String getName(){
    return name;
}
public android.content.SharedPreferences getSharedPreferences(){
    return sharedPreferences;
}

public boolean hasKey(String key){
    if(cipher != null){
        key = encodeKey(key);
    }
    return sharedPreferences.contains(key);
}
public List<String> findKeyStartWith(String pattern){
    List<String> strings = new ArrayList<>();
    Map<String, ?> keys = sharedPreferences.getAll();
    for(Map.Entry<String, ?> e: keys.entrySet()){
        String key = decodeKey(e.getKey());
        if((key != null) && key.startsWith(pattern)){
            strings.add(key);
        }
    }
    return Nullify.collection(strings);

}

public String getString(String key){
    return get(key);
}
public void put(String key, String data){
    offer(key, data);
}
public void put(String key, Set<String> datas){
    if(datas == null){
        offer(key, null);
    }
    else{
        offer(key, UtilsString.join(",", datas.iterator()).toString());
    }

}
public Set<String> getStringSet(String key){
    String s = getString(key);
    if(s == null){
        return null;
    }
    else{
        return new HashSet<>(Arrays.asList(s.split(",")));
    }
}
public byte[] getBytes(String key){
    return StringHexTo.Bytes(get(key));
}
public void put(String key, byte[] bytes){
    offer(key, BytesTo.StringHex(bytes));
}
public Uid getUID(String key){
    return Uid.fromHexString(get(key));
}
public UUID getUUID(String key){
    return UUID.fromHexString(get(key));
}
public void put(String key, defUid uid){
    offer(key, uid.toHexString());
}
public Integer getInt(String key){
    String s = get(key);
    if(s == null){
        return null;
    } else {
        return Integer.valueOf(s);
    }
}
public void put(String key, Integer data){
    offer(key, data != null ? Integer.toString(data) : null);
}
public Long getLong(String key){
    String s = get(key);
    if(s == null){
        return null;
    } else {
        return Long.valueOf(s);
    }
}
public void put(String key, Long data){
    offer(key, data != null ? Long.toString(data) : null);
}
public Float getFloat(String key){
    String s = get(key);
    if(s == null){
        return null;
    } else {
        return Float.valueOf(s);
    }
}
public void put(String key, Float data){
    offer(key, data != null ? Float.toString(data) : null);
}
public Boolean getBoolean(String key){
    String s = get(key);
    if(s == null){
        return null;
    } else {
        return Boolean.valueOf(s);
    }
}
public void put(String key, Boolean data){
    put(key, data != null ? Boolean.toString(data) : null);
}

private String get(String key){
    if(cipher != null){
        key = encodeKey(key);
    }
    String data = Nullify.string(sharedPreferences.getString(key, null));
    if(cipher != null){
        data = decodeValue(data);
    }
    return data;
}
private void offer(String key, String data){
    if(cipher != null){
        key = encodeKey(key);
    }
    if(Nullify.string(data) == null){
        sharedPreferences.edit().remove(key).commit();
    } else {
        if(cipher != null){
            data = encodeValue(data);
        }
        sharedPreferences.edit().putString(key, data).commit();
    }
}
public void remove(String key){
    if(cipher != null){
        key = encodeKey(key);
    }
    sharedPreferences.edit().remove(key).commit();
}
public void clear(){
    sharedPreferences.edit().clear().commit();
}
final public void toDebugLog(){
    for(Map.Entry<String, ?> e: sharedPreferences.getAll().entrySet()){
        String key = e.getKey();
        String data = (String)e.getValue();
        if(cipher != null){
            key = decodeKey(key);
            data = decodeValue(data);
        }
DebugLog.start().send("[" + key + ":" + data + "]").end();
    }
}

public File toFile(Directory directory, String[] excludedKey) throws IOException{
    return toFile(new File(directory, getName() + ".xml"), excludedKey);
}
public File toFile(Directory directory) throws IOException{
    return toFile(new File(directory, getName() + ".xml"));
}
public File toFile(File file) throws IOException{
    return toFile(file, null);
}
public File toFile(File file, String[] excludedKey) throws IOException{
    FileOutputStream os = null;
    try{
        List<String> excludedKeyList;
        if(excludedKey != null){
            excludedKeyList = Arrays.asList(excludedKey);
        } else {
            excludedKeyList = new ArrayList<>(0);
        }
        Properties properties = new Properties();
        Map<String, ?> keys = sharedPreferences.getAll();
        for(Map.Entry<String, ?> e: keys.entrySet()){
            if(!excludedKeyList.contains(e.getKey())){
                properties.setProperty(e.getKey(), (String)e.getValue());
            }
        }
        os = file.getOutputStream();
        properties.storeToXML(os, "");
        os.close();
        return file;
    } catch(IOException e){
        if(os != null){
            try{
                os.close();
            } catch(IOException ioException){

DebugException.start().log(ioException).end();

            }
        }
        throw e;
    }
}

public void fromFile(File file) throws IOException{
    fromFile(file, true, false);
}
public void fromFile(File file, boolean clearBefore) throws IOException{
    fromFile(file, clearBefore, true);
}
public void fromFile(File file, boolean clearBefore, boolean overrideValue) throws IOException{
    InputStream is = null;
    try{
        is = file.getInputStream();
        Properties properties = new Properties();
        properties.loadFromXML(is);
        is.close();
        if(clearBefore){
            clear();
        }
        android.content.SharedPreferences.Editor editor = sharedPreferences.edit();
        for(Map.Entry<Object, Object> e: properties.entrySet()){
            String key = (String)e.getKey();
            String value = (String)e.getValue();
            if(overrideValue || !sharedPreferences.contains(key)){
                editor.putString(key, value);
            }
        }
        editor.commit();
    } catch(Throwable e){
        if(is != null){
            try{
                is.close();
            } catch(java.lang.Throwable ex){

DebugException.start().log(ex).end();

            }
        }
        throw e;
    }
}

@Override
protected void finalize() throws Throwable{
    super.finalize();
}

}
