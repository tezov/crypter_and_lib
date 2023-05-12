/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.type.android.wrapper;

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

import android.os.Parcel;
import android.os.Parcelable;

import com.tezov.lib_java.definition.defClearable;
import com.tezov.lib_java_android.definition.defCreatable;
import com.tezov.lib_java_android.factory.FactoryParcel;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.primitive.BytesTo;

//Avoid not portable...
//IMPORTANT must refactor WITH BYTEBUFFER and my own definition both and android and pc.
// Do not use Parcelable anymore, not portable
public class ParcelW implements defClearable<ParcelW>{
private Parcel parcel = null;

protected ParcelW(){
DebugTrack.start().create(this).end();
}

public static String toHexString(Parcel parcel){
    parcel.setDataPosition(0);
    byte[] bytes = parcel.marshall();
    return BytesTo.StringHex(bytes);

}

public static String toCharString(Parcel parcel){
    parcel.setDataPosition(0);
    byte[] bytes = parcel.marshall();
    return BytesTo.StringChar(bytes);
}

public static defCreatable<ParcelW> getFactory(){
    return FactoryParcel.factory();
}

public static void factoryRelease(){
    FactoryParcel.factoryRelease();
}

public static ParcelW obtain(){
    return FactoryParcel.factory().create();
}

public ParcelW replace(Parcel parcel){
    if(this.parcel != null){
        this.parcel.recycle();
    }
    this.parcel = parcel;
    return this;
}

public ParcelW replace(Parcelable parcelable){
    parcelable.writeToParcel(get(), 0);
    return this;
}

public ParcelW replace(byte[] bytes){
    get().unmarshall(bytes, 0, bytes.length);
    return this;
}

public ParcelW replace(byte[] bytes, int offset, int length){
    get().unmarshall(bytes, offset, length);
    return this;
}

public ParcelW resetPosition(){
    get().setDataPosition(0);
    return this;
}

public Parcel get(){
    if(parcel == null){
        parcel = Parcel.obtain();
        parcel.setDataPosition(0);
    }
    return parcel;
}

public <T> T read(Class<T> type){
    return (T)get().readValue(type.getClassLoader());
}

public void readDummy(){
    get().readValue(null);
}

public Long readLong(){
    return read(Long.class);
}

public Boolean readBoolean(){
    return read(Boolean.class);
}

public Float readFloat(){
    return read(Float.class);
}

public Integer readInteger(){
    return read(Integer.class);
}

public String readString(){
    return read(String.class);
}

public byte[] readBytes(){
    return read(byte[].class);
}

public char[] readChars(){
    return read(char[].class);
}

public void write(Object o){
    get().writeValue(o);
}

public byte[] toBytes(){
    get().setDataPosition(0);
    return get().marshall();
}

public String toHexString(){
    return BytesTo.StringHex(toBytes());
}

@Override
public ParcelW clear(){
    if(parcel != null){
        parcel.setDataPosition(0);
    }
    return this;
}

public void recycle(){
    if(parcel != null){
        parcel.recycle();
    }
    parcel = null;
}

final public void toDebugLogHex(){
DebugLog.start().send(toHexString(get())).end();
}

final public void toDebugLogChar(){
DebugLog.start().send(toCharString(get())).end();
}

final public void toDebugLog(){
DebugLog.start().send(toHexString(get())).end();
DebugLog.start().send(toCharString(get())).end();
}

@Override
protected void finalize() throws Throwable{

    if(parcel != null){
DebugException.start().log("Parcel was not recycled").end();
    }

DebugTrack.start().destroy(this).end();
    super.finalize();
}


}
