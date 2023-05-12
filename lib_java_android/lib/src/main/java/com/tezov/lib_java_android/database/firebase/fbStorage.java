/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.database.firebase;

import com.tezov.lib_java.debug.DebugLog;
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
import com.tezov.lib_java_android.application.AppConfigKey;
import com.tezov.lib_java_android.application.Application;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tezov.lib_java_android.application.AppConfig;
import com.tezov.lib_java.application.AppUIDGenerator;
import com.tezov.lib_java.async.notifier.observer.state.ObserverStateE;
import com.tezov.lib_java.async.notifier.observer.value.ObserverValueE;
import com.tezov.lib_java.async.notifier.task.TaskState;
import com.tezov.lib_java.async.notifier.task.TaskValue;
import com.tezov.lib_java.buffer.ByteBuffer;
import com.tezov.lib_java.cipher.definition.defDecoder;
import com.tezov.lib_java.cipher.definition.defEncoder;
import com.tezov.lib_java_android.database.firebase.holder.fbContext;
import com.tezov.lib_java_android.database.prebuild.file.item.ItemFile;
import com.tezov.lib_java.file.Directory;
import com.tezov.lib_java.file.File;
import com.tezov.lib_java.generator.uid.defUid;
import com.tezov.lib_java.toolbox.Compare;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.runnable.RunnableGroup;
import com.tezov.lib_java.type.runnable.RunnableQueue;
import com.tezov.lib_java.type.runnable.RunnableTimeOut;
import com.tezov.lib_java.type.runnable.RunnableW;

import java.util.concurrent.TimeoutException;

