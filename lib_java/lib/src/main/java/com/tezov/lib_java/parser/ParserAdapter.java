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
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java.file.Directory;
import com.tezov.lib_java.file.File;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.collection.ListEntry;
import com.tezov.lib_java.type.primaire.Entry;
import com.tezov.lib_java.type.primaire.Pair;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.util.UtilsString;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class ParserAdapter<T>{
protected ParserReaderHelper<T> readerHelper = null;
protected ParserWriterHelper<T> writerHelper = null;
private String name;

public ParserAdapter(){
    this(null);
}
public ParserAdapter(String name){
DebugTrack.start().create(this).end();
    this.name = name;
}
private ParserAdapter<T> me(){
    return this;
}
public String getName(){
    return name;
}
public ParserAdapter<T> setName(String name){
    this.name = name;
    return this;
}
public abstract String fileExtension();

protected abstract ParserReaderHelper<T> newReaderHelper();
public ParserReaderHelper<T> newReader(){
    if((readerHelper == null) || (readerHelper.isClosed())){
        readerHelper = newReaderHelper();
    }
    return readerHelper;
}
public ParserReaderHelper<T> readerHelper(){
    return readerHelper;
}

public T read(defParserReader reader) throws IOException{
    if(reader.isBeginObject()){
        ListEntry<String, Object> datas = new ListEntry<>();
        reader.beginObject();
        while(reader.isNotEndObject()){
            datas.add(reader.nextName(), readValue(reader));
        }
        reader.endObject();
        return (T)datas;
    }
    else if(isName()){
        return (T)new Pair<>(reader.nextName(), readValue(reader));
    }
    else{
        return null;
    }
}
private Object readValue(defParserReader reader) throws IOException{
    Object value;
    defParserReader.Token token = reader.peek();
    switch(token){
        case STRING:{
            value = reader.nextString();
        }
        break;
        case NUMBER:{
            String s = reader.nextString();
            if(s.contains(UtilsString.NUMBER_SEPARATOR)){
                value = Double.parseDouble(s);
            } else if(s.length() < IntTo.MAX_DIGIT_POSITIVE){
                value = Integer.parseInt(s);
            } else {
                value = Long.parseLong(s);
            }
        }
        break;
        case BOOLEAN:{
            value = reader.nextBoolean();
        }
        break;
        case BEGIN_OBJECT:{
            value = read(reader);
        }
        break;
        case BEGIN_ARRAY:{
            List<Object> list = new ArrayList<>();
            reader.beginArray();
            while(reader.isNotEndArray()){
                list.add(readValue(reader));
            }
            reader.endArray();
            value = list;
        }
        break;
        case NULL:{
            reader.nextNull();
            value = null;
        }
        break;
        default:{

DebugException.start().unknown("token", token.name()).end();

            value = null;
        }
    }
    return value;
}

public ParserAdapter<T> openReader() throws IOException{
    newReader().open(writerHelper().getFile());
    return this;
}
public ParserAdapter<T> openReader(Directory directory, String fileName, boolean appendExtension) throws IOException{
    newReader().open(directory, fileName, appendExtension);
    return this;
}
public ParserAdapter<T> openReader(File file) throws IOException{
    newReader().open(file);
    return this;
}
public ParserAdapter<T> openReader(defParserReader reader) throws IOException{
    newReader().open(reader);
    return this;
}
public ParserAdapter<T> startReaderDocument() throws IOException{
    readerHelper().startDocument();
    return this;
}
public void startReaderSheet() throws IOException{
    readerHelper().startSheet();
}
public boolean isName() throws IOException{
    return readerHelper.isName();
}
public boolean isNotEndObject() throws IOException{
    return readerHelper.isNotEndObject();
}
public boolean isNotEndArray() throws IOException{
    return readerHelper.isNotEndArray();
}
public T read() throws IOException{
    return readerHelper().read();
}
public ParserAdapter<T> endReaderSheet() throws IOException{
    readerHelper().endSheet();
    return this;
}
public ParserAdapter<T> endReaderDocument() throws IOException{
    readerHelper().endDocument();
    return this;
}
public void closeReader() throws IOException{
    if(!isReaderClosed()){
        readerHelper().close();
    }
}
public boolean isReaderClosed(){
    return (readerHelper == null) || readerHelper.isClosed();
}

protected abstract ParserWriterHelper<T> newWriterHelper();
public ParserWriterHelper<T> newWriter(){
    if((writerHelper == null) || (writerHelper.isClosed())){
        writerHelper = newWriterHelper();
    }
    return writerHelper;
}
public ParserWriterHelper<T> writerHelper(){
    return writerHelper;
}

public ParserAdapter<T> openWriter() throws IOException{
    newWriter().open();
    return this;
}
public ParserAdapter<T> openWriter(String fileName, boolean appendExtension) throws IOException{
    newWriter().open(fileName, appendExtension);
    return this;
}
public ParserAdapter<T> openWriter(Directory directory) throws IOException{
    newWriter().open(directory);
    return this;
}
public ParserAdapter<T> openWriter(Directory directory, String fileName, boolean appendExtension) throws IOException{
    newWriter().open(directory, fileName, appendExtension);
    return this;
}
public ParserAdapter<T> openWriter(File file) throws IOException{
    newWriter().open(file);
    return this;
}
public ParserAdapter<T> startWriterDocument() throws IOException{
    writerHelper().startDocument();
    return this;
}
public void startWriterSheet() throws IOException{
    writerHelper().startSheet();
}
public void write(T item) throws IOException{
    writerHelper().write(item);
}

public void write(defParserWriter writer, T value) throws IOException{
    if(value instanceof ListEntry){
        writer.beginObject();
        for(Entry<String, Object> e: (ListEntry<String, Object>)value){
            writeValue(writer, e.key, e.value);
        }
        writer.endObject();
    }
    else if(value instanceof Pair){
        Pair<String, Object> p = (Pair<String, Object>)value;
        writeValue(writer, p.first, p.second);
    }
}
private void writeValue(defParserWriter writer, String name, Object o) throws IOException{
    if(o instanceof String){
        writer.write(name, (String)o);
    }
    else if(o instanceof Integer){
        writer.write(name, (Integer)o);
    }
    else if(o instanceof Long){
        writer.write(name, (Long)o);
    }
    else if(o instanceof Float){
        writer.write(name, (Float)o);
    }
    else if(o instanceof Double){
        writer.write(name, (Double)o);
    }
    else if(o instanceof Boolean){
        writer.write(name, (Boolean)o);
    }
    else if(o instanceof ListEntry){
        writer.name(name);
        write(writer, (T)o);
    }
    else if(o instanceof List){
        writer.beginArray();
        for(Object ol: (List)o){
            writeValue(writer, name, ol);
        }
        writer.endArray();
    }
    else if(o == null){
        writer.writeNull(name);
    } else {

DebugException.start().unknown("class", DebugTrack.getFullSimpleName(o)).end();

        writer.nullValue();
    }
}
public ParserAdapter<T> endWriterSheet() throws IOException{
    writerHelper().endSheet();
    return this;
}
public ParserAdapter<T> endWriterDocument() throws IOException{
    writerHelper().endDocument();
    return this;
}
public void closeWriter() throws IOException{
    if(!isWriterClosed()){
        writerHelper().close();
    }
}
public boolean isWriterClosed(){
    return (writerHelper == null) || writerHelper.isClosed();
}

protected abstract void writeFromDestinationStartDocument(ParserAdapter sourceAdapter, ParserWriterHelper destinationWriterHelper) throws IOException;

public abstract void writeFromDestinationEndDocument(ParserAdapter sourceAdapter, ParserWriterHelper destinationWriterHelper) throws IOException;
public <B> ParserAdapter<T> writeFrom(ParserAdapter<B> sourceAdapter, ParserAdapter<Object> stringAdapter) throws IOException{
    ParserAdapter workingAdapter;
    if(stringAdapter != null){
        stringAdapter.setName(sourceAdapter.getName())
                .newWriter().setFile(sourceAdapter.writerHelper().getFile());
        workingAdapter = stringAdapter;
    }
    else{
        workingAdapter = sourceAdapter;
    }
    writeFromDestinationStartDocument(workingAdapter, writerHelper());
    workingAdapter.openReader().startReaderDocument().startReaderSheet();
    defParserWriter destinationWriter = writerHelper().getWriterCopy();
    while(workingAdapter.isNotEndArray()){
        Object item = workingAdapter.read();
        workingAdapter.write(destinationWriter, item);
    }
    workingAdapter.endReaderSheet().endReaderDocument().closeReader();
    writeFromDestinationEndDocument(workingAdapter, writerHelper());
    return this;
}

public abstract void writeToSourceStartDocument(ParserAdapter destinationAdapter, ParserReaderHelper sourceReaderHelper) throws IOException;

public abstract void writeToSourceEndDocument(ParserAdapter destinationAdapter, ParserReaderHelper sourceReaderHelper) throws IOException;
public <B> ParserAdapter<T> writeTo(ParserAdapter<B> destinationAdapter, ParserAdapter<Object> stringAdapter, Directory destinationDirectory) throws IOException{
    ParserAdapter workingAdapter;
    if(stringAdapter != null){
        stringAdapter.setName(destinationAdapter.getName());
        workingAdapter = stringAdapter;
    }
    else{
        workingAdapter = destinationAdapter;
    }
    writeToSourceStartDocument(workingAdapter, readerHelper());
    workingAdapter.openWriter(destinationDirectory).startWriterDocument().startWriterSheet();
    defParserWriter destinationWriter = workingAdapter.writerHelper().getWriterCopy();
    while(isNotEndArray()){
        T item = read();
        write(destinationWriter, item);
    }
    workingAdapter.endWriterSheet().endWriterDocument().closeWriter();
    writeToSourceEndDocument(workingAdapter, readerHelper());
    if(stringAdapter != null){
        destinationAdapter.newWriter().setFile(workingAdapter.writerHelper().getFile());
    }
    return this;
}

public void close() throws IOException{
    IOException eR = null;
    IOException eW = null;
    try{
        closeReader();
    } catch(IOException e){
        eR = e;
    }
    try{
        closeWriter();
    } catch(IOException e){
        eW = e;
    }
    if(eR!=null){
        throw eR;
    }
    if(eW!=null){
        throw eW;
    }
}
public void closeNothrow(){
    try{
        close();
    }
    catch(IOException e){

    }
}



@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

}
