/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.type.defEnum;

import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.util.UtilsString;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java.application.AppRandomNumber;
import com.tezov.lib_java.generator.NumberGenerator;
import com.tezov.lib_java.toolbox.Nullify;
import com.tezov.lib_java.toolbox.Reflection;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java.debug.DebugTrack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnumBase{
private static final NumberGenerator uidGenerator = new NumberGenerator();
private static final Map<Class<Is>, List<Is>> values = new HashMap<>();
private static final Is NULL = new Is("NULL");

private EnumBase(){
}

private static int nextId(){
    return uidGenerator.nextInt();
}

synchronized private static void setLastIdIfGreater(int ordinal){
    if(uidGenerator.getLast() < ordinal){
        uidGenerator.setLast(ordinal);
    }
}

private static void addValue(Is is){
    Class<Is> type = (Class<Is>)is.getClass();
    List<Is> values = getValuesTypeOf(type);
    if(values == null){
        values = new ArrayList<>();
        EnumBase.values.put(type, values);
    }
    values.add(is);
}

private static void removeValue(Is is){
    Class<Is> type = (Class<Is>)is.getClass();
    List<Is> values = getValuesTypeOf(type);
    if(values == null){
        return;
    }
    values.remove(is);
    if(values.size() <= 0){
        EnumBase.values.remove(type);
    }
}

private static <I extends Is> List<I> getValuesTypeOf(Class<I> typeToFind){
    return (List<I>)EnumBase.values.get(typeToFind);
}

private static <I extends Is> List<I> getValuesInstanceOf(Class<I> typeToFind){
    List<I> values = new ArrayList<>();
    for(Map.Entry<Class<Is>, List<Is>> e: EnumBase.values.entrySet()){
        Class<I> typeToIdentify = (Class<I>)e.getKey();
        if(typeToIdentify == EnumBase.Is.class){
            continue;
        }
        if(Reflection.isInstanceOf(typeToIdentify, typeToFind)){
            values.addAll((List<I>)e.getValue());
        }
    }
    return Nullify.collection(values);
}

private static <I extends Is> I nextRandom(Class<I> type){
    List<I> values = getValuesTypeOf(type);
    if(values == null){
        return null;
    }
    return values.get(AppRandomNumber.nextInt(values.size()));
}

private static <I extends Is> boolean contains(Class<I> type, String name){
    List<I> values = getValuesTypeOf(type);
    if(values == null){
        return false;
    }
    for(Is is: values){
        if(is.name.equals(name)){
            return true;
        }
    }
    return false;
}

private static <I extends Is> boolean contains(Class<I> type, int ordinal){
    List<I> values = getValuesTypeOf(type);
    if(values == null){
        return false;
    }
    for(Is is: values){
        if(is.ordinal == ordinal){
            return true;
        }
    }
    return false;
}

private static <I extends Is> I findTypeOf(Class<I> type, String name){
    List<I> values = getValuesTypeOf(type);
    if(values == null){
        return null;
    }
    for(Is is: values){
        if(is.name.equals(name)){
            return (I)is;
        }
    }
    return null;
}

private static <I extends Is> I findTypeOf(Class<I> type, int ordinal){
    List<I> values = getValuesTypeOf(type);
    if(values == null){
        return null;
    }
    for(Is is: values){
        if(is.ordinal == ordinal){
            return (I)is;
        }
    }
    return null;
}

private static <I extends Is> I findInstanceOf(Class<I> type, String name){
    List<I> values = getValuesInstanceOf(type);
    if(values == null){
        return null;
    }
    for(Is is: values){
        if(is.name.equals(name)){
            return (I)is;
        }
    }
    return null;
}

private static <I extends Is> I findInstanceOf(Class<I> type, int ordinal){
    List<I> values = getValuesInstanceOf(type);
    if(values == null){
        return null;
    }
    for(Is is: values){
        if(is.ordinal == ordinal){
            return (I)is;
        }
    }
    return null;
}

public static void toDebugLog(){
    for(Map.Entry<Class<Is>, List<Is>> e: values.entrySet()){
        List<Is> values = e.getValue();
DebugLog.start().send("\n\n*** " + DebugTrack.getFullSimpleName(values.get(0))).end();
        for(Is is: values){
            is.toDebugLog();
        }
    }
}

public static <I extends Is> void toDebugLogClassOf(Class<I> type){
    List<I> values = getValuesTypeOf(type);
    if(values == null){
DebugLog.start().send("values is null").end();
        return;
    }
DebugLog.start().send("\n\n*** " + DebugTrack.getFullSimpleName(values.get(0))).end();
    for(Is is: values){
        is.toDebugLog();
    }
}

public static <I extends Is> void toDebugLogInstanceOf(Class<I> type){
    List<I> values = getValuesInstanceOf(type);
    if(values == null){
DebugLog.start().send("values is null").end();
        return;
    }
DebugLog.start().send("\n\n*** " + DebugTrack.getFullSimpleName(values.get(0))).end();
    for(Is is: values){
        is.toDebugLog();
    }
}

public static class Is{
    final private int ordinal;
    final private String name;
    public Is(String name){
        trackClassCreate();

        if(contains(this.getClass(), name)){
DebugException.start().explode("Enum class " + DebugTrack.getFullName(this) + ":" + name + " already exist, check duplicate name").end();
        }


        addValue(this);
        this.ordinal = nextId();
        this.name = name;
    }
    public Is(String name, int ordinal){
        trackClassCreate();

        if(contains(this.getClass(), name)){
DebugException.start().explode("Enum class " + DebugTrack.getFullName(this) + ":" + name + " already exist, check duplicate name").end();
        }
        if(contains(this.getClass(), ordinal)){
DebugException.start().explode("Enum class " + DebugTrack.getFullName(this) + ":" + name + " already exist, check duplicate ordinal:" + ordinal).end();
        }


        addValue(this);
        this.ordinal = ordinal;
        this.name = name;
        setLastIdIfGreater(ordinal);
    }
    public static <I extends Is> I findTypeOf(Class<I> type, int ordinal){
        return EnumBase.findTypeOf(type, ordinal);
    }
    public static <I extends Is> I findTypeOf(Class<I> type, String name){
        return EnumBase.findTypeOf(type, name);
    }
    public static <I extends Is> I findInstanceOf(Class<I> type, int ordinal){
        return EnumBase.findInstanceOf(type, ordinal);
    }
    public static <I extends Is> I findInstanceOf(Class<I> type, String name){
        return EnumBase.findInstanceOf(type, name);
    }
    public static <I extends Is> List<I> getValuesTypeOf(Class<I> type){
        return EnumBase.getValuesTypeOf(type);
    }
    public static <I extends Is> List<I> getValuesInstanceOf(Class<I> type){
        return EnumBase.getValuesInstanceOf(type);
    }
    public static <I extends Is> I nextRandom(Class<I> type){
        return EnumBase.nextRandom(type);
    }
    public static Is NULL(){
        return NULL;
    }
    private void trackClassCreate(){
DebugTrack.start().create(this).end();
    }
    public String name(){
        return name;
    }
    public int ordinal(){
        return ordinal;
    }

    @Override
    public int hashCode(){
        return ordinal;
    }

    @Override
    public boolean equals(Object obj){
        if(!(obj instanceof Is)){
            return false;
        } else {
            return ordinal == ((Is)obj).ordinal;
        }
    }

    @Override
    public String toString(){
        return name();
    }
    public DebugString toDebugString(){
        DebugString data = new DebugString();
        data.append("name", name);
        data.append("ordinal", ordinal);
        return data;
    }

    final public void toDebugLog(){
DebugLog.start().send(toDebugString()).end();
    }

    @Override
    protected void finalize() throws Throwable{
        removeValue(this);
DebugTrack.start().destroy(this).end();
        super.finalize();
    }

}

}
