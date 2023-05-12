/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.misc;

import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.type.primitive.ObjectTo;
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
import static com.tezov.lib_java_android.ui.misc.StateView.Feature.BUTTON_MULTI_INDEX;
import static com.tezov.lib_java_android.ui.misc.StateView.Feature.CLICK;
import static com.tezov.lib_java_android.ui.misc.StateView.Feature.CLICK_ICON;
import static com.tezov.lib_java_android.ui.misc.StateView.Feature.ENABLED;
import static com.tezov.lib_java_android.ui.misc.StateView.Feature.VISIBLE;

import android.view.View;

import com.tezov.lib_java.application.AppKryo;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.wrapperAnonymous.FunctionW;
import com.tezov.lib_java.type.collection.ListEntry;
import com.tezov.lib_java.type.collection.ListKey;
import com.tezov.lib_java.type.collection.ListOrObject;
import com.tezov.lib_java.type.primaire.Entry;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.ref.WR;
import com.tezov.lib_java_android.ui.component.plain.ButtonMultiIconMaterial;
import com.tezov.lib_java_android.ui.component.plain.EditTextWithIcon;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class StateView{
private final AtomicBoolean isBusy;
private ListKey<Integer, State> states;

public StateView(){
DebugTrack.start().create(this).end();
    isBusy = new AtomicBoolean(false);
    states = new ListKey<>(ArrayList::new, new FunctionW<State, Integer>(){
        @Override
        public Integer apply(State state){
            return state.viewId;
        }
    });
}
public boolean isLocked(){
    return isBusy.get();
}
public boolean lock(){
    return isBusy.compareAndSet(false, true);
}
public StateView unlock(){
    isBusy.set(false);
    return this;
}
public ListKey<Integer, State> getStates(){
    return states;
}
public StateView setStates(ListKey<Integer, State> states){
    this.states = states;
    return this;
}
public StateView clear(){
    states.clear();
    isBusy.set(false);
    return this;
}
public void restore(){
    for(State state: states){
        state.restore();
    }
}
public void restore(View parent){
    for(State state: states){
        state.restore(parent);
    }
}
public StateView copy(){
    StateView copy = new StateView();
    for(State s: states){
        copy.states.add(s.copy());
    }
    return copy;
}
public StateView put(Feature feature, View view, Object value){
    State state = states.getValue(view.getId());
    if(state == null){
        state = new State(view);
        states.add(state);
    }
    state.save(feature, value);
    return this;
}
public StateView visibleGone(View view){
    return setVisibility(view, View.GONE);
}
public StateView visibleNot(View view){
    return setVisibility(view, View.INVISIBLE);
}
public StateView visible(View view){
    return setVisibility(view, View.VISIBLE);
}
public StateView setVisibility(View view, int value){
    put(VISIBLE, view, view.getVisibility());
    view.setVisibility(value);
    return this;
}
public StateView visibilitySave(View view){
    put(VISIBLE, view, view.getVisibility());
    return this;
}
public StateView enableNot(View view){
    return setEnabled(view, false);
}
public StateView enable(View view){
    return setEnabled(view, true);
}
public StateView setEnabled(View view, boolean value){
    put(ENABLED, view, view.isEnabled());
    view.setEnabled(value);
    return this;
}
public StateView enableSave(View view){
    put(ENABLED, view, view.isEnabled());
    return this;
}
public StateView clickableNot(View view){
    return setClickable(view, false);
}
public StateView clickable(View view){
    return setClickable(view, true);
}
public StateView setClickable(View view, boolean value){
    put(CLICK, view, view.isClickable());
    view.setClickable(value);
    return this;
}
public StateView clickableSave(View view){
    put(CLICK, view, view.isClickable());
    return this;
}
public StateView clickableIconNot(EditTextWithIcon view){
    return setClickableIcon(view, false);
}
public StateView clickableIcon(EditTextWithIcon view){
    return setClickableIcon(view, true);
}
public StateView setClickableIcon(EditTextWithIcon view, boolean value){
    put(CLICK_ICON, view, view.isClickableIcon());
    view.setClickableIcon(value);
    return this;
}
public StateView clickableIconSave(EditTextWithIcon view){
    put(CLICK_ICON, view, view.isClickableIcon());
    return this;
}
public StateView setIndex(ButtonMultiIconMaterial view, int value){
    put(BUTTON_MULTI_INDEX, view, view.getIndex());
    view.setIndex(value);
    return this;
}
public StateView indexSave(ButtonMultiIconMaterial view){
    put(BUTTON_MULTI_INDEX, view, view.getIndex());
    return this;
}
public DebugString toDebugString(){
    DebugString data = new DebugString();
    data.append("isBusy", isBusy);
    data.append("states", states);
    return data;
}
final public void toDebugLog(){
DebugLog.start().send(toDebugString()).end();
}
@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}
public enum Feature{
    VISIBLE, ENABLED, CLICK, CLICK_ICON, BUTTON_MULTI_INDEX,
}

private static class State{
    int viewId;
    WR<View> viewWR;
    ListEntry<Feature, Object> features;
    State(View view){
DebugTrack.start().create(this).end();
        this.viewWR = WR.newInstance(view);
        this.features = new ListEntry<Feature, Object>(ListOrObject::new);
        this.viewId = view.getId();
    }
    View view(){
        return viewWR.get();
    }
    void save(Feature feature, Object value){
        features.put(feature, value);
    }
    boolean restore(){
        View view = viewWR.get();
        if(view != null){
            restoreAll(view);
            return true;
        }
        return false;
    }
    boolean restore(View parent){
        View view = parent.findViewById(viewId);
        if(view != null){
            restoreAll(view);
            return true;
        }
        return false;
    }
    void restoreAll(View view){
        for(Entry<Feature, Object> e: features){
            Feature feature = e.key;
            switch(feature){
                case VISIBLE:{
                    int value = (int)e.value;
                    view.setVisibility(value);
                }
                break;
                case ENABLED:{
                    boolean value = (boolean)e.value;
                    view.setEnabled(value);
                }
                break;
                case CLICK:{
                    boolean value = (boolean)e.value;
                    view.setClickable(value);
                }
                break;
                case CLICK_ICON:{
                    boolean value = (boolean)e.value;
                    ((EditTextWithIcon)view).setClickableIcon(value);
                }
                break;
                case BUTTON_MULTI_INDEX:{
                    int value = (int)e.value;
                    ((ButtonMultiIconMaterial)view).setIndex(value);
                }
                break;
            }
        }
    }
    State copy(){
        State copy = new State(view());
        for(Entry<Feature, Object> e: features){
            copy.save(e.key, AppKryo.copy(e.value));
        }
        return copy;
    }
    public DebugString toDebugString(){
        DebugString data = new DebugString();
        data.append("viewId", IntTo.Bytes(viewId));
        data.append("features", features);
        return data;
    }
    final public void toDebugLog(){
DebugLog.start().send(toDebugString()).end();
    }
    @Override
    protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
        super.finalize();
    }

}

}
