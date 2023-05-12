/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.crypter.export_import_keys;

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.debug.DebugException;
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
import static com.tezov.crypter.data.table.Descriptions.KEY;

import com.tezov.crypter.data.item.ItemKey;
import com.tezov.lib_java.parser.defParserReader;
import com.tezov.lib_java.parser.defParserWriter;

import java.io.IOException;

public class AdapterKey extends AdapterParserRaw<ItemKey>{
public AdapterKey(){
    super(KEY);
}
@Override
public void write(defParserWriter writer, ItemKey item, AdapterOptionWrite option) throws IOException{
    writer.beginObject();
    writer.write(OWNED, item.isOwner());
    writer.write(VALUE, option.getEncoder().encode(item.toBytes(true), byte[].class));
    writer.endObject();
}
@Override
public ItemKey read(defParserReader reader, AdapterOptionRead option) throws IOException{
    Boolean isOwner = null;
    String value = null;
    reader.beginObject();
    while(reader.isNotEndObject()){
        String fieldName = reader.nextName();
        if(OWNED.equals(fieldName)){
            isOwner = reader.nextBoolean();
        } else if(VALUE.equals(fieldName)){
            value = reader.nextString();
        } else {
            reader.skipValue();
        }
    }
    reader.endObject();
    if((isOwner == null) || (value == null)){
        throw new IOException("invalid object");
    }
    if(!isOwner && !option.isFileGeneratedFromSameApp()){
        return null;
    }
    ItemKey item = ItemKey.obtain().clear();
    item.fromBytes(option.getDecoder().decode(value));
    return item;
}

}
