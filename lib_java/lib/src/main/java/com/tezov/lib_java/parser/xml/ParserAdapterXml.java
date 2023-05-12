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
import com.tezov.lib_java.parser.ParserReaderHelper;
import com.tezov.lib_java.parser.ParserWriterHelper;

import java.io.IOException;

public class ParserAdapterXml<T> extends ParserAdapter<T>{
public final static String FILE_EXTENSION = "xml";
public final static String ATTR_NS_NULL = "http://www.w3.org/2001/XMLSchema-instance";
public final static String ATTR_NS_NULL_PREFIX = "xsi";
public final static String ATTR_NULL = "nil";
public final static String TAG_OBJECT = "object";
public final static String TAG_ARRAY = "array";

public ParserAdapterXml(String name){
    super(name);
}
@Override
public String fileExtension(){
    return FILE_EXTENSION;
}

@Override
protected ParserReaderHelper<T> newReaderHelper(){
    return new ParserReaderHelperXml<>(this);
}
@Override
protected ParserWriterHelper<T> newWriterHelper(){
    return new ParserWriterHelperXml<>(this);
}

@Override
protected void writeFromDestinationStartDocument(ParserAdapter sourceAdapter, ParserWriterHelper destinationWriterHelper) throws IOException{
    ParserWriterHelperXml parserWriterHelper = (ParserWriterHelperXml)writerHelper();
    parserWriterHelper.startSheet(writerHelper().getWriterCopy(), sourceAdapter.getName());
}
@Override
public void writeFromDestinationEndDocument(ParserAdapter sourceAdapter, ParserWriterHelper destinationWriterHelper) throws IOException{
    ParserWriterHelperXml parserWriterHelper = (ParserWriterHelperXml)writerHelper();
    parserWriterHelper.endSheet(writerHelper().getWriterCopy(), sourceAdapter.getName());
}

@Override
public void writeToSourceStartDocument(ParserAdapter destinationAdapter, ParserReaderHelper sourceReaderHelper) throws IOException{
    ParserReaderHelperXml parserReaderHelper = (ParserReaderHelperXml)readerHelper();
    parserReaderHelper.startSheet(readerHelper().getReaderCopy(), destinationAdapter.getName());

}
@Override
public void writeToSourceEndDocument(ParserAdapter destinationAdapter, ParserReaderHelper sourceReaderHelper) throws IOException{
    ParserReaderHelperXml parserReaderHelper = (ParserReaderHelperXml)readerHelper();
    parserReaderHelper.endSheet(readerHelper().getReaderCopy(), destinationAdapter.getName());
}

}