public class fbStorage{
public final static int LIST_MAX_ITEM = 5;
public final static float BUFFER_INPUT_SIZE_RATIO = 1.1f;
private final static Directory DIRECTORY_TEMP = AppConfig.getDirectory(AppConfigKey.FB_DIRECTORY_TEMP.getId());
private final static long FB_TABLE_PAUSE_TIMEOUT_DELAY_ms = com.tezov.lib_java.application.AppConfig.getLong(AppConfigKey.FB_TABLE_PAUSE_TIMEOUT_DELAY_ms.getId());
private fbTable<ItemFile>.Ref fbFileTable;
private StorageReference sb;
private RunnableQueue<Query> pendingQueries = null;
private boolean isStarted = true;

private defEncoder encoderKey = null;
private defEncoder encoderFile = null;
private defDecoder decoderFile = null;
private OnProgressListener onProgressListener = null;

public fbStorage(){
    try{
DebugTrack.start().create(this).end();
        pendingQueries = new RunnableQueue<>(this);
    } catch(java.lang.Throwable e){

DebugException.start().log(e).end();

    }
}

protected fbStorage me(){
    return this;
}

public fbStorage setFileTable(fbContext context, fbTable<ItemFile>.Ref fbFileTable){
    try{
        this.fbFileTable = fbFileTable;
        fbFileTable.setAutoLock(false);
        sb = context.getFireStorageReference().child(fbFileTable.getNameEncoded());
    } catch(java.lang.Throwable e){

DebugException.start().log(e).end();

    }
    return this;
}

public fbTable<ItemFile>.Ref fileTable(){
    return fbFileTable;
}

public fbStorage setEncoderKey(defEncoder encoderKey){
    this.encoderKey = encoderKey;
    return this;
}

public fbStorage setEncoderFile(defEncoder encoderFile){
    this.encoderFile = encoderFile;
    return this;
}

public fbStorage setDecoderFile(defDecoder decoderFile){
    this.decoderFile = decoderFile;
    return this;
}

public fbStorage setOnProgressListener(OnProgressListener onProgressListener){
    this.onProgressListener = onProgressListener;
    return this;
}

public String getName(){
    return fbFileTable.getName();
}

public String getNameEncoded(){
    return fbFileTable.getNameEncoded();
}

private StorageReference getStorageReference(File file){
    Directory directory = file.getDirectory();
    StorageReference reference = sb.child(encodeKey(directory.toLinkPathString()));
    reference = reference.child(encodeKey(file.getFullName()));
    return reference;
}

private String encodeKey(String key){
    if(encoderKey == null){
        return key;
    } else {
        return (String)encoderKey.encode(key, String.class);
    }
}

private ByteBuffer encodeFile(ByteBuffer byteBuffer){
    if(encoderFile == null){
        return byteBuffer;
    } else {
        return ByteBuffer.wrap(encoderFile.encode(byteBuffer.array()));
    }
}

private ByteBuffer decodeFile(byte[] b){
    if(decoderFile == null){
        return ByteBuffer.wrap(b);
    } else {
        return ByteBuffer.wrap(decoderFile.decode(b));
    }
}

private void updateTransferredBytes(long diff){
    if((diff != 0) && (onProgressListener != null)){
        onProgressListener.onProgress(diff);
    }
}

public TaskValue<ItemFile>.Observable insert(ItemFile item){
    TaskValue<ItemFile> task = new TaskValue<>();
    QueryInsert query = queriesObtain(QueryInsert.class);
    query.init(this, task).put(ArgumentsKey.ITEM, item);
    postQuery(query);
    return task.getObservable();
}

public TaskValue<ItemFile>.Observable update(ItemFile item){
    TaskValue<ItemFile> task = new TaskValue<>();
    QueryUpdate query = queriesObtain(QueryUpdate.class);
    query.init(this, task).put(ArgumentsKey.ITEM, item);
    postQuery(query);
    return task.getObservable();
}

public TaskValue<ItemFile>.Observable remove(defUid uid, boolean returnRemovedFile){
    TaskValue<ItemFile> task = new TaskValue<>();
    QueryRemove query = queriesObtain(QueryRemove.class);
    query.init(this, task).put(ArgumentsKey.UID_UUID, uid);
    query.put(ArgumentsKey.RETURN_REMOVED_FILE_BOOLEAN, returnRemovedFile);
    postQuery(query);
    return task.getObservable();
}

public TaskValue<ItemFile>.Observable remove(int index, boolean returnRemovedFile){
    TaskValue<ItemFile> task = new TaskValue<>();
    QueryRemove query = queriesObtain(QueryRemove.class);
    query.init(this, task).put(ArgumentsKey.INTEGER_INDEX, index);
    query.put(ArgumentsKey.RETURN_REMOVED_FILE_BOOLEAN, returnRemovedFile);
    postQuery(query);
    return task.getObservable();
}

public TaskValue<Void>.Observable clear(){
    TaskValue<Void> task = new TaskValue<>();
    QueryClear query = queriesObtain(QueryClear.class);
    query.init(this, task);
    postQuery(query);
    return task.getObservable();
}

public TaskValue<ItemFile>.Observable get(defUid uid){
    TaskValue<ItemFile> task = new TaskValue<>();
    QueryGet query = queriesObtain(QueryGet.class);
    query.init(this, task).put(ArgumentsKey.UID_UUID, uid);
    postQuery(query);
    return task.getObservable();
}

public TaskValue<ItemFile>.Observable get(int index){
    TaskValue<ItemFile> task = new TaskValue<>();
    QueryGet query = queriesObtain(QueryGet.class);
    query.init(this, task).put(ArgumentsKey.INTEGER_INDEX, index);
    postQuery(query);
    return task.getObservable();
}

public fbStorage start(){
    synchronized(me()){
        if(isStarted){
            return this;
        }
        this.isStarted = true;
        if(!pendingQueries.isBusy()){
            nextQuery();
        }
        return this;
    }
}

public TaskState.Observable pause(boolean force){
    synchronized(me()){
        TaskState task = new TaskState();
        if(!isStarted){
            task.notifyComplete();
            return task.getObservable();
        }
        if(!pendingQueries.isBusy()){
            isStarted = false;
            task.notifyComplete();
            return task.getObservable();
        }
        RunnableTimeOut rt = new RunnableTimeOut(this, FB_TABLE_PAUSE_TIMEOUT_DELAY_ms){
            @Override
            public void onStart(){
                isStarted = false;
                if(!pendingQueries.isBusy()){
                    completed();
                } else {
                    pendingQueries.setOnDone(new RunnableW(){
                        @Override
                        public void runSafe(){
                            completed();
                        }
                    });
                }
            }
            @Override
            public void onComplete(){
                task.notifyComplete();
            }
            @Override
            public void onTimeOut(){
                pendingQueries.setOnDone(null);
                Query query = pendingQueries.current();
                if(query != null){
                    query.queryCanceled();
                }
                task.notifyException(new TimeoutException());
            }
        };
        if(!force){
            rt.start();
        } else {
            isStarted = false;
            rt.onTimeOut();
        }
        return task.getObservable();
    }
}

protected <Q extends Query> Q queriesObtain(Class<Q> type){
    return (Q)createQuery(type);
}

protected void queriesRelease(Query q){

}

private Query createQuery(Class<? extends Query> type){
    Query query = null;
    if(type.equals(QueryInsert.class)){
        query = new QueryInsert(this);
    } else if(type.equals(QueryUpdate.class)){
        query = new QueryUpdate(this);
    } else if(type.equals(QueryRemove.class)){
        query = new QueryRemove(this);
    } else if(type.equals(QueryClear.class)){
        query = new QueryClear(this);
    } else if(type.equals(QueryGet.class)){
        query = new QueryGet(this);
    } else {
DebugException.start().explode(type.getName() + " unknown class.").end();
    }

    return query;
}

private void postQuery(Query query){
    pendingQueries.add(query);
    if(isStarted && !pendingQueries.isBusy()){
        nextQuery();
    }
}

private void nextQuery(){
    if(!isStarted || pendingQueries.isEmpty()){
        pendingQueries.done();
    } else {
        pendingQueries.next().getObservable().observe(new ObserverValueE(this){
            @Override
            public void onComplete(Object value){
                nextQuery();
            }

            @Override
            public void onException(Object value, java.lang.Throwable e){
                onComplete(null);
            }

            @Override
            public void onCancel(){
                onComplete(null);
            }
        });
    }
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}


public enum QueryException{
    CANCELED, DATABASE_ERROR, STORAGEBASE_ERROR, LOAD_FILE_ERROR, SAVE_FILE_ERROR, UID_DUPLICATE, UID_NULL, NO_ITEM_TO_INSERT, NO_ITEM_TO_REMOVE, NO_ITEM_TO_UPDATE;

