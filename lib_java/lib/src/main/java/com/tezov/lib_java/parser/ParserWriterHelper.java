/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.parser;

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.util.UtilsString;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java.file.Directory;
import com.tezov.lib_java.file.File;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugTrack;

import java.io.FileWriter;
import java.io.IOException;

import static com.tezov.lib_java.file.StoragePackage.Path.TEMP;
import static com.tezov.lib_java.file.StoragePackage.Type.PRIVATE_DATA_CACHE;

public abstract class ParserWriterHelper<T>{
private static final String DIRECTORY_PATH = "parser_writer" + Directory.PATH_SEPARATOR;

private final ParserAdapter<T> adapter;
private defParserWriter writer = null;
private File file = null;
public ParserWriterHelper(ParserAdapter<T> adapter){
DebugTrack.start().create(this).end();
    this.adapter = adapter;
}

protected ParserWriterHelper me(){
    return this;
}

public File getFile(){
    return file;
}
public ParserWriterHelper<T> setFile(File file){
    this.file = file;
    return this;
}
public defParserWriter getWriter(){
    return writer;
}
public defParserWriter getWriterCopy() throws IOException{
    return writer.copy();
}

public ParserWriterHelper<T> open() throws IOException{
    return open(adapter.getName(), true);
}
public ParserWriterHelper<T> open(String fileName, boolean appendExtension) throws IOException{
    return open(new Directory(PRIVATE_DATA_CACHE, TEMP, DIRECTORY_PATH), fileName, appendExtension);
}
public ParserWriterHelper<T> open(Directory directory) throws IOException{
    return open(directory, adapter.getName(), true);
}
public ParserWriterHelper<T> open(Directory directory, String fileName, boolean appendExtension) throws IOException{
    File file = new File(directory).setName(fileName);
    if(appendExtension){
        file.setExtension(adapter.fileExtension());
    }
    return open(file);
}
protected abstract defParserWriter newWriter(FileWriter writer) throws IOException;
public ParserWriterHelper<T> open(File file) throws IOException{
    this.file = file;
    return open(newWriter(file.getWriter()));
}
public ParserWriterHelper<T> open(defParserWriter writer) throws IOException{
    this.writer = writer;
    return this;
}
public abstract ParserWriterHelper<T> startDocument(defParserWriter writer, String adapterName) throws IOException;
public ParserWriterHelper<T> startDocument(String adapterName) throws IOException{
    return startDocument(getWriter(), adapterName);
}
public ParserWriterHelper<T> startDocument() throws IOException{
    return startDocument(getWriter(), adapter.getName());
}
public abstract void startSheet(defParserWriter writer, String adapterName) throws IOException;
public void startSheet(String adapterName) throws IOException{
    startSheet(getWriter(), adapterName);
}
public void startSheet() throws IOException{
    startSheet(getWriter(), adapter.getName());
}
public ParserWriterHelper<T> write(T item) throws IOException{
    adapter.write(writer, item);
    return this;
}

public abstract ParserWriterHelper<T> endSheet(defParserWriter writer, String adapterName) throws IOException;
public ParserWriterHelper<T> endSheet(String adapterName) throws IOException{
    return endSheet(getWriter(), adapterName);
}
public ParserWriterHelper<T> endSheet() throws IOException{
    return endSheet(getWriter(), adapter.getName());
}
public abstract ParserWriterHelper<T> endDocument(defParserWriter writer, String adapterName) throws IOException;
public ParserWriterHelper<T> endDocument(String adapterName) throws IOException{
    return endDocument(getWriter(), adapterName);
}
public ParserWriterHelper<T> endDocument() throws IOException{
    return endDocument(getWriter(), adapter.getName());
}
public void close() throws IOException{
    if(writer.canBeClosed()){
        writer.close();
        writer = null;
    }
}
public boolean isClosed(){
    return writer == null;
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();

    if(!isClosed()){
DebugException.start().log("finalized but writer was not closed " + adapter.getName()).end();
    }

    super.finalize();
}


}
