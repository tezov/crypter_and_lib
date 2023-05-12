/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.crypter.export_import_keys;

import com.tezov.lib_java.debug.DebugLog;
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
import static com.tezov.crypter.data.table.Descriptions.KEY_RING;
import static com.tezov.crypter.export_import_keys.dbKeyFormatter.HEADER_OPTION;

import com.tezov.crypter.data.item.ItemKeyRing;
import com.tezov.lib_java.buffer.ByteBufferPacker;
import com.tezov.lib_java.cipher.definition.defDecoderBytes;
import com.tezov.lib_java.cipher.definition.defEncoderBytes;
import com.tezov.lib_java.cipher.keyMaker.keySim.KeySimMakerBytes;
import com.tezov.lib_java.cipher.misc.PasswordCipher;
import com.tezov.lib_java.parser.defParserReader;
import com.tezov.lib_java.parser.defParserWriter;
import com.tezov.lib_java.debug.DebugTrack;

import java.io.IOException;

public class AdapterKeyRing extends AdapterParserRaw<ItemKeyRing>{

private OptionWrite optionWriteOwnedNot = null;
private OptionRead optionReadOwnedNot = null;

public AdapterKeyRing(){
    super(KEY_RING);
}


@Override
public void writeHeader(defParserWriter writer, AdapterOptionWrite option) throws IOException{
    optionWriteOwnedNot = new OptionWrite(option.passwordApp());
    optionWriteOwnedNot.build();
    writer.write(HEADER_OPTION, optionWriteOwnedNot.toBytes());
}
@Override
public void write(defParserWriter writer, ItemKeyRing item, AdapterOptionWrite option) throws IOException{
    writer.beginObject();
    writer.write(OWNED, item.isOwner());
    byte[] keyRing;
    if(item.isOwner()){
        keyRing = item.toBytes(true);
    } else {
        keyRing = optionWriteOwnedNot.getEncoder().encode(item.toBytes(true));
    }
    writer.write(VALUE, option.getEncoder().encode(keyRing, byte[].class));
    writer.endObject();
}
@Override
public void readHeader(defParserReader reader, AdapterOptionRead option) throws IOException{
    if(HEADER_OPTION.equals(reader.nextName())){
        optionReadOwnedNot = new OptionRead(option.passwordApp());
        optionReadOwnedNot.rebuild(reader.nextBytes());
    } else {
        throw new IOException("invalid file");
    }
}
@Override
public ItemKeyRing read(defParserReader reader, AdapterOptionRead option) throws IOException{
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
    ItemKeyRing item = ItemKeyRing.obtain().clear();
    byte[] keyRing = option.getDecoder().decode(value);
    if(!isOwner){
        keyRing = optionReadOwnedNot.getDecoder().decode(keyRing);
    }
    item.fromBytes(keyRing);
    return item;
}

public static class OptionWrite{
    private final KeySimMakerBytes keySimMaker;
    public OptionWrite(PasswordCipher password){
DebugTrack.start().create(this).end();
        this.keySimMaker = new KeySimMakerBytes(password);
    }
    public void build() throws IOException{
        try{
            this.keySimMaker.build();
            this.keySimMaker.getEncoder().setPacker(new ByteBufferPacker());
        } catch(Throwable e){
            throw new IOException("build failed");
        }
    }
    public defEncoderBytes getEncoder(){
        return keySimMaker.getEncoder();
    }
    public byte[] toBytes() throws IOException{
        try{
            return keySimMaker.specToBytes();
        } catch(Throwable e){
            throw new IOException("toBytes failed");
        }

    }
    @Override
    protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
        super.finalize();
    }

}

public static class OptionRead{
    private final KeySimMakerBytes keySimMaker;
    public OptionRead(PasswordCipher password){
DebugTrack.start().create(this).end();
        this.keySimMaker = new KeySimMakerBytes(password);
    }
    public void rebuild(byte[] spec) throws IOException{
        try{
            this.keySimMaker.reBuild(spec);
            this.keySimMaker.getDecoder().setPacker(new ByteBufferPacker());
        } catch(Throwable e){
            throw new IOException("rebuild failed");
        }
    }
    public defDecoderBytes getDecoder(){
        return keySimMaker.getDecoder();
    }
    @Override
    protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
        super.finalize();
    }

}

}
