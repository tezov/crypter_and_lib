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
import android.provider.DocumentsContract;
import android.webkit.MimeTypeMap;

import com.tezov.lib_java.file.File;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class UtilsFile extends com.tezov.lib_java.file.UtilsFile{
public final static String MINE_TYPE_DIRECTORY_ANDROID = DocumentsContract.Document.MIME_TYPE_DIR;

public static String getMimeTypeForFullName(String fileFullName){
    if(fileFullName != null){
        return getMimeTypeForExtension(getExtension(fileFullName));
    } else {
        return null;
    }
}
public static String getMimeTypeForExtension(String extension){
    String mineType = null;
    if(extension != null){
        mineType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        if(mineType == null){
            mineType = MINE_TYPE_OCTET_STREAM;
        }
    }
    return mineType;
}

public static void transfer(java.io.File fileSource, UriW uriDestination) throws IOException{
    transfer(new FileInputStream(fileSource), uriDestination);
}
public static void transfer(File fileSource, UriW uriDestination) throws IOException{
    transfer(fileSource.getInputStream(), uriDestination);
}
public static void transfer(InputStream source, UriW uriDestination) throws IOException{
    com.tezov.lib_java.file.UtilsFile.transfer(source, uriDestination.getOutputStream());
}
public static void transfer(UriW uriSource, java.io.File fileDestination) throws IOException{
    transfer(uriSource, new FileOutputStream(fileDestination));
}
public static void transfer(UriW uriSource, File fileDestination) throws IOException{
    transfer(uriSource, fileDestination.getOutputStream());
}
public static void transfer(UriW uriSource, OutputStream destination) throws IOException{
    com.tezov.lib_java.file.UtilsFile.transfer(uriSource.getInputStream(), destination);
}

}
