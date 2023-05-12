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
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import androidx.fragment.app.Fragment;

import com.tezov.lib_java_android.application.AppContext;
import com.tezov.lib_java_android.application.AppInfo;

import com.tezov.lib_java_android.application.AppPermission;
import com.tezov.lib_java.file.Directory;
import com.tezov.lib_java.type.runnable.RunnableGroup;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentResolver.SCHEME_CONTENT;
import static com.tezov.lib_java.util.UtilsList.NULL_INDEX;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.annotation.RequiresApi;

import com.tezov.lib_java_android.R;
import com.tezov.lib_java_android.application.VersionSDK;
import com.tezov.lib_java.async.notifier.observer.state.ObserverStateE;
import com.tezov.lib_java.async.notifier.observer.value.ObserverValue;
import com.tezov.lib_java.async.notifier.task.TaskState;
import com.tezov.lib_java.async.notifier.task.TaskValue;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.toolbox.Nullify;
import com.tezov.lib_java.wrapperAnonymous.ComparatorW;
import com.tezov.lib_java.wrapperAnonymous.PredicateW;
import com.tezov.lib_java.wrapperAnonymous.SupplierW;
import com.tezov.lib_java.type.collection.ListEntry;
import com.tezov.lib_java.type.collection.ListOrObject;
import com.tezov.lib_java_android.type.image.imageHolder.ImageBitmap;
import com.tezov.lib_java_android.ui.activity.ActivityBase;
import com.tezov.lib_java_android.ui.view.status.StatusParam;
import com.tezov.lib_java_android.util.UtilsIntent;
import com.tezov.lib_java.util.UtilsList;
import com.tezov.lib_java.util.UtilsString;

import org.apache.commons.lang3.math.NumberUtils;

import java.io.ByteArrayInputStream;
import java.util.List;

