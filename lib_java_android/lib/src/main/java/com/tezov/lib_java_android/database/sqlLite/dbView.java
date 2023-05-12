/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.database.sqlLite;

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
import androidx.annotation.NonNull;

import com.tezov.lib_java.async.notifier.Subscription;
import com.tezov.lib_java.async.notifier.observer.event.ObserverEvent;
import com.tezov.lib_java.async.notifier.observer.value.ObserverValue;
import com.tezov.lib_java_android.database.ItemBase;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilter;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder.Direction;
import com.tezov.lib_java_android.database.sqlLite.filter.dbSign;
import com.tezov.lib_java.definition.defProvider;
import com.tezov.lib_java.toolbox.Compare;
import com.tezov.lib_java.toolbox.IteratorBuffer;
import com.tezov.lib_java.toolbox.Nullify;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.collection.ListOrObject;

import java.util.List;

import static com.tezov.lib_java_android.database.sqlLite.dbTable.Event;

public class dbView<ITEM extends ItemBase<ITEM>> implements Iterable<ITEM>, defProvider<ITEM>{
private static final Integer PRELOAD_SIZE = 30;
private static final Integer PRELOAD_TRIGGER = 10;
private final dbTable<ITEM>.Ref db;
private final SRwNOC<ITEM> size;
private boolean isSizeValid;
private boolean notificationEnabled = true;

public dbView(dbTable<ITEM>.Ref db){
DebugTrack.start().create(this).end();
    this.db = db;
    isSizeValid = false;
    this.size = new SRwNOC<>(0);
}

public dbView<ITEM> setPrimary(dbField.Is field, Direction direction){
    db.getFilterNoSync().setPrimary(field, direction);
    return this;
}

@Override
final public void toDebugLog(){
DebugLog.start().send(db.select()).end();
}

protected dbView<ITEM> me(){
    return this;
}

public <D extends dbTable<ITEM>.Ref> D database(){
    return (D)db;
}

public dbFilter getFilterNoSync(){
    return db.getFilterNoSync();
}

public Subscription observeOnChanged(ObserverValue<SRwNOC.Value<ITEM>> observer){
    if(!size.hasObserver()){
        db.observe((ObserverEvent)new SizeObserver(this));
    }
    return size.observe(observer);
}
public void unObserveOnChanged(Object owner){
    size.unObserve(owner);
    if(!size.hasObserver()){
        db.unObserve(this);
    }
}
public void unObserveOnChangedAll(){
    size.unObserveAll();
    db.unObserve(this);
}

private void invalidate(){
    isSizeValid = false;
}

public <VIEW extends dbView<ITEM>> VIEW enableNotification(boolean flag){
    notificationEnabled = flag;
    return (VIEW)this;
}

public <VIEW extends dbView<ITEM>> VIEW where(dbField.Is field, dbSign.Is sign, Object value, boolean retain){
    invalidate();
    db.getFilterNoSync().where(field, sign, value, retain);
    return (VIEW)this;
}
public <VIEW extends dbView<ITEM>> VIEW where(dbField.Is field, Object value, boolean retain){
    invalidate();
    db.getFilterNoSync().where(field, value, retain);
    return (VIEW)this;
}
public <VIEW extends dbView<ITEM>> VIEW whereWeak(dbField.Is field, dbSign.Is sign, Object value){
    invalidate();
    db.getFilterNoSync().whereWeak(field, sign, value);
    return (VIEW)this;
}
public <VIEW extends dbView<ITEM>> VIEW whereWeak(dbField.Is field, Object value){
    invalidate();
    db.getFilterNoSync().whereWeak(field, value);
    return (VIEW)this;
}

public <VIEW extends dbView<ITEM>> VIEW order(dbField.Is field, Direction value, boolean retain){
    invalidate();
    db.getFilterNoSync().order(field, value, retain);
    return (VIEW)this;
}
public <VIEW extends dbView<ITEM>> VIEW orderWeak(dbField.Is field, Direction value){
    invalidate();
    db.getFilterNoSync().orderWeak(field, value);
    return (VIEW)this;
}

public <VIEW extends dbView<ITEM>> VIEW group(dbField.Is field, boolean value, boolean retain){
    invalidate();
    db.getFilterNoSync().group(field, value, retain);
    return (VIEW)this;
}
public <VIEW extends dbView<ITEM>> VIEW groupWeak(dbField.Is field, boolean value){
    invalidate();
    db.getFilterNoSync().groupWeak(field, value);
    return (VIEW)this;
}

@Override
public IteratorBuffer<ITEM> iterator(){
    return new IteratorBuffer<>(this).setSizeToDownload(PRELOAD_SIZE).setTriggerSizeBeforeDownload(PRELOAD_TRIGGER);
}

@Override
public boolean tryAcquireLock(Object inquirer){

DebugException.start().notImplemented().end();

    return false;
}

@Override
public void acquireLock(Object inquirer){

DebugException.start().notImplemented().end();


}

@Override
public void releaseLock(Object inquirer){

DebugException.start().notImplemented().end();


}

@Override
public Integer getFirstIndex(){
    if(size() == 0){
        return null;
    } else {
        return 0;
    }
}

@Override
public Integer getLastIndex(){
    if(size() == 0){
        return null;
    } else {
        return size() - 1;
    }
}

private void notifySizeChange(Event.Is event, ListOrObject<ITEM> items){
    int newSize = db.size();
    boolean sizeDidNotChange = Compare.equals(size, newSize);
    if((event == Event.INSERT) || (event == Event.INSERT_LIST)){
        if(sizeDidNotChange){
            return;
        }
    } else if((event == Event.REMOVE) || (event == Event.REMOVE_LIST)){
        if(sizeDidNotChange){
            return;
        }
    } else if(event == Event.CLEAR){
        if(sizeDidNotChange){
            return;
        }
    } else if(event == Event.UPDATE){
        if(sizeDidNotChange && (indexOf(items.get()) == null)){
            return;
        }
    } else if(event == Event.UPDATE_LIST){
        if(sizeDidNotChange){
            boolean hasItem = false;
            for(ITEM item: items){
                Integer index = indexOf(item);
                if(index != null){
                    hasItem = true;
                    break;
                }
            }
            if(!hasItem){
                return;
            }
        }
    }
    if(notificationEnabled){
        size.set(newSize, items);
    } else {
        invalidate();
    }
}

@Override
public int size(){
    if(!isSizeValid){
        size.set(db.size());
        isSizeValid = true;
    }
    return size.get();
}

@Override
public List<ITEM> select(int index, int length){
    return db.select(index, length);
}

@Override
public ITEM get(int index){
    return db.get(index);
}

public ITEM get(){
    return db.get();
}

@Override
public Integer indexOf(ITEM item){
    return db.indexOf(item);
}

@Override
public ITEM putToTrash(int index){
    return db.putToTrash(get(index));
}

@Override
public ITEM restoreFromTrash(int index, ITEM item){
    return db.restoreFromTrash(item);
}

@Override
public ITEM remove(int index){
    return db.remove(get(index));
}

@Override
protected void finalize() throws Throwable{
    db.unObserve(this);
DebugTrack.start().destroy(this).end();
    super.finalize();
}

private static class SizeObserver extends ObserverEvent<Event.Is, ListOrObject>{
    public SizeObserver(dbView owner){
        super(owner);
    }

