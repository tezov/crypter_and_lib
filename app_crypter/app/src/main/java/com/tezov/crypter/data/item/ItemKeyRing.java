/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.crypter.data.item;

import com.tezov.lib_java.buffer.ByteBufferBuilder;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugTrack;
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
import com.tezov.lib_java_android.application.AppContext;

import androidx.fragment.app.Fragment;
import android.os.Parcel;

import com.tezov.crypter.data_transformation.PasswordCipherL2;
import com.tezov.lib_java_android.application.AppInfo;
import com.tezov.lib_java_android.application.PackageSignature;
import com.tezov.lib_java.buffer.ByteBuffer;
import com.tezov.lib_java.cipher.key.KeyMac;
import com.tezov.lib_java.cipher.key.KeySim;
import com.tezov.lib_java.cipher.misc.PasswordCipher;
import com.tezov.lib_java_android.database.ItemBase;
import com.tezov.lib_java_android.definition.defCreatable;
import com.tezov.lib_java_android.factory.FactoryObject;
import com.tezov.lib_java.generator.uid.UUID;
import com.tezov.lib_java.generator.uid.UUIDGenerator;
import com.tezov.lib_java.generator.uid.defUid;
import com.tezov.lib_java.toolbox.Compare;
import com.tezov.lib_java.toolbox.Nullify;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java_android.type.android.wrapper.ParcelW;
import com.tezov.lib_java.type.primitive.BytesTo;
import com.tezov.lib_java.type.primitive.CharsTo;

