/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.recycler.prebuild.db.itemToSubItem;

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
import androidx.annotation.Nullable;

import com.tezov.lib_java.generator.uid.defHasUid;
import com.tezov.lib_java.generator.uid.defUid;
import com.tezov.lib_java.toolbox.Compare;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java_android.util.UtilsTypeWrapper;

public class SubItemString implements defHasUid{

static{
    UtilsTypeWrapper.addWrapper(new SubItemString.Wrapper());
}

private final defUid uid;
private String s;

public SubItemString(defUid uid, String s){
DebugTrack.start().create(this).end();
    this.uid = uid;
    this.s = s;
}

@Override
public defUid getUid(){
    return uid;
}

public String getString(){
    return s;
}

public String get(){
    return s;
}

public void set(String s){
    this.s = s;
}

@Override
public int hashCode(){
    if(s == null){
        return 0;
    }
    return s.hashCode();
}

@Override
public boolean equals(@Nullable Object obj){
    if(obj instanceof String){
        return Compare.equals(s, obj);
    }
    if(obj instanceof SubItemString){
        return Compare.equals(s, ((SubItemString)obj).s);
    }
    return false;
}

public DebugString toDebugString(){
    DebugString sb = new DebugString();
    sb.append("uid", uid);
    sb.append("s", s);
    return sb;
}

final public void toDebugLog(){
DebugLog.start().send(toDebugString()).end();
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

public static class Wrapper implements UtilsTypeWrapper.def<String, SubItemString>{
    @Override
    public Class<String> getType(){
        return String.class;
    }
    @Override
    public Class<SubItemString> getWrapType(){
        return SubItemString.class;
    }
    @Override
    public SubItemString wrap(String s){
        if(s == null){
            return null;
        } else {
            return new SubItemString(null, s);
        }
    }
    @Override
    public String unWrap(SubItemString wrapS){
        return wrapS.get();
    }

}

}
