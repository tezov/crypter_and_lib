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
import com.tezov.lib_java.toolbox.Clock;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java.toolbox.Compare;

import com.tezov.lib_java.type.primaire.Pair;
import com.tezov.lib_java.type.primitive.string.StringBase49To;
import com.tezov.lib_java.type.primitive.string.StringCharTo;

import com.tezov.lib_java.buffer.ByteBuffer;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.util.UtilsString;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;

import static com.tezov.lib_java.file.Directory.LINK_SEPARATOR;
import static com.tezov.lib_java.file.Directory.LINK_VALUE_NULL;

public class File{
public final static int GENERATED_NAME_LENGTH = 16;
public final static String LINK_DIRECTORY = "@";
public final static String DOT_SEPARATOR = ".";

private Directory directory;
private FileW file = null;
private String name;
private String extension;
private boolean appendDateAndTime = false;

public File(){
    this((Directory)null, null, null);
}
public File(StoragePackage.Type storage){
    this(new Directory(storage));
}
public File(StoragePackage.Type storage, StoragePackage.Path.Is path){
    this(new Directory(storage, path));
}
public File(Directory directory){
    this(directory, null, null);
    appendDateAndTime = true;
}
public File(StoragePackage.Type storage, StoragePackage.Path.Is path, String fileName){
    this(new Directory(storage, path), fileName);
}
//public StorageFile(UriW uri) throws IOException{
//    this(uri.getPath());
//}
public File(java.io.File file) throws IOException{
    this(file.getPath());
}
public File(String path) throws IOException{
    this((Directory)null, null, null);
    Pair<String, String> pathAndFullFileName = UtilsFile.splitToPathAndFileName(path);
    if(pathAndFullFileName.first == null){
        throw new IOException();
    }
    directory = new Directory(pathAndFullFileName.first);
    if(pathAndFullFileName.second != null){
        Pair<String, String> fileNameAndExtension = UtilsFile.splitToNameAndExtension(pathAndFullFileName.second);
        name = fileNameAndExtension.first;
        extension = fileNameAndExtension.second;
    }
}
public File(Directory directory, String fileName){
    this(directory, null, null);
    Pair<String, String> p = UtilsFile.splitToNameAndExtension(fileName);
    name = p.first;
    extension = p.second;
}
public File(StoragePackage.Type storage, StoragePackage.Path.Is path, String fileName, String fileExtension){
    this(new Directory(storage, path), fileName, fileExtension);
}
public File(Directory directory, String fileName, String fileExtension){
DebugTrack.start().create(this).end();
    this.directory = directory;
    this.name = fileName;
    this.extension = fileExtension;
}

public static File from(byte[] bytes){
    if(bytes == null){
        return null;
    }
    return new File().fromBytes(bytes);
}
public static File from(String link){
    if(link == null){
        return null;
    }
    return new File().fromString(link);
}

protected static File from(Iterator<String> it){
    return new File().fromStringIterator(it);
}

public static String toLinkString(String directoryLink, String fileName){
    Pair<String, String> p = UtilsFile.splitToNameAndExtension(fileName);
    return toLinkString(directoryLink, p.first, p.second);
}
public static String toLinkString(Directory directory, String fileName){
    Pair<String, String> p = UtilsFile.splitToNameAndExtension(fileName);
    return toLinkString(directory, p.first, p.second);
}
public static String toLinkString(Directory directory, String name, String extension){
    return toLinkString(directory != null ? directory.toLinkString() : null, name, extension);
}
public static String toLinkString(String directoryLink, String name, String extension){
    StringBuilder data = new StringBuilder();
    if(directoryLink != null){
        data.append(LINK_DIRECTORY).append(LINK_SEPARATOR);
        data.append(directoryLink).append(LINK_SEPARATOR);
    } else {
        data.append(LINK_VALUE_NULL).append(LINK_SEPARATOR);
    }
    data.append(name != null ? StringCharTo.StringBase49(name) : LINK_VALUE_NULL).append(LINK_SEPARATOR);
    data.append(extension != null ? StringCharTo.StringBase49(extension) : LINK_VALUE_NULL);
    return data.toString();
}

public static void consumeIterator(Iterator<String> it){
    fromStringIterator(null, it);
}
public static String getFullName(String link){
    Iterator<String> it = Arrays.asList(link.split(LINK_SEPARATOR)).iterator();
    String name = getName(it);
    String extension = getExtension(it);
    if(extension != null){
        return name + File.DOT_SEPARATOR + extension;
    } else {
        return name;
    }
}
public static String getExtension(String link){
    Iterator<String> it = Arrays.asList(link.split(LINK_SEPARATOR)).iterator();
    getName(it);
    return getExtension(it);
}
public static String getName(String link){
    return getName(Arrays.asList(link.split(LINK_SEPARATOR)).iterator());
}
private static String getName(Iterator<String> it){
    String directoryString = it.next();
    if(directoryString.equals(LINK_DIRECTORY)){
        Directory.consumeIterator(it);
    }
    String name = it.next();
    if(LINK_VALUE_NULL.equals(name)){
        name = null;
    }
    return name;
}
private static String getExtension(Iterator<String> it){
    String extension = it.next();
    if(LINK_VALUE_NULL.equals(extension)){
        extension = null;
    }
    return extension;
}
private static File fromStringIterator(File file, Iterator<String> it){
    if(file == null){
        String directoryString = it.next();
        if(directoryString.equals(LINK_DIRECTORY)){
            Directory.consumeIterator(it);
        }
        it.next();
        it.next();
    } else {
        String directoryString = it.next();
        if(LINK_DIRECTORY.equals(directoryString)){
            file.directory = new Directory().fromStringIterator(it);
        } else {
            file.directory = null;
        }
        file.name = it.next();
        if(LINK_VALUE_NULL.equals(file.name)){
            file.name = null;
        }
        else {
            file.name = StringBase49To.StringChar(file.name);
        }
        file.extension = it.next();
        if(LINK_VALUE_NULL.equals(file.extension)){
            file.extension = null;
        }
        else{
            file.extension = StringBase49To.StringChar(file.extension);
        }
    }
    return file;
}
public boolean canRead(){
    return getFile().canRead();
}
public boolean canWrite(){
    return getFile().canWrite();
}
private String buildFileName(){
    if(name == null){
        name = generateFileName();
    }
    if(appendDateAndTime){
        name = appendDateAndTime(name);
    }
    return getFullName();
}
private String generateFileName(){
    return UtilsString.randomBase49(GENERATED_NAME_LENGTH);
}
private String appendDateAndTime(String name){
    return UtilsString.appendDateAndTime(name);
}
public File appendDateAndTime(boolean flag){
    assertFileNotBuilt();
    appendDateAndTime = flag;
    return this;
}
public boolean isBuilt(){
    return file != null;
}
void assertFileNotBuilt(){

    if(isBuilt()){
DebugException.start().explode("StorageFile is already built").end();
    }

}
protected void build(){
    assertFileNotBuilt();
    file = new FileW(directory.getFile(), buildFileName());
}
public FileW getFile(){
    if(!isBuilt()){
        build();
    }
    return file;
}
void setFile(FileW file){
    this.file = file;
}
public Directory getDirectory(){
    return directory;
}
public File setDirectory(Directory directory){
    assertFileNotBuilt();
    this.directory = directory;
    return this;
}
public String getName(){
    return name;
}
public File setName(String name){
    assertFileNotBuilt();
    this.name = name;
    return this;
}
public String getFullName(){
    if((name == null) && (extension == null)){
        return null;
    }
    if(extension != null){
        return name + DOT_SEPARATOR + extension;
    } else {
        return name;
    }
}
public String getExtension(){
    return extension;
}
public File setExtension(String extension){
    assertFileNotBuilt();
    this.extension = extension;
    return this;
}
public String getPath(){
    return getFile().getPath();
}
public int getLength(){
    if(file == null){
        build();
    }
    return (int)file.length();
}
public boolean exists(){
    if(!isBuilt()){
        build();
    }
    return file.exists();
}
public File moveTo(Directory directory, boolean overWrite){
    return moveTo(new File(directory, name, extension), overWrite);
}
public File moveTo(StoragePackage.Type storage, StoragePackage.Path.Is path, boolean overWrite){
    return moveTo(new File(storage, path, name, extension), overWrite);
}
public File moveTo(File target, boolean overWrite){
    FileW targetFile = target.getFile();
    if(targetFile.exists()){
        if(!overWrite){

DebugException.start().log("Target file already exist").end();

            return null;
        }
        if(!targetFile.delete()){

DebugException.start().log("Target file failed to delete").end();

            return null;
        }
    }
    if(getFile().renameTo(targetFile)){
        return target;
    } else {
        return null;
    }
}

public boolean delete(){
    return getFile().delete();
}
protected ByteBuffer toByteBuffer(){
    ByteBufferBuilder byteBuffer = ByteBufferBuilder.obtain();
    byteBuffer.put(directory != null ? directory.toBytes() : null);
    byteBuffer.put(name);
    byteBuffer.put(extension);
    return byteBuffer;
}
public byte[] toBytes(){
    return toByteBuffer().array();
}
protected void fromByteBuffer(ByteBuffer byteBuffer){
    byte[] directoryBytes = byteBuffer.getBytes();
    if(directoryBytes != null){
        directory = Directory.from(directoryBytes);
    } else {
        directory = null;
    }
    name = byteBuffer.getString();
    extension = byteBuffer.getString();
}
protected File fromBytes(byte[] bytes){
    fromByteBuffer(ByteBuffer.wrap(bytes));
    return this;
}
protected File fromStringIterator(Iterator<String> it){
    return fromStringIterator(this, it);
}
protected File fromString(String link){
    fromStringIterator(Arrays.asList(link.split(LINK_SEPARATOR)).iterator());
    return this;
}
public String toLinkString(){
    return toLinkString(directory, name, extension);
}
public String toLinkPathString(){
    StringBuilder data = new StringBuilder();
    if(directory != null){
        data.append(directory.toLinkPathString());
    }
    String name = getFullName();
    if(name != null){
        data.append(name);
    }
    return data.toString();
}

@Override
public boolean equals(Object obj){
    if(!(obj instanceof File)){
        return false;
    }
    return Compare.equals(toBytes(), ((File)obj).toBytes());
}

public static DebugString toDebugString(java.io.File file){
    DebugString data = new DebugString();
    data.append("exist", file.exists());
    data.append("canRead", file.canRead());
    data.append("canWrite", file.canWrite());
    data.append("name", file.getName());
    data.append("size", file.length());
    data.append("type", UtilsFile.getMimeTypeForFullName(file.getName()));
    data.append("isFile", file.isFile());
    data.append("path", file.getPath());
    return data;
}
public DebugString toDebugString(){
    DebugString data = toDebugString(getFile());
    data.append("link", toLinkString());
    return data;
}
static public void toDebugLog(java.io.File file){
DebugLog.start().send(toDebugString(file)).end();
}
final public void toDebugLog(){
DebugLog.start().send(toDebugString()).end();
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

public FileOutputStream getOutputStream() throws FileNotFoundException{
    return new FileOutputStream(getFile());
}
public FileWriter getWriter() throws IOException{
    return new FileWriter(getFile());
}

public FileInputStream getInputStream() throws FileNotFoundException{
    return new FileInputStream(getFile());
}
public FileReader getReader() throws IOException{
    return new FileReader(getFile());
}


}
