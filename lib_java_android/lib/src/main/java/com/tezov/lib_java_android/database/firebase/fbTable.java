/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.database.firebase;

import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.util.UtilsString;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import androidx.fragment.app.Fragment;
import static com.tezov.lib_java_android.database.firebase.fbTable.Event.CLEAR;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.ArrayMap;
import androidx.collection.ArraySet;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.tezov.lib_java_android.application.AppConfigKey;
import com.tezov.lib_java_android.application.AppConfig;
import com.tezov.lib_java.application.AppUIDGenerator;
import com.tezov.lib_java.async.Handler;
import com.tezov.lib_java.async.notifier.Notifier;
import com.tezov.lib_java.async.notifier.observable.ObservableEvent;
import com.tezov.lib_java.async.notifier.observer.event.ObserverEvent;
import com.tezov.lib_java.async.notifier.observer.state.ObserverStateE;
import com.tezov.lib_java.async.notifier.observer.value.ObserverValueE;
import com.tezov.lib_java.async.notifier.task.TaskState;
import com.tezov.lib_java.async.notifier.task.TaskValue;
import com.tezov.lib_java.cipher.definition.defEncoder;
import com.tezov.lib_java_android.database.ItemBase;
import com.tezov.lib_java_android.database.TableDescription;
import com.tezov.lib_java_android.database.adapter.definition.defContentValuesTo;
import com.tezov.lib_java_android.database.adapter.definition.defParcelTo;
import com.tezov.lib_java_android.database.firebase.adapter.defDataSnapshotTo;
import com.tezov.lib_java_android.database.firebase.holder.fbTablesHandle;
import com.tezov.lib_java_android.database.sqlLite.dbField;
import com.tezov.lib_java_android.database.sqlLite.dbTableDefinition;
import com.tezov.lib_java_android.definition.defCreatable;
import com.tezov.lib_java.definition.defProviderAsync;
import com.tezov.lib_java.generator.uid.defUIDGenerator;
import com.tezov.lib_java.generator.uid.defUid;
import com.tezov.lib_java.toolbox.Compare;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.Extrema;
import com.tezov.lib_java_android.type.android.wrapper.ContentValuesW;
import com.tezov.lib_java_android.wrapperAnonymous.FireBaseChildEventListenerW;
import com.tezov.lib_java.wrapperAnonymous.PredicateW;
import com.tezov.lib_java.type.collection.Arguments;
import com.tezov.lib_java.type.collection.ListOrObject;
import com.tezov.lib_java.type.primitive.BytesTo;
import com.tezov.lib_java.type.runnable.RunnableGroup;
import com.tezov.lib_java.type.runnable.RunnableQueue;
import com.tezov.lib_java.type.runnable.RunnableTimeOut;
import com.tezov.lib_java.type.runnable.RunnableW;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeoutException;

import java9.util.stream.Collectors;
import java9.util.stream.StreamSupport;

//TODO: rule generate
public abstract class fbTable<ITEM extends ItemBase<ITEM>>{
private final static long FB_TABLE_PAUSE_TIMEOUT_DELAY_ms = AppConfig.getLong(AppConfigKey.FB_TABLE_PAUSE_TIMEOUT_DELAY_ms.getId());

final private static int TO_LOG_DELAY_LENGTH = 30;
private final static int FIRST_INDEX_INIT = 0;
private ReferenceEntry refEntry;
private ReferenceInfo refInfo;
private fbTable<ITEM>.Ref mainRef = null;
private Notifier<Event> notifier;
private dbTableDefinition.Ref tableDefinition;
private defDataSnapshotTo dataSnapshotTo;
private defParcelTo parcelTo;
private defContentValuesTo contentValuesTo;
private defCreatable<ITEM> factory;
private fbTablesHandle fb;
private fbTableLock lock;
private RunnableQueue<Query> pendingQueries = null;
private boolean isStarted = true;

protected fbTable(){
    try{
DebugTrack.start().create(this).end();
        notifier = new Notifier<>(new ObservableEvent<Event, Object>(), false);
        pendingQueries = new RunnableQueue<>(this);
    } catch(java.lang.Throwable e){

DebugException.start().log(e).end();

    }
}

protected fbTable<ITEM> me(){
    return this;
}

public void setDatabase(fbTablesHandle fb){
    this.fb = fb;
    lock = new fbTableLock(fb, getNameEncoded());
}

protected dbTableDefinition.Ref newTableDefinition(TableDescription description, defEncoder encoderField){
    return new dbTableDefinition(description, encoderField).newRef(null);
}

public fbTable<ITEM> setTableDescription(TableDescription description, defEncoder encoderField){
    return setTableDefinition(newTableDefinition(description, encoderField));
}

protected dbTableDefinition.Ref newTableDefinition(dbTableDefinition definition){
    return definition.newRef(null);
}

public dbTableDefinition.Ref getTableDefinition(){
    return tableDefinition;
}

public fbTable<ITEM> setTableDefinition(dbTableDefinition definition){
    return setTableDefinition(newTableDefinition(definition));
}

public fbTable<ITEM> setTableDefinition(dbTableDefinition.Ref definition){
    try{
        tableDefinition = definition;
        refEntry = new ReferenceEntry(definition.getNameEncoded());
        refInfo = new ReferenceInfo(definition.getNameEncoded());
        createMainRef();
    } catch(java.lang.Throwable e){

DebugException.start().log(e).end();

    }
    return this;
}

public String getName(){
    return tableDefinition.getName();
}

public String getNameEncoded(){
    return tableDefinition.getNameEncoded();
}

public defCreatable<ITEM> factory(){
    return factory;
}

public fbTable<ITEM> setFactory(defCreatable<ITEM> factory){
    this.factory = factory;
    return this;
}

public Class<ITEM> getType(){
    return factory.getType();
}

public abstract <GEN extends defUIDGenerator> GEN getUidGenerator();

public ContentValuesW toContentValues(ITEM item){
    return parcelTo.contentValues(item, getTableDefinition().getFields());
}

public ITEM toItem(ContentValuesW contentValues){
    return contentValuesTo.item(contentValues, getTableDefinition().getFields(), factory());
}

public ITEM toItem(DataSnapshot dataSnapshot){
    return dataSnapshotTo.item(dataSnapshot, getTableDefinition().getFields(), factory());
}

public Map<String, Object> toMap(Map<String, Object> map, String pushKey, ContentValuesW contentValues){
    Reference refEntry = this.refEntry.newEntry(pushKey);
    for(Map.Entry<String, Object> e: contentValues.get().valueSet()){
        String key = e.getKey();
        Object value = e.getValue();
        if(value instanceof byte[]){
            map.put(refEntry.ref(key), BytesTo.StringHex((byte[])value));
        } else {
            map.put(refEntry.ref(key), value);
        }
    }
    return map;
}

public <UID extends defUid> UID getUID(ContentValuesW contentValues){
    byte[] data = contentValues.get().getAsByteArray(tableDefinition.fieldName(dbField.UID));
    if(data == null){
        return null;
    }
    return (UID)getUidGenerator().make(data);
}

public <UID extends defUid> UID getUID(DataSnapshot dataSnapshot){
    String data = dataSnapshot.child(tableDefinition.fieldName(dbField.UID)).getValue(String.class);
    if(data == null){
        return null;
    }
    return (UID)getUidGenerator().make(data);
}

public <T> T getValue(dbField.Is field, ContentValuesW contentValues){
    return getValue(field, contentValues, null);
}

public <T> T getValue(dbField.Is field, ContentValuesW contentValues, Class<T> type){
    Object o = contentValues.getValue(getTableDefinition().fieldName(field));
    if(o instanceof byte[]){
        return (T)BytesTo.StringHex((byte[])o);
    } else {
        return (T)o;
    }
}

public <T> T getValue(dbField.Is field, DataSnapshot dataSnapshot){
    return getValue(field, dataSnapshot, null);
}

public <T> T getValue(dbField.Is field, DataSnapshot dataSnapshot, Class<T> type){
    Object o = dataSnapshot.child(getTableDefinition().fieldName(field)).getValue();
    if(o instanceof byte[]){
        return (T)BytesTo.StringHex((byte[])o);
    } else {
        return (T)o;
    }
}

public fbTable<ITEM> setDataSnapshotTo(defDataSnapshotTo dataSnapshotTo){
    this.dataSnapshotTo = dataSnapshotTo;
    return this;
}

public fbTable<ITEM> setContentValuesTo(defContentValuesTo contentValuesTo){
    this.contentValuesTo = contentValuesTo;
    return this;
}

public fbTable<ITEM> setParcelTo(defParcelTo parcelTo){
    this.parcelTo = parcelTo;
    return this;
}

public fbTableLock getLock(){
    return lock;
}

protected fbTable<ITEM>.Ref createRef(){
    return new Ref();
}

public <R extends fbTable<ITEM>.Ref> R newRef(){
    synchronized(me()){
        return (R)createRef();
    }
}

private void createMainRef(){
    mainRef = createRef();
}

public <R extends fbTable<ITEM>.Ref> R mainRef(){
    synchronized(me()){
        return (R)mainRef;
    }
}

public fbTable<ITEM> start(){
    synchronized(me()){
        if(isStarted){
            return this;
        }
        this.isStarted = true;
        lock.start();
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
        RunnableGroup gr = new RunnableGroup(this).name("fbTable_pause");
        gr.add(new RunnableGroup.Action(){
            @Override
            public void runSafe(){
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
                        next();
                    }
                    @Override
                    public void onTimeOut(){
                        pendingQueries.setOnDone(null);
                        Query query = pendingQueries.current();
                        if(query != null){
                            query.queryCanceled();
                        }
                        putException(new TimeoutException());
                    }
                };
                if(!force){
                    rt.start();
                } else {
                    isStarted = false;
                    rt.onTimeOut();
                }
            }
        }.name("pause_table"));
        gr.add(new RunnableGroup.Action(){
            @Override
            public void runSafe(){
                lock.pause(getException() != null).observe(new ObserverStateE(this){
                    @Override
                    public void onComplete(){
                        next();
                    }
                    @Override
                    public void onException(java.lang.Throwable eLock){
                        if(getException() == null){
                            putException(eLock);
                        }
                        next();
                    }
                });
            }
        }.name("pause_lock"));
        gr.notifyOnDone(task);
        gr.start();
        return task.getObservable();
    }
}

