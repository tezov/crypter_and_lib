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
import com.tezov.lib_java.generator.uid.defUid;
import com.tezov.lib_java.parser.defParserWriter;
import com.tezov.lib_java.toolbox.Nullify;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.primitive.BytesTo;
import com.tezov.lib_java.type.primitive.string.StringCharTo;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import static com.tezov.lib_java.parser.raw.ParserAdapterRaw.TAG_BEGIN_ARRAY;
import static com.tezov.lib_java.parser.raw.ParserAdapterRaw.TAG_END_ARRAY;
import static com.tezov.lib_java.parser.raw.ParserAdapterRaw.TAG_BEGIN_OBJECT;
import static com.tezov.lib_java.parser.raw.ParserAdapterRaw.TAG_END_OBJECT;
import static com.tezov.lib_java.parser.raw.ParserAdapterRaw.TOKEN_BOOLEAN;
import static com.tezov.lib_java.parser.raw.ParserAdapterRaw.TOKEN_NAME;
import static com.tezov.lib_java.parser.raw.ParserAdapterRaw.TOKEN_NULL;
import static com.tezov.lib_java.parser.raw.ParserAdapterRaw.TOKEN_NUMBER;
import static com.tezov.lib_java.parser.raw.ParserAdapterRaw.TOKEN_STRING;

public class ParserWriterRaw implements defParserWriter{
private java.io.Writer writer;
private boolean canBeClosed;

public ParserWriterRaw(OutputStream out){
    this(new OutputStreamWriter(out, StandardCharsets.UTF_8));
}
public ParserWriterRaw(java.io.Writer out){
DebugTrack.start().create(this).end();
    this.canBeClosed = true;
    this.writer = out;
}
@Override
public ParserWriterRaw copy(){
    ParserWriterRaw copy = new ParserWriterRaw(writer);
    copy.canBeClosed = false;
    return copy;
}

@Override
public ParserWriterRaw beginArray() throws IOException{
    writer.write(TAG_BEGIN_ARRAY);
    return this;
}
@Override
public ParserWriterRaw endArray() throws IOException{
    writer.write(TAG_END_ARRAY);
    return this;
}
@Override
public ParserWriterRaw beginObject() throws IOException{
    writer.write(TAG_BEGIN_OBJECT);
    return this;
}
@Override
public ParserWriterRaw endObject() throws IOException{
    writer.write(TAG_END_OBJECT);
    return this;
}

@Override
public ParserWriterRaw name(String name) throws IOException{
    if(Nullify.string(name) == null){
        writer.write(TOKEN_NAME);
        String sBase58Length = StringCharTo.StringBase58(Integer.toString(0));
        writer.write(String.format(Locale.US, "%02d", sBase58Length.length()));
        writer.write(sBase58Length);
    }
    else{
        write(name, TOKEN_NAME);
    }
    return this;
}

private void write(String s, char token) throws IOException{
    writer.write(token);
    String sBase58 = StringCharTo.StringBase58(s);
    String sBase58Length = StringCharTo.StringBase58(Integer.toString(sBase58.length()));
    writer.write(String.format(Locale.US, "%02d", sBase58Length.length()));
    writer.write(sBase58Length);
    writer.write(sBase58);
}
@Override
public ParserWriterRaw value(String value) throws IOException{
    if(Nullify.string(value) == null){
        nullValue();
    }
    else{
        write(value, TOKEN_STRING);
    }
    return this;
}
@Override
public ParserWriterRaw nullValue() throws IOException{
    writer.write(TOKEN_NULL);
    return this;
}
@Override
public ParserWriterRaw value(Boolean value) throws IOException{
    if(value == null){
        nullValue();
    }
    else{
        write(value ? "1" : "0", TOKEN_BOOLEAN);
    }
    return this;
}
@Override
public ParserWriterRaw value(Number value) throws IOException{
    if(value == null){
        nullValue();
    }
    else{
        String s;
        if(value instanceof Integer){
            s = Integer.toString((Integer)value);
        }
        else if(value instanceof Long){
            s = Long.toString((Long)value);
        }
        else if(value instanceof Float){
            s = Double.toString((Float)value);
        }
        else if(value instanceof Double){
            s = Double.toString((Double)value);
        }
        else {
            throw new IOException("unknown type " + value.getClass().getSimpleName());
        }
        write(s, TOKEN_NUMBER);
    }
    return this;
}
@Override
public ParserWriterRaw value(defUid value) throws IOException{
    return value(value != null ? value.toHexString() : null);
}
@Override
public ParserWriterRaw value(byte[] value) throws IOException{
    return value(value != null ? BytesTo.StringHex(value) : null);
}
@Override
public ParserWriterRaw write(String name, String o) throws IOException{
    return name(name).value(o);
}
@Override
public ParserWriterRaw write(String name, Number o) throws IOException{
    return name(name).value(o);
}
@Override
public ParserWriterRaw write(String name, Boolean o) throws IOException{
    return name(name).value(o);
}
@Override
public ParserWriterRaw write(String name, byte[] o) throws IOException{
    return name(name).value(o);
}
@Override
public ParserWriterRaw write(String name, defUid o) throws IOException{
    return name(name).value(o);
}
@Override
public ParserWriterRaw writeNull(String name) throws IOException{
    return name(name).nullValue();
}

@Override
public void flush() throws IOException{
    writer.flush();
}

@Override
public void close() throws IOException{
    if(canBeClosed()){
        writer.close();
        writer = null;
    }
}
@Override
public boolean canBeClosed(){
    return canBeClosed;
}
@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

}
