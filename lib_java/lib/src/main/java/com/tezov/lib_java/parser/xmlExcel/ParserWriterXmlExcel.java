/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.parser.xmlExcel;

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugTrack;
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
import com.tezov.lib_java.parser.xml.ParserWriterXml;
import com.tezov.lib_java.debug.DebugException;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import static com.tezov.lib_java.parser.xmlExcel.ParserAdapterXmlExcel.ARRAY_BEGIN_CHAR;
import static com.tezov.lib_java.parser.xmlExcel.ParserAdapterXmlExcel.ARRAY_END_CHAR;
import static com.tezov.lib_java.parser.xmlExcel.ParserAdapterXmlExcel.ATTR_DATA_TYPE;
import static com.tezov.lib_java.parser.xmlExcel.ParserAdapterXmlExcel.ATTR_DATA_TYPE_NUMBER;
import static com.tezov.lib_java.parser.xmlExcel.ParserAdapterXmlExcel.ATTR_DATA_TYPE_STRING;
import static com.tezov.lib_java.parser.xmlExcel.ParserAdapterXmlExcel.ATTR_NS_SPREADSHEET;
import static com.tezov.lib_java.parser.xmlExcel.ParserAdapterXmlExcel.ATTR_NS_TEZOV;
import static com.tezov.lib_java.parser.xmlExcel.ParserAdapterXmlExcel.ATTR_TEZOV_NAME;
import static com.tezov.lib_java.parser.xmlExcel.ParserAdapterXmlExcel.ATTR_TEZOV_TYPE;
import static com.tezov.lib_java.parser.xmlExcel.ParserAdapterXmlExcel.ATTR_TEZOV_TYPE_BEGIN;
import static com.tezov.lib_java.parser.xmlExcel.ParserAdapterXmlExcel.ATTR_TEZOV_TYPE_END;
import static com.tezov.lib_java.parser.xmlExcel.ParserAdapterXmlExcel.ATTR_TEZOV_TYPE_NAME;
import static com.tezov.lib_java.parser.xmlExcel.ParserAdapterXmlExcel.NAME_ARRAY;
import static com.tezov.lib_java.parser.xmlExcel.ParserAdapterXmlExcel.NAME_OBJECT;
import static com.tezov.lib_java.parser.xmlExcel.ParserAdapterXmlExcel.OBJECT_BEGIN_CHAR;
import static com.tezov.lib_java.parser.xmlExcel.ParserAdapterXmlExcel.OBJECT_END_CHAR;
import static com.tezov.lib_java.parser.xmlExcel.ParserAdapterXmlExcel.TAG_CELL;
import static com.tezov.lib_java.parser.xmlExcel.ParserAdapterXmlExcel.TAG_DATA;
import static com.tezov.lib_java.parser.xmlExcel.ParserAdapterXmlExcel.TAG_ROW;
import static org.xmlpull.v1.XmlPullParser.NO_NAMESPACE;

