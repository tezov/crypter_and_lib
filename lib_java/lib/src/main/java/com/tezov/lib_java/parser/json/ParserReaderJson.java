/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.parser.json;

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
import com.tezov.lib_java.debug.DebugLog;

import java.nio.charset.StandardCharsets;

import android.util.JsonToken;

import com.tezov.lib_java.generator.uid.UidBase;
import com.tezov.lib_java.generator.uid.defUid;
import com.tezov.lib_java.parser.defParserReader;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.primitive.string.StringHexTo;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ParserReaderJson implements defParserReader{
private java.io.Reader reader;
private android.util.JsonReader jsonReader;

public ParserReaderJson(InputStream in){
    this(new InputStreamReader(in, StandardCharsets.UTF_8));
}
public ParserReaderJson(java.io.Reader in){
DebugTrack.start().create(this).end();
    this.reader = in;
    if(in != null){
        this.jsonReader = new android.util.JsonReader(in);
    }
}
@Override
public ParserReaderJson copy() throws IOException{
    ParserReaderJson copy = new ParserReaderJson((java.io.Reader)null);
    copy.jsonReader = jsonReader;
    return this;
}
public boolean isLenient(){
    return jsonReader.isLenient();
}
public final void setLenient(boolean lenient){
    jsonReader.setLenient(lenient);
}

@Override
public defParserReader beginArray() throws IOException{
    jsonReader.beginArray();
    return this;
}
@Override
public defParserReader endArray() throws IOException{
    jsonReader.endArray();
    return this;
}
@Override
public defParserReader beginObject() throws IOException{
    jsonReader.beginObject();
    return this;
}
@Override
public defParserReader endObject() throws IOException{
    jsonReader.endObject();
    return this;
}

@Override
public boolean isName() throws IOException{
    return peek() == Token.NAME;
}
@Override
public boolean isBeginObject() throws IOException{
    return peek() == Token.BEGIN_OBJECT;
}
@Override
public boolean isNotEndObject() throws IOException{
    return peek() != Token.END_OBJECT;
}
@Override
public boolean isBeginArray() throws IOException{
    return peek() == Token.BEGIN_ARRAY;
}
@Override
public boolean isNotEndArray() throws IOException{
    return peek() != Token.END_ARRAY;
}

@Override
public Token peek() throws IOException{
    JsonToken token = jsonReader.peek();
    switch(token){
        case BEGIN_ARRAY:
            return Token.BEGIN_ARRAY;
        case END_ARRAY:
            return Token.END_ARRAY;
        case BEGIN_OBJECT:
            return Token.BEGIN_OBJECT;
        case END_OBJECT:
            return Token.END_OBJECT;
        case NAME:
            return Token.NAME;
        case STRING:
            return Token.STRING;
        case NUMBER:
            return Token.NUMBER;
        case BOOLEAN:
            return Token.BOOLEAN;
        case NULL:
            return Token.NULL;
        default:
            throw new IOException();
    }
}
@Override
public String nextName() throws IOException{
    return jsonReader.nextName();
}

@Override
public String nextString() throws IOException{
    return jsonReader.nextString();
}
@Override
public boolean nextBoolean() throws IOException{
    return jsonReader.nextBoolean();
}
@Override
public void nextNull() throws IOException{
    jsonReader.nextNull();
}
@Override
public double nextDouble() throws IOException{
    return jsonReader.nextDouble();
}
@Override
public long nextLong() throws IOException{
    return jsonReader.nextLong();
}
@Override
public int nextInt() throws IOException{
    return jsonReader.nextInt();
}
@Override
public defUid nextUid() throws IOException{
    return UidBase.fromHexString(jsonReader.nextString());
}
@Override
public byte[] nextBytes() throws IOException{
    return StringHexTo.Bytes(jsonReader.nextString());
}
@Override
public void skipValue() throws IOException{
    jsonReader.skipValue();
}

@Override
public void close() throws IOException{
    if(canBeClosed()){
        jsonReader.close();
        reader = null;
    }
}
@Override
public boolean canBeClosed(){
    return reader != null;
}
@Override
public String toString(){
    return jsonReader.toString();
}

public void toDebugLogObject() throws IOException{
    toDebugLogObject("");
}
private void toDebugLogObject(String prefix) throws IOException{
DebugLog.start().send(prefix + "{").end();
    beginObject();
    defParserReader.Token token;
    while(peek() != defParserReader.Token.END_OBJECT){
        String name = nextName();
        token = peek();
        if(token == defParserReader.Token.BEGIN_ARRAY){
DebugLog.start().send(prefix + name + " ::").end();
            prefix += "--|";
            toDebugLogArray(prefix);
            prefix = prefix.substring(3);
        }
        else if(token == defParserReader.Token.BEGIN_OBJECT){
DebugLog.start().send(prefix + name + " ::").end();
            prefix += "--|";
            toDebugLogObject(prefix);
            prefix = prefix.substring(3);
        }
        else {
            String value = null;
            if(token == defParserReader.Token.BOOLEAN){
                value = Boolean.toString(nextBoolean());
            }
            else if(token == defParserReader.Token.NULL){
                nextNull();
            }
            else {
                value = nextString();
            }
DebugLog.start().send(prefix + name + " :: " + value).end();
        }
    }
    endObject();
DebugLog.start().send(prefix + "}").end();
}
public void toDebugLogArray() throws IOException{
    toDebugLogArray("");
}
private void toDebugLogArray(String prefix) throws IOException{
DebugLog.start().send(prefix + "[").end();
    beginArray();
    defParserReader.Token token;
    while(peek() != defParserReader.Token.END_ARRAY){
        token = peek();
        if(token == defParserReader.Token.BEGIN_ARRAY){
            prefix += "--|";
            toDebugLogArray(prefix);
            prefix = prefix.substring(3);
        }
        else if(token == defParserReader.Token.BEGIN_OBJECT){
            prefix += "--|";
            toDebugLogObject(prefix);
            prefix = prefix.substring(3);
        }
        else {
            String value = null;
            if(token == defParserReader.Token.BOOLEAN){
                value = Boolean.toString(nextBoolean());
            }
            else if(token == defParserReader.Token.NULL){
                nextNull();
            }
            else {
                value = nextString();
            }
DebugLog.start().send(":: " + value).end();
        }
    }
    endArray();
DebugLog.start().send(prefix + "]").end();
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

}
