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
import com.tezov.lib_java_android.database.ItemBase;
import com.tezov.lib_java_android.database.TableDescription;
import com.tezov.lib_java.parser.defParserReader;
import com.tezov.lib_java.parser.defParserWriter;

import java.io.IOException;

public abstract class AdapterParserRaw<T extends ItemBase<T>> extends com.tezov.lib_java.parser.raw.ParserAdapterRaw<T>{
public final static String VALUE = "_VALUE";
public final static String OWNED = "_OWNED";

private AdapterOptionWrite optionWrite = null;
private AdapterOptionRead optionRead = null;

public AdapterParserRaw(TableDescription tableDescription){
    super(tableDescription.name());
}
private AdapterParserRaw me(){
    return this;
}
public AdapterOptionWrite getOptionWrite(){
    return optionWrite;
}
public AdapterParserRaw<T> setOptionWrite(AdapterOptionWrite option){
    this.optionWrite = option;
    return this;
}
protected AdapterOptionRead getOptionRead(){
    return optionRead;
}
public AdapterParserRaw<T> setOptionRead(AdapterOptionRead option){
    this.optionRead = option;
    return this;
}
final public void writeHeader() throws IOException{
    writeHeader(writerHelper().getWriter(), optionWrite);
}
protected void writeHeader(defParserWriter writer, AdapterOptionWrite option) throws IOException{

}
@Override
final public void write(defParserWriter writer, T value) throws IOException{
    write(writer, value, optionWrite);
}
public abstract void write(defParserWriter writer, T value, AdapterOptionWrite option) throws IOException;

final public void readHeader() throws IOException{
    readHeader(readerHelper().getReader(), optionRead);
}
public void readHeader(defParserReader reader, AdapterOptionRead option) throws IOException{

}
@Override
final public T read(defParserReader reader) throws IOException{
    return read(reader, optionRead);
}
public abstract T read(defParserReader reader, AdapterOptionRead option) throws IOException;

}
