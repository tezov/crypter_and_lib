/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.database.sqlLite.filter.chunk;

import com.tezov.lib_java.debug.DebugTrack;
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

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java.type.collection.ListOrObject;

import java.util.List;

public class Chunk{
private String statement;
private ListOrObject<String> values;

public Chunk(){
    this.statement = null;
    this.values = null;
}

public Chunk addValue(String value){
    if(value != null){
        obtainValues().add(value);
    }
    return this;
}

public Chunk addValues(List<String> value){
    obtainValues().addAll(value);
    return this;
}

public String getStatement(){
    return statement;
}

public Chunk setStatement(String statement){
    this.statement = statement;
    return this;
}

public List<String> obtainValues(){
    if(values == null){
        values = new ListOrObject<>();
    }
    return values;
}

public List<String> getValues(){
    return values;
}

public String[] getValuesArray(){
    if(values == null){
        return null;
    } else {
        return values.toArray(new String[0]);
    }
}

public DebugString toDebugString(){
    DebugString data = new DebugString();
    data.append("statement", statement);
    data.append("values", values);
    return data;
}

final public void toDebugLog(){
DebugLog.start().send(toDebugString()).end();
}

final public void toDebugLogStatement(){
DebugLog.start().send(statement).end();
}

}
