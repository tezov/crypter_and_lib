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
import com.tezov.lib_java.parser.ParserReaderHelper;
import com.tezov.lib_java.parser.ParserWriterHelper;

import java.io.IOException;

public class ParserAdapterJson<T> extends ParserAdapter<T>{
public final static String FILE_EXTENSION = "json";

public ParserAdapterJson(String name){
    super(name);
}
@Override
public String fileExtension(){
    return FILE_EXTENSION;
}
@Override
protected ParserReaderHelper<T> newReaderHelper(){
    return new ParserReaderHelperJson<>(this);
}
@Override
protected ParserWriterHelper<T> newWriterHelper(){
    return new ParserWriterHelperJson<>(this);
}

@Override
protected void writeFromDestinationStartDocument(ParserAdapter sourceAdapter, ParserWriterHelper destinationWriterHelper) throws IOException{
    ParserWriterHelperJson parserWriterHelper = (ParserWriterHelperJson)writerHelper();
    parserWriterHelper.startSheet(writerHelper().getWriterCopy(), sourceAdapter.getName());
}
@Override
public void writeFromDestinationEndDocument(ParserAdapter sourceAdapter, ParserWriterHelper destinationWriterHelper) throws IOException{
    ParserWriterHelperJson parserWriterHelper = (ParserWriterHelperJson)writerHelper();
    parserWriterHelper.endSheet(writerHelper().getWriterCopy(), sourceAdapter.getName());
}

@Override
public void writeToSourceStartDocument(ParserAdapter destinationAdapter, ParserReaderHelper sourceReaderHelper) throws IOException{
    ParserReaderHelperJson parserReaderHelper = (ParserReaderHelperJson)readerHelper();
    parserReaderHelper.startSheet(readerHelper().getReaderCopy(), destinationAdapter.getName());
}
@Override
public void writeToSourceEndDocument(ParserAdapter destinationAdapter, ParserReaderHelper sourceReaderHelper) throws IOException{
    ParserReaderHelperJson parserReaderHelper = (ParserReaderHelperJson)readerHelper();
    parserReaderHelper.endSheet(readerHelper().getReaderCopy(), destinationAdapter.getName());
}

}
