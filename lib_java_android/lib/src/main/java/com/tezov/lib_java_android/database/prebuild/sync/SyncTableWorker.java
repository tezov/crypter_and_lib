/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.database.prebuild.sync;

import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.tezov.lib_java_android.BuildConfig;
import com.tezov.lib_java.async.Handler;
import com.tezov.lib_java.async.notifier.Notifier;
import com.tezov.lib_java.async.notifier.observable.ObservableValue;
import com.tezov.lib_java.async.notifier.observer.event.ObserverEvent;
import com.tezov.lib_java.async.notifier.observer.value.ObserverValue;
import com.tezov.lib_java.async.notifier.task.TaskState;
import com.tezov.lib_java_android.database.ItemBase;
import com.tezov.lib_java_android.database.firebase.fbTable;
import com.tezov.lib_java_android.database.prebuild.sync.item.ItemSync;
import com.tezov.lib_java_android.database.prebuild.sync.item.ItemTransactionSync;
import com.tezov.lib_java_android.database.prebuild.sync.table.dbSyncTable;
import com.tezov.lib_java_android.database.prebuild.sync.table.dbTransactionSync;
import com.tezov.lib_java_android.database.prebuild.sync.table.fbSyncTable;
import com.tezov.lib_java_android.database.sqlLite.dbTable;
import com.tezov.lib_java.generator.uid.defUid;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.type.collection.ListOrObject;
import com.tezov.lib_java.type.runnable.RunnableW;
import com.tezov.lib_java_android.wrapperAnonymous.FireBaseChildEventListenerW;
import com.tezov.lib_java.util.UtilsString;

import static com.tezov.lib_java_android.database.prebuild.sync.item.ItemSync.Type.INSERT;
import static com.tezov.lib_java_android.database.prebuild.sync.item.ItemSync.Type.REMOVE;
import static com.tezov.lib_java_android.database.prebuild.sync.item.ItemSync.Type.UPDATE;
import static com.tezov.lib_java_android.database.prebuild.sync.item.ItemTransactionSync.Direction;

