/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.provider.contentProvider;

import com.tezov.lib_java.debug.DebugException;
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

import com.tezov.lib_java_android.file.UtilsFile;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.type.primitive.BytesTo;

import com.tezov.lib_java.type.primitive.string.StringHexTo;

import android.database.Cursor;
import android.util.Log;

import com.tezov.lib_java_android.application.AppContext;
import com.tezov.lib_java.async.notifier.task.TaskValue;
import com.tezov.lib_java.buffer.ByteBuffer;
import com.tezov.lib_java.file.Directory;
import com.tezov.lib_java.file.File;
import com.tezov.lib_java.file.StoragePackage;
import com.tezov.lib_java_android.file.UriW;
import com.tezov.lib_java.toolbox.Nullify;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.util.UtilsString;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentResolver.SCHEME_CONTENT;

public abstract class FileContentProviderClient{
public static final String AUTHORITY = ".authority.file.content.provider";
public static final String PERMISSION_READ = ".permission.FILE_CONTENT_PROVIDER.READ";
public static final String PERMISSION_WRITE = ".permission.FILE_CONTENT_PROVIDER.WRITE";
public static final int URI_ID_LENGTH = 8;
public static final String COL_LINK = "link";
public static final int COL_LINK_INDEX = 0;
private static final int VERSION = 1;

private static String packageName = null;

public static String getAuthority(){
    return getPackageName() + FileContentProviderClient.AUTHORITY;
}
private static String getPackageName(){
    if(packageName == null){
        packageName = AppContext.getPackageName();
    }
    return packageName;
}
public static void setPackageName(int stringResourceId){
    FileContentProviderClient.packageName = AppContext.getResources().getString(stringResourceId);
}
public static void setPackageName(String packageName){
    FileContentProviderClient.packageName = packageName;
}
public static android.net.Uri getAuthorityUri(){
    return new android.net.Uri.Builder().authority(getAuthority()).scheme(SCHEME_CONTENT).build();
}

private static String newId(){
    return UtilsString.randomHex(URI_ID_LENGTH);
}
public static android.net.Uri buildUri(ItemUri item){
    return new android.net.Uri.Builder().authority(getAuthority()).scheme(SCHEME_CONTENT).appendEncodedPath(item.toStringHex()).build();
}

public static android.net.Uri buildQueryLinksUri(String directoryLink, String patternPath, String patternFileName, boolean recursive){
    return buildQueryLinksUri(directoryLink, patternPath, patternFileName, recursive, false);
}
public static android.net.Uri buildQueryLinksUri(String directoryLink, String patternPath, String patternFileName, boolean recursive, boolean asyncQuery){
    ItemUriQuery item = new ItemUriQuery(VERSION).setDirectoryLink(directoryLink).setPatternPath(patternPath).setPatternFileName(patternFileName).setRecursive(recursive).setAsyncQuery(asyncQuery);
    return buildUri(item);
}
public static List<String> links(String directoryLink, String patternPath, String patternFileName, boolean recursive){
    android.net.Uri uri = FileContentProviderClient.buildQueryLinksUri(directoryLink, patternPath, patternFileName, recursive);
    return links(uri);
}
public static List<String> links(android.net.Uri uri){
    Cursor cursor = AppContext.getContentResolver().query(uri, null, null, null, null);
    if(cursor == null){
        return null;
    } else {
        List<String> links = new ArrayList<>();
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            links.add(cursor.getString(COL_LINK_INDEX));
            cursor.moveToNext();
        }
        cursor.close();
        return Nullify.collection(links);
    }
}
public static TaskValue<List<String>>.Observable requestLinks(String directoryLink, String patternPath, String patternFileName, boolean recursive){
    return new RequestLinksObserver().request(directoryLink, patternPath, patternFileName, recursive);
}
public static LinksBuilder links(){
    return new LinksBuilder();
}

public static UriW buildUriFile(String fileLink){
    ItemUriFile item = new ItemUriFile(VERSION).setLink(fileLink);
    return new UriW(new android.net.Uri.Builder().authority(getAuthority()).scheme(SCHEME_CONTENT).appendEncodedPath(item.toStringHex()).build(), UriW.Type.FILE_PROVIDER);
}
public static void transferFile(String fileLinkSource, File fileDestination) throws IOException{
    UtilsFile.transfer(buildUriFile(fileLinkSource), fileDestination);
}

