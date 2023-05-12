//package com.tezov.lib.database.prebuild.sync;
//
//import com.tezov.lib.toolbox.debug.DebugLog;
//import com.tezov.lib.type.primitive.IntTo;
//import com.tezov.lib.toolbox.CompareType;
//import com.tezov.lib.type.primitive.ObjectTo;
//import com.tezov.lib.util.UtilsString;
//import com.tezov.lib.toolbox.Clock;
//import com.tezov.lib.database.sqlLite.filter.dbFilterOrder;
//import com.tezov.lib.database.sqlLite.filter.chunk.ChunkCommand;
//import java.util.LinkedList;
//import java.util.Set;
//import com.tezov.lib.type.unit.UnitByte;
//i
//import java.util.ArrayList;
//import java.util.List;
//
//import com.tezov.lib.application.AppUIDGenerator;
//import com.tezov.lib.database.TableDescription;
//import com.tezov.lib.database.adapter.com.tezov.lib.definition.defContentValuesTo;
//import com.tezov.lib.database.adapter.com.tezov.lib.definition.defParcelTo;
//import com.tezov.lib.database.sqlLite.adapter.AdapterHolderLocal;
//import com.tezov.lib.cipher.holder.CipherHolderCrypto;
//import com.tezov.lib.database.firebase.adapter.AdapterHolderRemote;
//import com.tezov.lib.database.firebase.adapter.defDataSnapshotTo;
//import com.tezov.lib.database.firebase.fbTable;
//import com.tezov.lib.database.firebase.holder.fbContext;
//import com.tezov.lib.database.firebase.holder.fbTablesHandle;
//import com.tezov.lib.database.prebuild.file.table.DescriptionFile;
//import com.tezov.lib.database.prebuild.file.table.fbFileTable;
//import com.tezov.lib.database.prebuild.sync.table.DescriptionSync;
//import com.tezov.lib.database.prebuild.sync.table.DescriptionTransactionSync;
//import com.tezov.lib.database.prebuild.sync.table.dbSyncTable;
//import com.tezov.lib.database.prebuild.sync.table.dbTransactionSync;
//import com.tezov.lib.database.prebuild.sync.table.fbSyncTable;
//import com.tezov.lib.database.prebuild.trash.table.DescriptionTrash;
//import com.tezov.lib.database.sqlLite.adapter.defCursorTo;
//import com.tezov.lib.database.sqlLite.dbField;
//import com.tezov.lib.database.sqlLite.dbTable;
//import com.tezov.lib.database.sqlLite.dbTableDefinition;
//import com.tezov.lib.database.sqlLite.holder.dbContext;
//import com.tezov.lib.database.sqlLite.holder.dbTablesHandle;
//import com.tezov.lib.toolbox.Reflection;
//
//import com.tezov.lib.type.runnable.RunnableGroup;
//import com.tezov.lib.async.notifier.observer.state.ObserverState;
//import com.tezov.lib.async.notifier.observer.state.ObserverStateE;
//import com.tezov.lib.async.notifier.observer.value.ObserverValueE;
//import com.tezov.lib.async.notifier.task.TaskState;
//import com.tezov.lib.cipher.com.tezov.lib.definition.defEncoder;
//
//import com.tezov.lib.toolbox.debug.DebugTrack;
//import com.tezov.lib.type.collection.ListEntry;
//import com.tezov.lib.type.collection.ListKey;
//import com.tezov.lib.type.defEnum.EnumBase;
//import com.tezov.lib.type.runnable.RunnableW;
//import com.tezov.lib.type.anonymous.FunctionW;
//
//import static com.tezov.lib.database.prebuild.sync.SyncTableHolder.Description.FILE;
//
//public abstract class SyncTableHolder{
//private final ListKey<String, TableDescription> tableDescriptions;
//private dbTablesHandle dbHandle = null;
//private fbTablesHandle fbHandle = null;
//
//private ListEntry<String, SyncTableWorker> sync = null;
//private boolean isSyncStarted = false;
//private boolean syncLocalToRemoteEnabled = false;
//private boolean syncRemoteToLocalEnabled = false;
//
//public SyncTableHolder(){
//    DebugTrack.start().create(this).end();
//    tableDescriptions = new ListKey<String, TableDescription>(ArrayList::new, new FunctionW<TableDescription, String>(){
//        @Override
//        public String apply(TableDescription tableDescription){
//            return tableDescription.name();
//        }
//    });
//}
//
//public dbTablesHandle LOCAL(){
//    return dbHandle;
//}
//public fbTablesHandle REMOTE(){
//    return fbHandle;
//}
//
//public SyncTableWorker SYNC_LOCAL_WITH_REMOTE(TableDescription table){
//    return sync.getValue(table.name());
//}
//
//public List<TableDescription> getTableDescriptions(){
//    return tableDescriptions;
//}
//public TableDescription getTableDescription(String name){
//    return tableDescriptions.getValue(name);
//}
//public <H extends SyncTableHolder> H addTableDescriptions(TableDescription description){
//    tableDescriptions.add(description);
//    return (H)this;
//}
//public <H extends SyncTableHolder> H addTableDescriptions(List<TableDescription> descriptions){
//    tableDescriptions.addAll(descriptions);
//    return (H)this;
//}
//
//public abstract dbContext getLocalContext();
//public abstract String getLocalRootName();
//protected abstract AdapterHolderLocal getLocalAdapter();
//
//public abstract fbContext getRemoteContext();
//public String getRemoteRootName(){
//    return getLocalRootName();
//}
//protected abstract AdapterHolderRemote getRemoteAdapter();
//
//protected abstract CipherHolderCrypto getCipherHolder();
//
//protected TaskState.Observable beforeOpen(){
//    return TaskState.Complete();
//}
//protected TaskState.Observable afterOpen(){
//    return TaskState.Complete();
//}
//public TaskState.Observable open(){
//    TaskState task = new TaskState();
//    RunnableGroup gr = new RunnableGroup().name("open");
//    gr.add(new RunnableGroup.Action().name("beforeOpen"){
//        @Override
//        public void run(){
//            beforeOpen().observe(new ObserverStateE(this){
//                @Override
//                public void onComplete(){
//                    endRunnable();
//                }
//                @Override
//                public void onException(Exception e){
//                    task.notifyException(e);
//                    getParent().endGroup();
//                }
//            });
//        }
//    });
//    gr.add(new RunnableGroup.Action().name("openLocalTables"){
//        @Override
//        public void run(){
//            Exception e = openLocalTables();
//            if(e == null){
//                endRunnable();
//            } else {
//                task.notifyException(e);
//                getParent().endGroup();
//            }
//        }
//    });
//    gr.add(new RunnableGroup.Action().name("openRemoteTables"){
//        @Override
//        public void run(){
//            openRemoteTables().observe(new ObserverStateE(this){
//                @Override
//                public void onComplete(){
//                    endRunnable();
//                }
//                @Override
//                public void onException(Exception e){
//                    task.notifyException(e);
//                    getParent().endGroup();
//                }
//            });
//        }
//    });
//    gr.add(new RunnableGroup.Action().name("initSyncLocalWithRemote"){
//        @Override
//        public void run(){
//            openSyncLocalWithRemote();
//            endRunnable();
//        }
//    });
//    gr.add(new RunnableGroup.Action().name("afterOpen"){
//        @Override
//        public void run(){
//            afterOpen().observe(new ObserverStateE(this){
//                @Override
//                public void onComplete(){
//                    task.notifyComplete();
//                    endRunnable();
//                }
//                @Override
//                public void onException(Exception e){
//                    task.notifyException(e);
//                    getParent().endGroup();
//                }
//            });
//        }
//    });
//    gr.start();
//    return task.getObservable();
//}
//protected java.lang.Exception openLocalTables(){
//    ListKey<String, dbTable> dbTables = new ListKey<String, dbTable>(ArrayList::new, new FunctionW<dbTable, String>(){
//        @Override
//        public String apply(dbTable table){
//            return table.getName();
//        }
//    });
//    String userReference = getLocalRootName();
//
//    AdapterHolderLocal adapters = getLocalAdapter();
//    defParcelTo parcelTo = adapters.getParcelTo();
//    defCursorTo cursorTo = adapters.getCursorTo();
//    defContentValuesTo contentValuesTo = adapters.getContentValuesTo();
//
//    dbTableDefinition tableDefinition = new dbTableDefinition(Description.SYNC, null);
//    for(TableDescription t: tableDescriptions){
//        dbTable table = Reflection.newInstance(t.getLocalTableType())
//                .setTableDescription(t)
//                .setParcelTo(parcelTo)
//                .setCursorTo(cursorTo)
//                .setContentValuesTo(contentValuesTo);
//        dbTables.add(table);
//        dbTable syncTable = ((dbSyncTable)Reflection.newInstance(Description.SYNC.getLocalTableType()))
//                .setTableDefinition(table.newRef(), tableDefinition)
//                .setParcelTo(parcelTo).setCursorTo(cursorTo).setContentValuesTo(contentValuesTo);
//        dbTables.add(syncTable);
//    }
//    dbTable transactionTable = Reflection.newInstance(Description.TRANSACTION_SYNC.getLocalTableType())
//            .setTableDescription(Description.TRANSACTION_SYNC)
//            .setParcelTo(parcelTo)
//            .setCursorTo(cursorTo)
//            .setContentValuesTo(contentValuesTo);
//    dbTables.add(transactionTable);
//    dbHandle = new dbTablesHandle().open(getLocalContext(), userReference, dbTables);
//    return null;
//}
//protected TaskState.Observable openRemoteTables(){
//    ListKey<String, fbTable> fbTables = new ListKey<String, fbTable>(ArrayList::new, new FunctionW<fbTable, String>(){
//        @Override
//        public String apply(fbTable table){
//            return table.getName();
//        }
//    });
//    String userReference = getRemoteRootName();
//
//    AdapterHolderRemote adapters = getRemoteAdapter();
//    defParcelTo parcelTo = adapters.getParcelTo();
//    defDataSnapshotTo dataSnapshotTo = adapters.getDataSnapshotTo();
//    defContentValuesTo contentValuesTo = adapters.getContentValuesTo();
//
//    CipherHolderCrypto cipherHolder = getCipherHolder();
//    defEncoder encoderKey = cipherHolder != null ? cipherHolder.getEncoderKey() : null;
//
//    dbTableDefinition tableDefinition = new dbTableDefinition(Description.SYNC, encoderKey);
//    for(TableDescription t: tableDescriptions){
//        fbTable table = Reflection.newInstance(t.getRemoteTableType()).setTableDescription(t, encoderKey).setParcelTo(parcelTo).setDataSnapshotTo(dataSnapshotTo).setContentValuesTo(contentValuesTo);
//        fbTables.add(table);
//        fbTable syncTable = ((fbSyncTable)Reflection.newInstance(Description.SYNC.getRemoteTableType())).setTableDefinition(table.newRef(), tableDefinition)
//                .setParcelTo(parcelTo)
//                .setDataSnapshotTo(dataSnapshotTo)
//                .setContentValuesTo(contentValuesTo);
//        fbTables.add(syncTable);
//    }
//    fbHandle = new fbTablesHandle();
//    TaskState.Observable observable = fbHandle.open(getRemoteContext(), userReference, fbTables);
//    observable.observe(new ObserverStateE(this){
//        @Override
//        public void onComplete(){
//            if(tableDescriptions.contains(FILE)){
//                fbFileTable fbFileTable = (fbFileTable)fbTables.getValue(FILE.name());
//                fbFileTable.initStorage(getRemoteContext());
//                if(cipherHolder != null){
//                    fbFileTable.getFbStorage().setEncoderKey(cipherHolder.getEncoderKey()).setEncoderFile(cipherHolder.getEncoderValue()).setDecoderFile(cipherHolder.getDecoderValue());
//                }
//            }
//        }
//        @Override
//        public void onException(Exception e){
//            fbHandle = null;
//        }
//    });
//    return observable;
//}
//protected void openSyncLocalWithRemote(){
//    dbTransactionSync.Ref dbTransaction = dbHandle.getMainRef(Description.TRANSACTION_SYNC);
//    sync = new ListEntry<String, SyncTableWorker>(ArrayList::new);
//    for(TableDescription t: tableDescriptions){
//        SyncTableWorker s = new SyncTableWorker(dbTransaction, dbHandle.getMainRef(t, Description.SYNC), fbHandle.getMainRef(t, Description.SYNC));
//        sync.add(t.name(), s);
//    }
//}
//
//protected TaskState.Observable beforeClose(){
//    return TaskState.Complete();
//}
//protected TaskState.Observable afterClose(){
//    return TaskState.Complete();
//}
//public TaskState.Observable close(){
//    TaskState task = new TaskState();
//    RunnableGroup gr = new RunnableGroup().name("close");
//    gr.add(new RunnableGroup.Action().name("beforeClose"){
//        @Override
//        public void run(){
//            beforeClose().observe(new ObserverStateE(this){
//                @Override
//                public void onComplete(){
//                    endRunnable();
//                }
//                @Override
//                public void onException(Exception e){
//                    task.notifyException(e);
//                    getParent().endGroup();
//                }
//            });
//        }
//    });
//    gr.add(new RunnableGroup.Action().name("closeSyncLocalWithRemote"){
//        @Override
//        public void run(){
//            closeSyncLocalWithRemote().observe(new ObserverStateE(this){
//                @Override
//                public void onComplete(){
//                    endRunnable();
//                }
//                @Override
//                public void onException(Exception e){
//                    task.notifyException(e);
//                    getParent().endGroup();
//                }
//            });
//        }
//    });
//    gr.add(new RunnableGroup.Action().name("closeRemoteTables"){
//        @Override
//        public void run(){
//            closeRemoteTables().observe(new ObserverStateE(this){
//                @Override
//                public void onComplete(){
//                    endRunnable();
//                }
//                @Override
//                public void onException(Exception e){
//                    task.notifyException(e);
//                    getParent().endGroup();
//                }
//            });
//        }
//    });
//    gr.add(new RunnableGroup.Action().name("closeLocalTables"){
//        @Override
//        public void run(){
//            Exception e = closeLocalTables();
//            if(e == null){
//                endRunnable();
//            } else {
//                task.notifyException(e);
//                getParent().endGroup();
//            }
//        }
//    });
//    gr.add(new RunnableGroup.Action().name("afterClosed"){
//        @Override
//        public void run(){
//            afterClose().observe(new ObserverStateE(this){
//                @Override
//                public void onComplete(){
//                    task.notifyComplete();
//                    endRunnable();
//                }
//                @Override
//                public void onException(Exception e){
//                    task.notifyException(e);
//                    getParent().endGroup();
//                }
//            });
//        }
//    });
//    gr.start();
//    return task.getObservable();
//}
//public TaskState.Observable closeSyncLocalWithRemote(){
//    TaskState task = new TaskState();
//    if(sync == null){
//        task.notifyComplete();
//    } else {
//        int KEY_EXCEPTION = AppUIDGenerator.nextInt();
//        RunnableGroup gr = new RunnableGroup().name("closeSyncLocalWithRemote");
//        for(TableDescription t: tableDescriptions){
//            gr.add(new RunnableEvent("pause_sync_" + t.name()){
//                @Override
//                public void run(){
//                    java.lang.Exception e = getParent().getArguments().get(KEY_EXCEPTION);
//                    SYNC_LOCAL_WITH_REMOTE(t).pause(e != null).observe(new ObserverStateE(this){
//                        @Override
//                        public void onComplete(){
//                            endRunnable();
//                        }
//                        @Override
//                        public void onException(java.lang.Exception e){
//                            DebugException.pop().produce(e).logHidden().pop();
//                            getParent().getArguments().put(KEY_EXCEPTION, e);
//                            onComplete();
//                        }
//                    });
//                }
//            });
//        }
//        gr.add(new RunnableGroup.Action().name("close handle"){
//            @Override
//            public void run(){
//                sync = null;
//                isSyncStarted = false;
//                syncLocalToRemoteEnabled = false;
//                syncRemoteToLocalEnabled = false;
//                task.notifyComplete();
//                endRunnable();
//            }
//        });
//        gr.start();
//    }
//    return task.getObservable();
//}
//public TaskState.Observable closeRemoteTables(){
//    TaskState task = new TaskState();
//    if(fbHandle == null){
//        task.notifyComplete();
//    } else {
//        int KEY_EXCEPTION = AppUIDGenerator.nextInt();
//        RunnableGroup gr = new RunnableGroup().name("closeTables");
//        for(TableDescription t: tableDescriptions){
//            gr.add(new RunnableEvent("pause_table_" + t.getRemoteTableType().getSimpleName()){
//                @Override
//                public void run(){
//                    java.lang.Exception e = getParent().getArguments().get(KEY_EXCEPTION);
//                    fbHandle.getTable(t).pause(e != null).observe(new ObserverStateE(this){
//                        @Override
//                        public void onComplete(){
//                            endRunnable();
//                        }
//                        @Override
//                        public void onException(java.lang.Exception e){
//                            getParent().getArguments().put(KEY_EXCEPTION, e);
//                            endRunnable();
//                        }
//                    });
//                }
//            });
//        }
//        gr.add(new RunnableGroup.Action().name("close handle"){
//            @Override
//            public void run(){
//                fbHandle.close().observe(new ObserverStateE(this){
//                    @Override
//                    public void onComplete(){
//                        fbHandle = null;
//                        task.notifyComplete();
//                        endRunnable();
//                    }
//                    @Override
//                    public void onException(Exception e){
//                        fbHandle = null;
//                        task.notifyException(e);
//                        endRunnable();
//
//                    }
//                });
//            }
//        });
//        gr.start();
//    }
//    return task.getObservable();
//}
//public java.lang.Exception closeLocalTables(){
//    if(dbHandle != null){
//        dbHandle.close();
//        dbHandle = null;
//    }
//    return null;
//}
//
//public <H extends SyncTableHolder> H enableSyncLocalToRemote(boolean flag){
//    this.syncLocalToRemoteEnabled = flag;
//    for(TableDescription t: tableDescriptions){
//        SYNC_LOCAL_WITH_REMOTE(t).enableSyncLocalToRemote(flag);
//    }
//    return (H)this;
//}
//public <H extends SyncTableHolder> H enableSyncRemoteToLocal(boolean flag){
//    this.syncRemoteToLocalEnabled = flag;
//    for(TableDescription t: tableDescriptions){
//        SYNC_LOCAL_WITH_REMOTE(t).enableSyncRemoteToLocal(flag);
//    }
//    return (H)this;
//}
//public <H extends SyncTableHolder> H enableSync(boolean flag){
//    enableSyncLocalToRemote(flag);
//    enableSyncRemoteToLocal(flag);
//    return (H)this;
//}
//
//public boolean isSyncLocalToRemoteEnabled(){
//    return syncLocalToRemoteEnabled;
//}
//public boolean isSyncRemoteToLocalEnabled(){
//    return syncRemoteToLocalEnabled;
//}
//public boolean isSyncEnabled(){
//    return syncLocalToRemoteEnabled || syncRemoteToLocalEnabled;
//}
//public boolean isSyncStarted(){
//    return isSyncStarted;
//}
//
//public TaskState.Observable start(){
//    TaskState task = new TaskState();
//    if(isSyncStarted || !isSyncEnabled()){
//        task.notifyComplete();
//    } else {
//        RunnableGroup gr = new RunnableGroup().name("start_sync");
//        for(TableDescription t: tableDescriptions){
//            gr.add(new RunnableEvent("start_SYNC_" + t.getRemoteTableType().getSimpleName()){
//                @Override
//                public void run(){
//                    REMOTE().getTable(t, Description.SYNC).start();
//                    endRunnable();
//                }
//            });
//            gr.add(new RunnableEvent("start_" + t.getRemoteTableType().getSimpleName()){
//                @Override
//                public void run(){
//                    REMOTE().getTable(t).start();
//                    endRunnable();
//                }
//            });
//            gr.add(new RunnableEvent("start_sync_LwR_" + t.name()){
//                @Override
//                public void run(){
//                    SYNC_LOCAL_WITH_REMOTE(t).start();
//                    endRunnable();
//                }
//            });
//        }
//        gr.add(new RunnableGroup.Action().name("start_sync_end"){
//            @Override
//            public void run(){
//                isSyncStarted = true;
//                task.notifyComplete();
//                endRunnable();
//            }
//        });
//        gr.start();
//    }
//    return task.getObservable();
//}
//public TaskState.Observable pause(boolean force){
//    TaskState task = new TaskState();
//    if(!isSyncStarted){
//        task.notifyComplete();
//    } else {
//        int KEY_EXCEPTION = AppUIDGenerator.nextInt();
//        RunnableGroup gr = new RunnableGroup().name("pause");
//        for(TableDescription t: tableDescriptions){
//            gr.add(new RunnableEvent("pause_sync_LwR_" + t.name()){
//                @Override
//                public void run(){
//                    java.lang.Exception e = getParent().getArguments().get(KEY_EXCEPTION);
//                    SYNC_LOCAL_WITH_REMOTE(t).pause(force || (e != null)).observe(new ObserverStateE(this){
//                        @Override
//                        public void onComplete(){
//                            endRunnable();
//                        }
//                        @Override
//                        public void onException(java.lang.Exception e){
//                            DebugException.pop().produce(e).logHidden().pop();
//                            getParent().getArguments().put(KEY_EXCEPTION, e);
//                            onComplete();
//                        }
//                    });
//                }
//            });
//            gr.add(new RunnableEvent("pause_" + t.name()){
//                @Override
//                public void run(){
//                    java.lang.Exception e = getParent().getArguments().get(KEY_EXCEPTION);
//                    REMOTE().getTable(t).pause(force || (e != null)).observe(new ObserverStateE(this){
//                        @Override
//                        public void onComplete(){
//                            endRunnable();
//                        }
//
//                        @Override
//                        public void onException(java.lang.Exception e){
//                            DebugException.pop().produce(e).logHidden().pop();
//                            getParent().getArguments().put(KEY_EXCEPTION, e);
//                            onComplete();
//                        }
//                    });
//                }
//            });
//            gr.add(new RunnableEvent("pause_SYNC_" + t.name()){
//                @Override
//                public void run(){
//                    java.lang.Exception e = getParent().getArguments().get(KEY_EXCEPTION);
//                    REMOTE().getTable(t, Description.SYNC).pause(force || (e != null)).observe(new ObserverStateE(this){
//                        @Override
//                        public void onComplete(){
//                            endRunnable();
//                        }
//
//                        @Override
//                        public void onException(java.lang.Exception e){
//                            DebugException.pop().produce(e).logHidden().pop();
//                            getParent().getArguments().put(KEY_EXCEPTION, e);
//                            onComplete();
//                        }
//                    });
//                }
//            });
//        }
//        gr.add(new RunnableGroup.Action().name("pause_sync_end"){
//            @Override
//            public void run(){
//                isSyncStarted = false;
//                task.notifyComplete();
//                endRunnable();
//            }
//        });
//        gr.start();
//    }
//    return task.getObservable();
//}
//public TaskState.Observable clear(){
//    TaskState task = new TaskState();
//    RunnableGroup gr = new RunnableGroup().name("clear");
//    if(isSyncStarted){
//        gr.add(new RunnableGroup.Action().name("pause"){
//            @Override
//            public void run(){
//                pause(false).observe(new ObserverState(this){
//                    @Override
//                    public void onComplete(){
//                        endRunnable();
//                    }
//                });
//            }
//        });
//    }
//    gr.add(new RunnableGroup.Action().name("clear_local"){
//        @Override
//        public void run(){
//            for(TableDescription t: tableDescriptions){
//                dbHandle.getMainRef(t).clear();
//                dbHandle.getMainRef(t, Description.SYNC).clear();
//            }
//            dbHandle.getMainRef(Description.TRANSACTION_SYNC).clear();
//            endRunnable();
//        }
//    });
//    if(isSyncStarted){
//        for(TableDescription t: tableDescriptions){
//            gr.add(new RunnableEvent("clear_" + t.name()){
//                @Override
//                public void run(){
//                    fbHandle.getMainRef(t).clear().observe(new ObserverValueE<Void>(this){
//                        @Override
//                        public void onComplete(Void object){
//                            endRunnable();
//                        }
//
//                        @Override
//                        public void onException(Void object, java.lang.Exception e){
//                            DebugException.pop().produce(e).log().pop();
//                            endRunnable();
//                        }
//                    });
//                }
//            });
//            gr.add(new RunnableEvent("clear_SYNC_" + t.name()){
//                @Override
//                public void run(){
//                    fbHandle.getMainRef(t, Description.SYNC).clear().observe(new ObserverValueE<Void>(this){
//                        @Override
//                        public void onComplete(Void object){
//                            endRunnable();
//                        }
//
//                        @Override
//                        public void onException(Void object, java.lang.Exception e){
//                            DebugException.pop().produce(e).log().pop();
//                            endRunnable();
//                        }
//                    });
//                }
//            });
//        }
//        gr.add(new RunnableGroup.Action().name("start"){
//            @Override
//            public void run(){
//                start().observe(new ObserverState(this){
//                    @Override
//                    public void onComplete(){
//                        endRunnable();
//                    }
//                });
//
//            }
//        });
//    }
//    gr.add(new RunnableGroup.Action().name("clear_end"){
//        @Override
//        public void run(){
//            task.notifyComplete();
//            endRunnable();
//        }
//    });
//    gr.start();
//    return task.getObservable();
//}
//
//@Override
//protected void finalize() throws Throwable{
//    DebugTrack.start().destroy(this).end();
//    super.finalize();
//}
//
//public interface Description{
//    Type TRASH = new Type(DescriptionTrash.INSTANCE);
//    Type FILE = new Type(DescriptionFile.INSTANCE);
//    Type SYNC = new Type(DescriptionSync.INSTANCE);
//    Type TRANSACTION_SYNC = new Type(DescriptionTransactionSync.INSTANCE);
//    static Description.Type findInstanceOf(String name){
//        return Type.findInstanceOf(Type.class, name);
//    }
//    class Type extends EnumBase.Type implements TableDescription{
//        TableDescription description;
//
//        public Type(TableDescription description){
//            super(description.name());
//            this.description = description;
//        }
//
//        @Override
//        public String name(){
//            return description.name();
//        }
//
//        @Override
//        public String name(String prefix){
//            return description.name(prefix);
//        }
//
//        @Override
//        public Class<? extends dbField> getFieldType(){
//            return description.getFieldType();
//        }
//
//        @Override
//        public Class<? extends dbTable> getLocalTableType(){
//            return description.getLocalTableType();
//        }
//
//        @Override
//        public Class<? extends fbTable> getRemoteTableType(){
//            return description.getRemoteTableType();
//        }
//
//    }
//}
//
//}
