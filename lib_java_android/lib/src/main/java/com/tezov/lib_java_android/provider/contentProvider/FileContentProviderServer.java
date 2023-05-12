/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.provider.contentProvider;

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
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.ArrayMap;

import com.tezov.lib_java_android.application.AppContext;
import com.tezov.lib_java.file.Directory;
import com.tezov.lib_java.file.File;
import com.tezov.lib_java.file.FileTree;
import com.tezov.lib_java_android.provider.contentProvider.FileContentProviderClient.ItemUriFile;
import com.tezov.lib_java_android.provider.contentProvider.FileContentProviderClient.ItemUriQuery;
import com.tezov.lib_java_android.type.android.wrapper.BundleW;

import java.io.FileNotFoundException;
import java.util.Map;

import static com.tezov.lib_java_android.provider.contentProvider.FileContentProviderClient.COL_LINK;

public abstract class FileContentProviderServer extends android.content.ContentProvider{
private final Map<String, Object> queryBuffer;
public FileContentProviderServer(){
    queryBuffer = new ArrayMap<>();
}

protected <T> void putQueryBuffer(String id, T o){
    queryBuffer.put(id, o);
}
protected <T> T removeQueryBuffer(String id){
    return (T)queryBuffer.remove(id);
}
protected <T> T getQueryBuffer(String id){
    return (T)queryBuffer.get(id);
}

protected abstract boolean isNotAllowed(String method, String arg);

@Override
public boolean onCreate(){
    return true;
}
@Nullable
@Override
public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder){
    String last = uri.getLastPathSegment();
    if(isNotAllowed("query", last)){
        return null;
    }
    ItemUriQuery item = new ItemUriQuery().fromStringHex(last);
    if(item.isAsyncQuery()){
        Cursor cursor = removeQueryBuffer(item.getId());
        if(cursor != null){
            return cursor;
        }
    }
    MatrixCursor c = new MatrixCursor(new String[]{COL_LINK});
    if(item.getDirectoryLink() != null){
        FileTree fileTree = new FileTree(Directory.from(item.getDirectoryLink()), item.isRecursive()).setPatternPath(item.getPatternPath()).setPatternFileName(item.getPatternFileName()).build();
        if(fileTree.hasFileLinks()){
            for(String link: fileTree.getFileLinks()){
                c.newRow().add(link);
            }
        }
    }
    if(item.isAsyncQuery()){
        putQueryBuffer(item.getId(), c);
        AppContext.getContentResolver().notifyChange(uri, null);
        return null;
    } else {
        return c;
    }
}
@Nullable
@Override
public String getType(@NonNull Uri uri){
    return null;
}
@Nullable
@Override
public Uri insert(@NonNull Uri uri, @Nullable ContentValues values){
    return null;
}
@Override
public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs){
    return 0;
}
@Override
public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs){
    return 0;
}
@Nullable
@Override
public ParcelFileDescriptor openFile(@NonNull Uri uri, @NonNull String mode) throws FileNotFoundException{
    String last = uri.getLastPathSegment();
    if(isNotAllowed("openFile", last)){
        throw new FileNotFoundException("permission denied");
    }
    ItemUriFile item = new ItemUriFile().fromStringHex(last);
    File file = File.from(item.getLink());
    if(!file.exists()){
        throw new FileNotFoundException("no file found for " + item.getLink());
    } else {
        return ParcelFileDescriptor.open(file.getFile(), ParcelFileDescriptor.parseMode(mode));
    }
}

@Nullable
@Override
final public Bundle call(@NonNull String method, @Nullable String arg, @Nullable Bundle extras){
    if(isNotAllowed(method, arg)){
        return null;
    }
    BundleW bundle = BundleW.obtain();
    if(call(method, arg, bundle)){
        return bundle.get();
    } else {
        return null;
    }
}
protected boolean call(String method, String arg, BundleW out){
    return false;
}

@Override
protected void finalize() throws Throwable{
    super.finalize();
}

}