public class ItemKeyRing extends ItemBase<ItemKeyRing>{
public static final Creator<ItemKeyRing> CREATOR = new Creator<ItemKeyRing>(){
    @Override
    public ItemKeyRing createFromParcel(Parcel p){
        ParcelW parcel = ParcelW.obtain().replace(p);
        return ItemKeyRing.obtain().replaceBy(parcel);
    }

    @Override
    public ItemKeyRing[] newArray(int size){
        return new ItemKeyRing[size];
    }
};
final private static UUIDGenerator UUID_GENERATOR = UUIDGenerator.newInstance();

static{
    FactoryObject.init(ItemKeyRing.class);
}

private char[] password;
private defUid ownerUid;
private KeySim keyKey = null;
private KeyMac keyMac = null;
private KeySim keyHeader = null;
private KeySim keyData = null;

public static UUIDGenerator getUidGenerator(){
    return UUID_GENERATOR;
}
public static defCreatable<ItemKeyRing> getFactory(){
    return FactoryObject.singleton(ItemKeyRing.class);
}
public static void factoryRelease(){
    FactoryObject.singletonRelease(ItemKeyRing.class);
}
public static ItemKeyRing obtain(){
    return getFactory().create();
}

public defUid getOwnerUid(){
    return ownerUid;
}
public ItemKeyRing setOwnerUid(defUid guid){
    this.ownerUid = guid;
    return this;
}
public boolean isOwner(){
    return Compare.equals(ownerUid!=null?ownerUid.toBytes():null, AppInfo.getGUID().toBytes());
}

public void nullifyPassword(){
    password = null;
}
public boolean hasPassword(){
    return password != null;
}
public char[] getPassword(){
    return password;
}
public ItemKeyRing setPassword(char[] password){
    this.password = password;
    return this;
}

public KeySim getKeyKey(){
    return keyKey;
}
public ItemKeyRing setKeyKey(KeySim keyKey){
    this.keyKey = keyKey;
    return this;
}
public KeyMac getKeyMac(){
    return keyMac;
}
private void keyMacSetTag(){
    ByteBufferBuilder keyMacTag = ByteBufferBuilder.obtain();
    keyMacTag.put(AppContext.getPackageName());
    keyMacTag.put(PackageSignature.getFingerPrint());
    keyMac.setTag(keyMacTag.array());
}
public KeySim getKeyHeader(){
    return keyHeader;
}
public KeySim getKeyData(){
    return keyData;
}

private PasswordCipher getPasswordKey(){
    return PasswordCipher.fromClear(BytesTo.Chars(keyKey.getEncoded()));
}

public ItemKeyRing generateKey(PasswordCipherL2 password, UUID guid, KeySim.Transformation transformation, KeySim.Length length){
    clear();
    this.password = password.getL0().clone();
    this.keyKey = new KeySim().generate(password.scramble(guid.toBytes()), transformation, length);
    this.ownerUid = AppInfo.getGUID();
    return this;
}
public ItemKeyRing generateKey(PasswordCipher password, UUID guid, KeySim.Transformation transformation, KeySim.Length length){
    return generateKey(PasswordCipherL2.fromCiphered(password.get().clone()), guid, transformation, length);
}
public ItemKeyRing generateRing(){
    com.tezov.lib_java.cipher.misc.PasswordCipher passwordKey = getPasswordKey();
    this.keyMac = new KeyMac().generate(passwordKey);
    keyMacSetTag();
    this.keyHeader = new KeySim().generate(passwordKey, KeySim.Transformation.AES_CTR_NO_PAD);
    this.keyData = new KeySim().generate(passwordKey, keyKey.getTransformation(), KeySim.Length.findWithLength(keyKey.getLength()));
    return this;
}

public KeySim rebuildKeyKey(UUID guid, byte[] spec){
    return rebuildKeyKey(PasswordCipherL2.fromCiphered(password), guid, spec);
}
public KeySim rebuildKeyKey(PasswordCipher password, UUID guid, byte[] spec){
    return rebuildKeyKey(PasswordCipherL2.fromCiphered(password.get().clone()), guid, spec);
}
public KeySim rebuildKeyKey(PasswordCipherL2 password, UUID guid, byte[] spec){
    this.password = password.getL0().clone();
    this.keyKey = KeySim.fromSpec(password.scramble(guid.toBytes()), spec, false);
    return this.keyKey;
}
public KeyMac rebuildKeyMac(byte[] spec){
    this.keyMac = KeyMac.fromSpec(getPasswordKey(), spec);
    keyMacSetTag();
    return this.keyMac;
}
public KeySim rebuildKeyHeader(byte[] spec){
    this.keyHeader = KeySim.fromSpec(getPasswordKey(), spec);
    return this.keyHeader;
}
public KeySim rebuildKeyData(byte[] spec){
    this.keyData = KeySim.fromSpec(getPasswordKey(), spec);
    return this.keyData;
}

public boolean canOffer(){
    if(keyKey != null){
        return keyKey.canMakeByteBuffer();
    } else {
        return false;
    }
}

@Override
public ItemKeyRing clear(){
    super.clear();
    password = Nullify.array(password);
    ownerUid = null;
    return clearKey();
}
public ItemKeyRing clearKey(){
    if(this.keyKey != null){
        keyKey.destroyNoThrow();
        keyKey = null;
    }
    if(this.keyMac != null){
        keyMac.destroyNoThrow();
        keyMac = null;
    }
    if(this.keyHeader != null){
        keyHeader.destroyNoThrow();
        keyHeader = null;
    }
    if(this.keyData != null){
        keyData.destroyNoThrow();
        keyData = null;
    }
    return this;
}

@Override
public ItemKeyRing newItem(){
    return ItemKeyRing.obtain();
}

@Override
public ItemKeyRing copy(){
DebugException.start().notImplemented().end();
    return null;
}

@Override
protected void fromParcel(ParcelW parcel){
    super.fromParcel(parcel);
    this.password = BytesTo.Chars(parcel.readBytes());
    this.keyKey = KeySim.fromKey(parcel.readBytes(), false);
    this.ownerUid = UUID.fromBytes(parcel.readBytes());
}
@Override
protected void toParcel(Parcel parcel){
    super.toParcel(parcel);
    parcel.writeValue(CharsTo.Bytes(password));
    parcel.writeValue(keyKey.keyToBytes());
    parcel.writeValue(ownerUid!=null?ownerUid.toBytes():null);
}

public byte[] toBytes(boolean withOwnership){
    ByteBufferBuilder buffer = ByteBufferBuilder.obtain();
    buffer.put(getUid()!=null? getUid().toBytes():null);
    buffer.put(withOwnership ? password : null);
    buffer.put(keyKey.keyToBytes());
    buffer.put(withOwnership && isOwner());
    return buffer.array();
}
public ItemKeyRing fromBytes(byte[] bytes){
    clear();
    ByteBuffer buffer = ByteBuffer.wrap(bytes);
    setUid(UUID.fromBytes(buffer.getBytes()));
    this.password = buffer.getChars();
    this.keyKey = KeySim.fromKey(buffer.getBytes(), false);
    if(buffer.getBoolean()){
        this.ownerUid = AppInfo.getGUID();
    }
    return this;
}

@Override
public DebugString toDebugString(){
    DebugString data = super.toDebugString();
    data.append("password", password);
    data.append("ownerUid", ownerUid);
    data.append("keyKey", keyKey);
    data.append("keyMac", keyMac);
    data.append("keyHeader", keyHeader);
    data.append("keyData", keyData);
    return data;
}

@Override
protected void finalize() throws Throwable{
    Nullify.array(password);
    super.finalize();
}

}