private Query createQuery(Class<? extends Query> type){
    Query query = null;
    if(type.equals(QueryInsert.class)){
        query = new QueryInsert(this);
    } else if(type.equals(QueryInsertList.class)){
        query = new QueryInsertList(this);
    } else if(type.equals(QueryUpdate.class)){
        query = new QueryUpdate(this);
    } else if(type.equals(QueryUpdateList.class)){
        query = new QueryUpdateList(this);
    } else if(type.equals(QueryRemove.class)){
        query = new QueryRemove(this);
    } else if(type.equals(QueryRemoveList.class)){
        query = new QueryRemoveList(this);
    } else if(type.equals(QueryClear.class)){
        query = new QueryClear(this);
    } else if(type.equals(QueryGetWithUID.class)){
        query = new QueryGetWithUID(this);
    } else if(type.equals(QueryGetWithIndex.class)){
        query = new QueryGetWithIndex(this);
    } else if(type.equals(QuerySelect.class)){
        query = new QuerySelect(this);
    } else if(type.equals(QuerySize.class)){
        query = new QuerySize(this);
    } else if(type.equals(QueryIndexOf.class)){
        query = new QueryIndexOf(this);
    } else if(type.equals(QueryFirstIndex.class)){
        query = new QueryFirstIndex(this);
    } else if(type.equals(QueryLastIndex.class)){
        query = new QueryLastIndex(this);
    } else {
DebugException.start().unknown("type", type).end();
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


public enum Event{
    INSERT, INSERT_LIST, UPDATE, UPDATE_LIST, REMOVE, REMOVE_LIST, CLEAR
}


public enum QueryException{
    FAILED_TO_ACQUIRE_LOCK, FAILED_TO_RELEASE_LOCK, DATABASE_ERROR, UID_DUPLICATE, UID_NULL, COUNT_INCORRECT, DATA_IS_MISSING, NO_ITEM_TO_INSERT, NO_ITEM_TO_REMOVE, NO_ITEM_TO_UPDATE;
    private java.lang.Throwable make(){
        return new Throwable(name());
    }
    private java.lang.Throwable make(String message){
        return new Throwable(name() + " / " + message);
    }
}

protected enum ArgumentsKey{
    EXCEPTION, TASK, LOCK_QUERY, UNLOCK_QUERY_RESULT, PUSH_KEY_STRING, PUSH_KEY_MAP, ITEM, ITEM_LIST, CONTENT_VALUES, CONTENT_VALUES_MAP, ITEM_MAP, UID_UUID, UID_UUID_LAST, UID_UUID_LIST,
    INDEX_INTEGER, INDEX_MAP, LENGTH_INTEGER, COUNT_INTEGER,
}

private static class Reference{
    String root;
    public Reference(String root){
        this.root = root;
    }
    String root(){
        return root;
    }
    String ref(String ref){
        return this.root + "/" + ref;
    }

}

private static class ReferenceInfo{
    final static String ref = "info";
    String root;
    Size refSize;
    Index refIndex;
    ReferenceInfo(String root){
        this.root = root + "/" + ref;
        this.refSize = ()->this.root + "/" + Size.ref;
        this.refIndex = ()->this.root + "/" + Index.ref;
    }
    interface Index{
        String ref = "index";

        String root();

        default Reference newEntry(String ref){
            return new Reference(root() + "/" + ref);
        }

    }

    interface Size{
        String ref = "size";

        String root();

    }

}

private static class ReferenceEntry{
    final static String ref = "table";
    String root;
    ReferenceEntry(String root){
        this.root = root + "/" + ReferenceEntry.ref;
    }
    Reference newEntry(String ref){
        return new Reference(root + "/" + ref);
    }

}

public static class Subscription extends com.tezov.lib_java.async.notifier.Subscription<FireBaseChildEventListenerW>{
    public Subscription(FireBaseChildEventListenerW listener){
        super(listener);
    }

    @Override
    public boolean unsubscribe(){
        getRef().removeEvent();
        return true;
    }

}

private abstract static class FireBaseValueEventListenerW extends com.tezov.lib_java_android.wrapperAnonymous.FireBaseValueEventListenerW{
    int id;
    Query<?> query;
    FireBaseValueEventListenerW(Query<?> query){
        super(query.getHandler());
        this.id = query.getId();
        this.query = query;
    }
    @Override
    public boolean isEnabled(){
        return Compare.equals(query.getId(), id);
    }

}

private abstract static class FirebaseCompletionListenerW extends com.tezov.lib_java_android.wrapperAnonymous.FirebaseCompletionListenerW{
    int id;
    Query<?> query;
    FirebaseCompletionListenerW(Query<?> query){
        super(query.getHandler());
        this.id = query.getId();
        this.query = query;
    }
    @Override
    public boolean isEnabled(){
        return Compare.equals(query.getId(), id);
    }

}

public abstract static class Query<ITEM extends ItemBase<ITEM>> extends RunnableGroup{
    protected Integer id = null;
    protected fbTable<ITEM>.Ref ref;
    public Query(fbTable<ITEM> owner){
        super(owner);
        name(this.getClass().getName());
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
                fbTable<ITEM>.Ref tmp = ref;
                ref = null;
                tmp.queriesRelease(Query.this);
            }
        });
    }

    protected Query me(){
        return this;
    }

    public Query<ITEM> init(fbTable<ITEM>.Ref ref, TaskValue task){
        this.id = AppUIDGenerator.nextInt();
        this.ref = ref;
        put(ArgumentsKey.TASK, task);
        return this;
    }

    protected abstract void onCreate();

    public Integer getId(){
        return id;
    }

    public String uidKey(){
        return ref.getTableDefinition().fieldName(dbField.UID);
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

    protected void post(Event event, Object object){
        ref.post(event, object);
    }

}

