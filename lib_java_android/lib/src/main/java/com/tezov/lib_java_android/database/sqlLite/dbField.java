/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.database.sqlLite;

import com.tezov.lib_java.debug.DebugLog;
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

import com.tezov.lib_java.generator.uid.defUid;
import com.tezov.lib_java.type.defEnum.EnumBase;

import java.util.List;

import static com.tezov.lib_java_android.database.sqlLite.dbTableDefinition.TypeDB;

public interface dbField{
Is PRIMARY_KEY = new Is("PRIMARY_KEY");
@dbFieldAnnotation(order = Integer.MIN_VALUE, typeDB = TypeDB.ID, typeJava = defUid.class)
Is UID = new Is("UID");
@dbFieldAnnotation(order = Integer.MIN_VALUE + 1, typeDB = TypeDB.DELETED, typeJava = boolean.class)
Is DELETED = new Is("DELETED");

static int count(Class<? extends Is> type){
    int count = Is.getValuesTypeOf(Is.class).size();
    List<? extends Is> list = Is.getValuesTypeOf(type);
    if(list != null){
        count += Is.getValuesTypeOf(type).size();
    }
    return count;
}

class Is extends EnumBase.Is{
    public Is(String value){
        super(value);
    }

}

}
