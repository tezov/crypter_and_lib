/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.database.prebuild.trash.item;

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.util.UtilsString;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import androidx.fragment.app.Fragment;

import android.os.Parcel;

import com.tezov.lib_java_android.database.ItemBase;
import com.tezov.lib_java_android.database.TableDescription;
import com.tezov.lib_java_android.database.prebuild.Descriptions;
import com.tezov.lib_java_android.database.sqlLite.dbTable;
import com.tezov.lib_java_android.definition.defCreatable;
import com.tezov.lib_java_android.factory.FactoryObject;
import com.tezov.lib_java.generator.uid.UUIDGenerator;
import com.tezov.lib_java.generator.uid.UidBase;
import com.tezov.lib_java.generator.uid.defUid;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.toolbox.Compare;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java.type.defEnum.EnumBase;
import com.tezov.lib_java_android.type.android.wrapper.ParcelW;

import org.threeten.bp.ZoneId;

public class ItemTrash extends ItemBase<ItemTrash>{
final public static Clock.FormatDateAndTime TIMESTAMP_DATE_FORMAT = Clock.FormatDateAndTime.FULL;
public static final Creator<ItemTrash> CREATOR = new Creator<ItemTrash>(){
    @Override
    public ItemTrash createFromParcel(Parcel p){
        ParcelW parcel = ParcelW.obtain().replace(p);
        return ItemTrash.obtain().replaceBy(parcel);
    }

    @Override
    public ItemTrash[] newArray(int size){
        return new ItemTrash[size];
    }
};
final private static UUIDGenerator UUID_GENERATOR = UUIDGenerator.newInstance();

static{
    FactoryObject.init(ItemTrash.class);
}

public ZoneId timestampZoneId;
public defUid requester;
public Type.Is type;
public TableDescription itemTable;
public defUid itemUid;
private Long timestamp;

public static UUIDGenerator getUidGenerator(){
    return UUID_GENERATOR;
}

public static defCreatable<ItemTrash> getFactory(){
    return FactoryObject.singleton(ItemTrash.class);
}

public static void factoryRelease(){
    FactoryObject.singletonRelease(ItemTrash.class);
}

public static ItemTrash obtain(){
    return getFactory().create();
}

public ItemTrash setTimestampToNow(){
    return setTimestamp(Clock.MilliSecond.now(), null);
}

public ItemTrash setTimestamp(Long data, ZoneId zoneId){
    this.timestamp = data;
    if(timestamp == null){
        this.timestampZoneId = null;
    } else {
        if(zoneId == null){
            this.timestampZoneId = Clock.ZoneIdLocal();
        } else {
            this.timestampZoneId = zoneId;
        }
    }
    return this;
}

public ItemTrash setRequester(defUid data){
    this.requester = data;
    return this;
}

public ItemTrash setType(Type.Is data){
    this.type = data;
    return this;
}

public ItemTrash setItemTable(TableDescription data){
    this.itemTable = data;
    return this;
}

public ItemTrash setItemTable(dbTable<?>.Ref data){
    this.itemTable = data.getTableDefinition().getTableDescription();
    return this;
}

public ItemTrash setItemUid(defUid data){
    this.itemUid = data;
    return this;
}

public Long getTimestamp(){
    return timestamp;
}

public ItemTrash setTimestamp(Long data){
    return setTimestamp(data, null);
}

public String getTimestampString(){
    String data = null;
    if(timestamp != null){
        data = Clock.MilliSecondTo.DateAndTime.toString(timestamp, timestampZoneId, TIMESTAMP_DATE_FORMAT);
    }
    return data;
}

@Override
public ItemTrash clear(){
    super.clear();
    setTimestamp(null);
    setRequester(null);
    setType(null);
    setItemTable((TableDescription)null);
    setItemUid(null);
    return this;
}

@Override
public ItemTrash newItem(){
    return ItemTrash.obtain();
}

@Override
public ItemTrash copy(){
    ItemTrash copy = super.copy();
    copy.setTimestamp(timestamp);
    copy.setRequester(requester);
    copy.setType(type);
    copy.setItemTable(itemTable);
    copy.setItemUid(itemUid);
    return this;
}

@Override
protected void fromParcel(ParcelW parcel){
    super.fromParcel(parcel);
    setTimestamp(parcel.readLong(), Clock.ZoneId(parcel.readString()));
    setRequester(UidBase.fromBytes(parcel.readBytes()));
    setType(Type.findTypeOf(parcel.readString()));
    String name = parcel.readString();
    if(name == null){
        setItemTable((TableDescription)null);
    } else {
        setItemTable(Descriptions.findInstanceOf(name));
    }
    setItemUid(UidBase.fromBytes(parcel.readBytes()));
}

@Override
protected void toParcel(Parcel parcel){
    super.toParcel(parcel);
    parcel.writeValue(timestamp);
    parcel.writeValue(timestampZoneId != null ? timestampZoneId.getId() : null);
    parcel.writeValue(requester!=null?requester.toBytes():null);
    parcel.writeValue(type != null ? type.name() : null);
    parcel.writeValue(itemTable != null ? itemTable.name() : null);
    parcel.writeValue(itemUid!=null?itemUid.toBytes():null);
}

@Override
public boolean equals(Object obj){
    if(obj instanceof ItemTrash){
        boolean isEqual = super.equals(obj);
        if(!isEqual){
            return false;
        }
        ItemTrash second = (ItemTrash)obj;
        isEqual = (Compare.equals(this.timestamp, second.timestamp));
        isEqual &= (Compare.equals(this.timestampZoneId, second.timestampZoneId));
        isEqual &= (Compare.equals(this.requester, second.requester));
        isEqual &= (Compare.equals(this.type, second.type));
        isEqual &= (Compare.equals(this.itemTable, second.itemTable));
        isEqual &= (Compare.equals(this.itemUid, second.itemUid));
        return isEqual;
    }
    return super.equals(obj);
}

@Override
public DebugString toDebugString(){
    DebugString data = super.toDebugString();
    data.append("timestamp", getTimestampString());
    data.append("timestampZoneId", timestampZoneId);
    data.append("requester", requester);
    data.append("com.tezov.lib.type", type);
    data.append("itemTable", itemTable);
    data.append("itemUid", itemUid);
    return data;
}

public interface Type{
    Is PUT_TO_TRASH = new Is("PUT_TO_TRASH");
    Is REMOVED = new Is("REMOVED");

    static Is findTypeOf(String name){
        return Is.findTypeOf(Is.class, name);
    }

    class Is extends EnumBase.Is{
        public Is(String value){
            super(value);
        }

    }

}

}














