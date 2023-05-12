/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.file;

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
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.tezov.lib_java_android.application.AppContext;
import com.tezov.lib_java_android.type.android.LifecycleEvent;
import com.tezov.lib_java.file.UtilsFile;
import com.tezov.lib_java.type.primaire.Pair;
import com.tezov.lib_java.type.runnable.RunnableSubscription;
import com.tezov.lib_java_android.ui.activity.ActivityBase;
import com.tezov.lib_java_android.util.UtilsCursor;

import static android.content.ContentResolver.SCHEME_CONTENT;
import static android.content.ContentResolver.SCHEME_FILE;
import static androidx.lifecycle.Lifecycle.Event.ON_PAUSE;
import static androidx.lifecycle.Lifecycle.Event.ON_RESUME;
import static com.tezov.lib_java_android.file.DocumentFileHelper.ROOT;
import static com.tezov.lib_java.util.UtilsList.NULL_INDEX;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import androidx.annotation.RequiresApi;
import androidx.documentfile.provider.DocumentFile;
import androidx.lifecycle.OnLifecycleEvent;

import com.tezov.lib_java_android.application.VersionSDK;
import com.tezov.lib_java.async.notifier.task.TaskState;
import com.tezov.lib_java_android.provider.fileProvider.FileProvider;
import com.tezov.lib_java.toolbox.Nullify;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java_android.type.android.wrapper.BundleW;
import com.tezov.lib_java.type.primitive.string.StringBase49To;
import com.tezov.lib_java.type.primitive.string.StringCharTo;
import com.tezov.lib_java_android.util.UtilsIntent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Iterator;

