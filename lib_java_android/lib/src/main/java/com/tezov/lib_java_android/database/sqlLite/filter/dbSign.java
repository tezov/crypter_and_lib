/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.database.sqlLite.filter;

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

import com.tezov.lib_java.type.defEnum.EnumBase;

public interface dbSign{
Is EQUAL = new Is("EQUAL", "=");
Is SUP = new Is("SUP", ">");
Is SUP_EQUAL = new Is("SUP_EQUAL", ">=");
Is INF = new Is("INF", "<");
Is INF_EQUAL = new Is("INF_EQUAL", "<=");
Is NOT = new Is("NOT", "!=");
Is NULL = new Is("NULL", " IS NULL");
Is NOT_NULL = new Is("NOT_NULL", " IS NOT NULL");
Is LIKE = new Is("LIKE", " LIKE ");

class Is extends EnumBase.Is{
    protected String sign;

    protected Is(String name, String sign){
        super(name);
        this.sign = sign;
    }

    public String getSign(){
        return sign;
    }

}

}
