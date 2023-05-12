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
import com.tezov.lib_java.parser.xml.ParserReaderXml;
import com.tezov.lib_java.debug.DebugException;

import org.apache.commons.lang3.math.NumberUtils;
import org.xmlpull.v1.XmlPullParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayDeque;

import static com.tezov.lib_java.parser.xml.ParserAdapterXml.ATTR_NS_NULL;
import static com.tezov.lib_java.parser.xml.ParserAdapterXml.ATTR_NULL;
import static com.tezov.lib_java.parser.xmlExcel.ParserAdapterXmlExcel.ATTR_NS_TEZOV;
import static com.tezov.lib_java.parser.xmlExcel.ParserAdapterXmlExcel.ATTR_TEZOV_NAME;
import static com.tezov.lib_java.parser.xmlExcel.ParserAdapterXmlExcel.ATTR_TEZOV_TYPE;
import static com.tezov.lib_java.parser.xmlExcel.ParserAdapterXmlExcel.ATTR_TEZOV_TYPE_BEGIN;
import static com.tezov.lib_java.parser.xmlExcel.ParserAdapterXmlExcel.ATTR_TEZOV_TYPE_END;
import static com.tezov.lib_java.parser.xmlExcel.ParserAdapterXmlExcel.ATTR_TEZOV_TYPE_NAME;
import static com.tezov.lib_java.parser.xmlExcel.ParserAdapterXmlExcel.NAME_ARRAY;
import static com.tezov.lib_java.parser.xmlExcel.ParserAdapterXmlExcel.NAME_OBJECT;
import static com.tezov.lib_java.parser.xmlExcel.ParserAdapterXmlExcel.TAG_ROW;
import static com.tezov.lib_java.parser.xmlExcel.ParserAdapterXmlExcel.TAG_TABLE;

