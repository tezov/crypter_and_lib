/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.crypter.export_import_keys;

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
import static com.tezov.crypter.application.Environment.CachePath.KEYS_BACKUP;
import static com.tezov.crypter.data.table.Descriptions.KEY;
import static com.tezov.crypter.data.table.Descriptions.KEY_RING;

import com.tezov.crypter.application.AppInfo;
import com.tezov.crypter.application.Application;
import com.tezov.crypter.application.Environment;
import com.tezov.crypter.data.item.ItemKey;
import com.tezov.crypter.data.item.ItemKeyRing;
import com.tezov.crypter.data.table.context.dbDataUsersContext;
import com.tezov.lib_java.async.notifier.task.TaskState;
import com.tezov.lib_java.async.notifier.task.TaskValue;
import com.tezov.lib_java.cipher.dataAdapter.string.DataStringAdapter;
import com.tezov.lib_java.cipher.misc.PasswordCipher;
import com.tezov.lib_java_android.database.ItemBase;
import com.tezov.lib_java_android.database.sqlLite.dbTable;
import com.tezov.lib_java.file.Directory;
import com.tezov.lib_java_android.file.StorageFile;
import com.tezov.lib_java_android.file.UriW;
import com.tezov.lib_java_android.file.UtilsFile;
import com.tezov.lib_java.generator.uid.UidBase;
import com.tezov.lib_java.parser.ParserAdapter;
import com.tezov.lib_java.parser.defParserReader;
import com.tezov.lib_java.parser.defParserWriter;
import com.tezov.lib_java.wrapperAnonymous.ConsumerThrowW;
import com.tezov.lib_java.type.primitive.BytesTo;
import com.tezov.lib_java.type.primitive.string.StringBase58To;

import java.io.IOException;

