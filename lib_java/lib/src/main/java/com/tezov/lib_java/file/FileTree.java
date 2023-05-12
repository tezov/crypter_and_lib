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
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.util.UtilsString;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.toolbox.Nullify;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java.debug.DebugTrack;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileTree{
private Directory directory;
private boolean recursive;
private List<String> directoryLinks = null;
private List<String> fileLinks = null;
private Pattern patternPath = null;
private Pattern patternFileName = null;

public FileTree(){
    this((Directory)null, false);
}
public FileTree(StoragePackage.Type storageType, boolean recursive){
    this(new Directory(storageType), recursive);
}
public FileTree(Directory directory, boolean recursive){
DebugTrack.start().create(this).end();
    this.directory = directory;
    this.recursive = recursive;
}
private void copyPattern(FileTree fileTreeSource){
    patternPath = fileTreeSource.patternPath;
    patternFileName = fileTreeSource.patternFileName;
}
private boolean fileMatches(String fileName){
    if(patternFileName != null){
        Matcher matcher = patternFileName.matcher(fileName);
        return matcher.matches();
    } else {
        return true;
    }
}
private boolean pathMatches(String directoryPath){
    if(patternPath != null){
        Matcher matcher = patternPath.matcher(directoryPath);
        return matcher.matches();
    } else {
        return true;
    }
}
private void buildDirectory(){
    fileLinks = null;
    directoryLinks = null;
    if(directory == null){
        return;
    }
    String baseStorage = directory.getStorage() != null ? directory.getStorage().name() : null;
    String basePath = directory.getRelativePathString();
    boolean basePathMatches;
    if(basePath == null){
        basePath = "";
        basePathMatches = true;
    } else {
        basePathMatches = pathMatches(basePath);
    }
    String baseLink = directory.toLinkString();
    String[] fileNames = directory.getFile().list();
    if(fileNames != null){
        fileLinks = new ArrayList<>();
        directoryLinks = new ArrayList<>();
        for(String name: fileNames){
            java.io.File file = new java.io.File(directory.getFile(), name);
            if(file.isFile()){
                if(basePathMatches && fileMatches(name)){
                    fileLinks.add(File.toLinkString(baseLink, name));
                }
            } else if(file.isDirectory()){
                String directoryPath = basePath + name + Directory.PATH_SEPARATOR;
                if(pathMatches(directoryPath)){
                    String link = Directory.toLinkString(baseStorage, directoryPath);
                    directoryLinks.add(link);
                }
            } else {
DebugException.start().unknown("type", file).end();
            }

        }
    }
    fileLinks = Nullify.collection(fileLinks);
    directoryLinks = Nullify.collection(directoryLinks);
}
public FileTree build(){
    buildDirectory();
    if(recursive && (directoryLinks != null)){
        List<String> fileLinksAll = new ArrayList<>();
        List<String> directoryLinksAll = new ArrayList<>();
        for(String directoryLink: directoryLinks){
            Directory directory = Directory.from(directoryLink);
            FileTree fileTree = new FileTree(directory, true);
            fileTree.copyPattern(this);
            fileTree.build();
            if(fileTree.fileLinks != null){
                fileLinksAll.addAll(fileTree.fileLinks);
            }
            if(fileTree.directoryLinks != null){
                directoryLinksAll.addAll(fileTree.directoryLinks);
            }
        }
        if(!fileLinksAll.isEmpty()){
            if(fileLinks == null){
                fileLinks = fileLinksAll;
            } else {
                fileLinks.addAll(fileLinksAll);
            }
        }
        if(!directoryLinksAll.isEmpty()){
            directoryLinks.addAll(directoryLinksAll);
        }
    }
    return this;
}
public boolean hasDirectoryLinks(){
    return directoryLinks != null;
}
public List<String> getDirectoryLinks(){
    return directoryLinks;
}
public boolean hasFileLinks(){
    return fileLinks != null;
}
public List<String> getFileLinks(){
    return fileLinks;
}

public FileTree setDirectory(StoragePackage.Type storage, StoragePackage.Path.Is path){
    this.directory = new Directory(storage, path);
    return this;
}
public FileTree setDirectory(StoragePackage.Type storage){
    this.directory = new Directory(storage);
    return this;
}
public FileTree setDirectory(Directory directory){
    this.directory = directory;
    return this;
}
public FileTree setRecursive(boolean flag){
    this.recursive = flag;
    return this;
}

public FileTree setPatternPath(String pattern){
    if(pattern != null){
        this.patternPath = Pattern.compile(pattern);
    } else {
        this.patternPath = null;
    }
    return this;
}
public FileTree setPatternFileName(String pattern){
    if(pattern != null){
        this.patternFileName = Pattern.compile(pattern);
    } else {
        this.patternFileName = null;
    }
    return this;
}

public DebugString toDebugString(){
    DebugString data = new DebugString();
    data.append("directory", directory);
    data.append("recursive", recursive);
    data.append("patternPath", patternPath);
    data.append("patternFileName", patternFileName);
    return data;
}
public DebugString toDebugStringDirectoryLinks(){
    DebugString data = new DebugString();
    data.append("directoryLinks", directoryLinks);
    return data;
}
public DebugString toDebugStringFileLinks(){
    DebugString data = new DebugString();
    data.append("fileLinks", fileLinks);
    return data;
}
final public void toDebugLog(){
DebugLog.start().send(toDebugString()).end();
}
final public void toDebugLogDirectoryLinks(){
DebugLog.start().send(toDebugStringDirectoryLinks()).end();
}
final public void toDebugLogFileLinks(){
DebugLog.start().send(toDebugStringFileLinks()).end();
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

}
