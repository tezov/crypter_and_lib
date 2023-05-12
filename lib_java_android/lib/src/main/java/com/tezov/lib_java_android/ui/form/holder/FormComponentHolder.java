/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.form.holder;

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
import static com.tezov.lib_java_android.ui.form.adapter.FormManager.Id;
import static com.tezov.lib_java_android.ui.form.adapter.FormManager.Target;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tezov.lib_java_android.application.AppContext;
import com.tezov.lib_java.async.notifier.Notifier;
import com.tezov.lib_java.async.notifier.observer.value.ObserverValue;
import com.tezov.lib_java_android.definition.defViewContainer;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.collection.ListEntry;
import com.tezov.lib_java.type.primaire.Entry;
import com.tezov.lib_java.type.ref.Ref;
import com.tezov.lib_java.type.ref.WR;
import com.tezov.lib_java_android.ui.form.adapter.FormAdapter;
import com.tezov.lib_java_android.ui.form.adapter.FormManager;
import com.tezov.lib_java_android.ui.form.component.plain.defFormComponent;

public class FormComponentHolder<FORM extends FormManager, DEFINITION extends FormComponentHolder.ViewDefinition>{
private final WR containerWR;
private State state = null;
private WR<FormComponentHolder<?, ?>> previousWR;
private WR<FormComponentHolder<?, ?>> nextWR;
private Behavior behavior = null;
private View view = null;
private boolean isEmpty = true;
private boolean isVisible = false;
private boolean isDestroyed = false;
private ListEntry<Target.Is, defFormComponent> pickers = null;

public <V extends ViewGroup & defViewContainer> FormComponentHolder(V container){
DebugTrack.start().create(this).end();
    this.containerWR = WR.newInstance(container);
}

protected State newState(){
    return new State();
}

public boolean hasState(){
    return state != null;
}

private State makeState(){
    State state = newState();
    state.attach(this);
    return state;
}

public State obtainState(){
    if(!hasState()){
        state = makeState();
    }
    return state;
}

public State getState(){
    return state;
}

final public FormComponentHolder<FORM, DEFINITION> restoreState(State state){
    this.state = state;
    state.attach(this);
    return this;
}

public Notifier.Subscription observeOnSet(ObserverValue<FormAdapter> observer){
    return getFormManager().observeOnSet(observer);
}

protected void unObserveOnSet(Object owner){
    getFormManager().unObserveOnSet(owner);
}

protected void unObserveOnSetAll(){
    getFormManager().unObserveOnSetAll();
}

public ListEntry<Target.Is, ? extends defFormComponent> getPickers(){
    return pickers;
}

public ListEntry<Target.Is, ? extends defFormComponent> getPickersAll(){
    ListEntry pickers = this.pickers;
    if(hasNext()){
        if(pickers != null){
            ListEntry nextPickers = next().getPickersAll();
            if(nextPickers != null){
                pickers.addAll(nextPickers);
            }
        } else {
            pickers = next().getPickersAll();
        }
    }
    return pickers;

}

public defFormComponent getPicker(Target.Is target){
    defFormComponent picker = pickers.getValue(target);
    if(picker != null){
        return picker;
    } else {
        if(hasNext()){
            return next().getPicker(target);
        }
    }
    return null;
}

protected Behavior newDefaultBehavior(){
    return new DefaultBehavior();
}

public Behavior getBehavior(){
    if(behavior == null){
        setBehavior(newDefaultBehavior());
    }
    return behavior;
}

public <H extends FormComponentHolder<FORM, DEFINITION>> H setBehavior(Behavior behavior){
    this.behavior = behavior;
    if(behavior != null){
        behavior.attach(this);
    }
    return (H)this;
}

public boolean isVisible(){
    return isVisible;
}

public View getView(){
    return view;
}

public <V extends ViewGroup & defViewContainer> V getContainer(){
    return (V)Ref.get(containerWR);
}

protected FormComponentHolder newHolder(){
    return new FormComponentHolder(getContainer());
}

public FormComponentHolder<?, ?> sub(){
    if(hasNext()){
        return next();
    } else {
        FormComponentHolder holder = newHolder();
        this.nextWR = WR.newInstance(holder);
        holder.behavior = this.behavior;
        holder.previousWR = WR.newInstance(this);
        return holder;
    }
}

public boolean hasNext(){
    return Ref.isNotNull(nextWR);
}

public FormComponentHolder<?, ?> next(){
    return Ref.get(nextWR);
}

public boolean hasPrevious(){
    return Ref.isNotNull(previousWR);
}

public FormComponentHolder<?, ?> previous(){
    return Ref.get(previousWR);
}

public DEFINITION getViewDefinition(){
    if(state.viewDefinition == null){
        return null;
    } else {
        return (DEFINITION)state.viewDefinition.current();
    }
}

public FormComponentHolder<FORM, DEFINITION> setViewDefinition(ViewDefinition definition){
    return setViewDefinition(new DefaultDefinitionFlipper<>(definition));
}

public FormComponentHolder<FORM, DEFINITION> setViewDefinition(ViewDefinition.Flipper definition){
    state.viewDefinition = definition;
    if((definition != null) && (definition.current() != null)){
        isEmpty = false;
    }
    return this;
}

public FormComponentHolder<FORM, DEFINITION> onPrepare(){
    if((view != null) || (getViewDefinition() == null)){
        return this;
    }
    view = LayoutInflater.from(AppContext.getActivity()).inflate(getViewDefinition().getLayoutId(), getContainer(), false);
    pickers = new ListEntry<>();
    for(Entry<Target.Is, Integer> e: getViewDefinition().getViewsId()){
        pickers.put(e.key, view.findViewById(e.value));
    }
    if(!hasState()){
        state = makeState();
    } else {
        if(state.isBuilt()){
            onRestoreState();
        }
    }
    onBuildComponent(view);
    if(!state.isBuilt()){
        onBuildState();
    }
    getViewDefinition().attach(this);
    getViewDefinition().init();
    if((state.form != null) && (getFormManager() != null)){
        getFormManager().link(pickers);
    }
    isEmpty = false;
    return this;
}

public FormComponentHolder<FORM, DEFINITION> onResume(){
    if((view != null) && (getViewDefinition() != null)){
        getViewDefinition().resume();
    }
    return this;
}

protected void onRestoreState(){
    getState().restore();
}

protected void onBuildState(){
    getState().build();
}

protected void onBuildComponent(View view){
    for(defFormComponent c: pickers.iterableValues()){
        c.build(view);
    }
}

public FormComponentHolder<FORM, DEFINITION> setFormManagerFlipper(FORM.Flipper form){
    state.form = form;
    if((form != null) && (form.current() != null)){
        if(view != null){
            getFormManager().link(pickers);
        }
        isEmpty = false;
    }
    return this;
}

public FORM getFormManager(){
    FormManager.Flipper form = state.form;
    if(form != null){
        return form.current();
    } else {
        return null;
    }
}

public FormComponentHolder<FORM, DEFINITION> setFormManager(FORM form){
    return setFormManagerFlipper(new FormManager.DefaultFlipper(form));
}

public boolean hasFormManager(){
    return getFormManager() != null;
}

public defFormComponent getInputForm(){
    if(pickers == null){
        return null;
    }
    for(Entry<Target.Is, defFormComponent> e: pickers){
        if(e.value.isInput()){
            return e.value;
        }
    }

DebugException.start().logHidden(getViewDefinition().getFormId().name() + " has no attribute app:next=\"output\"").end();

    return null;
}

public defFormComponent getOutputForm(){
    if(pickers == null){
        return null;
    }
    for(Entry<Target.Is, defFormComponent> e: pickers){
        if(e.value.isOutput()){
            return e.value;
        }
    }

DebugException.start().logHidden(getViewDefinition().getFormId().name() + " has no attribute app:previous=\"input\"").end();


    return null;
}

public void updateLinkPrevious(){
    if(hasPrevious()){
        defFormComponent.linkPickers(previous().getOutputForm(), getInputForm());
    } else {
        defFormComponent.linkPickers(null, getInputForm());
    }
}

public void updateLinkNext(){
    if(hasNext()){
        defFormComponent.linkPickers(getOutputForm(), next().getInputForm());
    } else {
        defFormComponent.linkPickers(getOutputForm(), null);
    }
}

public void updateLinks(){
    updateLinkPrevious();
    updateLinkNext();
}

public boolean isFormValid(FormManager parent){
    return state.form.isValid(parent);
}

public void flipForm(FormManager parent){
    if(!isEmpty){
        return;
    }
    if(parent != null){
        state.form.flip(parent);
        state.viewDefinition.flip(parent);
    } else {
        state.form.clear();
        state.viewDefinition.clear();
    }
}

protected void showForm(){

    if(isDestroyed){
DebugException.start().explode("form has been destroyed").end();
    }

    if(isVisible){
        return;
    }
    onPrepare();
    getContainer().putView(view);
    isVisible = true;
}

protected void dismissForm(){

    if(isDestroyed){
DebugException.start().explode("form has been destroyed").end();
    }

    if(isEmpty){
        return;
    }
    if(state.viewDefinition != null){
        ViewDefinition current = state.viewDefinition.current();
        if(current != null){
            current.destroy();
        }
        state.viewDefinition.clear();
    }
    if(state.form != null){
        state.form.clear();
    }
    if(state.isBuilt()){
        state.clear();
        pickers = null;
    }
    if(view != null){
        getContainer().removeView(view);
        view = null;
    }
    isVisible = false;
    isEmpty = true;
}

protected void destroyForm(){
    if(isDestroyed){
        return;
    }
    if(state.viewDefinition != null){
        ViewDefinition current = state.viewDefinition.current();
        if(current != null){
            current.destroy();
        }
    }
    isDestroyed = true;
    state.detach(this);
}

public void show(){
    getBehavior().show();
}

public void close(){
    getBehavior().close();
}

public void destroy(){
    getBehavior().destroy();
}

public void showError(){
    if(hasFormManager()){
        for(defFormComponent picker: pickers.iterableValues()){
            picker.showError();
        }
    }
    if(hasNext()){
        next().showError();
    }
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

public interface Behavior{
    void attach(FormComponentHolder holder);

    void show();

    void close();

    void destroy();

}

protected static class DefaultDefinitionFlipper<DEFINITION extends FormComponentHolder.ViewDefinition> implements ViewDefinition.Flipper<DEFINITION>{
    DEFINITION viewDefinition;

    DefaultDefinitionFlipper(DEFINITION viewDefinition){
        this.viewDefinition = viewDefinition;
    }

    @Override
    public DEFINITION current(){
        return viewDefinition;
    }

    @Override
    public void flip(FormManager parent){

DebugException.start().notImplemented().end();

    }

    @Override
    public void clear(){
    }

}

public static abstract class ViewDefinition{
    protected FormComponentHolder formHolder;

    public ViewDefinition(){
DebugTrack.start().create(this).end();
    }

    <V extends ViewDefinition> V attach(FormComponentHolder formHolder){
        this.formHolder = formHolder;
        return (V)this;
    }

    public ListEntry<Target.Is, ? extends defFormComponent> getPickers(){
        return formHolder.getPickers();
    }

    public FormComponentHolder getFormHolder(){
        return formHolder;
    }

    abstract public Id.Is getFormId();

    abstract public int getLayoutId();

    abstract public ListEntry<Target.Is, Integer> getViewsId();

    abstract public void init();

    abstract public void resume();

    abstract public void destroy();

    @Override
    protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
        super.finalize();
    }

    public interface Flipper<DEFINITION extends ViewDefinition>{
        DEFINITION current();

        void flip(FormManager parent);

        void clear();

    }

}

public static class State extends com.tezov.lib_java_android.ui.state.State{
    boolean isBuilt = false;
    ViewDefinition.Flipper viewDefinition = null;
    FormManager.Flipper form = null;