    private java.lang.Throwable make(){
        return new Throwable(name());
    }

    private java.lang.Throwable make(String message){
        return new Throwable(name() + " / " + message);
    }
}


private enum ArgumentsKey{
    TASK, LOCK_QUERY, UNLOCK_QUERY_RESULT, UID_UUID, INTEGER_INDEX, ITEM, ITEM_REMOTE, BYTE_BUFFER, FILE, RETURN_REMOVED_FILE_BOOLEAN, REMOVE_OLD_FILE_BOOLEAN,
}

public interface OnProgressListener{
    void onProgress(long diff);

}

private abstract static class TaskOnSuccessListenerW<T> extends com.tezov.lib_java_android.wrapperAnonymous.TaskOnSuccessListenerW<T>{
    int id;
    Query query;
    TaskOnSuccessListenerW(Query query){
        super(query.getHandler());
        this.id = query.getId();
        this.query = query;
    }

    @Override
    public boolean isEnabled(){
        return Compare.equals(query.getId(), id);
    }

}

private abstract static class TaskOnFailureListenerW extends com.tezov.lib_java_android.wrapperAnonymous.TaskOnFailureListenerW{
    int id;
    Query query;

    TaskOnFailureListenerW(Query query){
        super(query.getHandler());
        this.id = query.getId();
        this.query = query;
    }

    @Override
    public boolean isEnabled(){
        return Compare.equals(query.getId(), id);
    }

}

private abstract static class TaskOnProgressListenerW<T> extends com.tezov.lib_java_android.wrapperAnonymous.TaskOnProgressListenerW<T>{
    int id;
    Query query;

    TaskOnProgressListenerW(Query query){
        super(query.getHandler());
        this.id = query.getId();
        this.query = query;
    }

    @Override
    public boolean isEnabled(){
        return Compare.equals(query.getId(), id);
    }

}

public abstract static class Query extends RunnableGroup{
    protected Integer id = null;
    protected fbStorage ref;

    public Query(fbStorage owner){
        super(owner);
        name(this.getClass().getSimpleName());
        onCreate();
        setOnDone(new Action(){
            @Override
            public void runSafe(){
                TaskValue task = getTask();
                if(task != null){
                    notify(task);
                }
                clear();
                id = null;
                fbStorage tmp = ref;
                ref = null;
                tmp.queriesRelease(Query.this);
            }
        });
    }
    protected Query me(){
        return this;
    }
    public Query init(fbStorage ref, TaskValue task){
        this.id = AppUIDGenerator.nextInt();
        this.ref = ref;
        put(ArgumentsKey.TASK, task);
        return this;
    }

    protected abstract void onCreate();

    public Integer getId(){
        return id;
    }

    protected TaskValue getTask(){
        return get(ArgumentsKey.TASK);
    }
    protected TaskValue.Observable getObservable(){
        return getTask().getObservable();
    }

    protected void queryCompleted(Object object){
        putValue(object);
        done();
    }

    protected void queryFailed(QueryException qe){
        queryFailed(qe, null);
    }
    protected void queryFailed(QueryException qe, java.lang.Throwable e){
        putException(qe.make(e != null ? e.getMessage() : null));
        done();
    }

