/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.file;

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
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import androidx.fragment.app.Fragment;

import com.tezov.lib_java.file.Directory;

public class StorageFile extends com.tezov.lib_java.file.StorageFile{

public static UriW obtainUri(Directory directory, String fileFullName){
    return obtainUri(directory.getPath(), fileFullName);
}
public static UriW obtainUri(String directoryPath, String fileFullName){
    java.io.File file = obtain(directoryPath, fileFullName);
    if(file != null){
        return new UriW(file);
    } else {
        return null;
    }
}
public static UriW obtainUniqueUri(Directory directory, String fileFullName){
    return obtainUniqueUri(directory.getPath(), fileFullName);
}
public static UriW obtainUniqueUri(String directoryPath, String fileFullName){
    java.io.File file = obtainUnique(directoryPath, fileFullName);
    if(file != null){
        return new UriW(file);
    } else {
        return null;
    }
}
public static UriW obtainClosestUri(Directory directory, String fileFullName){
    return obtainClosestUri(directory.getPath(), fileFullName);
}
public static UriW obtainClosestUri(String directoryPath, String fileFullName){
    java.io.File file = obtainClosest(directoryPath, fileFullName);
    if(file != null){
        return new UriW(file);
    } else {
        return null;
    }
}


}
