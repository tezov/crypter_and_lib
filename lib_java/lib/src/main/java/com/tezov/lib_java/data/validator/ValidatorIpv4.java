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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidatorIpv4 extends ValidatorBaseNoKey<String>{
final private Pattern pattern = Pattern.compile("^(?:(?:(?:25[0-5])|(?:[0-2]?[0-9]?[0-9])|(?:2[0-4][0-9]))(?:\\.(?!$)|$)){4}$");

@Override
protected String prefix(){
    return "ipv4";
}

@Override
public boolean isValid(String data){
    if(data == null){
        return false;
    } else {
        Matcher matcher = pattern.matcher(data);
        return matcher.matches();
    }
}

}
