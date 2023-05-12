/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.file;

import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.util.UtilsString;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java.application.AppConfig;
import com.tezov.lib_java.application.AppConfigKey;
import com.tezov.lib_java.debug.DebugLog;

import static com.tezov.lib_java.util.UtilsList.NULL_INDEX;

import com.tezov.lib_java.toolbox.Nullify;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.primaire.Pair;
import com.tezov.lib_java.type.primitive.BytesTo;
import com.tezov.lib_java.type.primitive.LongTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.util.UtilsBytes;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.CRC32;

public class UtilsFile{

public static final int DEFAULT_BUFFER_SIZE =
    (int)UnitByte.o.convert(AppConfig.getFloat(AppConfigKey.TRANSFER_FILE_BUFFER_SIZE_Ko.getId()), UnitByte.Ko);
public final static String MINE_TYPE_ALL = "*/*";
public final static String MINE_TYPE_DIRECTORY = "resource/folder";
public final static String MINE_TYPE_OCTET_STREAM = "application/octet-stream";
public final static String MINE_TYPE_PLAIN_TEXT = "text/plain";

protected UtilsFile(){
}

public static void transfer(java.io.File source, File destination) throws IOException{
    transfer(new FileInputStream(source), destination.getOutputStream());
}
public static void transfer(InputStream source, File destination) throws IOException{
    transfer(source, destination.getOutputStream());
}
public static void transfer(File source, java.io.File destination) throws IOException{
    transfer(source.getInputStream(), new FileOutputStream(destination));
}
public static void transfer(File source, OutputStream destination) throws IOException{
    transfer(source.getInputStream(), destination);
}
public static void transfer(File source, File destination) throws IOException{
    transfer(source.getInputStream(), destination.getOutputStream());
}
public static void transfer(InputStream source, java.io.File destination) throws IOException{
    transfer(source, new FileOutputStream(destination));
}
public static void transfer(java.io.File source, OutputStream destination) throws IOException{
    transfer(new FileInputStream(source), destination);
}
public static void transfer(File source, Directory destination) throws IOException{
    transfer(source.getInputStream(), new File(destination, source.getFullName()).getOutputStream());
}
public static void transfer(java.io.File source, java.io.File destination) throws IOException{
    transfer(new FileInputStream(source), new FileOutputStream(destination));
}
public static void transfer(InputStream source, OutputStream destination) throws IOException{
    try{
        copy(source, destination);
        destination.close();
        source.close();
    } catch(IOException e){

DebugException.start().log(e).end();

        UtilsStream.close(destination);
        UtilsStream.close(source);
        throw e;
    }
}
public static void copy(InputStream source, OutputStream destination) throws IOException{
    copy(source, destination, null, DEFAULT_BUFFER_SIZE);
}
public static void copy(InputStream source, OutputStream destination, Integer available) throws IOException{
    copy(source, destination, available, DEFAULT_BUFFER_SIZE);
}
public static void copy(InputStream source, OutputStream destination, Integer available, int bufferSize) throws IOException{
    if(available == null){
        int length;
        byte[] buffer = UtilsBytes.obtain(bufferSize);
        while((length = source.read(buffer)) > 0){
            destination.write(buffer, 0, length);
        }
    } else {
        int length;
        byte[] buffer = UtilsBytes.obtain(bufferSize);
        while((length = source.read(buffer, 0, Math.min(available, bufferSize))) > 0){
            destination.write(buffer, 0, length);
            available -= length;
        }
    }
}

public static DigesterCRC32 newDigester(){
    return new DigesterCRC32();
}

public static String shortenName(String fileFullName, int maxLength){
    int diff = fileFullName.length() - maxLength;
    String fileNameShorten;
    if(diff > 0){
        fileNameShorten = UtilsFile.truncateName(fileFullName, diff);
    } else {
        fileNameShorten = fileFullName;
    }
    return fileNameShorten;
}
public static String truncateName(String fileFullName, int lengthToRemove){
    if((Nullify.string(fileFullName) != null) && (lengthToRemove > 0) && (lengthToRemove < fileFullName.length())){
        FileName fileName = new FileName(fileFullName);
        fileName.truncateNameOf(lengthToRemove);
        return fileName.getFullName();
    } else {
        return fileFullName;
    }
}

public static String getMimeTypeForFullName(String fileFullName){
    String mimeType = null;
    if(fileFullName != null){
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        mimeType = fileNameMap.getContentTypeFor(fileFullName);
        if(mimeType == null){
            mimeType = MINE_TYPE_OCTET_STREAM;
        }
    }
    return mimeType;
}

public static String getExtension(String fileFullName){
    int dotIndex = fileFullName.lastIndexOf(File.DOT_SEPARATOR);
    if(dotIndex != -1){
        return Nullify.string(fileFullName.substring(dotIndex + 1));
    } else {
        return null;
    }
}
public static String getName(String fileFullName){
    int dotIndex = fileFullName.lastIndexOf(File.DOT_SEPARATOR);
    if(dotIndex != -1){
        return Nullify.string(fileFullName.substring(0, dotIndex));
    } else {
        return fileFullName;
    }
}

public static Pair<String, String> splitToNameAndExtension(String fileFullName){
    if(Nullify.string(fileFullName) == null){
        return null;
    }
    String name;
    String extension;
    int dotIndex = fileFullName.lastIndexOf(File.DOT_SEPARATOR);
    if(dotIndex != -1){
        extension = fileFullName.substring(dotIndex + 1);
        name = fileFullName.substring(0, dotIndex);
    } else {
        extension = null;
        name = fileFullName;
    }
    return new Pair<>(Nullify.string(name), Nullify.string(extension));
}
public static Pair<String, String> splitToPathAndFileName(String fullPath){
    if(Nullify.string(fullPath) == null){
        return null;
    }
    int index = fullPath.lastIndexOf(Directory.PATH_SEPARATOR);
    if(index != NULL_INDEX){
        String path = fullPath.substring(0, index + 1);
        String name = fullPath.substring(index + 1);
        return new Pair<>(Nullify.string(path), Nullify.string(name));
    } else {
        return new Pair<>(null, Nullify.string(fullPath));
    }
}

public static class DigesterCRC32{
    private final CRC32 digester;
    public DigesterCRC32(){
DebugTrack.start().create(this).end();
        this.digester = new CRC32();
    }
    public static int length(){
        return LongTo.BYTES;
    }
    public void update(byte b){
        digester.update(b);
    }
    public void update(byte[] bytes){
        digester.update(bytes, 0, bytes.length);
    }
    public void update(byte[] bytes, int off, int len){
        digester.update(bytes, off, len);
    }
    public long getValue(){
        return digester.getValue();
    }
    public byte[] getValueByte(){
        return LongTo.Bytes(digester.getValue());
    }
    public boolean equals(byte[] value){
        return BytesTo.Long(value) == getValue();
    }

