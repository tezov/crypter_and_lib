/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.file;

import com.tezov.lib_java.debug.DebugTrack;
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

import static android.os.Environment.DIRECTORY_DOCUMENTS;
import static com.tezov.lib_java.util.UtilsList.NULL_INDEX;

import android.content.ContentValues;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;

import androidx.annotation.RequiresApi;
import androidx.documentfile.provider.DocumentFile;

import com.tezov.lib_java_android.application.AppContext;
import com.tezov.lib_java_android.application.VersionSDK;
import com.tezov.lib_java.file.Directory;
import com.tezov.lib_java.file.StoragePackage;
import com.tezov.lib_java.toolbox.Nullify;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java.type.collection.ListOrObject;

import java.util.List;

public class DocumentFileHelper{
public final static String ROOT = "root:";
public final static String ROOT_PRIMARY = "primary:";
public final static String ROOT_SDCARD = "sdcard:";
public final static String ROOT_PRIVATE = "private:";
public final static String ROOT_UNKNOWN = "unknown:";
public final static String ROOT_HOME = "home:";
public final static String ROOT_HOME_AS_DOCUMENTS = DIRECTORY_DOCUMENTS + Directory.PATH_SEPARATOR;

private static <T> T extractData(Cursor cursor, String projection, Class<T> type) throws Throwable{
    Object data = null;
    if(cursor.getCount() == 1){
        cursor.moveToFirst();
        int index = cursor.getColumnIndex(projection);
        if(index != NULL_INDEX){
            if(type == String.class){
                data = Nullify.string(cursor.getString(index));
            } else if(type == Integer.class){
                data = cursor.getInt(index);
            } else if(type == Long.class){
                data = cursor.getLong(index);
            } else {
                throw new Throwable("unknown type " + type);
            }
        }
    }
    return (T)data;
}
public static <T> T query(Uri uri, String projection, Class<T> type){
    Cursor cursor = null;
    try{
        cursor = AppContext.getContentResolver().query(uri, new String[]{projection}, null, null, null);
        if(cursor != null){
            T data = extractData(cursor, projection, type);
            cursor.close();
            return data;
        } else {
            return null;
        }
    } catch(Throwable e){
        if(cursor != null){
            cursor.close();
        }
        return null;
    }
}

private static <T> List<T> extractListData(Cursor cursor, String projection, Class<T> type) throws Throwable{
    List<Object> datas = new ListOrObject<>();
    if(cursor.getCount() == 1){
        cursor.moveToFirst();
        int index = cursor.getColumnIndex(projection);
        if(index != NULL_INDEX){
            while(!cursor.isAfterLast()){
                if(type == String.class){
                    Object data = Nullify.string(cursor.getString(index));
                    if(data != null){
                        datas.add(data);
                    }
                } else if(type == Integer.class){
                    datas.add(cursor.getInt(index));
                } else if(type == Long.class){
                    datas.add(cursor.getLong(index));
                } else {
                    throw new Throwable("unknown type " + type);
                }
                cursor.moveToNext();
            }
        }
    }
    return Nullify.collection((List<T>)datas);
}
public static <T> List<T> query(Uri uri, String projection, String selection, String[] selectionArgs, Class<T> type){
    Cursor cursor = null;
    try{
        cursor = AppContext.getContentResolver().query(uri, new String[]{projection}, selection, selectionArgs, null);
        if(cursor != null){
            List<T> data = extractListData(cursor, projection, type);
            cursor.close();
            return data;
        } else {
            return null;
        }
    } catch(Throwable e){
        if(cursor != null){
            cursor.close();
        }
        return null;
    }
}
@RequiresApi(api = Build.VERSION_CODES.O)
public static <T> List<T> query(Uri uri, String projection, Bundle queryArgs, Class<T> type){
    Cursor cursor = null;
    try{
        cursor = AppContext.getContentResolver().query(uri, new String[]{projection}, queryArgs, null);
        if(cursor != null){
            List<T> data = extractListData(cursor, projection, type);
            cursor.close();
            return data;
        } else {
            return null;
        }
    } catch(Throwable e){
        if(cursor != null){
            cursor.close();
        }
        return null;
    }
}

public static boolean update(Uri uri, ContentValues values){
    try{
        int count = AppContext.getContentResolver().update(uri, values, null, null);
        return count == 1;
    } catch(Throwable e){
        return false;
    }
}
public static boolean delete(Uri uri){
    try{
        if(DocumentFile.isDocumentUri(AppContext.get(), uri)){
            return DocumentsContract.deleteDocument(AppContext.getContentResolver(), uri);
        } else {
            return AppContext.getContentResolver().delete(uri, null, null) == 1;
        }
    } catch(Throwable e){
        return false;
    }
}
public static Uri insert(Uri authority, ContentValues values){
    try{
        return AppContext.getContentResolver().insert(authority, values);
    } catch(Throwable e){
        return null;
    }
}

public static String getDocumentId(Uri uri){
    if(DocumentFile.isDocumentUri(AppContext.get(), uri)){
        return DocumentsContract.getDocumentId(uri);
    }
    else {
        return query(uri, DocumentsContract.Document.COLUMN_DOCUMENT_ID, String.class);
    }
}
public static Integer getFlags(Uri uri){
    return query(uri, DocumentsContract.Document.COLUMN_FLAGS, Integer.class);
}
public static String getDisplayName(Uri uri){
    return query(uri, DocumentsContract.Document.COLUMN_DISPLAY_NAME, String.class);
}
public static Integer getSize(Uri uri){
    return query(uri, DocumentsContract.Document.COLUMN_SIZE, Integer.class);
}
public static Long getLastModified(Uri uri){
    return query(uri, DocumentsContract.Document.COLUMN_LAST_MODIFIED, Long.class);
}
public static String getMineType(Uri uri){
    return query(uri, DocumentsContract.Document.COLUMN_MIME_TYPE, String.class);
}
public static void toDebugLogFlags(Uri uri){
    Integer flags = getFlags(uri);
    if(flags == null){
DebugLog.start().send("flags are null").end();
    } else {
        DebugString data = new DebugString();
        if((flags & DocumentsContract.Document.FLAG_DIR_BLOCKS_OPEN_DOCUMENT_TREE) != 0){
            data.append("DIR_BLOCKS_OPEN_DOCUMENT_TREE").nextLine();
        }
        if((flags & DocumentsContract.Document.FLAG_DIR_PREFERS_GRID) != 0){
            data.append("DIR_PREFERS_GRID").nextLine();
        }
        if((flags & DocumentsContract.Document.FLAG_DIR_PREFERS_LAST_MODIFIED) != 0){
            data.append("DIR_PREFERS_LAST_MODIFIED").nextLine();
        }
        if((flags & DocumentsContract.Document.FLAG_DIR_SUPPORTS_CREATE) != 0){
            data.append("DIR_SUPPORTS_CREATE").nextLine();
        }
        if((flags & DocumentsContract.Document.FLAG_PARTIAL) != 0){
            data.append("PARTIAL").nextLine();
        }
        if((flags & DocumentsContract.Document.FLAG_SUPPORTS_COPY) != 0){
            data.append("SUPPORTS_COPY").nextLine();
        }
        if((flags & DocumentsContract.Document.FLAG_SUPPORTS_DELETE) != 0){
            data.append("SUPPORTS_DELETE").nextLine();
        }
        if((flags & DocumentsContract.Document.FLAG_SUPPORTS_METADATA) != 0){
            data.append("SUPPORTS_METADATA").nextLine();
        }
        if((flags & DocumentsContract.Document.FLAG_SUPPORTS_MOVE) != 0){
            data.append("SUPPORTS_MOVE").nextLine();
        }
        if((flags & DocumentsContract.Document.FLAG_SUPPORTS_REMOVE) != 0){
            data.append("SUPPORTS_REMOVE").nextLine();
        }
        if((flags & DocumentsContract.Document.FLAG_SUPPORTS_RENAME) != 0){
            data.append("SUPPORTS_RENAME").nextLine();
        }
        if((flags & DocumentsContract.Document.FLAG_SUPPORTS_SETTINGS) != 0){
            data.append("SUPPORTS_SETTINGS").nextLine();
        }
        if((flags & DocumentsContract.Document.FLAG_SUPPORTS_THUMBNAIL) != 0){
            data.append("SUPPORTS_THUMBNAIL").nextLine();
        }
        if((flags & DocumentsContract.Document.FLAG_SUPPORTS_WRITE) != 0){
            data.append("SUPPORTS_WRITE").nextLine();
        }
        if((flags & DocumentsContract.Document.FLAG_VIRTUAL_DOCUMENT) != 0){
            data.append("VIRTUAL_DOCUMENT").nextLine();
        }
        if((flags & DocumentsContract.Document.FLAG_WEB_LINKABLE) != 0){
            data.append("WEB_LINKABLE").nextLine();
        }
DebugLog.start().send(data).end();
    }
}

public static String getDisplayPath(Uri uri){
    return getDisplayPath(DocumentFile.fromSingleUri(AppContext.get(), uri));
}
public static String getDisplayPath(DocumentFile documentFile){
    return getDisplayPath(documentFile.getUri(), documentFile);
}
public static String getDisplayPath(Uri uri, DocumentFile documentFile){
    boolean isDirectory = documentFile.isDirectory();
    String expectedName = documentFile.getName();
    if(Nullify.string(expectedName) == null){
DebugException.start().log("document file doesn't have fileName").end();
    }
    if(isDirectory && (expectedName!=null) && !expectedName.endsWith(Directory.PATH_SEPARATOR)){
        expectedName += Directory.PATH_SEPARATOR;
    }
    return getDisplayPath(uri, expectedName, isDirectory);
}
private static String getDisplayPath(Uri uri, String expectedName, boolean isDirectory){
    try{
        String documentId = DocumentFileHelper.getDocumentId(uri);
        if(documentId == null){
            String path = makeDisplayPathWithProvider(uri, expectedName);
            if(path != null){
                return path;
            }
            else{
                documentId = ROOT_UNKNOWN;
                if(expectedName!=null){
                    documentId+=expectedName;
                }
            }
        }
        String path = getDisplayPathFromDocumentId(documentId, isDirectory);
        if((path != null) && (expectedName != null) && !path.toLowerCase().endsWith(expectedName.toLowerCase())){
            path = makeDisplayPathWithProvider(uri, expectedName);
        }
        return path;
    } catch(Throwable e){
//        DebugException.pop().produce(e).log().pop();
        return null;
    }
}
private static String makeDisplayPathWithProvider(Uri uri, String expectedName){
    String documentId = null;
    ProviderInfo provider = AppContext.getPackageManager().resolveContentProvider(uri.getAuthority(), 0);
    if((provider != null)&&(provider.applicationInfo != null)){
        CharSequence appName = AppContext.getPackageManager().getApplicationLabel(provider.applicationInfo);
        if((appName != null)&&(appName.length()>0)){
            documentId = appName.toString().toLowerCase()+":";
            documentId = documentId.replace(" ", "_");
            if(expectedName!=null){
                documentId+=expectedName;
            }
        }
    }
    return documentId;
}

public static String getDisplayPathFromDocumentId(String documentId, boolean isDirectory){
    try{
        if(documentId == null){
            throw new Throwable("document id is null");
        }
        int indexOfSemiColon = documentId.indexOf(":");
        if(indexOfSemiColon == NULL_INDEX){
            throw new Throwable("invalid document id");
        }
        String idRoot = documentId.substring(0, indexOfSemiColon + 1);
        if(isRemovable(documentId)){
            documentId = documentId.replace(idRoot, ROOT_SDCARD);
            idRoot = ROOT_SDCARD;
        }
        String displayPath;
        if(ROOT_HOME.equals(idRoot)){
            displayPath = ROOT + documentId.replace(ROOT_HOME, ROOT_HOME_AS_DOCUMENTS);
        }
        else if(ROOT_PRIMARY.equals(idRoot)){
            String relativePath = documentId.substring(indexOfSemiColon + 1);
            if(relativePath.toLowerCase().startsWith(DIRECTORY_DOCUMENTS.toLowerCase())){
                relativePath = DIRECTORY_DOCUMENTS + relativePath.substring(DIRECTORY_DOCUMENTS.length());
            }
            displayPath = ROOT + relativePath;
        }
        else{
            displayPath = documentId;
        }
        if(isDirectory && !displayPath.endsWith(Directory.PATH_SEPARATOR)){
            displayPath += Directory.PATH_SEPARATOR;
        }
        return displayPath;
    } catch(Throwable e){
        return null;
    }
}
public static String getDisplayPathFromPath(String path){
    if(!VersionSDK.isSupEqualTo30_R()){
        String basePrimary = StorageMedia.getPublicDirectory_before30_R("/").getPath();
        if(path.startsWith(basePrimary)){
            return getDisplayPathFromRelativePath(ROOT, path.replace(basePrimary, ""));
        }
    }
    String basePrivateCache = StoragePackage.Type.PRIVATE_DATA_CACHE.getPath();
    if((basePrivateCache != null) && path.startsWith(basePrivateCache)){
        return getDisplayPathFromRelativePath(ROOT_PRIVATE, path.replace(basePrivateCache, ""));
    }
    return null;
}
public static String getDisplayPathFromRelativePath(String displayRoot, String relativePath){
    try{
        boolean isDirectory = relativePath.endsWith(Directory.PATH_SEPARATOR);
        if(relativePath.startsWith(Directory.PATH_SEPARATOR)){
            relativePath = relativePath.substring(1);
        }
        int indexOfSep = relativePath.indexOf(Directory.PATH_SEPARATOR);
        if(indexOfSep == NULL_INDEX){
            throw new Throwable("invalid relativePath");
        }
        String displayPath = displayRoot + relativePath;
        if(isDirectory && !displayPath.endsWith(Directory.PATH_SEPARATOR)){
            throw new Throwable("invalid relativePath");
        }
        return displayPath;
    } catch(Throwable e){
        return null;
    }
}

public static String getDocumentIdFromDisplayPath(String displayPath){
    try{
        boolean isDirectory = displayPath.endsWith(Directory.PATH_SEPARATOR);
        int indexOfSemiColon = displayPath.indexOf(":");
        if(indexOfSemiColon == NULL_INDEX){
            throw new Throwable("invalid displayPath");
        }
        String displayRoot = displayPath.substring(0, indexOfSemiColon+1);
        String documentId = "";
        if(ROOT.equals(displayRoot) && !VersionSDK.isSupEqualTo30_R()){
            boolean done = false;
            String relativePath = displayPath.substring(indexOfSemiColon + 1);
            int indexOfSep = displayPath.indexOf(Directory.PATH_SEPARATOR);
            if(indexOfSep != NULL_INDEX){
                String root = relativePath.substring(0, indexOfSep);
                if(DIRECTORY_DOCUMENTS.equals(root)){
                    documentId = ROOT_HOME + relativePath.substring(indexOfSep + 1);
                    done = true;
                }
            }
            if(!done){
                documentId = displayPath.replace(ROOT, ROOT_PRIMARY);
            }
        }
        else if(ROOT_PRIVATE.equals(displayRoot)){
            documentId = displayPath;
        }
        else {
            throw new Throwable("no resolution");
        }
        if(isDirectory){
            if(documentId.endsWith(Directory.PATH_SEPARATOR)){
                documentId = documentId.substring(0, documentId.length() - 1);
            }
            else{
                throw new Throwable("invalid displayPath");
            }
        }
        return documentId;
    } catch(Throwable e){
        return null;
    }
}
public static String getRelativePathFromDisplayPath(String displayPath){
    try{
        boolean isDirectory = displayPath.endsWith(Directory.PATH_SEPARATOR);
        int indexOfSemiColon = displayPath.indexOf(":");
        if(indexOfSemiColon == NULL_INDEX){
            throw new Throwable("invalid displayPath");
        }
        String relativePath = displayPath.substring(indexOfSemiColon + 1);
        if(isDirectory && !relativePath.endsWith(Directory.PATH_SEPARATOR)){
            throw new Throwable("invalid displayPath");
        }
        return relativePath;
    } catch(Throwable e){
        return null;
    }
}
public static String getPathFromDisplayPath(String displayPath){
    try{
        boolean isDirectory = displayPath.endsWith(Directory.PATH_SEPARATOR);
        int indexOfSemiColon = displayPath.indexOf(":");
        if(indexOfSemiColon == NULL_INDEX){
            throw new Throwable("invalid displayPath");
        }
        String displayRoot = displayPath.substring(0, indexOfSemiColon+1);
        StringBuilder pathBuilder = new StringBuilder();
        if(ROOT.equals(displayRoot) && !VersionSDK.isSupEqualTo30_R()){
            pathBuilder.append(StorageMedia.getPublicDirectory_before30_R("/").getPath());
        }
        else if(ROOT_PRIVATE.equals(displayRoot)){
            pathBuilder.append(StoragePackage.Type.PRIVATE_DATA.getPath());
        }
        else {
            throw new Throwable("no resolution");
        }
        pathBuilder.append(Directory.PATH_SEPARATOR).append(displayPath.substring(indexOfSemiColon + 1));
        String path = pathBuilder.toString();
        if(isDirectory && !path.endsWith(Directory.PATH_SEPARATOR)){
            throw new Throwable("invalid displayPath");
        }
        return path;
    } catch(Throwable e){
        return null;
    }
}

public static boolean isRemovable(String path){
    String r1 = "[ABCDEF[0-9]]";
    String r2 = r1 + r1 + r1 + r1;
    String regex = ".*\\b" + r2 + "-" + r2 + ":.*";
    if(path != null){
        return path.matches(regex);
    } else {
        return false;
    }
}

}
