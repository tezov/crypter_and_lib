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

import com.tezov.lib_java.async.notifier.observer.state.ObserverStateE;
import com.tezov.lib_java.async.notifier.observer.value.ObserverValueE;
import com.tezov.lib_java.async.notifier.task.TaskState;
import com.tezov.lib_java.async.notifier.task.TaskValue;
import com.tezov.lib_java_android.database.firebase.fbStorage;
import com.tezov.lib_java_android.database.firebase.fbTable;
import com.tezov.lib_java_android.database.firebase.holder.fbContext;
import com.tezov.lib_java_android.database.prebuild.file.item.ItemFile;
import com.tezov.lib_java_android.database.sqlLite.dbTableDefinition;
import com.tezov.lib_java_android.definition.defCreatable;
import com.tezov.lib_java.generator.uid.UUIDGenerator;
import com.tezov.lib_java.generator.uid.defUid;
import com.tezov.lib_java.toolbox.Nullify;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.type.runnable.RunnableGroup;

import java.util.ArrayList;
import java.util.List;

public class fbFileTable extends fbTable<ItemFile>{
private fbStorage sb = null;

@Override
protected fbFileTable me(){
    return (fbFileTable)super.me();
}

@Override
public defCreatable<ItemFile> factory(){
    return ItemFile.getFactory();
}

@Override
public UUIDGenerator getUidGenerator(){
    return ItemFile.getUidGenerator();
}

@Override
public fbTable<ItemFile> setTableDefinition(dbTableDefinition.Ref definition){
    super.setTableDefinition(definition);
    sb = new fbStorage();
    return this;
}

public void initStorage(fbContext context){
    sb.setFileTable(context, super.createRef());
}

public fbStorage getFbStorage(){
    return sb;
}

@Override
public fbTable<ItemFile> start(){
    synchronized(me()){
        super.start();
        sb.start();
        return this;
    }
}

private TaskState.Observable pauseSuper(boolean force){
    return super.pause(force);
}

@Override
public TaskState.Observable pause(boolean force){
    synchronized(me()){
        TaskState task = new TaskState();
        RunnableGroup gr = new RunnableGroup(this).name("pause");
        gr.add(new RunnableGroup.Action(){
            @Override
            public void runSafe(){
                sb.pause(force).observe(new ObserverStateE(this){
                    @Override
                    public void onComplete(){
                        next();
                    }
                    @Override
                    public void onException(java.lang.Throwable e){
                        putException(e);
                        next();
                    }
                });
            }
        });
        gr.add(new RunnableGroup.Action(){
            @Override
            public void runSafe(){
                java.lang.Throwable e = getException();
                pauseSuper(force || (e != null)).observe(new ObserverStateE(this){
                    @Override
                    public void onComplete(){
                        next();
                    }
                    @Override
                    public void onException(java.lang.Throwable e){
                        putException(e);
                        next();
                    }
                });
            }
        });
        gr.notifyOnDone(task);
        gr.start();
        return task.getObservable();
    }
}

@Override
protected fbTable<ItemFile>.Ref createRef(){
    return new Ref();
}

public class Ref extends fbTable<ItemFile>.Ref{
    protected Ref(){
    }

    public fbStorage getFbStorage(){
        return me().getFbStorage();
    }

    @Override
    public TaskValue<ItemFile>.Observable remove(defUid uid){
        return sb.remove(uid, false);
    }

    @Override
    public TaskValue<ItemFile>.Observable remove(int index){
        return sb.remove(index, false);
    }

    @Override
    public TaskValue<Void>.Observable clear(){
        return sb.clear();
    }

    @Override
    public TaskValue<ItemFile>.Observable insert(ItemFile itemFile){
        return sb.insert(itemFile);
    }

    @Override
    public TaskValue<ItemFile>.Observable update(ItemFile itemFile){
        return sb.update(itemFile);
    }

    @Override
    public TaskValue<ItemFile>.Observable get(defUid uid){
        return sb.get(uid);
    }

    @Override
    public TaskValue<ItemFile>.Observable get(int index){
        return sb.get(index);
    }

