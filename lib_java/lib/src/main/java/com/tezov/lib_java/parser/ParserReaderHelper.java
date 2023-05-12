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

import java.io.IOException;

public abstract class ParserReaderHelper<T>{
private final ParserAdapter<T> adapter;
private defParserReader reader = null;
private File file = null;

public ParserReaderHelper(ParserAdapter<T> adapter){
DebugTrack.start().create(this).end();
    this.adapter = adapter;
}
protected ParserReaderHelper<T> me(){
    return this;
}

public File getFile(){
    return file;
}
public ParserReaderHelper<T> setFile(File file){
    this.file = file;
    return this;
}
public defParserReader getReader(){
    return reader;
}
public defParserReader getReaderCopy() throws IOException{
    return reader.copy();
}

public ParserReaderHelper<T> open(Directory directory, String fileName, boolean appendExtension) throws IOException{
    if(appendExtension){
        fileName += adapter.fileExtension();
    }
    return open(new File(directory, fileName));
}
protected abstract defParserReader newReader(java.io.Reader reader) throws IOException;
public ParserReaderHelper<T> open(File file) throws IOException{
    this.file = file;
    return open(newReader(file.getReader()));
}
public ParserReaderHelper<T> open(defParserReader reader) throws IOException{
    this.reader = reader;
    return this;
}
public abstract ParserReaderHelper<T> startDocument(defParserReader reader, String adapterName) throws IOException;
public ParserReaderHelper<T> startDocument(String adapterName) throws IOException{
    return startDocument(getReader(), adapterName);
}
public ParserReaderHelper<T> startDocument() throws IOException{
    return startDocument(getReader(), adapter.getName());
}
public abstract void startSheet(defParserReader reader, String adapterName) throws IOException;
public void startSheet(String adapterName) throws IOException{
    startSheet(getReader(), adapterName);
}
public void startSheet() throws IOException{
    startSheet(getReader(), adapter.getName());
}

public boolean isName() throws IOException{
    return reader.isName();
}
public boolean isBeginObject() throws IOException{
    return reader.isBeginObject();
}
public boolean isNotEndObject() throws IOException{
    return reader.isNotEndObject();
}
public boolean isBeginArray() throws IOException{
    return reader.isBeginArray();
}
public boolean isNotEndArray() throws IOException{
    return reader.isNotEndArray();
}

public T read() throws IOException{
    return adapter.read(reader);
}
public abstract ParserReaderHelper<T> endSheet(defParserReader reader, String adapterName) throws IOException;
public ParserReaderHelper<T> endSheet(String adapterName) throws IOException{
    return endSheet(getReader(), adapterName);
}
public ParserReaderHelper<T> endSheet() throws IOException{
    return endSheet(getReader(), adapter.getName());
}
public abstract ParserReaderHelper<T> endDocument(defParserReader reader, String adapterName) throws IOException;
public ParserReaderHelper<T> endDocument(String adapterName) throws IOException{
    return endDocument(getReader(), adapterName);
}
public ParserReaderHelper<T> endDocument() throws IOException{
    return endDocument(getReader(), adapter.getName());
}
public void close() throws IOException{
    if(reader.canBeClosed()){
        reader.close();
        reader = null;
    }
}
public boolean isClosed(){
    return reader == null;
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();

    if(!isClosed()){
DebugException.start().log("finalized but reader was not closed " + adapter.getName()).end();
    }

    super.finalize();
}

}
