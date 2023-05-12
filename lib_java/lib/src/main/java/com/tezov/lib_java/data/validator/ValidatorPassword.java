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

public class ValidatorPassword extends ValidatorBaseNoKey<String>{
private final static String STRING_FORMAT = "_format";
private final static String STRING_FORMAT_DIGIT = STRING_FORMAT + "_digit";
private final static String STRING_FORMAT_MIN = STRING_FORMAT + "_min";
private final static String STRING_FORMAT_MAX = STRING_FORMAT + "_max";
private final static String STRING_PATTERN = "_pattern";

private RangeInt range = null;

public ValidatorPassword setLength(int length){
    return setLength(length, length);
}

public ValidatorPassword setLength(Integer min, Integer max){
    this.range = new RangeInt(min, max);
    return this;
}

public ValidatorPassword setLength(RangeInt range){
    this.range = range;
    return this;
}

@Override
protected String prefix(){
    return "password";
}

@Override
public boolean isValid(String data){
    return !Compare.isFalseOrNull(isLengthValid(data));
}

public Boolean isLengthValid(String s){
    if(s == null){
        return null;
    } else {
        return (range == null) || range.isInside(s.length());
    }
}

@Override
public String getErrorMessage(String s){
    StringBuilder data = new StringBuilder();
    if(Compare.isFalseOrNull(isLengthValid(s))){
        data.append(getString(STRING_FORMAT));
        if(Compare.equals(range.getMin(), range.getMax())){
            data.append(" ").append(range.getMax()).append(" ").append(getString(STRING_FORMAT_DIGIT));
        } else {
            data.append(" ").append(rangeToString());
        }

    } else {
        data.append(getString(STRING_PATTERN));
    }
    return data.toString();
}

private String rangeToString(){
    StringBuilder data = new StringBuilder();
    String min = range.getMin() != null ? getString(STRING_FORMAT_MIN) + " " + range.getMin() : null;
    if(min != null){
        data.append(range.isMinInclude() ? "[" : "]");
        data.append(min);
    }
    String max = range.getMax() != null ? getString(STRING_FORMAT_MAX) + " " + range.getMax() : null;
    if(max != null){
        data.append(range.isMaxInclude() ? "[" : "]");
        data.append(max);
    }
    if(data.length() != 0){
        data.append(" ").append(getString(STRING_FORMAT_DIGIT));
    }
    return data.toString();
}

}
