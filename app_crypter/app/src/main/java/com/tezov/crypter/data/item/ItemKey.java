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
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.util.UtilsString;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import androidx.fragment.app.Fragment;
import static com.tezov.crypter.application.AppInfo.SIGNATURE_KEY_PREFIX;

import android.os.Parcel;

import com.tezov.crypter.application.AppInfo;
import com.tezov.crypter.data.misc.ClockFormat;
import com.tezov.lib_java.buffer.ByteBuffer;
import com.tezov.lib_java.cipher.dataAdapter.bytes.DataBytesToStringAdapter;
import com.tezov.lib_java.cipher.dataAdapter.bytes.DataStringToBytesAdapter;
import com.tezov.lib_java.cipher.dataInput.Encoder;
import com.tezov.lib_java.cipher.dataOuput.Decoder;
import com.tezov.lib_java.cipher.definition.defDecoder;
import com.tezov.lib_java.cipher.definition.defEncoder;
import com.tezov.lib_java.cipher.key.KeyXor;
import com.tezov.lib_java.cipher.misc.PasswordCipher;
import com.tezov.lib_java_android.database.ItemBase;
import com.tezov.lib_java_android.definition.defCreatable;
import com.tezov.lib_java_android.factory.FactoryObject;
import com.tezov.lib_java.generator.uid.UUID;
import com.tezov.lib_java.generator.uid.UUIDGenerator;
import com.tezov.lib_java.generator.uid.UidBase;
import com.tezov.lib_java.generator.uid.defUid;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.toolbox.Compare;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java_android.type.android.wrapper.ParcelW;
import com.tezov.lib_java.util.UtilsBytes;

import org.threeten.bp.ZoneId;