public final class StorageMedia{
    
private final static String AUTHORITY_EXTERNAL_DOCUMENTS = "com.android.externalstorage.documents";
private final static String AUTHORITY_MEDIA_DOCUMENTS = "com.android.providers.media.documents";
private final static String AUTHORITY_MEDIA_DOWNLOAD = "com.android.providers.downloads.documents";
private StorageMedia(){
}
private static Class<StorageMedia> myClass(){
    return StorageMedia.class;
}
public static boolean isImage(String extension){
    return extension.toLowerCase().matches("(?:jpg)|(?:jpeg)|(?:png)|(?:gif)|(?:webp)");
}
public static boolean isVideo(String extension){
    return extension.toLowerCase().matches("(?:mp4)|(?:3gp)");
}
public static boolean isAudio(String extension){
    return extension.toLowerCase().matches("(?:mp3)|(?:ogg)");
}
public static boolean isDocument(String extension){
    return extension.toLowerCase().matches("(?:txt)|(?:xml)|(?:json)|(?:pdf)");
}

private static String getVolumeName(){
    if(VersionSDK.isSupEqualTo29_Q()){
        return MediaStore.VOLUME_EXTERNAL_PRIMARY;
    }
    else{
        return MediaStore.VOLUME_EXTERNAL;
    }

}
public static android.net.Uri getMediaImagesUri(){
    return MediaStore.Images.Media.getContentUri(getVolumeName());
}
public static android.net.Uri getMediaVideoUri(){
    return MediaStore.Video.Media.getContentUri(getVolumeName());
}
public static android.net.Uri getMediaAudioUri(){
    return MediaStore.Audio.Media.getContentUri(getVolumeName());
}
public static android.net.Uri getMediaFilesUri(){
    return MediaStore.Files.getContentUri(getVolumeName());
}
public static Uri getMediaUriForDirectory(String directory){
    int indexOfSep = directory.indexOf(Directory.PATH_SEPARATOR);
    if(indexOfSep != NULL_INDEX){
        directory = directory.substring(0, indexOfSep);
    }
    Uri uri;
    if(directory.equals(Environment.DIRECTORY_DOCUMENTS)){
        uri = getMediaFilesUri();
    } else if(directory.equals(Environment.DIRECTORY_PICTURES)){
        uri = getMediaImagesUri();
    } else if(directory.equals(Environment.DIRECTORY_MOVIES)){
        uri = getMediaVideoUri();
    } else if(directory.equals(Environment.DIRECTORY_MUSIC)){
        uri = getMediaAudioUri();
    } else {
        uri = getMediaFilesUri();
    }
    return uri;
}

@RequiresApi(api = Build.VERSION_CODES.Q)
public static android.net.Uri getMediaDownloadUri(){
    return MediaStore.Downloads.getContentUri(getVolumeName());
}

public static String findBestDirectoryForFile(String fileFullName){
    String extension = UtilsFile.getExtension(fileFullName);
    if(extension != null){
        return findBestDirectoryForExtension(extension);
    } else {
        return Environment.DIRECTORY_DOCUMENTS;
    }
}
public static String findBestDirectoryForExtension(String extension){
    if(isDocument(extension)){
        return Environment.DIRECTORY_DOCUMENTS;
    } else if(isImage(extension)){
        return Environment.DIRECTORY_PICTURES;
    } else if(isVideo(extension)){
        return Environment.DIRECTORY_MOVIES;
    } else if(isAudio(extension)){
        return Environment.DIRECTORY_MUSIC;
    } else {
        return Environment.DIRECTORY_DOCUMENTS;
    }
}

@SuppressWarnings("deprecation")
public static java.io.File getPublicDirectory_before30_R(String directory){
    if(!directory.startsWith(Directory.PATH_SEPARATOR)){
        directory = Directory.PATH_SEPARATOR + directory;
    }
    return Environment.getExternalStoragePublicDirectory(directory);
}
public static UriW obtainPendingUri(String fileFullName){
    return obtainPendingUri(findBestDirectoryForFile(fileFullName), fileFullName);
}
public static UriW obtainPendingUri(String directoryPath, String fileFullName){
    if(VersionSDK.isSupEqualTo30_R()){
        android.net.Uri mediaUri = getMediaUriForDirectory(directoryPath);
        String mimeType = UtilsFile.getMimeTypeForExtension(UtilsFile.getExtension(fileFullName));
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileFullName);
        values.put(MediaStore.MediaColumns.MIME_TYPE, mimeType);
        values.put(MediaStore.MediaColumns.IS_PENDING, 1);
        values.put(MediaStore.MediaColumns.RELATIVE_PATH, directoryPath);
        if(directoryPath.equals(Environment.DIRECTORY_PICTURES)){
            long timestamp = Clock.MilliSecond.now();
            values.put(MediaStore.Images.Media.DATE_ADDED, timestamp);
            values.put(MediaStore.Images.Media.DATE_TAKEN, timestamp);
        }
        android.net.Uri uri = DocumentFileHelper.insert(mediaUri, values);
        if(uri == null){
            return null;
        } else {
            UriW uriW = new UriW(uri, UriW.Type.STORAGE_MEDIA);
            if(fileFullName.equals(uriW.getFullName())){
                return uriW;
            } else {
                uriW.delete();
                return null;
            }
        }
    } else {
        java.io.File file = StorageFile.obtain(getPublicDirectory_before30_R(directoryPath).getPath(), fileFullName);
        if(file == null){
            return null;
        } else {
            return new UriW(file);
        }
    }
}
@RequiresApi(api = Build.VERSION_CODES.R)
private static List<String> selectFileLike(String directoryPath, String fileFullName, boolean excludeFileFullName){
    UtilsFile.FileName fileFullNameExploded = new UtilsFile.FileName(fileFullName);
    android.net.Uri mediaUri = getMediaUriForDirectory(directoryPath);
    if(!directoryPath.endsWith(Directory.PATH_SEPARATOR)){
        directoryPath += Directory.PATH_SEPARATOR;
    }
    Bundle queryArgs = new Bundle();
    String selection = "(" + MediaStore.MediaColumns.DISPLAY_NAME + " LIKE ? OR " + MediaStore.MediaColumns.DISPLAY_NAME + "=?)";
    selection += " AND " + MediaStore.MediaColumns.RELATIVE_PATH + "=?";
    String[] selection_Arguments = new String[]{fileFullNameExploded.getQueryPattern(), fileFullNameExploded.getFullNameWithoutNumber(), directoryPath};
    queryArgs.putString(ContentResolver.QUERY_ARG_SQL_SELECTION, selection);
    queryArgs.putStringArray(ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS, selection_Arguments);
    queryArgs.putInt(MediaStore.QUERY_ARG_MATCH_PENDING, MediaStore.MATCH_INCLUDE);
    String projection = MediaStore.MediaColumns.DISPLAY_NAME;
    List<String> fileNames = DocumentFileHelper.query(mediaUri, projection, queryArgs, String.class);
    if(fileNames != null){
        PredicateW<String> predicate = StorageFile.newPredicateSelectFileLike(fileFullNameExploded, excludeFileFullName);
        fileNames = UtilsList.filter(fileNames, predicate, (SupplierW<List<String>>)ListOrObject::new);
        return Nullify.collection(fileNames);
    } else {
        return null;
    }
}
public static UriW obtainUniquePendingUri(String fileFullName){
    return obtainUniquePendingUri(findBestDirectoryForFile(fileFullName), fileFullName);
}
public static UriW obtainUniquePendingUri(String directoryPath, String fileFullName){
    if(VersionSDK.isSupEqualTo30_R()){
        List<String> fileNames = selectFileLike(directoryPath, fileFullName, false);
        List<String> fileNamesSorted = null;
        if(fileNames != null){
            fileNamesSorted = UtilsList.sort(fileNames, new ComparatorW<String>(){
                @Override
                public int compare(String s1, String s2){
                    Integer n1 = UtilsFile.FileName.getNumber(s1);
                    Integer n2 = UtilsFile.FileName.getNumber(s2);
                    if((n1 != null) && (n2 != null)){
                        return Integer.compare(n1, n2);
                    } else if(n1 != null){
                        return 1;
                    } else {
                        return -1;
                    }
                }
            });
        }
        UtilsFile.FileName fileName = new UtilsFile.FileName(fileFullName);
        if(fileNamesSorted != null){
            Integer biggerNumber = UtilsFile.FileName.getNumber(fileNamesSorted.get(fileNamesSorted.size() - 1));
            if((biggerNumber == null) || (biggerNumber < 0)){
                biggerNumber = 0;
            }
            fileName.setNumber(biggerNumber + 1);
        }
        int attempt = 0;
        do{
            UriW uri = StorageMedia.obtainPendingUri(directoryPath, fileName.getFullName());
            if(uri != null){
                return uri;
            }
            fileName.incNumber();
            attempt++;
        } while(attempt < StorageFile.URI_OBTAIN_MAX_RETRY);
        fileName.appendToName("_" + UtilsString.randomBase49(StorageFile.URI_RANDOM_HEX_LENGTH)).setNumber(null);
        return StorageMedia.obtainPendingUri(directoryPath, fileName.getFullName());
    } else {
        java.io.File file = StorageFile.obtainUnique(getPublicDirectory_before30_R(directoryPath).getPath(), fileFullName);
        if(file == null){
            return null;
        } else {
            return new UriW(file);
        }
    }
}
public static UriW obtainClosestPendingUri(String fileFullName){
    return obtainClosestPendingUri(findBestDirectoryForFile(fileFullName), fileFullName);
}
public static UriW obtainClosestPendingUri(String directoryPath, String fileFullName){
    UriW uri = findUri(directoryPath, fileFullName);
    if((uri != null) && uri.pending(true)){
        return uri;
    }
    uri = obtainPendingUri(directoryPath, fileFullName);
    if((uri != null) && uri.pending(true)){
        return uri;
    }
    if(VersionSDK.isSupEqualTo30_R()){
        List<String> fileNames = selectFileLike(directoryPath, fileFullName, true);
        List<String> fileNamesSorted = null;
        if(fileNames != null){
            fileNamesSorted = UtilsList.sort(fileNames, new ComparatorW<String>(){
                @Override
                public int compare(String s1, String s2){
                    Integer n1 = UtilsFile.FileName.getNumber(s1);
                    Integer n2 = UtilsFile.FileName.getNumber(s2);
                    if((n1 != null) && (n2 != null)){
                        return Integer.compare(n1, n2);
                    } else if(n1 != null){
                        return 1;
                    } else {
                        return -1;
                    }
                }
            });
        }
        Integer biggerNumber = null;
        if(fileNamesSorted != null){
            for(String s: fileNamesSorted){
                uri = findUri(directoryPath, s);
                if((uri != null) && uri.pending(true)){
                    return uri;
                }
            }
            biggerNumber = UtilsFile.FileName.getNumber(fileNamesSorted.get(fileNamesSorted.size() - 1));
            if((biggerNumber == null) || (biggerNumber < 0)){
                biggerNumber = 0;
            }
        }
        int attempt = 0;
        UtilsFile.FileName fileName = new UtilsFile.FileName(fileFullName);
        if(biggerNumber == null){
            fileName.setNumber(null);
        } else {
            fileName.setNumber(biggerNumber + 1);
        }
        do{
            uri = StorageMedia.obtainPendingUri(directoryPath, fileName.getFullName());
            if(uri != null){
                return uri;
            }
            fileName.incNumber();
            attempt++;
        } while(attempt < StorageFile.URI_OBTAIN_MAX_RETRY);
        fileName.appendToName("_" + UtilsString.randomBase49(StorageFile.URI_RANDOM_HEX_LENGTH)).setNumber(null);
        return StorageMedia.obtainPendingUri(directoryPath, fileName.getFullName());
    } else {
        java.io.File file = StorageFile.obtainClosest(getPublicDirectory_before30_R(directoryPath).getPath(), fileFullName);
        if(file == null){
            return null;
        } else {
            return new UriW(file);
        }
    }
}
public static UriW findUri(String directoryPath, String fileFullName){
    if(VersionSDK.isSupEqualTo30_R()){
        android.net.Uri mediaUri = getMediaUriForDirectory(directoryPath);
        if(!directoryPath.endsWith(Directory.PATH_SEPARATOR)){
            directoryPath += Directory.PATH_SEPARATOR;
        }
        Bundle queryBundle = new Bundle();
        String selection = MediaStore.MediaColumns.DISPLAY_NAME + "=?";
        selection += " AND " + MediaStore.MediaColumns.RELATIVE_PATH + "=?";
        String[] selection_Arguments = new String[]{fileFullName, directoryPath};
        queryBundle.putString(ContentResolver.QUERY_ARG_SQL_SELECTION, selection);
        queryBundle.putStringArray(ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS, selection_Arguments);
        queryBundle.putInt(MediaStore.QUERY_ARG_MATCH_PENDING, MediaStore.MATCH_INCLUDE);
        ContentResolver resolver = AppContext.getContentResolver();
        String[] projection = new String[]{MediaStore.MediaColumns._ID};
        Cursor cursor = null;
        try{
            cursor = resolver.query(mediaUri, projection, queryBundle, null);
            if(cursor != null){
                UriW uri = null;
                cursor.moveToFirst();
                int indexId = cursor.getColumnIndex(MediaStore.MediaColumns._ID);
                if(indexId != NULL_INDEX){
                    String id = cursor.getString(indexId);
                    uri = new UriW(makeMediaUri(mediaUri, id), UriW.Type.STORAGE_MEDIA);
                }
                cursor.close();
                return uri;
            } else {
                return null;
            }
        } catch(Throwable e){
            if(cursor != null){
                cursor.close();
            }
            return null;
        }
    } else {
        java.io.File file = StorageFile.findFile(getPublicDirectory_before30_R(directoryPath).getPath(), fileFullName);
        if(file == null){
            return null;
        } else {
            return new UriW(file);
        }
    }
}
private static Uri makeMediaUri(Uri uriMedia, String id){
    String encodedPath = uriMedia.getEncodedPath();
    if(encodedPath.startsWith(Directory.PATH_SEPARATOR)){
        encodedPath = encodedPath.substring(Directory.PATH_SEPARATOR.length());
    }
    return new android.net.Uri.Builder().encodedAuthority(uriMedia.getAuthority()).scheme(SCHEME_CONTENT).appendEncodedPath(encodedPath).appendPath(id).build();
}

