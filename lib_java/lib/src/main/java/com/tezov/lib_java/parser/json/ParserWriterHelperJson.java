/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.parser.json;

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
import com.tezov.lib_java.parser.ParserAdapter;
import com.tezov.lib_java.parser.ParserWriterHelper;
import com.tezov.lib_java.parser.defParserWriter;

import java.io.FileWriter;
import java.io.IOException;

public class ParserWriterHelperJson<T> extends ParserWriterHelper<T>{
public final static String INDENT = "   ";

public ParserWriterHelperJson(ParserAdapter<T> adapter){
    super(adapter);
}
@Override
protected defParserWriter newWriter(FileWriter writer) throws IOException{
    return new ParserWriterJson(writer);
}

@Override
public ParserWriterHelperJson<T> startDocument(defParserWriter writer, String adapterName) throws IOException{
    ((ParserWriterJson)writer).setIndent(INDENT).beginObject();
    return this;
}
@Override
public void startSheet(defParserWriter writer, String adapterName) throws IOException{
    ((ParserWriterJson)writer).name(adapterName).beginArray();
}
@Override
public ParserWriterHelperJson<T> endSheet(defParserWriter writer, String adapterName) throws IOException{
    ((ParserWriterJson)writer).endArray();
    return this;
}

@Override
public ParserWriterHelperJson<T> endDocument(defParserWriter writer, String adapterName) throws IOException{
    ((ParserWriterJson)writer).endObject().flush();
    return this;
}

}
