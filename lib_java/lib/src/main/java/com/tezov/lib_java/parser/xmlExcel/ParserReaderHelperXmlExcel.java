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
import com.tezov.lib_java.parser.ParserReaderHelper;
import com.tezov.lib_java.parser.defParserReader;

import java.io.IOException;

import static com.tezov.lib_java.parser.xmlExcel.ParserAdapterXmlExcel.ATTR_NS_SPREADSHEET;
import static com.tezov.lib_java.parser.xmlExcel.ParserAdapterXmlExcel.ATTR_SPREADSHEET_NAME;
import static com.tezov.lib_java.parser.xmlExcel.ParserAdapterXmlExcel.TAG_TABLE;
import static com.tezov.lib_java.parser.xmlExcel.ParserAdapterXmlExcel.TAG_WORKBOOK;
import static com.tezov.lib_java.parser.xmlExcel.ParserAdapterXmlExcel.TAG_WORKSHEET;

public class ParserReaderHelperXmlExcel<T> extends ParserReaderHelper<T>{
public ParserReaderHelperXmlExcel(ParserAdapter<T> adapter){
    super(adapter);
}
@Override
protected defParserReader newReader(java.io.Reader reader) throws IOException{
    return new ParserReaderXmlExcel(reader);
}

@Override
public ParserReaderHelperXmlExcel<T> startDocument(defParserReader reader, String adapterName) throws IOException{
    ParserReaderXmlExcel xmlReader = ((ParserReaderXmlExcel)reader);
    xmlReader.nextStartTag(TAG_WORKBOOK);
    return this;
}
@Override
public void startSheet(defParserReader reader, String adapterName) throws IOException{
    ParserReaderXmlExcel xmlReader = ((ParserReaderXmlExcel)reader);
    xmlReader.nextStartTag(TAG_WORKSHEET);
    String name = xmlReader.getAttributeValue(ATTR_NS_SPREADSHEET, ATTR_SPREADSHEET_NAME);
    if(!adapterName.equals(name)){
        throw new IOException();
    }
    xmlReader.nextStartTag(TAG_TABLE);
    xmlReader.moveToNextRow();
}
@Override
public ParserReaderHelperXmlExcel<T> endSheet(defParserReader reader, String adapterName) throws IOException{
    ((ParserReaderXmlExcel)reader).nextEndTag(TAG_TABLE).nextEndTag(TAG_WORKSHEET);
    return this;
}
@Override
public ParserReaderHelperXmlExcel<T> endDocument(defParserReader reader, String adapterName) throws IOException{
    ((ParserReaderXmlExcel)reader).nextEndTag(TAG_WORKBOOK).next();
    return this;
}

}