public static Uri convertMediaDocumentsProviderUri(Uri uri){
    if(AUTHORITY_MEDIA_DOCUMENTS.equals(uri.getAuthority())){
        String documentId = DocumentFileHelper.getDocumentId(uri);
        if(documentId == null){
            return null;
        }
        int indexOfSemiColon = documentId.indexOf(":");
        if(indexOfSemiColon == NULL_INDEX){
            return null;
        }
        String idRoot = documentId.substring(0, indexOfSemiColon);
        if(Nullify.string(idRoot) == null){
            return null;
        }
        idRoot = idRoot.toLowerCase();
        String id = documentId.substring(indexOfSemiColon + 1);
        if(!NumberUtils.isDigits(id)){
            return null;
        }
        Uri uriMedia;
        switch(idRoot){
            case "image":{
                uriMedia = getMediaImagesUri();
            }break;
            case "video":{
                uriMedia = getMediaVideoUri();
            } break;
            case "audio":{
                uriMedia = getMediaAudioUri();
            }break;
            case "document":
            case "documents":{
                uriMedia = getMediaFilesUri();
            }break;
            default:
                return null;
        }
        return makeMediaUri(uriMedia, id);
    }
    else{
        return null;
    }
}

public static TaskState.Observable PERMISSION_REQUEST_READ(boolean showPermissionDeniedToast){
    TaskState task = new TaskState();
    ActivityBase.RequestForPermission request = AppPermission.request().add(Manifest.permission.READ_EXTERNAL_STORAGE);
    request.observe(new ObserverValue<ListEntry<String, Boolean>>(myClass()){
        @Override
        public void onComplete(ListEntry<String, Boolean> permissions){
            if(AppPermission.allTrue(permissions)){
                task.notifyComplete();
            } else {
                if(showPermissionDeniedToast){
                    AppInfo.toast(R.string.lbl_permission_denied, StatusParam.DELAY_FAIL_LONG_ms, StatusParam.Color.FAILED);
                }
                task.notifyException("AppPermission denied");
            }
        }
    }).start();
    return task.getObservable();
}
public static boolean PERMISSION_CHECK_READ(){
    AppPermission.Check check = AppPermission.check().add(Manifest.permission.READ_EXTERNAL_STORAGE);
    return AppPermission.allTrue(check.result());
}