private abstract static class QueryWithLock<ITEM extends ItemBase<ITEM>> extends Query<ITEM>{
    private final static int LBL_UNLOCK = AppUIDGenerator.nextInt();
    public QueryWithLock(fbTable<ITEM> owner){
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
                if(!ref.autoLock){
                    next();
                    return;
                }
                fbTableLock.Query lockQuery = ref.getLockQuery(getHandler());
                put(ArgumentsKey.LOCK_QUERY, lockQuery);
                lockQuery.acquire(new fbTableLock.OnAcquireListener(){
                    @Override
                    public void onAcquired(){
                        next();
                    }
                    @Override
                    public void onAcquireFailed(){

DebugException.start().log(ref.getName() + " failed to acquire lock " + groupName()).end();

                        done();
                    }

                    @Override
                    public void onAcquiredTimeout(){

DebugException.start().log(ref.getName() + " lock Time Out " + groupName()).end();

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
                if(!ref.autoLock){
                    run_query_result();
                    return;
                }
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

private static class QueryInsert<ITEM extends ItemBase<ITEM>> extends QueryWithLock<ITEM>{
    public QueryInsert(fbTable<ITEM> owner){
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
                if(uid == null){
                    queryFailed(QueryException.NO_ITEM_TO_INSERT);
                    return;
                }
                FireBaseValueEventListenerW postListener = new FireBaseValueEventListenerW(me()){
                    @Override
                    public void onChange(DataSnapshot dataSnapshot){
                        if(dataSnapshot.getChildrenCount() >= 1){
                            defUid uid = get(ArgumentsKey.UID_UUID);
DebugException.start().log(QueryException.UID_DUPLICATE.make("uuid is" + uid.toHexString())).end();
                        }
                        if(dataSnapshot.getChildrenCount() == 0){
                            next();
                        } else {
                            queryFailed(QueryException.NO_ITEM_TO_INSERT);
                        }
                    }

                    @Override
                    public void onCancel(DatabaseError databaseError){
                        queryFailed(QueryException.NO_ITEM_TO_INSERT, databaseError.toException());
                    }
                };
                ref.childEntry().orderByChild(uidKey()).equalTo(uid.toHexString()).addListenerForSingleValueEvent(postListener);
            }
        }.name("check_if_not_exist_item"));
        add(new Action(){
            @Override
            public void runSafe(){
                if(isCanceled()){
                    return;
                }
                FireBaseValueEventListenerW postListener = new FireBaseValueEventListenerW(me()){
                    @Override
                    public void onChange(DataSnapshot dataSnapshot){
                        Integer count = dataSnapshot.getValue(Integer.class);
                        if(count == null){
                            count = 0;
                        }
                        put(ArgumentsKey.COUNT_INTEGER, count);
                        next();
                    }

                    @Override
                    public void onCancel(DatabaseError databaseError){
                        queryFailed(QueryException.DATABASE_ERROR, databaseError.toException());
                    }
                };
                ref.childSize().addListenerForSingleValueEvent(postListener);
            }
        }.name("get_count_items"));
        add(new Action(){
            @Override
            public void runSafe(){
                if(isCanceled()){
                    return;
                }
                String pushKey = ref.generateKey();
                ContentValuesW contentValues = get(ArgumentsKey.CONTENT_VALUES);
                Integer count = get(ArgumentsKey.COUNT_INTEGER);
                Map<String, Object> data = ref.toMap(pushKey, contentValues);
                Reference refIndex = ref.getRefInfo().refIndex.newEntry(pushKey);
                data.put(refIndex.ref(ReferenceInfo.Index.ref), count + FIRST_INDEX_INIT);
                data.put(refIndex.ref(uidKey()), ref.getUID(contentValues).toHexString());
                data.put(ref.getRefInfo().refSize.root(), count + 1);
                ref.updateChildren(data, new FirebaseCompletionListenerW(me()){
                    @Override
                    public void onDone(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference){
                        ref.bufferMap.clear();
                        if(databaseError != null){
                            queryFailed(QueryException.DATABASE_ERROR, databaseError.toException());
                        } else {
                            ITEM dataToReturn = get(ArgumentsKey.ITEM);
                            ref.post(Event.INSERT, dataToReturn);
                            queryCompleted(dataToReturn);
                        }
                    }
                });
            }
        }.name("insert_item"));
        releaseLock();
    }

}

private static class QueryInsertList<ITEM extends ItemBase<ITEM>> extends QueryWithLock<ITEM>{
    public QueryInsertList(fbTable<ITEM> owner){
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
                FireBaseValueEventListenerW postListener = new FireBaseValueEventListenerW(me()){
                    @Override
                    public void onChange(DataSnapshot dataSnapshot){
                        Map<defUid, ContentValuesW> items = get(ArgumentsKey.CONTENT_VALUES_MAP);
                        List<defUid> uidToInsertList = new ArrayList<>(items.keySet());
                        if(uidToInsertList.size() <= 0){
                            queryFailed(QueryException.NO_ITEM_TO_INSERT);
                            return;
                        }
                        Set<defUid> uidExistingList = new ArraySet<>();
                        for(DataSnapshot entry: dataSnapshot.getChildren()){
                            defUid uid = ref.getUID(entry);
                            if(!uidExistingList.add(uid)){
DebugException.start().log(QueryException.UID_DUPLICATE.make("uuid " + "is" + uid.toHexString())).end();
                            }
                        }
                        PredicateW<defUid> filter = new PredicateW<defUid>(){
                            @Override
                            public boolean test(defUid uid){
                                boolean result = uidExistingList.contains(uid);
                                if(result){
                                    uidExistingList.remove(uid);
                                }
                                return !result;
                            }
                        };
                        uidToInsertList = StreamSupport.stream(uidToInsertList).filter(filter).collect(Collectors.toList());
                        if(uidToInsertList.size() <= 0){
                            queryFailed(QueryException.NO_ITEM_TO_INSERT);
                            return;
                        }
                        put(ArgumentsKey.UID_UUID_LIST, uidToInsertList);
                        next();
                    }

                    @Override
                    public void onCancel(DatabaseError databaseError){
                        queryFailed(QueryException.DATABASE_ERROR, databaseError.toException());
                    }
                };
                Map<defUid, ContentValuesW> items = get(ArgumentsKey.CONTENT_VALUES_MAP);
                List<defUid> uidToInsertList = new ArrayList<>(items.keySet());
                Extrema.Value<defUid> extrema = Extrema.find(uidToInsertList);
                ref.childInfoIndex().orderByChild(uidKey()).startAt(extrema.min.toHexString()).endAt(extrema.max.toHexString()).addListenerForSingleValueEvent(postListener);
            }
        }.name("check_if_not_exist_items"));
        add(new Action(){
            @Override
            public void runSafe(){
                if(isCanceled()){
                    return;
                }
                FireBaseValueEventListenerW postListener = new FireBaseValueEventListenerW(me()){
                    @Override
                    public void onChange(DataSnapshot dataSnapshot){
                        Integer count = dataSnapshot.getValue(Integer.class);
                        if(count == null){
                            count = 0;
                        }
                        put(ArgumentsKey.COUNT_INTEGER, count);
                        next();
                    }

                    @Override
                    public void onCancel(DatabaseError databaseError){
                        queryFailed(QueryException.DATABASE_ERROR, databaseError.toException());
                    }
                };
                ref.childSize().addListenerForSingleValueEvent(postListener);
            }
        }.name("get_count_items"));
        add(new Action(){
            @Override
            public void runSafe(){
                if(isCanceled()){
                    return;
                }
                List<ITEM> itemToInsertList = new ArrayList<>();
                List<defUid> uidToInsertList = get(ArgumentsKey.UID_UUID_LIST);
                Map<defUid, ContentValuesW> contentValuesMap = get(ArgumentsKey.CONTENT_VALUES_MAP);
                Map<defUid, ITEM> itemsMap = get(ArgumentsKey.ITEM_MAP);
                Integer count = get(ArgumentsKey.COUNT_INTEGER);
                Map<String, Object> data = ref.bufferMap;
                for(defUid uid: uidToInsertList){
                    ContentValuesW contentValues = contentValuesMap.get(uid);
                    itemToInsertList.add(itemsMap.get(uid));
                    String pushKey = ref.generateKey();
                    ref.toMap(pushKey, contentValues);
                    Reference refIndex = ref.getRefInfo().refIndex.newEntry(pushKey);
                    data.put(refIndex.ref(ReferenceInfo.Index.ref), count + FIRST_INDEX_INIT);
                    data.put(refIndex.ref(uidKey()), uid.toHexString());
                    count++;
                }
                data.put(ref.getRefInfo().refSize.root(), count);
                ref.updateChildren(data, new FirebaseCompletionListenerW(me()){
                    @Override
                    public void onDone(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference){
                        ref.bufferMap.clear();
                        if(databaseError != null){
                            queryFailed(QueryException.DATABASE_ERROR, databaseError.toException());
                        } else {
                            ref.post(Event.INSERT_LIST, itemToInsertList);
                            queryCompleted(itemToInsertList);
                        }

                    }
                });
            }
        }.name("insert_items"));
        releaseLock();
    }

}

private static class QueryUpdate<ITEM extends ItemBase<ITEM>> extends QueryWithLock<ITEM>{
    public QueryUpdate(fbTable<ITEM> owner){
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
                if(uid == null){
                    queryFailed(QueryException.NO_ITEM_TO_UPDATE);
                    return;
                }
                FireBaseValueEventListenerW postListener = new FireBaseValueEventListenerW(me()){
                    @Override
                    public void onChange(DataSnapshot dataSnapshot){
                        if(dataSnapshot.getChildrenCount() > 1){
                            defUid uid = get(ArgumentsKey.UID_UUID);
DebugException.start().log(QueryException.UID_DUPLICATE.make("uuid is" + uid.toHexString())).end();
                        } else {
                            if(dataSnapshot.getChildrenCount() == 0){
                                queryFailed(QueryException.NO_ITEM_TO_UPDATE);
                            } else {
                                dataSnapshot = dataSnapshot.getChildren().iterator().next();
                                String key = dataSnapshot.getKey();
                                put(ArgumentsKey.PUSH_KEY_STRING, key);
                                next();
                            }
                        }
                    }
                    @Override
                    public void onCancel(DatabaseError databaseError){
                        queryFailed(QueryException.DATABASE_ERROR, databaseError.toException());
                    }
                };
                ref.childEntry().orderByChild(uidKey()).equalTo(uid.toHexString()).addListenerForSingleValueEvent(postListener);
            }
        }.name("check_if_exist_item"));
        add(new Action(){
            @Override
            public void runSafe(){
                if(isCanceled()){
                    return;
                }
                ContentValuesW contentValues = get(ArgumentsKey.CONTENT_VALUES);
                String pushKey = get(ArgumentsKey.PUSH_KEY_STRING);
                Map<String, Object> data = ref.toMap(pushKey, contentValues);
                ref.updateChildren(data, new FirebaseCompletionListenerW(me()){
                    @Override
                    public void onDone(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference){
                        ref.bufferMap.clear();
                        if(databaseError != null){
                            queryFailed(QueryException.DATABASE_ERROR, databaseError.toException());
                        } else {
                            ITEM dataToReturn = get(ArgumentsKey.ITEM);
                            ref.post(Event.UPDATE, dataToReturn);
                            queryCompleted(dataToReturn);
                        }
                    }
                });
            }
        }.name("update_Item"));
        releaseLock();
    }

}

