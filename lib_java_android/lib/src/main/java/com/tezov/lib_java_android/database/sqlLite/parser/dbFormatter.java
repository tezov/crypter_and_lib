/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.database.sqlLite.parser;

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugException;
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

import com.tezov.lib_java.async.notifier.task.TaskValue;
import com.tezov.lib_java_android.database.ItemBase;
import com.tezov.lib_java_android.database.sqlLite.dbTable;
import com.tezov.lib_java_android.database.sqlLite.dbView;
import com.tezov.lib_java.file.Directory;
import com.tezov.lib_java.file.File;
import com.tezov.lib_java.generator.uid.defUid;
import com.tezov.lib_java.parser.ParserAdapter;
import com.tezov.lib_java.toolbox.Nullify;
import com.tezov.lib_java.debug.DebugTrack;

import java.util.ArrayList;
import java.util.List;

public class dbFormatter<T extends ItemBase<T>>{
private final ParserAdapter<T> adapter;
private final dbTable<T>.Ref db;

public dbFormatter(ParserAdapter<T> adapter, dbTable<T>.Ref db){
DebugTrack.start().create(this).end();
    this.db = db;
    this.adapter = adapter;
}

public TaskValue<File>.Observable toFile(Directory directory, String name, boolean override){
    File file = new File(directory).setName(name).setExtension(adapter.fileExtension());
    return toFile(file, override);
}
public TaskValue<File>.Observable toFile(File file, boolean override){
    TaskValue<File> task = new TaskValue<>();
    if(file.exists() && !override){
        task.notifyException(file, "file already exist " + db.getName());
        return task.getObservable();
    }
    if(db.size() <= 0){
        task.notifyException(file, "db " + db.getName() + " is empty");
        return task.getObservable();
    }
    try{
        dbView<T> view = new dbView<>(db);
        adapter.openWriter(file).startWriterDocument().startWriterSheet();
        for(T item: view){
            adapter.write(item);
        }
        adapter.endWriterSheet().endWriterDocument().closeWriter();
        task.notifyComplete(file);
    } catch(Throwable e){
        task.notifyException(null, e);
    }
    return task.getObservable();
}

public TaskValue<List<defUid>>.Observable fromFile(File file, boolean override){
    TaskValue<List<defUid>> task = new TaskValue<>();
    try{
        List<defUid> uids = new ArrayList<>();
        adapter.openReader(file).startReaderDocument().startWriterSheet();
        while(adapter.isNotEndArray()){
            T newItem = adapter.read();
            T currentItem = db.get(newItem.getUid());
            if(currentItem == null){
                if(db.insert(newItem) != null){
                    uids.add(newItem.getUid());
                }
            } else if(override){
                if(db.update(newItem)){
                    uids.add(newItem.getUid());
                }
            }
        }
        adapter.endReaderSheet().endReaderSheet().closeReader();
        task.notifyComplete(Nullify.collection(uids));
    } catch(Throwable e){
        task.notifyException(null, e);
    }
    return task.getObservable();
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

}
