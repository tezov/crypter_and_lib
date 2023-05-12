/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.type.currency.display;

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

import android.graphics.drawable.Drawable;

import androidx.annotation.Nullable;

import com.tezov.lib_java_android.application.AppContext;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java.debug.DebugTrack;

public class CurrencyDisplay{
public String code;
public String text;
public int flag;

public CurrencyDisplay(String code, String text, int flag){
DebugTrack.start().create(this).end();
    this.code = code;
    this.text = text;
    this.flag = flag;
}

public String getCode(){
    return code;
}

public String getText(){
    return text;
}

public Drawable getFlag(){
    return AppContext.getResources().getDrawable(flag);
}

public DebugString toDebugString(){
    DebugString data = new DebugString();
    data.append("code", code);
    data.append("text", text);
    data.append("flag", AppContext.getResources().getIdentifierName(flag));
    return data;
}

final public void toDebugLog(){
DebugLog.start().send(toDebugString()).end();
}

@Override
public boolean equals(@Nullable Object obj){
    if(obj instanceof String){
        return obj.equals(code);
    } else {
        if(obj instanceof CurrencyDisplay){
            return code.equals(((CurrencyDisplay)obj).code);
        } else {
            return false;
        }
    }
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

}

