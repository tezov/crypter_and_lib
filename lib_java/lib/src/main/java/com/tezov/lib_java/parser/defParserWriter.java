/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.parser;

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugTrack;
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

import java.io.IOException;

public interface defParserWriter{
defParserWriter copy() throws IOException;

defParserWriter name(String name) throws IOException;

defParserWriter beginObject() throws IOException;

defParserWriter endObject() throws IOException, IllegalStateException;

defParserWriter beginArray() throws IOException, IllegalStateException;

defParserWriter endArray() throws IOException;

defParserWriter value(String value) throws IOException;

defParserWriter nullValue() throws IOException;

defParserWriter value(Boolean value) throws IOException;

defParserWriter value(Number value) throws IOException;

defParserWriter value(defUid value) throws IOException;

defParserWriter value(byte[] value) throws IOException;

defParserWriter write(String name, String o) throws IOException;

defParserWriter write(String name, Number o) throws IOException;

defParserWriter write(String name, Boolean o) throws IOException;

defParserWriter write(String name, byte[] o) throws IOException;

defParserWriter write(String name, defUid o) throws IOException;

defParserWriter writeNull(String name) throws IOException;

void flush() throws IOException;

void close() throws IOException;

boolean canBeClosed();

}
