/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package activity;

import com.tezov.lib_java.async.notifier.observer.state.ObserverState;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.debug.DebugLog;
import java.util.Set;

import com.tezov.lib_java.type.runnable.RunnableGroup;
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
import com.tezov.lib_java.async.Handler;
import com.tezov.lib_java.type.runnable.RunnableW;
import com.tezov.crypter.R;
import com.tezov.crypter.activity.ActivityMain;

import test.TestCrypterCipher;
import test.TestManager;

public class ActivityMain_bt extends ActivityMain{

@Override
protected int getLayoutId(){
    return R.layout.activity_main_debug;
}

@Override
public void onOpen(boolean hasBeenReconstructed, boolean hasBeenRestarted){
    super.onOpen(hasBeenReconstructed, hasBeenRestarted);

//    Handler.MAIN().post(this, 1000, new RunnableW(){
//        @Override
//        public void runSafe(){
//            TestManager.launch();
//        }
//    });

//    Handler.MAIN().post(this, 1000, new RunnableW(){
//        @Override
//        public void runSafe(){
//            RunnableGroup gr = new RunnableGroup(this);
//            gr.add(new RunnableGroup.Action(){
//                @Override
//                public void runSafe(){
//                    TestCrypterCipher.beforeLaunch().observe(new ObserverState(this){
//                        @Override
//                        public void onComplete(){
//                            next();
//                        }
//                    });
//                }
//            });
//            gr.add(new RunnableGroup.Action(){
//                @Override
//                public void runSafe(){
//                    TestCrypterCipher.decodeString_Fix().observe(new ObserverState(this){
//                        @Override
//                        public void onComplete(){
//                            next();
//                        }
//                    });
//                }
//            });
//            gr.add(new RunnableGroup.Action(){
//                @Override
//                public void runSafe(){
//                    TestCrypterCipher.afterLaunch().observe(new ObserverState(this){
//                        @Override
//                        public void onComplete(){
//                            next();
//                        }
//                    });
//                }
//            });
//            gr.start();
//        }
//    });

}

}