public static class ItemUri{
    private Integer version;
    private String id;
    private boolean asyncQuery = false;
    public ItemUri(){
        this(null, newId());
    }
    public ItemUri(Integer version){
        this(version, newId());
    }
    public ItemUri(Integer version, String id){
        this.version = version;
        this.id = id;
    }
    public String getId(){
        return id;
    }
    public Integer getVersion(){
        return version;
    }
    public boolean isAsyncQuery(){
        return asyncQuery;
    }
    public <I extends ItemUri> I setAsyncQuery(boolean asyncQuery){
        this.asyncQuery = asyncQuery;
        return (I)this;
    }
    protected int length(){
        return ByteBuffer.INT_SIZE() + ByteBuffer.STRING_SIZE(id) + ByteBuffer.BOOLEAN_SIZE();
    }
    protected ByteBuffer toByteBuffer(){
        ByteBuffer buffer = ByteBuffer.obtain(length());
        buffer.put(version);
        buffer.put(id);
        buffer.put(asyncQuery);
        return buffer;
    }
    final public String toStringHex(){
        return BytesTo.StringHex(toByteBuffer().array());
    }
    protected void fromByteBuffer(ByteBuffer buffer){
        version = buffer.getInt();
        id = buffer.getString();
        asyncQuery = buffer.getBoolean();

    }
    final public <I extends ItemUri> I fromStringHex(String hex){
        fromByteBuffer(ByteBuffer.wrap(StringHexTo.Bytes(hex)));
        return (I)this;
    }

    public DebugString toDebugString(){
        DebugString data = new DebugString();
        data.append("version", version);
        data.append("id", id);
        data.append("asyncQuery", asyncQuery);
        return data;
    }
    final public void toDebugLog(){
DebugLog.start().send(toDebugString()).end();
    }

}

public static class ItemUriQuery extends ItemUri{
    private String directoryLink = null;
    private String patternPath = null;
    private String patternFileName = null;
    private boolean recursive = false;
    public ItemUriQuery(){
    }
    public ItemUriQuery(int version){
        super(version);
    }
    public ItemUriQuery(int version, String id){
        super(version, id);
    }
    public String getDirectoryLink(){
        return directoryLink;
    }
    public <I extends FileContentProviderClient.ItemUriQuery> I setDirectoryLink(String directoryLink){
        this.directoryLink = directoryLink;
        return (I)this;
    }
    public String getPatternPath(){
        return patternPath;
    }
    public <I extends FileContentProviderClient.ItemUriQuery> I setPatternPath(String patternPath){
        this.patternPath = patternPath;
        return (I)this;
    }
    public String getPatternFileName(){
        return patternFileName;
    }
    public <I extends FileContentProviderClient.ItemUriQuery> I setPatternFileName(String patternFileName){
        this.patternFileName = patternFileName;
        return (I)this;
    }
    public boolean isRecursive(){
        return recursive;
    }
    public <I extends FileContentProviderClient.ItemUriQuery> I setRecursive(boolean recursive){
        this.recursive = recursive;
        return (I)this;
    }
    @Override
    protected int length(){
        return super.length() + ByteBuffer.STRING_SIZE(directoryLink) + ByteBuffer.STRING_SIZE(patternPath) + ByteBuffer.STRING_SIZE(patternFileName) + ByteBuffer.BOOLEAN_SIZE(1);
    }
    @Override
    protected ByteBuffer toByteBuffer(){
        ByteBuffer buffer = super.toByteBuffer();
        buffer.put(directoryLink);
        buffer.put(patternPath);
        buffer.put(patternFileName);
        buffer.put(recursive);
        return buffer;
    }
    @Override
    protected void fromByteBuffer(ByteBuffer buffer){
        super.fromByteBuffer(buffer);
        directoryLink = buffer.getString();
        patternPath = buffer.getString();
        patternFileName = buffer.getString();
        recursive = buffer.getBoolean();
    }

    @Override
    public DebugString toDebugString(){
        DebugString data = super.toDebugString();
        data.append("directoryLink", directoryLink);
        data.append("patternPath", patternPath);
        data.append("patternFileName", patternFileName);
        data.append("recursive", recursive);
        return data;
    }

}

