/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.database.firebase.holder;

import com.tezov.lib_java.debug.DebugLog;
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

import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.async.notifier.observer.state.ObserverStateE;
import com.tezov.lib_java.async.notifier.observer.value.ObserverValueE;
import com.tezov.lib_java.async.notifier.task.TaskState;
import com.tezov.lib_java.cipher.definition.defEncoder;
import com.tezov.lib_java.cipher.holder.CipherHolderCrypto;
import com.tezov.lib_java_android.database.TableDescription;
import com.tezov.lib_java_android.database.adapter.definition.defContentValuesTo;
import com.tezov.lib_java_android.database.adapter.definition.defParcelTo;
import com.tezov.lib_java_android.database.firebase.adapter.AdapterHolderRemote;
import com.tezov.lib_java_android.database.firebase.adapter.defDataSnapshotTo;
import com.tezov.lib_java_android.database.firebase.fbTable;
import com.tezov.lib_java_android.database.prebuild.file.table.fbFileTable;
import com.tezov.lib_java.toolbox.Reflection;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.collection.ListKey;
import com.tezov.lib_java.type.collection.ListOrObject;
import com.tezov.lib_java.type.runnable.RunnableGroup;
import com.tezov.lib_java.wrapperAnonymous.FunctionW;

import java.util.List;

import static com.tezov.lib_java_android.database.prebuild.Descriptions.FILE;

public abstract class fbTableHolder{
private final ListKey<String, TableDescription> tableDescriptions;
private fbTablesHandle fbHandle = null;
private boolean isOpened = false;

public fbTableHolder(){
DebugTrack.start().create(this).end();
    this.tableDescriptions = new ListKey<String, TableDescription>(new FunctionW<TableDescription, String>(){
        @Override
        public String apply(TableDescription description){
            return description.name();
        }
    });
}
public fbTablesHandle handle(){
    return fbHandle;
}

public List<TableDescription> getTableDescriptions(){
    return tableDescriptions;
}
public TableDescription getTableDescription(String name){
    return tableDescriptions.getValue(name);
}
public <H extends fbTableHolder> H addTableDescriptions(TableDescription description){
    tableDescriptions.add(description);
    return (H)this;
}
public <H extends fbTableHolder> H addTableDescriptions(List<TableDescription> descriptions){
    tableDescriptions.addAll(descriptions);
    return (H)this;
}

public abstract fbContext getContext();

public abstract fbTablesOpener getTablesOpener();

public abstract String getRootName();

protected abstract CipherHolderCrypto getCipherHolder();

protected abstract AdapterHolderRemote getAdapterHolder();

public boolean isOpen(){
    return isOpened;
}

protected TaskState.Observable beforeOpen(){
    return TaskState.Complete();
}
protected TaskState.Observable afterOpen(){
    return TaskState.Complete();
}
public TaskState.Observable open(){
    TaskState task = new TaskState();
    RunnableGroup gr = new RunnableGroup(this).name("open");
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            beforeOpen().observe(new ObserverStateE(this){
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
    }.name("beforeOpen"));
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            openTables().observe(new ObserverStateE(this){
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
    }.name("openTables"));
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            afterOpen().observe(new ObserverStateE(this){
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
    }.name("afterOpen"));
    gr.setOnDone(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            isOpened = true;
            notify(task);
        }
    });
    gr.start();
    return task.getObservable();
}
protected TaskState.Observable openTables(){
    ListKey<String, fbTable> fbTables = new ListKey<String, fbTable>(ListOrObject::new, new FunctionW<fbTable, String>(){
        @Override
        public String apply(fbTable table){
            return table.getName();
        }
    });
    AdapterHolderRemote adapters = getAdapterHolder();
    defParcelTo parcelTo = adapters.getParcelTo();
    defDataSnapshotTo dataSnapshotTo = adapters.getDataSnapshotTo();
    defContentValuesTo contentValuesTo = adapters.getContentValuesTo();
    CipherHolderCrypto cipherHolder = getCipherHolder();
    defEncoder encoderKey = cipherHolder != null ? cipherHolder.getEncoderKey() : null;
    for(TableDescription t: tableDescriptions){
        fbTable table = Reflection.newInstance(t.getRemoteTableType()).setTableDescription(t, encoderKey).setParcelTo(parcelTo).setDataSnapshotTo(dataSnapshotTo).setContentValuesTo(contentValuesTo);
        fbTables.add(table);
    }
    fbHandle = new fbTablesHandle();
    fbTablesOpener opener = getTablesOpener();
    opener.setTables(fbTables);
    TaskState.Observable observable = fbHandle.open(opener);
    observable.observe(new ObserverStateE(this){
        @Override
        public void onComplete(){
            if(tableDescriptions.contains(FILE)){
                fbFileTable fbFileTable = (fbFileTable)fbTables.getValue(FILE.name());
                fbFileTable.initStorage(getContext());
                if(cipherHolder != null){
                    fbFileTable.getFbStorage().setEncoderKey(cipherHolder.getEncoderKey()).setEncoderFile(cipherHolder.getEncoderValue()).setDecoderFile(cipherHolder.getDecoderValue());
                }
            }
        }
        @Override
        public void onException(Throwable e){
            fbHandle = null;
        }
    });
    return observable;
}

