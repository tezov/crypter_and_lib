/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.database.prebuild.file.item;

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
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import androidx.fragment.app.Fragment;

import android.os.Parcel;

import com.tezov.lib_java_android.application.AppDisplay;
import com.tezov.lib_java_android.database.ItemBase;
import com.tezov.lib_java_android.definition.defCreatable;
import com.tezov.lib_java_android.factory.FactoryObject;
import com.tezov.lib_java.file.Directory;
import com.tezov.lib_java.file.File;
import com.tezov.lib_java.generator.uid.UUIDGenerator;
import com.tezov.lib_java.toolbox.Compare;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java_android.type.image.imageHolder.ImageBitmap;
import com.tezov.lib_java_android.type.image.imageHolder.ImageFormat;
import com.tezov.lib_java.type.primaire.Size;
import com.tezov.lib_java_android.type.android.wrapper.ParcelW;

public class ItemFile extends ItemBase<ItemFile>{
public static final Creator<ItemFile> CREATOR = new Creator<ItemFile>(){
    @Override
    public ItemFile createFromParcel(Parcel p){
        ParcelW parcel = ParcelW.obtain().replace(p);
        return ItemFile.obtain().replaceBy(parcel);
    }

    @Override
    public ItemFile[] newArray(int size){
        return new ItemFile[size];
    }
};
final private static UUIDGenerator UUID_GENERATOR = UUIDGenerator.newInstance();

static{
    FactoryObject.init(ItemFile.class);
}

public File file;
public File tempFile;
private int tempSize;

public static UUIDGenerator getUidGenerator(){
    return UUID_GENERATOR;
}

public static defCreatable<ItemFile> getFactory(){
    return FactoryObject.singleton(ItemFile.class);
}

public static void factoryRelease(){
    FactoryObject.singletonRelease(ItemFile.class);
}

public static ItemFile obtain(){
    return getFactory().create();
}

public ItemFile setFile(File data){
    this.file = data;
    if(exist()){
        tempSize = 0;
    }
    return this;
}

public boolean isTempFile(){
    return tempFile != null;
}
public ItemFile setTempFile(File file){
    return setTempFile(file, false, false);
}
public boolean exist(){
    return file != null && file.exists();
}
public int size(){
    return exist() ? file.getLength() : tempSize;
}
public ItemFile setTempFile(File file, boolean moveToFile, boolean overwrite){
    tempFile = file;
    if(moveToFile){
        moveTempFileToFile(overwrite);
    }
    return this;
}

public boolean moveTempFileToFile(boolean overwrite){
    File file = this.tempFile.moveTo(this.file, overwrite);
    if(file != null){
        this.file = file;
        tempFile = null;
        tempSize = 0;
        return true;
    } else {
        return false;
    }
}

@Override
public ItemFile clear(){
    super.clear();
    setFile(null);
    setTempFile(null, false, false);
    tempSize = 0;
    return this;
}

@Override
public ItemFile newItem(){
    return ItemFile.obtain();
}

@Override
public ItemFile copy(){
    ItemFile copy = super.copy();
    copy.setFile(file != null ? File.from(file.toLinkString()) : null);
    copy.setTempFile(tempFile != null ? File.from(tempFile.toLinkString()) : null);
    copy.tempSize = tempSize;
    return this;
}

@Override
protected void fromParcel(ParcelW parcel){
    super.fromParcel(parcel);
    byte[] directoryBytes = parcel.readBytes();
    if(directoryBytes != null){
        String name = parcel.readString();
        String extension = parcel.readString();
        int size = parcel.readInteger();
        file = new File(Directory.from(directoryBytes), name, extension);
        tempFile = null;
        if(!file.exists()){
            tempSize = size;
        } else {
            tempSize = 0;
        }
    } else {
        parcel.readDummy();  // dummyRead of name
        parcel.readDummy();  // dummyRead of extension
        parcel.readDummy(); // dummyRead of size
        file = null;
        tempFile = null;
        tempSize = 0;
    }
}

@Override
protected void toParcel(Parcel parcel){
    super.toParcel(parcel);
    if(file != null){
        parcel.writeValue(file.getDirectory().toBytes());
        parcel.writeValue(file.getName());
        parcel.writeValue(file.getExtension());
        parcel.writeValue(file.getLength());
    } else {
        parcel.writeValue(null);
        parcel.writeValue(null);
        parcel.writeValue(null);
        parcel.writeValue(null);
    }

}

public boolean moveFileToDirectory(Directory directory, boolean overwrite){
    File file = this.file.moveTo(directory, overwrite);
    if(file != null){
        this.file = file;
        tempFile = null;
        return true;
    } else {
        return false;
    }
}

public ItemFile replaceWithDummy(Directory directory, ImageFormat format){
    clear();
    setUid(getUidGenerator().next());
    file = new File(directory);
    Size size = AppDisplay.getSizeOriented();
    ImageBitmap.random(size.getWidth(), size.getHeight()).to(format).save(file);
    return this;
}

@Override
public boolean equals(Object obj){
    if(obj instanceof ItemFile){
        boolean isEqual = super.equals(obj);
        if(!isEqual){
            return false;
        }
        ItemFile second = (ItemFile)obj;
        isEqual = (Compare.equals(this.file, second.file));
        return isEqual;
    }
    return super.equals(obj);
}

@Override
public DebugString toDebugString(){
    DebugString data = super.toDebugString();
    data.append("com/tezov/lib/file", file);
    if(tempFile != null){
        data.append("tempFile", tempFile);
    }
    if(!exist()){
        data.append("tempSize", tempSize);
    }
    return data;
}

@Override
protected void finalize() throws Throwable{
    if(tempFile != null){
        tempFile.delete();
    }
    super.finalize();
}


}














