/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.data.validator;

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
import com.tezov.lib_java.toolbox.Compare;
import com.tezov.lib_java.type.RangeInt;
import com.tezov.lib_java.type.primitive.string.StringHexTo;

public class ValidatorBytes extends ValidatorBaseNoKey<String>{
private final static String STRING_DIGIT = "_digit";
private final static String STRING_MIN = "_min";
private final static String STRING_MAX = "_max";

private RangeInt range = null;

public ValidatorBytes setLength(int length){
    return setLength(length, length);
}

public ValidatorBytes setLength(Integer min, Integer max){
    this.range = new RangeInt(min, max);
    return this;
}

public ValidatorBytes setLength(RangeInt range){
    this.range = range;
    return this;
}

@Override
protected String prefix(){
    return "bytes";
}

@Override
public boolean isValid(String data){
    if(data == null){
        return range == null;
    }
    byte[] bytes = StringHexTo.Bytes(data);
    if(bytes == null){
        return false;
    }
    return isLengthValid(bytes.length);
}
public boolean isLengthValid(int length){
    return (range == null) || range.isInside(length);
}


@Override
public String getErrorMessage(String s){
    StringBuilder data = new StringBuilder();
    data.append(super.getErrorMessage(s));
    if(range != null){
        if(Compare.equals(range.getMin(), range.getMax())){
            data.append(" ").append(2 * range.getMax()).append(" ").append(getString(STRING_DIGIT));
        } else {
            data.append(" ").append(rangeToString());
        }
    }
    return data.toString();
}

private String rangeToString(){
    StringBuilder data = new StringBuilder();
    String min = range.getMin() != null ? getString(STRING_MIN) + " " + (range.getMin() * 2) : null;
    if(min != null){
        data.append(range.isMinInclude() ? "[" : "]");
        data.append(min);
    }
    String max = range.getMax() != null ? getString(STRING_MAX) + " " + (range.getMax() * 2) : null;
    if(max != null){
        data.append(range.isMaxInclude() ? "[" : "]");
        data.append(max);
    }
    if(data.length() != 0){
        data.append(" ").append(getString(STRING_DIGIT));
    }
    return data.toString();
}

}
