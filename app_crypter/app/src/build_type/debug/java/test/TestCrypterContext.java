/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package test;

import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.toolbox.CompareType;
import java.util.Set;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import java.util.List;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import androidx.fragment.app.Fragment;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.util.UtilsString;
import com.tezov.lib_java.toolbox.Clock;
import java.util.LinkedList;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.google.common.truth.Truth;
import com.tezov.crypter.application.AppInfo;
import com.tezov.lib_java.application.AppContext;
import com.tezov.lib_java.async.notifier.observer.state.ObserverState;
import com.tezov.lib_java.async.notifier.task.TaskState;
import com.tezov.lib_java.async.notifier.task.TaskValue;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.annotation.DebugLogEnable;
import com.tezov.lib_java.generator.uid.defUid;
import com.tezov.lib_java.type.runnable.RunnableGroup;

@DebugLogEnable
public class TestCrypterContext{

private static final int TEST_COUNT = 4;
private static int counter = 0;
private static Class<TestCrypterContext> myClass(){
    return TestCrypterContext.class;
}
private static void incrementCounter(){
    counter++;
}

public static TaskValue<Integer>.Observable launch(){
    TaskValue<Integer> task = new TaskValue<>();
    RunnableGroup gr = new RunnableGroup(myClass()).name("TestCrypterContext");
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
DebugLog.start().send("++++++++++++ start::TestCrypterContext").end();
            next();
        }
    });
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            packageName().observe(new ObserverState(this){
                @Override
                public void onComplete(){
                    next();
                }
            });
        }
    });
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            applicationId().observe(new ObserverState(this){
                @Override
                public void onComplete(){
                    next();
                }
            });
        }
    });
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            applicationGuid().observe(new ObserverState(this){
                @Override
                public void onComplete(){
                    next();
                }
            });
        }
    });
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            applicationDuid().observe(new ObserverState(this){
                @Override
                public void onComplete(){
                    next();
                }
            });
        }
    });
    gr.setOnDone(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            Truth.assertThat(counter).isEqualTo(TEST_COUNT);
DebugLog.start().send("------------ end::TestCrypterContext, done " + counter + "\n").end();
DebugLog.start().send().end();
            task.notifyComplete(counter);
        }
    });
    counter = 0;
    gr.start();
    return task.getObservable();
}

public static TaskState.Observable packageName(){
DebugLog.start().track(myClass()).end();
    Truth.assertThat(AppContext.getPackageName()).isEqualTo("com.tezov.crypter.dbg");
    incrementCounter();
    return TaskState.Complete();
}
public static TaskState.Observable applicationId(){
DebugLog.start().track(myClass()).end();
    Truth.assertThat(AppContext.getApplicationId()).isEqualTo("com.tezov.crypter");
    incrementCounter();
    return TaskState.Complete();
}
public static TaskState.Observable applicationGuid(){
DebugLog.start().track(myClass()).end();
    defUid uid = AppInfo.getGUID();
    Truth.assertThat(uid).isNotNull();
    Truth.assertThat(uid.getLength()).isEqualTo(16);
    incrementCounter();
    return TaskState.Complete();
}
public static TaskState.Observable applicationDuid(){
DebugLog.start().track(myClass()).end();
    defUid uid = AppInfo.getDUID();
    Truth.assertThat(uid).isNotNull();
    Truth.assertThat(uid.getLength()).isEqualTo(16);
    incrementCounter();
    return TaskState.Complete();
}



}