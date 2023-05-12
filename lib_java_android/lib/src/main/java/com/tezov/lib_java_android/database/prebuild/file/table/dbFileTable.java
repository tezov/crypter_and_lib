/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.database.prebuild.file.table;

import com.tezov.lib_java.debug.DebugLog;
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

import com.tezov.lib_java_android.application.AppConfigKey;
import com.tezov.lib_java_android.database.prebuild.file.item.ItemFile;
import com.tezov.lib_java_android.database.sqlLite.dbTable;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilter;
import com.tezov.lib_java_android.definition.defCreatable;
import com.tezov.lib_java_android.application.AppConfig;
import com.tezov.lib_java.file.Directory;
import com.tezov.lib_java.file.File;
import com.tezov.lib_java.generator.uid.UUIDGenerator;
import com.tezov.lib_java.generator.uid.defUid;
import com.tezov.lib_java.debug.DebugException;

import java.util.List;

import static com.tezov.lib_java_android.database.prebuild.file.table.DescriptionFile.Field.DIRECTORY;
import static com.tezov.lib_java_android.database.prebuild.file.table.DescriptionFile.Field.EXTENSION;
import static com.tezov.lib_java_android.database.prebuild.file.table.DescriptionFile.Field.NAME;

public class dbFileTable extends dbTable<ItemFile>{
private final static Directory DB_FILE_DIRECTORY_TRASH = AppConfig.getDirectory(AppConfigKey.DB_FILE_DIRECTORY_TRASH.getId());

@Override
public defCreatable<ItemFile> factory(){
    return ItemFile.getFactory();
}

@Override
public UUIDGenerator getUidGenerator(){
    return ItemFile.getUidGenerator();
}

@Override
protected dbTable<ItemFile>.Ref createRef(){
    return new Ref();
}

public class Ref extends dbTable<ItemFile>.Ref{
    protected Ref(){
        super();
    }

    public defUid getUID(File file){
        ItemFile item = get(file);
        if(item == null){
            return null;
        } else {
            return item.getUid();
        }
    }

    public ItemFile get(File file){
        synchronized(this){
            Directory directory = file.getDirectory();
            if(directory == null){
                return null;
            }
            return where(DIRECTORY, directory.toBytes(), false).where(NAME, file.getName(), false).where(EXTENSION, file.getExtension(), false).get();
        }
    }

    @Override
    public void clear(){
        List<ItemFile> items = group(DIRECTORY, true, false).select();
        if(items != null){
            for(ItemFile item: items){
                if(item.file != null){
                    item.file.getDirectory().deleteFiles(false);
                }
            }
        }
        DB_FILE_DIRECTORY_TRASH.delete();
        super.clear();
    }

    @Override
    public List<ItemFile> remove(dbFilter filter){
        List<ItemFile> items = super.remove(filter);
        if(items != null){
            for(ItemFile item: items){
                if(!item.isDeleted()){
                    item.file.delete();
                } else {
                    Directory trashDirectory = new Directory(DB_FILE_DIRECTORY_TRASH, item.file.getDirectory());
                    File trashFile = new File(trashDirectory, item.file.getFullName());
                    trashFile.delete();
                }
            }
        }
        return items;
    }

    @Override
    public ItemFile remove(ItemFile item){
        return remove(item.getUid());
    }

    private void moveFileToTrash(File file){
        if(file.exists()){
            Directory trashDirectory = new Directory(DB_FILE_DIRECTORY_TRASH, file.getDirectory());
            boolean failed = file.moveTo(trashDirectory, true) == null;

            if(failed){
DebugException.start().log("failed to move file to trash:" + file.toLinkString()).end();
            }

        } else {
DebugException.start().log("file doesn't exist:" + file.toLinkString()).end();
        }

    }

    @Override
    public List<ItemFile> putToTrash(dbFilter filter){
        List<ItemFile> items = super.putToTrash(filter);
        if(items != null){
            for(ItemFile item: items){
                moveFileToTrash(item.file);
            }
        }
        return items;
    }

    @Override
    public ItemFile putToTrash(ItemFile item){
        return putToTrash(item.getUid());
    }

    private void restoreFileFromTrash(File file){
        Directory trashDirectory = new Directory(DB_FILE_DIRECTORY_TRASH, file.getDirectory());
        File trashFile = new File(trashDirectory, file.getFullName());
        if(trashFile.exists()){
            boolean failed = trashFile.moveTo(file.getDirectory(), true) == null;

            if(failed){
DebugException.start().log("failed to move file from trash:" + file.toLinkString()).end();
            }

        } else {
DebugException.start().log("trash file doesn't exist, item:" + file.toLinkString()).end();
        }

    }

    @Override
    public List<ItemFile> restoreFromTrash(dbFilter filter){
        List<ItemFile> items = super.restoreFromTrash(filter);
        if(items != null){
            for(ItemFile item: items){
                restoreFileFromTrash(item.file);
            }
        }
        return items;
    }

    @Override
    public ItemFile restoreFromTrash(ItemFile item){
        return restoreFromTrash(item.getUid());
    }

    @Override
    public ItemFile restoreFromTrash(int index, ItemFile item){
        return restoreFromTrash(item.getUid());
    }

}

}
