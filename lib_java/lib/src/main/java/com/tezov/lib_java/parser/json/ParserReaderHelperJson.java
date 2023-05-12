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
import com.tezov.lib_java.parser.defParserReader;

import java.io.IOException;

public class ParserReaderHelperJson<T> extends ParserReaderHelper<T>{

public ParserReaderHelperJson(ParserAdapter<T> adapter){
    super(adapter);
}
@Override
protected defParserReader newReader(java.io.Reader reader) throws IOException{
    return new ParserReaderJson(reader);
}
@Override
public ParserReaderHelper<T> startDocument(defParserReader reader, String adapterName) throws IOException{
    reader.beginObject();
    return this;
}
@Override
public void startSheet(defParserReader reader, String adapterName) throws IOException{
    if(!adapterName.equals(reader.nextName())){
        throw new IOException();
    } else {
        reader.beginArray();
    }
}
@Override
public ParserReaderHelper<T> endSheet(defParserReader reader, String adapterName) throws IOException{
    reader.endArray();
    return this;
}
@Override
public ParserReaderHelper<T> endDocument(defParserReader reader, String adapterName) throws IOException{
    reader.endObject();
    return this;
}

}