private static class QueryUpdateList<ITEM extends ItemBase<ITEM>> extends QueryWithLock<ITEM>{
    public QueryUpdateList(fbTable<ITEM> owner){
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
                FireBaseValueEventListenerW postListener = new FireBaseValueEventListenerW(me()){
                    @Override
                    public void onChange(DataSnapshot dataSnapshot){
                        Map<defUid, ContentValuesW> items = get(ArgumentsKey.CONTENT_VALUES_MAP);
                        List<defUid> uidToUpdateList = new ArrayList<>(items.keySet());
                        if(uidToUpdateList.size() <= 0){
                            queryFailed(QueryException.NO_ITEM_TO_UPDATE);
                            return;
                        }
                        Map<defUid, String> pushKeyList = new ArrayMap<>();
                        for(DataSnapshot entry: dataSnapshot.getChildren()){
                            String pushKey = entry.getKey();
                            defUid uid = ref.getUID(entry);
                            if(pushKeyList.get(uid) != null){
DebugException.start().log(QueryException.UID_DUPLICATE.make("uuid is" + uid.toHexString())).end();
                            } else {
                                pushKeyList.put(uid, pushKey);
                            }
                        }
                        uidToUpdateList.retainAll(pushKeyList.keySet());
                        if(uidToUpdateList.size() <= 0){
                            queryFailed(QueryException.NO_ITEM_TO_UPDATE);
                            return;
                        }
                        put(ArgumentsKey.PUSH_KEY_MAP, pushKeyList);
                        put(ArgumentsKey.UID_UUID_LIST, uidToUpdateList);
                        next();
                    }

                    @Override
                    public void onCancel(DatabaseError databaseError){
                        queryFailed(QueryException.DATABASE_ERROR, databaseError.toException());
                    }
                };
                Map<defUid, ContentValuesW> items = get(ArgumentsKey.CONTENT_VALUES_MAP);
                List<defUid> uidToUpdateList = new ArrayList<>(items.keySet());
                Extrema.Value<defUid> extrema = Extrema.find(uidToUpdateList);
                ref.childInfoIndex().orderByChild(uidKey()).startAt(extrema.min.toHexString()).endAt(extrema.max.toHexString()).addListenerForSingleValueEvent(postListener);
            }
        }.name("check_if_exist_items"));
        add(new Action(){
            @Override
            public void runSafe(){
                if(isCanceled()){
                    return;
                }
                List<ITEM> itemToUpdateList = new ArrayList<>();
                List<defUid> uidToUpdateList = get(ArgumentsKey.UID_UUID_LIST);
                Map<defUid, ContentValuesW> contentValuesMap = get(ArgumentsKey.CONTENT_VALUES_MAP);
                Map<defUid, ITEM> itemsMap = get(ArgumentsKey.ITEM_MAP);
                Map<defUid, String> pushKeyList = get(ArgumentsKey.PUSH_KEY_MAP);
                Map<String, Object> data = ref.bufferMap;
                for(defUid uid: uidToUpdateList){
                    ContentValuesW contentValues = contentValuesMap.get(uid);
                    itemToUpdateList.add(itemsMap.get(uid));
                    String pushKey = pushKeyList.get(uid);
                    ref.toMap(pushKey, contentValues);
                }
                ref.updateChildren(data, new FirebaseCompletionListenerW(me()){
                    @Override
                    public void onDone(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference){
                        ref.bufferMap.clear();
                        if(databaseError != null){
                            queryFailed(QueryException.DATABASE_ERROR, databaseError.toException());
                        } else {
                            ref.post(Event.UPDATE_LIST, itemToUpdateList);
                            queryCompleted(itemToUpdateList);
                        }
                    }
                });
            }
        }.name("update_items"));
        releaseLock();
    }

}

private static class QueryRemove<ITEM extends ItemBase<ITEM>> extends QueryWithLock<ITEM>{
    public QueryRemove(fbTable<ITEM> owner){
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
                Integer index = get(ArgumentsKey.INDEX_INTEGER);
                FireBaseValueEventListenerW postListener = new FireBaseValueEventListenerW(me()){
                    @Override
                    public void onChange(DataSnapshot dataSnapshot){
                        if(dataSnapshot.getChildrenCount() == 0){
                            queryFailed(QueryException.NO_ITEM_TO_REMOVE);
                        } else {
                            DataSnapshot entry = dataSnapshot.getChildren().iterator().next();
                            defUid uid = get(ArgumentsKey.UID_UUID);
                            Integer index = get(ArgumentsKey.INDEX_INTEGER);
                            if(uid == null){
                                uid = ref.getUID(entry);
                                put(ArgumentsKey.UID_UUID, uid);
                            }
                            if(index == null){
                                index = entry.child(ReferenceInfo.Index.ref).getValue(Integer.class);
                                put(ArgumentsKey.INDEX_INTEGER, index);
                            }
                            if(dataSnapshot.getChildrenCount() > 1){
DebugException.start().log(QueryException.UID_DUPLICATE.make("uuid " + "is" + uid)).end();
                            }
                            String pushKey = entry.getKey();
                            put(ArgumentsKey.PUSH_KEY_STRING, pushKey);
                            next();
                        }
                    }

                    @Override
                    public void onCancel(DatabaseError databaseError){
                        queryFailed(QueryException.DATABASE_ERROR, databaseError.toException());
                    }
                };
                if(uid != null){
                    ref.childInfoIndex().orderByChild(uidKey()).equalTo(uid.toHexString()).addListenerForSingleValueEvent(postListener);
                } else {
                    if(index != null){
                        ref.childInfoIndex().orderByChild(ReferenceInfo.Index.ref).equalTo(index).addListenerForSingleValueEvent(postListener);
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
                String pushKey = get(ArgumentsKey.PUSH_KEY_STRING);
                FireBaseValueEventListenerW postListener = new FireBaseValueEventListenerW(me()){
                    @Override
                    public void onChange(DataSnapshot dataSnapshot){
                        ITEM item = ref.toItem(dataSnapshot);
                        put(ArgumentsKey.ITEM, item);
                        next();
                    }

                    @Override
                    public void onCancel(DatabaseError databaseError){
                        queryFailed(QueryException.DATABASE_ERROR, databaseError.toException());
                    }
                };
                ref.childEntry().child(pushKey).addListenerForSingleValueEvent(postListener);
            }
        }.name("get_item"));
        add(new Action(){
            @Override
            public void runSafe(){
                if(isCanceled()){
                    return;
                }
                Integer index = get(ArgumentsKey.INDEX_INTEGER);
                FireBaseValueEventListenerW postListener = new FireBaseValueEventListenerW(me()){
                    @Override
                    public void onChange(DataSnapshot dataSnapshot){
                        Map<String, Object> data = ref.bufferMap;
                        for(DataSnapshot item: dataSnapshot.getChildren()){
                            String pushKey = item.getKey();
                            int index = item.child(ReferenceInfo.Index.ref).getValue(Integer.class);
                            data.put(ref.getRefInfo().refIndex.newEntry(pushKey).ref(ReferenceInfo.Index.ref), index - 1);
                        }
                        next();
                    }

                    @Override
                    public void onCancel(DatabaseError databaseError){
                        queryFailed(QueryException.DATABASE_ERROR, databaseError.toException());
                    }
                };
                ref.childInfoIndex().orderByChild(ReferenceInfo.Index.ref).startAt(index + 1).addListenerForSingleValueEvent(postListener);
            }
        }.name("re_order_index"));
        add(new Action(){
            @Override
            public void runSafe(){
                if(isCanceled()){
                    return;
                }
                FireBaseValueEventListenerW postListener = new FireBaseValueEventListenerW(me()){
                    @Override
                    public void onChange(DataSnapshot dataSnapshot){
                        Integer count = dataSnapshot.getValue(Integer.class);
                        if(count == null){
DebugException.start().log(QueryException.COUNT_INCORRECT.make("size is" + " null " + "instead" + " of >= " + "1")).end();
                            count = 1;
                        }
                        put(ArgumentsKey.COUNT_INTEGER, count);
                        next();
                    }

                    @Override
                    public void onCancel(DatabaseError databaseError){
                        queryFailed(QueryException.DATABASE_ERROR, databaseError.toException());
                    }
                };
                ref.childSize().addListenerForSingleValueEvent(postListener);
            }
        }.name("get_count_items"));
        add(new Action(){
            @Override
            public void runSafe(){
                if(isCanceled()){
                    return;
                }
                Map<String, Object> data = ref.bufferMap;
                String pushKey = get(ArgumentsKey.PUSH_KEY_STRING);
                Integer count = get(ArgumentsKey.COUNT_INTEGER);
                data.put(ref.getRefEntry().newEntry(pushKey).root(), null);
                data.put(ref.getRefInfo().refIndex.newEntry(pushKey).root(), null);
                data.put(ref.getRefInfo().refSize.root(), count - 1);
                ref.updateChildren(data, new FirebaseCompletionListenerW(me()){
                    @Override
                    public void onDone(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference){
                        ref.bufferMap.clear();
                        if(databaseError != null){
                            queryFailed(QueryException.DATABASE_ERROR, databaseError.toException());
                        } else {
                            ITEM dataToReturn = get(ArgumentsKey.ITEM);
                            ref.post(Event.REMOVE, dataToReturn);
                            queryCompleted(dataToReturn);
                        }
                    }
                });
            }
        }.name("remove_item"));
        releaseLock();
    }

}

