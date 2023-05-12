/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.crypter.data.item;

import com.tezov.lib_java.buffer.ByteBufferBuilder;
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
import android.os.Parcel;

import com.tezov.crypter.data.misc.ClockFormat;
import com.tezov.crypter.fragment.FragmentCipherFile;
import com.tezov.lib_java.buffer.ByteBuffer;
import com.tezov.lib_java_android.database.ItemBase;
import com.tezov.lib_java_android.definition.defCreatable;
import com.tezov.lib_java_android.factory.FactoryObject;
import com.tezov.lib_java_android.file.UriW;
import com.tezov.lib_java.generator.uid.UUIDGenerator;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.toolbox.Compare;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java_android.type.android.wrapper.ParcelW;

public class ItemHistory extends ItemBase<ItemHistory>{
public static final Creator<ItemHistory> CREATOR = new Creator<ItemHistory>(){
    @Override
    public ItemHistory createFromParcel(Parcel p){
        ParcelW parcel = ParcelW.obtain().replace(p);
        return ItemHistory.obtain().replaceBy(parcel);
    }
    @Override
    public ItemHistory[] newArray(int size){
        return new ItemHistory[size];
    }
};
final private static UUIDGenerator UUID_GENERATOR = UUIDGenerator.newInstance();

static{
    FactoryObject.init(ItemHistory.class);
}

public Type type;
private Long timestamp;
private byte[] data;
public static UUIDGenerator getUidGenerator(){
    return UUID_GENERATOR;
}
public static defCreatable<ItemHistory> getFactory(){
    return FactoryObject.singleton(ItemHistory.class);
}
public static void factoryRelease(){
    FactoryObject.singletonRelease(ItemHistory.class);
}
public static ItemHistory obtain(){
    return getFactory().create();
}

public Type getType(){
    return type;
}
public Long getTimestamp(){
    return timestamp;
}
public byte[] getDataBytes(){
    return data;
}
protected ItemHistory setData(Type type, byte[] data){
    this.type = type;
    this.timestamp = Clock.MilliSecond.now();
    this.data = data;
    return this;
}
public <T extends Data<T>> T getData(){
    if((data == null) || (type == null)){
        return null;
    } else if(type == Type.FILE){
        return (T)new File().clear().fromBytes(timestamp, data);
    } else {
        return null;
    }
}

@Override
public ItemHistory clear(){
    super.clear();
    this.type = null;
    this.data = null;
    return this;
}
@Override
public ItemHistory newItem(){
    return ItemHistory.obtain();
}
@Override
public ItemHistory copy(){
    ItemHistory copy = super.copy();
    copy.type = type;
    copy.data = data != null ? data.clone() : null;
    return copy;
}
@Override
protected void fromParcel(ParcelW parcel){
    super.fromParcel(parcel);
    this.type = Type.valueOf(parcel.readString());
    this.timestamp = parcel.readLong();
    this.data = parcel.readBytes();
}
@Override
protected void toParcel(Parcel parcel){
    super.toParcel(parcel);
    parcel.writeValue(type.name());
    parcel.writeValue(timestamp);
    parcel.writeValue(data);
}
@Override
public boolean equals(Object obj){
    if(obj instanceof ItemHistory){
        boolean isEqual = super.equals(obj);
        if(!isEqual){
            return false;
        }
        ItemHistory second = (ItemHistory)obj;
        isEqual = (Compare.equals(this.type, second.type));
        isEqual &= (Compare.equals(this.data, second.data));
        return isEqual;
    }
    return super.equals(obj);
}
@Override
public DebugString toDebugString(){
    DebugString data = super.toDebugString();
    data.append("type", this.type);
    data.append("data", getData());
    return data;
}
public enum Type{
    FILE
}

public abstract static class Data<T extends Data<T>>{
    public Data(){
DebugTrack.start().create(this).end();
    }
    public abstract T clear();

    public abstract byte[] toBytes();

    protected abstract T fromBytes(Long timestamp, byte[] bytes);

    public abstract Type getType();
    public ItemHistory toItem(){
        return new ItemHistory().clear().setData(getType(), toBytes());
    }
    public DebugString toDebugString(){
        DebugString data = new DebugString();
        data.append("type", getType());
        return data;
    }
    final public void toDebugLog(){
DebugLog.start().send(toDebugString()).end();
    }
    @Override
    protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
        super.finalize();
    }

}

