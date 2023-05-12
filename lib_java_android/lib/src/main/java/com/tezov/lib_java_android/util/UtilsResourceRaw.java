/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.util;

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
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import androidx.fragment.app.Fragment;

import java.nio.charset.StandardCharsets;

import com.tezov.lib_java_android.application.AppContext;
import com.tezov.lib_java_android.application.AppResources;
import com.tezov.lib_java.async.notifier.task.TaskValue;
import com.tezov.lib_java.buffer.ByteBufferOutput;
import com.tezov.lib_java.file.Directory;
import com.tezov.lib_java.file.File;
import com.tezov.lib_java.file.UtilsFile;
import com.tezov.lib_java.file.UtilsStream;
import com.tezov.lib_java.parser.defParserReader.Token;
import com.tezov.lib_java.parser.json.ParserReaderJson;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.type.collection.ListEntry;
import com.tezov.lib_java.util.UtilsBytes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Iterator;

import static com.tezov.lib_java.file.Directory.PATH_SEPARATOR;

import android.util.Log;

public class UtilsResourceRaw{
private final static int BUFFER_SIZE_byte = 1024;

private UtilsResourceRaw(){
}

public static String toString(int resId){
    InputStream is = null;
    ByteArrayOutputStream os = null;
    try{
        is = AppContext.getResources().openRawResource(resId);
        os = new ByteArrayOutputStream();
        int length;
        byte[] buffer = UtilsBytes.obtain(BUFFER_SIZE_byte);
        while((length = is.read(buffer)) > 0){
            os.write(buffer, 0, length);
        }
        is.close();
        os.close();
        return os.toString(StandardCharsets.UTF_8.name());
    } catch(java.lang.Throwable e){
//        Log.d(DebugLog.TAG, e.getClass().getSimpleName() + ":" + e.getMessage());
//        DebugException.start().log(e).end();
        UtilsStream.close(is);
        UtilsStream.close(os);
        return null;
    }
}

public static ListEntry<String, String> JsonToList(int resId){
    ParserReaderJson parser = null;
    try{
        ListEntry<String, String> properties = new ListEntry<>();
        parser = new ParserReaderJson(AppContext.getResources().openRawResource(resId));
        parser.beginObject();
        while(parser.peek() != Token.END_OBJECT){
            String key = parser.nextName();
            Token token = parser.peek();
//            Log.d(DebugLog.TAG, key + "/" + token);
            switch(token){
                case STRING:
                case NUMBER:{
                    properties.put(key, parser.nextString());
                }
                break;
                case BOOLEAN:{
                    properties.put(key, String.valueOf(parser.nextBoolean()));
                }
                break;
                case NULL:{
                    properties.put(key, null);
                    parser.nextNull();
                }break;
                default:
                    throw new Throwable("unknown token");
            }
        }
        parser.endObject();
        parser.close();
        return properties;
    } catch(Throwable e){
//        Log.d(DebugLog.TAG, e.getClass().getSimpleName() + ":" + e.getMessage());
        if(parser != null){
            try{
                parser.close();
            } catch(Throwable ec){}
        }
        return null;
    }
}
public static byte[] JsonToBytes(int resId){
    ParserReaderJson parser = null;
    try{
        ByteBufferOutput out = ByteBufferOutput.obtain();
        parser = new ParserReaderJson(AppContext.getResources().openRawResource(resId));
        parser.beginObject();
        while(parser.peek() != Token.END_OBJECT){
            String key = parser.nextName();
            Token token = parser.peek();
            switch(token){
                case STRING:
                case NUMBER:{
                    out.put(key).put(parser.nextString());
                }
                break;
                case BOOLEAN:{
                    out.put(key).put(String.valueOf(parser.nextBoolean()));
                }
                break;
                default:
                    throw new Throwable("unknown token");
            }
        }
        parser.endObject();
        return out.toBytes();
    } catch(Throwable e){
//        Log.d(DebugLog.TAG, e.getClass().getSimpleName() + ":" + e.getMessage());
        if(parser != null){
            try{
                parser.close();
            } catch(Throwable ec){}
        }
        return null;
    }
}

public static TaskValue<File>.Observable toFile(Directory directory, String resName, boolean override){
    String res;
    String extension = UtilsFile.getExtension(resName);
    if(extension != null){
        res = resName.replace("." + extension, "");
    } else {
        res = resName;
    }
    return toFile(directory, resName, AppContext.getResources().getIdentifier(AppResources.IdentifierType.raw, res), override);
}
public static TaskValue<File>.Observable toFile(Directory directory, int resId, boolean override){
    String path = AppContext.getResources().getIdentifierPath(resId);
    Iterator<String> it = Arrays.asList(path.split(PATH_SEPARATOR)).iterator();
    String resName;
    do{
        resName = it.next();
    } while(it.hasNext());
    return toFile(directory, resName, resId, override);
}
public static TaskValue<File>.Observable toFile(Directory directory, String fileName, int resId, boolean override){
    return toFile(new File(directory, fileName), resId, override);
}
public static TaskValue<File>.Observable toFile(File file, int resId, boolean override){
    TaskValue<File> task = new TaskValue<>();
    if(file.exists() && !override){
        task.notifyException(file, "file already exist");
        return task.getObservable();
    }
    InputStream in = null;
    try{
        in = AppContext.getResources().openRawResource(resId);
        UtilsFile.transfer(in, file);
        task.notifyComplete(file);
    } catch(IOException e){
        UtilsStream.close(in);
        task.notifyException(null, e);
    }
    return task.getObservable();
}

public void toDebugLogString(int resId){
DebugLog.start().send(toString(resId)).end();
}

}
