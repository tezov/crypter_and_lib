/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.database.firebase.holder;

import com.tezov.lib_java.debug.DebugLog;
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
import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Transaction;
import com.tezov.lib_java.async.notifier.observer.value.ObserverValueE;
import com.tezov.lib_java.async.notifier.task.TaskState;
import com.tezov.lib_java.async.notifier.task.TaskValue;
import com.tezov.lib_java_android.database.ItemBase;
import com.tezov.lib_java_android.database.TableDescription;
import com.tezov.lib_java_android.database.firebase.fbTable;
import com.tezov.lib_java_android.database.firebase.fbTableIterable;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.collection.ListKey;
import com.tezov.lib_java_android.wrapperAnonymous.FireBaseValueEventListenerW;
import com.tezov.lib_java_android.wrapperAnonymous.FirebaseCompletionListenerW;

import java.util.Map;

public class fbTablesHandle{
private DatabaseReference fb = null;
private ListKey<String, fbTable> tables = null;

public fbTablesHandle(){
DebugTrack.start().create(this).end();
}
public static TaskValue<Integer>.Observable getVersion(fbContext context, String name){
    TaskValue<Integer> task = new TaskValue<>();
    DatabaseReference fb = context.getFireBaseReference();
    fb.child(fbTablesOpener.ReferenceInfo.Schema.entry(name)).addListenerForSingleValueEvent(new FireBaseValueEventListenerW(){
        @Override
        public void onChange(@NonNull DataSnapshot snapshot){
            if(snapshot.getChildrenCount() > 0){
                DataSnapshot data = snapshot.getChildren().iterator().next();
                if(!fbTablesOpener.ReferenceInfo.Schema.Entry.version.equals(data.getKey())){
                    task.notifyException(null, "incorrect key");
                } else {
                    Integer fbVersion = data.getValue(Integer.class);
                    task.notifyComplete(fbVersion);
                }
            } else {
                task.notifyComplete(null);
            }
        }
        @Override
        public void onCancel(@NonNull DatabaseError databaseError){
            task.notifyException(null, databaseError.toException());
        }
    });
    return task.getObservable();
}
private fbTablesHandle me(){
    return this;
}
public TaskState.Observable open(fbTablesOpener opener){
    TaskState task = new TaskState();
    opener.open().observe(new ObserverValueE<DatabaseReference>(this){
        @Override
        public void onComplete(DatabaseReference ref){
            fb = ref;
            tables = opener.getTables();
            for(fbTable table: tables){
                table.setDatabase(me());
            }
            task.notifyComplete();
        }
        @Override
        public void onException(DatabaseReference ref, Throwable e){
            task.notifyException(e);
        }
    });
    return task.getObservable();
}
public <T extends fbTable> T getTable(String name){
    return (T)tables.getValue(name);
}
public <R extends fbTable.Ref> R getMainRef(String name){
    return (R)getTable(name).mainRef();
}
public <R extends fbTable.Ref> R newRef(String name){
    return (R)getTable(name).newRef();
}
public <ITEM extends ItemBase<ITEM>> fbTableIterable<ITEM> newIterable(String name){
    return new fbTableIterable(newRef(name));
}
public <T extends fbTable> T getTable(TableDescription t){
    return getTable(t.name());
}
public <R extends fbTable.Ref> R getMainRef(TableDescription t){
    return (R)getTable(t).mainRef();
}
public <R extends fbTable.Ref> R newRef(TableDescription t){
    return (R)getTable(t).newRef();
}
public <ITEM extends ItemBase<ITEM>> fbTableIterable<ITEM> newIterable(TableDescription t){
    return newIterable(t.name());
}
public <T extends fbTable> T getTable(TableDescription prefix, TableDescription t){
    return getTable(t.name(prefix.name()));
}
public <R extends fbTable.Ref> R getMainRef(TableDescription prefix, TableDescription t){
    return (R)getTable(prefix, t).mainRef();
}
public <R extends fbTable.Ref> R newRef(TableDescription prefix, TableDescription t){
    return (R)getTable(prefix, t).newRef();
}
public <ITEM extends ItemBase<ITEM>> fbTableIterable<ITEM> newIterable(TableDescription prefix, TableDescription t){
    return newIterable(t.name(prefix.name()));
}

public String getName(){
    return fb.getKey();
}

public DatabaseReference generateRootKey(){
    return root().push();
}
public DatabaseReference generateKey(String table){
    return child(table).push();
}
public DatabaseReference root(){
    return fb;
}
public DatabaseReference child(String table){
    return fb.child(table);
}
public void updateChildren(String table, Map<String, Object> data, FirebaseCompletionListenerW listener){
    child(table).updateChildren(data, listener);
}
public void setValue(String table, Object value, FirebaseCompletionListenerW listener){
    child(table).setValue(value, listener);
}
public void runTransaction(String table, Transaction.Handler handler){
    child(table).runTransaction(handler);
}
public void updateChildren(Map<String, Object> data, FirebaseCompletionListenerW listener){
    root().updateChildren(data, listener);
}
public void setValue(Object value, FirebaseCompletionListenerW listener){
    root().setValue(value, listener);
}
public void runTransaction(Transaction.Handler handler){
    root().runTransaction(handler);
}
public TaskState.Observable close(){
    return TaskState.Complete();
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

}
