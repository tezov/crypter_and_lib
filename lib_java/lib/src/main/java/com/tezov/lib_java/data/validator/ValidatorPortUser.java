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
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java.util.UtilsString;

import org.apache.commons.lang3.math.NumberUtils;

public class ValidatorPortUser extends ValidatorBaseNoKey<String>{
public final static int MIN = 1024;
public final static int MAX = 49151;

@Override
protected String prefix(){
    return "port_user";
}

@Override
public boolean isValid(String data){
    if(!NumberUtils.isCreatable(data)){
        return false;
    } else {
        UtilsString.Number number = UtilsString.parseNumber(data);
        if(!number.isInteger() || (number.isNegative())){
            return false;
        } else {
            return isValid(number.getInteger());
        }
    }
}
public boolean isValid(int port){
    return port >= MIN && port <= MAX;
}

}
