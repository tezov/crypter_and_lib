/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.provider.fileProvider;

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

import com.tezov.lib_java_android.application.AppContext;
import com.tezov.lib_java.debug.DebugException;

import static android.content.ContentResolver.SCHEME_CONTENT;
import static android.content.ContentResolver.SCHEME_FILE;
import static android.os.Environment.*;
import static com.tezov.lib_java.util.UtilsList.NULL_INDEX;

import android.content.Intent;
import android.net.Uri;

import com.tezov.lib_java_android.application.VersionSDK;
import com.tezov.lib_java.file.Directory;
import com.tezov.lib_java_android.file.StorageMedia;
import com.tezov.lib_java.debug.DebugLog;

import java.io.File;

public class FileProvider extends androidx.core.content.FileProvider{
public static final String AUTHORITY = ".provider";
public static String getAuthority(){
    return AppContext.getPackageName() + FileProvider.AUTHORITY;
}
public static boolean isAuthorityMe(String authority){
    return getAuthority().equals(authority);
}

public static Uri makeExportable(Uri uri){
    if(SCHEME_FILE.equals(uri.getScheme())){
        java.io.File file = new java.io.File(uri.getPath());
        return uriFromFile(file);
    }
    else if(SCHEME_CONTENT.equals(uri.getScheme())){
        return uri;
    }
DebugException.start().log(" no resolution " + uri).end();
    return null;
}
public static Uri uriFromFile(File file){
    return getUriForFile(AppContext.get(), getAuthority(), file);
}
public static File fileFromUri(Uri uri){
    if(VersionSDK.isSupEqualTo30_R()){
DebugException.start().log("version sdk >30, should not be possible").end();
        return null;
    }
    String path = uri.getPath();
    if(path.startsWith(Directory.PATH_SEPARATOR)){
        path = path.substring(1);
    }
    int indexOfSep = path.indexOf(Directory.PATH_SEPARATOR);
    if(indexOfSep == NULL_INDEX){
DebugException.start().log("invalid path").end();
        return null;
    }
    String root = path.substring(0, indexOfSep);
    boolean isRootValid = DIRECTORY_DOCUMENTS.equals(root) || DIRECTORY_PICTURES.equals(root)
      || DIRECTORY_MOVIES.equals(root) || DIRECTORY_MUSIC.equals(root);
    if(!isRootValid){
DebugException.start().log("invalid root").end();
        return null;
    }
    path = StorageMedia.getPublicDirectory_before30_R("/") + path;
DebugLog.start().send(path).end();
    return new File(path);
}

public static Uri permissionGrantRead(String packageName, Uri uri){
    int flag = Intent.FLAG_GRANT_READ_URI_PERMISSION;
    AppContext.get().grantUriPermission(packageName, uri, flag);
    return uri;
}
public static Uri permissionRevokeRead(String packageName, Uri uri){
    int flag = Intent.FLAG_GRANT_READ_URI_PERMISSION;
    if(VersionSDK.isSupEqualTo26_OREO()){
        AppContext.get().revokeUriPermission(packageName, uri, flag);
    } else {
        AppContext.get().revokeUriPermission(uri, flag);
    }
    return uri;
}
public static Uri permissionGrantWrite(String packageName, Uri uri){
    int flag = Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION;
    AppContext.get().grantUriPermission(packageName, uri, flag);
    return uri;
}
public static Uri permissionRevokeWrite(String packageName, Uri uri){
    int flag = Intent.FLAG_GRANT_WRITE_URI_PERMISSION;
    if(VersionSDK.isSupEqualTo26_OREO()){
        AppContext.get().revokeUriPermission(packageName, uri, flag);
    } else {
        AppContext.get().revokeUriPermission(uri, flag);
    }
    return uri;
}
public static Uri permissionRevokeWriteAndRead(String packageName, Uri uri){
    int flag = Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION;
    if(VersionSDK.isSupEqualTo26_OREO()){
        AppContext.get().revokeUriPermission(packageName, uri, flag);
    } else {
        AppContext.get().revokeUriPermission(uri, flag);
    }
    return uri;
}

}