public static TaskState.Observable PERMISSION_REQUEST_WRITE(boolean showPermissionDeniedToast){
    TaskState task = new TaskState();
    ActivityBase.RequestForPermission request = AppPermission.request().add(Manifest.permission.READ_EXTERNAL_STORAGE);
    if(!VersionSDK.isSupEqualTo30_R()){
        request.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }
    request.observe(new ObserverValue<ListEntry<String, Boolean>>(myClass()){
        @Override
        public void onComplete(ListEntry<String, Boolean> permissions){
            if(AppPermission.allTrue(permissions)){
                task.notifyComplete();
            } else {
                if(showPermissionDeniedToast){
                    AppInfo.toast(R.string.lbl_permission_denied, StatusParam.DELAY_FAIL_LONG_ms, StatusParam.Color.FAILED);
                }
                task.notifyException("AppPermission denied");
            }
        }
    }).start();
    return task.getObservable();
}
public static boolean PERMISSION_CHECK_WRITE(){
    AppPermission.Check check = AppPermission.check().add(Manifest.permission.READ_EXTERNAL_STORAGE);
    if(!VersionSDK.isSupEqualTo30_R()){
        check.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }
    return AppPermission.allTrue(check.result());
}

public static TaskValue<ListOrObject<UriW>> openDocument(boolean retainPermission){
    return openDocument(retainPermission, false);
}
public static TaskValue<ListOrObject<UriW>> openDocument(boolean retainPermission, boolean allowMultiple){
    return openDocument(null, retainPermission, allowMultiple);
}
public static TaskValue<ListOrObject<UriW>> openDocument(String mimeType, boolean retainPermission, boolean allowMultiple){
    return openDocument(mimeType, R.string.select_file_title, retainPermission, allowMultiple, true);
}
public static TaskValue<ListOrObject<UriW>> openDocument(String mimeType, int titleResourceId, boolean retainPermission, boolean allowMultiple, boolean  showPermissionDeniedToast){
    return openDocument(mimeType, AppContext.getResources().getString(titleResourceId), retainPermission, allowMultiple, showPermissionDeniedToast);
}
public static TaskValue<ListOrObject<UriW>> openDocument(String mimeType, String title, boolean retainPermission, boolean allowMultiple, boolean showPermissionDeniedToast){
    RequestForResult request = new RequestForResult(retainPermission);
    RunnableGroup gr = new RunnableGroup("openDocument");
    if(!StorageMedia.PERMISSION_CHECK_READ()){
        gr.add(new RunnableGroup.Action(){
            @Override
            public void runSafe() throws Throwable{
                StorageMedia.PERMISSION_REQUEST_READ(showPermissionDeniedToast).observe(new ObserverStateE(this){
                    @Override
                    public void onComplete(){
                        next();
                    }
                    @Override
                    public void onException(Throwable e){
                        putException(e);
                        done();
                    }
                });
            }
        }.name("request permission read"));
    }
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe() throws Throwable{
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
            intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            if(allowMultiple){
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            }
            intent.setType(mimeType!=null?mimeType:UtilsFile.MINE_TYPE_ALL);
            intent = Intent.createChooser(intent, title);
            request.setIntent(intent);
            request.start();
            done();
        }
    });
    gr.setOnDone(new RunnableGroup.Action(){
        @Override
        public void runSafe() throws Throwable{
            Throwable e = getException();
            if(e != null){
                request.getTask().notifyException(e);
            }
        }
    });
    gr.start();
    return request.getTask();
}

