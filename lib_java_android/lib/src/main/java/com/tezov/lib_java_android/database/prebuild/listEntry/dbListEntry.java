/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.database.prebuild.listEntry;

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugException;
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

import com.tezov.lib_java_android.database.prebuild.listEntry.item.ItemEntry;
import com.tezov.lib_java_android.database.prebuild.listEntry.table.dbListEntryTable;
import com.tezov.lib_java.toolbox.Nullify;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.collection.ListEntry;
import com.tezov.lib_java.type.primitive.BytesTo;
import com.tezov.lib_java.type.primitive.string.StringHexTo;

import java.util.ArrayList;
import java.util.List;

public class dbListEntry{
private final dbListEntryTable.Ref table;

public dbListEntry(dbListEntryTable.Ref table){
DebugTrack.start().create(this).end();
    this.table = table;
}


public List<String> findKeyContain(String pattern){
    List<ItemEntry> items = table.find(pattern);
    if(items != null){
        List<String> strings = new ArrayList<>();
        for(ItemEntry item: items){
            strings.add(item.getKey());
        }
        return Nullify.collection(strings);
    } else {
        return null;
    }
}

public String getString(String key){
    return get(key);
}

public void put(String key, String data){
    offer(key, data);

}
public void put(ListEntry<String, String> datas){
    offer(datas);
}

public byte[] getBytes(String key){
    return StringHexTo.Bytes(get(key));
}
public void put(String key, byte[] bytes){
    offer(key, BytesTo.StringHex(bytes));
}

public Integer getInt(String key){
    String s = get(key);
    if(s == null){
        return null;
    } else {
        return Integer.valueOf(s);
    }
}

public void put(String key, Integer data){
    offer(key, data != null ? Integer.toString(data) : null);
}

public Long getLong(String key){
    String s = get(key);
    if(s == null){
        return null;
    } else {
        return Long.valueOf(s);
    }
}

public void put(String key, Long data){
    offer(key, data != null ? Long.toString(data) : null);
}

public Float getFloat(String key){
    String s = get(key);
    if(s == null){
        return null;
    } else {
        return Float.valueOf(s);
    }
}

public void put(String key, Float data){
    offer(key, data != null ? Float.toString(data) : null);
}

public Boolean getBoolean(String key){
    String s = get(key);
    if(s == null){
        return null;
    } else {
        return Boolean.valueOf(s);
    }
}

public void put(String key, Boolean data){
    put(key, data != null ? Boolean.toString(data) : null);
}


private String get(String key){
    ItemEntry item = table.get(key);
    if(item != null){
        return item.value;
    } else {
        return null;
    }
}

private void offer(String key, String data){
    table.put(key, data);
}
private void offer(ListEntry<String, String> datas){
    table.put(datas);
}

public void remove(String key){
    table.remove(key);
}

public void clear(){
    table.clear();
}

final public void toDebugLog(){
    table.toDebugLog();
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

}