    @Override
    public FormComponentHolder getOwner(){
        return (FormComponentHolder)super.getOwner();
    }

    protected boolean isBuilt(){
        return isBuilt;
    }

    protected void build(){
        isBuilt = true;
    }

    void restore(){

    }

    void clear(){
        isBuilt = false;
    }

}

//BEHAVIOR
public static class DefaultBehavior implements Behavior{
    WR<FormComponentHolder> holderWR;

    protected FormComponentHolder getHolder(){
        return holderWR.get();
    }

    @Override
    public void attach(FormComponentHolder holder){
        this.holderWR = WR.newInstance(holder);
    }

    @Override
    public void show(){
        getHolder().showForm();
    }

    @Override
    public void close(){
        getHolder().dismissForm();
    }

    @Override
    public void destroy(){
        getHolder().destroyForm();
    }

}

public static class RootBehavior implements Behavior{
    WR<FormComponentHolder> holderWR;

    protected FormComponentHolder getHolder(){
        return holderWR.get();
    }

    @Override
    public void attach(FormComponentHolder holder){
        this.holderWR = WR.newInstance(holder);
    }

    @Override
    public void show(){
        FormComponentHolder holder = getHolder();
        FormComponentHolder parentHolder = holder.previous();
        do{
            if(parentHolder != null){
                FormManager parentForm = parentHolder.getFormManager();
                if(!holder.isFormValid(parentForm) || !holder.isVisible()){
                    if(holder.isVisible()){
                        holder.dismissForm();
                    }
                    holder.flipForm(parentHolder.getFormManager());
                    if(holder.getFormManager() != null){
                        holder.showForm();
                        defFormComponent.linkPickers(parentHolder.getOutputForm(), holder.getInputForm());
                    } else {
                        defFormComponent.linkPickers(parentHolder.getOutputForm(), null);
                    }
                }
            } else {
                holder.showForm();
                defFormComponent.linkPickers(null, holder.getInputForm());
            }
            parentHolder = holder;
            holder = holder.next();
        } while(holder != null);
        defFormComponent.linkPickers(parentHolder.getOutputForm(), null);
    }

    @Override
    public void close(){
        FormComponentHolder holder = getHolder();
        FormComponentHolder parentHolder = holder.previous();
        if(parentHolder != null){
            defFormComponent.linkPickers(parentHolder.getOutputForm(), null);
        }
        do{
            holder.dismissForm();
            holder = holder.next();
        } while(holder != null);
    }

    @Override
    public void destroy(){
        FormComponentHolder holder = getHolder();
        do{
            holder.destroyForm();
            holder = holder.next();
        } while(holder != null);
    }

}

}
