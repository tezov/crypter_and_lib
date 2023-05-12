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
import org.apache.commons.lang3.math.NumberUtils;

public class ValidatorDigit extends ValidatorBaseNoKey<String>{
private final static String STRING_EMPTY_SUFFIX = "_empty";

@Override
protected String prefix(){
    return "digit";
}

@Override
public boolean isValid(String data){
    if(data == null){
        return false;
    }
    else {
        return NumberUtils.isDigits(data);
    }
}

@Override
public String getErrorMessage(String data){
    if(data == null){
        return getString(STRING_EMPTY_SUFFIX);
    } else {
        return super.getErrorMessage(data);
    }
}

}