    protected boolean isCanceled(){
        TaskValue task = getTask();
        return (task == null) || task.isCanceled();
    }
    protected void queryCanceled(){
        TaskValue task = getTask();
        if(task != null){
            task.cancel();
        }
        done();
    }

}

private abstract static class QueryWithLock extends Query{
    private final static int LBL_UNLOCK = AppUIDGenerator.nextInt();
    public QueryWithLock(fbStorage owner){
        super(owner);
    }

    @Override
    protected void queryCompleted(Object object){
        RunnableW r = new RunnableW(){
            @Override
            public void runSafe(){
                queryCompletedSuper(object);
            }
        };
        put(ArgumentsKey.UNLOCK_QUERY_RESULT, r);
        skipUntilLabel(LBL_UNLOCK);
    }
    private void queryCompletedSuper(Object object){
        super.queryCompleted(object);
    }

    @Override
    protected void queryFailed(QueryException qe, java.lang.Throwable e){
        RunnableW r = new RunnableW(){
            @Override
            public void runSafe(){
                queryFailedSuper(qe, e);
            }
        };
        put(ArgumentsKey.UNLOCK_QUERY_RESULT, r);
        skipUntilLabel(LBL_UNLOCK);
    }
    private void queryFailedSuper(QueryException qe, java.lang.Throwable e){
        super.queryFailed(qe, e);
    }

    @Override
    public void queryCanceled(){
        RunnableW r = new RunnableW(){
            @Override
            public void runSafe(){
                queryCanceled();
            }
        };
        put(ArgumentsKey.UNLOCK_QUERY_RESULT, r);
        skipUntilLabel(LBL_UNLOCK);
    }