public class UriW{
public final static String LINK_SEPARATOR = ":";
public final static String LINK_VALUE_NULL = "#";

private android.net.Uri uri;
private Type type;

public UriW(com.tezov.lib_java.file.File file){
    this(file.getFile());
}
public UriW(File file){
    this(Uri.fromFile(file), Type.STORAGE_MEDIA_FILE);
}
public UriW(DocumentFile document, Type type){
    this(document.getUri(), type);
}
public UriW(android.net.Uri uri, Type type){
DebugTrack.start().create(this).end();
    this.uri = uri;
    this.type = type;
}
public static UriW fromLink(String link){
    Iterator<String> it = Arrays.asList(link.split(LINK_SEPARATOR)).iterator();
    Type type;
    String typeString = it.next();
    if(LINK_VALUE_NULL.equals(typeString)){
        return null;
    }
    type = Type.valueOf(typeString);
    String uriString = it.next();
    if(LINK_VALUE_NULL.equals(uriString)){
        return null;
    }
    Uri uri = Uri.parse(StringBase49To.StringChar(uriString));
    return new UriW(uri, type);
}

private UriW me(){
    return this;
}

public android.net.Uri get(){
    return uri;
}
public Type getType(){
    return type;
}
public String getScheme(){
    return uri.getScheme();
}
public String getPath(){
    return uri.getPath();
}

private boolean checkUriPermission(Uri uri, int flags){
    return checkUriPermission(uri, null, null, flags);
}
private boolean checkUriPermission(Uri uri, String readPermission, String writePermission, int flags){
    return AppContext.get().checkUriPermission(uri, readPermission, writePermission, Binder.getCallingPid(), Binder.getCallingUid(), flags) == PackageManager.PERMISSION_GRANTED;
}

public boolean canExport(){
    return (type == Type.STORAGE_TREE) || (type == Type.FILE_PROVIDER) || (type == Type.STORAGE_MEDIA) || (type == Type.INTENT);
}
public UriW makeExportable(){
    if(canExport()){
        return this;
    } else if(type == Type.STORAGE_MEDIA_FILE){
        uri = FileProvider.makeExportable(uri);
        type = Type.FILE_PROVIDER;
        return this;
    }
DebugException.start().log(type.name() + " no resolution " + uri.toString()).end();
    return null;
}

public boolean canRead(){
    if(SCHEME_FILE.equals(uri.getScheme())){
        java.io.File file = new java.io.File(uri.getPath());
        return file.canRead();
    }
    if(type == Type.STORAGE_TREE){
        return checkUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
    }
    if(type == Type.FILE_PROVIDER){
        java.io.File file = FileProvider.fileFromUri(uri);
        return (file != null) && file.canRead();
    }
    if((type == Type.STORAGE_MEDIA) || (type == Type.INTENT) || SCHEME_CONTENT.equals(uri.getScheme())){
        return checkUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
    }
DebugException.start().log(type.name() + " no resolution " + uri.toString()).end();
    return false;
}
public boolean canWrite(){
    if(SCHEME_FILE.equals(uri.getScheme())){
        java.io.File file = new java.io.File(uri.getPath());
        return file.canWrite();
    }
    if(type == Type.STORAGE_TREE){
        return checkUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
    }
    if(type == Type.FILE_PROVIDER){
        java.io.File file = FileProvider.fileFromUri(uri);
        return (file != null) && file.canWrite();
    }
    if((type == Type.STORAGE_MEDIA) || (type == Type.INTENT) || SCHEME_CONTENT.equals(uri.getScheme())){
        return checkUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
    }
DebugException.start().log(type.name() + " no resolution " + uri.toString()).end();
    return false;
}
public boolean exist(){
    if(SCHEME_FILE.equals(uri.getScheme())){
        java.io.File file = new java.io.File(uri.getPath());
        return file.exists();
    }
    if(type == Type.STORAGE_TREE){
        DocumentFile file = DocumentFile.fromSingleUri(AppContext.get(), uri);
        return (file != null) && file.exists();
    }
    if(type == Type.FILE_PROVIDER){
        java.io.File file = FileProvider.fileFromUri(uri);
        return (file != null) && file.exists();
    }
    if(type == Type.STORAGE_MEDIA){
        Pair<String, String> p = UtilsFile.splitToPathAndFileName(getDisplayPath());
        if((p != null) && (p.first != null) && (p.second != null)){
            return StorageMedia.findUri(p.first, p.second) != null;
        } else {
            return false;
        }
    }
    if((type == Type.INTENT) || SCHEME_CONTENT.equals(uri.getScheme())){
        return DocumentFileHelper.getDocumentId(uri) != null;
    }
DebugException.start().log(type.name() + " no resolution " + uri.toString()).end();
    return false;
}
public boolean pending(boolean flag){
    if(SCHEME_FILE.equals(uri.getScheme())){
        if(flag){
            java.io.File file = new java.io.File(uri.getPath());
            return file.canWrite();
        } else {
            return true;
        }
    }
    if(type == Type.STORAGE_TREE){
        if(flag){
            return checkUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        } else {
            return true;
        }
    }
    if(type == Type.FILE_PROVIDER){
        if(flag){
            java.io.File file = FileProvider.fileFromUri(uri);
            return (file != null) && file.canWrite();
        } else {
            return true;
        }
    }
    if(type == Type.STORAGE_MEDIA){
        if(VersionSDK.isSupEqualTo30_R()){
            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.IS_PENDING, flag ? 1 : 0);
            return DocumentFileHelper.update(uri, values);
        }
        return false;
    }
DebugException.start().log(type.name() + " no resolution " + uri.toString()).end();
    return false;
}
public boolean delete(){
    if(SCHEME_FILE.equals(uri.getScheme())){
        java.io.File file = new java.io.File(uri.getPath());
        return file.delete();
    }
    if(type == Type.STORAGE_TREE){
        try{
            return DocumentsContract.deleteDocument(AppContext.getContentResolver(), uri);
        } catch(Throwable e){
            return false;
        }
    }
    if(type == Type.FILE_PROVIDER){
        java.io.File file = FileProvider.fileFromUri(uri);
        return (file != null) && file.delete();
    }
    if((type == Type.STORAGE_MEDIA) || (type == Type.INTENT) || SCHEME_CONTENT.equals(uri.getScheme())){
        return DocumentFileHelper.delete(uri);
    }
DebugException.start().log(type.name() + " no resolution " + uri.toString()).end();
    return false;
}

