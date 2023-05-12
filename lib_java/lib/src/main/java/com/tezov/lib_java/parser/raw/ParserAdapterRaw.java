/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.parser.raw;

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

public class ParserAdapterRaw<T> extends ParserAdapter<T>{
public final static String FILE_EXTENSION = "raw";

public final static char TAG_BEGIN_OBJECT = 'h';
public final static char TAG_END_OBJECT = 'H';
public final static char TAG_BEGIN_ARRAY = 'F';
public final static char TAG_END_ARRAY = 'f';
public final static char TOKEN_NAME = 'N';
public final static char TOKEN_STRING = 'S';
public final static char TOKEN_NUMBER = 'U';
public final static char TOKEN_BOOLEAN = 'B';
public final static char TOKEN_NULL = 'o';

public ParserAdapterRaw(String name){
    super(name);
}
@Override
public String fileExtension(){
    return FILE_EXTENSION;
}
@Override
protected ParserReaderHelper<T> newReaderHelper(){
    return new ParserReaderHelperRaw<>(this);
}
@Override
protected ParserWriterHelper<T> newWriterHelper(){
    return new ParserWriterHelperRaw<>(this);
}

@Override
protected void writeFromDestinationStartDocument(ParserAdapter sourceAdapter, ParserWriterHelper destinationWriterHelper) throws IOException{
    ParserWriterHelperRaw parserWriterHelper = (ParserWriterHelperRaw)writerHelper();
    parserWriterHelper.startSheet(writerHelper().getWriterCopy(), sourceAdapter.getName());
}
@Override
public void writeFromDestinationEndDocument(ParserAdapter sourceAdapter, ParserWriterHelper destinationWriterHelper) throws IOException{
    ParserWriterHelperRaw parserWriterHelper = (ParserWriterHelperRaw)writerHelper();
    parserWriterHelper.endSheet(writerHelper().getWriterCopy(), sourceAdapter.getName());
}

@Override
public void writeToSourceStartDocument(ParserAdapter destinationAdapter, ParserReaderHelper sourceReaderHelper) throws IOException{
    ParserReaderHelperRaw parserReaderHelper = (ParserReaderHelperRaw)readerHelper();
    parserReaderHelper.startSheet(readerHelper().getReaderCopy(), destinationAdapter.getName());
}
@Override
public void writeToSourceEndDocument(ParserAdapter destinationAdapter, ParserReaderHelper sourceReaderHelper) throws IOException{
    ParserReaderHelperRaw parserReaderHelper = (ParserReaderHelperRaw)readerHelper();
    parserReaderHelper.endSheet(readerHelper().getReaderCopy(), destinationAdapter.getName());
}

}
