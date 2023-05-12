/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.parser.xmlPlain;

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.util.UtilsString;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import java.nio.charset.StandardCharsets;

import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugTrack;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.xmlpull.v1.XmlPullParser.END_DOCUMENT;
import static org.xmlpull.v1.XmlPullParser.END_TAG;
import static org.xmlpull.v1.XmlPullParser.NO_NAMESPACE;
import static org.xmlpull.v1.XmlPullParser.START_TAG;
import static org.xmlpull.v1.XmlPullParser.TEXT;

public class ParserReaderXmlPlain{
protected java.io.Reader reader;
protected XmlPullParser xmlReader;
protected State state;

public ParserReaderXmlPlain(InputStream in) throws IOException{
    this(new InputStreamReader(in, StandardCharsets.UTF_8));
}
public ParserReaderXmlPlain(java.io.Reader in) throws IOException{
DebugTrack.start().create(this).end();
    try{
        this.reader = in;
        if(in != null){
            XmlPullParserFactory xppFactory = XmlPullParserFactory.newInstance();
            xppFactory.setNamespaceAware(true);
            this.xmlReader = xppFactory.newPullParser();
            this.xmlReader.setInput(in);
            this.state = newState();
        }
    } catch(XmlPullParserException e){
        throw new IOException(e);
    }
}
protected State newState(){
    return new State();
}
protected ParserReaderXmlPlain newParserReaderXml() throws IOException{
    return new ParserReaderXmlPlain((java.io.Reader)null);
}
protected State state(){
    return state;
}

private int getXmlReaderEventType() throws IOException{
    try{
        return xmlReader.getEventType();
    } catch(XmlPullParserException e){

DebugException.start().log(e).end();

        throw new IOException("XmlPullParserException");
    }
}
public int getEventType(){
    return state().currentEvent;
}
public boolean isWhiteSpace() throws IOException{
    try{
        return xmlReader.isWhitespace();
    } catch(XmlPullParserException e){

DebugException.start().log(e).end();

        throw new IOException("XmlPullParserException");
    }
}
public void next() throws IOException{
    try{
        if(state().skipFutureNext){
            state().skipFutureNext = false;
            state().currentEvent = getXmlReaderEventType();
            state().currentDepth = xmlReader.getDepth();
            state().currentText = xmlReader.getText();
        } else {
            int previousEvent = state().currentEvent;
            xmlReader.next();
            state().currentEvent = getXmlReaderEventType();
            state().currentDepth = xmlReader.getDepth();
            state().currentText = xmlReader.getText();
            if(state().currentEvent == TEXT){
                xmlReader.next();
                int futureEvent = getXmlReaderEventType();
                if((previousEvent == START_TAG) && (futureEvent == END_TAG)){
                    state().skipFutureNext = true;
                } else {
                    state().currentEvent = futureEvent;
                    state().currentDepth = xmlReader.getDepth();
                    state().currentText = xmlReader.getText();
                }
            }
        }

    } catch(XmlPullParserException e){

DebugException.start().log(e).end();

        throw new IOException("XmlPullParserException");
    }
}
public void nextToken() throws IOException{
    try{
        xmlReader.nextToken();
        state().currentEvent = getXmlReaderEventType();
        state().currentDepth = xmlReader.getDepth();
        state().currentText = xmlReader.getText();
    } catch(XmlPullParserException e){

DebugException.start().log(e).end();

        throw new IOException("XmlPullParserException");
    }
}
public String getName(){
    return xmlReader.getName();
}
public String getText(){
    return state().currentText;
}
public int getAttributeCount(){
    return xmlReader.getAttributeCount();
}
public String getAttributeValue(String nameSpace, String name){
    return xmlReader.getAttributeValue(nameSpace, name);
}
public String getAttributeValue(String name){
    return xmlReader.getAttributeValue(NO_NAMESPACE, name);
}
public String getAttributeValue(int index){
    return xmlReader.getAttributeValue(index);
}
public String getAttributeName(int index){
    return xmlReader.getAttributeName(index);
}
public String getAttributeNameSpace(int index){
    return xmlReader.getAttributeNamespace(index);
}
public int getDepth(){
    return state().currentDepth;
}

public String nextInstruction() throws IOException{
    try{
        do{
            xmlReader.nextToken();
            int type = xmlReader.getEventType();
            if(type == XmlPullParser.PROCESSING_INSTRUCTION){
                return xmlReader.getText();
            }
            if((type == START_TAG) || (type == END_DOCUMENT)){
                state().currentEvent = getXmlReaderEventType();
                state().currentDepth = xmlReader.getDepth();
                state().currentText = xmlReader.getText();
                return null;
            }
        } while(true);
    } catch(XmlPullParserException e){

DebugException.start().log(e).end();

        throw new IOException("XmlPullParserException");
    }
}

public ParserReaderXmlPlain nextStartTag() throws IOException{
    while((getEventType() != START_TAG)){
        next();
    }
    return this;
}
public ParserReaderXmlPlain nextStartTag(String tag) throws IOException{
    while((getEventType() != START_TAG) || !tag.equals(getName())){
        next();
    }
    return this;
}
public ParserReaderXmlPlain nextEndTag() throws IOException{
    while((getEventType() != END_TAG)){
        next();
    }
    return this;
}
public ParserReaderXmlPlain nextEndTag(String tag) throws IOException{
    while((getEventType() != END_TAG) || !tag.equals(getName())){
        next();
    }
    return this;
}

public void close() throws IOException{
    reader.close();
    reader = null;
}

@Override
public String toString(){
    return xmlReader.toString();
}
@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

protected static class State{
    protected boolean skipFutureNext = false;
    protected int currentEvent = XmlPullParser.START_DOCUMENT;
    protected int currentDepth = 0;
    protected String currentText = null;

}

}
