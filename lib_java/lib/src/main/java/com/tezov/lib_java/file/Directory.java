/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.file;

import com.tezov.lib_java.buffer.ByteBufferBuilder;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.util.UtilsString;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java.application.AppRandomNumber;
import com.tezov.lib_java.buffer.ByteBuffer;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.toolbox.Compare;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.primitive.string.StringBase49To;
import com.tezov.lib_java.type.primitive.string.StringCharTo;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

public class Directory{
public final static String PATH_SEPARATOR = File.separator;
public final static String LINK_SEPARATOR = ":";
public final static String LINK_VALUE_NULL = "#";

private FileW file = null;
private StoragePackage.Type storage;
private StoragePackage.Path.Is relativePath;
private String relativePathString;

public Directory(){
    init(null, null, null);
}
public Directory(StoragePackage.Type storage){
    init(storage, null, null);
}
public Directory(StoragePackage.Type storage, String relativePath){
    init(storage, null, relativePath);
}
public Directory(String path){
    init(null, null, path);
}
public Directory(Directory directory, Directory pathDirectory){
    this(directory, pathDirectory.getRelativePathString());
}
public Directory(Directory directory, StoragePackage.Path.Is pathDirectory){
    this(directory, pathDirectory.path());
}
public Directory(Directory directory, String pathDirectory){
    init(directory.storage, null, null);
    String relativePath = directory.getRelativePathString();
    StringBuilder pathString = new StringBuilder();
    if(relativePath != null){
        pathString.append(relativePath);
    }
    pathString.append(pathDirectory);
    setRelativePathString(pathString.toString());
}
public Directory(StoragePackage.Type storage, StoragePackage.Path.Is relativePath){
    init(storage, relativePath, null);
}
public Directory(StoragePackage.Type storage, StoragePackage.Path.Is relativePath, String subPath){
    init(storage, null, relativePath.path() + subPath);
}
public static Directory from(byte[] bytes){
    if(bytes == null){
        return null;
    }
    return new Directory().fromBytes(bytes);
}
public static Directory from(String link){
    if(link == null){
        return null;
    } else {
        return new Directory().fromString(link);
    }
}
protected static Directory from(Iterator<String> it){
    return new Directory().fromStringIterator(it);
}
public static String randomPath(){
    return randomPath(3, 3, "aze", '/');
}
public static String randomPath(int directoryDepthMax, int nameLengthMax){
    return randomPath(directoryDepthMax, nameLengthMax, "aze", '/');
}
public static String randomPath(int directoryDepthMax, int nameLengthMax, String nameLetters, Character separator){
    int directoryDepth = AppRandomNumber.nextInt(directoryDepthMax) + 1;
    StringBuilder directory = new StringBuilder();
    for(int i = 0; i < directoryDepth; i++){
        int nameLength = AppRandomNumber.nextInt(nameLengthMax) + 2;
        StringBuilder name = new StringBuilder();
        for(int j = 0; j < nameLength; j++){
            name.append(nameLetters.charAt(AppRandomNumber.nextInt(nameLetters.length())));
        }
        directory.append(name).append(separator);
    }
    return directory.toString();
}
public static String toLinkString(StoragePackage.Type storage, StoragePackage.Path.Is path){
    return toLinkString(storage.name(), path.name());
}
public static String toLinkString(StoragePackage.Type storage, String path){
    return toLinkString(storage.name(), path);
}
public static String toLinkString(StoragePackage.Type storage){
    return toLinkString(storage.name(), null);
}
public static String toLinkString(String storage, String path){
    StringBuilder data = new StringBuilder();
    data.append(storage != null ? storage : LINK_VALUE_NULL).append(LINK_SEPARATOR);
    data.append(path != null ? StringCharTo.StringBase49(path) : LINK_VALUE_NULL);
    return data.toString();
}
public static void consumeIterator(Iterator<String> it){
    fromStringIterator(null, it);
}
private static Directory fromStringIterator(Directory directory, Iterator<String> it){
    if(directory == null){
        it.next();
        it.next();
    } else {
        String storageString = it.next();
        directory.storage = StoragePackage.Type.valueOf(storageString);
        String pathString = it.next();
        directory.relativePath = null;
        directory.relativePathString = null;
        if((pathString != null) && !LINK_VALUE_NULL.equals(pathString)){
            pathString = StringBase49To.StringChar(pathString);
            directory.relativePath = StoragePackage.Path.find(pathString);
            if(directory.relativePath == null){
                directory.relativePathString = pathString;
            }
        }
    }
    return directory;
}
private void init(StoragePackage.Type storage, StoragePackage.Path.Is path, String pathString){
DebugTrack.start().create(this).end();
    this.storage = storage;
    this.relativePath = path;
    this.relativePathString = pathString;
}
public boolean isBuilt(){
    return file != null;
}
void assertFileNotBuilt(){

    if(isBuilt()){
DebugException.start().explode("StorageFile is already built").end();
    }

}
private void build(boolean create){
    assertFileNotBuilt();
    if(relativePath != null){
        file = new FileW(storage != null ? storage.newFileW() : null, relativePath.path());
    } else if(relativePathString != null){
        file = new FileW(storage != null ? storage.newFileW() : null, relativePathString);
    } else {
        file = storage.newFileW();
    }
    if(create){
        boolean success = file.createDirectory();
        if(!success){
DebugException.start().log("failed to create directory").end();
        }
    }
}
public boolean isDirectory(){
    return getFile().isDirectory();
}
public boolean canWrite(){
    return getFile(false).canWrite();
}

public boolean canRead(){
    return getFile(false).canRead();
}
public FileW getFile(){
    return getFile(true);
}
void setFile(FileW file){
    this.file = file;
}
public FileW getFile(boolean create){
    if(!isBuilt()){
        build(create);
    }
    return file;
}
public StoragePackage.Type getStorage(){
    return storage;
}
public void setStorage(StoragePackage.Type storage){
    assertFileNotBuilt();
    this.storage = storage;
}
public boolean hasRelativePath(){
    return (relativePath != null) || (relativePathString != null);
}
public StoragePackage.Path.Is getRelativePath(){
    return relativePath;
}
public void setRelativePath(StoragePackage.Path.Is relativePath){
    assertFileNotBuilt();
    this.relativePath = relativePath;
    this.relativePathString = null;
}
public String getRelativePathString(){
    return relativePath != null ? relativePath.path() : relativePathString;
}
public void setRelativePathString(String path){
    assertFileNotBuilt();
    this.relativePathString = path;
    this.relativePath = null;
}
public String getPath(){
    return getFile().getPath();
}
public int delete(){
    return deleteFiles(true, true);
}
public int deleteFiles(boolean recursive){
    return deleteFiles(recursive, true);
}
public int deleteFiles(boolean recursive, boolean deleteDirectoryIfEmpty){
    if(!isDirectory()){
        return 0;
    }
    int count = 0;
    FileW file = getFile();
    String[] children = file.list();
    if(children != null){
        for(String childName: children){
            java.io.File child = new java.io.File(file, childName);
            if(child.isDirectory()){
                if(recursive){
                    count += new Directory(this, PATH_SEPARATOR + childName).deleteFiles(true, deleteDirectoryIfEmpty);
                }
            } else if(child.delete()){
                count++;
            }
        }
    }
    if(deleteDirectoryIfEmpty && isEmpty()){
        file.delete();
    }
    return count;
}

public int deleteFiles(long expiredDelay, TimeUnit unit, boolean recursive, boolean deleteDirectoryIfEmpty){
    return deleteFiles(TimeUnit.MILLISECONDS.convert(expiredDelay, unit), recursive, deleteDirectoryIfEmpty);
}
public int deleteFiles(long expiredDelay_ms, boolean recursive, boolean deleteDirectoryIfEmpty){
    return deleteFiles(Clock.MilliSecond.now(), expiredDelay_ms, recursive, deleteDirectoryIfEmpty);
}
private int deleteFiles(long now, long expiredDelay_ms, boolean recursive, boolean deleteDirectoryIfEmpty){
    if(!isDirectory()){
        return 0;
    }
    int count = 0;
    FileW file = getFile();
    String[] children = file.list();
    if(children != null){
        for(String childName: children){
            java.io.File child = new java.io.File(file, childName);
            if(child.isDirectory()){
                if(recursive){
                    count += new Directory(this, PATH_SEPARATOR + childName).deleteFiles(now, expiredDelay_ms, true, deleteDirectoryIfEmpty);
                }
            } else {
                long expired = file.lastModified() + expiredDelay_ms;
                if(expired < now){
                    if(child.delete()){
                        count++;
                    }
                }
            }
        }
    }
    if(deleteDirectoryIfEmpty && isEmpty()){
        file.delete();
    }
    return count;
}

public boolean exists(){
    return getFile(false).exists();

}
public boolean isEmpty(){
    return getFile().list().length == 0;
}
public boolean create(){
    if(!isBuilt()){
        build(true);
        return true;
    } else {
        return file.createDirectory();
    }
}
public String[] list(){
    return getFile().list();
}
public String[] list(FilenameFilter filter){
    return getFile().list(filter);
}
public File[] listFiles(){
    return getFile().listFiles();
}
protected ByteBuffer toByteBuffer(){
    ByteBufferBuilder byteBuffer = ByteBufferBuilder.obtain();
    byteBuffer.put(storage != null ? storage.name() : null);
    if(relativePath != null){
        byteBuffer.put(relativePath.name());
    } else if(relativePathString != null){
        byteBuffer.put(relativePathString);
    } else {
        byteBuffer.put((String)null);
    }
    return byteBuffer;
}
public byte[] toBytes(){
    return toByteBuffer().array();
}
protected void fromByteBuffer(ByteBuffer byteBuffer){
    String storageString = byteBuffer.getString();
    this.storage = StoragePackage.Type.valueOf(storageString);
    String pathString = byteBuffer.getString();
    this.relativePath = null;
    this.relativePathString = null;
    if((pathString != null) && !pathString.equals(LINK_VALUE_NULL)){
        relativePath = StoragePackage.Path.find(pathString);
        if(relativePath == null){
            this.relativePathString = pathString;
        }
    }
}
protected Directory fromBytes(byte[] bytes){
    fromByteBuffer(ByteBuffer.wrap(bytes));
    return this;
}
protected Directory fromStringIterator(Iterator<String> it){
    return fromStringIterator(this, it);
}
protected Directory fromString(String link){
    fromStringIterator(Arrays.asList(link.split(LINK_SEPARATOR)).iterator());
    return this;
}

public String toLinkString(){
    return toLinkString(storage != null ? storage.name() : null, getRelativePathString());
}

public String toLinkPathString(){
    StringBuilder data = new StringBuilder();
    if(storage != null){
        data.append(storage.name().toLowerCase()).append(PATH_SEPARATOR);
    }
    if(relativePath != null){
        data.append(relativePath.name());
    } else if(relativePathString != null){
        data.append(relativePathString);
    }
    return data.toString();
}

@Override
public boolean equals(Object obj){
    if(!(obj instanceof Directory)){
        return false;
    }
    return Compare.equals(toBytes(), ((Directory)obj).toBytes());
}

public DebugString toDebugString(){
    DebugString data = new DebugString();
    data.append("exist", file.exists());
    data.append("canRead", file.canRead());
    data.append("canWrite", file.canWrite());
    data.append("name", file.getName());
    data.append("path", file.getPath());
    data.append("storage", storage);
    if(relativePath != null){
        data.append("relativePath", relativePath);
    }
    if(relativePathString != null){
        data.append("relativePathString", relativePathString);
    }
    data.append("link", toLinkString());
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