// NOW  rebuildRemoteSync download only with last timestamp...
//  another class to find and fix mistake
// IMPROVE execute as block insert, update
public class SyncTableWorker<ITEM extends ItemBase<ITEM>>{
private final Handler handler = Handler.LOW();
private final Notifier<Void> notifier;
private final dbTransactionSync.Ref dbTransaction;
private final dbSyncTable<ITEM>.Ref syncTableLocal;
private final fbSyncTable<ITEM>.Ref syncTableRemote;
private final boolean isStarted = false;
//private final Pool<Query> poolQueries;
//private final RunnableQueue<Query> pendingQueries;
private Notifier.Subscription localToRemoteSubscription = null;
private fbTable.Subscription remoteToLocalSubscription = null;
private String transactionName = null;

public SyncTableWorker(dbTransactionSync.Ref dbTransaction, dbSyncTable<ITEM>.Ref dbSync, fbSyncTable<ITEM>.Ref fbSync){
    this.notifier = new Notifier<>(new ObservableValue(), false);
    this.syncTableLocal = dbSync;
    this.syncTableRemote = fbSync;
    this.dbTransaction = dbTransaction;
//    if(!Pool.exist(Query.class)){
//        Pool.init(Query.class, new FunctionW<Class<Query>, Query>(){
//            @Override
//            public Query apply(Class<Query> com.tezov.lib.type){
//                return createQuery(com.tezov.lib.type);
//            }
//        });
//    }
//    poolQueries = Pool.pool(Query.class);
//    pendingQueries = new RunnableQueue<>(handler);
}

public SyncTableWorker me(){
    return this;
}

private void post(Event event){
    ObservableValue.Access access = notifier.obtainAccess(this, null);
    access.setValue(event);
}

public Notifier.Subscription observe(ObserverValue<Event> observer){
    return notifier.register(observer);
}

public void unObserve(Object owner){
    notifier.unregister(owner);
}

public void unObserveAll(){
    notifier.unregisterAll();
}

public <T extends dbSyncTable<ITEM>.Ref> T getSyncTableLocal(){
    return (T)syncTableLocal;
}

public boolean isSyncLocalToRemoteEnabled(){
    return localToRemoteSubscription != null;
}

public SyncTableWorker<ITEM> enableSyncLocalToRemote(boolean flag){
    if(flag && !isSyncLocalToRemoteEnabled()){
        localToRemoteSubscription = syncTableLocal.getTableSync().observe(new ObserverEvent<dbTable.Event.Is, ListOrObject<ITEM>>(this){
            @Override
            public void onComplete(dbTable.Event.Is event, ListOrObject<ITEM> items){
                handler.post(this, new RunnableW(){
                    @Override
                    public void runSafe(){
                        if(isSyncLocalToRemoteEnabled()){
                            pushLocalToRemote(event, items);
                        }
                    }
                });
            }
        });
    } else {
        if(!flag && isSyncLocalToRemoteEnabled()){
            localToRemoteSubscription.unsubscribe();
            localToRemoteSubscription = null;
        }
    }
    return this;
}

public <T extends fbSyncTable<ITEM>.Ref> T getSyncTableRemote(){
    return (T)syncTableRemote;
}

public boolean isSyncRemoteToLocalEnabled(){
    return remoteToLocalSubscription != null;
}

public SyncTableWorker<ITEM> enableSyncRemoteToLocal(boolean flag){
    if(flag && !isSyncRemoteToLocalEnabled()){
        remoteToLocalSubscription = syncTableRemote.observe(new FireBaseChildEventListenerW(){
            void pushRemoteToLocal(DataSnapshot dataSnapshot){
                if(isSubscribeNotValid()){
                    return;
                }
                handler.post(this, new RunnableW(){
                    @Override
                    public void runSafe(){
                        if(isSyncRemoteToLocalEnabled()){
                            me().pushRemoteToLocal(dataSnapshot);
                        }
                    }
                });
            }

            @Override
            public void onAdded(DataSnapshot dataSnapshot, String s){
                pushRemoteToLocal(dataSnapshot);
            }

            @Override
            public void onChanged(DataSnapshot dataSnapshot, String s){
                pushRemoteToLocal(dataSnapshot);
            }

            @Override
            public void onRemoved(@NonNull DataSnapshot dataSnapshot){
                pushRemoteToLocal(dataSnapshot);
            }
        });
    } else {
        if(!flag && isSyncRemoteToLocalEnabled()){
            remoteToLocalSubscription.unsubscribe();
            remoteToLocalSubscription = null;
        }
    }
    return this;
}

public boolean isEnabled(){
    return (localToRemoteSubscription != null) || (remoteToLocalSubscription != null);
}

public boolean isBusy(){
//    return pendingQueries.isBusy();
    return true;
}

public SyncTableWorker<ITEM> start(){
//    if(isStarted){
//        return this;
//    }
//    this.isStarted = true;
//    if(isSyncLocalToRemoteEnabled()){
//        rebuildQueries();
//    }
//    if(isSyncRemoteToLocalEnabled()){
//        rebuildRemoteSync();
//    }
//    if(!pendingQueries.isBusy()){
//        post(Event.STARTED);
//        if(!pendingQueries.isEmpty()){
//            nextQuery();
//        }
//    }
    return this;
}

public TaskState.Observable pause(boolean force){
    TaskState task = new TaskState();
//    if(!isStarted){
//        task.notifyComplete();
//        return task.getObservable();
//    }
//    if(!pendingQueries.isBusy()){
//        isStarted = false;
//        post(Event.PAUSED);
//        task.notifyComplete();
//        return task.getObservable();
//    }
//    RunnableTimeOut rt = new RunnableTimeOut(this, FB_TABLE_PAUSE_TIMEOUT_DELAY_ms){
//        @Override
//        public void onStart(){
//            isStarted = false;
//            if(!pendingQueries.isBusy()){
//                completed();
//            } else {
//                pendingQueries.setOnDone(new RunnableW(){
//                    @Override
//                    public void runSafe(){
//                        completed();
//                    }
//                });
//            }
//        }
//
//        @Override
//        public void onComplete(){
//            me().post(Event.PAUSED);
//            task.notifyComplete();
//        }
//
//        @Override
//        public void onTimeOut(){
//            pendingQueries.setOnDone(null);
//            Query query = pendingQueries.current();
//            if(query != null){
//                query.queryCanceled();
//            }
//            me().post(Event.PAUSED);
//            task.notifyException(new TimeoutException());
//        }
//    };
//    if(!force){
//        rt.start();
//    } else {
//        isStarted = false;
//        rt.onTimeOut();
//    }
    return task.getObservable();

}

public int size(){
//    return pendingQueries.size();
    return 0;
}

private String getTransactionName(){
    if(transactionName == null){
        String l = syncTableLocal.getTableSync().getTableDefinition().getName();
        String r = syncTableRemote.getTableSync().getTableDefinition().getName();
        if(l.equals(r)){
            transactionName = l;
        } else {
            transactionName = "L:" + l + ":R:" + r;
        }
    }
    return transactionName;
}

//public void rebuildQueries(){
//    handler.post(this, new RunnableW(){
//        @Override
//        public void runSafe(){
//            if(!isStarted){
//                return;
//            }
//            List<ItemTransactionSync> transactions;
//            if(isSyncLocalToRemoteEnabled() && isSyncRemoteToLocalEnabled()){ // IMPROVE with limit
//                transactions = dbTransaction.select(getTransactionName());
//            } else {
//                if(isSyncLocalToRemoteEnabled()){
//                    transactions = dbTransaction.select(getTransactionName(), REMOTE);
//                } else {
//                    if(isSyncRemoteToLocalEnabled()){
//                        transactions = dbTransaction.select(getTransactionName(), LOCAL);
//                    } else {
//                        transactions = null;
//                    }
//                }
//            }
//            if(transactions != null){
//                List<Query> queries = new ArrayList<>();
//                for(ItemTransactionSync transaction: transactions){
//                    if(!isStarted){
//                        return;
//                    }
//                    if(pendingQueries.contain(new PredicateW<Query>(){
//                        @Override
//                        public boolean test(Query query){
//                            ItemTransactionSync queryTransaction = (ItemTransactionSync)query.get(ArgumentsKey.TRANSACTION);
//                            return transaction.getUid().equals(queryTransaction.getUid());
//                        }
//                    })){
//                        continue;
//                    }
//                    Query query = null;
//                    if(!isStarted){
//                        return;
//                    }
//                    switch(transaction.direction){
//                        case LOCAL:
//                            query = rebuildRemoteToLocalQuery(transaction);
//                            break;
//                        case REMOTE:
//                            query = rebuildLocalToRemoteQuery(transaction);
//                            break;
//                        default:{
//
//                            DebugException.pop().produce("unknown direction " + transaction.direction.name()).explode().pop();
//
//
//                        }
//                    }
//                    if(query != null){
//                        queries.add(query);
//                    }
//                }
//                if(queries.size() > 0){
//                    for(Query q: queries){
//                        postQuery(q);
//                    }
//                }
//            }
//        }
//    });
//}

//private Query rebuildRemoteToLocalQuery(ItemTransactionSync transaction){
//    Query<ITEM> query;
//    switch(transaction.getType()){
//        case INSERT:{
//            query = poolQueriesObtain(QueryInsertLocal.class);
//        }
//        break;
//        case UPDATE:{
//            query = poolQueriesObtain(QueryUpdateLocal.class);
//        }
//        break;
//        case REMOVE:{
//            query = poolQueriesObtain(QueryRemoveLocal.class);
//        }
//        break;
//        default:{
//
//            DebugException.pop().produce("unknown type " + transaction.getType().name()).explode().pop();
//
//
//            return null;
//        }
//    }
//    query.init(this).put(ArgumentsKey.TRANSACTION, transaction);
//    return query;
//
//}

//private Query rebuildLocalToRemoteQuery(ItemTransactionSync transaction){
//    ITEM item = syncTableLocal.getTableSync().get(transaction.sync.getUid());
//    Query<ITEM> query;
//    switch(transaction.getType()){
//        case INSERT:{
//            query = poolQueriesObtain(QueryInsertRemote.class);
//        }
//        break;
//        case UPDATE:{
//            query = poolQueriesObtain(QueryUpdateRemote.class);
//        }
//        break;
//        case REMOVE:{
//            query = poolQueriesObtain(QueryRemoveRemote.class);
//        }
//        break;
//        default:{
//
//            DebugException.pop().produce("unknown type " + transaction.getType().name()).log().pop();
//
//
//            return null;
//        }
//
//    }
//    query.init(this).put(ArgumentsKey.TRANSACTION, transaction);
//    if(transaction.getType() == REMOVE){
//        return query;
//    } else {
//        if(item != null){
//            query.put(ArgumentsKey.ITEM, item);
//            return query;
//        } else {
//            return null;
//        }
//    }
//}

private void rebuildRemoteSync(){
//    handler.post(this, new RunnableW(){
//        @Override
//        public void runSafe(){
//            if(!isStarted){
//                return;
//            }
//            fbTableIterable<ItemSync> iterable = new fbTableIterable<>(syncTableRemote);
//            for(ItemSync itemSyncRemote: iterable){
//                ItemSync itemSyncLocal = syncTableLocal.get(itemSyncRemote.getUid());
//                if(((itemSyncLocal == null) && (itemSyncRemote.type != REMOVE)) || ((itemSyncLocal != null) && (itemSyncLocal.timestamp < itemSyncRemote.timestamp))){
//                    if(!pendingQueries.contain(new PredicateW<Query>(){
//                        @Override
//                        public boolean test(Query query){
//                            ItemTransactionSync queryTransaction = (ItemTransactionSync)query.get(ArgumentsKey.TRANSACTION);
//                            return itemSyncRemote.equals(queryTransaction.sync);
//                        }
//                    })){
//                        pushRemoteToLocal(itemSyncRemote);
//                    }
//                }
//            }
//        }
//    });
}

private void pushLocalToRemote(dbTable.Event.Is event, ListOrObject<ITEM> items){
    {
        if(items == null){
            return;
        }
        if(event == dbTable.Event.INSERT){
            pushLocalToRemote(items.get(), INSERT);
        } else {
            if(event == dbTable.Event.INSERT_LIST){
                for(ITEM item: items){
                    pushLocalToRemote(item, INSERT);
                }
            } else {
                if(event == dbTable.Event.UPDATE){
                    pushLocalToRemote(items.get(), UPDATE);
                } else {
                    if(event == dbTable.Event.UPDATE_LIST){
                        for(ITEM item: items){
                            pushLocalToRemote(item, UPDATE);
                        }
                    } else {
                        if(event == dbTable.Event.REMOVE){
                            pushLocalToRemote(items.get(), REMOVE);
                        } else {
                            if(event == dbTable.Event.REMOVE_LIST){
                                for(ITEM item: items){
                                    pushLocalToRemote(item, REMOVE);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private void pushLocalToRemote(ITEM item, ItemSync.Type type){
//    defUid uid = item.getUid();
//    if(isTransactionBackEvent(com.tezov.lib.type, LOCAL, uid)){
//        return;
//    }
//    ItemSync sync = null;
//    Query<ITEM> query = null;
//    switch(com.tezov.lib.type){
//        case INSERT:{
//            sync = obtain().initWith(com.tezov.lib.type, uid);
//            syncTableLocal.insert(sync);
//            query = poolQueriesObtain(QueryInsertRemote.class);
//        }
//        break;
//        case UPDATE:{
//            sync = obtain().initWith(com.tezov.lib.type, uid);
//            syncTableLocal.update(sync);
//            query = poolQueriesObtain(QueryUpdateRemote.class);
//        }
//        break;
//        case REMOVE:{
//            sync = syncTableLocal.remove(uid);
//            if(sync != null){
//                sync.updateWith(com.tezov.lib.type);
//                query = poolQueriesObtain(QueryRemoveRemote.class);
//            } else {
//                DebugException.pop().produce("sync is null").logHidden().pop();
//            }
//
//        }
//        break;
//        default:{
//
//            DebugException.pop().produce("unknown type " + com.tezov.lib.type.name()).log().pop();
//
//
//        }
//    }
//    ItemTransactionSync transaction = ItemTransactionSync.obtain().initWith(getTransactionName(), REMOTE, sync);
//    transaction = dbTransaction.insert(transaction);
//    query.init(this).put(ArgumentsKey.ITEM, item).put(ArgumentsKey.TRANSACTION, transaction);
//    postQuery(query);
}

private void pushRemoteToLocal(DataSnapshot dataSnapshot){
    pushRemoteToLocal(syncTableRemote.toItem(dataSnapshot));
}

private void pushRemoteToLocal(ItemSync sync){
//    if(isTransactionBackEvent(sync.com.tezov.lib.type, REMOTE, sync.getUid())){
//        return;
//    }
//    Query<ITEM> query = null;
//    switch(sync.com.tezov.lib.type){
//        case INSERT:{
//            query = poolQueriesObtain(QueryInsertLocal.class);
//        }
//        break;
//        case UPDATE:{
//            query = poolQueriesObtain(QueryUpdateLocal.class);
//        }
//        break;
//        case REMOVE:{
//            query = poolQueriesObtain(QueryRemoveLocal.class);
//        }
//        break;
//        default:{
//
//            DebugException.pop().produce("unknown type " + sync.com.tezov.lib.type.name()).log().pop();
//
//
//        }
//    }
//    ItemTransactionSync transaction = ItemTransactionSync.obtain().initWith(getTransactionName(), LOCAL, sync);
//    transaction = dbTransaction.insert(transaction);
//    query.init(this).put(ArgumentsKey.TRANSACTION, transaction);
//    postQuery(query);
}

//private <Q extends Query> Q poolQueriesObtain(Class<Q> com.tezov.lib.type){
//    return poolQueries.obtain(com.tezov.lib.type);
//}
//
//private void poolQueriesRelease(Query q){
//    poolQueries.release(q);
//}

//private Query createQuery(Class<Query> com.tezov.lib.type){
//    Query query = null;
//    if(com.tezov.lib.type.equals(QueryInsertRemote.class)){
//        query = new QueryInsertRemote();
//    } else {
//        if(com.tezov.lib.type.equals(QueryUpdateRemote.class)){
//            query = new QueryUpdateRemote();
//        } else {
//            if(com.tezov.lib.type.equals(QueryRemoveRemote.class)){
//                query = new QueryRemoveRemote();
//            } else {
//                if(com.tezov.lib.type.equals(QueryInsertLocal.class)){
//                    query = new QueryInsertLocal();
//                } else {
//                    if(com.tezov.lib.type.equals(QueryUpdateLocal.class)){
//                        query = new QueryUpdateLocal();
//                    } else {
//                        if(com.tezov.lib.type.equals(QueryRemoveLocal.class)){
//                            query = new QueryRemoveLocal();
//                        } else {
//
//                            DebugException.pop().produce(com.tezov.lib.type.getName() + " unknown class").explode().pop();
//
//
//                        }
//                    }
//                }
//            }
//        }
//    }
//    return query;
//}

//private void postQuery(Query query){
//    pendingQueries.addRunnable(query);
//    if(isStarted){
//        post(Event.QUERY_ADDED);
//        if(!pendingQueries.isBusy()){
//            nextQuery();
//        }
//    }
//}

private void nextQuery(){
//    if(pendingQueries.isEmpty()){
//        post(Event.DONE);
//        pendingQueries.done();
//    } else {
//        if(!isStarted){
//            pendingQueries.done();
//        } else {
//            Query query = pendingQueries.element();
//            query.getObservable().observe(new ObserverStateE(this){
//                @Override
//                public void onComplete(){
//                    post(Event.QUERY_REMOVED);
//                    nextQuery();
//                }
//
//                @Override
//                public void onException(java.lang.Exception e){
//                    onComplete();
//                }
//
//                @Override
//                public void onCancel(){
//                    onComplete();
//                }
//            });
//            ItemTransactionSync transaction = (ItemTransactionSync)query.get(ArgumentsKey.TRANSACTION);
//
//            DebugLog.send(this, transaction.getType().name() + " START" + " " + transaction.name + " " + UtilsString.removeLeadingZero(transaction.getUid().toHexString())
//                    //                " " + transaction.sync.getUID()
//                         );
//
//            pendingQueries.next();
//        }
//    }
}

private ItemTransactionSync findTransactionInProgress(ItemSync.Type type, Direction direction, defUid itemSyncUID){
    ItemTransactionSync transaction = dbTransaction.getFirst(getTransactionName(), direction, type, itemSyncUID);
//    if((transaction != null) && !pendingQueries.contain(new PredicateW<Query>(){
//        @Override
//        public boolean test(Query query){
//            ItemTransactionSync queryTransaction = (ItemTransactionSync)query.get(ArgumentsKey.TRANSACTION);
//            return Compare.equals(transaction.getUid(), queryTransaction.getUid());
//        }
//    })){
//
//        DebugException.pop().produce("Transaction found in db but not on pending query : " + transaction).logHidden().pop();
//
//
//    }
    return transaction;
}

private boolean isTransactionBackEvent(ItemSync.Type type, Direction direction, defUid itemSyncUID){
    ItemTransactionSync transaction = findTransactionInProgress(type, direction, itemSyncUID);

    if(transaction != null){
DebugLog.start().send(this, transaction.getType().name() + " BACK EVENT" + " " + transaction.name + " " + UtilsString.removeLeadingZero(transaction.getUid().toHexString()) +
                            //                " " + transaction.sync.getUID() +
                            " elapsed " + Clock.MilliSecondTo.MilliSecond.Elapsed.toString(transaction.timestamp) + "ms").end();
    }

    return transaction != null;
}

private void transactionComplete(ItemTransactionSync transaction){
    dbTransaction.remove(transaction.getUid());

DebugLog.start().send(this, transaction.getType().name() + " COMPLETED" + " " + transaction.name + " " + UtilsString.removeLeadingZero(transaction.getUid().toHexString()) +
                        //            " " + transaction.sync.getUID() +
                        " elapsed " + Clock.MilliSecondTo.MilliSecond.Elapsed.toString(transaction.timestamp) + "ms").end();

}

private void transactionFail(ItemTransactionSync transaction, QueryException qe, java.lang.Throwable e){
    transaction.setFailedMessage(qe.name() + (e != null ? ":" + e : ""));
    if((qe == QueryException.UPDATE_UP_TO_DATE) || !BuildConfig.DEBUG_ONLY){
        dbTransaction.remove(transaction.getUid());
    } else {
        dbTransaction.update(transaction);
    }

DebugLog.start().send(this, transaction.getType().name() + " FAILED" + " " + transaction.name + " " + UtilsString.removeLeadingZero(transaction.getUid().toHexString()) + " " + transaction.failedMessage +
                        //                " " + transaction.sync.getUID() +
                        " elapsed " + Clock.MilliSecondTo.MilliSecond.Elapsed.toString(transaction.timestamp) + "ms").end();

}


public enum Event{
    STARTED, DONE, PAUSED, QUERY_ADDED, QUERY_REMOVED
}


public enum QueryException{
    GET_ITEM_FAILED, GET_SYNC_FAILED, INSERT_ITEM_FAILED, INSERT_SYNC_FAILED, UPDATE_UP_TO_DATE, UPDATE_ITEM_FAILED, UPDATE_SYNC_FAILED, REMOVE_ITEM_FAILED, REMOVE_SYNC_FAILED
}

private enum ArgumentsKey{
    TASK, TRANSACTION, ITEM, SYNC,
}

//private abstract static class ObserverValueE<T> extends com.tezov.lib.async.notifier.observer.value.ObserverValueE<T>{
//    int id;
//    ObserverValueE(Query<?> query){
//        super(query);
//        this.id = query.getId();
//    }
//    @Override
//    public Query getOwner(){
//        return super.getOwner();
//    }
//    @Override
//    public boolean isEnabled(){
//        return Compare.equals(getOwner().getId(), id);
//    }
//}

//private abstract static class Query<ITEM extends ItemBase<ITEM>> extends RunnableGroup{
//    protected Integer id = null;
//    protected SyncTableWorker<ITEM> ref;
//
//    Query(){
//        setName(this.getClass().getName());
//        onCreate();
//    }
//
//    Query me(){
//        return this;
//    }
//
//    Arguments<ArgumentsKey> init(SyncTableWorker<ITEM> ref){
//        clear();
//        this.id = AppUIDGenerator.nextInt();
//        this.ref = ref;
//        put(ArgumentsKey.TASK, new TaskState());
//        return getArguments();
//    }
//
//    abstract void onCreate();
//
//    public Integer getId(){
//        return id;
//    }
//
//    <O> O get(ArgumentsKey key){
//        return getArguments().get(key);
//    }
//
//    void put(ArgumentsKey key, Object o){
//        getArguments().put(key, o);
//    }
//
//    TaskState getTask(){
//        return get(ArgumentsKey.TASK);
//    }
//
//    TaskState.Observable getObservable(){
//        return getTask().getObservable();
//    }
//
//    void queryCompleted(){
//        TaskState task = getTask();
//        ref.transactionComplete(get(ArgumentsKey.TRANSACTION));
//        task.notifyComplete();
//        endGroup();
//    }
//
//    void queryFailed(QueryException qe){
//        queryFailed(qe, null);
//    }
//
//    void queryFailed(QueryException qe, java.lang.Exception e){
//        TaskState task = getTask();
//        ref.transactionFail(get(ArgumentsKey.TRANSACTION), qe, e);
//        task.notifyException(e);
//        endGroup();
//    }
//
//    boolean isCanceled(){
//        TaskState task = getTask();
//        return (task == null) || task.isCanceled();
//    }
//
//    void queryCanceled(){
//        TaskState task = getTask();
//        task.cancel();
//        task.notifyCanceled();
//        endGroup();
//    }
//
//    @Override
//    public void release(){
//        if(ref == null){
//            return;
//        }
//        getArguments().clear();
//        ref.poolQueriesRelease(this);
//        ref = null;
//        id = null;
//    }
//
//}
//private static class QueryInsertRemote<ITEM extends ItemBase<ITEM>> extends Query<ITEM>{
//    @Override
//    void onCreate(){
//        add(new RunnableEvent("check_if_item_exist_in_local"){
//            @Override
//            public void runSafe(){
//                if(isCanceled()){
//                    return;
//                }
//                ItemTransactionSync transaction = get(ArgumentsKey.TRANSACTION);
//                ITEM item = ref.syncTableLocal.getTableSync().get(transaction.sync.getUid());
//                if(item == null){
//                    queryFailed(QueryException.GET_ITEM_FAILED);
//                } else {
//                    endRunnable();
//                }
//            }
//        });
//        add(new RunnableEvent("insert_item_to_remote"){
//            @Override
//            public void runSafe(){
//                if(isCanceled()){
//                    return;
//                }
//                ITEM item = get(ArgumentsKey.ITEM);
//                ref.syncTableRemote.getTableSync().insert(item).observe(new ObserverValueE<ITEM>(me()){
//                    @Override
//                    public void onComplete(ITEM item){
//                        endRunnable();
//                    }
//
//                    @Override
//                    public void onException(ITEM item, java.lang.Exception e){
//                        queryFailed(QueryException.INSERT_ITEM_FAILED, e);
//                    }
//                });
//            }
//        });
//        add(new RunnableEvent("insert_sync_to_remote"){
//            @Override
//            public void runSafe(){
//                ItemTransactionSync transaction = get(ArgumentsKey.TRANSACTION);
//                ref.syncTableRemote.insert(transaction.sync).observe(new ObserverValueE<ItemSync>(me()){
//                    @Override
//                    public void onComplete(ItemSync itemSync){
//                        queryCompleted();
//                    }
//
//                    @Override
//                    public void onException(ItemSync itemSync, java.lang.Exception e){
//                        queryFailed(QueryException.INSERT_SYNC_FAILED, e);
//                    }
//                });
//            }
//        });
//    }
//
//}
//private static class QueryUpdateRemote<ITEM extends ItemBase<ITEM>> extends Query<ITEM>{
//    @Override
//    void onCreate(){
//        add(new RunnableEvent("check_if_item_exist_in_local"){
//            @Override
//            public void runSafe(){
//                if(isCanceled()){
//                    return;
//                }
//                ItemTransactionSync transaction = get(ArgumentsKey.TRANSACTION);
//                ITEM item = ref.syncTableLocal.getTableSync().get(transaction.sync.getUid());
//                if(item == null){
//                    queryFailed(QueryException.GET_ITEM_FAILED);
//                } else {
//                    endRunnable();
//                }
//            }
//        });
//        add(new RunnableEvent("check_timestamp"){
//            @Override
//            public void runSafe(){
//                if(isCanceled()){
//                    return;
//                }
//                ItemTransactionSync transaction = get(ArgumentsKey.TRANSACTION);
//                ref.syncTableRemote.get(transaction.sync.getUid()).observe(new ObserverValueE<ItemSync>(me()){
//
//                    @Override
//                    public void onComplete(ItemSync itemSync){
//                        if(itemSync == null){
//                            endRunnable();
//                        } else {
//                            if(itemSync.timestamp >= transaction.sync.timestamp){
//                                queryFailed(QueryException.UPDATE_UP_TO_DATE);
//                            } else {
//                                endRunnable();
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onException(ItemSync itemSync, java.lang.Exception e){
//                        queryFailed(QueryException.GET_SYNC_FAILED, e);
//                    }
//                });
//            }
//        });
//        add(new RunnableEvent("update_item_to_remote"){
//            @Override
//            public void runSafe(){
//                if(isCanceled()){
//                    return;
//                }
//                ITEM item = get(ArgumentsKey.ITEM);
//                ref.syncTableRemote.getTableSync().update(item).observe(new ObserverValueE<ITEM>(me()){
//
//                    @Override
//                    public void onComplete(ITEM item){
//                        endRunnable();
//                    }
//
//                    @Override
//                    public void onException(ITEM item, java.lang.Exception e){
//                        queryFailed(QueryException.UPDATE_ITEM_FAILED, e);
//                    }
//                });
//            }
//        });
//        add(new RunnableEvent("update_sync_to_remote"){
//            @Override
//            public void runSafe(){
//                ItemTransactionSync transaction = get(ArgumentsKey.TRANSACTION);
//                ref.syncTableRemote.update(transaction.sync).observe(new ObserverValueE<ItemSync>(me()){
//
//                    @Override
//                    public void onComplete(ItemSync itemSync){
//                        queryCompleted();
//                    }
//
//                    @Override
//                    public void onException(ItemSync itemSync, java.lang.Exception e){
//                        queryFailed(QueryException.UPDATE_SYNC_FAILED, e);
//                    }
//                });
//            }
//        });
//    }
//
//}
//private static class QueryRemoveRemote<ITEM extends ItemBase<ITEM>> extends Query<ITEM>{
//    @Override
//    void onCreate(){
//        add(new RunnableEvent("remove_item_from_remote"){
//            @Override
//            public void runSafe(){
//                if(isCanceled()){
//                    return;
//                }
//                ItemTransactionSync transaction = get(ArgumentsKey.TRANSACTION);
//                ref.syncTableRemote.getTableSync().remove(transaction.sync.getUid()).observe(new ObserverValueE<ITEM>(me()){
//
//                    @Override
//                    public void onComplete(ITEM item){
//                        endRunnable();
//                    }
//
//                    @Override
//                    public void onException(ITEM item, java.lang.Exception e){
//                        queryFailed(QueryException.REMOVE_ITEM_FAILED, e);
//                    }
//                });
//            }
//        });
//        add(new RunnableEvent("update_sync_remote"){
//            @Override
//            public void runSafe(){
//                ItemTransactionSync transaction = get(ArgumentsKey.TRANSACTION);
//                ref.syncTableRemote.update(transaction.sync).observe(new ObserverValueE<ItemSync>(me()){
//
//                    @Override
//                    public void onComplete(ItemSync itemSync){
//                        queryCompleted();
//                    }
//
//                    @Override
//                    public void onException(ItemSync itemSync, java.lang.Exception e){
//                        queryFailed(QueryException.REMOVE_SYNC_FAILED, e);
//                    }
//                });
//            }
//        });
//    }
//
//}
//private static class QueryInsertLocal<ITEM extends ItemBase<ITEM>> extends Query<ITEM>{
//    @Override
//    void onCreate(){
//        add(new RunnableEvent("check_if_item_exist_in_local"){
//            @Override
//            public void runSafe(){
//                if(isCanceled()){
//                    return;
//                }
//                ItemTransactionSync transaction = get(ArgumentsKey.TRANSACTION);
//                ItemSync itemSync = ref.syncTableLocal.get(transaction.sync.getUid());
//                if(itemSync == null){
//                    endRunnable();
//                } else {
//                    if(itemSync.timestamp >= transaction.sync.timestamp){
//                        queryFailed(QueryException.UPDATE_UP_TO_DATE);
//                    } else {
//                        endRunnable();
//                    }
//                }
//            }
//        });
//        add(new RunnableEvent("get_item_in_remote"){
//            @Override
//            public void runSafe(){
//                if(isCanceled()){
//                    return;
//                }
//                ItemTransactionSync transaction = get(ArgumentsKey.TRANSACTION);
//                ref.syncTableRemote.getTableSync().get(transaction.sync.getUid()).observe(new ObserverValueE<ITEM>(me()){
//                    @Override
//                    public void onComplete(ITEM item){
//                        if(item == null){
//                            queryFailed(QueryException.GET_ITEM_FAILED);
//                        } else {
//                            put(ArgumentsKey.ITEM, item);
//                            endRunnable();
//                        }
//                    }
//
//                    @Override
//                    public void onException(ITEM object, java.lang.Exception e){
//                        queryFailed(QueryException.GET_ITEM_FAILED, e);
//                    }
//                });
//            }
//        });
//        add(new RunnableEvent("insert_item_to_local"){
//            @Override
//            public void runSafe(){
//                if(isCanceled()){
//                    return;
//                }
//                ITEM item = get(ArgumentsKey.ITEM);
//                if(ref.syncTableLocal.getTableSync().insert(item) == null){
//                    queryFailed(QueryException.INSERT_ITEM_FAILED);
//                } else {
//                    if(item instanceof ItemFile){
//                        ((ItemFile)item).moveTempFileToFile(true);
//                    }
//                    endRunnable();
//                }
//            }
//        });
//        add(new RunnableEvent("insert_sync_to_local"){
//            @Override
//            public void runSafe(){
//                ItemTransactionSync transaction = get(ArgumentsKey.TRANSACTION);
//                if(ref.syncTableLocal.insert(transaction.sync) == null){
//                    queryFailed(QueryException.INSERT_SYNC_FAILED);
//                } else {
//                    queryCompleted();
//                }
//            }
//        });
//    }
//
//}
//private static class QueryUpdateLocal<ITEM extends ItemBase<ITEM>> extends Query<ITEM>{
//    @Override
//    void onCreate(){
//        add(new RunnableEvent("check_if_item_exist_in_local"){
//            @Override
//            public void runSafe(){
//                if(isCanceled()){
//                    return;
//                }
//                ItemTransactionSync transaction = get(ArgumentsKey.TRANSACTION);
//                ItemSync itemSync = ref.syncTableLocal.get(transaction.sync.getUid());
//                if(itemSync == null){
//                    queryFailed(QueryException.GET_SYNC_FAILED);
//                } else {
//                    put(ArgumentsKey.SYNC, itemSync);
//                    endRunnable();
//                }
//            }
//        });
//        add(new RunnableEvent("check_timestamp"){
//            @Override
//            public void runSafe(){
//                if(isCanceled()){
//                    return;
//                }
//                ItemSync itemSync = get(ArgumentsKey.SYNC);
//                ItemTransactionSync transaction = get(ArgumentsKey.TRANSACTION);
//                if(itemSync.timestamp >= transaction.sync.timestamp){
//                    queryFailed(QueryException.UPDATE_UP_TO_DATE);
//                } else {
//                    endRunnable();
//                }
//            }
//        });
//        add(new RunnableEvent("get_item_in_remote"){
//            @Override
//            public void runSafe(){
//                if(isCanceled()){
//                    return;
//                }
//                ItemTransactionSync transaction = get(ArgumentsKey.TRANSACTION);
//                ref.syncTableRemote.getTableSync().get(transaction.sync.getUid()).observe(new ObserverValueE<ITEM>(me()){
//
//                    @Override
//                    public void onComplete(ITEM item){
//                        if(item == null){
//                            queryFailed(QueryException.GET_ITEM_FAILED);
//                        } else {
//                            put(ArgumentsKey.ITEM, item);
//                            endRunnable();
//                        }
//                    }
//
//                    @Override
//                    public void onException(ITEM object, java.lang.Exception e){
//                        queryFailed(QueryException.GET_ITEM_FAILED, e);
//                    }
//                });
//            }
//        });
//        add(new RunnableEvent("update_item"){
//            @Override
//            public void runSafe(){
//                if(isCanceled()){
//                    return;
//                }
//                ITEM item = get(ArgumentsKey.ITEM);
//                if(!ref.syncTableLocal.getTableSync().update(item)){
//                    queryFailed(QueryException.UPDATE_ITEM_FAILED);
//                } else {
//                    endRunnable();
//                }
//            }
//        });
//        add(new RunnableEvent("update_sync"){
//            @Override
//            public void runSafe(){
//                ItemTransactionSync transaction = get(ArgumentsKey.TRANSACTION);
//                if(!ref.syncTableLocal.update(transaction.sync)){
//                    queryFailed(QueryException.UPDATE_SYNC_FAILED);
//                } else {
//                    queryCompleted();
//                }
//            }
//        });
//    }
//
//}
//private static class QueryRemoveLocal<ITEM extends ItemBase<ITEM>> extends Query<ITEM>{
//    @Override
//    void onCreate(){
//        add(new RunnableEvent("check_if_item_exist_in_local"){
//            @Override
//            public void runSafe(){
//                if(isCanceled()){
//                    return;
//                }
//                ItemTransactionSync transaction = get(ArgumentsKey.TRANSACTION);
//                ITEM item = ref.syncTableLocal.getTableSync().get(transaction.sync.getUid());
//                if(item == null){
//                    queryFailed(QueryException.GET_ITEM_FAILED);
//                } else {
//                    endRunnable();
//                }
//            }
//        });
//        add(new RunnableEvent("remove_item"){
//            @Override
//            public void runSafe(){
//                if(isCanceled()){
//                    return;
//                }
//                ItemTransactionSync transaction = get(ArgumentsKey.TRANSACTION);
//                ITEM item = ref.syncTableLocal.getTableSync().remove(transaction.sync.getUid());
//                if(item == null){
//                    queryFailed(QueryException.REMOVE_ITEM_FAILED);
//                } else {
//                    endRunnable();
//                }
//            }
//        });
//        add(new RunnableEvent("remove_sync"){
//            @Override
//            public void runSafe(){
//                ItemTransactionSync transaction = get(ArgumentsKey.TRANSACTION);
//                ItemSync itemSync = ref.syncTableLocal.remove(transaction.sync.getUid());
//                if(itemSync == null){
//                    queryFailed(QueryException.REMOVE_ITEM_FAILED);
//                } else {
//                    queryCompleted();
//                }
//            }
//        });
//    }
//
//}

}