protected TaskState.Observable beforeClose(){
    return TaskState.Complete();
}
protected TaskState.Observable afterClose(){
    return TaskState.Complete();
}
public TaskState.Observable close(){
    TaskState task = new TaskState();
    isOpened = false;
    RunnableGroup gr = new RunnableGroup(this).name("close");
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            beforeClose().observe(new ObserverStateE(this){
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
    }.name("beforeClose"));
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            closeTables().observe(new ObserverStateE(this){
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
    }.name("closeTables"));
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            afterClose().observe(new ObserverStateE(this){
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
    }.name("afterClosed"));
    gr.notifyOnDone(task);
    gr.start();
    return task.getObservable();
}
protected TaskState.Observable closeTables(){
    TaskState task = new TaskState();
    if(fbHandle == null){
        task.notifyComplete();
    } else {
        RunnableGroup gr = new RunnableGroup(this).name("closeTables");
        for(TableDescription t: tableDescriptions){
            gr.add(new RunnableGroup.Action(){
                @Override
                public void runSafe(){
                    java.lang.Throwable e = getException();
                    fbHandle.getTable(t).pause(e != null).observe(new ObserverStateE(this){
                        @Override
                        public void onComplete(){
                            next();
                        }
                        @Override
                        public void onException(java.lang.Throwable e){
                            if(getException() == null){
                                putException(e);
                            }
                            next();
                        }
                    });
                }
            }.name("pause_table_" + t.getRemoteTableType().getSimpleName()));
        }
        gr.add(new RunnableGroup.Action(){
            @Override
            public void runSafe(){
                fbHandle.close().observe(new ObserverStateE(this){
                    @Override
                    public void onComplete(){
                        fbHandle = null;
                        next();
                    }
                    @Override
                    public void onException(Throwable e){
                        fbHandle = null;
                        putException(e);
                        next();
                    }
                });
            }
        }.name("close handle"));
        gr.notifyOnDone(task);
        gr.start();
    }
    return task.getObservable();
}

public TaskState.Observable clear(){
    TaskState task = new TaskState();
    RunnableGroup gr = new RunnableGroup(this).name("clear");
    for(TableDescription t: tableDescriptions){
        gr.add(new RunnableGroup.Action(){
            @Override
            public void runSafe(){
                fbHandle.getMainRef(t).clear().observe(new ObserverValueE<Void>(this){
                    @Override
                    public void onComplete(Void object){
                        next();
                    }

                    @Override
                    public void onException(Void object, java.lang.Throwable e){
DebugException.start().log(e).end();
                        next();
                    }
                });
            }
        }.name("clear_" + t.name()));
    }
    gr.notifyOnDone(task);
    gr.start();
    return task.getObservable();
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

}
