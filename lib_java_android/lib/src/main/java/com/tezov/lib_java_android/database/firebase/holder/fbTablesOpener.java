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
import androidx.annotation.Nullable;
import androidx.collection.ArrayMap;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.tezov.lib_java.async.notifier.observer.state.ObserverStateE;
import com.tezov.lib_java.async.notifier.task.TaskState;
import com.tezov.lib_java.async.notifier.task.TaskValue;
import com.tezov.lib_java_android.database.firebase.fbTable;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.collection.ListKey;
import com.tezov.lib_java.type.runnable.RunnableGroup;
import com.tezov.lib_java_android.wrapperAnonymous.FireBaseValueEventListenerW;
import com.tezov.lib_java_android.wrapperAnonymous.FirebaseCompletionListenerW;

import java.util.Map;

public class fbTablesOpener{
private final static String VERSION_PREFIX = "version_";
private final fbContext context;
private final String name;
private DatabaseReference fb = null;
private ListKey<String, fbTable> tables;

public fbTablesOpener(fbContext context, String name){
DebugTrack.start().create(this).end();
    this.context = context;
    this.name = name;
}
private fbTablesOpener me(){
    return this;
}

public ListKey<String, fbTable> getTables(){
    return tables;
}
public fbTablesOpener setTables(ListKey<String, fbTable> tables){
    this.tables = tables;
    return this;
}

protected DatabaseReference newFb(){
    return context.getFireBaseReference().child(VERSION_PREFIX + context.getVersion()).child(name);
}
TaskValue<DatabaseReference> open(){
    TaskValue<DatabaseReference> task = new TaskValue<>();
    RunnableGroup gr = new RunnableGroup(this).name("check fb version");
    int LBL_CREATE = gr.label();
    int LBL_DOWNGRADE = gr.label();
    int LBL_UPGRADE = gr.label();
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            DatabaseReference fb = context.getFireBaseReference();
            fb.child(ReferenceInfo.Schema.entry(name)).addListenerForSingleValueEvent(new FireBaseValueEventListenerW(){
                @Override
                public void onChange(@NonNull DataSnapshot snapshot){
                    if(snapshot.getChildrenCount() > 0){
                        DataSnapshot data = snapshot.getChildren().iterator().next();
                        if(!ReferenceInfo.Schema.Entry.version.equals(data.getKey())){
                            putException(new Throwable("incorrect key"));
                            done();
                        } else {
                            Integer fbVersion = data.getValue(Integer.class);
                            putValue(fbVersion);
                            if(context.getVersion() > fbVersion){
                                skipUntilLabel(LBL_DOWNGRADE);
                            } else if(context.getVersion() < fbVersion){
                                skipUntilLabel(LBL_UPGRADE);
                            } else {
                                done();
                            }
                        }
                    } else {
                        skipUntilLabel(LBL_CREATE);
                    }
                }
                @Override
                public void onCancel(@NonNull DatabaseError databaseError){
                    putException(databaseError.toException());
                    done();
                }
            });
        }
    }.name("check version"));
    gr.add(new RunnableGroup.Action(LBL_CREATE){
        @Override
        public void runSafe(){
            onCreate(newFb()).observe(new ObserverStateE(this){
                @Override
                public void onComplete(){
                    done();
                }
                @Override
                public void onException(Throwable e){
                    putException(e);
                    done();
                }
            });
        }
    }.name("create"));
    gr.add(new RunnableGroup.Action(LBL_UPGRADE){
        @Override
        public void runSafe(){
            int fbVersion = getValue();
            onUpgrade(newFb(), fbVersion, context.getVersion()).observe(new ObserverStateE(this){
                @Override
                public void onComplete(){
                    done();
                }
                @Override
                public void onException(Throwable e){
                    putException(e);
                    done();
                }
            });
        }
    }.name("upgrade"));
    gr.add(new RunnableGroup.Action(LBL_DOWNGRADE){
        @Override
        public void runSafe(){
            int fbVersion = getValue();
            onDowngrade(newFb(), fbVersion, context.getVersion()).observe(new ObserverStateE(this){
                @Override
                public void onComplete(){
                    done();
                }
                @Override
                public void onException(Throwable e){
                    putException(e);
                    done();
                }
            });
        }
    }.name("downgrade"));
    gr.setOnDone(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            Throwable e = getException();
            if(e == null){
                onOpen(newFb()).observe(new ObserverStateE(this){
                    @Override
                    public void onComplete(){
                        me().fb = newFb();
                        task.notifyComplete(me().fb);
                    }
                    @Override
                    public void onException(Throwable e){
                        task.notifyException(null, e);
                    }
                });
            } else {
                task.notifyException(null, e);
            }
        }
    });
    gr.start();
    return task;
}

protected TaskState.Observable onCreate(DatabaseReference fb){
    TaskState task = new TaskState();
    Map<String, Object> map = new ArrayMap<>();
    map.put(ReferenceInfo.Schema.Entry.version, context.getVersion());
    context.getFireBaseReference().child(ReferenceInfo.Schema.entry(name)).updateChildren(map, new FirebaseCompletionListenerW(){
        @Override
        public void onDone(@Nullable DatabaseError databaseError, @NonNull DatabaseReference ref){
            if(databaseError == null){
                task.notifyComplete();
            } else {
                task.notifyException(databaseError.toException());
            }
        }
    });
    return task.getObservable();
}
protected TaskState.Observable onOpen(DatabaseReference fb){
    return TaskState.Complete();
}
protected TaskState.Observable onUpgrade(DatabaseReference fb, int oldVersion, int newVersion){
    return TaskState.Complete();
}
protected TaskState.Observable onDowngrade(DatabaseReference fb, int oldVersion, int newVersion){
    return TaskState.Complete();
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

public interface ReferenceInfo{
    String ref = "info";

    interface Schema{
        String ref = ReferenceInfo.ref + "/schema";

        static String entry(String ref){
            return ReferenceInfo.Schema.ref + "/" + ref;
        }

        interface Entry{
            String version = "version";

        }

    }

}

}