public static TaskValue<ListOrObject<UriW>> getContent(boolean retainPermission){
    return getContent(retainPermission, false);
}
public static TaskValue<ListOrObject<UriW>> getContent(boolean retainPermission, boolean allowMultiple){
    return getContent(null, retainPermission, allowMultiple);
}
public static TaskValue<ListOrObject<UriW>> getContent(String mimeType, boolean retainPermission, boolean allowMultiple){
    return getContent(mimeType, R.string.select_file_title, retainPermission, allowMultiple, true);
}
public static TaskValue<ListOrObject<UriW>> getContent(String mimeType, int titleResourceId, boolean retainPermission, boolean allowMultiple, boolean showPermissionDeniedToast){
    return getContent(mimeType, AppContext.getResources().getString(titleResourceId), retainPermission, allowMultiple, showPermissionDeniedToast);
}
public static TaskValue<ListOrObject<UriW>> getContent(String mimeType, String title, boolean retainPermission, boolean allowMultiple, boolean showPermissionDeniedToast){
    RequestForResult request = new RequestForResult(retainPermission);
    RunnableGroup gr = new RunnableGroup("openDocument");
    if(!StorageMedia.PERMISSION_CHECK_READ()){
        gr.add(new RunnableGroup.Action(){
            @Override
            public void runSafe() throws Throwable{
                StorageMedia.PERMISSION_REQUEST_READ(showPermissionDeniedToast).observe(new ObserverStateE(this){
                    @Override
                    public void onComplete(){
                        next();
                    }
                    @Override
                    public void onException(Throwable e){
                        putException(e);
                        done();
                    }
                });
            }
        }.name("request permission read"));
    }
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe() throws Throwable{
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
            intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            if(allowMultiple){
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            }
            intent.setType(mimeType!=null?mimeType:UtilsFile.MINE_TYPE_ALL);
            intent = Intent.createChooser(intent, title);
            request.setIntent(intent);
            request.start();
            done();
        }
    });
    gr.setOnDone(new RunnableGroup.Action(){
        @Override
        public void runSafe() throws Throwable{
            Throwable e = getException();
            if(e != null){
                request.getTask().notifyException(e);
            }
        }
    });
    gr.start();
    return request.getTask();
}

