/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.parser.xmlPlain;

import com.tezov.lib_java.debug.DebugLog;
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
import java.nio.charset.StandardCharsets;

import android.util.Xml;

import com.tezov.lib_java.debug.DebugTrack;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import static org.xmlpull.v1.XmlPullParser.NO_NAMESPACE;

public class ParserWriterXmlPlain{
protected java.io.Writer writer;
protected XmlSerializer xmlWriter;
protected State state;

public ParserWriterXmlPlain(OutputStream out) throws IOException{
    this(new OutputStreamWriter(out, StandardCharsets.UTF_8));
}
public ParserWriterXmlPlain(java.io.Writer out) throws IOException{
DebugTrack.start().create(this).end();
    try{
        this.writer = out;
        if(out != null){
            this.xmlWriter = Xml.newSerializer();
            this.xmlWriter.setOutput(out);
            this.state = newState();
        }
    } catch(Throwable e){
        throw new IOException(e);
    }
}

protected State newState(){
    return null;
}
protected ParserWriterXmlPlain newParserWriterXml() throws IOException{
    return new ParserWriterXmlPlain((java.io.Writer)null);
}
protected State state(){
    return state;
}

public ParserWriterXmlPlain enableIndent(boolean flag){
    return setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", flag);
}

public ParserWriterXmlPlain setFeature(String name, boolean state){
    xmlWriter.setFeature(name, state);
    return this;
}
public ParserWriterXmlPlain setProperty(String name, Object value){
    xmlWriter.setProperty(name, value);
    return this;
}

public ParserWriterXmlPlain instruction(String text) throws IOException{
    xmlWriter.processingInstruction(text);
    return this;
}
public ParserWriterXmlPlain docType(String text) throws IOException{
    xmlWriter.docdecl(text);
    return this;
}
public ParserWriterXmlPlain cdData(String text) throws IOException{
    xmlWriter.cdsect(text);
    return this;
}
public ParserWriterXmlPlain entityDeclaration(String name, String value) throws IOException{
    xmlWriter.ignorableWhitespace("<!ENTITY " + name + " \"" + value + "\">");
    return this;
}
public ParserWriterXmlPlain entityRef(String name) throws IOException{
    xmlWriter.entityRef(name);
    return this;
}
public ParserWriterXmlPlain ignorableWhitespace(String text) throws IOException{
    xmlWriter.ignorableWhitespace(text);
    return this;
}

public ParserWriterXmlPlain comment(String text) throws IOException{
    xmlWriter.comment(text);
    return this;
}

public ParserWriterXmlPlain startDocument(String encoding, Boolean standalone) throws IOException{
    xmlWriter.startDocument(encoding, standalone);
    return this;
}
public ParserWriterXmlPlain endDocument() throws IOException{
    xmlWriter.endDocument();
    return this;
}

public ParserWriterXmlPlain setPrefix(String prefix, String namespace) throws IOException{
    xmlWriter.setPrefix(prefix, namespace);
    return this;
}
public ParserWriterXmlPlain startTag(String name) throws IOException{
    xmlWriter.startTag(NO_NAMESPACE, name);
    return this;
}
public ParserWriterXmlPlain startTag(String namespace, String name) throws IOException{
    xmlWriter.startTag(namespace, name);
    return this;
}
public ParserWriterXmlPlain attribute(String name, String value) throws IOException{
    xmlWriter.attribute(NO_NAMESPACE, name, value);
    return this;
}
public ParserWriterXmlPlain attribute(String namespace, String name, String value) throws IOException{
    xmlWriter.attribute(namespace, name, value);
    return this;
}

public ParserWriterXmlPlain endTag(String name) throws IOException{
    xmlWriter.endTag(NO_NAMESPACE, name);
    return this;
}
public ParserWriterXmlPlain endTag(String namespace, String name) throws IOException{
    xmlWriter.endTag(namespace, name);
    return this;
}

public void close() throws IOException{
    writer.close();
    writer = null;
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

protected static class State{

}

}