    @Override
    protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
        super.finalize();
    }

}

public static class FileName{
    private final static Pattern pattern = Pattern.compile("^(.*?)(?:\\(([0-9]+)\\))?(?:\\.([^.]*))?$");
    private String name;
    private Integer number = null;
    private final String extension;
    public FileName(String fileFullName){
DebugTrack.start().create(this).end();
        Matcher matcher = pattern.matcher(fileFullName);
        if(matcher.find()){
            name = matcher.group(1);
            String number = matcher.group(2);
            if(number != null){
                this.number = Integer.parseInt(number);
            }
            extension = matcher.group(3);
        } else {
            Pair<String, String> p = splitToNameAndExtension(fileFullName);
            name = p.first;
            extension = p.second;
        }
    }

    public static Integer getNumber(String fileFullName){
        try{
            Pattern pattern = Pattern.compile("^(?:.*?)(?:\\(([0-9]+)\\))?(?:\\.[^.]*)?$");
            Matcher matcher = pattern.matcher(fileFullName);
            if(matcher.find()){
                String number = matcher.group(1);
                if(number != null){
                    return Integer.parseInt(number);
                }
            }
        } catch(Throwable e){
        }
        return null;
    }
    public static String addNumber(String fileFullName, Integer number){
        Pair<String, String> p = splitToNameAndExtension(fileFullName);
        return addNumber(p.first, p.second, number);
    }
    public static String addNumber(String fileName, String extension, Integer number){
        String fileNameWithNumber = fileName;
        if(number != null){
            fileNameWithNumber += "(" + number + ")";
        }
        if(extension != null){
            fileNameWithNumber += File.DOT_SEPARATOR + extension;
        }
        return fileNameWithNumber;
    }

    public String getFullName(){
        return addNumber(name, extension, number);
    }
    public String getFullName(Integer number){
        return addNumber(name, extension, number);
    }
    public String getFullNameWithoutNumber(){
        return addNumber(name, extension, null);
    }

    public String getName(){
        return addNumber(name, null, number);
    }
    public FileName setName(String name){
        this.name = name;
        return this;
    }
    public FileName appendToName(String s){
        this.name = name + s;
        return this;
    }
    public String getShortName(){
        return name;
    }
    public Integer getNumber(){
        return number;
    }
    public void setNumber(Integer number){
        this.number = number;
    }
    public String getExtension(){
        return extension;
    }
    public void incNumber(){
        if(number == null){
            number = 1;
        } else {
            number++;
        }
    }
    public void truncateNameOf(int length){
        if((length > 1) && (name.length() > length)){
            name = name.substring(0, (name.length() - length)) + "~";
        }
    }
    public String getQueryPattern(){
        String pattern = name + "(%)";
        if(extension != null){
            pattern += "." + extension;
        }
        return pattern;
    }

    public DebugString toDebugString(){
        DebugString data = new DebugString();
        data.append("name", name);
        data.append("number", number);
        data.append("extension", extension);
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

}