    @Override
    public TaskValue<List<ItemFile>>.Observable remove(List<ItemFile> items){
        List<defUid> uids = new ArrayList<>();
        for(ItemFile item: items){
            uids.add(item.getUid());
        }
        return removeUids(uids);
    }
    @Override
    public TaskValue<List<ItemFile>>.Observable removeUids(List<defUid> uids){
        TaskValue<List<ItemFile>> task = new TaskValue<>();
        RunnableGroup gr = new RunnableGroup(this).name("insert list file");
        gr.putValue(new ArrayList<>());
        for(defUid uid: uids){
            gr.add(new RunnableGroup.Action(){
                @Override
                public void runSafe(){
                    remove(uid).observe(new ObserverValueE<ItemFile>(this){
                        @Override
                        public void onComplete(ItemFile itemFile){
                            List<ItemFile> list = getValue();
                            list.add(itemFile);
                            next();
                        }
                        @Override
                        public void onException(ItemFile itemFile, Throwable e){
                            if(getException() == null){
                                putException(e);
                            }
                            next();
                        }
                    });
                }
            });
        }
        gr.setOnDone(new RunnableGroup.Action(){
            @Override
            public void runSafe(){
                List<ItemFile> list = getValue();
                putValue(Nullify.collection(list));
                notify(task);
            }
        });
        gr.start();
        return task.getObservable();
    }

    @Override
    public TaskValue<List<ItemFile>>.Observable insert(List<ItemFile> itemFiles){
        TaskValue<List<ItemFile>> task = new TaskValue<>();
        RunnableGroup gr = new RunnableGroup(this).name("insert list file");
        gr.putValue(new ArrayList<>());
        for(ItemFile itemFile: itemFiles){
            gr.add(new RunnableGroup.Action(){
                @Override
                public void runSafe(){
                    insert(itemFile).observe(new ObserverValueE<ItemFile>(this){
                        @Override
                        public void onComplete(ItemFile itemFile){
                            List<ItemFile> list = getValue();
                            list.add(itemFile);
                            next();
                        }
                        @Override
                        public void onException(ItemFile itemFile, Throwable e){
                            if(getException() == null){
                                putException(e);
                            }
                            next();
                        }
                    });
                }
            });
        }
        gr.setOnDone(new RunnableGroup.Action(){
            @Override
            public void runSafe(){
                List<ItemFile> list = getValue();
                putValue(Nullify.collection(list));
                notify(task);
            }
        });
        gr.start();
        return task.getObservable();
    }

    @Override
    public TaskValue<List<ItemFile>>.Observable update(List<ItemFile> itemFiles){
        TaskValue<List<ItemFile>> task = new TaskValue<>();
        RunnableGroup gr = new RunnableGroup(this).name("insert list file");
        gr.putValue(new ArrayList<>());
        for(ItemFile itemFile: itemFiles){
            gr.add(new RunnableGroup.Action(){
                @Override
                public void runSafe(){
                    update(itemFile).observe(new ObserverValueE<ItemFile>(this){
                        @Override
                        public void onComplete(ItemFile itemFile){
                            List<ItemFile> list = getValue();
                            list.add(itemFile);
                            next();
                        }
                        @Override
                        public void onException(ItemFile itemFile, Throwable e){
                            if(getException() == null){
                                putException(e);
                            }
                            next();
                        }
                    });
                }
            });
        }
        gr.setOnDone(new RunnableGroup.Action(){
            @Override
            public void runSafe(){
                List<ItemFile> list = getValue();
                putValue(Nullify.collection(list));
                notify(task);
            }
        });
        gr.start();
        return task.getObservable();
    }

    @Override
    public TaskValue<List<ItemFile>>.Observable select(int index, int length){

DebugException.start().notImplemented().end();

        return null;
    }

    @Override
    public TaskValue<List<ItemFile>>.Observable select(defUid uid, int length){

DebugException.start().notImplemented().end();

        return null;
    }

    @Override
    public TaskValue<List<ItemFile>>.Observable select(defUid uidStart, defUid uidEnd){

DebugException.start().notImplemented().end();

        return null;
    }

    @Override
    public TaskValue<List<ItemFile>>.Observable select(List<defUid> uids){

DebugException.start().notImplemented().end();

        return null;
    }

    @Override
    public TaskValue<List<ItemFile>>.Observable select(){

DebugException.start().notImplemented().end();

        return null;
    }

}

}
