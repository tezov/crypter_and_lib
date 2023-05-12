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
import com.tezov.lib_java.generator.uid.UidBase;
import com.tezov.lib_java.generator.uid.defUid;
import com.tezov.lib_java.parser.defParserReader;
import com.tezov.lib_java.parser.xmlPlain.ParserReaderXmlPlain;
import com.tezov.lib_java.type.primitive.string.StringHexTo;

import org.apache.commons.lang3.math.NumberUtils;

import java.io.IOException;
import java.io.InputStream;

import static com.tezov.lib_java.parser.xml.ParserAdapterXml.ATTR_NS_NULL;
import static com.tezov.lib_java.parser.xml.ParserAdapterXml.ATTR_NULL;
import static com.tezov.lib_java.parser.xml.ParserAdapterXml.TAG_ARRAY;
import static com.tezov.lib_java.parser.xml.ParserAdapterXml.TAG_OBJECT;
import static org.xmlpull.v1.XmlPullParser.END_TAG;
import static org.xmlpull.v1.XmlPullParser.START_TAG;
import static org.xmlpull.v1.XmlPullParser.TEXT;

public class ParserReaderXml extends ParserReaderXmlPlain implements defParserReader{

public ParserReaderXml(InputStream in) throws IOException{
    super(in);
}
public ParserReaderXml(java.io.Reader in) throws IOException{
    super(in);
}

protected ParserReaderXml newParserReaderXml() throws IOException{
    return new ParserReaderXml((java.io.Reader)null);
}

@Override
public ParserReaderXml copy() throws IOException{
    ParserReaderXml copy = newParserReaderXml();
    copy.xmlReader = xmlReader;
    copy.state = state;
    return copy;
}

@Override
public ParserReaderXml beginArray() throws IOException{
    next();
    return this;
}
@Override
public ParserReaderXml endArray() throws IOException{
    next();
    return this;
}
@Override
public ParserReaderXml beginObject() throws IOException{
    next();
    return this;
}
@Override
public ParserReaderXml endObject() throws IOException{
    next();
    return this;
}

@Override
public boolean isName() throws IOException{
    return peek() == Token.NAME;
}
@Override
public boolean isBeginObject() throws IOException{
    return peek() == Token.BEGIN_OBJECT;
}
@Override
public boolean isNotEndObject() throws IOException{
    return peek() != Token.END_OBJECT;
}
@Override
public boolean isBeginArray() throws IOException{
    return peek() == Token.BEGIN_ARRAY;
}
@Override
public boolean isNotEndArray() throws IOException{
    return peek() != Token.END_ARRAY;
}

@Override
public Token peek() throws IOException{
    Token token;
    int event = getEventType();
    switch(event){
        case START_TAG:{
            String s = getName();
            if(TAG_ARRAY.equals(s)){
                token = Token.BEGIN_ARRAY;
            } else if(TAG_OBJECT.equals(s)){
                token = Token.BEGIN_OBJECT;
            } else {
                token = Token.NAME;
            }
        }
        break;
        case TEXT:{
            String s = getName();
            if(NumberUtils.isParsable(s)){
                token = Token.NUMBER;
            } else if(Boolean.toString(true).equals(s) || Boolean.toString(false).equals(s)){
                token = Token.BOOLEAN;
            } else {
                token = Token.STRING;
            }
        }
        break;
        case END_TAG:{
            if((getAttributeCount() > 0) && Boolean.toString(true).equals(getAttributeValue(ATTR_NS_NULL, ATTR_NULL))){
                token = Token.NULL;
            } else {
                String s = getName();
                if(TAG_ARRAY.equals(s)){
                    token = Token.END_ARRAY;
                } else if(TAG_OBJECT.equals(s)){
                    token = Token.END_OBJECT;
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
    String name = getName();
    boolean nextIfEndTag = true;
    if(getAttributeCount() > 0){
        String attrNull = getAttributeValue(ATTR_NS_NULL, ATTR_NULL);
        if(Boolean.toString(true).equals(attrNull)){
            nextIfEndTag = false;
        }
    }
    next();
    if(nextIfEndTag && (getEventType() == END_TAG)){
        next();
    }
    return name;
}
@Override
public String nextString() throws IOException{
    String s = getText();
    next();
    next();
    return s;
}
@Override
public boolean nextBoolean() throws IOException{
    return Boolean.parseBoolean(nextString());
}
@Override
public void nextNull() throws IOException{
    next();
}
@Override
public double nextDouble() throws IOException{
    return Double.parseDouble(nextString());
}
@Override
public long nextLong() throws IOException{
    return Long.parseLong(nextString());
}
@Override
public int nextInt() throws IOException{
    return Integer.parseInt(nextString());
}
@Override
public defUid nextUid() throws IOException{
    return UidBase.fromHexString(nextString());
}
@Override
public byte[] nextBytes() throws IOException{
    return StringHexTo.Bytes(nextString());
}
@Override
public void skipValue() throws IOException{
    next();
    next();
}
@Override
public void close() throws IOException{
    if(canBeClosed()){
        reader.close();
        reader = null;
    }
}
@Override
public boolean canBeClosed(){
    return reader != null;
}


}
