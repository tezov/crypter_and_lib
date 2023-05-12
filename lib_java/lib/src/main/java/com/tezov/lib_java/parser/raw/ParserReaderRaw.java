/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.parser.raw;

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
import com.tezov.lib_java.type.primitive.string.StringBase58To;

import java.io.Reader;

import com.tezov.lib_java.generator.uid.UidBase;
import com.tezov.lib_java.generator.uid.defUid;
import com.tezov.lib_java.parser.defParserReader;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.primitive.string.StringHexTo;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import static com.tezov.lib_java.parser.raw.ParserAdapterRaw.TAG_BEGIN_ARRAY;
import static com.tezov.lib_java.parser.raw.ParserAdapterRaw.TAG_END_ARRAY;
import static com.tezov.lib_java.parser.raw.ParserAdapterRaw.TAG_BEGIN_OBJECT;
import static com.tezov.lib_java.parser.raw.ParserAdapterRaw.TAG_END_OBJECT;
import static com.tezov.lib_java.parser.raw.ParserAdapterRaw.TOKEN_BOOLEAN;
import static com.tezov.lib_java.parser.raw.ParserAdapterRaw.TOKEN_NAME;
import static com.tezov.lib_java.parser.raw.ParserAdapterRaw.TOKEN_NULL;
import static com.tezov.lib_java.parser.raw.ParserAdapterRaw.TOKEN_NUMBER;
import static com.tezov.lib_java.parser.raw.ParserAdapterRaw.TOKEN_STRING;

