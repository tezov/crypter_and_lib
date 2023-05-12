/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.form.adapter;

import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.util.UtilsString;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import androidx.fragment.app.Fragment;
import com.tezov.lib_java.async.notifier.Notifier;
import com.tezov.lib_java.async.notifier.observable.ObservableValue;
import com.tezov.lib_java.async.notifier.observer.value.ObserverValue;
import com.tezov.lib_java.definition.defEntry;
import com.tezov.lib_java.definition.defValidable;
import com.tezov.lib_java.toolbox.Compare;
import com.tezov.lib_java.toolbox.Nullify;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.collection.ListEntry;
import com.tezov.lib_java.type.defEnum.EnumBase;
import com.tezov.lib_java.type.primaire.Entry;
import com.tezov.lib_java.type.ref.Ref;
import com.tezov.lib_java.type.ref.WR;
import com.tezov.lib_java.data.validator.Validator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class FormManager implements defValidable{
private final Id.Is id;
private final Notifier<Void> onSetNotifier;
private final List<FormAdapter> adapters;
private boolean isReady = false;
private WR<FormManager> parentWR = null;
private ListEntry<Id.Is, FormManager> children = null;

public FormManager(Id.Is id){
DebugTrack.start().create(this).end();
    this.id = id;
    onSetNotifier = new Notifier<>(new ObservableValue<FormAdapter>(), false);
    adapters = new ArrayList<>();
}

public Id.Is getId(){
    return id;
}

public boolean hasParent(){
    return Ref.isNotNull(parentWR);
}

public Id.Is getParentId(){
    if(Ref.isNull(parentWR)){
        return null;
    } else {
        return Ref.get(parentWR).getId();
    }
}

public FormManager getParent(){
    return Ref.get(parentWR);
}

public FormManager setParent(FormManager parent){
    if(Ref.isNotNull(parentWR)){
        FormManager currentParent = getParent();

        if(currentParent == parent){
DebugException.start().explode("parent key " + parent.id.name() + " is already parent").end();
        }


        currentParent.removeChild(id);
    }
    if(parent != null){
        parent.putChild(id, this);
    }
    return this;
}

public boolean hasChildren(){
    return children != null;
}

public ListEntry<Id.Is, FormManager> getChildren(){
    return children;
}

public <FM extends FormManager> FM getChild(Id.Is id){
    if(children == null){
        return null;
    }
    return (FM)children.getValue(id);
}

public void putChild(Id.Is id, FormManager child){
    if(children == null){
        children = new ListEntry<>();
    }

    if(children.hasKey(id)){
DebugException.start().explode("Children key " + id.name() + " already exist").end();
    }


    child.parentWR = WR.newInstance(this);
    children.add(id, child);
    for(FormAdapter adapter: child.getAdapters()){
        addAdapter(adapter);
    }
}

public void removeChild(Id.Is id){
    if(children == null){
        return;
    }
    FormManager child = children.removeKey(id);
    children = Nullify.collection(children);
    if(child == null){
        return;
    }
    child.parentWR = null;
    for(FormAdapter adapter: child.getAdapters()){
        removeAdapter(adapter);
    }
}

public void clearChildren(){
    if(children == null){
        return;
    }
    for(Entry<Id.Is, FormManager> e: children){
        e.value.setParent(null);
    }
}

public boolean isReady(){
    return isReady;
}

public void setReady(){
    isReady = true;
}

protected List<FormAdapter> getAdapters(){
    return adapters;
}

private void addAdapter(FormAdapter adapter){
    getAdapters().add(adapter);
    if(hasParent()){
        getParent().addAdapter(adapter);
    }
}

public void addAdapter(Object initialValue, FormAdapter adapter){
    adapter.attach(this);
    if(initialValue != null){
        adapter.setValue(initialValue.getClass(), initialValue);
    } else {
        adapter.setValue(null);
    }
    addAdapter(adapter);
}

private void removeAdapter(FormAdapter adapter){
    getAdapters().remove(adapter);
    if(hasParent()){
        getParent().removeAdapter(adapter);
    }
}

public void removeAdapter(Target.Is target){
    Iterator<FormAdapter> iterator = getAdapters().iterator();
    while(iterator.hasNext()){
        FormAdapter adapter = iterator.next();
        if(adapter.getTarget().equals(target)){
            iterator.remove();
            if(hasParent()){
                getParent().removeAdapter(adapter);
            }
            return;
        }
    }
}

protected boolean hasAdapter(FormAdapter adapter){
    return getAdapters().contains(adapter);
}

public FormAdapter getAdapter(Target.Is target){
    for(FormAdapter adapter: getAdapters()){
        if(adapter.getTarget().equals(target)){
            return adapter;
        }
    }
    return null;
}

public FormAdapter getAdapter(int index){
    return getAdapters().get(index);
}

public int adaptersSize(){
    return getAdapters().size();
}

public void link(Target.Is target, defEntry entry){
    FormAdapter adapter = getAdapter(target);
    adapter.attach(entry);
    entry.attach(adapter);
}

public void link(ListEntry<? extends Target.Is, ? extends defEntry> pickers){
    for(Entry<? extends Target.Is, ? extends defEntry> e: pickers){
        link(e.key, e.value);
    }
}

@Override
public <S, K, V extends Validator<S, K>> V getValidator(){

DebugException.start().notImplemented().end();

    return null;
}

public <V extends Validator> V getValidator(Target.Is target){
    return (V)getAdapter(target).getValidator();
}

public Boolean hasValidator(Target.Is target){
    FormAdapter adapter = getAdapter(target);
    if(adapter == null){
        return null;
    } else {
        return adapter.hasValidator();
    }
}

public boolean hasValidator(List<Target.Is> targets){
    for(Target.Is target: targets){
        if(Compare.isFalseOrNull(hasValidator(target))){
            return false;
        }
    }
    return true;
}

@Override
public boolean hasValidator(){
    for(FormAdapter adapter: getAdapters()){
        if(!adapter.hasValidator()){
            return false;
        }
    }
    return true;
}

public int countValidator(List<Target.Is> targets){
    int count = 0;
    for(Target.Is target: targets){
        if(Compare.isTrue(hasValidator(target))){
            count++;
        }
    }
    return count;
}

public int countValidator(){
    int count = 0;
    for(FormAdapter adapter: getAdapters()){
        if(adapter.hasValidator()){
            count++;
        }
    }
    return count;
}

public Boolean isValid(Target.Is target){
    FormAdapter adapter = getAdapter(target);
    if(adapter == null){
        return null;
    } else {
        return adapter.isValid();
    }
}

public boolean isValid(List<Target.Is> targets){
    for(Target.Is target: targets){
        if(Compare.isFalse(isValid(target))){
            return false;
        }
    }
    return true;
}

@Override
public Boolean isValid(){
    for(FormAdapter adapter: getAdapters()){
        if(Compare.isFalse(adapter.isValid())){
            return false;
        }
    }
    return true;
}

public int countValid(List<Target.Is> targets){
    int count = 0;
    for(Target.Is target: targets){
        if(Compare.isTrue(isValid(target))){
            count++;
        }
    }
    return count;
}

public int countValid(){
    int count = 0;
    for(FormAdapter adapter: getAdapters()){
        if(Compare.isTrue(adapter.isValid())){
            count++;
        }
    }
    return count;
}

public Boolean hasChanged(Target.Is target){
    FormAdapter adapter = getAdapter(target);
    if(adapter == null){
        return null;
    } else {
        return adapter.hasChanged();
    }
}

public Boolean hasChanged(List<Target.Is> targets){
    for(Target.Is target: targets){
        if(Compare.isTrue(hasChanged(target))){
            return true;
        }
    }
    return false;
}

@Override
public Boolean hasChanged(){
    for(FormAdapter adapter: getAdapters()){
        if(Compare.isTrue(adapter.hasChanged())){
            return true;
        }
    }
    return false;
}

public int countChanged(List<Target.Is> targets){
    int count = 0;
    for(Target.Is target: targets){
        if(Compare.isTrue(hasChanged(target))){
            count++;
        }
    }
    return count;
}

public int countChanged(){
    int count = 0;
    for(FormAdapter adapter: getAdapters()){
        if(adapter.hasChanged()){
            count++;
        }
    }
    return count;
}

public Boolean hasChangedFromInitial(Target.Is target){
    FormAdapter adapter = getAdapter(target);
    if(adapter == null){
        return null;
    } else {
        return adapter.hasChangedFromInitial();
    }

}

public Boolean hasChangedFromInitial(List<Target.Is> targets){
    for(Target.Is target: targets){
        if(Compare.isTrue(hasChangedFromInitial(target))){
            return true;
        }
    }
    return false;
}

@Override
public Boolean hasChangedFromInitial(){
    for(FormAdapter adapter: getAdapters()){
        if(Compare.isTrue(adapter.hasChangedFromInitial())){
            return true;
        }
    }
    return false;
}

public int countChangedFromInitial(List<Target.Is> targets){
    int count = 0;
    for(Target.Is target: targets){
        if(Compare.isTrue(hasChangedFromInitial(target))){
            count++;
        }
    }
    return count;
}

public int countChangedFromInitial(){
    int count = 0;
    for(FormAdapter adapter: getAdapters()){
        if(Compare.isTrue(adapter.hasChangedFromInitial())){
            count++;
        }
    }
    return count;
}

public Notifier.Subscription observeOnSet(ObserverValue<FormAdapter> observer){
    return onSetNotifier.register(observer);
}

public void unObserveOnSet(Object owner){
    onSetNotifier.unregister(owner);
}

public void unObserveOnSetAll(){
    onSetNotifier.unregisterAll();
}

private void postOnSet(FormAdapter adapter){
    ObservableValue<FormAdapter>.Access access = onSetNotifier.obtainAccess(this, null);
    access.setValue(adapter);
}

protected boolean notifySetValue(FormAdapter adapter){
    if(onSetValue(adapter)){
        postOnSet(adapter);
        return true;
    } else if(children != null){
        for(Entry<Id.Is, FormManager> e: children){
            if(!e.value.onSetValue(adapter)){
                continue;
            }
            postOnSet(adapter);
            return true;
        }
        return false;
    } else {
        return false;
    }
}

protected boolean onSetValue(FormAdapter adapter){
    return true;
}

protected void setItem(FormAdapter adapter){
    if(children == null){
        return;
    }
    for(Entry<Id.Is, FormManager> e: children){
        if(e.value.onSetItem(adapter)){
            break;
        }
    }
}

protected boolean onSetItem(FormAdapter adapter){
    return true;
}

public boolean offer(){

DebugException.start().notImplemented().end();

    return true;
}

protected boolean offerChildren(){
    if(children == null){
        return true;
    }
    for(Entry<Id.Is, FormManager> e: children){
        if(!e.value.offer()){
            return false;
        }
        if(!e.value.offerChildren()){
            return false;
        }
    }
    return true;
}

public DebugString toDebugString(){
    DebugString data = new DebugString();
    data.append("getId().name()", getId().name());
    data.append("valid", isValid());
    data.append("countValid()", countValid());
    data.append("size", getAdapters().size());
    data.append("hasChangedFromInitial()", hasChangedFromInitial());
    data.appendFullSimpleName("validator", getValidator());
    data.appendFullSimpleName("countValidator()", countValidator());
    data.append("\n");
    for(FormAdapter adapter: adapters){
        data.append(adapter.toDebugString() + "\n");
    }
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

public interface Flipper{
    <F extends FormManager> F current();

    boolean isValid(FormManager parent);

    void clear();

    void flip(FormManager parent);

}

public interface Id{
    Is ROOT = new Is("ROOT");
    Is SUB = new Is("SUB");

    class Is extends EnumBase.Is{
        public Is(String name){
            super(name);
        }

    }

}

public interface Target{
    static <I extends Is> I nextRandom(Class<I> type){
        return Target.Is.nextRandom(type);
    }

    class Is extends EnumBase.Is{
        public Is(String name){
            super(name);
        }

    }

}

public static class DefaultFlipper implements Flipper{
    FormManager form;

    public DefaultFlipper(FormManager form){
        this.form = form;
    }

    @Override
    public <F extends FormManager> F current(){
        return (F)form;
    }

    @Override
    public boolean isValid(FormManager parent){
        return true;
    }

    @Override
    public void clear(){
    }

    @Override
    public void flip(FormManager parent){

DebugException.start().notImplemented().end();

    }

}


}
