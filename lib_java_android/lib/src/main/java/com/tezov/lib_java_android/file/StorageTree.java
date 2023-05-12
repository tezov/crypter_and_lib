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
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import androidx.fragment.app.Fragment;

import com.tezov.lib_java.async.notifier.task.TaskValue;
import com.tezov.lib_java.file.Directory;
import com.tezov.lib_java.file.UtilsFile;
import com.tezov.lib_java.type.collection.ListOrObject;
import com.tezov.lib_java_android.R;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugString;

import com.tezov.lib_java.wrapperAnonymous.PredicateW;

import android.content.Intent;
import android.net.Uri;

import androidx.documentfile.provider.DocumentFile;

import com.tezov.lib_java_android.application.AppContext;
import com.tezov.lib_java.toolbox.Nullify;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.wrapperAnonymous.ComparatorW;
import com.tezov.lib_java.util.UtilsList;
import com.tezov.lib_java.util.UtilsString;

import java.util.ArrayList;
import java.util.List;

public class StorageTree{
private final static String MINE_TYPE_DUMMY = "application/dummy";
private final DocumentFile document;
public StorageTree(Uri uri){
DebugTrack.start().create(this).end();
    document = DocumentFile.fromTreeUri(AppContext.get(), uri);
}

public static TaskValue<ListOrObject<UriW>> openDocumentTree(){
    return openDocumentTree(R.string.select_directory_title);
}
public static TaskValue<ListOrObject<UriW>> openDocumentTree(int titleResourceId){
    return openDocumentTree(AppContext.getResources().getString(titleResourceId));
}
public static TaskValue<ListOrObject<UriW>> openDocumentTree(String title){
    Intent intent = new Intent();
    intent.setAction(Intent.ACTION_OPEN_DOCUMENT_TREE);
    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
    intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
    intent = Intent.createChooser(intent, title);
    StorageMedia.RequestForResult request = new StorageMedia.RequestForResult(true);
    request.setIntent(intent);
    request.start();
    return request.getTask();
}

public static StorageTree fromLink(String link){
    Uri uri = Uri.parse(link);
    if(uri == null){
        return null;
    } else {
        return new StorageTree(uri);
    }
}
public static String toLink(UriW uri){
    return toLink(uri.get());
}
public static String toLink(Uri uri){
    return uri.toString();
}

public boolean exist(){
    return document.exists();
}
public boolean canRead(){
    return document.canRead();
}
public boolean canWrite(){
    return document.canWrite();
}

public String getName(){
    return Nullify.string(document.getName());
}
public String getDisplayPath(){
    return DocumentFileHelper.getDisplayPath(document);
}
private DocumentFile obtainDocumentDirectory(String directoryPath) throws Throwable{
    DocumentFile documentDirectory = document;
    if(directoryPath != null){
        if(directoryPath.endsWith(Directory.PATH_SEPARATOR)){
            directoryPath = directoryPath.substring(0, directoryPath.length() - 1);
        }
        String[] directories = directoryPath.split(Directory.PATH_SEPARATOR);
        for(String d: directories){
            DocumentFile f = documentDirectory.findFile(d);
            if(f != null){
                if(!f.isDirectory()){
                    throw new Throwable(d + " exist, but is not a directory for path " + document + "../" + directoryPath);
                }
            } else {
                f = documentDirectory.createDirectory(d);
                if(f == null){
                    throw new Throwable("directory can't created");
                }
            }
            documentDirectory = f;
        }
    }
    if(!documentDirectory.canWrite()){
        throw new Throwable("directory can't be written");
    }
    return documentDirectory;
}
private DocumentFile findDocumentDirectory(String directoryPath) throws Throwable{
    DocumentFile documentDirectory = document;
    if(directoryPath != null){
        if(directoryPath.endsWith(Directory.PATH_SEPARATOR)){
            directoryPath = directoryPath.substring(0, directoryPath.length() - 1);
        }
        String[] directories = directoryPath.split(Directory.PATH_SEPARATOR);
        for(String d: directories){
            DocumentFile f = documentDirectory.findFile(d);
            if(f == null){
                return null;
            }
            if(!f.isDirectory()){
                throw new Throwable(d + " exist, but is not a directory for path " + document + "../" + directoryPath);
            }
            if(!f.canWrite()){
                throw new Throwable("directory can't be written");
            }
            documentDirectory = f;
        }
    }
    return documentDirectory;
}
private UriW obtainUri(DocumentFile documentDirectory, String fileFullName){
    if(documentDirectory == null){
        return null;
    }
    DocumentFile d = documentDirectory.findFile(fileFullName);
    if(d == null){
        d = documentDirectory.createFile(MINE_TYPE_DUMMY, fileFullName);
        if(d == null){
            return null;
        }
    }
    if(!d.canWrite()){
        return null;
    }
    return new UriW(d, UriW.Type.STORAGE_TREE);
}
public UriW obtainUri(String directoryPath, String fileFullName){
    try{
        return obtainUri(obtainDocumentDirectory(directoryPath), fileFullName);
    } catch(Throwable e){
DebugException.start().log(e).end();
        return null;
    }
}
private List<String> selectFileLike(String directoryPath, String fileFullName, boolean excludeFileFullName){
    try{
        DocumentFile documentDirectory = findDocumentDirectory(directoryPath);
        if(documentDirectory == null){
            return null;
        }
        UtilsFile.FileName fileFullNameExploded = new UtilsFile.FileName(fileFullName);
        PredicateW<String> predicate = StorageFile.newPredicateSelectFileLike(fileFullNameExploded, excludeFileFullName);
        List<String> fileNames = new ArrayList<>();
        DocumentFile[] files = documentDirectory.listFiles();
        for(DocumentFile f: files){
            String fullName = f.getName();
            if(predicate.test(fullName)){
                fileNames.add(fullName);
            }
        }
        return Nullify.collection(fileNames);
    } catch(Throwable e){
DebugException.start().log(e).end();
        return null;
    }
}
public UriW obtainUniqueUri(String directoryPath, String fileFullName){
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
    try{
        DocumentFile documentDirectory = obtainDocumentDirectory(directoryPath);
        int attempt = 0;
        do{
            UriW uri = obtainUri(documentDirectory, fileName.getFullName());
            if(uri != null){
                return uri;
            }
            fileName.incNumber();
            attempt++;
        } while(attempt < StorageFile.URI_OBTAIN_MAX_RETRY);
        fileName.appendToName("_" + UtilsString.randomBase49(StorageFile.URI_RANDOM_HEX_LENGTH)).setNumber(null);
        return obtainUri(documentDirectory, fileName.getFullName());
    } catch(Throwable e){
DebugException.start().log(e).end();
        return null;
    }
}
public UriW obtainClosestUri(String directoryPath, String fileFullName){
    UriW uri = findUri(directoryPath, fileFullName);
    if(uri != null){
        return uri;
    }
    uri = obtainUri(directoryPath, fileFullName);
    if(uri != null){
        return uri;
    }
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
            if(uri != null){
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
    DocumentFile documentDirectory;
    try{
        documentDirectory = obtainDocumentDirectory(directoryPath);
    } catch(Throwable e){
DebugException.start().log(e).end();
        return null;
    }
    do{
        uri = obtainUri(documentDirectory, fileName.getFullName());
        if(uri != null){
            return uri;
        }
        fileName.incNumber();
        attempt++;
    } while(attempt < StorageFile.URI_OBTAIN_MAX_RETRY);
    fileName.appendToName("_" + UtilsString.randomBase49(StorageFile.URI_RANDOM_HEX_LENGTH)).setNumber(null);
    return obtainUri(directoryPath, fileName.getFullName());
}
public UriW findUri(String directoryPath, String fileFullName){
    try{
        return getUri(findDocumentDirectory(directoryPath), fileFullName);
    } catch(Throwable e){
        return null;
    }
}
private UriW getUri(DocumentFile documentDirectory, String fileFullName) throws Throwable{
    if(documentDirectory == null){
        return null;
    }
    DocumentFile d = documentDirectory.findFile(fileFullName);
    if(d == null){
        return null;
    }
    if(!d.canWrite()){
        throw new Throwable("file can't be written");
    }
    return new UriW(d, UriW.Type.STORAGE_TREE);
}
public UriW getUri(String directoryPath, String fileFullName) throws Throwable{
    return getUri(findDocumentDirectory(directoryPath), fileFullName);
}
public String toLink(){
    return toLink(document.getUri());
}

public static DebugString toDebugString(DocumentFile d){
    DebugString data = new DebugString();
    data.append("exist", d.exists());
    data.append("canRead", d.canRead());
    data.append("canWrite", d.canWrite());
    data.append("name", d.getName());
    data.append("size", d.length());
    data.append("type", UtilsFile.getMimeTypeForFullName(d.getName()));
    data.append("isFile", d.isFile());
    data.append("isDirectory", d.isDirectory());
    data.append("uri", d.getUri().toString());
    return data;
}
public DebugString toDebugString(){
    DebugString data = toDebugString(document);
    data.append("displayPath", getDisplayPath());
    return data;
}
static public void toDebugLog(DocumentFile d){
DebugLog.start().send(toDebugString(d)).end();
}

final public void toDebugLog(){
DebugLog.start().send(toDebugString()).end();
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

}