public class ItemKey extends ItemBase<ItemKey>{
public static final Creator<ItemKey> CREATOR = new Creator<ItemKey>(){
    @Override
    public ItemKey createFromParcel(Parcel p){
        ParcelW parcel = ParcelW.obtain().replace(p);
        return ItemKey.obtain().replaceBy(parcel);
    }

    @Override
    public ItemKey[] newArray(int size){
        return new ItemKey[size];
    }
};
final private static UUIDGenerator UUID_GENERATOR = UUIDGenerator.newInstance();

static{
    FactoryObject.init(ItemKey.class);
}

private String alias;
private Boolean isOwner;
private defUid guid;
private defUid keyRingUid;
private Long createdDate;
private ZoneId createdZoneId;
private Boolean hasPassword;
private boolean encryptDeleteOriginalFile;
private boolean encryptOverwriteFile;
private boolean encryptFileName;
private boolean encryptAddTimeAndTimeToFileName;
private boolean encryptStrictMode;
private boolean decryptDeleteEncryptedFile;
private boolean decryptOverwriteFile;
private boolean encryptSignText;
private boolean encryptAddDeeplinkToText;


public static UUIDGenerator getUidGenerator(){
    return UUID_GENERATOR;
}
public static defCreatable<ItemKey> getFactory(){
    return FactoryObject.singleton(ItemKey.class);
}
public static void factoryRelease(){
    FactoryObject.singletonRelease(ItemKey.class);
}
public static ItemKey obtain(){
    return getFactory().create();
}

public defUid getKeyRingUid(){
    return keyRingUid;
}
public ItemKey setKeyRingUid(defUid data){
    this.keyRingUid = data;
    return this;
}

public defUid getGuid(){
    return guid;
}
public ItemKey setGuid(defUid guid){
    this.guid = guid;
    return this;
}

public boolean hasSignatureKey(){
    return getUid() != null;
}
public String getSignatureKey(){
    return getSignatureKey(getUid().toBytes(), getGuid().toBytes());
}
private String getSignatureKey(byte[] uid, byte[] guid){
    if(uid == null){
        return AppInfo.toSignature(guid);
    } else {
        return AppInfo.toSignature(SIGNATURE_KEY_PREFIX, UtilsBytes.xor(uid.clone(), guid));
    }
}

public String getAlias(){
    return alias;
}
public ItemKey setAlias(String alias){
    this.alias = alias;
    return this;
}
public byte[] getAliasEncoded(){
    KeyXor keyXor = new KeyXor().generate(PasswordCipher.fromClear(getSignatureKey().toCharArray()));
    defEncoder<String, byte[]> encoder = Encoder.newEncoder(keyXor, DataStringToBytesAdapter.forEncoder());
    ByteBufferBuilder buffer = ByteBufferBuilder.obtain();
    buffer.put(keyXor.specToBytes());
    buffer.put(encoder.encode(alias));
    return buffer.arrayPacked();
}
public boolean isAliasEqual(byte[] uid, byte[] guid, byte[] aliasEncoded){
    if((uid == null) || (aliasEncoded == null)){
        return false;
    }
    ByteBuffer buffer = ByteBuffer.wrapPacked(aliasEncoded);
    byte[] spec = buffer.getBytes();
    byte[] crypted = buffer.getBytes();
    KeyXor keyXor = KeyXor.fromSpec(PasswordCipher.fromClear(getSignatureKey(uid, guid).toCharArray()), spec);
    defDecoder<byte[], String> decoder = Decoder.newDecoder(keyXor, DataBytesToStringAdapter.forDecoder());
    String alias = decoder.decode(crypted, String.class);
    return Compare.equals(this.alias, alias);

}
public boolean isUidEqual(byte[] uid){
    if(uid == null){
        return false;
    } else {
        return UidBase.fromBytes(uid).equals(getUid());
    }
}

public boolean isOwner(){
    return Compare.isTrue(isOwner);
}
public ItemKey setOwner(Boolean owner){
    isOwner = owner;
    return this;
}

public boolean isCreatedByApp(){
    return Compare.equals(guid.toBytes(), AppInfo.getGUID().toBytes());
}

public Long getCreatedDate(){
    return createdDate;
}
private void setCreatedDate(Long data){
    setCreatedDate(data, null);
}
public void setCreatedDate(Long timestamp, ZoneId zoneId){
    this.createdDate = timestamp;
    if(timestamp == null){
        this.createdZoneId = null;
    } else if(zoneId == null){
        this.createdZoneId = Clock.ZoneIdLocal();
    } else {
        this.createdZoneId = zoneId;
    }
}
public ZoneId getCreatedZoneId(){
    return createdZoneId;
}
public String getCreatedDateString(){
    return ClockFormat.longToDateTime_FULL(createdDate, createdZoneId);
}

public Boolean hasPassword(){
    return hasPassword;
}
public ItemKey hasPassword(Boolean hasPassword){
    this.hasPassword = hasPassword;
    return this;
}
public ItemKey setHasPassword(Boolean hasPassword){
    this.hasPassword = hasPassword;
    return this;
}
public ItemKey generate(String alias, UUID guid){
    clear();
    this.alias = alias;
    this.guid = guid;
    setCreatedDate(Clock.MilliSecond.now());
    this.isOwner = true;
    this.hasPassword = true;
    return this;
}

public boolean mustEncryptDeleteOriginalFile(){
    return encryptDeleteOriginalFile;
}
public ItemKey setEncryptDeleteOriginalFile(boolean flag){
    this.encryptDeleteOriginalFile = flag;
    return this;
}
public boolean mustEncryptOverwriteFile(){
    return encryptOverwriteFile;
}
public ItemKey setEncryptOverwriteFile(boolean flag){
    this.encryptOverwriteFile = flag;
    return this;
}
public boolean mustEncryptFileName(){
    return encryptFileName;
}
public ItemKey setEncryptFileName(boolean flag){
    this.encryptFileName = flag;
    return this;
}
public boolean mustEncryptAddTimeAndTimeToFileName(){
    return encryptAddTimeAndTimeToFileName;
}
public ItemKey setEncryptAddTimeAndTimeToFileName(boolean flag){
    this.encryptAddTimeAndTimeToFileName = flag;
    return this;
}
public boolean mustEncryptStrictMode(){
    return encryptStrictMode;
}
public ItemKey setEncryptStrictMode(boolean flag){
    this.encryptStrictMode = flag;
    return this;
}
public boolean mustDecryptDeleteEncryptedFile(){
    return decryptDeleteEncryptedFile;
}
public ItemKey setDecryptDeleteEncryptedFile(boolean flag){
    this.decryptDeleteEncryptedFile = flag;
    return this;
}
public boolean mustDecryptOverwriteFile(){
    return decryptOverwriteFile;
}
public ItemKey setDecryptOverwriteFile(boolean flag){
    this.decryptOverwriteFile = flag;
    return this;
}
public boolean mustEncryptSigneText(){
    return encryptSignText;
}
public ItemKey setEncryptSignText(boolean flag){
    this.encryptSignText = flag;
    return this;
}
public boolean mustEncryptAddDeepLinkToText(){
    return encryptAddDeeplinkToText;
}
public ItemKey setEncryptAddDeepLinkToText(boolean flag){
    this.encryptAddDeeplinkToText = flag;
    return this;
}
public byte[] getOption(){
    ByteBufferBuilder buffer = ByteBufferBuilder.obtain();
    buffer.put(encryptDeleteOriginalFile);
    buffer.put(encryptOverwriteFile);
    buffer.put(encryptFileName);
    buffer.put(encryptAddTimeAndTimeToFileName);
    buffer.put(encryptStrictMode);
    buffer.put(decryptDeleteEncryptedFile);
    buffer.put(decryptOverwriteFile);
    buffer.put(encryptSignText);
    buffer.put(encryptAddDeeplinkToText);
    return buffer.arrayPacked();
}
public ItemKey setOption(byte[] option){
    ByteBuffer buffer = ByteBuffer.wrapPacked(option);
    encryptDeleteOriginalFile = buffer.getBoolean();
    encryptOverwriteFile = buffer.getBoolean();
    encryptFileName = buffer.getBoolean();
    encryptAddTimeAndTimeToFileName = buffer.getBoolean();
    encryptStrictMode = buffer.getBoolean();
    decryptDeleteEncryptedFile = buffer.getBoolean();
    decryptOverwriteFile = buffer.getBoolean();
    encryptSignText = buffer.getBoolean();
    encryptAddDeeplinkToText = buffer.getBoolean();
    return this;
}
@Override
public ItemKey clear(){
    super.clear();
    this.alias = null;
    this.guid = null;
    this.isOwner = null;
    this.keyRingUid = null;
    this.createdDate = null;
    this.createdZoneId = null;
    this.hasPassword = null;
    this.encryptDeleteOriginalFile = false;
    this.encryptOverwriteFile = false;
    this.encryptFileName = false;
    this.encryptAddTimeAndTimeToFileName = false;
    this.encryptStrictMode = false;
    this.decryptDeleteEncryptedFile = false;
    this.decryptOverwriteFile = false;
    this.encryptSignText = true;
    this.encryptAddDeeplinkToText = true;
    return this;
}

@Override
public ItemKey newItem(){
    return ItemKey.obtain();
}

@Override
public ItemKey copy(){
    ItemKey copy = super.copy();
    copy.alias = alias;
    copy.guid = guid;
    copy.isOwner = isOwner;
    copy.keyRingUid = keyRingUid;
    copy.createdDate = createdDate;
    copy.createdZoneId = createdZoneId;
    copy.hasPassword = hasPassword;
    copy.encryptDeleteOriginalFile = encryptDeleteOriginalFile;
    copy.encryptOverwriteFile = encryptOverwriteFile;
    copy.encryptFileName = encryptFileName;
    copy.encryptAddTimeAndTimeToFileName = encryptAddTimeAndTimeToFileName;
    copy.encryptStrictMode = encryptStrictMode;
    copy.decryptDeleteEncryptedFile = decryptDeleteEncryptedFile;
    copy.decryptOverwriteFile = decryptOverwriteFile;
    copy.encryptSignText = encryptSignText;
    copy.encryptAddDeeplinkToText = encryptAddDeeplinkToText;
    return copy;
}

@Override
protected void fromParcel(ParcelW parcel){
    super.fromParcel(parcel);
    this.alias = parcel.readString();
    this.guid = UUID.fromBytes(parcel.readBytes());
    this.isOwner = parcel.readBoolean();
    setKeyRingUid(UUID.fromBytes(parcel.readBytes()));
    setCreatedDate(parcel.readLong(), Clock.ZoneId(parcel.readString()));
    this.hasPassword = parcel.readBoolean();
    setOption(parcel.readBytes());
}

@Override
protected void toParcel(Parcel parcel){
    super.toParcel(parcel);
    parcel.writeValue(alias);
    parcel.writeValue(guid.toBytes());
    parcel.writeValue(isOwner);
    parcel.writeValue(keyRingUid.toBytes());
    if(createdDate == null){
        parcel.writeValue(null);
        parcel.writeValue(null);
    } else {
        parcel.writeValue(createdDate);
        parcel.writeValue(createdZoneId.getId());
    }
    parcel.writeValue(hasPassword);
    parcel.writeValue(getOption());
}

public byte[] toBytes(boolean withOwnership){
    ByteBufferBuilder buffer = ByteBufferBuilder.obtain();
    buffer.put(getUid()!=null? getUid().toBytes():null);
    buffer.put(alias);
    buffer.put(guid.toBytes());
    buffer.put(withOwnership ? isOwner : false);
    buffer.put(keyRingUid!=null?keyRingUid.toBytes():null);
    buffer.put(createdDate);
    buffer.put(createdZoneId.getId());
    buffer.put(withOwnership ? hasPassword : false);
    buffer.put(getOption());
    return buffer.array();
}
public ItemKey fromBytes(byte[] bytes){
    ByteBuffer buffer = ByteBuffer.wrap(bytes);
    setUid(UUID.fromBytes(buffer.getBytes()));
    alias = buffer.getString();
    guid = UUID.fromBytes(buffer.getBytes());
    isOwner = buffer.getBoolean();
    keyRingUid = UUID.fromBytes(buffer.getBytes());
    setCreatedDate(buffer.getLong(), Clock.ZoneId(buffer.getString()));
    hasPassword = buffer.getBoolean();
    setOption(buffer.getBytes());
    return this;
}

@Override
public boolean equals(Object obj){
    if(obj instanceof ItemKey){
        boolean isEqual = super.equals(obj);
        if(!isEqual){
            return false;
        }
        ItemKey second = (ItemKey)obj;
        isEqual = (Compare.equals(this.alias, second.alias));
        isEqual &= (Compare.equals(this.isOwner, second.isOwner));
        isEqual &= (Compare.equals(this.guid, second.guid));
        isEqual &= (Compare.equals(this.keyRingUid, second.keyRingUid));
        isEqual &= (Compare.equals(this.createdDate, second.createdDate));
        isEqual &= (Compare.equals(this.createdZoneId, second.createdZoneId));
        isEqual &= (Compare.equals(this.hasPassword, second.hasPassword));
        isEqual &= (Compare.equals(this.encryptDeleteOriginalFile, second.encryptDeleteOriginalFile));
        isEqual &= (Compare.equals(this.encryptOverwriteFile, second.encryptOverwriteFile));
        isEqual &= (Compare.equals(this.encryptFileName, second.encryptFileName));
        isEqual &= (Compare.equals(this.encryptAddTimeAndTimeToFileName, second.encryptAddTimeAndTimeToFileName));
        isEqual &= (Compare.equals(this.encryptStrictMode, second.encryptStrictMode));
        isEqual &= (Compare.equals(this.decryptDeleteEncryptedFile, second.decryptDeleteEncryptedFile));
        isEqual &= (Compare.equals(this.decryptOverwriteFile, second.decryptOverwriteFile));
        isEqual &= (Compare.equals(this.encryptSignText, second.encryptSignText));
        isEqual &= (Compare.equals(this.encryptAddDeeplinkToText, second.encryptAddDeeplinkToText));
        return isEqual;
    }
    return super.equals(obj);
}
@Override
public DebugString toDebugString(){
    DebugString data = super.toDebugString();
    data.append("alias", alias);
    data.append("isOwner", isOwner);
    data.append("keyRingUid", keyRingUid);
    data.append("createdDate", getCreatedDateString());
    data.append("createdZoneId", createdZoneId);
    data.append("hasPassword", hasPassword);
    data.append("encryptDeleteOriginalFile", encryptDeleteOriginalFile);
    data.append("encryptOverwriteFile", encryptOverwriteFile);
    data.append("encryptFileName", encryptFileName);
    data.append("encryptAddTimeAndTimeToFileName", encryptAddTimeAndTimeToFileName);
    data.append("encryptStrictMode", encryptStrictMode);
    data.append("decryptDeleteEncryptedFile", decryptDeleteEncryptedFile);
    data.append("decryptOverwriteFile", decryptOverwriteFile);
    data.append("encryptSignText", encryptSignText);
    data.append("encryptAddDeeplinkToText", encryptAddDeeplinkToText);
    return data;
}


}