public static class File extends Data<File>{
    Long timestamp;
    FragmentCipherFile.Operation operation;
    FragmentCipherFile.Step result;
    UriW uriIn;
    String uriInPath;
    UriW uriOut;
    String uriOutPath;
    String signatureApp;
    String signatureKey;

    public Long getTimestamp(){
        return timestamp;
    }
    public File setTimestamp(Long timestamp){
        this.timestamp = timestamp;
        return this;
    }

    public FragmentCipherFile.Operation getOperation(){
        return operation;
    }
    public File setOperation(FragmentCipherFile.Operation operation){
        this.operation = operation;
        return this;
    }
    public FragmentCipherFile.Step getResult(){
        return result;
    }
    public File setResult(FragmentCipherFile.Step result){
        this.result = result;
        return this;
    }

    public UriW getUriIn(){
        return uriIn;
    }
    public String getUriInPath(){
        return uriInPath;
    }
    public File setUriIn(UriW uri, boolean saveUriLink){
        if(saveUriLink){
            this.uriIn = uri;
        } else {
            this.uriIn = null;
        }
        if(uri == null){
            this.uriInPath = null;
        } else {
            this.uriInPath = uri.getDisplayPath();
        }
        return this;
    }

    public UriW getUriOut(){
        return uriOut;
    }
    public File setUriOut(UriW uri){
        this.uriOut = uri;
        if(uri == null){
            this.uriOutPath = null;
        } else {
            this.uriOutPath = uri.getDisplayPath();
        }
        return this;
    }
    public String getUriOutPath(){
        return uriOutPath;
    }
    public String getSignatureApp(){
        return signatureApp;
    }
    public File setSignatureApp(String appSignature){
        this.signatureApp = appSignature;
        return this;
    }
    public String getSignatureKey(){
        return signatureKey;
    }
    public File setSignatureKey(String keySignature){
        this.signatureKey = keySignature;
        return this;
    }

    @Override
    public File clear(){
        timestamp = null;
        operation = null;
        result = null;
        uriIn = null;
        uriInPath = null;
        uriOut = null;
        uriOutPath = null;
        signatureApp = null;
        signatureKey = null;
        return this;
    }
    @Override
    public byte[] toBytes(){
        ByteBufferBuilder buffer = ByteBufferBuilder.obtain();
        buffer.put(timestamp);
        buffer.put(operation != null ? operation.name() : null);
        buffer.put(result != null ? result.name() : null);
        buffer.put(uriIn != null ? uriIn.toLink() : null);
        buffer.put(uriInPath);
        buffer.put(uriOut != null ? uriOut.toLink() : null);
        buffer.put(uriOutPath);
        buffer.put(signatureApp);
        buffer.put(signatureKey);
        return buffer.arrayPacked();
    }
    @Override
    public File fromBytes(Long timestamp, byte[] bytes){
        ByteBuffer buffer = ByteBuffer.wrapPacked(bytes);
        timestamp = buffer.getLong();
        String operationString = buffer.getString();
        if(operationString != null){
            operation = FragmentCipherFile.Operation.valueOf(operationString);
        }
        String resultString = buffer.getString();
        if(resultString != null){
            result = FragmentCipherFile.Step.valueOf(resultString);
        }
        String uriInLink = buffer.getString();
        if(uriInLink != null){
            uriIn = UriW.fromLink(uriInLink);
        }
        uriInPath = buffer.getString();
        String uriOutLink = buffer.getString();
        if(uriOutLink != null){
            uriOut = UriW.fromLink(uriOutLink);
        }
        uriOutPath = buffer.getString();
        signatureApp = buffer.getString();
        signatureKey = buffer.getString();
        this.timestamp = timestamp;
        return this;
    }
    @Override
    public Type getType(){
        return Type.FILE;
    }

    @Override
    public DebugString toDebugString(){
        DebugString data = super.toDebugString();
        data.append("timestamp", ClockFormat.longToDateTime_FULL(timestamp));
        data.append("operation", operation);
        data.append("result", result);
        data.append("uriInLink", uriIn != null ? uriIn.toLink() : null);
        data.append("uriInPath", uriInPath);
        data.append("uriOutLink", uriOut != null ? uriOut.toLink() : null);
        data.append("uriOutPath", uriOutPath);
        data.append("appSignature", signatureApp);
        data.append("keySignature", signatureApp);
        return data;
    }

}

}