public static TaskValue<ListOrObject<UriW>>.Observable randomPicture(int width, int height){
    return randomPicture(width, height, 1);
}
public static TaskValue<ListOrObject<UriW>>.Observable randomPicture(int width, int height, int number){
    TaskValue<ListOrObject<UriW>> task = new TaskValue<>();
    StorageMedia.PERMISSION_REQUEST_WRITE(true).observe(new ObserverStateE(myClass()){
        @Override
        public void onComplete(){
            ListOrObject<UriW> uris = new ListOrObject<>();
            try{
                for(int i = 0; i < number; i++){
                    ImageBitmap img = ImageBitmap.random(width, height);
                    UriW uri = StorageMedia.obtainPendingUri(width + "x" + height + "_" + UtilsString.randomHex(4) + ".jpg");
                    UtilsFile.transfer(new ByteArrayInputStream(img.toImageJpeg(100).toByteBuffer().array()), uri);
                    uri.pending(false);
                    uris.add(uri);
                }
                task.notifyComplete(uris);
            } catch(Throwable e){
                for(UriW uri: uris){
                    uri.delete();
                }
                task.notifyException(e);
            }
        }
        @Override
        public void onException(Throwable e){
            task.notifyException(e);
        }
    });
    return task.getObservable();
}

public static class RequestForResult extends ActivityBase.RequestForResult{
    private final TaskValue<ListOrObject<UriW>> task;
    private final boolean retainPermission;
    public RequestForResult(boolean retainPermission){
        task = new TaskValue<>();
        this.retainPermission = retainPermission;
    }
    public TaskValue<ListOrObject<UriW>> getTask(){
        return task;
    }
    @Override
    public void onActivityResult(int resultCode, Intent intent){
        if(resultCode == RESULT_OK){
            ListOrObject<UriW> results = UtilsIntent.getUris(intent, retainPermission);
            if(results != null){
                task.notifyComplete(results);
            } else {
                task.notifyException(null, "result ok, but no uri returned");
            }
        } else {
            task.notifyException(null, "result not ok");
        }
    }

}

}