public class ParserReaderRaw implements defParserReader{
private RawReader rawReader;
private final java.io.Reader reader;

private static class RawReader{
    private final java.io.Reader reader;
    private final static char NULL_CHAR = 0xFFFF;
    private char b = NULL_CHAR;
    public RawReader(Reader reader){
        this.reader = reader;
    }
    private char getChar() throws IOException{
        char token = peekChar();
        b = NULL_CHAR;
        return token;
    }
    private char peekChar() throws IOException{
        if(b == NULL_CHAR){
            b =(char)reader.read();
        }
        return b;
    }
    public int read(char[] c) throws IOException{
        return reader.read(c);
    }
    public void close() throws IOException{
        reader.close();
    }
}

public ParserReaderRaw(InputStream in){
    this(new InputStreamReader(in, StandardCharsets.UTF_8));
}
public ParserReaderRaw(java.io.Reader reader){
DebugTrack.start().create(this).end();
    this.reader = reader;
    if(reader != null){
        this.rawReader = new RawReader(reader);
    }
}
@Override
public ParserReaderRaw copy() throws IOException{
    ParserReaderRaw copy = new ParserReaderRaw((java.io.Reader)null);
    copy.rawReader = rawReader;
    return copy;
}

@Override
public defParserReader beginArray() throws IOException{
    char b = rawReader.getChar();
    if(b != TAG_BEGIN_ARRAY){
        throw new IOException();
    }
    return this;
}
@Override
public defParserReader endArray() throws IOException{
    char b = rawReader.getChar();
    if(b != TAG_END_ARRAY){
        throw new IOException();
    }
    return this;
}
@Override
public defParserReader beginObject() throws IOException{
    char b = rawReader.getChar();
    if(b != TAG_BEGIN_OBJECT){
        throw new IOException();
    }
    return this;
}
@Override
public defParserReader endObject() throws IOException{
    char b = rawReader.getChar();
    if(b != TAG_END_OBJECT){
        throw new IOException();
    }
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
    char b = rawReader.peekChar();
    if(b == TAG_BEGIN_OBJECT){
        return Token.BEGIN_OBJECT;
    }
    else if(b == TAG_END_OBJECT){
        return Token.END_OBJECT;
    }
    else if(b == TAG_END_ARRAY){
        return Token.END_ARRAY;
    }
    else if(b == TAG_BEGIN_ARRAY){
        return Token.BEGIN_ARRAY;
    }
    else if(b == TOKEN_NAME){
        return Token.NAME;
    }
    else if(b == TOKEN_STRING){
        return Token.STRING;
    }
    else if(b == TOKEN_NUMBER){
        return Token.NUMBER;
    }
    else if(b == TOKEN_BOOLEAN){
        return Token.BOOLEAN;
    }
    else if(b == TOKEN_NULL){
        return Token.NULL;
    }
    else{
        throw new IOException();
    }
}
@Override
public String nextName() throws IOException{
    char b = rawReader.getChar();
    if(b != TOKEN_NAME){
        throw new IOException();
    }
    return read();
}

private String read(char tokenExpected) throws IOException{
    char b = rawReader.getChar();
    if(b != tokenExpected){
        throw new IOException();
    }
    String s = read();
    if(s == null){
        throw new IOException();
    }
    return s;
}
private String read() throws IOException{
    char[] lengthChar = new char[2];
    if(rawReader.read(lengthChar) != lengthChar.length){
        throw new IOException();
    }
    int length;
    try{
        String s = new String(lengthChar);
        length = Integer.parseInt(s);
    }
    catch(NumberFormatException e){
        throw new IOException();
    }
    char[] sBase58LengthChar = new char[length];
    if(rawReader.read(sBase58LengthChar) != sBase58LengthChar.length){
        throw new IOException();
    }
    int sBase58Length;
    try{
        String s = new String(sBase58LengthChar);
        sBase58Length = Integer.parseInt(StringBase58To.StringChar(s));
    }
    catch(NumberFormatException e){
        throw new IOException();
    }
    if(sBase58Length <= 0){
        return null;
    }
    char[] sBase58Char = new char[sBase58Length];
    if(rawReader.read(sBase58Char) != sBase58Char.length){
        throw new IOException();
    }
    String s = new String(sBase58Char);
    return StringBase58To.StringChar(s);
}

@Override
public String nextString() throws IOException{
    char b = rawReader.peekChar();
    if(b == TOKEN_STRING){
        return read(TOKEN_STRING);
    }
    else if(b == TOKEN_NUMBER){
        return read(TOKEN_NUMBER);
    }
    else if(b == TOKEN_BOOLEAN){
        return read(TOKEN_BOOLEAN);
    }
    else if(b == TOKEN_NULL){
        nextNull();
        return null;
    }
    else{
        throw new IOException();
    }
}
@Override
public boolean nextBoolean() throws IOException{
    String s = read(TOKEN_BOOLEAN);
    if("1".equals(s)){
        return true;
    }
    else if("0".equals(s)){
        return false;
    }
    else {
        throw new IOException();
    }
}
@Override
public void nextNull() throws IOException{
    char b = rawReader.getChar();
    if(b != TOKEN_NULL){
        throw new IOException();
    }
}
@Override
public double nextDouble() throws IOException{
    try{
        return Double.parseDouble(read(TOKEN_NUMBER));
    }
    catch(NumberFormatException e){
        throw new IOException();
    }
}
@Override
public long nextLong() throws IOException{
    try{
        return Long.parseLong(read(TOKEN_NUMBER));
    }
    catch(NumberFormatException e){
        throw new IOException();
    }
}
@Override
public int nextInt() throws IOException{
    try{
        return Integer.parseInt(read(TOKEN_NUMBER));
    }
    catch(NumberFormatException e){
        throw new IOException();
    }
}
@Override
public defUid nextUid() throws IOException{
    return UidBase.fromHexString(nextString());
}
@Override
public byte[] nextBytes() throws IOException{
    return StringHexTo.Bytes(nextString());
}
@Override
public void skipValue() throws IOException{
    char b = rawReader.peekChar();
    if((b == TOKEN_STRING) || (b == TOKEN_NUMBER) || (b == TOKEN_BOOLEAN)){
        read(b);
    }
    else if(b == TOKEN_NULL){
        nextNull();
    }
    else{
        throw new IOException();
    }
}

@Override
public void close() throws IOException{
    if(canBeClosed()){
        rawReader.close();
        rawReader = null;
    }
}
@Override
public boolean canBeClosed(){
    return rawReader != null;
}
@Override
public String toString(){
    return rawReader.toString();
}
@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

}
