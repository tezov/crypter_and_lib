/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.misc;

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
import static com.tezov.lib_java_android.ui.misc.TransitionManager.Direction.ENTER;
import static com.tezov.lib_java_android.ui.misc.TransitionManager.Direction.ENTER_BACK;
import static com.tezov.lib_java_android.ui.misc.TransitionManager.Direction.EXIT;
import static com.tezov.lib_java_android.ui.misc.TransitionManager.Direction.EXIT_BACK;

import android.view.View;

import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.collection.ListEntry;
import com.tezov.lib_java.type.defEnum.EnumBase;

public abstract class TransitionManager<A, T extends TransitionManager.Transitions<A>>{
private final ListEntry<Name.Is, T> transitions;

public TransitionManager(){
DebugTrack.start().create(this).end();
    transitions = new ListEntry<>();
}

public void add(TransitionManager.Name.Is name, int enter, int exit){
    add(name, enter, exit, exit, enter);
}
public void add(TransitionManager.Name.Is name, int enter, int exit, int enterBack, int exitBack){
    T t = newTransition(enter, exit, enterBack, exitBack);
    transitions.put(name, t);

}
protected abstract T newTransition(int enter, int exit, int enterBack, int exitBack);
public T get(TransitionManager.Name.Is name){
    if(name == null){
        return null;
    } else {
        return transitions.getValue(name);
    }
}
public abstract long getDuration(TransitionManager.Name.Is name);

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

public enum Direction{
    ENTER, ENTER_BACK, EXIT, EXIT_BACK
}

public interface Name{
    Is FADE = new Is("FADE");
    Is SLIDE_RIGHT = new Is("SLIDE_RIGHT");
    Is SLIDE_LEFT = new Is("SLIDE_LEFT");
    Is SLIDE_UP = new Is("SLIDE_UP");
    Is SLIDE_DOWN = new Is("SLIDE_DOWN");

    class Is extends EnumBase.Is{
        protected Is(String name){
            super(name);
        }

    }

}

public abstract static class Transitions<T>{
    public int enter;
    public int exit;
    public int enterBack;
    public int exitBack;
    public Transitions(int enter, int exit, int enterBack, int exitBack){
DebugTrack.start().create(this).end();
        this.enter = enter;
        this.exit = exit;
        this.enterBack = enterBack;
        this.exitBack = exitBack;
    }
    public Transitions(int enter, int exit){
        this(enter, exit, exit, enter);
    }
    public T getFor(View view, Direction type){
        if(type == ENTER){
            return getEnterFor(view);
        } else if(type == ENTER_BACK){
            return getEnterBackFor(view);
        } else if(type == EXIT){
            return getExitFor(view);
        } else if(type == EXIT_BACK){
            return getExitBackFor(view);
        } else {
DebugException.start().unknown("direction", type).end();
            return null;
        }
    }
    public abstract T getExitFor(View view);

    public abstract T getExitBackFor(View view);

    public abstract T getEnterFor(View view);

    public abstract T getEnterBackFor(View view);

    @Override
    protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
        super.finalize();
    }

}

}
