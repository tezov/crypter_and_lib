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
import com.tezov.lib_java.generator.uid.defUid;
import com.tezov.lib_java.parser.defParserWriter;
import com.tezov.lib_java.parser.xmlPlain.ParserWriterXmlPlain;
import com.tezov.lib_java.toolbox.Nullify;
import com.tezov.lib_java.type.primitive.BytesTo;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import static com.tezov.lib_java.parser.xml.ParserAdapterXml.ATTR_NS_NULL;
import static com.tezov.lib_java.parser.xml.ParserAdapterXml.ATTR_NULL;
import static com.tezov.lib_java.parser.xml.ParserAdapterXml.TAG_ARRAY;
import static com.tezov.lib_java.parser.xml.ParserAdapterXml.TAG_OBJECT;
import static org.xmlpull.v1.XmlPullParser.NO_NAMESPACE;

public class ParserWriterXml extends ParserWriterXmlPlain implements defParserWriter{

public ParserWriterXml(OutputStream out) throws IOException{
    super(out);
}
public ParserWriterXml(Writer out) throws IOException{
    super(out);
}
protected ParserWriterXml newParserWriterXml() throws IOException{
    return new ParserWriterXml((Writer)null);
}

@Override
public ParserWriterXml copy() throws IOException{
    ParserWriterXml copy = newParserWriterXml();
    copy.xmlWriter = xmlWriter;
    copy.state = state;
    return copy;
}

@Override
public ParserWriterXml beginObject() throws IOException{
    startTag(NO_NAMESPACE, TAG_OBJECT);
    return this;
}
@Override
public ParserWriterXml endObject() throws IOException{
    endTag(NO_NAMESPACE, TAG_OBJECT);
    return this;
}
@Override
public ParserWriterXml beginArray() throws IOException{
    startTag(NO_NAMESPACE, TAG_ARRAY);
    return this;
}
@Override
public ParserWriterXml endArray() throws IOException{
    endTag(NO_NAMESPACE, TAG_ARRAY);
    return this;
}

@Override
public ParserWriterXml name(String name) throws IOException{
    return (ParserWriterXml)startTag(NO_NAMESPACE, name).endTag(NO_NAMESPACE, name);
}
public ParserWriterXml name(String nameSpace, String name) throws IOException{
    return (ParserWriterXml)startTag(nameSpace, name).endTag(nameSpace, name);
}

@Override
public ParserWriterXml value(String value) throws IOException{
    if(Nullify.string(value) != null){
        xmlWriter.text(value);
    } else {
        nullValue();
    }
    return this;
}
@Override
public ParserWriterXml nullValue() throws IOException{
    xmlWriter.attribute(ATTR_NS_NULL, ATTR_NULL, "true");
    return this;
}
@Override
public ParserWriterXml value(Boolean value) throws IOException{
    if(value != null){
        xmlWriter.text(value.toString());
    } else {
        nullValue();
    }
    return this;
}
@Override
public ParserWriterXml value(Number value) throws IOException{
    if(value != null){
        xmlWriter.text(value.toString());
    } else {
        nullValue();
    }
    return this;
}
@Override
public ParserWriterXml value(defUid value) throws IOException{
    if(value != null){
        xmlWriter.text(value.toHexString());
    } else {
        nullValue();
    }
    return this;
}
@Override
public ParserWriterXml value(byte[] value) throws IOException{
    if(value != null){
        xmlWriter.text(BytesTo.StringHex(value));
    } else {
        nullValue();
    }
    return this;
}

@Override
public ParserWriterXml write(String name, String o) throws IOException{
    ((ParserWriterXml)startTag(name)).value(o).endTag(name);
    return this;
}
@Override
public ParserWriterXml write(String name, Number o) throws IOException{
    ((ParserWriterXml)startTag(name)).value(o).endTag(name);
    return this;
}
@Override
public ParserWriterXml write(String name, Boolean o) throws IOException{
    ((ParserWriterXml)startTag(name)).value(o).endTag(name);
    return this;
}
@Override
public ParserWriterXml write(String name, byte[] o) throws IOException{
    ((ParserWriterXml)startTag(name)).value(o).endTag(name);
    return this;
}
@Override
public ParserWriterXml write(String name, defUid o) throws IOException{
    ((ParserWriterXml)startTag(name)).value(o).endTag(name);
    return this;
}
@Override
public ParserWriterXml writeNull(String name) throws IOException{
    ((ParserWriterXml)startTag(name)).nullValue().endTag(name);
    return this;
}

@Override
public void flush() throws IOException{
    xmlWriter.flush();
}
@Override
public void close() throws IOException{
    if(writer != null){
        writer.close();
        writer = null;
    }
}
@Override
public boolean canBeClosed(){
    return writer != null;
}


}