public String getFullName(){
    if(SCHEME_FILE.equals(uri.getScheme())){
        java.io.File file = new java.io.File(uri.getPath());
        return file.getName();
    }
    if(type == Type.STORAGE_TREE){
        DocumentFile file = DocumentFile.fromSingleUri(AppContext.get(), uri);
        if(file != null){
            return file.getName();
        } else {
            return null;
        }
    }
    if(type == Type.FILE_PROVIDER){
        java.io.File file = FileProvider.fileFromUri(uri);
        if(file != null){
            return file.getName();
        } else {
            return null;
        }
    }
    if((type == Type.STORAGE_MEDIA) || (type == Type.INTENT) || SCHEME_CONTENT.equals(uri.getScheme())){
        return DocumentFileHelper.getDisplayName(uri);
    }
DebugException.start().log(type.name() + " no resolution " + uri.toString()).end();
    return null;
}
public String getName(){
    String fileFullName = getFullName();
    if(fileFullName == null){
        return null;
    } else {
        return UtilsFile.getName(fileFullName);
    }
}
public String getExtension(){
    String fileFullName = getFullName();
    if(fileFullName == null){
        return null;
    } else {
        return UtilsFile.getExtension(fileFullName);
    }
}
public String getDisplayPath(){
    if(SCHEME_FILE.equals(uri.getScheme())){
        java.io.File file = new java.io.File(uri.getPath());
        return DocumentFileHelper.getDisplayPathFromPath(file.getPath());
    }
    if(type == Type.STORAGE_TREE){
        return DocumentFileHelper.getDisplayPath(uri);
    }
    if(type == Type.FILE_PROVIDER){
        return DocumentFileHelper.getDisplayPathFromRelativePath(ROOT, uri.getPath());
    }
    if((type == Type.STORAGE_MEDIA) || (MediaStore.AUTHORITY.equals(uri.getAuthority()))){
        if(VersionSDK.isSupEqualTo29_Q()){
            String path = getRelativePathStorageMedia_after29_Q(uri);
            if(path != null){
                return DocumentFileHelper.getDisplayPathFromRelativePath(ROOT, path);
            }
        }
        return null;
    }
    if(type == Type.INTENT){
        Uri uriMedia = StorageMedia.convertMediaDocumentsProviderUri(uri);
        if(uriMedia != null){
            String path;
            if(VersionSDK.isSupEqualTo29_Q()){
                path = getRelativePathStorageMedia_after29_Q(uriMedia);
            }
            else{
                path = getRelativePathStorageMedia_before29_Q(uriMedia);
            }
            if(path != null){
                return DocumentFileHelper.getDisplayPathFromRelativePath(ROOT, path);
            }
        }
        return DocumentFileHelper.getDisplayPath(uri);
    }
DebugException.start().log(type.name() + " no resolution " + uri.toString()).end();
    return null;
}
@RequiresApi(api = Build.VERSION_CODES.Q)
private static String getRelativePathStorageMedia_after29_Q(Uri uri){
    Cursor cursor = null;
    try{
        cursor = AppContext.getContentResolver().query(uri, new String[]{MediaStore.MediaColumns.RELATIVE_PATH, MediaStore.MediaColumns.DISPLAY_NAME}, null, null, null);
        if(cursor != null){
            String relativePath = "";
            if(cursor.getCount() == 1){
                cursor.moveToFirst();
                int indexPath = cursor.getColumnIndex(MediaStore.MediaColumns.RELATIVE_PATH);
                int indexDisplayName = cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME);
                if((indexPath != NULL_INDEX) && (indexDisplayName != NULL_INDEX)){
                    String path = Nullify.string(cursor.getString(indexPath));
                    if(path != null){
                        relativePath = path;
                    }
                    String displayName = Nullify.string(cursor.getString(indexDisplayName));
                    if(displayName != null){
                        relativePath += displayName;
                    }
                }
            }
            cursor.close();
            return Nullify.string(relativePath);
        }
        return null;
    } catch(Throwable e){
        if(cursor != null){
            cursor.close();
        }
        return null;
    }
}
@SuppressWarnings("deprecation")
private static String getRelativePathStorageMedia_before29_Q(Uri uri){
    Cursor cursor = null;
    try{
        cursor = AppContext.getContentResolver().query(uri, new String[]{MediaStore.MediaColumns.DATA}, null, null, null);
        if(cursor != null){
            String relativePath = "";
            if(cursor.getCount() == 1){
                cursor.moveToFirst();
                int indexPath = cursor.getColumnIndex(MediaStore.MediaColumns.DATA);
                if(indexPath != NULL_INDEX){
                    String path = Nullify.string(cursor.getString(indexPath));
                    if(path != null){
                        String pathBase = StorageMedia.getPublicDirectory_before30_R("").getPath();
                        if(path.startsWith(pathBase)){
                            path = path.replace(pathBase, "");
                        }
                        relativePath = path;
                    }
                }
            }
            cursor.close();
            return Nullify.string(relativePath);
        }
        return null;
    } catch(Throwable e){
        if(cursor != null){
            cursor.close();
        }
        return null;
    }
}
public Integer getFileSize(){
    if(SCHEME_FILE.equals(uri.getScheme())){
        java.io.File file = new java.io.File(uri.getPath());
        return (int)file.length();
    }
    if(type == Type.STORAGE_TREE){
        DocumentFile file = DocumentFile.fromSingleUri(AppContext.get(), uri);
        if((file != null)){
            return (int)file.length();
        } else {
            return null;
        }
    }
    if(type == Type.FILE_PROVIDER){
        java.io.File file = FileProvider.fileFromUri(uri);
        if((file != null)){
            return (int)file.length();
        } else {
            return null;
        }
    }
    if((type == Type.STORAGE_MEDIA) || (type == Type.INTENT) || SCHEME_CONTENT.equals(uri.getScheme())){
        return DocumentFileHelper.getSize(uri);
    }
DebugException.start().log(type.name() + " no resolution " + uri.toString()).end();
    return null;
}
public String getMimeType(){
    if(SCHEME_FILE.equals(uri.getScheme())){
        java.io.File file = new java.io.File(uri.getPath());
        return UtilsFile.getMimeTypeForFullName(file.getName());
    }
    if(type == Type.STORAGE_TREE){
        DocumentFile documentFile = DocumentFile.fromSingleUri(AppContext.get(), uri);
        String mineType = null;
        if(documentFile != null){
            mineType = documentFile.getType();
            if(mineType == null){
                mineType = UtilsFile.MINE_TYPE_OCTET_STREAM;
            }
        }
        return mineType;
    }
    if(type == Type.FILE_PROVIDER){
        java.io.File file = FileProvider.fileFromUri(uri);
        if(file != null){
            return UtilsFile.getMimeTypeForFullName(file.getName());
        } else {
            return null;
        }
    }
    if((type == Type.STORAGE_MEDIA) || (type == Type.INTENT) || SCHEME_CONTENT.equals(uri.getScheme())){
        String mineType = DocumentFileHelper.getMineType(uri);
        if(mineType == null){
            mineType = UtilsFile.MINE_TYPE_OCTET_STREAM;
        }
        return mineType;
    }
DebugException.start().log(type.name() + " no resolution " + uri.toString()).end();
    return null;
}
public OutputStream getOutputStream() throws FileNotFoundException{
    if(SCHEME_FILE.equals(uri.getScheme())){
        java.io.File file = new java.io.File(uri.getPath());
        return new FileOutputStream(file);
    }
    if(SCHEME_CONTENT.equals(uri.getScheme())){
        return AppContext.getContentResolver().openOutputStream(uri);
    }
DebugException.start().log(type.name() + " no resolution " + uri.toString()).end();
    return null;
}
public InputStream getInputStream() throws FileNotFoundException{
    if(SCHEME_FILE.equals(uri.getScheme())){
        java.io.File file = new java.io.File(uri.getPath());
        return new FileInputStream(file);
    }
    if(SCHEME_CONTENT.equals(uri.getScheme())){
        return AppContext.getContentResolver().openInputStream(uri);
    }
DebugException.start().log(type.name() + " no resolution " + uri.toString()).end();
    return null;
}

