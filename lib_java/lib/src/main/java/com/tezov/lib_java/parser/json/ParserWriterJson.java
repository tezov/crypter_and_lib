/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.parser.json;

import com.tezov.lib_java.debug.DebugLog;
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
import java.nio.charset.StandardCharsets;

import com.tezov.lib_java.generator.uid.defUid;
import com.tezov.lib_java.parser.defParserWriter;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.primitive.BytesTo;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class ParserWriterJson implements defParserWriter{
private java.io.Writer writer;
private android.util.JsonWriter jsonWriter;

public ParserWriterJson(OutputStream out){
    this(new OutputStreamWriter(out, StandardCharsets.UTF_8));
}
public ParserWriterJson(java.io.Writer out){
DebugTrack.start().create(this).end();
    this.writer = out;
    if(out != null){
        this.jsonWriter = new android.util.JsonWriter(out);
    }
}
@Override
public ParserWriterJson copy(){
    ParserWriterJson copy = new ParserWriterJson((java.io.Writer)null);
    copy.jsonWriter = jsonWriter;
    return copy;
}

public final ParserWriterJson setIndent(String indent){
    jsonWriter.setIndent(indent);
    return this;
}
public boolean isLenient(){
    return jsonWriter.isLenient();
}
public final ParserWriterJson setLenient(boolean lenient){
    jsonWriter.setLenient(lenient);
    return this;
}
@Override
public ParserWriterJson name(String name) throws IOException{
    jsonWriter.name(name);
    return this;
}
@Override
public ParserWriterJson beginArray() throws IOException{
    jsonWriter.beginArray();
    return this;
}
@Override
public ParserWriterJson endArray() throws IOException{
    jsonWriter.endArray();
    return this;
}
@Override
public ParserWriterJson beginObject() throws IOException{
    jsonWriter.beginObject();
    return this;
}
@Override
public ParserWriterJson endObject() throws IOException{
    jsonWriter.endObject();
    return this;
}

@Override
public ParserWriterJson value(String value) throws IOException{
    jsonWriter.value(value);
    return this;
}
@Override
public ParserWriterJson nullValue() throws IOException{
    jsonWriter.nullValue();
    return this;
}
@Override
public ParserWriterJson value(Boolean value) throws IOException{
    jsonWriter.value(value);
    return this;
}
@Override
public ParserWriterJson value(Number value) throws IOException{
    jsonWriter.value(value);
    return this;
}
@Override
public ParserWriterJson value(defUid value) throws IOException{
    jsonWriter.value(value != null ? value.toHexString() : null);
    return this;
}
@Override
public ParserWriterJson value(byte[] value) throws IOException{
    jsonWriter.value(value != null ? BytesTo.StringHex(value) : null);
    return this;
}

@Override
public ParserWriterJson write(String name, String o) throws IOException{
    name(name).value(o);
    return this;
}
@Override
public ParserWriterJson write(String name, Number o) throws IOException{
    name(name).value(o);
    return this;
}
@Override
public ParserWriterJson write(String name, Boolean o) throws IOException{
    name(name).value(o);
    return this;
}
@Override
public ParserWriterJson write(String name, byte[] o) throws IOException{
    name(name).value(o);
    return this;
}
@Override
public ParserWriterJson write(String name, defUid o) throws IOException{
    name(name).value(o);
    return this;
}
@Override
public ParserWriterJson writeNull(String name) throws IOException{
    name(name).nullValue();
    return this;
}

@Override
public void flush() throws IOException{
    jsonWriter.flush();
}

@Override
public void close() throws IOException{
    if(canBeClosed()){
        jsonWriter.close();
        writer = null;
    }
}
@Override
public boolean canBeClosed(){
    return writer != null;
}
@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

}
