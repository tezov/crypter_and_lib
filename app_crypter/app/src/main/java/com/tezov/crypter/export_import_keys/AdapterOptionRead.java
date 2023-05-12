/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.crypter.export_import_keys;

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
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import androidx.fragment.app.Fragment;
import static com.tezov.crypter.export_import_keys.dbKeyFormatter.CRYPT_FORMAT;

import com.tezov.crypter.application.AppInfo;
import com.tezov.lib_java.buffer.ByteBufferPacker;
import com.tezov.lib_java.cipher.definition.defDecoder;
import com.tezov.lib_java.cipher.keyMaker.keySim.KeySimMaker;
import com.tezov.lib_java.cipher.keyMaker.keySim.KeySimMaker_StringToBytes;
import com.tezov.lib_java.cipher.misc.PasswordCipher;
import com.tezov.lib_java.generator.uid.defUid;
import com.tezov.lib_java.debug.DebugTrack;

import java.io.IOException;

public class AdapterOptionRead{
private Integer versionTable = null;
private Integer versionFile = null;
private final KeySimMaker keySimMaker;
private defUid uid = null;
private boolean isFileGeneratedFromSameApp = false;
public AdapterOptionRead(PasswordCipher password){
DebugTrack.start().create(this).end();
    this.keySimMaker = new KeySimMaker_StringToBytes(password, CRYPT_FORMAT);
}
public void rebuild(byte[] spec) throws IOException{
    try{
        this.keySimMaker.reBuild(spec);
        this.keySimMaker.getDecoder().setPacker(new ByteBufferPacker());
    } catch(Throwable e){
        throw new IOException("rebuild failed");
    }
}
public Integer getVersionTable(){
    return versionTable;
}
public AdapterOptionRead setVersionTable(Integer value){
    this.versionTable = value;
    return this;
}
public Integer getVersionFile(){
    return versionFile;
}
public AdapterOptionRead setVersionFile(Integer value){
    this.versionFile = value;
    return this;
}
public defUid getUid(){
    return uid;
}
public AdapterOptionRead setUid(defUid uid){
    this.uid = uid;
    this.isFileGeneratedFromSameApp = AppInfo.isSameApp(uid);
    return this;
}
public boolean isFileGeneratedFromSameApp(){
    return isFileGeneratedFromSameApp;
}
public defDecoder<String, byte[]> getDecoder(){
    return keySimMaker.getDecoder();
}
public PasswordCipher passwordApp(){
    return PasswordCipher.fromClear(uid.toHexString().toCharArray());
}
@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

}
