/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.type.android;

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
import com.tezov.lib_java.toolbox.Compare;
import com.tezov.lib_java.type.misc.SupplierSubscription;

import android.view.View;
import android.view.ViewTreeObserver;

import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.ref.Ref;
import com.tezov.lib_java.type.ref.WR;
import com.tezov.lib_java.type.runnable.RunnableSubscription;
import com.tezov.lib_java_android.wrapperAnonymous.ViewTreeObserverOnGlobalLayoutListenerW;
import com.tezov.lib_java_android.wrapperAnonymous.ViewTreeObserverOnPreDrawListenerW;

public class ViewTreeEvent{

private final Event event;
private final WR<View> view;
private Object runnable;
private Object observer;

private ViewTreeEvent(Event event, View view, Object runnable){
DebugTrack.start().create(this).end();
    this.event = event;
    this.view = WR.newInstance(view);
    this.runnable = runnable;
    if(event == Event.ON_LAYOUT){
        onLayout(view);
    }
    else if(event == Event.ON_PRE_DRAW){
        onPreDraw(view);
    } else {
DebugException.start().unknown("event", event).end();
    }
}

public static Subscription onLayout(View view, RunnableSubscription runnable){
    ViewTreeEvent layout = new ViewTreeEvent(Event.ON_LAYOUT, view, runnable);
    Subscription subscription = new Subscription(layout);
    runnable.bind(subscription);
    return subscription;
}
public static Subscription onPreDraw(View view, SupplierSubscription<Boolean> runnable){
    ViewTreeEvent layout = new ViewTreeEvent(Event.ON_PRE_DRAW, view, runnable);
    Subscription subscription = new Subscription(layout);
    runnable.bind(subscription);
    return subscription;
}

private ViewTreeEvent me(){
    return this;
}

private void onLayout(View view){
    observer = new ViewTreeObserverOnGlobalLayoutListenerW(){
        @Override
        public void onGlobalLayout(){
            synchronized(me()){
                if(runnable != null){
                    ((RunnableSubscription)runnable).onComplete();
                }
            }
        }
    };
    view.getViewTreeObserver().addOnGlobalLayoutListener((ViewTreeObserver.OnGlobalLayoutListener)observer);
}
private void onPreDraw(View view){
    observer = new ViewTreeObserverOnPreDrawListenerW(){
        @Override
        public boolean onPreDraw(){
            synchronized(me()){
                if(runnable != null){
                    return Compare.isTrueOrNull(((SupplierSubscription<Boolean>)runnable).onComplete());
                }
                else {
                    return true;
                }
            }
        }
    };
    view.getViewTreeObserver().addOnPreDrawListener((ViewTreeObserver.OnPreDrawListener)observer);
}

public boolean unsubscribe(){
    synchronized(me()){
        if(observer == null){
            return false;
        }
        if(Ref.isNotNull(view)){
            ViewTreeObserver tree = view.get().getViewTreeObserver();
            if(event == Event.ON_LAYOUT){
                tree.removeOnGlobalLayoutListener((ViewTreeObserver.OnGlobalLayoutListener)observer);
            }
            else if(event == Event.ON_PRE_DRAW){
                tree.removeOnPreDrawListener((ViewTreeObserver.OnPreDrawListener)observer);
            }
        }
        observer = null;
        runnable = null;
        return true;
    }
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

private enum Event{
    ON_PRE_DRAW, ON_LAYOUT
}

public static class Subscription extends com.tezov.lib_java.async.notifier.Subscription<ViewTreeEvent>{
    private Subscription(ViewTreeEvent unsubscribeRef){
        super(unsubscribeRef);
    }
    @Override
    public boolean unsubscribe(){
        return getRef().unsubscribe();
    }
    public View getView(){
        return Ref.get(getRef().view);
    }
}

}