private static class QueryRemoveList<ITEM extends ItemBase<ITEM>> extends QueryWithLock<ITEM>{
    public QueryRemoveList(fbTable<ITEM> owner){
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
                FireBaseValueEventListenerW postListener = new FireBaseValueEventListenerW(me()){
                    @Override
                    public void onChange(DataSnapshot dataSnapshot){
                        List<defUid> uidToRemoveList = get(ArgumentsKey.UID_UUID_LIST);
                        Map<defUid, String> pushKeyList = new ArrayMap<>();
                        Map<defUid, Integer> indexList = new ArrayMap<>();
                        for(DataSnapshot entry: dataSnapshot.getChildren()){
                            String pushKey = entry.getKey();
                            Integer index = entry.child(ReferenceInfo.Index.ref).getValue(Integer.class);
                            defUid uid = ref.getUID(entry);
                            if(pushKeyList.get(uid) != null){
DebugException.start().log(QueryException.UID_DUPLICATE.make("uuid is " + uid.toHexString())).end();
                            } else {
                                pushKeyList.put(uid, pushKey);
                                indexList.put(uid, index);
                            }
                        }
                        uidToRemoveList.retainAll(pushKeyList.keySet());
                        if(uidToRemoveList.size() <= 0){
                            queryFailed(QueryException.NO_ITEM_TO_REMOVE);
                            return;
                        }
                        put(ArgumentsKey.PUSH_KEY_MAP, pushKeyList);
                        put(ArgumentsKey.INDEX_MAP, indexList);
                        put(ArgumentsKey.UID_UUID_LIST, uidToRemoveList);
                        next();
                    }

                    @Override
                    public void onCancel(DatabaseError databaseError){
                        queryFailed(QueryException.DATABASE_ERROR, databaseError.toException());
                    }
                };
                List<defUid> uidToRemoveList = get(ArgumentsKey.UID_UUID_LIST);
                if(uidToRemoveList.size() <= 0){
                    queryFailed(QueryException.NO_ITEM_TO_REMOVE);
                    return;
                }
                Extrema.Value<defUid> extrema = Extrema.find(uidToRemoveList);
                ref.childInfoIndex().orderByChild(uidKey()).startAt(extrema.min.toHexString()).endAt(extrema.max.toHexString()).addListenerForSingleValueEvent(postListener);
            }
        }.name("check_if_exist_items"));
        add(new Action(){
            @Override
            public void runSafe(){
                if(isCanceled()){
                    return;
                }
                FireBaseValueEventListenerW postListener = new FireBaseValueEventListenerW(me()){
                    @Override
                    public void onChange(DataSnapshot dataSnapshot){
                        if(dataSnapshot.getChildrenCount() == 0){
                            queryFailed(QueryException.NO_ITEM_TO_REMOVE);
                        } else {
                            List<ITEM> items = new LinkedList<>();
                            List<defUid> uidToRemoveList = get(ArgumentsKey.UID_UUID_LIST);
                            List<defUid> uidToRemoveFound = new ArrayList<>();
                            for(DataSnapshot entry: dataSnapshot.getChildren()){
                                defUid uid = ref.getUID(entry);
                                if(uidToRemoveList.contains(uid)){
                                    uidToRemoveFound.add(uid);
                                } else {
                                    continue;
                                }
                                ITEM item = ref.toItem(entry);
                                items.add(item);
                            }
                            if(uidToRemoveList.size() > uidToRemoveFound.size()){
DebugException.start().log(QueryException.DATA_IS_MISSING.make()).end();
                                if(uidToRemoveFound.size() <= 0){
                                    queryFailed(QueryException.NO_ITEM_TO_REMOVE);
                                    return;
                                }
                                put(ArgumentsKey.UID_UUID_LIST, uidToRemoveFound);
                            }
                            put(ArgumentsKey.ITEM_LIST, items);
                            next();
                        }
                    }

                    @Override
                    public void onCancel(DatabaseError databaseError){
                        queryFailed(QueryException.DATABASE_ERROR, databaseError.toException());
                    }
                };
                List<defUid> uidToRemoveList = get(ArgumentsKey.UID_UUID_LIST);
                Extrema.Value<defUid> extrema = Extrema.find(uidToRemoveList);
                ref.childEntry().orderByChild(uidKey()).startAt(extrema.min.toHexString()).endAt(extrema.max.toHexString()).addListenerForSingleValueEvent(postListener);
            }
        }.name("select_items"));
        add(new Action(){
            @Override
            public void runSafe(){
                if(isCanceled()){
                    return;
                }
                FireBaseValueEventListenerW postListener = new FireBaseValueEventListenerW(me()){
                    @Override
                    public void onChange(DataSnapshot dataSnapshot){
                        Map<String, Object> data = ref.bufferMap;
                        List<defUid> uidToRemoveList = get(ArgumentsKey.UID_UUID_LIST);
                        int offset = 1;
                        for(DataSnapshot entry: dataSnapshot.getChildren()){
                            String pushKey = entry.getKey();
                            defUid uid = ref.getUID(entry);
                            if(uidToRemoveList.contains(uid)){
                                offset++;
                            } else {
                                int index = entry.child(ReferenceInfo.Index.ref).getValue(Integer.class);
                                data.put(ref.getRefInfo().refIndex.newEntry(pushKey).ref(ReferenceInfo.Index.ref), index - offset);
                            }
                        }
                        next();
                    }

                    @Override
                    public void onCancel(DatabaseError databaseError){
                        queryFailed(QueryException.DATABASE_ERROR, databaseError.toException());
                    }
                };
                List<defUid> uidToRemoveList = get(ArgumentsKey.UID_UUID_LIST);
                Map<defUid, Integer> indexes = get(ArgumentsKey.INDEX_MAP);
                Extrema.Value<defUid> extrema = Extrema.find(uidToRemoveList);
                ref.childInfoIndex().orderByChild(ReferenceInfo.Index.ref).startAt(indexes.get(extrema.min) + 1).addListenerForSingleValueEvent(postListener);
            }
        }.name("re_order_index"));
        add(new Action(){
            @Override
            public void runSafe(){
                if(isCanceled()){
                    return;
                }
                FireBaseValueEventListenerW postListener = new FireBaseValueEventListenerW(me()){
                    @Override
                    public void onChange(DataSnapshot dataSnapshot){
                        List<defUid> uidToRemoveList = get(ArgumentsKey.UID_UUID_LIST);
                        Integer count = dataSnapshot.getValue(Integer.class);
                        if(count == null){
DebugException.start().log(QueryException.COUNT_INCORRECT.make("size is" + " null " + "instead" + " of " + uidToRemoveList.size() + " minimum")).end();
                            count = uidToRemoveList.size();
                        } else {
                            if(count < uidToRemoveList.size()){
DebugException.start().log(QueryException.COUNT_INCORRECT.make("size is" + " " + count + " instead of " + uidToRemoveList.size() + " minimum")).end();
                                count = uidToRemoveList.size();
                            }
                        }
                        put(ArgumentsKey.COUNT_INTEGER, count);
                        next();
                    }

                    @Override
                    public void onCancel(DatabaseError databaseError){
                        queryFailed(QueryException.DATABASE_ERROR, databaseError.toException());
                    }
                };
                ref.childSize().addListenerForSingleValueEvent(postListener);
            }
        }.name("get_count_items"));
        add(new Action(){
            @Override
            public void runSafe(){
                if(isCanceled()){
                    return;
                }
                Map<defUid, String> pushKeyList = get(ArgumentsKey.PUSH_KEY_MAP);
                Map<String, Object> data = ref.bufferMap;
                List<defUid> uidToRemoveList = get(ArgumentsKey.UID_UUID_LIST);
                Integer count = get(ArgumentsKey.COUNT_INTEGER);
                for(defUid uid: uidToRemoveList){
                    String pushKey = pushKeyList.get(uid);
                    data.put(ref.getRefEntry().newEntry(pushKey).root(), null);
                    data.put(ref.getRefInfo().refIndex.newEntry(pushKey).root(), null);
                }
                data.put(ref.getRefInfo().refSize.root(), count - uidToRemoveList.size());
                ref.updateChildren(data, new FirebaseCompletionListenerW(me()){
                    @Override
                    public void onDone(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference){
                        ref.bufferMap.clear();
                        if(databaseError != null){
                            queryFailed(QueryException.DATABASE_ERROR, databaseError.toException());
                        } else {
                            List<ITEM> dataToReturn = get(ArgumentsKey.ITEM);
                            ref.post(Event.REMOVE_LIST, dataToReturn);
                            queryCompleted(dataToReturn);
                        }
                    }
                });
            }
        }.name("remove_items"));
        releaseLock();
    }

}

private static class QueryClear<ITEM extends ItemBase<ITEM>> extends QueryWithLock<ITEM>{
    public QueryClear(fbTable<ITEM> owner){
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
                Map<String, Object> data = ref.bufferMap;
                data.put(ref.getRefEntry().root, null);
                data.put(ref.getRefInfo().refIndex.root(), null);
                data.put(ref.getRefInfo().refSize.root(), null);
                ref.updateChildren(data, new FirebaseCompletionListenerW(me()){
                    @Override
                    public void onDone(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference){
                        ref.bufferMap.clear();
                        if(databaseError == null){
                            ref.post(CLEAR, null);
                            queryCompleted(null);
                        } else {
                            queryFailed(QueryException.DATABASE_ERROR, databaseError.toException());
                        }
                    }
                });
            }
        }.name("clear"));
        releaseLock();
    }

}

