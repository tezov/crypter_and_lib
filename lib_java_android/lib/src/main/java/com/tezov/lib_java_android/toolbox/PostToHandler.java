/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.toolbox;

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugTrack;
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

import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java_android.ui.activity.ActivityBase;

import android.view.View;

import com.tezov.lib_java.async.Handler;
import com.tezov.lib_java.type.runnable.RunnableW;

import java.util.concurrent.TimeUnit;

public class PostToHandler{
private PostToHandler(){
}

public static boolean of(ActivityBase activity, RunnableW runnable){
    return of(activity, 0, TimeUnit.MILLISECONDS, runnable);
}
public static boolean of(ActivityBase activity, long delay, RunnableW runnable){
    return of(activity, delay, TimeUnit.MILLISECONDS, runnable);
}
public static boolean of(ActivityBase activity, long delay, TimeUnit timeUnit, RunnableW runnable){
    return of(activity.getViewRoot(), delay, timeUnit, runnable);
}

public static boolean of(View view, RunnableW runnable){
    return of(view, 0, TimeUnit.MILLISECONDS, runnable);
}
public static boolean of(View view, long delay, RunnableW runnable){
    return of(view, delay, TimeUnit.MILLISECONDS, runnable);
}
public static boolean of(View view, long delay, TimeUnit timeUnit, RunnableW runnable){
    if(view == null){
DebugException.start().log("view is null").end();
        return false;
    }
    else{
        android.os.Handler handler = view.getHandler();
        if((delay<=0) && Handler.isThread(Handler.MAIN()) && ((handler==null) || Handler.equals(handler, Handler.MAIN()))){
            runnable.run();
        }
        else{
            view.postDelayed(runnable, TimeUnit.MILLISECONDS.convert(delay, timeUnit));
        }
        return true;
    }
}

}