public TaskState.Observable send(){
    return send(null, null);
}
public TaskState.Observable send(String packageNameDestination){
    return send(packageNameDestination, null);
}
public TaskState.Observable send(BundleW extras){
    return send(null, extras);
}
public TaskState.Observable send(String packageNameDestination, BundleW extras){
    TaskState task = new TaskState();
    try{
        Intent intent = new Intent();
        if(!UtilsIntent.setUriStream(intent, me())){
            throw new Throwable("fail to set uri stream");
        }
        else{
            intent.setAction(Intent.ACTION_SEND);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
            intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            if(extras != null){
                intent.putExtras(extras.get());
            }
            if(packageNameDestination != null){
                intent.setPackage(packageNameDestination);
            } else {
                intent = Intent.createChooser(intent, "");
            }
            ActivityBase activity = AppContext.getActivity();
            LifecycleEvent.on(LifecycleEvent.Event.RESTART, activity, new RunnableSubscription(){
                @Override
                public void onComplete(){
                    unsubscribe();
                    task.notifyComplete();
                }
            });
            activity.startActivity(intent);
        }
    }
    catch(Throwable e){
        task.notifyException(e);
    }
    return task.getObservable();
}

public TaskState.Observable open(){
    return open(null, null);
}
public TaskState.Observable open(String packageNameDestination){
    return open(packageNameDestination, null);
}
public TaskState.Observable open(BundleW extras){
    return open(null, extras);
}
public TaskState.Observable open(String packageNameDestination, BundleW extras){
    TaskState task = new TaskState();
    try{
        Intent intent = new Intent();
        if(!UtilsIntent.setUriData(intent, me())){
            throw new Throwable("fail to set uri retrofit.data");
        }
        else{
            intent.setAction(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
            intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            if(extras != null){
                intent.putExtras(extras.get());
            }
            if(packageNameDestination != null){
                intent.setPackage(packageNameDestination);
            } else {
                intent = Intent.createChooser(intent, "");
            }
            ActivityBase activity = AppContext.getActivity();
            LifecycleEvent.on(LifecycleEvent.Event.RESTART, activity, new RunnableSubscription(){
                @Override
                public void onComplete(){
                    unsubscribe();
                    task.notifyComplete();
                }
            });
            activity.startActivity(intent);
        }
    }
    catch(Throwable e){
        task.notifyException(e);
    }
    return task.getObservable();
}

public DebugString toDebugString(){
    DebugString data = new DebugString();
    if(uri != null){
        data.append("type", type);
        data.append("scheme", uri.getScheme());
        data.append("authority", FileProvider.isAuthorityMe(uri.getAuthority()) ? "me" : uri.getAuthority());
        data.append("exist", exist());
        data.append("canRead", canRead());
        data.append("canWrite", canWrite());
        data.append("canExport", canExport());
        data.append("name", getFullName());
        data.append("size", getFileSize());
        data.append("mimeType", getMimeType());
        data.append("display path", getDisplayPath());
        data.append("uri path", uri.getPath());
    } else {
        data.append("uri is null");
    }
    return data;
}
final public void toDebugLog(){
DebugLog.start().send(toDebugString()).end();
}
final public void toDebugLogQuery(){
    toDebugLogQuery(uri);
}
public static void toDebugLogQuery(Uri uri){
    try{
        if(uri == null){
            throw new NullPointerException("uri is null");
        }
        Cursor cursor = AppContext.getContentResolver().query(uri, null, null, null, null);
        if(cursor == null){
            throw new NullPointerException("cursor is null");
        }
        UtilsCursor.toDebugLogNoClose(cursor);
        cursor.close();
    } catch(Throwable e){
DebugException.start().log(e).end();
    }
}
public String toLink(){
    StringBuilder data = new StringBuilder();
    data.append(type != null ? type.name() : LINK_VALUE_NULL).append(LINK_SEPARATOR);
    data.append(uri != null ? StringCharTo.StringBase49(uri.toString()) : LINK_VALUE_NULL);
    return data.toString();
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}
public enum Type{
    STORAGE_MEDIA, STORAGE_MEDIA_FILE, FILE_PROVIDER, STORAGE_TREE, INTENT
}

}