private static class QueryGetWithIndex<ITEM extends ItemBase<ITEM>> extends QueryWithLock<ITEM>{
    public QueryGetWithIndex(fbTable<ITEM> owner){
        super(owner);
    }
    @Override
    public void onCreate(){
        add(new Action(){
            @Override
            public void runSafe(){
                if(isCanceled()){
                    return;
                }
                Integer index = get(ArgumentsKey.INDEX_INTEGER);
                FireBaseValueEventListenerW postListener = new FireBaseValueEventListenerW(me()){
                    @Override
                    public void onChange(DataSnapshot dataSnapshot){
                        if(dataSnapshot.getChildrenCount() == 0){
                            queryCompleted(null);
                        } else {
                            DataSnapshot entry = dataSnapshot.getChildren().iterator().next();
                            defUid uid = ref.getUID(entry);
                            if(dataSnapshot.getChildrenCount() > 1){
DebugException.start().log(QueryException.UID_DUPLICATE.make("uuid is" + uid.toHexString())).end();
                            }
                            put(ArgumentsKey.UID_UUID, uid);
                            String pushKey = entry.getKey();
                            put(ArgumentsKey.PUSH_KEY_STRING, pushKey);
                            next();
                        }
                    }

                    @Override
                    public void onCancel(DatabaseError databaseError){
                        queryFailed(QueryException.DATABASE_ERROR, databaseError.toException());
                    }
                };
                ref.childInfoIndex().orderByChild(ReferenceInfo.Index.ref).equalTo(index).addListenerForSingleValueEvent(postListener);
            }
        }.name("get_uid_item"));
        add(new Action(){
            @Override
            public void runSafe(){
                if(isCanceled()){
                    return;
                }
                String pushKey = get(ArgumentsKey.PUSH_KEY_STRING);
                FireBaseValueEventListenerW postListener = new FireBaseValueEventListenerW(me()){
                    @Override
                    public void onChange(DataSnapshot dataSnapshot){
                        ITEM item = ref.toItem(dataSnapshot);
                        queryCompleted(item);
                    }

                    @Override
                    public void onCancel(DatabaseError databaseError){
                        queryFailed(QueryException.DATABASE_ERROR, databaseError.toException());
                    }
                };
                ref.childEntry().child(pushKey).addListenerForSingleValueEvent(postListener);
            }
        }.name("get_item"));
    }

}

private static class QueryGetWithUID<ITEM extends ItemBase<ITEM>> extends Query<ITEM>{
    public QueryGetWithUID(fbTable<ITEM> owner){
        super(owner);
    }
    @Override
    public void onCreate(){
        add(new Action(){
            @Override
            public void runSafe(){
                if(isCanceled()){
                    return;
                }
                defUid uid = get(ArgumentsKey.UID_UUID);
                FireBaseValueEventListenerW postListener = new FireBaseValueEventListenerW(me()){
                    @Override
                    public void onChange(DataSnapshot dataSnapshot){
                        if(dataSnapshot.getChildrenCount() == 0){
                            queryCompleted(null);
                        } else {
                            String key = dataSnapshot.getChildren().iterator().next().getKey();
                            ITEM item = ref.toItem(dataSnapshot.child(key));
                            queryCompleted(item);
                        }
                    }
                    @Override
                    public void onCancel(DatabaseError databaseError){
                        queryFailed(QueryException.DATABASE_ERROR, databaseError.toException());
                    }
                };
                ref.childEntry().orderByChild(uidKey()).equalTo(uid.toHexString()).addListenerForSingleValueEvent(postListener);
            }
        }.name("get_item"));
    }

}

private static class QuerySelect<ITEM extends ItemBase<ITEM>> extends Query<ITEM>{
    public QuerySelect(fbTable<ITEM> owner){
        super(owner);
    }
    @Override
    public void onCreate(){
        add(new Action(){
            @Override
            public void runSafe(){
                if(isCanceled()){
                    return;
                }
                defUid uid = get(ArgumentsKey.UID_UUID);
                List<defUid> uids = get(ArgumentsKey.UID_UUID_LIST);
                Integer index = get(ArgumentsKey.INDEX_INTEGER);
                Integer length = get(ArgumentsKey.LENGTH_INTEGER);
                if((uid != null) || (index == null) || (uids != null)){
                    next();
                    return;
                }
                FireBaseValueEventListenerW postListener = new FireBaseValueEventListenerW(me()){
                    @Override
                    public void onChange(DataSnapshot dataSnapshot){
                        if(dataSnapshot.getChildrenCount() <= 0){
                            queryCompleted(null);
                        } else {
                            String pushKey = dataSnapshot.getChildren().iterator().next().getKey();
                            put(ArgumentsKey.PUSH_KEY_STRING, pushKey);
                            defUid uid = ref.getUID(dataSnapshot.child(pushKey));
                            put(ArgumentsKey.UID_UUID, uid);
                            int indexWanted = get(ArgumentsKey.INDEX_INTEGER);
                            int indexFound = dataSnapshot.child(pushKey).child(ReferenceInfo.Index.ref).getValue(Integer.class);
                            int diff = indexFound - indexWanted;
                            int length = get(ArgumentsKey.LENGTH_INTEGER);
                            put(ArgumentsKey.LENGTH_INTEGER, length - diff);
                            next();
                        }
                    }

                    @Override
                    public void onCancel(DatabaseError databaseError){
                        queryFailed(QueryException.DATABASE_ERROR, databaseError.toException());
                    }
                };
                ref.childInfoIndex().orderByChild(ReferenceInfo.Index.ref).startAt(index).endAt(index + length - 1).limitToFirst(1).addListenerForSingleValueEvent(postListener);
            }
        }.name("get_index_items"));
        add(new Action(){
            @Override
            public void runSafe(){
                if(isCanceled()){
                    return;
                }
                String pushKey = get(ArgumentsKey.PUSH_KEY_STRING);
                defUid uid = get(ArgumentsKey.UID_UUID);
                defUid uidEnd = get(ArgumentsKey.UID_UUID_LAST);
                List<defUid> uids = get(ArgumentsKey.UID_UUID_LIST);
                Integer length = get(ArgumentsKey.LENGTH_INTEGER);
                FireBaseValueEventListenerW postListener = new FireBaseValueEventListenerW(me()){
                    @Override
                    public void onChange(DataSnapshot dataSnapshot){
                        if(dataSnapshot.getChildrenCount() == 0){
                            queryCompleted(null);
                        } else {
                            List<ITEM> items = new LinkedList<>();
                            List<defUid> uids = get(ArgumentsKey.UID_UUID_LIST);
                            for(DataSnapshot entry: dataSnapshot.getChildren()){
                                if(uids != null){
                                    defUid uid = ref.getUID(entry);
                                    if(uids.contains(uid)){
                                        uids.remove(uid);
                                    } else {
                                        continue;
                                    }
                                }
                                ITEM item = ref.toItem(entry);
                                items.add(item);
                            }
                            queryCompleted(items);
                        }
                    }

                    @Override
                    public void onCancel(DatabaseError databaseError){
                        queryFailed(QueryException.DATABASE_ERROR, databaseError.toException());
                    }
                };
                if((uid != null) && (uidEnd != null)){
                    ref.childEntry().orderByChild(uidKey()).startAt(uid.toHexString()).endAt(uidEnd.toHexString()).addListenerForSingleValueEvent(postListener);
                } else {
                    if(pushKey != null){
                        ref.childEntry().orderByKey().startAt(pushKey).limitToFirst(length).addListenerForSingleValueEvent(postListener);
                    } else {
                        if((uid != null) && (length != null)){
                            ref.childEntry().orderByChild(uidKey()).startAt(uid.toHexString()).limitToFirst(length).addListenerForSingleValueEvent(postListener);
                        } else {
                            if(uids != null){
                                Extrema.Value<defUid> extrema = Extrema.find(uids);
                                ref.childEntry().orderByChild(uidKey()).startAt(extrema.min.toHexString()).endAt(extrema.max.toHexString()).addListenerForSingleValueEvent(postListener);
                            } else {
                                ref.childEntry().orderByChild(uidKey()).startAt(null, uidKey()).addListenerForSingleValueEvent(postListener);
                            }
                        }
                    }
                }
            }
        }.name("select_items"));
    }

}

private static class QuerySize<ITEM extends ItemBase<ITEM>> extends Query<ITEM>{
    public QuerySize(fbTable<ITEM> owner){
        super(owner);
    }
    @Override
    public void onCreate(){
        add(new Action(){
            @Override
            public void runSafe(){
                if(isCanceled()){
                    return;
                }
                FireBaseValueEventListenerW postListener = new FireBaseValueEventListenerW(me()){
                    @Override
                    public void onChange(DataSnapshot dataSnapshot){
                        Integer count = dataSnapshot.getValue(Integer.class);
                        if(count == null){
                            count = 0;
                        }
                        queryCompleted(count);
                    }

                    @Override
                    public void onCancel(DatabaseError databaseError){
                        queryFailed(QueryException.DATABASE_ERROR, databaseError.toException());
                    }
                };
                ref.childSize().addListenerForSingleValueEvent(postListener);
            }
        }.name("get_count_items"));
    }

}