public class ParserReaderXmlExcel extends ParserReaderXml{

public ParserReaderXmlExcel(InputStream in) throws IOException{
    super(in);
}
public ParserReaderXmlExcel(Reader in) throws IOException{
    super(in);
}

@Override
protected ParserReaderXml newParserReaderXml() throws IOException{
    return new ParserReaderXmlExcel((Reader)null);
}
@Override
protected ParserReaderXml.State newState(){
    return new State();
}
@Override
protected State state(){
    return (State)super.state();
}

private void startCell() throws IOException{
    next();
    next();
}
private void endCell() throws IOException{
    next();
    next();
    next();
}

public void moveToNextRow() throws IOException{
    do{
        int event = getEventType();
        if(event == XmlPullParser.START_TAG){
            if(TAG_ROW.equals(getName())){
                return;
            }
        } else if(event == XmlPullParser.END_TAG){
            if(TAG_TABLE.equals(getName())){
                return;
            }
        }
        next();
    } while(true);
}

@Override
public ParserReaderXml beginObject() throws IOException{
    if((state().objectOpenedLevel == 0) && (state().arrayOpenedLevel == 0)){
        next();
    } else {
        startCell();
        nextString();
    }
    state().objectOpenedLevel++;
    state().lastOpenedBlocks.addLast(Token.BEGIN_OBJECT);
    return this;
}
@Override
public ParserReaderXml endObject() throws IOException{
    if((state().objectOpenedLevel == 1) && (state().arrayOpenedLevel == 0)){
        next();
    } else {
        startCell();
        nextString();
    }
    state().objectOpenedLevel--;
    Token lastOpened = state().lastOpenedBlocks.removeLast();

    if(lastOpened != Token.BEGIN_OBJECT){
DebugException.start().log("tag " + lastOpened.name() + " in stack different from expected").end();
    }

    return this;
}
@Override
public ParserReaderXml beginArray() throws IOException{
    if((state().arrayOpenedLevel == 0) && (state().objectOpenedLevel == 0)){
        next();
    } else {
        startCell();
        nextString();
    }
    state().arrayOpenedLevel++;
    state().lastOpenedBlocks.addLast(Token.BEGIN_OBJECT);
    return this;
}
@Override
public ParserReaderXml endArray() throws IOException{
    if((state().arrayOpenedLevel == 1) && (state().objectOpenedLevel == 0)){
        next();
    } else {
        startCell();
        nextString();
    }
    state().arrayOpenedLevel--;
    Token lastOpened = state().lastOpenedBlocks.removeLast();

    if(lastOpened != Token.BEGIN_ARRAY){
DebugException.start().log("tag " + lastOpened.name() + " in stack different from expected").end();
    }

    return this;
}

@Override
public Token peek() throws IOException{
    Token token;
    int event = getEventType();
    switch(event){
        case XmlPullParser.START_TAG:{
            String name = getAttributeValue(ATTR_NS_TEZOV, ATTR_TEZOV_NAME);
            if(NAME_OBJECT.equals(name)){
                String type = getAttributeValue(ATTR_NS_TEZOV, ATTR_TEZOV_TYPE);
                if((type == null) || ATTR_TEZOV_TYPE_BEGIN.equals(type)){
                    token = Token.BEGIN_OBJECT;
                } else if(ATTR_TEZOV_TYPE_END.equals(type)){
                    token = Token.END_OBJECT;
                } else {
                    throw new IOException();
                }
            } else if(NAME_ARRAY.equals(name)){
                String type = getAttributeValue(ATTR_NS_TEZOV, ATTR_TEZOV_TYPE);
                if((type == null) || ATTR_TEZOV_TYPE_BEGIN.equals(type)){
                    token = Token.BEGIN_ARRAY;
                } else if(ATTR_TEZOV_TYPE_END.equals(type)){
                    token = Token.END_ARRAY;
                } else {
                    throw new IOException();
                }
            } else if(name != null){
                token = Token.NAME;
            } else if(TAG_TABLE.equals(getName())){
                token = Token.BEGIN;
            } else {
                throw new IOException();
            }
        }
        break;
        case XmlPullParser.TEXT:{
            String s = getText();
            if(NumberUtils.isParsable(s)){
                token = Token.NUMBER;
            } else if(Boolean.toString(true).equals(s) || Boolean.toString(false).equals(s)){
                token = Token.BOOLEAN;
            } else {
                token = Token.STRING;
            }
        }
        break;
        case XmlPullParser.END_TAG:{
            if((getAttributeCount() > 0) && Boolean.toString(true).equals(getAttributeValue(ATTR_NS_NULL, ATTR_NULL))){
                token = Token.NULL;
            } else {
                String s = getName();
                if(TAG_ROW.equals(s)){
                    Token lastOpened = state().lastOpenedBlocks.peekLast();
                    if(lastOpened == Token.BEGIN_OBJECT){
                        token = Token.END_OBJECT;
                    } else if(lastOpened == Token.BEGIN_ARRAY){
                        token = Token.END_ARRAY;
                    } else {
                        throw new IOException();
                    }
                } else if(TAG_TABLE.equals(s)){
                    token = Token.END;
                } else {
                    throw new IOException();
                }
            }
        }
        break;
        default:{
            throw new IOException();
        }
    }
    return token;
}
@Override
public String nextName() throws IOException{
    String s = getAttributeValue(ATTR_NS_TEZOV, ATTR_TEZOV_NAME);
    String type = getAttributeValue(ATTR_NS_TEZOV, ATTR_TEZOV_TYPE);
    if(ATTR_TEZOV_TYPE_NAME.equals(type)){
        startCell();
        endCell();
    } else {
        boolean nextIfEndTag = true;
        next();
        if(getAttributeCount() > 0){
            String attrNull = getAttributeValue(ATTR_NS_NULL, ATTR_NULL);
            if(Boolean.toString(true).equals(attrNull)){
                nextIfEndTag = false;
            }
        }
        next();
        if(nextIfEndTag && (getEventType() == XmlPullParser.END_TAG)){
            next();
            next();
        }
    }
    return s;
}

@Override
public String nextString() throws IOException{
    String s = getText();
    endCell();
    return s;
}
@Override
public void nextNull() throws IOException{
    next();
    next();
}
@Override
public void skipValue() throws IOException{
    endCell();
}

protected static class State extends ParserReaderXml.State{
    private final ArrayDeque<Token> lastOpenedBlocks = new ArrayDeque<>();
    private int objectOpenedLevel = 0;
    private int arrayOpenedLevel = 0;

}

}