public class dbKeyFormatter{
public final static DataStringAdapter.Format CRYPT_FORMAT = DataStringAdapter.Format.BASE58;
public final static int FOR_EACH_READ_BUFFER_LENGTH = 20;
public final static String HEADER_VERSION = "_VERSION";
public final static String HEADER_GUID = "_GUID";
public final static String HEADER_OPTION = "_OPTION";
private final static String ADAPTER_FILE_NAME = "KEYS";
private ParserAdapter<Object> fileAdapter = null;
private AdapterParserRaw<ItemKeyRing> keyRingAdapter = null;
private AdapterParserRaw<ItemKey> keyAdapter = null;
private Integer versionTable = null;
private Integer versionFile = null;

public dbKeyFormatter(){
}
private static Class<dbKeyFormatter> myClass(){
    return dbKeyFormatter.class;
}

public static Directory newCacheDirectory(Direction direction){
    return Environment.obtainUniqueDirectoryCache(KEYS_BACKUP, direction.name());
}
private ParserAdapter<Object> newParserAdapter(String name){
    return new com.tezov.lib_java.parser.raw.ParserAdapterRaw<>(name);
}
public String getExtension(){
    return Environment.EXTENSION_KEYS_BACKUP;
}
private void closeAndDestroyAdapter(){
    if(keyAdapter != null){
        keyAdapter.closeNothrow();
        keyAdapter = null;
    }
    if(keyRingAdapter != null){
        keyRingAdapter.closeNothrow();
        keyRingAdapter = null;
    }
    if(fileAdapter != null){
        fileAdapter.closeNothrow();
        fileAdapter = null;
    }
}
private void destroyAdapter(){
    fileAdapter = null;
    keyAdapter = null;
    keyRingAdapter = null;
}

//EXPORT
public TaskValue<com.tezov.lib_java.file.File>.Observable toFile(PasswordCipher password, String fileName, boolean override){
    return toFile(password, StorageFile.obtainUniqueFile(Environment.obtainDirectoryCache(KEYS_BACKUP), fileName), override);
}
public TaskValue<com.tezov.lib_java.file.File>.Observable toFile(PasswordCipher password, com.tezov.lib_java.file.File file, boolean override){
    TaskValue<com.tezov.lib_java.file.File> task = new TaskValue<>();
    if(file.exists() && !override){
        task.notifyException(file, "file already exist");
        return task.getObservable();
    }
    Directory cacheDirectory = newCacheDirectory(Direction.EXPORT);
    try{
        versionTable = dbDataUsersContext.getInstance().getVersion();
        AdapterOptionWrite option = new AdapterOptionWrite(password);
        option.setVersionTable(versionTable);
        option.setUid(AppInfo.getDuidOrGuid()).build();
        // CREATE FILES ITEM CACHE
        Application.lockerTables().lock(myClass());
        keyRingAdapter = adapterItemWrite(Application.tableHolderCipher().handle().getMainRef(KEY_RING), new AdapterKeyRing(), option, cacheDirectory);
        keyAdapter = adapterItemWrite(Application.tableHolder().handle().getMainRef(KEY), new AdapterKey(), option, cacheDirectory);
        Application.lockerTables().unlock(myClass());
        // CREATE FILE OUTPUT

        fileAdapter = newParserAdapter(ADAPTER_FILE_NAME);
        fileAdapter.openWriter(file).startWriterDocument();
        defParserWriter writer = fileAdapter.writerHelper().getWriter();
        writer.write(HEADER_VERSION, String.valueOf(versionTable));
        writer.write(HEADER_OPTION, BytesTo.StringBase58(option.toBytes()));
        writer.write(HEADER_GUID, option.getEncoder().encode(option.getUid().toBytes(), byte[].class));
        // MERGE FILES ITEM TO FILE OUTPUT
        fileAdapter.writeFrom(keyRingAdapter, newParserAdapter(null));
        fileAdapter.writeFrom(keyAdapter, newParserAdapter(null));
        fileAdapter.endWriterDocument().closeWriter();
        // CLEAN UP
        destroyAdapter();
        cacheDirectory.delete();
        task.notifyComplete(file);
    } catch(Throwable e){
        Application.lockerTables().unlock(myClass());
        closeAndDestroyAdapter();
        cacheDirectory.delete();
        task.notifyException(null, e);
    }
    return task.getObservable();
}
private <T extends ItemBase<T>> AdapterParserRaw<T> adapterItemWrite(dbTable<T>.Ref db, AdapterParserRaw<T> adapter, AdapterOptionWrite option, Directory directory) throws Throwable{
    adapter.setOptionWrite(option);
    adapter.openWriter(directory).startWriterDocument().startWriterSheet();
    adapter.writeHeader();
    db.forEach(new ConsumerThrowW<T>(){
        @Override
        public void accept(T t) throws IOException{
            adapter.write(t);
        }
    }, FOR_EACH_READ_BUFFER_LENGTH);
    adapter.endWriterSheet().endWriterDocument().closeWriter();
    return adapter;
}

//IMPORT
public TaskState.Observable fromUri(PasswordCipher password, UriW uri, boolean override){
    return fromUri(password, uri, override, newCacheDirectory(Direction.IMPORT));
}
public TaskState.Observable fromUri(PasswordCipher password, UriW uri, boolean override, Directory cacheDirectory){
    try{
        com.tezov.lib_java.file.File file = new com.tezov.lib_java.file.File(cacheDirectory, ADAPTER_FILE_NAME);
        UtilsFile.transfer(uri, file);
        return fromFile(password, file, override, cacheDirectory);
    } catch(Throwable e){
        cacheDirectory.delete();
        TaskState task = new TaskState();
        task.notifyException(e);
        return task.getObservable();
    }
}
private TaskState.Observable fromFile(PasswordCipher password, com.tezov.lib_java.file.File file, boolean override){
    return fromFile(password, file, override, newCacheDirectory(Direction.IMPORT));
}
private TaskState.Observable fromFile(PasswordCipher password, com.tezov.lib_java.file.File file, boolean override, Directory cacheDirectory){
    TaskState task = new TaskState();
    try{
        // READ FILE IN
        fileAdapter = newParserAdapter(ADAPTER_FILE_NAME);
        fileAdapter.openReader(file).startReaderDocument();
        defParserReader reader = fileAdapter.readerHelper().getReader();
        if(HEADER_VERSION.equals(reader.nextName())){
            versionFile = Integer.valueOf(reader.nextString());
        }
        if(versionFile == null){
            throw new IOException("invalid file");
        }
        byte[] spec = null;
        if(HEADER_OPTION.equals(reader.nextName())){
            spec = StringBase58To.Bytes(reader.nextString());
        }
        if(spec == null){
            throw new IOException("invalid file");
        }
        AdapterOptionRead option = new AdapterOptionRead(password);
        option.setVersionTable(versionTable).setVersionFile(versionFile);
        option.rebuild(spec);
        byte[] uid = null;
        if(HEADER_GUID.equals(reader.nextName())){
            uid = option.getDecoder().decode(reader.nextString(), byte[].class);
        }
        if(uid == null){
            throw new IOException("invalid file");
        }
        option.setUid(UidBase.fromBytes(uid));
        // SPLIT FILE IN TO FILES ITEM
        keyRingAdapter = new AdapterKeyRing();
        fileAdapter.writeTo(keyRingAdapter, newParserAdapter(null), cacheDirectory);
        keyAdapter = new AdapterKey();
        fileAdapter.writeTo(keyAdapter, newParserAdapter(null), cacheDirectory);
        fileAdapter.endReaderDocument().closeReader();
        //READ FILE ITEM
        Application.lockerTables().lock(myClass());
        keyRingAdapterItemRead(option, override);
        keyAdapterItemRead(option, override);
        Application.lockerTables().unlock(myClass());
        destroyAdapter();
        cacheDirectory.delete();
        task.notifyComplete();

    } catch(Throwable e){
        Application.lockerTables().unlock(myClass());
        closeAndDestroyAdapter();
        cacheDirectory.delete();
        task.notifyException(e);
    }
    return task.getObservable();
}

private void keyRingAdapterItemRead(AdapterOptionRead option, boolean override) throws IOException{
    dbTable<ItemKeyRing>.Ref db = Application.tableHolderCipher().handle().getMainRef(KEY_RING);
    AdapterParserRaw<ItemKeyRing> adapter = keyRingAdapter;
    adapter.setOptionRead(option);
    adapter.openReader().startReaderDocument().startReaderSheet();
    adapter.readHeader();
    while(adapter.isNotEndArray()){
        ItemKeyRing readItem = adapter.read();
        if(readItem != null){
            ItemKeyRing currentItem = db.get(readItem.getUid());
            if(currentItem == null){
                db.insert(readItem);
            } else if(override){
                db.update(readItem);
            }
        }
    }
    adapter.endReaderSheet().endReaderDocument().closeReader();
}
private void keyAdapterItemRead(AdapterOptionRead option, boolean override) throws IOException{
    dbTable<ItemKeyRing>.Ref dbKeyRingTable = Application.tableHolderCipher().handle().getMainRef(KEY_RING);
    dbTable<ItemKey>.Ref dbKeyTable = Application.tableHolder().handle().getMainRef(KEY);
    AdapterParserRaw<ItemKey> adapter = keyAdapter;
    adapter.setOptionRead(option);
    adapter.openReader().startReaderDocument().startReaderSheet();
    adapter.readHeader();
    while(adapter.isNotEndArray()){
        ItemKey readItem = adapter.read();
        if((readItem == null) || (readItem.getUid() == null)){
            continue;
        }
        ItemKeyRing itemKeyRing = dbKeyRingTable.get(readItem.getKeyRingUid());
        if(itemKeyRing == null){
            continue;
        }
        ItemKey currentItem = dbKeyTable.get(readItem.getUid());
        if(currentItem == null){
            dbKeyTable.insert(readItem);
        } else if(override){
            dbKeyTable.update(readItem);
        }
    }
    adapter.endReaderSheet().endReaderDocument().closeReader();
}

public enum Direction{
    EXPORT, IMPORT
}

}
