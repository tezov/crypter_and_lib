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
import com.tezov.lib_java.async.notifier.task.TaskState;
import com.tezov.lib_java.async.notifier.task.TaskValue;

import com.tezov.lib_java.async.notifier.observer.state.ObserverState;
import com.tezov.lib_java.async.notifier.observer.value.ObserverValueE;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.annotation.DebugLogEnable;
import com.tezov.lib_java.type.runnable.RunnableGroup;

@DebugLogEnable
public class TestManager{

private static int counter = 0;
private static Class<TestManager> myClass(){
    return TestManager.class;
}
public static void incrementCounter(int value){
    counter+=value;
}

public static TaskState.Observable beforeLaunch(){
    return TaskState.Complete();
}
public static TaskState.Observable afterLaunch(){
    return TaskState.Complete();
}
public static TaskValue<Integer>.Observable launch(){
    TaskValue<Integer> task = new TaskValue<>();
    RunnableGroup gr = new RunnableGroup(myClass()).name("TestManager");
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            beforeLaunch().observe(new ObserverState(this){
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
            TestCrypterContext.launch().observe(new ObserverValueE<>(this){
                @Override
                public void onComplete(Integer value){
                    incrementCounter(value);
                    next();
                }
            });
        }
    });
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            TestCipher.launch().observe(new ObserverValueE<>(this){
                @Override
                public void onComplete(Integer value){
                    incrementCounter(value);
                    next();
                }
            });
        }
    });
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            TestCrypterCipher.launch().observe(new ObserverValueE<>(this){
                @Override
                public void onComplete(Integer value){
                    incrementCounter(value);
                    next();
                }
            });
        }
    });
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            afterLaunch().observe(new ObserverState(this){
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
DebugLog.start().send("******* all tests finished, done " + counter).end();
            task.notifyComplete(counter);
        }
    });
    counter = 0;
    gr.start();
    return task.getObservable();
}



}