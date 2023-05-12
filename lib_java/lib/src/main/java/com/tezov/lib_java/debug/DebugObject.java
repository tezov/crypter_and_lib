/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.debug;

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java.type.primitive.CharsTo;
import com.tezov.lib_java.generator.uid.defUid;
import com.tezov.lib_java.toolbox.Nullify;
import com.tezov.lib_java.toolbox.Reflection;
import com.tezov.lib_java.type.defEnum.EnumBase;
import com.tezov.lib_java.type.primitive.ByteTo;
import com.tezov.lib_java.type.primitive.BytesTo;
import com.tezov.lib_java.wrapperAnonymous.FunctionW;
import com.tezov.lib_java.util.UtilsString;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

public class DebugObject{
private DebugObject(){

}

public static String toString(Object o){
    if(o == null){
        return "Object is null";
    } else if(o instanceof String){
        return (String)o;
    } else if(o instanceof DebugString){
        return o.toString();
    } else if(o instanceof Byte){
        return toString((Byte)o);
    } else if(o instanceof Number){
        return toString((Number)o);
    } else if(o instanceof byte[]){
        return toString((byte[])o);
    } else if(o instanceof char[]){
        return toString((char[])o);
    } else if(o instanceof Boolean){
        return toString((Boolean)o);
    } else if(o instanceof defUid){
        return toString((defUid)o);
    } else if(o instanceof Class){
        return toString((Class)o);
    } else if(o instanceof Enum){
        return toString((Enum)o);
    } else if(o instanceof EnumBase.Is){
        return toString((EnumBase.Is)o);
    } else if(o instanceof Collection){
        return toString((Collection)o);
    } else if(o instanceof Map){
        return toString((Map)o);
    } else if(o instanceof Object[]){
        return toString((Object[])o);
    } else if(o instanceof Throwable){
        return toString((Throwable)o);
    } else if(o instanceof StringBuilder){
        return o.toString();
    } else {
        Method method = Reflection.findMethod(o.getClass(), DebugString.class);
        if(method != null){
            try{
                DebugString debug = (DebugString)method.invoke(o);
                if(debug != null){
                    return debug.toString();
                }
            } catch(java.lang.Throwable e){
DebugException.start().log(e).end();
            }
        }
        return o.toString();
    }
}

public static <T extends Number> String toString(T data){
    return (String.valueOf(data));
}

public static <T extends defUid> String toString(T data){
    return data!=null?(data.toHexString()):null;
}

public static String toString(Boolean data){
    return (Boolean.toString(data));
}

public static String toString(Byte data){
    return (ByteTo.StringHex(data));
}

public static String toString(byte[] data){
    return (BytesTo.StringHex(data));
}

public static String toString(char[] data){
    return toString(CharsTo.Bytes(data));
}

public static String toString(Class<Object> type){
    return (DebugTrack.getFullSimpleName(type));
}

public static <T extends Enum<T>> String toString(T e){
    return (e.name());
}

public static String toString(EnumBase.Is is){
    return (is.name());
}

public static String toString(Collection<Object> data){
    if(data.size() <= 0){
        return ("Collection is empty");
    } else {
        return UtilsString.join("\n", data, new FunctionW<Object, String>(){
            @Override
            public String apply(Object o){
                return DebugObject.toString(o);
            }
        }).toString();
    }
}

public static String toString(Object[] data){
    return toString(Arrays.asList(data));
}

public static String toString(Map<Object, Object> data){
    if(data.size() <= 0){
        return ("Collection is empty");
    } else {
        return UtilsString.join("\n", data.entrySet(), new FunctionW<Map.Entry<Object, Object>, String>(){
            @Override
            public String apply(Map.Entry<Object, Object> e){
                return "[" + DebugObject.toString(e.getKey()) + "->" + DebugObject.toString(e.getValue()) + "]";
            }
        }).toString();
    }
}

public static String toString(Throwable e){
    return toString(e.getMessage(), e.getStackTrace());
}

public static String toString(String message, StackTraceElement[] stackTraceElements){
    StringBuilder data = new StringBuilder();
    if(Nullify.string(message) != null){
        data.append(message).append("\n");
    }
    for(StackTraceElement ste: stackTraceElements){
        DebugLog.TraceDetails st = new DebugLog.TraceDetails(ste);
        if(!st.isDebugMethod()){
            data.append(ste).append("\n");
        }
    }
    String dataString = Nullify.string(data.toString());
    if(dataString == null){
        dataString = "Error with no info...";
    }
    return dataString;
}

}