private static class QueryIndexOf<ITEM extends ItemBase<ITEM>> extends Query<ITEM>{
    public QueryIndexOf(fbTable<ITEM> owner){
        super(owner);
    }
    @Override
    public void onCreate(){
        add(new Action(){
            @Override
            public void runSafe(){
                if(isCanceled()){
                    return;
                }
                defUid uid = get(ArgumentsKey.UID_UUID);
                FireBaseValueEventListenerW postListener = new FireBaseValueEventListenerW(me()){
                    @Override
                    public void onChange(DataSnapshot dataSnapshot){
                        defUid uid = get(ArgumentsKey.UID_UUID);
                        if(dataSnapshot.getChildrenCount() > 1){
DebugException.start().log(QueryException.UID_DUPLICATE.make("uuid is " + uid)).end();
                        }
                        Integer index = null;
                        if(dataSnapshot.getChildrenCount() > 0){
                            String pushKey = dataSnapshot.getChildren().iterator().next().getKey();
                            index = dataSnapshot.child(pushKey).child(ReferenceInfo.Index.ref).getValue(Integer.class);
                        }
                        queryCompleted(index);
                    }

                    @Override
                    public void onCancel(DatabaseError databaseError){
                        queryFailed(QueryException.DATABASE_ERROR, databaseError.toException());
                    }
                };
                ref.childInfoIndex().orderByChild(uidKey()).equalTo(uid.toHexString()).addListenerForSingleValueEvent(postListener);
            }
        }.name("index_of_item"));
    }

}

private static class QueryFirstIndex<ITEM extends ItemBase<ITEM>> extends Query<ITEM>{
    public QueryFirstIndex(fbTable<ITEM> owner){
        super(owner);
    }
    @Override
    public void onCreate(){
        add(new Action(){
            @Override
            public void runSafe(){
                if(isCanceled()){
                    return;
                }
                FireBaseValueEventListenerW postListener = new FireBaseValueEventListenerW(me()){
                    @Override
                    public void onChange(@NonNull DataSnapshot dataSnapshot){
                        Integer index = null;
                        if(dataSnapshot.getChildrenCount() > 0){
                            String pushKey = dataSnapshot.getChildren().iterator().next().getKey();
                            index = dataSnapshot.child(pushKey).child(ReferenceInfo.Index.ref).getValue(Integer.class);
                        }
                        queryCompleted(index);
                    }

                    @Override
                    public void onCancel(DatabaseError databaseError){
                        queryFailed(QueryException.DATABASE_ERROR, databaseError.toException());
                    }
                };
                ref.childInfoIndex().orderByChild(ReferenceInfo.Index.ref).limitToFirst(1).addListenerForSingleValueEvent(postListener);
            }
        }.name("get_first_index"));
    }

}

private static class QueryLastIndex<ITEM extends ItemBase<ITEM>> extends Query<ITEM>{
    public QueryLastIndex(fbTable<ITEM> owner){
        super(owner);
    }
    @Override
    public void onCreate(){
        add(new Action(){
            @Override
            public void runSafe(){
                if(isCanceled()){
                    return;
                }
                FireBaseValueEventListenerW postListener = new FireBaseValueEventListenerW(me()){
                    @Override
                    public void onChange(DataSnapshot dataSnapshot){
                        Integer index = null;
                        if(dataSnapshot.getChildrenCount() > 0){
                            String pushKey = dataSnapshot.getChildren().iterator().next().getKey();
                            index = dataSnapshot.child(pushKey).child(ReferenceInfo.Index.ref).getValue(Integer.class);
                        }
                        queryCompleted(index);
                    }

                    @Override
                    public void onCancel(DatabaseError databaseError){
                        queryFailed(QueryException.DATABASE_ERROR, databaseError.toException());
                    }
                };
                ref.childInfoIndex().orderByChild(ReferenceInfo.Index.ref).limitToLast(1).addListenerForSingleValueEvent(postListener);
            }
        }.name("get_last_index"));
    }

}

public class Ref implements defProviderAsync<ITEM>{
    private fbTableLockHelper threadLock = null;
    private boolean autoLock;
    private Map<String, Object> bufferMap;

    protected Ref(){
        try{
DebugTrack.start().create(this).end();
            autoLock = true;
            bufferMap = new ArrayMap<>();
        } catch(java.lang.Throwable e){

DebugException.start().log(e).end();

        }
    }

    public Class<ITEM> getType(){
        return me().getType();
    }

    public dbTableDefinition.Ref getTableDefinition(){
        return me().getTableDefinition();
    }

    public String getName(){
        return me().getName();
    }

    public String getNameEncoded(){
        return me().getNameEncoded();
    }

    public ReferenceEntry getRefEntry(){
        return refEntry;
    }

    public ReferenceInfo getRefInfo(){
        return refInfo;
    }

    public DatabaseReference childEntry(){
        return fb.child(refEntry.root);
    }

    public DatabaseReference childSize(){
        return fb.child(refInfo.refSize.root());
    }

    public DatabaseReference childInfoIndex(){
        return fb.child(refInfo.refIndex.root());
    }

    public String generateKey(){
        return fb.generateKey(refEntry.root).getKey();
    }

    public void updateChildren(Map<String, Object> data, FirebaseCompletionListenerW listener){
        fb.updateChildren(data, listener);
    }

    public <GEN extends defUIDGenerator> GEN getUidGenerator(){
        return me().getUidGenerator();
    }

    public ContentValuesW toContentValues(ITEM item){
        return me().toContentValues(item);
    }

    public ITEM toItem(ContentValuesW contentValues){
        return me().toItem(contentValues);
    }

    public ITEM toItem(DataSnapshot dataSnapshot){
        return me().toItem(dataSnapshot);
    }

    public Map<String, Object> toMap(String pushKey, ContentValuesW contentValues){
        return me().toMap(bufferMap, pushKey, contentValues);
    }

    public <UID extends defUid> UID getUID(ContentValuesW contentValues){
        return me().getUID(contentValues);
    }

    public <UID extends defUid> UID getUID(DataSnapshot dataSnapshot){
        return me().getUID(dataSnapshot);
    }

    public <T> T getValue(dbField.Is field, ContentValuesW contentValues){
        return me().getValue(field, contentValues);
    }

    public <T> T getValue(dbField.Is field, ContentValuesW contentValues, Class<T> type){
        return me().getValue(field, contentValues, type);
    }

    public <T> T getValue(dbField.Is field, DataSnapshot dataSnapshot){
        return me().getValue(field, dataSnapshot);
    }

    public <T> T getValue(dbField.Is field, DataSnapshot dataSnapshot, Class<T> type){
        return me().getValue(field, dataSnapshot, type);
    }

    public Ref setAutoLock(boolean flag){
        autoLock = flag;
        return this;
    }

    public fbTableLock.Query getLockQuery(){
        return getLockQuery(Handler.PRIMARY());
    }
    public fbTableLock.Query getLockQuery(Handler handler){
        return me().getLock().newQuery(handler);
    }

    protected <Q extends Query> Q queriesObtain(Class<Q> type){
        return (Q)createQuery(type);
    }
    protected void queriesRelease(Query q){

    }

    public fbTable<ITEM>.Ref start(){
        me().start();
        return this;
    }
    public TaskState.Observable pause(boolean force){
        return me().pause(force);
    }

    final public TaskState.Observable acquireLock(Object inquirer){
        return acquireLock(inquirer, null, null, null);
    }
    final public TaskState.Observable acquireLock(Object inquirer, RunnableW runnableOnTimeOut){
        return acquireLock(inquirer, null, null, runnableOnTimeOut);
    }
    @Override
    final public TaskState.Observable acquireLock(Object inquirer, Long maxRetainTime_ms, Long acquireRequestMaxRetainTime_ms, RunnableW runnableOnTimeOut){

        if(autoLock){
DebugException.start().log("autoLock must be set to false if manuel lock is used").end();
        }
        if(threadLock != null){
DebugException.start().explode("threadLock not null, Owner:" + DebugTrack.getFullSimpleName(threadLock.getOwner())).end();
        }

        threadLock = new fbTableLockHelper(this, inquirer);
        return threadLock.acquireLock(maxRetainTime_ms, acquireRequestMaxRetainTime_ms, runnableOnTimeOut);
    }
    @Override
    final public TaskState.Observable releaseLock(Object inquirer){
        if(threadLock == null){

DebugException.start().log("threadLock is null").end();

            return TaskState.Complete();
        } else {
            fbTableLockHelper tmp = threadLock;
            threadLock = null;
            return tmp.releaseLock();
        }
    }

    public void post(Event event, Object object){
        ObservableEvent<Event, Object>.Access access = notifier.obtainAccess(this, event);
        access.setValue(ListOrObject.with(object));
    }

    public Notifier.Subscription observe(ObserverEvent<Event, ListOrObject<ITEM>> observer){
        return notifier.register(observer);
    }

    public void unObserve(Object owner){
        notifier.unregister(owner);
    }

    public void unObserveAll(){
        notifier.unregisterAll();
    }

    public Subscription observe(FireBaseChildEventListenerW listener){
        Subscription subscription = new Subscription(listener);
        listener.bind(subscription, childEntry());
        listener.addChildEvent();
        return subscription;
    }

    @Override
    public TaskValue<Integer>.Observable size(){
        TaskValue<Integer> task = new TaskValue<>();
        QuerySize<ITEM> query = queriesObtain(QuerySize.class);
        query.init(this, task);
        postQuery(query);
        return task.getObservable();
    }

    public TaskValue<ITEM>.Observable remove(ITEM item){
        return remove(item.getUid());
    }

    public TaskValue<ITEM>.Observable remove(defUid uid){
        TaskValue<ITEM> task = new TaskValue<>();
        QueryRemove<ITEM> query = queriesObtain(QueryRemove.class);
        query.init(this, task).put(ArgumentsKey.UID_UUID, uid);
        postQuery(query);
        return task.getObservable();
    }

