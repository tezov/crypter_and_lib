/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package application;

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugTrack;
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
import android.content.Intent;

import com.tezov.crypter.application.Application;
import com.tezov.lib_java_android.application.ApplicationSystem;
import com.tezov.lib_java_android.debug.DebugMemoryLog;

public class Application_bt extends Application{
private static final DebugMemoryLog memoryUsed = null;

public static void onMainActivityStart(ApplicationSystem app, Intent source, boolean isRestarted){
    Application.onMainActivityStart(app, source, isRestarted);
//    memoryUsed = new DebugMemoryLog()
//            .peek(ItemBase.class)
//            .peek(ByteBuffer.class)
//            .peek(WR.class)
//            .peek(FragmentNavigable.class)
//            .peek(DialogNavigable.class);
//    RunnableTimeOut r = new RunnableTimeOut(myClass(), 5000){
//        @Override
//        public void onTimeOut(){
//            memoryUsed.toDebugLog();
//            restart();
//        }
//    };
//    r.start();
}
public static void onApplicationPause(ApplicationSystem app){
    Application.onApplicationPause(app);
}
public static void onApplicationClose(ApplicationSystem app){
    Application.onApplicationClose(app);

}

}
