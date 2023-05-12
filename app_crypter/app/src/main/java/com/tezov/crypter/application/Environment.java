/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.crypter.application;

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
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import com.tezov.lib_java_android.application.AppContext;

import androidx.fragment.app.Fragment;
import static com.tezov.crypter.application.AppConfig.CLEAN_CACHE_EXPIRED_DELAY_ms;
import static com.tezov.crypter.application.SharePreferenceKey.SP_DESTINATION_DIRECTORY_STRING;
import static com.tezov.lib_java.file.StoragePackage.Type.PRIVATE_DATA_CACHE;

import com.tezov.crypter.R;
import com.tezov.lib_java_android.application.SharedPreferences;
import com.tezov.lib_java.file.Directory;
import com.tezov.lib_java_android.file.StorageMedia;
import com.tezov.lib_java.file.StoragePackage;
import com.tezov.lib_java_android.file.StorageTree;
import com.tezov.lib_java_android.file.UriW;
import com.tezov.lib_java.util.UtilsString;

public class Environment{

public final static String EXTENSION_CIPHER_FILE = AppContext.getResources().getString(R.string.extension_cipher_file);
public final static String EXTENSION_CIPHER_TEXT = AppContext.getResources().getString(R.string.extension_cipher_text);
public final static String EXTENSION_KEYS_BACKUP = AppContext.getResources().getString(R.string.extension_keys_backup);
public final static String EXTENSION_SHARE_PUBLIC_KEY = AppContext.getResources().getString(R.string.extension_share_key_public);
public final static String EXTENSION_SHARE_ENCRYPTED_KEY = AppContext.getResources().getString(R.string.extension_share_key_encrypted);

//USER
public final static String DIRECTORY_ROOT = AppContext.getApplicationName() + Directory.PATH_SEPARATOR;
private final static int SUBDIRECTORY_LENGTH = 8;
//CACHE
private final static String PATH_PROVIDER = "provider" + Directory.PATH_SEPARATOR;
private final static String PATH_KEY_SHARE = "key_share" + Directory.PATH_SEPARATOR;
//URI
public static UriW obtainPendingUri(MediaPath mediaPath, String fileFullName){
    return obtainUri(mediaPath, fileFullName, new uriSupplier(){
        @Override
        public UriW obtainUriFromUriTree(StorageTree uriTree, String directory, String fileFullName){
            return uriTree.obtainUri(directory, fileFullName);
        }
        @Override
        public UriW obtainUriFromStorageMedia(String directory, String fileFullName){
            return StorageMedia.obtainPendingUri(directory, fileFullName);
        }
    });
}
public static UriW obtainUniquePendingUri(MediaPath mediaPath, String fileFullName){
    return obtainUri(mediaPath, fileFullName, new uriSupplier(){
        @Override
        public UriW obtainUriFromUriTree(StorageTree uriTree, String directory, String fileFullName){
            return uriTree.obtainUniqueUri(directory, fileFullName);
        }
        @Override
        public UriW obtainUriFromStorageMedia(String directory, String fileFullName){
            return StorageMedia.obtainUniquePendingUri(directory, fileFullName);
        }
    });
}
public static UriW obtainClosestPendingUri(MediaPath mediaPath, String fileFullName){
    return obtainUri(mediaPath, fileFullName, new uriSupplier(){
        @Override
        public UriW obtainUriFromUriTree(StorageTree uriTree, String directory, String fileFullName){
            return uriTree.obtainClosestUri(directory, fileFullName);
        }
        @Override
        public UriW obtainUriFromStorageMedia(String directory, String fileFullName){
            return StorageMedia.obtainClosestPendingUri(directory, fileFullName);
        }
    });
}
private static UriW obtainUri(MediaPath mediaPath, String fileFullName, uriSupplier supplier){
    String directoryPath = DIRECTORY_ROOT + mediaPath.getPath();
    UriW uri = null;
    SharedPreferences sp = Application.sharedPreferences();
    String destinationFolder = sp.getString(SP_DESTINATION_DIRECTORY_STRING);
    if(destinationFolder != null){
        StorageTree uriTree = StorageTree.fromLink(destinationFolder);
        if(uriTree != null){
            if(uriTree.canWrite()){
                uri = supplier.obtainUriFromUriTree(uriTree, directoryPath, fileFullName);
            } else {
                sp.remove(SP_DESTINATION_DIRECTORY_STRING);
            }
        }
    }
    if(uri == null){
        directoryPath = StorageMedia.findBestDirectoryForFile(fileFullName) + Directory.PATH_SEPARATOR + directoryPath;
        uri = supplier.obtainUriFromStorageMedia(directoryPath, fileFullName);
    }
    return uri;
}

//DIRECTORY CACHE
public static Directory obtainDirectoryCache(CachePath.Is path){
    return new Directory(PRIVATE_DATA_CACHE, path);
}
public static Directory obtainUniqueDirectoryCache(CachePath.Is path, String prefixName){
    Directory directory = obtainDirectoryCache(path);
    String name = UtilsString.appendDateAndTime(prefixName) + "_" + UtilsString.randomBase49(SUBDIRECTORY_LENGTH) + Directory.PATH_SEPARATOR;
    return new Directory(directory, name);
}
public static void cleanCache(){
    Directory directoryProvider = new Directory(PRIVATE_DATA_CACHE, PATH_PROVIDER);
    directoryProvider.deleteFiles(CLEAN_CACHE_EXPIRED_DELAY_ms, true, false);
    Directory directoryBackup = new Directory(PRIVATE_DATA_CACHE, CachePath.KEYS_BACKUP);
    directoryBackup.deleteFiles(CLEAN_CACHE_EXPIRED_DELAY_ms, true, true);
    Directory directoryCipherText = new Directory(PRIVATE_DATA_CACHE, CachePath.CIPHER_TEXT);
    directoryCipherText.deleteFiles(CLEAN_CACHE_EXPIRED_DELAY_ms, true, false);
}

public enum MediaPath{
    ENCRYPTED_FILES("files_encrypted"), DECRYPTED_FILES("files_decrypted"), ENCRYPTED_TEXT("texts_encrypted"), DECRYPTED_TEXT("texts_decrypted"), EXPORTED_KEYS("keys_backup");
    String path;
    MediaPath(String path){
        this.path = path + Directory.PATH_SEPARATOR;
    }
    public String getPath(){
        return path;
    }
}
public interface CachePath extends StoragePackage.Path{
    Is KEYS_BACKUP = new Is("KEYS_BACKUP", "keys_backup");
    Is KEY_SHARE_PUBLIC = new Is("KEY_SHARE_PUBLIC", PATH_PROVIDER + PATH_KEY_SHARE + "public");
    Is KEY_SHARE_ENCRYPTED = new Is("KEY_SHARE_ENCRYPTED", PATH_PROVIDER + PATH_KEY_SHARE + "encrypted");
    Is CIPHER_TEXT = new Is("CIPHER_TEXT", PATH_PROVIDER + "cipher_text");
}

private interface uriSupplier{
    UriW obtainUriFromUriTree(StorageTree uriTree, String directory, String fileFullName);
    UriW obtainUriFromStorageMedia(String directory, String fileFullName);
}


}