    @Override
    public TaskValue<ITEM>.Observable remove(int index){
        TaskValue<ITEM> task = new TaskValue<>();
        QueryRemove<ITEM> query = queriesObtain(QueryRemove.class);
        query.init(this, task).put(ArgumentsKey.INDEX_INTEGER, index);
        postQuery(query);
        return task.getObservable();
    }

    public TaskValue<List<ITEM>>.Observable remove(List<ITEM> items){
        List<defUid> uids = new ArrayList<>();
        for(ITEM item: items){
            uids.add(item.getUid());
        }
        return removeUids(uids);
    }
    public TaskValue<List<ITEM>>.Observable removeUids(List<defUid> uids){
        TaskValue<List<ITEM>> task = new TaskValue<>();
        QueryRemoveList<ITEM> query = queriesObtain(QueryRemoveList.class);
        query.init(this, task).put(ArgumentsKey.UID_UUID_LIST, uids);
        postQuery(query);
        return task.getObservable();
    }

    public TaskValue<Void>.Observable clear(){
        TaskValue<Void> task = new TaskValue<>();
        QueryClear<ITEM> query = queriesObtain(QueryClear.class);
        query.init(this, task);
        postQuery(query);
        return task.getObservable();
    }

    public TaskValue<ITEM>.Observable insert(ITEM item){
        TaskValue<ITEM> task = new TaskValue<>();
        ContentValuesW contentValues = toContentValues(item);
        QueryInsert<ITEM> query = queriesObtain(QueryInsert.class);
        query.init(this, task).put(ArgumentsKey.ITEM, item);
        query.put(ArgumentsKey.CONTENT_VALUES, contentValues);
        query.put(ArgumentsKey.UID_UUID, getUID(contentValues));
        postQuery(query);
        return task.getObservable();

    }

    public TaskValue<List<ITEM>>.Observable insert(List<ITEM> items){
        TaskValue<List<ITEM>> task = new TaskValue<>();
        Map<defUid, ITEM> mapItem = new ArrayMap<>();
        Map<defUid, ContentValuesW> mapContentValues = new ArrayMap<>();
        for(ITEM item: items){
            ContentValuesW contentValues = toContentValues(item);
            defUid uid = getUID(contentValues);
            if(uid == null){
DebugException.start().log(QueryException.UID_NULL.make()).end();
            }
            mapContentValues.put(uid, contentValues);
            mapItem.put(uid, item);
        }
        QueryInsertList<ITEM> query = queriesObtain(QueryInsertList.class);
        query.init(this, task).put(ArgumentsKey.ITEM_MAP, mapItem);
        query.put(ArgumentsKey.CONTENT_VALUES_MAP, mapContentValues);
        postQuery(query);
        return task.getObservable();
    }

    public TaskValue<ITEM>.Observable update(ITEM item){
        TaskValue<ITEM> task = new TaskValue<>();
        ContentValuesW contentValues = toContentValues(item);
        QueryUpdate<ITEM> query = queriesObtain(QueryUpdate.class);
        query.init(this, task).put(ArgumentsKey.ITEM, item);
        query.put(ArgumentsKey.CONTENT_VALUES, contentValues);
        query.put(ArgumentsKey.UID_UUID, getUID(contentValues));
        postQuery(query);
        return task.getObservable();
    }

    public TaskValue<List<ITEM>>.Observable update(List<ITEM> items){
        TaskValue<List<ITEM>> task = new TaskValue<>();
        Map<defUid, ITEM> mapItem = new ArrayMap<>();
        Map<defUid, ContentValuesW> mapContentValues = new ArrayMap<>();
        for(ITEM item: items){
            ContentValuesW contentValues = toContentValues(item);
            defUid uid = getUID(contentValues);
            if(uid == null){
DebugException.start().log(QueryException.UID_NULL.make()).end();
            }
            mapContentValues.put(uid, contentValues);
            mapItem.put(uid, item);
        }
        QueryUpdateList<ITEM> query = queriesObtain(QueryUpdateList.class);
        query.init(this, task).put(ArgumentsKey.ITEM_MAP, mapItem);
        query.put(ArgumentsKey.CONTENT_VALUES_MAP, mapContentValues);
        postQuery(query);
        return task.getObservable();
    }

    public TaskValue<ITEM>.Observable get(defUid uid){
        TaskValue<ITEM> task = new TaskValue<>();
        QueryGetWithUID<ITEM> query = queriesObtain(QueryGetWithUID.class);
        query.init(this, task).put(ArgumentsKey.UID_UUID, uid);
        postQuery(query);
        return task.getObservable();
    }

    @Override
    public TaskValue<ITEM>.Observable get(int index){
        TaskValue<ITEM> task = new TaskValue<>();
        QueryGetWithIndex<ITEM> query = queriesObtain(QueryGetWithIndex.class);
        query.init(this, task).put(ArgumentsKey.INDEX_INTEGER, index);
        postQuery(query);
        return task.getObservable();
    }

    @Override
    public TaskValue<List<ITEM>>.Observable select(int index, int length){
        TaskValue<List<ITEM>> task = new TaskValue<>();
        QuerySelect<ITEM> query = queriesObtain(QuerySelect.class);
        query.init(this, task).put(ArgumentsKey.INDEX_INTEGER, index);
        query.put(ArgumentsKey.LENGTH_INTEGER, length);
        postQuery(query);
        return task.getObservable();
    }

    public TaskValue<List<ITEM>>.Observable select(defUid uid, int length){
        TaskValue<List<ITEM>> task = new TaskValue<>();
        QuerySelect<ITEM> query = queriesObtain(QuerySelect.class);
        query.init(this, task).put(ArgumentsKey.UID_UUID, uid);
        query.put(ArgumentsKey.LENGTH_INTEGER, length);
        postQuery(query);
        return task.getObservable();
    }

    public TaskValue<List<ITEM>>.Observable select(defUid uidStart, defUid uidEnd){
        TaskValue<List<ITEM>> task = new TaskValue<>();
        QuerySelect<ITEM> query = queriesObtain(QuerySelect.class);
        query.init(this, task).put(ArgumentsKey.UID_UUID, uidStart);
        query.put(ArgumentsKey.UID_UUID_LAST, uidEnd);
        postQuery(query);
        return task.getObservable();
    }

    public TaskValue<List<ITEM>>.Observable select(List<defUid> uids){
        TaskValue<List<ITEM>> task = new TaskValue<>();
        QuerySelect<ITEM> query = queriesObtain(QuerySelect.class);
        query.init(this, task).put(ArgumentsKey.UID_UUID_LIST, uids);
        postQuery(query);
        return task.getObservable();
    }

    public TaskValue<List<ITEM>>.Observable select(){
        TaskValue<List<ITEM>> task = new TaskValue<>();
        QuerySelect<ITEM> query = queriesObtain(QuerySelect.class);
        query.init(this, task);
        postQuery(query);
        return task.getObservable();
    }

    @Override
    public TaskValue<Integer>.Observable getFirstIndex(){
        TaskValue<Integer> task = new TaskValue<>();
        QueryFirstIndex<ITEM> query = queriesObtain(QueryFirstIndex.class);
        query.init(this, task);
        postQuery(query);
        return task.getObservable();
    }

    @Override
    public TaskValue<Integer>.Observable getLastIndex(){
        TaskValue<Integer> task = new TaskValue<>();
        QueryLastIndex<ITEM> query = queriesObtain(QueryLastIndex.class);
        query.init(this, task);
        postQuery(query);
        return task.getObservable();
    }

    @Override
    public TaskValue<Integer>.Observable indexOf(ITEM item){
        TaskValue<Integer> task = new TaskValue<>();
        QueryIndexOf<ITEM> query = queriesObtain(QueryIndexOf.class);
        query.init(this, task).put(ArgumentsKey.UID_UUID, item.getUid());
        postQuery(query);
        return task.getObservable();
    }

    @Override
    public TaskValue<ITEM>.Observable putToTrash(int index){

DebugException.start().notImplemented().end();

        return null;
    }

    @Override
    public TaskValue<ITEM>.Observable restoreFromTrash(int index, ITEM item){

DebugException.start().notImplemented().end();

        return null;
    }

    @Override
    public TaskState.Observable toDebugLog(){
        return toDebugLog(new Arguments<>());
    }

    private TaskState.Observable toDebugLog(Arguments<String> arguments){
        TaskState task = new TaskState();
        Integer index = arguments.getValue("index");
        if(index == null){
            index = 0;
            arguments.put("index", index);
        }
        select(index, TO_LOG_DELAY_LENGTH).observe(new ObserverValueE<List<ITEM>>(this){
            @Override
            public void onComplete(List<ITEM> items){
                if(items != null){
DebugLog.start().send(items).end();
                    Integer index = arguments.getValue("index");
                    arguments.put("index", index + items.size());
                    if(items.size() >= TO_LOG_DELAY_LENGTH){
                        toDebugLog(arguments);
                        return;
                    }
                }
                Integer index = arguments.getValue("index");
                if(index == 0){
DebugLog.start().send(me().getTableDefinition().getName() + " is null").end();
                }
                task.notifyComplete();
            }
        });
        return task.getObservable();
    }

    @Override
    protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
        super.finalize();
    }

}

}


