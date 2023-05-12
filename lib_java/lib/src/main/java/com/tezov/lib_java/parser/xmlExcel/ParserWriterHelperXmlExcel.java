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
import com.tezov.lib_java.parser.ParserWriterHelper;
import com.tezov.lib_java.parser.defParserWriter;
import com.tezov.lib_java.parser.xml.ParserWriterXml;

import java.io.FileWriter;
import java.io.IOException;

import static com.tezov.lib_java.parser.xml.ParserAdapterXml.ATTR_NS_NULL;
import static com.tezov.lib_java.parser.xml.ParserAdapterXml.ATTR_NS_NULL_PREFIX;
import static com.tezov.lib_java.parser.xmlExcel.ParserAdapterXmlExcel.ATTR_NS_EXCEL;
import static com.tezov.lib_java.parser.xmlExcel.ParserAdapterXmlExcel.ATTR_NS_EXCEL_PREFIX;
import static com.tezov.lib_java.parser.xmlExcel.ParserAdapterXmlExcel.ATTR_NS_HTML;
import static com.tezov.lib_java.parser.xmlExcel.ParserAdapterXmlExcel.ATTR_NS_HTML_PREFIX;
import static com.tezov.lib_java.parser.xmlExcel.ParserAdapterXmlExcel.ATTR_NS_OFFICE;
import static com.tezov.lib_java.parser.xmlExcel.ParserAdapterXmlExcel.ATTR_NS_OFFICE_PREFIX;
import static com.tezov.lib_java.parser.xmlExcel.ParserAdapterXmlExcel.ATTR_NS_SPREADSHEET;
import static com.tezov.lib_java.parser.xmlExcel.ParserAdapterXmlExcel.ATTR_NS_SPREADSHEET_PREFIX;
import static com.tezov.lib_java.parser.xmlExcel.ParserAdapterXmlExcel.ATTR_NS_TEZOV;
import static com.tezov.lib_java.parser.xmlExcel.ParserAdapterXmlExcel.ATTR_NS_TEZOV_PREFIX;
import static com.tezov.lib_java.parser.xmlExcel.ParserAdapterXmlExcel.ATTR_SPREADSHEET_NAME;
import static com.tezov.lib_java.parser.xmlExcel.ParserAdapterXmlExcel.INST_EXCEL;
import static com.tezov.lib_java.parser.xmlExcel.ParserAdapterXmlExcel.TAG_TABLE;
import static com.tezov.lib_java.parser.xmlExcel.ParserAdapterXmlExcel.TAG_WORKBOOK;
import static com.tezov.lib_java.parser.xmlExcel.ParserAdapterXmlExcel.TAG_WORKSHEET;

public class ParserWriterHelperXmlExcel<T> extends ParserWriterHelper<T>{
public ParserWriterHelperXmlExcel(ParserAdapter<T> adapter){
    super(adapter);
}
@Override
protected defParserWriter newWriter(FileWriter writer) throws IOException{
    return new ParserWriterXmlExcel(writer);
}
@Override
public ParserWriterHelperXmlExcel<T> startDocument(defParserWriter writer, String adapterName) throws IOException{
    ((ParserWriterXml)writer).enableIndent(true)
            .startDocument("UTF-8", true)
            .instruction(INST_EXCEL)
            .setPrefix(ATTR_NS_OFFICE_PREFIX, ATTR_NS_OFFICE)
            .setPrefix(ATTR_NS_EXCEL_PREFIX, ATTR_NS_EXCEL)
            .setPrefix(ATTR_NS_SPREADSHEET_PREFIX, ATTR_NS_SPREADSHEET)
            .setPrefix(ATTR_NS_HTML_PREFIX, ATTR_NS_HTML)
            .setPrefix(ATTR_NS_NULL_PREFIX, ATTR_NS_NULL)
            .setPrefix(ATTR_NS_TEZOV_PREFIX, ATTR_NS_TEZOV)
            .startTag(TAG_WORKBOOK)
            .attribute("", "xmlns", ATTR_NS_SPREADSHEET);
    return this;
}
@Override
public void startSheet(defParserWriter writer, String adapterName) throws IOException{
    ((ParserWriterXml)writer).startTag(TAG_WORKSHEET).attribute(ATTR_NS_SPREADSHEET, ATTR_SPREADSHEET_NAME, adapterName).startTag(TAG_TABLE);
}
@Override
public ParserWriterHelperXmlExcel<T> endSheet(defParserWriter writer, String adapterName) throws IOException{
    ((ParserWriterXml)writer).endTag(TAG_TABLE).endTag(TAG_WORKSHEET);
    return this;
}
@Override
public ParserWriterHelperXmlExcel<T> endDocument(defParserWriter writer, String adapterName) throws IOException{
    ((ParserWriterXml)writer).endTag(TAG_WORKBOOK).endDocument();
    return this;
}

}