    @Override
    public dbView getOwner(){
        return super.getOwner();
    }

    @Override
    public void onComplete(Event.Is event, ListOrObject list){
        getOwner().notifySizeChange(event, list);
    }

}

public static class SRwNOC<ITEM> extends com.tezov.lib_java.type.ref.SRwNOC<Integer>{
    public SRwNOC(Integer integer){
        super(integer);
    }

    @Override
    protected Value<ITEM> newValue(){
        return new Value<>();
    }

    @Override
    protected Value<ITEM> getValue(){
        return super.getValue();
    }

    @Override
    public void set(Integer size){
        getValue().set((ListOrObject<ITEM>)null);
        notificationEnable(false);
        super.set(size);
        notificationEnable(true);
    }

    public void set(Integer size, ListOrObject<ITEM> itemsChanged){
        getValue().set(Nullify.collection(itemsChanged));
        super.set(size);
    }

    public static class Value<ITEM> extends com.tezov.lib_java.type.ref.SRwNOC.Value<Integer>{
        public ListOrObject<ITEM> itemsChanged = null;

        public Value<ITEM> set(ListOrObject<ITEM> itemsChanged){
            this.itemsChanged = itemsChanged;
            return this;
        }

        @NonNull
        @Override
        public DebugString toDebugString(){
            DebugString data = super.toDebugString();
            if(itemsChanged != null){
                for(ITEM item: itemsChanged){
                    data.append("\n").append(item);
                }
            }
            return data;
        }

    }

}

}
