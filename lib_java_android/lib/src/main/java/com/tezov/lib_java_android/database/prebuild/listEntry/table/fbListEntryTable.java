/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.database.prebuild.listEntry.table;

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugTrack;
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

import com.tezov.lib_java.async.notifier.observer.value.ObserverValueE;
import com.tezov.lib_java.async.notifier.task.TaskValue;
import com.tezov.lib_java_android.database.firebase.fbTable;
import com.tezov.lib_java_android.database.prebuild.listEntry.item.ItemEntry;
import com.tezov.lib_java_android.definition.defCreatable;
import com.tezov.lib_java.generator.uid.UUIDGenerator;
import com.tezov.lib_java.generator.uid.defUid;
import com.tezov.lib_java.toolbox.Nullify;
import com.tezov.lib_java.type.collection.ListEntry;
import com.tezov.lib_java.type.primaire.Entry;
import com.tezov.lib_java.type.runnable.RunnableGroup;

import java.util.ArrayList;
import java.util.List;

public class fbListEntryTable extends fbTable<ItemEntry>{
@Override
public defCreatable<ItemEntry> factory(){
    return ItemEntry.getFactory();
}

@Override
public UUIDGenerator getUidGenerator(){
    return ItemEntry.getUidGenerator();
}

@Override
protected fbTable<ItemEntry>.Ref createRef(){
    return new fbListEntryTable.Ref();
}

public class Ref extends fbTable<ItemEntry>.Ref{
    protected Ref(){
        super();
    }
    private Ref me(){
        return this;
    }

    public TaskValue<ItemEntry>.Observable get(String key){
        return get(ItemEntry.keyToUID(key));
    }
    public TaskValue<List<ItemEntry>>.Observable selectKeys(List<String> keys){
        List<defUid> uids = new ArrayList<>();
        for(String key: keys){
            uids.add(ItemEntry.keyToUID(key));
        }
        return select(uids);
    }

    public TaskValue<ItemEntry>.Observable put(String key, String value){
        TaskValue<ItemEntry> task = new TaskValue<>();
        RunnableGroup gr = new RunnableGroup(this).name("fbListEntryTable put(String, String)");
        int LBL_INSERT = gr.label();
        int LBL_UPDATE = gr.label();
        gr.add(new RunnableGroup.Action(){
            @Override
            public void runSafe(){
                me().get(key).observe(new ObserverValueE<ItemEntry>(this){
                    @Override
                    public void onComplete(ItemEntry item){
                        if(item == null){
                            skipUntilLabel(LBL_INSERT);
                        } else {
                            putValue(item);
                            skipUntilLabel(LBL_UPDATE);
                        }
                    }
                    @Override
                    public void onException(ItemEntry item, Throwable e){
                        putValue(item);
                        putException(e);
                        done();
                    }
                });
            }
        }.name("get item if exist"));
        gr.add(new RunnableGroup.Action(LBL_INSERT){
            @Override
            public void runSafe(){
                ItemEntry item = ItemEntry.obtain().clear().setKey(key).setValue(value);
                insert(item).observe(new ObserverValueE<ItemEntry>(this){
                    @Override
                    public void onComplete(ItemEntry item){
                        putValue(item);
                        done();
                    }
                    @Override
                    public void onException(ItemEntry item, Throwable e){
                        putValue(item);
                        putException(e);
                        done();
                    }
                });
            }
        }.name("insert item"));
        gr.add(new RunnableGroup.Action(LBL_UPDATE){
            @Override
            public void runSafe(){
                ItemEntry item = getValue();
                item.setValue(value);
                update(item).observe(new ObserverValueE<ItemEntry>(this){
                    @Override
                    public void onComplete(ItemEntry item){
                        putValue(item);
                        done();
                    }

                    @Override
                    public void onException(ItemEntry item, Throwable e){
                        putValue(item);
                        putException(e);
                        done();
                    }
                });
            }
        }.name("update item"));
        gr.notifyOnDone(task);
        gr.start();
        return task.getObservable();
    }
    public TaskValue<List<ItemEntry>>.Observable put(ListEntry<String, String> values){
        TaskValue<List<ItemEntry>> task = new TaskValue<>();
        RunnableGroup gr = new RunnableGroup(this).name("fbListEntryTable put(ListEntry<String, String>)");
        gr.putValue(new ArrayList<>());
        for(Entry<String, String> e: values){
            gr.add(new RunnableGroup.Action(){
                @Override
                public void runSafe(){
                    me().put(e.key, e.value).observe(new ObserverValueE<ItemEntry>(this){
                        @Override
                        public void onComplete(ItemEntry item){
                            List<ItemEntry> values = getValue();
                            values.add(item);
                            next();
                        }
                        @Override
                        public void onException(ItemEntry itemEntry, Throwable e){
                            putException(e);
                            done();
                        }
                    });
                }
            }.name("put " + e.key));
        }
        gr.add(new RunnableGroup.Action(){
            @Override
            public void runSafe(){
                List<ItemEntry> values = getValue();
                putValue(Nullify.collection(values));
                next();
            }
        }.name("nullify"));
        gr.notifyOnDone(task);
        gr.start();
        return task.getObservable();
    }

    public TaskValue<ItemEntry>.Observable remove(String key){
        return remove(ItemEntry.keyToUID(key));
    }

}

}
