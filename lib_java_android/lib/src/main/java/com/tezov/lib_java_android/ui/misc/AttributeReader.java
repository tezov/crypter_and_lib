/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.misc;

import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.util.UtilsString;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import androidx.fragment.app.Fragment;
import static com.tezov.lib_java_android.application.AppResources.NOT_A_RESOURCE;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.util.ArrayMap;
import android.util.AttributeSet;

import com.tezov.lib_java_android.application.AppContext;
import com.tezov.lib_java.toolbox.Nullify;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.wrapperAnonymous.FunctionW;
import com.tezov.lib_java.type.collection.ListEntry;
import com.tezov.lib_java_android.type.primaire.Color;
import com.tezov.lib_java.type.primaire.Entry;
import com.tezov.lib_java_android.util.UtilsDimension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class AttributeReader{
public static final char TOKEN_RESOURCE_THEME = '?';

private int[] attrsIndex = null;
private ListEntry<String, Attribute> attributes = null;

public AttributeReader(){
DebugTrack.start().create(this).end();
}
public AttributeReader setAttrsIndex(int[] attrsIndex){
    this.attrsIndex = attrsIndex;
    return this;
}

public AttributeReader parse(android.content.Context context, int[] attrsIndex, AttributeSet attrs){
    setAttrsIndex(attrsIndex);
    return parse(context, attrs);
}
public AttributeReader parse(android.content.Context context, AttributeSet attrs){
    return parse(context, attrs, null);
}
public AttributeReader parse(android.content.Context context, AttributeSet attrs, String nameSpace){
    if(attributes == null){
        attributes = new ListEntry<>();
    }
    for(int end = attrs.getAttributeCount(), i = 0; i < end; i++){
        int index = attrs.getAttributeNameResource(i);
        String key = attrs.getAttributeName(i);
        Attribute attribute = null;
        int resourceId = attrs.getAttributeResourceValue(i, NOT_A_RESOURCE);
        if(resourceId == NOT_A_RESOURCE){
            String s = attrs.getAttributeValue(i);
            if((s != null) && (s.charAt(0) == TOKEN_RESOURCE_THEME)){
                Integer resourceIdResolved = AppContext.getResources().resolveAttributeId(context, Integer.parseInt(s.substring(1)), true);
                if(resourceIdResolved != null){
                    resourceId = resourceIdResolved;
                }
            }
        }
        if(resourceId == NOT_A_RESOURCE){
            String value;
            if(nameSpace != null){
                value = attrs.getAttributeValue(nameSpace, key);
            } else {
                value = attrs.getAttributeValue(i);
            }
            if(value != null){
                attribute = new Attribute(index, false, value);
            }
        } else {
            if((nameSpace == null) || (attrs.getAttributeValue(nameSpace, key) != null)){
                attribute = new Attribute(index, true, resourceId);
            }
        }
        if(attribute != null){
            if(attrsIndex == null){
                attributes.put(key, attribute);
            } else {
                for(int attrIndex: attrsIndex){
                    if(attrIndex == index){
                        attributes.put(key, attribute);
                        break;
                    }
                }
            }
        }
    }
    attributes = Nullify.collection(attributes);
    return this;
}

private Attribute getAttribute(int index){
    if(attributes == null){
        return null;
    }
    int attrIndex;
    if(attrsIndex != null){
        attrIndex = attrsIndex[index];
    } else {
        attrIndex = index;
    }
    for(Entry<String, Attribute> e: attributes){
        if(e.value.index == attrIndex){
            return e.value;
        }
    }
    return null;
}

public boolean has(int index){
    return getAttribute(index) != null;
}
public boolean has(String key){
    return attributes.getValue(key) != null;
}

public String asString(int index){
    return asString(getAttribute(index));
}
public String asString(String key){
    return asString(attributes.getValue(key));
}
private String asString(Attribute attribute){
    if(attribute == null){
        return null;
    }
    if(attribute.isReference){
        return AppContext.getResources().getString(attribute.getReference());
    } else {
        return attribute.getString();
    }
}

public Boolean isReference(int index){
    return isReference(getAttribute(index));
}
public Boolean isReference(String key){
    return isReference(attributes.getValue(key));
}
private Boolean isReference(Attribute attribute){
    if(attribute == null){
        return null;
    }
    return attribute.isReference;
}

public Integer getReference(int index){
    return getReference(getAttribute(index));
}
public Integer getReference(String key){
    return getReference(attributes.getValue(key));
}
private Integer getReference(Attribute attribute){
    if(attribute == null){
        return null;
    }
    if(attribute.isReference){
        return attribute.getReference();
    } else {

DebugException.start().log("index " + attribute.index + " is not a resource").end();


        return null;
    }
}

public Integer asInteger(int index){
    return asInteger(getAttribute(index));
}
public Integer asInteger(String key){
    return asInteger(attributes.getValue(key));
}
private Integer asInteger(Attribute attribute){
    if(attribute == null){
        return null;
    }
    if(attribute.isReference){
        return AppContext.getResources().getInteger(attribute.getReference());
    } else {
        return Integer.valueOf(attribute.getString());
    }
}

public Long asLong(int index){
    return asLong(getAttribute(index));
}
public Long asLong(String key){
    return asLong(attributes.getValue(key));
}
private Long asLong(Attribute attribute){
    if(attribute == null){
        return null;
    }
    if(attribute.isReference){

DebugException.start().log("index " + attribute.index + " is a resource, so it can not be a long").end();


        return null;
    } else {
        return Long.valueOf(attribute.getString());
    }
}

public Float asFloat(int index){
    return asFloat(getAttribute(index));
}
public Float asFloat(String key){
    return asFloat(attributes.getValue(key));
}
private Float asFloat(Attribute attribute){
    if(attribute == null){
        return null;
    }
    if(attribute.isReference){

DebugException.start().log("index " + attribute.index + " is a resource, so it can not be a float").end();

        return null;
    } else {
        return Float.valueOf(attribute.getString());
    }
}

public Double asDouble(int index){
    return asDouble(getAttribute(index));
}
public Double asDouble(String key){
    return asDouble(attributes.getValue(key));
}
private Double asDouble(Attribute attribute){
    if(attribute == null){
        return null;
    }
    if(attribute.isReference){

DebugException.start().log("index " + attribute.index + " is a resource, so it can not be a double").end();


        return null;
    } else {
        return Double.valueOf(attribute.getString());
    }
}

public Boolean asBoolean(int index){
    return asBoolean(getAttribute(index));
}
public Boolean asBoolean(String key){
    return asBoolean(attributes.getValue(key));
}
private Boolean asBoolean(Attribute attribute){
    if(attribute == null){
        return null;
    }
    if(attribute.isReference){
        return AppContext.getResources().getBoolean(attribute.getReference());
    } else {
        return Boolean.valueOf(attribute.getString());
    }
}

public Integer asDimPx(int index){
    return asDimPx(getAttribute(index));
}
public Integer asDimPx(String key){
    return asDimPx(attributes.getValue(key));
}
private Integer asDimPx(Attribute attribute){
    if(attribute == null){
        return null;
    } else {
        String s = asString(attribute);
        if(s == null){
            return null;
        } else {
            return UtilsDimension.fromToPx(s);
        }
    }
}

public Drawable asDrawable(int index){
    return asDrawable(getAttribute(index));
}
public Drawable asDrawable(String key){
    return asDrawable(attributes.getValue(key));
}
private Drawable asDrawable(Attribute attribute){
    if(attribute == null){
        return null;
    }
    if(attribute.isReference){
        return AppContext.getResources().getDrawable(attribute.getReference());
    } else {

DebugException.start().log("index " + attribute.index + " is not a resource").end();


        return null;
    }
}

public Integer asColorARGB(int index){
    return asColorARGB(getAttribute(index));
}
public Integer asColorARGB(String key){
    return asColorARGB(attributes.getValue(key));
}
private Integer asColorARGB(Attribute attribute){
    if(attribute == null){
        return null;
    }
    if(attribute.isReference){
        return AppContext.getResources().getColorARGB(attribute.getReference());
    } else {

DebugException.start().log("index " + attribute.index + " is not a resource").end();


        return null;
    }
}

public Color asColor(int index){
    return asColor(getAttribute(index));
}
public Color asColor(String key){
    return asColor(attributes.getValue(key));
}
private Color asColor(Attribute attribute){
    if(attribute == null){
        return null;
    }
    if(attribute.isReference){
        return AppContext.getResources().getColor(attribute.getReference());
    } else {

DebugException.start().log("index " + attribute.index + " is not a resource").end();


        return null;
    }
}

public ColorStateList asColorStateList(int index){
    return asColorStateList(getAttribute(index));
}
public ColorStateList asColorStateList(String key){
    return asColorStateList(attributes.getValue(key));
}
private ColorStateList asColorStateList(Attribute attribute){
    if(attribute == null){
        return null;
    }
    if(attribute.isReference){
        return AppContext.getResources().getColorStateList(attribute.getReference());
    } else {

DebugException.start().log("index " + attribute.index + " is not a resource").end();


        return null;
    }
}

private <T> FunctionW<String, T> getConverter(Class<T> type){
    if(type == String.class){
        return new FunctionW<String, T>(){
            @Override
            public T apply(String s){
                return (T)s;
            }
        };
    }
    if(type == Integer.class){
        return new FunctionW<String, T>(){
            @Override
            public T apply(String s){
                return (T)Integer.valueOf(s);
            }
        };
    }
    if(type == Long.class){
        return new FunctionW<String, T>(){
            @Override
            public T apply(String s){
                return (T)Long.valueOf(s);
            }
        };
    }
    if(type == Float.class){
        return new FunctionW<String, T>(){
            @Override
            public T apply(String s){
                return (T)Float.valueOf(s);
            }
        };
    }
    if(type == Double.class){
        return new FunctionW<String, T>(){
            @Override
            public T apply(String s){
                return (T)Double.valueOf(s);
            }
        };
    }
    if(type == Boolean.class){
        return new FunctionW<String, T>(){
            @Override
            public T apply(String s){
                return (T)Boolean.valueOf(s);
            }
        };
    }

DebugException.start().unknown("type", type).end();

    return null;
}
public <T> List<T> asArrayList(String key, Class<T> type, String splitter){
    return asArrayList(key, type, getConverter(type), splitter);
}
public <T> List<T> asArrayList(String key, Class<T> type, FunctionW<String, T> converter, String splitter){
    String s = asString(key);
    if(s == null){
        return null;
    }
    String[] l = s.split(splitter);
    if(type == String.class){
        return (List<T>)Arrays.asList(l);
    }
    List<T> values = new ArrayList<>();
    try{
        for(String value: l){
            values.add(converter.apply(value));
        }
        return Nullify.collection(values);
    } catch(java.lang.Throwable e){

DebugException.start().explode(e).end();

        return null;
    }
}
public <T> Map<String, T> asArrayMap(String key, Class<T> type, String splitterKey, String splitterEntry){
    return asArrayMap(key, type, getConverter(type), splitterKey, splitterEntry);
}
public <T> Map<String, T> asArrayMap(String key, Class<T> type, FunctionW<String, T> converter, String splitterKey, String splitterEntry){
    String s = asString(key);
    if(s == null){
        return null;
    }
    String[] l = s.split(splitterEntry);
    Map<String, T> values = new ArrayMap<>();
    try{
        for(String value: l){
            String[] entry = value.split(splitterKey);
            if(entry.length != 2){

DebugException.start().log("entry wrong size (" + entry.length + ")").end();


                return null;
            }
            values.put(entry[0], converter.apply(entry[1]));
        }
        return Nullify.map(values);
    } catch(java.lang.Throwable e){

DebugException.start().explode(e).end();

        return null;
    }
}

public DebugString toDebugString(){
    DebugString data = new DebugString();
    if(attributes == null){
        data.append("attribute set is empty");
    } else {
        for(Entry<String, Attribute> e: attributes){
            Attribute attribute = e.value;
            if(attribute.isReference){
                data.append("[" + attribute.index + ":" + e.key + ":@" + attribute.getReference() + "]");
            } else {
                data.append("[" + attribute.index + ":" + e.key + ":" + attribute.getString() + "]");
            }
        }
    }
    return data;
}
final public void toDebugLog(){
DebugLog.start().send(toDebugString()).end();
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

private static class Attribute{
    int index;
    boolean isReference;
    Object value;
    Attribute(int index, boolean isReference, Object value){
        this.index = index;
        this.isReference = isReference;
        this.value = value;
    }
    String getString(){
        return (String)value;
    }
    int getReference(){
        return (int)value;
    }

}

}
