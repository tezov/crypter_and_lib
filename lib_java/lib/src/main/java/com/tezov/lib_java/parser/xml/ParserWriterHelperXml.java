/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.parser.xml;

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

import static com.tezov.lib_java.parser.xml.ParserAdapterXml.ATTR_NS_NULL;
import static com.tezov.lib_java.parser.xml.ParserAdapterXml.ATTR_NS_NULL_PREFIX;

public class ParserWriterHelperXml<T> extends ParserWriterHelper<T>{
public ParserWriterHelperXml(ParserAdapter<T> adapter){
    super(adapter);
}
@Override
protected defParserWriter newWriter(FileWriter writer) throws IOException{
    return new ParserWriterXml(writer);
}
@Override
public ParserWriterHelperXml<T> startDocument(defParserWriter writer, String adapterName) throws IOException{
    ((ParserWriterXml)writer).enableIndent(true).startDocument("UTF-8", true).setPrefix(ATTR_NS_NULL_PREFIX, ATTR_NS_NULL);
    writer.beginObject();
    return this;
}
@Override
public void startSheet(defParserWriter writer, String adapterName) throws IOException{
    ((ParserWriterXml)writer).startTag(adapterName);
    writer.beginArray();
}
@Override
public ParserWriterHelperXml<T> endSheet(defParserWriter writer, String adapterName) throws IOException{
    ((ParserWriterXml)writer).endArray().endTag(adapterName);
    return this;
}
@Override
public ParserWriterHelperXml<T> endDocument(defParserWriter writer, String adapterName) throws IOException{
    ((ParserWriterXml)writer).endObject().endDocument();
    return this;
}

}
