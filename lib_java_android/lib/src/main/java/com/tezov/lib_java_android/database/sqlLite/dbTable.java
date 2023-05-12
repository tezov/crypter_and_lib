/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.database.sqlLite;

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.util.UtilsString;
import java.util.Set;
import androidx.fragment.app.Fragment;

import android.database.Cursor;

import com.tezov.lib_java.async.LockThread;
import com.tezov.lib_java.async.notifier.Notifier;
import com.tezov.lib_java.async.notifier.observable.ObservableEvent;
import com.tezov.lib_java.async.notifier.observer.event.ObserverEvent;
import com.tezov.lib_java.cipher.definition.defEncoder;
import com.tezov.lib_java_android.database.ItemBase;
import com.tezov.lib_java_android.database.TableDescription;
import com.tezov.lib_java_android.database.adapter.definition.defContentValuesTo;
import com.tezov.lib_java_android.database.adapter.definition.defParcelTo;
import com.tezov.lib_java_android.database.sqlLite.adapter.defCursorTo;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilter;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.dbSign;
import com.tezov.lib_java_android.database.sqlLite.holder.dbTablesHandle;
import com.tezov.lib_java_android.definition.defCreatable;
import com.tezov.lib_java.definition.defProvider;
import com.tezov.lib_java.generator.uid.defUIDGenerator;
import com.tezov.lib_java.generator.uid.defUid;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java_android.type.android.wrapper.ContentValuesW;
import com.tezov.lib_java.wrapperAnonymous.ConsumerThrowW;
import com.tezov.lib_java.wrapperAnonymous.PredicateThrowW;
import com.tezov.lib_java.type.collection.ListOrObject;
import com.tezov.lib_java.type.defEnum.EnumBase;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public abstract class dbTable<ITEM extends ItemBase<ITEM>>{
public dbTablesHandle db = null;
private LockThread<dbTable<ITEM>> lockThread = null;
private dbTable<ITEM>.Ref mainRef = null;
private dbFilter nullFilter = null;
private dbTableDefinition.Ref tableDefinition;
private defCursorTo cursorTo;
private defParcelTo parcelTo;
private defContentValuesTo contentValuesTo;
private Notifier<Event.Is> notifier;

protected dbTable(){
    try{
DebugTrack.start().create(this).end();
        notifier = new Notifier<>(new ObservableEvent<Event.Is, Object>(), false);
    } catch(java.lang.Throwable e){

DebugException.start().log(e).end();

    }
}

private dbTable<ITEM> me(){
    return this;
}

public void setDatabase(dbTablesHandle db){
    this.db = db;
}

protected dbTableDefinition.Ref newTableDefinition(TableDescription description, defEncoder encoderField){
    return new dbTableDefinition(description, encoderField).newRef(null);
}

public dbTable<ITEM> setTableDescription(TableDescription description, defEncoder encoderField){
    return setTableDefinition(newTableDefinition(description, encoderField));
}

protected dbTableDefinition.Ref newTableDefinition(dbTableDefinition definition){
    return definition.newRef(null);
}

public dbTableDefinition.Ref getTableDefinition(){
    return tableDefinition;
}

public dbTable<ITEM> setTableDefinition(dbTableDefinition definition){
    return setTableDefinition(newTableDefinition(definition));
}

public dbTable<ITEM> setTableDefinition(dbTableDefinition.Ref definition){
    this.tableDefinition = definition;
    createMainRef();
    return this;
}

public String getName(){
    return tableDefinition.getName();
}

public String getNameEncoded(){
    return tableDefinition.getNameEncoded();
}

public abstract defCreatable<ITEM> factory();

public Class<ITEM> getType(){
    return factory().getType();
}

public abstract <GEN extends defUIDGenerator> GEN getUidGenerator();

private <UID extends defUid> UID generateUID(ContentValuesW contentValues){
    UID uid;
    Long id;
    do{
        uid = (UID)getUidGenerator().next();
        id = mainRef().getPrimaryKey(uid);
    } while(id != null);
    contentValues.get().put(tableDefinition.fieldName(dbField.UID), uid.toBytes());
    return uid;
}

public ContentValuesW toContentValues(ITEM item){
    return parcelTo.contentValues(item, getTableDefinition().getFields());
}

public ITEM toItem(ContentValuesW contentValues){
    return contentValuesTo.item(contentValues, getTableDefinition().getFields(), factory());
}

public ITEM toItem(Cursor cursor){
    return cursorTo.item(cursor, getTableDefinition().getFields(), factory());
}

public <UID extends defUid> UID getUID(ContentValuesW contentValues){
    byte[] data = contentValues.get().getAsByteArray(tableDefinition.fieldName(dbField.UID));
    if(data == null){
        return null;
    } else {
        return (UID)getUidGenerator().make(data);
    }
}

public dbTable<ITEM> setCursorTo(defCursorTo cursorTo){
    this.cursorTo = cursorTo;
    return this;
}

public dbTable<ITEM> setParcelTo(defParcelTo parcelTo){
    this.parcelTo = parcelTo;
    return this;
}

public dbTable<ITEM> setContentValuesTo(defContentValuesTo contentValuesTo){
    this.contentValuesTo = contentValuesTo;
    return this;
}

protected dbTable<ITEM>.Ref createRef(){
    return new Ref();
}

public <R extends dbTable<ITEM>.Ref> R newRef(){
    synchronized(me()){
        return (R)createRef();
    }
}

private void createMainRef(){
    mainRef = createRef();
    nullFilter = newFilter();
}

public <R extends dbTable<ITEM>.Ref> R mainRef(){
    synchronized(me()){
        return (R)mainRef;
    }
}

public dbFilter newFilter(){
    return new dbFilter().addDefinition(tableDefinition);
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

public interface Event{
    Is INSERT = new Is("INSERT");
    Is INSERT_LIST = new Is("INSERT_LIST");
    Is UPDATE = new Is("UPDATE");
    Is UPDATE_LIST = new Is("UPDATE_LIST");
    Is REMOVE = new Is("REMOVE");
    Is REMOVE_LIST = new Is("REMOVE_LIST");
    Is CLEAR = new Is("CLEAR");

    class Is extends EnumBase.Is{
        public Is(String value){
            super(value);
        }

    }

}

public class Ref implements defProvider<ITEM>{
    final private static int TO_LOG_DELAY_LENGTH = 100;
    private dbFilter filter;

    protected Ref(){
        try{
DebugTrack.start().create(this).end();
            this.filter = newFilter();
        } catch(java.lang.Throwable e){

DebugException.start().log(e).end();

        }
    }

    private LockThread getLockThread(){
        if(lockThread == null){
            lockThread = new LockThread(me());
        }
        return lockThread;
    }

    @Override
    final public boolean tryAcquireLock(Object inquirer){
        return getLockThread().tryLock(inquirer);
    }

    @Override
    final public void acquireLock(Object inquirer){
        getLockThread().lock(inquirer);
    }

    @Override
    final public void releaseLock(Object inquirer){
        getLockThread().unlock(inquirer);
    }

    public dbTableDefinition.Ref getTableDefinition(){
        return me().getTableDefinition();
    }

    public String getName(){
        return me().getName();
    }

    public String getNameEncoded(){
        return me().getNameEncoded();
    }

    public Class<ITEM> getType(){
        return me().getType();
    }

    private void post(Event.Is event, Object object){
        ObservableEvent<Event.Is, Object>.Access access = notifier.obtainAccess(this, event);
        access.setValue(ListOrObject.with(object));
    }

    public Notifier.Subscription observe(ObserverEvent<Event.Is, ListOrObject<ITEM>> observer){
        return notifier.register(observer);
    }

    public void unObserve(Object owner){
        notifier.unregister(owner);
    }

    public dbFilter newFilter(){
        return me().newFilter();
    }

    public dbFilter getFilterNoSync(){
        filter.invalidate();
        return filter;
    }

    public dbFilter getFilter(){
        synchronized(me()){
            return getFilterNoSync();
        }
    }

    public void setFilter(dbFilter filter){

        if(this == mainRef()){
DebugException.start().log(DebugTrack.getFullSimpleName(this) + " used setFilter on main reference").end();

        }


        synchronized(me()){
            this.filter = filter;
        }
    }

    public dbFilter getNullFilterNoSync(){
        nullFilter.invalidate();
        return nullFilter;
    }

    public dbFilter getNullFilter(){
        synchronized(me()){
            return getNullFilterNoSync();
        }
    }

    public <R extends dbTable<ITEM>.Ref> R where(dbField.Is field, dbSign.Is sign, Object value, boolean retain){

        if(this == mainRef() && retain){
DebugException.start().log(DebugTrack.getFullSimpleName(this) + " used where filter on main reference").end();
        }

        synchronized(me()){
            getFilterNoSync().where(field, sign, value, retain);
        }
        return (R)this;
    }

    public <R extends dbTable<ITEM>.Ref> R where(dbField.Is field, Object value, boolean retain){

        if(this == mainRef() && retain){
DebugException.start().log(DebugTrack.getFullSimpleName(this) + " used where filter on main reference").end();
        }

        synchronized(me()){
            getFilterNoSync().where(field, value, retain);
        }
        return (R)this;
    }

    public <R extends dbTable<ITEM>.Ref> R group(dbField.Is field, boolean value, boolean retain){

        if(this == mainRef() && retain){
DebugException.start().log(DebugTrack.getFullSimpleName(this) + " used group filter on main reference").end();
        }

        synchronized(me()){
            getFilterNoSync().group(field, value, retain);
        }
        return (R)this;
    }

    public <R extends dbTable<ITEM>.Ref> R order(dbField.Is field, dbFilterOrder.Direction value, boolean retain){

        if(this == mainRef() && retain){
DebugException.start().log("used where order on main reference").end();
        }


        synchronized(me()){
            getFilterNoSync().order(field, value, retain);
        }
        return (R)this;
    }

    public <GEN extends defUIDGenerator> GEN getUidGenerator(){
        return me().getUidGenerator();
    }

    public ContentValuesW toContentValues(ITEM item){
        return me().toContentValues(item);
    }

    public ITEM toItem(ContentValuesW contentValues){
        return me().toItem(contentValues);
    }

    public ITEM toItem(Cursor cursor){
        return me().toItem(cursor);
    }

    public <UID extends defUid> UID getUID(ContentValuesW contentValues){
        return me().getUID(contentValues);
    }

    public Long getPrimaryKey(defUid uid){
        synchronized(me()){
            return getPrimaryKey(getNullFilterNoSync(), uid);
        }
    }

    public Long getPrimaryKey(dbFilter filter, defUid uid){
        if(uid == null){
            return null;
        }
        ChunkCommand selectQuery = filter.where(dbField.UID, uid, false).statementSelectField(dbField.PRIMARY_KEY);
        Cursor cursor = db.rawQuery(selectQuery);
        Long primaryKey = null;
        if(cursor.getCount() > 0){
            cursor.moveToFirst();
            primaryKey = cursor.getLong(0);
        }
        cursor.close();
        return primaryKey;
    }

    public int size(dbFilter filter){
        ChunkCommand selectQuery = filter.statementSize();
        Cursor cursor = db.rawQuery(selectQuery);
        cursor.getCount();
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public List<ITEM> remove(dbFilter filter){
        List<ITEM> list = select(filter);
        if(list == null){
            return null;
        }
        ChunkCommand selectQuery = filter.statementRemove();
        db.execSQL(selectQuery);
        post(Event.REMOVE_LIST, list);
        return list;
    }

    public void clear(){
        ChunkCommand selectQuery = new ChunkCommand().setCommand("DELETE FROM " + getTableDefinition().getName());
        db.execSQL(selectQuery);
        post(Event.CLEAR, null);
    }

    private defUid generateUidIfNotExist(ContentValuesW contentValues){
        defUid uid = getUID(contentValues);
        if(uid == null){
            uid = generateUID(contentValues);
        }
        return uid;
    }

    public ITEM insert(ITEM item){
        ContentValuesW contentValues = toContentValues(item);
        defUid uid = generateUidIfNotExist(contentValues);
        db.insert(getTableDefinition().getName(), contentValues.get());
        item = get(uid);
        if(item != null){
            post(Event.INSERT, item);
        }
        return item;
    }

    public List<ITEM> insert(List<ITEM> items){
        //generate Uid
        List<ContentValuesW> itemToInsert = new ArrayList<>();
        for(ITEM item: items){
            ContentValuesW contentValues = toContentValues(item);
            generateUidIfNotExist(contentValues);
            itemToInsert.add(contentValues);
        }
        synchronized(me()){
            //memorize last index before insert
            Cursor cursor = db.rawQuery(getNullFilterNoSync().statementSize());
            cursor.moveToFirst();
            int lastIndexBeforeInsert = cursor.getInt(0) - 1;
            cursor.close();
            //execute sql statement
            db.beginTransaction();
            for(ContentValuesW contentValues: itemToInsert){
                db.insert(getTableDefinition().getName(), contentValues.get());
            }
            db.endTransaction();
            //return new entries
            List<ITEM> itemsComplete = select(getNullFilterNoSync().range(lastIndexBeforeInsert + 1, items.size()));
            if(itemsComplete != null){
                post(Event.INSERT_LIST, itemsComplete);
            }
            return itemsComplete;
        }
    }

    public boolean update(ITEM item){
        ContentValuesW contentValues = parcelTo.contentValues(item, getTableDefinition().getFields());
        int rowNumberAffected = db.update(1, getTableDefinition().getName(), contentValues.get(),
                getTableDefinition().field(dbField.UID).getName() + "=x'" + getUID(contentValues).toHexString() + "'");
        if(rowNumberAffected == 1){
            post(Event.UPDATE, item);
        }
        return (rowNumberAffected == 1);
    }

    public boolean update(List<ITEM> items){
        List<ITEM> itemsComplete = new ArrayList<>();
        synchronized(me()){
            db.beginTransaction();
            for(ITEM item: items){
                ContentValuesW contentValues = toContentValues(item);
                int rowNumberAffected = db.update(1, getTableDefinition().getName(), contentValues.get(),
                        getTableDefinition().field(dbField.UID).getName() + "=x'" + getUID(contentValues).toHexString() + "'");
                if(rowNumberAffected == 1){
                    itemsComplete.add(item);
                }
            }
            db.endTransaction();
        }
        if(itemsComplete.size() > 0){
            post(Event.UPDATE_LIST, itemsComplete);
        }
        return (itemsComplete.size() == items.size());
    }

    public List<ITEM> select(dbFilter filter){
        ChunkCommand selectQuery = filter.statementSelect();
        Cursor cursor = db.rawQuery(selectQuery);
        List<ITEM> items = new ArrayList<>();
        if(cursor.getCount() > 0){
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                ITEM item = toItem(cursor);
                items.add(item);
                cursor.moveToNext();
            }
        }
        cursor.close();
        if(items.size() > 0){
            return items;
        } else {
            return null;
        }
    }

    public <T> List<T> select(dbFilter filter, dbField.Is field){
        ChunkCommand selectQuery = filter.group(field, true, false).statementSelectField(field);
        Cursor cursor = db.rawQuery(selectQuery);
        List<T> items = new ArrayList<>();
        if(cursor.getCount() > 0){
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                T item = cursorTo.valueFrom(cursor, getTableDefinition().field(field), 0);
                items.add(item);
                cursor.moveToNext();
            }
        }
        cursor.close();
        if(items.size() > 0){
            return items;
        } else {
            return null;
        }
    }

    public <T> List<T> select(dbField.Is field){
        synchronized(me()){
            return select(getFilterNoSync(), field);
        }
    }

    public ITEM get(dbFilter filter){
        List<ITEM> items = select(filter);
        if(items != null){
            return items.get(0);
        } else {
            return null;
        }
    }

    public List<ITEM> select(defUid uidStart, defUid uidEnd){
        synchronized(me()){
            return select(getNullFilterNoSync().between(uidStart, uidEnd));
        }
    }

    public List<ITEM> select(List<defUid> uids){
        return select(newFilter().uidIn(dbField.UID, uids));
    }

    public List<ITEM> putToTrash(dbFilter filter){
        List<ITEM> items = select(filter);
        if(items == null){
            return null;
        }
        List<ITEM> itemsInTrash = new LinkedList<>();
        for(ITEM item: items){
            update(item.setDeleted(true));
            itemsInTrash.add(item);
        }
        if(itemsInTrash.size() <= 0){
            return null;
        } else {
            return itemsInTrash;
        }
    }

    public List<ITEM> restoreFromTrash(dbFilter filter){
        List<ITEM> items = select(filter);
        if(items == null){
            return null;
        }
        List<ITEM> itemsInTrash = new LinkedList<>();
        for(ITEM item: items){
            update(item.setDeleted(false));
            itemsInTrash.add(item);
        }
        if(itemsInTrash.size() <= 0){
            return null;
        } else {
            return itemsInTrash;
        }
    }

    public List<ITEM> emptyFromTrash(){
        synchronized(me()){
            List<ITEM> items = select(getNullFilterNoSync().where(dbField.DELETED, true, false));
            if(items == null){
                return null;
            }
            remove(getNullFilterNoSync().where(dbField.DELETED, true, false));
            return items;
        }
    }

    public ITEM putToTrash(ITEM item){
        return putToTrash(item.getUid());
    }

    public ITEM putToTrash(defUid uid){
        List<ITEM> items;
        synchronized(me()){
            items = putToTrash(getNullFilterNoSync().where(dbField.UID, uid, false));
        }
        if(items != null){
            return items.get(0);
        }
        return null;
    }

    public ITEM restoreFromTrash(ITEM item){
        return restoreFromTrash(item.getUid());
    }

    public ITEM restoreFromTrash(defUid uid){
        List<ITEM> items;
        synchronized(me()){
            items = restoreFromTrash(getNullFilterNoSync().where(dbField.UID, uid, false));
        }
        if(items != null){
            return items.get(0);
        } else {
            return null;
        }
    }

    public ITEM remove(ITEM item){
        return remove(item.getUid());
    }

    public ITEM remove(defUid uid){
        List<ITEM> items;
        synchronized(me()){
            items = remove(getNullFilterNoSync().where(dbField.UID, uid, false));
        }
        if(items != null){
            return items.get(0);
        } else {
            return null;
        }
    }

    public List<ITEM> remove(List<ITEM> items){
        List<defUid> uids = new ArrayList<>();
        for(ITEM item: items){
            uids.add(item.getUid());
        }
        return removeUids(uids);
    }
    public List<ITEM> removeUids(List<defUid> uids){
        return remove(newFilter().uidIn(dbField.UID, uids));
    }

    public ITEM get(defUid uid){
        synchronized(me()){
            return get(getNullFilterNoSync().where(dbField.UID, uid, false));
        }
    }

    public List<ITEM> select(){
        synchronized(me()){
            return select(getFilterNoSync());
        }
    }

    public ITEM get(){
        List<ITEM> items;
        synchronized(me()){
            items = select(getFilterNoSync());
        }
        if(items != null){
            return items.get(0);
        } else {
            return null;
        }
    }

    @Override
    public ITEM get(int index){
        synchronized(me()){
            return get(getFilterNoSync().at(index));
        }
    }

    @Override
    public Integer getFirstIndex(){
        int size;
        synchronized(me()){
            size = size(getFilterNoSync());
        }
        if(size == 0){
            return null;
        } else {
            return 0;
        }
    }

    @Override
    public Integer getLastIndex(){
        synchronized(me()){
            int size = size(getFilterNoSync());
            if(size == 0){
                return null;
            } else {
                return size(getFilterNoSync()) - 1;
            }
        }
    }

    @Override
    public int size(){
        synchronized(me()){
            return size(getFilterNoSync());
        }
    }

    @Override
    public List<ITEM> select(int offset, int length){
        synchronized(me()){
            return select(getFilterNoSync().range(offset, length));
        }
    }

    @Override
    public Integer indexOf(ITEM item){
        synchronized(me()){
            dbFilter filter = getFilterNoSync();
            dbField.Is field = filter.getPrimaryField();
            Object value;
            if(field == dbField.PRIMARY_KEY){
                value = getPrimaryKey(filter, item.getUid());
            }
            else {
                value = toContentValues(item).getValue(field.name());
            }
            if(value == null){
DebugException.start().log("value is null, can not find indexOf " + getName() + ":" + field.name()).end();
                return null;
            }
            else {
                int index = size(getFilterNoSync().indexOf(value)) - 1;
                return index >= 0 ? index : null;
            }
        }
    }
    public Integer indexOf(defUid uid){
        synchronized(me()){
            dbFilter filter = getFilterNoSync();
            dbField.Is field = filter.getPrimaryField();
            Object value;
            if(field == dbField.PRIMARY_KEY){
                value = getPrimaryKey(filter, uid);
            }
            else {
                ITEM item = get(uid);
                if(item != null){
                    value = toContentValues(item).getValue(filter.getPrimaryField().name());
                }
                else{
                    value = null;
                }
            }
            if(value == null){
DebugException.start().log("value is null, can not find indexOf " + getName() + ":" + field.name()).end();
                return null;
            }
            else{
                int index = size(getFilterNoSync().indexOf(value)) - 1;
                return index >= 0 ? index : null;
            }
        }
    }

    public defUid getUID(int index){
        ITEM item = get(index);
        if(item == null){
            return null;
        }
        return item.getUid();
    }

    @Override
    public ITEM putToTrash(int index){
        return putToTrash(getUID(index));
    }

    public ITEM restoreFromTrash(int index){
        return restoreFromTrash(getUID(index));
    }

    @Override
    public ITEM restoreFromTrash(int index, ITEM item){
        return restoreFromTrash(item.getUid());
    }

    @Override
    public ITEM remove(int index){
        return remove(getUID(index));
    }

    public void forEachNoThrow(ConsumerThrowW<ITEM> consumer, int bufferLength){
        try{
            forEach(consumer, bufferLength);
        } catch(Throwable e){

        }
    }
    public void forEach(ConsumerThrowW<ITEM> consumer, int bufferLength) throws Throwable{
        int offset = 0;
        List<ITEM> items;
        while((items = select(offset, bufferLength)) != null){
            offset += items.size();
            for(ITEM t: items){
                consumer.accept(t);
            }
        }
    }

    public int forEachDeleteNoThrow(PredicateThrowW<ITEM> consumer, int bufferLength){
        try{
            return forEachDelete(consumer, bufferLength);
        } catch(Throwable e){
            return -1;
        }
    }
    public int forEachDelete(PredicateThrowW<ITEM> consumer, int bufferLength) throws Throwable{
        int deleted = 0;
        int offset = 0;
        List<ITEM> items;
        while((items = select(offset, bufferLength)) != null){
            offset += items.size();
            for(ITEM t: items){
                if(consumer.test(t)){
                    if(remove(t.getUid()) != null){
                        deleted++;
                        offset--;
                    }
                }
            }
        }
        return deleted;
    }

    @Override
    final public void toDebugLog(){
        forEachNoThrow(new ConsumerThrowW<ITEM>(){
            @Override
            public void accept(ITEM item) throws Throwable{
                item.toDebugLog();
            }
        }, TO_LOG_DELAY_LENGTH);
    }

    @Override
    protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
        super.finalize();
    }

}

}