public static class ItemUriFile extends ItemUri{
    private String link = null;
    public ItemUriFile(){
    }
    public ItemUriFile(int version){
        super(version);
    }
    public ItemUriFile(int version, String id){
        super(version, id);
    }
    public String getLink(){
        return link;
    }
    public <I extends ItemUriFile> I setLink(String link){
        this.link = link;
        return (I)this;
    }
    @Override
    protected int length(){
        return super.length() + ByteBuffer.STRING_SIZE(link);
    }
    @Override
    protected ByteBuffer toByteBuffer(){
        ByteBuffer buffer = super.toByteBuffer();
        buffer.put(link);
        return buffer;
    }
    @Override
    protected void fromByteBuffer(ByteBuffer buffer){
        super.fromByteBuffer(buffer);
        link = buffer.getString();
    }

    @Override
    public DebugString toDebugString(){
        DebugString data = super.toDebugString();
        data.append("link", link);
        return data;
    }

}

public static class LinksBuilder{
    private String directoryLink = null;
    private String patternPath = null;
    private String patternFileName = null;
    private boolean recursive = false;

    public LinksBuilder(){
DebugTrack.start().create(this).end();
    }
    public LinksBuilder setDirectory(Directory directory){
        this.directoryLink = directory.toLinkString();
        return this;
    }
    public LinksBuilder setDirectory(String directoryLink){
        this.directoryLink = directoryLink;
        return this;
    }
    public LinksBuilder setDirectory(StoragePackage.Type storage){
        this.directoryLink = new Directory(storage).toLinkString();
        return this;
    }
    public LinksBuilder setDirectory(StoragePackage.Type storage, String path){
        this.directoryLink = new Directory(storage, path).toLinkString();
        return this;
    }
    public LinksBuilder setDirectory(StoragePackage.Type storage, StoragePackage.Path.Is path){
        this.directoryLink = new Directory(storage, path).toLinkString();
        return this;
    }

    public LinksBuilder setPatternPath(String patternPath){
        this.patternPath = patternPath;
        return this;
    }
    public LinksBuilder setPatternFileName(String patternFileName){
        this.patternFileName = patternFileName;
        return this;
    }
    public LinksBuilder setRecursive(boolean recursive){
        this.recursive = recursive;
        return this;
    }

    public List<String> links(){
        return FileContentProviderClient.links(directoryLink, patternPath, patternFileName, recursive);
    }
    public TaskValue<List<String>>.Observable requestLinks(){
        return FileContentProviderClient.requestLinks(directoryLink, patternPath, patternFileName, recursive);
    }

    @Override
    protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    }

}

protected static abstract class ContentObserver<R> extends android.database.ContentObserver{
    private android.net.Uri uri = null;
    private TaskValue<R> task = null;
    protected ContentObserver(){
        this(null);
    }
    protected ContentObserver(android.os.Handler handler){
        super(handler);
    }
    @Override
    public void onChange(boolean selfChange){
        AppContext.getContentResolver().unregisterContentObserver(this);
        R r = getData();
        if(r != null){
            task.notifyComplete(r);
        } else {
            task.notifyException(r, new Throwable("retrofit.data is null"));
        }
    }

    protected android.net.Uri getUri(){
        return uri;
    }
    protected void setUri(android.net.Uri uri){
        this.uri = uri;
    }
    protected abstract R getData();

    protected abstract void requestData();

    @Override
    public void onChange(boolean selfChange, android.net.Uri uri){
        onChange(selfChange);
    }
    protected TaskValue<R> newTask(){
        return new TaskValue<>();
    }
    public TaskValue<R>.Observable request(){
        task = newTask();
        AppContext.getContentResolver().registerContentObserver(getUri(), false, this);
        requestData();
        return task.getObservable();
    }

}

private static class RequestLinksObserver extends ContentObserver<List<String>>{
    RequestLinksObserver(){
        this(null);
    }
    RequestLinksObserver(android.os.Handler handler){
        super(handler);
    }
    @Override
    protected List<String> getData(){
        return FileContentProviderClient.links(getUri());
    }
    @Override
    protected void requestData(){
        if(links(getUri()) != null){
            Log.e(">>:", "Should not be possible !!!!!");
        }
    }
    public TaskValue<List<String>>.Observable request(String directoryLink, String patternPath, String patternFileName, boolean recursive){
        setUri(FileContentProviderClient.buildQueryLinksUri(directoryLink, patternPath, patternFileName, recursive, true));
        return request();
    }

}

}
