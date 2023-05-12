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

public interface defParserReader{
defParserReader copy() throws IOException;

boolean isName() throws IOException;
boolean isBeginObject() throws IOException;
boolean isNotEndObject() throws IOException;
boolean isBeginArray() throws IOException;
boolean isNotEndArray() throws IOException;

Token peek() throws IOException;

String nextName() throws IOException;

defParserReader beginArray() throws IOException;

defParserReader endArray() throws IOException;

defParserReader beginObject() throws IOException;

defParserReader endObject() throws IOException;

String nextString() throws IOException;

boolean nextBoolean() throws IOException;

void nextNull() throws IOException;

double nextDouble() throws IOException;

long nextLong() throws IOException;

int nextInt() throws IOException;

defUid nextUid() throws IOException;

byte[] nextBytes() throws IOException;

void skipValue() throws IOException;

void close() throws IOException;

boolean canBeClosed();

enum Token{ //Now
    BEGIN, BEGIN_ARRAY, BEGIN_OBJECT, NAME, NULL, STRING, BOOLEAN, NUMBER, END_OBJECT, END_ARRAY, END
}

}
