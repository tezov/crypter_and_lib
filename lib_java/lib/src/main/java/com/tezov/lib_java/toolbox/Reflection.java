/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.toolbox;

import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.util.UtilsString;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java.BuildConfig;

import java.lang.reflect.Modifier;

import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugTrack;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Reflection{
private Reflection(){
}

public static Boolean isClassName(String className){
    Class<?> type = getClass(className);
    return (type != null);
}
public static Class getClass(String className){
    try{
        return Class.forName(className);
    } catch(java.lang.Throwable e){

DebugException.start().log(e).end();

    }
    return null;
}
private static Class[] getTypes(List<Object> args){
    Class[] typeArgs = new Class[args.size()];
    for(int i = 0; i < args.size(); i++){
        typeArgs[i] = args.get(i).getClass();
    }
    return typeArgs;
}
public static boolean hasInterface(Class typeToIdentify, Class type){
    if((typeToIdentify == null) || (type == null)){
        return false;
    }
    Class superClass = typeToIdentify;
    do{
        if(superClass == type){
            return true;
        }
        Class[] itfces = superClass.getInterfaces();
        for(Class it: itfces){
            if(it == type){
                return true;
            }
        }
    } while((superClass = superClass.getSuperclass()) != null);
    return false;
}
public static boolean hasSuperClass(Class typeToIdentify, Class type){
    if((typeToIdentify == null) || (type == null)){
        return false;
    }
    Class superClass = typeToIdentify;
    do{
        if(superClass == type){
            return true;
        }
    } while((superClass = superClass.getSuperclass()) != null);
    return false;
}
public static boolean hasSuperClass(Object objectToIdentify, Class type){
    if(objectToIdentify == null){
        return false;
    }
    if(objectToIdentify instanceof Class){
        return hasSuperClass((Class)objectToIdentify, type);
    } else {
        return hasSuperClass(objectToIdentify.getClass(), type);
    }
}
public static boolean isInstanceOf(Object objectToIdentify, Class type){
    if((objectToIdentify == null) || (type == null)){
        return false;
    }
    return type.isInstance(objectToIdentify);
}
public static boolean isInstanceOf(Class typeToIdentify, Class type){
    return hasSuperClass(typeToIdentify, type) || hasInterface(typeToIdentify, type);
}
public static boolean areInstanceOf(List objectsToIdentify, List<Class> types){
    if(objectsToIdentify.size() != types.size()){
        return false;
    }
    for(int i = 0; i < types.size(); i++){
        if(!isInstanceOf(objectsToIdentify.get(i), types.get(i))){
            return false;
        }
    }
    return true;
}
public static boolean areTypeOf(List objectsToIdentify, List<Class> types){
    if(objectsToIdentify.size() != types.size()){
        return false;
    }
    for(int i = 0; i < types.size(); i++){
        Object o = objectsToIdentify.get(i);
        Class typeToIdentify;
        if(o instanceof Class){
            typeToIdentify = (Class)o;
        } else {
            typeToIdentify = o.getClass();
        }
        if(typeToIdentify != types.get(i)){
            return false;
        }
    }
    return true;
}

private static <T> Constructor<T> findConstructor(Class<T> type, List<Object> argsTypes){
    Constructor<?>[] constructors = type.getDeclaredConstructors();
    for(Constructor<?> constructor: constructors){
        Type[] types = constructor.getParameterTypes();
        if(areInstanceOf(argsTypes, Arrays.asList((Class[])types))){
            return (Constructor<T>)constructor;
        }
    }
    return null;
}

public static <T> T newInstance(Class<T> type, List<Object> args){
    try{
        args = Nullify.collection(args);
        if(args == null){
            Constructor<T> constructor = type.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } else {
            Constructor<T> constructor = findConstructor(type, args);
            constructor.setAccessible(true);
            return constructor.newInstance(args.toArray(new Object[0]));
        }
    } catch(java.lang.Throwable e){
        if(BuildConfig.DEBUG_ONLY){
            StringBuilder data = new StringBuilder();
            data.append("Failed to create new Instance: ").append(DebugTrack.getFullSimpleName(e)).append(" ").append(type.getName()).append("(");
            if(args != null){
                for(Object o: args){
                    data.append(o.getClass().getName()).append(", ");
                }
            }
            if(!data.substring(data.length() - 1, data.length()).equals("(")){
                data.replace(data.length() - 2, data.length(), "");
            }
            data.append(")");
DebugException.start().explode(data.toString()).end();
        }
        return null;
    }
}
public static <T> T newInstance(Class<T> type, Object[] args){
    return newInstance(type, Arrays.asList(args));
}
public static <T> T newInstance(Class<T> type, Object arg){
    return newInstance(type, (arg != null) ? Collections.singletonList(arg) : null);
}
public static <T> T newInstance(Class<T> type){
    try{
        Constructor<T> constructor = type.getDeclaredConstructor();
        constructor.setAccessible(true);
        return constructor.newInstance();
    } catch(java.lang.Throwable e){

DebugException.start().log(e).end();

    }
    return null;
}

public static List<Method> findMethods(Class fromClass, Class returnType, List<Class> argsTypes, boolean exactType){
    try{
        List<Method> l = new ArrayList<>();
        Method[] methods = fromClass.getMethods();
        if(exactType){
            for(Method m: methods){
                if((returnType != null) && (m.getReturnType() != returnType)){
                    continue;
                }
                if((argsTypes != null) && !areInstanceOf(argsTypes, Arrays.asList(m.getParameterTypes()))){
                    continue;
                }
                l.add(m);
            }
        } else {
            for(Method m: methods){
                if((returnType != null) && (!isInstanceOf(m.getReturnType(), returnType))){
                    continue;
                }
                if((argsTypes != null) && !areTypeOf(argsTypes, Arrays.asList(m.getParameterTypes()))){
                    continue;
                }
                l.add(m);
            }
        }
        return Nullify.collection(l);
    } catch(java.lang.Throwable e){

DebugException.start().log(e).end();

        return null;
    }
}
public static Method findMethod(Class fromClass, Class returnType, List<Class> argsTypes, boolean exactType){
    List<Method> l = findMethods(fromClass, returnType, argsTypes, exactType);
    if(l == null){
        return null;
    }
    return l.get(0);
}
public static Method findMethod(Class fromClass, Class returnType, boolean exactType){
    return findMethod(fromClass, returnType, null, exactType);
}
public static Method findMethod(Class fromClass, List<Class> argsTypes, boolean exactType){
    return findMethod(fromClass, null, argsTypes, exactType);
}
public static Method findMethod(Class fromClass, Class returnType){
    return findMethod(fromClass, returnType, null, false);
}
public static Method findMethod(Class type, List<Class> argsTypes){
    return findMethod(type, null, argsTypes, false);
}

public static <T> List<T> findMembers(Class fromClass, Class<T> memberType, Object instance, boolean exactType){
    try{
        List<T> l = new ArrayList<>();
        Field[] fields = fromClass.getFields();
        if(exactType){
            for(Field f: fields){
                if(f.getType() == memberType){
                    l.add((T)f.get(instance));
                }
            }
        } else {
            for(Field f: fields){
                if(Reflection.isInstanceOf(f.getType(), memberType)){
                    l.add((T)f.get(instance));
                }
            }
        }
        return Nullify.collection(l);
    } catch(java.lang.Throwable e){

DebugException.start().log(e).end();

        return null;
    }
}
public static <T> T findMember(Class fromClass, Class<T> memberType, Object instance, boolean exactType){
    List<T> l = findMembers(fromClass, memberType, instance, exactType);
    if(l == null){
        return null;
    }
    return l.get(0);
}
public static <T> T findMember(Class fromClass, Class<T> memberType, boolean exactType){
    return findMember(fromClass, memberType, null, exactType);
}
public static <T> T findMember(Class fromClass, Class<T> memberType){
    return findMember(fromClass, memberType, null, true);
}
public static <T> T findMember(Field field, Object instance){
    try{
        return (T)field.get(instance);
    } catch(java.lang.Throwable e){

DebugException.start().log(e).end();

        return null;
    }
}

public static Class getEnclosingClass(Class fromClass){
    return fromClass.getEnclosingClass();
}
public static Class getEnclosingClassIfInstanceOf(Class fromClass, Class enclosingTypeToFind){
    do{
        Class enclosingType = fromClass.getEnclosingClass();
        if(enclosingType == null){
            return null;
        }
        if(isInstanceOf(enclosingType, enclosingTypeToFind)){
            return enclosingType;
        }
    } while(true);
}
public static List<Class> getMemberClasses(Class fromClass){
    return Arrays.asList(fromClass.getDeclaredClasses());
}
public static List<Class> getMemberClassesIfInstanceOf(Class fromClass, Class memberTypeToFind){
    List<Class> memberTypesFound = new ArrayList<>();
    if(isInstanceOf(fromClass, memberTypeToFind)){
        memberTypesFound.add(fromClass);
    }
    Class[] memberTypes = fromClass.getDeclaredClasses();
    if(memberTypes.length == 0){
        return Nullify.collection(memberTypesFound);
    }
    for(Class c: memberTypes){
        List<Class> mf = getMemberClassesIfInstanceOf(c, memberTypeToFind);
        if(mf != null){
            memberTypesFound.addAll(mf);
        } else {
            if(isInstanceOf(c, memberTypeToFind)){
                memberTypesFound.add(c);
            }
        }
    }
    return Nullify.collection(memberTypesFound);
}

public static void toDebugLogClass(Class<?> type){
DebugLog.start().send(DebugTrack.getFullSimpleName(type)).end();
DebugLog.start().send("*** Members").end();
    toDebugLogMembers(type);
DebugLog.start().send("*** Constructor").end();
    toDebugLogConstructor(type);
DebugLog.start().send("*** Methods").end();
    toDebugLogMethods(type);
DebugLog.start().send("*** Inner Class").end();
    toDebugLogInnerClasses(type);
}
protected static String toSimpleName(Object o){
    String[] names = o.toString().split("\\.");
    String name = "null";
    if(names.length > 0){
        name = names[names.length - 1];
    }
    name = name.replace(";", "[]");
    return name;
}
protected static String toModifier(int value){
    value = value & (Modifier.PUBLIC | Modifier.PRIVATE | Modifier.PROTECTED);
    String modifier = "?";
    switch(value){
        case Modifier.PUBLIC : modifier = "public";
            break;
        case Modifier.PRIVATE: modifier = "private";
            break;
        case Modifier.PROTECTED: modifier = "protected";
            break;
    }
    return modifier;
}
public static void toDebugLogConstructor(Class<?> type){
    Constructor[] constructors = type.getDeclaredConstructors();
    for(Constructor constructor: constructors){
        toDebugLogConstructor(constructor);
    }
}
public static void toDebugLogConstructor(Constructor constructor){
DebugLog.start().send(" " + toSimpleName(constructor.getName()) + "(" + toStringConstructorArguments(constructor) + ")").end();
}
protected static String toStringConstructorArguments(Constructor constructor){
    StringBuilder data = new StringBuilder();
    Type[] types = constructor.getParameterTypes();
    for(Type type: types){
        data.append(toSimpleName(type) + ", ");
    }
    if(data.length() > 2){
        return data.substring(0, data.length() - 2);
    } else {
        return data.toString();
    }
}
public static void toDebugLogMembers(Class<?> type){
    Field[] fields = type.getFields();
    for(Field field: fields){
        toDebugLogMembers(field);
    }
}
public static void toDebugLogMembers(Field field){
    String modifier = toModifier(field.getModifiers());
DebugLog.start().send(modifier + " " + toSimpleName(field.getType()) + " " + field.getName()).end();
}
public static void toDebugLogMethods(Class<?> type){
    Method[] methods = type.getMethods();
    for(Method method: methods){
        toDebugLogMethod(method);
    }
}
public static void toDebugLogMethod(Method method){
    String modifier = toModifier(method.getModifiers());
    Class returnTypeClass = method.getReturnType();
    String returnType = returnTypeClass.getSimpleName();
DebugLog.start().send(modifier + " " + returnType + " " + method.getName() + "(" + toStringMethodArguments(method) + ")").end();
}
protected static String toStringMethodArguments(Method method){
    StringBuilder data = new StringBuilder();
    Type[] types = method.getParameterTypes();
    for(Type type: types){
        data.append(toSimpleName(type) + ", ");
    }
    if(data.length() > 2){
        return data.substring(0, data.length() - 2);
    } else {
        return data.toString();
    }
}
public static void toDebugLogInnerClasses(Class<?> type){
    Class[] types = type.getDeclaredClasses();
    for(Class t: types){
DebugLog.start().send(t.getSimpleName()).end();
    }
}





}