    protected void acquireLock(){
        add(new Action(){
            @Override
            public void runSafe(){
                fbTableLock.Query lockQuery = ref.fileTable().getLockQuery(getHandler());
                put(ArgumentsKey.LOCK_QUERY, lockQuery);
                lockQuery.acquire(new fbTableLock.OnAcquireListener(){
                    @Override
                    public void onAcquired(){
                        next();
                    }

                    @Override
                    public void onAcquireFailed(){

DebugException.start().log(ref.getName() + "failed to acquire lock " + groupName()).end();

                        done();
                    }

                    @Override
                    public void onAcquiredTimeout(){

DebugException.start().log(ref.getName() + " lock " + "Time Out " + groupName()).end();

                    }
                });
            }
        }.name("lock"));
    }
    protected void releaseLock(){
        add(new Action(LBL_UNLOCK){
            void run_query_result(){
                RunnableW r = get(ArgumentsKey.UNLOCK_QUERY_RESULT);
                r.run();
            }
            @Override
            public void runSafe(){
                fbTableLock.Query lockQuery = get(ArgumentsKey.LOCK_QUERY);
                lockQuery.release(new fbTableLock.OnReleaseListener(){
                    @Override
                    public void onReleased(){
                        run_query_result();
                    }
                    @Override
                    public void onAcquiredCanceled(){

DebugException.start().log(ref.getName() + " failed to release lock " + groupName()).end();

                        run_query_result();
                    }
                    @Override
                    public void onReleaseFailed(){

DebugException.start().log(ref.getName() + " failed to release lock " + groupName()).end();

                        run_query_result();
                    }
                });
            }
        }.name("unlock"));
    }

}

private static class QueryInsert extends QueryWithLock{
    public QueryInsert(fbStorage owner){
        super(owner);
    }
    @Override
    public void onCreate(){
        acquireLock();
        add(new Action(){
            @Override
            public void runSafe(){
                if(isCanceled()){
                    return;
                }
                ItemFile item = get(ArgumentsKey.ITEM);
                if(item.getUid() == null){
                    queryFailed(QueryException.NO_ITEM_TO_INSERT);
                    return;
                }
                ref.fileTable().get(item.getUid()).observe(new ObserverValueE<ItemFile>(this){
                    @Override
                    public void onComplete(ItemFile itemRemote){
                        if(itemRemote != null){
DebugException.start().log(QueryException.UID_DUPLICATE.make("uuid is " + itemRemote.getUid().toHexString())).end();

                        }
                        next();
                    }

                    @Override
                    public void onException(ItemFile itemRemote, java.lang.Throwable e){
                        queryFailed(QueryException.DATABASE_ERROR, e);
                    }

                    @Override
                    public void onCancel(){
                        queryCanceled();
                    }
                });
            }
        }.name("check_if_not_exist_item"));
        add(new Action(){
            @Override
            public void runSafe(){
                if(isCanceled()){
                    return;
                }
                ItemFile item = get(ArgumentsKey.ITEM);
                Application.fileQueue().read(item.file).observe(new ObserverValueE<ByteBuffer>(this){
                    @Override
                    public void onComplete(ByteBuffer byteBuffer){
                        byteBuffer = ref.encodeFile(byteBuffer);
                        put(ArgumentsKey.BYTE_BUFFER, byteBuffer);
                        next();
                    }

                    @Override
                    public void onException(ByteBuffer byteBuffer, java.lang.Throwable e){
                        queryFailed(QueryException.LOAD_FILE_ERROR, e);
                    }
                });
            }
        }.name("get_file_local"));
        add(new Action(){
            @Override
            public void runSafe(){
                if(isCanceled()){
                    return;
                }
                ItemFile item = get(ArgumentsKey.ITEM);
                ByteBuffer byteBuffer = get(ArgumentsKey.BYTE_BUFFER);
                ref.updateTransferredBytes(byteBuffer.capacity());
                ref.getStorageReference(item.file).putBytes(byteBuffer.array()).addOnSuccessListener(new TaskOnSuccessListenerW<UploadTask.TaskSnapshot>(me()){
                    @Override
                    public void onSucceed(UploadTask.TaskSnapshot taskSnapshot){
                        next();
                    }
                }).addOnProgressListener(new TaskOnProgressListenerW<UploadTask.TaskSnapshot>(me()){
                    long lastBytesTransferred = 0;

                    @Override
                    public void onProgressed(@NonNull UploadTask.TaskSnapshot taskSnapshot){
                        ref.updateTransferredBytes(lastBytesTransferred - taskSnapshot.getBytesTransferred());
                        lastBytesTransferred = taskSnapshot.getBytesTransferred();
                    }
                }).addOnFailureListener(new TaskOnFailureListenerW(me()){
                    @Override
                    public void onFailed(@NonNull java.lang.Throwable e){
                        queryFailed(QueryException.STORAGEBASE_ERROR, e);
                    }
                });
            }
        }.name("insert_file_remote"));
        add(new Action(){
            @Override
            public void runSafe(){
                ItemFile item = get(ArgumentsKey.ITEM);
                ref.fileTable().insert(item).observe(new ObserverValueE<ItemFile>(this){
                    @Override
                    public void onComplete(ItemFile item){
                        queryCompleted(item);
                    }

                    @Override
                    public void onException(ItemFile item, java.lang.Throwable e){
                        queryFailed(QueryException.DATABASE_ERROR, e);
                    }

                    @Override
                    public void onCancel(){
                        queryCanceled();
                    }
                });
            }
        }.name("insert_item"));
        releaseLock();
    }

}

private static class QueryUpdate extends QueryWithLock{
    private final static int LBL_UPDATE_ITEM = AppUIDGenerator.nextInt();
    public QueryUpdate(fbStorage owner){
        super(owner);
    }
    @Override
    public void onCreate(){
        acquireLock();
        add(new Action(){
            @Override
            public void runSafe(){
                if(isCanceled()){
                    return;
                }
                ItemFile item = get(ArgumentsKey.ITEM);
                if(item.getUid() == null){
                    queryFailed(QueryException.NO_ITEM_TO_UPDATE);
                    return;
                }
                ref.fileTable().get(item.getUid()).observe(new ObserverValueE<ItemFile>(this){
                    @Override
                    public void onComplete(ItemFile itemRemote){
                        if(itemRemote == null){
                            queryFailed(QueryException.NO_ITEM_TO_UPDATE);
                        } else {
                            put(ArgumentsKey.ITEM_REMOTE, itemRemote);
                            next();
                        }
                    }

                    @Override
                    public void onException(ItemFile itemRemote, java.lang.Throwable e){
                        queryFailed(QueryException.DATABASE_ERROR, e);
                    }

                    @Override
                    public void onCancel(){
                        queryCanceled();
                    }
                });
            }
        }.name("check_if_exist_item"));
        add(new Action(){
            @Override
            public void runSafe(){
                if(isCanceled()){
                    return;
                }
                ItemFile newItem = get(ArgumentsKey.ITEM);
                ItemFile currentItem = get(ArgumentsKey.ITEM_REMOTE);
                if((newItem.file.equals(currentItem.file)) && (newItem.size() == currentItem.size())){
                    put(ArgumentsKey.REMOVE_OLD_FILE_BOOLEAN, false);
                    skipUntilLabel(LBL_UPDATE_ITEM);
                } else {
                    put(ArgumentsKey.REMOVE_OLD_FILE_BOOLEAN, true);
                    next();
                }
            }
        }.name("compare_file_name"));
        add(new Action(){
            @Override
            public void runSafe(){
                if(isCanceled()){
                    return;
                }
                ItemFile item = get(ArgumentsKey.ITEM);
                Application.fileQueue().read(item.file).observe(new ObserverValueE<ByteBuffer>(this){
                    @Override
                    public void onComplete(ByteBuffer byteBuffer){
                        byteBuffer = ref.encodeFile(byteBuffer);
                        put(ArgumentsKey.BYTE_BUFFER, byteBuffer);
                        next();
                    }

                    @Override
                    public void onException(ByteBuffer byteBuffer, java.lang.Throwable e){
                        queryFailed(QueryException.LOAD_FILE_ERROR, e);
                    }
                });
            }
        }.name("get_file_local"));
        add(new Action(){
            @Override
            public void runSafe(){
                if(isCanceled()){
                    return;
                }
                ItemFile item = get(ArgumentsKey.ITEM);
                ByteBuffer byteBuffer = get(ArgumentsKey.BYTE_BUFFER);
                ref.updateTransferredBytes(byteBuffer.capacity());
                ref.getStorageReference(item.file).putBytes(byteBuffer.array()).addOnSuccessListener(new TaskOnSuccessListenerW<UploadTask.TaskSnapshot>(me()){
                    @Override
                    public void onSucceed(UploadTask.TaskSnapshot taskSnapshot){
                        next();
                    }
                }).addOnProgressListener(new TaskOnProgressListenerW<UploadTask.TaskSnapshot>(me()){
                    long lastBytesTransferred = 0;

                    @Override
                    public void onProgressed(@NonNull UploadTask.TaskSnapshot taskSnapshot){
                        ref.updateTransferredBytes(lastBytesTransferred - taskSnapshot.getBytesTransferred());
                        lastBytesTransferred = taskSnapshot.getBytesTransferred();
                    }
                }).addOnFailureListener(new TaskOnFailureListenerW(me()){
                    @Override
                    public void onFailed(@NonNull java.lang.Throwable e){
                        queryFailed(QueryException.STORAGEBASE_ERROR, e);
                    }
                });
            }
        }.name("update_file_remote"));
        add(new Action(LBL_UPDATE_ITEM){
            @Override
            public void runSafe(){
                ItemFile item = get(ArgumentsKey.ITEM);
                ref.fileTable().update(item).observe(new ObserverValueE<ItemFile>(this){
                    @Override
                    public void onComplete(ItemFile item){
                        boolean removeOldFile = get(ArgumentsKey.REMOVE_OLD_FILE_BOOLEAN);
                        if(!removeOldFile){
                            queryCompleted(item);
                        } else {
                            next();
                        }
                    }

                    @Override
                    public void onException(ItemFile item, java.lang.Throwable e){
                        queryFailed(QueryException.DATABASE_ERROR, e);
                    }

                    @Override
                    public void onCancel(){
                        queryCanceled();
                    }
                });
            }
        }.name("update_item"));
        add(new Action(){
            @Override
            public void runSafe(){
                ItemFile item = get(ArgumentsKey.ITEM_REMOTE);
                ref.getStorageReference(item.file).delete().addOnSuccessListener(new TaskOnSuccessListenerW<Void>(me()){
                    @Override
                    public void onSucceed(Void nothing){
                        queryCompleted(item);
                    }
                }).addOnFailureListener(new TaskOnFailureListenerW(me()){
                    @Override
                    public void onFailed(@NonNull java.lang.Throwable e){
                        queryFailed(QueryException.STORAGEBASE_ERROR, e);
                    }
                });
            }
        }.name("remove_old_file_remote"));
        releaseLock();
    }

}

private static class QueryRemove extends QueryWithLock{
    private final static int LBL_REMOVE_FILE = AppUIDGenerator.nextInt();
    public QueryRemove(fbStorage owner){
        super(owner);
    }
    @Override
    public void onCreate(){
        acquireLock();
        add(new Action(){
            @Override
            public void runSafe(){
                if(isCanceled()){
                    return;
                }
                defUid uid = get(ArgumentsKey.UID_UUID);
                Integer index = get(ArgumentsKey.INTEGER_INDEX);
                ObserverValueE<ItemFile> observer = new ObserverValueE<ItemFile>(this){
                    @Override
                    public void onComplete(ItemFile itemRemote){
                        if(itemRemote == null){
                            queryFailed(QueryException.NO_ITEM_TO_REMOVE);
                        } else {
                            put(ArgumentsKey.ITEM, itemRemote);
                            boolean file_download_enable = get(ArgumentsKey.RETURN_REMOVED_FILE_BOOLEAN);
                            if(file_download_enable){
                                next();
                            } else {
                                skipUntilLabel(LBL_REMOVE_FILE);
                            }
                        }
                    }

                    @Override
                    public void onException(ItemFile itemRemote, Throwable e){
                        queryFailed(QueryException.DATABASE_ERROR, e);
                    }

                    @Override
                    public void onCancel(){
                        queryCanceled();
                    }
                };
                if(uid != null){
                    ref.fileTable().get(uid).observe(observer);
                } else {
                    if(index != null){
                        ref.fileTable().get(index).observe(observer);
                    } else {
DebugException.start().unknown("type").end();
                    }

                }
            }
        }.name("check_if_exist_item"));
        add(new Action(){
            @Override
            public void runSafe(){
                if(isCanceled()){
                    return;
                }
                ItemFile item = get(ArgumentsKey.ITEM);
                ref.getStorageReference(item.file).getBytes((long)(item.size() * BUFFER_INPUT_SIZE_RATIO)).addOnSuccessListener(new TaskOnSuccessListenerW<byte[]>(me()){
                    @Override
                    public void onSucceed(byte[] bytes){
                        ByteBuffer byteBuffer = ref.decodeFile(bytes);
                        put(ArgumentsKey.BYTE_BUFFER, byteBuffer);
                        next();
                    }
                }).addOnFailureListener(new TaskOnFailureListenerW(me()){
                    @Override
                    public void onFailed(@NonNull java.lang.Throwable e){
                        queryFailed(QueryException.STORAGEBASE_ERROR, e);
                    }
                });
            }
        }.name("get_file_remote"));
        add(new Action(){
            @Override
            public void runSafe(){
                ByteBuffer byteBuffer = get(ArgumentsKey.BYTE_BUFFER);
                ItemFile itemFile = get(ArgumentsKey.ITEM);
                File file = new File(DIRECTORY_TEMP, itemFile.file.getFullName());
                put(ArgumentsKey.FILE, file);
                Application.fileQueue().write(file, byteBuffer).observe(new ObserverStateE(this){
                    @Override
                    public void onComplete(){
                        ItemFile item = get(ArgumentsKey.ITEM);
                        item.setTempFile(get(ArgumentsKey.FILE));
                        next();
                    }

                    @Override
                    public void onException(java.lang.Throwable e){
                        queryFailed(QueryException.SAVE_FILE_ERROR);
                    }
                });
            }
        }.name("save_to_cache"));
        add(new Action(LBL_REMOVE_FILE){
            @Override
            public void runSafe(){
                if(isCanceled()){
                    return;
                }
                ItemFile item = get(ArgumentsKey.ITEM);
                ref.getStorageReference(item.file).delete().addOnSuccessListener(new TaskOnSuccessListenerW<Void>(me()){
                    @Override
                    public void onSucceed(Void nothing){
                        next();
                    }
                }).addOnFailureListener(new TaskOnFailureListenerW(me()){
                    @Override
                    public void onFailed(@NonNull java.lang.Throwable e){
                        queryFailed(QueryException.STORAGEBASE_ERROR, e);
                    }
                });
            }
        }.name("remove_file_remote"));
        add(new Action(){
            @Override
            public void runSafe(){
                ItemFile item = get(ArgumentsKey.ITEM);
                ref.fileTable().remove(item.getUid()).observe(new ObserverValueE<ItemFile>(this){
                    @Override
                    public void onComplete(ItemFile item){
                        queryCompleted(item);
                    }

                    @Override
                    public void onException(ItemFile item, java.lang.Throwable e){
                        queryFailed(QueryException.DATABASE_ERROR, e);
                    }

                    @Override
                    public void onCancel(){
                        queryCanceled();
                    }
                });
            }
        }.name("remove_item"));
        releaseLock();
    }

}

private static class QueryClear extends QueryWithLock{
    public QueryClear(fbStorage owner){
        super(owner);
    }
    @Override
    public void onCreate(){
        acquireLock();
        add(new Action(){
            int call = 0;

            @Override
            public void runSafe(){
                run(ref.sb, null);
            }
            void run(StorageReference sb, String token){
                Task<ListResult> request;
                if(token == null){
                    request = sb.list(LIST_MAX_ITEM);
                } else {
                    request = sb.list(LIST_MAX_ITEM, token);
                }
                incCall();
                request.addOnSuccessListener(new TaskOnSuccessListenerW<ListResult>(me()){
                    @Override
                    public void onSucceed(ListResult result){
                        for(StorageReference ref: result.getItems()){
                            ref.delete();
                        }
                        for(StorageReference ref: result.getPrefixes()){
                            run(ref, null);
                        }
                        String token = result.getPageToken();
                        if(token != null){
                            run(sb, token);
                        }
                        decCall();
                    }
                }).addOnFailureListener(new TaskOnFailureListenerW(me()){
                    @Override
                    public void onFailed(@NonNull java.lang.Throwable e){
                        queryFailed(QueryException.STORAGEBASE_ERROR, e);
                    }
                });
            }
            void incCall(){
                call++;
            }
            void decCall(){
                call--;
                if(call <= 0){
                    next();
                }
            }
        }.name("clear_file_remote"));
        add(new Action(){
            @Override
            public void runSafe(){
                ref.fileTable().clear().observe(new ObserverValueE<Void>(this){
                    @Override
                    public void onComplete(Void nothing){
                        queryCompleted(null);
                    }

                    @Override
                    public void onException(Void nothing, java.lang.Throwable e){
                        queryFailed(QueryException.DATABASE_ERROR, e);
                    }

                    @Override
                    public void onCancel(){
                        queryCanceled();
                    }
                });
            }
        }.name("clear_item"));
        releaseLock();
    }

}

private static class QueryGet extends QueryWithLock{
    public QueryGet(fbStorage owner){
        super(owner);
    }
    @Override
    public void onCreate(){
        acquireLock();
        add(new Action(){
            @Override
            public void runSafe(){
                if(isCanceled()){
                    return;
                }
                defUid uid = get(ArgumentsKey.UID_UUID);
                Integer index = get(ArgumentsKey.INTEGER_INDEX);
                ObserverValueE<ItemFile> observer = new ObserverValueE<ItemFile>(this){
                    @Override
                    public void onComplete(ItemFile itemRemote){
                        if(itemRemote == null){
                            queryCompleted(null);
                        } else {
                            put(ArgumentsKey.ITEM, itemRemote);
                            next();
                        }
                    }

                    @Override
                    public void onException(ItemFile itemRemote, Throwable e){
                        queryFailed(QueryException.DATABASE_ERROR, e);
                    }

                    @Override
                    public void onCancel(){
                        queryCanceled();
                    }
                };
                if(uid != null){
                    ref.fileTable().get(uid).observe(observer);
                } else {
                    if(index != null){
                        ref.fileTable().get(index).observe(observer);
                    } else {
DebugException.start().unknown("type").end();
                    }

                }
            }
        }.name("get_item"));
        add(new Action(){
            @Override
            public void runSafe(){
                if(isCanceled()){
                    return;
                }
                ItemFile item = get(ArgumentsKey.ITEM);
                ref.getStorageReference(item.file).getBytes((long)(item.size() * BUFFER_INPUT_SIZE_RATIO)).addOnSuccessListener(new TaskOnSuccessListenerW<byte[]>(me()){
                    @Override
                    public void onSucceed(byte[] bytes){
                        ByteBuffer byteBuffer = ref.decodeFile(bytes);
                        put(ArgumentsKey.BYTE_BUFFER, byteBuffer);
                        next();
                    }
                }).addOnFailureListener(new TaskOnFailureListenerW(me()){
                    @Override
                    public void onFailed(@NonNull java.lang.Throwable e){
                        queryFailed(QueryException.STORAGEBASE_ERROR, e);
                    }
                });
            }
        }.name("get_file_remote"));
        add(new Action(){
            @Override
            public void runSafe(){
                ByteBuffer byteBuffer = get(ArgumentsKey.BYTE_BUFFER);
                ItemFile itemFile = get(ArgumentsKey.ITEM);
                File file = new File(DIRECTORY_TEMP, itemFile.file.getFullName());
                put(ArgumentsKey.FILE, file);
                Application.fileQueue().write(file, byteBuffer).observe(new ObserverStateE(this){
                    @Override
                    public void onComplete(){
                        ItemFile item = get(ArgumentsKey.ITEM);
                        item.setTempFile(get(ArgumentsKey.FILE));
                        queryCompleted(item);
                    }

                    @Override
                    public void onException(java.lang.Throwable e){
                        queryFailed(QueryException.SAVE_FILE_ERROR);
                    }
                });
            }
        }.name("save_to_cache"));
        releaseLock();
    }

}

}