public class ParserWriterXmlExcel extends ParserWriterXml{
public ParserWriterXmlExcel(OutputStream out) throws IOException{
    super(out);
}
public ParserWriterXmlExcel(Writer out) throws IOException{
    super(out);
}
@Override
protected ParserWriterXml newParserWriterXml() throws IOException{
    return new ParserWriterXmlExcel((Writer)null);
}
@Override
protected State newState(){
    return new State();
}
@Override
protected State state(){
    return (State)super.state();
}

private void startCell(String name, String type, String tezovAttribute, String tezovAttributeValue) throws IOException{
    startTag(TAG_CELL).attribute(ATTR_NS_TEZOV, ATTR_TEZOV_NAME, name)
            .attribute(ATTR_NS_TEZOV, tezovAttribute, tezovAttributeValue)
            .startTag(TAG_DATA)
            .attribute(ATTR_NS_SPREADSHEET, ATTR_DATA_TYPE, type);
}
private void startCell(String name, String type) throws IOException{
    startTag(TAG_CELL).attribute(ATTR_NS_TEZOV, ATTR_TEZOV_NAME, name).startTag(TAG_DATA).attribute(ATTR_NS_SPREADSHEET, ATTR_DATA_TYPE, type);
}
private void endCell() throws IOException{
    endTag(TAG_DATA).endTag(TAG_CELL);
}

@Override
public ParserWriterXmlExcel beginObject() throws IOException{
    if((state().objectOpenedLevel == 0) && (state().arrayOpenedLevel == 0)){
        startTag(NO_NAMESPACE, TAG_ROW).attribute(ATTR_NS_TEZOV, ATTR_TEZOV_NAME, NAME_OBJECT);
    } else {
        startCell(NAME_OBJECT, ATTR_DATA_TYPE_STRING, ATTR_TEZOV_TYPE, ATTR_TEZOV_TYPE_BEGIN);
        value(OBJECT_BEGIN_CHAR);
        endCell();
    }
    state().objectOpenedLevel++;
    return this;
}
@Override
public ParserWriterXmlExcel endObject() throws IOException{
    if((state().objectOpenedLevel == 1) && (state().arrayOpenedLevel == 0)){
        endTag(NO_NAMESPACE, TAG_ROW);
    } else {
        startCell(NAME_OBJECT, ATTR_DATA_TYPE_STRING, ATTR_TEZOV_TYPE, ATTR_TEZOV_TYPE_END);
        value(OBJECT_END_CHAR);
        endCell();
    }
    state().objectOpenedLevel--;
    return this;
}

@Override
public ParserWriterXmlExcel beginArray() throws IOException{
    if((state().arrayOpenedLevel == 0) && (state().objectOpenedLevel == 0)){
        startTag(NO_NAMESPACE, TAG_ROW).attribute(ATTR_NS_TEZOV, ATTR_TEZOV_NAME, NAME_ARRAY);
    } else {
        startCell(NAME_ARRAY, ATTR_DATA_TYPE_STRING, ATTR_TEZOV_TYPE, ATTR_TEZOV_TYPE_BEGIN);
        value(ARRAY_BEGIN_CHAR);
        endCell();
    }
    state().arrayOpenedLevel++;
    return this;
}
@Override
public ParserWriterXmlExcel endArray() throws IOException{
    if((state().arrayOpenedLevel == 1) && (state().objectOpenedLevel == 0)){
        endTag(NO_NAMESPACE, TAG_ROW);
    } else {
        startCell(NAME_ARRAY, ATTR_DATA_TYPE_STRING, ATTR_TEZOV_TYPE, ATTR_TEZOV_TYPE_END);
        value(ARRAY_END_CHAR);
        endCell();
    }
    state().arrayOpenedLevel--;
    return this;
}

@Override
public ParserWriterXml name(String name) throws IOException{
    startCell(name, ATTR_DATA_TYPE_STRING, ATTR_TEZOV_TYPE, ATTR_TEZOV_TYPE_NAME);
    value(name);
    endCell();
    return this;
}
@Override
public ParserWriterXml name(String nameSpace, String name) throws IOException{

DebugException.start().notImplemented().end();

    return this;
}

@Override
public ParserWriterXml write(String name, String o) throws IOException{
    startCell(name, ATTR_DATA_TYPE_STRING);
    value(o);
    endCell();
    return this;
}
@Override
public ParserWriterXml write(String name, Number o) throws IOException{
    startCell(name, ATTR_DATA_TYPE_NUMBER);
    value(o);
    endCell();
    return this;
}
@Override
public ParserWriterXml write(String name, Boolean o) throws IOException{
    startCell(name, ATTR_DATA_TYPE_STRING);
    value(o);
    endCell();
    return this;
}
@Override
public ParserWriterXml write(String name, byte[] o) throws IOException{
    startCell(name, ATTR_DATA_TYPE_STRING);
    value(o);
    endCell();
    return this;
}
@Override
public ParserWriterXml write(String name, defUid o) throws IOException{
    startCell(name, ATTR_DATA_TYPE_STRING);
    value(o);
    endCell();
    return this;
}
@Override
public ParserWriterXml writeNull(String name) throws IOException{
    startCell(name, ATTR_DATA_TYPE_STRING);
    nullValue();
    endCell();
    return this;
}

protected static class State extends ParserWriterXml.State{
    private int objectOpenedLevel = 0;
    private int arrayOpenedLevel = 0;

}

}
