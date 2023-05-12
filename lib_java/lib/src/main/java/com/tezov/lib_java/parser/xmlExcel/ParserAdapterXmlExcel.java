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
import com.tezov.lib_java.application.AppContext;
import com.tezov.lib_java.parser.ParserAdapter;
import com.tezov.lib_java.parser.ParserReaderHelper;
import com.tezov.lib_java.parser.ParserWriterHelper;
import com.tezov.lib_java.parser.xml.ParserAdapterXml;

import java.io.IOException;

public class ParserAdapterXmlExcel<T> extends ParserAdapterXml<T>{

public final static String INST_EXCEL = "mso-application progid=\"Excel.Sheet\"";

public final static String ATTR_NS_OFFICE = "urn:schemas-microsoft-com:office:office";
public final static String ATTR_NS_OFFICE_PREFIX = "o";

public final static String ATTR_NS_EXCEL = "urn:schemas-microsoft-com:office:excel";
public final static String ATTR_NS_EXCEL_PREFIX = "x";

public final static String ATTR_NS_SPREADSHEET = "urn:schemas-microsoft-com:office:spreadsheet";
public final static String ATTR_NS_SPREADSHEET_PREFIX = "ss";
public final static String ATTR_SPREADSHEET_NAME = "Name";

public final static String ATTR_NS_HTML = "http://www.w3.org/TR/REC-html40";
public final static String ATTR_NS_HTML_PREFIX = "html";

public final static String TAG_WORKBOOK = "Workbook";

public final static String TAG_WORKSHEET = "Worksheet";
public final static String TAG_TABLE = "Table";

public final static String TAG_ROW = "Row";
public final static String TAG_CELL = "Cell";
public final static String TAG_DATA = "Data";

public final static String ATTR_DATA_TYPE = "Name";
public final static String ATTR_DATA_TYPE_NUMBER = "Number";
public final static String ATTR_DATA_TYPE_STRING = "String";

public final static String ATTR_NS_TEZOV = AppContext.getPackageOwner();
public final static String ATTR_NS_TEZOV_PREFIX = "app";
public final static String ATTR_TEZOV_NAME = "Name";

public final static String ATTR_TEZOV_TYPE = "Name";
public final static String ATTR_TEZOV_TYPE_NAME = "name";
public final static String ATTR_TEZOV_TYPE_BEGIN = "begin";
public final static String ATTR_TEZOV_TYPE_END = "end";
public final static String NAME_OBJECT = "object";
public final static String OBJECT_BEGIN_CHAR = "{";
public final static String OBJECT_END_CHAR = "}";
public final static String NAME_ARRAY = "array";
public final static String ARRAY_BEGIN_CHAR = "[";
public final static String ARRAY_END_CHAR = "]";

public ParserAdapterXmlExcel(String name){
    super(name);
}

@Override
protected ParserReaderHelper<T> newReaderHelper(){
    return new ParserReaderHelperXmlExcel<>(this);
}
@Override
protected ParserWriterHelper<T> newWriterHelper(){
    return new ParserWriterHelperXmlExcel<>(this);
}

@Override
protected void writeFromDestinationStartDocument(ParserAdapter sourceAdapter, ParserWriterHelper destinationWriterHelper) throws IOException{
    ParserWriterHelperXmlExcel parserWriterHelper = (ParserWriterHelperXmlExcel)writerHelper();
    parserWriterHelper.startSheet(writerHelper().getWriterCopy(), sourceAdapter.getName());
}
@Override
public void writeFromDestinationEndDocument(ParserAdapter sourceAdapter, ParserWriterHelper destinationWriterHelper) throws IOException{
    ParserWriterHelperXmlExcel parserWriterHelper = (ParserWriterHelperXmlExcel)writerHelper();
    parserWriterHelper.endSheet(writerHelper().getWriterCopy(), sourceAdapter.getName());
}

@Override
public void writeToSourceStartDocument(ParserAdapter destinationAdapter, ParserReaderHelper sourceReaderHelper) throws IOException{
    ParserReaderHelperXmlExcel parserReaderHelper = (ParserReaderHelperXmlExcel)readerHelper();
    parserReaderHelper.startSheet(readerHelper().getReaderCopy(), destinationAdapter.getName());
}
@Override
public void writeToSourceEndDocument(ParserAdapter destinationAdapter, ParserReaderHelper sourceReaderHelper) throws IOException{
    ParserReaderHelperXmlExcel parserReaderHelper = (ParserReaderHelperXmlExcel)readerHelper();
    parserReaderHelper.endSheet(readerHelper().getReaderCopy(), destinationAdapter.getName());
}

}
